package com.isakic.sauna.solver

typealias Tile = Char

class Map(input: String) {
    private val rows = input.lines()

    val height = rows.size
    val width = rows.maxOfOrNull { it.length } ?: 0

    operator fun get(row: Int, col: Int): Tile = rows.getOrNull(row)?.getOrNull(col) ?: ' '

    operator fun get(position: Position): Tile = this[position.row, position.col]
}