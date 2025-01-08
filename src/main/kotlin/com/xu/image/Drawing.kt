package com.xu.com.xu.image

import org.bytedeco.javacpp.Pointer
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point
import org.bytedeco.opencv.opencv_core.Scalar
import org.bytedeco.opencv.opencv_highgui.MouseCallback
import org.opencv.core.Core
import java.io.File
import java.util.*

object Drawing {

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

    // 使用 Kotlin 的数据类来存储鼠标状态
    data class MouseState(
        val image: Mat,
        var second: Point = Point(),
        var first: Point = Point(),
        var drawing: Boolean = false
    )

    @JvmStatic
    fun main(args: Array<String>) {
        change()
    }

    private fun change() {
        // 创建画布(白色背景)
        val image = Mat(700, 1000, opencv_core.CV_8UC3, Scalar(255.0, 255.0, 255.0, 0.0))
        val windowName = "Kotlin Mouse Drawing"

        // 创建窗口
        opencv_highgui.namedWindow(windowName, opencv_highgui.WINDOW_AUTOSIZE)

        // 创建状态对象
        val state = MouseState(image)

        // 创建鼠标回调对象
        val mouseCallback = object : MouseCallback() {
            override fun call(event: Int, x: Int, y: Int, flags: Int, params: Pointer?) {
                when (event) {
                    opencv_highgui.EVENT_LBUTTONDOWN -> {
                        state.drawing = true
                        state.first.x(x)
                        state.first.y(y)
                    }

                    opencv_highgui.EVENT_MOUSEMOVE -> {
                        if (state.drawing) {
                            state.second.x(x)
                            state.second.y(y)
                            // 画线(红色)
                            opencv_imgproc.line(
                                image,
                                state.first,
                                state.second,
                                Scalar(0.0, 0.0, 255.0, 0.0)
                            )
                            // 更新起点
                            state.first.x(state.second.x())
                            state.first.y(state.second.y())
                        }
                    }

                    opencv_highgui.EVENT_LBUTTONUP -> {
                        state.drawing = false
                    }
                }
            }
        }

        // 设置鼠标回调
        opencv_highgui.setMouseCallback(windowName, mouseCallback, null)

        // 主循环
        while (true) {
            opencv_highgui.imshow(windowName, image)
            if (opencv_highgui.waitKey(1).toChar() == 27.toChar()) {
                break
            }
        }
    }

}