package com.polycom.sqa.xma.webservices.driver;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.log4j.Logger;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.interceptor.JsonInvocationHandler;
import com.polycom.sqa.xma.webservices.driver.interceptor.SoapHeaderOutInterceptor;
import com.polycom.webservices.RedundancyManager.JCredentials;
import com.polycom.webservices.RedundancyManager.JRedundancyConfig;
import com.polycom.webservices.RedundancyManager.JRedundancyManager;
import com.polycom.webservices.RedundancyManager.JRedundancyManager_Service;
import com.polycom.webservices.RedundancyManager.JRedundancyServer;
import com.polycom.webservices.RedundancyManager.JWebResult;

/**
 * Redundancy manager handler
 *
 * @author Tao Chen
 *
 */
public class RedundancyManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("RedundancyManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JRedundancyManager");
    JRedundancyManager         port;

    /**
     * Construction of RedundancyManagerHandler class
     */
    public RedundancyManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JRedundancyManager) jsonInvocationHandler
                    .getProxy(JRedundancyManager.class);
        } else {
            final URL wsdlURL = RedundancyManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JRedundancyManager.wsdl");
            final JRedundancyManager_Service ss = new JRedundancyManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJRedundancyManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JRedundancyManager");
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
     * Set up redundancy server
     *
     * @param userToken
     * @param publicIP
     * @param destIP
     * @param servIP
     * @param vhostName
     * @param flag
     * @return
     * @throws WebServiceException
     *             Throw WebServiceException when the XMA rebooted
     */
    public JWebResult addRedundancyServer(final String userToken,
                                          final JRedundancyConfig config)
                                                  throws WebServiceException {
        System.out.println("Invoking addRedundancyServer...");
        logger.info("Invoking addRedundancyServer...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port.addRedundancyServer(credentials, config);
        return result;
    }

    /**
     * Get the fail over information on the redundancy server such as last fail
     * over time and reason
     *
     * @param userToken
     * @return
     */
    @SuppressWarnings("unused")
    public Map<String, String>
            getAllRedundancyServer4FailoverInfo(final String userToken) {
        System.out
                .println("Invoking getAllRedundancyServer4FailoverInfo for getting the fail over information...");
        logger.info("Invoking getAllRedundancyServer4FailoverInfo for getting the fail over information...");
        final Map<String, String> failoverInfo = new HashMap<String, String>();
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JRedundancyServer>> servers = new Holder<List<JRedundancyServer>>();
        final Holder<JRedundancyConfig> config = new Holder<JRedundancyConfig>();
        final Holder<String> lastFailoverTime = new Holder<String>();
        final Holder<String> lastFailoverReason = new Holder<String>();
        final JWebResult _getAllRedundancyServers__return = port
                .getAllRedundancyServers(credentials,
                                         servers,
                                         config,
                                         lastFailoverTime,
                                         lastFailoverReason);
        failoverInfo.put("lastFailoverTime", lastFailoverTime.value);
        failoverInfo.put("lastFailoverReason", lastFailoverReason.value);
        return failoverInfo;
    }

    /**
     * Get each server unit information on the redundancy server
     *
     * @param userToken
     * @return
     */
    public List<JRedundancyServer>
            getAllRedundancyServer4ServerInfo(final String userToken) {
        System.out
                .println("Invoking getAllRedundancyServer4ServerInfo for getting each server information...");
        logger.info("Invoking getAllRedundancyServer4ServerInfo for getting each server information...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JRedundancyServer>> servers = new Holder<List<JRedundancyServer>>();
        final Holder<JRedundancyConfig> config = new Holder<JRedundancyConfig>();
        final Holder<String> lastFailoverTime = new Holder<String>();
        final Holder<String> lastFailoverReason = new Holder<String>();
        port.getAllRedundancyServers(credentials,
                                     servers,
                                     config,
                                     lastFailoverTime,
                                     lastFailoverReason);
        return servers.value;
    }

    /**
     * Get redundancy server network and host name configuration information
     *
     * @param userToken
     * @return
     */
    @SuppressWarnings("unused")
    public JRedundancyConfig
            getAllRedundancyServerConfig(final String userToken) {
        System.out
                .println("Invoking getAllRedundancyServers for the network configuration information...");
        logger.info("Invoking getAllRedundancyServers for the network configuration information...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JRedundancyServer>> servers = new Holder<List<JRedundancyServer>>();
        final Holder<JRedundancyConfig> config = new Holder<JRedundancyConfig>();
        final Holder<String> lastFailoverTime = new Holder<String>();
        final Holder<String> lastFailoverReason = new Holder<String>();
        final JWebResult result = port
                .getAllRedundancyServers(credentials,
                                         servers,
                                         config,
                                         lastFailoverTime,
                                         lastFailoverReason);
        return config.value;
    }

    /**
     * Get redundancy server network and host name configuration information
     *
     * @param userToken
     * @return
     */
    @SuppressWarnings("unused")
    public List<JRedundancyServer>
            getAllRedundancyServers(final String userToken) {
        System.out
                .println("Invoking getAllRedundancyServers for the network configuration information...");
        logger.info("Invoking getAllRedundancyServers for the network configuration information...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JRedundancyServer>> servers = new Holder<List<JRedundancyServer>>();
        final Holder<JRedundancyConfig> config = new Holder<JRedundancyConfig>();
        final Holder<String> lastFailoverTime = new Holder<String>();
        final Holder<String> lastFailoverReason = new Holder<String>();
        final JWebResult result = port
                .getAllRedundancyServers(credentials,
                                         servers,
                                         config,
                                         lastFailoverTime,
                                         lastFailoverReason);
        return servers.value;
    }

    /**
     * Check if the server is configured as redundancy
     *
     * @return
     */
    public boolean isRedundancyConfigured() {
        System.out.println("Invoking isRedundancyConfigured...");
        logger.info("Invoking isRedundancyConfigured...");
        final Holder<Boolean> _isRedundancyConfigured_isRedundancy = new Holder<Boolean>();
        final JWebResult _isRedundancyConfigured__return = port
                .isRedundancyConfigured(_isRedundancyConfigured_isRedundancy);
        System.out.println("isRedundancyConfigured.result="
                + _isRedundancyConfigured__return);
        System.out
                .println("isRedundancyConfigured._isRedundancyConfigured_isRedundancy="
                        + _isRedundancyConfigured_isRedundancy.value);
        return _isRedundancyConfigured_isRedundancy.value;
    }

    /**
     * Reset redundancy server
     *
     * @param userToken
     * @return
     * @throws WebServiceException
     *             Throw WebServiceException when the XMA rebooted
     */
    public JWebResult resetRedundancyServer(final String userToken)
            throws WebServiceException {
        System.out.println("Invoking resetRedundancyServer...");
        logger.info("Invoking resetRedundancyServer...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult _resetRedundancyServer__return = port
                .resetRedundancyServer(credentials);
        System.out.println("resetRedundancyServer.result="
                + _resetRedundancyServer__return);
        return _resetRedundancyServer__return;
    }

    /**
     * Switch the redundancy server
     *
     * @param userToken
     * @return
     */
    public JWebResult switchRedundancyServer(final String userToken) {
        System.out.println("Invoking switchRedundancyServer...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final com.polycom.webservices.RedundancyManager.JWebResult _switchRedundancyServer__return = port
                .switchRedundancyServer(credentials);
        System.out.println("switchRedundancyServer.result="
                + _switchRedundancyServer__return);
        return _switchRedundancyServer__return;
    }

    /**
     * Set the local and remote peer password when setting up redundant server
     *
     * @param userToken
     * @param whichPassword
     * @param password
     * @param config
     * @return JWebResult
     */
    public Integer testPassword(final String userToken,
                                final int whichPassword,
                                final String password,
                                final JRedundancyConfig config) {
        System.out.println("Invoking testPassword...");
        logger.info("Invoking testPassword...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<Integer> _testPassword_whichPassword = new Holder<Integer>(
                whichPassword);
        final Holder<String> _testPassword_password = new Holder<String>(
                password);
        final Holder<JRedundancyConfig> _testPassword_config = new Holder<JRedundancyConfig>(
                config);
        final Holder<Integer> _testPassword_testResult = new Holder<Integer>();
        port.testPassword(credentials,
                          _testPassword_whichPassword,
                          _testPassword_password,
                          _testPassword_config,
                          _testPassword_testResult);
        return _testPassword_testResult.value;
    }
}
