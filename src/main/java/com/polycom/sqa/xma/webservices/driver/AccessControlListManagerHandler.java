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
import com.polycom.webservices.AccessControlListManager.JAccessControlList;
import com.polycom.webservices.AccessControlListManager.JAccessControlListManager;
import com.polycom.webservices.AccessControlListManager.JAccessControlListManager_Service;
import com.polycom.webservices.AccessControlListManager.JCredentials;
import com.polycom.webservices.AccessControlListManager.JWebResult;

/**
 * Access Control List Manager Handler
 *
 * @author Tao Chen
 *
 */
public class AccessControlListManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("AccessControlListManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JAccessControlListManager");
    JAccessControlListManager  port;

    /**
     * Construction of AccessControlListManagerHandler class
     */
    public AccessControlListManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JAccessControlListManager) jsonInvocationHandler
                    .getProxy(JAccessControlListManager.class);
        } else {
            final URL wsdlURL = AddressBookManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JAccessControlListManager.wsdl");
            final JAccessControlListManager_Service ss = new JAccessControlListManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJAccessControlListManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JAccessControlListManager");
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
     * Add new ACL
     *
     * @param userToken
     * @param accessControlList
     * @return
     */
    public JWebResult addAcl(final String userToken,
                             final JAccessControlList accessControlList) {
        logger.info("Invoking addAcl...");
        final JCredentials _addAcl_credentials = new JCredentials();
        _addAcl_credentials.setUserToken(userToken);
        final JAccessControlList _addAcl_acl = accessControlList;
        final JWebResult _addAcl__return = port.addAcl(_addAcl_credentials,
                                                       _addAcl_acl);
        return _addAcl__return;
    }

    /**
     * Delete specified ACL
     *
     * @param userToken
     * @param id
     * @return
     */
    public JWebResult deleteAcl(final String userToken, final String id) {
        logger.info("Invoking deleteAcl...");
        final JCredentials _deleteAcl_credentials = new JCredentials();
        _deleteAcl_credentials.setUserToken(userToken);
        final String _deleteAcl_aclId = id;
        final JWebResult _deleteAcl__return = port
                .deleteAcl(_deleteAcl_credentials, _deleteAcl_aclId);
        return _deleteAcl__return;
    }

    /**
     * Get all ACL
     *
     * @param userToken
     * @return
     */
    public List<JAccessControlList> getAll(final String userToken) {
        logger.info("Invoking getAll...");
        final JCredentials _getAll_credentials = new JCredentials();
        _getAll_credentials.setUserToken(userToken);
        final Holder<List<JAccessControlList>> _getAll_acls = new Holder<List<JAccessControlList>>();
        port.getAll(_getAll_credentials, _getAll_acls);
        return _getAll_acls.value;
    }

    /**
     * Update the ACL
     *
     * @param userToken
     * @param acl
     * @return
     */
    public JWebResult updateAcl(final String userToken,
                                final JAccessControlList acl) {
        logger.info("Invoking updateAcl...");
        final JCredentials _updateAcl_credentials = new JCredentials();
        _updateAcl_credentials.setUserToken(userToken);
        final JAccessControlList _updateAcl_acl = acl;
        final JWebResult _updateAcl__return = port
                .updateAcl(_updateAcl_credentials, _updateAcl_acl);
        return _updateAcl__return;
    }
}
