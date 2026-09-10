package dev.romainguy.kotlin.math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VectorTest {
    @Test
    fun `Float3 comparisons with a vector return component masks`() {
        val a = Float3(2.0f, 3.0f, 0.0f)
        val b = Float3(1.0f, 3.0f, 3.0f)

        val result = listOf(
            lessThan(a, b), a lt b,
            lessThanEqual(a, b), a lte b,
            greaterThan(a, b), a gt b,
            greaterThanEqual(a, b), a gte b,
            equal(a, b), a eq b,
            notEqual(a, b), a neq b,
        )

        assertEquals(
            listOf(
                Bool3(false, false, true), Bool3(false, false, true),
                Bool3(false, true, true), Bool3(false, true, true),
                Bool3(true, false, false), Bool3(true, false, false),
                Bool3(true, true, false), Bool3(true, true, false),
                Bool3(false, true, false), Bool3(false, true, false),
                Bool3(true, false, true), Bool3(true, false, true),
            ),
            result,
        )
    }

    @Test
    fun `Float3 comparisons with a scalar return component masks`() {
        val a = Float3(4.0f, 3.0f, 0.0f)
        val b = 3.0f

        val result = listOf(
            lessThan(a, b), a lt b,
            lessThanEqual(a, b), a lte b,
            greaterThan(a, b), a gt b,
            greaterThanEqual(a, b), a gte b,
            equal(a, b), a eq b,
            notEqual(a, b), a neq b,
        )

        assertEquals(
            listOf(
                Bool3(false, false, true), Bool3(false, false, true),
                Bool3(false, true, true), Bool3(false, true, true),
                Bool3(true, false, false), Bool3(true, false, false),
                Bool3(true, true, false), Bool3(true, true, false),
                Bool3(false, true, false), Bool3(false, true, false),
                Bool3(true, false, true), Bool3(true, false, true),
            ),
            result,
        )
    }

    @Test
    fun `greaterThan compares matching components for every vector type`() {
        val a = Float4(2.0f, 0.0f, 4.0f, 0.0f)
        val b = Float4(1.0f, 3.0f, 3.0f, 1.0f)
        val halfA = Half4(a.x.toHalf(), a.y.toHalf(), a.z.toHalf(), a.w.toHalf())
        val halfB = Half4(b.x.toHalf(), b.y.toHalf(), b.z.toHalf(), b.w.toHalf())

        val floats = listOf(
            greaterThan(a.xy, b.xy), greaterThan(b.xy, a.xy),
            greaterThan(a.xyz, b.xyz), greaterThan(b.xyz, a.xyz),
            greaterThan(a, b), greaterThan(b, a),
        )
        val halves = listOf(
            greaterThan(halfA.xy, halfB.xy), greaterThan(halfB.xy, halfA.xy),
            greaterThan(halfA.xyz, halfB.xyz), greaterThan(halfB.xyz, halfA.xyz),
            greaterThan(halfA, halfB), greaterThan(halfB, halfA),
        )

        val expected = listOf(
            Bool2(true, false), Bool2(false, true),
            Bool3(true, false, true), Bool3(false, true, false),
            Bool4(true, false, true, false), Bool4(false, true, false, true),
        )
        assertEquals(expected, floats)
        assertEquals(expected, halves)
    }

    @Test
    fun `Float3 equality supports scalar and vector tolerances with a strict boundary`() {
        val a = Float3(1.0f, 1.125f, 1.25f)
        val b = Float3(1.0f)

        val result = listOf(
            equal(a, 1.0f, 0.25f), notEqual(a, 1.0f, 0.25f),
            equal(a, b, 0.25f), notEqual(a, b, 0.25f),
        )

        assertEquals(
            listOf(
                Bool3(true, true, false), Bool3(false, false, true),
                Bool3(true, true, false), Bool3(false, false, true),
            ),
            result,
        )
    }

    @Test
    fun `exact equality works across vector and matrix sizes`() {
        val vector = Float4(1.0f, 2.0f, 3.0f, 4.0f)
        val matrix = Mat4.of(
            1.0f, 2.0f, 3.0f, 4.0f,
            5.0f, 6.0f, 7.0f, 8.0f,
            9.0f, 10.0f, 11.0f, 12.0f,
            13.0f, 14.0f, 15.0f, 16.0f,
        )

        val result = listOf(
            all(equal(vector.xy, vector.xy)),
            all(equal(vector.xyz, vector.xyz)),
            all(equal(vector, vector)),
            vector.xy.equals(vector.xy, 0.0f),
            vector.xyz.equals(vector.xyz, 0.0f),
            vector.equals(vector, 0.0f),
            Mat2().equals(Mat2(), 0.0f),
            Mat3().equals(Mat3(), 0.0f),
            matrix.equals(Mat4(matrix), 0.0f),
        )

        assertEquals(List(9) { true }, result)
    }

    @Test
    fun `Float3 equality treats matching infinities and signed zeros as equal`() {
        val a = Float3(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, -0.0f)
        val b = Float3(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, 0.0f)

        val result = listOf(equal(a, b), equal(a, b, 0.001f), a eq b)
        val unequal = listOf(notEqual(a, b), notEqual(a, b, 0.001f), a neq b)

        assertEquals(List(3) { Bool3(true, true, true) }, result)
        assertEquals(List(3) { Bool3(false, false, false) }, unequal)
    }

    @Test
    fun `NaN and opposite infinities remain unequal`() {
        val a = Float3(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)
        val b = Float3(Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY)

        val result = listOf(equal(a, b), equal(a, b, 0.001f), a eq b)
        val unequal = listOf(notEqual(a, b), notEqual(a, b, 0.001f), a neq b)

        assertEquals(List(3) { Bool3(false, false, false) }, result)
        assertEquals(List(3) { Bool3(true, true, true) }, unequal)
    }

    @Test
    fun `scalar equality accepts exact values and rejects unequal values at zero tolerance`() {
        val equal = 1.0f.equals(1.0f, 0.0f)
        val unequal = 1.0f.equals(1.125f, 0.0f)

        assertTrue(equal)
        assertFalse(unequal)
    }
}
