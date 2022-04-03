package com.github.eliascoelho911.robok.ui.screens

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.rubikcube.AnimCubeModelCreator
import com.github.eliascoelho911.robok.rubikcube.Move
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import com.github.eliascoelho911.robok.ui.dialogs.LoadingDialog
import com.github.eliascoelho911.robok.ui.screens.RubikCubeSolveFragmentDirections.Companion.actionRubikCubeSolveFragmentToCaptureFragment
import com.github.eliascoelho911.robok.ui.viewmodels.RubikCubeSolveViewModel
import kotlinx.android.synthetic.main.rubik_cube_solve_fragment.fab_confirm
import kotlinx.android.synthetic.main.rubik_cube_solve_fragment.fab_retry
import kotlinx.android.synthetic.main.rubik_cube_solve_fragment.preview_cube_view
import kotlinx.android.synthetic.main.rubik_cube_solve_fragment.txt_moves
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RubikCubeSolveFragment : Fragment() {
    private val args: RubikCubeSolveFragmentArgs by navArgs()
    private val viewModel: RubikCubeSolveViewModel by viewModel()
    private val rubikCubeSolver: RubikCubeSolver by inject()
    private val rubikCube by lazy {
        args.rubikCube
    }
    private val modelCreator by lazy {
        AnimCubeModelCreator()
    }
    private val solvingDialog by lazy {
        LoadingDialog(requireContext(), R.string.solving_rubik_cube).apply {
            setCancelable(false)
        }
    }
    private val previewCubeView by lazy { preview_cube_view }
    private val confirmFabView by lazy { fab_confirm }
    private val retryFabView by lazy { fab_retry }
    private val movesTextView by lazy { txt_moves }
    private var currentMoveIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.rubik_cube_solve_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showRubikCubePreview()
        setupClickListeners()
        setupRuleForConnectionToRobot()
    }

    private fun setupRuleForConnectionToRobot() {
        viewModel.isConnectedWithRobot.observe(viewLifecycleOwner) { isConnected ->
//            confirmFabView.isClickable = isConnected
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
            solveRubikCube()
        }
    }

    private fun solveRubikCube() {
        solvingDialog.show()
        lifecycleScope.launch {
            rubikCubeSolver.solve(rubikCube).let { moves ->
                val moveSequence = moves.joinToString(separator = " ")
                previewCubeView.setMoveSequence(moveSequence)

                previewCubeView.setOnAnimationFinishedListener {
                    startNextMove(moveSequence, moves)
                }
                startNextMove(moveSequence, moves)
            }
            solvingDialog.dismiss()
        }
    }

    private fun startNextMove(
        moveSequence: String,
        moves: List<Move>,
    ) {
        val nextMoveIndex = currentMoveIndex + 1
        if (nextMoveIndex <= moves.lastIndex) {
            currentMoveIndex++
            movesTextView.text = spannableWithCurrentMoveInHighlight(moveSequence, moves)
            previewCubeView.animateMove()
        }
    }

    private fun spannableWithCurrentMoveInHighlight(
        moveSequence: String,
        moves: List<Move>,
    ): SpannableString {
        val movesSpannable = SpannableString(moveSequence)
        val color = ContextCompat.getColor(requireContext(), R.color.green_a400)
        val nextMove = moves[currentMoveIndex]
        val previousMovesLength = moves.subList(0, currentMoveIndex).sumOf { it.length }
        val spacesLength = currentMoveIndex
        val start = spacesLength + previousMovesLength
        val end = start + nextMove.length
        movesSpannable.setSpan(ForegroundColorSpan(color),
            start,
            end,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return movesSpannable
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