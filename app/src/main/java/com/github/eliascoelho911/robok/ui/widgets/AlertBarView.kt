package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.github.eliascoelho911.robok.R
import kotlinx.android.synthetic.main.alert_bar.view.alert_bar_root
import kotlinx.android.synthetic.main.alert_bar.view.button
import kotlinx.android.synthetic.main.alert_bar.view.message

class AlertBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val messageTextView by lazy { message }
    private val buttonView by lazy { button }
    private val rootView by lazy { alert_bar_root }

    init {
        inflate(context, R.layout.alert_bar, this)
    }

    fun show(@StringRes messageRes: Int, action: Action? = null) {
        rootView.isVisible = true
        action?.let {
            buttonView.apply {
                text = context.getText(action.textRes)
                setOnClickListener { action.onClickListener() }
                isVisible = true
            }
        } ?: run { buttonView.isVisible = false }
        messageTextView.text = context.getText(messageRes)
    }

    fun hide() {
        rootView.isVisible = false
    }

    data class Action(@StringRes val textRes: Int, val onClickListener: () -> Unit)
}