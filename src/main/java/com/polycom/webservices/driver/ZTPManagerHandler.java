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
import com.polycom.webservices.ZTPManager.JCredentials;
import com.polycom.webservices.ZTPManager.JUserSearchType;
import com.polycom.webservices.ZTPManager.JWebResult;
import com.polycom.webservices.ZTPManager.JZTPManager;
import com.polycom.webservices.ZTPManager.JZTPManager_Service;
import com.polycom.webservices.ZTPManager.JZTPUIInfo;

/**
 * ZTP Manager Handler
 *
 * @author wbchao
 *
 */
public class ZTPManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("ZtpManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JZTPManager");
    private JZTPManager        port;

    /**
     * Construction of ZtpManagerHandler class
     */
    public ZTPManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JZTPManager) jsonInvocationHandler
                    .getProxy(JZTPManager.class);
        } else {
            final URL wsdlURL = ZTPManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JZTPManager.wsdl");
            final JZTPManager_Service ss = new JZTPManager_Service(wsdlURL,
                    SERVICE_NAME);
            port = ss.getJZTPManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext().put(
                                             BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                             webServiceUrl + "/JZTPManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public List<JZTPUIInfo> getZTPData(final String userToken) {
        System.out.println("Invoking getZTPData...");
        logger.info("Invoking getZTPData...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JZTPUIInfo>> ztpinfo = new Holder<List<JZTPUIInfo>>();
        port.getZTPData(credentials,
                        null,
                        null,
                        JUserSearchType.ALL___USERS,
                        ztpinfo);
        return ztpinfo.value;
    }

    public JWebResult setZTPConfig(final String userToken,
                                   final boolean autoGenerate,
                                   final String defaultLanguage) {
        System.out.println("Invoking setZTPConfig...");
        logger.info("Invoking setZTPConfig...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.setZTPConfig(credentials, autoGenerate, defaultLanguage);
    }
}
