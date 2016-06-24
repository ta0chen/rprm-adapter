package com.polycom.sqa.xma.webservices.driver;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.log4j.Logger;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.interceptor.JsonInvocationHandler;
import com.polycom.sqa.xma.webservices.driver.interceptor.SoapHeaderOutInterceptor;
import com.polycom.webservices.RsyslogManager.JCredentials;
import com.polycom.webservices.RsyslogManager.JRsyslogManager;
import com.polycom.webservices.RsyslogManager.JRsyslogManager_Service;
import com.polycom.webservices.RsyslogManager.JWebResult;

/**
 * Rsyslog Manager Handler
 *
 * @author wbchao
 *
 */
public class RsyslogManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("RsyslogManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JRsyslogManager");
    private JRsyslogManager    port;

    /**
     * Construction of RsyslogManagerHandler class
     */
    public RsyslogManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JRsyslogManager) jsonInvocationHandler
                    .getProxy(JRsyslogManager.class);
        } else {
            final URL wsdlURL = RsyslogManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JRsyslogManager.wsdl");
            final JRsyslogManager_Service ss = new JRsyslogManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJRsyslogManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JRsyslogManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public JWebResult rotate(final String userToken,
                             final String logconfigSOAP) {
        System.out.println("Invoking rotate...");
        logger.info("Invoking rotate...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.rotate(credentials, logconfigSOAP);
    }
}
