package com.github.eliascoelho911.robok.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.ui.dialogs.LoadingDialog
import com.github.eliascoelho911.robok.ui.viewmodels.RootViewModel
import com.github.eliascoelho911.robok.ui.widgets.AlertBarView
import kotlinx.android.synthetic.main.root_activity.alert_bar
import kotlinx.android.synthetic.main.root_activity.coordinator_layout
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : AppCompatActivity(R.layout.root_activity) {
    private val viewModel: RootViewModel by viewModel()
    private val robotDisconnectedAlertBar by lazy { alert_bar }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRobotDisconnectedAlertBar()
    }

    private fun setupRobotDisconnectedAlertBar() {
        viewModel.isConnectedWithRobot.observe(this) { isConnected ->
            if (isConnected) {
                robotDisconnectedAlertBar.hide()
            } else {
                robotDisconnectedAlertBar.show(
                    messageRes = R.string.robot_disconnected,
                    action = AlertBarView.Action(R.string.connect) {
                        LoadingDialog(this, R.string.connecting).show()
                    })
            }
        }
    }
}