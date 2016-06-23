package com.polycom.webservices.driver;

import java.net.URL;
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
import com.polycom.webservices.RealConnectProxyManager.JCredentials;
import com.polycom.webservices.RealConnectProxyManager.JRealConnectProxyEntry;
import com.polycom.webservices.RealConnectProxyManager.JRealConnectProxyManager;
import com.polycom.webservices.RealConnectProxyManager.JRealConnectProxyManager_Service;
import com.polycom.webservices.RealConnectProxyManager.JWebResult;

/**
 * Real Connect Proxy Manager Handler
 *
 * @author wbchao
 *
 */
public class RealConnectProxyManagerHandler {
    protected static Logger                logger       = Logger
            .getLogger("RealConnectProxyHandler");
    private static final QName             SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JRealConnectProxyManager");
    private final JRealConnectProxyManager port;

    /**
     * Construction of DeviceManagerHandler class
     */
    public RealConnectProxyManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JRealConnectProxyManager) jsonInvocationHandler
                    .getProxy(JRealConnectProxyManager.class);
        } else {
            final URL wsdlURL = RealConnectProxyManagerHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JRealConnectProxyManager.wsdl");
            final JRealConnectProxyManager_Service ss = new JRealConnectProxyManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJRealConnectProxyManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JRealConnectProxyManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public JWebResult addProxyEntry(final String userToken,
                                    final JRealConnectProxyEntry entry) {
        System.out.println("Invoking addProxyEntry...");
        logger.info("Invoking addProxyEntry...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.addProxyEntry(credentials, entry);
    }

    public JWebResult deleteProxyEntryByFQDN(final String userToken,
                                             final String proxyFdqn) {
        System.out.println("Invoking deleteProxyEntryByFQDN...");
        logger.info("Invoking deleteProxyEntryByFQDN...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.deleteProxyEntryByFQDN(credentials, proxyFdqn);
    }

    public List<JRealConnectProxyEntry>
            getProxyEntries(final String userToken) {
        System.out.println("Invoking getProxyEntries...");
        logger.info("Invoking getProxyEntries...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JRealConnectProxyEntry>> proxyEntries = new Holder<List<JRealConnectProxyEntry>>();
        port.getProxyEntries(credentials, proxyEntries);
        return proxyEntries.value;
    }
}
