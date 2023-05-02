package com.scs.voxlib.chunk;

import com.scs.voxlib.VLStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class VLVoxGroupChunk extends VLVoxChunk {
	
	public final int id;
	public List<Integer> childIds = new ArrayList<Integer>();

    public VLVoxGroupChunk(int id) {
        super(VLChunkFactory.nGRP);
        this.id = id;
    }

    public static VLVoxGroupChunk read(InputStream stream) throws IOException {
        var id = VLStreamUtils.readIntLE(stream);
        var chunk = new VLVoxGroupChunk(id);
        HashMap<String, String> dict = VLStreamUtils.readDictionary(stream);
        /*if (dict.size() > 0) {
    		Settings.p("dict=" + dict);
        }*/
        int num_children = VLStreamUtils.readIntLE(stream);

        for (int i=0 ; i<num_children ; i++) {
            int child_id = VLStreamUtils.readIntLE(stream);
            chunk.childIds.add(child_id);
        }
        return chunk;
    }

    @Override
    protected void writeContent(OutputStream stream) throws IOException {
        VLStreamUtils.writeIntLE(id, stream);
        VLStreamUtils.writeIntLE(0, stream); // dict
        VLStreamUtils.writeIntLE(childIds.size(), stream);
        for (var childId : childIds) VLStreamUtils.writeIntLE(childId, stream);
    }
}
