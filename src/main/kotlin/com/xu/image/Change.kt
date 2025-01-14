package com.xu.com.xu.image

import org.bytedeco.javacpp.FloatPointer
import org.bytedeco.javacpp.IntPointer
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
import org.opencv.core.Core
import java.io.File
import java.util.*

object Change {

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
        change()
    }


    private fun change() {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\xuyq\\Desktop\\1.png")
        if (src == null || src.empty()) {
            return
        }

        val org = Point2f()
        org.put<IntPointer>(Point2f(0f, 0f))
        org.put<IntPointer>(Point2f(src.cols().toFloat(), 0f))
        org.put<IntPointer>(Point2f(0f, src.rows().toFloat()))
        org.put<IntPointer>(Point2f(src.cols().toFloat(), src.rows().toFloat()))


        val dst = Point2f()
        dst.put<IntPointer>(Point2f(src.cols().toFloat(), src.rows().toFloat()))
        dst.put<IntPointer>(Point2f(src.cols().toFloat() * 0.1f, src.rows().toFloat() * 0.8f))
        dst.put<IntPointer>(Point2f(src.cols().toFloat() * 0.7f, src.rows().toFloat() * 0.3f))
        dst.put<IntPointer>(Point2f(0f, 0f))

        // 获取透视变换矩阵
        val matrix = opencv_imgproc.getPerspectiveTransform(dst, org)

        // 应用透视变换
        val output = Mat(src.size())
        opencv_imgproc.warpPerspective(
            src,
            output,
            matrix,
            src.size()
        )

        // 显示结果
        opencv_highgui.imshow("Perspective Transform", output)
        opencv_highgui.waitKey(0)

    }


    private fun modify() {
        // 读取图像
        val src = opencv_imgcodecs.imread("C:\\Users\\xuyq\\Desktop\\11.png")
        if (src == null || src.empty()) {
            println("Error: Could not read the image.")
            return
        }

        println("Image width: ${src.cols()}")
        println("Image height: ${src.rows()}")


        val org = Point2f()
        org.put<FloatPointer>(Point2f(0f, 0f))
        org.put<FloatPointer>(Point2f(src.cols().toFloat(), 0f))
        org.put<FloatPointer>(Point2f(0f, src.rows().toFloat()))
        org.put<FloatPointer>(Point2f(src.cols().toFloat(), src.rows().toFloat()))

        // 创建源点矩阵
        val point1 = Mat(1, 4, opencv_core.CV_32FC2)  // 注意这里改为 1x4 矩阵
        val srcPoints = arrayOf(
            FloatPointer(Point2f(0f, 0f)),// 左上
            FloatPointer(Point2f(src.cols().toFloat(), 0f)),// 右上
            FloatPointer(Point2f(0f, src.rows().toFloat())),// 左下
            FloatPointer(Point2f(src.cols().toFloat(), src.rows().toFloat()))// 右下
        )
        var dst = Point2f()
        for (i in srcPoints.indices) {
            point1.ptr(0, i).put<FloatPointer>(srcPoints[i])
            org.put<FloatPointer>(Point2f(srcPoints[i]))
        }

        // 创建目标点矩阵
        val point2 = Mat(1, 4, opencv_core.CV_32FC2)  // 注意这里改为 1x4 矩阵
        val dstPoints = click(src)
        for (i in dstPoints.indices) {
            point2.ptr(0, i).put<FloatPointer>(dstPoints[i])
        }

        // 获取透视变换矩阵
        val matrix = opencv_imgproc.getPerspectiveTransform(dst, org)

        // 应用透视变换
        val output = Mat()
        opencv_imgproc.warpPerspective(
            src,
            output,
            matrix,
            src.size()
        )

        // 显示结果
        opencv_highgui.imshow("Perspective Transform", output)
        opencv_highgui.waitKey(0)

        // 释放资源
        srcPoints.forEach { it.deallocate() }
        dstPoints.forEach { it.deallocate() }

    }


    private fun click(image: Mat): Array<FloatPointer> {
        // 创建画布(白色背景)
        val windowName = "Kotlin Mouse Drawing"

        // 创建窗口
        opencv_highgui.namedWindow(windowName, opencv_highgui.WINDOW_AUTOSIZE)

        var index = 0
        val dstPoints = Array(
            size = 4,
            init = { FloatPointer(Point2f(0f, 0f)) }
        )
        // 创建鼠标回调对象
        val mouseCallback = object : MouseCallback() {
            override fun call(event: Int, x: Int, y: Int, flags: Int, params: Pointer?) {
                when (event) {
                    opencv_highgui.EVENT_LBUTTONDOWN -> {
                        println("点击点: ($x, $y)")
                        dstPoints[index] = FloatPointer(Point2f(x.toFloat(), y.toFloat()))
                        index++
                        // 在原图上绘制点
                        opencv_imgproc.circle(
                            image, Point(x, y), 5,
                            Scalar(0.0, 0.0, 255.0, 0.0), -1, opencv_imgproc.LINE_AA, 0
                        )
                        opencv_highgui.imshow(windowName, image)
                    }
                }
            }
        }

        // 设置鼠标回调
        opencv_highgui.setMouseCallback(windowName, mouseCallback, null)

        // 主循环
        while (true) {
            opencv_highgui.imshow(windowName, image)
            if (opencv_highgui.waitKey(1).toChar() == 27.toChar() || index >= 4) {
                opencv_highgui.destroyWindow(windowName)
                break
            }
        }

        return dstPoints;
    }

}