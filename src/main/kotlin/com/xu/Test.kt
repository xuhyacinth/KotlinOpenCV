package com.xu.com.xu

import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.global.opencv_core


fun main() {
    Loader.load(opencv_core::class.java)
}