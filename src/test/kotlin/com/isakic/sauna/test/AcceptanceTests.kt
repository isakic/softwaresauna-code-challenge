package com.isakic.sauna.test

import com.isakic.sauna.solver.Output
import com.isakic.sauna.solver.solve
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AcceptanceTests {

    @Test
    fun `Map 1 - a basic example`() {
        val input = """
            @---A---+
                    |
            x-B-+   C
                |   |
                +---+
            """.trimIndent()

        val expectedOutput = Output(
                "ACB",
                "@---A---+|C|+---+|+-B-x")

        val actualOutput = solve(input)

        Assertions.assertEquals(actualOutput, expectedOutput)
    }

    @Test
    fun `Map 2 - go straight through intersections`() {
        val input = """
            @
            | +-C--+
            A |    |
            +---B--+
              |      x
              |      |
              +---D--+
            """.trimIndent()

        val expectedOutput = Output(
                "ABCD",
                "@|A+---B--+|+--C-+|-||+---D--+|x")

        val actualOutput = solve(input)

        Assertions.assertEquals(actualOutput, expectedOutput)
    }

    @Test
    fun `Map 3 - letters may be found on turns`() {
        val input = """
            @---A---+
                    |
            x-B-+   |
                |   |
                +---C
            """.trimIndent()

        val expectedOutput = Output(
                "ACB",
                "@---A---+|||C---+|+-B-x")

        val actualOutput = solve(input)

        Assertions.assertEquals(actualOutput, expectedOutput)
    }

    @Test
    fun `Map 4 - do not collect a letter from the same location twice`() {
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

        val expectedOutput = Output(
                "GOONIES",
                "@-G-O-+|+-+|O||+-O-N-+|I|+-+|+-I-+|ES|x")

        val actualOutput = solve(input)

        Assertions.assertEquals(actualOutput, expectedOutput)
    }

    @Test
    fun `Map 5 - keep direction, even in a compact space`() {
        val input = """
             +-L-+
             |  +A-+
            @B+ ++ H
             ++    x
            """.trimIndent()

        val expectedOutput = Output(
                "BLAH",
                "@B+++B|+-L-+A+++A-+Hx")

        val actualOutput = solve(input)

        Assertions.assertEquals(actualOutput, expectedOutput)
    }

    @Test
    fun `Map 6 - no start`() {
        val input = """
               -A---+
                    |
            x-B-+   C
                |   |
                +---+
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }

    @Test
    fun `Map 7 - no end`() {
        val input = """
            @--A---+
                   |
             B-+   C
               |   |
               +---+
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }

    @Test
    fun `Map 8 - multiple starts`() {
        val input = """
             @--A-@-+
                    |
            x-B-+   C
                |   |
                +---+
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }

    @Test
    fun `Map 9 - multiple ends`() {
        val input = """
             @--A---+
                    |
            x-Bx+   C
                |   |
                +---+
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }

    @Test
    fun `Map 10 - T forks`() {
        val input = """
                 x-B
                   |
            @--A---+
                   |
              x+   C
               |   |
               +---+
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }

    @Test
    fun `Map 11 - broken path`() {
        val input = """
            @--A-+
                 |
                  
                 B-x
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }

    @Test
    fun `Map 12 - multiple starting paths`() {
        val input = """
            -B-@-A-x
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }

    @Test
    fun `Map 13 - fake turn`() {
        val input = """
            @-A-+-B-x
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }
}