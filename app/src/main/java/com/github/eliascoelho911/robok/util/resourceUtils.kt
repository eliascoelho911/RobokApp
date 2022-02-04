package com.github.eliascoelho911.robok.util

import android.content.Context
import androidx.annotation.IntegerRes

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)