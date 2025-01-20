package com.xu.com.xu.trans

import org.bytedeco.javacpp.Loader
import org.bytedeco.javacpp.Pointer
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgcodecs
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point2f
import org.bytedeco.opencv.opencv_core.Rect
import org.bytedeco.opencv.opencv_core.Size

object Affine {

    init {
        Loader.load(opencv_core::class.java)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        cropping1()
    }

    /**
     * 仿射变换 平移变换
     *
     * @since 2025年1月20日12点33分
     */
    private fun move() {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")
        if (src == null || src.empty()) {
            return
        }
        // 创建源点矩阵三个点
        val mat1 = Mat(1, 3, opencv_core.CV_32FC2)
        mat1.ptr(0, 0).put<Pointer>(Point2f(0f, 0f))
        mat1.ptr(0, 1).put<Pointer>(Point2f(src.cols() - 1f, 0f))
        mat1.ptr(0, 2).put<Pointer>(Point2f(0f, src.rows() - 1f))
        // 创建目标点矩阵三个点
        val mat2 = Mat(1, 3, opencv_core.CV_32FC2)
        mat2.ptr(0, 0).put<Pointer>(Point2f(100f, 100f))
        mat2.ptr(0, 1).put<Pointer>(Point2f(src.cols() + 100f, 100f))
        mat2.ptr(0, 2).put<Pointer>(Point2f(100f, src.rows() + 100f))
        // 获取旋转矩阵
        val matrix = opencv_imgproc.getAffineTransform(mat1, mat2)
        // 应用透视变换
        val images = Mat()
        opencv_imgproc.warpAffine(src, images, matrix, src.size())
        // 显示结果
        opencv_highgui.imshow("MOVE", images)
        opencv_highgui.waitKey(0)
    }

    /**
     * 仿射变换 旋转变换
     *
     * @since 2025年1月20日12点33分
     */
    private fun revolve() {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")
        if (src == null || src.empty()) {
            return
        }
        // 旋转中心
        val center = Point2f((src.cols() / 2).toFloat(), (src.rows() / 2).toFloat())
        // 获取旋转矩阵
        val matrix = opencv_imgproc.getRotationMatrix2D(center, 45.0, 0.5)
        // 应用透视变换
        val images = Mat()
        opencv_imgproc.warpAffine(src, images, matrix, src.size())
        // 显示结果
        opencv_highgui.imshow("REVOLVE", images)
        opencv_highgui.waitKey(0)
    }

    /**
     * 仿射变换 图像缩放
     *
     * @since 2025年1月20日12点33分
     */
    private fun zoom() {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")
        if (src == null || src.empty()) {
            return
        }
        // 旋转中心
        val center = Point2f((src.cols() / 2).toFloat(), (src.rows() / 2).toFloat())
        // 获取旋转矩阵
        val matrix = opencv_imgproc.getRotationMatrix2D(center, 0.0, 0.5)
        // 应用透视变换
        val images = Mat()
        opencv_imgproc.warpAffine(src, images, matrix, src.size())
        // 显示结果
        opencv_highgui.imshow("REVOLVE", images)
        opencv_highgui.waitKey(0)
    }

    /**
     * 仿射变换 图像裁剪
     *
     * @since 2025年1月20日12点33分
     */
    private fun cropping1() {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")
        if (src == null || src.empty()) {
            return
        }
        // 定义裁剪区域
        val rect = Rect(100, 100, 400, 200)
        // 应用透视变换
        val images = Mat(src, rect)
        // 显示结果
        opencv_highgui.imshow("ORACLE", src)
        opencv_highgui.imshow("CROPPING", images)
        opencv_highgui.waitKey(0)
    }


    /**
     * 仿射变换 图像裁剪
     *
     * @since 2025年1月20日12点33分
     */
    private fun cropping1(type: Int) {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")
        if (src == null || src.empty()) {
            return
        }
        val dst = Mat()
        opencv_imgproc.getRectSubPix(
            src,
            Size(400, 200), // 裁剪大小
            Point2f((src.rows() / 2.0).toFloat(), (src.cols() / 2.0).toFloat()), // 裁剪图片中心
            dst
        )
        // 显示ROI
        opencv_highgui.imshow("src", src)
        opencv_highgui.imshow("dst", dst)
        opencv_highgui.waitKey(0)
    }

}