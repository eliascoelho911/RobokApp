package com.github.eliascoelho911.robok.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.eliascoelho911.robok.rubikcube.RubikCubeConnectionManager
import kotlin.coroutines.CoroutineContext

class RubikCubeSolveViewModel(
    connectionManager: RubikCubeConnectionManager,
    context: CoroutineContext,
) : ViewModel() {
    val isConnectedWithRobot = connectionManager.isConnected.asLiveData(context)
}