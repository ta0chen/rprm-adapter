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
import com.polycom.webservices.DashboardManager.JCMAConfigPodRequest;
import com.polycom.webservices.DashboardManager.JCMAInfoPodRequest;
import com.polycom.webservices.DashboardManager.JCredentials;
import com.polycom.webservices.DashboardManager.JDashboardDataRequest;
import com.polycom.webservices.DashboardManager.JDashboardDataResponse;
import com.polycom.webservices.DashboardManager.JDashboardManager;
import com.polycom.webservices.DashboardManager.JDashboardManager_Service;
import com.polycom.webservices.DashboardManager.JDashboardUserPreferences;
import com.polycom.webservices.DashboardManager.JDmaPodRequest;
import com.polycom.webservices.DashboardManager.JLicensesPodRequest;
import com.polycom.webservices.DashboardManager.JPbxPodRequest;
import com.polycom.webservices.DashboardManager.JRedundancyPodRequest;
import com.polycom.webservices.DashboardManager.JUMCDashboardDataRequest;
import com.polycom.webservices.DashboardManager.JUMCDashboardDataResponse;
import com.polycom.webservices.DashboardManager.JWebResult;

/**
 * Dash board manager handler
 *
 * @author Tao Chen
 *
 */
public class DashboardManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("DashboardManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JDashboardManager");
    JDashboardManager          port;

    /**
     * Construction of DashboardManagerHandler class
     */
    public DashboardManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JDashboardManager) jsonInvocationHandler
                    .getProxy(JDashboardManager.class);
        } else {
            final URL wsdlURL = DashboardManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JDashboardManager.wsdl");
            final JDashboardManager_Service ss = new JDashboardManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJDashboardManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JDashboardManager");
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
     * Get dash board data from XMA
     *
     * @param userToken
     * @return
     */
    public JDashboardDataResponse getDashboardData(final String userToken) {
        System.out.println("Invoking getDashboardData...");
        logger.info("Invoking getDashboardData...");
        final JCredentials _getDashboardData_credentials = new JCredentials();
        _getDashboardData_credentials.setUserToken(userToken);
        final JDashboardDataRequest _getDashboardData_dataRequest = new JDashboardDataRequest();
        final JRedundancyPodRequest redundancyPod = new JRedundancyPodRequest();
        redundancyPod.setPodId("id-redundant");
        final JCMAConfigPodRequest cmaConfigPod = new JCMAConfigPodRequest();
        cmaConfigPod.setPodId("id-cmaConfig");
        final JCMAInfoPodRequest cmaInfoPod = new JCMAInfoPodRequest();
        cmaInfoPod.setPodId("id-cmaInfo");
        final JLicensesPodRequest licensePod = new JLicensesPodRequest();
        licensePod.setPodId("id-licenses");
        final JDmaPodRequest dmaPod = new JDmaPodRequest();
        dmaPod.setPodId("id-dma");
        final JPbxPodRequest pbxPod = new JPbxPodRequest();
        pbxPod.setPodId("id-cucm");
        _getDashboardData_dataRequest.setRedundancy(redundancyPod);
        _getDashboardData_dataRequest.setCmaConfig(cmaConfigPod);
        _getDashboardData_dataRequest.setCmaInfo(cmaInfoPod);
        _getDashboardData_dataRequest.setLicenses(licensePod);
        _getDashboardData_dataRequest.setDmaStatus(dmaPod);
        _getDashboardData_dataRequest.setPbxStatus(pbxPod);
        final Holder<JDashboardDataResponse> _getDashboardData_dataResponse = new Holder<JDashboardDataResponse>();
        final JWebResult _getDashboardData__return = port
                .getDashboardData(_getDashboardData_credentials,
                                  _getDashboardData_dataRequest,
                                  _getDashboardData_dataResponse);
        System.out.println("getDashboardData.result="
                + _getDashboardData__return);
        System.out.println("getDashboardData._getDashboardData_dataResponse="
                + _getDashboardData_dataResponse.value);
        logger.info("getDashboardData._getDashboardData_dataResponse="
                + _getDashboardData_dataResponse.value);
        return _getDashboardData_dataResponse.value;
    }

    public JDashboardUserPreferences
            getDashboardPreferences(final String userToken) {
        System.out.println("Invoking getDashboardPreferences...");
        logger.info("Invoking getDashboardPreferences...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JDashboardUserPreferences> preferences = new Holder<JDashboardUserPreferences>();
        port.getDashboardPreferences(credentials, preferences);
        return preferences.value;
    }

    public JUMCDashboardDataResponse
            getUMCDashboardData(final String userToken,
                                final JUMCDashboardDataRequest dataRequest) {
        System.out.println("Invoking getUMCDashboardData...");
        logger.info("Invoking getUMCDashboardData...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JUMCDashboardDataResponse> dataResponse = new Holder<JUMCDashboardDataResponse>();
        port.getUMCDashboardData(credentials, dataRequest, dataResponse);
        return dataResponse.value;
    }
}
