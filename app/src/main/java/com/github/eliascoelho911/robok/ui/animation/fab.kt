package com.github.eliascoelho911.robok.ui.animation

import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.github.eliascoelho911.robok.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.openWithAnimation() {
    val fabOpenAnimation by lazy { AnimationUtils.loadAnimation(context, R.anim.fab_open) }

    isVisible = true
    startAnimation(fabOpenAnimation)
}