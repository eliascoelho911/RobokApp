package com.github.eliascoelho911.robok.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.github.eliascoelho911.robok.domain.RubikCube

class HomeViewModel : ViewModel() {
    var scannedRubikCube: RubikCube? = null
}