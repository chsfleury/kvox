package com.scs.voxlib.chunk;

import com.scs.voxlib.VLStreamUtils;
import com.scs.voxlib.mat.VLVoxMaterial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public final class VLVoxMATLChunk extends VLVoxChunk {
	
    private final VLVoxMaterial material;

    public VLVoxMATLChunk(VLVoxMaterial material) {
        super(VLChunkFactory.MATL);
        this.material = material;
    }

    public static VLVoxMATLChunk read(InputStream stream) throws IOException {
        int id = VLStreamUtils.readIntLE(stream);
        HashMap<String, String> props = VLStreamUtils.readDictionary(stream);
        var material = new VLVoxMaterial(id, props);
        return new VLVoxMATLChunk(material);
    }

    public VLVoxMaterial getMaterial() {
        return material;
    }

    @Override
    protected void writeContent(OutputStream stream) throws IOException {
        VLStreamUtils.writeIntLE(material.getID(), stream);
        VLStreamUtils.writeDictionary(material.getProps(), stream);
    }
}
