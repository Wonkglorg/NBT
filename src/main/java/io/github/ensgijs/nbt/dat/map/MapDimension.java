package io.github.ensgijs.nbt.dat.map;

public enum MapDimension {

	OVERWORLD(0, "minecraft:overworld", "Overworld"),
	THE_END(1, "minecraft:the_end", "The End"),
	NETHER(-1, "minecraft:nether", "Nether");

	private final int id;
	private final String textID;
	private final String text;

	MapDimension(int id, String textID, String text) {
		this.id = id;
		this.textID = textID;
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	public static MapDimension byId(int id) {
		for (MapDimension dimension : values()) {
			if (id == dimension.id) {
				return dimension;
			}
		}
		throw new IllegalArgumentException("invalid dimension: " + id);
	}

	public static MapDimension byTextId(String id) {
		for (MapDimension dimension : values()) {
			if (dimension.textID.equals(id)) {
				return dimension;
			}
		}
		return null;
	}

	public int getId() {
		return id;
	}

	public String getTextID() {
		return textID;
	}
}
