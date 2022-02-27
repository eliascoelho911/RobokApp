package com.github.eliascoelho911.robok.util

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

fun Batch.transaction(block: Batch.() -> Unit) {
    begin()
    block()
    end()
}

fun ShapeRenderer.transaction(
    shapeType: ShapeType = ShapeType.Line,
    block: ShapeRenderer.() -> Unit,
) {
    begin(shapeType)
    block()
    end()
}