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
import com.polycom.webservices.NetworkDeviceManager.BridgeDeviceForDetails;
import com.polycom.webservices.NetworkDeviceManager.JCredentials;
import com.polycom.webservices.NetworkDeviceManager.JDeviceType;
import com.polycom.webservices.NetworkDeviceManager.JNetworkDeviceManager;
import com.polycom.webservices.NetworkDeviceManager.JNetworkDeviceManager_Service;
import com.polycom.webservices.NetworkDeviceManager.JVBP;
import com.polycom.webservices.NetworkDeviceManager.JWebResult;
import com.polycom.webservices.NetworkDeviceManager.McuPoolOrderForDetails;
import com.polycom.webservices.NetworkDeviceManager.NetworkDeviceForList;

/**
 * Network device manager handler
 *
 * @author Tao Chen
 *
 */
public class NetworkDeviceManagerHandler {
    protected static Logger            logger                 = Logger
            .getLogger("NetworkDeviceManagerHandler");
    private static final QName         SERVICE_NAME           = new QName(
            "http://polycom.com/WebServices", "JNetworkDeviceManager");
    JNetworkDeviceManager              port;
    private List<NetworkDeviceForList> networkDeviceList      = new ArrayList<NetworkDeviceForList>();
    private List<NetworkDeviceForList> networkDeviceList4pool = new ArrayList<NetworkDeviceForList>();

