package com.github.eliascoelho911.robok.ui.compose.screens.solve

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.eliascoelho911.robok.robot.Robot
import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager
import com.github.eliascoelho911.robok.rubikcube.LeftHand
import com.github.eliascoelho911.robok.rubikcube.RightHand
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.ui.compose.theme.AppTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SolveActivity : ComponentActivity() {
    private val robotBluetoothManager: RobotBluetoothManager by inject()

    private val robot: Robot by lazy {
        Robot(
            cube = rubikCube,
            leftHand = LeftHand,
            rightHand = RightHand,
            bluetoothManager = robotBluetoothManager
        )
    }

    private val viewModel: SolveViewModel by viewModel { parametersOf(robot) }

    private lateinit var rubikCube: RubikCube

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rubikCube = intent.getParcelableExtra<RubikCube>(EXTRA_RUBIK_CUBE)!!

        setContent {
            AppTheme {
                SolveScreen(viewModel = viewModel)
            }
        }
    }

    companion object {
        private const val EXTRA_RUBIK_CUBE = "extra_rubik_cube"

        fun getIntent(context: Context, rubikCube: RubikCube): Intent {
            return Intent(context, SolveActivity::class.java).apply {
                putExtra(EXTRA_RUBIK_CUBE, rubikCube)
            }
        }
    }
}