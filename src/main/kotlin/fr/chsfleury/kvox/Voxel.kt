package fr.chsfleury.kvox

import java.util.*

class Voxel(
    val position: Vec3,
    val colourIndex: Byte
) {
    constructor(x: Int, y: Int, z: Int, colourIndex: Byte) : this(Vec3(x, y, z), colourIndex)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val voxel = other as Voxel
        return colourIndex == voxel.colourIndex && position == voxel.position
    }

    override fun hashCode() = Objects.hash(position, colourIndex)

    override fun toString() = "(" + position.toString() + ", " + java.lang.Byte.toUnsignedInt(colourIndex) + ")"

}