package com.polycom.webservices;

import java.io.IOException;

import com.polycom.sqa.xma.webservices.driver.NamingSchemeServiceHandler;
import com.polycom.webservices.NamingSchemeService.JH323AvailableField;
import com.polycom.webservices.NamingSchemeService.JH323IDScheme;
import com.polycom.webservices.NamingSchemeService.JSIPAvailableField;
import com.polycom.webservices.NamingSchemeService.JSIPURIDto;
import com.polycom.webservices.NamingSchemeService.JSIPURIScheme;
import com.polycom.webservices.NamingSchemeService.JSipUriLevel;
import com.polycom.webservices.NamingSchemeService.JStatus;
import com.polycom.webservices.NamingSchemeService.JSystemAvailableField;
import com.polycom.webservices.NamingSchemeService.JSystemNameScheme;
import com.polycom.webservices.NamingSchemeService.JWebResult;

/**
 * Naming Scheme handler. This class will handle the webservice request of
 * Naming Scheme module
 *
 * @author wbchao
 *
 */
public class NamingSchemeHandler extends XMAWebServiceHandler {
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
        // "sysNamingField1=city sysSeparator1=none sysNamingField2=last~name sysSeparator2=none sysNamingField3=device~type sysSeparator3=comma sysNamingField4=first~name isUseSysNameForHostname=true isUseSysNameForH323ID=false h323IdNamingField1=first~name h323IdSeparator1=none h323IdNamingField2=city h323IdSeparator2=none h323IdNamingField3=last~name h323IdSeparator3=none h323IdNamingField4=domain ";
        final String method = "updateSIPURIScheme ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params1 = "enableFlag=false ";
        final String command = "http://localhost:8888/PlcmRmWeb/JNamingSchemeService NamingSchemeService "
                + method + auth + params1;
        final NamingSchemeHandler handler = new NamingSchemeHandler(command);
        final String result = handler.build();
        System.out.println("Result==" + result);
    }

    private final NamingSchemeServiceHandler namingSchemeServiceHandler;

    public NamingSchemeHandler(final String cmd) throws IOException {
        super(cmd);
        namingSchemeServiceHandler = new NamingSchemeServiceHandler(
                                                                    webServiceUrl);
    }

    /**
     * Internal method, convert the H323 id scheme
     *
     * @param info
     *            The H323 info
     * @param infoType
     *            The H323 info type
     * @return JH323AvailableField
     */
    private JH323AvailableField
    changeH323IdSchemeInfoFromStringToField(final String info,
            final String infoType) {
        String toChangeInfo = info.toUpperCase().replace('~', '_');
        if (infoType.equals("field")) {
            toChangeInfo = "H323FIELD_" + toChangeInfo;
        } else if (infoType.equals("separator")) {
            toChangeInfo = "SEP_" + toChangeInfo;
        }
        return JH323AvailableField.fromValue(toChangeInfo);
    }

    /**
     * Internal method, convert the sip scheme
     *
     * @param info
     *            The sip info
     * @param infoType
     *            The sip info type
     * @return JSIPAvailableField
     */
    private JSIPAvailableField
    changeSIPURISchemeInfoFromStringToField(final String info,
            final String infoType) {
        String toChangeInfo = info.toUpperCase().replace('~', '_');
        if (infoType.equals("field")) {
            toChangeInfo = "SIPFIELD_" + toChangeInfo;
        } else if (infoType.equals("separator")) {
            if (toChangeInfo.equals("-")) {
                toChangeInfo = "SEP_DASH";
            } else if (toChangeInfo.equals("_")) {
                toChangeInfo = "SEP_UNDERSCORE";
            } else if (toChangeInfo.equals(".")) {
                toChangeInfo = "SEP_DOT";
            } else if (toChangeInfo.equals("@")) {
                toChangeInfo = "SEP_ATSIGN";
            } else {
                toChangeInfo = "SEP_" + toChangeInfo;
            }
        }
        return JSIPAvailableField.fromValue(toChangeInfo);
    }

    /**
     * Internal method, convert the system name scheme
     *
     * @param info
     *            The system name info
     * @param infoType
     *            The system name info type
     * @return JSystemAvailableField
     */
    private JSystemAvailableField
    changeSystemNameSchemeInfoFromStringToField(final String info,
            final String infoType) {
        String toChangeInfo = info.toUpperCase().replace('~', '_');
        if (infoType.equals("field")) {
            toChangeInfo = "SYSFIELD_" + toChangeInfo;
        } else if (infoType.equals("separator")) {
            toChangeInfo = "SEP_" + toChangeInfo;
        }
        return JSystemAvailableField.fromValue(toChangeInfo);
    }

    @Override
    protected void injectCmdArgs() {
        put("sysNamingField1", "");
        put("sysSeparator1", "");
        put("sysNamingField2", "");
        put("sysSeparator2", "");
        put("sysNamingField3", "");
        put("sysSeparator3", "");
        put("sysNamingField4", "");
        put("isUseSysNameForHostname", "");
        put("isUseSysNameForH323ID", "");
        put("h323IdNamingField1", "");
        put("h323IdSeparator1", "");
        put("h323IdNamingField2", "");
        put("h323IdSeparator2", "");
        put("h323IdNamingField3", "");
        put("h323IdSeparator3", "");
        put("h323IdNamingField4", "");
        put("namingField1", "");
        put("separator1", "");
        put("namingFieldAdAttribute1", "");
        put("separatorAdAttribute1", "");
        put("namingField2", "");
        put("separator2", "");
        put("namingFieldAdAttribute2", "");
        put("separatorAdAttribute2", "");
        put("namingField3", "");
        put("separator3", "");
        put("namingFieldAdAttribute3", "");
        put("separatorAdAttribute3", "");
        put("namingField4", "");
        put("namingFieldAdAttribute4", "");
        put("sipUriOnlyEMail", "false");
        put("domainName", "");
        put("keepUrisUnique", "");
        put("onlyEMail", "");
        put("enableFlag", "");
    }

    /**
     * Update the sip uri scheme
     *
     * @see enableFlag=true <br/>
     *      onlyEMail=false <br/>
     *      namingField1=Title <br/>
     *      separator1=none <br/>
     *      namingField2=AD_attribute <br/>
     *      namingFieldAdAttribute2=ipphone <br/>
     *      separator2=ATSIGN <br/>
     *      namingField3=none <br/>
     *      separator3=none <br/>
     *      namingField4=none <br/>
     *      domainName=pctc.com <br/>
     *      keepUrisUnique=true
     *
     * @param namingField
     *            [1-4] The naming field
     * @param namingFieldAdAttribute
     *            [1-4] The naming field attribute value
     * @param separator
     *            [1-3] The separator
     * @param onlyEMail
     *            Whether only use email as sip uri
     * @param keepUrisUnique
     *            The level to keep sip uri unique(Optional, default is
     *            User_Level)
     * @param domainName
     *            The domain name
     * @param enableFlag
     *            Whether enable(Optional, default is true)
     * @return The result
     */
    public String updateSIPURIScheme() {
        String status = "Failed";
        final JSIPURIScheme scheme = namingSchemeServiceHandler
                .getSIPURIScheme(userToken, -1);
        if (scheme != null) {
            // clear the fields
            scheme.getFields().clear();
        }
        for (int i = 1; i <= 4; i++) {
            final String namingValue = inputCmd.get("namingField" + i);
            if (namingValue != null && !namingValue.isEmpty()) {
                final JSIPAvailableField field = changeSIPURISchemeInfoFromStringToField(namingValue,
                        "field");
                final JSIPURIDto SIPURIDto = new JSIPURIDto();
                SIPURIDto.setType(field);
                final String namingFieldAdAttribute = inputCmd
                        .get("namingFieldAdAttribute" + i);
                if (namingFieldAdAttribute != null
                        && !namingFieldAdAttribute.isEmpty()) {
                    SIPURIDto.setAdAttribute(namingFieldAdAttribute);
                } else {
                    SIPURIDto.setAdAttribute("");
                }
                scheme.getFields().add(SIPURIDto);
            }
            final String separator = inputCmd.get("separator" + i);
            if (separator != null && !separator.isEmpty()) {
                final JSIPAvailableField field = changeSIPURISchemeInfoFromStringToField(separator,
                        "separator");
                final JSIPURIDto SIPURIDto = new JSIPURIDto();
                SIPURIDto.setType(field);
                final String separatorAdAttribute = inputCmd
                        .get("separatorAdAttribute" + i);
                if (separatorAdAttribute != null
                        && !separatorAdAttribute.isEmpty()) {
                    SIPURIDto.setAdAttribute(separatorAdAttribute);
                } else {
                    SIPURIDto.setAdAttribute("");
                }
                scheme.getFields().add(SIPURIDto);
            }
        }
        final String onlyEMail = inputCmd.get("onlyEMail");
        if (!onlyEMail.isEmpty()) {
            scheme.setOnlyEMail(Boolean.parseBoolean(onlyEMail));
        }
        final String keepUrisUnique = inputCmd.get("keepUrisUnique");
        if (!keepUrisUnique.isEmpty()) {
            if (keepUrisUnique.equalsIgnoreCase("true")) {
                scheme.setSiplevel(JSipUriLevel.GLOBAL___LEVEL);
            } else {
                scheme.setSiplevel(JSipUriLevel.USER___LEVEL);
            }
        }
        final String domainName = inputCmd.get("domainName");
        if (!domainName.isEmpty()) {
            if (domainName.equalsIgnoreCase("disable")) {
                scheme.setDomainName(null);
            } else {
                scheme.setDomainName(domainName);
            }
        }
        final String enableFlag = inputCmd.get("enableFlag");
        if (!enableFlag.isEmpty()) {
            if (enableFlag.equalsIgnoreCase("false")) {
                scheme.setEnabledflag(false);
            } else {
                scheme.setEnabledflag(true);
            }
        }
        final JWebResult result = namingSchemeServiceHandler
                .updateSIPURIScheme(userToken, scheme);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Update SIPURI scheme successfully.");
            status = "SUCCESS";
        } else {
            logger.info("Update SIPURI scheme failed, please make sure your scheme information is correct");
            status = "Failed";
        }
        return status;
    }

    /**
     * Update the system name scheme
     *
     * @see sysNamingField1=last~name <br/>
     *      sysSeparator1=none <br/>
     *      sysNamingField2=first~name <br/>
     *      sysSeparator2=none <br/>
     *      sysNamingField3=device~type <br/>
     *      sysSeparator3=none <br/>
     *      sysNamingField4=none <br/>
     *      isUseSysNameForHostname=true <br/>
     *      isUseSysNameForH323ID=false <br/>
     *      h323IdNamingField1=last~name <br/>
     *      h323IdSeparator1=none <br/>
     *      h323IdNamingField2=first~name <br/>
     *      h323IdSeparator2=none <br/>
     *      h323IdNamingField3=title <br/>
     *      h323IdSeparator3=none <br/>
     *      h323IdNamingField4=none
     *
     *
     * @param sysNamingField
     *            [1-4] The system naming field
     * @param sysSeparator
     *            [1-3] The system naming separator
     * @param h323IdNamingField
     *            [1-4] The h323 id naming field
     * @param h323IdSeparator
     *            [1-3] The h323 id naming separator
     * @param isUseSysNameForHostname
     *            Whether use system name for host name
     * @param isUseSysNameForH323ID
     *            Whether use system name for h323 id
     * @return The result
     */
    public String updateSystemNameScheme() {
        String status = "Failed";
        final JSystemNameScheme systemNameScheme = namingSchemeServiceHandler
                .getSystemNameScheme(userToken, -1);
        final JH323IDScheme h323IdScheme = namingSchemeServiceHandler
                .getH323IDScheme(userToken, -1);
        boolean isUseSysNameForHostname = false;
        boolean isUseSysNameForH323ID = false;
        if (inputCmd.get("isUseSysNameForHostname").equals("true")) {
            isUseSysNameForHostname = true;
        }
        if (inputCmd.get("isUseSysNameForH323ID").equals("true")) {
            isUseSysNameForH323ID = true;
        }
        if (namingSchemeServiceHandler
                .UpdateSystemNameScheme(userToken,
                                        changeSystemNameSchemeInfoFromStringToField(inputCmd.get("sysNamingField1"),
                                                "field"),
                                                changeSystemNameSchemeInfoFromStringToField(inputCmd.get("sysSeparator1"),
                                                        "separator"),
                                                        changeSystemNameSchemeInfoFromStringToField(inputCmd.get("sysNamingField2"),
                                                                "field"),
                                                                changeSystemNameSchemeInfoFromStringToField(inputCmd.get("sysSeparator2"),
                                                                        "separator"),
                                                                        changeSystemNameSchemeInfoFromStringToField(inputCmd.get("sysNamingField3"),
                                                                                "field"),
                                                                                changeSystemNameSchemeInfoFromStringToField(inputCmd.get("sysSeparator3"),
                                                                                        "separator"),
                                                                                        changeSystemNameSchemeInfoFromStringToField(inputCmd.get("sysNamingField4"),
                                                                                                "field"),
                                                                                                systemNameScheme
                                                                                                .getSystemNameSchemeGuid(),
                                                                                                isUseSysNameForHostname,
                                                                                                isUseSysNameForH323ID).getStatus()
                                                                                                .compareTo(JStatus.SUCCESS) == 0) {
            logger.info("Update system naming scheme successfully.");
            if (isUseSysNameForH323ID == false) {
                if (namingSchemeServiceHandler
                        .UpdateH323IDScheme(userToken,
                                            changeH323IdSchemeInfoFromStringToField(inputCmd.get("h323IdNamingField1"),
                                                    "field"),
                                                    changeH323IdSchemeInfoFromStringToField(inputCmd.get("h323IdSeparator1"),
                                                            "separator"),
                                                            changeH323IdSchemeInfoFromStringToField(inputCmd.get("h323IdNamingField2"),
                                                                    "field"),
                                                                    changeH323IdSchemeInfoFromStringToField(inputCmd.get("h323IdSeparator2"),
                                                                            "separator"),
                                                                            changeH323IdSchemeInfoFromStringToField(inputCmd.get("h323IdNamingField3"),
                                                                                    "field"),
                                                                                    changeH323IdSchemeInfoFromStringToField(inputCmd.get("h323IdSeparator3"),
                                                                                            "separator"),
                                                                                            changeH323IdSchemeInfoFromStringToField(inputCmd.get("h323IdNamingField4"),
                                                                                                    "field"),
                                                                                                    h323IdScheme
                                                                                                    .getSystemNameSchemeGuid())
                                                                                                    .getStatus().compareTo(JStatus.SUCCESS) == 0) {
                    logger.info("Update h323id scheme successfully.");
                    status = "SUCCESS";
                } else {
                    logger.info("Update h323id scheme failed, please make sure your scheme information is correct");
                    status = "Failed";
                }
            } else {
                logger.info("isUseSysNameForH323ID false, no need to update h323 id scheme");
                status = "SUCCESS";
            }
        } else {
            logger.error("Update naming scheme failed, please make sure your scheme information is correct.");
            status = "Failed";
        }
        return status;
    }
}
