package com.polycom.sqa.xma.webservices.driver;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.log4j.Logger;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.interceptor.JsonInvocationHandler;
import com.polycom.sqa.xma.webservices.driver.interceptor.SoapHeaderOutInterceptor;
import com.polycom.webservices.ConfigManager.JConfigManager;
import com.polycom.webservices.ConfigManager.JConfigManager_Service;
import com.polycom.webservices.ConfigManager.JCredentials;
import com.polycom.webservices.ConfigManager.JFTPConfiguration;
import com.polycom.webservices.ConfigManager.JHardwareRevision;
import com.polycom.webservices.ConfigManager.JLdapConfigurationSettings;
import com.polycom.webservices.ConfigManager.JLdapSecurityLevel;
import com.polycom.webservices.ConfigManager.JLogFileInfo;
import com.polycom.webservices.ConfigManager.JNetworkSettingsKey;
import com.polycom.webservices.ConfigManager.JSecuritySettings;
import com.polycom.webservices.ConfigManager.JWebResult;
import com.polycom.webservices.ConfigManager.SystemTimeConfig;

/**
 * Configuration manager handler
 *
 * @author Tao Chen
 *
 */
public class ConfigManagerHandler {
    protected static Logger    logger                         = Logger
            .getLogger("ConfigManagerHandler");
    private static final QName SERVICE_NAME                   = new QName(
            "http://polycom.com/WebServices", "JConfigManager");
    JConfigManager             port;
    private boolean            softupdateMaintenanceEnable    = false;
    private Boolean            allowNoRoleUser                = false;
    private String             softupdateMaintenanceStartTime = "";
    private String             softupdateMaintenanceDuration  = "";
    private String             unauthorizedUserMessage        = "";

