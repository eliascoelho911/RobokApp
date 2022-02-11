package com.github.eliascoelho911.robok.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Size
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
import androidx.camera.view.PreviewView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.github.eliascoelho911.robok.analyzers.getColorsOfGrid
import com.github.eliascoelho911.robok.constants.RubikCubeConstants.LineHeight
import com.github.eliascoelho911.robok.util.converters.toBitmap
import com.github.eliascoelho911.robok.util.rotate
import java.util.concurrent.Executor

class RubikCubeSideScanner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addView(_previewView)
    }

    fun startCamera(lifecycleOwner: LifecycleOwner, executor: Executor) {
        _previewView.isVisible = true
        _cameraProviderFuture.addListener({
            bindCamera(lifecycleOwner)
        }, executor)
    }

    fun closeCamera() {
        _cameraProviderFuture.get().unbindAll()
    }

    fun lookForTheGridColors(
        executor: Executor,
        onFound: (colors: List<Color>) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        _imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = image.image?.toBitmap()?.rotate(90f) ?: return
                onFound.invoke(bitmap.getColorsOfGrid(LineHeight, LineHeight))
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onFailure.invoke(exception)
            }
        })
    }

    private fun bindCamera(lifecycleOwner: LifecycleOwner) {
        val cameraProvider = _cameraProviderFuture.get()
        cameraProvider.unbindAll()

        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(_preview)
            .addUseCase(_imageCapture)
            .setViewPort(_previewView.viewPort!!)
            .build()

        with(cameraProvider) {
            bindToLifecycle(
                lifecycleOwner,
                DEFAULT_BACK_CAMERA,
                useCaseGroup
            )
        }
    }

    private val _cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(context) }
    private val _preview by lazy {
        Preview.Builder()
            .build()
            .apply { setSurfaceProvider(_previewView.surfaceProvider) }
    }
    private val _imageCapture: ImageCapture by lazy {
        ImageCapture.Builder()
            .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetResolution(Size(1000, 1000))
            .setTargetRotation(ROTATION_0)
            .build()
    }
    private val _previewView: PreviewView = PreviewView(context, attrs, defStyleAttr)
}