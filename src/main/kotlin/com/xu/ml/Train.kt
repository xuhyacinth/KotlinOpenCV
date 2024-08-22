package com.xu.com.xu.ml

import cn.hutool.core.util.CharsetUtil
import cn.hutool.extra.compress.CompressUtil
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.ml.DTrees
import org.opencv.ml.Ml
import java.io.File
import java.util.*


object Train {

    init {
        val os = System.getProperty("os.name")
        val type = System.getProperty("sun.arch.data.model")
        if (os.uppercase(Locale.getDefault()).contains("WINDOWS")) {
            val lib = if (type.endsWith("64")) {
                File("lib\\opencv-4.9\\x64\\" + System.mapLibraryName(Core.NATIVE_LIBRARY_NAME))
            } else {
                File("lib\\opencv-4.9\\x86\\" + System.mapLibraryName(Core.NATIVE_LIBRARY_NAME))
            }
            System.load(lib.absolutePath)
        }
        println(Core.VERSION)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val (trainImages, trainLabels) = load("lib/data/image/train/")
        val (testImages, testLabels) = load("lib/data/image/predict/")

        // 梯度提升树
        val model = DTrees.create()
        model.maxDepth = 20
        model.minSampleCount = 2
        model.useSurrogates = false
        model.cvFolds = 0
        model.use1SERule = false
        model.truncatePrunedTree = false
        model.regressionAccuracy = 0.01f

        // 转换为OpenCV的Mat格式
        val trainImagesData = Mat(trainImages.size, 784, CvType.CV_32F)
        trainImages.forEachIndexed { index, floatArray ->
            trainImagesData.put(index, 0, floatArray)
        }
        val trainLabelsData = Mat(trainLabels.size, 1, CvType.CV_32S)
        trainLabelsData.put(0, 0, trainLabels.toIntArray())

        // 训练模型
        model.train(trainImagesData, Ml.ROW_SAMPLE, trainLabelsData)
        model.save("lib/data/image/ml/DTrees.xml")

        // 评估训练集准确率
        val train = accuracy(model, trainImages, trainLabels)
        println("训练集准确率: $train")

        // 评估测试集准确率
        val test = accuracy(model, testImages, testLabels)
        println("测试集准确率: $test")
    }

    /**
     * 加载数据
     */
    private fun load(path: String): Pair<List<FloatArray>, List<Int>> {
        val images = mutableListOf<FloatArray>()
        val labels = mutableListOf<Int>()

        for (i in 0..9) {
            val dir = File("$path/$i")
            dir.listFiles()?.forEach { file ->
                val img = Imgcodecs.imread(file.absolutePath, Imgcodecs.IMREAD_GRAYSCALE)
                if (!img.empty()) {
                    Imgproc.resize(img, img, Size(28.0, 28.0))
                    val array = ByteArray(784)
                    img.get(0, 0, array)
                    images.add(array.map { it / 255.0f }.toFloatArray())
                    labels.add(i)
                }
            }
        }
        return Pair(images, labels)
    }

    /**
     * 计算准确率
     */
    private fun accuracy(model: DTrees, images: List<FloatArray>, labels: List<Int>): Double {
        var correct = 0
        images.forEachIndexed { index, image ->
            val sample = Mat(1, 784, CvType.CV_32F)
            sample.put(0, 0, image)
            val response = model.predict(sample)
            if (response.toInt() == labels[index]) {
                correct++
            }
        }
        return correct.toDouble() / images.size * 100
    }

    private fun unzip() {
        // 解压训练图片
        CompressUtil.createExtractor(
            CharsetUtil.defaultCharset(),
            File("lib/data/image/train.7z")
        ).extract(File("lib/data/image/train/"))
        // 解压测试图片
        CompressUtil.createExtractor(
            CharsetUtil.defaultCharset(),
            File("lib/data/image/predict.7z")
        ).extract(File("lib/data/image/predict/"))
    }

}
