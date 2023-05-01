package com.scs;

import com.scs.voxlib.VLVoxFile;
import com.scs.voxlib.VLVoxReader;
import com.scs.voxlib.VLVoxWriter;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;


public class ReadNWriteModelsTest {

    private VLVoxFile loadVox(String resourcePath) throws IOException {
        var fullPath = getClass().getResource(resourcePath).getPath();
        try (var reader = new VLVoxReader(new FileInputStream(fullPath))) {
            return reader.read();
        }
    }

    private void testModel(String path, int fileVersion, int modelCount, int voxelCount, int materialCount) throws IOException {
        var file = loadVox(path);

        var tempFilePath = Paths.get("target" + path);
        try (
            var tempFileOut = Files.newOutputStream(tempFilePath, CREATE, TRUNCATE_EXISTING);
            var writer = new VLVoxWriter(tempFileOut)
        ) {
            writer.write(file);
        }

        //Reload and test again
        try (var reader = new VLVoxReader(Files.newInputStream(tempFilePath))) {
            file = reader.read();
        }

        assertThat(file.getVersion()).isEqualTo(fileVersion);

        var models = file.getModelInstances();

        assertThat(models)
            .isNotNull()
            .hasSize(modelCount)
            .element(0)
            .isNotNull();

        int voxelSum = 0;
        for (var modelInstance : models) {
            voxelSum += modelInstance.model.getVoxels().length;
        }
        assertThat(voxelSum).isEqualTo(voxelCount);

        // First colour is always black
        int[] palette = file.getPalette();
        assertThat(palette[0]).isEqualTo(0x00000000);

        for (int i = 1; i < palette.length; i++) {
            assertThat(palette[i]).isNotEqualTo(0x00000000);
        }

        assertThat(file.getMaterials()).hasSize(materialCount);
    }

    @Test
    public void testChrKnight() throws IOException {
        testModel("/chr_knight.vox", 150, 1, 398, 256);
    }

    @Test
    public void testTeapot() throws IOException {
        testModel("/teapot.vox", 150, 1, 28411, 256);
    }

    @Test
    public void testMonu2() throws IOException {
        testModel("/monu2.vox", 150, 1, 150764, 256);
    }
}
