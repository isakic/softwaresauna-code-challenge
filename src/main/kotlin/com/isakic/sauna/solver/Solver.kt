package com.isakic.sauna.solver

data class Pos(
    val row: Int,
    val col: Int
) {

    fun go(dir: Dir) = dir.apply(this)
}

enum class Dir(
    private val dRow: Int,
    private val dCol: Int,
    val isHorizontal: Boolean
) {

    Up(-1, 0, false),
    Left(0, -1, true),
    Right(0, 1, true),
    Down(1, 0, false);

    val isVertical get() = !isHorizontal

    val opposite
        get() = when (this) {
            Up -> Down
            Left -> Right
            Right -> Left
            Down -> Up
        }

    fun apply(from: Pos) = Pos(from.row + dRow, from.col + dCol)
}

fun from(pos: Pos): List<Pos> {
    return listOf(pos)
}

fun List<Pos>.go(dir: Dir, distance: Int = 1): List<Pos> {
    if (distance < 0) throw IllegalArgumentException("Distance may not be a negative number!")
    return if (distance == 0) {
        this
    } else {
        (this + this.last().go(dir)).go(dir, distance - 1)
    }
}

typealias Result = List<Pos>

val InvalidPath = listOf<Pos>(Pos(-1, -1))
val NoPath = listOf<Pos>()

fun ensureSingleValidPath(paths: List<Result>): Result  {
    val allPaths = paths.filter { it != NoPath }
    if (allPaths.isEmpty()) return NoPath
    if (allPaths.count() > 1) return InvalidPath
    return allPaths.first()
}

fun currentTileAlreadyVisited(path: List<Pos>) = path.count { it == path.last() } > 1

fun processNextTile(map: Map, path: List<Pos>, currentDir: Dir): Result {
    val nextTile = map[path.last().go(currentDir)]
    return when {
        nextTile == '-' -> processHorizontalTile(map, path.go(currentDir), currentDir)
        nextTile == '|' -> processVerticalTile(map, path.go(currentDir), currentDir)
        nextTile == '+' -> processCornerTile(map, path.go(currentDir), currentDir)
        nextTile == 'x' -> processEndTile(map, path.go(currentDir), currentDir)
        nextTile.isUpperCase() -> processLetterTile(map, path.go(currentDir), currentDir)
        else -> NoPath
    }
}

fun start(map: Map): Result {
    val allPaths = Dir.values().toList().map { processNextTile(map, listOf(map.startPos), it) }.filter { it != NoPath }
    if (allPaths.isEmpty()) return NoPath
    if (allPaths.count() > 1) return InvalidPath
   return allPaths.first()
}

fun processHorizontalTile(map: Map, path: List<Pos>, currentDir: Dir): Result {
    return if (currentDir.isHorizontal or (currentTileAlreadyVisited(path) and currentDir.isVertical)) {
        processNextTile(map, path, currentDir)
    } else InvalidPath
}

fun processVerticalTile(map: Map, path: List<Pos>, currentDir: Dir): Result {
    return if (currentDir.isVertical or (currentTileAlreadyVisited(path) and currentDir.isHorizontal)) {
        processNextTile(map, path, currentDir)
    } else InvalidPath
}

fun processCornerTile(map: Map, path: List<Pos>, currentDir: Dir): Result {
    val continuationPath = processNextTile(map, path, currentDir)
    return if (continuationPath != NoPath) {
        InvalidPath
    } else if (currentDir.isHorizontal) {
        ensureSingleValidPath(
            listOf(
                processNextTile(map, path, Dir.Up),
                processNextTile(map, path, Dir.Down)
            )
        )
    } else if (currentDir.isVertical) {
        ensureSingleValidPath(
            listOf(
                processNextTile(map, path, Dir.Left),
                processNextTile(map, path, Dir.Right)
            )
        )
    } else InvalidPath
}

fun processLetterTile(map: Map, path: List<Pos>, currentDir: Dir): Result {
    val hPath = processHorizontalTile(map, path, currentDir)

    return if (hPath != NoPath && hPath != InvalidPath) {
        hPath
    } else {
        val vPath = processVerticalTile(map, path, currentDir)

        if (vPath != NoPath && vPath != InvalidPath) {
            vPath
        } else {
            val cPath = processCornerTile(map, path, currentDir)

            if (cPath != NoPath && cPath != InvalidPath) {
                cPath
            } else {
                InvalidPath
            }
        }
    }
}

fun processEndTile(map: Map, path: List<Pos>, currentDir: Dir): Result {
    val continuationPaths = Dir.values().toList()
        .filter { it != currentDir.opposite }
        .map { processNextTile(map, path, it) }

    return if (continuationPaths.all { it == NoPath }) path else InvalidPath
}

fun derivePath(map: Map, path: List<Pos>) = path.map { map[it] }.joinToString("")

fun deriveLetters(map: Map, path: List<Pos>) = path.distinct()
    .map { map[it] }
    .filter { it.isUpperCase() }
    .joinToString("")

class Map(input: String) {
    private val rows: List<String>
    val startPos: Pos

    init {
        input.count { it == '@' }.also {
            if (it == 0) throw MissingStartTile()
            if (it > 1) throw AmbiguousStartTile()
        }

        rows = input.lines().also {
            val row = it.indexOfFirst { row -> row.contains('@') }
            val col = it[row].indexOf('@')
            startPos = Pos(row, col)
        }
    }

    operator fun get(row: Int, col: Int) = rows.getOrNull(row)?.getOrNull(col) ?: ' '
    operator fun get(pos: Pos) = this[pos.row, pos.col]
}

fun solve(input: String): Output? {
    val map = try {
        Map(input)
    } catch (error: Error) {
        return null
    }
    val path = start(Map(input))
    return if (path == NoPath || path == InvalidPath) null else Output(deriveLetters(map, path), derivePath(map, path))
}

data class Output(
    val letters: String,
    val path: String,
)
