package com.scs.voxlib.chunk;

import com.scs.voxlib.StreamUtils;
import com.scs.voxlib.Voxel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public final class VLVoxXYZIChunk extends VLVoxChunk {
	
    private final Voxel[] voxels;

    public VLVoxXYZIChunk(int voxelCount) {
        super(VLChunkFactory.XYZI);
        voxels = new Voxel[voxelCount];
    }

    public VLVoxXYZIChunk(Collection<Voxel> voxels) {
        super(VLChunkFactory.XYZI);
        this.voxels = new Voxel[voxels.size()];
        voxels.toArray(this.voxels);
    }

    public static VLVoxXYZIChunk read(InputStream stream) throws IOException {
        int voxelCount = StreamUtils.readIntLE(stream);
        var chunk = new VLVoxXYZIChunk(voxelCount);
        //System.out.println(voxelCount + " voxels");

        for (int i = 0; i < voxelCount; i++) {
            var position = StreamUtils.readVector3b(stream);
            var colorIndex = (byte) ((byte)stream.read() & 0xff);
            chunk.voxels[i] = new Voxel(position, colorIndex);
        }
        return chunk;
    }

    public Voxel[] getVoxels() {
        return voxels;
    }

    @Override
    protected void writeContent(OutputStream stream) throws IOException {
        StreamUtils.writeIntLE(voxels.length, stream);
        for (var voxel : voxels) {
            StreamUtils.writeVector3b(voxel.getPosition(), stream);
            stream.write(voxel.getColourIndex());
        }
    }
}
