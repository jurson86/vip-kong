package com.tuandai.ms.ar.utils;


import java.util.UUID;



public class TokenGenerator {
	public static String generateValue() {
		return UUID.randomUUID().toString();
	}
}
