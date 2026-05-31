package server.world.map;
//import src.Stream;
//import src.StreamLoader;
//import src.Varp;

// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public final class VarBit {

	public static void unpackConfig() {
		ByteStream stream = new ByteStream(getBuffer("varbit.dat"));
		int cacheSize = stream.getUShort();
		System.out.println(String.format("Loaded: %d varbits", cacheSize));
		if (cache == null)
			cache = new VarBit[cacheSize];
		for (int j = 0; j < cacheSize; j++) {
			if (cache[j] == null)
				cache[j] = new VarBit();
			cache[j].readValues(stream);
			if (cache[j].aBoolean651)
				Varp.cache[cache[j].setting].aBoolean713 = true;
		}
		if (stream.getOffset() != stream.length())
			System.out.println("varbit load mismatch");
	}
	public static byte[] getBuffer(String s) {
		try {
			java.io.File f = new java.io.File("./Data/world/object/" + s);
			if (!f.exists())
				return null;
			byte[] buffer = new byte[(int) f.length()];
			java.io.DataInputStream dis = new java.io.DataInputStream(
					new java.io.FileInputStream(f));
			dis.readFully(buffer);
			dis.close();
			return buffer;
		} catch (Exception e) {
		}
		return null;
	}
	private void readValues(ByteStream stream) {
		int opcode = stream.getUByte();

		if (opcode == 0) {
			return;
		} else if (opcode == 1) {
			setting = stream.getUShort();
			low = stream.getUByte();
			high = stream.getUByte();
		} else {
			System.out.println(opcode);
		}
}

	//low = stream.getUByte();
	private VarBit() {
		aBoolean651 = false;
	}

	public static VarBit cache[];
	public int setting;
	public int low;
	public int high;
	private boolean aBoolean651;
}
