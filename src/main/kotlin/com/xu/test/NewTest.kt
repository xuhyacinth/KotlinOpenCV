package com.xu.com.xu.test

import org.opencv.highgui.HighGui
import org.opencv.imgcodecs.Imgcodecs
import java.io.File
import java.util.*

/**
 * Java OpenCV
 *
 * @author hyacinth
 * @since 2025年3月20日12点28分
 */
object NewTest {

    init {
        val os = System.getProperty("os.name")
        val type = System.getProperty("sun.arch.data.model")
        if (os.uppercase(Locale.getDefault()).contains("WINDOWS")) {
            if (type.endsWith("64")) {
                val lib = File("lib\\opencv-4.10\\x64\\" + System.mapLibraryName("opencv_java4100"))
                System.load(lib.absolutePath)
            } else {
                val lib = File("lib\\opencv-4.10\\x86\\" + System.mapLibraryName("opencv_java4100"))
                System.load(lib.absolutePath)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val src = Imgcodecs.imread("C:\\Users\\xuyq\\Desktop\\1.png")
        HighGui.imshow("T", src)
        HighGui.waitKey(0)
    }

}