package com.github.eliascoelho911.robok.ui.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.catalinjurjiu.animcubeandroid.AnimCube
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.rubikcube.AnimCubeModelParser
import com.github.eliascoelho911.robok.rubikcube.BackFace
import com.github.eliascoelho911.robok.rubikcube.Cell
import com.github.eliascoelho911.robok.rubikcube.DownFace
import com.github.eliascoelho911.robok.rubikcube.FrontFace
import com.github.eliascoelho911.robok.rubikcube.LeftFace
import com.github.eliascoelho911.robok.rubikcube.RightFace
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import com.github.eliascoelho911.robok.rubikcube.UpFace

/**
 * Compose wrapper for the AnimCube Android library view.
 *
 * @param modifier The modifier to be applied to the AnimCube view
 * @param cubeModel The string representation of cube state (generated from RubikCubeModelParser)
 * @param cubeColors The array of colors for the cube faces
 * @param moveSequence The sequence of moves to animate
 * @param onAnimationFinished Callback that will be invoked when animation finishes
 * @param state Optional state object to control animations externally
 */
@Composable
fun AnimCube(
    modifier: Modifier = Modifier,
    cubeModel: String? = null,
    cubeColors: IntArray? = null,
    moveSequence: String? = null,
    onAnimationFinished: (() -> Unit)? = null,
    state: AnimCubeState? = null
) {
    val context = LocalContext.current

    // Track if we're animating
    var isAnimating by remember { mutableStateOf(false) }
    // Reference to the underlying AnimCube Android view
    var animCubeView by remember { mutableStateOf<AnimCube?>(null) }

    AndroidView(
        modifier = modifier,
        factory = {
            AnimCube(context).apply {
                // Store reference to the view
                animCubeView = this

                // Apply initial configuration if provided
                cubeModel?.let { setCubeModel(it) }
                cubeColors?.let { setCubeColors(it) }
                moveSequence?.let { setMoveSequence(it) }
            }
        },
        update = { animCube ->
            // Update properties if they change
            cubeModel?.let { animCube.setCubeModel(it) }
            cubeColors?.let { animCube.setCubeColors(it) }
            moveSequence?.let { animCube.setMoveSequence(it) }

            animCube.setOnAnimationFinishedListener {
                isAnimating = false
                state?.isAnimating = false
                onAnimationFinished?.invoke()
            }
        }
    )

    // Connect state object to animations
    LaunchedEffect(state?.animationRequest) {
        if (state?.animationRequest != AnimationRequest.NONE) {
            when (state?.animationRequest) {
                AnimationRequest.ANIMATE_FORWARD -> {
                    if (!isAnimating && !animCubeView?.isAnimating!!) {
                        isAnimating = true
                        state.isAnimating = true
                        animCubeView?.animateMove()
                    }
                }

                AnimationRequest.ANIMATE_BACKWARD -> {
                    if (!isAnimating && !animCubeView?.isAnimating!!) {
                        isAnimating = true
                        state.isAnimating = true
                        animCubeView?.animateMoveReversed()
                    }
                }

                else -> { /* No animation requested */
                }
            }
            // Reset the request after processing
            state?.animationRequest = AnimationRequest.NONE
        }
    }

    // Clean up resources when component is disposed
    DisposableEffect(Unit) {
        onDispose {
            // Any necessary cleanup
        }
    }
}

/**
 * Enum class representing types of animation requests
 */
enum class AnimationRequest {
    NONE,
    ANIMATE_FORWARD,
    ANIMATE_BACKWARD
}

/**
 * State object to control animations of the AnimCube component
 */
class AnimCubeState {
    var isAnimating by mutableStateOf(false)
        internal set

    var animationRequest by mutableStateOf(AnimationRequest.NONE)
        internal set

    /**
     * Request to animate the next move
     */
    fun animateMove() {
        if (!isAnimating) {
            animationRequest = AnimationRequest.ANIMATE_FORWARD
        }
    }

    /**
     * Request to animate the previous move in reverse
     */
    fun animateMoveReversed() {
        if (!isAnimating) {
            animationRequest = AnimationRequest.ANIMATE_BACKWARD
        }
    }
}

/**
 * Create a state object that can be used to control the AnimCube animations
 */
@Composable
fun rememberAnimCubeState(): AnimCubeState {
    return remember { AnimCubeState() }
}

