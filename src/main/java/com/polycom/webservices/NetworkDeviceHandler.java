package com.polycom.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.NetworkDeviceManagerHandler;
import com.polycom.webservices.NetworkDeviceManager.BridgeDeviceForDetails;
import com.polycom.webservices.NetworkDeviceManager.JStatus;
import com.polycom.webservices.NetworkDeviceManager.JVBP;
import com.polycom.webservices.NetworkDeviceManager.JVBPModel;
import com.polycom.webservices.NetworkDeviceManager.JWebResult;
import com.polycom.webservices.NetworkDeviceManager.McuPoolOrderForDetails;
import com.polycom.webservices.NetworkDeviceManager.NetworkDeviceForList;

/**
 * Network Device handler. This class will handle the webservice request of
 * Network Device module
 *
 * @author wbchao
 *
 */
public class NetworkDeviceHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
         final String method = "getMcuNetworkDeviceSpecific ";
         final String auth =
         "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
         final String params = "mcuIp=10.220.202.213 keyword=deviceUUID ";
        // final String method = "getNetworkDeviceSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "networkDeviceIpAddress=1.1.1.1 keyword=deviceUUID ";
        // final String method = "addSbc ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // " sbcName=autoSbc lanIp=1.1.1.1 wanIp=1.1.1.2  ";
