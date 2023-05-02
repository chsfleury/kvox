package com.scs.voxlib;

import fr.chsfleury.kvox.VoxWriter;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class VLVoxWriter implements VoxWriter<VLVoxFile> {
    public static final int VERSION = 150;

    private final DataOutputStream stream;

    public VLVoxWriter(OutputStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("stream must not be null");
        }

        this.stream = new DataOutputStream(stream);
    }

    public void write(VLVoxFile file) throws IOException {
        try (stream) {
            stream.write(VLVoxReader.MAGIC_BYTES);
            VLStreamUtils.writeIntLE(file.getVersion(), stream);
            file.getRoot().writeTo(stream);
        }
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
