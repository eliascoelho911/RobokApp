package com.github.eliascoelho911.robok.ui.screens.capture

import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.rubikcube.face.Position
import com.github.eliascoelho911.robok.ui.widgets.Direction

class CaptureViewModel : ViewModel() {
    private var scannedRubikCubeBuilder = RubikCube.Builder()
    private val _scannedRubikCube = MutableLiveData<RubikCube>()
    private val faceScanOrderManager = FaceScanOrderManager.Default
    val currentFaceToScan = faceScanOrderManager.currentFaceToScan
    val scannedRubikCube: LiveData<RubikCube> get() = _scannedRubikCube

    fun resetScan() {
        faceScanOrderManager.backToFirstFace()
        scannedRubikCubeBuilder = RubikCube.Builder()
    }

    fun finishesScanningTheCurrentFace(@ColorInt colors: List<Int>) {
        scannedRubikCubeBuilder.withFace(Face(currentFaceToScan.value!!.position, colors))
        if (faceScanOrderManager.hasPendentToScan)
            requestToScanNextFace()
        else
            createRubikCube()
    }

    private fun createRubikCube() {
        _scannedRubikCube.value = scannedRubikCubeBuilder.build()
    }

    private fun requestToScanNextFace() {
        faceScanOrderManager.nextFace()
    }
}

class FaceScanOrderManager(vararg orderToScan: Item) {
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
        val Default = FaceScanOrderManager(
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