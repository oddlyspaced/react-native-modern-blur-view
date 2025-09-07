package com.modernblurview.core

import android.graphics.Canvas
import androidx.annotation.ColorInt

interface BlurController {

    companion object {
        const val DEFAULT_BLUR_RADIUS: Float = 16f
    }

    fun setBlurEnabled(enabled: Boolean): BlurController
    fun setBlurAutoUpdate(enabled: Boolean): BlurController
    fun setBlurRadius(radius: Float): BlurController
    fun setTintColor(@ColorInt tintColor: Int): BlurController
    fun setTintOpacity(tintOpacity: Float): BlurController
    fun draw(canvas: Canvas): Boolean
    fun updateBlurViewSize()
    fun destroy()
}
