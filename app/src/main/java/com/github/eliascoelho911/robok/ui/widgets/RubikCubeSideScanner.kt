package com.github.eliascoelho911.robok.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.view.Surface.ROTATION_0
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.domain.RubikCube.Side
import com.github.eliascoelho911.robok.domain.SidePosition
import com.github.eliascoelho911.robok.domain.constants.RubikCubeConstants
import com.github.eliascoelho911.robok.util.Matrix
import com.github.eliascoelho911.robok.util.converters.toBitmap
import com.github.eliascoelho911.robok.util.getColorsOfGrid
import com.github.eliascoelho911.robok.util.rotate
import com.github.eliascoelho911.robok.util.toMatrix
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.Executor
import kotlinx.android.synthetic.main.home_fragment.view.rubik_cube_side_scanner
import kotlinx.android.synthetic.main.rubik_cube_side_scanner.view.crop_area
import kotlinx.android.synthetic.main.rubik_cube_side_scanner.view.fab_capture
import kotlinx.android.synthetic.main.rubik_cube_side_scanner.view.preview_view
import kotlinx.android.synthetic.main.rubik_cube_side_scanner.view.rubik_cube_side_scanner_container

private const val CameraRotation = ROTATION_0

class RubikCubeSideScanner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.rubik_cube_side_scanner, this)
    }

    fun start(
        lifecycleOwner: LifecycleOwner,
        executor: Executor,
        onSideCaptured: (Side) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        rubik_cube_side_scanner_container.isVisible = true
        cameraProviderFuture.addListener({
            bindCamera(lifecycleOwner)
        }, executor)
        fab_capture.setOnClickCaptureListener(executor, onSideCaptured, onError)
    }

    fun finish() {
        cameraProviderFuture.get().unbindAll()
    }

    private fun bindCamera(lifecycleOwner: LifecycleOwner) {
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()

        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(preview)
            .addUseCase(imageCapture)
            .build()

        with(cameraProvider) {
            bindToLifecycle(
                lifecycleOwner,
                DEFAULT_BACK_CAMERA,
                useCaseGroup
            )
        }
    }

    private fun takePicture(
        executor: Executor,
        onFound: (Bitmap) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = image.image?.toBitmap()?.adjustRotation() ?: return
                onFound(bitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onFailure.invoke(exception)
            }
        })
    }

    private fun Bitmap.adjustRotation(): Bitmap = rotate(90f)

    private fun FloatingActionButton.setOnClickCaptureListener(
        executor: Executor,
        onSideCaptured: (Side) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        setOnClickListener {
            takePicture(executor, onFound = { bitmap ->
                onSideCaptured.invoke(createSide(bitmap))
            }, onError)
        }
    }

    private fun createSide(bitmap: Bitmap): Side {
        val sideColors = bitmap.cropCubeSide().getColorsOfSide()
        return Side(SidePosition.FRONT, sideColors)
    }

    private fun Bitmap.cropCubeSide(): Bitmap {
        val widthFinal = crop_area.width * width / rubik_cube_side_scanner.width
        val heightFinal = crop_area.height * height / rubik_cube_side_scanner.height
        val leftFinal = crop_area.left * width / rubik_cube_side_scanner.width
        val topFinal = crop_area.top * height / rubik_cube_side_scanner.height
        return Bitmap.createBitmap(this, leftFinal, topFinal, widthFinal, heightFinal)
    }

    private fun Bitmap.getColorsOfSide(): Matrix<Color> {
        return getColorsOfGrid(RubikCubeConstants.SideLineHeight,
            RubikCubeConstants.SideLineHeight).toMatrix(
            width = RubikCubeConstants.SideLineHeight,
            height = RubikCubeConstants.SideLineHeight
        )
    }

    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(context) }
    private val preview by lazy {
        Preview.Builder()
            .build()
            .apply { setSurfaceProvider(preview_view.surfaceProvider) }
    }
    private val imageCapture: ImageCapture by lazy {
        ImageCapture.Builder()
            .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetRotation(CameraRotation)
            .build()
    }
}
