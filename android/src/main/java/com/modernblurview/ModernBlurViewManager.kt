package com.modernblurview

import android.graphics.Color
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.ModernBlurViewManagerInterface
import com.facebook.react.viewmanagers.ModernBlurViewManagerDelegate
import android.view.View

const val defaultRadius: Float = 10f

@ReactModule(name = ModernBlurViewManager.NAME)
class ModernBlurViewManager : SimpleViewManager<ModernBlurView>(),
  ModernBlurViewManagerInterface<ModernBlurView> {
  private val mDelegate: ViewManagerDelegate<ModernBlurView>

  init {
    mDelegate = ModernBlurViewManagerDelegate(this)
  }

  override fun getDelegate(): ViewManagerDelegate<ModernBlurView>? {
    return mDelegate
  }

  override fun getName(): String {
    return NAME
  }

  public override fun createViewInstance(context: ThemedReactContext): ModernBlurView {
    val blurView = ModernBlurView(context)
     val decorView: View = requireNotNull(context.currentActivity).window.decorView
        blurView
            .setupWith(decorView.findViewById(android.R.id.content))
            .setBlurRadius(defaultRadius.toFloat())
        return blurView
  }

 @ReactProp(name = "blurRadius", defaultFloat = defaultRadius)
    override fun setBlurRadius(view: ModernBlurView, radius: Float) {
        view.setBlurRadius(maxOf(minOf(radius, 25f), 0f))
        view.invalidate()
    }

    @ReactProp(name = "autoUpdate", defaultBoolean = true)
    override fun setAutoUpdate(view: ModernBlurView, autoUpdate: Boolean) {
        view.setBlurAutoUpdate(autoUpdate)
        view.invalidate()
    }

    @ReactProp(name = "enabled", defaultBoolean = true)
    override fun setEnabled(view: ModernBlurView, enabled: Boolean) {
        view.setBlurEnabled(enabled)
    }

    @ReactProp(name = "tintColor", customType = "Color")
    override fun setTintColor(view: ModernBlurView?, color: Int?) {
        view?.setTintColor(color ?: Color.TRANSPARENT)
        view?.invalidate()
    }

    @ReactProp(name = "tintOpacity", defaultFloat = 0.5f)
    override fun setTintOpacity(view: ModernBlurView?, value: Float) {
        view?.setTintOpacity(value)
    }

  companion object {
    const val NAME = "ModernBlurView"
  }
}
