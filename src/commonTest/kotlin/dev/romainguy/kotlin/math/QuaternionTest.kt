/*
 * Copyright (C) 2017 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.romainguy.kotlin.math

import kotlin.test.Test

class QuaternionTest {

    @Test
    fun fromAxisAngleUsesRadians() {
        val axis = Float3(z = 3f)
        val cases = listOf(
            0f to Quaternion(w = 1f),
            HALF_PI to Quaternion(z = 0.7071068f, w = 0.7071068f),
            -HALF_PI to Quaternion(z = -0.7071068f, w = 0.7071068f),
            PI to Quaternion(z = 1f, w = 0f),
            TWO_PI to Quaternion(w = -1f),
        )

        for ((angle, expected) in cases) {
            val result = Quaternion.fromAxisAngle(axis, angle)

            MatrixTest.assertArrayEquals(expected.toFloatArray(), result.toFloatArray())
        }
    }

    @Test
    fun eulerOverloadsUseRadiansForEveryOrder() {
        val angles = Float3(0.25f, -0.4f, 0.6f)

        for (order in RotationsOrder.entries) {
            val quaternion = Quaternion.fromEuler(angles, order)
            val scalarQuaternion = Quaternion.fromEuler(
                angles[order.yaw], angles[order.pitch], angles[order.roll], order
            )
            val extracted = eulerAngles(quaternion, order)

            MatrixTest.assertArrayEquals(scalarQuaternion.toFloatArray(), quaternion.toFloatArray())
            MatrixTest.assertArrayEquals(angles.toFloatArray(), extracted.toFloatArray())
            MatrixTest.assertMatEquals(rotation(angles, order), quaternion.toMatrix())
        }
    }

    @Test
    fun toEulerAnglesReturnsRadians() {
        val quaternion = Quaternion.fromAxisAngle(Float3(z = 1f), HALF_PI)

        val angles = quaternion.toEulerAngles()

        MatrixTest.assertArrayEquals(Float3(z = HALF_PI).toFloatArray(), angles.toFloatArray())
    }

    @Test
    fun fromRotationHandlesOppositeVectors() {
        val vectors = listOf(
            Float3(x = 1f),
            Float3(y = 1f),
            Float3(z = 1f),
            Float3(0.6f, 0.8f, 0f),
        )

        for (from in vectors) {
            val to = -from

            val quaternion = Quaternion.fromRotation(from, to)
            val rotated = quaternion * from

            MatrixTest.assertArrayEquals(to.toFloatArray(), rotated.toFloatArray())
        }
    }

    @Test
    fun fromAxisAngle() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.0093f, 0.0186f, 0.0280f, 0.9994f).toFloatArray(),
            Quaternion.fromAxisAngle(Float3(1.0f, 2.0f, 3.0f), radians(4.0f)).toFloatArray()
        )
    }

    @Test
    fun fromEulerXYZ() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.009179f, 0.0172174f, 0.0263242f, 0.999463f).toFloatArray(),
            Quaternion.fromEuler(Float3(radians(1f), radians(2f), radians(3f)), RotationsOrder.XYZ).toFloatArray()
        )
    }

    @Test
    fun fromEulerXZY() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.0082654f, 0.0172174f, 0.0263242f, 0.999471f).toFloatArray(),
            Quaternion.fromEuler(Float3(radians(1f), radians(2f), radians(3f)), RotationsOrder.XZY).toFloatArray()
        )
    }

    @Test
    fun fromEulerYXZ() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.009179f, 0.0172174f, 0.0260197f, 0.999471f).toFloatArray(),
            Quaternion.fromEuler(Float3(radians(1f), radians(2f), radians(3f)), RotationsOrder.YXZ).toFloatArray()
        )
    }

    @Test
    fun fromEulerYZX() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.009179f, 0.0176742f, 0.0260197f, 0.999463f).toFloatArray(),
            Quaternion.fromEuler(Float3(radians(1f), radians(2f), radians(3f)), RotationsOrder.YZX).toFloatArray()
        )
    }

    @Test
    fun fromEulerZXY() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.0082654f, 0.0176742f, 0.0263242f, 0.999463f).toFloatArray(),
            Quaternion.fromEuler(Float3(radians(1f), radians(2f), radians(3f)), RotationsOrder.ZXY).toFloatArray()
        )
    }

    @Test
    fun fromEulerZYX() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.0082654f, 0.0176742f, 0.0260197f, 0.999471f).toFloatArray(),
            Quaternion.fromEuler(Float3(radians(1f), radians(2f), radians(3f)), RotationsOrder.ZYX).toFloatArray()
        )
    }

    @Test
    fun toEulerXYZ() {
        MatrixTest.assertArrayEquals(
            transform(Float3(-63.4349488f, 41.8103164f, 169.6951532f), ::radians).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.XYZ).toFloatArray()
        )
    }

    @Test
    fun toEulerXZY() {
        MatrixTest.assertArrayEquals(
            transform(Float3(109.6538245f, 137.7263108f, 7.6622561f), ::radians).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.XZY).toFloatArray()
        )
    }

    @Test
    fun toEulerYXZ() {
        MatrixTest.assertArrayEquals(
            transform(Float3(-41.8103164f, 63.4349488f, 116.5650512f), ::radians).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.YXZ).toFloatArray()
        )
    }

    @Test
    fun toEulerYZX() {
        MatrixTest.assertArrayEquals(
            transform(Float3(-116.5650512f, -169.6951532f, 41.8103164f), ::radians).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.YZX).toFloatArray()
        )
    }

    @Test
    fun toEulerZXY() {
        MatrixTest.assertArrayEquals(
            transform(Float3(68.9605309f, -21.8014099f, 158.1985901f), ::radians).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.ZXY).toFloatArray()
        )
    }

    @Test
    fun toEulerZYX() {
        MatrixTest.assertArrayEquals(
            transform(Float3(70.3461755f, -7.6622561f, 137.7263108f), ::radians).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.ZYX).toFloatArray()
        )
    }
}
