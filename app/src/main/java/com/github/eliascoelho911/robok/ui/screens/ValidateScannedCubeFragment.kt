package com.github.eliascoelho911.robok.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.rubikcube.AnimCubeModelCreator
import com.github.eliascoelho911.robok.ui.screens.ValidateScannedCubeFragmentDirections.Companion.actionValidateScannedCubeFragmentToCaptureFragment
import kotlinx.android.synthetic.main.validate_scanned_cube_fragment.fab_confirm
import kotlinx.android.synthetic.main.validate_scanned_cube_fragment.fab_retry
import kotlinx.android.synthetic.main.validate_scanned_cube_fragment.preview_cube_view
import kotlinx.android.synthetic.main.validate_scanned_cube_fragment.text_analysis

class ValidateScannedCubeFragment : Fragment() {

    private val args: ValidateScannedCubeFragmentArgs by navArgs()
    private val rubikCube by lazy {
        args.rubikCube
    }
    private val modelCreator by lazy {
        AnimCubeModelCreator()
    }
    private val previewCubeView by lazy { preview_cube_view }
    private val analysisView by lazy { text_analysis }
    private val confirmFabView by lazy { fab_confirm }
    private val retryFabView by lazy { fab_retry }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.validate_scanned_cube_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showRubikCubePreview()
        setupAnalysis()
        setupClickListeners()
    }

    private fun setupAnalysis() {
        if (rubikCube.isValid) {
            analysisView.text = getString(R.string.rubik_cube_valid)
            confirmFabView.isEnabled = true
        } else {
            analysisView.text = getString(R.string.rubik_cube_invalid)
            confirmFabView.isEnabled = false
        }
    }

    private fun setupClickListeners() {
        retryFabView.setOnClickListener {
            actionValidateScannedCubeFragmentToCaptureFragment().let {
                findNavController().navigate(it)
            }
        }
    }

    private fun showRubikCubePreview() {
        previewCubeView.apply {
            setCubeModel(rubikCube.createModelWith(modelCreator))
            setCubeColors(rubikCube.distinctColors.toIntArray())
        }
    }
}