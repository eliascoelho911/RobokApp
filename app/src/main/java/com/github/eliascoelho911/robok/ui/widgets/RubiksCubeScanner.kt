package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.ui.animation.openWithAnimation
import com.github.eliascoelho911.robok.util.dpToPx
import kotlinx.android.synthetic.main.rubiks_cube_scanner.view.*

private const val GridItemsMargin = 4

class RubiksCubeScanner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    var onClickStartScan: () -> Unit = {}

    private val _gridView: GridLayout
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(context) }

    init {
        inflate(context, R.layout.rubiks_cube_scanner, this).run {
            _gridView = findViewById(R.id.grid)
        }
        clickListeners()
    }

    fun startCamera() {
        camera_preview.isVisible = true
        start_scan.isVisible = false
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCameraPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        showGrid()

        val preview = Preview.Builder()
            .build()
            .apply { setSurfaceProvider(camera_preview.surfaceProvider) }

        with(cameraProvider) {
            unbindAll()
            bindToLifecycle(
                findViewTreeLifecycleOwner()!!,
                DEFAULT_BACK_CAMERA,
                preview
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
        _gridView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                _gridView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                _gridView.children.forEach {
                    it.updateLayoutParams {
                        width = (_gridView.width / 3) - (context.dpToPx(GridItemsMargin) * 2)
                        height = (_gridView.height / 3) - (context.dpToPx(GridItemsMargin) * 2)
                    }
                }
            }
        })
    }

    private fun clickListeners() {
        start_scan.setOnClickListener {
            onClickStartScan.invoke()
        }
    }
}