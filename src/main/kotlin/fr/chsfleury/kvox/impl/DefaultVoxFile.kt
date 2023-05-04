package fr.chsfleury.kvox.impl

import fr.chsfleury.kvox.VoxFile
import fr.chsfleury.kvox.chunk.VoxRootChunk

class DefaultVoxFile(
    override val version: Int,
    override val root: VoxRootChunk
): VoxFile<VoxRootChunk> {

    @Transient
    val palette: IntArray = root.palette

    @Transient
    val modelInstances = root.modelInstances

    @Transient
    val materials = root.materials

}