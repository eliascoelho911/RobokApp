package com.github.eliascoelho911.robok.rubikcube

internal fun createRubikCubeStub() = RubikCube(
    upFace = UpFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -1320180),
            Cell(x = 1, y = 0, color = -3796710),
            Cell(x = 2, y = 0, color = -33758),
            Cell(x = 0, y = 1, color = -16747558),
            Cell(x = 1, y = 1, color = -4666656),
            Cell(x = 2, y = 1, color = -33758),
            Cell(x = 0, y = 2, color = -1320180),
            Cell(x = 1, y = 2, color = -3796710),
            Cell(x = 2, y = 2, color = -4666656)
        )
    ),
    frontFace = FrontFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -33758),
            Cell(x = 1, y = 0, color = -1320180),
            Cell(x = 2, y = 0, color = -3796710),
            Cell(x = 0, y = 1, color = -4666656),
            Cell(x = 1, y = 1, color = -33758),
            Cell(x = 2, y = 1, color = -4666656),
            Cell(x = 0, y = 2, color = -4666656),
            Cell(x = 1, y = 2, color = -33758),
            Cell(x = 2, y = 2, color = -3796710)
        )
    ),
    rightFace = RightFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -16747558),
            Cell(x = 1, y = 0, color = -9374135),
            Cell(x = 2, y = 0, color = -9374135),
            Cell(x = 0, y = 1, color = -33758),
            Cell(x = 1, y = 1, color = -9374135),
            Cell(x = 2, y = 1, color = -9374135),
            Cell(x = 0, y = 2, color = -16747558),
            Cell(x = 1, y = 2, color = -9374135),
            Cell(x = 2, y = 2, color = -9374135)
        )
    ),
    backFace = BackFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -4666656),
            Cell(x = 1, y = 0, color = -4666656),
            Cell(x = 2, y = 0, color = -3796710),
            Cell(x = 0, y = 1, color = -4666656),
            Cell(x = 1, y = 1, color = -3796710),
            Cell(x = 2, y = 1, color = -1320180),
            Cell(x = 0, y = 2, color = -4666656),
            Cell(x = 1, y = 2, color = -1320180),
            Cell(x = 2, y = 2, color = -1320180)
        )
    ),
    leftFace = LeftFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -9374135),
            Cell(x = 1, y = 0, color = -3796710),
            Cell(x = 2, y = 0, color = -9374135),
            Cell(x = 0, y = 1, color = -16747558),
            Cell(x = 1, y = 1, color = -16747558),
            Cell(x = 2, y = 1, color = -16747558),
            Cell(x = 0, y = 2, color = -16747558),
            Cell(x = 1, y = 2, color = -16747558),
            Cell(x = 2, y = 2, color = -16747558)
        )
    ),
    downFace = DownFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -33758),
            Cell(x = 1, y = 0, color = -1320180),
            Cell(x = 2, y = 0, color = -1320180),
            Cell(x = 0, y = 1, color = -33758),
            Cell(x = 1, y = 1, color = -1320180),
            Cell(x = 2, y = 1, color = -3796710),
            Cell(x = 0, y = 2, color = -33758),
            Cell(x = 1, y = 2, color = -9374135),
            Cell(x = 2, y = 2, color = -3796710)
        )
    )
)