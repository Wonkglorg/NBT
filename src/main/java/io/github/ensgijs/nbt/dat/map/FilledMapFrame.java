package io.github.ensgijs.nbt.dat.map;

import io.github.ensgijs.nbt.util.IntPointXYZ;

public class FilledMapFrame{

	private int id;
	private int rotation;
	private IntPointXYZ pos;

	public FilledMapFrame(int id, int rotation, IntPointXYZ pos) {
		this.id = id;
		this.rotation = rotation;
		this.pos = pos;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public IntPointXYZ getPos() {
		return pos;
	}

	public void setPos(IntPointXYZ pos) {
		this.pos = pos;
	}
}
