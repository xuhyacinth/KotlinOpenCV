package com.xu.com.xu.image

import org.bytedeco.javacpp.Loader
import org.bytedeco.javacpp.Pointer
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgcodecs
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point
import org.bytedeco.opencv.opencv_core.Rect
import org.bytedeco.opencv.opencv_core.Scalar
import org.bytedeco.opencv.opencv_highgui.MouseCallback

object Drawing {

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
        //line()
        rectangle()
        //point()
    }

    /**
     * 画矩形
     *
     * @since 2025年1月20日12点33分
     */
    private fun rectangle() {
        // 创建画布(白色背景)
        val image = Mat(700, 1300, opencv_core.CV_8UC3, Scalar(255.0, 255.0, 255.0, 0.0))
        val window = "Rectangle Drawing"
        // 创建窗口
        opencv_highgui.namedWindow(window, opencv_highgui.WINDOW_AUTOSIZE)
        // 创建状态对象
        val state = MouseState(image)
        state.temp = image.clone() // 用于保存临时画布
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
                break
            }
        }
    }

    /**
     * 鼠标画圆形
     *
     * @since 2025年1月20日12点33分
     */
    private fun point() {
        val image = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")
        if (image == null || image.empty()) {
            return
        }
        val window = "Drawing"
        // 创建窗口
        opencv_highgui.namedWindow(window, opencv_highgui.WINDOW_AUTOSIZE)
        // 创建鼠标回调对象
        val callback = object : MouseCallback() {
            override fun call(event: Int, x: Int, y: Int, flags: Int, params: Pointer?) {
                when (event) {
                    opencv_highgui.EVENT_LBUTTONDOWN -> {
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
            if (opencv_highgui.waitKey(1).toChar() == 27.toChar()) {
                break
            }
        }
    }

    /**
     * 鼠标画线条
     *
     * @since 2025年1月20日12点33分
     */
    private fun line() {
        // 创建画布(白色背景)
        val image = Mat(700, 1300, opencv_core.CV_8UC3, Scalar(255.0, 255.0, 255.0, 0.0))
        val window = "Drawing"
        // 创建窗口
        opencv_highgui.namedWindow(window, opencv_highgui.WINDOW_AUTOSIZE)
        // 创建状态对象
        val state = MouseState(image)
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
                    // 移动
                    opencv_highgui.EVENT_MOUSEMOVE -> {
                        if (state.drawing) {
                            state.second.x(x)
                            state.second.y(y)
                            // 画线(红色)
                            opencv_imgproc.line(image, state.first, state.second, Scalar(0.0, 0.0, 255.0, 0.0))
                            // 更新起点
                            state.first.x(state.second.x())
                            state.first.y(state.second.y())
                        }
                    }
                    // 鼠标按钮回弹
                    opencv_highgui.EVENT_LBUTTONUP -> {
                        state.drawing = false
                    }
                }
            }
        }
        // 设置鼠标回调
        opencv_highgui.setMouseCallback(window, callback, null)
        // 主循环
        while (true) {
            opencv_highgui.imshow(window, image)
            if (opencv_highgui.waitKey(1).toChar() == 27.toChar()) {
                break
            }
        }
    }

}