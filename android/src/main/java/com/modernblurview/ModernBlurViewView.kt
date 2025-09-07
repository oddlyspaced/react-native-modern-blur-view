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

class ModernBlurViewView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

      lateinit var blurController: BlurController
        private set

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


}
