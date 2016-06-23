package com.polycom.webservices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Holder;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.ConfigManagerHandler;
import com.polycom.sqa.xma.webservices.driver.RedundancyManagerHandler;
import com.polycom.webservices.ConfigManager.JFTPConfiguration;
import com.polycom.webservices.ConfigManager.JLogFileInfo;
import com.polycom.webservices.ConfigManager.JNetworkSettingsKey;
import com.polycom.webservices.ConfigManager.JSecuritySettings;
import com.polycom.webservices.ConfigManager.JStatus;
import com.polycom.webservices.ConfigManager.JWebResult;
import com.polycom.webservices.ConfigManager.SystemTimeConfig;

import Decoder.BASE64Encoder;

/**
 * Configuration handler. This class will handle the webservice request of
 * Configuration module
 *
 * @author wbchao
 *
 */
public class ConfigHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "setFtpPassword ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = "ftpPassword=PlcmSpIp ";
        // final String method = "getSoftwareVersion ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = "";
        // final String method = "setGabClientPassword ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params = "oldPassword=Polycom123 ";
        // final String method = "getSystemTime ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params = " ";
        final String method = "updateADUserDefaultRole ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "keyList=allowNoRoleUser,unauthorizedUserMessage
        // valueList=true,forAutomationTest ";
        final String params = "unauthorizedUserMessage=allowNoRoleUser ";
        final String command = "https://10.220.209.183:8443/PlcmRmWeb/rest/JConfigManager ConfigManager "
                + method + auth + params;
        final ConfigHandler handler = new ConfigHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    private final ConfigManagerHandler cmh;

    public ConfigHandler(final String cmd) throws IOException {
        super(cmd);
        cmh = new ConfigManagerHandler(webServiceUrl);
    }

    /**
     * Retrieve the AD user default role
     *
     * @see keyWord=allowNoRoleUser<br/>
     *
     * @param keyWord
     *            The specific information need to retrieve from the RPRM.
     *
     * @return The value of the keyWord.
     */
    public String getADUserDefaultRole() {
        String rtn = "";
        final JWebResult result = cmh.getADUserDefaultRole(userToken);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Successfully retrieve the AD user default role.");
        } else {
            logger.error("Failed to retrieve the AD user default role.");
            return "Failed to retrieve the AD user default role.";
        }
        if (inputCmd.get("keyWord").equalsIgnoreCase("allowNoRoleUser")) {
            rtn = cmh.getAllowNoRoleUser().toString();
        } else if (inputCmd.get("keyWord")
                .equalsIgnoreCase("unauthorizedUserMessage")) {
            rtn = cmh.getUnauthorizedUserMessage();
        }
        return rtn;
    }

    /**
     * Validate the network settings
     *
     * @see systemName=$secondarySystemName_B <br/>
     *      dscpMaker=0 <br/>
     *      ipv4Address1=$secondaryxmaAddr_B <br/>
     *      subnetMask1=$submask_255 <br/>
     *      gateWay1=$nic1GW_B <br/>
     *      dnsAddress=$dnsAddr <br/>
     *      dnsDomain=$dnsDomain <br/>
     *      ipv4Address2=$secondaryNIC2IP_B <br/>
     *      subnetMask2=$submask_255 <br/>
     *      gateWay2=$nic2GW_B
     *
     * @param systemName
     *            The system name of network setting
     * @param ipv4Address
     *            [1-2] The IPV4 address
     * @param subnetMask
     *            [1-2] The subnet mask address
     * @param gateWay
     *            [1-2] The gateway
     * @return The result
     */
    public String getNetworkSettingsAndValidation() {
        String status = "Failed";
        final Map<JNetworkSettingsKey, String> netInfo = cmh
                .getNetworkAndRebootSettings(userToken);
        if (netInfo.get(JNetworkSettingsKey.SYSTEM___NAME)
                .equals(inputCmd.get("systemName"))
                && netInfo.get(JNetworkSettingsKey.DSCP___MARKER)
                        .equals(inputCmd.get("dscpMaker"))
                && netInfo.get(JNetworkSettingsKey.IPV_4___ADDRESS___1)
                        .equals(inputCmd.get("ipv4Address1"))
                && netInfo.get(JNetworkSettingsKey.SUBNET___MASK___1)
                        .equals(inputCmd.get("subnetMask1"))
                && netInfo.get(JNetworkSettingsKey.GATEWAY___1)
                        .equals(inputCmd.get("gateWay1"))
                && netInfo.get(JNetworkSettingsKey.IPV_4___ADDRESS___2)
                        .equals(inputCmd.get("ipv4Address2"))
                && netInfo.get(JNetworkSettingsKey.SUBNET___MASK___2)
                        .equals(inputCmd.get("subnetMask2"))
                && netInfo.get(JNetworkSettingsKey.DNS___SERVER___1)
                        .equals(inputCmd.get("dnsAddress"))
                && netInfo.get(JNetworkSettingsKey.DNS___DOMAIN___1)
                        .equals(inputCmd.get("dnsDomain"))) {
            if (!inputCmd.get("gateWay2").isEmpty()) {
                if (netInfo.get(JNetworkSettingsKey.GATEWAY___2)
                        .equals(inputCmd.get("gateWay2"))) {
                    logger.info("The gateway of the second NIC is as expected.");
                    status = "SUCCESS";
                } else {
                    logger.error("The gateway of the second NIC is not as expected");
                    status = "Failed";
                    return status;
                }
            }
            logger.info("XMA network is configured as expected.");
            status = "SUCCESS";
        } else {
            logger.error("XMA network is not configured as expected.");
            status = "Failed";
            return status;
        }
        return status;
    }

    /**
     * Get the software update maintenance window values according to the key
     * word
     *
     * @return The value the key word referred to
     */
    public String getSoftupdateMaintenanceWindowValues() {
        String result = "Failed";
        final String deviceType = inputCmd.get("deviceType");
        final String keyWord = inputCmd.get("keyWord");
        if (cmh.getSoftupdateMaintenanceWindowValues(userToken, deviceType)
                .getStatus().equals(JStatus.SUCCESS)) {
            if (keyWord.equalsIgnoreCase("duration")) {
                result = cmh.getSoftUpdateMaintenanceDuration();
            } else if (keyWord.equalsIgnoreCase("startTime")) {
                result = cmh.getSoftUpdateMaintenanceStartTime();
            } else if (keyWord.equalsIgnoreCase("maintenanceWindowStatus")) {
                result = String.valueOf(cmh.getSoftUpdateMaintenanceEnable());
            } else {
                result = "Cannot find the value the keyword " + keyWord
                        + " referred to.";
            }
        } else {
            logger.error("Failed to retrieve the software update maintenancy window values for device "
                    + deviceType);
            return "Failed to retrieve the software update maintenancy window values for device "
                    + deviceType;
        }
        return result;
    }

    /**
     * Get the XMA software version
     *
     * @see No param
     *
     * @return The XMA software version
     */
    public String getSoftwareVersion() {
        return cmh.getSoftwareVersion(userToken);
    }

    /**
     * Get system log size
     *
     * @see
     *
     * @return The result
     */
    public String getSystemLogSize() {
        List<JLogFileInfo> logs = null;
        try {
            logs = cmh.getSystemLogFiles(userToken);
        } catch (final Exception e) {
            e.printStackTrace();
            return "Failed, got exception when get logs. Error msg is "
                    + e.getMessage();
        }
        if (!logs.isEmpty()) {
            return new Integer(logs.size()).toString();
        } else {
            return "Logs is empty";
        }
    }

    /**
     * Get the XMA time
     *
     * @see
     *
     * @return The result
     */
    public String getSystemTime() {
        final SystemTimeConfig currentTime = cmh.getSystemTimeConfig(userToken);
        return currentTime.getYear() + "";
    }

    @Override
    protected void injectCmdArgs() {
        put("systemName", "");
        put("dscpMaker", "");
        put("ipv4Address1", "");
        put("subnetMask1", "");
        put("gateWay1", "");
        put("ipv4Address2", "");
        put("subnetMask2", "");
        put("gateWay2", "");
        put("dnsAddress", "");
        put("dnsAltAddress", "");
        put("dnsDomain", "");
        put("legacydnsDomain", "");
        put("newsystemName", "");
        put("newipv4Address1", "");
        put("newsubnetMask1", "");
        put("newgateWay1", "");
        put("newipv4Address2", "");
        put("newsubnetMask2", "");
        put("newgateWay2", "");
        put("newdnsAddress", "");
        put("newdnsAltAddress", "");
        put("newdnsDomain", "");
        // Domain\Enterprise Directory User ID
        put("enterpriseDirectoryUserID", "");
        // Enterprise Directory User Password
        put("enterpriseDirectoryUserPWD", "");
        // Security Level
        put("securityLevel", "");
        // Enterprise Directory Exclusion Filter
        put("enterpriseDirectoryExclusionFilter", "");
        // Enterprise Directory Search BaseDN
        put("enterpriseDirectorySearchBaseDN", "");
        // DNS Name
        put("dnsName", "");
        // Fully qualified host name
        put("fullyQualifiedHostName", "");
        // Domain\Computer Name
        put("computerDomain", "");
        // Computer name
        put("computerName", "");
        // Computer admin password
        put("computerPWD", "");
        // Is ldap integrated
        put("ldapIntegrated", "");
        put("useNtpServer", "");
        put("timeZone", "");
        put("year", "");
        put("month", "");
        put("day", "");
        put("hours", "");
        put("minutes", "");
        put("externalNtpServers", "");
        put("nonLdapDirectoryEnabled", "true");
        put("showAllUsers", "true");
        put("hideGuestBook", "false");
        put("adjustLifesize", "false");
        put("localViewAD", "true");
        put("allowMedialStringSearch", "false");
        put("oldPassword", "");
        put("newPassword", "");
        put("deviceType", "");
        put("keyWord", "");
        put("startTime", "");
        put("duration", "");
        put("maintenanceEnable", "");
        put("unauthorizedUserMessage", "");
        put("allowNoRoleUser", "false");
    }

    /**
     * Integrate with AD server
     *
     * @see enterpriseDirectoryUserID=SQA\\adxma <br/>
     *      enterpriseDirectoryUserPWD=Polycom123 <br/>
     *      securityLevel=Plain <br/>
     *      dnsName=sqa.org <br/>
     *      fullyQualifiedHostName=ad1.sqa.org <br/>
     *      computerDomain=SQA <br/>
     *      computerName=82HA-NIC2 <br/>
     *      computerPWD=Polycom123
     *
     * @param enterpriseDirectoryUserID
     *            The user id of LDAP
     * @param enterpriseDirectoryUserPWD
     *            The user password of LDAP
     * @param securityLevel
     *            The security level
     * @param dnsName
     *            The DNS name
     * @param fullyQualifiedHostName
     *            The host name
     * @param computerDomain
     *            The computer domain
     * @param computerName
     *            The computer name
     * @param computerPWD
     *            The computer password
     * @return The result
     */
    public String ldapIntegrationWithSSO() {
        String status = "Failed";
        if (!inputCmd.get("dnsName").isEmpty()
                && !inputCmd.get("enterpriseDirectoryUserID").isEmpty()
                && !inputCmd.get("enterpriseDirectoryUserPWD").isEmpty()
                && !inputCmd.get("securityLevel").isEmpty()
                && !inputCmd.get("fullyQualifiedHostName").isEmpty()
                && !inputCmd.get("computerDomain").isEmpty()
                && !inputCmd.get("computerName").isEmpty()
                && !inputCmd.get("computerPWD").isEmpty()) {
            if (cmh.setLdapConfiguration(userToken,
                                         inputCmd.get("enterpriseDirectoryUserID"),
                                         inputCmd.get("enterpriseDirectoryUserPWD"),
                                         inputCmd.get("enterpriseDirectoryExclusionFilter"),
                                         inputCmd.get("enterpriseDirectorySearchBaseDN"),
                                         inputCmd.get("securityLevel"),
                                         inputCmd.get("dnsName"),
                                         true)
                    .getStatus().compareTo(JStatus.SUCCESS) == 0) {
                logger.info("XMA LDAP integration passed.");
                if (cmh.adIntegrateSpecifyComputer(userToken,
                                                   inputCmd.get("fullyQualifiedHostName"),
                                                   inputCmd.get("computerDomain"),
                                                   inputCmd.get("computerName"),
                                                   inputCmd.get("computerPWD"))
                        .getStatus().compareTo(JStatus.SUCCESS) == 0) {
                    status = "SUCCESS";
                    logger.info("XMA allow delegated authentication to enterprise directory server (SSO) passed");
                }
            }
        } else {
            status = "Failed";
            logger.error("Some mandatory parameters are missing. Please check your input command.");
            return status
                    + " Some mandatory parameters are missing. Please check your input command.";
        }
        return status;
    }

    /**
     * Remove the LDAP
     *
     * @see No param
     *
     * @return The result
     */
    public String removeLdap() {
        String status = "Failed";
        if (cmh.setLdapConfiguration(userToken,
                                     "",
                                     "",
                                     "",
                                     "",
                                     "Plain",
                                     "",
                                     false)
                .getStatus().compareTo(JStatus.SUCCESS) == 0) {
            logger.info("XMA LDAP remove passed.");
            status = "SUCCESS";
        } else {
            status = "Failed";
            logger.error("XMA LDAP remove failed.");
            return " XMA LDAP remove failed.";
        }
        return status;
    }

    /**
     * Reset system passwords
     *
     * @see No param
     *
     * @return The result
     */
    public String resetSystemPasswords() {
        final JWebResult result = cmh.resetSystemPasswords(userToken);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Reset system passwords successfully");
            return "SUCCESS";
        } else {
            logger.error("Reset system passwords failed");
            return "Failed, error msg is: " + result.getMessages();
        }
    }

    /**
     * Restart the XMA server
     *
     * @see No param
     *
     * @return The result
     */
    public String restartServer() {
        final JWebResult result = cmh.restartServer(userToken);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Restart XMA successfully");
            return "SUCCESS";
        } else {
            logger.error("Restart XMA failed");
            return "Failed, error msg is: " + result.getMessages();
        }
    }

    /**
     * Set the FTP password on XMA
     *
     * @see ftpPassword=Polycom123
     *
     * @param ftpPassword
     *            The FTP password
     * @return The result
     */
    public String setFtpPassword() {
        String status = "Failed";
        final BASE64Encoder base64encoder = new BASE64Encoder();
        final Holder<JFTPConfiguration> ftpConfig = cmh
                .getFtpConfiguration(userToken);
        ftpConfig.value.setFtpPassword(base64encoder
                .encode(inputCmd.get("ftpPassword").getBytes()));
        if (cmh.setFtpPassword(userToken, ftpConfig).getStatus()
                .compareTo(JStatus.SUCCESS) == 0) {
            logger.info("FTP password successfully updated in XMA.");
            status = "SUCCESS";
        } else {
            status = "Failed";
            logger.error("FTP password update failed.");
            return " FTP password update failed.";
        }
        return status;
    }

    /**
     * Set the GAB client password
     *
     * @return The result
     */
    public String setGabClientPassword() {
        String status = "Failed";
        final String oldPassword = inputCmd.get("oldPassword");
        final String newPassword = inputCmd.get("newPassword");
        final JWebResult result = cmh
                .setGabClientPassword(userToken, oldPassword, newPassword);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfully set the GAB client password with the new one "
                    + inputCmd.get("newPassword"));
        } else {
            logger.error("Failed to set the GAB client password with the new one "
                    + inputCmd.get("newPassword"));
            return status
                    + " Failed to set the GAB client password with the new one "
                    + inputCmd.get("newPassword");
        }
        return status;
    }

    /**
     * Update the GAB system setting
     *
     * @param nonLdapDirectoryEnabled
     *            Allow non-LDAP directory protocols or not
     * @param showAllUsers
     *            Include dynamically-managed devices in the global address book
     * @param hideGuestBook
     *            Hide the guest book. False, if show guest book entries in the
     *            directory, else true
     * @param adjustLifesize
     *            Modify directory listings for LifeSize endpoint support
     * @param localViewAD
     *            Allow endpoint directory for local users to include enterprise
     *            directory user and group infomation
     * @param allowMedialStringSearch
     *            Allow mid-string search for local user/room/guest and legacy
     *            endpoint
     *
     * @return The result
     */
    public String setGabSystemSetting() {
        String status = "Failed";
        Boolean alloNonLdap;
        Boolean hideGab;
        Boolean showAllUsr;
        Boolean adjLifeSize;
        Boolean localViewAd;
        Boolean alloMediaString;
        if (inputCmd.get("nonLdapDirectoryEnabled").equalsIgnoreCase("true")) {
            alloNonLdap = true;
        } else if (inputCmd.get("nonLdapDirectoryEnabled")
                .equalsIgnoreCase("false")) {
            alloNonLdap = false;
        } else {
            alloNonLdap = true;
        }
        if (inputCmd.get("showAllUsers").equalsIgnoreCase("true")) {
            showAllUsr = true;
        } else if (inputCmd.get("showAllUsers").equalsIgnoreCase("false")) {
            showAllUsr = false;
        } else {
            showAllUsr = true;
        }
        if (inputCmd.get("hideGuestBook").equalsIgnoreCase("true")) {
            hideGab = true;
        } else if (inputCmd.get("hideGuestBook").equalsIgnoreCase("false")) {
            hideGab = false;
        } else {
            hideGab = true;
        }
        if (inputCmd.get("adjustLifesize").equalsIgnoreCase("true")) {
            adjLifeSize = true;
        } else if (inputCmd.get("adjustLifesize").equalsIgnoreCase("false")) {
            adjLifeSize = false;
        } else {
            adjLifeSize = false;
        }
        if (inputCmd.get("localViewAD").equalsIgnoreCase("true")) {
            localViewAd = true;
        } else if (inputCmd.get("localViewAD").equalsIgnoreCase("false")) {
            localViewAd = false;
        } else {
            localViewAd = true;
        }
        if (inputCmd.get("allowMedialStringSearch").equalsIgnoreCase("true")) {
            alloMediaString = true;
        } else if (inputCmd.get("allowMedialStringSearch")
                .equalsIgnoreCase("false")) {
            alloMediaString = false;
        } else {
            alloMediaString = false;
        }
        if (cmh.setGabSystemSetting(userToken,
                                    alloNonLdap,
                                    hideGab,
                                    showAllUsr,
                                    adjLifeSize,
                                    localViewAd,
                                    alloMediaString)
                .getStatus().equals(JStatus.SUCCESS)) {
            logger.info("GAB system setting succeed.");
            status = "SUCCESS";
        } else {
            logger.error("GAB system setting failed.");
            status = "Failed";
            return status + " GAB system setting failed.";
        }
        return status;
    }

    /**
     * Set the network setting
     *
     * @see systemName=$secondarySystemName <br/>
     *      dscpMaker=0 <br/>
     *      ipv4Address1=$secondaryxmaAddr <br/>
     *      subnetMask1=$submask_255 <br/>
     *      gateWay1=$nic1GW_s <br/>
     *      dnsAddress=$dnsAddr <br/>
     *      dnsDomain=$dnsDomain <br/>
     *      ipv4Address2=$secondaryNIC2IP <br/>
     *      subnetMask2=$submask_192 <br/>
     *      gateWay2=$nic2GW_s <br/>
     *      newsubnetMask2=$submask_224
     *
     * @param systemName
     *            The system new name of network setting(Optional)
     * @param newsystemName
     *            The system new name of network setting(Optional)
     * @param newipv4Address
     *            [1-2] The new IPV4 address(Optional)
     * @param newsubnetMask
     *            [1-2] The new subnet mask address(Optional)
     * @param newgateWay
     *            [1-2] The new gateway(Optional)
     * @return newThe result
     */
    public String setNetworkSettings() {
        String status = "Failed";
        final RedundancyManagerHandler rmh = new RedundancyManagerHandler(
                webServiceUrl);
        if (!inputCmd.get("newsystemName").isEmpty() && !inputCmd
                .get("newsystemName").equals(inputCmd.get("systemName"))) {
            inputCmd.put("systemName", inputCmd.get("newsystemName"));
        }
        if (!inputCmd.get("newipv4Address1").isEmpty() && !inputCmd
                .get("newipv4Address1").equals(inputCmd.get("ipv4Address1"))) {
            inputCmd.put("ipv4Address1", inputCmd.get("newipv4Address1"));
        }
        if (!inputCmd.get("newsubnetMask1").isEmpty() && !inputCmd
                .get("newsubnetMask1").equals(inputCmd.get("subnetMask1"))) {
            inputCmd.put("subnetMask1", inputCmd.get("newsubnetMask1"));
        }
        if (!inputCmd.get("newgateWay1").isEmpty() && !inputCmd
                .get("newgateWay1").equals(inputCmd.get("gateWay1"))) {
            inputCmd.put("gateWay1", inputCmd.get("newgateWay1"));
        }
        if (!inputCmd.get("newipv4Address2").isEmpty() && !inputCmd
                .get("newipv4Address2").equals(inputCmd.get("ipv4Address2"))) {
            if (!inputCmd.get("newipv4Address2").equalsIgnoreCase("empty")) {
                inputCmd.put("ipv4Address2", inputCmd.get("newipv4Address2"));
            } else
                if (inputCmd.get("newipv4Address2").equalsIgnoreCase("empty")) {
                inputCmd.put("ipv4Address2", "");
            } else {
                logger.error("Please input correct IP address for the NIC2.");
            }
        }
        if (!inputCmd.get("newsubnetMask2").isEmpty() && !inputCmd
                .get("newsubnetMask2").equals(inputCmd.get("subnetMask2"))) {
            if (!inputCmd.get("newsubnetMask2").equalsIgnoreCase("empty")) {
                inputCmd.put("subnetMask2", inputCmd.get("newsubnetMask2"));
            } else
                if (inputCmd.get("newsubnetMask2").equalsIgnoreCase("empty")) {
                inputCmd.put("subnetMask2", "");
            } else {
                logger.error("Please input correct subnet mask for the NIC2.");
            }
        }
        if (!inputCmd.get("newgateWay2").isEmpty() && !inputCmd
                .get("newgateWay2").equals(inputCmd.get("gateWay2"))) {
            if (!inputCmd.get("newgateWay2").equalsIgnoreCase("empty")) {
                inputCmd.put("gateWay2", inputCmd.get("newgateWay2"));
            } else if (inputCmd.get("newgateWay2").equalsIgnoreCase("empty")) {
                inputCmd.put("gateWay2", "");
            } else {
                logger.error("Please input correct getway for the NIC2.");
            }
        }
        if (!inputCmd.get("newdnsAddress").isEmpty() && !inputCmd
                .get("newdnsAddress").equals(inputCmd.get("dnsAddress"))) {
            inputCmd.put("dnsAddress", inputCmd.get("newdnsAddress"));
        }
        if (!inputCmd.get("newdnsDomain").isEmpty() && !inputCmd
                .get("newdnsDomain").equals(inputCmd.get("dnsDomain"))) {
            inputCmd.put("legacydnsDomain", inputCmd.get("dnsDomain"));
            inputCmd.put("dnsDomain", inputCmd.get("newdnsDomain"));
        }
        if (!rmh.isRedundancyConfigured()) {
            if (cmh.setNetworkSettings(userToken,
                                       inputCmd.get("systemName"),
                                       inputCmd.get("dscpMaker"),
                                       inputCmd.get("ipv4Address1"),
                                       inputCmd.get("subnetMask1"),
                                       inputCmd.get("gateWay1"),
                                       inputCmd.get("ipv4Address2"),
                                       inputCmd.get("subnetMask2"),
                                       inputCmd.get("gateWay2"),
                                       inputCmd.get("dnsAddress"),
                                       inputCmd.get("dnsAltAddress"),
                                       inputCmd.get("dnsDomain"),
                                       inputCmd.get("legacydnsDomain"))
                    .getStatus().compareTo(JStatus.SUCCESS) == 0) {
                final JWebResult result = cmh.restartServer(userToken);
                if (result.getStatus().equals(JStatus.SUCCESS)) {
                    logger.info("Network configuration updated successfully. Server now rebooted.");
                    status = "SUCCESS";
                }
            }
        }
        return status;
    }

    /**
     * Set security settings
     *
     * @see
     *
     * @return The result
     */
    public String setSecuritySetting() {
        final JSecuritySettings securitySetting = cmh
                .getSecuritySettings(userToken);
        final String[] attributeList = inputCmd.get("keyList").split(",");
        final String[] valueList = inputCmd.get("valueList").split(",");
        for (int i = 0; i < attributeList.length; i++) {
            try {
                CommonUtils.invokeSetMethod(securitySetting,
                                            attributeList[i],
                                            valueList[i]);
            } catch (IllegalAccessException
                     | IllegalArgumentException
                     | InvocationTargetException
                     | InstantiationException e) {
                e.printStackTrace();
                return CommonUtils.getExceptionStackTrace(e);
            }
        }
        final JWebResult result = cmh.setSecuritySettings(userToken,
                                                          securitySetting);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Set security setting on XMA successfully");
            return "SUCCESS";
        } else {
            logger.error("Set security setting on XMA failed");
            return "Failed, error msg is: " + result.getMessages();
        }
    }

    /**
     * Set the software update maintenance window values
     *
     * @param deviceType
     *            The device type
     * @param maintenanceEnable
     *            The maintenance window switch status. Set true to enable, set
     *            false to disable
     * @param duration
     *            The maintenance window duration
     * @param startTime
     *            The maintenance window start time
     *
     * @return The result of the operation
     */
    public String setSoftupdateMaintenanceWindowValues() {
        String status = "Failed";
        final String deviceType = inputCmd.get("deviceType");
        final boolean enableOrNot = Boolean
                .valueOf(inputCmd.get("maintenanceEnable"));
        final String duration = inputCmd.get("duration");
        final String startTime = inputCmd.get("startTime");
        if (cmh.setSoftupdateMaintenanceWindowValues(userToken,
                                                     deviceType,
                                                     startTime,
                                                     duration,
                                                     enableOrNot)
                .getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfully set the softupdate maintenance window values.");
        } else {
            logger.error("Failed to set the softupdate maintenance window values.");
            return status
                    + " Failed to set the softupdate maintenance window values.";
        }
        return status;
    }

    /**
     * Set the XMA time
     *
     * @see
     *
     * @return The result
     */
    public String setSystemTime() {
        final SystemTimeConfig currentTime = cmh.getSystemTimeConfig(userToken);
        try {
            for (final String key : inputCmd.keySet()) {
                if (!inputCmd.get(key).isEmpty()) {
                    CommonUtils.invokeSetMethod(currentTime,
                                                key,
                                                inputCmd.get(key));
                }
            }
            currentTime.setLocalUtcOffsetHours(0);
            currentTime.setLocalUtcOffsetMinutes(0);
            currentTime.setLocalUtcOffsetSign("");
            currentTime.setOSTimeZone(null);
        } catch (IllegalAccessException
                 | IllegalArgumentException
                 | InvocationTargetException
                 | InstantiationException e) {
            e.printStackTrace();
            return CommonUtils.getExceptionStackTrace(e);
        }
        final JWebResult result = cmh.setSystemTimeConfig(userToken,
                                                          currentTime);
        // final String useNtpServer = inputCmd.get("useNtpServer");
        // final String timeZone = inputCmd.get("timeZone");
        // final String year = inputCmd.get("year");
        // final String month = inputCmd.get("month");
        // final String day = inputCmd.get("day");
        // final String hours = inputCmd.get("hours");
        // final String minutes = inputCmd.get("minutes");
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Set time on XMA successfully");
            return "SUCCESS";
        } else {
            logger.error("Did not set the time on XMA");
            return "Failed, error msg is: " + result.getMessages();
        }
    }

    /**
     * Update the AD user default role
     *
     * @see unauthorizedUserMessage=auto<br/>
     *      allowNoRoleUser=false<br/>
     *
     * @param unauthorizedUserMessage
     *            Unauthorized user message.
     * @param allowNoRoleUser
     *            Associate Enterprise Directory users with basic scheduler role
     *            by default or not. The default value is false.
     *
     * @return The result of the operation. SUCCESS or Failed.
     */
    public String updateADUserDefaultRole() {
        final String status = "Failed";
        final String unauthorizedUserMessage = inputCmd
                .get("unauthorizedUserMessage");
        Boolean allowNoRoleUser = false;
        if (inputCmd.get("allowNoRoleUser").equalsIgnoreCase("true")) {
            allowNoRoleUser = true;
        }
        final JWebResult result = cmh
                .setADUserDefaultRole(userToken,
                                      allowNoRoleUser,
                                      unauthorizedUserMessage);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Update the AD user default role successfully.");
            return "SUCCESS";
        } else {
            logger.error("Failed to update the AD user default role.");
            return status + " Failed to update the AD user default role.";
        }
    }
}
