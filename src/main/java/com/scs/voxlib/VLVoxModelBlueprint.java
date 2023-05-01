package com.scs.voxlib;

import java.util.Arrays;
import java.util.Objects;

public final class VLVoxModelBlueprint {
	
	public final int id;
    private final VLGridPoint3 size;
    private final VLVoxel[] voxels;

    public VLVoxModelBlueprint(int _id, VLGridPoint3 _size, VLVoxel[] _voxels) {
        if (_size == null || _voxels == null) {
            throw new IllegalArgumentException("Both size and voxels must be non-null");
        }

        id = _id;
        this.size = _size;
        this.voxels = _voxels;
    }

    public VLGridPoint3 getSize() {
        return size;
    }

    public VLVoxel[] getVoxels() {
        return voxels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VLVoxModelBlueprint voxModel = (VLVoxModelBlueprint) o;
        return size.equals(voxModel.size) &&
                Arrays.equals(voxels, voxModel.voxels);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size);
        result = 31 * result + Arrays.hashCode(voxels);
        return result;
    }
}
