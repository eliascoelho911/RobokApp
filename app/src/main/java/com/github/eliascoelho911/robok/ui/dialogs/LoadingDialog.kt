package com.github.eliascoelho911.robok.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.annotation.StringRes
import com.github.eliascoelho911.robok.R

class LoadingDialog(
    context: Context,
    @StringRes private val description: Int = R.string.loading,
) : Dialog(context) {
    init {
        setContentView(R.layout.loading_dialog)
    }

    override fun show() {
        super.show()
        findViewById<TextView>(R.id.txt_description).text = context.getText(description)
    }
}