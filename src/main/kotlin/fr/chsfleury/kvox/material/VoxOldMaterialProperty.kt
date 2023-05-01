package fr.chsfleury.kvox.material

enum class VoxOldMaterialProperty {
    PLASTIC,
    ROUGHNESS,
    SPECULAR,
    IOR,
    ATTENUATION,
    POWER,
    GLOW,
    IS_TOTAL_POWER;

    private fun flag(): Int {
        return 1 shl ordinal
    }

    fun isSet(flags: Int): Boolean {
        val flag = flag()
        return flags and flag == flag
    }
}