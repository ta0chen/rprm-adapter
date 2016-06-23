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
import com.polycom.webservices.BundledProvisioningManager.JBundledProvisioningManager;
import com.polycom.webservices.BundledProvisioningManager.JBundledProvisioningManager_Service;
import com.polycom.webservices.BundledProvisioningManager.JCredentials;
import com.polycom.webservices.BundledProvisioningManager.JEndpointForList;
import com.polycom.webservices.BundledProvisioningManager.JProvisioningBundleAttr;
import com.polycom.webservices.BundledProvisioningManager.JWebResult;

/**
 * Bundled Provisioning handler
 *
 * @author Wenbo Chao
 *
 */
public class BundledProvisioningManagerHandler {
    protected static Logger             logger       = Logger
            .getLogger("BundledProvisioningManager");
    private static final QName          SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JBundledProvisioningManager");
    private JBundledProvisioningManager port;

    /**
     * Construction of the BundledProvisioningManagerHandler class
     */
    public BundledProvisioningManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JBundledProvisioningManager) jsonInvocationHandler
                    .getProxy(JBundledProvisioningManager.class);
        } else {
            final URL wsdlURL = BundledProvisioningManagerHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JBundledProvisioningManager.wsdl");
            final JBundledProvisioningManager_Service ss = new JBundledProvisioningManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJBundledProvisioningManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JBundledProvisioningManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public JWebResult downloadBundle(final String userToken,
                                     final int deviceId,
                                     final String bundleName,
                                     final String bundleDescription) {
        System.out.println("Invoking downloadBundle...");
        logger.info("Invoking downloadBundle...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.downloadBundle(credentials,
                                   deviceId,
                                   bundleName,
                                   bundleDescription);
    }

    public List<JEndpointForList>
            getAvailableDevicesByBundle(final String userToken,
                                        final int bundleIdVar) {
        System.out.println("Invoking getAvailableDevicesByBundle...");
        logger.info("Invoking getAvailableDevicesByBundle...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<Integer> bundleId = new Holder<Integer>(bundleIdVar);
        final Holder<List<JEndpointForList>> availableDevices = new Holder<List<JEndpointForList>>();
        port.getAvailableDevicesByBundle(credentials,
                                         bundleId,
                                         availableDevices);
        return availableDevices.value;
    }

    public List<JProvisioningBundleAttr> listBundles(final String userToken) {
        System.out.println("Invoking listBundles...");
        logger.info("Invoking listBundles...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JProvisioningBundleAttr>> bundles = new Holder<List<JProvisioningBundleAttr>>();
        port.listBundles(credentials, bundles);
        return bundles.value;
    }

    public JWebResult swapAndDeleteBundles(final String userToken,
                                           final List<JProvisioningBundleAttr> bundlesToBeDeleted,
                                           final int toBundleId) {
        System.out.println("Invoking swapAndDeleteBundles...");
        logger.info("Invoking swapAndDeleteBundles...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<String>> failedBundles = new Holder<List<String>>();
        return port.swapAndDeleteBundles(credentials,
                                         bundlesToBeDeleted,
                                         toBundleId,
                                         failedBundles);
    }

    public JWebResult updateBundle(final String userToken,
                                   final JProvisioningBundleAttr bundleVar) {
        System.out.println("Invoking updateBundle...");
        logger.info("Invoking updateBundle...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JProvisioningBundleAttr> bundle = new Holder<JProvisioningBundleAttr>(
                bundleVar);
        return port.updateBundle(credentials, bundle);
    }
}