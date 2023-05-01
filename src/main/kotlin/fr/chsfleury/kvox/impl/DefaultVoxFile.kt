package fr.chsfleury.kvox.impl

import fr.chsfleury.kvox.VoxFile
import fr.chsfleury.kvox.chunk.VoxRootChunk

class DefaultVoxFile(
    override val version: Int,
    override val root: VoxRootChunk
): VoxFile<VoxRootChunk> {
    val palette: IntArray get() = root.palette
    val modelInstances get() = root.modelInstances
    val materials get() = root.materials

}