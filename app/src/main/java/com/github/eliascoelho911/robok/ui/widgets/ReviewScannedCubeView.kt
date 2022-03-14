package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import kotlinx.android.synthetic.main.review_scanned_cube.view.preview_cube_view

class ReviewScannedCubeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        inflate(context, R.layout.review_scanned_cube, this)
    }

    //todo depender de uma interface
    fun show(rubikCube: RubikCube) {
//        val up = "402415221"
//        val down = "542204233"
//        val front = "311244100"
//        val back = "310551454"
//        val left = "031221300"
//        val right = "555330435"
//        val expected = "$up$down$front$back$left$right"
            previewCubeView.setCubeModel(rubikCube.model)
            previewCubeView.setCubeColors(rubikCube.distinctColors.toIntArray())
    }

    private val previewCubeView by lazy { preview_cube_view }
}