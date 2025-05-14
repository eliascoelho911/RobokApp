package com.github.eliascoelho911.robok.util

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams

fun View.getRect(): Rect = Rect(left, top, right, bottom)

fun View.addImePadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

        v.updateLayoutParams<MarginLayoutParams> {
            leftMargin = leftMargin + insets.left
            bottomMargin = bottomMargin + insets.bottom
            rightMargin = rightMargin + insets.right
        }

        WindowInsetsCompat.CONSUMED
    }
}