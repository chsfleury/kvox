package com.scs.voxlib;

import com.scs.voxlib.chunk.VLVoxRootChunk;
import com.scs.voxlib.mat.VoxMaterial;
import fr.chsfleury.kvox.VoxFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class VLVoxFile implements VoxFile<VLVoxRootChunk> {
    private final int version;
    private final VLVoxRootChunk root;

    public VLVoxFile(int version, VLVoxRootChunk root) {
        this.version = version;
        this.root = root;
    }

    public VLVoxRootChunk getRoot() {
        return root;
    }

    public List<VLVoxModelInstance> getModelInstances() {
        return root.getModelInstances();
    }

    @NotNull
    public int[] getPalette() {
        return root.getPalette();
    }

    public Map<Integer, VoxMaterial> getMaterials() {
        return root.getMaterials();
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "VoxFile{version=" + version + "}";
    }
}
