package com.isakic.sauna.solver

/**
 * Represents a unique position in the given [row] and [col] in an unbound 2D grid.
 */
data class Position(
    val row: Int,
    val col: Int,
) {
    /**
     * Returns the position directly next to it in the given [direction].
     */
    fun go(direction: Direction) = direction.apply(this)
}
