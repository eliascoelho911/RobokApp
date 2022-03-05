package com.github.eliascoelho911.robok.util

import android.graphics.Color
import android.graphics.ColorSpace
import androidx.annotation.ColorInt
import kotlin.math.pow
import kotlin.math.sqrt

fun createColorFrom(@ColorInt int: Int) = Color.valueOf(int)

fun Color.similarityFrom(other: Color): Float {
    val referenceCL = ColorSpace.connect(colorSpace, ColorSpace.get(ColorSpace.Named.CIE_LAB))
        .transform(red(), green(), blue())
    val otherCL = ColorSpace.connect(other.colorSpace, ColorSpace.get(ColorSpace.Named.CIE_LAB))
        .transform(other.red(), other.green(), other.blue())

    return sqrt((otherCL[0] - referenceCL[0]).pow(2)
            + (otherCL[1] - referenceCL[1]).pow(2)
            + (otherCL[2] - referenceCL[2]).pow(2))
}