package com.polycom.sqa.utils;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

class Base64OutputStream extends FilterOutputStream {
    private static char[] toBase64 =
        {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
        'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
        };

    private int col = 0;

    private int i = 0;

    private final int[] inbuf = new int[3];

    /**
		Constructs the stream filter
		@param out the stream to filter
     */
    public Base64OutputStream(final OutputStream out)
    {
        super(out);
    }
    @Override
    public void flush() throws IOException
    {
        if (i == 1)
        {
            super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
            super.write(toBase64[(inbuf[0] & 0x03) << 4]);
            super.write('=');
            super.write('=');
        }
        else if (i == 2)
        {
            super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
            super.write(toBase64[((inbuf[0] & 0x03) << 4) | ((inbuf[1] & 0xF0) >> 4)]);
            super.write(toBase64[(inbuf[1] & 0x0F) << 2]);
            super.write('=');
        }
    }
    @Override
    public void write(final int c) throws IOException
    {
        inbuf[i] = c;
        i++;
        if (i == 3)
        {
            super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
            super.write(toBase64[((inbuf[0] & 0x03) << 4) | ((inbuf[1] & 0xF0) >> 4)]);
            super.write(toBase64[((inbuf[1] & 0x0F) << 2) | ((inbuf[2] & 0xC0) >> 6)]);
            super.write(toBase64[inbuf[2] & 0x3F]);
            col += 4;
            i = 0;
            if (col >= 76)
            {
                super.write('\n');
                col = 0;
            }
        }
    }

}


public class HttpUtil {

    private static Logger logger = Logger.getLogger("HttpUtil");

    @SuppressWarnings("resource")
    public static String base64Encode(final String s) {
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        final Base64OutputStream out = new Base64OutputStream(bOut);
        try {
            out.write(s.getBytes());
            out.flush();
            //out.close();
        } catch (final IOException e) {
            logger.debug("IO exception: "+e);
        }
        return bOut.toString();
    }

    public static boolean rget(final String urlStr, final String postMsgStr, final StringBuffer outStr, final String userCredentials, String charsetStr, final String restMethod, final StringBuffer respHeader, final String contentType) {
        // logger.debug(" javax.net.ssl.trustStore: "+System.getProperty("javax.net.ssl.trustStore"));
        logger.debug(" urlStr: "+urlStr);
        logger.debug(" restMethod: "+restMethod);
        logger.debug(" postMsgStr:\n" + postMsgStr);
        logger.debug(" outStr: "+outStr);
        logger.debug(" userCredentials: "+userCredentials);
        logger.debug(" charsetStr: "+charsetStr);

        boolean rtn = true;
        HttpsURLConnection conn = null;

        try
        {
            String encodingCredential = "";
            if (!userCredentials.equals("")) {
                encodingCredential = base64Encode(userCredentials);
                logger.debug(" encodingCredential: "+encodingCredential);
            }

            if (charsetStr.equals("")) {
                charsetStr = "UTF-8";
            }

            CommonUtils.doTrustToAllCertificates();
            conn = (HttpsURLConnection) new URL(urlStr).openConnection();
            if(!contentType.equals("")) {
                if(restMethod.equals("POST")){
                    conn.setRequestProperty("Content-Type", contentType);
                } else if(restMethod.equals("GET")) {
                    conn.setRequestProperty("Accept", contentType);
                }
            }
            conn.setDoInput(true);
            conn.setRequestMethod(restMethod);
            conn.setUseCaches(false);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("Connection", "keep-alive");
            if ( ! userCredentials.equals("") ){
                conn.setRequestProperty("Authorization", "Basic " + encodingCredential);
            }
            if (!postMsgStr.equals("") ) {
                conn.setDoOutput(true);
                final PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(postMsgStr);
                out.close();
            }

            conn.connect();

            final Map<String, List<String>> headers = conn.getHeaderFields();
            for (final Map.Entry<String, List<String>> entry : headers.entrySet())
            {
                final String key = entry.getKey();
                for (final String value : entry.getValue())
                {
                    logger.debug(" " + key + ": " + value);
                    respHeader.append("\n" + key + ": " + value);
                }
            }

            logger.debug(" getContentType: " + conn.getContentType());
            logger.debug(" getContentLength: " + conn.getContentLength());
            logger.debug(" getContentEncoding: " + conn.getContentEncoding());
            logger.debug(" getDate: " + conn.getDate());
            logger.debug(" getExpiration: " + conn.getExpiration());
            logger.debug(" getLastModifed: " + conn.getLastModified());

            final String respCode =  "response code: " + conn.getResponseCode();
            final String respMsg = "response msg: " + conn.getResponseMessage();

            logger.debug(respCode);
            logger.debug(respMsg);
            respHeader.append("\n" + respCode);
            respHeader.append("\n" + respMsg + "\n");

            final Scanner input = new Scanner(conn.getInputStream());
            while (input.hasNextLine() ) {
                outStr.append( input.nextLine() );
            }
            input.close();
            conn.disconnect();

        } catch (final IOException e) {
            e.printStackTrace();
            rtn = false;
            final Scanner input = new Scanner(conn.getErrorStream());
            while (input.hasNextLine() ) {
                outStr.append( input.nextLine() );
            }
            input.close();
            conn.disconnect();
            logger.debug("IO exception: "+e);
        } catch (final NoSuchAlgorithmException e) {
            rtn = false;
            e.printStackTrace();
            logger.debug("NoSuchAlgorithmException: " + e.getMessage());
        } catch (final KeyManagementException e) {
            rtn = false;
            e.printStackTrace();
            logger.debug("KeyManagementException: " + e.getMessage());
        } finally {
            conn.disconnect();
        }

        return rtn;
    }

