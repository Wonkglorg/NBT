package io.github.ensgijs.nbt.dat.map;

import io.github.ensgijs.nbt.io.BinaryNbtHelpers;
import io.github.ensgijs.nbt.io.CompressionType;
import io.github.ensgijs.nbt.mca.util.VersionedDataContainer;
import io.github.ensgijs.nbt.tag.CompoundTag;
import io.github.ensgijs.nbt.tag.IntTag;
import io.github.ensgijs.nbt.tag.ListTag;
import io.github.ensgijs.nbt.tag.StringTag;
import io.github.ensgijs.nbt.util.IntPointXYZ;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FilledMap implements VersionedDataContainer{
	public static final int SCALE = 3;
	public static final int IMAGE_WIDTH = 128;
	public static final int IMAGE_HEIGHT = 128;
	/**
	 * All Maps are compressed in GZip as per <a
	 * href="https://minecraft.fandom.com/wiki/Map_item_format">Map Item Format Specification</a>
	 */
	public static final CompressionType COMPRESSION_TYPE = CompressionType.GZIP;
	
	protected int dataVersion;
	/**
	 * Optionally set variable that denotes this maps id (this is not an internally stored property
	 * from the .dat file and is derived by the dat files naming
	 */
	private int mapId = -1;
	private final CompoundTag root;
	/**
	 * Byte array of pixels in the length of {@link #IMAGE_HEIGHT} * {@link #IMAGE_WIDTH}
	 */
	private byte[] imageData;
	private Integer xCenter;
	private Integer zCenter;
	private FilledMapScale scale;
	private FilledMapDimension dimension;
	private boolean trackingPosition;
	private boolean unlimitedTracking;
	private boolean locked;
	private final List<FilledMapIconData> banners = new ArrayList<>();
	private final List<FilledMapFrame> frames = new ArrayList<>();
	
	//-------------tags-------------
	private static final String DATA_TAG = "data";
	private static final String BANNERS_TAG = "banners";
	private static final String NAME_TAG = "Name";
	private static final String COLOR_TAG = "Color";
	private static final String POS_TAG = "Pos";
	private static final String FRAMES_TAG = "frames";
	private static final String ENTITY_ID_TAG = "EntityId";
	private static final String ROTATION_TAG = "Rotation";
	private static final String DIMENSION_TAG = "dimension";
	private static final String DATA_VERSION_TAG = "DataVersion";
	private static final String LOCKED_TAG = "locked";
	private static final String SCALE_TAG = "scale";
	private static final String TRACKING_POSITION_TAG = "trackingPosition";
	private static final String UNLIMITED_TRACKING_TAG = "unlimitedTracking";
	private static final String X_CENTER_TAG = "xCenter";
	private static final String Z_CENTER_TAG = "zCenter";
	private static final String COLORS_TAG = "colors";
	
	/**
	 * Creates a new map object based on the given CompountTag derrived from reading a valid .dat map
	 * file using {@link BinaryNbtHelpers#read(Path, CompressionType)} with the GZip compression
	 *
	 * @param root
	 */
	public FilledMap(CompoundTag root) throws IOException {
		this.root = root;
		readData();
	}
	
	private void readData() throws IOException {
		CompoundTag data = root.getCompoundTag(DATA_TAG);
		if(data == null){
			throw new IOException("unable to parse data tag");
		}
		
		if(data.getListTag(BANNERS_TAG) != null){
			for(CompoundTag banner : data.getListTag(BANNERS_TAG).asCompoundTagList()){
				FilledMapIconData mapIconData = new FilledMapIconData(banner.getString(NAME_TAG),
						banner.getString(COLOR_TAG),
						parsePos(banner.getCompoundTag(POS_TAG)));
				this.banners.add(mapIconData);
			}
		}
		
		if(data.getListTag(FRAMES_TAG) != null){
			for(CompoundTag frame : data.getListTag(FRAMES_TAG).asCompoundTagList()){
				FilledMapFrame mapFrame = new FilledMapFrame(frame.getInt(ENTITY_ID_TAG), frame.getInt(ROTATION_TAG), parsePos(frame.getCompoundTag(POS_TAG)));
				this.frames.add(mapFrame);
			}
		}
		
		dataVersion = root.getInt(DATA_VERSION_TAG);
		locked = data.getBoolean(LOCKED_TAG);
		scale = FilledMapScale.byId(data.getByte(SCALE_TAG));
		trackingPosition = data.getBoolean(TRACKING_POSITION_TAG);
		unlimitedTracking = data.getBoolean(UNLIMITED_TRACKING_TAG);
		xCenter = data.getInt(X_CENTER_TAG);
		zCenter = data.getInt(Z_CENTER_TAG);
		if(data.get(DIMENSION_TAG) != null && data.get(DIMENSION_TAG).getID() == IntTag.ID){
			dimension = FilledMapDimension.byId(data.getInt(DIMENSION_TAG));
		} else if(data.get(DIMENSION_TAG) != null && data.get(DIMENSION_TAG).getID() == StringTag.ID){
			dimension = FilledMapDimension.byTextId(data.getString(DIMENSION_TAG));
		}
		imageData = data.getByteArray(COLORS_TAG);
		if(imageData.length != IMAGE_WIDTH * IMAGE_HEIGHT){
			imageData = new byte[IMAGE_WIDTH * IMAGE_HEIGHT];
		}
	}
	
	/**
	 * Saves the current MapData to a png,
	 *
	 * @param saveTo a path or png file, if the path is a directory appends the current mapDatas name
	 * as the new pngs name.
	 */
	public void saveAsPng(Path saveTo) {
		if(saveTo == null){
			System.out.println("File is null, cannot save PNG.");
			return;
		}
		
		if(Files.exists(saveTo)){
			System.out.println("File already exists, cannot save PNG.");
			return;
		}
		
		if(Files.isDirectory(saveTo)){
			if(mapId == -1){
				throw new RuntimeException("Provided Save location is a folder, and no valid mapId is give, unable to determine " +
										   "name. Either provide a file Path or supply a mapId");
			}
			saveTo = saveTo.resolve("map_%d.dat".formatted(mapId));
		}
		
		if(imageData == null || imageData.length != IMAGE_WIDTH * IMAGE_HEIGHT){
			System.out.println("Invalid image data");
			System.out.println("Expected length: " + IMAGE_WIDTH * IMAGE_HEIGHT + " Actual: " + (imageData == null ? 0 : imageData.length));
			System.out.println("Data: " + this);
			return;
		}
		
		try{
			BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			
			for(int i = 0; i < imageData.length; i++){
				int x = i % IMAGE_WIDTH;
				int y = i / IMAGE_WIDTH;
				Color color = FilledMapColor.getJavaColor(imageData[i] & 0xFF);
				image.setRGB(x, y, color.getRGB());
			}
			
			try(OutputStream os = Files.newOutputStream(saveTo)){
				ImageIO.write(image, "png", os);
			}
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Clears all data from the MapData
	 */
	public void clear() {
		dataVersion = 0;
		imageData = null;
		xCenter = null;
		zCenter = null;
		scale = null;
		dimension = FilledMapDimension.OVERWORLD;
		trackingPosition = false;
		unlimitedTracking = false;
		locked = false;
		banners.clear();
		frames.clear();
		root.clear();
	}
	
	/**
	 * Writes the provided data to a given .dat file
	 *
	 * @param file a valid .dat file or a non existing file
	 * @throws IOException
	 */
	public void writeFile(Path file) throws IOException {
		CompoundTag data = root.getCompoundTag("data");
		root.putInt(DATA_VERSION_TAG, dataVersion);
		data.putBoolean(LOCKED_TAG, locked);
		data.putByte(SCALE_TAG, scale.getId());
		data.putBoolean("trackingPosition", trackingPosition);
		data.putBoolean(UNLIMITED_TRACKING_TAG, unlimitedTracking);
		data.putString("dimension", dimension.getTextID());
		data.putByteArray(COLORS_TAG, imageData);
		
		ListTag<CompoundTag> icons = new ListTag<>(CompoundTag.class);
		banners.forEach(b -> icons.add(b.toTag()));
		data.put("banners", icons);
		
		data.putInt(X_CENTER_TAG, xCenter);
		data.putInt(Z_CENTER_TAG, zCenter);
		
		BinaryNbtHelpers.write(root, file, CompressionType.NONE);
	}
	
	private IntPointXYZ parsePos(CompoundTag posTag) {
		if(posTag == null){
			return new IntPointXYZ(0, 0, 0);
		}
		int x = posTag.getInt("X");
		int y = posTag.getInt("Y");
		int z = posTag.getInt("Z");
		return new IntPointXYZ(x, y, z);
	}
	
	public CompoundTag getRoot() {
		return root;
	}
	
	public byte[] getImageData() {
		return imageData;
	}
	
	public Integer getxCenter() {
		return xCenter;
	}
	
	public Integer getzCenter() {
		return zCenter;
	}
	
	public FilledMapScale getScale() {
		return scale;
	}
	
	public FilledMapDimension getDimension() {
		return dimension;
	}
	
	public boolean isTrackingPosition() {
		return trackingPosition;
	}
	
	public boolean isUnlimitedTracking() {
		return unlimitedTracking;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public List<FilledMapIconData> getBanners() {
		return banners;
	}
	
	public List<FilledMapFrame> getFrames() {
		return frames;
	}
	
	public int getMapId() {
		return mapId;
	}
	
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}
	
	@Override
	public int getDataVersion() {
		return dataVersion;
	}
	
	@Override
	public void setDataVersion(int dataVersion) {
		this.dataVersion = dataVersion;
	}
}
