package com.github.eliascoelho911.robok.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.eliascoelho911.robok.domain.RubikCube
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import com.github.eliascoelho911.robok.util.updateValue

class HomeViewModel : ViewModel() {
    private val _scannedRubikCube = MutableLiveData(RubikCube(emptySet()))
    val scannedRubikCube: LiveData<RubikCube> get() = _scannedRubikCube

    fun addScannedSide(side: RubikCubeSide) {
        _scannedRubikCube.updateValue { addSide(side) }
    }
}