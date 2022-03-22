package com.github.eliascoelho911.robok.rubikcube.face

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.eliascoelho911.robok.ui.widgets.Direction

class FaceScanOrder(vararg orderToScan: Item) {
    private var currentIndex = 0
        set(value) {
            field = value
            _currentFaceToScan.value = facesToScan[currentIndex]
        }
    private val facesToScan = orderToScan.toList()
    private val _currentFaceToScan = MutableLiveData(facesToScan[currentIndex])
    val hasPendentToScan get() = currentIndex < facesToScan.lastIndex
    val currentFaceToScan: LiveData<Item> get() = _currentFaceToScan

    fun nextFace() {
        currentIndex++
    }

    fun backToFirstFace() {
        currentIndex = 0
    }

    data class Item(
        val position: Position,
        val movesToDestination: Int,
        val directionToDestination: Direction?,
    )

    companion object {
        val WithDefaultOrder: FaceScanOrder
            get() = FaceScanOrder(
                Item(Position.UP,
                    movesToDestination = 0,
                    directionToDestination = null),
                Item(Position.DOWN,
                    movesToDestination = 2,
                    directionToDestination = Direction.DOWN),
                Item(Position.FRONT,
                    movesToDestination = 1,
                    directionToDestination = Direction.UP),
                Item(Position.RIGHT,
                    movesToDestination = 1,
                    directionToDestination = Direction.RIGHT),
                Item(Position.BACK,
                    movesToDestination = 1,
                    directionToDestination = Direction.RIGHT),
                Item(Position.LEFT,
                    movesToDestination = 1,
                    directionToDestination = Direction.RIGHT))
    }
}