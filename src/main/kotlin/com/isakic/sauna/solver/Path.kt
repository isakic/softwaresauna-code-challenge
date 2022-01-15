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
 * Constant representing a path with no steps. This constant is returned when an empty tile,
 * represented by a ' ' symbol, is encountered.
 */
val NoPath: Path = listOf()

/**
 *  `true`, if a path is neither [NoPath] nor [InvalidPath].
 *  The condition that a path must reach the end tile for it to be valid is enforced implicitly by the algorithm
 *  (see [processEndTile]).
 */
val Path.isValid
    get() = this != NoPath && this != InvalidPath

/**
 * `true` if the path isn't [NoPath].
 */
val Path.isSomePath
    get() = this != NoPath

/**
 * Gives the [Position] at the tail end of the path, representing the current location during the parsing process.
 * The algorithm ensures the current path is never empty and thus never throws. Do not use on [NoPath] constant.
 */
val Path.currentPosition
    get() = last()

/**
 * `true` if the [currentPosition] was previously visited by the path.
 */
val Path.isRevisitingCurrentPosition get() = count { it == currentPosition } > 1

/**
 * Extends the path by appending the next position on the map moving by moving one step from the last position
 * in given direction.
 */
fun Path.go(direction: Direction, distance: Int = 1): Path {
    if (distance < 0) throw IllegalArgumentException("Distance may not be a negative number!")
    return if (distance == 0) {
        this
    } else {
        (this + currentPosition.go(direction)).go(direction, distance - 1)
    }
}
