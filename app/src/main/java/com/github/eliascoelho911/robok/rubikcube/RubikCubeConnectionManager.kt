package com.github.eliascoelho911.robok.rubikcube

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RubikCubeConnectionManager {
    var isConnected: Flow<Boolean> = flowOf(false)
        private set
}