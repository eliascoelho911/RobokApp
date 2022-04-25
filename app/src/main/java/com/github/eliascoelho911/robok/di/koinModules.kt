package com.github.eliascoelho911.robok.di

import com.github.eliascoelho911.robok.rubikcube.AnimCubeModelCreator
import com.github.eliascoelho911.robok.rubikcube.DefaultModelCreator
import com.github.eliascoelho911.robok.rubikcube.Min2PhaseModelCreator
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import com.github.eliascoelho911.robok.rubikcube.face.FaceImageCropper
import com.github.eliascoelho911.robok.rubikcube.face.FaceScanOrder
import com.github.eliascoelho911.robok.ui.helpers.RubikCubeSolvePlayerHelper
import com.github.eliascoelho911.robok.ui.viewmodels.CaptureViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CaptureViewModel(get()) }
}

val helpersModule = module {
    single { params -> RubikCubeSolvePlayerHelper(get(), params[0], params[1]) }
}

val rubikCubeModule = module {
    single { DefaultModelCreator() }
    single { AnimCubeModelCreator() }
    single { Min2PhaseModelCreator() }
    single { RubikCubeSolver(get()) }
    single { FaceImageCropper() }
    single { FaceScanOrder.Default }
}