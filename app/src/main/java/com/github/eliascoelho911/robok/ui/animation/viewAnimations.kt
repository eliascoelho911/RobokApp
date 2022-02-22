package com.github.eliascoelho911.robok.ui.animation

import android.animation.Animator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible
import com.github.eliascoelho911.robok.util.setOnAnimationEndListener
import com.github.eliascoelho911.robok.util.setOnAnimationStartListener

fun View.fadeIn() {
    animate()
        .alphaBy(0f)
        .alpha(1f)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .setDuration(AnimationDurations.short)
        .setOnAnimationStartListener {
            isVisible = true
        }
        .start()
}

fun View.fadeOut() {
    animate()
        .alphaBy(1f)
        .alpha(0f)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .setDuration(AnimationDurations.short)
        .setOnAnimationEndListener {
            isVisible = false
        }
        .start()
}

fun View.scaleIn() {
    animate()
        .scaleXBy(0f).scaleX(1f)
        .scaleYBy(0f).scaleY(1f)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .setDuration(AnimationDurations.short)
        .setOnAnimationStartListener {
            isVisible = true
        }
        .start()
}

fun View.scaleOut() {
    animate()
        .scaleXBy(1f).scaleX(0f)
        .scaleYBy(1f).scaleY(0f)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .setDuration(AnimationDurations.short)
        .setOnAnimationEndListener {
            isVisible = false
        }
        .start()
}