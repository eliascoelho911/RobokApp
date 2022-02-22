package com.github.eliascoelho911.robok.ui.screens

import android.Manifest.permission.CAMERA
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import com.github.eliascoelho911.robok.domain.RubikCubeSideColor
import com.github.eliascoelho911.robok.domain.SidePosition
import com.github.eliascoelho911.robok.domain.constants.RubikCubeConstants.SideLineHeight
import com.github.eliascoelho911.robok.ui.animation.fadeIn
import com.github.eliascoelho911.robok.ui.animation.fadeOut
import com.github.eliascoelho911.robok.ui.states.State
import com.github.eliascoelho911.robok.ui.states.State.ENABLE
import com.github.eliascoelho911.robok.ui.viewmodels.HomeViewModel
import com.github.eliascoelho911.robok.ui.widgets.CameraPreview
import com.github.eliascoelho911.robok.util.getColorsOfGrid
import com.github.eliascoelho911.robok.util.showToast
import com.github.eliascoelho911.robok.util.toMatrix
import kotlinx.android.synthetic.main.home_fragment.camera
import kotlinx.android.synthetic.main.home_fragment.crop_area
import kotlinx.android.synthetic.main.home_fragment.fab_capture
import kotlinx.android.synthetic.main.home_fragment.fab_ok
import kotlinx.android.synthetic.main.home_fragment.fab_retry
import kotlinx.android.synthetic.main.home_fragment.fade
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
        setupUIState()
        bindData()
    }


    override fun onDestroy() {
        super.onDestroy()
        camera.closeCamera()
    }

    private fun setupUIState() {
        viewModel.previewUIState.observe(viewLifecycleOwner, ::setupPreviewUIState)
        viewModel.captureUIState.observe(viewLifecycleOwner, ::setupCaptureUIState)
    }

    private fun bindData() {
        viewModel.lastSideScanned.observe(viewLifecycleOwner, rubik_cube_side_preview::show)
    }

    private fun setupPreviewUIState(state: State) {
        if (state == ENABLE) {
            fade.fadeIn()
            rubik_cube_side_preview.isVisible = true
            crop_area.visibility = INVISIBLE
            fab_ok.show()
            fab_retry.show()
        } else {
            fade.fadeOut()
            rubik_cube_side_preview.isVisible = false
            crop_area.isVisible = true
            fab_ok.hide()
            fab_retry.hide()
        }
    }

    private fun setupCaptureUIState(state: State) {
        if (state == ENABLE) {
            fab_capture.show()
        } else {
            fab_capture.hide()
        }
    }

    private fun startCamera(permissionIsGranted: Boolean) {
        if (permissionIsGranted) {
            camera.startCamera(viewLifecycleOwner, executor)
            fab_capture.show()
        }
    }

    private fun clickListeners() {
        fab_capture.setOnClickListener {
            onClickCaptureListener()
        }
    }

    private fun onClickCaptureListener() {
        camera.lookForTheGridColors()
    }

    private fun CameraPreview.lookForTheGridColors() {
        takePicture(executor, onFound = { bitmap ->
            bitmap.cropCubeSide().scanSide()
            viewModel.startPreviewUIState()
        }, onFailure = {
            showScanCubeSideError()
            viewModel.startCaptureUIState()
        })
    }

    private fun Bitmap.cropCubeSide(): Bitmap {
        val widthFinal = crop_area.width * width / camera.width
        val heightFinal = crop_area.height * height / camera.height
        val leftFinal = crop_area.left * width / camera.width
        val topFinal = crop_area.top * height / camera.height
        return Bitmap.createBitmap(this, leftFinal, topFinal, widthFinal, heightFinal)
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
        val position = viewModel.lastSideScanned.value?.position?.next() ?: SidePosition.first()
        runCatching {
            viewModel.addScannedSide(RubikCubeSide(position, matrix))
        }.onFailure {
            showScanCubeSideError()
        }
    }

    private fun List<Color>.toRubikCubeSideColor() = map {
        RubikCubeSideColor.findBySimilarity(requireContext(), it)
    }

    private val executor get() = ContextCompat.getMainExecutor(requireContext())
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var requestPermissionToStartCamera: ActivityResultLauncher<String>
}
