package com.modernblurview

import android.view.View
import android.widget.FrameLayout
import com.modernblurview.core.*
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.ColorInt

class ModernBlurView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

      lateinit var blurController: BlurController
        private set
    
    companion object {
        private const val TAG: String = "ModernBlurView"
    }

    @ColorInt
    private var tintColor: Int = PreDrawBlurController.TRANSPARENT

    private var tintOpacity: Float = 1f

      override fun draw(canvas: Canvas) {
        val shouldDraw = blurController.draw(canvas)
        if (shouldDraw) {
            super.draw(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        blurController.updateBlurViewSize()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        blurController.setBlurAutoUpdate(false)
    }

     override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isHardwareAccelerated) {
            Log.e(TAG, "BlurView can't be used in not hardware-accelerated window!")
        } else {
            blurController.setBlurAutoUpdate(true)
        }
    }

      /**
     * @param rootView root to start blur from.
     * Can be Activity's root content layout (android.R.id.content)
     * or (preferably) some of your layouts. The lower amount of Views are in the root, the better for performance.
     * @return [BlurController] to setup needed params.
     */
    fun setupWith(rootView: ViewGroup): BlurController {
        if (::blurController.isInitialized) {
            blurController.destroy()
        }
        val controller = PreDrawBlurController(this, rootView)
        blurController = controller
        return controller
    }

    fun setBlurRadius(radius: Float) {
        blurController.setBlurRadius(radius)
    }

    fun setTintColor(@ColorInt tintColor: Int) {
        this.tintColor = tintColor
        blurController.setTintColor(tintColor)
    }

    fun setTintOpacity(tintOpacity: Float) {
        this.tintOpacity = tintOpacity
        blurController.setTintOpacity(tintOpacity)
    }

    fun setBlurAutoUpdate(enabled: Boolean) {
        blurController.setBlurAutoUpdate(enabled)
    }

    fun setBlurEnabled(enabled: Boolean) {
        blurController.setBlurEnabled(enabled)
    }

}
