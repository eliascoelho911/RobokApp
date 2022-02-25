package com.github.eliascoelho911.robok.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Surface.ROTATION_0
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.annotation.AttrRes
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.lifecycle.LifecycleOwner
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.domain.RubikCube.Side
import com.github.eliascoelho911.robok.domain.SidePosition
import com.github.eliascoelho911.robok.factory.SideFactory
import com.github.eliascoelho911.robok.util.converters.toBitmap
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.Executor
import kotlinx.android.synthetic.main.rubik_cube_side_scanner.view.crop_area
import kotlinx.android.synthetic.main.rubik_cube_side_scanner.view.fab_capture
import kotlinx.android.synthetic.main.rubik_cube_side_scanner.view.preview_view

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

        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            DEFAULT_BACK_CAMERA,
            useCaseGroup
        )
    }

    private fun takePicture(
        executor: Executor,
        onCaptured: (Bitmap) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                requireNotNull(image.image)
                val bitmap = image.image!!.toBitmap()
                onCaptured(bitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onFailure.invoke(exception)
            }
        })
    }


    private fun FloatingActionButton.setOnClickCaptureListener(
        executor: Executor,
        onSideCaptured: (Side) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        setOnClickListener {
            takePicture(executor, onCaptured = {
                val cropFrame = run {
                    val rect = Rect()
                    cropView.getGlobalVisibleRect(rect)
                    rect
                }
                val previewFrame = run {
                    val rect = Rect()
                    previewView.getGlobalVisibleRect(rect)
                    rect
                }
                val side = SideFactory.createByImageCaptured(
                    originalImageCaptured = it,
                    cropFrame,
                    previewFrame,
                    SidePosition.FRONT)
                onSideCaptured.invoke(side)
            }, onError)
        }
    }

    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(context) }
    private val preview by lazy {
        Preview.Builder()
            .build()
            .apply { setSurfaceProvider(previewView.surfaceProvider) }
    }
    private val imageCapture: ImageCapture by lazy {
        ImageCapture.Builder()
            .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetRotation(CameraRotation)
            .build()
    }
    private val cropView by lazy { crop_area as GridLayout }
    private val previewView by lazy { preview_view }
}