    /**
     * Construction of the ConfigManagerHandler class
     */
    public ConfigManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JConfigManager) jsonInvocationHandler
                    .getProxy(JConfigManager.class);
        } else {
            final URL wsdlURL = ConfigManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JConfigManager.wsdl");
            final JConfigManager_Service ss = new JConfigManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJConfigManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext().put(
                                             BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                             webServiceUrl + "/JConfigManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    /**
     * Integrate the LDAP server for SSO
     *
     * @param userToken
     * @param fqdn
     *            Full qualified host name of the LDAP server. Set auto if no
     *            host name specified.
     * @param domain
     * @param hostName
     * @param password
     */
    public JWebResult adIntegrateSpecifyComputer(final String userToken,
                                                 final String fqdn,
                                                 final String domain,
                                                 final String hostName,
                                                 final String password) {
        System.out.println("Invoking adIntegrateSpecifyComputer...");
        logger.info("Invoking adIntegrateSpecifyComputer...");
        final JCredentials _adIntegrateSpecifyComputer_credentials = new JCredentials();
        _adIntegrateSpecifyComputer_credentials.setUserToken(userToken);
        final String _adIntegrateSpecifyComputer_adServerVal = fqdn;
        final Holder<String> _adIntegrateSpecifyComputer_adServer = new Holder<String>(
                _adIntegrateSpecifyComputer_adServerVal);
        final String _adIntegrateSpecifyComputer_computerDomainVal = domain;
        final Holder<String> _adIntegrateSpecifyComputer_computerDomain = new Holder<String>(
                _adIntegrateSpecifyComputer_computerDomainVal);
        final String _adIntegrateSpecifyComputer_computerAdminUserVal = hostName;
        final Holder<String> _adIntegrateSpecifyComputer_computerAdminUser = new Holder<String>(
                _adIntegrateSpecifyComputer_computerAdminUserVal);
        final String _adIntegrateSpecifyComputer_computerAdminPasswordVal = password;
        final Holder<String> _adIntegrateSpecifyComputer_computerAdminPassword = new Holder<String>(
                _adIntegrateSpecifyComputer_computerAdminPasswordVal);
        final JWebResult _adIntegrateSpecifyComputer__return = port
                .adIntegrateSpecifyComputer(_adIntegrateSpecifyComputer_credentials,
                                            _adIntegrateSpecifyComputer_adServer,
                                            _adIntegrateSpecifyComputer_computerDomain,
                                            _adIntegrateSpecifyComputer_computerAdminUser,
                                            _adIntegrateSpecifyComputer_computerAdminPassword);
        System.out.println("adIntegrateSpecifyComputer.result="
                + _adIntegrateSpecifyComputer__return);
        System.out
                .println("adIntegrateSpecifyComputer._adIntegrateSpecifyComputer_adServer="
                        + _adIntegrateSpecifyComputer_adServer.value);
        System.out
                .println("adIntegrateSpecifyComputer._adIntegrateSpecifyComputer_computerDomain="
                        + _adIntegrateSpecifyComputer_computerDomain.value);
        System.out
                .println("adIntegrateSpecifyComputer._adIntegrateSpecifyComputer_computerAdminUser="
                        + _adIntegrateSpecifyComputer_computerAdminUser.value);
        System.out
                .println("adIntegrateSpecifyComputer._adIntegrateSpecifyComputer_computerAdminPassword="
                        + _adIntegrateSpecifyComputer_computerAdminPassword.value);
        return _adIntegrateSpecifyComputer__return;
    }

    /**
     * Get the AD user default role
     *
     * @param userToken
     * @return
     */
    public JWebResult getADUserDefaultRole(final String userToken) {
        logger.info("Invoking getADUserDefaultRole...");
        final JCredentials _getADUserDefaultRole_credentials = new JCredentials();
        _getADUserDefaultRole_credentials.setUserToken(userToken);
        final Holder<Boolean> _getADUserDefaultRole_allowNoRoleUser = new Holder<Boolean>();
        final Holder<String> _getADUserDefaultRole_unauthorizedUserMessage = new Holder<String>();
        final JWebResult _getADUserDefaultRole__return = port
                .getADUserDefaultRole(_getADUserDefaultRole_credentials,
                                      _getADUserDefaultRole_allowNoRoleUser,
                                      _getADUserDefaultRole_unauthorizedUserMessage);
        setUnauthorizedUserMessage(_getADUserDefaultRole_unauthorizedUserMessage.value);
        setAllowNoRoleUser(_getADUserDefaultRole_allowNoRoleUser.value);
        return _getADUserDefaultRole__return;
    }

    public Boolean getAllowNoRoleUser() {
        return allowNoRoleUser;
    }

    /**
     * Get FTP Password
     *
     * @param userToken
     */
    public Holder<JFTPConfiguration>
            getFtpConfiguration(final String userToken) {
        System.out.println("Invoking getFtpConfiguration...");
        logger.info("Invoking getFtpConfiguration...");
        final JCredentials _getFtpPassword_credentials = new JCredentials();
        _getFtpPassword_credentials.setUserToken(userToken);
        final Holder<JFTPConfiguration> _getFtpPassword_ftpConfig = new Holder<JFTPConfiguration>();
        port.getFtpConfiguration(_getFtpPassword_credentials,
                                 _getFtpPassword_ftpConfig);
        return _getFtpPassword_ftpConfig;
    }

    /**
     * Get the network configuration
     *
     * @param userToken
     * @return
     */
    public Map<JNetworkSettingsKey, String>
            getNetworkAndRebootSettings(final String userToken) {
        final Map<JNetworkSettingsKey, String> netInfo = new HashMap<JNetworkSettingsKey, String>();
        System.out.println("Invoking getNetworkAndRebootSettings...");
        logger.info("Invoking getNetworkAndRebootSettings...");
        final JCredentials _getNetworkAndRebootSettings_credentials = new JCredentials();
        _getNetworkAndRebootSettings_credentials.setUserToken(userToken);
        final Holder<List<JNetworkSettingsKey>> _getNetworkAndRebootSettings_keys = new Holder<List<JNetworkSettingsKey>>();
        final Holder<List<String>> _getNetworkAndRebootSettings_values = new Holder<List<String>>();
        final Holder<Boolean> _getNetworkAndRebootSettings_isApacheRebootRequired = new Holder<Boolean>();
        final Holder<Boolean> _getNetworkAndRebootSettings_isNetworkSettingsPending = new Holder<Boolean>();
        final JWebResult _getNetworkAndRebootSettings__return = port
                .getNetworkAndRebootSettings(_getNetworkAndRebootSettings_credentials,
                                             _getNetworkAndRebootSettings_keys,
                                             _getNetworkAndRebootSettings_values,
                                             _getNetworkAndRebootSettings_isApacheRebootRequired,
                                             _getNetworkAndRebootSettings_isNetworkSettingsPending);
        System.out.println("getNetworkAndRebootSettings.result="
                + _getNetworkAndRebootSettings__return);
        System.out
                .println("getNetworkAndRebootSettings._getNetworkAndRebootSettings_keys="
                        + _getNetworkAndRebootSettings_keys.value);
        System.out
                .println("getNetworkAndRebootSettings._getNetworkAndRebootSettings_values="
                        + _getNetworkAndRebootSettings_values.value);
        System.out
                .println("getNetworkAndRebootSettings._getNetworkAndRebootSettings_isApacheRebootRequired="
                        + _getNetworkAndRebootSettings_isApacheRebootRequired.value);
        System.out
                .println("getNetworkAndRebootSettings._getNetworkAndRebootSettings_isNetworkSettingsPending="
                        + _getNetworkAndRebootSettings_isNetworkSettingsPending.value);
        for (final JNetworkSettingsKey key : _getNetworkAndRebootSettings_keys.value) {
            netInfo.put(key,
                        _getNetworkAndRebootSettings_values.value
                                .get(_getNetworkAndRebootSettings_keys.value
                                        .indexOf(key)));
        }
        System.out.println("The XMA network configuration information is: "
                + netInfo);
        logger.info("The XMA network configuration information is: " + netInfo);
        return netInfo;
    }

    /**
     * Set security settings
     *
     * @param userToken
     * @return security settings
     */
    public JSecuritySettings getSecuritySettings(final String userToken) {
        System.out.println("Invoking getSecuritySettings...");
        logger.info("Invoking getSecuritySettings...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JSecuritySettings> settings = new Holder<JSecuritySettings>();
        port.getSecuritySettings(credentials, settings);
        return settings.value;
    }

    public String getSoftUpdateMaintenanceDuration() {
        return softupdateMaintenanceDuration;
    }

    public boolean getSoftUpdateMaintenanceEnable() {
        return softupdateMaintenanceEnable;
    }

    public String getSoftUpdateMaintenanceStartTime() {
        return softupdateMaintenanceStartTime;
    }

    /**
     * Get the softupdate maintenance window values
     *
     * @param userToken
     * @param deviceType
     * @return
     */
    public JWebResult
            getSoftupdateMaintenanceWindowValues(final String userToken,
                                                 final String deviceType) {
        logger.info("Invoking getSoftupdateMaintenanceWindowValues...");
        final JCredentials _getSoftupdateMaintenanceWindowValues_credentials = new JCredentials();
        _getSoftupdateMaintenanceWindowValues_credentials
                .setUserToken(userToken);
        final String _getSoftupdateMaintenanceWindowValues_deviceType = deviceType;
        final Holder<Boolean> _getSoftupdateMaintenanceWindowValues_enabled = new Holder<Boolean>();
        final Holder<String> _getSoftupdateMaintenanceWindowValues_startTime = new Holder<String>();
        final Holder<String> _getSoftupdateMaintenanceWindowValues_duration = new Holder<String>();
        final JWebResult _getSoftupdateMaintenanceWindowValues__return = port
                .getSoftupdateMaintenanceWindowValues(_getSoftupdateMaintenanceWindowValues_credentials,
                                                      _getSoftupdateMaintenanceWindowValues_deviceType,
                                                      _getSoftupdateMaintenanceWindowValues_enabled,
                                                      _getSoftupdateMaintenanceWindowValues_startTime,
                                                      _getSoftupdateMaintenanceWindowValues_duration);
        System.out
                .println("getSoftupdateMaintenanceWindowValues._getSoftupdateMaintenanceWindowValues_enabled="
                        + _getSoftupdateMaintenanceWindowValues_enabled.value);
        System.out
                .println("getSoftupdateMaintenanceWindowValues._getSoftupdateMaintenanceWindowValues_startTime="
                        + _getSoftupdateMaintenanceWindowValues_startTime.value);
        System.out
                .println("getSoftupdateMaintenanceWindowValues._getSoftupdateMaintenanceWindowValues_duration="
                        + _getSoftupdateMaintenanceWindowValues_duration.value);
        setSoftUpdateMaintenanceEnable(_getSoftupdateMaintenanceWindowValues_enabled.value);
        setSoftUpdateMaintenanceStartTime(_getSoftupdateMaintenanceWindowValues_startTime.value);
        setSoftUpdateMaintenanceDuration(_getSoftupdateMaintenanceWindowValues_duration.value);
        return _getSoftupdateMaintenanceWindowValues__return;
    }

    public String getSoftwareVersion(final String userToken) {
        System.out.println("Invoking getServerVersion...");
        logger.info("Invoking getServerVersion...");
        final Holder<JHardwareRevision> hardwareRevision = new Holder<JHardwareRevision>();
        final Holder<String> softwareVersion = new Holder<String>();
        port.getServerVersion(hardwareRevision, softwareVersion);
        return softwareVersion.value;
    }

    /**
     * Get system log files
     *
     * @param userToken
     * @return system log files
     */
    public List<JLogFileInfo> getSystemLogFiles(final String userToken) {
        System.out.println("Invoking getSystemLogs...");
        logger.info("Invoking getSystemLogs...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JLogFileInfo>> files = new Holder<List<JLogFileInfo>>();
        port.getSystemLogFiles(credentials, files);
        return files.value;
    }

    public SystemTimeConfig getSystemTimeConfig(final String userToken) {
        System.out.println("Invoking getSystemTimeConfig...");
        logger.info("Invoking getSystemTimeConfig...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<SystemTimeConfig> systemTimeConfig = new Holder<SystemTimeConfig>();
        final Holder<List<String>> timeZones = new Holder<List<String>>();
        port.getSystemTimeConfig(credentials, systemTimeConfig, timeZones);
        return systemTimeConfig.value;
    }

    public String getUnauthorizedUserMessage() {
        return unauthorizedUserMessage;
    }

    public JWebResult resetSystemPasswords(final String userToken) {
        System.out.println("Invoking resetSystemPasswords...");
        logger.info("Invoking resetSystemPasswords...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port.resetSystemPasswords(credentials);
        return result;
    }

    /**
     * Restart the XMA server
     *
     * @param userToken
     * @return
     */
    public JWebResult restartServer(final String userToken) {
        System.out.println("Invoking restartServer...");
        logger.info("Invoking restartServer...");
        final JCredentials _restartServer_credentials = new JCredentials();
        _restartServer_credentials.setUserToken(userToken);
        final com.polycom.webservices.ConfigManager.JWebResult _restartServer__return = port
                .restartServer(_restartServer_credentials);
        System.out.println("restartServer.result=" + _restartServer__return);
        return _restartServer__return;
    }

    /**
     * Set the AD user default role
     *
     * @param userToken
     * @param allowNoRoleUserVal
     * @param unauthorizedUserMessage
     * @return
     */
    public JWebResult
            setADUserDefaultRole(final String userToken,
                                 final Boolean allowNoRoleUserVal,
                                 final String unauthorizedUserMessage) {
        logger.info("Invoking setADUserDefaultRole...");
        final JCredentials _setADUserDefaultRole_credentials = new JCredentials();
        _setADUserDefaultRole_credentials.setUserToken(userToken);
        final Boolean _setADUserDefaultRole_allowNoRoleUserVal = allowNoRoleUserVal;
        final Holder<Boolean> _setADUserDefaultRole_allowNoRoleUser = new Holder<Boolean>(
                _setADUserDefaultRole_allowNoRoleUserVal);
        final String _setADUserDefaultRole_unauthorizedUserMessageVal = unauthorizedUserMessage;
        final Holder<String> _setADUserDefaultRole_unauthorizedUserMessage = new Holder<String>(
                _setADUserDefaultRole_unauthorizedUserMessageVal);
        final JWebResult _setADUserDefaultRole__return = port
                .setADUserDefaultRole(_setADUserDefaultRole_credentials,
                                      _setADUserDefaultRole_allowNoRoleUser,
                                      _setADUserDefaultRole_unauthorizedUserMessage);
        return _setADUserDefaultRole__return;
    }

    public void setAllowNoRoleUser(final Boolean allowNoRoleUser) {
        this.allowNoRoleUser = allowNoRoleUser;
    }

    /**
     * Set FTP Password
     *
     * @param userToken
     * @param ftpUsername
     * @param ftpPassword
     */
    public JWebResult
            setFtpPassword(final String userToken,
                           final Holder<JFTPConfiguration> configuartion) {
        System.out.println("Invoking setFtpPassword...");
        logger.info("Invoking setFtpPassword...");
        final JCredentials _setFtpPassword_credentials = new JCredentials();
        _setFtpPassword_credentials.setUserToken(userToken);
        final Holder<JFTPConfiguration> _setFtpPassword_ftpConfig = configuartion;
        final JWebResult _setFtpPassword__return = port
                .setFtpConfiguration(_setFtpPassword_credentials,
                                     _setFtpPassword_ftpConfig);
        return _setFtpPassword__return;
    }

    /**
     * Set GAB password
     *
     * @param userToken
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public JWebResult setGabClientPassword(final String userToken,
                                           final String oldPassword,
                                           final String newPassword) {
        logger.info("Invoking setGabClientPassword...");
        final JCredentials _setGabClientPassword_credentials = new JCredentials();
        _setGabClientPassword_credentials.setUserToken(userToken);
        final String _setGabClientPassword_oldPassword = oldPassword;
        final String _setGabClientPassword_newPassword = newPassword;
        final JWebResult _setGabClientPassword__return = port
                .setGabClientPassword(_setGabClientPassword_credentials,
                                      _setGabClientPassword_oldPassword,
                                      _setGabClientPassword_newPassword);
        return _setGabClientPassword__return;
    }

    /**
     * Set GAB system settings
     *
     * @param userToken
     * @param alloNonLdap
     * @param hideGab
     * @param showAllUsr
     * @param adjLifeSize
     * @param localViewAd
     * @param alloMediaString
     * @return
     */
    public JWebResult setGabSystemSetting(final String userToken,
                                          final Boolean alloNonLdap,
                                          final Boolean hideGab,
                                          final Boolean showAllUsr,
                                          final Boolean adjLifeSize,
                                          final Boolean localViewAd,
                                          final Boolean alloMediaString) {
        logger.info("Invoking setGabSystemSetting...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Boolean _setGabSystemSetting_nonLdapDirectoryEnabled = alloNonLdap;
        final Boolean _setGabSystemSetting_showAllUsers = showAllUsr;
        final Boolean _setGabSystemSetting_hideGuestBook = hideGab;
        final Boolean _setGabSystemSetting_adjustLifesize = adjLifeSize;
        final Boolean _setGabSystemSetting_localViewAD = localViewAd;
        final Boolean _setGabSystemSetting_allowMedialStringSearch = alloMediaString;
        final JWebResult _setGabSystemSetting__return = port
                .setGabSystemSetting(credentials,
                                     _setGabSystemSetting_nonLdapDirectoryEnabled,
                                     _setGabSystemSetting_showAllUsers,
                                     _setGabSystemSetting_hideGuestBook,
                                     _setGabSystemSetting_adjustLifesize,
                                     _setGabSystemSetting_localViewAD,
                                     _setGabSystemSetting_allowMedialStringSearch);
        System.out.println("setGabSystemSetting.result="
                + _setGabSystemSetting__return);
        return _setGabSystemSetting__return;
    }

    /**
     * Integrate the LDAP server
     *
     * @param userToken
     * @param adminUserID
     * @param adminPwd
     * @param exclusiveFilter
     * @param searchBaseDN
     * @param secuLevel
     * @param DNSName
     * @return
     */
    public JWebResult setLdapConfiguration(final String userToken,
                                           final String adminUserID,
                                           final String adminPwd,
                                           final String exclusiveFilter,
                                           final String searchBaseDN,
                                           final String secuLevel,
                                           final String DNSName,
                                           final Boolean isIntegrated) {
        System.out.println("Invoking setLdapConfiguration...");
        logger.info("Invoking setLdapConfiguration...");
        final JCredentials _setLdapConfiguration_credentials = new JCredentials();
        _setLdapConfiguration_credentials.setUserToken(userToken);
        final Boolean _setLdapConfiguration_savePassword = true;
        final JLdapConfigurationSettings _setLdapConfiguration_config = new JLdapConfigurationSettings();
        _setLdapConfiguration_config.setAdminUserLoginId(adminUserID);
        _setLdapConfiguration_config.setAdminUserPassword(adminPwd);
        _setLdapConfiguration_config.setDbKey(0);
        _setLdapConfiguration_config.setIntegrated(isIntegrated);
        _setLdapConfiguration_config.setLdapExclusionFilter(exclusiveFilter);
        _setLdapConfiguration_config.setSearchRoot(searchBaseDN);
        _setLdapConfiguration_config.setServerAddress(DNSName);
        _setLdapConfiguration_config.setServerPort(0);
        if (secuLevel.equalsIgnoreCase("Plain")) {
            _setLdapConfiguration_config
                    .setLdapSecurityLevel(JLdapSecurityLevel.PLAIN);
        } else if (secuLevel.equalsIgnoreCase("LDAPS")) {
            _setLdapConfiguration_config
                    .setLdapSecurityLevel(JLdapSecurityLevel.LDAPS);
        } else if (secuLevel.equalsIgnoreCase("StartTls")) {
            _setLdapConfiguration_config
                    .setLdapSecurityLevel(JLdapSecurityLevel.START_TLS);
        } else {
            logger.error("The input LDAP security level is not correct."
                    + "Please input Plain, LDAPS or StartTls only.");
        }
        final JWebResult _setLdapConfiguration__return = port
                .setLdapConfiguration(_setLdapConfiguration_credentials,
                                      _setLdapConfiguration_savePassword,
                                      _setLdapConfiguration_config);
        System.out.println("setLdapConfiguration.result="
                + _setLdapConfiguration__return);
        logger.info("The status of the LDAP configuration setting is: "
                + _setLdapConfiguration__return.getStatus().toString());
        return _setLdapConfiguration__return;
    }

    /**
     * Set the network configuration
     *
     * @param userToken
     * @param systemName
     * @param dscpMaker
     * @param ipv4Address
     * @param subnetMask
     * @param gateWay
     * @param dnsAddress
     * @param dnsAltAddress
     * @param dnsDomain
     * @param legacydnsDomain
     * @return
     */
    public JWebResult setNetworkSettings(final String userToken,
                                         final String systemName,
                                         final String dscpMaker,
                                         final String ipv4Address1,
                                         final String subnetMask1,
                                         final String gateWay1,
                                         final String ipv4Address2,
                                         final String subnetMask2,
                                         final String gateWay2,
                                         final String dnsAddress,
                                         final String dnsAltAddress,
                                         final String dnsDomain,
                                         final String legacydnsDomain) {
        System.out.println("Invoking setNetworkSettings...");
        logger.info("Invoking setNetworkSettings...");
        final JCredentials _setNetworkSettings_credentials = new JCredentials();
        _setNetworkSettings_credentials.setUserToken(userToken);
        final List<JNetworkSettingsKey> _setNetworkSettings_keysVal = new LinkedList<JNetworkSettingsKey>();
        final Holder<List<JNetworkSettingsKey>> _setNetworkSettings_keys = new Holder<List<JNetworkSettingsKey>>(
                _setNetworkSettings_keysVal);
        _setNetworkSettings_keys.value.add(JNetworkSettingsKey.SYSTEM___NAME);
        _setNetworkSettings_keys.value.add(JNetworkSettingsKey.DSCP___MARKER);
        _setNetworkSettings_keys.value.add(JNetworkSettingsKey.NETWORK___MODE);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.IPV_4___ADDRESS___1);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.SUBNET___MASK___1);
        _setNetworkSettings_keys.value.add(JNetworkSettingsKey.GATEWAY___1);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.IPV_4___ADDRESS___2);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.SUBNET___MASK___2);
        _setNetworkSettings_keys.value.add(JNetworkSettingsKey.GATEWAY___2);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.DNS___SERVER___1);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.DNS___SERVER___ALT___1);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.DNS___DOMAIN___1);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.SYSTEM___REDIRECT___FLAG);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.DNS___SERVER___2);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.DNS___SERVER___ALT___2);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.DNS___DOMAIN___2);
        _setNetworkSettings_keys.value
                .add(JNetworkSettingsKey.EAP___METHOD___802___1___X);
        final List<String> _setNetworkSettings_valuesVal = new LinkedList<String>();
        final Holder<List<String>> _setNetworkSettings_values = new Holder<List<String>>(
                _setNetworkSettings_valuesVal);
        _setNetworkSettings_values.value.add(systemName);
        _setNetworkSettings_values.value.add(dscpMaker);
        _setNetworkSettings_values.value.add("0");
        _setNetworkSettings_values.value.add(ipv4Address1);
        _setNetworkSettings_values.value.add(subnetMask1);
        _setNetworkSettings_values.value.add(gateWay1);
        _setNetworkSettings_values.value.add(ipv4Address2);
        _setNetworkSettings_values.value.add(subnetMask2);
        _setNetworkSettings_values.value.add(gateWay2);
        _setNetworkSettings_values.value.add(dnsAddress);
        _setNetworkSettings_values.value.add(dnsAltAddress);
        _setNetworkSettings_values.value.add(dnsDomain);
        _setNetworkSettings_values.value.add("0");
        _setNetworkSettings_values.value.add(dnsAddress);
        _setNetworkSettings_values.value.add("");
        _setNetworkSettings_values.value.add(legacydnsDomain);
        _setNetworkSettings_values.value.add("false");
        final JWebResult _setNetworkSettings__return = port
                .setNetworkSettings(_setNetworkSettings_credentials,
                                    _setNetworkSettings_keys,
                                    _setNetworkSettings_values);
        System.out.println("setNetworkSettings.result="
                + _setNetworkSettings__return);
        System.out.println("setNetworkSettings._setNetworkSettings_keys="
                + _setNetworkSettings_keys.value);
        System.out.println("setNetworkSettings._setNetworkSettings_values="
                + _setNetworkSettings_values.value);
        return _setNetworkSettings__return;
    }

    /**
     * Set security settings
     *
     * @param userToken
     * @param securitySettings
     * @return
     */
    public JWebResult setSecuritySettings(final String userToken,
                                          final JSecuritySettings settings) {
        System.out.println("Invoking setSecuritySettings...");
        logger.info("Invoking setSecuritySettings...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        // return port.setSecuritySettings(credentials, settings);
        return null;
    }

    public void
            setSoftUpdateMaintenanceDuration(final String softupdateMaintenanceDuration) {
        this.softupdateMaintenanceDuration = softupdateMaintenanceDuration;
    }

    public void
            setSoftUpdateMaintenanceEnable(final boolean softupdateMaintenanceEnable) {
        this.softupdateMaintenanceEnable = softupdateMaintenanceEnable;
    }

    public void
            setSoftUpdateMaintenanceStartTime(final String softupdateMaintenanceStartTime) {
        this.softupdateMaintenanceStartTime = softupdateMaintenanceStartTime;
    }

    /**
     * Set softupdate maintenance window values
     *
     * @param userToken
     * @param deviceType
     * @param startTime
     * @param duration
     * @param enableOrNot
     * @return
     */
    public JWebResult
            setSoftupdateMaintenanceWindowValues(final String userToken,
                                                 final String deviceType,
                                                 final String startTime,
                                                 final String duration,
                                                 final boolean enableOrNot) {
        logger.info("Invoking setSoftupdateMaintenanceWindowValues...");
        final JCredentials _setSoftupdateMaintenanceWindowValues_credentials = new JCredentials();
        _setSoftupdateMaintenanceWindowValues_credentials
                .setUserToken(userToken);
        final String _setSoftupdateMaintenanceWindowValues_deviceType = deviceType;
        final boolean _setSoftupdateMaintenanceWindowValues_enabled = enableOrNot;
        final String _setSoftupdateMaintenanceWindowValues_startTime = startTime;
        final String _setSoftupdateMaintenanceWindowValues_duration = duration;
        final JWebResult _setSoftupdateMaintenanceWindowValues__return = port
                .setSoftupdateMaintenanceWindowValues(_setSoftupdateMaintenanceWindowValues_credentials,
                                                      _setSoftupdateMaintenanceWindowValues_deviceType,
                                                      _setSoftupdateMaintenanceWindowValues_enabled,
                                                      _setSoftupdateMaintenanceWindowValues_startTime,
                                                      _setSoftupdateMaintenanceWindowValues_duration);
        return _setSoftupdateMaintenanceWindowValues__return;
    }

    /**
     * Set system time config
     *
     * @param userToken
     * @param systemTimeSettings
     * @return
     */
    public JWebResult
            setSystemTimeConfig(final String userToken,
                                final SystemTimeConfig systemTimeSettings) {
        System.out.println("Invoking setSystemTimeConfig...");
        logger.info("Invoking setSystemTimeConfig...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<Boolean> serversReacheable = new Holder<Boolean>();
        return port.setSystemTimeConfig(credentials,
                                        systemTimeSettings,
                                        serversReacheable);
    }

    public void
            setUnauthorizedUserMessage(final String unauthorizedUserMessage) {
        this.unauthorizedUserMessage = unauthorizedUserMessage;
    }
}
