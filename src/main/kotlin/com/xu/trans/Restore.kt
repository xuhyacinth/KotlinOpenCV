package com.xu.com.xu.trans

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

object Restore {

    init {
        Loader.load(opencv_core::class.java)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        restore(1)
    }

    /**
     * 透视变换 图像修改
     *
     * @since 2025年1月20日12点33分
     */
    private fun restore(type: Int) {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\11.png")
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
        val matrix = opencv_imgproc.getPerspectiveTransform(dst, org)
        // 应用透视变换
        val images = Mat()
        opencv_imgproc.warpPerspective(src, images, matrix, src.size())
        // 显示结果
        opencv_highgui.imshow("RESTORE", images)
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