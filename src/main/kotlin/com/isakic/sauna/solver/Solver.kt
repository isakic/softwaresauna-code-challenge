package com.isakic.sauna.solver

/**
 * Represents a unique position in the given [row] and [col] in an unbound 2D grid.
 */
data class Position(
    val row: Int,
    val col: Int,
) {
    /**
     * Returns the position directly next to it in the given direction.
     */
    fun go(direction: Direction) = direction.apply(this)
}

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
}

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

typealias Path = List<Position>

val InvalidPath: Path = listOf(Position(-1, -1))
val NoPath: Path = listOf()

fun isValid(path: Path) = path != NoPath && path != InvalidPath

fun ensureSingleValidPath(vararg paths: Path): Path {
    val allPaths = paths.filter { it != NoPath }
    if (allPaths.isEmpty()) return NoPath
    if (allPaths.count() > 1) return InvalidPath
    return allPaths.first()
}

fun currentTileAlreadyVisited(path: List<Position>) = path.count { it == path.last() } > 1

fun processNextTileInDirection(map: Map, path: List<Position>, currentDirection: Direction): Path {
    val nextTile = map[path.last().go(currentDirection)]
    return when {
        nextTile == '-' -> processHorizontalTile(map, path.go(currentDirection), currentDirection)
        nextTile == '|' -> processVerticalTile(map, path.go(currentDirection), currentDirection)
        nextTile == '+' -> processCornerTile(map, path.go(currentDirection), currentDirection)
        nextTile == 'x' -> processEndTile(map, path.go(currentDirection), currentDirection)
        nextTile.isUpperCase() -> processLetterTile(map, path.go(currentDirection), currentDirection)
        nextTile == ' ' -> NoPath
        else -> InvalidPath
    }
}

fun start(map: Map): Path {
    val allPaths = Direction.values().map { processNextTileInDirection(map, listOf(map.startPosition), it) }.toTypedArray()
    return ensureSingleValidPath(*allPaths)
}

fun processHorizontalTile(map: Map, path: List<Position>, currentDirection: Direction): Path {
    return if (currentDirection.isHorizontal || (currentTileAlreadyVisited(path) && currentDirection.isVertical)) {
        processNextTileInDirection(map, path, currentDirection)
    } else InvalidPath
}

fun processVerticalTile(map: Map, path: List<Position>, currentDirection: Direction): Path {
    return if (currentDirection.isVertical || (currentTileAlreadyVisited(path) && currentDirection.isHorizontal)) {
        processNextTileInDirection(map, path, currentDirection)
    } else InvalidPath
}

fun processCornerTile(map: Map, path: List<Position>, currentDirection: Direction): Path {
    val continuationPath = processNextTileInDirection(map, path, currentDirection)
    return if (continuationPath != NoPath) {
        InvalidPath
    } else if (currentDirection.isHorizontal) {
        ensureSingleValidPath(
                processNextTileInDirection(map, path, Direction.Up),
                processNextTileInDirection(map, path, Direction.Down)
        )
    } else if (currentDirection.isVertical) {
        ensureSingleValidPath(
                processNextTileInDirection(map, path, Direction.Left),
                processNextTileInDirection(map, path, Direction.Right)
        )
    } else InvalidPath
}

fun processLetterTile(map: Map, path: List<Position>, currentDirection: Direction): Path {
    val hPath = processHorizontalTile(map, path, currentDirection)

    return if (isValid(hPath)) {
        hPath
    } else {
        val vPath = processVerticalTile(map, path, currentDirection)

        if (isValid(vPath)) {
            vPath
        } else {
            val cPath = processCornerTile(map, path, currentDirection)

            if (isValid(cPath)) cPath else InvalidPath
        }
    }
}

fun processEndTile(map: Map, path: List<Position>, currentDirection: Direction): Path {
    val continuationPaths = Direction.values().toList()
        .filter { it != currentDirection.opposite }
        .map { processNextTileInDirection(map, path, it) }

    return if (continuationPaths.all { it == NoPath }) path else InvalidPath
}

/**
 * Returns a string containing all tile symbols visited by the given path around the map.
 */
fun derivePath(map: Map, path: List<Position>) = path.map { map[it] }.joinToString("")

/**
 * Returns a string of letters in order of visit on a given path around the map. Letters visited more than once are
 * skipped after first visit.
 */
fun deriveLetters(map: Map, path: List<Position>) = path.distinct()
    .map { map[it] }
    .filter { it.isUpperCase() }
    .joinToString("")

class Map(input: String) {
    private val startTile = '@'
    private val rows: List<String>
    val startPosition: Position

    init {
        input.count { it == startTile }.also {
            if (it != 1) throw Error("Map must have exactly one starting tile!")
        }

        rows = input.lines().also {
            val row = it.indexOfFirst { row -> row.contains(startTile) }
            val col = it[row].indexOf(startTile)
            startPosition = Position(row, col)
        }
    }

    operator fun get(row: Int, col: Int) = rows.getOrNull(row)?.getOrNull(col) ?: ' '
    operator fun get(position: Position) = this[position.row, position.col]
}

data class Output(
    val letters: String,
    val path: String,
)

fun solve(input: String): Output? {
    return try {
        val map = Map(input)
        val path = start(Map(input))

        if (isValid(path)) {
            Output(deriveLetters(map, path), derivePath(map, path))
        } else null
    } catch (error: Error) {
        null
    }
}
