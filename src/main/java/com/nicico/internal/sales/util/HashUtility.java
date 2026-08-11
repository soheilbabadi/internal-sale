package com.nicico.internal.sales.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtility {
	private static final String SHA_256 = "SHA-256";

	private HashUtility() {
		// Private constructor to prevent instantiation
	}

	public static String calculateSHA256(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(SHA_256);
		byte[] hashBytes = digest.digest(data);
		return bytesToHex(hashBytes);
	}

	public static String generateSHA256(String input) {
		if (input == null) {
			return null;
		}
		try {
			return calculateSHA256(input.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}