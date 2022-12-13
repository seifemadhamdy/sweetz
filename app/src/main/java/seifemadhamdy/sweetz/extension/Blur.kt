package seifemadhamdy.sweetz.extension

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.BlurView
import seifemadhamdy.sweetz.R

fun BlurView.initialize(
    viewGroup: ViewGroup,
    frameClearDrawable: Drawable = (context as Activity).window.decorView.background,
    blurAlgorithm: BlurAlgorithm? = null,
    blurRadius: Float = 25F,
    autoUpdate: Boolean = true,
    @ColorInt overlayColor: Int = ContextCompat.getColor(
        context,
        R.color.default_blur_background
    )
) {
    if (blurAlgorithm == null) {
        setupWith(viewGroup)
    } else {
        setupWith(viewGroup, blurAlgorithm)
    }.apply {
        setFrameClearDrawable(frameClearDrawable)
        setBlurRadius(blurRadius)
        setBlurAutoUpdate(autoUpdate)
        setOverlayColor(overlayColor)
    }
}