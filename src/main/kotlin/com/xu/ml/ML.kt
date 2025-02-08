package com.xu.com.xu.ml

import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_dnn
import org.bytedeco.opencv.global.opencv_imgcodecs
import org.bytedeco.opencv.opencv_core.MatVector
import org.bytedeco.opencv.opencv_core.Scalar
import org.bytedeco.opencv.opencv_core.Size

object ML {

    init {
        Loader.load(opencv_core::class.java)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // 加载模型
        val net = opencv_dnn.readNet("src/main/kotlin/com/xu/ml/frozen_east_text_detection.pb")

        // 读取图像 (使用默认的 IMREAD_COLOR 而不是 CV_32FC1)
        val image = opencv_imgcodecs.imread("C:\\Users\\xuyq\\Desktop\\22.png",opencv_core.CV_32F)
        if (image.empty()) {
            throw RuntimeException("Could not read image")
        }

        // 获取图像尺寸并调整到32的倍数
        val width = (image.cols() / 32) * 32
        val height = (image.rows() / 32) * 32

        // 预处理图像
        val blob = opencv_dnn.blobFromImage(
            image,                     // 输入图像
            1.0,                       // 缩放因子
            Size(width, height),       // 目标大小
            Scalar(123.68, 116.78, 103.94, 0.0), // 均值
            true,                      // swapRB
            false,                      // crop
            1
        )

        // 设置网络输入
        net.setInput(blob)

        // 定义输出层名称
        //val outNames = StringVector("feature_fusion/Conv_7/Sigmoid", "feature_fusion/concat_3")

        // 执行前向传播
        val output = MatVector()
        net.forward(output)

        // 获取检测结果
        val scores = output.get(0)
        val geometry = output.get(1)

        println("Detection completed successfully")
        println("Scores shape: ${scores.rows()} x ${scores.cols()}")
        println("Geometry shape: ${geometry.rows()} x ${geometry.cols()}")
    }

}