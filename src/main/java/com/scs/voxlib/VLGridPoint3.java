package com.scs.voxlib;

import java.util.Objects;

public class VLGridPoint3 {

	public int x, y, z;
	
	public VLGridPoint3() {
	}

	public VLGridPoint3(int _x, int _y, int _z) {
		x = _x;
		y = _y;
		z = _z;
	}
	
	
	public VLGridPoint3(VLGridPoint3 point) {
		x = point.x;
		y = point.y;
		z = point.z;
	}

	public void set(int _x, int _y, int _z) {
		x = _x;
		y =_y;
		z = _z;
	}


	public void add(int _x, int _y, int _z) {
		x += _x;
		y +=_y;
		z += _z;
	}


	public void add(VLGridPoint3 point) {
		x += point.x;
		y += point.y;
		z += point.z;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VLGridPoint3 that = (VLGridPoint3) o;
		return x == that.x && y == that.y && z == that.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	@Override
	public String toString() {
		return String.format("%d, %d, %d)", x, y, z);
	}

}
