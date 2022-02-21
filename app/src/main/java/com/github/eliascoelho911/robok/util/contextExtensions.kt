package com.github.eliascoelho911.robok.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.IntegerRes
import kotlin.math.roundToInt

fun Context.getPictureDirectory(): String = ContextWrapper(this).run {
    getDir("pictures", Context.MODE_PRIVATE).toString()
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

fun Context.dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).roundToInt()

fun Context.pxToDp(px: Int): Int {
    val displayMetrics: DisplayMetrics = resources.displayMetrics
    return (px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun Activity.displayMetrics(): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics
}