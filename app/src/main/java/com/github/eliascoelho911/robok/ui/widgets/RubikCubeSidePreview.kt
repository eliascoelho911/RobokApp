package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import com.github.eliascoelho911.robok.ui.animation.scaleIn
import com.github.eliascoelho911.robok.ui.animation.scaleOut
import com.github.eliascoelho911.robok.util.Position
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.rubik_cube_side_preview.view.rubik_cube_side_preview_grid

class RubikCubeSidePreview @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    var rubikCubeSide: RubikCubeSide? = null

    init {
        inflate(context, R.layout.rubik_cube_side_preview, this)
    }

    fun show() {
        rubikCubeSide?.let { side ->
            rubik_cube_side_preview_grid.forEachIndexed { index, view ->
                val position = index.toPosition()
                view as MaterialCardView
                side.colors[position]?.let {
                    view.setCardBackgroundColor(it.androidColor(context).toArgb())
                }
                view.scaleIn()
            }
        }
    }

    fun hide() {
        rubik_cube_side_preview_grid.forEach {
            it.scaleOut()
        }
    }

    private fun Int.toPosition(): Position {
        val x = this % (rubik_cube_side_preview_grid.rowCount)
        val y = (this - x) / (rubik_cube_side_preview_grid.columnCount)
        return Position(x, y)
    }
}