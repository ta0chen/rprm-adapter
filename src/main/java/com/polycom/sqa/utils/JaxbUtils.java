package com.polycom.sqa.utils;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.polycom.api.rest.plcm_reservation.PlcmReservation;
import com.polycom.api.rest.plcm_reserved_participant.PlcmReservedParticipant;

public class JaxbUtils {
    private static final Logger             LOGGER         = LoggerFactory
            .getLogger(JaxbUtils.class);
    private static Map<String, JAXBContext> jaxbContextMap = new ConcurrentHashMap<String, JAXBContext>();

    /**
     * xml to javabean
     *
     * @param xml
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertToJavaBean(final String xml, final Class<T> c) {
        T t = null;
        try {
            final JAXBContext context = JAXBContext.newInstance(c);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final DocumentBuilderFactory dbf = DocumentBuilderFactory
                    .newInstance();
            dbf.setNamespaceAware(true);
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(new InputSource(
                                                          new ByteArrayInputStream(xml.getBytes("utf-8"))));
            t = (T) unmarshaller.unmarshal(doc);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static String convertToXml(final Object jaxbObject) {
        final String jaxbObjectPackageName = jaxbObject.getClass().getPackage()
                .getName();
        JAXBContext jaxbContext = jaxbContextMap.get(jaxbObjectPackageName);
        if (jaxbContext == null) {
            LOGGER.info("JAXB Context is null, init JAXB Context for "
                    + jaxbObjectPackageName);
            try {
                jaxbContext = JAXBContext.newInstance(jaxbObjectPackageName);
                jaxbContextMap.put(jaxbObjectPackageName, jaxbContext);
            } catch (final JAXBException e) {
                throw new RuntimeException(
                                           "Error happen when init JAXB Context for "
                                                   + jaxbObjectPackageName, e);
            }
        }
        final StringWriter stringWriter = new StringWriter();
        try {
            jaxbContext.createMarshaller().marshal(jaxbObject, stringWriter);
        } catch (final JAXBException e) {
            throw new RuntimeException("JAXB Bean marshal failed", e);
        }
        return stringWriter.toString();
    }

    public static Source convertToXmlSource(final Object jaxbObject) {
        final String jaxbObjectPackageName = jaxbObject.getClass().getPackage()
                .getName();
        JAXBContext jaxbContext = jaxbContextMap.get(jaxbObjectPackageName);
        if (jaxbContext == null) {
            LOGGER.info("JAXB Context is null, init JAXB Context for "
                    + jaxbObjectPackageName);
            try {
                jaxbContext = JAXBContext.newInstance(jaxbObjectPackageName);
                jaxbContextMap.put(jaxbObjectPackageName, jaxbContext);
            } catch (final JAXBException e) {
                throw new RuntimeException(
                                           "Error happen when init JAXB Context for "
                                                   + jaxbObjectPackageName, e);
            }
        }
        final StringWriter stringWriter = new StringWriter();
        try {
            jaxbContext.createMarshaller().marshal(jaxbObject, stringWriter);
        } catch (final JAXBException e) {
            throw new RuntimeException("JAXB Bean marshal failed", e);
        }
        return new StreamSource(new StringReader(stringWriter.toString()));
    }

    public static Object createJavaBean(final Class<?> c, final String arg)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        final String delimeter = "\\s+";
        final String[] cmdArgs = arg.split(delimeter);
        final Object o = c.newInstance();
        for (final String s : cmdArgs) {
            if (!s.contains("=")) {
                continue;
            }
            final Pattern p = Pattern.compile("(.*?)=(.*)");
            final Matcher m = p.matcher(s);
            String value;
            if (m.find()) {
                final String key = m.group(1);
                value = m.group(2).replaceAll("~", " ");
                if (m.group(2).startsWith("~")) {
                    value = "~" + value;
                }
                CommonUtils.invokeSetMethod(o, key, value);
            }
        }
        return o;
    }

    public static void main(final String[] args) {
        final String rootElement = "plcm-reservation";
        String arg = "name=auto supportedLanguageEnum=ENGLISH plcmReservedParticipantList={username=wenbo,domain=local}{username=lingxi,domain=ad}";
        final String arg1 = "plcmUserRoleList:{plcmUserRole={name=Scheduler,description=Granted}}";
        try {
            final PlcmReservation plcmReservation = (PlcmReservation) createJavaBean(PlcmReservation.class,
                                                                                     arg);
            for (final PlcmReservedParticipant p : plcmReservation
                    .getPlcmReservedParticipantList()
                    .getPlcmReservedParticipantList()) {
                System.out.println("username==" + p.getUsername());
                System.out.println("domain==" + p.getDomain());
            }
            System.out.println(plcmReservation.getName());
            System.out.println(plcmReservation.getSupportedLanguageEnum());
            System.out.println("============Update=============");
            arg = "name=auto1 plcmReservedParticipantList={username=wenbo2,domain=local}{username=wenbo3,domain=local}";
            updateJavaBean(plcmReservation, arg);
            for (final PlcmReservedParticipant p : plcmReservation
                    .getPlcmReservedParticipantList()
                    .getPlcmReservedParticipantList()) {
                System.out.println("username==" + p.getUsername());
                System.out.println("domain==" + p.getDomain());
            }
            System.out.println(plcmReservation.getName());
            System.out.println(plcmReservation.getSupportedLanguageEnum());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Generic to String for POJO objects
     *
     * @return Each of the member variables as a string with commas between each
     *         value.
     */
    public static String pojoToString(final Object pojo) {
        final Method[] allMethods = pojo.getClass().getMethods();
        final StringBuilder sb = new StringBuilder("{");
        for (final Method method : allMethods) {
            String methodName = method.getName();
            if ((methodName.startsWith("get") || methodName.startsWith("is"))
                    && !methodName.startsWith("getClass")) {
                // Strip of get
                methodName = methodName
                        .substring((methodName.startsWith("get")) ? 3 : 2);
                // Convert the first character to lower case.
                methodName = CommonUtils.lowerFirstChar(methodName);
                if (sb.length() != 1) {
                    sb.append(", ");
                }
                try {
                    final String str;
                    final Class<?> type = method.getReturnType();
                    final Object value = method.invoke(pojo, (Object[]) null);
                    if (type.isArray()) {
                        str = Arrays.deepToString((Object[]) value);
                    } else if (type.isInstance(new Integer(0))
                            || type.isPrimitive()) {
                        str = (value == null) ? "null" : value.toString();
                    } else {
                        str = (value == null) ? "null" : ("\""
                                + value.toString() + "\"");
                    }
                    sb.append("\"" + methodName + "\":" + str);
                } catch (IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    // Do nothing if we could not get this value.
                    // Print the stack so we can see what types we cannot get
                    // the value of and fix that as we have time.
                    LOGGER.debug("pojoToString got exception. This is probably not an error but put this message in till I am sure.");
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public static Object updateJavaBean(final Object src, final String arg)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, InstantiationException {
        final String delimeter = "\\s+";
        final String[] cmdArgs = arg.split(delimeter);
        for (final String s : cmdArgs) {
            if (!s.contains("=")) {
                continue;
            }
            final Pattern p = Pattern.compile("(.*?)=(.*)");
            final Matcher m = p.matcher(s);
            if (m.find()) {
                final String key = m.group(1);
                final String value = m.group(2).replaceAll("~", " ");
                CommonUtils.invokeSetMethod(src, key, value);
            }
        }
        return src;
    }
}
