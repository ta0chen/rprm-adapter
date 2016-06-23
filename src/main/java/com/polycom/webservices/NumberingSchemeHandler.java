package com.polycom.webservices;

import java.io.IOException;
import java.util.List;

import com.polycom.sqa.xma.webservices.driver.NumberingSchemeServiceHandler;
import com.polycom.sqa.xma.webservices.driver.customBeans.E164DeviceTypeRule;
import com.polycom.sqa.xma.webservices.driver.customBeans.E164EmptyRule;
import com.polycom.sqa.xma.webservices.driver.customBeans.E164NumberRule;
import com.polycom.sqa.xma.webservices.driver.customBeans.E164PhoneNumberRule;
import com.polycom.sqa.xma.webservices.driver.customBeans.E164RangeRule;
import com.polycom.webservices.NumberingSchemeService.JE164DeviceTypeRule;
import com.polycom.webservices.NumberingSchemeService.JE164NumberRule;
import com.polycom.webservices.NumberingSchemeService.JE164PhoneNumberRule;
import com.polycom.webservices.NumberingSchemeService.JE164RangeRule;
import com.polycom.webservices.NumberingSchemeService.JE164RulesList;
import com.polycom.webservices.NumberingSchemeService.JStatus;
import com.polycom.webservices.NumberingSchemeService.JWebResult;

/**
 * Numbering Scheme handler. This class will handle the webservice request of
 * Numbering Scheme module
 *
 * @author wbchao
 *
 */
