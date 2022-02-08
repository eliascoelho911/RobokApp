package com.github.eliascoelho911.robok.ui.animation

import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.util.setOnAnimationEndListener
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.openWithAnimation() {
    val animation = AnimationUtils.loadAnimation(context, R.anim.fab_open)

    startAnimation(animation)
    isVisible = true
}

fun FloatingActionButton.closeWithAnimation() {
    val animation = AnimationUtils.loadAnimation(context, R.anim.fab_close).apply {
        setOnAnimationEndListener {
            isVisible = false
        }
    }

    startAnimation(animation)
}