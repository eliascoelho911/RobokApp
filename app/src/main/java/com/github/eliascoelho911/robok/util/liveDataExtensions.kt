package com.github.eliascoelho911.robok.util

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.updateValue(block: (T) -> T) {
    value = value?.let { block(it) }
}