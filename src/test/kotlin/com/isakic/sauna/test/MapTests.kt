package com.isakic.sauna.test

import com.isakic.sauna.solver.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MapTests {

    @Test
    fun `Map finds the starting position`() {
        val input = """
                       +---A---+
                       |       |
                       +---@   B
                               |
                           x---C""".trimIndent()
        val map = Map(input)

        Assertions.assertEquals(Position(2, 4), map.startPosition)
    }


    @Test
    fun `Map treats out-of-bounds tiles as empty tiles`() {
        val input = """
                       @---A---+
                               |
                       x-B-+   C
                           |   |
                           +---+""".trimIndent()
        val map = Map(input)

        val samples = listOf(map[-1, 0], map[0, -1], map[-1, -1], map[100, 0], map[0, 100], map[100, 100])
        val expected = listOf(' ', ' ', ' ', ' ', ' ', ' ')

        Assertions.assertIterableEquals(expected, samples)
    }

    @Test
    fun `Walk construction extension`() {
        val expected = listOf(
                Position(0, 0),
                Position(1, 0),
                Position(2, 0),
                Position(2, 1),
                Position(2, 2),
                Position(1, 2),
                Position(1, 1),
                Position(0, 1),
                Position(0, 0))

        val actual = listOf(Position(0, 0))
                .go(Direction.Right, 2)
                .go(Direction.Down, 2)
                .go(Direction.Left)
                .go(Direction.Up)
                .go(Direction.Left)
                .go(Direction.Up)

        Assertions.assertIterableEquals(expected, actual)
    }

    @Test
    fun `Path is properly extracted from a walk`() {
        val input = """
                           +-O-N-+
                           |     |
                           |   +-I-+
                       @-G-O-+ | | |
                           | | +-+ E
                           +-+     S
                                   |
                                   x
                    """.trimIndent()
        val map = Map(input)

        val walk = listOf(Position(3, 0))
                .go(Direction.Right, 6)
                .go(Direction.Down, 2)
                .go(Direction.Left, 2)
                .go(Direction.Up, 5)
                .go(Direction.Right, 6)
                .go(Direction.Down, 4)
                .go(Direction.Left, 2)
                .go(Direction.Up, 2)
                .go(Direction.Right, 4)
                .go(Direction.Down, 5)

        val actual = derivePath(map, walk)

        Assertions.assertEquals("@-G-O-+|+-+|O||+-O-N-+|I|+-+|+-I-+|ES|x", actual)
    }

    @Test
    fun `Letters are properly extracted from a walk`() {
        val input = """
                           +-O-N-+
                           |     |
                           |   +-I-+
                       @-G-O-+ | | |
                           | | +-+ E
                           +-+     S
                                   |
                                   x
                    """.trimIndent()
        val map = Map(input)

        val walk = listOf(Position(3, 0))
                .go(Direction.Right, 6)
                .go(Direction.Down, 2)
                .go(Direction.Left, 2)
                .go(Direction.Up, 5)
                .go(Direction.Right, 6)
                .go(Direction.Down, 4)
                .go(Direction.Left, 2)
                .go(Direction.Up, 2)
                .go(Direction.Right, 4)
                .go(Direction.Down, 5)

        val actual = deriveLetters(map, walk)

        Assertions.assertEquals("GOONIES", actual)
    }
}