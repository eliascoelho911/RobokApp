package com.github.eliascoelho911.robok.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.rubikcube.AnimCubeModelCreator
import com.github.eliascoelho911.robok.ui.dialogs.LoadingDialog
import com.github.eliascoelho911.robok.ui.helpers.RubikCubeSolvePlayerHelper
import kotlinx.android.synthetic.main.rubik_cube_solve_fragment.player_view
import kotlinx.android.synthetic.main.rubik_cube_solve_fragment.preview_cube_view
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class RubikCubeSolveFragment : Fragment() {
    private val args: RubikCubeSolveFragmentArgs by navArgs()
    private val modelCreator: AnimCubeModelCreator by inject()
    private val rubikCubeSolvePlayerHelper: RubikCubeSolvePlayerHelper by inject {
        parametersOf(preview_cube_view, player_view)
    }
    private val rubikCube by lazy { args.rubikCube }
    private val solvingDialog by lazy {
        LoadingDialog(requireContext(), R.string.solving_rubik_cube).apply {
            setCancelable(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.rubik_cube_solve_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showRubikCubePreview()
        setupPlayer()
        solveRubikCube()
    }

    private fun setupPlayer() {
        setupPlayerButtons()
    }

    private fun setupPlayerButtons() {
        player_view.apply {
            previousBtnOnClickListener = { rubikCubeSolvePlayerHelper.previousMove() }
            nextBtnOnClickListener = { rubikCubeSolvePlayerHelper.nextMove() }
            playBtnOnClickListener = { rubikCubeSolvePlayerHelper.playOrPause() }
        }
    }

    private fun solveRubikCube() {
        solvingDialog.show()
        lifecycleScope.launch {
            rubikCubeSolvePlayerHelper.init(rubikCube)
            solvingDialog.dismiss()
        }
    }

    private fun showRubikCubePreview() {
        preview_cube_view.apply {
            setCubeModel(rubikCube.createModelWith(modelCreator))
            setCubeColors(rubikCube.distinctColors.values.toIntArray())
        }
    }
}