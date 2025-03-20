package com.xu.com.xu.test

import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgcodecs

/**
 * JavaCPP OpenCV
 *
 * @author hyacinth
 * @since 2025年3月20日12点28分
 */
object Test {

    init {
        Loader.load(opencv_core::class.java)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val src = opencv_imgcodecs.imread("C:\\Users\\hyacinth\\Desktop\\1.png")
        opencv_highgui.imshow("T", src)
        opencv_highgui.waitKey(0)
    }

}