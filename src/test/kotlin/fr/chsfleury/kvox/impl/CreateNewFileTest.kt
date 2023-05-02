package fr.chsfleury.kvox.impl

import com.scs.voxlib.VLVoxWriter
import fr.chsfleury.kvox.Voxel
import fr.chsfleury.kvox.chunk.VoxGroupChunk
import fr.chsfleury.kvox.chunk.VoxRGBAChunk
import fr.chsfleury.kvox.chunk.VoxRootChunk
import fr.chsfleury.kvox.chunk.VoxShapeChunk
import fr.chsfleury.kvox.chunk.VoxSizeChunk
import fr.chsfleury.kvox.chunk.VoxTransformChunk
import fr.chsfleury.kvox.chunk.VoxXYZIChunk
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class CreateNewFileTest {

    /**
     * This test creates a minimal .vox file.
     */
    @Test
    @Throws(IOException::class)
    fun testCreateNewFile() {
        // Set size of the model

        // Set size of the model
        val size = VoxSizeChunk(3, 3, 3)

        // Color indices that we'll use. Must be between 1..255.

        // Color indices that we'll use. Must be between 1..255.
        val ID_GREEN: Byte = 1
        val ID_YELLOW: Byte = 2
        val ID_RED: Byte = 3

        // Set actual ARGB values for our color indices.

        // Set actual ARGB values for our color indices.
        val paletteArray = IntArray(4)
        paletteArray[ID_GREEN.toInt()] = -0xcc33cd
        paletteArray[ID_YELLOW.toInt()] = -0x3333cd
        paletteArray[ID_RED.toInt()] = -0x33cccd
        val palette = VoxRGBAChunk(paletteArray)

        // Set voxels using the color indices.

        // Set voxels using the color indices.
        val voxels = mutableListOf<Voxel>()
        voxels.add(Voxel(1, 1, 0, ID_GREEN))
        voxels.add(Voxel(1, 1, 1, ID_YELLOW))
        voxels.add(Voxel(1, 1, 2, ID_RED))
        val model = VoxXYZIChunk(voxels)

        // The following chunks are the necessary containers for our model:

        // The following chunks are the necessary containers for our model:
        val groupTransform = VoxTransformChunk(0)

        val shapeTransform = VoxTransformChunk(2)
        val shape = VoxShapeChunk(3, listOf(0)) // id of the 1st model

        shapeTransform.childNodeId = shape.id
        val group = VoxGroupChunk(1, listOf(shapeTransform.id))
        groupTransform.childNodeId = group.id

        // Assemble all our chunks under the root chunk.
        // The order of chunks is important.

        // Assemble all our chunks under the root chunk.
        // The order of chunks is important.
        val root = VoxRootChunk()
        root.appendChunk(size)
        root.appendChunk(model)
        root.appendChunk(groupTransform)
        root.appendChunk(group)
        root.appendChunk(shapeTransform)
        root.appendChunk(shape)
        root.appendChunk(palette)
        val voxFile = DefaultVoxFile(VLVoxWriter.VERSION, root)

        // Write out the file.

        // Write out the file.
        val path = Paths.get("target/test_file.vox")
        Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { outputStream ->
            DefaultVoxWriter(outputStream).use { writer ->
                writer.write(
                    voxFile
                )
            }
        }
        // Magica Voxel should be able to open 'test_file.vox'
    }
}