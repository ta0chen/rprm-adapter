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
import com.polycom.webservices.SoftupdateManager.JCredentials;
import com.polycom.webservices.SoftupdateManager.JDeviceType;
import com.polycom.webservices.SoftupdateManager.JModel;
import com.polycom.webservices.SoftupdateManager.JSoftupdateManager;
import com.polycom.webservices.SoftupdateManager.JSoftupdateManager_Service;
import com.polycom.webservices.SoftupdateManager.JSoftupdateProfile;
import com.polycom.webservices.SoftupdateManager.JSoftupdateVersion;
import com.polycom.webservices.SoftupdateManager.JSoftupdateVersionPolicy;
import com.polycom.webservices.SoftupdateManager.JUIUtcDateTime;
import com.polycom.webservices.SoftupdateManager.JWebResult;

/**
 * Soft update handler
 *
 * @author Lingxi Zhao
 *
 */
public class SoftupdateManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("SoftupdateManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JSoftupdateManager");
    private JSoftupdateManager port;

    /**
     * Construction of the SoftupdateManagerHandler class
     */
    public SoftupdateManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JSoftupdateManager) jsonInvocationHandler
                    .getProxy(JSoftupdateManager.class);
        } else {
            final URL wsdlURL = SoftupdateManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JSoftupdateManager.wsdl");
            final JSoftupdateManager_Service ss = new JSoftupdateManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJSoftupdateManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JSoftupdateManager");
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
     * add softupdate
     *
     * @param userToken
     * @param deviceType
     * @param deviceModel
     * @param description
     * @param keyFilename
     * @param softupdateFilename
     * @param testGroupId
     * @return
     */
    public JWebResult addSoftupdate(final String userToken,
                                    final JDeviceType deviceType,
                                    final JModel deviceModel,
                                    final String description,
                                    final String keyFilename,
                                    final String softupdateFilename,
                                    final int testGroupId) {
        System.out.println("Invoking addSoftupdate...");
        final JCredentials _addSoftupdate_credentials = new JCredentials();
        _addSoftupdate_credentials.setUserToken(userToken);
        final JDeviceType _addSoftupdate_deviceType = deviceType;
        final JModel _addSoftupdate_deviceModel = deviceModel;
        final String _addSoftupdate_description = description;
        final String _addSoftupdate_keyFilename = keyFilename;
        final String _addSoftupdate_softupdateFilename = softupdateFilename;
        final int _addSoftupdate_testGroupId = testGroupId;
        final Holder<JSoftupdateVersion> _addSoftupdate_softVersion = new Holder<JSoftupdateVersion>();
        final Holder<Boolean> _addSoftupdate_policyAssumed = new Holder<Boolean>();
        final JWebResult _addSoftupdate__return = port
                .addSoftupdate(_addSoftupdate_credentials,
                               _addSoftupdate_deviceType,
                               _addSoftupdate_deviceModel,
                               _addSoftupdate_description,
                               _addSoftupdate_keyFilename,
                               _addSoftupdate_softupdateFilename,
                               _addSoftupdate_testGroupId,
                               _addSoftupdate_softVersion,
                               _addSoftupdate_policyAssumed);
        System.out.println("addSoftupdate.result=" + _addSoftupdate__return);
        return _addSoftupdate__return;
    }

    /**
     * clear the profile for legacy ep
     *
     * @param userToken
     * @param models
     * @return
     */
    public JWebResult clearLegacySUProfile(final String userToken,
                                           final List<JModel> models) {
        logger.info("Invoking clearLegacySUProfile...");
        System.out.println("Invoking clearLegacySUProfile...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port.clearLegacySUProfile(credentials,
                                                            models);
        return result;
    }

    public JWebResult
            deleteSoftupdate(final String userToken,
                             final List<Integer> softUpdateVersionIds) {
        final Holder<Boolean> _deleteSoftupdate_policyCleared = new Holder<Boolean>();
        System.out.println("Invoking deleteSoftupdate...");
        final JCredentials _deleteSoftupdate_credentials = new JCredentials();
        _deleteSoftupdate_credentials.setUserToken(userToken);
        final List<Integer> _deleteSoftupdate_softUpdateVersionIds = softUpdateVersionIds;
        final JWebResult _deleteSoftupdate__return = port
                .deleteSoftupdate(_deleteSoftupdate_credentials,
                                  _deleteSoftupdate_softUpdateVersionIds,
                                  _deleteSoftupdate_policyCleared);
        System.out.println("deleteSoftupdate.result="
                + _deleteSoftupdate__return);
        return _deleteSoftupdate__return;
    }

    /**
     * Get all legacy profiles
     *
     * @param userToken
     * @return
     */
    @SuppressWarnings("unused")
    public List<JSoftupdateProfile>
            getLegacySUProfiles(final String userToken) {
        logger.info("Invoking getLegacySUProfiles...");
        System.out.println("Invoking getLegacySUProfiles...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JSoftupdateProfile>> profiles = new Holder<List<JSoftupdateProfile>>();
        final JWebResult result = port.getLegacySUProfiles(credentials,
                                                           profiles);
        return profiles.value;
    }

    public List<JSoftupdateVersion> getSoftupdates(final String userToken,
                                                   final JDeviceType deviceType,
                                                   final JModel deviceModel,
                                                   final Integer areaUgpId) {
        final Holder<List<JSoftupdateVersion>> _getSoftupdates_softupdatesVersion = new Holder<List<JSoftupdateVersion>>();
        final Holder<JSoftupdateVersionPolicy> _getSoftupdates_policy = new Holder<JSoftupdateVersionPolicy>();
        final Holder<Integer> _getSoftupdates_shippedVersionID = new Holder<Integer>();
        System.out.println("Invoking getSoftupdates...");
        final JCredentials _getSoftupdates_credentials = new JCredentials();
        _getSoftupdates_credentials.setUserToken(userToken);
        final JDeviceType _getSoftupdates_deviceType = deviceType;
        final JModel _getSoftupdates_deviceModel = deviceModel;
        final Integer _getSoftupdates_areaUgpId = areaUgpId;
        final JWebResult _getSoftupdates__return = port
                .getSoftupdates(_getSoftupdates_credentials,
                                _getSoftupdates_deviceType,
                                _getSoftupdates_deviceModel,
                                _getSoftupdates_areaUgpId,
                                _getSoftupdates_softupdatesVersion,
                                _getSoftupdates_policy,
                                _getSoftupdates_shippedVersionID);
        System.out.println("getSoftupdates.result=" + _getSoftupdates__return);
        return _getSoftupdates_softupdatesVersion.value;
    }

    /**
     * Schedule update
     *
     * @param userToken
     * @param deviceIds
     * @param passiveMode
     * @param rmAddrBookEntries
     * @param rmSystemFiles
     * @param enableEpDHCPServer
     * @param isDeviceTime
     * @param isNow
     * @param utcDate
     * @return
     */
    public JWebResult schedule(final String userToken,
                               final List<Integer> deviceIds,
                               final boolean passiveMode,
                               final boolean rmAddrBookEntries,
                               final boolean rmSystemFiles,
                               final boolean enableEpDHCPServer,
                               final boolean isDeviceTime,
                               final boolean isNow,
                               final JUIUtcDateTime utcDate) {
        logger.info("Invoking schedule...");
        System.out.println("Invoking schedule...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port.schedule(credentials,
                                                deviceIds,
                                                passiveMode,
                                                rmAddrBookEntries,
                                                rmSystemFiles,
                                                enableEpDHCPServer,
                                                isDeviceTime,
                                                isNow,
                                                utcDate);
        return result;
    }

    /**
     * update legacy ep profile
     *
     * @param userToken
     * @param profile
     * @return
     */
    public JWebResult updateLegacyProfile(final String userToken,
                                          final JSoftupdateProfile profile) {
        logger.info("Invoking updateLegacyProfile...");
        System.out.println("Invoking updateLegacyProfile...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port.updateLegacyProfile(credentials,
                                                           profile);
        return result;
    }

    public JWebResult updatePolicy(final String userToken,
                                   final JDeviceType deviceType,
                                   final JModel deviceModel,
                                   final Integer areaUgpId,
                                   final String versionToUse,
                                   final boolean allowNewerVersion) {
        System.out.println("Invoking updatePolicy...");
        final JCredentials _updatePolicy_credentials = new JCredentials();
        _updatePolicy_credentials.setUserToken(userToken);
        final JDeviceType _updatePolicy_deviceType = deviceType;
        final JModel _updatePolicy_deviceModel = deviceModel;
        final Integer _updatePolicy_areaUgpId = areaUgpId;
        final String _updatePolicy_versionToUse = versionToUse;
        final boolean _updatePolicy_allowNewerVersion = allowNewerVersion;
        final JWebResult _updatePolicy__return = port
                .updatePolicy(_updatePolicy_credentials,
                              _updatePolicy_deviceType,
                              _updatePolicy_deviceModel,
                              _updatePolicy_areaUgpId,
                              _updatePolicy_versionToUse,
                              _updatePolicy_allowNewerVersion);
        System.out.println("updatePolicy.result=" + _updatePolicy__return);
        return _updatePolicy__return;
    }
}