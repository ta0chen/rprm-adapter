package com.polycom.sqa.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SimpleXmlUtil {

    private static Logger logger = Logger.getLogger("sys");
    private static Serializer serializer = new Persister();

    public static String getInfoFromXmlResp(final String xmlMsg, final String info) throws ParserConfigurationException, SAXException, IOException {

        String strRtn = null;

        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlMsg));
        final Document doc = db.parse(is);
        final Element root = doc.getDocumentElement();
        final String tagName = root.getTagName();

        if(!tagName.equals("resp")) {
            System.err.println("<getInfoFromXmlResp>unexpected tag name:" + tagName);
            return null;
        }

        final NodeList rtnNodeList = doc.getElementsByTagName(info);
        if(rtnNodeList.getLength() != 1) {
            System.err.println("<getInfoFromXmlResp>unexpected num of info nodes:" + rtnNodeList.getLength() + " info= " + info);


            return null;
        }

        final Node rtnNode = rtnNodeList.item(0);
        strRtn = rtnNode.getTextContent();

        return strRtn;
    }

    public static String parseXmlFile4SoapAction(final String fileName) {
        final File xmlFile = new File(fileName);
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        try {
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(xmlFile);
            doc.getDocumentElement().normalize();

            final NodeList nList = doc.getElementsByTagName("soapenv:Body");
            final Node nNode = nList.item(0);
            logger.info("The action element for soap action header is:"
                    + nNode.getChildNodes().item(1).getNodeName());

            final String[] arg = nNode.getChildNodes().item(1).getNodeName()
                    .split(":");
            System.out.println("action is : " + arg[1]);
            return arg[1];
        } catch (final ParserConfigurationException | SAXException
                | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Object unmarshal(final Class<?> clazz, final File inFile) {
        try {
            final Object obj = serializer.read(clazz, inFile);
            return obj;
        }
        catch (final Exception e) {
            logger.error("Fails to unmarshal from the File\n"+e+"\n"+inFile.getPath());
            return null;
        }
    }

    public static Object unmarshal(final Class<?> clazz, final String in) {
        try {
            final Object obj = serializer.read(clazz, in);
            return obj;
        }
        catch (final Exception e) {
            logger.error("Fails to unmarshal from the String\n"+e+"\n"+in);
            return null;
        }
    }

    public static boolean updateXmlStr(final StringBuffer xmlMsg, final String[] NodePath, final String value) throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {

        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlMsg.toString()));
        final Document doc = db.parse(is);
        //Element root = doc.getDocumentElement();
        //String tagName = root.getTagName();
        //System.out.println("tagname:" + tagName);

        NodeList nl = null;
        Node nd = null;
        nl = doc.getElementsByTagName(NodePath[0]);
        if (nl == null) {
            logger.error("The node named " + NodePath[0] + " can not be found.");
            return false;
        }
        nd = nl.item(0);
        for (int i = 1 ; i < NodePath.length ; i++){
            nl = nd.getChildNodes();
            if (nl == null) {
                logger.error("The node named " + nd.getNodeName() + " has no child node.");
                return false;
            }
            int j;
            for(j = 0 ; j < nl.getLength() ; j++) {
                if (nl.item(j).getNodeName().equalsIgnoreCase(NodePath[i])) {
                    nd = nl.item(j);
                    break;
                }
            }
            if (j == nl.getLength()) {
                logger.error("The node named " + NodePath[i] + " can not be found.");
                return false;
            }
            //System.out.println("length: " + nl.getLength() +"\nname: " + nd.getNodeName() + "\nvalue: " + nd.getTextContent());
        }

        nd.setTextContent(value);


        final StreamResult result = new StreamResult(new StringWriter());
        final DOMSource source = new DOMSource(doc);

        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT,"no");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
        transformer.transform(source, result);

        final String xmlString = result.getWriter().toString();
        xmlMsg.replace(0,xmlMsg.length(),xmlString);
        logger.debug("xmlstr after update:\n" + xmlString);
        return true;

    }
    public static String xmlCmdGen(final String cmdName) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {

        final DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = fact.newDocumentBuilder();
        final Document doc = builder.newDocument();

        final Node root = doc.createElement("command");
        doc.appendChild(root);
        final NamedNodeMap respAttributes = root.getAttributes();

        final Attr attrCmd = doc.createAttribute("name");
        attrCmd.setValue(cmdName);
        respAttributes.setNamedItem(attrCmd);

        final Attr attrId = doc.createAttribute("id");
        respAttributes.setNamedItem(attrId);

        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");

        final StreamResult result = new StreamResult(new StringWriter());
        final DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);

        final String xmlString = result.getWriter().toString();

        return xmlString;

    }

    public static String xmlCmdGen(final String cmdName, final HashMap<String,String> parameters) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {

        final DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = fact.newDocumentBuilder();
        final Document doc = builder.newDocument();

        final Node root = doc.createElement("command");
        doc.appendChild(root);
        final NamedNodeMap respAttributes = root.getAttributes();

        final Attr attrCmd = doc.createAttribute("name");
        attrCmd.setValue(cmdName);
        respAttributes.setNamedItem(attrCmd);

        final Attr attrId = doc.createAttribute("id");
        respAttributes.setNamedItem(attrId);

        if(parameters != null && !parameters.isEmpty()) {
            for(final String key : parameters.keySet()) {
                final Node nodeItem = doc.createElement(key);
                nodeItem.setTextContent(parameters.get(key));
                root.appendChild(nodeItem);
            }
        }

        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");

        final StreamResult result = new StreamResult(new StringWriter());
        final DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);

        final String xmlString = result.getWriter().toString();

        return xmlString;
    }
}
