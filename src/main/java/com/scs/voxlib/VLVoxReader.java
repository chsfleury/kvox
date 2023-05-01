package com.scs.voxlib;

import com.scs.voxlib.chunk.VoxChunk;
import com.scs.voxlib.chunk.VLVoxRootChunk;
import fr.chsfleury.kvox.VoxReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class VLVoxReader implements VoxReader {
	public static final int VERSION = 150;

    protected static final byte[] MAGIC_BYTES = new byte[] {
        (byte)'V', (byte)'O', (byte)'X', (byte)' '
    };

    private final InputStream stream;

    public VLVoxReader(InputStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("stream must not be null");
        }

        this.stream = stream;
    }

    public VLVoxFile read() throws IOException {
        byte[] magicBytes = new byte[4];
        if (stream.read(magicBytes) != 4) {
            throw new InvalidVoxException("Could not read magic bytes");
        }

        if (!Arrays.equals(magicBytes, MAGIC_BYTES)) {
            throw new InvalidVoxException("Invalid magic bytes");
        }

        int fileVersion = StreamUtils.readIntLE(stream);

        if (fileVersion < VERSION) {
            throw new InvalidVoxException(
                String.format("Vox versions older than %d are not supported", VERSION)
            );
        }

        VoxChunk chunk = VoxChunk.readChunk(stream);
        
        if (chunk == null) {
            throw new InvalidVoxException("No root chunk present in the file");
        }

        if (!(chunk instanceof VLVoxRootChunk)) {
            throw new InvalidVoxException("First chunk is not of ID \"MAIN\"");
        }

        return new VLVoxFile(fileVersion, (VLVoxRootChunk)chunk);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
