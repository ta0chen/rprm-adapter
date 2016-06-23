package com.polycom.webservices.driver;

import java.net.URL;
import java.util.ArrayList;
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
import com.polycom.webservices.AudioProfileManager.JAudioProfile;
import com.polycom.webservices.AudioProfileManager.JAudioProfileManager;
import com.polycom.webservices.AudioProfileManager.JAudioProfileManager_Service;
import com.polycom.webservices.AudioProfileManager.JCredentials;
import com.polycom.webservices.AudioProfileManager.JDeviceModelVO;
import com.polycom.webservices.AudioProfileManager.JWebResult;

/**
 * Audio Profile Manager Handler
 *
 * @author Lingxi Zhao
 *
 */
public class AudioProfileManagerHandler {
    protected static Logger      logger       = Logger
            .getLogger("AudioProfileManagerHandler");
    private static final QName   SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JAudioProfileManager");
    private JAudioProfileManager port;

    /**
     * Construction of DeviceManagerHandler class
     */
    public AudioProfileManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JAudioProfileManager) jsonInvocationHandler
                    .getProxy(JAudioProfileManager.class);
        } else {
            final URL wsdlURL = AudioProfileManagerHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JAudioProfileManager.wsdl");
            final JAudioProfileManager_Service ss = new JAudioProfileManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJAudioProfileManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JAudioProfileManager");
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
     * Add audio provision profile
     *
     * @param userToken
     * @param profile
     * @return
     */
    public JWebResult add(final String userToken, final JAudioProfile profile) {
        System.out.println("Invoking add...");
        final JCredentials _add_credentials = new JCredentials();
        _add_credentials.setUserToken(userToken);
        final JAudioProfile _add_profile = profile;
        final JWebResult _add__return = port.addAudioProfile(_add_credentials,
                                                             _add_profile);
        System.out.println("add.result=" + _add__return);
        return _add__return;
    }

    /**
     * Delete the audio provision profile
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
                .deleteAudioProfileByIds(_deleteByIds_credentials,
                                         _deleteByIds_profileIds);
        System.out.println("deleteByIds.result=" + _deleteByIds__return);
        return _deleteByIds__return;
    }

    /**
     * Get all the audio profiles
     *
     * @param userToken
     * @return
     */
    public List<JAudioProfile> getAllAudioProfiles(final String userToken) {
        logger.info("Invoking getAllAudioProfiles...");
        final JCredentials _getAllAudioProfiles_credentials = new JCredentials();
        final Holder<List<JAudioProfile>> _getAllAudioProfiles_allAudioProfiles = new Holder<List<JAudioProfile>>();
        final JWebResult _getAllAudioProfiles__return = port
                .getAllAudioProfiles(_getAllAudioProfiles_credentials,
                                     _getAllAudioProfiles_allAudioProfiles);
        System.out.println("getAllAudioProfiles.result="
                + _getAllAudioProfiles__return);
        System.out
                .println("getAllAudioProfiles._getAllAudioProfiles_allAudioProfiles="
                        + _getAllAudioProfiles_allAudioProfiles.value);
        return _getAllAudioProfiles_allAudioProfiles.value;
    }

    /**
     * Get all available audio model
     *
     * @param userToken
     * @param groupName
     * @return
     */
    public List<JDeviceModelVO>
            getAllAvailableAudioModel(final String userToken,
                                      final String groupName) {
        logger.info("Invoking getAllAvailableAudioModel...");
        final JCredentials _getAllAvailableAudioModel_credentials = new JCredentials();
        _getAllAvailableAudioModel_credentials.setUserToken(userToken);
        final String _getAllAvailableAudioModel_groupName = groupName;
        final Holder<List<JDeviceModelVO>> _getAllAvailableAudioModel_deviceModels = new Holder<List<JDeviceModelVO>>();
        final JWebResult _getAllAvailableAudioModel__return = port
                .getAllAvailableAudioModel(_getAllAvailableAudioModel_credentials,
                                           _getAllAvailableAudioModel_groupName,
                                           _getAllAvailableAudioModel_deviceModels);
        System.out.println("getAllAvailableAudioModel.result="
                + _getAllAvailableAudioModel__return);
        System.out
                .println("getAllAvailableAudioModel._getAllAvailableAudioModel_deviceModels="
                        + _getAllAvailableAudioModel_deviceModels.value);
        return _getAllAvailableAudioModel_deviceModels.value;
    }

    /**
     * Get all available audio model for the profile
     *
     * @param userToken
     * @return
     */
    public List<JDeviceModelVO>
            getAllAvailableAudioModelForProfile(final String userToken) {
        logger.info("Invoking getAllAvailableAudioModelForProfile...");
        final JCredentials _getAllAvailableAudioModelForProfile_credentials = new JCredentials();
        _getAllAvailableAudioModelForProfile_credentials
                .setUserToken(userToken);
        final String _getAllAvailableAudioModelForProfile_groupName = "PhoneBasicProvision";
        final Integer _getAllAvailableAudioModelForProfile_profileId = -1;
        final Holder<List<JDeviceModelVO>> _getAllAvailableAudioModelForProfile_deviceModels = new Holder<List<JDeviceModelVO>>();
        final JWebResult _getAllAvailableAudioModelForProfile__return = port
                .getAllAvailableAudioModelForProfile(_getAllAvailableAudioModelForProfile_credentials,
                                                     _getAllAvailableAudioModelForProfile_groupName,
                                                     _getAllAvailableAudioModelForProfile_profileId,
                                                     _getAllAvailableAudioModelForProfile_deviceModels);
        System.out.println("getAllAvailableAudioModelForProfile.result="
                + _getAllAvailableAudioModelForProfile__return);
        System.out
                .println("getAllAvailableAudioModelForProfile._getAllAvailableAudioModelForProfile_deviceModels="
                        + _getAllAvailableAudioModelForProfile_deviceModels.value);
        return _getAllAvailableAudioModelForProfile_deviceModels.value;
    }

    /**
     * Get all the default audio profiles
     *
     * @param userToken
     * @return
     */
    public List<JAudioProfile>
            getAllDefaultAudioProfiles(final String userToken) {
        logger.info("Invoking getAllDefaultAudioProfiles...");
        final JCredentials _getAllDefaultAudioProfiles_credentials = new JCredentials();
        _getAllDefaultAudioProfiles_credentials.setUserToken(userToken);
        final Holder<List<JAudioProfile>> _getAllDefaultAudioProfiles_defaultAudioProfiles = new Holder<List<JAudioProfile>>();
        final JWebResult _getAllDefaultAudioProfiles__return = port
                .getAllDefaultAudioProfiles(_getAllDefaultAudioProfiles_credentials,
                                            _getAllDefaultAudioProfiles_defaultAudioProfiles);
        System.out.println("getAllDefaultAudioProfiles.result="
                + _getAllDefaultAudioProfiles__return);
        System.out
                .println("getAllDefaultAudioProfiles._getAllDefaultAudioProfiles_defaultAudioProfiles="
                        + _getAllDefaultAudioProfiles_defaultAudioProfiles.value);
        return _getAllDefaultAudioProfiles_defaultAudioProfiles.value;
    }

    /**
     * Get all audio provision profiles on XMA
     *
     * @param userToken
     * @return
     */
    public List<JAudioProfile> getAllEndpointProfile(final String userToken) {
        logger.info("Invoking getAllEndpointProfile...");
        System.out.println("Invoking getAllEndpointProfile...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JAudioProfile>> profiles = new Holder<List<JAudioProfile>>();
        port.getAllAudioProfiles(credentials, profiles);
        return profiles.value;
    }

    /**
     * Get audio provision profile by its ID
     *
     * @param userToken
     * @param profileId
     * @return
     */
    public JAudioProfile getByProfileId(final String userToken,
                                        final int profileId) {
        logger.info("Invoking getByProfileId...");
        final JCredentials _getByProfileId_credentials = new JCredentials();
        _getByProfileId_credentials.setUserToken(userToken);
        final List<Integer> _getByProfileId_profileId = new ArrayList<Integer>();
        _getByProfileId_profileId.add(profileId);
        final Holder<List<JAudioProfile>> _getByProfileId_profile = new Holder<List<JAudioProfile>>();
        final JWebResult _getByProfileId__return = port
                .getAudioProfileByIds(_getByProfileId_credentials,
                                      _getByProfileId_profileId,
                                      _getByProfileId_profile);
        System.out.println("getByProfileId.result=" + _getByProfileId__return);
        System.out.println("getByProfileId._getByProfileId_profile="
                + _getByProfileId_profile.value.get(0));
        return _getByProfileId_profile.value.get(0);
    }

    /**
     * Reset the audio phone provision profile
     *
     * @param userToken
     * @param audioProfileIds
     * @return
     */
    public JWebResult
            resetAudioProfileByIds(final String userToken,
                                   final List<Integer> audioProfileIds) {
        logger.info("Invoking resetAudioProfileByIds...");
        final JCredentials _resetAudioProfileByIds_credentials = new JCredentials();
        _resetAudioProfileByIds_credentials.setUserToken(userToken);
        final List<Integer> _resetAudioProfileByIds_audioProfileIds = audioProfileIds;
        final JWebResult _resetAudioProfileByIds__return = port
                .resetAudioProfileByIds(_resetAudioProfileByIds_credentials,
                                        _resetAudioProfileByIds_audioProfileIds);
        System.out.println("resetAudioProfileByIds.result="
                + _resetAudioProfileByIds__return);
        return _resetAudioProfileByIds__return;
    }

    /**
     * Update audio provision profile
     *
     * @param userToken
     * @param profile
     * @return
     */
    public JWebResult update(final String userToken,
                             final JAudioProfile profile) {
        logger.info("Invoking update...");
        System.out.println("Invoking update...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.updateAudioProfile(credentials, profile);
    }
}
