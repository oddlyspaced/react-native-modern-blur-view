package com.modernblurview

import android.graphics.Color
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.ModernBlurViewViewManagerInterface
import com.facebook.react.viewmanagers.ModernBlurViewViewManagerDelegate

@ReactModule(name = ModernBlurViewViewManager.NAME)
class ModernBlurViewViewManager : SimpleViewManager<ModernBlurViewView>(),
  ModernBlurViewViewManagerInterface<ModernBlurViewView> {
  private val mDelegate: ViewManagerDelegate<ModernBlurViewView>

  init {
    mDelegate = ModernBlurViewViewManagerDelegate(this)
  }

  override fun getDelegate(): ViewManagerDelegate<ModernBlurViewView>? {
    return mDelegate
  }

  override fun getName(): String {
    return NAME
  }

  public override fun createViewInstance(context: ThemedReactContext): ModernBlurViewView {
    return ModernBlurViewView(context)
  }

  @ReactProp(name = "color")
  override fun setColor(view: ModernBlurViewView?, color: String?) {
    view?.setBackgroundColor(Color.parseColor(color))
  }

  companion object {
    const val NAME = "ModernBlurViewView"
  }
}
