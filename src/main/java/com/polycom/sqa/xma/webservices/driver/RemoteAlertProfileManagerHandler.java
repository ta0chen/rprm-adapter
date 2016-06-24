package com.polycom.sqa.xma.webservices.driver;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.interceptor.JsonInvocationHandler;
import com.polycom.sqa.xma.webservices.driver.interceptor.SoapHeaderOutInterceptor;
import com.polycom.webservices.RemoteAlertProfileManager.JCredentials;
import com.polycom.webservices.RemoteAlertProfileManager.JDeviceForDetails;
import com.polycom.webservices.RemoteAlertProfileManager.JQueryCondition;
import com.polycom.webservices.RemoteAlertProfileManager.JRemoteAlertProfile;
import com.polycom.webservices.RemoteAlertProfileManager.JRemoteAlertProfileManager;
import com.polycom.webservices.RemoteAlertProfileManager.JRemoteAlertProfileManager_Service;
import com.polycom.webservices.RemoteAlertProfileManager.JViewType;
import com.polycom.webservices.RemoteAlertProfileManager.JWebResult;

/**
 * Remote alert profile manager handler
 *
 * @author Tao Chen
 *
 */
public class RemoteAlertProfileManagerHandler {
    protected static Logger    logger              = Logger
            .getLogger("RemoteAlertProfileManagerHandler");
    private static final QName SERVICE_NAME        = new QName(
            "http://polycom.com/WebServices", "JRemoteAlertProfileManager");
    final TLSClientParameters  tlsClientParameters = new TLSClientParameters();
    JRemoteAlertProfileManager port;