public class NumberingSchemeHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "updateSystemNameScheme ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "sysNamingField1=city sysSeparator1=none sysNamingField2=last~name
        // sysSeparator2=none sysNamingField3=device~type sysSeparator3=comma
        // sysNamingField4=first~name isUseSysNameForHostname=true
        // isUseSysNameForH323ID=false h323IdNamingField1=first~name
        // h323IdSeparator1=none h323IdNamingField2=city h323IdSeparator2=none
        // h323IdNamingField3=last~name h323IdSeparator3=none
        // h323IdNamingField4=domain ";
        final String method = "updateE164Scheme ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params1 = "prefixType=defaultDeviceTypePrefix prefixDeviceNumber_GS=22 prefixDeviceNumber_RPD=11 prefixDeviceNumber_ITP=44 prefixDeviceNumber_CMA=33 prefixDeviceNumber_RPM=55 prefixDeviceNumber_HDX=66 prefixDeviceNumber_VVX=77 baseFieldType=specifyNumberRange numberDigits=5 startNumber=1 endNumber=999 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JNumberingSchemeService NumberingSchemeService "
                + method + auth + params1;
        final NumberingSchemeHandler handler = new NumberingSchemeHandler(
                                                                          command);
        final String result = handler.build();
        System.out.println("Result==" + result);
    }

    private final NumberingSchemeServiceHandler nssh;

    public NumberingSchemeHandler(final String cmd) throws IOException {
        super(cmd);
        nssh = new NumberingSchemeServiceHandler(webServiceUrl);
    }

    @Override
    protected void injectCmdArgs() {
        put("prefixType", "");
        put("prefixNumber", "");
        put("prefixDeviceNumber_GS", "44");
        put("prefixDeviceNumber_RPD", "66");
        put("prefixDeviceNumber_ITP", "77");
        put("prefixDeviceNumber_CMA", "22");
        put("prefixDeviceNumber_RPM", "55");
        put("prefixDeviceNumber_HDX", "11");
        put("prefixDeviceNumber_VVX", "33");
        put("prefixDeviceNumber_Centro", "88");
        put("prefixDeviceNumber_Debut", "99");
        put("baseFieldType", "");
        put("attributeName", "");
        put("numberDigits", "");
        put("startNumber", "");
        put("endNumber", "");
        put("suffixNumber", "");
    }

    /**
     * Update the E164 scheme
     *
     * @see prefixType=numberPrefix <br/>
     *      prefixNumber=9 <br/>
     *      baseFieldType=usePhoneNumber <br/>
     *      attributeName=telephonenumber <br/>
     *      numberDigits=4 <br/>
     *      startNumber=33 <br/>
     *      endNumber=99 <br/>
     *      suffixNumber=6
     *
     * @param prefixType
     *            The prefix type
     * @param prefixNumber
     *            The prefix number
     * @param attributeName
     *            The attribute name
     * @param numberDigits
     *            The number digists
     * @param startNumber
     *            The start number
     * @param endNumber
     *            The end number
     * @param suffixNumber
     *            The suffix number
     * @return The result
     */
    public String updateE164Scheme() {
        final String status = "SUCCESS";
        final JE164RulesList tmp = new JE164RulesList();
        // Prefix
        final String prefixType = inputCmd.get("prefixType");
        if ("noPrefix".equals(prefixType)) {
            tmp.getJE164RulesBase().add(new E164EmptyRule());
        } else if ("numberPrefix".equals(prefixType)) {
            final JE164NumberRule numberRule = new E164NumberRule();
            numberRule.setNumber(inputCmd.get("prefixNumber"));
            tmp.getJE164RulesBase().add(numberRule);
        } else if ("deviceTypePrefix".equals(prefixType)) {
            final JE164DeviceTypeRule deviceTypeRule = new E164DeviceTypeRule();
            final List<String> keyList = deviceTypeRule.getPrefixMapKeys();
            final List<String> valueList = deviceTypeRule.getPrefixMapValues();
            keyList.clear();
            valueList.clear();
            keyList.add("GroupSeries");
            valueList.add(inputCmd.get("prefixDeviceNumber_GS"));
            keyList.add("RP-Desktop");
            valueList.add(inputCmd.get("prefixDeviceNumber_RPD"));
            keyList.add("ITP");
            valueList.add(inputCmd.get("prefixDeviceNumber_ITP"));
            keyList.add("CMA-Desktop");
            valueList.add(inputCmd.get("prefixDeviceNumber_CMA"));
            keyList.add("RP-Mobile");
            valueList.add(inputCmd.get("prefixDeviceNumber_RPM"));
            keyList.add("HDX");
            valueList.add(inputCmd.get("prefixDeviceNumber_HDX"));
            keyList.add("VVX");
            valueList.add(inputCmd.get("prefixDeviceNumber_VVX"));
            keyList.add("RP-Centro");
            valueList.add(inputCmd.get("prefixDeviceNumber_Centro"));
            keyList.add("RP-Debut");
            valueList.add(inputCmd.get("prefixDeviceNumber_Debut"));
            tmp.getJE164RulesBase().add(deviceTypeRule);
        } else {
            logger.error("prefix type is not right.");
            return "Failed, prefix type is not right.";
        }
        // Base Field
        final String baseFieldType = inputCmd.get("baseFieldType");
        if ("specifyNumberRange".equals(baseFieldType)) {
            final JE164RangeRule rangeRule = new E164RangeRule();
            rangeRule.setLastValue(0);
            rangeRule.setStartValue(Integer
                                    .parseInt(inputCmd.get("startNumber")));
            rangeRule.setEndValue(Integer.parseInt(inputCmd.get("endNumber")));
            rangeRule.setNumDigits(Integer.parseInt(Integer
                                                    .toString(inputCmd.get("endNumber").length())));
            tmp.getJE164RulesBase().add(1, rangeRule);
        } else if ("usePhoneNumber".equals(baseFieldType)) {
            final JE164RangeRule rangeRule = new E164RangeRule();
            rangeRule.setLastValue(0);
            rangeRule.setStartValue(Integer
                                    .parseInt(inputCmd.get("startNumber")));
            rangeRule.setEndValue(Integer.parseInt(inputCmd.get("endNumber")));
            rangeRule.setNumDigits(Integer.parseInt(Integer
                                                    .toString(inputCmd.get("endNumber").length())));
            final JE164PhoneNumberRule phoneNumberRule = new E164PhoneNumberRule();
            phoneNumberRule.setAdAttributeName(inputCmd.get("attributeName"));
            phoneNumberRule.setNumDigits(Integer
                                         .parseInt(inputCmd.get("numberDigits")));
            phoneNumberRule.setBackupRule(rangeRule);
            tmp.getJE164RulesBase().add(1, phoneNumberRule);
        } else {
            logger.error("base field is not right.");
            return "Failed, base field is not right.";
        }
        // Suffix
        final String suffixNumber = inputCmd.get("suffixNumber");
        if (suffixNumber.isEmpty()) {
            tmp.getJE164RulesBase().add(2, new E164EmptyRule());
        } else {
            final JE164NumberRule numberRule = new E164NumberRule();
            numberRule.setNumber(suffixNumber);
            tmp.getJE164RulesBase().add(2, numberRule);
        }
        final JWebResult result = nssh.modifyE164NumberingScheme(userToken,
                                                                 tmp);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("e164 number scheme is successfully updated.");
        } else {
            logger.error("e164 number scheme is failed to update.");
            return "Failed, e164 number scheme is failed to update.";
        }
        return status;
    }
}
