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

object Change {

    init {
        Loader.load(opencv_core::class.java)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        modify()
    }


    private fun modify() {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\xuyq\\Desktop\\11.png")
        if (src == null || src.empty()) {
            return
        }

        // 创建源点矩阵
        val org = Point2f(4)
        // 左上
        org.position(0).put(Point2f(0f, 0f))
        // 右上
        org.position(1).put(Point2f(src.cols().toFloat(), 0f))
        // 左下
        org.position(2).put(Point2f(src.cols().toFloat(), src.rows().toFloat()))
        // 右下
        org.position(3).put(Point2f(0f, src.rows().toFloat()))

        // 创建目标点矩阵
        val dst = Point2f(4)
        val target = click(src)
        for (i in target.indices) {
            dst.position(i.toLong()).put(target[i])
        }

        // 获取透视变换矩阵
        val matrix = opencv_imgproc.getPerspectiveTransform(org, dst)

        // 应用透视变换
        val images = Mat()
        opencv_imgproc.warpPerspective(src, images, matrix, src.size())

        // 打印源点和目标点的坐标
        org.position(0)
        for (i in 0..3) {
            println("Source point $i: (${org.x()}, ${org.y()})")
            org.position((i + 1).toLong())
        }

        dst.position(0)
        for (i in 0..3) {
            println("Destination point $i: (${dst.x()}, ${dst.y()})")
            dst.position((i + 1).toLong())
        }

        println("Transformation Matrix:")
        for (i in 0 until matrix.rows()) {
            for (j in 0 until matrix.cols()) {
                print("${matrix.ptr(i, j).get()} ")
            }
            println()
        }

        // 显示结果
        opencv_highgui.imshow("DST", images)
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