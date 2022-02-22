package com.github.eliascoelho911.robok.util

import android.animation.Animator
import android.view.ViewPropertyAnimator

fun ViewPropertyAnimator.setOnAnimationEndListener(onAnimationEnd: () -> Unit): ViewPropertyAnimator = apply {
    setListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
            onAnimationEnd.invoke()
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationRepeat(p0: Animator?) {
        }
    })
}

fun ViewPropertyAnimator.setOnAnimationStartListener(onAnimationStart: () -> Unit): ViewPropertyAnimator = apply {
    setListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator?) {
            onAnimationStart()
        }

        override fun onAnimationEnd(p0: Animator?) {
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationRepeat(p0: Animator?) {
        }
    })
}