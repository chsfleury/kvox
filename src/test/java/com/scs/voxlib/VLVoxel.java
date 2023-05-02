package com.scs.voxlib;

import java.util.Objects;

public final class VLVoxel {
	
    private final VLGridPoint3 position;
    private final byte colourIndex;

    public VLVoxel(VLGridPoint3 position, byte colourIndex) {
        this.position = position;
        this.colourIndex = colourIndex;
    }

    public VLVoxel(int x, int y, int z, byte colourIndex) {
        this(new VLGridPoint3(x, y, z), colourIndex);
    }

    public VLGridPoint3 getPosition() {
        return position;
    }

    public int getColourIndex() {
        return colourIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VLVoxel voxel = (VLVoxel) o;
        return colourIndex == voxel.colourIndex &&
                Objects.equals(position, voxel.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, colourIndex);
    }

    @Override
    public String toString() {
        return "(" + position.toString() + ", " + Byte.toUnsignedInt(colourIndex) + ")";
    }
}
