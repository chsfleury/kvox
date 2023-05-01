package fr.chsfleury.kvox.chunk

import com.scs.voxlib.InvalidVoxException
import com.scs.voxlib.StreamUtils
import com.scs.voxlib.chunk.VLChunkFactory
import com.scs.voxlib.chunk.VLVoxMATTChunk
import com.scs.voxlib.mat.VoxOldMaterial
import com.scs.voxlib.mat.VoxOldMaterialProperty
import com.scs.voxlib.mat.VoxOldMaterialType
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
            val matType = VoxOldMaterialType.fromIndex(typeIndex)
                .orElseThrow { InvalidVoxException("Unknown material type $typeIndex") }
            val weight = stream.readFloatLittleEndian()
            val propBits = stream.readIntLittleEndian()
            val isTotalPower = VoxOldMaterialProperty.IS_TOTAL_POWER.isSet(propBits)
            var propCount = Integer.bitCount(propBits)
            if (isTotalPower) {
                propCount-- // IS_TOTAL_POWER has no value
            }
            val properties = HashMap<VoxOldMaterialProperty, Float>(propCount)
            for (prop in VoxOldMaterialProperty.values()) {
                if (prop != VoxOldMaterialProperty.IS_TOTAL_POWER && prop.isSet(propBits)) {
                    properties[prop] = StreamUtils.readFloatLE(stream)
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