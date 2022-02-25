package com.github.eliascoelho911.robok.util

import android.graphics.Rect
import android.view.View

fun View.getRect(): Rect = Rect(left, top, right, bottom)