package fr.chsfleury.kvox.chunk

import fr.chsfleury.kvox.ColorPalette
import fr.chsfleury.kvox.Vec3
import fr.chsfleury.kvox.VoxModelBlueprint
import fr.chsfleury.kvox.VoxModelInstance
import fr.chsfleury.kvox.material.VoxMaterial
import fr.chsfleury.kvox.material.VoxOldMaterial
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxRootChunk(chunks: Iterable<VoxChunk>): VoxChunk(ChunkFactory.MAIN) {
    constructor(vararg chunks: VoxChunk) : this(chunks.asIterable()) {
        iterateThruSceneGraph()
    }

    private val models = mutableMapOf<Int, VoxModelBlueprint>()
    private val chunkModelInstances = mutableListOf<VoxModelInstance>()
    private var chunkPalette: IntArray = ColorPalette.DEFAULT_PALETTE
    private val chunkMaterials = mutableMapOf<Int, VoxMaterial>()
    private val oldMaterials = mutableMapOf<Int, VoxOldMaterial>()
    private val shapeChunks = mutableMapOf<Int, VoxShapeChunk>()
    private val transformChunks = mutableMapOf<Int, VoxTransformChunk>()
    private val groupChunks = mutableMapOf<Int, VoxGroupChunk>()
    private var rootTransform: VoxTransformChunk? = null

    val palette: IntArray get() = chunkPalette
    val modelInstances: List<VoxModelInstance> get() = chunkModelInstances
    val materials: Map<Int, VoxMaterial> get() = chunkMaterials

    /**
     * Chunks SIZE and XYZI always come in pairs.
     * This is a temporary variable that remembers the last SIZE, so that
     * it can be used for the next XYZI chunk.
     */
    private var size: Vec3? = null
    private val children: MutableList<VoxChunk> = mutableListOf()

    init {
        chunks.forEach(::append)
    }

    companion object {

        @Throws(IOException::class)
        fun read(childrenStream: InputStream): VoxRootChunk {
            val children = mutableListOf<VoxChunk>()
            var first = readChunk(childrenStream)
            if (first is VoxPackChunk) {
                first = null
            }
            while (childrenStream.available() > 0) {
                var chunk1: VoxChunk?
                if (first != null) {
                    // If first != null, then that means that the first chunk was not a PACK chunk,
                    // and we've already read a SIZE chunk.
                    chunk1 = first
                    first = null
                } else {
                    chunk1 = readChunk(childrenStream)
                }
                if (chunk1 != null) {
                    children += chunk1
                }
                if (chunk1 is VoxSizeChunk) {
                    val chunk2 = readChunk(childrenStream, ChunkFactory.XYZI)
                    if (chunk2 != null) {
                        children += chunk2
                    }
                }
            }

            // Calc world offset by iterating through the scenegraph
            return VoxRootChunk(children)
                .apply { iterateThruSceneGraph() }
        }

    }

    private fun append(chunk: VoxChunk) = apply {
        children.add(chunk)
        when (chunk) {
            is VoxSizeChunk -> {
                size = chunk.size
            }
            is VoxXYZIChunk -> {
                models[models.size] = VoxModelBlueprint(models.size, size!!, chunk.voxels)
            }
            is VoxRGBAChunk -> {
                chunkPalette = chunk.palette
            }
            else -> {
                processChunk(chunk)
            }
        }
    }

    private fun processChunk(chunk: VoxChunk) {
        when (chunk) {
            is VoxMATLChunk -> {
                val mat = chunk.material
                chunkMaterials[mat.id] = mat
            }
            is VoxMATTChunk -> {
                val mat = chunk.material
                oldMaterials[mat.id] = mat
            }
            is VoxShapeChunk -> {
                shapeChunks[chunk.id] = chunk
            }
            is VoxTransformChunk -> {
                if (transformChunks.isEmpty()) {
                    rootTransform = chunk
                }
                transformChunks[chunk.id] = chunk
            }
            is VoxGroupChunk -> {
                groupChunks[chunk.id] = chunk
            }
        }
    }

    private fun findShapeOrGroupParent(shapeId: Int): Vec3 {
        for (transformChunk in transformChunks.values) {
            if (transformChunk.childNodeId == shapeId) {
                return transformChunk.transform + findTransformParent(transformChunk.id)
            }
        }
        return Vec3.ORIGIN
    }

    private fun findTransformParent(transformId: Int): Vec3 {
        for (groupChunk in groupChunks.values) {
            if (groupChunk.childIds.contains(transformId)) {
                return findShapeOrGroupParent(groupChunk.id)
            }
        }
        return Vec3.ORIGIN
    }

    private fun iterateThruSceneGraph() {
        val rTransform = rootTransform!!
        findTransformParent(rTransform.id)
        processTransformChunk(rTransform, rTransform.transform)
    }


    private fun processTransformChunk(transformChunk: VoxTransformChunk, pos: Vec3) {
        if (groupChunks.containsKey(transformChunk.childNodeId)) {
            processGroupChunk(groupChunks[transformChunk.childNodeId]!!, pos)
        } else if (shapeChunks.containsKey(transformChunk.childNodeId)) {
            processShapeChunk(shapeChunks[transformChunk.childNodeId]!!, pos)
        }
    }


    private fun processGroupChunk(groupChunk: VoxGroupChunk, pos: Vec3) {
        for (childId in groupChunk.childIds) {
            val transformChunk = transformChunks[childId]
                ?: throw IllegalStateException("child chunk $childId not found")
            processTransformChunk(transformChunk, pos + transformChunk.transform)
        }
    }


    private fun processShapeChunk(shapeChunk: VoxShapeChunk, pos: Vec3) {
        for (modelId in shapeChunk.modelIds) {
            val model = models[modelId] ?: throw IllegalStateException("model $modelId not found")
            if (model.voxels.isNotEmpty()) {
                chunkModelInstances += VoxModelInstance(model, pos)
            }
        }
    }

    @Throws(IOException::class)
    override fun writeChildren(stream: OutputStream) {
        for (chunk in children) {
            if (ChunkFactory.supportedTypes.contains(chunk.type)) {
                chunk.writeTo(stream)
            }
        }
    }

}