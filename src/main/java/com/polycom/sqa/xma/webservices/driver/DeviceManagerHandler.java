package com.polycom.sqa.xma.webservices.driver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.polycom.webservices.DeviceManager.JCredentials;
import com.polycom.webservices.DeviceManager.JDeviceCapability;
import com.polycom.webservices.DeviceManager.JDeviceFamilyType;
import com.polycom.webservices.DeviceManager.JDeviceManager;
import com.polycom.webservices.DeviceManager.JDeviceManager_Service;
import com.polycom.webservices.DeviceManager.JDeviceModelVO;
import com.polycom.webservices.DeviceManager.JDeviceTypeParam;
import com.polycom.webservices.DeviceManager.JDeviceTypeVO;
import com.polycom.webservices.DeviceManager.JEndpointForDetails;
import com.polycom.webservices.DeviceManager.JEndpointForList;
import com.polycom.webservices.DeviceManager.JQueryCondition;
import com.polycom.webservices.DeviceManager.JViewType;
import com.polycom.webservices.DeviceManager.JWebResult;
import com.polycom.webservices.DeviceManager.Operator;

/**
 * Device Manager Handler
 *
 * @author Tao Chen
 *
 */
public class DeviceManagerHandler {
    protected static Logger    logger       = Logger.getLogger("DeviceManagerHandler");
    private static final QName SERVICE_NAME = new QName(
                                                        "http://polycom.com/WebServices",
            "JDeviceManager");
    private JDeviceManager     port;

