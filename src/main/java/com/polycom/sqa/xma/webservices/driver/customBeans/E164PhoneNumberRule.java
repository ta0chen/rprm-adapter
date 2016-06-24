package com.polycom.sqa.xma.webservices.driver.customBeans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.polycom.webservices.NumberingSchemeService.JE164PhoneNumberRule;

public class E164PhoneNumberRule extends JE164PhoneNumberRule {
    @JsonProperty(value = "class")
    private String class1 = "com.polycom.rm.shared.types.provisionRules.E164PhoneNumberRule";

    public String getClass1() {
        return class1;
    }

    public void setClass1(final String class1) {
        this.class1 = class1;
    }
}
