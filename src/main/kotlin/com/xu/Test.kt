package com.xu.com.xu

import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgcodecs
import org.bytedeco.opencv.opencv_core.Mat

fun main() {
    Loader.load(opencv_core::class.java)
    val mat1 = opencv_imgcodecs.imread("C:\\Users\\xuyq\\Desktop\\1.png")
    val mat2 = opencv_imgcodecs.imread("C:\\Users\\xuyq\\Desktop\\1.png")
    val image = Mat()
    opencv_core.hconcat(mat1, mat2, image)
    opencv_highgui.imshow("NEW", image)
    opencv_highgui.waitKey(100)
}