@Preview
@Composable
fun PreviewAnimCube() {
    val rubikCube = remember { createRubikCubeStub() }
    var moveSequence: String by remember { mutableStateOf("") }
    val animCubeState = rememberAnimCubeState()

    LaunchedEffect(rubikCube) {
        moveSequence = RubikCubeSolver().solve(rubikCube).joinToString(separator = " ")
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        AnimCube(
            modifier = Modifier.size(250.dp),
            cubeModel = AnimCubeModelParser().parse(rubikCube),
            cubeColors = AnimCubeModelParser().getDistinctColorsInOrder(rubikCube).toIntArray(),
            moveSequence = moveSequence,
            state = animCubeState,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Cube Animation Controls",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            animCubeState.animateMoveReversed()
                        },
                        enabled = !animCubeState.isAnimating
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_previous_24),
                            contentDescription = "Previous move"
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            animCubeState.animateMove()
                        },
                        enabled = !animCubeState.isAnimating
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_next_24),
                            contentDescription = "Next move"
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = if (animCubeState.isAnimating) "Animating..." else "Ready",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewAnimCubeSimple() {
    val rubikCube = remember { createRubikCubeStub() }

    AnimCube(
        modifier = Modifier.size(250.dp),
        cubeModel = AnimCubeModelParser().parse(rubikCube),
        cubeColors = AnimCubeModelParser().getDistinctColorsInOrder(rubikCube).toIntArray()
    )
}

private fun createRubikCubeStub() = RubikCube(
    upFace = UpFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -2573020),
            Cell(x = 1, y = 0, color = -2573020),
            Cell(x = 2, y = 0, color = -5063217),
            Cell(x = 0, y = 1, color = -2573020),
            Cell(x = 1, y = 1, color = -5827545),
            Cell(x = 2, y = 1, color = -5063217),
            Cell(x = 0, y = 2, color = -5827545),
            Cell(x = 1, y = 2, color = -5063217),
            Cell(x = 2, y = 2, color = -5063217)
        )
    ),
    frontFace = FrontFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -2573020),
            Cell(x = 1, y = 0, color = -5827545),
            Cell(x = 2, y = 0, color = -369869),
            Cell(x = 0, y = 1, color = -16554827),
            Cell(x = 1, y = 1, color = -5063217),
            Cell(x = 2, y = 1, color = -369869),
            Cell(x = 0, y = 2, color = -2573020),
            Cell(x = 1, y = 2, color = -5827545),
            Cell(x = 2, y = 2, color = -5063217)
        )
    ),
    rightFace = RightFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -10763197),
            Cell(x = 1, y = 0, color = -10763197),
            Cell(x = 2, y = 0, color = -10763197),
            Cell(x = 0, y = 1, color = -10763197),
            Cell(x = 1, y = 1, color = -10763197),
            Cell(x = 2, y = 1, color = -10763197),
            Cell(x = 0, y = 2, color = -16554827),
            Cell(x = 1, y = 2, color = -369869),
            Cell(x = 2, y = 2, color = -16554827)
        )
    ),
    backFace = BackFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -5827545),
            Cell(x = 1, y = 0, color = -10763197),
            Cell(x = 2, y = 0, color = -369869),
            Cell(x = 0, y = 1, color = -5827545),
            Cell(x = 1, y = 1, color = -2573020),
            Cell(x = 2, y = 1, color = -369869),
            Cell(x = 0, y = 2, color = -2573020),
            Cell(x = 1, y = 2, color = -2573020),
            Cell(x = 2, y = 2, color = -369869)
        )
    ),
    leftFace = LeftFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -16554827),
            Cell(x = 1, y = 0, color = -16554827),
            Cell(x = 2, y = 0, color = -10763197),
            Cell(x = 0, y = 1, color = -16554827),
            Cell(x = 1, y = 1, color = -16554827),
            Cell(x = 2, y = 1, color = -5827545),
            Cell(x = 0, y = 2, color = -16554827),
            Cell(x = 1, y = 2, color = -16554827),
            Cell(x = 2, y = 2, color = -10763197)
        )
    ),
    downFace = DownFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -369869),
            Cell(x = 1, y = 0, color = -2573020),
            Cell(x = 2, y = 0, color = -5827545),
            Cell(x = 0, y = 1, color = -5063217),
            Cell(x = 1, y = 1, color = -369869),
            Cell(x = 2, y = 1, color = -5063217),
            Cell(x = 0, y = 2, color = -5063217),
            Cell(x = 1, y = 2, color = -369869),
            Cell(x = 2, y = 2, color = -5827545)
        )
    )
)