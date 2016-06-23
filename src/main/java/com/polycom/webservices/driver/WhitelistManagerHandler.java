package com.polycom.webservices.driver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import com.polycom.webservices.WhitelistManager.JCredentials;
import com.polycom.webservices.WhitelistManager.JWebResult;
import com.polycom.webservices.WhitelistManager.JWhitelistManager;
import com.polycom.webservices.WhitelistManager.JWhitelistManager_Service;

/**
 * White List Manager Handler
 *
 * @author Tao Chen
 *
 */
public class WhitelistManagerHandler {
    protected static Logger    logger          = Logger.getLogger("WhitelistManagerHandler");
    private static final QName SERVICE_NAME    = new QName(
                                                           "http://polycom.com/WebServices",
            "JWhiteListManager");
    JWhitelistManager          port;
    private Boolean            whiteListEnable = false;
    private List<String>       whiteList       = new ArrayList<String>();
    private String             clientIpAddress = "";

    /**
     * Construction of WhitelistManagerHandler class
     */
    public WhitelistManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JWhitelistManager) jsonInvocationHandler
                    .getProxy(JWhitelistManager.class);
        } else {
            final URL wsdlURL = WhitelistManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JWhiteListManager.wsdl");
            final JWhitelistManager_Service ss = new JWhitelistManager_Service(
                                                                               wsdlURL, SERVICE_NAME);
            port = ss.getJWhitelistManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
            .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                 webServiceUrl + "/JWhiteListManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
            .add(new SoapHeaderOutInterceptor());
        }
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    /**
     * Get the white list on the RPRM
     *
     * @param userToken
     * @return
     */
    public JWebResult getWhitelist(final String userToken) {
        logger.info("Invoking getWhitelist...");
        final JCredentials _getWhitelist_credentials = new JCredentials();
        _getWhitelist_credentials.setUserToken(userToken);
        final Holder<Boolean> _getWhitelist_whitelistEnabled = new Holder<Boolean>();
        final Holder<List<String>> _getWhitelist_whitelist = new Holder<List<String>>();
        final Holder<String> _getWhitelist_clientIpAddress = new Holder<String>();
        final JWebResult _getWhitelist__return = port
                .getWhitelist(_getWhitelist_credentials,
                              _getWhitelist_whitelistEnabled,
                              _getWhitelist_whitelist,
                              _getWhitelist_clientIpAddress);
        setWhiteListEnableStatus(_getWhitelist_whitelistEnabled.value);
        setWhiteList(_getWhitelist_whitelist.value);
        setClientIpAddress(_getWhitelist_clientIpAddress.value);

        return _getWhitelist__return;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public Boolean getWhiteListEnableStatus() {
        return whiteListEnable;
    }

    /**
     * Save the white list on the RPRM
     *
     * @param userToken
     * @param whitelistEnabled
     * @param whitelist
     * @return
     */
    public JWebResult saveWhitelist(final String userToken,
            final Boolean whitelistEnabled,
            final List<String> whitelist) {
        logger.info("Invoking saveWhitelist...");
        final JCredentials _saveWhitelist_credentials = new JCredentials();
        _saveWhitelist_credentials.setUserToken(userToken);
        final Boolean _saveWhitelist_whitelistEnabled = whitelistEnabled;
        final List<String> _saveWhitelist_whitelist = whitelist;
        final JWebResult _saveWhitelist__return = port
                .saveWhitelist(_saveWhitelist_credentials,
                               _saveWhitelist_whitelistEnabled,
                               _saveWhitelist_whitelist);
        return _saveWhitelist__return;
    }

    public void setClientIpAddress(final String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public void setWhiteList(final List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public void setWhiteListEnableStatus(final Boolean whiteListEnable) {
        this.whiteListEnable = whiteListEnable;
    }
}
