package com.scs.voxlib.chunk;

import com.scs.voxlib.VLInvalidVoxException;
import com.scs.voxlib.VLStreamUtils;
import com.scs.voxlib.mat.VLVoxOldMaterial;
import com.scs.voxlib.mat.VLVoxOldMaterialProperty;
import com.scs.voxlib.mat.VLVoxOldMaterialType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public final class VLVoxMATTChunk extends VLVoxChunk {
	
    private final VLVoxOldMaterial material;

    public VLVoxMATTChunk(VLVoxOldMaterial material) {
        super(VLChunkFactory.MATT);
        this.material = material;
    }

    public static VLVoxMATTChunk read(InputStream stream) throws IOException {
        int id = VLStreamUtils.readIntLE(stream);
        int typeIndex = VLStreamUtils.readIntLE(stream);
        VLVoxOldMaterialType matType = VLVoxOldMaterialType.fromIndex(typeIndex)
            .orElseThrow(() -> new VLInvalidVoxException("Unknown material type " + typeIndex));
        float weight = VLStreamUtils.readFloatLE(stream);
        int propBits = VLStreamUtils.readIntLE(stream);
        boolean isTotalPower = VLVoxOldMaterialProperty.IS_TOTAL_POWER.isSet(propBits);

        int propCount = Integer.bitCount(propBits);

        if (isTotalPower) {
            propCount--; // IS_TOTAL_POWER has no value
        }

        HashMap<VLVoxOldMaterialProperty, Float> properties = new HashMap<>(propCount);

        for (VLVoxOldMaterialProperty prop : VLVoxOldMaterialProperty.values()) {
            if (prop != VLVoxOldMaterialProperty.IS_TOTAL_POWER && prop.isSet(propBits)) {
                properties.put(prop, VLStreamUtils.readFloatLE(stream));
            }
        }

        try {
            var material = new VLVoxOldMaterial(id, weight, matType, properties, isTotalPower);
            return new VLVoxMATTChunk(material);
        } catch (IllegalArgumentException e) {
            throw new VLInvalidVoxException("Material with ID " + id + " is invalid", e);
        }
    }

    public VLVoxOldMaterial getMaterial() {
        return material;
    }

    //TODO: write old material, if necessary
}
