package com.scs.voxlib.chunk;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public final class VLChunkFactory {

	public static final String MAIN = "MAIN"; 
	public static final String PACK = "PACK"; 
	public static final String SIZE = "SIZE"; 
	public static final String XYZI = "XYZI"; 
	public static final String RGBA = "RGBA"; 
	public static final String MATL = "MATL"; 
	public static final String MATT = "MATT"; 
	public static final String nSHP = "nSHP"; 
	public static final String nTRN = "nTRN"; 
	public static final String nGRP = "nGRP"; 
	public static final String LAYR = "LAYR"; 
	
	public static Set<String> supportedTypes = new HashSet<>();
	static {
		supportedTypes.add(MAIN);
		supportedTypes.add(PACK);
		supportedTypes.add(SIZE);
		supportedTypes.add(XYZI);
		supportedTypes.add(RGBA);
		supportedTypes.add(MATL);
		supportedTypes.add(nSHP);
		supportedTypes.add(nTRN);
		supportedTypes.add(nGRP);
		supportedTypes.add(LAYR);
	}

	public static VLVoxChunk createChunk(String type, InputStream stream, InputStream childrenStream) throws IOException {
		VLVoxChunk chunk = null;

		//Settings.p("Reading type " + type);
		
		switch (type) {
		case MAIN:
			chunk = VLVoxRootChunk.read(stream, childrenStream);
			break;
		case PACK:
			chunk = VLVoxPackChunk.read(stream);
			break;
		case SIZE:
			chunk = VLVoxSizeChunk.read(stream);
			break;
		case XYZI:
			chunk = VLVoxXYZIChunk.read(stream);
			break;
		case RGBA:
			chunk = VLVoxRGBAChunk.read(stream);
			break;
		case MATT: // Obsolete
			chunk = VLVoxMATTChunk.read(stream);
			break;
		case MATL:
			chunk = VLVoxMATLChunk.read(stream);
			break;
			
		case nSHP: // Shape Node Chunk
			chunk = VLVoxShapeChunk.read(stream);
			break;
			
		case nTRN: // Transform Node Chunk
			chunk = VLVoxTransformChunk.read(stream);
			break;

		case nGRP: // Group Node Chunk
			chunk = VLVoxGroupChunk.read(stream);
			break;

		case LAYR:
			chunk = VLVoxLayerChunk.read(stream);
			break;

			// These chunks are unsupported and simply skipped.
		case "rOBJ":
		case "rCAM":
		case "NOTE":
			chunk = new VLVoxDummyChunk(type);
			break;
			
		default:
			System.out.println("Ignoring " + type);
		}

		return chunk;
	}
}
