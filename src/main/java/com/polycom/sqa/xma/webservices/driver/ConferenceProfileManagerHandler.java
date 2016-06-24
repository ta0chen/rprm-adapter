package com.polycom.sqa.xma.webservices.driver;

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
import com.polycom.webservices.ConferenceProfileManager.JConferencePolicy;
import com.polycom.webservices.ConferenceProfileManager.JConferenceProfileAttribute;
import com.polycom.webservices.ConferenceProfileManager.JConferenceProfileManager;
import com.polycom.webservices.ConferenceProfileManager.JConferenceProfileManager_Service;
import com.polycom.webservices.ConferenceProfileManager.JCredentials;
import com.polycom.webservices.ConferenceProfileManager.JWebResult;

/**
 * Conference Profile Manager Handler
 *
 * @author wbchao
 *
 */
public class ConferenceProfileManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("ConferenceProfileManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JConferenceProfileManager");
    JConferenceProfileManager  port;

    /**
     * Construction of ConferenceProfileManagerHandler class
     */
    public ConferenceProfileManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JConferenceProfileManager) jsonInvocationHandler
                    .getProxy(JConferenceProfileManager.class);
        } else {
            final URL wsdlURL = ConferenceProfileManagerHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JConferenceProfileManager.wsdl");
            final JConferenceProfileManager_Service ss = new JConferenceProfileManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJConferenceProfileManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JConferenceProfileManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    @SuppressWarnings("unused")
    public JConferencePolicy downloadProfileById(final String userToken,
                                                 final String mcuId,
                                                 final long profileId) {
        System.out.println("Invoking downloadProfileById...");
        logger.info("Invoking downloadProfileById...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JConferencePolicy> profile = new Holder<JConferencePolicy>();
        final JWebResult result = port
                .downloadProfileById(credentials, mcuId, profileId, profile);
        return profile.value;
    }

    public List<JConferenceProfileAttribute>
            getProfileList(final String userToken, final String mcuId) {
        System.out.println("Invoking getProfileList...");
        logger.info("Invoking getProfileList...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JConferenceProfileAttribute>> profiles = new Holder<List<JConferenceProfileAttribute>>();
        port.getProfileList(credentials, mcuId, profiles);
        return profiles.value;
    }
}
