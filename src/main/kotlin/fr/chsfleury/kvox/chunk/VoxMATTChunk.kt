package fr.chsfleury.kvox.chunk

import com.scs.voxlib.mat.VLVoxOldMaterialProperty
import com.scs.voxlib.mat.VLVoxOldMaterialType
import fr.chsfleury.kvox.InvalidVoxException
import fr.chsfleury.kvox.material.VoxOldMaterial
import fr.chsfleury.kvox.utils.StreamUtils.readFloatLittleEndian
import fr.chsfleury.kvox.utils.StreamUtils.readIntLittleEndian
import java.io.IOException
import java.io.InputStream

class VoxMATTChunk(
    val material: VoxOldMaterial
): VoxChunk(ChunkFactory.MATT) {

    companion object {

        @Throws(IOException::class)
        fun read(stream: InputStream): VoxMATTChunk {
            val id = stream.readIntLittleEndian()
            val typeIndex = stream.readIntLittleEndian()
            val matType = VLVoxOldMaterialType.fromIndex(typeIndex)
                .orElseThrow { InvalidVoxException("Unknown material type $typeIndex") }
            val weight = stream.readFloatLittleEndian()
            val propBits = stream.readIntLittleEndian()
            val isTotalPower = VLVoxOldMaterialProperty.IS_TOTAL_POWER.isSet(propBits)
            var propCount = Integer.bitCount(propBits)
            if (isTotalPower) {
                propCount-- // IS_TOTAL_POWER has no value
            }
            val properties = HashMap<VLVoxOldMaterialProperty, Float>(propCount)
            for (prop in VLVoxOldMaterialProperty.values()) {
                if (prop != VLVoxOldMaterialProperty.IS_TOTAL_POWER && prop.isSet(propBits)) {
                    properties[prop] = stream.readFloatLittleEndian()
                }
            }
            return try {
                val material = VoxOldMaterial(id, weight, matType, properties, isTotalPower)
                VoxMATTChunk(material)
            } catch (e: IllegalArgumentException) {
                throw InvalidVoxException("Material with ID $id is invalid", e)
            }
        }

    }

}