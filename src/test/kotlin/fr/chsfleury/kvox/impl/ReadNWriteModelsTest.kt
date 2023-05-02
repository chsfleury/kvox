package fr.chsfleury.kvox.impl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class ReadNWriteModelsTest {

    @Throws(IOException::class)
    private fun loadVox(resourcePath: String): DefaultVoxFile? {
        return javaClass.getResource(resourcePath)
            ?.path
            ?.let {
                DefaultVoxReader(FileInputStream(it))
                    .use { reader -> reader.read() }
            }
    }

    @Throws(IOException::class)
    private fun testModel(path: String, fileVersion: Int, modelCount: Int, voxelCount: Int, materialCount: Int) {
        var file = loadVox(path)!!
        val tempFilePath = Paths.get("target$path")
        Files.newOutputStream(tempFilePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { tempFileOut ->
            DefaultVoxWriter(tempFileOut).use { writer ->
                writer.write(
                    file
                )
            }
        }
        DefaultVoxReader(Files.newInputStream(tempFilePath)).use { reader -> file = reader.read() }
        assertThat(file.version).isEqualTo(fileVersion)
        val models = file.modelInstances
        assertThat(models)
            .isNotNull()
            .hasSize(modelCount)
            .element(0)
            .isNotNull()
        var voxelSum = 0
        for (modelInstance in models) {
            voxelSum += modelInstance.model.voxels.size
        }
        assertThat(voxelSum).isEqualTo(voxelCount)

        // First colour is always black
        val palette = file.palette
        assertThat(palette[0]).isEqualTo(0x00000000)
        for (i in 1 until palette.size) {
            assertThat(palette[i]).isNotEqualTo(0x00000000)
        }
        assertThat(file.materials).hasSize(materialCount)
    }

    @Test
    @Throws(IOException::class)
    fun testChrKnight() {
        testModel("/chr_knight.vox", 150, 1, 398, 256)
    }

    @Test
    @Throws(IOException::class)
    fun testTeapot() {
        testModel("/teapot.vox", 150, 1, 28411, 256)
    }

    @Test
    @Throws(IOException::class)
    fun testMonu2() {
        testModel("/monu2.vox", 150, 1, 150764, 256)
    }

}