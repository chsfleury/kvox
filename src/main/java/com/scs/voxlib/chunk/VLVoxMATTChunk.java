package com.scs.voxlib.chunk;

import com.scs.voxlib.InvalidVoxException;
import com.scs.voxlib.StreamUtils;
import com.scs.voxlib.mat.VoxOldMaterial;
import com.scs.voxlib.mat.VoxOldMaterialProperty;
import com.scs.voxlib.mat.VoxOldMaterialType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public final class VLVoxMATTChunk extends VLVoxChunk {
	
    private final VoxOldMaterial material;

    public VLVoxMATTChunk(VoxOldMaterial material) {
        super(VLChunkFactory.MATT);
        this.material = material;
    }

    public static VLVoxMATTChunk read(InputStream stream) throws IOException {
        int id = StreamUtils.readIntLE(stream);
        int typeIndex = StreamUtils.readIntLE(stream);
        VoxOldMaterialType matType = VoxOldMaterialType.fromIndex(typeIndex)
            .orElseThrow(() -> new InvalidVoxException("Unknown material type " + typeIndex));
        float weight = StreamUtils.readFloatLE(stream);
        int propBits = StreamUtils.readIntLE(stream);
        boolean isTotalPower = VoxOldMaterialProperty.IS_TOTAL_POWER.isSet(propBits);

        int propCount = Integer.bitCount(propBits);

        if (isTotalPower) {
            propCount--; // IS_TOTAL_POWER has no value
        }

        HashMap<VoxOldMaterialProperty, Float> properties = new HashMap<>(propCount);

        for (VoxOldMaterialProperty prop : VoxOldMaterialProperty.values()) {
            if (prop != VoxOldMaterialProperty.IS_TOTAL_POWER && prop.isSet(propBits)) {
                properties.put(prop, StreamUtils.readFloatLE(stream));
            }
        }

        try {
            var material = new VoxOldMaterial(id, weight, matType, properties, isTotalPower);
            return new VLVoxMATTChunk(material);
        } catch (IllegalArgumentException e) {
            throw new InvalidVoxException("Material with ID " + id + " is invalid", e);
        }
    }

    public VoxOldMaterial getMaterial() {
        return material;
    }

    //TODO: write old material, if necessary
}
