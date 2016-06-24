package com.polycom.sqa.xma.webservices.driver.customBeans;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.polycom.webservices.GroupManager.JGroup;
import com.polycom.webservices.NumberingSchemeService.JE164EmptyRule;

public class E164EmptyRule extends JE164EmptyRule {
    public static void main(final String[] args)
            throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(
                               DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                               false);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,
                               true);
        final JaxbAnnotationModule module = new JaxbAnnotationModule();
        objectMapper.registerModule(module);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        final JGroup group = new JGroup();
        group.setGUID("11543453451");
        final String groupStr = objectMapper.writeValueAsString(group);
        System.out.println(groupStr);
    }

    @JsonProperty(value = "class")
    private String class1 = "com.polycom.rm.shared.types.provisionRules.E164EmptyRule";

    public String getClass1() {
        return class1;
    }

    public void setClass1(final String class1) {
        this.class1 = class1;
    }
}
