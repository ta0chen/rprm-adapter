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
import com.polycom.webservices.LicenseManager.JCredentials;
import com.polycom.webservices.LicenseManager.JFeature;
import com.polycom.webservices.LicenseManager.JLicenseManager;
import com.polycom.webservices.LicenseManager.JLicenseManager_Service;
import com.polycom.webservices.LicenseManager.JLicenseViewInfo;
import com.polycom.webservices.LicenseManager.JWebResult;

/**
 * License Manager Handler
 *
 * @author Tao Chen
 *
 */
public class LicenseManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("LicenseManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JLicenseManager");
    JLicenseManager            port;

    /**
     * Construction of LicenseManagerHandler class
     */
    public LicenseManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JLicenseManager) jsonInvocationHandler
                    .getProxy(JLicenseManager.class);
        } else {
            final URL wsdlURL = LicenseManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JLicenseManager.wsdl");
            final JLicenseManager_Service ss = new JLicenseManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJLicenseManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JLicenseManager");
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
     * Get the configuration for preparing to upload the license file
     *
     * @param userToken
     */
    @SuppressWarnings("unused")
    public String getConfigurationForUpload(final String userToken) {
        System.out.println("Invoking getConfigurationForUpload...");
        logger.info("Invoking getConfigurationForUpload...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<String> fileName = new Holder<String>();
        final JWebResult result = port.getConfigurationForUpload(credentials,
                                                                 fileName);
        return fileName.value;
    }

    /**
     * Get the current license content. It is refer to the View License button.
     *
     * @param userToken
     * @return
     */
    public String getCurrentLicenseContent(final String userToken) {
        System.out.println("Invoking getCurrentLicenseContent...");
        logger.info("Invoking getCurrentLicenseContent...");
        final JCredentials _getCurrentLicenseContent_credentials = new JCredentials();
        _getCurrentLicenseContent_credentials.setUserToken(userToken);
        final Holder<String> _getCurrentLicenseContent_content = new Holder<String>();
        final JWebResult _getCurrentLicenseContent__return = port
                .getCurrentLicenseContent(_getCurrentLicenseContent_credentials,
                                          _getCurrentLicenseContent_content);
        System.out.println("getCurrentLicenseContent.result="
                + _getCurrentLicenseContent__return);
        System.out
                .println("getCurrentLicenseContent._getCurrentLicenseContent_content="
                        + _getCurrentLicenseContent_content.value);
        logger.info("getCurrentLicenseContent._getCurrentLicenseContent_content="
                + _getCurrentLicenseContent_content.value);
        return _getCurrentLicenseContent_content.value;
    }

    /**
     * Get the current license information
     *
     * @param userToken
     * @return
     */
    public JLicenseViewInfo getCurrentLicenseInfo(final String userToken) {
        System.out.println("Invoking getCurrentLicenseInfo...");
        logger.info("Invoking getCurrentLicenseInfo...");
        final JCredentials _getCurrentLicenseInfo_credentials = new JCredentials();
        _getCurrentLicenseInfo_credentials.setUserToken(userToken);
        final Holder<JLicenseViewInfo> _getCurrentLicenseInfo_licenseViewInfo = new Holder<JLicenseViewInfo>();
        final JWebResult _getCurrentLicenseInfo__return = port
                .getCurrentLicenseInfo(_getCurrentLicenseInfo_credentials,
                                       _getCurrentLicenseInfo_licenseViewInfo);
        System.out.println("getCurrentLicenseInfo.result="
                + _getCurrentLicenseInfo__return);
        System.out
                .println("getCurrentLicenseInfo._getCurrentLicenseInfo_licenseViewInfo="
                        + _getCurrentLicenseInfo_licenseViewInfo.value);
        return _getCurrentLicenseInfo_licenseViewInfo.value;
    }

    /**
     * Get the resource number by feature name
     *
     * @param userToken
     * @param feature
     * @return
     */
    public int getFeatureResource(final String userToken,
                                  final JFeature feature) {
        System.out.println("Invoking getFeatureResource...");
        logger.info("Invoking getFeatureResource...");
        final JCredentials _getFeatureResource_credentials = new JCredentials();
        _getFeatureResource_credentials.setUserToken(userToken);
        final JFeature _getFeatureResource_feature = feature;
        final Holder<Integer> _getFeatureResource_counts = new Holder<Integer>();
        final JWebResult _getFeatureResource__return = port
                .getFeatureResource(_getFeatureResource_credentials,
                                    _getFeatureResource_feature,
                                    _getFeatureResource_counts);
        System.out.println("getFeatureResource.result="
                + _getFeatureResource__return);
        System.out.println("getFeatureResource._getFeatureResource_counts="
                + _getFeatureResource_counts.value);
        logger.info("getFeatureResource._getFeatureResource_counts="
                + _getFeatureResource_counts.value);
        return _getFeatureResource_counts.value;
    }

    /**
     * Get the uploaded license preview
     *
     * @param userToken
     * @param filename
     * @return
     */
    public JWebResult getLicensePreview(final String userToken,
                                        final String filename) {
        System.out.println("Invoking getLicensePreview...");
        logger.info("Invoking getLicensePreview...");
        final JCredentials _getLicensePreview_credentials = new JCredentials();
        _getLicensePreview_credentials.setUserToken(userToken);
        final String _getLicensePreview_fileName = filename;
        final Holder<JLicenseViewInfo> _getLicensePreview_licenseView = new Holder<JLicenseViewInfo>();
        final JWebResult _getLicensePreview__return = port
                .getLicensePreview(_getLicensePreview_credentials,
                                   _getLicensePreview_fileName,
                                   _getLicensePreview_licenseView);
        System.out.println("getLicensePreview.result="
                + _getLicensePreview__return);
        System.out.println("getLicensePreview._getLicensePreview_licenseView="
                + _getLicensePreview_licenseView.value);
        logger.info("getLicensePreview._getLicensePreview_licenseView="
                + _getLicensePreview_licenseView.value);
        return _getLicensePreview__return;
    }

    /**
     * Upload the specified license file
     *
     * @param userToken
     * @param filename
     * @return
     */
    public JWebResult uploadLicense(final String userToken,
                                    final String filename) {
        System.out.println("Invoking uploadLicense...");
        logger.info("Invoking uploadLicense...");
        final JCredentials _uploadLicense_credentials = new JCredentials();
        _uploadLicense_credentials.setUserToken(userToken);
        final String _uploadLicense_fileName = filename;
        final JWebResult _uploadLicense__return = port
                .uploadLicense(_uploadLicense_credentials,
                               _uploadLicense_fileName);
        System.out.println("uploadLicense.result=" + _uploadLicense__return);
        return _uploadLicense__return;
    }
}
