package com.isakic.sauna.solver

/**
 * Detects dead ends and path forks by inspecting [paths] discovered proceeding from a map tile.
 *
 * Returns a [Path] if there is a single continuation path, either valid or invalid, proceeding from this tile.
 *
 * Returns [InvalidPath] if a fork is detected at the current position (i.e. more than one continuing path),
 * regardless of their validity.
 *
 * Returns [NoPath] if the current tile is a dead end.
 */
fun ensureSinglePathExists(vararg paths: Path): Path {
    val allPaths = paths.filter { it.isSomePath }
    if (allPaths.isEmpty()) return NoPath
    if (allPaths.count() > 1) return InvalidPath
    return allPaths.first()
}

/**
 * Detects dead ends and path forks by inspecting [paths] discovered proceeding from a map tile.
 *
 * Returns a [Path] if there is a single continuation path, either valid or invalid, proceeding from this tile.
 *
 * Returns [InvalidPath] if a fork is detected at the current position (i.e. more than one continuing path),
 * regardless of their validity.
 *
 * Returns [NoPath] if the current tile is a dead end.
 */
fun ensureSinglePathExists(paths: List<Path>) = ensureSinglePathExists(*paths.toTypedArray())

/**
 * Performs a lookahead from the tail of the current [path] in the given [currentDirection] on the [map] and calls
 * the appropriate tile handler. This behavior is the same for all valid tiles.
 *
 * All illegal characters result in this function returning [InvalidPath].
 */
fun parsePathInDirection(map: Map, path: Path, currentDirection: Direction): Path {
    val newPath = path.go(currentDirection)
    val nextTile = map[newPath.currentPosition]

    return when {
        nextTile.isUpperCase() -> processLetterTile(map, newPath, currentDirection)
        nextTile == '-' -> processHorizontalTile(map, newPath, currentDirection)
        nextTile == '|' -> processVerticalTile(map, newPath, currentDirection)
        nextTile == '+' -> processCornerTile(map, newPath, currentDirection)
        nextTile == 'x' -> processEndTile(map, newPath, currentDirection)
        nextTile == ' ' -> NoPath
        else -> InvalidPath
    }
}

fun findTile(map: Map, symbol: Char): Position? {
    var position: Position? = null
    for (row in 0 until map.height) {
        for (col in 0 until map.width ) {
            if (map[row, col] == symbol) {
                if (position != null) {
                    return null
                } else {
                    position = Position(row, col)
                }
            }
        }
    }
    return position
}

/**
 * Returns the [Position] of the start tile, represented by '@' symbol, if there is exactly one such tile on the map,
 * `null` otherwise.
 */
fun findStartPosition(map: Map) = findTile(map, '@')

/**
 * Starts the parsing process by scanning for paths in all directions starting at the detected starting tile represented
 * by '@' symbol. It allows only one single valid path be present.
 *
 * Returns a valid [Path] to the end tile, if such exists, [InvalidPath] otherwise.
 */
fun start(map: Map): Path {
    val startPosition = findStartPosition(map) ?: return InvalidPath

    val startingPath: Path = listOf(startPosition)
    val allPaths = Direction.all.map { parsePathInDirection(map, startingPath, it) }
    return ensureSinglePathExists(allPaths)
}

/**
 * Processes a horizontal tile, represented by '-' symbol. Returns a valid [Path] if all conditions are met.
 *
 * This tile can also be interpreted as a horizontal tile if the [currentDirection] is vertical, representing a path
 * intersection at a previously visited path. The algorithm assumes that a path revisiting a given tile always goes
 * *underneath* the previous path.
 *
 * Returns [InvalidPath] if there is no valid path going through this tile in the [currentDirection].
 */
fun processHorizontalTile(map: Map, path: Path, currentDirection: Direction): Path {
    return if (currentDirection.isHorizontal || path.isRevisitingCurrentPosition) {
        parsePathInDirection(map, path, currentDirection)
    } else InvalidPath
}

/**
 * Processes a vertical tile, represented by '|' symbol. Returns a valid [Path] if all conditions are met.
 *
 * This tile can also be interpreted as a horizontal tile if the [currentDirection] is horizontal, representing a path
 * intersection at a previously visited path. The algorithm assumes that a path revisiting a given tile always goes
 * *underneath* the previous path.
 *
 * Returns [InvalidPath] if there is no valid path going through this tile in the [currentDirection].
 */
fun processVerticalTile(map: Map, path: Path, currentDirection: Direction): Path {
    return if (currentDirection.isVertical || path.isRevisitingCurrentPosition) {
        parsePathInDirection(map, path, currentDirection)
    } else InvalidPath
}

/**
 * Processes a corner tile, represented by '+' symbol. Returns a valid [Path] if all conditions are met.
 *
 * Returns [InvalidPath] if there is no single valid path taking a turn at this tile exists. A path going through this
 * tile in the same direction is not valid and will also return [InvalidPath].
 */
fun processCornerTile(map: Map, path: Path, currentDirection: Direction): Path {
    val forbiddenPath = parsePathInDirection(map, path, currentDirection)
    if (forbiddenPath.isSomePath) return InvalidPath

    val allPaths = currentDirection
        .orthogonalDirections
        .map { parsePathInDirection(map, path, it) }
    return ensureSinglePathExists(allPaths)
}

/**
 * Letter tiles can behave as horizontal, vertical or corner tiles, also allowing for path intersection.
 *
 * Problem specification is missing some edge cases which appear to contravene the apparent idea behind the problem
 * statement. These cases are described in extra acceptance test suite but set to be ignored.
 */
fun processLetterTile(map: Map, path: Path, currentDirection: Direction): Path {
    val hPath = processHorizontalTile(map, path, currentDirection)
    if (hPath.isValid) return hPath

    val vPath = processVerticalTile(map, path, currentDirection)
    if (vPath.isValid) return vPath

    val cPath = processCornerTile(map, path, currentDirection)
    if (cPath.isValid) return cPath

    return InvalidPath
}

/**
 * This function handles an encountered end tile. Returns the [Path] ending at this tile if the path doesn't continue
 * beyond this tile in any direction apart from the incoming direction, [InvalidPath] otherwise.
 *
 * This is the only tile handler that can return a valid path, ensuring that the solver will always return a valid path
 * one is present.
 */
fun processEndTile(map: Map, path: Path, currentDirection: Direction): Path {
    val continuationPaths = Direction.all
        .filter { it != currentDirection.oppositeDirection }
        .map { parsePathInDirection(map, path, it) }

    return if (continuationPaths.none { it.isSomePath }) path else InvalidPath
}

/**
 * Returns a string containing all tile symbols visited by the given [path] around the [map].
 */
fun extractTiles(map: Map, path: Path) = path.map { map[it] }.joinToString("")

/**
 * Returns a string of letters in order of visit on a given [path] around the [map]. Letters visited more than once are
 * skipped after first visit.
 */
fun extractLetters(map: Map, path: Path) = path.distinct()
    .map { map[it] }
    .filter { it.isUpperCase() }
    .joinToString("")

/**
 * Parses the map represented by the [input] string and returns a sequence of visited letters as well as visited tiles
 * on a valid path from the start tile to the end tile as [Output]. Returns null if no valid path exists.
 */
fun solve(input: String) = try {
    val map = Map(input)
    val path = start(map)

    if (path.isValid) {
        Output(extractLetters(map, path), extractTiles(map, path))
    } else null
} catch (error: Error) {
    null
}
