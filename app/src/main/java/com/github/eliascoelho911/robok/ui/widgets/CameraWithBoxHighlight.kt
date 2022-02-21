package com.github.eliascoelho911.robok.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
import com.github.eliascoelho911.robok.util.converters.toBitmap
import com.github.eliascoelho911.robok.util.rotate
import java.util.concurrent.Executor
import kotlinx.android.synthetic.main.camera_with_box_highlight.view.camera_preview
import kotlinx.android.synthetic.main.camera_with_box_highlight.view.crop_area

private const val CameraRotation = ROTATION_0

class CameraWithBoxHighlight @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.camera_with_box_highlight, this)
    }

    fun startCamera(lifecycleOwner: LifecycleOwner, executor: Executor) {
        camera_preview.isVisible = true
        cameraProviderFuture.addListener({
            bindCamera(lifecycleOwner)
        }, executor)
    }

    fun closeCamera() {
        cameraProviderFuture.get().unbindAll()
    }

    fun takePicture(
        executor: Executor,
        onFound: (Bitmap) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = image.image?.toBitmap()?.adjustRotation()?.cropHighlight() ?: return
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

    private fun Bitmap.cropHighlight(): Bitmap {
        val heightOriginal = camera_preview.height
        val widthOriginal = camera_preview.width
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

    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(context) }
    private val preview by lazy {
        Preview.Builder()
            .build()
            .apply { setSurfaceProvider(camera_preview.surfaceProvider) }
    }
    private val imageCapture: ImageCapture by lazy {
        ImageCapture.Builder()
            .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetRotation(CameraRotation)
            .build()
    }
}
