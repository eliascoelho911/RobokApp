package com.github.eliascoelho911.robok.ui.viewmodels

import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.rubikcube.face.FaceScanOrder

class CaptureViewModel(private val faceScanOrderManager: FaceScanOrder) : ViewModel() {
    private var scannedRubikCubeBuilder = RubikCube.Builder()
    private val _scannedRubikCube = MutableLiveData<RubikCube>()
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