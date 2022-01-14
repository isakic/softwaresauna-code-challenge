package com.isakic.sauna.solver

/**
 * This is a quick way of creating the necessary type using extensions.
 */
typealias Path = List<Position>

/**
 * Constant representing a path that doesn't reach the end tile properly.
 */
val InvalidPath: Path = listOf(Position(0, 0))

/**
 * Constant representing no path - meaning there are no steps.
 */
val NoPath: Path = listOf()

val Path.isValid
    get() = this != NoPath && this != InvalidPath

val Path.isSomePath
    get() = this != NoPath

val Path.isNoPath
    get() = this == NoPath

/**
 * Extends the path by appending the next tile on the map moving by moving one step from the last tile of the path
 * in given direction.
 */
fun Path.go(direction: Direction, distance: Int = 1): Path {
    if (distance < 0) throw IllegalArgumentException("Distance may not be a negative number!")
    return if (distance == 0) {
        this
    } else {
        (this + this.last().go(direction)).go(direction, distance - 1)
    }
}
