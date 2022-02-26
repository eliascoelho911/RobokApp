package com.github.eliascoelho911.robok.ui.widgets

import android.R.integer.config_longAnimTime
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getDrawable
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.ui.widgets.ConnectionStatus.DISCONNECTED
import com.github.eliascoelho911.robok.util.getInteger
import kotlinx.android.synthetic.main.app_toolbar.view.*

class AppToolbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : Toolbar(context, attrs, defStyleAttr) {
    var onClickRefreshButton: () -> Unit = {}
    var connectionStatus: ConnectionStatus = DISCONNECTED
        set(value) {
            stopLoadingAnimation()
            field = value
            updateConnectionStatusIconView()
        }

    private val statusDrawable: Drawable
        get() {
            return if (connectionStatus == DISCONNECTED)
                getDrawable(context, R.drawable.ic_usb_off_24dp)!!.apply { setTint(redColor) }
            else
                getDrawable(context, R.drawable.ic_usb_24dp)!!.apply { setTint(greenColor) }
        }

    private val loadingAnimation by lazy {
        RotateAnimation(0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = context.getInteger(config_longAnimTime).toLong()
            repeatCount = Animation.INFINITE
            repeatMode = Animation.RESTART
        }
    }

    private val redColor by lazy {
        context.getColor(R.color.red_a700)
    }

    private val greenColor by lazy {
        context.getColor(R.color.green_a400)
    }

    init {
        inflate(context, R.layout.app_toolbar, this)
        clickListeners()
        updateConnectionStatusIconView()
    }

    private fun clickListeners() {
        refresh.setOnClickListener {
            startLoadingAnimation()
            onClickRefreshButton.invoke()
        }
    }

    private fun startLoadingAnimation() {
        refresh.startAnimation(loadingAnimation)
    }

    private fun stopLoadingAnimation() {
        refresh.clearAnimation()
    }

    private fun updateConnectionStatusIconView() {
        connection_status.setCompoundDrawablesWithIntrinsicBounds(null, null, statusDrawable, null)
    }
}

enum class ConnectionStatus {
    CONNECTED, DISCONNECTED
}