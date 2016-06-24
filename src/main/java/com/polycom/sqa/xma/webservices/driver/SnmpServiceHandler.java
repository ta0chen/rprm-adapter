package com.polycom.sqa.xma.webservices.driver;

import java.net.URL;

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
import com.polycom.webservices.SnmpService.JCredentials;
import com.polycom.webservices.SnmpService.JSnmpConfiguration;
import com.polycom.webservices.SnmpService.JSnmpService;
import com.polycom.webservices.SnmpService.JSnmpService_Service;
import com.polycom.webservices.SnmpService.JWebResult;

/**
 * Snmp Service handler
 *
 * @author Wenbo Chao
 *
 */
public class SnmpServiceHandler {
    protected static Logger    logger       = Logger.getLogger("SnmpService");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JSnmpService");
    private JSnmpService       port;

    /**
     * Construction of the SnmpServiceHandler class
     */
    public SnmpServiceHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JSnmpService) jsonInvocationHandler
                    .getProxy(JSnmpService.class);
        } else {
            final URL wsdlURL = SnmpServiceHandler.class.getClassLoader()
                    .getResource("wsdl/current/JSnmpService.wsdl");
            final JSnmpService_Service ss = new JSnmpService_Service(wsdlURL,
                    SERVICE_NAME);
            port = ss.getJSnmpServicePort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext().put(
                                             BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                             webServiceUrl + "/JSnmpService");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public JSnmpConfiguration getSnmpConfiguration(final String userToken) {
        System.out.println("Invoking getSnmpConfiguration...");
        logger.info("Invoking getSnmpConfiguration...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JSnmpConfiguration> snmpConfiguration = new Holder<JSnmpConfiguration>();
        port.getSnmpConfiguration(credentials, snmpConfiguration);
        return snmpConfiguration.value;
    }

    public JWebResult
            saveSnmpConfiguration(final String userToken,
                                  final JSnmpConfiguration snmpConfiguration) {
        System.out.println("Invoking saveSnmpConfiguration...");
        logger.info("Invoking saveSnmpConfiguration...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.saveSnmpConfiguration(credentials, snmpConfiguration);
    }
}