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
import org.bytedeco.opencv.opencv_core.Size
import org.bytedeco.opencv.opencv_highgui.MouseCallback
import org.bytedeco.opencv.opencv_imgproc.Vec4fVector


object Circles {

    init {
        Loader.load(opencv_core::class.java)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        roi(1)
    }

    /** ROI */
    private fun roi(type:Int) {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\2.png")
        if (src == null || src.empty()) {
            return
        }
        click(src)
        val dst = Mat()
        opencv_imgproc.getRectSubPix(
            src,
            Size(100, 100), // 裁剪大小
            Point2f((src.rows() / 2.0).toFloat(), (src.cols() / 2.0).toFloat()), // 裁剪图片中心
            dst
        )
        // 显示ROI
        opencv_highgui.imshow("src", src)
        opencv_highgui.imshow("dst", dst)
        opencv_highgui.waitKey(0)
    }

    /** 圆形检测 */
    private fun circles() {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\2.png")
        if (src == null || src.empty()) {
            return
        }
        // 中值模糊(滤波-->平滑)
        val img = Mat()
        opencv_imgproc.medianBlur(src, img, 1)
        // 图片转灰色
        val gray = Mat()
        opencv_imgproc.cvtColor(img, gray, opencv_imgproc.COLOR_BGR2GRAY)
        // 霍夫变换-圆形检测
        val point = Vec4fVector()
        opencv_imgproc.HoughCircles(
            gray.getPointer(),
            point,
            opencv_imgproc.HOUGH_GRADIENT,
            1.0,
            20.0,
            50.0,
            30.0,
            20,
            40
        )
        println(point.get().size)

        // 遍历每个圆并绘制到图像上
        for (i in 0 until point.size()) {
            val circle = point[i]
            val x = circle[0].toInt() // 圆心 x 坐标
            val y = circle[1].toInt() // 圆心 y 坐标
            val radius = circle[2].toInt() // 半径

            // 绘制圆心
            opencv_imgproc.circle(
                src,
                Point(x, y),
                5,
                Scalar(0.0, 255.0, 0.0, 0.0), // 绿色圆心
                -1, // 填充
                opencv_imgproc.LINE_AA,
                0
            )

            // 绘制圆周
            opencv_imgproc.circle(
                src,
                Point(x, y),
                radius,
                Scalar(255.0, 0.0, 0.0, 0.0), // 红色圆周
                2, // 边框宽度
                opencv_imgproc.LINE_AA,
                0
            )
        }

        // 显示结果
        opencv_highgui.imshow("Detected Circles", src)
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