    /**
     * Construction of DeviceManagerHandler class
     */
    public DeviceManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JDeviceManager) jsonInvocationHandler
                    .getProxy(JDeviceManager.class);
        } else {
            final URL wsdlURL = DeviceManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JDeviceManager.wsdl");
            final JDeviceManager_Service ss = new JDeviceManager_Service(
                                                                         wsdlURL, SERVICE_NAME);
            port = ss.getJDeviceManagerPort();
            final BindingProvider bindingProvider = (BindingProvider) port;
            final String webServicePath = webServiceUrl + "/JDeviceManager";
            bindingProvider.getRequestContext()
            .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                 webServicePath);
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
     * Search legacy endpoint or 3rd part endpoint for later adding
     *
     * @param userToken
     * @param ipAddress
     * @param deviceLoginName
     * @param devicePassword
     * @param deviceType
     * @return
     */
    public JEndpointForDetails addDeviceAutoPopulate(final String userToken,
            final String ipAddress,
            final String deviceLoginName,
            final String devicePassword,
            final String deviceType,
            final DeviceMetaManagerHandler dmmh) {
        System.out.println("Invoking addDeviceAutoPopulate...");
        logger.info("Invoking addDeviceAutoPopulate...");
        final JCredentials _addDeviceAutoPopulate_credentials = new JCredentials();
        _addDeviceAutoPopulate_credentials.setUserToken(userToken);
        final com.polycom.webservices.DeviceMetaManager.JDeviceTypeVO metaDeviceType = dmmh
                .getDeviceTypeByDeviceModel(userToken, deviceType);
        final JDeviceTypeVO _addDeviceAutoPopulate_deviceType = new JDeviceTypeVO();
        final JDeviceCapability dc = new JDeviceCapability();
        dc.setAlertable(metaDeviceType.getCapability().isAlertable());
        dc.setAssignable(metaDeviceType.getCapability().isAssignable());
        dc.setCallStatusStartTimeInGMTMode(metaDeviceType.getCapability()
                                           .isCallStatusStartTimeInGMTMode());
        dc.setEndpoint(true);
        dc.setGroupedDevice(metaDeviceType.getCapability().isGroupedDevice());
        dc.setH323Supported(metaDeviceType.getCapability().isH323Supported());
        dc.setMinusEqualsEmpty(metaDeviceType.getCapability()
                               .isMinusEqualsEmpty());
        dc.setNeedAdditionalCommand(metaDeviceType.getCapability()
                                    .isNeedAdditionalCommand());
        dc.setNotConcernedWithConferenceGuestDevice(metaDeviceType
                                                    .getCapability().isNotConcernedWithConferenceGuestDevice());
        dc.setSoftEndpoint(metaDeviceType.getCapability().isSoftEndpoint());
        dc.setSupportAclByModel(metaDeviceType.getCapability()
                                .isSupportAclByModel());
        dc.setSupportAutoReplaceH323TraversalIP(metaDeviceType.getCapability()
                                                .isSupportAutoReplaceH323TraversalIP());
        dc.setSupportAutoSoftupdate(metaDeviceType.getCapability()
                                    .isSupportAutoSoftupdate());
        dc.setSupportCDR(metaDeviceType.getCapability().isSupportCDR());
        dc.setSupportDialout(metaDeviceType.getCapability().isSupportDialout());
        dc.setSupportGMS(metaDeviceType.getCapability().isSupportGMS());
        dc.setSupportH320(metaDeviceType.getCapability().isSupportH320());
        dc.setSupportLegacyProvision(metaDeviceType.getCapability()
                                     .isSupportLegacyProvision());
        dc.setSupportLegacySoftupdate(metaDeviceType.getCapability()
                                      .isSupportLegacySoftupdate());
        dc.setSupportMSM(metaDeviceType.getCapability().isSupportMSM());
        dc.setSupportMaintenanceWindow(metaDeviceType.getCapability()
                                       .isSupportMaintenanceWindow());
        dc.setSupportManualAdd(metaDeviceType.getCapability()
                               .isSupportManualAdd());
        dc.setSupportSIPTraversal(metaDeviceType.getCapability()
                                  .isSupportSIPTraversal());
        dc.setSupportSNMPV3(metaDeviceType.getCapability().isSupportSNMPV3());
        dc.setSupportSoftupdateByModel(metaDeviceType.getCapability()
                                       .isSupportSoftupdateByModel());
        dc.setSupportUCSoftware(metaDeviceType.getCapability()
                                .isSupportUCSoftware());
        dc.setSupportsEagleEyeConfig(metaDeviceType.getCapability()
                                     .isSupportsEagleEyeConfig());
        dc.setSupportsTrackableCameraConfig(metaDeviceType.getCapability()
                                            .isSupportsTrackableCameraConfig());
        dc.setSupportVC2AndLegacyMode(metaDeviceType.getCapability()
                                      .isSupportVC2AndLegacyMode());
        dc.setSupportsBundledProvisioning(metaDeviceType.getCapability()
                                          .isSupportsBundledProvisioning());
        dc.setSupportsCDRSync(metaDeviceType.getCapability()
                              .isSupportsCDRSync());
        dc.setSupportsCredentialsChanged(metaDeviceType.getCapability()
                                         .isSupportsCredentialsChanged());
        dc.setSupportsGetCallStatus(metaDeviceType.getCapability()
                                    .isSupportsGetCallStatus());
        dc.setSupportsHangUp(metaDeviceType.getCapability().isSupportsHangUp());
        dc.setSupportsLegacyNotification(metaDeviceType.getCapability()
                                         .isSupportsLegacyNotification());
        dc.setSupportsPeripheral(metaDeviceType.getCapability()
                                 .isSupportsPeripheral());
        dc.setSupportsPresence(metaDeviceType.getCapability()
                               .isSupportsPresence());
        dc.setSupportsRabbitEyeConfig(metaDeviceType.getCapability()
                                      .isSupportsRabbitEyeConfig());
        dc.setSupportsSystemConfig(metaDeviceType.getCapability()
                                   .isSupportsSystemConfig());
        dc.setSupportsVenusConfig(metaDeviceType.getCapability()
                                  .isSupportsVenusConfig());
        dc.setVc2Device(metaDeviceType.getCapability().isVc2Device());
        dc.setVideoHardwareEndpoint(metaDeviceType.getCapability()
                                    .isVideoHardwareEndpoint());
        dc.setVideoSoftwareEndpoint(metaDeviceType.getCapability()
                                    .isVideoSoftwareEndpoint());
        dc.setAAutopopulatedDeviceType(metaDeviceType.getCapability()
                                       .isAAutopopulatedDeviceType());
        dc.setABorderDevice(metaDeviceType.getCapability().isABorderDevice());
        dc.setACommunicableDeviceType(metaDeviceType.getCapability()
                                      .isACommunicableDeviceType());
        dc.setAGabDevice(metaDeviceType.getCapability().isAGabDevice());
        dc.setAManageableDeviceType(metaDeviceType.getCapability()
                                    .isAManageableDeviceType());
        dc.setAPollingForCallDataDeviceType(metaDeviceType.getCapability()
                                            .isAPollingForCallDataDeviceType());
        dc.setAPollingForStatusDeviceType(metaDeviceType.getCapability()
                                          .isAPollingForStatusDeviceType());
        dc.setGKRegisteredNetworkDevice(metaDeviceType.getCapability()
                                        .isGKRegisteredNetworkDevice());
        _addDeviceAutoPopulate_deviceType.setCapability(dc);
        if (deviceType.equalsIgnoreCase("HDX") || deviceType.equals("Tandberg")) {
            _addDeviceAutoPopulate_deviceType.setDbValue(metaDeviceType
                                                         .getDbValue());
            _addDeviceAutoPopulate_deviceType.setDisplayName(metaDeviceType
                                                             .getDisplayName());
            _addDeviceAutoPopulate_deviceType
            .setFamily(JDeviceFamilyType.VIDEO___ENDPOINT);
            _addDeviceAutoPopulate_deviceType.setSchemaName(metaDeviceType
                                                            .getSchemaName());
            _addDeviceAutoPopulate_deviceType.setShortName(metaDeviceType
                                                           .getShortName());
        }
        final List<com.polycom.webservices.DeviceMetaManager.JDeviceTypeParam> metadtpList = metaDeviceType
                .getParams();
        for (final com.polycom.webservices.DeviceMetaManager.JDeviceTypeParam metadtp : metadtpList) {
            final JDeviceTypeParam dtp = new JDeviceTypeParam();
            dtp.setName(metadtp.getName());
            dtp.setValue(metadtp.getValue());
            _addDeviceAutoPopulate_deviceType.getParams().add(dtp);
        }
        final String _addDeviceAutoPopulate_ipAddress = ipAddress;
        final String _addDeviceAutoPopulate_deviceLoginName = deviceLoginName;
        final String _addDeviceAutoPopulate_devicePassword = devicePassword;
        final String _addDeviceAutoPopulate_deviceAMFPassword = "";
        final Holder<JEndpointForDetails> _addDeviceAutoPopulate_deviceForDetails = new Holder<JEndpointForDetails>();
        final JWebResult _addDeviceAutoPopulate__return = port
                .addDeviceAutoPopulate(_addDeviceAutoPopulate_credentials,
                                       _addDeviceAutoPopulate_deviceType,
                                       _addDeviceAutoPopulate_ipAddress,
                                       _addDeviceAutoPopulate_deviceLoginName,
                                       _addDeviceAutoPopulate_devicePassword,
                                       _addDeviceAutoPopulate_deviceAMFPassword,
                                       _addDeviceAutoPopulate_deviceForDetails);
        System.out.println("addDeviceAutoPopulate.result="
                + _addDeviceAutoPopulate__return);
        System.out
        .println("addDeviceAutoPopulate._addDeviceAutoPopulate_deviceForDetails="
                + _addDeviceAutoPopulate_deviceForDetails.value);
        return _addDeviceAutoPopulate_deviceForDetails.value;
    }

    /**
     * Auto detect ip in the ip range
     *
     * @param userToken
     * @param startingIpAddress
     * @param endingIpAddress
     * @return
     */
    public JWebResult autoDetectDevices(final String userToken,
            final String startingIpAddress,
            final String endingIpAddress) {
        System.out.println("Invoking autoDetectDevices...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port.autoDetectDevices(credentials,
                                                         startingIpAddress,
                                                         endingIpAddress);
        return result;
    }

    /**
     * Delete the legacy or 3rd endpoints
     *
     * @param userToken
     * @param deviceIds
     * @return
     */
    public JWebResult deleteDevices(final String userToken,
            final List<Integer> deviceIds) {
        System.out.println("Invoking deleteDevices...");
        final JCredentials _deleteDevices_credentials = new JCredentials();
        _deleteDevices_credentials.setUserToken(userToken);
        final List<Integer> _deleteDevices_deviceIds = deviceIds;
        final JWebResult _deleteDevices__return = port
                .deleteDevices(_deleteDevices_credentials,
                               _deleteDevices_deviceIds);
        System.out.println("deleteDevices.result=" + _deleteDevices__return);
        return _deleteDevices__return;
    }

    /**
     * Delete devices with dial string
     *
     * @param userToken
     * @param deviceIds
     * @return
     */
    public JWebResult deleteDevicesWithAlias(final String userToken,
            final List<Integer> deviceIds) {
        System.out.println("Invoking deleteDevicesWithAlias...");
        final JCredentials _deleteDevicesWithAlias_credentials = new JCredentials();
        _deleteDevicesWithAlias_credentials.setUserToken(userToken);
        final List<Integer> _deleteDevicesWithAlias_deviceIds = deviceIds;
        final JWebResult _deleteDevicesWithAlias__return = port
                .deleteDevicesWithAlias(_deleteDevicesWithAlias_credentials,
                                        _deleteDevicesWithAlias_deviceIds);
        System.out.println("deleteDevicesWithAlias.result="
                + _deleteDevicesWithAlias__return);
        return _deleteDevicesWithAlias__return;
    }

    public JWebResult disableCommonPassword(final String userToken) {
        System.out.println("Invoking disableCommonPassword...");
        logger.info("Invoking disableCommonPassword...");
        final JCredentials _saveDevice_credentials = new JCredentials();
        _saveDevice_credentials.setUserToken(userToken);
        final JWebResult result = port
                .disableCommonPassword(_saveDevice_credentials);
        return result;
    }

    public
    JWebResult
    getAutoDetectDevices(final String userToken,
            Holder<Boolean> _getAutoDetectDevices_finished,
            Holder<Integer> _getAutoDetectDevices_ipCount,
            Holder<Integer> _getAutoDetectDevices_attempted,
            Holder<List<com.polycom.webservices.DeviceManager.JDeviceTypeVO>> _getAutoDetectDevices_deviceTypes,
            Holder<List<String>> _getAutoDetectDevices_deviceIpAddresses,
            Holder<Integer> _getAutoDetectDevices_licenseAvailable) {
        System.out.println("Invoking getAutoDetectDevices...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        _getAutoDetectDevices_finished = new Holder<Boolean>();
        _getAutoDetectDevices_ipCount = new Holder<Integer>();
        _getAutoDetectDevices_attempted = new Holder<Integer>();
        _getAutoDetectDevices_deviceTypes = new Holder<List<com.polycom.webservices.DeviceManager.JDeviceTypeVO>>();
        _getAutoDetectDevices_deviceIpAddresses = new Holder<List<String>>();
        _getAutoDetectDevices_licenseAvailable = new Holder<Integer>();
        final JWebResult result = port
                .getAutoDetectDevices(credentials,
                                      _getAutoDetectDevices_finished,
                                      _getAutoDetectDevices_ipCount,
                                      _getAutoDetectDevices_attempted,
                                      _getAutoDetectDevices_deviceTypes,
                                      _getAutoDetectDevices_deviceIpAddresses,
                                      _getAutoDetectDevices_licenseAvailable);
        return result;
    }

    /**
     * View peripherals information when select the endpoint
     *
     * @param userToken
     * @param deviceId
     * @return
     */
    @SuppressWarnings("unused")
    public List<JEndpointForDetails>
    getDeviceDataForPeripherals(final String userToken,
                                final int deviceId) {
        logger.info("Invoking getDeviceDataForPeripherals...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JEndpointForDetails>> peripherals = new Holder<List<JEndpointForDetails>>();
        final JWebResult result = port.getDeviceDataForPeripherals(credentials,
                                                                   deviceId,
                                                                   peripherals);
        return peripherals.value;
    }

    /**
     * Use device id to get EP details
     *
     * @param userToken
     * @param deviceRole
     * @param deviceId
     * @return
     */
    @SuppressWarnings("unused")
    public JEndpointForDetails getDeviceForDetails(final String userToken,
            final int deviceRole,
            final int deviceId) {
        System.out.println("Invoking getDeviceForDetails...");
        logger.info("Invoking getDeviceForDetails...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JEndpointForDetails> deviceForDetails = new Holder<com.polycom.webservices.DeviceManager.JEndpointForDetails>();
        final JWebResult result = port.getDeviceForDetails(credentials,
                                                           deviceRole,
                                                           deviceId,
                                                           deviceForDetails);
        return deviceForDetails.value;
    }

    public List<JEndpointForList>
    getDevicesForSUSerialNumList(final String userToken,
                                 final boolean isVc2,
                                 final List<JDeviceModelVO> models) {
        logger.info("Invoking getDevicesForSUSerialNumList...");
        System.out.println("Invoking getDevicesForSUSerialNumList...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JEndpointForList>> _getDevicesForSUSerialNumList_devList = new Holder<List<JEndpointForList>>();
        port.getDevicesForSUSerialNumList(credentials,
                                          models,
                                          isVc2,
                                          _getDevicesForSUSerialNumList_devList);
        return _getDevicesForSUSerialNumList_devList.value;
    }

    @SuppressWarnings("unused")
    public List<JEndpointForList>
    getDevicesFromEndpointCdr(final String userToken) {
        logger.info("Invoking getDevicesFromEndpointCdr...");
        System.out.println("Invoking getDevicesFromEndpointCdr...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JEndpointForList>> devicesList = new Holder<List<JEndpointForList>>();
        final JWebResult result = port.getDevicesFromEndpointCdr(credentials,
                                                                 devicesList);
        return devicesList.value;
    }

    public List<JEndpointForList>
    getDevicesFromEndpointCdr(final String userToken,
                              final String endpointName) {
        final List<JEndpointForList> epList = getDevicesFromEndpointCdr(userToken);
        final List<JEndpointForList> result = new ArrayList<JEndpointForList>();
        for (final JEndpointForList ep : epList) {
            if (ep.getDeviceName() == null || ep.getSerialNumber() == null) {
                continue;
            }
            if (endpointName.contains(ep.getDeviceName())
                    && !ep.getSerialNumber().isEmpty()) {
                result.add(ep);
            }
        }
        return result;
    }

    /**
     * Get the device type according to the input view type
     *
     * @param userToken
     * @param viewTypeVal
     * @return
     */
    public List<JDeviceTypeVO> getDeviceTypes4Paging(final String userToken,
                                                     final JViewType viewTypeVal) {
        logger.info("Invoking getDeviceTypes4Paging...");
        final JCredentials _getDeviceTypes4Paging_credentials = new JCredentials();
        _getDeviceTypes4Paging_credentials.setUserToken(userToken);
        final JViewType _getDeviceTypes4Paging_viewTypeVal = viewTypeVal;
        final Holder<JViewType> _getDeviceTypes4Paging_viewType = new Holder<JViewType>(
                _getDeviceTypes4Paging_viewTypeVal);
        final Holder<List<JDeviceTypeVO>> _getDeviceTypes4Paging_deviceTypes = new Holder<List<JDeviceTypeVO>>();
        final JWebResult result = port
                .getDeviceTypes4Paging(_getDeviceTypes4Paging_credentials,
                                       _getDeviceTypes4Paging_viewType,
                                       _getDeviceTypes4Paging_deviceTypes);
        logger.info("The result for calling getDeviceTypes4Paging is: "
                + result.getStatus().toString());
        return _getDeviceTypes4Paging_deviceTypes.value;
    }

    /**
     * Get all the endpoints in the XMA without any condition
     *
     * @param userToken
     * @param itemsPerPageVal
     * @return
     */
    public List<JEndpointForList> getEndpoints4Paging(final String userToken,
                                                      final int itemsPerPageVal) {
        System.out.println("Invoking getEndpoints4Paging...");
        logger.info("Invoking getEndpoints4Paging...");
        final JCredentials _getEndpoints4Paging_credentials = new JCredentials();
        _getEndpoints4Paging_credentials.setUserToken(userToken);
        final JViewType _getEndpoints4Paging_viewTypeVal = JViewType.EP_MONITOR___VIEW;
        final Holder<JViewType> _getEndpoints4Paging_viewType = new Holder<JViewType>(
                _getEndpoints4Paging_viewTypeVal);
        final Integer _getEndpoints4Paging_itemsPerPageVal = itemsPerPageVal;
        final Holder<Integer> _getEndpoints4Paging_itemsPerPage = new Holder<Integer>(
                _getEndpoints4Paging_itemsPerPageVal);
        final JQueryCondition _getEndpoints4Paging_queryConditionVal = new JQueryCondition();
        _getEndpoints4Paging_queryConditionVal.setAttributeName("1");
        _getEndpoints4Paging_queryConditionVal.setAttributeValue("1");
        _getEndpoints4Paging_queryConditionVal.setHasNoCondition(false);
        _getEndpoints4Paging_queryConditionVal.setIsAsc(true);
        _getEndpoints4Paging_queryConditionVal.setIsRelation(false);
        _getEndpoints4Paging_queryConditionVal.setOperator(Operator.EQUAL);
        _getEndpoints4Paging_queryConditionVal.setOrderBy("Endpoint_Name");
        final Holder<JQueryCondition> _getEndpoints4Paging_queryCondition = new Holder<JQueryCondition>(
                _getEndpoints4Paging_queryConditionVal);
        final Integer _getEndpoints4Paging_pageNoVal = 1;
        final Holder<Integer> _getEndpoints4Paging_pageNo = new Holder<Integer>(
                _getEndpoints4Paging_pageNoVal);
        final Holder<Integer> _getEndpoints4Paging_totalPage = new Holder<Integer>();
        final Holder<Integer> _getEndpoints4Paging_totalCount = new Holder<Integer>();
        final Holder<List<JEndpointForList>> _getEndpoints4Paging_endpointList = new Holder<List<JEndpointForList>>();
        port.getEndpoints4Paging(_getEndpoints4Paging_credentials,
                                 _getEndpoints4Paging_viewType,
                                 _getEndpoints4Paging_itemsPerPage,
                                 _getEndpoints4Paging_queryCondition,
                                 _getEndpoints4Paging_pageNo,
                                 _getEndpoints4Paging_totalPage,
                                 _getEndpoints4Paging_totalCount,
                                 _getEndpoints4Paging_endpointList);
        return _getEndpoints4Paging_endpointList.value;
    }

    /**
     * Retrieve all the peripheral devices from RPRM
     *
     * @param userToken
     * @param queryConditionVal
     * @param itemsPerPageVal
     * @return
     */
    @SuppressWarnings("unused")
    public List<JEndpointForList>
    getPeripheralEndpoints4Paging(final String userToken,
                                  final JQueryCondition queryConditionVal,
                                  final Integer itemsPerPageVal) {
        logger.info("Invoking getPeripheralEndpoints4Paging...");
        final JCredentials _getPeripheralEndpoints4Paging_credentials = new JCredentials();
        _getPeripheralEndpoints4Paging_credentials.setUserToken(userToken);
        final JViewType _getPeripheralEndpoints4Paging_viewTypeVal = JViewType.EP_PERIPHERAL___VIEW;
        final Holder<JViewType> _getPeripheralEndpoints4Paging_viewType = new Holder<JViewType>(
                _getPeripheralEndpoints4Paging_viewTypeVal);
        final Integer _getPeripheralEndpoints4Paging_itemsPerPageVal = itemsPerPageVal;
        final Holder<Integer> _getPeripheralEndpoints4Paging_itemsPerPage = new Holder<Integer>(
                _getPeripheralEndpoints4Paging_itemsPerPageVal);
        final JQueryCondition _getPeripheralEndpoints4Paging_queryConditionVal = queryConditionVal;
        final Holder<JQueryCondition> _getPeripheralEndpoints4Paging_queryCondition = new Holder<JQueryCondition>(
                _getPeripheralEndpoints4Paging_queryConditionVal);
        final Integer _getPeripheralEndpoints4Paging_pageNoVal = 1;
        final Holder<Integer> _getPeripheralEndpoints4Paging_pageNo = new Holder<Integer>(
                _getPeripheralEndpoints4Paging_pageNoVal);
        final Holder<Integer> _getPeripheralEndpoints4Paging_totalPage = new Holder<Integer>();
        final Holder<Integer> _getPeripheralEndpoints4Paging_totalCount = new Holder<Integer>();
        final Holder<List<JEndpointForList>> _getPeripheralEndpoints4Paging_endpointList = new Holder<List<JEndpointForList>>();
        final JWebResult _getPeripheralEndpoints4Paging__return = port
                .getPeripheralEndpoints4Paging(_getPeripheralEndpoints4Paging_credentials,
                                               _getPeripheralEndpoints4Paging_viewType,
                                               _getPeripheralEndpoints4Paging_itemsPerPage,
                                               _getPeripheralEndpoints4Paging_queryCondition,
                                               _getPeripheralEndpoints4Paging_pageNo,
                                               _getPeripheralEndpoints4Paging_totalPage,
                                               _getPeripheralEndpoints4Paging_totalCount,
                                               _getPeripheralEndpoints4Paging_endpointList);

        return _getPeripheralEndpoints4Paging_endpointList.value;
    }

    /**
     * Get the unassigned devices
     *
     * @param userToken
     *            User token
     * @return Return the list of the devices
     */
    public List<JEndpointForList>
    getUnassignedDynamicDevices(final String userToken) {
        return getUnassignedDynamicDevices(userToken, null);
    }

    /**
     * Get the unassigned devices
     *
     * @param userToken
     *            User token
     * @return Return the list of the devices
     */
    public List<JEndpointForList>
    getUnassignedDynamicDevices(final String userToken,
                                final String searchStr) {
        System.out.println("Invoking getUnassignedDynamicDevices...");
        final JCredentials _getUnassignedDynamicDevices_credentials = new JCredentials();
        _getUnassignedDynamicDevices_credentials.setUserToken(userToken);
        final int _getUnassignedDynamicDevices_token = -1;
        final Holder<List<JEndpointForList>> _getUnassignedDynamicDevices_deviceList = new Holder<List<JEndpointForList>>();
        final Holder<Integer> _getUnassignedDynamicDevices_newToken = new Holder<Integer>();
        final JWebResult _getUnassignedDynamicDevices__return = port
                .getUnassignedDynamicDevices(_getUnassignedDynamicDevices_credentials,
                                             _getUnassignedDynamicDevices_token,
                                             _getUnassignedDynamicDevices_deviceList,
                                             _getUnassignedDynamicDevices_newToken);
        System.out.println("getUnassignedDynamicDevices.result="
                + _getUnassignedDynamicDevices__return);
        System.out
        .println("getUnassignedDynamicDevices._getUnassignedDynamicDevices_deviceList="
                + _getUnassignedDynamicDevices_deviceList.value);
        System.out
        .println("getUnassignedDynamicDevices._getUnassignedDynamicDevices_newToken="
                + _getUnassignedDynamicDevices_newToken.value);
        if (searchStr != null && !searchStr.isEmpty()) {
            final Pattern pattern = Pattern.compile(searchStr);
            final List<JEndpointForList> endpoints = new ArrayList<JEndpointForList>();
            for (final JEndpointForList endpoint : _getUnassignedDynamicDevices_deviceList.value) {
                final Matcher matcher = pattern.matcher(endpoint
                                                        .getDeviceName());
                if (matcher.find()) {
                    endpoints.add(endpoint);
                }
            }
            return endpoints;
        } else {
            return _getUnassignedDynamicDevices_deviceList.value;
        }
    }

    /**
     * Reboot device
     *
     * @param userToken
     * @param deviceId
     * @return web result
     */
    public JWebResult rebootDevice(final String userToken, final int deviceId) {
        System.out.println("Invoking rebootDevice...");
        logger.info("Invoking rebootDevice...");
        final JCredentials _saveDevice_credentials = new JCredentials();
        _saveDevice_credentials.setUserToken(userToken);
        return port.rebootDevice(_saveDevice_credentials, deviceId);
    }

    /**
     * Reset phone digest user password
     *
     * @param userToken
     * @param deviceIds
     * @param newPassword
     * @return
     */
    public JWebResult resetPhoneDigestUserPassword(final String userToken,
            final List<Integer> deviceIds,
            final String newPassword) {
        System.out.println("Invoking resetPhoneDigestUserPassword...");
        logger.info("Invoking resetPhoneDigestUserPassword...");
        final JCredentials _resetDigestPassword_credentials = new JCredentials();
        _resetDigestPassword_credentials.setUserToken(userToken);
        final List<Integer> _resetDigestPassword_deviceIds = deviceIds;
        final String _resetDigestPassword_newPassword = newPassword;
        final JWebResult result = port
                .resetPhoneDigestUserPassword(_resetDigestPassword_credentials,
                                              _resetDigestPassword_deviceIds,
                                              _resetDigestPassword_newPassword);
        return result;
    }

    /**
     * Add the searched endpoint
     *
     * @param userToken
     * @param deviceForDetails
     * @param isAdd
     * @return
     */
    public JWebResult saveDevice(final String userToken,
            final JEndpointForDetails deviceForDetails,
            final boolean isAdd) {
        System.out.println("Invoking saveDevice...");
        logger.info("Invoking saveDevice...");
        final JCredentials _saveDevice_credentials = new JCredentials();
        _saveDevice_credentials.setUserToken(userToken);
        final boolean _saveDevice_isAdd = isAdd;
        final JEndpointForDetails _saveDevice_deviceForDetails = deviceForDetails;
        final JWebResult _saveDevice__return = port
                .saveDevice(_saveDevice_credentials,
                            _saveDevice_isAdd,
                            _saveDevice_deviceForDetails);
        System.out.println("saveDevice.result=" + _saveDevice__return);
        return _saveDevice__return;
    }

    public JWebResult setCommonPassword(final String userToken,
            final String username,
            final String password) {
        System.out.println("Invoking setCommonPassword...");
        logger.info("Invoking setCommonPassword...");
        final JCredentials _saveDevice_credentials = new JCredentials();
        _saveDevice_credentials.setUserToken(userToken);
        final JWebResult result = port
                .setCommonPassword(_saveDevice_credentials, password, username);
        return result;
    }

    /**
     * Set digest account default password
     *
     * @param userToken
     * @param password
     * @return
     */
    public JWebResult setDigestAccountDefaultPassword(final String userToken,
            final String password) {
        System.out.println("Invoking setDigestAccountDefaultPassword...");
        logger.info("Invoking setDigestAccountDefaultPassword...");
        final JCredentials _setDigestAccountDefaultPassword_credentials = new JCredentials();
        _setDigestAccountDefaultPassword_credentials.setUserToken(userToken);
        final String _setDigestAccountDefaultPassword_newPassword = password;
        final JWebResult result = port
                .setDigestAccountDefaultPassword(_setDigestAccountDefaultPassword_credentials,
                                                 _setDigestAccountDefaultPassword_newPassword);
        return result;
    }
}