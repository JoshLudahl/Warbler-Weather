package com.warbler.utilities

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExtensionsKtTest {
    @Test
    fun `test areNonZeroValuesFound with non-zero values`() {
        val nonZeroValues = listOf(1.0, 2.5, 0.0, 3.0)
        assertTrue(areNonZeroValuesFound(nonZeroValues))
    }

    @Test
    fun `test areNonZeroValuesFound with only zero values`() {
        val zeroValues = listOf(0.0, 0.0, 0.0)
        assertFalse(areNonZeroValuesFound(zeroValues))
    }

    @Test
    fun `test areNonZeroValuesFound with empty list`() {
        val emptyList = emptyList<Double>()
        assertFalse(areNonZeroValuesFound(emptyList))
    }

    @Test
    fun `test areNonZeroValuesFound with mixed values`() {
        val mixedValues = listOf(0.0, 0.0, 5.0, 0.0)
        assertTrue(areNonZeroValuesFound(mixedValues))
    }

    @Test
    fun `test doesAnyListContainValues with non-zero values`() {
        val listWithNonZero =
            listOf(
                listOf(0.0, 0.0, 0.0),
                listOf(1.0, 2.5, 0.0),
                listOf(0.0, 3.0, 0.0),
            )
        assertTrue(doesAnyListContainValues(listWithNonZero))
    }

    @Test
    fun `test doesAnyListContainValues with only zero values`() {
        val listWithZero =
            listOf(
                listOf(0.0, 0.0, 0.0),
                listOf(0.0, 0.0, 0.0),
                listOf(0.0, 0.0, 0.0),
            )
        assertFalse(doesAnyListContainValues(listWithZero))
    }

    @Test
    fun `test doesAnyListContainValues with empty list`() {
        val emptyList = emptyList<List<Double>>()
        assertFalse(doesAnyListContainValues(emptyList))
    }

    @Test
    fun `test doesAnyListContainValues with empty inner lists`() {
        val listOfEmptyLists =
            listOf(
                emptyList<Double>(),
                emptyList<Double>(),
                emptyList<Double>(),
            )
        assertFalse(doesAnyListContainValues(listOfEmptyLists))
    }
}
