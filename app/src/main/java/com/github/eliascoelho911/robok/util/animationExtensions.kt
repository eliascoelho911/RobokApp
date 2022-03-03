package com.github.eliascoelho911.robok.util

import android.view.animation.Animation


fun Animation.setOnAnimationEndListener(onAnimationEnd: () -> Unit) = apply {
    setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {}

        override fun onAnimationEnd(p0: Animation?) { onAnimationEnd() }

        override fun onAnimationRepeat(p0: Animation?) {}
    })
}