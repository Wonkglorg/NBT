package io.github.ensgijs.nbt.dat.map;

public enum MapScale {

	SCALE_0(0),
	SCALE_1(1),
	SCALE_2(2),
	SCALE_3(3),
	SCALE_4(4);

	private final int id;

	MapScale(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "" + id;
	}

	public static MapScale byId(int id) {
		for (MapScale scale : values()) {
			if (id == scale.id) {
				return scale;
			}
		}
		throw new IllegalArgumentException("invalid scale: " + id);
	}

	public byte getId() {
		return (byte) id;
	}
}
