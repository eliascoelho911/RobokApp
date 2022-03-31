package com.github.eliascoelho911.robok.di

import com.github.eliascoelho911.robok.rubikcube.AnimCubeModelCreator
import com.github.eliascoelho911.robok.rubikcube.DefaultModelCreator
import com.github.eliascoelho911.robok.rubikcube.Min2PhaseModelCreator
import com.github.eliascoelho911.robok.rubikcube.RubikCubeConnectionManager
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import com.github.eliascoelho911.robok.rubikcube.face.FaceImageCropper
import com.github.eliascoelho911.robok.rubikcube.face.FaceScanOrder
import com.github.eliascoelho911.robok.rubikcube.face.Position
import com.github.eliascoelho911.robok.ui.viewmodels.CaptureViewModel
import com.github.eliascoelho911.robok.ui.viewmodels.RootViewModel
import com.github.eliascoelho911.robok.ui.viewmodels.RubikCubeSolveViewModel
import com.github.eliascoelho911.robok.ui.widgets.Direction
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CaptureViewModel(get()) }
    viewModel { RootViewModel(get(), Dispatchers.IO) }
    viewModel { RubikCubeSolveViewModel(get(), Dispatchers.IO) }
}

val rubikCubeModule = module {
    single { RubikCubeConnectionManager() }
    single { DefaultModelCreator() }
    single { AnimCubeModelCreator() }
    single { Min2PhaseModelCreator() }
    single { RubikCubeSolver(get()) }
    single { FaceImageCropper() }
    single {
        FaceScanOrder(
            FaceScanOrder.Item(Position.UP,
                movesToDestination = 0,
                directionToDestination = null),
            FaceScanOrder.Item(Position.DOWN,
                movesToDestination = 2,
                directionToDestination = Direction.DOWN),
            FaceScanOrder.Item(Position.FRONT,
                movesToDestination = 1,
                directionToDestination = Direction.UP),
            FaceScanOrder.Item(Position.RIGHT,
                movesToDestination = 1,
                directionToDestination = Direction.RIGHT),
            FaceScanOrder.Item(Position.BACK,
                movesToDestination = 1,
                directionToDestination = Direction.RIGHT),
            FaceScanOrder.Item(Position.LEFT,
                movesToDestination = 1,
                directionToDestination = Direction.RIGHT))
    }
}