package com.scs;

import com.scs.voxlib.VLVoxFile;
import com.scs.voxlib.VLVoxWriter;
import com.scs.voxlib.VLVoxel;
import com.scs.voxlib.chunk.VLVoxGroupChunk;
import com.scs.voxlib.chunk.VLVoxRGBAChunk;
import com.scs.voxlib.chunk.VLVoxRootChunk;
import com.scs.voxlib.chunk.VLVoxShapeChunk;
import com.scs.voxlib.chunk.VLVoxSizeChunk;
import com.scs.voxlib.chunk.VLVoxTransformChunk;
import com.scs.voxlib.chunk.VLVoxXYZIChunk;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class CreateNewFileTest {
    /**
     * This test creates a minimal .vox file.
     */
    @Test
    public void testCreateNewFile() throws IOException {
        // Set size of the model
        var size = new VLVoxSizeChunk(3, 3, 3);

        // Color indices that we'll use. Must be between 1..255.
        final byte ID_GREEN = 1;
        final byte ID_YELLOW = 2;
        final byte ID_RED = 3;

        // Set actual ARGB values for our color indices.
        var paletteArray = new int[4];
        paletteArray[ID_GREEN] = 0xFF33CC33;
        paletteArray[ID_YELLOW] = 0xFFCCCC33;
        paletteArray[ID_RED] = 0xFFCC3333;
        var palette = new VLVoxRGBAChunk(paletteArray);

        // Set voxels using the color indices.
        var voxels = new ArrayList<VLVoxel>();
        voxels.add(new VLVoxel(1, 1, 0, ID_GREEN));
        voxels.add(new VLVoxel(1, 1, 1, ID_YELLOW));
        voxels.add(new VLVoxel(1, 1, 2, ID_RED));
        var model = new VLVoxXYZIChunk(voxels);

        // The following chunks are the necessary containers for our model:
        var groupTransform = new VLVoxTransformChunk(0);
        var group = new VLVoxGroupChunk(1);
        groupTransform.childNodeId = group.id;

        var shapeTransform = new VLVoxTransformChunk(2);
        var shape = new VLVoxShapeChunk(3);
        shape.model_ids.add(0); // id of the 1st model
        shapeTransform.childNodeId = shape.id;
        group.childIds.add(shapeTransform.id);

        // Assemble all our chunks under the root chunk.
        // The order of chunks is important.
        var root = new VLVoxRootChunk();
        root.appendChunk(size);
        root.appendChunk(model);
        root.appendChunk(groupTransform);
        root.appendChunk(group);
        root.appendChunk(shapeTransform);
        root.appendChunk(shape);
        root.appendChunk(palette);
        var voxFile = new VLVoxFile(VLVoxWriter.VERSION, root);

        // Write out the file.
        var path = Paths.get("target/test_file.vox");
        try (
            var outputStream = Files.newOutputStream(path, CREATE, TRUNCATE_EXISTING);
            var writer = new VLVoxWriter(outputStream)
        ) {
            writer.write(voxFile);
        }
        // Magica Voxel should be able to open 'test_file.vox'
    }
}
