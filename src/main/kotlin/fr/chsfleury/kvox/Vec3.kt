package fr.chsfleury.kvox

data class Vec3(
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0
) {
    operator fun plus(v: Vec3) = Vec3(
        x + v.x,
        y + v.y,
        z + v.z
    )

    companion object {
        val ORIGIN = Vec3(0, 0, 0)
    }
}
