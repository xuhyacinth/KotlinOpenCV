package com.xu.com.xu.image

import org.opencv.core.Core
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.highgui.HighGui
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.util.*

object FaceDetect {

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
        face()
    }

    private fun face() {
        val facebook = CascadeClassifier("lib/opencv-4.9/data/haarcascades/haarcascade_frontalcatface_extended.xml")
        val image = Imgcodecs.imread("lib/data/image/2.png")
        val face = MatOfRect()
        facebook.detectMultiScale(image, face)
        val reacts = face.toArray()
        println("匹配到${reacts.size}个人脸")
        for (i in reacts.indices) {
            Imgproc.rectangle(
                image,
                Point(reacts[i].x.toDouble(), reacts[i].y.toDouble()),
                Point((reacts[i].x + reacts[i].width).toDouble(), (reacts[i].y + reacts[i].height).toDouble()),
                Scalar(0.0, 0.0, 255.0), 2
            )
            Imgproc.putText(
                image,
                i.toString(),
                Point(reacts[i].x.toDouble(), reacts[i].y.toDouble()),
                Imgproc.FONT_HERSHEY_SCRIPT_SIMPLEX,
                1.0,
                Scalar(0.0, 0.0, 255.0),
                2,
                Imgproc.LINE_AA,
                false
            )
        }
        HighGui.imshow("人脸识别", image)
        HighGui.waitKey(0)
    }

}