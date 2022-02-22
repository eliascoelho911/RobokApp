package com.github.eliascoelho911.robok.ui.states

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

typealias UIState = LiveData<State>

class MutableUIState(initialState: State) : MutableLiveData<State>(initialState)

enum class State {
    ENABLE, DISABLED
}