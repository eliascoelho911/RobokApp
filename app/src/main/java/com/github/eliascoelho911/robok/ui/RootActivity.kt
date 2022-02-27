package com.github.eliascoelho911.robok.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.github.eliascoelho911.robok.R
import kotlinx.android.synthetic.main.root_activity.app_toolbar

class RootActivity : AppCompatActivity(R.layout.root_activity), AndroidFragmentApplication.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = null
        setSupportActionBar(app_toolbar)
    }

    override fun exit() {}
}