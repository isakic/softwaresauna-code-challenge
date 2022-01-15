package com.isakic.sauna.test

import com.isakic.sauna.solver.Output
import com.isakic.sauna.solver.solve
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

/**
 * Additional cases I've discovered during my analysis. The ignored tests represent nice-to-have features that
 * wasn't explicitly or implicitly mentioned by the problem statement. These features were skipped due to time
 * constraints.
 */
class ExtraAcceptanceTests {

    @Test
    fun `Map 14 - Crossroads`() {
        val input = """
                 x-B
                   |
            @--A---+--D-x
                   |
              x+   C
               |   |
               +---+
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }

    @Test
    fun `Map 15 - T forks 2`() {
        val input = """
                 x-B
                   |
            @--A---+-C-x
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }

    @Ignore
    @Test
    fun `Map 16 - T fork on a letter`() {
        val input = """
            @-A-x
              |
              x
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }

    @Ignore
    @Test
    fun `Map 17 - Crossroad on a letter`() {
        val input = """
              +-B-x
              |
            @-A-x
              |
              x
            """.trimIndent()

        val actualOutput = solve(input)

        Assertions.assertNull(actualOutput)
    }

    @Ignore
    @Test
    fun `Map 18 - Really tight paths`() {
        val input = """
            +-B-+
            +-A+|
             @-+|
            x-C-+
            """.trimIndent()

        val actualOutput = solve(input)

        val expectedOutput = Output(
            "ABC",
            "@-++A-++-B-+||+-C-x")

        Assertions.assertEquals(expectedOutput, actualOutput)
    }
}