package io.github.ensgijs.nbt.dat.map;

public enum FilledMapDimension{

	OVERWORLD(0, "minecraft:overworld", "Overworld"),
	THE_END(1, "minecraft:the_end", "The End"),
	NETHER(-1, "minecraft:nether", "Nether");

	private final int id;
	private final String textID;
	private final String text;

	FilledMapDimension(int id, String textID, String text) {
		this.id = id;
		this.textID = textID;
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	public static FilledMapDimension byId(int id) {
		for (FilledMapDimension dimension : values()) {
			if (id == dimension.id) {
				return dimension;
			}
		}
		throw new IllegalArgumentException("invalid dimension: " + id);
	}

	public static FilledMapDimension byTextId(String id) {
		for (FilledMapDimension dimension : values()) {
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
