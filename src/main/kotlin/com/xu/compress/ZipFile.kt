package com.xu.com.xu.compress

import cn.hutool.core.util.CharsetUtil
import cn.hutool.extra.compress.CompressUtil
import java.io.File

fun main() {
    val file = File("lib/data/image/predict.7z")
    println(file.exists())
    if (!file.exists()) {
        return
    }

    CompressUtil.createExtractor(
        CharsetUtil.defaultCharset(),
        file
    ).extract(File("lib/data/image/predict/"))
}
