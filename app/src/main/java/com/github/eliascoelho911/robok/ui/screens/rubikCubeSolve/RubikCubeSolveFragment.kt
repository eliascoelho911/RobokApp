package com.github.eliascoelho911.robok.ui.screens.rubikCubeSolve

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.catalinjurjiu.animcubeandroid.AnimCube
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.bluetooth.RobotBluetoothManager
import com.github.eliascoelho911.robok.databinding.RubikCubeSolveFragmentBinding
import com.github.eliascoelho911.robok.rubikcube.AnimCubeModelParser
import com.github.eliascoelho911.robok.rubikcube.Moves
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import com.github.eliascoelho911.robok.ui.dialogs.LoadingDialog
import com.github.eliascoelho911.robok.ui.widgets.RubikCubeSolvePlayerView
import com.github.eliascoelho911.robok.util.addImePadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RubikCubeSolveFragment : Fragment() {
    private val args: RubikCubeSolveFragmentArgs by navArgs()
    private val modelParser by lazy { AnimCubeModelParser() }
    private var binding: RubikCubeSolveFragmentBinding? = null
    private val playerView by lazy { binding!!.playerView }
    private val previewCubeView by lazy { binding!!.previewCubeView }
    private val robotBluetoothManager by lazy { RobotBluetoothManager(requireContext()) }
    private val rubikCubeSolvePlayerHelper: RubikCubeSolvePlayerHelper by lazy {
        RubikCubeSolvePlayerHelper(
            RubikCubeSolver(),
            previewCubeView,
            playerView,
            robotBluetoothManager
        )
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
        binding?.playerView?.addImePadding()
        return binding!!.root
    }

    override fun onDestroy() {
        super.onDestroy()
        robotBluetoothManager.disconnect()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showRubikCubePreview()
        setupPlayer()
        setupRobotConnectionUI()
        solveRubikCube()
    }

    private fun setupRobotConnectionUI() {
        binding?.apply {
            robotConnectButton.setOnClickListener {
                if (robotBluetoothManager.connectionState.value == RobotBluetoothManager.ConnectionState.CONNECTED) {
                    robotBluetoothManager.disconnect()
                } else {
                    connectToRobot()
                }
            }
        }

        // Observe connection state changes
        lifecycleScope.launch {
            robotBluetoothManager.connectionState.collectLatest { state ->
                updateConnectionState(state)
            }
        }
    }

    private fun connectToRobot() {
        lifecycleScope.launch {
            if (!robotBluetoothManager.isBluetoothSupported()) {
                showToast(getString(R.string.robot_connection_error))
                return@launch
            }

            if (!robotBluetoothManager.isBluetoothEnabled()) {
                showToast(getString(R.string.robot_connection_error))
                return@launch
            }

            robotBluetoothManager.connect()
        }
    }

    private fun updateConnectionState(state: RobotBluetoothManager.ConnectionState) {
        binding?.apply {
            val (text, color, icon) = when (state) {
                RobotBluetoothManager.ConnectionState.CONNECTED -> Triple(
                    getString(R.string.connected),
                    ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark),
                    R.drawable.ic_usb_24dp
                )

                RobotBluetoothManager.ConnectionState.CONNECTING -> Triple(
                    getString(R.string.connecting),
                    ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light),
                    R.drawable.ic_usb_24dp
                )

                RobotBluetoothManager.ConnectionState.DISCONNECTED -> Triple(
                    getString(R.string.disconnected),
                    ContextCompat.getColor(requireContext(), android.R.color.darker_gray),
                    R.drawable.ic_usb_24dp
                )

                RobotBluetoothManager.ConnectionState.ERROR -> Triple(
                    getString(R.string.robot_connection_error),
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark),
                    R.drawable.ic_usb_off_24dp
                )

                RobotBluetoothManager.ConnectionState.PERMISSION_REQUIRED -> Triple(
                    getString(R.string.robot_permission_required),
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_light),
                    R.drawable.ic_usb_off_24dp
                )

                RobotBluetoothManager.ConnectionState.NO_DEVICE -> Triple(
                    getString(R.string.robot_not_found),
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_light),
                    R.drawable.ic_usb_off_24dp
                )
            }

            robotConnectionStatus.text = text
            robotConnectionStatus.setTextColor(color)
            robotConnectButton.setImageResource(icon)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
            setCubeModel(modelParser.parse(rubikCube))
            setCubeColors(modelParser.getDistinctColorsInOrder(rubikCube).toIntArray())
        }
    }
}

private class RubikCubeSolvePlayerHelper(
    private val rubikCubeSolver: RubikCubeSolver,
    private val previewRubikCube: AnimCube,
    private val player: RubikCubeSolvePlayerView,
    private val robotBluetoothManager: RobotBluetoothManager,
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
            // Send command to robot if connected
            sendMoveToRobot(moveSequence[index])
            onStartAnimation()
        }
    }

    private fun previousMove(onStartAnimation: () -> Unit) {
        val hasPreviousMove = index > 0

        if (!previewRubikCube.isAnimating && hasPreviousMove) {
            previewRubikCube.animateMoveReversed()
            // For previous move, we need the inverse of the previous move
            if (index > 0) {
                val inverseMoveCommand = getInverseMoveCommand(moveSequence[index - 1])
                sendMoveToRobot(inverseMoveCommand)
            }
            onStartAnimation()
        }
    }

    private fun getInverseMoveCommand(move: String): String {
        // If move ends with ' (inverted move), remove it, otherwise add it
        return if (move.endsWith("'")) {
            move.substring(0, move.length - 1)
        } else {
            "$move'"
        }
    }

    private fun sendMoveToRobot(move: String) {
        if (robotBluetoothManager.connectionState.value == RobotBluetoothManager.ConnectionState.CONNECTED) {
            // Start a coroutine to send the command
            CoroutineScope(Dispatchers.IO).launch {
                robotBluetoothManager.sendCommand("move:hl,cw;")
            }
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