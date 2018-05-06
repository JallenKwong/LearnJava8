package com.lun.c01;

import java.io.File;
import java.io.FileFilter;

public class MethodArgument {

	public static void main(String[] args) {
		File[] hiddenFiles = new File(".").listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				return file.isHidden();
			}
		});
		
		
		//Java 8 的写法
		File[] hiddenFiles2 = new File(".").listFiles(File::isHidden);
	}

}
