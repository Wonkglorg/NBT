package io.github.ensgijs.nbt.dat.map;

import static io.github.ensgijs.nbt.dat.map.FilledMap.COMPRESSION_TYPE;
import io.github.ensgijs.nbt.io.BinaryNbtHelpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class to create and access Values from a Dat Map File Format
 */
public class FilledMapFileHelper{
	
	private static final Pattern MAP_NAME_PATTER = Pattern.compile("map_[0-9]+\\.dat");
	public final static Predicate<Path> MAP_NAME_FILTER = p -> MAP_NAME_PATTER.matcher(p.getFileName().toString()).matches();
	
	public static void convertToPng(Path sourceDir, Path outputDir, Predicate<Path> fileFilter) throws IOException {
		getFilesFromDirectory(sourceDir, fileFilter)
				.parallel()
				.forEach(m -> FilledMap.saveDataAsPng(outputDir
								.resolve(m.getFileName()
										  .toString()
										  .replace(".dat", ".png")),
				FilledMap.getPixelsFromFile(m),
				-1));
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
	public static Stream<Path> getFilesFromDirectory(Path directory, Predicate<Path> fileFilter) throws IOException {
		return Files.list(directory).filter(fileFilter);
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
		return getFilesFromDirectory(directory, fileFilter).parallel().map(p -> {
			try{
				return java.util.Map.entry(p, new FilledMap(BinaryNbtHelpers.readCompound(p, COMPRESSION_TYPE)));
			} catch(Exception e){
				System.err.println(e.getMessage());
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
