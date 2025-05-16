package com.github.eliascoelho911.robok.rubikcube

import cs.min2phase.Search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RubikCubeSolver {
    private val modelParser = Min2PhaseModelParser()

    suspend fun solve(rubikCube: RubikCube) = findShorterSolutions(rubikCube)

    private suspend fun findShorterSolutions(rubikCube: RubikCube): RubikCubeMoves =
        withContext(Dispatchers.Default) {
            val model = modelParser.parse(rubikCube)
            Search().solution(model, 21, 100000000, 10000, 0)
                .replace("  ", " ")
                .split(" ")
                .filterNot { it.isBlank() }
                .map { RubikCube.Movement.fromNotation(it)!! }
        }
}