package com.polycom.sqa.utils;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class JsonUtils {
    static Logger logger = Logger.getLogger(JsonUtils.class);

    @SuppressWarnings("deprecation")
    public static <T> T convertToJavaBean(final String json, final Class<T> c) {
        T t = null;
        final ObjectMapper mapper = new ObjectMapper();
        logger.info("Begin to convert the Json message to java bean...");
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                         false);
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        final JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper.registerModule(module);
        mapper.setSerializationInclusion(Include.NON_NULL);
        try {
            t = mapper.readValue(json, c);
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
        return t;
    }

    @SuppressWarnings("deprecation")
    public static String convertToJson(final Object jaxbObject) {
        String rtn = "";
        // final String jaxbObjectPackageName =
        // jaxbObject.getClass().getPackage()
        // .getName();
        final ObjectMapper mapper = new ObjectMapper();
        logger.info("Begin to convert the java object to JSON message...");
        final AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
        final AnnotationIntrospector secondary = new JaxbAnnotationIntrospector();
        final AnnotationIntrospector pair = AnnotationIntrospector
                .pair(primary, secondary);
        mapper.setAnnotationIntrospector(pair);
        // mapper.setSerializationInclusion(Include.NON_NULL);
        final JacksonJsonProvider provider = new JacksonJsonProvider();
        provider.setMapper(mapper);
        try {
            rtn = mapper.writeValueAsString(jaxbObject);
            logger.info("The message after convert is: " + rtn);
        } catch (final JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rtn;
    }
}
