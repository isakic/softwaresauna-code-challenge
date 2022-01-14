package com.isakic.sauna.solver

fun ensureSinglePathExists(paths: List<Path>) = ensureSinglePathExists(*paths.toTypedArray())
fun ensureSinglePathExists(vararg paths: Path): Path {
    val allPaths = paths.filter { it.isSomePath }
    if (allPaths.isEmpty()) return NoPath
    if (allPaths.count() > 1) return InvalidPath
    return allPaths.first()
}

fun currentTileAlreadyVisited(path: Path) = path.count { it == path.last() } > 1

fun parsePathInDirection(map: Map, path: Path, currentDirection: Direction): Path {
    val newPath = path.go(currentDirection)
    val nextTile = map[newPath.last()]

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

fun start(map: Map): Path {
    val startingPath: Path = listOf(map.startPosition)
    val allPaths = Direction.all.map { parsePathInDirection(map, startingPath, it) }
    return ensureSinglePathExists(allPaths)
}

fun processHorizontalTile(map: Map, path: Path, currentDirection: Direction): Path {
    return if (currentDirection.isHorizontal || currentDirection.isVertical && currentTileAlreadyVisited(path)) {
        parsePathInDirection(map, path, currentDirection)
    } else InvalidPath
}

fun processVerticalTile(map: Map, path: Path, currentDirection: Direction): Path {
    return if (currentDirection.isVertical || currentDirection.isHorizontal && currentTileAlreadyVisited(path)) {
        parsePathInDirection(map, path, currentDirection)
    } else InvalidPath
}

fun processCornerTile(map: Map, path: Path, currentDirection: Direction): Path {
    val pathThroughCornerTile = parsePathInDirection(map, path, currentDirection)
    return if (pathThroughCornerTile.isSomePath) {
        InvalidPath
    } else if (currentDirection.isHorizontal) {
        ensureSinglePathExists(
            parsePathInDirection(map, path, Direction.Up),
            parsePathInDirection(map, path, Direction.Down)
        )
    } else if (currentDirection.isVertical) {
        ensureSinglePathExists(
            parsePathInDirection(map, path, Direction.Left),
            parsePathInDirection(map, path, Direction.Right)
        )
    } else InvalidPath
}

/**
 * Letter tiles can behave as horizontal, vertical or corner tiles.
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

fun processEndTile(map: Map, path: Path, currentDirection: Direction): Path {
    val continuationPaths = Direction.all
        .filter { it != currentDirection.opposite }
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

fun solve(input: String): Output? {
    return try {
        val map = Map(input)
        val path = start(Map(input))

        if (path.isValid) {
            Output(extractLetters(map, path), extractTiles(map, path))
        } else null
    } catch (error: Error) {
        null
    }
}
