/**
 * 
 */
package server.util.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * @author ReverendDread
 * Aug 4, 2019
 */
public class GZIPUtil {
	
	 public static byte[] decompress(byte[] compressedData) throws IOException {
	        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
	             GZIPInputStream gzipIn = new GZIPInputStream(bais);
	             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
	            byte[] buffer = new byte[4096];
	            int len;
	            while ((len = gzipIn.read(buffer)) > 0) {
	                baos.write(buffer, 0, len);
	            }
	            return baos.toByteArray();
	        }
	    }
	
}
