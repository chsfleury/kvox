package fr.chsfleury.kvox.impl

import fr.chsfleury.kvox.ColorARGB
import fr.chsfleury.kvox.Vec3
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
        val sizeChunk = VoxSizeChunk(3, 3, 8)

        val green = ColorARGB.of(-0xcc33cd)
        val yellow = ColorARGB.of(-0x3333cd)
        val red = ColorARGB.of(-0x33cccd)
        val blue = ColorARGB(0, 0, 255, 64)
        val colorPalette = MutableColorPalette(
            green,
            yellow,
            red,
            blue
        )

        val palette = VoxRGBAChunk(colorPalette)


        // Set voxels using the color indices.
        val voxels = mutableListOf<Voxel>()
        voxels.add(Voxel(1, 1, 0, colorPalette[green]))
        voxels.add(Voxel(1, 1, 1, colorPalette[yellow]))
        voxels.add(Voxel(1, 1, 2, colorPalette[red]))
        voxels.add(Voxel(1, 1, 3, colorPalette[blue]))
        voxels.add(Voxel(1, 1, 4, colorPalette[red]))
        voxels.add(Voxel(1, 1, 5, colorPalette[yellow]))
        voxels.add(Voxel(1, 1, 6, colorPalette[red]))
        voxels.add(Voxel(1, 1, 7, colorPalette[blue]))
        val model = VoxXYZIChunk(voxels)

        // The following chunks are the necessary containers for our model:
        val shape = VoxShapeChunk(3, listOf(0)) // id of the 1st model
        val shapeTransform = VoxTransformChunk(2, shape.id, Vec3(0, 0, sizeChunk.size.z / 2))

        val shape2 = VoxShapeChunk(5, listOf(0)) // id of the 1st model
        val shapeTransform2 = VoxTransformChunk(4, shape2.id, Vec3(2, 0, sizeChunk.size.z / 2))

        val group = VoxGroupChunk(1, listOf(shapeTransform.id, shapeTransform2.id))
        val groupTransform = VoxTransformChunk(0, group.id)

        // Assemble all our chunks under the root chunk.
        // The order of chunks is important.
        val root = VoxRootChunk(
            sizeChunk,
            model,
            groupTransform,
            group,
            shapeTransform,
            shape,
            shapeTransform2,
            shape2,
            palette
        )

        val voxFile = DefaultVoxFile(DefaultVoxReader.VOX_FORMAT_VERSION, root)

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