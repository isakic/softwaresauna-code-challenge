package com.isakic.sauna.test

import com.isakic.sauna.solver.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SolverTests {

    @Test
    fun `Current tile counts as visited if it appears in the path at least once before`() {
        val path = listOf(Position(0, 0))
            .go(Direction.Right, 2)
            .go(Direction.Down, 2)
            .go(Direction.Left)
            .go(Direction.Up)

        Assertions.assertFalse(currentTileAlreadyVisited(path))

        val updatedPath = path.go(Direction.Up)

        Assertions.assertTrue(currentTileAlreadyVisited(updatedPath))
    }
}
