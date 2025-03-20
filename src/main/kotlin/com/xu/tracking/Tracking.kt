package com.xu.com.xu.tracking

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point
import org.bytedeco.opencv.opencv_core.Rect
import org.bytedeco.opencv.opencv_core.Scalar
import org.bytedeco.opencv.opencv_tracking.TrackerCSRT
import org.bytedeco.opencv.opencv_tracking.TrackerKCF
import org.bytedeco.opencv.opencv_video.Tracker
import org.bytedeco.opencv.opencv_video.TrackerDaSiamRPN
import org.bytedeco.opencv.opencv_video.TrackerGOTURN
import org.bytedeco.opencv.opencv_video.TrackerMIL
import org.bytedeco.opencv.opencv_video.TrackerNano
import org.bytedeco.opencv.opencv_video.TrackerVit
import org.bytedeco.opencv.opencv_videoio.VideoCapture


object Tracking {

    init {
        Loader.load(opencv_core::class.java)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        MIL()
    }

    fun MIL() {
        val video = VideoCapture("lib/data/video/cup.mp4")
        // 第一帧影像
        val first = Mat()
        video.read(first)
        // 鼠标框选区域
        val roi: Rect? = opencv_highgui.selectROI("roi", first, false, false, false)
        opencv_highgui.destroyWindow("roi")
        // 创建跟踪器
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
            opencv_highgui.imshow("MIL", first)
            if (opencv_highgui.waitKey(1) == 27) {
                break
            }
        }
        video.release()
        opencv_highgui.destroyAllWindows()
    }

    fun KCF() {
        val video = VideoCapture("lib/data/video/cup.mp4")
        // 第一帧影像
        val first = Mat()
        video.read(first)
        // 鼠标框选区域
        val roi: Rect? = opencv_highgui.selectROI("roi", first, false, false, false)
        opencv_highgui.destroyWindow("roi")
        // 创建跟踪器
        val tracker: Tracker = TrackerKCF.create()
        tracker.init(first, roi)
        while (video.read(first)) {
            // 更新跟踪器
            val ok = tracker.update(first, roi)
            if (ok) {
                opencv_imgproc.rectangle(first, roi, Scalar(Point()))
            } else {
                println("跟踪失败")
            }
            opencv_highgui.imshow("KCF", first)
            if (opencv_highgui.waitKey(1) == 27) {
                break
            }
        }
        video.release()
        opencv_highgui.destroyAllWindows()
    }

    fun CSRT() {
        val video = VideoCapture("lib/data/video/cup.mp4")
        // 第一帧影像
        val first = Mat()
        video.read(first)
        // 鼠标框选区域
        val roi: Rect? = opencv_highgui.selectROI("roi", first, false, false, false)
        opencv_highgui.destroyWindow("roi")
        // 创建跟踪器
        val tracker: Tracker = TrackerCSRT.create()
        tracker.init(first, roi)
        while (video.read(first)) {
            // 更新跟踪器
            val ok = tracker.update(first, roi)
            if (ok) {
                opencv_imgproc.rectangle(first, roi, Scalar(Point()))
            } else {
                println("跟踪失败")
            }
            opencv_highgui.imshow("CSRT", first)
            if (opencv_highgui.waitKey(1) == 27) {
                break
            }
        }
        video.release()
        opencv_highgui.destroyAllWindows()
    }

    fun Nano() {
        val video = VideoCapture("lib/data/video/cup.mp4")
        // 第一帧影像
        val first = Mat()
        video.read(first)
        // 鼠标框选区域
        val roi: Rect? = opencv_highgui.selectROI("roi", first, false, false, false)
        opencv_highgui.destroyWindow("roi")
        // 创建跟踪器
        val tracker: Tracker = TrackerNano.create()
        tracker.init(first, roi)
        while (video.read(first)) {
            // 更新跟踪器
            val ok = tracker.update(first, roi)
            if (ok) {
                opencv_imgproc.rectangle(first, roi, Scalar(Point()))
            } else {
                println("跟踪失败")
            }
            opencv_highgui.imshow("Nano", first)
            if (opencv_highgui.waitKey(1) == 27) {
                break
            }
        }
        video.release()
        opencv_highgui.destroyAllWindows()
    }

    fun Vit() {
        val video = VideoCapture("lib/data/video/cup.mp4")
        // 第一帧影像
        val first = Mat()
        video.read(first)
        // 鼠标框选区域
        val roi: Rect? = opencv_highgui.selectROI("roi", first, false, false, false)
        opencv_highgui.destroyWindow("roi")
        // 创建跟踪器
        val tracker: Tracker = TrackerVit.create()
        tracker.init(first, roi)
        while (video.read(first)) {
            // 更新跟踪器
            val ok = tracker.update(first, roi)
            if (ok) {
                opencv_imgproc.rectangle(first, roi, Scalar(Point()))
            } else {
                println("跟踪失败")
            }
            opencv_highgui.imshow("CSRT", first)
            if (opencv_highgui.waitKey(1) == 27) {
                break
            }
        }
        video.release()
        opencv_highgui.destroyAllWindows()
    }

    fun DaSiamRPN() {
        val video = VideoCapture("lib/data/video/cup.mp4")
        // 第一帧影像
        val first = Mat()
        video.read(first)
        // 鼠标框选区域
        val roi: Rect? = opencv_highgui.selectROI("roi", first, false, false, false)
        opencv_highgui.destroyWindow("roi")
        // 创建跟踪器
        val tracker: Tracker = TrackerDaSiamRPN.create()
        tracker.init(first, roi)
        while (video.read(first)) {
            // 更新跟踪器
            val ok = tracker.update(first, roi)
            if (ok) {
                opencv_imgproc.rectangle(first, roi, Scalar(Point()))
            } else {
                println("跟踪失败")
            }
            opencv_highgui.imshow("CSRT", first)
            if (opencv_highgui.waitKey(1) == 27) {
                break
            }
        }
        video.release()
        opencv_highgui.destroyAllWindows()
    }

    fun GOTURN() {
        val video = VideoCapture("lib/data/video/cup.mp4")
        // 第一帧影像
        val first = Mat()
        video.read(first)
        // 鼠标框选区域
        val roi: Rect? = opencv_highgui.selectROI("roi", first, false, false, false)
        opencv_highgui.destroyWindow("roi")
        // 创建跟踪器
        val param = TrackerGOTURN.Params()
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
            opencv_highgui.imshow("CSRT", first)
            if (opencv_highgui.waitKey(1) == 27) {
                break
            }
        }
        video.release()
        opencv_highgui.destroyAllWindows()
    }

}

