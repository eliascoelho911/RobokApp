package com.github.eliascoelho911.robok.util

class Matrix<T>(
    val width: Int,
    val height: Int,
) : LinkedHashMap<Position, T>() {
    override fun put(key: Position, value: T): T? {
        if (key.x >= width)
            throw IndexOutOfBoundsException("x: ${key.x} >= width: $width")
        if (key.y >= height)
            throw IndexOutOfBoundsException("y: ${key.y} >= height: $height")

        return super.put(key, value)
    }
}

data class Position(val x: Int, val y: Int)

fun <T> List<T>.toMatrix(width: Int, height: Int) =
    Matrix<T>(width, height).also { matrix ->
        forEachIndexed { index, value ->
            matrix[matrix.calculatePosition(index)] = value
        }
    }

private fun Matrix<*>.calculatePosition(index: Int): Position {
    val x = index % (width)
    val y = (index - x) / (height)

    return Position(x, y)
}