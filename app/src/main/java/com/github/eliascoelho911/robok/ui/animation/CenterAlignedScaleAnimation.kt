package com.github.eliascoelho911.robok.ui.animation

import android.view.animation.ScaleAnimation

class CenterAlignedScaleAnimation(
    fromX: Float,
    toX: Float,
    fromY: Float,
    toY: Float,
) : ScaleAnimation(
    fromX,
    toX,
    fromY,
    toY,
    RELATIVE_TO_SELF,
    0.5f,
    RELATIVE_TO_SELF,
    0.5f
)