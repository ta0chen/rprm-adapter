package com.polycom.sqa.xma.webservices.driver.interceptor;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;

import org.apache.commons.lang3.ClassUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.polycom.sqa.rest.RestHandler;
import com.polycom.sqa.utils.CommonUtils;

public class JsonInvocationHandler extends RestHandler
        implements InvocationHandler {
    private static JsonInvocationHandler instance  = null;
    private static long                  startTime = 0;
    protected static ObjectMapper        MAPPER    = null;

    public static JsonInvocationHandler getInstance(final String url) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                         false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,
                         true);
        final JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper.registerModule(module);
        mapper.setSerializationInclusion(Include.NON_NULL);
        return getInstance(url, mapper);
    }

    public static JsonInvocationHandler getInstance(final String url,
                                                    final ObjectMapper mapper) {
        final long currentTime = System.currentTimeMillis();
        if (instance == null || (currentTime - startTime) > 1000 * 30) {
            instance = new JsonInvocationHandler(url);
            logger.info("Create the JsonInvocationHandler instance");
            startTime = currentTime;
        }
        instance.url = url;
        MAPPER = mapper;
        return instance;
    }

    private String url;

    private JsonInvocationHandler(final String url) {
        super();
        this.url = url;
    }

    @Override
    public String build(final String cmd) {
        return null;
    }

    public Object getProxy(final Class<?> cls) {
        return Proxy.newProxyInstance(
                                      Thread.currentThread()
                                              .getContextClassLoader(),
                                      new Class[] { cls },
                                      this);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object invoke(final Object proxy,
                         final Method method,
                         final Object[] args) throws Throwable {
        logger.info("========in JsonInvocationHandler=======");
        logger.info("method==" + method.getName());
        final RequestWrapper requestWrapperAnno = method
                .getAnnotation(RequestWrapper.class);
        final String methodName = requestWrapperAnno.localName();
        final Parameter[] parameters = method.getParameters();
        final Map<String, Object> valueMap = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            Object arg = args[i];
            final WebParam webParamAnno = parameter
                    .getAnnotation(WebParam.class);
            // Create new ArrayList for list field
            if (arg == null
                    && List.class.isAssignableFrom(parameter.getType())) {
                arg = new ArrayList();
            } else {
                CommonUtils.initList(arg);
            }
            if (Mode.OUT.equals(webParamAnno.mode())) {
                continue;
            }
            if (arg instanceof Holder) {
                valueMap.put(webParamAnno.name(), ((Holder) arg).value);
            } else {
                valueMap.put(webParamAnno.name(), arg);
            }
        }
        final Map<String, Map<String, Object>> requestMap = new HashMap<>();
        requestMap.put(methodName, valueMap);
        String requestString = MAPPER.writeValueAsString(requestMap);
        if (requestString.contains("Acl")) {
            requestString = requestString.replaceAll("\"DN\"", "\"dn\"");
            requestString = requestString.replaceAll("\"GUID\"", "\"guid\"");
            requestString = requestString.replaceAll("\"GUIDAsString\"",
                                                     "\"guidasString\"");
        }
        Protocol protocal = Protocol.HTTP;
        if (url.contains("https://")) {
            protocal = Protocol.HTTPS;
        }
        final Representation result = new StringRepresentation(requestString);
        result.setMediaType(MediaType.APPLICATION_JSON);
        final String fullUrl = url + "/"
                + method.getDeclaringClass().getSimpleName();
        logger.info("Post url is:\n" + fullUrl);
        logger.info("Post message is:\n" + requestString);
        connect(fullUrl, protocal, "", "");
        String responseString = resource.post(result).getText();
        if (method.getName().equals("searchConfGuests")) {
        	responseString = responseString.replace("\"sipuri\":\"\",", "");
        }
        logger.info("Response message is:\n" + responseString);
        final Map<String, Map<String, Object>> responseMap = MAPPER
                .readValue(responseString, Map.class);
        final Map<String, Object> responseParam = responseMap
                .get(methodName + "Response");
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final WebParam webParamAnno = parameter
                    .getAnnotation(WebParam.class);
            if (!Mode.OUT.equals(webParamAnno.mode())
                    && !Mode.INOUT.equals(webParamAnno.mode())) {
                continue;
            }
            if (Holder.class.equals(parameter.getType())) {
                Object holderValue = null;
                final Object value = responseParam.get(webParamAnno.name());
                final Type holderType = parameter.getParameterizedType();
                final Type inSideType = ((ParameterizedType) holderType)
                        .getActualTypeArguments()[0];
                // Holder<List<>>
                if (inSideType instanceof ParameterizedType) {
                    final ParameterizedType parameterizedInSideType = (ParameterizedType) inSideType;
                    final Class<?> rawType = ClassUtils
                            .getClass(parameterizedInSideType.getRawType()
                                    .getTypeName());
                    // Holder<List<>>
                    if (ClassUtils.isAssignable(rawType, Collection.class)) {
                        holderValue = processCollection(parameterizedInSideType,
                                                        value);
                    }
                } else {
                    holderValue = MAPPER
                            .readValue(MAPPER.writeValueAsString(value),
                                       ClassUtils.getClass(inSideType
                                               .getTypeName()));
                }
                final Holder holder = (Holder) args[i];
                holder.value = holderValue;
            }
        }
        final Class retunType = method.getReturnType();
        final Object returnValue = MAPPER.readValue(
                                                    MAPPER.writeValueAsString(responseParam
                                                            .get("return")),
                                                    retunType);
        return returnValue;
    }

    @Override
    protected void login(final String username, final String password)
            throws IOException {
    }

    private Collection<?> processCollection(final ParameterizedType holderType,
                                            final Object value)
                                                    throws Exception {
        final Type inSideType = holderType.getActualTypeArguments()[0];
        final Collection<?> collection = (Collection<?>) value;
        final List<Object> listParam = new ArrayList<>();
        if (value == null) {
            return listParam;
        }
        for (final Object subObj : collection) {
           String tmp = MAPPER.writeValueAsString(subObj);
            final Class<?> cls = ClassUtils.getClass(inSideType.getTypeName());
            final Object holderValue = MAPPER.readValue(tmp, cls);
            listParam.add(holderValue);
        }
        return listParam;
    }
}
