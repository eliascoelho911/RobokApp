package com.github.eliascoelho911.robok.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.IntegerRes
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

fun Context.dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).roundToInt()

fun Context.pxToDp(px: Int): Int {
    val displayMetrics: DisplayMetrics = resources.displayMetrics
    return (px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

@ColorInt
fun Context.getAttrColor(@AttrRes attrRes: Int, typedValue: TypedValue = TypedValue()): Int {
    theme.resolveAttribute(attrRes, typedValue, true)
    return ContextCompat.getColor(this, typedValue.resourceId)
}