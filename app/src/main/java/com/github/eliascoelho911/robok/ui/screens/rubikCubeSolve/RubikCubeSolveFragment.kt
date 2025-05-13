package com.github.eliascoelho911.robok.ui.screens.rubikCubeSolve

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.catalinjurjiu.animcubeandroid.AnimCube
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.databinding.RubikCubeSolveFragmentBinding
import com.github.eliascoelho911.robok.rubikcube.AnimCubeModelCreator
import com.github.eliascoelho911.robok.rubikcube.Moves
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import com.github.eliascoelho911.robok.ui.dialogs.LoadingDialog
import com.github.eliascoelho911.robok.ui.widgets.RubikCubeSolvePlayerView
import kotlinx.coroutines.launch

class RubikCubeSolveFragment : Fragment() {
    private val args: RubikCubeSolveFragmentArgs by navArgs()
    private val modelCreator by lazy { AnimCubeModelCreator() }
    private var binding: RubikCubeSolveFragmentBinding? = null
    private val playerView by lazy { binding!!.playerView }
    private val previewCubeView by lazy { binding!!.previewCubeView }
    private val rubikCubeSolvePlayerHelper: RubikCubeSolvePlayerHelper by lazy {
        RubikCubeSolvePlayerHelper(RubikCubeSolver(), previewCubeView, playerView)
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
    ): View {
        binding = RubikCubeSolveFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

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
        playerView.apply {
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
        previewCubeView.apply {
            setCubeModel(rubikCube.createModelWith(modelCreator))
            setCubeColors(rubikCube.distinctColors.values.toIntArray())
        }
    }
}

private class RubikCubeSolvePlayerHelper(
    private val rubikCubeSolver: RubikCubeSolver,
    private val previewRubikCube: AnimCube,
    private val player: RubikCubeSolvePlayerView,
) {
    private lateinit var moveSequence: Moves
    private var index = 0
    private var isPause = true
    private val hasNextMove get() = index < moveSequence.size

    suspend fun init(rubikCube: RubikCube) {
        moveSequence = rubikCubeSolver.solve(rubikCube)
        previewRubikCube.setMoveSequence(moveSequence.joinToString(separator = " "))
        player.amountOfMoves = moveSequence.size
        resetIndex()
        updatePlayer()
    }

    fun nextMove() {
        nextMove(onStartAnimation = {
            updateIndexWhenFinishAnimation(increase = 1)
        })
    }

    fun previousMove() {
        previousMove(onStartAnimation = {
            updateIndexWhenFinishAnimation(increase = -1)
        })
    }

    fun playOrPause() {
        if (isPause && hasNextMove) play() else pause()
    }

    private fun play() {
        isPause = false
        playRemainingMoves()
    }

    private fun playRemainingMoves() {
        player.play()

        nextMove(onStartAnimation = {
            updateIndexWhenFinishAnimation(increase = 1, onIndexUpdated = {
                pauseIfMovementsAreOver()
                if (!isPause) playRemainingMoves()
            })
        })
    }

    private fun pause() {
        isPause = true
        player.pause()
    }

    private fun nextMove(onStartAnimation: () -> Unit) {
        if (!previewRubikCube.isAnimating && hasNextMove) {
            previewRubikCube.animateMove()
            onStartAnimation()
        }
    }

    private fun previousMove(onStartAnimation: () -> Unit) {
        val hasPreviousMove = index > 0

        if (!previewRubikCube.isAnimating && hasPreviousMove) {
            previewRubikCube.animateMoveReversed()
            onStartAnimation()
        }
    }

    private fun updateIndexWhenFinishAnimation(increase: Int, onIndexUpdated: () -> Unit = {}) {
        previewRubikCube.setOnAnimationFinishedListener {
            updateIndex(increase)
            onIndexUpdated()
        }
    }

    private fun resetIndex() {
        index = 0
    }

    private fun updateIndex(increase: Int) {
        index += increase
        updatePlayer()
    }

    private fun updatePlayer() {
        if (index == moveSequence.size) {
            player.solved()
        } else {
            player.changeCurrentMove(moveSequence[index], index)
        }
    }

    private fun pauseIfMovementsAreOver() {
        if (index == moveSequence.size) {
            isPause = true
            player.pause()
        }
    }
}