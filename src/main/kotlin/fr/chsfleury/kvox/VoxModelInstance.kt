package fr.chsfleury.kvox

class VoxModelInstance(
    val model: VoxModelBlueprint,
    val worldOffset: Vec3
) {
    val id: Int = nextId++

    override fun toString() = "ModelInstance#" + id + "_" + worldOffset

    companion object {
        private var nextId = 0
    }
}