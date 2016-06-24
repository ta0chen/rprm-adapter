package com.polycom.sqa.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class StringUtil {

    private static Logger logger = Logger.getLogger("sys");

    /*
     * extrace cmd name from cmd line
     */
    public static String getCmdName(final String cmd) {

        final Pattern pattern = Pattern.compile("(.+?)(\\s|$)");
        final Matcher m = pattern.matcher(cmd);

        if (!m.find()) {
            logger.error("failed to extract the command name.");
            return null;
        }

        return m.group(1);
    }

    public static String getHexString(final byte[] b) throws Exception {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1)
                    + ",";
        }
        return result;
    }

    public static boolean isPureAscii(final String v) {
        final CharsetEncoder asciiEncoder = Charset.forName("US-ASCII")
                .newEncoder(); // or "ISO-8859-1" for ISO Latin 1
        return asciiEncoder.canEncode(v);
    }

    // cmd line to xml
    public static String parseCmd(final String cmd) {

        try {
            String delimeter = "\\s+";
            final String[] tmpList = cmd.split(delimeter);
            final ArrayList<String> cmdList = new ArrayList<String>();
            final HashMap<String, String> para = new HashMap<String, String>();

            final String cmdName = tmpList[0];
            if (tmpList.length == 1) {
                final String dataRtn = SimpleXmlUtil.xmlCmdGen(cmdName);
                return dataRtn;
            }

            for (int i = 1; i < tmpList.length; i++) {
                cmdList.add(tmpList[i]);
            }

            for (final String item : cmdList) {

                String key;
                String value;
                delimeter = "=";

                final String[] kvp = item.split(delimeter, 2);
                key = kvp[0];
                value = kvp[1];

                para.put(key, value);
            }

            final String dataRtn = SimpleXmlUtil.xmlCmdGen(cmdName, para);

            return dataRtn;
        } catch (final Exception e) {
            System.err.println("Exception in <parseCmd>");
            return null;
        }
    }

    public static boolean readFile2Str(final String filename,
            final StringBuffer outStr) {
        logger.debug("filename: " + filename);
        // logger.debug("outStr:\n" + outStr);
        boolean rtn = true;

        try {
            outStr.append(new Scanner(new File(filename)).useDelimiter("\\Z")
                          .next());
        } catch (final FileNotFoundException e) {
            rtn = false;
            logger.error("File is not found " + e);
        } finally {
            // close file ???
        }
        if (filename.contains("XmaWsFiles")) {
            return rtn;
        }
        // logger.debug("outStr:\n"+outStr);
        return rtn;
    }

}
