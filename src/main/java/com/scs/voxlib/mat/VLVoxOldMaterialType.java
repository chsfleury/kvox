package com.scs.voxlib.mat;

import java.util.Optional;

public enum VLVoxOldMaterialType {
    DIFFUSE,
    METAL,
    GLASS,
    EMISSIVE;

    public int getIndex() {
        return ordinal();
    }

    public static Optional<VLVoxOldMaterialType> fromIndex(int index) {
        VLVoxOldMaterialType[] materials = VLVoxOldMaterialType.values();

        if (index >= 0 && index < materials.length) {
            return Optional.of(materials[index]);
        }

        return Optional.empty();
    }

    public static Optional<VLVoxOldMaterialType> parse(String material) {
        VLVoxOldMaterialType mat = null;

        switch (material) {
            case "_diffuse":
                mat = DIFFUSE;
                break;
            case "_metal":
                mat = METAL;
                break;
            case "_glass":
                mat = GLASS;
                break;
            case "_emit":
                mat = EMISSIVE;
                break;
        }

        return Optional.ofNullable(mat);
    }
}
