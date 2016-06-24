package com.polycom.sqa.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.AbstractBasicInterceptorProvider;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.polycom.api.rest.plcm_objects.PlcmMapItem;
//import com.polycom.auto.constants.PlcmEnum;
import com.polycom.webservices.ConferenceService.ConferenceFilter;

/**
 * The util class contains common functions
 *
 * @author wbchao
 *
 */
public class CommonUtils {
    protected static Logger            logger         = Logger
            .getLogger("CommonUtils");
    public static final String         PLCM_ENUM_NAME = "com.polycom.auto.constants.PlcmEnum";
    public static final String         PATTERN        = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private static Map<String, Object> objectCache    = new HashMap<String, Object>();

    public static void addObjectCache(final Object object) {
        if (object != null) {
            objectCache.put(object.getClass().getSimpleName(), object);
        }
    }

    /**
     * Copy the attribute value with same attribute name
     *
     * @param from
     *            Source object
     * @param to
     *            Target object
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void copyProperties(final Object from, final Object to)
            throws Exception {
        final List<Method> fromMethods = getDeclaredMethods(from);
        final List<Method> toMethods = getDeclaredMethods(to);
        final List<Field> fields = getDeclaredFields(to);
        Method toMethod = null;
        Method fromMethod = null;
        String fromMethodName = null, toMethodName = null;
        for (final Field field : fields) {
            final String fieldName = field.getName();
            final String fieldNameUpper = upperFirstChar(fieldName);
            if (field.getType() == boolean.class
                    || field.getType() == Boolean.class) {
                fromMethodName = "is" + fieldNameUpper;
            } else {
                fromMethodName = "get" + fieldNameUpper;
            }
            fromMethod = findMethodByName(fromMethods, fromMethodName);
            if (fromMethod == null) {
                continue;
            }
            final Object value = fromMethod.invoke(from, new Object[0]);
            if (value == null) {
                continue;
            }
            if (value instanceof List) {
                final ArrayList<?> fromList = (ArrayList<?>) value;
                if (fromList.size() <= 0) {
                    continue;
                }
                field.setAccessible(true);
                final Class<?> clz = getGenericClass(field);
                final ArrayList<Object> toList = new ArrayList<Object>();
                for (final Object obj : fromList) {
                    final Object toValue = clz.newInstance();
                    copyProperties(obj, toValue);
                    toList.add(toValue);
                }
                field.set(to, toList);
                continue;
            }
            toMethodName = "set" + fieldNameUpper;
            toMethod = findMethodByName(toMethods, toMethodName);
            if (toMethod == null) {
                continue;
            }
            try {
                if (field.getType() == String.class
                        || field.getType().isPrimitive()
                        || isWrapClass(field.getType())) {
                    toMethod.invoke(to, new Object[] { value });
                } else if (field.getType().isEnum()) {
                    toMethod.invoke(to,
                                    new Object[] {
                            Enum.valueOf((Class<Enum>) field
                                         .getType(),
                                         value.toString()) });
                } else {
                    final Object newField = field.getType().newInstance();
                    copyProperties(value, newField);
                    toMethod.invoke(to, new Object[] { newField });
                }
            } catch (final Exception e) {
                e.printStackTrace();
                e.getMessage();
            }
        }
    }

    /**
     * Get the default truse manager
     *
     * @return tm default truse manager {@link X509TrustManager}
     */
    public static X509TrustManager defaultTrustManager() {
        final X509TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] xcs,
                    final String string)
                            throws CertificateException {
                return;
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] xcs,
                    final String string)
                            throws CertificateException {
                return;
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        return tm;
    }

    /**
     * Delete the folder
     *
     * @param dir
     *            The folder to delete
     * @return boolean Returns "true" if all deletions were successful. If a
     *         deletion fails, the method stops attempting to delete and returns
     *         "false".
     */
    public static boolean deleteDir(final File dir) {
        if (dir.isDirectory()) {
            final String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                final boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * Trust all certificates from the server
     *
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static void doTrustToAllCertificates()
            throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext ctx = SSLContext.getInstance("TLS");
        final X509TrustManager trustAllCerts = defaultTrustManager();
        ctx.init(null, new TrustManager[] { trustAllCerts }, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        final HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname,
                    final SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    /**
     * Download the file through HTTP
     *
     * @param path
     *            The local path to store the file
     * @param url
     *            The download URL
     * @throws IOException
     */
    public static String downloadFile(final String path,
            final String url,
            final String cookie) throws IOException {
        logger.info("url:" + url);
        final HttpClient httpClient = WebClientDevWrapper
                .wrapClient(new DefaultHttpClient(), 8443);
        final HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Cookie", cookie);
        final HttpResponse response = httpClient.execute(httpGet);
        final int statusCode = response.getStatusLine().getStatusCode();
        try {
            if (statusCode == HttpStatus.SC_OK) {
                final byte[] result = EntityUtils
                        .toByteArray(response.getEntity());
                BufferedOutputStream bw = null;
                try {
                    final File f = new File(path);
                    if (!f.getParentFile().exists()) {
                        f.getParentFile().mkdirs();
                    }
                    bw = new BufferedOutputStream(new FileOutputStream(path));
                    bw.write(result);
                } catch (final Exception e) {
                    logger.error("Save file error, path=" + path + ",url="
                            + url, e);
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (final Exception e) {
                        logger.error("finally BufferedOutputStream shutdown close",
                                     e);
                    }
                }
            } else {
                final StringBuffer errorMsg = new StringBuffer();
                errorMsg.append("\nhttpStatus:");
                errorMsg.append(statusCode);
                errorMsg.append(response.getStatusLine().getReasonPhrase());
                errorMsg.append("\nHeader: ");
                final Header[] headers = response.getAllHeaders();
                for (final Header header : headers) {
                    errorMsg.append(header.getName());
                    errorMsg.append(":");
                    errorMsg.append(header.getValue());
                    errorMsg.append("\n");
                }
                logger.error("HttpResonse Error:" + errorMsg);
            }
        } catch (final ClientProtocolException e) {
            logger.error("Download file exception, path=" + path + ",url="
                    + url, e);
            throw e;
        } catch (final IOException e) {
            logger.error("Save file exception, path=" + path + ",url=" + url,
                         e);
            throw e;
        } finally {
            try {
                httpClient.getConnectionManager().shutdown();
            } catch (final Exception e) {
                logger.error("finally HttpClient shutdown error", e);
            }
        }
        return statusCode + "";
    }

    /**
     * Run the command in cmd line
     *
     * @param command
     *            The cmd string
     * @return The response after execute
     */
    public static String executeCommand(final String command) {
        final StringBuffer output = new StringBuffer();
        Process p;
        String line = "";
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            final BufferedReader reader = new BufferedReader(
                                                             new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    /**
     * Find the method with specified method name
     *
     * @param Methods
     *            All methods to find
     * @param name
     *            Method name
     * @return The method with specified method name
     */
    private static Method findMethodByName(final List<Method> methods,
            final String name) {
        return findMethodByName(methods, name, -1);
    }

    /**
     * Find the method with specified method name and arg count
     *
     * @param methods
     *            Methods All methods to find
     * @param name
     *            Method name
     * @param argSize
     *            The arg count
     * @return The method with specified method name and arg count
     */
    private static Method findMethodByName(final List<Method> methods,
            final String name,
            final int argSize) {
        for (final Method m : methods) {
            if (m.getName().equalsIgnoreCase(name)) {
                if (argSize < 0) {
                    return m;
                } else {
                    if (m.getParameterTypes().length == argSize) {
                        return m;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Init all the field value, assigned the String type with empty String
     *
     * @param obj
     *            The object to init
     */
    public static void generateField(final Object obj) {
        final List<Field> fields = getDeclaredFields(obj);
        for (final Field field : fields) {
            try {
                field.setAccessible(true);
                final Object val = field.get(obj);
                if (val == null) {
                    field.setAccessible(true);
                    if (field.getType() == String.class) {
                        field.set(obj, "");
                    } else if (field.getType().isEnum()) {
                        continue;
                    } else if (field.getType() == List.class) {
                        field.set(obj, new ArrayList<Object>());
                    } else if (field.getType() == Integer.class) {
                        field.set(obj, 0);
                    } else if (field.getType() == Long.class) {
                        field.set(obj, 0L);
                    } else if (field.getType() == Float.class) {
                        field.set(obj, 0F);
                    } else if (field.getType() == Boolean.class) {
                        field.set(obj, false);
                    } else {
                        final Object newField = field.getType().newInstance();
                        generateField(newField);
                        field.set(obj, newField);
                    }
                }
            } catch (SecurityException
                    | IllegalArgumentException
                    | IllegalAccessException
                    | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the field with specified fieldName(include super class)
     *
     * @param object
     *            The object to find
     * @param fieldName
     *            The field name
     * @return The field with specified fieldName(include super class)
     */
    public static Field getDeclaredField(final Object object,
            final String fieldName) {
        final List<Field> fieldList = getDeclaredFields(object);
        for (final Field f : fieldList) {
            if (f.getName().equalsIgnoreCase(fieldName)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Get all fields, (include super class)
     *
     * @param object
     *            The object to find
     * @return All fields, (include super class)
     */
    public static List<Field> getDeclaredFields(final Object object) {
        final List<Field> fieldList = new ArrayList<Field>();
        for (Class<?> clazz = object
                .getClass(); clazz != Object.class; clazz = clazz
                .getSuperclass()) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        return fieldList;
    }

    /**
     * Get the method with spcified method name and param types
     *
     * @param object
     *            The object to find
     * @param methodName
     *            The method name
     * @param parameterTypes
     *            The param types
     * @return The method
     */
    public static Method getDeclaredMethod(final Object object,
            final String methodName,
            final Class<?>... parameterTypes) {
        Method method = null;
        for (Class<?> clazz = object
                .getClass(); clazz != Object.class; clazz = clazz
                .getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (final Exception e) {
            }
        }
        return null;
    }

    /**
     * Get All methods of the object(include super class)
     *
     * @param object
     *            The object to find
     * @return All methods of the object(include super class)
     */
    public static List<Method> getDeclaredMethods(final Object object) {
        Class<?> startClass = null;
        if (object == Class.class) {
            startClass = (Class<?>) object;
        } else {
            startClass = object.getClass();
        }
        final List<Method> methodList = new ArrayList<Method>();
        for (Class<?> clazz = startClass; clazz != Object.class; clazz = clazz
                .getSuperclass()) {
            methodList.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        }
        return methodList;
    }

    /**
     * Get the instance in the list with specified attribute key value
     *
     * @param list
     *            The list to find
     * @param keyword
     *            The format is key=value, find the first instance in the list
     *            which has the key/value
     * @return The instance in the list with specified attribute key value
     */
    public static List<?> getElementFromList(final List<?> list,
                                             final String keyword) {
        final String replaceFirstReg = "(([A-Za-z0-9=_\\./-]*(\\[.*?\\])?):?)+?";
        final Pattern fieldPattern = Pattern.compile(replaceFirstReg);
        final Matcher fieldMatcher = fieldPattern.matcher(keyword);
        String tmp = "";
        if (fieldMatcher.find()) {
            tmp = fieldMatcher.group(3);
        }
        if (tmp == null) {
            tmp = keyword.split(":")[0];
        }
        if (list.size() == 1 && !tmp.contains("[")) {
            return list;
        }
        final Pattern p = Pattern.compile("\\[(([a-z:A-Z0-9]*)=?(.*))\\]");
        final Matcher m = p.matcher(tmp);
        String attrName = "";
        String attrValue = "";
        String fullValue = "";
        if (m.find()) {
            fullValue = m.group(1);
            attrName = m.group(2);
            attrValue = m.group(3).replaceAll("~", " ");
        }
        if (attrValue == null || attrValue.isEmpty()) {
            final String fieldName = tmp.replaceAll("(.*\\[|\\])", "");
            return getListValues(list, fieldName);
        }
        final List<Object> result = new ArrayList<Object>();
        for (final Object obj : list) {
            final Class<?> clz = obj.getClass();
            if ((clz.isPrimitive() || clz == String.class || isWrapClass(clz)
                    || clz.isEnum()) && obj.equals(fullValue)) {
                result.add(obj);
            } else if (attrValue.equals(invokeGetMethod(obj, attrName))) {
                result.add(obj);
            }
        }
        return result;
    }

    /**
     * Convert the strValue to Enum
     *
     * @param enumClassName
     *            The enum class name, if in PLCMTest.jar no need the full name
     * @param strValue
     *            The str value of enum, if in PLCMTester.jar use rest value str
     * @return The Enum class instance
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object
    getEnumValue(final String enumClassName, final String strValue)
            throws ClassNotFoundException, NoSuchMethodException,
            SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        final Class enumClass = Class.forName(enumClassName);
        final Method enumMethod = enumClass.getDeclaredMethod("fromRestValue",
                                                              String.class);
        if (enumMethod != null) {
            return enumMethod.invoke(enumClass, new Object[] { strValue });
        } else if (strValue.equalsIgnoreCase("null")) {
            return null;
        } else {
            return Enum.valueOf(enumClass, strValue);
        }
    }

    /**
     * Get the stack trace message of Exception
     *
     * @param e
     *            The Exception
     * @return The stack trace message of Exception
     */
    public static String getExceptionStackTrace(final Exception e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Return the generic class of Enum/List class
     *
     * @param f
     * @return The generic class
     */
    public static Class<?> getGenericClass(final Field f) {
        final Type fc = f.getGenericType();
        if (fc instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType) fc;
            final Class<?> genericClazz = (Class<?>) pt
                    .getActualTypeArguments()[0];
            return genericClazz;
        }
        return null;
    }

    public static List<String> getListValues(final List<?> list,
                                             final String fieldName) {
        final ArrayList<String> result = new ArrayList<String>();
        for (final Object obj : list) {
            result.add(invokeGetMethod(obj, fieldName));
        }
        Collections.sort(result);
        return result;
    }

    public static Object getObjectCache(final String key) {
        return objectCache.get(key);
    }

    /**
     * Make web service connection support the https
     *
     * @param port
     *            The specific interface in the driver
     */
    public static void httpsConnectionSupport(final Object port) {
        final TLSClientParameters tlsClientParameters = new TLSClientParameters();
        final X509TrustManager trustAllCerts = defaultTrustManager();
        tlsClientParameters
        .setTrustManagers(new TrustManager[] { trustAllCerts });
        tlsClientParameters.setDisableCNCheck(true);
        final Client client = ClientProxy.getClient(port);
        final AbstractBasicInterceptorProvider interceptorProvider = (AbstractBasicInterceptorProvider) client;
        interceptorProvider.setInInterceptors(Arrays
                                              .<Interceptor<? extends Message>> asList(new LoggingInInterceptor()));
        interceptorProvider.setOutInterceptors(Arrays
                                               .<Interceptor<? extends Message>> asList(new LoggingOutInterceptor()));
        final HTTPConduit http = (HTTPConduit) client.getConduit();
        http.setTlsClientParameters(tlsClientParameters);
        final HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(2 * 60 * 1000);
        httpClientPolicy.setAllowChunking(false);
        httpClientPolicy.setReceiveTimeout(2 * 60 * 1000);
        http.setClient(httpClientPolicy);
    }

    /**
     * Init the List in bean
     *
     * @param obj
     *            The object to init
     */
    public static void initList(final Object obj) {
        if (obj == null || !isCustomizedClass(obj.getClass())) {
            return;
        }
        final List<Field> fields = getDeclaredFields(obj);
        for (final Field field : fields) {
            try {
                field.setAccessible(true);
                final Object val = field.get(obj);
                final Class<?> fieldClass = field.getType();
                if (List.class.isAssignableFrom(fieldClass)) {
                    final String methodName = "get"
                            + upperFirstChar(field.getName());
                    try {
                        final Method m = getDeclaredMethod(obj, methodName);
                        if (m != null) {
                            m.invoke(obj);
                        }
                    } catch (final InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else if (isCustomizedClass(fieldClass)) {
                    if (val != null) {
                        initList(val);
                    }
                }
            } catch (SecurityException
                    | IllegalArgumentException
                    | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Invoke the get method of javabean
     *
     * @param object
     *            Javabean instance
     * @param keyword
     *            The attribute name of javabean
     * @return The toString value of specified attribute in javabean
     */
    public static String invokeGetMethod(final Object object,
            final String keyword) {
        // Match the first :
        final String replaceFirstReg = "(([A-Za-z0-9=_\\./-]*(\\[.*?\\])?):)+?";
        final Pattern fieldPattern = Pattern.compile(replaceFirstReg);
        final Matcher fieldMatcher = fieldPattern.matcher(keyword);
        String fieldName = "";
        if (fieldMatcher.find()) {
            fieldName = fieldMatcher.group(2);
        } else {
            fieldName = keyword.split(":")[0];
        }
        if (object == null) {
            return "NotFound, the object to search is null";
        } else if (Set.class.isAssignableFrom(object.getClass())) {
            final HashSet<?> objectSet = (HashSet<?>) object;
            return invokeGetMethod(objectSet.toArray(), keyword);
        } else if (object.getClass().isArray()) {
            return invokeGetMethod(Arrays.asList((Object[]) object), keyword);
        } else if (List.class.isAssignableFrom(object.getClass())) {
            final List<?> list = (List<?>) object;
            if (fieldName.matches(".*\\[size\\].*")) {
                return list.size() + "";
            }
            if (list.size() == 0) {
                return "NotFound, the list" + fieldName + " is empty";
            }
            Class<?> clz = list.get(0).getClass();
            if (clz.isPrimitive() || clz == String.class || isWrapClass(clz)
                    || clz.isEnum()) {
                return list.toString();
            }
            final List<?> elements = getElementFromList(list, keyword);
            Object listElement = null;
            final String nextKeyword = keyword.replaceFirst(replaceFirstReg,
                    "");
            if (elements.size() == 1 && !nextKeyword.startsWith("[")) {
                listElement = elements.get(0);
            } else {
                return invokeGetMethod(elements, nextKeyword);
            }
            if (listElement == null) {
                return "NotFound, could not find the element in list";
            }
            clz = listElement.getClass();
            if (clz == String.class) {
                return listElement.toString();
            }
            return invokeGetMethod(listElement, nextKeyword);
        }
        if (fieldName.contains("[")) {
            final Pattern p = Pattern.compile("(.*)\\[(.*)\\]");
            final Matcher m = p.matcher(fieldName);
            if (m.find()) {
                fieldName = m.group(1);
            }
        }
        final Field f = getDeclaredField(object, fieldName);
        String methodName = "";
        if (f == null) {
            methodName = "get" + upperFirstChar(fieldName);
        } else
            if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                methodName = "is" + upperFirstChar(fieldName);
            } else {
                methodName = "get" + upperFirstChar(fieldName);
            }
        try {
            Method method = getDeclaredMethod(object,
                                              methodName,
                                              new Class[] {});
            Object obj = null;
            if (method == null) {
                methodName = methodName.replaceAll(upperFirstChar(fieldName),
                                                   upperFirstChar(f.getName()));
                method = getDeclaredMethod(object, methodName, new Class[] {});
            }
            if (method == null) {
                methodName = "get" + fieldName;
                method = getDeclaredMethod(object, methodName, new Class[] {});
            }
            if (method == null) {
                return "NotFound, could not find the method " + methodName;
            }
            obj = method.invoke(object, new Object[] {});
            if (obj != null && obj.getClass().getSimpleName()
                    .equals("XMLGregorianCalendarImpl")) {
                return obj.toString();
            }
            if (obj == null || obj.equals("")) {
                return "NotFound";
            }
            if (List.class.isAssignableFrom(obj.getClass())) {
                final String nextKeyword = keyword
                        .replaceFirst(replaceFirstReg, "");
                return invokeGetMethod(obj, nextKeyword);
            } else if (isCustomizedClass(obj.getClass())) {
                return invokeGetMethod(obj,
                                       keyword.replaceFirst(replaceFirstReg,
                                               ""));
            } else if (obj.getClass().isEnum()
                    && isImplementsInterface(obj.getClass(), PLCM_ENUM_NAME)) {
//                final PlcmEnum plcmEnum = (PlcmEnum) obj;
//                return plcmEnum.restValue().toString();
                return "";
            } else {
                return obj.toString();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            return getExceptionStackTrace(e);
        }
    }

    /**
     * Common function to invoke the method with specified method name and args
     *
     * @param obj
     *            The object to invoke
     * @param methodName
     *            The method name
     * @param args
     *            The arg array
     * @return The return value of specified method
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static Object
    invokeMethod(final Object obj,
            final String methodName,
            final String[] args,
            final String... parameterTypes)
                    throws ClassNotFoundException,
                    NoSuchMethodException, SecurityException,
                    IllegalAccessException,
                    IllegalArgumentException,
                    InvocationTargetException {
        Object result = null;
        final int argSize = args.length;
        final Object[] parameterValues = new Object[argSize];
        final List<Method> methods = getDeclaredMethods(obj);
        Method method = null;
        if (parameterTypes == null || parameterTypes.length == 0) {
            method = findMethodByName(methods, methodName, argSize);
        } else {
            final Class<?>[] argTypes = new Class[parameterTypes.length];
            for (int i = 0; i < argSize; i++) {
                if (parameterTypes[i].equals("String")) {
                    argTypes[i] = String.class;
                } else if (parameterTypes[i].equals("int")) {
                    argTypes[i] = int.class;
                } else if (parameterTypes[i].equals("long")) {
                    argTypes[i] = long.class;
                } else if (parameterTypes[i].equals("boolean")) {
                    argTypes[i] = boolean.class;
                } else {
                    argTypes[i] = Class.forName(parameterTypes[i]);
                }
                method = getDeclaredMethod(obj, methodName, argTypes);
            }
        }
        if (method == null) {
            return "Failed, could not find the method " + methodName
                    + " in Endpoint class";
        }
        Object value = null;
        final Class<?>[] argTypes = method.getParameterTypes();
        if (argTypes.length != argSize) {
            return "Failed, the arg size is not consistent with the endpoint method "
                    + methodName + " , the size should be" + argTypes.length
                    + ", but got " + argSize;
        }
        for (int i = 0; i < argSize; i++) {
            final Class<?> argType = argTypes[i];
            final String strValue = args[i].trim();
            if (argType == String.class || argType == CharSequence.class) {
                value = strValue.equals("null") ? "" : strValue;
            } else if (argType == Integer.class || argType == int.class) {
                value = Integer.parseInt(strValue);
            } else if (argType == Long.class || argType == long.class) {
                value = Long.parseLong(strValue);
            } else if (argType == Float.class || argType == float.class) {
                value = Float.parseFloat(strValue);
            } else if (argType == Double.class || argType == double.class) {
                value = Double.parseDouble(strValue);
            } else if (argType == Boolean.class || argType == boolean.class) {
                value = Boolean.parseBoolean(strValue);
            } else if (argType.isEnum()) {
                final String enumClassName = argType.getName();
                value = getEnumValue(enumClassName, strValue);
            } else if (argType == String[].class
                    || argType == CharSequence[].class) {
                value = strValue.split("\\|");
            }
            parameterValues[i] = value;
        }
        result = method.invoke(obj, parameterValues);
        return result;
    }

    /**
     * Common function to invoke the set method
     *
     * @param object
     *            The object to invoke
     * @param keyword
     *            The attribute field name
     * @param strValue
     *            The str value to set
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void invokeSetMethod(final Object object,
            final String keyword,
            String strValue)
                    throws IllegalAccessException,
                    IllegalArgumentException,
                    InvocationTargetException,
                    InstantiationException {
        String fieldName = "";
        String childClassName = "";
        final Pattern fieldTypePattern = Pattern.compile("(.*)\\((.*)\\):?");
        final Matcher filedTypeMatcher = fieldTypePattern.matcher(keyword);
        if (filedTypeMatcher.find()) {
            fieldName = filedTypeMatcher.group(1);
            childClassName = filedTypeMatcher.group(2);
        } else {
            fieldName = keyword.split(":")[0];
        }
        final Field f = getDeclaredField(object, fieldName.split(":")[0]);
        if (f == null) {
            return;
        }
        f.setAccessible(true);
        final Class fieldType = f.getType();
        String methodName = "";
        methodName = "set" + upperFirstChar(fieldName);
        if (f.getType() == boolean.class || f.getType() == Boolean.class) {
            methodName = methodName.replaceFirst("setIs", "set");
        }
        // Special operation for class PlcmBaseline
        if (object.getClass().getSimpleName().equals("PlcmBaseline")) {
            methodName = "set" + upperFirstChar(fieldName);
        }
        Method method = getDeclaredMethod(object,
                                          methodName,
                                          new Class[] { fieldType });
        if (method == null) {
            methodName = "set" + fieldName;
            method = getDeclaredMethod(object,
                                       methodName,
                                       new Class[] { fieldType });
        }
        Object value = null;
        if (Collection.class.isAssignableFrom(fieldType)) {
            final Class genericClass = getGenericClass(f);
            Collection<Object> collection = (Collection<Object>) f.get(object);
            if (collection == null) {
                if (List.class.isAssignableFrom(fieldType)) {
                    collection = new ArrayList<Object>();
                } else {
                    collection = new HashSet<Object>();
                }
                f.set(object, collection);
            }
            if (strValue.startsWith("replaceWith")) {
                collection.clear();
                strValue = strValue.replaceFirst("replaceWith", "");
            }
            final String regex = "\\{(.*?)\\}";
            final Pattern p = Pattern.compile(regex);
            final Matcher m = p.matcher(strValue);
            String elementStr = "";
            if (m.find()) {
                elementStr = m.group(1);
                if (elementStr.isEmpty()) {
                    return;
                }
                if (genericClass.isEnum()) {
                    collection.add(Enum.valueOf(genericClass, elementStr));
                } else if (genericClass == String.class) {
                    collection.add(elementStr);
                } else if (genericClass == Integer.class) {
                    collection.add(Integer.parseInt(elementStr));
                } else if (genericClass == short.class) {
                    collection.add(Short.parseShort(elementStr));
                } else if (genericClass == Long.class) {
                    collection.add(Long.parseLong(elementStr));
                } else if (genericClass == Boolean.class) {
                    collection.add(Boolean.parseBoolean(elementStr));
                } else if (isCustomizedClass(genericClass)) {
                    Object newElement = genericClass.newInstance();
                    for (final String s : elementStr.split(",")) {
                        final String[] keyValue = s.split("=");
                        final String key = keyValue[0];
                        final String value1 = keyValue[1];
                        if (genericClass == PlcmMapItem.class) {
                            for (final Object element : collection) {
                                final PlcmMapItem item = (PlcmMapItem) element;
                                if (item.getKey().equals(value1)) {
                                    newElement = item;
                                    break;
                                }
                            }
                        }
                        invokeSetMethod(newElement, key, value1);
                    }
                    if (genericClass != PlcmMapItem.class) {
                        collection.add(newElement);
                    }
                }
                invokeSetMethod(object,
                                keyword,
                                strValue.replaceFirst(regex, ""));
            } else {
                return;
            }
        } else if (fieldType == XMLGregorianCalendar.class) {
            logger.info("Need to instance a new date class");
            try {
                final XMLGregorianCalendar cal = DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(strValue);
                value = cal;
            } catch (final DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        } else if (isCustomizedClass(fieldType)) {
            final Object cachedObject = objectCache.get(strValue);
            if (cachedObject != null && fieldType == cachedObject.getClass()) {
                value = cachedObject;
            } else {
                Object obj = f.get(object);
                if (obj == null) {
                    if (childClassName.isEmpty()) {
                        obj = fieldType.newInstance();
                    } else {
                        final String pachageName = fieldType.getPackage()
                                .getName();
                        try {
                            obj = Class
                                    .forName(pachageName + "." + childClassName)
                                    .newInstance();
                        } catch (final ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                invokeSetMethod(obj,
                                keyword.replaceFirst("([A-Za-z0-9\\(\\)]+:)+?",
                                        ""),
                                        strValue);
                value = obj;
            }
        } else if (fieldType == String.class) {
            value = strValue;
        } else if (fieldType == Integer.class || fieldType == int.class) {
            value = Integer.parseInt(strValue);
        } else if (fieldType == Short.class || fieldType == short.class) {
            value = Short.parseShort(strValue);
        } else if (fieldType == Long.class || fieldType == long.class) {
            value = Long.parseLong(strValue);
        } else if (fieldType == Float.class || fieldType == float.class) {
            value = Float.parseFloat(strValue);
        } else if (fieldType == Double.class || fieldType == double.class) {
            value = Double.parseDouble(strValue);
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            value = Boolean.parseBoolean(strValue);
        } else if (fieldType.isEnum()) {
            value = Enum.valueOf(fieldType, strValue);
        }
        if (method != null && !Collection.class.isAssignableFrom(fieldType)) {
            method.invoke(object, new Object[] { value });
        }
    }

    /**
     * Return true if the class is customized
     *
     * @param clz
     *            The class to judge
     * @return Return true if the class is customized
     */
    public static boolean isCustomizedClass(final Class<?> clz) {
        if (clz.isPrimitive() || clz == String.class || isWrapClass(clz)
                || clz.isEnum() || Collection.class.isAssignableFrom(clz)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Return true if the objClass has implements the specified interface
     *
     * @param objClass
     *            The Class to judge
     * @param interfaceClassName
     *            The specified interface name
     * @return Return true if the objClass has implements the specified
     *         interface
     */
    public static boolean
    isImplementsInterface(final Class<?> objClass,
            final String interfaceClassName) {
        final Type[] interfaces = objClass.getInterfaces();
        for (final Type type : interfaces) {
            if (type.getTypeName().equals(interfaceClassName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if the class is Wrap class, such as Integer, Long, Boolean
     *
     * @param clz
     *            The class to judge
     * @return Return true if the class is Wrap class
     */
    public static boolean isWrapClass(final Class<?> clz) {
        try {
            return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Lower the first char of str value
     *
     * @param str
     *            The str value need to treat
     * @return The str value with first char lower case
     */
    public static String lowerFirstChar(final String str) {
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public static void main(final String[] args) throws IOException {
        // final PlcmReservedParticipantList plcmReservedParticipantList = new
        // PlcmReservedParticipantList();
        // final String str =
        // "{username=wenbo,domain=local}{username=lingxi,domain=ad}";
        // try {
        // invokeSetMethod(plcmReservedParticipantList,
        // "plcmReservedParticipantList",
        // str);
        // for (final PlcmReservedParticipant p : plcmReservedParticipantList
        // .getPlcmReservedParticipantList()) {
        // System.out.println("username==" + p.getUsername());
        // System.out.println("domain==" + p.getDomain());
        // }
        // } catch (IllegalAccessException | IllegalArgumentException
        // | InvocationTargetException | InstantiationException e) {
        // e.printStackTrace();
        // }
        // System.out.println("=================================");
        // String keyword =
        // "[firstName=debuguser3]:devices[name=GroupSeries]:addressList[addressType=IP_EXTENSION]:number";
        // keyword = "devices[GroupSeries]";
        // final Pattern p = Pattern.compile("\\[(([a-zA-Z0-9]*)=?(.*))\\]");
        // final Matcher m = p.matcher(keyword.split(":")[0]);
        // String attrName = "";
        // String attrValue = "";
        // String fullValue = "";
        // if (!m.find()) {
        // return;
        // } else {
        // fullValue = m.group(1);
        // attrName = m.group(2);
        // attrValue = m.group(3);
        // }
        // System.out.println("attrName==" + attrName);
        // System.out.println("attrValue==" + attrValue);
        // System.out.println("fullValue==" + fullValue);
        // keyword = "addressList[addressType=IP_ADDRESS]:number";
        // final String newStr =
        // keyword.replaceFirst("([A-Za-z0-9\\[\\]=_]+:)+?",
        // "");
        // System.out.println(newStr);
        // System.out.println("=================================");
        // final List<String> list = new ArrayList<String>();
        // list.add("aaa");
        // final Type type = list.getClass().getGenericSuperclass();
        // if (type instanceof ParameterizedType) {
        // final ParameterizedType pt = (ParameterizedType) type;
        // final Class<?> genericClazz = (Class<?>) pt
        // .getActualTypeArguments()[0];
        // System.out.println(genericClazz.getName());
        // }
        final ConferenceFilter conf = new ConferenceFilter();
        initList(conf);
        System.out.println("=================================");
    }

    /**
     * Parse the str array value from TCL framework to XMA attribute map
     *
     * @param cmdArgs
     *            Str array from TCL
     * @param inputCmd
     *            The XMA attribute map
     */
    public static void parseInputCmd(final String[] cmdArgs,
            final Map<String, String> inputCmd) {
        String cmd, key, value;
        cmd = key = value = null;
        for (int i = 3; i < cmdArgs.length; i++) {
            cmd = cmdArgs[i];
            try {
                final Pattern pattern = Pattern
                        .compile("^([\\w\\.-~,:#@]+?)=([\\(!\\w\\.\\[\\]\"\\|&()-~,=:#$@\\)]+)$");
                final Matcher matcher = pattern.matcher(cmd);
                if (matcher.find()) {
                    key = matcher.group(1);
                    value = matcher.group(2);
                }
                inputCmd.put(key, value);
            } catch (final Exception e) {
                logger.error("FAIL to parse the input cmd=" + cmd
                             + " exception=" + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Parse the str array value from TCL framework to CUCM attribute map
     *
     * @param cmdArgs
     *            Str array from TCL
     * @param inputCmd
     *            The CUCM attribute map
     */
    public static void parseInputCmd4Cucm(final String[] cmdArgs,
            final Map<String, String> inputCmd) {
        String cmd, key, value;
        cmd = key = value = null;
        for (int i = 2; i < cmdArgs.length; i++) {
            cmd = cmdArgs[i];
            try {
                final Pattern pattern = Pattern
                        .compile("^([\\w\\-~,:#@]+)=([!\\w\\.\\[\\]\"\\|&()-~,=:#@$]+)$");
                final Matcher matcher = pattern.matcher(cmd);
                if (matcher.find()) {
                    key = matcher.group(1);
                    value = matcher.group(2);
                }
                inputCmd.put(key, value);
            } catch (final Exception e) {
                logger.error("FAIL to parse the input cmd=" + cmd
                             + " exception=" + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Parse the str array value from TCL framework to VM attribute map
     *
     * @param cmdArgs
     *            Str array from TCL
     * @param inputCmd
     *            The VM attribute map
     */
    public static void parseInputCmd4VM(final String[] cmdArgs,
            final Map<String, String> inputCmd) {
        String cmd, key, value;
        cmd = key = value = null;
        for (int i = 2; i < cmdArgs.length; i++) {
            cmd = cmdArgs[i];
            try {
                final Pattern pattern = Pattern
                        .compile("^([\\w\\.-~,:#@]+)=([!\\w\\.\\[\\]\"\\|&()-~,=:#@]+)$");
                final Matcher matcher = pattern.matcher(cmd);
                if (matcher.find()) {
                    key = matcher.group(1);
                    value = matcher.group(2);
                }
                inputCmd.put(key, value);
            } catch (final Exception e) {
                logger.error("FAIL to parse the input cmd=" + cmd
                             + " exception=" + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Update the soap url of wsdl file
     *
     * @param wsdlfile
     *            The wsdl file location
     * @param url
     *            The url to update to
     */
    public static void replaceSoapUrl(final String wsdlfile, final String url) {
        final StringBuffer fileContent = new StringBuffer();
        if (!StringUtil.readFile2Str(wsdlfile, fileContent)) {
            final String resp = "FAIL\nNot able to read wsdl file content from file "
                    + wsdlfile;
            logger.error(resp);
            return;
        }
        final File file = new File(wsdlfile);
        final String regex = "soap:address location=.*?/>";
        final String replacement = "soap:address location=\"" + url + "\"/>";
        final String tmpStr = fileContent.toString().replaceAll(regex,
                                                                replacement);
        try {
            final FileOutputStream fw = new FileOutputStream(file);
            fw.write(tmpStr.getBytes());
            fw.flush();
            fw.close();
        } catch (final Exception e) {
            e.printStackTrace();
            final String resp = "FAIL\n" + e.getMessage();
            logger.error(resp);
            return;
        }
    }

    /**
     * Scp file to server
     *
     * @param ip
     *            Server ip
     * @param username
     *            SSH login username
     * @param password
     *            SSH login password
     * @param srcFile
     *            The src file location
     * @param path
     *            The target file path
     */
    public static void scpPutFile(final String ip,
            final String username,
            final String password,
            final String srcFile,
            final String path) {
        try {
            final Connection conn = new Connection(ip);
            conn.connect();
            final boolean isAuthenticated = conn
                    .authenticateWithPassword(username, password);
            if (!isAuthenticated) {
                throw new IOException("Authentication failed.");
            }
            final SCPClient client = new SCPClient(conn);
            client.put(srcFile, path);
            conn.close();
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Authentication failed when trying to SCP put file: "
                    + srcFile);
        }
    }

    public static void sleep(final long msec) {
        try {
            Thread.sleep(msec);
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Excute the SSH command
     *
     * @param ip
     *            The server ip
     * @param username
     *            SSH login username
     * @param password
     *            SSH login password
     * @param command
     *            The command to excute
     * @return The SSH response code
     */
    public static String sshAndOperation(final String ip,
            final String username,
            final String password,
            final String command) {
        int respCode = 1;
        final StringBuffer result = new StringBuffer();
        try {
            final Connection conn = new Connection(ip);
            conn.connect();
            final boolean isAuthenticated = conn
                    .authenticateWithPassword(username, password);
            if (!isAuthenticated) {
                throw new IOException("Authentication failed.");
            }
            final Session sess = conn.openSession();
            sess.execCommand(command);
            while (sess.getExitStatus() != null) {
                respCode = sess.getExitStatus();
            }
            System.out.println("ExitCode: " + respCode);
            final InputStream stdout = new StreamGobbler(sess.getStdout());
            final BufferedReader br = new BufferedReader(
                                                         new InputStreamReader(stdout));
            while (true) {
                final String line = br.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
                result.append(line).append("\n");
            }
            sess.close();
            conn.close();
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Authentication failed when trying to operate on XMA using SSH: "
                    + command);
        }
        return result.toString() + "\nExitCode:" + respCode;
    }

    /**
     * Update the wsdl file url with specified service
     *
     * @param urlStr
     *            The url to update to
     * @param serviceName
     *            The service name
     * @param newName
     *            Other related service name
     */
    public static void updateWsdlUrl(String urlStr,
            String serviceName,
            final String... newName) {
        // Replace the service file url
        String wsdlFileName = "wsdl" + File.separator + serviceName + ".wsdl";
        // replaceSoapUrl(wsdlFileName, urlStr);
        // Replace the other related services files url
        for (final String relatedServiceName : newName) {
            urlStr = urlStr.replaceAll(serviceName, relatedServiceName);
            wsdlFileName = wsdlFileName.replaceAll(serviceName,
                                                   relatedServiceName);
            replaceSoapUrl(wsdlFileName, urlStr);
            serviceName = relatedServiceName;
        }
    }

    /**
     * Upper the first char of str value
     *
     * @param str
     *            The str value need to treat
     * @return The str value with first char Upper case
     */
    public static String upperFirstChar(final String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static boolean validateIpAddress(final String ip) {
        final Pattern pattern = Pattern.compile(PATTERN);
        final Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}
