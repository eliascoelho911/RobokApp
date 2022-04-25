package com.github.eliascoelho911.robok.ui.helpers

import com.catalinjurjiu.animcubeandroid.AnimCube
import com.github.eliascoelho911.robok.rubikcube.Moves
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import com.github.eliascoelho911.robok.ui.widgets.RubikCubeSolvePlayerView

class RubikCubeSolvePlayerHelper(
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