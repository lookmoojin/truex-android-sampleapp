package com.truedigital.features.tuned.service.music.exoutil

import android.annotation.SuppressLint
import com.google.android.exoplayer2.extractor.Extractor
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor
import com.google.android.exoplayer2.extractor.ts.AdtsExtractor

class StreamExtractorFactory : ExtractorsFactory {
    @SuppressLint("WrongConstant")
    @Synchronized
    override fun createExtractors(): Array<Extractor> = arrayOf(
        FragmentedMp4Extractor(),
        Mp4Extractor(),
        AdtsExtractor(AdtsExtractor.FLAG_ENABLE_CONSTANT_BITRATE_SEEKING),
        Mp3Extractor(Mp3Extractor.FLAG_ENABLE_CONSTANT_BITRATE_SEEKING)
    )
}
