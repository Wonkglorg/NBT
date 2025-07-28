package io.github.ensgijs.nbt.io;

import io.github.ensgijs.nbt.tag.Tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Utilities for reading and writing {@link Tag}'s to and from binary NBT data. */
public final class BinaryNbtHelpers {
	private BinaryNbtHelpers() {
	}

	// <editor-fold desc="Big Endian read/write (MC Java)">
	public static Path write(NamedTag tag, Path file, CompressionType compression)
			throws IOException {
		try (OutputStream fos = Files.newOutputStream(file)) {
			new BinaryNbtSerializer(compression).toStream(tag, fos);
		}
		return file;
	}

	public static Path write(NamedTag tag, String file, CompressionType compression)
			throws IOException {
		return write(tag, Path.of(file), compression);
	}
	
	public static Path write(Tag<?> tag, String file, CompressionType compression)
			throws IOException {
		return write(new NamedTag(null, tag), file, compression);
	}

	public static Path write(Tag<?> tag, Path path, CompressionType compression) throws IOException {
		return write(new NamedTag(null, tag), path, compression);
	}

	public static NamedTag read(Path file, CompressionType compression) throws IOException {
		try (InputStream fis = Files.newInputStream(file)) {
			return new BinaryNbtDeserializer(compression).fromStream(fis);
		}
	}

	public static NamedTag read(String file, CompressionType compression) throws IOException {
		return read(Path.of(file), compression);
	}


	/**
	 * Note that Paper's ItemStack#serializeAsBytes returns binary nbt data with
	 * {@link CompressionType#GZIP}.
	 */
	public static NamedTag deserializeBytes(byte[] bytes, CompressionType compression)
			throws IOException {
		return new BinaryNbtDeserializer(compression).fromStream(new ByteArrayInputStream(bytes));
	}

	/**
	 * Note that Paper's ItemStack#serializeAsBytes returns binary nbt data with
	 * {@link CompressionType#GZIP}.
	 */
	public static byte[] serializeAsBytes(Tag<?> tag, CompressionType compression)
			throws IOException {
		return serializeAsBytes(new NamedTag(null, tag), compression);
	}

	/**
	 * Note that Paper's ItemStack#serializeAsBytes returns binary nbt data with
	 * {@link CompressionType#GZIP}.
	 */
	public static byte[] serializeAsBytes(NamedTag tag, CompressionType compression)
			throws IOException {
		try (ByteArrayOutputStream fos = new ByteArrayOutputStream(1024)) {
			new BinaryNbtSerializer(compression).toStream(tag, fos);
			return fos.toByteArray();
		}
	}

	/**
	 * Note that Paper's ItemStack#serializeAsBytes returns binary nbt data with
	 * {@link CompressionType#GZIP}.
	 */
	public static NamedTag serializeAsBytes(byte[] bytes, CompressionType compression)
			throws IOException {
		return new BinaryNbtDeserializer(compression).fromStream(new ByteArrayInputStream(bytes));
	}
	// </editor-fold>

	// <editor-fold desc="Little Endian read/write (MC Bedrock)">
	public static Path writeLittleEndian(NamedTag tag, File file, CompressionType compression)
			throws IOException {
		try (FileOutputStream fos = new FileOutputStream(file)) {
			new BinaryNbtSerializer(compression, true).toStream(tag, fos);
		}
		return file.toPath();
	}

	public static Path writeLittleEndian(NamedTag tag, String file, CompressionType compression)
			throws IOException {
		return writeLittleEndian(tag, new File(file), compression);
	}

	public static Path writeLittleEndian(NamedTag tag, Path path, CompressionType compression)
			throws IOException {
		return writeLittleEndian(tag, path.toFile(), compression);
	}

	public static Path writeLittleEndian(Tag<?> tag, File file, CompressionType compression)
			throws IOException {
		return writeLittleEndian(new NamedTag(null, tag), file, compression);
	}

	public static Path writeLittleEndian(Tag<?> tag, String file, CompressionType compression)
			throws IOException {
		return writeLittleEndian(new NamedTag(null, tag), new File(file), compression);
	}

	public static Path writeLittleEndian(Tag<?> tag, Path path, CompressionType compression)
			throws IOException {
		return writeLittleEndian(new NamedTag(null, tag), path.toFile(), compression);
	}

	public static NamedTag readLittleEndian(File file, CompressionType compression)
			throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			return new BinaryNbtDeserializer(compression, true).fromStream(fis);
		}
	}

	public static NamedTag readLittleEndian(String file, CompressionType compression)
			throws IOException {
		return readLittleEndian(new File(file), compression);
	}

	public static NamedTag readLittleEndian(Path path, CompressionType compression)
			throws IOException {
		return readLittleEndian(path.toFile(), compression);
	}
	// </editor-fold>
}
