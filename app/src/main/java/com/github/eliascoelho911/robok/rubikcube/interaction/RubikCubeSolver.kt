package com.github.eliascoelho911.robok.rubikcube.interaction

import com.github.eliascoelho911.robok.rubikcube.Min2PhaseModelParser
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.RubikCubeMoves
import cs.min2phase.Search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RubikCubeSolver {
    private val modelParser = Min2PhaseModelParser()

    suspend fun solve(rubikCube: RubikCube) = findShorterSolutions(rubikCube)

    private suspend fun findShorterSolutions(rubikCube: RubikCube): RubikCubeMoves =
        withContext(Dispatchers.Default) {
            val model = modelParser.parse(rubikCube)
            val solution = Search().solution(model, 21, 100000000, 10000, 0)
            solution.replace("  ", " ")
                .split(" ")
                .filterNot { it.isBlank() }
                .map {
                    if (it.contains("2")) it.replace("2", "").let { listOf(it, it) } else listOf(
                        it
                    )
                }
                .flatten()
                .map { RubikCube.Movement.fromNotation(it)!! }
        }
}