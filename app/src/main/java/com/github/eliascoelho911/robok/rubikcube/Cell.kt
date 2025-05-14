package com.github.eliascoelho911.robok.rubikcube

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cell(
    val x: Int,
    val y: Int,
    @ColorInt val color: Int
) : Parcelable