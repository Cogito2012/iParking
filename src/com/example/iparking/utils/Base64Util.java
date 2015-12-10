package com.example.iparking.utils;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


public class Base64Util {
	// �� s ���� BASE64 ���� 
	public static String getBASE64(String s) { 
		if (s == null) return null; 
		return (new BASE64Encoder()).encode( s.getBytes() ); 
	} 

	// �� BASE64 ������ַ��� s ���н��� 
	public static String getFromBASE64(String s) { 
		if (s == null) return null; 
		BASE64Decoder decoder = new BASE64Decoder(); 
		try { 
			byte[] b = decoder.decodeBuffer(s); 
			return new String(b); 
			} catch (Exception e) { 
				return null; 
				} 
		}
}
