package com.enation.framework.cache.redis;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ByteUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static final InputStream byteTostream(byte[] buf) {
		return new ByteArrayInputStream(buf);
	}

	public static final byte[] steamToByte(InputStream inStream)
			throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}

	public static String getBase64Content(InputStream inputStream)
			throws IOException {

		byte[] output = steamToByte(inputStream);
		BASE64Encoder encoder = new BASE64Encoder();
		String outstr = encoder.encode(output);
		System.out.println(outstr);
		// saveBase64strToFile(outstr);
		return outstr;
	}

	public static byte[] getByteByBase64str(String base64str) {
		if (base64str == null) {
			return null;
		}
		byte[] b = null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			b = decoder.decodeBuffer(base64str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	public static void saveBase64strToFile(String base64str) {
		if (base64str == null) {
			return;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(base64str);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			String imgFilePath = "c:/base.jpg";// ����ɵ�ͼƬ
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static InputStream convertStringToStream(String str)
			throws UnsupportedEncodingException {
		InputStream in_withcode = new ByteArrayInputStream(
				str.getBytes("UTF-8"));
		return in_withcode;
	}

	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static String getBASE64(String s) {
		if (s == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
	}

	 
	public static String getFromBASE64(String s) {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}
	
 
	public static String getBase64Content(byte[] output) throws IOException {
		BASE64Encoder encoder = new BASE64Encoder();
		String outstr = encoder.encode(output);
		return outstr;
	}

	 
	public static byte[] base64strToBytes(String base64str) {
		if (base64str == null) {
			return null;
		}
		byte[] b = null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			b = decoder.decodeBuffer(base64str);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}

	
	
}
