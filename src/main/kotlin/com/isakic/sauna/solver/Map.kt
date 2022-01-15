package com.isakic.sauna.solver

typealias Tile = Char
const val StartTile: Tile = '@'

class Map(input: String) {
    private val rows: List<String>
    val startPosition: Position

    init {
        input.count { it == StartTile }.also {
            if (it != 1) throw Error("Map must have exactly one starting tile!")
        }

        rows = input.lines().also {
            val row = it.indexOfFirst { row -> row.contains(StartTile) }
            val col = it[row].indexOf(StartTile)
            startPosition = Position(row, col)
        }
    }

    operator fun get(row: Int, col: Int): Tile = rows.getOrNull(row)?.getOrNull(col) ?: ' '

    operator fun get(position: Position): Tile = this[position.row, position.col]
}