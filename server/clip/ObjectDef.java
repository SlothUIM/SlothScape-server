package server.clip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import server.util.Buffer;
//import src.Definitions.ObjectDef;


public final class ObjectDef {

	public static ObjectDef forID(int i) {
		
		if (i <= -1) {
			return null;
		}
		
		if (i > streamIndices.length)
			i = streamIndices.length - 2;

		/*if (i == 25913 || i == 25916 || i == 25917)
			i = 15552;*/

		for (int j = 0; j < 20; j++)
			if (cache[j].type == i)
				return cache[j];

		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDef objectDef = cache[cacheIndex];
		stream.currentOffset = streamIndices[i];
		objectDef.setDefaults();
		objectDef.readValues(stream);
		objectDef.type = i;
		switch (i) {

		case 1560:
			objectDef.solid = true;
			break;
			case 22652:
			case 22653:
			case 22654:
			case 22655:
				objectDef.solid = false;
				break;

	
		}
		
		return objectDef;
	}


	private void setDefaults() {
		modelIds = null;
		modelTypes = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		// originalTexture = null;
		// modifiedTexture = null;
		width = 1;
		length = 1;
		solid = true;
		impenetrable = true;
		hasActions = false;
		contouredGround = false;
		delaysShading = false;
		occludes = false;
		animation = -1;
		decorDisplacement = 16;
		ambientLighting = 0;
		lightDiffusion = 0;
		actions = null;
		mapFunction = -1;
		mapscene = -1;
		inverted = false;
		castsShadow = true;
		scaleX = 128;
		scaleY = 128;
		scaleZ = 128;
		surroundings = 0;
		translateX = 0;
		translateY = 0;
		translateZ = 0;
		obstructsGround = false;
		hollow = false;
		supportItems = -1;
		varbit = -1;
		varp = -1;
		morphisms = null;
		
		this.modifiedTexture = null;
		this.originalTexture = null;
	}
	public static int totalObjects;

	public static void unpackConfig() throws IOException {
		stream = new Buffer(Files.readAllBytes(new File("./Data/world/object/loc.dat").toPath()));
		Buffer stream = new Buffer(Files.readAllBytes(new File("./Data/world/object/loc.idx").toPath()));
		 int totalObjects = stream.readUnsignedWord();

			System.out.println(String.format("Loaded: %d objects", totalObjects));

	        streamIndices = new int[totalObjects];

	        int offset = 2; // initial offset, likely header size or starting index
	        for (int objId = 0; objId < totalObjects; objId++) {
	            streamIndices[objId] = offset;
	            offset += stream.readUnsignedWord();
	        }

	        cache = new ObjectDef[20];
	        
	        for (int i = 0; i < 20; i++) {
	            cache[i] = new ObjectDef();
	        }
	    }