//        final String method = "getNetworkDeviceCount ";
//        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
//        final String params = "  ";
        final String command = "http://localhost:8888/PlcmRmWeb/JNetworkDeviceManager NetworkDeviceManager "
                + method + auth + params;
        final NetworkDeviceHandler handler = new NetworkDeviceHandler(command);
        final String result = handler.build();
        System.out.println("result==" + result);
    }

    NetworkDeviceManagerHandler ndmh;

    public NetworkDeviceHandler(final String cmd) throws IOException {
        super(cmd);
        ndmh = new NetworkDeviceManagerHandler(webServiceUrl);
    }

    /**
     * Add MCU bridge to XMA
     *
     * @see bridgeIPAddress=$mcuAddr <br/>
     *      bridgeUserID=$mcuUsr <br/>
     *      bridgeUserPWD=$mcuPWD
     *
     * @param bridgeIPAddress
     *            The MCU ip
     * @param bridgeUserID
     *            The MCU login username
     * @param bridgeUserPWD
     *            The MCU login password
     * @param bridgeDNSAddress
     *            The MCU DNS ip(Optional)
     * @param isdnCountryCode
     *            The MCU ISDN country code(Optional)
     * @param isdnAreaCode
     *            The MCU ISDN area code(Optional)
     * @return The result
     */
    public String addBridge() {
        String status = "Failed";
        final String bridgeIPAddress = getInputCmd().get("bridgeIPAddress");
        final String bridgeUserID = getInputCmd().get("bridgeUserID");
        final String bridgeUserPWD = getInputCmd().get("bridgeUserPWD");
        final String bridgeDNSAddress = getInputCmd().get("bridgeDNSAddress");
        final String isdnCountryCode = inputCmd.get("isdnCountryCode");
        final String isdnAreaCode = inputCmd.get("isdnAreaCode");
        if (!bridgeIPAddress.isEmpty() && !bridgeUserID.isEmpty()
                && !bridgeUserPWD.isEmpty()) {
            // Check the availability of the bridge. If is already in the XMA,
            // just skip adding operation
            final List<NetworkDeviceForList> deviceList = ndmh
                    .getNetworkDevicesForList(userToken);
            for (final NetworkDeviceForList networkDevice : deviceList) {
                if (networkDevice.getIpAddress().equals(bridgeIPAddress)) {
                    status = "Failed";
                    logger.error("Failed to add the specificed bridge since it is already in the XMA.");
                    return status
                            + " Failed to add the specificed bridge since it is already in the XMA.";
                }
            }
            final BridgeDeviceForDetails details = ndmh
                    .addBridgeDeviceAutoPopulate(userToken,
                                                 bridgeIPAddress,
                                                 bridgeDNSAddress,
                                                 bridgeUserID,
                                                 bridgeUserPWD);
            if (!isdnCountryCode.isEmpty()) {
                details.setIsdnCountryCode(isdnCountryCode);
                details.setIsdnAreaCode(isdnAreaCode);
            }
            final JWebResult result = ndmh.saveBridgeDevice(userToken, details);
            if (result.getStatus().compareTo(JStatus.SUCCESS) == 0) {
                status = "SUCCESS";
                logger.info("Bridge " + bridgeIPAddress
                        + " is successfully added into the XMA.");
            } else {
                status = "Failed";
                logger.error("Bridge " + bridgeIPAddress
                        + " is not successfully added into the XMA.");
                return status + " Bridge " + bridgeIPAddress
                        + " is not successfully added into the XMA.";
            }
        } else {
            status = "Failed";
            logger.error("Some parameters are missing in the input command. "
                    + "Please check your input command.");
            return status
                    + " Some parameters are missing in the input command.";
        }
        return status;
    }

    /**
     * Add RPAD to XMA
     *
     * @see rpadName=$rpadName <br/>
     *      rpadIp=$rpadIp
     * @param rpadName
     *            The RPAD name
     * @param rpadIp
     *            The RPAD ip
     * @return The result
     */
    public String addRpad() {
        final String rpadName = inputCmd.get("rpadName");
        final String rpadIp = inputCmd.get("rpadIp");
        final JWebResult result = ndmh.manualAddAccessServer(userToken,
                                                             rpadName,
                                                             rpadIp);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Add RPAD successfully");
            return "SUCCESS";
        } else {
            logger.error("Fail to add RPAD");
            return "Failed, fail to add RPAD, error status is "
            + result.getStatus();
        }
    }

    /**
     * Add SBC to XMA
     *
     * @see sbcName=$sbcName <br/>
     *      lanIp=$lanIp <br/>
     *      wanIp=$wanIp
     * @param sbcName
     *            The SBC name
     * @param lanIp
     *            The lan ip
     * @param wanIp
     *            The wan ip
     * @return The result
     */
    public String addSbc() {
        final JVBP sbcToAdd = new JVBP();
        final String sbcName = inputCmd.get("sbcName");
        final String lanIp = inputCmd.get("lanIp");
        final String wanIp = inputCmd.get("wanIp");
        sbcToAdd.setName(sbcName);
        sbcToAdd.setModel(JVBPModel.SBC);
        sbcToAdd.setLanIP4(lanIp);
        sbcToAdd.setWanIP4(wanIp);
        sbcToAdd.setVBPId(0);
        final JWebResult result = ndmh.addSBC(userToken, sbcToAdd);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Add SBC successfully");
            return "SUCCESS";
        } else {
            logger.error("Fail to add SBC");
            return "Failed, fail to add SBC, error status is "
            + result.getStatus();
        }
    }

    /**
     * Add VBP to XMA
     *
     * @see vbpName=$vbpName <br/>
     *      lanIp=$lanIp <br/>
     *      wanIp=$wanIp
     * @param vbpName
     *            The VBP name
     * @param lanIp
     *            The lan ip
     * @param wanIp
     *            The wan ip
     * @return The result
     */
    public String addVbp() {
        final JVBP sbcToAdd = new JVBP();
        final String sbcName = inputCmd.get("vbpName");
        final String lanIp = inputCmd.get("lanIp");
        final String wanIp = inputCmd.get("wanIp");
        sbcToAdd.setName(sbcName);
        sbcToAdd.setModel(JVBPModel.ST___SERIES);
        sbcToAdd.setLanIP4(lanIp);
        sbcToAdd.setWanIP4(wanIp);
        sbcToAdd.setVBPId(0);
        final JWebResult result = ndmh.addVBP(userToken, sbcToAdd);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Add VBP successfully");
            return "SUCCESS";
        } else {
            logger.error("Fail to add VBP");
            return "Failed, fail to add VBP, error status is "
            + result.getStatus();
        }
    }

    /**
     * Delete the MCU bridge
     *
     * @see bridgeIPAddress=$mcuAddr
     *
     * @param bridgeIPAddress
     *            The MCU ip
     * @return The result
     */
    public String deleteBridge() {
        String status = "Failed";
        final List<NetworkDeviceForList> deviceList = ndmh
                .getNetworkDevicesForList(userToken);
        for (final NetworkDeviceForList networkDevice : deviceList) {
            if (networkDevice.getIpAddress().equals(getInputCmd()
                    .get("bridgeIPAddress"))) {
                final List<String> uid = new LinkedList<String>();
                uid.add(networkDevice.getDeviceUUID());
                if (ndmh.deleteNetworkDevices(userToken, uid).getStatus()
                        .compareTo(JStatus.SUCCESS) == 0) {
                    status = "SUCCESS";
                    logger.info("Bridge "
                            + getInputCmd().get("bridgeIPAddress")
                            + " is successfully deleted from XMA.");
                } else {
                    status = "Failed";
                    logger.error("Bridge "
                            + getInputCmd().get("bridgeIPAddress")
                            + " is not successfully deleted from XMA.");
                    return status + " Bridge "
                            + getInputCmd().get("bridgeIPAddress")
                            + " is not successfully deleted from XMA.";
                }
            }
        }
        return status;
    }

    /**
     * Delete RPAD from XMA
     *
     * @see deviceIp=$deviceIp
     * @param deviceIp
     *            The device ip
     * @return The result
     */
    public String deleteRpad() {
        final String deviceIp = inputCmd.get("deviceIp");
        final NetworkDeviceForList device = getNetworkDeviceByIp(deviceIp);
        final List<String> deviceIds = new ArrayList<String>();
        deviceIds.add(device.getDeviceUUID());
        final JWebResult result = ndmh
                .deleteAccessServers(userToken, deviceIds);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Delete RPAD successfully");
            return "SUCCESS";
        } else {
            logger.error("Fail to delete RPAD");
            return "Failed, fail to delete RPAD, error status is "
            + result.getStatus();
        }
    }

    /**
     * Delete SBC from XMA
     *
     * @see deviceIp=$deviceIp
     * @param deviceIp
     *            The device ip
     * @return The result
     */
    public String deleteSbc() {
        final String deviceIp = inputCmd.get("deviceIp");
        final NetworkDeviceForList device = getNetworkDeviceByIp(deviceIp);
        final JWebResult result = ndmh.deleteSBC(userToken,
                                                 device.getDeviceUUID());
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Delete SBC successfully");
            return "SUCCESS";
        } else {
            logger.error("Fail to delete SBC");
            return "Failed, fail to delete SBC, error status is "
            + result.getStatus();
        }
    }

    /**
     * Delete VBP from XMA
     *
     * @see deviceIp=$deviceIp
     * @param deviceId
     *            The device ip
     * @return The result
     */
    public String deleteVbp() {
        final String deviceIp = inputCmd.get("deviceIp");
        final NetworkDeviceForList device = getNetworkDeviceByIp(deviceIp);
        final JWebResult result = ndmh.deleteSBC(userToken,
                                                 device.getDeviceUUID());
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Delete VBP successfully");
            return "SUCCESS";
        } else {
            logger.error("Fail to delete VBP");
            return "Failed, fail to delete VBP, error status is "
            + result.getStatus();
        }
    }

    /**
     * Get the DMA pool order specified attribute value
     *
     * @see keyword=name <br/>
     *      dmaPoolOrderName=$poolOrderNames($id)
     * @param keyword
     *            The attribute name
     * @param dmaPoolOrderName
     *            The DMA pool order name
     * @return The DMA pool order specified attribute value
     */
    public String getDmaPoolOrderSpecific() {
        final String result = "NotFound";
        String uuid = "";
        try {
            final List<NetworkDeviceForList> dmaPoolOrderList = ndmh
                    .getDmaPoolOrdersList(userToken);
            if (dmaPoolOrderList.isEmpty()) {
                logger.error("Cannot get the DMA pool order from the XMA.");
                return "Cannot get the DMA pool order from the XMA.";
            } else {
                for (final NetworkDeviceForList poolOrder : dmaPoolOrderList) {
                    if (poolOrder.getDeviceName().equals(inputCmd
                            .get("dmaPoolOrderName").replaceAll("~", " "))) {
                        uuid = poolOrder.getDeviceUUID();
                    }
                }
                final McuPoolOrderForDetails poolOrderDetails = ndmh
                        .getDmaPoolOrdersDetail(userToken, uuid);
                final String keyword = inputCmd.get("keyword");
                if (keyword.isEmpty()) {
                    return result + ", keyword is empty!";
                }
                return CommonUtils.invokeGetMethod(poolOrderDetails, keyword);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            logger.error("Failed to retrieve the specific detail information from the network device "
                    + inputCmd.get("dmaPoolOrderName")
                    + " with error message "
                    + e.getMessage());
            return "Failed to retrieve the specific detail information from the DMA pool order "
                    + inputCmd.get("dmaPoolOrderName")
                    + " with error message "
                    + e.getMessage();
        }
    }

    /**
     * Internal method, get the mcu NetworkDevice instance by specified ip
     *
     * @param ip
     *            The specified ip
     * @return The mcu NetworkDevice instance by specified ip
     */
    private NetworkDeviceForList getMcuNetworkDeviceByIp(final String ip) {
        NetworkDeviceForList device = null;
        final List<NetworkDeviceForList> networkDeviceList = ndmh
                .getMCUsNetworkDevicesForList(userToken);
        for (final NetworkDeviceForList networkDevice : networkDeviceList) {
            if (networkDevice.getIpAddress().equals(ip)) {
                device = networkDevice;
            }
        }
        return device;
    }

    /**
     * Get the mcu network device attribute value
     *
     * @see networkDeviceIpAddress=$ipAddress <br/>
     *      keyword=name
     * @param networkDeviceIpAddress
     *            The network device ip
     * @param keyword
     *            The attribute name
     * @return The mcu network device attribute value
     */
    public String getMcuNetworkDeviceSpecific() {
        final String result = "NotFound";
        final String mcuIp = inputCmd.get("mcuIp");
        final NetworkDeviceForList device = getMcuNetworkDeviceByIp(mcuIp);
        if (device == null) {
            logger.error("Cannot get the network device information from the XMA.");
            return "Cannot get the network device information from the XMA.";
        }
        final String keyword = inputCmd.get("keyword");
        if (keyword.isEmpty()) {
            return result + ", keyword is empty!";
        }
        return CommonUtils.invokeGetMethod(device, keyword);
    }

    /**
     * Internal method, get the NetworkDevice instance by specified ip
     *
     * @param ip
     *            The specified ip
     * @return The NetworkDevice instance by specified ip
     */
    private NetworkDeviceForList getNetworkDeviceByIp(final String ip) {
        NetworkDeviceForList device = null;
        final List<NetworkDeviceForList> networkDeviceList = ndmh
                .getNetworkDevicesForList(userToken);
        for (final NetworkDeviceForList networkDevice : networkDeviceList) {
            if (networkDevice.getIpAddress().equals(ip)) {
                device = networkDevice;
            }
        }
        return device;
    }

    /**
     * Get Network Device count with specified attribute value
     *
     * @see value=OFFLINE <br/>
     *      keyword=deviceStatus
     * @param keyword
     *            The attribute name
     * @param value
     *            The attribute value
     * @return The Network Device count with specified attribute value
     */
    public String getNetworkDeviceCount() {
        final String value = inputCmd.get("value");
        final String keyword = inputCmd.get("keyword");
        final List<NetworkDeviceForList> networkDeviceList;
        try {
        	networkDeviceList = ndmh
                    .getNetworkDevicesForList(userToken);
        }  catch (final Exception e) {
			e.printStackTrace();
			return "Failed, got exception when get network device list. Error msg is "
					+ e.getMessage();
		}
        int count = 0;
        for (final NetworkDeviceForList networkDevice : networkDeviceList) {
            if (networkDevice.getBelongsToAreaUgpId() == -1) {
                continue;
            }
            if (keyword.isEmpty()) {
                count++;
                continue;
            }
            if (value.equalsIgnoreCase(CommonUtils
                    .invokeGetMethod(networkDevice, keyword))) {
                count++;
            }
        }
        return count + "";
    }

    /**
     * Get the network device attribute value
     *
     * @see networkDeviceIpAddress=$ipAddress <br/>
     *      keyword=name
     * @param networkDeviceIpAddress
     *            The network device ip
     * @param keyword
     *            The attribute name
     * @return The network device attribute value
     */
    public String getNetworkDeviceSpecific() {
        final String result = "NotFound";
        final NetworkDeviceForList device = getNetworkDeviceByIp(inputCmd
                .get("networkDeviceIpAddress"));
        if (device == null) {
            logger.error("Cannot get the network device information from the XMA.");
            return "Cannot get the network device information from the XMA.";
        }
        final String keyword = inputCmd.get("keyword");
        if (keyword.isEmpty()) {
            return result + ", keyword is empty!";
        }
        return CommonUtils.invokeGetMethod(device, keyword);
    }

    @Override
    protected void injectCmdArgs() {
        // Bridge IP address
        put("bridgeIPAddress", "");
        // Bridge DNS address
        put("bridgeDNSAddress", "");
        // The bridge user ID
        put("bridgeUserID", "");
        // The bridge user password
        put("bridgeUserPWD", "");
        put("isdnCountryCode", "");
        put("isdnAreaCode", "");
        put("dmaPoolOrderName", "");
        put("networkDeviceIpAddress", "");
        put("mcuIp", "");
        put("sbcName", "");
        put("vbpName", "");
        put("rpadName", "");
        put("lanIp", "");
        put("wanIp", "");
        put("rpadIp", "");
        put("deviceIp", "");
    }
}
