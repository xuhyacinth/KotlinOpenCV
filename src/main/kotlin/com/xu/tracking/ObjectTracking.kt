package com.xu.com.xu.tracking

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point
import org.bytedeco.opencv.opencv_core.Rect
import org.bytedeco.opencv.opencv_core.Scalar
import org.bytedeco.opencv.opencv_video.Tracker
import org.bytedeco.opencv.opencv_video.TrackerGOTURN
import org.bytedeco.opencv.opencv_videoio.VideoCapture
import org.opencv.core.Core
import java.io.File
import java.util.*

object ObjectTracking {


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
        val video = VideoCapture("lib/data/video/cup.mp4")
        // 第一帧影像
        val first = Mat()
        video.read(first)
        // 鼠标框选区域
        val roi: Rect? = opencv_highgui.selectROI("roi", first, false, false, false)
        opencv_highgui.destroyWindow("roi")
        // 创建跟踪器
        var param = TrackerGOTURN.Params()
        param.modelTxt(BytePointer("lib/goturn/goturn.prototxt", "UTF-8"))
        param.modelBin(BytePointer("lib/goturn/goturn.caffemodel", "UTF-8"))
        val tracker: Tracker = TrackerGOTURN.create(param)
        tracker.init(first, roi)
        while (video.read(first)) {
            // 更新跟踪器
            val ok = tracker.update(first, roi)
            if (ok) {
                opencv_imgproc.rectangle(first, roi, Scalar(Point()))
            } else {
                println("跟踪失败")
            }
            opencv_highgui.imshow("Tracking", first)
            if (opencv_highgui.waitKey(1) == 27) {
                break
            }
        }
        video.release()
        opencv_highgui.destroyAllWindows()
    }

}