	public void readValues(Buffer stream) {
        while(true) {
			int type;
			type = stream.readUnsignedByte();
			if (type == 0)
				break;
			if (type == 1) {
				int len = stream.readUnsignedByte();
				if (len > 0) {
					if (modelIds == null || lowMem) {
						modelTypes = new int[len];
						modelIds = new int[len];
						for (int k1 = 0; k1 < len; k1++) {
							modelIds[k1] = stream.readUnsignedWord();
							modelTypes[k1] = stream.readUnsignedByte();
						}
					} else {
						stream.currentOffset += len * 3;
					}
				}
			} else if (type == 2)
				name = stream.readString();
			else if (type == 3)
				description = stream.readString();
			else if (type == 5) {
				int len = stream.readUnsignedByte();
				if (len > 0) {
					if (modelIds == null) {
						modelTypes = null;
						modelIds = new int[len];
						for (int l1 = 0; l1 < len; l1++)
							modelIds[l1] = stream.readUnsignedWord();
					} else {
						stream.currentOffset += len * 2;
					}
				}
			} else if (type == 14)
				width = stream.readUnsignedByte();
			else if (type == 15)
				length = stream.readUnsignedByte();
			else if (type == 17)
				solid = false;
			else if (type == 18)
				impenetrable = false;
			else if (type == 19)
				hasActions = (stream.readUnsignedByte() == 1);
			else if (type == 21)
				contouredGround = true;
			else if (type == 22)
				delaysShading = true;
			else if (type == 23)
				occludes = true;
			else if (type == 24) { // Object Animations
				animation = stream.readUnsignedWord();
				if (animation == 65535)
					animation = -1;
			} else if (type == 28)
				decorDisplacement = stream.readUnsignedByte();
			else if (type == 29)
				ambientLighting = stream.readSignedByte();
			else if (type == 39)
				lightDiffusion = stream.readSignedByte();
			else if (type >= 30 && type < 35) {
				if (actions == null)
					actions = new String[5];
				actions[type - 30] = stream.readString();
				if (actions[type - 30].equalsIgnoreCase("hidden"))
					actions[type - 30] = null;
			} else if (type == 40) {
				int i1 = stream.readUnsignedByte();
				modifiedModelColors = new int[i1];
				originalModelColors = new int[i1];
				for (int i2 = 0; i2 < i1; i2++) {
					modifiedModelColors[i2] = stream.readUnsignedWord();
					originalModelColors[i2] = stream.readUnsignedWord();
				}
			} else if (type == 41) {
				int i1 = stream.readUnsignedByte();
				originalTexture = new short[i1];
				modifiedTexture = new short[i1];
				for (int i2 = 0; i2 < i1; i2++) {
					originalTexture[i2] = (short) stream.readUnsignedWord();
					modifiedTexture[i2] = (short) stream.readUnsignedWord();
				}
			} //else if (type == 60)
				//mapFunction = stream.readUnsignedWord();
			else if (type == 62)
				inverted = true;
			else if (type == 64)
				castsShadow = false;
			else if (type == 65)
				scaleX = stream.readUnsignedWord();
			else if (type == 66)
				scaleY = stream.readUnsignedWord();
			else if (type == 67)
				scaleZ = stream.readUnsignedWord();
			else if (type == 68)
				mapscene = stream.readUnsignedWord();
			else if (type == 69)
				surroundings = stream.readUnsignedByte();
			else if (type == 70)
				translateX = stream.readSignedWord();
			else if (type == 71)
				translateY = stream.readSignedWord();
			else if (type == 72)
				translateZ = stream.readSignedWord();
			else if (type == 73)
				obstructsGround = true;
			else if (type == 74)
				hollow = true;
			else if (type == 75)
				supportItems = stream.readUnsignedByte();
			else if (type == 77 || type == 92) {
				varbit = stream.readUnsignedWord();
				if (varbit == 65535)
					varbit = -1;
				
				varp = stream.readUnsignedWord();
				if (varp == 65535)
					varp = -1;
				
				int var3 = -1;
				if(type == 92) {
					var3 = stream.readUnsignedWord();
					if(var3 == 65535)
						var3 = -1;
				}
				
				
				int count = stream.readUnsignedByte();
				morphisms = new int[count + 2];
				for (int j2 = 0; j2 <= count; j2++) {
					morphisms[j2] = stream.readUnsignedWord();
					if (morphisms[j2] == 65535)
						morphisms[j2] = -1;
				}
				morphisms[count + 1] = var3;
				
			} else if(type == 78) {//TODO Figure out what these do in OSRS
				//First short = ambient sound
				stream.readUnsignedWord();
				stream.readUnsignedByte();
			} else if(type == 79) {
				stream.currentOffset += 5;
				int len = stream.readSignedByte();
				stream.currentOffset += (len * 2);
			} else if(type == 81) {
				stream.readUnsignedByte();
			} else if(type == 82) {
				stream.readUnsignedWord();
			}
		}
		if (name != "null" && name != null) {
			hasActions = modelIds != null && (modelTypes == null || modelTypes[0] == 10);
			if (actions != null)
				hasActions = true;
		}
		if (hollow) {
			solid = false;
			impenetrable = false;
		}
		if (supportItems == -1)
			supportItems = solid ? 1 : 0;
	}

	private ObjectDef() {
		type = -1;
	}

	private short[] originalTexture;
	private short[] modifiedTexture;
	public boolean obstructsGround;
	private byte lightDiffusion;
	private byte ambientLighting;
	private int translateX;
	public String name;
	public int scaleZ;
	public int width;
	private int translateY;
	public int mapFunction;
	private int[] originalModelColors;
	public int scaleX;
	public int varp;
	private boolean inverted;
	public static boolean lowMem;
	private static Buffer stream;
	public int type;
	public static int[] streamIndices;
	public boolean impenetrable;
	public int mapscene;
	public int morphisms[];
	public int supportItems;
	public int length;
	public boolean contouredGround;
	public boolean occludes;
	private boolean hollow;
	public boolean solid;
	public int surroundings;
	private boolean delaysShading;
	private static int cacheIndex;
	public int scaleY;
	public int[] modelIds;
	public int varbit;
	public int decorDisplacement;
	private int[] modelTypes;
	public String description;
	public boolean hasActions;
	public boolean castsShadow;
	public int animation;
	private static ObjectDef[] cache;
	private int translateZ;
	private int[] modifiedModelColors;
	public String actions[];
}
