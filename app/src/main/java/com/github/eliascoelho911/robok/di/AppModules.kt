package com.github.eliascoelho911.robok.di

import com.github.eliascoelho911.robok.robot.Robot
import com.github.eliascoelho911.robok.robot.bluetooth.MockRobotBluetoothManager
import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager
import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManagerImpl
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import com.github.eliascoelho911.robok.ui.compose.screens.solve.SolveViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Bluetooth module providing implementations for Bluetooth-related dependencies
 */
val bluetoothModule = module {
    // Default implementation
    single<RobotBluetoothManager> { RobotBluetoothManagerImpl(get()) }

    // Mock implementation for testing/development
    single<RobotBluetoothManager>(named("mock")) { MockRobotBluetoothManager() }
}

/**
 * Rubik's cube module providing implementations for cube-related dependencies
 */
val rubikCubeModule = module {
    single { RubikCubeSolver() }
}

/**
 * View models module for all app's ViewModels
 */
val viewModelModule = module {
    viewModel { (robot: Robot) -> SolveViewModel(robot) }
}

/**
 * List of all modules in the application
 */
val appModules = listOf(
    bluetoothModule,
    rubikCubeModule,
    viewModelModule
)