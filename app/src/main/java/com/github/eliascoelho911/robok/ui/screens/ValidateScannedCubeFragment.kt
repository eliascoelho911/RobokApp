package com.github.eliascoelho911.robok.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.rubikcube.AnimCubeModelCreator
import kotlinx.android.synthetic.main.validate_scanned_cube_fragment.preview_cube_view

class ValidateScannedCubeFragment : Fragment() {

    private val previewCubeView by lazy { preview_cube_view }
    private val args: ValidateScannedCubeFragmentArgs by navArgs()
    private val rubikCube by lazy {
        args.rubikCube
    }
    private val modelCreator by lazy {
        AnimCubeModelCreator()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.validate_scanned_cube_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewCubeView.apply {
            setCubeModel(rubikCube.createModelWith(modelCreator))
            setCubeColors(rubikCube.distinctColors.toIntArray())
        }
    }
}