package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.annotation.AnimRes
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.databinding.FaceScannerBinding
import com.github.eliascoelho911.robok.ui.analyzers.FaceColorsAnalyzer
import com.github.eliascoelho911.robok.ui.analyzers.FaceImageCropper
import com.github.eliascoelho911.robok.util.getRect
import com.github.eliascoelho911.robok.util.setOnAnimationEndListener
import com.google.android.material.card.MaterialCardView
import java.util.concurrent.Executor

class FaceScannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = FaceScannerBinding.inflate(LayoutInflater.from(context), this, true)

    @ColorInt
    private var lastColorsScanned: List<Int> = emptyList()
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(context) }
    private val preview by lazy {
        Preview.Builder()
            .build()
            .apply { surfaceProvider = binding.previewView.surfaceProvider }
    }
    private val imageCapture: ImageCapture by lazy {
        ImageCapture.Builder()
            .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    private val imageAnalysis by lazy {
        ImageAnalysis.Builder().build()
    }
    private val faceColorsAnalyzer by lazy {
        FaceColorsAnalyzer(cropViewRect, previewViewRect, onSuccess = { colors ->
            showColorsOnPreview(colors)
            lastColorsScanned = colors
        }, faceImageCropper = FaceImageCropper())
    }
    private val showColorsScannedAnim by lazy { loadAnimation(R.anim.show_colors_scanned) }
    private val cropView get() = binding.cropArea.root
    private val previewView get() = binding.previewView
    private val hintTextView get() = binding.txtHint
    private val hintMultiplierTextView get() = binding.txtHintMultiplier
    private val arrowHintView get() = binding.imgHint
    private val hintContainerView get() = binding.hintContainer
    private val cropViewRect: Rect get() = cropView.getRect()
    private val previewViewRect: Rect get() = binding.previewView.getRect()

    fun start(
        lifecycleOwner: LifecycleOwner,
        executor: Executor,
    ) {
        cameraProviderFuture.addListener({
            bindCamera(lifecycleOwner, executor)
        }, executor)
    }

    fun finish() {
        cameraProviderFuture.get().unbindAll()
    }

    @ColorInt
    fun scanColorsOfFace(): List<Int> {
        showColorsScanned()
        return lastColorsScanned
    }

    fun showHintToScanFace(
        moves: Int = 1,
        direction: Direction?,
        onAnimationEnd: () -> Unit,
    ) {
        if (moves == 0 && direction == null) {
            showHintToFirstScanFace()
            onAnimationEnd()
        } else if (direction != null) {
            showMessage(moves, direction)
            showMultiplierIfMovesThanGreaterOne(moves)
            adjustArrowDirection(direction)
            showHintAnimation(direction, onAnimationEnd)
        }
    }

    private fun showHintToFirstScanFace() {
        hintTextView.text = context.getString(R.string.scan_hint_any_face)
    }

    private fun showMessage(moves: Int, direction: Direction) {
        hintTextView.text = when (moves) {
            0 -> {
                context.getString(R.string.scan_hint_any_face)
            }
            1 -> {
                context.getString(R.string.scan_hint_one_move, getLiteral(direction))
            }
            else -> {
                context.getString(R.string.scan_hint_various_moves, moves, getLiteral(direction))
            }
        }
    }

    private fun getLiteral(direction: Direction) = context.getString(direction.directionLiteralRes)

    private fun bindCamera(lifecycleOwner: LifecycleOwner, executor: Executor) {
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()

        imageAnalysis.setAnalyzer(executor, faceColorsAnalyzer)

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

    private fun showMultiplierIfMovesThanGreaterOne(moves: Int) {
        hintMultiplierTextView.text = moves.takeIf { it > 1 }?.let {
            context.getString(R.string.scan_hint_multiplier, it.toString())
        }.orEmpty()
    }

    private fun adjustArrowDirection(direction: Direction) {
        arrowHintView.rotation = direction.arrowDegrees
    }

    private fun showHintAnimation(direction: Direction, onAnimationEnd: () -> Unit) {
        AnimationUtils.loadAnimation(context, direction.animationRes).let { animation ->
            animation.setOnAnimationEndListener(onAnimationEnd)
            hintContainerView.apply {
                isVisible = true
                startAnimation(animation)
            }
        }
    }

    private fun showColorsScanned() {
        cropView.forEachIndexed { index, view ->
            view.findViewById<View>(R.id.color_fill).apply {
                this as MaterialCardView
                setCardBackgroundColor(lastColorsScanned[index])
                isVisible = true
                startAnimation(showColorsScannedAnim)
            }
        }
    }

    private fun showColorsOnPreview(colors: List<Int>) {
        cropView.forEachIndexed { index, view ->
            view as FrameLayout
            view.findViewById<View>(R.id.color_preview).setBackgroundColor(colors[index])
        }
    }

    private fun loadAnimation(anim: Int) = AnimationUtils.loadAnimation(context, anim)
}

private const val ARROW_RIGHT = 90f
private const val ARROW_UP = 0f
private const val ARROW_DOWN = 180f

enum class Direction(
    val arrowDegrees: Float,
    @AnimRes val animationRes: Int,
    @StringRes val directionLiteralRes: Int,
) {
    RIGHT(ARROW_RIGHT, R.anim.hint_arrow_right, R.string.scan_hint_right),
    UP(ARROW_UP, R.anim.hint_arrow_up, R.string.scan_hint_up),
    DOWN(ARROW_DOWN, R.anim.hint_arrow_down, R.string.scan_hint_down)
}