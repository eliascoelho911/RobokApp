package com.github.eliascoelho911.robok.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.ColorSpace
import android.util.AttributeSet
import android.util.Size
import android.view.Surface.ROTATION_0
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.util.converters.toBitmap
import com.github.eliascoelho911.robok.util.dpToPx
import com.github.eliascoelho911.robok.util.rotate
import java.util.concurrent.Executor
import kotlin.math.pow
import kotlin.math.sqrt
import kotlinx.android.synthetic.main.cube_scanner.view.camera_preview
import kotlinx.android.synthetic.main.cube_scanner.view.start_scan

private const val GridItemsMargin = 4

class GridScanner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    var onClickStartScan: () -> Unit = {}
    var columns: Int = -1
        set(value) {
            invalidate()
            field = value
        }
    var rows: Int = -1
        set(value) {
            invalidate()
            field = value
        }

    init {
        inflate(context, R.layout.cube_scanner, this).run {
            _gridView = findViewById(R.id.grid)
        }
        clickListeners()
        initAttrs(attrs)
    }

    fun startCamera(lifecycleOwner: LifecycleOwner, executor: Executor) {
        camera_preview.isVisible = true
        start_scan.isVisible = false
        showGrid()
        _cameraProviderFuture.addListener({
            bindCamera(lifecycleOwner)
        }, executor)
    }

    fun closeCamera() {
        _cameraProviderFuture.get().unbindAll()
    }

    fun lookForTheCubeFace(
        executor: Executor,
        onFound: (colors: List<Int>) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        _imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = image.image?.toBitmap()?.rotate(90f) ?: return
//                val colors = bitmap.getColors()
//                onFound.invoke()
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onFailure.invoke(exception)
            }
        })
    }

    private fun initAttrs(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GridScanner,
            0, 0
        ).apply {
            try {
                columns = getInteger(R.styleable.GridScanner_columns, -1)
                rows = getInteger(R.styleable.GridScanner_rows, -1)
            } finally {
                recycle()
            }
        }
    }

//    private fun Bitmap.getColors() =
//        getColorsOfGridBySimilarity(3, 3)

    private fun bindCamera(lifecycleOwner: LifecycleOwner) {
        val cameraProvider = _cameraProviderFuture.get()
        cameraProvider.unbindAll()

        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(_preview)
            .addUseCase(_imageCapture)
            .setViewPort(camera_preview.viewPort!!)
            .build()

        with(cameraProvider) {
            bindToLifecycle(
                lifecycleOwner,
                DEFAULT_BACK_CAMERA,
                useCaseGroup
            )
        }
    }

    private fun showGrid() {
        _gridView.isVisible = true
        addGridItems()
        adjustSizeOfGridItemsRelativeToParent()
    }

    private fun addGridItems() {
        for (i in 0 until 9) {
            ImageView(context).apply {
                id = generateViewId()
                setImageDrawable(getDrawable(context, R.drawable.outline_square_rounded))
                layoutParams = GridLayout.LayoutParams().apply {
                    setMargins(context.dpToPx(GridItemsMargin))
                }
            }.run {
                _gridView.addView(this)
            }
        }
    }

    private fun adjustSizeOfGridItemsRelativeToParent() {
        _gridView.viewTreeObserver.addOnDrawListener {
            _gridView.children.forEach {
                it.updateLayoutParams {
                    width = (_gridView.width / columns) - (context.dpToPx(GridItemsMargin) * 2)
                    height = (_gridView.height / rows) - (context.dpToPx(GridItemsMargin) * 2)
                }
            }
        }
    }

    private fun clickListeners() {
        start_scan.setOnClickListener {
            onClickStartScan.invoke()
        }
    }

    private var _gridView: GridLayout
    private val _cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(context) }
    private val _preview by lazy {
        Preview.Builder()
            .build()
            .apply { setSurfaceProvider(camera_preview.surfaceProvider) }
    }
    private val _imageCapture: ImageCapture by lazy {
        ImageCapture.Builder()
            .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetResolution(Size(1000, 1000))
            .setTargetRotation(ROTATION_0)
            .build()
    }
}

private fun List<Color>.standardizeBySimilarity(): List<Color> {
    val clusters = getClusters().toList()
    return map { currentColor ->
        clusters.single { it.second.contains(currentColor) }.first
    }
}

private fun List<Color>.getClusters(): Map<Color, List<Color>> {
    val colorsOutOfClusters = toMutableList()
    val clusters = mutableListOf<List<Color>>()

    forEach { currentColor ->
        if (currentColor in colorsOutOfClusters) {
            colorsOutOfClusters.filter {
                currentColor.similarityFrom(it) < 30f
            }.let {
                clusters.add(it)
                colorsOutOfClusters.removeAll(it)
            }
        }
    }

    return clusters.associateBy { cluster ->
        val defaultColor = cluster.maxByOrNull { it.luminance() }!!
        defaultColor
    }
}

private fun Color.similarityFrom(other: Color): Float {
    val referenceCL = ColorSpace.connect(colorSpace, ColorSpace.get(ColorSpace.Named.CIE_LAB))
        .transform(red(), green(), blue())
    val otherCL = ColorSpace.connect(other.colorSpace, ColorSpace.get(ColorSpace.Named.CIE_LAB))
        .transform(other.red(), other.green(), other.blue())

    return sqrt((otherCL[0] - referenceCL[0]).pow(2)
            + (otherCL[1] - referenceCL[1]).pow(2)
            + (otherCL[2] - referenceCL[2]).pow(2))
}