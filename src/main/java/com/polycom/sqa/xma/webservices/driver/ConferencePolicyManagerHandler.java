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
import com.polycom.webservices.ConferencePolicyManager.JConferencePolicy;
import com.polycom.webservices.ConferencePolicyManager.JConferencePolicyManager;
import com.polycom.webservices.ConferencePolicyManager.JConferencePolicyManager_Service;
import com.polycom.webservices.ConferencePolicyManager.JCredentials;
import com.polycom.webservices.ConferencePolicyManager.JWebResult;

/**
 * Conference PolicyManager manager handler
 *
 * @author wbchao
 *
 */
public class ConferencePolicyManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("ConferencePolicyManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JConferencePolicyManager");
    JConferencePolicyManager   port;

    /**
     * Construction of ConferenceProfileManagerHandler class
     */
    public ConferencePolicyManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JConferencePolicyManager) jsonInvocationHandler
                    .getProxy(JConferencePolicyManager.class);
        } else {
            final URL wsdlURL = ConferencePolicyManagerHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JConferencePolicyManager.wsdl");
            final JConferencePolicyManager_Service ss = new JConferencePolicyManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJConferencePolicyManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JConferencePolicyManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public JWebResult
            addConferencePolicy(final String userToken,
                                final JConferencePolicy conferencePolicy) {
        System.out.println("Invoking addConferencePolicy...");
        logger.info("Invoking addConferencePolicy...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.addConferencePolicy(credentials, conferencePolicy);
    }

    public JWebResult deleteConferencePolicyById(final String userToken,
                                                 final String policyId) {
        System.out.println("Invoking deleteConferencePolicyById...");
        logger.info("Invoking deleteConferencePolicyById...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.deleteConferencePolicyById(credentials, policyId);
    }

    public JWebResult
            eidtConferencePolicy(final String userToken,
                                 final JConferencePolicy conferencePolicy) {
        System.out.println("Invoking editConferencePolicy...");
        logger.info("Invoking eidtConferencePolicy...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.editConferencePolicy(credentials, conferencePolicy);
    }

    public List<JConferencePolicy>
            getAllConferencePolicy(final String userToken) {
        System.out.println("Invoking getAllConferencePolicy...");
        logger.info("Invoking getAllConferencePolicy...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JConferencePolicy>> profiles = new Holder<List<JConferencePolicy>>();
        port.getAllConferencePolicy(credentials, profiles);
        return profiles.value;
    }

    public JConferencePolicy getByConferencePolicyId(final String userToken,
                                                     final String policyId) {
        System.out.println("Invoking getByConferencePolicyId...");
        logger.info("Invoking getByConferencePolicyId...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JConferencePolicy> conferencePolicy = new Holder<JConferencePolicy>();
        port.getByConferencePolicyId(credentials, policyId, conferencePolicy);
        return conferencePolicy.value;
    }
}
