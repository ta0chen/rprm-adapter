package com.polycom.webservices.driver.interceptor;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("serial")
public class CustomObjectMapper extends ObjectMapper {
    @SuppressWarnings("unchecked")
    @Override
    public <T> T readValue(final String content, final Class<T> valueType)
            throws IOException, JsonParseException, JsonMappingException {
        String tmp = content;
        if (valueType.getName()
                .equals("com.polycom.webservices.GroupManager.JGroup")) {
            tmp = tmp.replaceAll("guidasString", "GUIDAsString");
            tmp = tmp.replaceAll("guid", "GUID");
            tmp = tmp.replaceAll("dn", "DN");
        }
        return (T) _readMapAndClose(_jsonFactory.createParser(tmp),
                                    _typeFactory.constructType(valueType));
    }

    @Override
    public String writeValueAsString(final Object value)
            throws JsonProcessingException {
        String requestString = "";
        // alas, we have to pull the recycler directly here...
        final SegmentedStringWriter sw = new SegmentedStringWriter(
                                                                   _jsonFactory._getBufferRecycler());
        try {
            _configAndWriteValue(_jsonFactory.createGenerator(sw), value);
        } catch (final JsonProcessingException e) { // to support [JACKSON-758]
            throw e;
        } catch (final IOException e) { // shouldn't really happen, but is
            // declared as possibility so:
            throw JsonMappingException.fromUnexpectedIOE(e);
        }

        requestString = sw.getAndClear();
        if (requestString.contains("\"GUIDAsString\"")) {
            requestString = requestString.replaceAll("\"GUIDAsString\"",
                    "\"guidasString\"");
        }
        if (requestString.contains("\"GUID\"")) {
            requestString = requestString.replaceAll("\"GUID\"", "\"guid\"");
        }
        if (requestString.contains("\"DN\"")) {
            requestString = requestString.replaceAll("\"DN\"", "\"dn\"");
        }
        return requestString;
    }
}
