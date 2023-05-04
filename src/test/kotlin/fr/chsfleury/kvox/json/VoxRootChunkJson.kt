package fr.chsfleury.kvox.json

import fr.chsfleury.kvox.VoxModelInstance
import fr.chsfleury.kvox.chunk.VoxRootChunk
import fr.chsfleury.kvox.material.VoxMaterial

data class VoxRootChunkJson(
    val palette: IntArray,
    val modelInstances: List<VoxModelInstance>,
    val materials: Map<Int, VoxMaterial>
) {
    constructor(root: VoxRootChunk) : this(
        root.palette,
        root.modelInstances,
        root.materials
    )
}
