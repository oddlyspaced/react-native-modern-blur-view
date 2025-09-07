package com.modernblurview.core

import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import androidx.annotation.ColorInt
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.max
import kotlin.math.min

/**
 * Leverages the new RenderEffect.createBlurEffect API to perform blur.
 * Hardware accelerated.
 * Blur is performed on a separate thread - native RenderThread.
 * It doesn't block the Main thread, however it can still cause an FPS drop,
 * because it's just in a different part of the rendering pipeline.
 */
class RenderEffectBlur {

    private val node = RenderNode("BlurViewNode")

    private var height: Int = 0
    private var width: Int = 0

    fun blur(bitmap: Bitmap, blurRadius: Float, @ColorInt blurTintColor: Int, blurTintOpacity: Float): Bitmap {
        if (bitmap.height != height || bitmap.width != width) {
            height = bitmap.height
            width = bitmap.width
            node.setPosition(0, 0, width, height)
        }

        val canvas = node.beginRecording()
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        node.endRecording()

        // --- blur + red-tint chain ---
        val blurEffect = RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.MIRROR)

        val tintEffect = RenderEffect.createColorFilterEffect(
            BlendModeColorFilter(
                Color.argb(
                    max((blurTintOpacity * 255).toInt(), 1), blurTintColor.red, blurTintColor.green, blurTintColor.blue
                ), BlendMode.SRC_ATOP
            )
        )

        // First blur, then apply tint
        val chained = RenderEffect.createChainEffect(tintEffect, blurEffect)
        node.setRenderEffect(chained)
        // --------------------------------

        // returning the original bitmap; rendering happens via RenderNode
        return bitmap
    }

    fun destroy() {
        node.discardDisplayList()
    }

    fun canModifyBitmap(): Boolean = true

    fun getSupportedBitmapConfig(): Bitmap.Config = Bitmap.Config.ARGB_8888

    fun render(canvas: Canvas, bitmap: Bitmap) {
        canvas.drawRenderNode(node)
    }
}
