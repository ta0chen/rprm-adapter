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
import com.polycom.webservices.AudioSoftupdateManager.JAudioSoftupdateManager;
import com.polycom.webservices.AudioSoftupdateManager.JAudioSoftupdateManager_Service;
import com.polycom.webservices.AudioSoftupdateManager.JCredentials;
import com.polycom.webservices.AudioSoftupdateManager.JSoftupdateVersion;
import com.polycom.webservices.AudioSoftupdateManager.JSoftupdateVersionPolicy;
import com.polycom.webservices.AudioSoftupdateManager.JWebResult;

/**
 * Audio Soft update handler
 *
 * @author Wenbo Chao
 *
 */
public class AudioSoftupdateManagerHandler {
    protected static Logger         logger       = Logger
            .getLogger("AudioSoftupdateManagerHandler");
    private static final QName      SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JAudioSoftupdateManager");
    private JAudioSoftupdateManager port;

    /**
     * Construction of the SoftupdateManagerHandler class
     */
    public AudioSoftupdateManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JAudioSoftupdateManager) jsonInvocationHandler
                    .getProxy(JAudioSoftupdateManager.class);
        } else {
            final URL wsdlURL = AudioSoftupdateManagerHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JAudioSoftupdateManager.wsdl");
            final JAudioSoftupdateManager_Service ss = new JAudioSoftupdateManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJAudioSoftupdateManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JAudioSoftupdateManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public JWebResult addAudioSoftupdate(final String userToken,
                                         final String description,
                                         final String softupdateFilename) {
        System.out.println("Invoking addAudioSoftupdate...");
        logger.info("Invoking addAudioSoftupdate...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JSoftupdateVersion> version = new Holder<JSoftupdateVersion>();
        final Holder<Boolean> policyAssumed = new Holder<Boolean>();
        return port.addAudioSoftupdate(credentials,
                                       description,
                                       softupdateFilename,
                                       version,
                                       policyAssumed);
    }

    public JWebResult
            deleteAudioSoftupdate(final String userToken,
                                  final List<Integer> softUpdateVersionIds) {
        System.out.println("Invoking deleteAudioSoftupdate...");
        logger.info("Invoking deleteAudioSoftupdate...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<Boolean> policyCleared = new Holder<Boolean>();
        return port.deleteAudioSoftupdate(credentials,
                                          softUpdateVersionIds,
                                          policyCleared);
    }

    public List<JSoftupdateVersion> getAudioImages(final String userToken,
                                                   final Integer areaUgpId) {
        System.out.println("Invoking getAudioImages...");
        logger.info("Invoking getAudioImages...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JSoftupdateVersion>> softupdates = new Holder<List<JSoftupdateVersion>>();
        final Holder<JSoftupdateVersionPolicy> policy = new Holder<JSoftupdateVersionPolicy>();
        port.getAudioImages(credentials, areaUgpId, softupdates, policy);
        return softupdates.value;
    }

    public JWebResult updateAudioPolicy(final String userToken,
                                        final Integer areaUgpId,
                                        final String versionToUse) {
        System.out.println("Invoking updateAudioPolicy...");
        logger.info("Invoking updateAudioPolicy...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.updateAudioPolicy(credentials, areaUgpId, versionToUse);
    }
}