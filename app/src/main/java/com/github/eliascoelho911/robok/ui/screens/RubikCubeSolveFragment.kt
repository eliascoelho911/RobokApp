package com.github.eliascoelho911.robok.ui.screens

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.rubikcube.AnimCubeModelCreator
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import com.github.eliascoelho911.robok.ui.screens.RubikCubeSolveFragmentDirections.Companion.actionRubikCubeSolveFragmentToCaptureFragment
import kotlinx.android.synthetic.main.rubik_cube_solve_fragment.fab_confirm
import kotlinx.android.synthetic.main.rubik_cube_solve_fragment.fab_retry
import kotlinx.android.synthetic.main.rubik_cube_solve_fragment.preview_cube_view
import kotlinx.android.synthetic.main.rubik_cube_solve_fragment.text_description
import kotlinx.coroutines.launch

class RubikCubeSolveFragment : Fragment() {

    private val args: RubikCubeSolveFragmentArgs by navArgs()
    private val rubikCube by lazy {
        args.rubikCube
    }
    private val modelCreator by lazy {
        AnimCubeModelCreator()
    }
    private val solvingDialog by lazy {
        Dialog(requireContext()).apply {
            setContentView(R.layout.solving_rubik_cube_dialog)
            setCancelable(false)
        }
    }
    private val previewCubeView by lazy { preview_cube_view }
    private val descriptionTextView by lazy { text_description }
    private val confirmFabView by lazy { fab_confirm }
    private val retryFabView by lazy { fab_retry }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.rubik_cube_solve_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showRubikCubePreview()
        setupAnalysis()
        setupClickListeners()
    }

    private fun setupAnalysis() {
        if (rubikCube.isValid) {
            descriptionTextView.text = getString(R.string.rubik_cube_valid)
            confirmFabView.isEnabled = true
        } else {
            descriptionTextView.text = getString(R.string.rubik_cube_invalid)
            confirmFabView.isEnabled = false
        }
    }

    private fun setupClickListeners() {
        retryFabView.setOnClickListener {
            actionRubikCubeSolveFragmentToCaptureFragment().let {
                findNavController().navigate(it)
            }
        }
        confirmFabView.setOnClickListener {
            hideButtons()
            solvingDialog.show()
            lifecycleScope.launch {
                RubikCubeSolver.solve(rubikCube).let {
                    previewCubeView.setMoveSequence(it)
                }
                solvingDialog.dismiss()
                previewCubeView.animateMoveSequence()
            }
        }
    }

    private fun hideButtons() {
        confirmFabView.hide()
        retryFabView.hide()
    }

    private fun showRubikCubePreview() {
        previewCubeView.apply {
            setCubeModel(rubikCube.createModelWith(modelCreator))
            setCubeColors(rubikCube.distinctColors.values.toIntArray())
        }
    }
}