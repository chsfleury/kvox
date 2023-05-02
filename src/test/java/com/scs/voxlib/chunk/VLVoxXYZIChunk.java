package com.scs.voxlib.chunk;

import com.scs.voxlib.VLStreamUtils;
import com.scs.voxlib.VLVoxel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public final class VLVoxXYZIChunk extends VLVoxChunk {
	
    private final VLVoxel[] voxels;

    public VLVoxXYZIChunk(int voxelCount) {
        super(VLChunkFactory.XYZI);
        voxels = new VLVoxel[voxelCount];
    }

    public VLVoxXYZIChunk(Collection<VLVoxel> voxels) {
        super(VLChunkFactory.XYZI);
        this.voxels = new VLVoxel[voxels.size()];
        voxels.toArray(this.voxels);
    }

    public static VLVoxXYZIChunk read(InputStream stream) throws IOException {
        int voxelCount = VLStreamUtils.readIntLE(stream);
        var chunk = new VLVoxXYZIChunk(voxelCount);
        //System.out.println(voxelCount + " voxels");

        for (int i = 0; i < voxelCount; i++) {
            var position = VLStreamUtils.readVector3b(stream);
            var colorIndex = (byte) ((byte)stream.read() & 0xff);
            chunk.voxels[i] = new VLVoxel(position, colorIndex);
        }
        return chunk;
    }

    public VLVoxel[] getVoxels() {
        return voxels;
    }

    @Override
    protected void writeContent(OutputStream stream) throws IOException {
        VLStreamUtils.writeIntLE(voxels.length, stream);
        for (var voxel : voxels) {
            VLStreamUtils.writeVector3b(voxel.getPosition(), stream);
            stream.write(voxel.getColourIndex());
        }
    }
}
