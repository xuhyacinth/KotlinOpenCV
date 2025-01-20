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
import org.bytedeco.opencv.opencv_core.Rect
import org.bytedeco.opencv.opencv_core.Scalar
import org.bytedeco.opencv.opencv_core.Size
import org.bytedeco.opencv.opencv_highgui.MouseCallback

object Cropping {

    init {
        Loader.load(opencv_core::class.java)
    }

    // 使用 Kotlin 的数据类来存储鼠标状态
    data class MouseState(
        val image: Mat
    ) {
        lateinit var temp: Mat
        var second: Point = Point()
        var first: Point = Point()
        var drawing: Boolean = false
    }

    @JvmStatic
    fun main(args: Array<String>) {
        roi()
    }

    /**
     * 裁切
     *
     * @since 2025年1月20日12点33分
     */
    private fun roi() {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")
        if (src == null || src.empty()) {
            return
        }
        val (size, point) = select(src)
        println("${point.x()},${point.x()}")
        val dst = Mat()
        opencv_imgproc.getRectSubPix(
            src,
            size, // 裁剪大小
            Point2f(point), // 裁剪图片中心
            dst
        )
        // 显示ROI
        opencv_highgui.imshow("src", src)
        opencv_highgui.imshow("dst", dst)
        opencv_highgui.waitKey(0)
    }

    /**
     * 仿射变换 图像裁剪
     *
     * @since 2025年1月20日12点33分
     */
    private fun roi2() {
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
    private fun roi3(type: Int) {
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

    /**
     * 画矩形
     *
     * @since 2025年1月20日12点33分
     */
    private fun select(image: Mat): Pair<Size, Point2f> {
        val window = "Drawing"
        // 创建窗口
        opencv_highgui.namedWindow(window, opencv_highgui.WINDOW_AUTOSIZE)
        // 创建状态对象
        val state = MouseState(image)
        // 用于保存临时画布
        state.temp = image.clone()
        // 创建鼠标回调对象
        val callback = object : MouseCallback() {
            override fun call(event: Int, x: Int, y: Int, flags: Int, params: Pointer?) {
                when (event) {
                    // 鼠标按钮按下
                    opencv_highgui.EVENT_LBUTTONDOWN -> {
                        state.drawing = true
                        state.first.x(x)
                        state.first.y(y)
                    }
                    // 鼠标移动
                    opencv_highgui.EVENT_MOUSEMOVE -> {
                        if (state.drawing) {
                            // 复制原始画布
                            state.temp = state.image.clone()
                            state.second.x(x)
                            state.second.y(y)
                            // 在临时画布上绘制矩形(蓝色预览)
                            opencv_imgproc.rectangle(
                                state.temp,
                                Rect(Point(state.first), Point(state.second)),
                                Scalar(255.0, 0.0, 0.0, 0.0)
                            )
                            // 显示预览
                            opencv_highgui.imshow(window, state.temp)
                        }
                    }
                    // 鼠标按钮回弹
                    opencv_highgui.EVENT_LBUTTONUP -> {
                        state.drawing = false
                        state.second.x(x)
                        state.second.y(y)
                        // 在最终画布上绘制矩形(绿色)
                        opencv_imgproc.rectangle(
                            state.image,
                            Rect(Point(state.first), Point(state.second)),
                            Scalar(0.0, 255.0, 0.0, 0.0)
                        )
                        // 显示最终画布
                        opencv_highgui.imshow(window, state.image)
                    }
                }
            }
        }
        // 设置鼠标回调
        opencv_highgui.setMouseCallback(window, callback, null)
        // 主循环
        while (true) {
            opencv_highgui.imshow(window, state.temp)
            if (opencv_highgui.waitKey(1).toChar() == 27.toChar()) {
                opencv_highgui.destroyWindow(window)
                return Pair(
                    Size(
                        state.second.x() - state.first.x(),
                        state.second.y() - state.first.y()
                    ),
                    Point2f(
                        (0.5 * state.second.x() + 0.5 * state.first.x()).toFloat(),
                        (0.5 * state.second.y() + 0.5 * state.first.y()).toFloat()
                    )
                )
            }
        }
    }

}