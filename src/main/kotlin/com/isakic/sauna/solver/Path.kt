package com.isakic.sauna.solver

/**
 * Represents the path traversed by the solver from the starting tile to the current one.
 * A tile may appear more than once in a given path, but it can logically happen no more than twice.
 *
 * This is a quick way of creating the necessary type using extensions without having to define a separate class type.
 */
typealias Path = List<Position>

/**
 * Constant representing a path that doesn't reach the end tile properly.
 *
 * Invalid path can be safely represented by a list containing only one position, as a valid path must contain at least
 * a start and an end tile.
 */
val InvalidPath: Path = listOf(Position(0, 0))

/**
 * Constant representing a path with no steps.
 */
val NoPath: Path = listOf()

val Path.isValid
    get() = this != NoPath && this != InvalidPath

val Path.isSomePath
    get() = this != NoPath

val Path.isNoPath
    get() = this == NoPath

val Path.currentPosition
    get() = this.last()

val Path.isRevisitingCurrentPosition get() = count { it == currentPosition } > 1

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
