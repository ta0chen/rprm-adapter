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
import com.polycom.webservices.AnytimeManager.JAnytimeManager;
import com.polycom.webservices.AnytimeManager.JAnytimeManager_Service;
import com.polycom.webservices.AnytimeManager.JCredentials;
import com.polycom.webservices.AnytimeManager.JDmaConferenceTemplate;
import com.polycom.webservices.AnytimeManager.JWebResult;

/**
 * Scheduler engine handler
 *
 * @author Lingxi Zhao
 *
 */
public class AnytimeManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("AnytimeManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JAnytimeManager");
    URL                        wsdlURL;
    JAnytimeManager_Service    ss;
    JAnytimeManager            port;

    /**
     * Construction of SchedulerEngineHandler class
     */
    public AnytimeManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JAnytimeManager) jsonInvocationHandler
                    .getProxy(JAnytimeManager.class);
        } else {
            final URL wsdlURL = AnytimeManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JAnytimeManager.wsdl");
            final JAnytimeManager_Service ss = new JAnytimeManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJAnytimeManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JAnytimeManager");
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
     * Get DMA Policies
     *
     * @param credentials
     *
     */
    public List<JDmaConferenceTemplate>
            getDmaConferenceTemplates(final String userToken) {
        final JCredentials _getDmaConferenceTemplates_credentials = new JCredentials();
        _getDmaConferenceTemplates_credentials.setUserToken(userToken);
        final Holder<List<JDmaConferenceTemplate>> _getDmaConferenceTemplates_dmaConferenceTemplates = new Holder<List<JDmaConferenceTemplate>>();
        final JWebResult _getDmaConferenceTemplates__return = port
                .getDMATemplates(_getDmaConferenceTemplates_credentials,
                                 _getDmaConferenceTemplates_dmaConferenceTemplates);
        System.out
                .println("getDmaConferenceTemplates._getDmaConferenceTemplates_schedResult="
                        + _getDmaConferenceTemplates__return.toString());
        return _getDmaConferenceTemplates_dmaConferenceTemplates.value;
    }
}
