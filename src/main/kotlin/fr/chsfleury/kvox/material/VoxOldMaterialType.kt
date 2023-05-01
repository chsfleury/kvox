package fr.chsfleury.kvox.material

enum class VoxOldMaterialType {

    DIFFUSE,
    METAL,
    GLASS,
    EMISSIVE;

    fun getIndex(): Int {
        return ordinal
    }

    companion object {

        fun fromIndex(index: Int): VoxOldMaterialType? {
            val materials = VoxOldMaterialType.values()
            return if (index >= 0 && index < materials.size) {
                materials[index]
            } else null
        }

        fun parse(material: String): VoxOldMaterialType? = when (material) {
            "_diffuse" -> DIFFUSE
            "_metal" -> METAL
            "_glass" -> GLASS
            "_emit" -> EMISSIVE
            else -> null
        }
    }
}