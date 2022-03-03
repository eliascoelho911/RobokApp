package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.rubikcube.RubikCube

class ReviewScannedCubeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        inflate(context, R.layout.review_scanned_cube, this)
    }

    fun show(rubikCube: RubikCube) {

    }
}