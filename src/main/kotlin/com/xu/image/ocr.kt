package com.xu.com.xu.image

import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfFloat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.highgui.HighGui
import org.opencv.imgcodecs.Imgcodecs
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
        println("OpenCV: ${Core.VERSION}")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        ocr()
    }

    fun ocr() {
        val image = Imgcodecs.imread("C:\\Users\\xuyq\\Desktop\\1.png")

        val gray = Mat()
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY)

        val binary = Mat()
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
        Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        // 使用决策树进行识别
        val ml = DTrees.load("lib/data/image/ml/DTrees.xml")
        for (contour in contours) {
            val rect = Imgproc.boundingRect(contour)
            // 过滤小区域
            if (rect.width > 28 && rect.height > 28) {
                rect.x -= 15
                rect.y -= 15
                rect.width += 30
                rect.height += 30

                val mat = Mat(gray, rect)
                Imgproc.resize(mat, mat, Size(28.0, 28.0))

                val float = MatOfFloat()
                mat.convertTo(float, CvType.CV_32FC1)

                val ocr = Mat(1, 28 * 28, CvType.CV_32FC1)
                float.copyTo(ocr.row(0))

                val result = ml.predict(float, Mat())
                println("识别结果：${result.toInt()}")

                Imgproc.rectangle(image, rect, Scalar(0.0, 255.0, 0.0), 2)
            }
        }
        HighGui.imshow("src", image)
        HighGui.waitKey(0)
    }

    private fun contour() {
        //1 获取原图
        val src = Imgcodecs.imread("C:\\Users\\xuyq\\Desktop\\1.png")
        //2 图片灰度化
        val gary = Mat()
        Imgproc.cvtColor(src, gary, Imgproc.COLOR_RGB2GRAY)
        //3 图像边缘处理
        val edges = Mat()
        Imgproc.Canny(gary, edges, 1.0, 1.0, 7, true)
        //4 发现轮廓
        val list = ArrayList<MatOfPoint>()
        Imgproc.findContours(edges, list, Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)
        //5 绘制轮廓
        for (i in 0 until list.size) {
            Imgproc.drawContours(src, list, i, Scalar(0.0, 255.0, 0.0), 1, Imgproc.LINE_AA)
        }
        HighGui.imshow("src", src)
        HighGui.waitKey(0)
    }


    fun test() {
        val image = Imgcodecs.imread("C:\\Users\\xuyq\\Desktop\\1.png")

        val gray = Mat()
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY)

        val binary = Mat()
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
        Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        for (contour in contours) {
            val rect = Imgproc.boundingRect(contour)

            // 过滤小区域
            if (rect.width > 10 && rect.height > 10) {
                Imgproc.rectangle(image, rect, Scalar(0.0, 255.0, 0.0), 2)
            }
        }

        HighGui.imshow("src", image)
        HighGui.waitKey(0)

    }

}