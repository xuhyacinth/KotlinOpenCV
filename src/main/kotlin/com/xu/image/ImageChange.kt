package com.xu.com.xu.image

import org.bytedeco.javacpp.Loader
import org.bytedeco.javacpp.Pointer
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgcodecs
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point
import org.bytedeco.opencv.opencv_core.Point2f
import org.bytedeco.opencv.opencv_core.Scalar
import org.bytedeco.opencv.opencv_highgui.MouseCallback

object ImageChange {

    init {
        Loader.load(opencv_core::class.java)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        perspective(1)
    }

    /** 透视变换 */
    private fun perspective(type: Int) {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")
        if (src == null || src.empty()) {
            return
        }
        // 创建源点矩阵4个点
        val org = Mat(1, 4, opencv_core.CV_32FC2)
        org.ptr(0, 0).put<Pointer>(Point2f(0f, 0f))
        org.ptr(0, 1).put<Pointer>(Point2f(src.cols().toFloat(), 0f))
        org.ptr(0, 2).put<Pointer>(Point2f(src.cols().toFloat(), src.rows().toFloat()))
        org.ptr(0, 3).put<Pointer>(Point2f(0f, src.rows().toFloat()))
        // 创建目标点矩阵4个点
        val dst = Mat(1, 4, opencv_core.CV_32FC2)
        if (1 == type) {
            val target = click(src)
            for (i in target.indices) {
                dst.ptr(0, i).put<Pointer>(target[i])
            }
        } else {
            dst.ptr(0, 0).put<Pointer>(Point2f(21f, 20f))
            dst.ptr(0, 1).put<Pointer>(Point2f(953f, 74f))
            dst.ptr(0, 2).put<Pointer>(Point2f(847f, 574f))
            dst.ptr(0, 3).put<Pointer>(Point2f(109f, 643f))
        }
        // 获取透视变换矩阵
        val matrix = opencv_imgproc.getPerspectiveTransform(org, dst)
        // 应用透视变换
        val images = Mat()
        opencv_imgproc.warpPerspective(src, images, matrix, src.size())
        // 显示结果
        opencv_highgui.imshow("Perspective", images)
        opencv_highgui.waitKey(0)
    }

    /** 仿射变换 平移变换 */
    private fun affine() {
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
        opencv_highgui.imshow("Affine", images)
        opencv_highgui.waitKey(0)
    }

    /** 仿射变换 旋转变换 */
    private fun rotation() {
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

    private fun click(image: Mat): List<Point2f> {
        // 创建画布(白色背景)
        val window = "Click"
        // 创建窗口
        opencv_highgui.namedWindow(window, opencv_highgui.WINDOW_AUTOSIZE)
        val points = listOf<Point2f>().toMutableList()
        // 创建鼠标回调对象
        val callback = object : MouseCallback() {
            override fun call(event: Int, x: Int, y: Int, flags: Int, params: Pointer?) {
                when (event) {
                    opencv_highgui.EVENT_LBUTTONDOWN -> {
                        println("点击点: ($x, $y)")
                        points.add(Point2f(x.toFloat(), y.toFloat()))
                        // 在原图上绘制点
                        opencv_imgproc.circle(
                            image, Point(x, y), 5,
                            Scalar(0.0, 0.0, 255.0, 0.0), -1, opencv_imgproc.LINE_AA, 0
                        )
                        opencv_highgui.imshow(window, image)
                    }
                }
            }
        }
        // 设置鼠标回调
        opencv_highgui.setMouseCallback(window, callback, null)
        // 主循环
        while (true) {
            opencv_highgui.imshow(window, image)
            if (opencv_highgui.waitKey(1).toChar() == 27.toChar() || points.size >= 4) {
                opencv_highgui.destroyWindow(window)
                break
            }
        }
        return points
    }

}