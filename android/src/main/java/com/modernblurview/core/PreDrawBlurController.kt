package com.modernblurview.core

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.ColorInt
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withScale
import androidx.core.graphics.withSave

class BlurViewCanvas(bitmap: Bitmap) : Canvas(bitmap)

/**
 * Blur Controller that handles all blur logic for the attached View.
 * It honors View size changes, View animation and Visibility changes.
 *
 * The basic idea is to draw the view hierarchy on a bitmap, excluding the attached View,
 * then blur and draw it on the system Canvas.
 *
 * It uses [ViewTreeObserver.OnPreDrawListener] to detect when blur should be updated.
 */
class PreDrawBlurController(
    private val blurView: View, private val rootView: ViewGroup
) : BlurController {

    companion object {
        @ColorInt
        const val TRANSPARENT: Int = 0
    }

    private var blurRadius: Float = BlurController.DEFAULT_BLUR_RADIUS

    @ColorInt
    private var tintColor: Int = TRANSPARENT
    private var tintOpacity: Float = 1f

    private val blurAlgorithm = RenderEffectBlur()
    private lateinit var internalCanvas: BlurViewCanvas
    private lateinit var internalBitmap: Bitmap

    private val rootLocation = IntArray(2)
    private val blurViewLocation = IntArray(2)

    private val drawListener = ViewTreeObserver.OnPreDrawListener {
        // Not invalidating a View here, just updating the Bitmap.
        // This relies on the HW accelerated bitmap drawing behavior in Android.
        updateBlur()
        true
    }

    private var blurEnabled = true
    private var initialized = false

    init {
        val measuredWidth = blurView.measuredWidth
        val measuredHeight = blurView.measuredHeight
        initInternal(measuredWidth, measuredHeight)
    }

    private fun initInternal(measuredWidth: Int, measuredHeight: Int) {
        setBlurAutoUpdate(true)
        if (measuredWidth == 0 || measuredHeight == 0) {
            // Will be initialized later when the View reports a size change
            blurView.setWillNotDraw(true)
            return
        }

        blurView.setWillNotDraw(false)
        internalBitmap = createBitmap(measuredWidth, measuredHeight, blurAlgorithm.supportedBitmapConfig)
        internalCanvas = BlurViewCanvas(internalBitmap)
        initialized = true
        // Usually it's not needed, because `onPreDraw` updates the blur anyway.
        // But it handles cases when the PreDraw listener is attached to a different Window, for example
        // when the BlurView is in a Dialog window, but the root is in the Activity.
        // Previously it was done in `draw`, but it was causing potential side effects and Jetpack Compose crashes
        updateBlur()
    }

    private fun updateBlur() {
        if (!blurEnabled || !initialized) return

        internalCanvas.withSave {
            setupInternalCanvasMatrix()
            rootView.draw(this)
        }

        blurAndSave()
    }

    /**
     * Set up matrix to draw starting from blurView's position
     */
    private fun setupInternalCanvasMatrix() {
        rootView.getLocationOnScreen(rootLocation)
        blurView.getLocationOnScreen(blurViewLocation)

        val left = blurViewLocation[0] - rootLocation[0]
        val top = blurViewLocation[1] - rootLocation[1]
        Log.d("BlurView", "Position: $left // $top")

        // https://github.com/Dimezis/BlurView/issues/128
        val scaleFactorH = blurView.height.toFloat() / internalBitmap.height
        val scaleFactorW = blurView.width.toFloat() / internalBitmap.width

        val scaledLeftPosition = -left / scaleFactorW
        val scaledTopPosition = -top / scaleFactorH

        internalCanvas.translate(scaledLeftPosition, scaledTopPosition)
        internalCanvas.scale(1f / scaleFactorW, 1f / scaleFactorH)
    }

    override fun draw(canvas: Canvas): Boolean {
        if (!blurEnabled || !initialized) return true

        // Not blurring itself or other BlurViews to not cause recursive draw calls
        // Related: https://github.com/Dimezis/BlurView/issues/110
        if (canvas is BlurViewCanvas) return false

        // https://github.com/Dimezis/BlurView/issues/128
        val scaleFactorH = blurView.height.toFloat() / internalBitmap.height
        val scaleFactorW = blurView.width.toFloat() / internalBitmap.width

        canvas.withScale(scaleFactorW, scaleFactorH) {
            blurAlgorithm.render(this, internalBitmap)
        }
        if (tintColor != TRANSPARENT) {
            // canvas.drawColor(overlayColor)
        }
        return true
    }

    private fun blurAndSave() {
        internalBitmap = blurAlgorithm.blur(internalBitmap, blurRadius, tintColor, tintOpacity)
        if (!blurAlgorithm.canModifyBitmap()) {
            internalCanvas.setBitmap(internalBitmap)
        }
    }

    override fun updateBlurViewSize() {
        val measuredWidth = blurView.measuredWidth
        val measuredHeight = blurView.measuredHeight
        initInternal(measuredWidth, measuredHeight)
    }

    override fun destroy() {
        setBlurAutoUpdate(false)
        blurAlgorithm.destroy()
        initialized = false
    }

    override fun setBlurRadius(radius: Float): BlurController {
        blurRadius = radius
        return this
    }

    override fun setBlurEnabled(enabled: Boolean): BlurController {
        blurEnabled = enabled
        setBlurAutoUpdate(enabled)
        blurView.invalidate()
        return this
    }

    override fun setBlurAutoUpdate(enabled: Boolean): BlurController {
        rootView.viewTreeObserver.removeOnPreDrawListener(drawListener)
        if (enabled) {
            rootView.viewTreeObserver.addOnPreDrawListener(drawListener)
        }
        return this
    }

    override fun setTintColor(@ColorInt tintColor: Int): BlurController {
        if (this.tintColor != tintColor) {
            this.tintColor = tintColor
            blurView.invalidate()
        }
        return this
    }

    override fun setTintOpacity(tintOpacity: Float): BlurController {
        if (this.tintOpacity != tintOpacity) {
            this.tintOpacity = tintOpacity
            blurView.invalidate()
        }
        return this
    }

    // Convenience properties for Kotlin callers
    private val RenderEffectBlur.supportedBitmapConfig: Bitmap.Config
        get() = getSupportedBitmapConfig()
}
