package com.polycom.sqa.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
public class FileUtils {
    public static void main(String[] args) {
    
        List<String> lstFileNames = new ArrayList<String>();
        lstFileNames.add("D:\\ftp\\1.txt");
        lstFileNames.add("D:\\ftp\\2.txt");
        lstFileNames.add("D:\\ftp\\3.txt");
    
        String strOutputZipFileName = "D:\\ftp\\test";
        FileUtils fileUtils = new FileUtils();
        fileUtils.zipFile(lstFileNames, strOutputZipFileName);
    }
    
    public void zipFile(List<String> lstFileNames, String strOutputZipFileName){
	    try {
	      FileOutputStream f = new FileOutputStream(strOutputZipFileName + ".zip");
	      CheckedOutputStream ch = new CheckedOutputStream(f, new CRC32());
	      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(ch));
	      for (String strFileName : lstFileNames) {
	        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(strFileName),"ISO8859_1"));
	        int c;
	        out.putNextEntry(new ZipEntry(strFileName.substring(strFileName.lastIndexOf(File.separator)+1)));
	        while ((c = in.read()) != -1)
	          out.write(c);
	        in.close();
	      }
	      out.close();
	    } catch (FileNotFoundException e) {
	          e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	       e.printStackTrace();
	    }
	
    }
    
    public void zipFileList(List<String> lstFileNames, File strOutputZipFileName){
	    try {
	      FileOutputStream f = new FileOutputStream(strOutputZipFileName );
	      CheckedOutputStream ch = new CheckedOutputStream(f, new CRC32());
	      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(ch));
	      for (String strFileName : lstFileNames) {
	        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(strFileName),"ISO8859_1"));
	        int c;
	        out.putNextEntry(new ZipEntry(strFileName.substring(strFileName.lastIndexOf(File.separator)+1)));
	        while ((c = in.read()) != -1)
	          out.write(c);
	        in.close();
	      }
	      out.close();
	    } catch (FileNotFoundException e) {
	          e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	       e.printStackTrace();
	    }
    }    
    
}