package io.github.ensgijs.nbt.dat.map;

import com.wonkglorg.minecraft.point.Point3i;
import io.github.ensgijs.nbt.tag.CompoundTag;
import io.github.ensgijs.nbt.util.IntPointXYZ;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapIconData {

	private String name;
	private List<MapText> textElements;
	private MapIcon color;
	private IntPointXYZ pos;

	public MapIconData(String name, String color, IntPointXYZ pos) {
		setName(name);
		this.color = MapIcon.byName(color);
		this.pos = pos;
	}

	public MapIconData(String name, MapIcon color, IntPointXYZ pos) {
		setName(name);
		this.color = color;
		this.pos = pos;
	}

	public List<MapText> getTextElements() {
		return textElements;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		try {
			textElements = parseJson(name);
			if (textElements.isEmpty()) {
				throw new Exception();
			}
			this.name = name;
		} catch (Exception e) {
			textElements = new ArrayList<>();
			textElements.add(new MapText(name, "", false, false, false, false));
			this.name = createJSONObjectName(name);
		}
	}

	private String createJSONObjectName(String name) {
		JSONObject stringName = new JSONObject();
		stringName.put("text", name);
		return stringName.toString();
	}

	public MapIcon getColor() {
		return color;
	}

	public void setIcon(MapIcon color) {
		this.color = color;
	}

	public IntPointXYZ getPos() {
		return pos;
	}

	public void setPos(IntPointXYZ pos) {
		this.pos = pos;
	}

	private List<MapText> parseJson(String json) {
		JSONObject result = new JSONObject(json);

		List<MapText> textElements = new ArrayList<>();
		addTextElement(result, textElements);

		if (result.has("extra")) {
			parseExtra(result.getJSONArray("extra"), textElements);
		}

		return textElements;
	}

	private void parseExtra(JSONArray array, List<MapText> textElements) {
		for (Object entry : array) {
			addTextElement((JSONObject) entry, textElements);
			if (((JSONObject) entry).has("extra")) {
				parseExtra(((JSONObject) entry).getJSONArray("extra"), textElements);
			}
		}
	}

	private void addTextElement(JSONObject map, List<MapText> textElements) {
		String text = map.getString("text");
		if (text == null) {
			return;
		}
		String color = map.has("color") ? map.getString("color") : "";
		boolean bold = map.has("bold") && map.getBoolean("bold");
		boolean italic = map.has("italic") && map.getBoolean("italic");
		boolean strikethrough = map.has("strikethrough") && map.getBoolean("strikethrough");
		boolean underline = map.has("underline") && map.getBoolean("underline");

		MapText textElement = new MapText(text, color, bold, italic, strikethrough, underline);
		textElements.add(textElement);
	}

	public CompoundTag toTag() {
		CompoundTag icon = new CompoundTag();
		icon.putString("Color", color.getName());
		icon.putString("Name", name);
		CompoundTag pos = new CompoundTag();
		pos.putInt("X", this.pos.getX());
		pos.putInt("Y", this.pos.getY());
		pos.putInt("Z", this.pos.getZ());
		icon.put("Pos", pos);
		return icon;
	}
}
