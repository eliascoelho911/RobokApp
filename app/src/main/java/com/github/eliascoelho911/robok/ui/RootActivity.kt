package com.github.eliascoelho911.robok.ui

import android.graphics.Color.BLUE
import android.graphics.Color.GREEN
import android.graphics.Color.MAGENTA
import android.graphics.Color.RED
import android.graphics.Color.WHITE
import android.graphics.Color.YELLOW
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.rubikcube.face.Position.BACK
import com.github.eliascoelho911.robok.rubikcube.face.Position.DOWN
import com.github.eliascoelho911.robok.rubikcube.face.Position.FRONT
import com.github.eliascoelho911.robok.rubikcube.face.Position.LEFT
import com.github.eliascoelho911.robok.rubikcube.face.Position.RIGHT
import com.github.eliascoelho911.robok.rubikcube.face.Position.UP

class RootActivity : AppCompatActivity(R.layout.root_activity) {
    private val rubikCube by lazy {
        RubikCube(listOf(
            Face(FRONT, GREEN, YELLOW, WHITE, WHITE, BLUE, YELLOW, MAGENTA, GREEN, GREEN),
            Face(RIGHT, GREEN, YELLOW, BLUE, RED, RED, WHITE, YELLOW, GREEN, MAGENTA),
            Face(BACK, YELLOW, BLUE, WHITE, MAGENTA, GREEN, BLUE, WHITE, GREEN, BLUE),
            Face(LEFT, RED, GREEN, RED, WHITE, MAGENTA, RED, YELLOW, BLUE, WHITE),
            Face(UP, BLUE, RED, MAGENTA, YELLOW, YELLOW, MAGENTA, YELLOW, BLUE, RED),
            Face(DOWN, BLUE, MAGENTA, MAGENTA, MAGENTA, WHITE, WHITE, RED, RED, GREEN)
        ))
    }

    private val debugMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (debugMode) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(
                resId = R.id.rubikCubeSolveFragment,
                args = Bundle().putRubikCube())
        }
    }

    private fun Bundle.putRubikCube(): Bundle = apply { putParcelable("rubikCube", rubikCube) }
}
