package com.polycom.sqa.utils;

import java.util.List;

import org.simpleframework.xml.*;

@Root
public class WebDriverPath {
	
	@ElementList(inline=true)
	private static String path;
	
	public static String getWebDriverPath() {
		System.out.println("path value is :"+ path);	
		return path;
	}
	
	public static void setWebDriverPath(String name) {
		path = name;

	}
}
