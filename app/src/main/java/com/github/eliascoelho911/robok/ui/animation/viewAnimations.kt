package com.github.eliascoelho911.robok.ui.animation

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible

fun View.fadeIn() {
    animate()
        .alphaBy(0f)
        .alpha(1f)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .setDuration(AnimationDurations.short)
        .start()
    isVisible = true
}

fun View.fadeOut() {
    animate()
        .alphaBy(1f)
        .alpha(0f)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .setDuration(AnimationDurations.short)
        .start()
    isVisible = false
}