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

        Assertions.assertEquals(Pos(2, 4), map.startPos)
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
                Pos(0, 0),
                Pos(1, 0),
                Pos(2, 0),
                Pos(2, 1),
                Pos(2, 2),
                Pos(1, 2),
                Pos(1, 1),
                Pos(0, 1),
                Pos(0, 0))

        val actual = from(Pos(0, 0))
                .go(Dir.Right, 2)
                .go(Dir.Down, 2)
                .go(Dir.Left)
                .go(Dir.Up)
                .go(Dir.Left)
                .go(Dir.Up)

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

        val walk = from(Pos(3, 0))
                .go(Dir.Right, 6)
                .go(Dir.Down, 2)
                .go(Dir.Left, 2)
                .go(Dir.Up, 5)
                .go(Dir.Right, 6)
                .go(Dir.Down, 4)
                .go(Dir.Left, 2)
                .go(Dir.Up, 2)
                .go(Dir.Right, 4)
                .go(Dir.Down, 5)

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

        val walk = from(Pos(3, 0))
                .go(Dir.Right, 6)
                .go(Dir.Down, 2)
                .go(Dir.Left, 2)
                .go(Dir.Up, 5)
                .go(Dir.Right, 6)
                .go(Dir.Down, 4)
                .go(Dir.Left, 2)
                .go(Dir.Up, 2)
                .go(Dir.Right, 4)
                .go(Dir.Down, 5)

        val actual = deriveLetters(map, walk)

        Assertions.assertEquals("GOONIES", actual)
    }
}