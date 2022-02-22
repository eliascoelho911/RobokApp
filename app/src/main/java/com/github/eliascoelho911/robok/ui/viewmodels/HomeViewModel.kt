package com.github.eliascoelho911.robok.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import com.github.eliascoelho911.robok.util.updateValue

class HomeViewModel : ViewModel() {
    private val _rubikCubeSides = MutableLiveData<List<RubikCubeSide>>(emptyList())
    val rubikCubeSides: LiveData<List<RubikCubeSide>> get() = _rubikCubeSides
    private val _lastSideScanned = MutableLiveData<RubikCubeSide>()
    val lastSideScanned: LiveData<RubikCubeSide> get() = _lastSideScanned

    fun addSide(side: RubikCubeSide) {
        _rubikCubeSides.updateValue { it.toMutableList().apply { add(side) } }
    }

    fun setLastSideScanned(side: RubikCubeSide) {
        _lastSideScanned.value = side
    }
}