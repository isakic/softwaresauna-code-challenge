package com.isakic.sauna.solver

/**
 * Enumeration of all orthogonal vectors of movement in a 2D grid.
 * @param dRow component of the vector of displacement along y-axis.
 * @param dCol component of the vector of displacement along x-axis.
 * @param isHorizontal `true` if the vector is parallel with x-axis.
 */
enum class Direction(
    private val dRow: Int,
    private val dCol: Int,
    val isHorizontal: Boolean,
) {

    Up(-1, 0, false),
    Left(0, -1, true),
    Right(0, 1, true),
    Down(1, 0, false);

    /**
     * `true` if the vector is parallel with y-axis.
     */
    val isVertical get() = !isHorizontal

    /**
     * Gets the direction along the same axis with the opposite value
     */
    val opposite
        get() = when (this) {
            Up -> Down
            Left -> Right
            Right -> Left
            Down -> Up
        }

    /**
     * Returns the position directly next to the given position in this direction.
     */
    fun apply(from: Position) = Position(from.row + dRow, from.col + dCol)

    companion object {
        val all = Direction.values().toList()
    }
}