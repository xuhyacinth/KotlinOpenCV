//package com.xu.com.xu.audio
//
//
//import org.bytedeco.ffmpeg.avcodec.AVCodecContext
//import org.bytedeco.ffmpeg.avcodec.AVPacket
//import org.bytedeco.ffmpeg.avformat.AVFormatContext
//import org.bytedeco.ffmpeg.avformat.AVIOContext
//import org.bytedeco.ffmpeg.avutil.AVDictionary
//import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MP3
//import org.bytedeco.ffmpeg.global.avcodec.av_packet_unref
//import org.bytedeco.ffmpeg.global.avcodec.avcodec_find_decoder
//import org.bytedeco.ffmpeg.global.avcodec.avcodec_find_encoder
//import org.bytedeco.ffmpeg.global.avcodec.avcodec_free_context
//import org.bytedeco.ffmpeg.global.avcodec.avcodec_open2
//import org.bytedeco.ffmpeg.global.avcodec.avcodec_parameters_from_context
//import org.bytedeco.ffmpeg.global.avcodec.avcodec_parameters_to_context
//import org.bytedeco.ffmpeg.global.avcodec.avcodec_receive_frame
//import org.bytedeco.ffmpeg.global.avcodec.avcodec_receive_packet
//import org.bytedeco.ffmpeg.global.avcodec.avcodec_send_frame
//import org.bytedeco.ffmpeg.global.avcodec.avcodec_send_packet
//import org.bytedeco.ffmpeg.global.avformat.AVFMT_NOFILE
//import org.bytedeco.ffmpeg.global.avformat.AVIO_FLAG_WRITE
//import org.bytedeco.ffmpeg.global.avformat.av_interleaved_write_frame
//import org.bytedeco.ffmpeg.global.avformat.av_read_frame
//import org.bytedeco.ffmpeg.global.avformat.av_write_trailer
//import org.bytedeco.ffmpeg.global.avformat.avformat_alloc_output_context2
//import org.bytedeco.ffmpeg.global.avformat.avformat_close_input
//import org.bytedeco.ffmpeg.global.avformat.avformat_find_stream_info
//import org.bytedeco.ffmpeg.global.avformat.avformat_free_context
//import org.bytedeco.ffmpeg.global.avformat.avformat_network_init
//import org.bytedeco.ffmpeg.global.avformat.avformat_new_stream
//import org.bytedeco.ffmpeg.global.avformat.avformat_open_input
//import org.bytedeco.ffmpeg.global.avformat.avformat_write_header
//import org.bytedeco.ffmpeg.global.avformat.avio_closep
//import org.bytedeco.ffmpeg.global.avformat.avio_open
//import org.bytedeco.ffmpeg.global.avutil.AVMEDIA_TYPE_AUDIO
//import org.bytedeco.ffmpeg.global.avutil.AV_CH_LAYOUT_STEREO
//import org.bytedeco.ffmpeg.global.avutil.AV_LOG_ERROR
//import org.bytedeco.ffmpeg.global.avutil.AV_ROUND_NEAR_INF
//import org.bytedeco.ffmpeg.global.avutil.AV_ROUND_PASS_MINMAX
//import org.bytedeco.ffmpeg.global.avutil.AV_SAMPLE_FMT_FLTP
//import org.bytedeco.ffmpeg.global.avutil.AV_TIME_BASE
//import org.bytedeco.ffmpeg.global.avutil.av_frame_alloc
//import org.bytedeco.ffmpeg.global.avutil.av_frame_get_buffer
//import org.bytedeco.ffmpeg.global.avutil.av_frame_make_writable
//import org.bytedeco.ffmpeg.global.avutil.av_log_set_level
//import org.bytedeco.ffmpeg.global.avutil.av_rescale_q
//import org.bytedeco.ffmpeg.global.avutil.av_rescale_q_rnd
//import org.bytedeco.ffmpeg.global.swresample.swr_convert
//import org.bytedeco.javacpp.PointerPointer
//
//fun convertAudioToMp3(inputPath: String, outputPath: String) {
//    av_log_set_level(AV_LOG_ERROR)
//
//    avformat_network_init()
//
//    val inputFormatContext = AVFormatContext(null)
//    if (avformat_open_input(inputFormatContext, inputPath, null, null) < 0) {
//        throw RuntimeException("Could not open input file: $inputPath")
//    }
//
//    if (avformat_find_stream_info(inputFormatContext, null as PointerPointer<*>?) < 0) {
//        throw RuntimeException("Could not find stream information")
//    }
//
//    val outputFormatContext = AVFormatContext(null)
//    if (avformat_alloc_output_context2(outputFormatContext, null, "mp3", outputPath) < 0) {
//        throw RuntimeException("Could not create output context")
//    }
//
//    val inputStream = inputFormatContext.streams(0)
//    val decoder = avcodec_find_decoder(inputStream.codecpar().codec_id())
//    val decoderContext = AVCodecContext(null)
//    avcodec_parameters_to_context(decoderContext, inputStream.codecpar())
//    avcodec_open2(decoderContext, decoder, null as AVDictionary?)
//
//    val encoder = avcodec_find_encoder(AV_CODEC_ID_MP3)
//    val encoderContext = AVCodecContext(null)
//    encoderContext.bit_rate(128000)
//    encoderContext.sample_rate(44100)
//    encoderContext.channel_layout(AV_CH_LAYOUT_STEREO.toLong())
//    encoderContext.channels(2)
//    encoderContext.sample_fmt(AV_SAMPLE_FMT_FLTP)
//    encoderContext.time_base().num(1)
//    encoderContext.time_base().den(44100)
//
//    if (avcodec_open2(encoderContext, encoder, null as AVDictionary?) < 0) {
//        throw RuntimeException("Could not open encoder")
//    }
//
//    val outputStream = avformat_new_stream(outputFormatContext, null)
//    avcodec_parameters_from_context(outputStream.codecpar(), encoderContext)
//
//    if ((outputFormatContext.oformat().flags() and AVFMT_NOFILE) == 0) {
//        val pb = AVIOContext(null)
//        if (avio_open(pb, outputPath, AVIO_FLAG_WRITE) < 0) {
//            throw RuntimeException("Could not open output file: $outputPath")
//        }
//        outputFormatContext.pb(pb)
//    }
//
//    if (avformat_write_header(outputFormatContext, null as AVDictionary?) < 0) {
//        throw RuntimeException("Could not write header")
//    }
//
//    val inputFrame = av_frame_alloc()
//    val outputFrame = av_frame_alloc()
//    outputFrame.nb_samples(encoderContext.frame_size())
//    outputFrame.format(encoderContext.sample_fmt())
//    outputFrame.channel_layout(encoderContext.channel_layout())
//    av_frame_get_buffer(outputFrame, 0)
//
//    val packet = AVPacket()
//    val encodedPacket = AVPacket()
//
//    while (av_read_frame(inputFormatContext, packet) >= 0) {
//        if (packet.stream_index() == 0) {
//            if (avcodec_send_packet(decoderContext, packet) < 0) {
//                break
//            }
//
//            while (avcodec_receive_frame(decoderContext, inputFrame) >= 0) {
//                av_frame_make_writable(outputFrame)
//                swr_convert(
//                    null, outputFrame.data(), outputFrame.nb_samples(),
//                    inputFrame.data(), inputFrame.nb_samples()
//                )
//
//                outputFrame.pts(av_rescale_q(inputFrame.pts(), inputStream.time_base(), encoderContext.time_base()))
//
//                if (avcodec_send_frame(encoderContext, outputFrame) < 0) {
//                    break
//                }
//
//                while (avcodec_receive_packet(encoderContext, encodedPacket) >= 0) {
//                    encodedPacket.stream_index(0)
//                    encodedPacket.pts(
//                        av_rescale_q_rnd(
//                            encodedPacket.pts(), encoderContext.time_base(),
//                            outputStream.time_base(), AV_ROUND_NEAR_INF or AV_ROUND_PASS_MINMAX
//                        )
//                    )
//                    encodedPacket.dts(
//                        av_rescale_q_rnd(
//                            encodedPacket.dts(), encoderContext.time_base(),
//                            outputStream.time_base(), AV_ROUND_NEAR_INF or AV_ROUND_PASS_MINMAX
//                        )
//                    )
//                    encodedPacket.duration(
//                        av_rescale_q(
//                            encodedPacket.duration(),
//                            encoderContext.time_base(),
//                            outputStream.time_base()
//                        )
//                    )
//
//                    av_interleaved_write_frame(outputFormatContext, encodedPacket)
//                    av_packet_unref(encodedPacket)
//                }
//            }
//        }
//        av_packet_unref(packet)
//    }
//
//    av_write_trailer(outputFormatContext)
//
//    avcodec_free_context(decoderContext)
//    avcodec_free_context(encoderContext)
//    avformat_close_input(inputFormatContext)
//    if ((outputFormatContext.oformat().flags() and AVFMT_NOFILE) == 0) {
//        avio_closep(outputFormatContext.pb())
//    }
//    avformat_free_context(outputFormatContext)
//}
//
//fun main() {
//    val inputFile = "C:\\Users\\hyacinth\\Desktop\\3.wav"  // 替换为你的输入文件路径
//    val outputFile = "C:\\Users\\hyacinth\\Desktop\\3.mp3"  // 替换为你想要的输出文件路径
//
//    convertAudioToMp3(inputFile, outputFile)
//    println("Conversion completed: $outputFile")
//}
////
////
////object Audio {
////
////    @JvmStatic
////    fun main(args: Array<String>) {
////        val path = "D:\\Kugou\\KugouMusic\\张敬轩 - 酷爱.mp3"
////
////        av_log_set_level(AV_LOG_ERROR)
////
////        val formatContext = AVFormatContext(null)
////        if (avformat_open_input(formatContext, path, null, null) != 0) {
////            println("无法打开文件")
////            return
////        }
////
////        if (avformat_find_stream_info(formatContext, null as AVDictionary?) < 0) {
////            println("无法找到流信息")
////            return
////        }
////
////        val duration = formatContext.duration() / AV_TIME_BASE
////        val bitRate = formatContext.bit_rate() / 1000
////
////        println("录音地址: $path")
////        println("录音时长: ${duration}秒")
////        println("录音比特率: ${bitRate}kbps")
////
////        for (i in 0 until formatContext.nb_streams()) {
////            val stream = formatContext.streams(i)
////            val codecpar = stream.codecpar()
////
////            when (codecpar.codec_type()) {
////                AVMEDIA_TYPE_AUDIO -> {
////                    println("音频信息:")
////                    println("  采样率: ${codecpar.sample_rate()}Hz")
////                    println("  声道数: ${codecpar.channels()}")
////                }
////            }
////        }
////
////        avformat_close_input(formatContext)
////    }
////
////}