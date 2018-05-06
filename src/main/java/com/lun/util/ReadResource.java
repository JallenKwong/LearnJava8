package com.lun.util;

import java.io.File;
import java.io.InputStream;

public class ReadResource {
	
	public static InputStream getInputStream(String path) {
		return ReadResource.class.getResourceAsStream(path);
	}
	
	public static File getFile(String path) {
		String filePath = ReadResource.class.getClassLoader().getResource(path).getPath();
		return new File(filePath);
	}
	
	public static void main(String[] args) {
		System.out.println(getFile("hello.txt").getPath());
	}
	
}
