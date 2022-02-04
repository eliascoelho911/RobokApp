package com.github.eliascoelho911.robok.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.eliascoelho911.robok.R
import kotlinx.android.synthetic.main.home_activity.*

class RootActivity : AppCompatActivity(R.layout.home_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = null
        setSupportActionBar(app_toolbar)
    }
}