package com.github.eliascoelho911.robok.ui.screens

import android.Manifest.permission.CAMERA
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
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
import com.github.eliascoelho911.robok.ui.animation.AnimationDurations.short
import com.github.eliascoelho911.robok.ui.animation.openWithAnimation
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
        observers()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.closeCamera()
    }

    private fun observers() {
        lastSideScannedObserver()
    }

    private fun lastSideScannedObserver() {
        viewModel.lastSideScanned.observe(viewLifecycleOwner) {
            it.showPreview()
            fab_capture.hide()
            fab_ok.show()
            fab_retry.show()
        }
    }

    private fun RubikCubeSide.showPreview() {
        rubik_cube_side_preview.show(this)
        fade.animate()
            .alphaBy(0f)
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(short)
            .start()
        fade.isVisible = true
    }

    private fun startCamera(permissionIsGranted: Boolean) {
        if (permissionIsGranted) {
            camera.startCamera(viewLifecycleOwner, executor)
            fab_capture.openWithAnimation()
        }
    }

    private fun clickListeners() {
        fab_capture.setOnClickListener {
            onClickCaptureListener()
        }
    }

    private fun onClickCaptureListener() {
        fab_capture.isClickable = false
        camera.lookForTheGridColors()
    }

    private fun CameraPreview.lookForTheGridColors() {
        takePicture(executor, onFound = { bitmap ->
            bitmap.cropCubeSide().scanSide()
        }, onFailure = {
            showScanCubeSideError()
        })
    }

    private fun Bitmap.cropCubeSide(): Bitmap {
        val heightOriginal = camera.height
        val widthOriginal = camera.width
        val heightFrame = crop_area.height
        val widthFrame = crop_area.width
        val leftFrame = crop_area.left
        val topFrame = crop_area.top
        val heightReal = height
        val widthReal = width
        val widthFinal = widthFrame * widthReal / widthOriginal
        val heightFinal = heightFrame * heightReal / heightOriginal
        val leftFinal = leftFrame * widthReal / widthOriginal
        val topFinal = topFrame * heightReal / heightOriginal
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
