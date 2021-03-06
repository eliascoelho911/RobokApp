package com.github.eliascoelho911.robok.rubikcube

import cs.min2phase.Search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RubikCubeSolver {
    private val modelCreator = Min2PhaseModelCreator()

    suspend fun solve(rubikCube: RubikCube) = findShorterSolutions(rubikCube)

    private suspend fun findShorterSolutions(rubikCube: RubikCube): Moves =
        withContext(Dispatchers.Default) {
            val model = rubikCube.createModelWith(modelCreator)
            Search().solution(model, 21, 100000000, 10000, 0)
                .replace("  ", " ").split(" ").filterNot { it.isBlank() }
        }
}

typealias Move = String
typealias Moves = List<Move>