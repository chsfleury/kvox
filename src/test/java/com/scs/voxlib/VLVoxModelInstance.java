package com.scs.voxlib;

public class VLVoxModelInstance {

	public final int id;
	private static int next_id = 0;
	
	public final VLVoxModelBlueprint model;
	public final VLGridPoint3 worldOffset;
	
	public VLVoxModelInstance(VLVoxModelBlueprint _model, VLGridPoint3 _world_offset) {
		id = next_id++;
		model = _model;
		worldOffset = _world_offset;
	}
	
	
	@Override
	public String toString() {
		return "ModelInstance#" + id + "_" + this.worldOffset;
	}

}
