package io.github.ensgijs.nbt.dat.map;

public class MapText {
	private final String text;
	private final String color;
	private final boolean bold;
	private final boolean italic;
	private final boolean underlined;
	private final boolean strikethrough;

	public MapText(String text, String color, boolean bold, boolean italic, boolean strikethrough,
			boolean underline) {
		this.text = text;
		this.color = color;
		this.bold = bold;
		this.italic = italic;
		this.strikethrough = strikethrough;
		this.underlined = underline;
	}

}
