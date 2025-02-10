package com.xu.com.xu.tracking

import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point
import org.bytedeco.opencv.opencv_core.Rect
import org.bytedeco.opencv.opencv_core.Scalar
import org.bytedeco.opencv.opencv_video.Tracker
import org.bytedeco.opencv.opencv_video.TrackerMIL
import org.bytedeco.opencv.opencv_videoio.VideoCapture


object Tracking {

    init {
        Loader.load(opencv_core::class.java)
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
        //val tracker: TrackerCSRT = TrackerCSRT.create()
        val tracker: Tracker = TrackerMIL.create()
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

