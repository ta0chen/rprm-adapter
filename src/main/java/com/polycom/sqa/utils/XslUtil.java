package com.polycom.sqa.utils;


import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

public class XslUtil {
	
	private static Logger logger = Logger.getLogger("sys");

	private XslUtil() {}
	
	public static boolean transform(String xslStr, String xmlStr, StringBuffer outStr) {
		logger.debug(" javax.net.ssl.trustStore: "+System.getProperty("javax.net.ssl.trustStore"));
		logger.debug(" xslStr:\n" + xslStr);
		logger.debug(" xmlStr:\n" + xmlStr);
		logger.debug(" outStr:\n" + outStr);	
		boolean rtn = true;
		
		try {
			Transformer xmlTransformer = TransformerFactory.newInstance().newTransformer(   new StreamSource(new StringReader(xslStr))   );
			xmlTransformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8"); 

			StringWriter outStrWriter = new StringWriter();
			xmlTransformer.transform(   new StreamSource(new StringReader(xmlStr)),    new StreamResult(outStrWriter)   );

			outStr.append(  outStrWriter.getBuffer().toString()  );	
		} catch (TransformerFactoryConfigurationError e) {
			rtn = false;
			logger.error("error during transforming xml " + e);
		} catch (TransformerException e) {
			rtn = false;
			logger.error("error during transforming xml " + e);
		} finally {
		}
		
		logger.debug(" outStr:\n" + outStr);	
		logger.error("return code " + rtn);
		return rtn;
	}		

}