    /**
     * Construction of RemoteAlertProfileManagerHandler class
     */
    public RemoteAlertProfileManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                             false);
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,
                             false);
            final JaxbAnnotationModule module = new JaxbAnnotationModule();
            mapper.registerModule(module);
            mapper.setSerializationInclusion(Include.NON_NULL);
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl, mapper);
            port = (JRemoteAlertProfileManager) jsonInvocationHandler
                    .getProxy(JRemoteAlertProfileManager.class);
        } else {
            final URL wsdlURL = RemoteAlertProfileManagerHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JRemoteAlertProfileManager.wsdl");
            final JRemoteAlertProfileManager_Service ss = new JRemoteAlertProfileManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJRemoteAlertProfileManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JRemoteAlertProfileManager");
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
     * Add remote alert profile into RPRM
     *
     * @param userToken
     * @param profile
     * @return
     */
    public JWebResult addRemoteAlertProfile(final String userToken,
                                            final JRemoteAlertProfile profile) {
        logger.info("Invoking addRemoteAlertProfile...");
        final JCredentials _addRemoteAlertProfile_credentials = new JCredentials();
        _addRemoteAlertProfile_credentials.setUserToken(userToken);
        final JRemoteAlertProfile _addRemoteAlertProfile_profile = profile;
        final JWebResult _addRemoteAlertProfile__return = port
                .addRemoteAlertProfile(_addRemoteAlertProfile_credentials,
                                       _addRemoteAlertProfile_profile);
        logger.info("addRemoteAlertProfile.result="
                + _addRemoteAlertProfile__return.getStatus().toString());
        return _addRemoteAlertProfile__return;
    }

    /**
     * Delete the specified remote alert profile on the RPRM according to the
     * profile ID
     *
     * @param userToken
     * @param profileId
     * @return
     */
    public JWebResult deleteRemoteAlertProfile(final String userToken,
                                               final int profileId) {
        logger.info("Invoking deleteRemoteAlertProfile...");
        final JCredentials _deleteRemoteAlertProfile_credentials = new JCredentials();
        _deleteRemoteAlertProfile_credentials.setUserToken(userToken);
        final int _deleteRemoteAlertProfile_profileId = profileId;
        final JWebResult _deleteRemoteAlertProfile__return = port
                .deleteRemoteAlertProfile(_deleteRemoteAlertProfile_credentials,
                                          _deleteRemoteAlertProfile_profileId);
        logger.info("deleteRemoteAlertProfile.result="
                + _deleteRemoteAlertProfile__return.getStatus().toString());
        return _deleteRemoteAlertProfile__return;
    }

    /**
     * Get the list of the network devices need to alert
     *
     * @param userToken
     * @return
     */
    public List<JDeviceForDetails>
            getAlertableNetworkDevices(final String userToken) {
        logger.info("Invoking getAlertableNetworkDevices...");
        final JCredentials _getAlertableNetworkDevices_credentials = new JCredentials();
        _getAlertableNetworkDevices_credentials.setUserToken(userToken);
        final Holder<List<JDeviceForDetails>> _getAlertableNetworkDevices_devicesRetrieved = new Holder<List<JDeviceForDetails>>();
        final JWebResult _getAlertableNetworkDevices__return = port
                .getAlertableNetworkDevices(_getAlertableNetworkDevices_credentials,
                                            _getAlertableNetworkDevices_devicesRetrieved);
        logger.info("getAlertableNetworkDevices.result="
                + _getAlertableNetworkDevices__return.getStatus().toString());
        return _getAlertableNetworkDevices_devicesRetrieved.value;
    }

    /**
     * Get the list of the endpoints need to alert
     *
     * @param userToken
     * @param viewTypeVal
     * @return
     */
    public List<JDeviceForDetails>
            getEndpoints4PagingInAlertProfile(final String userToken,
                                              final JViewType viewTypeVal) {
        logger.info("Invoking getEndpoints4PagingInAlertProfile...");
        final JCredentials _getEndpoints4PagingInAlertProfile_credentials = new JCredentials();
        _getEndpoints4PagingInAlertProfile_credentials.setUserToken(userToken);
        final JViewType _getEndpoints4PagingInAlertProfile_viewTypeVal = viewTypeVal;
        final Holder<JViewType> _getEndpoints4PagingInAlertProfile_viewType = new Holder<JViewType>(
                _getEndpoints4PagingInAlertProfile_viewTypeVal);
        final Integer _getEndpoints4PagingInAlertProfile_itemsPerPageVal = 50;
        final Holder<Integer> _getEndpoints4PagingInAlertProfile_itemsPerPage = new Holder<Integer>(
                _getEndpoints4PagingInAlertProfile_itemsPerPageVal);
        final JQueryCondition _getEndpoints4PagingInAlertProfile_queryConditionVal = null;
        final Holder<JQueryCondition> _getEndpoints4PagingInAlertProfile_queryCondition = new Holder<JQueryCondition>(
                _getEndpoints4PagingInAlertProfile_queryConditionVal);
        final Integer _getEndpoints4PagingInAlertProfile_pageNoVal = 1;
        final Holder<Integer> _getEndpoints4PagingInAlertProfile_pageNo = new Holder<Integer>(
                _getEndpoints4PagingInAlertProfile_pageNoVal);
        final Holder<Integer> _getEndpoints4PagingInAlertProfile_totalPage = new Holder<Integer>();
        final Holder<Integer> _getEndpoints4PagingInAlertProfile_totalCount = new Holder<Integer>();
        final Holder<List<JDeviceForDetails>> _getEndpoints4PagingInAlertProfile_endpointList = new Holder<List<JDeviceForDetails>>();
        final JWebResult _getEndpoints4PagingInAlertProfile__return = port
                .getEndpoints4PagingInAlertProfile(_getEndpoints4PagingInAlertProfile_credentials,
                                                   _getEndpoints4PagingInAlertProfile_viewType,
                                                   _getEndpoints4PagingInAlertProfile_itemsPerPage,
                                                   _getEndpoints4PagingInAlertProfile_queryCondition,
                                                   _getEndpoints4PagingInAlertProfile_pageNo,
                                                   _getEndpoints4PagingInAlertProfile_totalPage,
                                                   _getEndpoints4PagingInAlertProfile_totalCount,
                                                   _getEndpoints4PagingInAlertProfile_endpointList);
        logger.info("getEndpoints4PagingInAlertProfile.result="
                + _getEndpoints4PagingInAlertProfile__return.getStatus()
                        .toString());
        logger.info("getEndpoints4PagingInAlertProfile._getEndpoints4PagingInAlertProfile_queryCondition="
                + _getEndpoints4PagingInAlertProfile_queryCondition.value);
        logger.info("getEndpoints4PagingInAlertProfile._getEndpoints4PagingInAlertProfile_endpointList="
                + _getEndpoints4PagingInAlertProfile_endpointList.value);
        return _getEndpoints4PagingInAlertProfile_endpointList.value;
    }

    /**
     * Get the list of the current remote alert profiles on the RPRM
     *
     * @param userToken
     */
    public List<JRemoteAlertProfile>
            getRemoteAlertProfiles(final String userToken) {
        logger.info("Invoking getRemoteAlertProfiles...");
        final JCredentials _getRemoteAlertProfiles_credentials = new JCredentials();
        _getRemoteAlertProfiles_credentials.setUserToken(userToken);
        final Holder<List<JRemoteAlertProfile>> _getRemoteAlertProfiles_profilesRetrieved = new Holder<List<JRemoteAlertProfile>>();
        final JWebResult _getRemoteAlertProfiles__return = port
                .getRemoteAlertProfiles(_getRemoteAlertProfiles_credentials,
                                        _getRemoteAlertProfiles_profilesRetrieved);
        logger.info("getRemoteAlertProfiles.result="
                + _getRemoteAlertProfiles__return.getStatus().toString());
        return _getRemoteAlertProfiles_profilesRetrieved.value;
    }
}
