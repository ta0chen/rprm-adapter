package com.polycom.webservices.driver.customBeans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.polycom.webservices.NumberingSchemeService.JE164RangeRule;

public class E164RangeRule extends JE164RangeRule {
    @JsonProperty(value = "class")
    private String class1 = "com.polycom.rm.shared.types.provisionRules.E164RangeRule";

    public String getClass1() {
        return class1;
    }

    public void setClass1(final String class1) {
        this.class1 = class1;
    }
}
