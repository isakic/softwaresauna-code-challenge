package com.isakic.sauna.test

import com.isakic.sauna.solver.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SolverTests {

    @Test
    fun `findStartPosition returns null if no start tile is present`() {
        val input = """
            -----+     
                 |
                 +---
        """.trimIndent()
        val map = Map(input)

        val startPosition = findStartPosition(map)

        Assertions.assertNull(startPosition)
    }

    @Test
    fun `findStartPosition returns null if more than one start tile is present`() {
        val input = """
            @--+ @---+
               |     |
               +---x x
        """.trimIndent()
        val map = Map(input)

        val startPosition = findStartPosition(map)

        Assertions.assertNull(startPosition)
    }

    @Test
    fun `findStartPosition returns the starting position`() {
        val input = """
            +---A---+
            |       |
            +---@   B
                    |
                x---C
            """.trimIndent()
        val map = Map(input)

        val startPosition = findStartPosition(map)

        Assertions.assertEquals(Position(2, 4), startPosition)
    }

    @Test
    fun `Current tile counts as visited if it appears in the path at least once before`() {
        val path = listOf(Position(0, 0))
            .go(Direction.Right, 2)
            .go(Direction.Down, 2)
            .go(Direction.Left)
            .go(Direction.Up)

        Assertions.assertFalse(path.isRevisitingCurrentPosition)

        val updatedPath = path.go(Direction.Up)

        Assertions.assertTrue(updatedPath.isRevisitingCurrentPosition)
    }

    @Test
    fun `processHorizontalTile supports simple path`() {
        val context = """
                 
            --x
                
        """.trimIndent()

        val path = processHorizontalTile(Map(context), listOf(Position(1, 1)), Direction.Right)

        Assertions.assertTrue(path.isValid)
    }

    @Test
    fun `processHorizontalTile supports intersections`() {
        val context = """
             | 
            ---
             x  
        """.trimIndent()

        val path = processHorizontalTile(Map(context), listOf(Position(1, 1), Position(1, 1)), Direction.Down)

        Assertions.assertTrue(path.isValid)
    }


    @Test
    fun `processVerticalTile supports simple path`() {
        val context = """
            -+   
             |
             x    
        """.trimIndent()

        val path = processVerticalTile(Map(context), listOf(Position(1, 1)), Direction.Down)

        Assertions.assertTrue(path.isValid)
    }

    @Test
    fun `processVerticalTile supports intersections`() {
        val context = """
             |
            -|x
             | 
        """.trimIndent()

        val path = processVerticalTile(Map(context), listOf(Position(1, 1), Position(1, 1)), Direction.Right)

        Assertions.assertTrue(path.isValid)
    }

    @Test
    fun `processCornerTile supports basic case`() {
        val context = """
             x
            -+
            
        """.trimIndent()

        val path = processCornerTile(Map(context), listOf(Position(1, 1)), Direction.Right)

        Assertions.assertTrue(path.isValid)
    }

    @Test
    fun `processCornerTile supports basic case 2`() {
        val context = """
             |
            x+
             
        """.trimIndent()

        val path = processCornerTile(Map(context), listOf(Position(1, 1)), Direction.Down)

        Assertions.assertTrue(path.isValid)
    }
}
