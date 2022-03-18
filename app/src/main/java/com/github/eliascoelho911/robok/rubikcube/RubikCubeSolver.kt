package com.github.eliascoelho911.robok.rubikcube

import cs.min2phase.Search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RubikCubeSolver {
    suspend fun solve(rubikCube: RubikCube) = findShorterSolutions(rubikCube)

    private suspend fun findShorterSolutions(rubikCube: RubikCube): String =
        withContext(Dispatchers.Default) {
            val model = rubikCube.createModelWith(Min2PhaseModelCreator)
            Search().solution(model, 21, 100000000, 10000, 0)
        }
}