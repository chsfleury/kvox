package com.scs;

import com.scs.voxlib.VLVoxFile;
import com.scs.voxlib.VLVoxReader;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ParseModelsTest {
    private VLVoxFile loadVox(String resourcePath) throws IOException {
        var fullPath = getClass().getResource(resourcePath).getPath();
        try (VLVoxReader reader = new VLVoxReader(new FileInputStream(fullPath))) {
            return reader.read();
        }
    }

    private void testModel(String path, int fileVersion, int modelCount, int voxelCount, int materialCount) throws IOException {
        var file = loadVox(path);
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
