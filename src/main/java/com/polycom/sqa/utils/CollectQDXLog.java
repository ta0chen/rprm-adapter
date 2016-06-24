package com.polycom.sqa.utils;

import java.io.File;
import java.net.URL;

public class CollectQDXLog {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File logFile = new File("D:\\ftp\\log.txt");
    	try {
			URL url = new URL("http://172.21.119.117/a_getlog.cgi?name=messages");
			org.apache.commons.io.FileUtils.copyURLToFile(url, logFile);
		} catch(Exception e) {
			System.out.println("Exception once trying to get remote file.\n" + e);
			e.printStackTrace();
		}

	}

}
