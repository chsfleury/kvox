package fr.chsfleury.kvox

import java.util.*

class VoxModelBlueprint(
    val id: Int = 0,
    val size: Vec3,
    val voxels: List<Voxel>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val voxModel = other as VoxModelBlueprint
        return size == voxModel.size && voxels == voxModel.voxels
    }

    override fun hashCode(): Int {
        var result = Objects.hash(size)
        result = 31 * result + voxels.hashCode()
        return result
    }

}