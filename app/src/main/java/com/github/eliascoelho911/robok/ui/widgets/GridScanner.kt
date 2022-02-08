package com.github.eliascoelho911.robok.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Size
import android.view.Surface.ROTATION_0
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
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
import com.github.eliascoelho911.robok.analyzers.getColorsOfGrid
import com.github.eliascoelho911.robok.ui.animation.AnimationDurations
import com.github.eliascoelho911.robok.ui.animation.CenterAlignedScaleAnimation
import com.github.eliascoelho911.robok.util.converters.toBitmap
import com.github.eliascoelho911.robok.util.dpToPx
import com.github.eliascoelho911.robok.util.rotate
import java.util.concurrent.Executor
import kotlinx.android.synthetic.main.cube_scanner.view.camera_preview
import kotlinx.android.synthetic.main.cube_scanner.view.fade
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
        _gridView.show()
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
                onFound.invoke(bitmap.getColorsOfGrid(rows, columns))
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onFailure.invoke(exception)
            }
        })
    }

    fun coloringItem(index: Int, color: Color, onAnimationEnd: () -> Unit = {}) {
        val drawable = getDrawable(context, R.drawable.rounded_cell_grid)
            ?.apply { setTint(color.toArgb()) }
        val child = _gridView.getChildAt(index) as ImageView

        child.setImageDrawableWithScaleInAnimation(drawable, onAnimationEnd)
        fade.startFadeAnimation()
    }

    fun removeItemColor(index: Int) {
        val drawable = getDrawable(context, R.drawable.outline_rounded_cell_grid)

        val child = _gridView.getChildAt(index) as ImageView
        child.scaleOutAnimation(onAnimationEnd = {
            child.setImageDrawable(drawable)
            child.isVisible = true
        })
    }

    private fun ImageView.setImageDrawableWithScaleInAnimation(
        drawable: Drawable?,
        onAnimationEnd: () -> Unit,
    ) {
        isVisible = false
        setImageDrawable(drawable)
        val scaleAnimation = _scaleInAnimation.apply {
            setOnAnimationEndListener(onAnimationEnd)
        }
        startAnimation(scaleAnimation)
        isVisible = true
    }

    private fun ImageView.scaleOutAnimation(
        onAnimationEnd: () -> Unit,
    ) {
        val scaleAnimation = _scaleOutAnimation.apply {
            setOnAnimationEndListener {
                isVisible = false
                onAnimationEnd()
            }
        }
        startAnimation(scaleAnimation)
    }

    private fun View.startFadeAnimation() {
        startAnimation(_fadeAnimation)
        isVisible = true
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

    private fun GridLayout.show() {
        isVisible = true
        addGridItems()
        adjustSizeOfGridItemsRelativeToParent()
    }

    private fun addGridItems() {
        for (i in 0 until 9) {
            ImageView(context).apply {
                id = generateViewId()
                setImageDrawable(getDrawable(context, R.drawable.outline_rounded_cell_grid))
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

    private fun Animation.setOnAnimationEndListener(onAnimationEnd: () -> Unit) {
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                onAnimationEnd.invoke()
            }

            override fun onAnimationRepeat(p0: Animation?) {}
        })
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
    private val _fadeAnimation: Animation
        get() {
            val fadeIn = AlphaAnimation(0f, 0.7f).apply {
                interpolator = DecelerateInterpolator()
                duration = AnimationDurations.medium
            }
            val fadeOut = AlphaAnimation(0.7f, 0f).apply {
                interpolator = AccelerateInterpolator()
                startOffset = AnimationDurations.long
                duration = AnimationDurations.medium
            }
            return AnimationSet(false).apply {
                fillBefore = true
                fillAfter = true
            }.apply {
                addAnimation(fadeIn)
                addAnimation(fadeOut)
            }
        }
    private val _scaleInAnimation: Animation
        get() =
            CenterAlignedScaleAnimation(
                fromX = 0f,
                toX = 1f,
                fromY = 0f,
                toY = 1f,
            ).apply {
                duration = AnimationDurations.short
                interpolator = AccelerateInterpolator()
                fillAfter = true
                fillBefore = true
            }
    private val _scaleOutAnimation: Animation
        get() = CenterAlignedScaleAnimation(
            fromX = 1f,
            toX = 0f,
            fromY = 1f,
            toY = 0f,
        ).apply {
            duration = AnimationDurations.short
            interpolator = DecelerateInterpolator()
            startOffset = AnimationDurations.medium
            fillBefore = true
        }
}