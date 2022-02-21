package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.ScaleAnimation.RELATIVE_TO_SELF
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import com.github.eliascoelho911.robok.ui.animation.AnimationDurations.short
import com.github.eliascoelho911.robok.util.Position
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.rubik_cube_side_preview.view.rubik_cube_side_preview_grid

class RubikCubeSidePreview @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.rubik_cube_side_preview, this)
    }

    fun show(side: RubikCubeSide) {
        this.rubikCubeSide = side
        rubik_cube_side_preview_grid.forEachIndexed { index, view ->
            val position = index.toPosition()
            view as MaterialCardView
            side.colors[position]?.let {
                view.setCardBackgroundColor(it.androidColor(context).toArgb())
            }
            view.showWithScaleAnimation()
        }
    }

    private fun View.showWithScaleAnimation() {
        startAnimation(scaleInAnimation)
        isVisible = true
    }

    private fun Int.toPosition(): Position {
        val x = this % (rubik_cube_side_preview_grid.rowCount)
        val y = (this - x) / (rubik_cube_side_preview_grid.columnCount)
        return Position(x, y)
    }

    private var rubikCubeSide: RubikCubeSide? = null
    private val scaleInAnimation = ScaleAnimation(
        0f,
        1f,
        0f,
        1f,
        RELATIVE_TO_SELF,
        0.5f,
        RELATIVE_TO_SELF,
        0.5f
    ).apply {
        duration = short
        interpolator = AccelerateDecelerateInterpolator()
    }
}