package fr.chsfleury.kvox.chunk

import com.scs.voxlib.VLGridPoint3
import com.scs.voxlib.VLVoxModelBlueprint
import com.scs.voxlib.VLVoxModelInstance
import com.scs.voxlib.mat.VLVoxOldMaterial
import fr.chsfleury.kvox.material.VoxMaterial
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VoxRootChunk: VoxChunk(ChunkFactory.MAIN) {

    private val models = mutableMapOf<Int, VLVoxModelBlueprint>()
    private val chunkModelInstances = mutableListOf<VLVoxModelInstance>()
    private var chunkPalette: IntArray = VoxRGBAChunk.DEFAULT_PALETTE
    private val chunkMaterials = mutableMapOf<Int, VoxMaterial>()
    private val oldMaterials = mutableMapOf<Int, VLVoxOldMaterial>()
    private val shapeChunks = mutableMapOf<Int, VoxShapeChunk>()
    private val transformChunks = mutableMapOf<Int, VoxTransformChunk>()
    private val groupChunks = mutableMapOf<Int, VoxGroupChunk>()
    private var rootTransform: VoxTransformChunk? = null

    val palette: IntArray get() = chunkPalette
    val modelInstances: List<VLVoxModelInstance> get() = chunkModelInstances
    val materials: Map<Int, VoxMaterial> get() = chunkMaterials

    /**
     * Chunks SIZE and XYZI always come in pairs.
     * This is a temporary variable that remembers the last SIZE, so that
     * it can be used for the next XYZI chunk.
     */
    private var size: VLGridPoint3? = null
    private val children: MutableList<VoxChunk> = mutableListOf()

    companion object {

        @Throws(IOException::class)
        fun read(childrenStream: InputStream): VoxRootChunk {
            val root = VoxRootChunk()
            var first = readChunk(childrenStream)
            if (first is VoxPackChunk) {
                //VoxPackChunk pack = (VoxPackChunk)first;
                //modelCount = pack.getModelCount(); // Ignore this, it is obsolete
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
                    root.appendChunk(chunk1)
                }
                if (chunk1 is VoxSizeChunk) {
                    val chunk2 = readChunk(childrenStream, ChunkFactory.XYZI)
                    if (chunk2 != null) {
                        root.appendChunk(chunk2)
                    }
                }
            }

            // Calc world offset by iterating through the scenegraph
            root.iterateThruScenegraph()
            return root
        }

    }


    private fun appendChunk(chunk: VoxChunk) {
        children.add(chunk)
        when (chunk) {
            is VoxSizeChunk -> {
                size = chunk.size
            }
            is VoxXYZIChunk -> {
                models[models.size] = VLVoxModelBlueprint(models.size, size, chunk.voxels)
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

    private fun findShapeOrGroupParent(shapeId: Int): VLGridPoint3 {
        val offset = VLGridPoint3(0, 0, 0)
        for (transformChunk in transformChunks.values) {
            if (transformChunk.childNodeId == shapeId) {
                offset.add(transformChunk.transform)
                offset.add(findTransformParent(transformChunk.id))
                break
            }
        }
        return offset
    }


    private fun findTransformParent(transformId: Int): VLGridPoint3 {
        val offset = VLGridPoint3(0, 0, 0)
        for (groupChunk in groupChunks.values) {
            if (groupChunk.childIds.contains(transformId)) {
                val subOffset = findShapeOrGroupParent(groupChunk.id)
                offset.add(subOffset)
                break
            }
        }
        return offset
    }

    private fun iterateThruScenegraph() {
        findTransformParent(rootTransform!!.id)
        processTransformChunk(rootTransform, rootTransform!!.transform)
    }


    private fun processTransformChunk(transformChunk: VoxTransformChunk?, pos: VLGridPoint3) {
        val newPos = VLGridPoint3(pos)
        if (groupChunks.containsKey(transformChunk!!.childNodeId)) {
            processGroupChunk(groupChunks[transformChunk.childNodeId]!!, newPos)
        } else if (shapeChunks.containsKey(transformChunk.childNodeId)) {
            processShapeChunk(shapeChunks[transformChunk.childNodeId]!!, newPos)
        }
    }


    private fun processGroupChunk(groupChunk: VoxGroupChunk, pos: VLGridPoint3) {
        for (childId in groupChunk.childIds) {
            val trn = transformChunks[childId]
            val newPos = VLGridPoint3(pos)
            newPos.add(trn!!.transform)
            processTransformChunk(trn, newPos)
        }
    }


    private fun processShapeChunk(shapeChunk: VoxShapeChunk, pos: VLGridPoint3) {
        for (modelId in shapeChunk.modelIds) {
            val model = models[modelId]
            if (model!!.voxels.isNotEmpty()) {
                val instance = VLVoxModelInstance(model, VLGridPoint3(pos))
                chunkModelInstances.add(instance)
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