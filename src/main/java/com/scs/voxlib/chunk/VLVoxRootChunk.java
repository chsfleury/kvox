package com.scs.voxlib.chunk;

import com.scs.voxlib.VLGridPoint3;
import com.scs.voxlib.VLVoxModelInstance;
import com.scs.voxlib.VLVoxModelBlueprint;
import com.scs.voxlib.mat.VLVoxMaterial;
import com.scs.voxlib.mat.VLVoxOldMaterial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class VLVoxRootChunk extends VLVoxChunk {

	private final HashMap<Integer, VLVoxModelBlueprint> models = new HashMap<>();
	private final List<VLVoxModelInstance> model_instances = new ArrayList<>();
	private int[] palette = VLVoxRGBAChunk.DEFAULT_PALETTE;
	private final HashMap<Integer, VLVoxMaterial> materials = new HashMap<>();
	private final HashMap<Integer, VLVoxOldMaterial> oldMaterials = new HashMap<>();
	private final HashMap<Integer, VLVoxShapeChunk> shapeChunks = new HashMap<Integer, VLVoxShapeChunk>();
	private final HashMap<Integer, VLVoxTransformChunk> transformChunks = new HashMap<Integer, VLVoxTransformChunk>();
	private final HashMap<Integer, VLVoxGroupChunk> groupChunks = new HashMap<Integer, VLVoxGroupChunk>();
	private VLVoxTransformChunk root_transform;

	/**
	 * Chunks SIZE and XYZI always come in pairs.
	 * This is a temporary variable that remembers the last SIZE, so that
	 * it can be used for the next XYZI chunk.
	 */
	private VLGridPoint3 size;
	private final List<VLVoxChunk> children = new ArrayList<>();

	public VLVoxRootChunk() {
		super(VLChunkFactory.MAIN);
	}


	public static VLVoxRootChunk read(InputStream stream, InputStream childrenStream) throws IOException {
		var root = new VLVoxRootChunk();
		VLVoxChunk first = VLVoxChunk.readChunk(childrenStream);

		if (first instanceof VLVoxPackChunk) {
			//VoxPackChunk pack = (VoxPackChunk)first;
			//modelCount = pack.getModelCount(); // Ignore this, it is obsolete
			first = null;
		}

		while (childrenStream.available() > 0) {
			VLVoxChunk chunk1;

			if (first != null) {
				// If first != null, then that means that the first chunk was not a PACK chunk,
				// and we've already read a SIZE chunk.
				chunk1 = first;
				first = null;
			} else {
				chunk1 = VLVoxChunk.readChunk(childrenStream);
			}
			root.appendChunk(chunk1);

			if (chunk1 instanceof VLVoxSizeChunk) {
				VLVoxChunk chunk2 = VLVoxChunk.readChunk(childrenStream, VLChunkFactory.XYZI);
				root.appendChunk(chunk2);
			}
		}

		// Calc world offset by iterating through the scenegraph
		root.iterateThruScenegraph();
		return root;
	}

	public void appendChunk(VLVoxChunk chunk) {
		children.add(chunk);

		if (chunk instanceof VLVoxSizeChunk) {
			this.size = ((VLVoxSizeChunk) chunk).getSize();
		} else if (chunk instanceof VLVoxXYZIChunk) {
			VLVoxXYZIChunk xyzi = (VLVoxXYZIChunk) chunk;
			models.put(
				models.size(),
				new VLVoxModelBlueprint(models.size(), size, xyzi.getVoxels())
			);
		} else if (chunk instanceof VLVoxRGBAChunk) {
			VLVoxRGBAChunk rgba = (VLVoxRGBAChunk) chunk;
			palette = rgba.getPalette();
		} else {
			processChunk(chunk);
		}
	}

	private void processChunk(VLVoxChunk chunk) {
		if (chunk instanceof VLVoxMATLChunk) {
			VLVoxMaterial mat = ((VLVoxMATLChunk) chunk).getMaterial();
			materials.put(mat.getID(), mat);
		} else if (chunk instanceof VLVoxMATTChunk) {
			VLVoxOldMaterial mat = ((VLVoxMATTChunk) chunk).getMaterial();
			oldMaterials.put(mat.getID(), mat);
		} else if (chunk instanceof VLVoxShapeChunk) {
			VLVoxShapeChunk shapeChunk = (VLVoxShapeChunk)chunk;
			this.shapeChunks.put(shapeChunk.id, shapeChunk);
		} else if (chunk instanceof VLVoxTransformChunk) {
			VLVoxTransformChunk transformChunk = (VLVoxTransformChunk)chunk;
			if (this.transformChunks.size() == 0) {
				root_transform = transformChunk;
			}
			this.transformChunks.put(transformChunk.id, transformChunk);
		} else if (chunk instanceof VLVoxGroupChunk) {
			VLVoxGroupChunk groupChunk = (VLVoxGroupChunk)chunk;
			this.groupChunks.put(groupChunk.id, groupChunk);
		}
	}

	
	private VLGridPoint3 findShapeOrGroupParent(int shape_id) {
		VLGridPoint3 offset = new VLGridPoint3(0, 0, 0);

		for(VLVoxTransformChunk transformChunk : this.transformChunks.values()) {
			if (transformChunk.childNodeId == shape_id) {
				offset.add(transformChunk.transform);
				offset.add(this.findTransformParent(transformChunk.id));
				break;
			}
		}

		return offset;		
	}


	private VLGridPoint3 findTransformParent(int transform_id) {
		VLGridPoint3 offset = new VLGridPoint3(0, 0, 0);

		for(VLVoxGroupChunk groupChunk : this.groupChunks.values()) {
			if (groupChunk.childIds.contains(transform_id)) {
				VLGridPoint3 suboffset = this.findShapeOrGroupParent(groupChunk.id);
				offset.add(suboffset);
				break;
			}
		}

		return offset;		
	}



	public List<VLVoxModelInstance> getModelInstances() {
		return model_instances;
	}


	public int[] getPalette() {
		return palette;
	}


	public HashMap<Integer, VLVoxMaterial> getMaterials() {
		return materials;
	}


	public HashMap<Integer, VLVoxOldMaterial> getOldMaterials() {
		return oldMaterials;
	}


	private void iterateThruScenegraph() {
		this.findTransformParent(root_transform.id);
		this.processTransformChunk(root_transform, root_transform.transform);
	}


	private void processTransformChunk(VLVoxTransformChunk transform_chunk, VLGridPoint3 pos) {
		VLGridPoint3 new_pos = new VLGridPoint3(pos);
		if (this.groupChunks.containsKey(transform_chunk.childNodeId)) {
			processGroupChunk(this.groupChunks.get(transform_chunk.childNodeId), new_pos);
		} else if (this.shapeChunks.containsKey(transform_chunk.childNodeId)) {
			processShapeChunk(this.shapeChunks.get(transform_chunk.childNodeId), new_pos);
		}
	}


	private void processGroupChunk(VLVoxGroupChunk group_chunk, VLGridPoint3 pos) {
		for (int child_id : group_chunk.childIds) {
			VLVoxTransformChunk trn = this.transformChunks.get(child_id);
			VLGridPoint3 new_pos = new VLGridPoint3(pos);
			new_pos.add(trn.transform);
			this.processTransformChunk(trn, new_pos);
		}
	}


	private void processShapeChunk(VLVoxShapeChunk shape_chunk, VLGridPoint3 pos) {
		for (int model_id : shape_chunk.model_ids) {
			VLVoxModelBlueprint model = this.models.get(model_id);
			if (model.getVoxels().length > 0) {
				VLVoxModelInstance instance = new VLVoxModelInstance(model, new VLGridPoint3(pos));
				this.model_instances.add(instance);
			}
		}
	}

	@Override
	protected void writeChildren(OutputStream stream) throws IOException {
		for (var chunk : children) {
			if (VLChunkFactory.supportedTypes.contains(chunk.getType())) {
				chunk.writeTo(stream);
			}
		}
	}
}
