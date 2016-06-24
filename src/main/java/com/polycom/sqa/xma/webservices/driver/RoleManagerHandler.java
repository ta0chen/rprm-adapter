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
import com.polycom.webservices.RoleManager.JCredentials;
import com.polycom.webservices.RoleManager.JPredefinedRole;
import com.polycom.webservices.RoleManager.JRole;
import com.polycom.webservices.RoleManager.JRoleManager;
import com.polycom.webservices.RoleManager.JRoleManager_Service;
import com.polycom.webservices.RoleManager.JWebResult;

/**
 * Role Manager Handler
 *
 * @author Tao Chen
 *
 */
public class RoleManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("RoleManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JRoleManager");
    JRoleManager               port;

    /**
     * Construction of RoleManagerHandler class
     */
    public RoleManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JRoleManager) jsonInvocationHandler
                    .getProxy(JRoleManager.class);
        } else {
            final URL wsdlURL = RoleManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JRoleManager.wsdl");
            final JRoleManager_Service ss = new JRoleManager_Service(wsdlURL,
                    SERVICE_NAME);
            port = ss.getJRoleManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext().put(
                                             BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                             webServiceUrl + "/JRoleManager");
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
     * Get the list of the available roles
     *
     * @param userToken
     * @return
     */
    public List<JRole> getRoles(final String userToken) {
        System.out.println("Invoking getRoles...");
        logger.info("Invoking getRoles...");
        final JCredentials _getRoles_credentials = new JCredentials();
        _getRoles_credentials.setUserToken(userToken);
        final Holder<List<JRole>> _getRoles_roles = new Holder<List<JRole>>();
        final Holder<List<JPredefinedRole>> _getRoles_defaultRoles = new Holder<List<JPredefinedRole>>();
        final JWebResult _getRoles__return = port
                .getRoles(_getRoles_credentials,
                          _getRoles_roles,
                          _getRoles_defaultRoles);
        System.out.println("getRoles.result=" + _getRoles__return);
        System.out.println("getRoles._getRoles_roles=" + _getRoles_roles.value);
        System.out.println("getRoles._getRoles_defaultRoles="
                + _getRoles_defaultRoles.value);
        return _getRoles_roles.value;
    }
    
    /**
     * Add custom role
     *
     * @param userToken
     * @param role
     * @return
     */
    public JWebResult addRole(final String userToken, final JRole role) {
        System.out.println("Invoking addRole...");
        logger.info("Invoking addRole...");
        final JCredentials _addRole_credentials = new JCredentials();
        _addRole_credentials.setUserToken(userToken);
        final JWebResult _addRole__return = port
                .addRole(_addRole_credentials,role);
        System.out.println("addRole.result=" + _addRole__return);
        return _addRole__return;
    }
    
    /**
     * Delete role
     *
     * @param userToken
     * @param role
     * @return
     */
    public JWebResult deleteRole(final String userToken, final JRole role) {
        System.out.println("Invoking deleteRole...");
        logger.info("Invoking deleteRole...");
        final JCredentials _deleteRole_credentials = new JCredentials();
        _deleteRole_credentials.setUserToken(userToken);
        final JWebResult _deleteRole__return = port
                .deleteRole(_deleteRole_credentials,role);
        System.out.println("deleteRole.result=" + _deleteRole__return);
        return _deleteRole__return;
    }
}
