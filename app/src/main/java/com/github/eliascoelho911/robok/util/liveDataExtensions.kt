package com.github.eliascoelho911.robok.util

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.notifyObservers() {
    this.value = this.value
}

fun <T> MutableLiveData<T>.updateValue(block: T.() -> Unit) {
    value?.run {
        block()
        notifyObservers()
    }
}