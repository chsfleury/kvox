package com.scs.voxlib.chunk;

import com.scs.voxlib.VLStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class VLVoxPackChunk extends VLVoxChunk {
    private final int modelCount;

    public VLVoxPackChunk(int modelCount) {
        super(VLChunkFactory.PACK);
        this.modelCount = modelCount;
    }

    public static VLVoxPackChunk read(InputStream stream) throws IOException {
        var modelCount = VLStreamUtils.readIntLE(stream);
        return new VLVoxPackChunk(modelCount);
    }

    int getModelCount() {
        return modelCount;
    }

    @Override
    protected void writeContent(OutputStream stream) throws IOException {
        VLStreamUtils.writeIntLE(modelCount, stream);
    }
}
