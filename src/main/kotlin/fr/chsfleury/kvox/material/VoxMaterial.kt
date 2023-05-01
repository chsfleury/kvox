package fr.chsfleury.kvox.material

import java.util.*

class VoxMaterial(
    val id: Int,
    val properties: Map<String, String>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as VoxMaterial
        return id == that.id && properties == that.properties
    }

    override fun hashCode() = Objects.hash(id, properties)
}