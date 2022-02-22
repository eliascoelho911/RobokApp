package com.github.eliascoelho911.robok.ui.screens

import android.Manifest.permission.CAMERA
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.github.eliascoelho911.robok.ui.viewmodels.HomeViewModel
import com.github.eliascoelho911.robok.util.Matrix
import com.github.eliascoelho911.robok.util.getColorsOfGrid
import com.github.eliascoelho911.robok.util.showToast
import com.github.eliascoelho911.robok.util.toMatrix
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
        bindData()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.closeCamera()
    }

    private fun startCamera(permissionIsGranted: Boolean) {
        if (permissionIsGranted) {
            camera.startCamera(viewLifecycleOwner, executor)
            startCaptureUI()
        }
    }

    private fun bindData() {
        viewModel.lastSideScanned.observe(viewLifecycleOwner) {
            rubik_cube_side_preview.rubikCubeSide = it
        }
    }

    private fun clickListeners() {
        fab_capture.setOnClickCaptureListener()
        fab_retry.setOnClickRetryListener()
        fab_ok.setOnClickConfirmListener()
    }

    private fun FloatingActionButton.setOnClickCaptureListener() {
        setOnClickListener {
            camera.takePicture(executor, onFound = { bitmap ->
                runCatching {
                    createSide(bitmap)
                }.onSuccess {
                    viewModel.setLastSideScanned(it)
                    startPreviewUI()
                }.onFailure {
                    showScanCubeSideError()
                }
            }, onFailure = {
                showScanCubeSideError()
                startCaptureUI()
            })
        }
    }

    private fun FloatingActionButton.setOnClickRetryListener() {
        setOnClickListener {
            startCaptureUI()
        }
    }

    private fun FloatingActionButton.setOnClickConfirmListener() {
        setOnClickListener {
            viewModel.lastSideScanned.value?.let(viewModel::addSide)
            startCaptureUI()
        }
    }

    private fun startCaptureUI() {
        hideSideScannedPreview()
        fab_capture.show()
    }

    private fun startPreviewUI() {
        showSideScannedPreview()
        fab_capture.hide()
    }

    private fun hideSideScannedPreview() {
        fade.fadeOut()
        rubik_cube_side_preview.hide()
        crop_area.isVisible = true
        fab_ok.hide()
        fab_retry.hide()
    }

    private fun showSideScannedPreview() {
        fade.fadeIn()
        rubik_cube_side_preview.show()
        crop_area.visibility = INVISIBLE
        fab_ok.show()
        fab_retry.show()
    }

    private fun createSide(bitmap: Bitmap): RubikCubeSide {
        val sideColors = bitmap.cropCubeSide().scanSide()
        val position = viewModel.lastSideScanned.value?.position?.next() ?: SidePosition.first()
        return RubikCubeSide(position, sideColors)
    }

    private fun Bitmap.cropCubeSide(): Bitmap {
        val widthFinal = crop_area.width * width / camera.width
        val heightFinal = crop_area.height * height / camera.height
        val leftFinal = crop_area.left * width / camera.width
        val topFinal = crop_area.top * height / camera.height
        return Bitmap.createBitmap(this, leftFinal, topFinal, widthFinal, heightFinal)
    }

    private fun Bitmap.scanSide(): Matrix<RubikCubeSideColor> {
        return getColorsOfGrid(SideLineHeight, SideLineHeight).toRubikCubeSideColor().toMatrix(
            width = SideLineHeight,
            height = SideLineHeight
        )
    }

    private fun List<Color>.toRubikCubeSideColor() = map {
        RubikCubeSideColor.findBySimilarity(requireContext(), it)
    }

    private fun showScanCubeSideError() {
        requireContext().showToast(getString(R.string.error_capture_cube_side))
    }

    private val executor get() = ContextCompat.getMainExecutor(requireContext())
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var requestPermissionToStartCamera: ActivityResultLauncher<String>
}
