package com.github.eliascoelho911.robok.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import com.github.eliascoelho911.robok.util.updateValue

class HomeViewModel : ViewModel() {
    private val _scannedRubikCubeSides = MutableLiveData<List<RubikCubeSide>>()
    val scannedRubikCubeSides: LiveData<List<RubikCubeSide>> get() = _scannedRubikCubeSides

    fun addScannedSide(side: RubikCubeSide) {
        _scannedRubikCubeSides.updateValue { toMutableList().add(side) }
    }
}