    @SuppressWarnings("resource")
    public static boolean wget(final String urlStr,
            final String postMsgStr,
            final StringBuffer outStr,
            final String userStr,
            final String pwdStr,
            String charsetStr,
            final String action) {
        logger.debug(" urlStr: "+urlStr);
        logger.debug(" postMsgStr:\n" + postMsgStr);
        logger.debug(" outStr: "+outStr);
        logger.debug(" userStr: "+userStr);
        logger.debug(" pwdStr: "+pwdStr);
        logger.debug(" charsetStr: "+charsetStr);
        URLConnection conn = null;
        boolean rtn = true;
        try
        {
            String encodingCredential = "";
            if (!userStr.equals("")) {
                encodingCredential = base64Encode(userStr + ":" + pwdStr);
            }

            if (charsetStr.equals("")) {
                charsetStr = "UTF-8";
            }

            CommonUtils.doTrustToAllCertificates();
            conn = new URL(urlStr).openConnection();

            conn = new URL(urlStr).openConnection();
            if (urlStr.substring(0, urlStr.indexOf(":"))
                    .equalsIgnoreCase("https")) {
                conn = (HttpsURLConnection) conn;
            }
                                    
            conn.setRequestProperty("Accept-Charset", charsetStr);
            conn.setRequestProperty("Content-Type", "text/xml;charset=" + charsetStr);
            conn.setRequestProperty("SOAPAction",
                                    "http://polycom.com/WebServices/web:"
                                            + action);
            conn.setReadTimeout(15000);
            if ( ! userStr.equals("") ) {
                conn.setRequestProperty("Authorization", "Basic " + encodingCredential);
            }

            if (!postMsgStr.equals("") ) {
                conn.setDoOutput(true);
                final PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(postMsgStr);
                out.close();
            }

            conn.connect();

            final Map<String, List<String>> headers = conn.getHeaderFields();
            for (final Map.Entry<String, List<String>> entry : headers.entrySet())
            {
                final String key = entry.getKey();
                for (final String value : entry.getValue()) {
                    logger.debug(" " + key + ": " + value);
                }
            }

            logger.debug(" getContentType: " + conn.getContentType());
            logger.debug(" getContentLength: " + conn.getContentLength());
            logger.debug(" getContentEncoding: " + conn.getContentEncoding());
            logger.debug(" getDate: " + conn.getDate());
            logger.debug(" getExpiration: " + conn.getExpiration());
            logger.debug(" getLastModifed: " + conn.getLastModified());


            final Scanner input = new Scanner(conn.getInputStream());
            while (input.hasNextLine() ) {
                outStr.append( input.nextLine() );
            }

        } catch (final Exception e) {
            rtn = false;
            e.printStackTrace();
            logger.debug("Exception happened: " + e);
        }

        logger.debug(" outStr:\n"+outStr);
        return rtn;
    }

    private HttpUtil() {}

}
