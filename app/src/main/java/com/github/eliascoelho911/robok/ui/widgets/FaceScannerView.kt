package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.annotation.AnimRes
import androidx.annotation.AttrRes
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
import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.rubikcube.face.FaceColorsAnalyzer
import com.github.eliascoelho911.robok.rubikcube.face.Position
import com.github.eliascoelho911.robok.rubikcube.face.Position.BACK
import com.github.eliascoelho911.robok.rubikcube.face.Position.DOWN
import com.github.eliascoelho911.robok.rubikcube.face.Position.FRONT
import com.github.eliascoelho911.robok.rubikcube.face.Position.LEFT
import com.github.eliascoelho911.robok.rubikcube.face.Position.RIGHT
import com.github.eliascoelho911.robok.rubikcube.face.Position.UP
import com.github.eliascoelho911.robok.util.getRect
import com.github.eliascoelho911.robok.util.setOnAnimationEndListener
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.Executor
import kotlinx.android.synthetic.main.face_scanner.view.crop_area
import kotlinx.android.synthetic.main.face_scanner.view.fab_capture
import kotlinx.android.synthetic.main.face_scanner.view.fab_reset
import kotlinx.android.synthetic.main.face_scanner.view.hint_container
import kotlinx.android.synthetic.main.face_scanner.view.img_hint
import kotlinx.android.synthetic.main.face_scanner.view.preview_view
import kotlinx.android.synthetic.main.face_scanner.view.txt_hint
import kotlinx.android.synthetic.main.face_scanner.view.txt_hint_multiplier

class FaceScannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.face_scanner, this)
    }

    fun start(
        lifecycleOwner: LifecycleOwner,
        executor: Executor,
        onFaceCaptured: (Face) -> Unit = {},
        onReset: () -> Unit = {},
        onFinish: () -> Unit = {},
    ) {
        cameraProviderFuture.addListener({
            bindCamera(lifecycleOwner, executor)
        }, executor)

        captureFabView.setOnClickCaptureListener(onFaceCaptured, onFinish)
        resetFabView.setOnClickResetListener(onReset)

        showHintMessage()
    }

    fun finish() {
        cameraProviderFuture.get().unbindAll()
    }

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

    private fun FloatingActionButton.setOnClickCaptureListener(
        onFaceCaptured: (Face) -> Unit,
        onFinish: () -> Unit,
    ) {
        setOnClickListener {
            isClickable = false
            resetFabView.isClickable = false

            onFaceCaptured.invoke(Face(scanOrder.current.position, lastColorsScanned))

            if (scanOrder.hasNext) {
                scanOrder.next()
            } else {
                captureFabView.hide()
                onFinish()
            }

            showColorsScanned()
            showHint(onAnimationEnd = {
                isClickable = true
                resetFabView.isClickable = true
            })
        }
    }

    private fun FloatingActionButton.setOnClickResetListener(onReset: () -> Unit) {
        setOnClickListener {
            scanOrder.restart()
            showHintMessage()
            onReset()
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

    private fun showHint(onAnimationEnd: () -> Unit) {
        showHintMessage()
        scanOrder.current.animation?.let {
            AnimationUtils.loadAnimation(context, it)
        }?.let { animation ->
            hintMultiplierTextView.text = scanOrder.current.multiplier.takeIf { it > 1 }?.let {
                context.getString(R.string.scan_hint_multiplier, it.toString())
            }.orEmpty()

            arrowHintView.rotation = scanOrder.current.arrowDegrees

            hintContainerView.apply {
                isVisible = true
                animation.setOnAnimationEndListener(onAnimationEnd)
                startAnimation(animation)
            }
        } ?: onAnimationEnd()
    }

    private fun showHintMessage() {
        hintTextView.text = context.getString(scanOrder.current.messageRes)
    }

    private fun showColorsOnPreview(colors: List<Int>) {
        cropView.forEachIndexed { index, view ->
            view as FrameLayout
            view.findViewById<View>(R.id.color_preview).setBackgroundColor(colors[index])
        }
    }

    private fun loadAnimation(anim: Int) = AnimationUtils.loadAnimation(context, anim)

    private var lastColorsScanned: List<Int> = emptyList()
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
    private val faceColorsAnalyzer by lazy {
        FaceColorsAnalyzer(cropViewRect, previewViewRect, onSuccess = { colors ->
            showColorsOnPreview(colors)
            lastColorsScanned = colors
        })
    }
    private val showColorsScannedAnim by lazy { loadAnimation(R.anim.show_colors_scanned) }
    private val scanOrder by lazy {
        ScanOrder(ScanOrderItem(UP, animation = null, R.string.scan_hint_any_face),
            ScanOrderItem(DOWN, R.anim.hint_arrow_down, R.string.scan_hint_two_down, ARROW_DOWN, 2),
            ScanOrderItem(FRONT, R.anim.hint_arrow_up, R.string.scan_hint_next_up, ARROW_UP),
            ScanOrderItem(RIGHT, R.anim.hint_arrow_right, R.string.scan_hint_next_right),
            ScanOrderItem(BACK, R.anim.hint_arrow_right, R.string.scan_hint_next_right),
            ScanOrderItem(LEFT, R.anim.hint_arrow_right, R.string.scan_hint_next_right))
    }
    private val cropView by lazy { crop_area as GridLayout }
    private val previewView by lazy { preview_view }
    private val hintTextView by lazy { txt_hint }
    private val hintMultiplierTextView by lazy { txt_hint_multiplier }
    private val arrowHintView by lazy { img_hint }
    private val hintContainerView by lazy { hint_container }
    private val resetFabView by lazy { fab_reset }
    private val captureFabView by lazy { fab_capture }
    private val cropViewRect: Rect get() = cropView.getRect()
    private val previewViewRect: Rect get() = previewView.getRect()
}

private class ScanOrder(vararg scanOrderItem: ScanOrderItem) {
    private var currentIndex = 0
        set(value) {
            field = value
            _current = list[currentIndex]
        }
    private val list = scanOrderItem.toList()
    private var _current = list[currentIndex]
    val current get() = _current

    val hasNext get() = currentIndex < list.lastIndex

    fun next(): ScanOrderItem {
        ++currentIndex

        return current
    }

    fun restart() {
        currentIndex = 0
    }
}

private const val ARROW_RIGHT = 90f
private const val ARROW_UP = 0f
private const val ARROW_DOWN = 180f

private data class ScanOrderItem(
    val position: Position,
    @AnimRes val animation: Int?,
    @StringRes val messageRes: Int,
    val arrowDegrees: Float = ARROW_RIGHT,
    val multiplier: Int = 1,
)
