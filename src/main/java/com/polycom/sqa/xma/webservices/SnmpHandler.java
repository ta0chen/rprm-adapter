package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.SnmpServiceHandler;
import com.polycom.webservices.SnmpService.JNotificationReceiver;
import com.polycom.webservices.SnmpService.JSNMPAuthenticationType;
import com.polycom.webservices.SnmpService.JSNMPEncryptionType;
import com.polycom.webservices.SnmpService.JSNMPNotificationType;
import com.polycom.webservices.SnmpService.JSNMPTransportType;
import com.polycom.webservices.SnmpService.JSNMPVersion;
import com.polycom.webservices.SnmpService.JSnmpConfiguration;
import com.polycom.webservices.SnmpService.JSnmpUSMUser;
import com.polycom.webservices.SnmpService.JStatus;
import com.polycom.webservices.SnmpService.JWebResult;

/**
 * Snmp handler. This class will handle the webservice request of Snmp module
 *
 * @author wbchao
 *
 */
public class SnmpHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "addUser ";
        // final String params = " authenticationPassword=Polycom123
        // authenticationType=MD_5 encryptionPassword=Polycom123
        // encryptionType=DES remoteUser=usermd5des";
        final String method = "initSnmpConfiguration ";
        final String params = " ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String command = "https://172.21.120.181:8443/PlcmRmWeb/rest/JSnmpService SnmpService "
                + method + auth + params;
        final SnmpHandler handler = new SnmpHandler(command);
        final String result = handler.build();
        handler.logger.info("result==" + result);
    }

    private final SnmpServiceHandler snmpHandler;

    public SnmpHandler(final String cmd) throws IOException {
        super(cmd);
        snmpHandler = new SnmpServiceHandler(webServiceUrl);
    }

    /**
     * Add SNMP Receiver
     *
     * @see address=1.1.1.1<br/>
     *      enabled=true<br/>
     *      notificationType=TRAP<br/>
     *      receivePort=162<br/>
     *      snmpVersion=V_2_C<br/>
     *      transportType=UDP
     *
     * @param address
     *            The address
     * @param enabled
     *            whether enabled(Optional)
     * @param notificationType
     *            TRAP/INFORM(Optional)
     * @param receivePort
     *            The receivePort(Optional)
     * @param snmpVersion
     *            V_2_C/V_3(Optional)
     * @param transportType
     *            TCP/UDP(Optional)
     * @param remoteUser
     *            The remoteUser(Optional)
     * @return The result
     */
    public String addReceiver() {
        final JSnmpConfiguration snmpConfiguration = snmpHandler
                .getSnmpConfiguration(userToken);
        final JNotificationReceiver receiver = new JNotificationReceiver();
        receiver.setAddress(inputCmd.get("address"));
        receiver.setEnabled(Boolean.parseBoolean(inputCmd.get("enabled")));
        receiver.setNotificationType(JSNMPNotificationType
                .valueOf(inputCmd.get("notificationType")));
        receiver.setReceivePort(Integer.parseInt(inputCmd.get("receivePort")));
        receiver.setSnmpVersion(JSNMPVersion
                .valueOf(inputCmd.get("snmpVersion")));
        receiver.setTransportType(JSNMPTransportType
                .valueOf(inputCmd.get("transportType")));
        receiver.setRemoteUser(inputCmd.get("remoteUser"));
        List<JNotificationReceiver> recievers = snmpConfiguration
                .getRecievers();
        if (recievers == null) {
            recievers = new ArrayList<JNotificationReceiver>();
        }
        recievers.add(receiver);
        final JWebResult result = snmpHandler
                .saveSnmpConfiguration(userToken, snmpConfiguration);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Save SNMP configuration successfully.");
            return "SUCCESS";
        } else {
            logger.error("Fail to save SNMP configuration");
            return "Failed. Fail to save SNMP configuration. errorMsg is "
                    + result.getMessages();
        }
    }

    /**
     * Add SNMP user
     *
     * @see authenticationPassword=Polycom123<br/>
     *      authenticationType=MD_5<br/>
     *      encryptionPassword=Polycom123<br/>
     *      encryptionType=DES<br/>
     *      remoteUser=user1
     *
     * @param authenticationPassword
     *            The authenticationPassword
     * @param authenticationType
     *            SHA/MD_5
     * @param encryptionPassword
     *            The encryptionPassword
     * @param encryptionType
     *            DES/AES
     * @param remoteUser
     *            The remote username
     * @return The result
     */
    public String addUser() {
        final JSnmpConfiguration snmpConfiguration = snmpHandler
                .getSnmpConfiguration(userToken);
        final JSnmpUSMUser user = new JSnmpUSMUser();
        user.setAuthenticationPassword(inputCmd.get("authenticationPassword"));
        user.setAuthenticationType(JSNMPAuthenticationType
                .valueOf(inputCmd.get("authenticationType")));
        user.setEncryptionPassword(inputCmd.get("encryptionPassword"));
        user.setEncryptionType(JSNMPEncryptionType
                .valueOf(inputCmd.get("encryptionType")));
        user.setRemoteUser(inputCmd.get("remoteUser"));
        List<JSnmpUSMUser> securtiyUsers = snmpConfiguration.getSecurtiyUsers();
        if (securtiyUsers == null) {
            securtiyUsers = new ArrayList<JSnmpUSMUser>();
        }
        securtiyUsers.add(user);
        final JWebResult result = snmpHandler
                .saveSnmpConfiguration(userToken, snmpConfiguration);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Save SNMP configuration successfully.");
            return "SUCCESS";
        } else {
            logger.error("Fail to save SNMP configuration");
            return "Failed. Fail to save SNMP configuration. errorMsg is "
                    + result.getMessages();
        }
    }

    /**
     * Delete SNMP Receiver
     *
     * @see remoteUser=user1
     *
     * @param remoteUser
     *            The remote username to edit
     * @return The result
     */
    public String deleteReceiver() {
        final JSnmpConfiguration snmpConfiguration = snmpHandler
                .getSnmpConfiguration(userToken);
        final String address = inputCmd.get("address");
        final List<JNotificationReceiver> receivers = snmpConfiguration
                .getRecievers();
        if (receivers == null) {
            return "Failed, could not found SNMP agent " + address;
        }
        for (int i = 0; i < receivers.size(); i++) {
            if (receivers.get(i).getAddress().equals(address)) {
                receivers.remove(i);
                break;
            }
        }
        final JWebResult result = snmpHandler
                .saveSnmpConfiguration(userToken, snmpConfiguration);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Save SNMP configuration successfully.");
            return "SUCCESS";
        } else {
            logger.error("Fail to save SNMP configuration");
            return "Failed. Fail to save SNMP configuration. errorMsg is "
                    + result.getMessages();
        }
    }

    /**
     * Delete SNMP user
     *
     * @see remoteUser=user1
     *
     * @param remoteUser
     *            The remote username to edit
     * @return The result
     */
    public String deleteUser() {
        final JSnmpConfiguration snmpConfiguration = snmpHandler
                .getSnmpConfiguration(userToken);
        final String remoteUser = inputCmd.get("remoteUser");
        final List<JSnmpUSMUser> securtiyUsers = snmpConfiguration
                .getSecurtiyUsers();
        if (securtiyUsers == null) {
            return "Failed, could not found SNMP user " + remoteUser;
        }
        for (int i = 0; i < securtiyUsers.size(); i++) {
            if (securtiyUsers.get(i).getRemoteUser().equals(remoteUser)) {
                securtiyUsers.remove(i);
                break;
            }
        }
        final JWebResult result = snmpHandler
                .saveSnmpConfiguration(userToken, snmpConfiguration);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Save SNMP configuration successfully.");
            return "SUCCESS";
        } else {
            logger.error("Fail to save SNMP configuration");
            return "Failed. Fail to save SNMP configuration. errorMsg is "
                    + result.getMessages();
        }
    }

    /**
     * Get the Snmp Configuration specified attribute value
     *
     * @see keyword=port
     *
     * @param keyword
     *            The attribute name
     * @return The Snmp Configuration specified attribute value
     */
    public String getSnmpConfigurationSpecific() {
        final JSnmpConfiguration snmpConfiguration = snmpHandler
                .getSnmpConfiguration(userToken);
        final String keyword = inputCmd.get("keyword");
        return CommonUtils.invokeGetMethod(snmpConfiguration, keyword);
    }

    /**
     * Save SNMP configuration to default
     *
     * @return The result
     */
    public String initSnmpConfiguration() {
        final JSnmpConfiguration snmpConfiguration = snmpHandler
                .getSnmpConfiguration(userToken);
        snmpConfiguration.getSecurtiyUsers().clear();
        snmpConfiguration.getRecievers().clear();
        snmpConfiguration.setSnmpVersion(JSNMPVersion.V_2_C);
        snmpConfiguration.setTransportType(JSNMPTransportType.UDP);
        snmpConfiguration.setEnabled(true);
        snmpConfiguration.setSnmpPort((short) 161);
        snmpConfiguration.setCommunityString("public");
        snmpConfiguration.setSupportedMIB("POLYCOM_CMA_MIB");
        final JWebResult result = snmpHandler
                .saveSnmpConfiguration(userToken, snmpConfiguration);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Init SNMP configuration successfully.");
            return "SUCCESS";
        } else {
            logger.error("Fail to init SNMP configuration");
            return "Failed. Fail to init SNMP configuration. errorMsg is "
                    + result.getMessages();
        }
    }

    /**
     * Inject the args from tcl
     */
    @Override
    protected void injectCmdArgs() {
        put("authenticationPassword", "");
        put("authenticationType", "");
        put("encryptionPassword", "");
        put("encryptionType", "");
        put("remoteUser", "");
        put("address", "");
        put("enabled", "true");
        put("notificationType", "TRAP");
        put("receivePort", "162");
        put("snmpVersion", "V_2_C");
        put("transportType", "UDP");
    }

    /**
     * Edit SNMP Receiver
     *
     * @see field1=authenticationPassword<br/>
     *      value1=Polycom123<br/>
     *      remoteUser=user1
     *
     * @param field
     *            [1-10] The attribute name
     * @param value
     *            [1-10] The attribute value to update
     * @param remoteUser
     *            The remote username to edit
     * @return The result
     */
    public String updateReceiver() {
        final JSnmpConfiguration snmpConfiguration = snmpHandler
                .getSnmpConfiguration(userToken);
        final String address = inputCmd.get("address");
        final List<JNotificationReceiver> receivers = snmpConfiguration
                .getRecievers();
        if (receivers == null || receivers.isEmpty()) {
            return "Failed, could not found SNMP agent " + address;
        }
        for (final JNotificationReceiver receiver : receivers) {
            if (receiver.getAddress().equals(address)) {
                for (int i = 1; i <= 10; i++) {
                    final String keyword = inputCmd.get("field" + i);
                    final String strValue = inputCmd.get("value" + i)
                            .replaceAll("~", " ");
                    if (keyword.isEmpty()) {
                        continue;
                    }
                    try {
                        CommonUtils.invokeSetMethod(receiver,
                                                    keyword,
                                                    strValue);
                    } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException
                             | InstantiationException e) {
                        e.printStackTrace();
                        return "Failed, " + e.getMessage();
                    }
                }
                break;
            }
        }
        final JWebResult result = snmpHandler
                .saveSnmpConfiguration(userToken, snmpConfiguration);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Save SNMP configuration successfully.");
            return "SUCCESS";
        } else {
            logger.error("Fail to save SNMP configuration");
            return "Failed. Fail to save SNMP configuration. errorMsg is "
                    + result.getMessages();
        }
    }

    /**
     * Save SNMP configuration
     *
     * @see field1=snmpVersion<br/>
     *      value1=V_2_C
     *
     * @param field
     *            [1-10] The attribute name
     * @param value
     *            [1-10] The attribute value to update
     * @return The result
     */
    public String updateSnmpConfiguration() {
        final JSnmpConfiguration snmpConfiguration = snmpHandler
                .getSnmpConfiguration(userToken);
        for (int i = 1; i <= 10; i++) {
            final String keyword = inputCmd.get("field" + i);
            final String strValue = inputCmd.get("value" + i).replaceAll("~",
                                                                         " ");
            if (keyword.isEmpty()) {
                continue;
            }
            try {
                CommonUtils.invokeSetMethod(snmpConfiguration,
                                            keyword,
                                            strValue);
            } catch (IllegalAccessException
                     | IllegalArgumentException
                     | InvocationTargetException
                     | InstantiationException e) {
                e.printStackTrace();
                return "Failed, " + e.getMessage();
            }
        }
        final JWebResult result = snmpHandler
                .saveSnmpConfiguration(userToken, snmpConfiguration);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Save SNMP configuration successfully.");
            return "SUCCESS";
        } else {
            logger.error("Fail to save SNMP configuration");
            return "Failed. Fail to save SNMP configuration. errorMsg is "
                    + result.getMessages();
        }
    }

    /**
     * Edit SNMP user
     *
     * @see field1=authenticationPassword<br/>
     *      value1=Polycom123<br/>
     *      remoteUser=user1
     *
     * @param field
     *            [1-10] The attribute name
     * @param value
     *            [1-10] The attribute value to update
     * @param remoteUser
     *            The remote username to edit
     * @return The result
     */
    public String updateUser() {
        final JSnmpConfiguration snmpConfiguration = snmpHandler
                .getSnmpConfiguration(userToken);
        final String remoteUser = inputCmd.get("remoteUser");
        final List<JSnmpUSMUser> securtiyUsers = snmpConfiguration
                .getSecurtiyUsers();
        if (securtiyUsers == null || securtiyUsers.isEmpty()) {
            return "Failed, could not found SNMP user " + remoteUser;
        }
        for (final JSnmpUSMUser user : securtiyUsers) {
            if (user.getRemoteUser().equals(remoteUser)) {
                for (int i = 1; i <= 10; i++) {
                    final String keyword = inputCmd.get("field" + i);
                    final String strValue = inputCmd.get("value" + i)
                            .replaceAll("~", " ");
                    if (keyword.isEmpty()) {
                        continue;
                    }
                    try {
                        CommonUtils.invokeSetMethod(user, keyword, strValue);
                    } catch (IllegalAccessException
                             | IllegalArgumentException
                             | InvocationTargetException
                             | InstantiationException e) {
                        e.printStackTrace();
                        return "Failed, " + e.getMessage();
                    }
                }
                break;
            }
        }
        final JWebResult result = snmpHandler
                .saveSnmpConfiguration(userToken, snmpConfiguration);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Save SNMP configuration successfully.");
            return "SUCCESS";
        } else {
            logger.error("Fail to save SNMP configuration");
            return "Failed. Fail to save SNMP configuration. errorMsg is "
                    + result.getMessages();
        }
    }
}