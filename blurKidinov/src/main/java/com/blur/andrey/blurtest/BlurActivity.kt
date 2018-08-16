package com.blur.andrey.blurtest

import android.os.Bundle
import android.renderscript.RenderScript
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.parameter.selector.AspectRatioSelectors
import io.fotoapparat.parameter.selector.LensPositionSelectors
import io.fotoapparat.parameter.selector.SizeSelectors
import kotlinx.android.synthetic.main.activity_blur.*

class BlurActivity : AppCompatActivity() {
    private lateinit var fotoapparat: Fotoapparat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur)

        initQrScanner()

        ArtemkaMydila.setOnClickListener(View.OnClickListener {
            Log.d("Kotlin", "piece of shit")

            Toast.makeText(this, "Last stand", Toast.LENGTH_SHORT).show()

            tvWholeOverlay.visibility = View.GONE
        })

        ArtemkaMydarasik.setOnClickListener(View.OnClickListener {
            Log.d("Kotlin", "piece of 5h1t")

            Toast.makeText(this, "Last stand", Toast.LENGTH_SHORT).show()

            tvWholeOverlay.visibility = View.VISIBLE
        })
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        fotoapparat.stop()
        super.onStop()
    }

    private fun initQrScanner() {
        val filter = BlurFilter(RenderScript.create(this))
        tvWholeOverlay.surfaceTextureListener = filter

        fotoapparat = Fotoapparat
                .with(this)
                .into(cvQrScanner)
                .previewScaleType(ScaleType.CENTER_CROP)
                .previewSize(AspectRatioSelectors.aspectRatio(1.7777778f, SizeSelectors.biggestSize()))
                .lensPosition(LensPositionSelectors.back())
                /*.frameProcessor({
                    filter.execute(it.image, it.size.width, it.size.height, it.rotation)
                })*/
                .build()
    }
}
