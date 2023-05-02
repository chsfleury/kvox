package com.scs.voxlib.chunk;

import com.scs.voxlib.VLGridPoint3;
import com.scs.voxlib.VLStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public final class VLVoxLayerChunk extends VLVoxChunk {

	public int id;
	public int child_node_id;
	public VLGridPoint3 transform = new VLGridPoint3();

	public VLVoxLayerChunk() {
		super(VLChunkFactory.LAYR);
	}

	public static VLVoxLayerChunk read(InputStream stream)
		throws IOException
	{
		var chunk = new VLVoxLayerChunk();
		chunk.id = VLStreamUtils.readIntLE(stream);
		HashMap<String, String> dict = VLStreamUtils.readDictionary(stream);
		//Settings.p("dict=" + dict);
		/*if (dict.containsKey("_name")) {
			Settings.p("Layer Name: " + dict.get("_name"));
		}*/
		int reserved = VLStreamUtils.readIntLE(stream);
		return chunk;
	}

	@Override
	public String toString() {
		return "VoxLayerChunk#" + id + "_" + this.transform;
	}

	@Override
	protected void writeContent(OutputStream stream) throws IOException {
		VLStreamUtils.writeIntLE(id, stream);
		VLStreamUtils.writeIntLE(0, stream); // dict
		VLStreamUtils.writeIntLE(0, stream); // reserved
	}
}


