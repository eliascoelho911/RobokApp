package com.github.eliascoelho911.robok.ui.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class SidePreviewFragment : AndroidFragmentApplication() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return initializeForView(SideApplicationListener(), AndroidApplicationConfiguration())
    }
}

class SideApplicationListener : ApplicationAdapter() {
    var batch: SpriteBatch? = null
    private var font: BitmapFont? = null


    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
        font!!.color = Color.BLUE
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch!!.begin()

        //batch.draw(img, 0, 0);
        font!!.data.setScale(6.0f)
        font!!.draw(batch, "Hello World from libgdx running in a fragment! :)", 100f, 300f)
        batch!!.end()
    }

    override fun dispose() {
        batch!!.dispose()
    }
}