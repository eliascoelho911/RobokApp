package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import kotlinx.android.synthetic.main.review_scanned_cube.view.colors_container

class ReviewScannedCubeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        inflate(context, R.layout.review_scanned_cube, this)
    }

    fun show(rubikCube: RubikCube) {
        rubikCube.facesWithStandardizesColors.flatMap { it.colors }.distinct().forEach { color ->
            val view = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(48, 48)
                id = View.generateViewId()
                setBackgroundColor(color)
            }
            colorsContainerView.addView(view)
        }
    }

    private val colorsContainerView by lazy { colors_container }
}