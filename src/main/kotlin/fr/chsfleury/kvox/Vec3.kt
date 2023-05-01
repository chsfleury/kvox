package fr.chsfleury.kvox

import java.util.*

class Vec3(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0
) {
    constructor(vec3: Vec3) : this(vec3.x, vec3.y, vec3.z)

    fun set(x2: Int, y2: Int, z2: Int) {
        x = x2
        y = y2
        z = z2
    }

    fun add(x2: Int, y2: Int, z2: Int) {
        x += x2
        y += y2
        z += z2
    }

    fun add(vec3: Vec3) = add(vec3.x, vec3.y, vec3.z)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Vec3
        return x == that.x && y == that.y && z == that.z
    }

    override fun hashCode() = Objects.hash(x, y, z)

    override fun toString() = String.format("%d, %d, %d)", x, y, z)

}