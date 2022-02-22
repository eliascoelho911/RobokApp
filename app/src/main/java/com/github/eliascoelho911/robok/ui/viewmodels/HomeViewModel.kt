package com.github.eliascoelho911.robok.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import com.github.eliascoelho911.robok.util.updateValue

class HomeViewModel : ViewModel() {
    private val _scannedRubikCubeSides = MutableLiveData<List<RubikCubeSide>>(emptyList())
    val scannedRubikCubeSides: LiveData<List<RubikCubeSide>> get() = _scannedRubikCubeSides
    private val _lastSideScanned = MutableLiveData<RubikCubeSide>()
    val lastSideScanned: LiveData<RubikCubeSide> get() = _lastSideScanned

    fun addScannedSide(side: RubikCubeSide) {
        _scannedRubikCubeSides.updateValue { this?.toMutableList()?.apply { add(side) } }
        _lastSideScanned.value = side
    }
}