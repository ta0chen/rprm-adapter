package com.polycom.webservices.driver;

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
import com.polycom.webservices.PatchUpgrade.JCredentials;
import com.polycom.webservices.PatchUpgrade.JPatchUpgrade;
import com.polycom.webservices.PatchUpgrade.JPatchUpgrade_Service;
import com.polycom.webservices.PatchUpgrade.JStatus;
import com.polycom.webservices.PatchUpgrade.JWebResult;

/**
 * RPRM patch upgrade handler
 *
 * @author Tao Chen
 *
 */
public class PatchUpgradeManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("PatchUpgradeManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JPatchUpgrade");
    JPatchUpgrade              port;

    /**
     * Construction of the PatchUpgradeHandler class
     */
    public PatchUpgradeManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JPatchUpgrade) jsonInvocationHandler
                    .getProxy(JPatchUpgrade.class);
        } else {
            final URL wsdlURL = GroupManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JPatchUpgrade.wsdl");
            final JPatchUpgrade_Service ss = new JPatchUpgrade_Service(wsdlURL,
                    SERVICE_NAME);
            port = ss.getJPatchUpgradePort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext().put(
                                             BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                             webServiceUrl + "/JPatchUpgrade");
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
     * Check if the uploaded patch is acceptable for upgrade
     *
     * @param userToken
     * @return
     */
    public JWebResult canUpgrade(final String userToken) {
        logger.info("Invoking canUpgrade...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult canUpgrade_return = port.canUpgrade(credentials);
        return canUpgrade_return;
    }

    /**
     * Get the patch uppacking status
     *
     * @param userToken
     * @return
     */
    @SuppressWarnings("unused")
    public JStatus getUnpackingStatus(final String userToken) {
        logger.info("Invoking getUnpackingStatus...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JStatus> status = new Holder<JStatus>();
        final JWebResult getUnpackingStatus_return = port
                .getUnpackingStatus(credentials, status);
        return status.value;
    }

    /**
     * Trigger upgrade
     *
     * @param userToken
     * @return
     */
    public JWebResult runUpgrade(final String userToken) {
        logger.info("Invoking runUpgrade...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult runUpgrade_return = port.runUpgrade(credentials);
        return runUpgrade_return;
    }

    /**
     * Trigger patch package unpacking
     *
     * @param userToken
     * @param fileName
     * @return
     */
    public JWebResult startUpgradeUnpacking(final String userToken,
                                            final String fileName) {
        logger.info("Invoking startUpgradeUnpacking...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final String _startUpgradeUnpacking_fileName = fileName;
        final JWebResult startUpgradeUnpacking_return = port
                .startUpgradeUnpacking(credentials,
                                       _startUpgradeUnpacking_fileName);
        return startUpgradeUnpacking_return;
    }
}
