package io.github.ensgijs.nbt.dat.map;

import static io.github.ensgijs.nbt.dat.map.FilledMap.COMPRESSION_TYPE;
import static io.github.ensgijs.nbt.dat.map.FilledMap.IMAGE_HEIGHT;
import static io.github.ensgijs.nbt.dat.map.FilledMap.IMAGE_WIDTH;
import io.github.ensgijs.nbt.io.BinaryNbtHelpers;
import io.github.ensgijs.nbt.io.NamedTag;
import io.github.ensgijs.nbt.tag.CompoundTag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Helper class to create and access Values from a Dat Map File Format
 */
public class FilledMapFileHelper{
	
	private static final Pattern MAP_NAME_PATTER = Pattern.compile("map_[0-9]+\\.dat");
	public static Predicate<Path> MAP_NAME_FILTER = p -> MAP_NAME_PATTER.matcher(p.getFileName().toString()).matches();
	
	public static void convertToPng(Path sourceDir, Path outputDir, Predicate<Path> fileFilter) throws IOException {
		fromDirectory(sourceDir, fileFilter).entrySet().forEach(m -> (m.getValue()).saveAsPng(outputDir.resolve(m.getKey()
																												 .getFileName()
																												 .toString()
																												 .replace(".dat", ".png"))));
	}
	
	/**
	 * Converts all .dat map files in a direcotry to pngs, given the provided output dir, they keep
	 * their original File Names, this one filters files on minecrafts specified
	 * {@link #MAP_NAME_FILTER}
	 *
	 * @param sourceDir the dir to check for files
	 * @param outputDir the dir to place the outputs in
	 * @throws IOException
	 */
	public static void convertToPng(Path sourceDir, Path outputDir) throws IOException {
		convertToPng(sourceDir, outputDir, MAP_NAME_FILTER);
	}
	
	/**
	 * Gets all files matching the required filter
	 *
	 * @throws IOException
	 */
	public static Set<Path> getFilesFromDirectory(Path directory, Predicate<Path> fileFilter) throws IOException {
		return Files.list(directory).filter(fileFilter).collect(Collectors.toSet());
	}
	
	/**
	 * Returns a set of MapData objects for every valid file in the provided directory
	 *
	 * @param directory
	 * @return
	 */
	public static Map<Path, FilledMap> fromDirectory(Path directory) throws IOException {
		return fromDirectory(directory, MAP_NAME_FILTER);
	}
	
	/**
	 * Returns a set of MapData objects for every valid file in the provided directory
	 *
	 * @param directory
	 * @param fileFilter
	 * @return
	 */
	public static Map<Path, FilledMap> fromDirectory(Path directory, Predicate<Path> fileFilter) throws IOException {
		return Files.list(directory).filter(fileFilter).parallel().map(p -> {
						try{
							return java.util.Map.entry(p, new FilledMap(BinaryNbtHelpers.readCompound(p, COMPRESSION_TYPE)));
						} catch(Exception e){
							System.err.println(e.getMessage());
							return null; // must return something
						}
					}).filter(Objects::nonNull) // filter out failed entries
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	/**
	 * An optimized method to read out the pixels of a provided map file without instantiating the
	 * full class as {@link FilledMap#FilledMap(CompoundTag)}
	 *
	 * @param file the path to the file
	 * @return an array of pixels in the length of {@link FilledMap#IMAGE_HEIGHT} * {@link FilledMap#IMAGE_WIDTH}
	 * @throws IOException
	 */
	public static byte[] getPixelsFromFile(Path file) throws IOException {
		NamedTag tag = BinaryNbtHelpers.read(file, COMPRESSION_TYPE);
		CompoundTag root = (CompoundTag) tag.getTag();
		CompoundTag data = root.getCompoundTag("data");
		
		byte[] imageData = data.getByteArray("colors");
		if(imageData.length != IMAGE_WIDTH * IMAGE_HEIGHT){
			imageData = new byte[IMAGE_WIDTH * IMAGE_HEIGHT];
		}
		return imageData;
	}
	
}
