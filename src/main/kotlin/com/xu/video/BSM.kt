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
        val capture = VideoCapture("lib/video/video_003.avi")
        if (!capture.isOpened) {
            return
        }

        val prev = Mat()  // 存储上一帧灰度图像
        val gray = Mat()      // 当前帧灰度图像
        val flow = Mat()      // 存储光流结果

        val org = Mat()       // 存储当前原始帧
        val mask = Mat()      // 用于存储前景掩码

        while (true) {
            // 读取当前帧
            capture.read(org)
            if (org.empty()) break
            // 转换为灰度图
            opencv_imgproc.cvtColor(org, gray, opencv_imgproc.COLOR_BGR2GRAY)
            if (!prev.empty()) {
                // 计算光流
                opencv_video.calcOpticalFlowFarneback(
                    prev, gray, flow,
                    0.5, // `pyrScale` 适当降低（0.4-0.6），减少背景干扰
                    3,   // `levels` 适当增加（2-4），提高检测范围
                    1,   // `windowSize` 增大（3→5），增强检测
                    20,  // `iterations` 增加迭代次数，提高准确度
                    5,   // `polyN`（5-7），适当提高减少噪声
                    1.2, // `polySigma` 增大（1.2 → 1.5），平滑光流
                    0    // `flags` 默认为 0
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
                val calc = Mat()
                norm.convertTo(calc, opencv_core.CV_8U)
                // 进行二值化处理，提取运动区域（mask 的类型为 CV_8UC1）
                //opencv_imgproc.threshold(calc, mask, 10.0, 255.0, opencv_imgproc.THRESH_BINARY)
                opencv_imgproc.adaptiveThreshold(
                    calc, mask, 255.0,
                    opencv_imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                    opencv_imgproc.THRESH_BINARY,
                    7, 2.0
                )
                // 形态学处理去噪声
                val kernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, Size(1, 1))
                opencv_imgproc.morphologyEx(mask, mask, opencv_imgproc.MORPH_OPEN, kernel)
                opencv_imgproc.morphologyEx(mask, mask, opencv_imgproc.MORPH_CLOSE, kernel)
                // 确保类型一致
                val comm = Mat()
                opencv_imgproc.cvtColor(mask, comm, opencv_imgproc.COLOR_GRAY2BGR)
                // 确保大小一致
                if (comm.size() != org.size()) {
                    opencv_imgproc.resize(comm, comm, org.size())
                }
                // 进行拼接显示
                val show = Mat()
                opencv_core.hconcat(org, comm, show)
                opencv_highgui.imshow("Optical Flow BSM", show)
            }
            // 复制当前帧用于下一次计算
            gray.copyTo(prev)
            // 检查用户按键
            if (opencv_highgui.waitKey(30) == 'q'.code) break
        }
        // 释放资源
        capture.release()
        opencv_highgui.destroyAllWindows()
    }

    fun gmm() {
        // 打开视频流（可使用摄像头或者视频文件路径）
        val capture = VideoCapture(0)
        capture.open("lib/video/video_003.avi")
        // 创建背景减除器
        val bg = opencv_video.createBackgroundSubtractorMOG2().apply {
            history = 500         // 历史帧数
            varThreshold = 16.0   // 方差阈值
            detectShadows = true  // 检测阴影
        }
        // 创建Mat对象用于存储视频帧和结果
        val org = Mat()
        val dst = Mat()

        while (true) {
            // 读取当前帧
            capture.read(org)
            if (org.empty()) break
            // 应用背景减除器
            bg.apply(org, dst)
            // 可选：对前景蒙版进行一些后处理（如形态学操作）
            val kernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, Size(1, 1))
            opencv_imgproc.morphologyEx(dst, dst, opencv_imgproc.MORPH_CLOSE, kernel)
            // 确保类型一致
            val mat = Mat()
            opencv_imgproc.cvtColor(dst, mat, opencv_imgproc.COLOR_GRAY2BGR)
            // 确保大小一致
            if (dst.size() != org.size()) {
                opencv_imgproc.resize(mat, mat, org.size())
            }
            // 进行拼接
            val show = Mat()
            opencv_core.hconcat(org, mat, show)
            opencv_highgui.imshow("MOG2 BSM", show)
            // 检查用户按键
            if (opencv_highgui.waitKey(30) == 'q'.code) break
        }
        // 释放资源
        capture.release()
        opencv_highgui.destroyAllWindows()
    }

    fun knn() {
        // 打开视频流（可使用摄像头或者视频文件路径）
        val capture = VideoCapture("lib/video/video_003.avi")
        if (!capture.isOpened) {
            return
        }
        // 创建 KNN 背景减除器
        val bg = opencv_video.createBackgroundSubtractorKNN().apply {
            history = 500         // 历史帧数
            dist2Threshold = 400.0 // 距离阈值，控制前景检测的敏感度
            detectShadows = true  // 是否检测阴影
        }
        // 创建 Mat 对象用于存储视频帧和结果
        val org = Mat()
        val dst = Mat()
        while (true) {
            // 读取当前帧
            capture.read(org)
            if (org.empty()) break
            // 应用背景减除器
            bg.apply(org, dst)
            // 可选：对前景蒙版进行一些后处理（如形态学操作）
            val kernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, Size(3, 3))
            opencv_imgproc.morphologyEx(dst, dst, opencv_imgproc.MORPH_CLOSE, kernel)
            // 确保类型一致
            val mat = Mat()
            opencv_imgproc.cvtColor(dst, mat, opencv_imgproc.COLOR_GRAY2BGR)
            // 确保大小一致
            if (dst.size() != org.size()) {
                opencv_imgproc.resize(mat, mat, org.size())
            }
            // 进行拼接
            val show = Mat()
            opencv_core.hconcat(org, mat, show)
            // 显示结果
            opencv_highgui.imshow("KNN BSM", show)
            // 检查用户按键
            if (opencv_highgui.waitKey(30) == 'q'.code) break
        }
        // 释放资源
        capture.release()
        opencv_highgui.destroyAllWindows()
    }

}