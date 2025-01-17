package com.xu.com.xu.image

import org.bytedeco.javacpp.Loader
import org.bytedeco.javacpp.Pointer
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgcodecs
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point2f

object ImageChange {

    init {
        Loader.load(opencv_core::class.java)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        trans()
    }

    /** 平移变换 */
    private fun trans() {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")
        if (src == null || src.empty()) {
            return
        }
        // 创建源点矩阵三个点
        val mat1 = Mat(1, 3, opencv_core.CV_32FC2)
        mat1.ptr(0, 0).put<Pointer>(Point2f(0f, 0f))
        mat1.ptr(0, 1).put<Pointer>(Point2f(0f, src.rows().toFloat()))
        mat1.ptr(0, 2).put<Pointer>(Point2f(src.cols().toFloat(), 0f))
        // 创建目标点矩阵三个点
        val mat2 = Mat(1, 3, opencv_core.CV_32FC2)
        mat2.ptr(0, 0).put<Pointer>(Point2f(src.cols() * 0.3f, src.cols() * 0.3f))
        mat2.ptr(0, 1).put<Pointer>(Point2f(src.cols() * 0.2f, src.cols() * 0.6f))
        mat2.ptr(0, 2).put<Pointer>(Point2f(src.cols() * 0.7f, src.cols() * 0.2f))
        // 获取旋转矩阵
        val matrix = opencv_imgproc.getAffineTransform(mat1, mat2)
        // 应用透视变换
        val images = Mat()
        opencv_imgproc.warpAffine(src, images, matrix, src.size())
        // 显示结果
        opencv_highgui.imshow("Translation", images)
        opencv_highgui.waitKey(0)
    }

    /** 旋转变换 */
    private fun rotate() {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")
        if (src == null || src.empty()) {
            return
        }
        val center = Point2f((src.cols() / 2).toFloat(), (src.rows() / 2).toFloat())
        // 获取旋转矩阵
        val matrix = opencv_imgproc.getRotationMatrix2D(center, 45.0, 0.5)
        // 应用透视变换
        val images = Mat()
        opencv_imgproc.warpAffine(src, images, matrix, src.size())
        // 显示结果
        opencv_highgui.imshow("Rotation", images)
        opencv_highgui.waitKey(0)
    }

}