package com.xu.com.xu.image

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.highgui.HighGui
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import java.util.*

object Contour {

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
        println("OpenCV: ${Core.VERSION}")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val image = Imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")

        val gray = Mat()
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY)

        val binary = Mat()
        // 灰度图像自适应阈值二值化
        Imgproc.adaptiveThreshold(
            gray,
            binary,
            255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY_INV,
            11,
            2.0
        )

        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()
        // 检测图像中的轮廓
        Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        for (contour in contours) {
            // 计算并返回包围特定点集或轮廓的最小矩形边框
            val rect = Imgproc.boundingRect(contour)
            // 过滤小区域
            if (rect.width > 28 || rect.height > 28) {
                rect.x -= 15
                rect.y -= 15
                rect.width += 30
                rect.height += 30
                Imgproc.rectangle(image, rect, Scalar(0.0, 0.0, 255.0), 2)
            }
        }
        HighGui.imshow("src", image)
        HighGui.waitKey(0)
    }

}