package com.scs.voxlib.chunk;

import com.scs.voxlib.VLGridPoint3;
import com.scs.voxlib.VLStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public final class VLVoxTransformChunk extends VLVoxChunk {

	public final int id;
	public int childNodeId;
	public VLGridPoint3 transform = new VLGridPoint3();

	public VLVoxTransformChunk(int id) {
		super(VLChunkFactory.nTRN);
		this.id = id;
	}

	public static VLVoxTransformChunk read(InputStream stream) throws IOException {
		var id = VLStreamUtils.readIntLE(stream);
		var chunk = new VLVoxTransformChunk(id);
		HashMap<String, String> dict = VLStreamUtils.readDictionary(stream);
		chunk.childNodeId = VLStreamUtils.readIntLE(stream);
		int neg1 = VLStreamUtils.readIntLE(stream);
		if (neg1 != -1) {
			throw new RuntimeException("neg1 checksum failed");
		}
		int layer_id = VLStreamUtils.readIntLE(stream);
		int num_frames = VLStreamUtils.readIntLE(stream);

		// Rotation
		for (int i=0 ; i<num_frames ; i++) {
			HashMap<String, String> rot = VLStreamUtils.readDictionary(stream);
			if (rot.containsKey("_t")) {
				//Settings.p("Got _t=" + rot.get("_t"));
				String[] tokens = rot.get("_t").split(" ");
				VLGridPoint3 tmp = new VLGridPoint3(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
				chunk.transform.set(tmp.x, tmp.y, tmp.z);
			}
			if (rot.containsKey("_r")) {
				System.err.println("Warning: _r is being ignored");
			}
		}
		return chunk;
	}

	@Override
	public String toString() {
		return "VoxTransformChunk#" + id + "_" + this.transform;
	}

	@Override
	protected void writeContent(OutputStream stream) throws IOException {
		VLStreamUtils.writeIntLE(id, stream);
		VLStreamUtils.writeIntLE(0, stream); // dict
		VLStreamUtils.writeIntLE(childNodeId, stream);
		VLStreamUtils.writeIntLE(-1, stream); // neg
		VLStreamUtils.writeIntLE(0, stream); // layer_id
		if (transform.x != 0 || transform.y != 0 || transform.z != 0) {
			VLStreamUtils.writeIntLE(1, stream); // frames
			var rot = new HashMap<String, String>();
			rot.put("_t", String.format("%d %d %d", transform.x, transform.y, transform.z));
			VLStreamUtils.writeDictionary(rot, stream);
		} else {
			VLStreamUtils.writeIntLE(0, stream); // frames
		}

	}
}


