package com.scs.voxlib.chunk;

import com.scs.voxlib.StreamUtils;
import com.scs.voxlib.mat.VoxMaterial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public final class VLVoxMATLChunk extends VLVoxChunk {
	
    private final VoxMaterial material;

    public VLVoxMATLChunk(VoxMaterial material) {
        super(VLChunkFactory.MATL);
        this.material = material;
    }

    public static VLVoxMATLChunk read(InputStream stream) throws IOException {
        int id = StreamUtils.readIntLE(stream);
        HashMap<String, String> props = StreamUtils.readDictionary(stream);
        var material = new VoxMaterial(id, props);
        return new VLVoxMATLChunk(material);
    }

    public VoxMaterial getMaterial() {
        return material;
    }

    @Override
    protected void writeContent(OutputStream stream) throws IOException {
        StreamUtils.writeIntLE(material.getID(), stream);
        StreamUtils.writeDictionary(material.getProps(), stream);
    }
}
