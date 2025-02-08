package com.xu.com.xu.ml

import org.bytedeco.javacpp.*
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.opencv_core.*
import org.bytedeco.opencv.opencv_imgproc.*
import org.bytedeco.opencv.opencv_text.*
import org.bytedeco.opencv.global.opencv_imgcodecs.*
import org.bytedeco.opencv.global.opencv_imgproc.*
import java.io.File
import java.net.URL

class TextDetector {
    private lateinit var detector: TextDetectorCNN

    fun initialize() {
        // 定义模型文件的本地保存路径
        val modelDir = File("models").apply { mkdirs() }
        val prototxtPath = File(modelDir, "textbox.prototxt")
        val caffemodelPath = File(modelDir, "TextBoxes_icdar13.caffemodel")

        // 如果模型文件不存在，则下载
        if (!prototxtPath.exists()) {
            println("Downloading prototxt file...")
            URL("https://raw.githubusercontent.com/MhLiao/TextBoxes/master/models/deploy.prototxt")
                .openStream().use { input ->
                    prototxtPath.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
        }

        // 检查模型文件是否存在
        if (!caffemodelPath.exists()) {
            println("Please download the caffemodel file from: ")
            println("https://www.dropbox.com/s/g8pjzv2de9gty8g/TextBoxes_icdar13.caffemodel?dl=0")
            println("and place it in the models directory as: ${caffemodelPath.absolutePath}")
            throw IllegalStateException("Caffemodel file not found")
        }

        // 初始化检测器
        try {
            detector = TextDetectorCNN.create(
                prototxtPath.absolutePath,
                caffemodelPath.absolutePath
            )
        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize detector: ${e.message}")
        }
    }

    fun detectText(imagePath: String): List<DetectedText> {
        // 读取图像
        val image = imread(imagePath)
        if (image.empty()) {
            throw IllegalArgumentException("Could not read image: $imagePath")
        }

        // 检测容器
        val detections = RectVector()
        val confidences = FloatVector()

        // 执行检测
        detector.detect(image, detections, confidences)

        // 转换结果
        val results = mutableListOf<DetectedText>()
        for (i in 0 until detections.size().toInt()) {
            val rect = detections.get(i.toLong())
            val confidence = confidences.get(i.toLong())

            results.add(DetectedText(
                rect = Rectangle(
                    x = rect.x(),
                    y = rect.y(),
                    width = rect.width(),
                    height = rect.height()
                ),
                confidence = confidence
            ))

            // 在图像上绘制边界框
            rectangle(
                image,
                Point(rect.x(), rect.y()),
                Point(rect.x() + rect.width(), rect.y() + rect.height()),
                Scalar(0.0, 255.0, 0.0, 0.0),
                2,
                LINE_8,
                0
            )
        }

        // 保存标注后的图像
        val outputPath = imagePath.replace(".jpg", "_detected.jpg")
        imwrite(outputPath, image)

        return results
    }

    data class Rectangle(
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int
    )

    data class DetectedText(
        val rect: Rectangle,
        val confidence: Float
    )
}

fun main() {
    Loader.load(opencv_core::class.java)
    val detector = TextDetector()

    try {
        // 初始化检测器（包括下载必要的模型文件）
        detector.initialize()

        // 执行检测
        val results = detector.detectText("your_image.jpg")

        // 打印结果
        results.forEachIndexed { index, detection ->
            println("Detection $index:")
            println("  Position: (${detection.rect.x}, ${detection.rect.y})")
            println("  Size: ${detection.rect.width}x${detection.rect.height}")
            println("  Confidence: ${detection.confidence}")
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}