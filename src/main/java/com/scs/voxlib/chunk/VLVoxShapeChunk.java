package com.scs.voxlib.chunk;

import com.scs.voxlib.VLStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class VLVoxShapeChunk extends VLVoxChunk {
	
	public final int id;
	public List<Integer> model_ids = new ArrayList<Integer>();

    public VLVoxShapeChunk(int id) {
        super(VLChunkFactory.nSHP);
        this.id = id;
    }

    public static VLVoxShapeChunk read(InputStream stream) throws IOException {
        var id = VLStreamUtils.readIntLE(stream);
        var chunk = new VLVoxShapeChunk(id);

        HashMap<String, String> dict = VLStreamUtils.readDictionary(stream);
        /*if (dict.size() > 0) {
    		Settings.p("dict=" + dict);
        }*/

        int num_models = VLStreamUtils.readIntLE(stream);

        for (int i=0 ; i<num_models ; i++) {
            int model_id = VLStreamUtils.readIntLE(stream);
            HashMap<String, String> model_dict = VLStreamUtils.readDictionary(stream);
            /*if (model_dict.size() > 0) {
        		Settings.p("model_dict=" + dict);
            }*/

            chunk.model_ids.add(model_id);
        }
        return chunk;
	}

    @Override
    protected void writeContent(OutputStream stream) throws IOException {
        VLStreamUtils.writeIntLE(id, stream);
        VLStreamUtils.writeIntLE(0, stream); // dict
        VLStreamUtils.writeIntLE(model_ids.size(), stream);
        for (var modelId : model_ids) {
            VLStreamUtils.writeIntLE(modelId, stream);
            VLStreamUtils.writeIntLE(0, stream); // dict
        }
    }
}
