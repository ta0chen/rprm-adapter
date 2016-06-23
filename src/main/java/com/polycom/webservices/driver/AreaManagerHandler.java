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
import com.polycom.webservices.AreaManager.JArea;
import com.polycom.webservices.AreaManager.JAreaManager;
import com.polycom.webservices.AreaManager.JAreaManager_Service;
import com.polycom.webservices.AreaManager.JAreaResourceInventory;
import com.polycom.webservices.AreaManager.JCredentials;
import com.polycom.webservices.AreaManager.JWebResult;

/**
 * Area Manager Handler
 *
 * @author Wenbo Chao
 *
 */
public class AreaManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("AreaManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JAreaManager");
    JAreaManager               port;

    /**
     * Construction of AreaManagerHandler class
     */
    public AreaManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JAreaManager) jsonInvocationHandler
                    .getProxy(JAreaManager.class);
        } else {
            final URL wsdlURL = AddressBookManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JAreaManagerHandler.wsdl");
            final JAreaManager_Service ss = new JAreaManager_Service(wsdlURL,
                    SERVICE_NAME);
            port = ss.getJAreaManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext().put(
                                             BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                             webServiceUrl + "/JAreaManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public JWebResult addArea(final String userToken, final JArea area) {
        logger.info("Invoking addArea...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.addArea(credentials, area);
    }

    public JWebResult deleteArea(final String userToken, final JArea area) {
        logger.info("Invoking deleteArea...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JAreaResourceInventory> resourceInventory = new Holder<JAreaResourceInventory>();
        return port.deleteArea(credentials, area, resourceInventory);
    }

    public List<JArea> getAreas(final String userToken) {
        logger.info("Invoking getAreasConfiguration...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<Boolean> areasEnabled = new Holder<Boolean>();
        final Holder<Boolean> areasConfigured = new Holder<Boolean>();
        final Holder<Boolean> areasLicensed = new Holder<Boolean>();
        final Holder<List<JArea>> areas = new Holder<List<JArea>>();
        final Holder<String> singularName = new Holder<String>();
        final Holder<String> pluralName = new Holder<String>();
        port.getAreasConfiguration(credentials,
                                   areasEnabled,
                                   areasConfigured,
                                   areasLicensed,
                                   areas,
                                   singularName,
                                   pluralName);
        return areas.value;
    }

    public JWebResult setAreasConfiguration(final String userToken,
                                            final String singular,
                                            final String plural,
                                            final boolean configure) {
        logger.info("Invoking setAreasConfiguration...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.setAreasConfiguration(credentials,
                                          singular,
                                          plural,
                                          configure);
    }
}
