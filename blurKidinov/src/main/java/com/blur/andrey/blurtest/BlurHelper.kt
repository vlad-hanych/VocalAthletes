package com.blur.andrey.blurtest

import android.content.Context
import android.renderscript.RenderScript
import android.view.TextureView
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.parameter.selector.AspectRatioSelectors
import io.fotoapparat.parameter.selector.LensPositionSelectors
import io.fotoapparat.parameter.selector.SizeSelectors
import io.fotoapparat.view.CameraView

/**
 * Created by Vlad Aspire Hanych on 03.04.2018.
 */
class BlurHelper {

    companion object {
        lateinit var instance: BlurHelper
    }

    init {
        instance = this
    }

    private lateinit var fotoapparat: Fotoapparat

    fun setBlur(cameraView: CameraView, overlayTextureView: TextureView, context: Context) {
        val filter = BlurFilter(RenderScript.create(context))
        overlayTextureView.surfaceTextureListener = filter

        fotoapparat = Fotoapparat
                .with(context)
                .into(cameraView)
                .previewScaleType(ScaleType.CENTER_CROP)
                .previewSize(AspectRatioSelectors.aspectRatio(1.7777778f, SizeSelectors.biggestSize()))
                .lensPosition(LensPositionSelectors.front())
                .frameProcessor({
                    filter.execute(it.image, it.size.width, it.size.height, it.rotation)
                })
                .build()
    }

    fun startFotoapparat () {
        fotoapparat.start()
    }

    fun stopFotoapparat () {
        fotoapparat.stop()
    }
}