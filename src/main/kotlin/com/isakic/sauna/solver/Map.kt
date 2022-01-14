package com.isakic.sauna.solver

class Map(input: String) {
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

    companion object {
        const val startTile = '@'
    }
}