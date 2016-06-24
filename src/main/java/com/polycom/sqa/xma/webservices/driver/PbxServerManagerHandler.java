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
import com.polycom.webservices.PbxServerManager.JCredentials;
import com.polycom.webservices.PbxServerManager.JPbxServer;
import com.polycom.webservices.PbxServerManager.JPbxServerManager;
import com.polycom.webservices.PbxServerManager.JPbxServerManager_Service;
import com.polycom.webservices.PbxServerManager.JWebResult;

/**
 * Pbx Server handler
 *
 * @author Lingxi Zhao
 *
 */
public class PbxServerManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("PbxServerManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JPbxServerManager");
    private JPbxServerManager  port;

    /**
     * Construction of the PbxServerManagerHandler class
     */
    public PbxServerManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JPbxServerManager) jsonInvocationHandler
                    .getProxy(JPbxServerManager.class);
        } else {
            final URL wsdlURL = PbxServerManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JPbxServerManager.wsdl");
            final JPbxServerManager_Service ss = new JPbxServerManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJPbxServerManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JPbxServerManager");
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
     * add CUCM
     *
     * @param userToken
     * @param deviceName
     * @param description
     * @param ipAddress
     * @param username
     * @param password
     * @param sameXmaAdServer
     * @param ldapAttributeForUserId
     * @return
     */
    public JWebResult addCUCM(final String userToken,
                              final JPbxServer _addCUCM_server) {
        System.out.println("Invoking addCUCM...");
        final com.polycom.webservices.PbxServerManager.JCredentials _addCUCM_credentials = new JCredentials();
        _addCUCM_credentials.setUserToken(userToken);
        final JWebResult _addCUCM__return = port
                .addCucmServer(_addCUCM_credentials, _addCUCM_server);
        System.out.println("addPbxServer.result=" + _addCUCM__return);
        return _addCUCM__return;
    }

    /**
     * delete CUCM
     *
     * @param userToken
     * @param uuid
     * @return
     */
    public JWebResult deleteCUCM(final String userToken, final String uuid) {
        System.out.println("Invoking deleteCUCM...");
        final com.polycom.webservices.PbxServerManager.JCredentials _deleteCUCM_credentials = new JCredentials();
        _deleteCUCM_credentials.setUserToken(userToken);
        final String _deleteCUCM_uuid = uuid;
        final JWebResult _deleteCUCM__return = port
                .deleteCucmServer(_deleteCUCM_credentials, _deleteCUCM_uuid);
        System.out.println("deleteCUCM.result=" + _deleteCUCM__return);
        return _deleteCUCM__return;
    }

    /**
     * get cucm list
     *
     * @param userToken
     * @return cucmsRetrieved
     */
    public List<JPbxServer> getCUCMs(final String userToken) {
        System.out.println("Invoking getCUCMs...");
        logger.info("Invoking getCUCMs...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JPbxServer>> cucmsRetrieved = new Holder<List<JPbxServer>>();
        port.getPbxServers(credentials, cucmsRetrieved);
        return cucmsRetrieved.value;
    }

    /**
     * synchronize CUCM
     *
     * @param userToken
     * @param pbxServer
     * @return
     */
    public Boolean synchronizeCucmServer(final String userToken,
                                         final JPbxServer pbxServer) {
        System.out.println("Invoking synchronizeCucmServer...");
        logger.info("Invoking synchronizeCucmServer...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<Boolean> whetherStarted = new Holder<Boolean>();
        port.startFullSyncTaskNow(credentials, pbxServer, whetherStarted);
        return whetherStarted.value;
    }

    /**
     * updateCucmServer CUCM
     *
     * @param userToken
     * @param pbxServer
     * @return
     */
    public JWebResult updateCucmServer(final String userToken,
                                       final JPbxServer pbxServer) {
        System.out.println("Invoking updateCucmServer...");
        logger.info("Invoking updateCucmServer...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.updateCucmServer(credentials, pbxServer);
    }
}