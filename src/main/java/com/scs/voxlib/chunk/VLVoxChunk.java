package com.scs.voxlib.chunk;

import com.scs.voxlib.InvalidVoxException;
import com.scs.voxlib.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class VLVoxChunk {

	private final String type;

	public String getType() {
		return type;
	}

	public VLVoxChunk(String type) {
		this.type = type;
	}
	
	public static VLVoxChunk readChunk(InputStream stream) throws IOException {
		return readChunk(stream, null);
	}

	public static VLVoxChunk readChunk(InputStream stream, String expectedID) throws IOException {
		byte[] chunkID = new byte[4];
		int bytesRead = stream.read(chunkID);
		if (bytesRead != 4) {
			if (bytesRead == -1) {
				// There's no chunk here, this is fine.
				return null;
			}

			throw new InvalidVoxException("Incomplete chunk ID");
		}

		String id = new String(chunkID);

		if (expectedID != null && !expectedID.equals(id)) {
			throw new InvalidVoxException(expectedID + " chunk expected, got " + id);
		}

		int length = StreamUtils.readIntLE(stream);
		int childrenLength = StreamUtils.readIntLE(stream);

		byte[] chunkBytes = new byte[length];
		byte[] childrenChunkBytes = new byte[childrenLength];

		if (length > 0 && stream.read(chunkBytes) != length) {
			throw new InvalidVoxException("Chunk \"" + id + "\" is incomplete");
		}

		stream.read(childrenChunkBytes);

		try (ByteArrayInputStream chunkStream = new ByteArrayInputStream(chunkBytes);
				ByteArrayInputStream childrenStream = new ByteArrayInputStream(childrenChunkBytes)) {
			return VLChunkFactory.createChunk(id, chunkStream, childrenStream);
		}
	}

	public final void writeTo(OutputStream stream) throws IOException {
		try (
			var contentStream = new ByteArrayOutputStream();
			var childStream = new ByteArrayOutputStream();
		) {
			stream.write(type.getBytes(StandardCharsets.UTF_8));
			writeContent(contentStream);
			var contentBytes = contentStream.toByteArray();

			writeChildren(childStream);
			var childBytes = childStream.toByteArray();

			StreamUtils.writeIntLE(contentBytes.length, stream);
			StreamUtils.writeIntLE(childBytes.length, stream);
			stream.write(contentBytes);
			stream.write(childBytes);
		}
	}

	/** Write to the stream the content directly associated with this chunk. */
	protected void writeContent(OutputStream stream) throws IOException {}

	/** Write to the stream the content associated with this chunk's children. */
	protected void writeChildren(OutputStream stream) throws IOException {}
}
