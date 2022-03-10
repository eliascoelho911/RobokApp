package com.github.eliascoelho911.robok.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.github.eliascoelho911.robok.rubikcube.RubikCube

class CaptureViewModel : ViewModel() {
    val scannedRubikCubeBuilder = RubikCube.Builder()
}