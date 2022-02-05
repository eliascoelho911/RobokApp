package com.github.eliascoelho911.robok.util.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import java.nio.ByteBuffer

fun Image.toBitmap(): Bitmap {
    val buffer: ByteBuffer = planes[0].buffer
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
}