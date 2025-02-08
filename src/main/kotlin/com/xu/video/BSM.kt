package com.xu.com.xu.video

import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_highgui
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.global.opencv_video
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.MatVector
import org.bytedeco.opencv.opencv_core.Size
import org.bytedeco.opencv.opencv_videoio.VideoCapture

object BSM {

    init {
        Loader.load(opencv_core::class.java)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        flow()
    }

    fun flow() {
        // 打开视频流
        val videoCapture = VideoCapture("lib/video/video_003.avi")
        if (!videoCapture.isOpened) {
            println("无法打开视频文件！")
            return
        }

        val prevGray = Mat()  // 存储上一帧灰度图像
        val gray = Mat()      // 当前帧灰度图像
        val flow = Mat()      // 存储光流结果

        val org = Mat()       // 存储当前原始帧
        val mask = Mat()      // 用于存储前景掩码

        println("按 'q' 键退出程序...")

        while (true) {
            // 读取当前帧
            videoCapture.read(org)
            if (org.empty()) break
            // 转换为灰度图
            opencv_imgproc.cvtColor(org, gray, opencv_imgproc.COLOR_BGR2GRAY)
            if (!prevGray.empty()) {
                // 计算光流
                opencv_video.calcOpticalFlowFarneback(
                    prevGray, gray, flow,
                    0.5, // 金字塔尺度
                    3,   // 金字塔层数
                    15,  // 窗口大小
                    3,   // 迭代次数
                    5,   // 多项式像素邻域大小
                    1.2, // 多项式sigma
                    0
                )
                // 计算光流的幅度
                val channels = MatVector()
                opencv_core.split(flow, channels) // 拆分 X / Y 分量
                val mag = Mat()
                opencv_core.magnitude(channels[0], channels[1], mag)
                // 归一化幅度（norm 的类型为 CV_32F）
                val norm = Mat()
                opencv_core.normalize(mag, norm, 0.0, 255.0, opencv_core.NORM_MINMAX, -1, Mat())
                // 将 norm 转换为 8 位图像（CV_8U）
                val norm8u = Mat()
                norm.convertTo(norm8u, opencv_core.CV_8U)
                // 进行二值化处理，提取运动区域（mask 的类型为 CV_8UC1）
                opencv_imgproc.threshold(norm8u, mask, 25.0, 255.0, opencv_imgproc.THRESH_BINARY)
                // 形态学处理去噪声
                val kernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, Size(3, 3))
                opencv_imgproc.morphologyEx(mask, mask, opencv_imgproc.MORPH_CLOSE, kernel)
                // 将 mask 转换为 3 通道图像以匹配 org（comm 的类型为 CV_8UC3）
                val comm = Mat()
                opencv_imgproc.cvtColor(mask, comm, opencv_imgproc.COLOR_GRAY2BGR)
                // 确保大小匹配
                if (comm.size() != org.size()) {
                    opencv_imgproc.resize(comm, comm, org.size())
                }
                // 进行拼接显示
                val show = Mat()
                opencv_core.hconcat(org, comm, show)
                opencv_highgui.imshow("Optical Flow BSM", show)
            }
            // 复制当前帧用于下一次计算
            gray.copyTo(prevGray)
            // 检查用户按键
            if (opencv_highgui.waitKey(30) == 'q'.code) break
        }
        // 释放资源
        videoCapture.release()
        opencv_highgui.destroyAllWindows()
    }

    fun gmm() {
        // 打开视频流（可使用摄像头或者视频文件路径）
        val videoCapture = VideoCapture(0)
        videoCapture.open("lib/video/video_003.avi")
        // 创建背景减除器
        val bg = opencv_video.createBackgroundSubtractorMOG2().apply {
            history = 500         // 历史帧数
            varThreshold = 16.0   // 方差阈值
            detectShadows = true  // 检测阴影
        }
        // 创建Mat对象用于存储视频帧和结果
        val org = Mat()
        val dst = Mat()

        println("按'q'键退出程序")

        while (true) {
            // 读取当前帧
            videoCapture.read(org)
            if (org.empty()) break
            // 应用背景减除器
            bg.apply(org, dst)
            // 可选：对前景蒙版进行一些后处理（如形态学操作）
            val kernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, Size(1, 1))
            opencv_imgproc.morphologyEx(dst, dst, opencv_imgproc.MORPH_CLOSE, kernel)
            // 确保通道匹配
            val mat = Mat()
            opencv_imgproc.cvtColor(dst, mat, opencv_imgproc.COLOR_GRAY2BGR)
            // 确保大小匹配
            if (dst.size() != org.size()) {
                opencv_imgproc.resize(mat, mat, org.size())
            }
            // 进行拼接
            val show = Mat()
            opencv_core.hconcat(org, mat, show)
            opencv_highgui.imshow("BSM", show)
            // 检查用户按键
            if (opencv_highgui.waitKey(30) == 'q'.code) break
        }
        // 释放资源
        videoCapture.release()
        opencv_highgui.destroyAllWindows()
    }

}