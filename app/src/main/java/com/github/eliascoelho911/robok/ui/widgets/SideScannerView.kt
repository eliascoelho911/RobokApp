package com.github.eliascoelho911.robok.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.annotation.AttrRes
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageAnalysis
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
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.side.Side
import com.github.eliascoelho911.robok.rubikcube.side.SideColorsAnalyzer
import com.github.eliascoelho911.robok.util.converters.toBitmap
import com.github.eliascoelho911.robok.util.getRect
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.Executor
import kotlinx.android.synthetic.main.side_scanner.view.crop_area
import kotlinx.android.synthetic.main.side_scanner.view.fab_capture
import kotlinx.android.synthetic.main.side_scanner.view.preview_view

class SideScannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.side_scanner, this)
    }

    fun start(
        lifecycleOwner: LifecycleOwner,
        executor: Executor,
        onSideCaptured: (Side) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        cameraProviderFuture.addListener({
            bindCamera(lifecycleOwner, executor)
        }, executor)

        fab_capture.setOnClickCaptureListener(executor, onSideCaptured, onError)
    }

    fun finish() {
        cameraProviderFuture.get().unbindAll()
    }

    private fun bindCamera(lifecycleOwner: LifecycleOwner, executor: Executor) {
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()

        imageAnalysis.setAnalyzer(executor, sideColorsAnalyzer)

        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(preview)
            .addUseCase(imageCapture)
            .addUseCase(imageAnalysis)
            .run {
                previewView.viewPort?.let { setViewPort(it) } ?: this
            }
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
        onError: (Throwable) -> Unit,
    ) {
        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                image.image!!.toBitmap()!!.let { bitmap ->
                    onCaptured.invoke(bitmap)
                    image.close()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onError.invoke(exception)
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
                val side = Side.Factory.createByImageCaptured(
                    originalImageCaptured = it,
                    cropViewRect,
                    previewViewRect,
                    RubikCube.SidePosition.FRONT)
                onSideCaptured.invoke(side)
            }, onError)
        }
    }

    private fun showColorsOnPreview(colors: List<Int>) {
        cropView.forEachIndexed { index, view ->
            view as FrameLayout
            view.children.first().setBackgroundColor(colors[index])
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
            .build()
    }
    private val imageAnalysis by lazy {
        ImageAnalysis.Builder().build()
    }
    private val sideColorsAnalyzer by lazy {
        SideColorsAnalyzer(cropViewRect, previewViewRect, onSuccess = { colors ->
            showColorsOnPreview(colors)
        })
    }
    private val cropView by lazy { crop_area as GridLayout }
    private val previewView by lazy { preview_view }
    private val cropViewRect: Rect get() = cropView.getRect()
    private val previewViewRect: Rect get() = previewView.getRect()
}
