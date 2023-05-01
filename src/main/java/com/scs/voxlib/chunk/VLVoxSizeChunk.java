package com.scs.voxlib.chunk;

import com.scs.voxlib.VLGridPoint3;
import com.scs.voxlib.VLStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class VLVoxSizeChunk extends VLVoxChunk {
	
    private final VLGridPoint3 size;

    public VLVoxSizeChunk(VLGridPoint3 size) {
        super(VLChunkFactory.SIZE);
        this.size = size;
    }

    public VLVoxSizeChunk(int width, int length, int height) {
        super(VLChunkFactory.SIZE);
        this.size = new VLGridPoint3(width, length, height);
    }

    public static VLVoxSizeChunk read(InputStream stream) throws IOException {
        var size = VLStreamUtils.readVector3i(stream);
        //System.out.println("Read size of " + size);
        return new VLVoxSizeChunk(size);
    }

    public VLGridPoint3 getSize() {
        return size;
    }

    @Override
    protected void writeContent(OutputStream stream) throws IOException {
        VLStreamUtils.writeVector3i(size, stream);
    }
}
