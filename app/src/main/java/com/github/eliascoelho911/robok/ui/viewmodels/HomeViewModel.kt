package com.github.eliascoelho911.robok.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import com.github.eliascoelho911.robok.ui.states.State.DISABLED
import com.github.eliascoelho911.robok.ui.states.State.ENABLE
import com.github.eliascoelho911.robok.ui.states.MutableUIState
import com.github.eliascoelho911.robok.ui.states.UIState
import com.github.eliascoelho911.robok.util.updateValue

class HomeViewModel : ViewModel() {
    private val _scannedRubikCubeSides = MutableLiveData<List<RubikCubeSide>>(emptyList())
    val scannedRubikCubeSides: LiveData<List<RubikCubeSide>> get() = _scannedRubikCubeSides
    private val _lastSideScanned = MutableLiveData<RubikCubeSide>()
    val lastSideScanned: LiveData<RubikCubeSide> get() = _lastSideScanned
    private val _captureUIState = MutableUIState(initialState = ENABLE)
    val captureUIState: UIState get() = _captureUIState
    private val _previewUIState = MutableUIState(initialState = DISABLED)
    val previewUIState: UIState get() = _previewUIState

    fun addScannedSide(side: RubikCubeSide) {
        _scannedRubikCubeSides.updateValue { it.toMutableList().apply { add(side) } }
        _lastSideScanned.value = side
    }

    fun startCaptureUIState() {
        _captureUIState.value = ENABLE
        _previewUIState.value = DISABLED
    }

    fun startPreviewUIState() {
        _captureUIState.value = DISABLED
        _previewUIState.value = ENABLE
    }
}