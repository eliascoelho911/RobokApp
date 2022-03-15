package com.github.eliascoelho911.robok.rubikcube.face

import android.os.Parcelable
import androidx.annotation.ColorInt
import com.github.eliascoelho911.robok.rubikcube.RubikCube.Companion.NumberOfFacelets
import kotlinx.android.parcel.Parcelize

@Parcelize
class Face(
    val position: Position,
    @ColorInt val colors: List<Int>,
): Parcelable {
    constructor(position: Position, @ColorInt vararg colors: Int) : this(position, colors.toList())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Face

        return centerColor(colors) == centerColor(other.colors)
    }

    override fun hashCode(): Int {
        return centerColor(colors).hashCode()
    }
}

private fun centerColor(colors: List<Int>) = colors[(NumberOfFacelets - 1) / 2]