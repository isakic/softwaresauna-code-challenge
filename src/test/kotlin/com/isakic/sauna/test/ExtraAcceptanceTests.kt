package com.isakic.sauna.test

import com.isakic.sauna.solver.solve
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

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

    @Ignore
    @Test
    fun `Map 15 - T fork on a letter`() {
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
    fun `Map 16 - Crossroad on a letter`() {
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
}