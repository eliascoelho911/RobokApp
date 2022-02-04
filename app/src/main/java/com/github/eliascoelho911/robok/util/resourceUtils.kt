package com.github.eliascoelho911.robok.util

import android.content.Context
import androidx.annotation.IntegerRes
import kotlin.math.roundToInt

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

fun Context.dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).roundToInt()