    /**
     * Construction of NetworkDeviceManagerHandler class
     */
    public NetworkDeviceManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JNetworkDeviceManager) jsonInvocationHandler
                    .getProxy(JNetworkDeviceManager.class);
        } else {
            final URL wsdlURL = NetworkDeviceManagerHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JNetworkDeviceManager.wsdl");
            final JNetworkDeviceManager_Service ss = new JNetworkDeviceManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJNetworkDeviceManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JNetworkDeviceManager");
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
     * Search the bridge and get the response data
     *
     * @param userToken
     * @param ipAddress
     * @param dnsAddress
     * @param userId
     * @param password
     * @return
     */
    public BridgeDeviceForDetails
            addBridgeDeviceAutoPopulate(final String userToken,
                                        final String ipAddress,
                                        final String dnsAddress,
                                        final String userId,
                                        final String password) {
        System.out.println("Invoking addBridgeDeviceAutoPopulate...");
        logger.info("Invoking addBridgeDeviceAutoPopulate...");
        final JCredentials _addBridgeDeviceAutoPopulate_credentials = new JCredentials();
        _addBridgeDeviceAutoPopulate_credentials.setUserToken(userToken);
        final JDeviceType _addBridgeDeviceAutoPopulate_deviceType = JDeviceType.CR;
        final String _addBridgeDeviceAutoPopulate_ipAddress = ipAddress;
        final String _addBridgeDeviceAutoPopulate_dnsAddress = dnsAddress;
        final String _addBridgeDeviceAutoPopulate_userId = userId;
        final String _addBridgeDeviceAutoPopulate_password = password;
        final Holder<BridgeDeviceForDetails> _addBridgeDeviceAutoPopulate_detail = new Holder<BridgeDeviceForDetails>();
        final JWebResult _addBridgeDeviceAutoPopulate__return = port
                .addBridgeDeviceAutoPopulate(_addBridgeDeviceAutoPopulate_credentials,
                                             _addBridgeDeviceAutoPopulate_deviceType,
                                             _addBridgeDeviceAutoPopulate_ipAddress,
                                             _addBridgeDeviceAutoPopulate_dnsAddress,
                                             _addBridgeDeviceAutoPopulate_userId,
                                             _addBridgeDeviceAutoPopulate_password,
                                             _addBridgeDeviceAutoPopulate_detail);
        System.out.println("addBridgeDeviceAutoPopulate.result="
                + _addBridgeDeviceAutoPopulate__return);
        logger.info("The bridge searching return is: "
                + _addBridgeDeviceAutoPopulate__return.getStatus());
        System.out
                .println("addBridgeDeviceAutoPopulate._addBridgeDeviceAutoPopulate_detail="
                        + _addBridgeDeviceAutoPopulate_detail.value);
        return _addBridgeDeviceAutoPopulate_detail.value;
    }

    public JWebResult addSBC(final String userToken, final JVBP sbcToAdd) {
        System.out.println("Invoking addSBC...");
        logger.info("Invoking addSBC...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.addSBC(credentials, sbcToAdd);
    }

    public JWebResult addVBP(final String userToken, final JVBP vbpToAdd) {
        System.out.println("Invoking addVBP...");
        logger.info("Invoking addVBP...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.addVBP(credentials, vbpToAdd);
    }

    public JWebResult deleteAccessServers(final String userToken,
                                          final List<String> deviceIds) {
        System.out.println("Invoking deleteVBP...");
        logger.info("Invoking deleteVBP...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.deleteAccessServers(credentials, deviceIds);
    }

    /**
     * Delete the network device
     *
     * @param userToken
     * @param deviceId
     * @return
     */
    public JWebResult deleteNetworkDevices(final String userToken,
                                           final List<String> deviceId) {
        System.out.println("Invoking deleteNetworkDevices...");
        logger.info("Invoking deleteNetworkDevices...");
        final JCredentials _deleteNetworkDevices_credentials = new JCredentials();
        _deleteNetworkDevices_credentials.setUserToken(userToken);
        final List<String> _deleteNetworkDevices_deviceIds = deviceId;
        final JWebResult _deleteNetworkDevices__return = port
                .deleteNetworkDevices(_deleteNetworkDevices_credentials,
                                      _deleteNetworkDevices_deviceIds);
        System.out.println("deleteNetworkDevices.result="
                + _deleteNetworkDevices__return);
        return _deleteNetworkDevices__return;
    }

    public JWebResult deleteSBC(final String userToken,
                                final String deviceUUID) {
        System.out.println("Invoking deleteSBC...");
        logger.info("Invoking deleteSBC...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.deleteSBC(credentials, deviceUUID);
    }

    public JWebResult deleteVBP(final String userToken,
                                final String deviceUUID) {
        System.out.println("Invoking deleteVBP...");
        logger.info("Invoking deleteVBP...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.deleteVBP(credentials, deviceUUID);
    }

    /**
     * Find bridge device for its details
     *
     * @param userToken
     * @param deviceUUID
     * @return
     */
    public BridgeDeviceForDetails findBridgeDevice(final String userToken,
                                                   final String deviceUUID) {
        System.out.println("Invoking findBridgeDevice...");
        logger.info("Invoking findBridgeDevice...");
        final JCredentials _findBridgeDevice_credentials = new JCredentials();
        final String _findBridgeDevice_deviceUUID = deviceUUID;
        final Holder<BridgeDeviceForDetails> _findBridgeDevice_detail = new Holder<BridgeDeviceForDetails>();
        final JWebResult _findBridgeDevice__return = port
                .findBridgeDevice(_findBridgeDevice_credentials,
                                  _findBridgeDevice_deviceUUID,
                                  _findBridgeDevice_detail);
        System.out.println("findBridgeDevice.result="
                + _findBridgeDevice__return);
        System.out.println("findBridgeDevice._findBridgeDevice_detail="
                + _findBridgeDevice_detail.value);
        return _findBridgeDevice_detail.value;
    }

    /**
     * Get available bridges for scheduling conference
     *
     * @param userToken
     * @return
     */
    public JWebResult getAvailableBridges(final String userToken) {
        System.out.println("Invoking getAvailableBridges...");
        logger.info("Invoking getAvailableBridges...");
        final JCredentials _getAvailableBridges_credentials = new JCredentials();
        _getAvailableBridges_credentials.setUserToken(userToken);
        final Boolean _getAvailableBridges_resultsForConferenceOwner = false;
        final Integer _getAvailableBridges_areaId = -1;
        final Holder<List<NetworkDeviceForList>> _getAvailableBridges_poolOrderList = new Holder<List<NetworkDeviceForList>>();
        final Holder<List<NetworkDeviceForList>> _getAvailableBridges_mcuList = new Holder<List<NetworkDeviceForList>>();
        final JWebResult _getAvailableBridges__return = port
                .getAvailableBridges(_getAvailableBridges_credentials,
                                     _getAvailableBridges_resultsForConferenceOwner,
                                     _getAvailableBridges_areaId,
                                     _getAvailableBridges_poolOrderList,
                                     _getAvailableBridges_mcuList);
        System.out.println("getAvailableBridges.result="
                + _getAvailableBridges__return);
        System.out
                .println("getAvailableBridges._getAvailableBridges_poolOrderList="
                        + _getAvailableBridges_poolOrderList.value);
        setAvailablePoolOrder(_getAvailableBridges_poolOrderList.value);
        System.out.println("getAvailableBridges._getAvailableBridges_mcuList="
                + _getAvailableBridges_mcuList.value);
        setAvailableMCU(_getAvailableBridges_mcuList.value);
        return _getAvailableBridges__return;
    }

    public List<NetworkDeviceForList> getAvailableMCU() {
        return networkDeviceList;
    }

    public List<NetworkDeviceForList> getAvailablePoolOrder() {
        return networkDeviceList4pool;
    }

    /**
     * Get the DMA pool orders detail information
     *
     * @param userToken
     *            User token
     * @param token
     *            This is actually the deviceUUID of the pool order
     * @return
     */
    @SuppressWarnings("unused")
    public McuPoolOrderForDetails getDmaPoolOrdersDetail(final String userToken,
                                                         final String token) {
        logger.info("Invoking getDmaPoolOrders...");
        final JCredentials _getDmaPoolOrders_credentials = new JCredentials();
        _getDmaPoolOrders_credentials.setUserToken(userToken);
        final String _getDmaPoolOrders_token = token;
        final boolean _getDmaPoolOrders_commonPoolIncluded = true;
        final Holder<List<NetworkDeviceForList>> _getDmaPoolOrders_poolOrderList = new Holder<List<NetworkDeviceForList>>();
        final Holder<McuPoolOrderForDetails> _getDmaPoolOrders_poolOrderDetail = new Holder<McuPoolOrderForDetails>();
        final JWebResult _getDmaPoolOrders__return = port
                .getDmaPoolOrders(_getDmaPoolOrders_credentials,
                                  _getDmaPoolOrders_token,
                                  _getDmaPoolOrders_commonPoolIncluded,
                                  _getDmaPoolOrders_poolOrderList,
                                  _getDmaPoolOrders_poolOrderDetail);
        return _getDmaPoolOrders_poolOrderDetail.value;
    }

    /**
     * Get the DMA pool orders list
     *
     * @param userToken
     * @return
     */
    @SuppressWarnings("unused")
    public List<NetworkDeviceForList>
            getDmaPoolOrdersList(final String userToken) {
        logger.info("Invoking getDmaPoolOrders...");
        final JCredentials _getDmaPoolOrders_credentials = new JCredentials();
        _getDmaPoolOrders_credentials.setUserToken(userToken);
        final String _getDmaPoolOrders_token = "";
        final boolean _getDmaPoolOrders_commonPoolIncluded = true;
        final Holder<List<NetworkDeviceForList>> _getDmaPoolOrders_poolOrderList = new Holder<List<NetworkDeviceForList>>();
        final Holder<McuPoolOrderForDetails> _getDmaPoolOrders_poolOrderDetail = new Holder<McuPoolOrderForDetails>();
        final JWebResult _getDmaPoolOrders__return = port
                .getDmaPoolOrders(_getDmaPoolOrders_credentials,
                                  _getDmaPoolOrders_token,
                                  _getDmaPoolOrders_commonPoolIncluded,
                                  _getDmaPoolOrders_poolOrderList,
                                  _getDmaPoolOrders_poolOrderDetail);
        return _getDmaPoolOrders_poolOrderList.value;
    }

    /**
     * Get MCU and other network devices list
     *
     * @return
     */
    public List<NetworkDeviceForList>
            getMCUsNetworkDevicesForList(final String userToken) {
        System.out.println("Invoking getMCUsNetworkDevicesForList...");
        logger.info("Invoking getMCUsNetworkDevicesForList...");
        final JCredentials _getMCUsNetworkDevicesForList_credentials = new JCredentials();
        _getMCUsNetworkDevicesForList_credentials.setUserToken(userToken);
        final int _getMCUsNetworkDevicesForList_token = 0;
        final Holder<List<NetworkDeviceForList>> _getMCUsNetworkDevicesForList_modifiedList = new Holder<List<NetworkDeviceForList>>();
        final Holder<List<String>> _getMCUsNetworkDevicesForList_deletedList = new Holder<List<String>>();
        final Holder<Integer> _getMCUsNetworkDevicesForList_newToken = new Holder<Integer>();
        final Holder<Boolean> _getMCUsNetworkDevicesForList_isReset = new Holder<Boolean>();
        final Holder<Boolean> _getMCUsNetworkDevicesForList_isDmaIntegrated = new Holder<Boolean>();
        final Holder<Boolean> _getMCUsNetworkDevicesForList_isDmaIntegratedAsPoolOrderSource = new Holder<Boolean>();
        final JWebResult _getMCUsNetworkDevicesForList__return = port
                .getMCUsNetworkDevicesForList(_getMCUsNetworkDevicesForList_credentials,
                                              _getMCUsNetworkDevicesForList_token,
                                              _getMCUsNetworkDevicesForList_modifiedList,
                                              _getMCUsNetworkDevicesForList_deletedList,
                                              _getMCUsNetworkDevicesForList_newToken,
                                              _getMCUsNetworkDevicesForList_isReset,
                                              _getMCUsNetworkDevicesForList_isDmaIntegrated,
                                              _getMCUsNetworkDevicesForList_isDmaIntegratedAsPoolOrderSource);
        System.out.println("getMCUsNetworkDevicesForList.result="
                + _getMCUsNetworkDevicesForList__return);
        System.out
                .println("getMCUsNetworkDevicesForList._getMCUsNetworkDevicesForList_modifiedList="
                        + _getMCUsNetworkDevicesForList_modifiedList.value);
        System.out
                .println("getMCUsNetworkDevicesForList._getMCUsNetworkDevicesForList_deletedList="
                        + _getMCUsNetworkDevicesForList_deletedList.value);
        System.out
                .println("getMCUsNetworkDevicesForList._getMCUsNetworkDevicesForList_newToken="
                        + _getMCUsNetworkDevicesForList_newToken.value);
        System.out
                .println("getMCUsNetworkDevicesForList._getMCUsNetworkDevicesForList_isReset="
                        + _getMCUsNetworkDevicesForList_isReset.value);
        System.out
                .println("getMCUsNetworkDevicesForList._getMCUsNetworkDevicesForList_isDmaIntegrated="
                        + _getMCUsNetworkDevicesForList_isDmaIntegrated.value);
        System.out
                .println("getMCUsNetworkDevicesForList._getMCUsNetworkDevicesForList_isDmaIntegratedAsPoolOrderSource="
                        + _getMCUsNetworkDevicesForList_isDmaIntegratedAsPoolOrderSource.value);
        return _getMCUsNetworkDevicesForList_modifiedList.value;
    }

    /**
     * Get the network devices list
     *
     * @param userToken
     * @return
     */
    public List<NetworkDeviceForList>
            getNetworkDevicesForList(final String userToken) {
        System.out.println("Invoking getNetworkDevicesForList...");
        logger.info("Invoking getNetworkDevicesForList...");
        final JCredentials _getNetworkDevicesForList_credentials = new JCredentials();
        _getNetworkDevicesForList_credentials.setUserToken(userToken);
        final int _getNetworkDevicesForList_token = -1;
        final Holder<List<NetworkDeviceForList>> _getNetworkDevicesForList_modifiedList = new Holder<List<NetworkDeviceForList>>();
        final Holder<List<String>> _getNetworkDevicesForList_deletedList = new Holder<List<String>>();
        final Holder<Integer> _getNetworkDevicesForList_newToken = new Holder<Integer>();
        final Holder<Boolean> _getNetworkDevicesForList_isReset = new Holder<Boolean>();
        final Holder<Boolean> _getNetworkDevicesForList_isDmaIntegrated = new Holder<Boolean>();
        final Holder<Boolean> _getNetworkDevicesForList_isDmaIntegratedAsPoolOrderSource = new Holder<Boolean>();
        final JWebResult _getNetworkDevicesForList__return = port
                .getNetworkDevicesForList(_getNetworkDevicesForList_credentials,
                                          _getNetworkDevicesForList_token,
                                          _getNetworkDevicesForList_modifiedList,
                                          _getNetworkDevicesForList_deletedList,
                                          _getNetworkDevicesForList_newToken,
                                          _getNetworkDevicesForList_isReset,
                                          _getNetworkDevicesForList_isDmaIntegrated,
                                          _getNetworkDevicesForList_isDmaIntegratedAsPoolOrderSource);
        System.out.println("getNetworkDevicesForList.result="
                + _getNetworkDevicesForList__return);
        System.out
                .println("getNetworkDevicesForList._getNetworkDevicesForList_modifiedList="
                        + _getNetworkDevicesForList_modifiedList.value);
        System.out
                .println("getNetworkDevicesForList._getNetworkDevicesForList_deletedList="
                        + _getNetworkDevicesForList_deletedList.value);
        System.out
                .println("getNetworkDevicesForList._getNetworkDevicesForList_newToken="
                        + _getNetworkDevicesForList_newToken.value);
        System.out
                .println("getNetworkDevicesForList._getNetworkDevicesForList_isReset="
                        + _getNetworkDevicesForList_isReset.value);
        System.out
                .println("getNetworkDevicesForList._getNetworkDevicesForList_isDmaIntegrated="
                        + _getNetworkDevicesForList_isDmaIntegrated.value);
        System.out
                .println("getNetworkDevicesForList._getNetworkDevicesForList_isDmaIntegratedAsPoolOrderSource="
                        + _getNetworkDevicesForList_isDmaIntegratedAsPoolOrderSource.value);
        return _getNetworkDevicesForList_modifiedList.value;
    }

    public JWebResult manualAddAccessServer(final String userToken,
                                            final String accessServerName,
                                            final String accessServerIP) {
        System.out.println("Invoking manualAddAccessServer...");
        logger.info("Invoking manualAddAccessServer...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.manualAddAccessServer(credentials,
                                          accessServerName,
                                          accessServerIP);
    }

    /**
     * Save the searched bridge just now
     *
     * @param userToken
     * @param bridgeDetails
     * @return
     */
    public JWebResult
            saveBridgeDevice(final String userToken,
                             final BridgeDeviceForDetails bridgeDetails) {
        System.out.println("Invoking saveBridgeDevice...");
        logger.info("Invoking saveBridgeDevice...");
        final JCredentials _saveBridgeDevice_credentials = new JCredentials();
        _saveBridgeDevice_credentials.setUserToken(userToken);
        final boolean _saveBridgeDevice_isAdd = true;
        final BridgeDeviceForDetails _saveBridgeDevice_detail = bridgeDetails;
        final boolean _saveBridgeDevice_forceRefresh = false;
        final JWebResult _saveBridgeDevice__return = port
                .saveBridgeDevice(_saveBridgeDevice_credentials,
                                  _saveBridgeDevice_isAdd,
                                  _saveBridgeDevice_detail,
                                  _saveBridgeDevice_forceRefresh);
        System.out.println("saveBridgeDevice.result="
                + _saveBridgeDevice__return);
        return _saveBridgeDevice__return;
    }

    public void
            setAvailableMCU(final List<NetworkDeviceForList> networkDeviceList) {
        this.networkDeviceList = networkDeviceList;
    }

    public void
            setAvailablePoolOrder(final List<NetworkDeviceForList> networkDeviceList4pool) {
        this.networkDeviceList4pool = networkDeviceList4pool;
    }
}
