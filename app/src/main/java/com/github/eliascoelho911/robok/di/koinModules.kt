package com.github.eliascoelho911.robok.di

import com.github.eliascoelho911.robok.ui.viewmodels.CaptureViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CaptureViewModel() }
}