package server.world.map;

//import src.Stream;
//import src.StreamLoader;
//import src.Varp;

//Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
//Jad home page: http://www.kpdus.com/jad.html
//Decompiler options: packimports(3) 

public final class  Varp {

 public static void unpackConfig()
 {
		ByteStream stream = new ByteStream(getBuffer("varp.dat"));
	// ByteStream stream = new Stream(streamLoader.getDataForName("varp.dat"));
     anInt702 = 0;
     int cacheSize = stream.getUShort();
		System.out.println(String.format("Loaded: %d varps", cacheSize));
     if(cache == null)
         cache = new Varp[cacheSize];
     if(anIntArray703 == null)
         anIntArray703 = new int[cacheSize];
     for(int j = 0; j < cacheSize; j++)
     {
         if(cache[j] == null)
             cache[j] = new Varp();
         cache[j].readValues(stream, j);
     }
     if(stream.getOffset() != stream.length())
         System.out.println("varptype load mismatch");
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
 private void readValues(ByteStream stream, int i)
 {
     do
     {
         int j = stream.getUByte();
         if(j == 0)
             return;
         int dummy;
         if(j == 1)
              stream.getUByte();
         else
         if(j == 2)
             stream.getUByte();
         else
         if(j == 3)
             anIntArray703[anInt702++] = i;
         else
         if(j == 4)
             dummy = 2;
         else
         if(j == 5)
             anInt709 = stream.getUShort();
         else
         if(j == 6)
             dummy = 2;
         else
         if(j == 7)
             stream.getInt();
         else
         if(j == 8)
             aBoolean713 = true;
          else
         if(j == 10)
              stream.readString();
         else
         if(j == 11)
             aBoolean713 = true;
         else
         if(j == 12)
             stream.getInt();
         else
         if(j == 13)
             dummy = 2;
         else
             System.out.println("Error unrecognised config code: " + j);
     } while(true);
 }

 private Varp()
 {
     aBoolean713 = false;
     anInt709 = 0;
 }

 public static Varp cache[];
 private static int anInt702;
 private static int[] anIntArray703;
 public int anInt709;
 public boolean aBoolean713;

}
