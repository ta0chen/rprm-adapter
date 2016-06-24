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
import com.polycom.webservices.ProfileManager.JCredentials;
import com.polycom.webservices.ProfileManager.JPolicyType;
import com.polycom.webservices.ProfileManager.JProfile;
import com.polycom.webservices.ProfileManager.JProfileManager;
import com.polycom.webservices.ProfileManager.JProfileManager_Service;
import com.polycom.webservices.ProfileManager.JWebResult;

/**
 * Profile Manager Handler
 *
 * @author Tao Chen
 *
 */
public class ProfileManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("ProfileManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JProfileManager");
    private JProfileManager    port;

    /**
     * Construction of DeviceManagerHandler class
     */
    public ProfileManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JProfileManager) jsonInvocationHandler
                    .getProxy(JProfileManager.class);
        } else {
            final URL wsdlURL = ProfileManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JProfileManager.wsdl");
            final JProfileManager_Service ss = new JProfileManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJProfileManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JProfileManager");
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
     * Add provision profile
     *
     * @param userToken
     * @param profile
     * @return
     */
    public JWebResult add(final String userToken, final JProfile profile) {
        System.out.println("Invoking add...");
        final JCredentials _add_credentials = new JCredentials();
        _add_credentials.setUserToken(userToken);
        final JProfile _add_profile = profile;
        final JWebResult _add__return = port.add(_add_credentials,
                                                 _add_profile);
        System.out.println("add.result=" + _add__return);
        return _add__return;
    }

    /**
     * Delete the provision profile
     *
     * @param userToken
     * @param profileIds
     * @return
     */
    public JWebResult deleteByIds(final String userToken,
                                  final List<Integer> profileIds) {
        System.out.println("Invoking deleteByIds...");
        final JCredentials _deleteByIds_credentials = new JCredentials();
        _deleteByIds_credentials.setUserToken(userToken);
        final List<Integer> _deleteByIds_profileIds = profileIds;
        final JWebResult _deleteByIds__return = port
                .deleteByIds(_deleteByIds_credentials, _deleteByIds_profileIds);
        System.out.println("deleteByIds.result=" + _deleteByIds__return);
        return _deleteByIds__return;
    }

    /**
     * Get all provision profiles on XMA
     *
     * @param userToken
     * @return
     */
    public List<JProfile> getAllEndpointProfile(final String userToken) {
        logger.info("Invoking getAllEndpointProfile...");
        System.out.println("Invoking getAllEndpointProfile...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JProfile>> profiles = new Holder<List<JProfile>>();
        port.getAllEndpointProfile(credentials, profiles);
        return profiles.value;
    }

    /**
     * Get all server provision profiles on XMA
     *
     * @param userToken
     * @return
     */
    public List<JProfile> getAllServerProfile(final String userToken) {
        logger.info("Invoking getAllServerProfile...");
        System.out.println("Invoking getAllServerProfile...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JProfile>> profiles = new Holder<List<JProfile>>();
        port.getAllServerProfile(credentials, profiles);
        return profiles.value;
    }

    /**
     * Get provision profile by its ID
     *
     * @param userToken
     * @param profileId
     * @return
     */
    public JProfile getByProfileId(final String userToken,
                                   final int profileId) {
        logger.info("Invoking getByProfileId...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JProfile> profile = new Holder<JProfile>();
        final JWebResult _getByProfileId__return = port
                .getByProfileId(credentials, profileId, profile);
        System.out.println("getByProfileId.result=" + _getByProfileId__return);
        System.out.println("getByProfileId._getByProfileId_profile="
                + profile.value);
        return profile.value;
    }

    /**
     * Verify the candidate provision profile is legal or not
     *
     * @param userToken
     * @param profileType
     * @param profileName
     * @return
     */
    public Boolean isLegalProfileName(final String userToken,
                                      final JPolicyType profileType,
                                      final String profileName) {
        System.out.println("Invoking isLegalProfileName...");
        final JCredentials _isLegalProfileName_credentials = new JCredentials();
        _isLegalProfileName_credentials.setUserToken(userToken);
        final JPolicyType _isLegalProfileName_profileType = profileType;
        final String _isLegalProfileName_profileName = profileName;
        final Holder<Boolean> _isLegalProfileName_isLegal = new Holder<Boolean>();
        final JWebResult _isLegalProfileName__return = port
                .isLegalProfileName(_isLegalProfileName_credentials,
                                    _isLegalProfileName_profileType,
                                    _isLegalProfileName_profileName,
                                    _isLegalProfileName_isLegal);
        System.out.println("isLegalProfileName.result="
                + _isLegalProfileName__return);
        System.out.println("isLegalProfileName._isLegalProfileName_isLegal="
                + _isLegalProfileName_isLegal.value);
        return _isLegalProfileName_isLegal.value;
    }

    /**
     * reset By Id
     *
     * @param userToken
     * @param profile
     * @return
     */
    public JWebResult resetById(final String userToken, final int profileId) {
        logger.info("Invoking resetById...");
        System.out.println("Invoking resetById...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.resetById(credentials, profileId);
    }

    /**
     * Update provision profile
     *
     * @param userToken
     * @param profile
     * @return
     */
    public JWebResult update(final String userToken, final JProfile profile) {
        logger.info("Invoking update...");
        System.out.println("Invoking update...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.update(credentials, profile);
    }
}
