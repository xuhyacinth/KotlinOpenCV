package com.xu.com.xu.image

import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgcodecs.imread
import org.bytedeco.opencv.global.opencv_imgproc.resize
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Rect
import org.bytedeco.opencv.opencv_core.Size
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.imgproc.Imgproc
import org.opencv.ml.DTrees
import java.io.File
import java.util.*

object ocr {

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
        // 读取图片
        val src = imread("C:\\Users\\xuyq\\Desktop\\1.png")
        // 鼠标框选区域
        val roi: Rect? = opencv_highgui.selectROI("ROI", src, false, false, false)
        opencv_highgui.destroyWindow("ROI")
        if (roi == null) {
            return
        }

        val dst = Mat(src, roi)
        resize(dst, dst, Size(28, 28))
        val newMat = dst.reshape(0,1)
        newMat.total().toInt()

        val images = intArrayOf(1, 2, 3, 4, 5, 6)
        //images.add(array.map.toIntArray())

        // 创建和训练模型
        val model = DTrees.load("lib/data/image/ml/DTrees.xml")
        val sample = org.opencv.core.Mat(1, 784, CvType.CV_32F)

        sample.put(0, 0, images)

        val response = model.predict(sample)
        println(response.toInt())

        opencv_highgui.imshow("DST", dst)
        opencv_highgui.waitKey(1)
    }

}