package com.github.eliascoelho911.robok.ui.screens

import android.Manifest.permission.CAMERA
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import com.github.eliascoelho911.robok.domain.RubikCubeSideColor
import com.github.eliascoelho911.robok.domain.SidePosition
import com.github.eliascoelho911.robok.domain.constants.RubikCubeConstants
import com.github.eliascoelho911.robok.domain.constants.RubikCubeConstants.SideLineHeight
import com.github.eliascoelho911.robok.ui.animation.closeWithAnimation
import com.github.eliascoelho911.robok.ui.animation.openWithAnimation
import com.github.eliascoelho911.robok.ui.viewmodels.HomeViewModel
import com.github.eliascoelho911.robok.ui.widgets.CameraWithBoxHighlight
import com.github.eliascoelho911.robok.util.getColorsOfGrid
import com.github.eliascoelho911.robok.util.showToast
import com.github.eliascoelho911.robok.util.toMatrix
import kotlinx.android.synthetic.main.home_fragment.capture
import kotlinx.android.synthetic.main.home_fragment.camera
import kotlinx.android.synthetic.main.home_fragment.rubik_cube_side_preview
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.home_fragment, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionToStartCamera = registerForActivityResult(RequestPermission()) {
            startCamera(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissionToStartCamera.launch(CAMERA)
        clickListeners()
        observers()
    }

    private fun observers() {
        scannedRubikCubeObserver()
    }

    private fun scannedRubikCubeObserver() {
        viewModel.scannedRubikCubeSides.observe(viewLifecycleOwner) {
            lastSideScanned?.let(rubik_cube_side_preview::show)
            if (it.size == RubikCubeConstants.NumberOfSides)
                capture.closeWithAnimation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.closeCamera()
    }

    private fun startCamera(permissionIsGranted: Boolean) {
        if (permissionIsGranted) {
            camera.startCamera(viewLifecycleOwner, executor)
            capture.openWithAnimation()
        }
    }

    private fun clickListeners() {
        capture.setOnClickListener {
            onClickCaptureListener()
        }
    }

    private fun onClickCaptureListener() {
        capture.isClickable = false
        camera.lookForTheGridColors()
    }

    private fun CameraWithBoxHighlight.lookForTheGridColors() {
        takePicture(executor, onFound = { bitmap ->
            bitmap.scanSide()
        }, onFailure = {
            showScanCubeSideError()
        })
    }

    private fun showScanCubeSideError() {
        requireContext().showToast(getString(R.string.error_capture_cube_side))
    }

    private fun Bitmap.scanSide() {
        getColorsOfGrid(SideLineHeight, SideLineHeight)
            .toRubikCubeSideColor()
            .createAndSaveScannedSide()
    }

    private fun List<RubikCubeSideColor>.createAndSaveScannedSide() {
        val matrix = toMatrix(
            width = SideLineHeight,
            height = SideLineHeight
        )
        val position = lastSideScanned?.position?.next() ?: SidePosition.first()
        viewModel.addScannedSide(RubikCubeSide(position, matrix))
    }

    private fun List<Color>.toRubikCubeSideColor() = map {
        RubikCubeSideColor.findBySimilarity(requireContext(), it)
    }

    private val executor get() = ContextCompat.getMainExecutor(requireContext())
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var requestPermissionToStartCamera: ActivityResultLauncher<String>
    private val lastSideScanned get() = viewModel.scannedRubikCubeSides.value?.lastOrNull()
}
