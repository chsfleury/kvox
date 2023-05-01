package fr.chsfleury.kvox.material

import java.util.*

class VoxOldMaterial (
    val id: Int,
    private val weight: Float = 0f,
    private val type: VoxOldMaterialType,
    private val properties: Map<VoxOldMaterialProperty, Float?>,
    private val isTotalPower: Boolean = false
) {
    init {
        require(!(id < 1 || id > 255)) { "id must be in [1..255]" }
        if (type == VoxOldMaterialType.DIFFUSE) {
            require(weight.toDouble() == 1.0) { "A diffuse material must have a weight of 1.0" }
        } else {
            require(weight > 0.0 && weight <= 1.0) { "weight must be in (0.0..1.0]" }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as VoxOldMaterial
        return id == that.id
                && that.weight.compareTo(weight) == 0
                && isTotalPower == that.isTotalPower
                && type == that.type
                && properties == that.properties
    }

    override fun hashCode() = Objects.hash(id, weight, type, properties, isTotalPower)
}