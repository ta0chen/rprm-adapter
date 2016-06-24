package com.polycom.sqa.utils;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

public class SysUtil {
	
	private static Logger logger = Logger.getLogger("sys");
	
	public static void avoidDuplicateRun() {
		try {
			int MAGIC_PORT = 9999; 
			ServerSocket magic_socket;
			magic_socket = new ServerSocket(MAGIC_PORT, 0, InetAddress.getByAddress(new byte[] {127,0,0,1}));
		} catch(BindException e) {
			System.err.println("An instance of the program is already running.");
			System.exit(1);
		} catch(IOException e) {
			System.err.println("Exception in avoidDuplicateRun()\n" + e);
			e.printStackTrace();
			System.exit(2);
		}
	}	
	
	private static String getIpAddrFromSys() {
		try {
			
			String dataRtn;
			
			InetAddress thisIP = InetAddress.getLocalHost();
			dataRtn = thisIP.getHostAddress();
			return dataRtn;
		} catch(Exception e) {
			logger.error("Exception in <getIpAddrFromSys>\n" + e);
			return null;
		}
	}
	
	/*
	 *   get the ip addr of Test Proxy
	 *   SUT will connect test proxy via this ip
	 *   return null if failed to get a valid ip
	 */
	public static String getIpAddr() {
		
		try {
			String ip = getIpAddrFromSys();
			return ip;
		} catch(Exception e) {
			logger.error("Exception in <getIpAddr>.\n" + e);
			return null;
		}
	}

}
