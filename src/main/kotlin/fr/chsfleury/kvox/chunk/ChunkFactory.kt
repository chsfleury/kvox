package fr.chsfleury.kvox.chunk

import java.io.IOException
import java.io.InputStream

object ChunkFactory {
    const val MAIN = "MAIN"
    const val PACK = "PACK"
    const val SIZE = "SIZE"
    const val XYZI = "XYZI"
    const val RGBA = "RGBA"
    const val MATL = "MATL"
    const val MATT = "MATT"
    const val nSHP = "nSHP"
    const val nTRN = "nTRN"
    const val nGRP = "nGRP"
    const val LAYR = "LAYR"

    val supportedTypes = setOf(MAIN, PACK, SIZE, XYZI, RGBA, MATL, nSHP, nTRN, nGRP, LAYR)

    @Throws(IOException::class)
    fun createChunk(type: String, stream: InputStream, childrenStream: InputStream): VoxChunk? {
        return when (type) {
            MAIN -> VoxRootChunk.read(childrenStream)
            PACK -> VoxPackChunk.read(stream)
            SIZE -> VoxSizeChunk.read(stream)
            XYZI -> VoxXYZIChunk.read(stream)
            RGBA -> VoxRGBAChunk.read(stream)
            MATT -> VoxMATTChunk.read(stream)
            MATL -> VoxMATLChunk.read(stream)
            nSHP -> VoxShapeChunk.read(stream)
            nTRN -> VoxTransformChunk.read(stream)
            nGRP -> VoxGroupChunk.read(stream)
            LAYR -> VoxLayerChunk.read(stream)
            "rOBJ", "rCAM", "NOTE" -> VoxDummyChunk(type)
            else -> null
        }
    }
}