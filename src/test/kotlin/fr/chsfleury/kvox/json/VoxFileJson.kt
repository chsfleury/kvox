package fr.chsfleury.kvox.json

import fr.chsfleury.kvox.impl.DefaultVoxFile

data class VoxFileJson(
    val version: Int,
    val root: VoxRootChunkJson
) {
    constructor(voxFile: DefaultVoxFile) : this (
        voxFile.version,
        VoxRootChunkJson(voxFile.root)
    )
}