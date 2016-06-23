package com.polycom.webservices.driver;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.polycom.webservices.SystemAlertManager.JAlertLevel;
import com.polycom.webservices.SystemAlertManager.JCredentials;
import com.polycom.webservices.SystemAlertManager.JSystemAlert;
import com.polycom.webservices.SystemAlertManager.JSystemAlertManager;
import com.polycom.webservices.SystemAlertManager.JSystemAlertManager_Service;
import com.polycom.webservices.SystemAlertManager.JSystemAlertThresholdConfig;
import com.polycom.webservices.SystemAlertManager.JWebResult;

/**
 * System Alert Manager Handler
 *
 * @author wbchao
 *
 */
public class SystemAlertManagerHandler {
    protected static Logger     logger       = Logger
            .getLogger("SystemAlertManagerHandler");
    private static final QName  SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JSystemAlertManager");
    private JSystemAlertManager port;

    /**
     * Construction of SiteTopoManagerHandler class
     */
    public SystemAlertManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JSystemAlertManager) jsonInvocationHandler
                    .getProxy(JSystemAlertManager.class);
        } else {
            final URL wsdlURL = SystemAlertManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JSystemAlertManager.wsdl");
            final JSystemAlertManager_Service ss = new JSystemAlertManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJSystemAlertManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JSystemAlertManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public JWebResult clear(final String userToken, final int id) {
        System.out.println("Invoking clear...");
        logger.info("Invoking clear...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JSystemAlert>> alerts = new Holder<List<JSystemAlert>>();
        final Holder<List<JAlertLevel>> levels = new Holder<List<JAlertLevel>>();
        return port.clear(credentials, id, alerts, levels);
    }

    public JSystemAlertThresholdConfig
            getAlertThresholdConfiguration(final String userToken) {
        System.out.println("Invoking getAlertThresholdConfiguration...");
        logger.info("Invoking getAlertThresholdConfiguration...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JSystemAlertThresholdConfig> thresholdConfig = new Holder<JSystemAlertThresholdConfig>();
        port.getAlertThresholdConfiguration(credentials, thresholdConfig);
        return thresholdConfig.value;
    }

    public Map<JSystemAlert, JAlertLevel> getList(final String userToken) {
        System.out.println("Invoking getList...");
        logger.info("Invoking getList...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JSystemAlert>> alerts = new Holder<List<JSystemAlert>>();
        final Holder<List<JAlertLevel>> levels = new Holder<List<JAlertLevel>>();
        port.getList(credentials, alerts, levels);
        final Map<JSystemAlert, JAlertLevel> systemAlert = new HashMap<JSystemAlert, JAlertLevel>();
        for (int i = 0; i < alerts.value.size(); i++) {
            systemAlert.put(alerts.value.get(i), levels.value.get(i));
        }
        return systemAlert;
    }

    public JWebResult
            setAlertThresholdConfiguration(final String userToken,
                                           final JSystemAlertThresholdConfig thresholdConfig) {
        System.out.println("Invoking setAlertThresholdConfiguration...");
        logger.info("Invoking setAlertThresholdConfiguration...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.setAlertThresholdConfiguration(credentials,
                                                   thresholdConfig);
    }
}
