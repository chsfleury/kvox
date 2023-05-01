package com.scs.voxlib.chunk;

import com.scs.voxlib.GridPoint3;
import com.scs.voxlib.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class VLVoxSizeChunk extends VLVoxChunk {
	
    private final GridPoint3 size;

    public VLVoxSizeChunk(GridPoint3 size) {
        super(VLChunkFactory.SIZE);
        this.size = size;
    }

    public VLVoxSizeChunk(int width, int length, int height) {
        super(VLChunkFactory.SIZE);
        this.size = new GridPoint3(width, length, height);
    }

    public static VLVoxSizeChunk read(InputStream stream) throws IOException {
        var size = StreamUtils.readVector3i(stream);
        //System.out.println("Read size of " + size);
        return new VLVoxSizeChunk(size);
    }

    public GridPoint3 getSize() {
        return size;
    }

    @Override
    protected void writeContent(OutputStream stream) throws IOException {
        StreamUtils.writeVector3i(size, stream);
    }
}
