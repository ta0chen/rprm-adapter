package com.polycom.webservices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.utils.IPAddress;
import com.polycom.sqa.xma.webservices.driver.DeviceManagerHandler;
import com.polycom.sqa.xma.webservices.driver.DeviceMetaManagerHandler;
import com.polycom.webservices.DeviceManager.JDeviceAlias;
import com.polycom.webservices.DeviceManager.JDeviceAliasType;
import com.polycom.webservices.DeviceManager.JDeviceModelVO;
import com.polycom.webservices.DeviceManager.JEndpointForDetails;
import com.polycom.webservices.DeviceManager.JEndpointForList;
import com.polycom.webservices.DeviceManager.JQueryCondition;
import com.polycom.webservices.DeviceManager.JStatus;
import com.polycom.webservices.DeviceManager.JWebResult;
import com.polycom.webservices.DeviceManager.Operator;

/**
 * Device handler. This class will handle the webservice request of Device
 * module
 *
 * @author wbchao
 *
 */
public class DeviceHandler extends XMAWebServiceHandler {
    protected static int       passNum               = 0;
    protected static int       failNum               = 0;
    private static final int[] CISCO_DATABASE_VALUES = new int[] {
        32,
        38,
        39,
        58,
        59 };
    private static final int[] GS_DATABASE_VALUES    = new int[] {
        46,
        47,
        48,
        52,
        60 };
    private static final int[] HDX_DATABASE_VALUES   = new int[] {
        20,
        21,
        40,
        22,
        23,
        24,
        25,
        26,
        27,
        28,
        29 };
    private static final int[] ITP_DATABASE_VALUES   = new int[] { 53, 61 };
    private static final int[] DEBUT_DATABASE_VALUES = new int[] { 62 };

    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "addDMA ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "deviceName=lingxi description=automation ipAddress=10.220.202.156
        // httpPort=8443 dmaUsername=admin dmaPassword=admin
        // percentPortAllocation=100 mcuPoolOrderSource=true callServer=true
        // supportDmaFailOver=true ";
        // final String method = "deleteDMA ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "deviceName=aaa ipAddress=10.220.202.156 ";
        final String method = "getEndpointSpecificDetail ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params1 = "deviceName=hhhdebuguser8RP-Desktop    keyword=deviceId ";
        // final String method = "getEndpointSpecificDetail ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params1 = "keyword=[vc2Device=true]:serialNumber
        // deviceType=GroupSeries ";
        // final String method = "getEndpointCount ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "keyword=source value=SYNC___FROM___DMA ";
        // final String method = "getDeviceForDetails ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "keyword=rabbitEyeTrackingMode deviceRole=0 deviceId=58 ";
        // final String method = "addEndpointsAsyncMode ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "ipAddress=1.1.1.1 totalNum=8 deviceType=HDX deviceName=bogus ";
        // final String method = "updateEndpoint ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "ipAddress=172.21.98.194 deviceRole=0 deviceId=22
        // keyword=Description(aaa) ";
        // final String method = "autoDetectDevices ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "startIp=172.21.112.83 endIp=172.21.112.85 ";
        // final String method = "getDevicesForSUSerialNumList ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params1 = "keyword=SerialNumber isVc2=true
        // deviceModel=ITP ";
        // final String method = "checkEndpointInfo ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "deviceType=Sound~Station ipAddress=172.21.98.146 status=online
        // mode=AUDIO___ENDPOINT ";
        // final String method = "deleteEndpointWithDialString ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "deviceType=RP-Desktop ipAddress=10.230.110.213 ";
        // final String method = "disableCommonPassword ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "deviceLoginName=admin devicePassword=1234 ";
        // final String method = "resetPhoneDigestPassword ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "deviceName=PLCM0004f2f801ca digestPassword=PLCMrprm12345678 ";
        final String command = "https://172.21.120.181:8443/PlcmRmWeb/rest/JDeviceManager DeviceManager "
                + method + auth + params1;
        final DeviceHandler handler = new DeviceHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    protected final DeviceManagerHandler deviceManagerHandler;

    public DeviceHandler(final String cmd) throws IOException {
        super(cmd);
        deviceManagerHandler = new DeviceManagerHandler(webServiceUrl);
    }

    /**
     * Add device endpoint
     *
     * @see deviceName=$deviceName<br/>
     *      ipAddress=$ciscoAddr<br/>
     *      deviceType=Tandberg<br/>
     *      deviceLoginName=$deviceLoginName
     *
     * @param deviceLoginName
     *            The login username of device(Optional)
     * @param deviceLoginName
     *            The login password of device(Optional)
     * @param ipAddress
     *            The device ip
     * @param deviceType
     *            The device type
     * @param deviceName
     *            The device name
     *
     * @return The result
     */
    public String addEndpoint() {
        String status = "Failed";
        final String deviceLoginName = inputCmd.get("deviceLoginName");
        final String devicePassword = inputCmd.get("devicePassword");
        try {
            final DeviceMetaManagerHandler dmmh = new DeviceMetaManagerHandler(
                                                                               webServiceUrl);
            final JEndpointForDetails epDetails = deviceManagerHandler
                    .addDeviceAutoPopulate(userToken,
                                           inputCmd.get("ipAddress"),
                                           deviceLoginName,
                                           devicePassword,
                                           inputCmd.get("deviceType"),
                                           dmmh);
            epDetails.setDeviceName(inputCmd.get("deviceName"));
            // Token timeout, retrieve again
            userToken = umh.userLogin(inputCmd.get("username"),
                                      inputCmd.get("domain"),
                                      inputCmd.get("password"));
            if (deviceManagerHandler.saveDevice(userToken, epDetails, true)
                    .getStatus()
                    .compareTo(com.polycom.webservices.DeviceManager.JStatus.SUCCESS) == 0) {
                logger.info("Endpoint " + inputCmd.get("deviceName")
                            + " added successfully");
                status = "SUCCESS";
            } else {
                logger.error("Endpoint " + inputCmd.get("deviceName")
                             + " added failed,  please make sure your endpoint information is correct");
                status = "Failed";
            }
        } catch (final Exception e) {
            e.printStackTrace();
            status = "Failed, got exception when add endpoint. Error msg is "
                    + e.getMessage();
        }
        return status;
    }

    /**
     * Add device endpoint circularly according to the beginning IP address and
     * the total number. This operation will be terminated by the expected
     * number.
     *
     * @see deviceName=$deviceName<br/>
     *      ipAddress=$ciscoAddr<br/>
     *      deviceType=Tandberg<br/>
     *      deviceLoginName=$deviceLoginName
     *
     * @param deviceLoginName
     *            The login username of device(Optional)
     * @param deviceLoginName
     *            The login password of device(Optional)
     * @param ipAddress
     *            The beginning device ip
     * @param deviceType
     *            The device type
     * @param deviceName
     *            The beginning device name
     * @param totalNum
     *            The total number of the endpoint need to add into the RPRM
     * @return The result
     */
    public String addEndpointCircularly() {
        String status = "Failed";
        passNum = 0;
        failNum = 0;
        final String deviceLoginName = inputCmd.get("deviceLoginName");
        final String devicePassword = inputCmd.get("devicePassword");
        final DeviceMetaManagerHandler dmmh = new DeviceMetaManagerHandler(
                                                                           webServiceUrl);
        if (CommonUtils.validateIpAddress(inputCmd.get("ipAddress"))) {
            IPAddress ip = new IPAddress(inputCmd.get("ipAddress"));
            for (int i = 0; i < Integer
                    .valueOf(inputCmd.get("totalNum")); i++) {
                logger.info("Adding the endpoint with IP address: "
                        + ip.toString());
                final JEndpointForDetails epDetails = deviceManagerHandler
                        .addDeviceAutoPopulate(userToken,
                                               ip.toString(),
                                               deviceLoginName,
                                               devicePassword,
                                               inputCmd.get("deviceType"),
                                               dmmh);
                epDetails.setDeviceName(inputCmd.get("deviceName") + i);
                if (deviceManagerHandler.saveDevice(userToken, epDetails, true)
                        .getStatus()
                        .compareTo(com.polycom.webservices.DeviceManager.JStatus.SUCCESS) == 0) {
                    logger.info("Endpoint " + inputCmd.get("deviceName") + i
                                + " with IP address " + ip.toString()
                                + " added successfully");
                    passNum++;
                    // The operation will be interrupted by the expected number
                    if (passNum >= Integer.valueOf(inputCmd.get("expectNum"))) {
                        logger.info("The successfully added endpoint number is as the expected: "
                                + inputCmd.get("expectNum"));
                        status = "SUCCESS";
                        break;
                    }
                } else {
                    logger.error("Endpoint " + inputCmd.get("deviceName") + i
                                 + " with IP address " + ip.toString()
                                 + " added failed,  please make sure your endpoint information is correct");
                    failNum++;
                }
                ip = ip.next();
                try {
                    Thread.sleep(1000);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                    logger.error("Got interrupted exception...");
                }
            }
        } else {
            logger.error("Please input a validate IP address.");
            return "Failed, please input a validate IP address.";
        }
        logger.info("Successfully launch the thread...");
        return status;
    }

    /**
     * Auto detect devices in the ip range
     *
     * @param startIp
     *            The start ip point
     * @param endIp
     *            The end ip point
     * @return The result
     */
    public String autoDetectDevices() {
        String status = "Failed";
        final String startIp = inputCmd.get("startIp");
        final String endIp = inputCmd.get("endIp");
        final JWebResult result = deviceManagerHandler
                .autoDetectDevices(userToken, startIp, endIp);
        if (result.getStatus()
                .equals(com.polycom.webservices.DeviceManager.JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Auto detect EP between " + startIp + " and " + endIp
                        + " successfully");
        } else {
            status = "Failed";
            logger.error("Auto detect EP between " + startIp + " and " + endIp
                         + " failed");
            return status + "auto detect EP between " + startIp + " and "
            + endIp;
        }
        return status;
    }

    /**
     * Validate the endpoint info
     *
     * @see deviceType=RP-Desktop<br/>
     *      ipAddress=$rpd3Addr<br/>
     *      e164=9336
     *
     * @param deviceType
     *            The device type
     * @param ipAddress
     *            The device ip
     * @param h323ID
     *            The expected H323 id(Optional)
     * @param sysName
     *            The expected system name(Optional)
     * @param sipUri
     *            The expected sip uri(Optional)
     * @param e164
     *            The expected number(Optional)
     * @param mode
     *            The expected mode(Optional)
     * @param status
     *            The expected status(Optional)
     *
     * @return The result
     */
    public String checkEndpointInfo() {
        final List<JEndpointForList> toCheckEndpointList = new ArrayList<JEndpointForList>();
        String comments = "";
        boolean checkResult = false;
        boolean deviceFound = false;
        final List<JEndpointForList> endpointList = deviceManagerHandler
                .getEndpoints4Paging(userToken, 100);
        for (final JEndpointForList endpoint : endpointList) {
            System.out.println("display name: "
                    + endpoint.getDeviceType().getDisplayName());
            if ((endpoint.getDeviceType().getDisplayName()
                    .equals(inputCmd.get("deviceType").replace('~', ' ')))
                    && (endpoint.getIpAddress()
                            .equals(inputCmd.get("ipAddress")))
                            && !(endpoint.getSerialNumber().isEmpty())) {
                deviceFound = true;
                toCheckEndpointList.add(endpoint);
            }
        }
        if (deviceFound == false) {
            logger.error("did not find device with type: "
                    + inputCmd.get("deviceType") + " and ip: "
                    + inputCmd.get("ipAddress"));
            return "Failed, did not find device with type: "
            + inputCmd.get("deviceType") + " and ip: "
            + inputCmd.get("ipAddress");
        }
        // check h323 id
        if (!inputCmd.get("h323ID").isEmpty()) {
            final List<Integer> toDeleteEndpointIndexList = new ArrayList<Integer>();
            int i = 0;
            for (final JEndpointForList toCheckEndpoint : toCheckEndpointList) {
                checkResult = false;
                final List<JDeviceAlias> endpointAliasList = toCheckEndpoint
                        .getAliasList();
                for (final JDeviceAlias alias : endpointAliasList) {
                    logger.info("alias type: " + alias.getType().value());
                    logger.info("expect h323 id: "
                            + inputCmd.get("h323ID").replace('~', ' '));
                    logger.info("actual h323 id: " + alias.getValue());
                    if ("H323".equals(alias.getType().value())) {
                        if (inputCmd.get("h323ID").replace('~', ' ')
                                .equals(alias.getValue())) {
                            logger.info("check h323 id succeed, alias is: "
                                    + inputCmd.get("h323ID"));
                            checkResult = true;
                            break;
                        } else {
                            logger.error("check h323 id failed, expect alias is: "
                                    + inputCmd.get("h323ID")
                                    + ", but actual alias is: "
                                    + alias.getValue());
                            comments = comments
                                    .concat("check h323 id failed, expect alias is: "
                                            + inputCmd.get("h323ID")
                                            + ", but actual alias is: "
                                            + alias.getValue());
                        }
                    }
                }
                if (checkResult == false) {
                    toDeleteEndpointIndexList.add(new Integer(i));
                }
                i++;
            }
            if (!toDeleteEndpointIndexList.isEmpty()) {
                int deletedNumber = 0;
                for (final Integer index : toDeleteEndpointIndexList) {
                    toCheckEndpointList
                    .remove(index.intValue() - deletedNumber);
                    deletedNumber++;
                }
            }
            if (toCheckEndpointList.isEmpty()) {
                logger.error("check h323 id failed");
                return "Failed, " + comments;
            }
        }
        // check system name
        if (!inputCmd.get("sysName").isEmpty()) {
            final List<Integer> toDeleteEndpointIndexList = new ArrayList<Integer>();
            int i = 0;
            for (final JEndpointForList toCheckEndpoint : toCheckEndpointList) {
                if (inputCmd.get("sysName").replace('~', ' ')
                        .equals(toCheckEndpoint.getDeviceName())) {
                    logger.info("check system name succeed, system name is: "
                            + inputCmd.get("sysName"));
                } else {
                    logger.error("check system name failed, expect name is: "
                            + inputCmd.get("sysName") + ", but actual name is: "
                            + toCheckEndpoint.getDeviceName());
                    comments = comments
                            .concat("check system name failed, expect name is: "
                                    + inputCmd.get("sysName")
                                    + ", but actual name is: "
                                    + toCheckEndpoint.getDeviceName());
                    toDeleteEndpointIndexList.add(new Integer(i));
                }
                i++;
            }
            if (!toDeleteEndpointIndexList.isEmpty()) {
                int deletedNumber = 0;
                for (final Integer index : toDeleteEndpointIndexList) {
                    toCheckEndpointList
                    .remove(index.intValue() - deletedNumber);
                    deletedNumber++;
                }
            }
            if (toCheckEndpointList.isEmpty()) {
                logger.error("check system name failed");
                return "Failed, " + comments;
            }
        }
        // check sip uri
        final String sipUri = inputCmd.get("sipUri");
        if (!sipUri.isEmpty()) {
            final List<Integer> toDeleteEndpointIndexList = new ArrayList<Integer>();
            int i = 0;
            for (final JEndpointForList toCheckEndpoint : toCheckEndpointList) {
                if (sipUri.equals(toCheckEndpoint.getSipUri())) {
                    logger.info("check sip uri succeed, sip uri is: " + sipUri);
                } else if (sipUri.equalsIgnoreCase("NotFound")
                        && toCheckEndpoint.getSipUri().isEmpty()) {
                    logger.info("check sip uri succeed, sip uri is: " + sipUri);
                } else {
                    logger.error("check sip uri failed, expect name is: "
                            + sipUri + ", but actual sip uri is: "
                            + toCheckEndpoint.getSipUri());
                    comments = comments
                            .concat("check sip uri failed, expect sip uri is: "
                                    + sipUri + ", but actual sip uri is: "
                                    + toCheckEndpoint.getSipUri());
                    toDeleteEndpointIndexList.add(new Integer(i));
                }
                i++;
            }
            if (!toDeleteEndpointIndexList.isEmpty()) {
                int deletedNumber = 0;
                for (final Integer index : toDeleteEndpointIndexList) {
                    toCheckEndpointList
                    .remove(index.intValue() - deletedNumber);
                    deletedNumber++;
                }
            }
            if (toCheckEndpointList.isEmpty()) {
                logger.error("check sip uri failed");
                return "Failed, " + comments;
            }
        }
        // check E164
        if (!inputCmd.get("e164").isEmpty()) {
            final List<Integer> toDeleteEndpointIndexList = new ArrayList<Integer>();
            int i = 0;
            for (final JEndpointForList toCheckEndpoint : toCheckEndpointList) {
                checkResult = false;
                final List<JDeviceAlias> endpointAliasList = toCheckEndpoint
                        .getAliasList();
                for (final JDeviceAlias alias : endpointAliasList) {
                    logger.info("alias type: " + alias.getType().value());
                    logger.info("expect e164: "
                            + inputCmd.get("e164").replace('~', ' '));
                    logger.info("actual e164: " + alias.getValue());
                    if ("E164".equals(alias.getType().value())) {
                        if (inputCmd.get("e164").replace('~', ' ')
                                .equals(alias.getValue())) {
                            logger.info("check e164 succeed, alias is: "
                                    + inputCmd.get("e164"));
                            checkResult = true;
                            break;
                        } else {
                            logger.error("check e164 failed, expect alias is: "
                                    + inputCmd.get("e164")
                                    + ", but actual alias is: "
                                    + alias.getValue());
                            comments = comments
                                    .concat("check e164 failed, expect alias is: "
                                            + inputCmd.get("e164")
                                            + ", but actual alias is: "
                                            + alias.getValue());
                        }
                    }
                }
                if (checkResult == false) {
                    toDeleteEndpointIndexList.add(new Integer(i));
                }
                i++;
            }
            if (!toDeleteEndpointIndexList.isEmpty()) {
                int deletedNumber = 0;
                for (final Integer index : toDeleteEndpointIndexList) {
                    toCheckEndpointList
                    .remove(index.intValue() - deletedNumber);
                    deletedNumber++;
                }
            }
            if (toCheckEndpointList.isEmpty()) {
                logger.error("check e164 failed");
                return "Failed, " + comments;
            }
        }
        // check mode
        final String mode = inputCmd.get("mode");
        if (!mode.isEmpty()) {
            final List<Integer> toDeleteEndpointIndexList = new ArrayList<Integer>();
            int i = 0;
            for (final JEndpointForList toCheckEndpoint : toCheckEndpointList) {
                if (mode.equalsIgnoreCase(toCheckEndpoint.getSource()
                                          .toString())) {
                    logger.info("check mode succeed, it is: " + mode);
                } else {
                    logger.error("check mode failed, expect name is: " + mode
                                 + ", but actual mode is: "
                                 + toCheckEndpoint.getSource().toString());
                    comments = comments
                            .concat("check mode failed, expect mode is: " + mode
                                    + ", but actual mode is: "
                                    + toCheckEndpoint.getSource());
                    toDeleteEndpointIndexList.add(new Integer(i));
                }
                i++;
            }
            if (!toDeleteEndpointIndexList.isEmpty()) {
                int deletedNumber = 0;
                for (final Integer index : toDeleteEndpointIndexList) {
                    toCheckEndpointList
                    .remove(index.intValue() - deletedNumber);
                    deletedNumber++;
                }
            }
            if (toCheckEndpointList.isEmpty()) {
                logger.error("check mode failed");
                return "Failed, " + comments;
            }
        }
        // check status
        final String status = inputCmd.get("status");
        if (!status.isEmpty()) {
            final List<Integer> toDeleteEndpointIndexList = new ArrayList<Integer>();
            int i = 0;
            for (final JEndpointForList toCheckEndpoint : toCheckEndpointList) {
                if (status.equalsIgnoreCase(toCheckEndpoint.getDeviceStatus()
                                            .toString())) {
                    logger.info("check status succeed, it is: " + status);
                } else {
                    logger.error("check status failed, expect status is: "
                            + status + ", but actual status is: "
                            + toCheckEndpoint.getDeviceStatus().toString());
                    comments = comments
                            .concat("check status failed, expect status is: "
                                    + status + ", but actual status is: "
                                    + toCheckEndpoint.getDeviceStatus()
                                    .toString());
                    toDeleteEndpointIndexList.add(new Integer(i));
                }
                i++;
            }
            if (!toDeleteEndpointIndexList.isEmpty()) {
                int deletedNumber = 0;
                for (final Integer index : toDeleteEndpointIndexList) {
                    toCheckEndpointList
                    .remove(index.intValue() - deletedNumber);
                    deletedNumber++;
                }
            }
            if (toCheckEndpointList.isEmpty()) {
                logger.error("check status failed");
                return "Failed, " + comments;
            }
        }
        return "SUCCESS";
    }

    /**
     * Delete the device
     *
     * @see deviceName=$deviceName <br/>
     *      ipAddress=$ciscoAddr
     * @param deviceName
     *            The device name
     * @param ipAddress
     *            The device ip
     * @return The result
     */
    public String deleteEndpoint() {
        String status = "Failed";
        final List<JEndpointForList> endpointList = deviceManagerHandler
                .getEndpoints4Paging(userToken, 100);
        final List<Integer> toDeleteEndpointIdList = new ArrayList<Integer>();
        final String ipAddress = inputCmd.get("ipAddress");
        final String deviceName = inputCmd.get("deviceName");
        for (final JEndpointForList endpoint : endpointList) {
            if (ipAddress.equals(endpoint.getIpAddress())
                    || deviceName.equals(endpoint.getDeviceName())) {
                toDeleteEndpointIdList.add(endpoint.getDeviceId());
            }
        }
        if (toDeleteEndpointIdList.isEmpty()) {
            logger.info("Endpoint " + deviceName + ipAddress
                        + " was not found in XMA.");
            return "SUCCESS, device was not found in xma";
        }
        final JWebResult result = deviceManagerHandler
                .deleteDevices(userToken, toDeleteEndpointIdList);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Endpoint " + ipAddress
                        + " is successfully deleted from XMA.");
        } else {
            status = "Failed";
            logger.error("Endpoint " + ipAddress
                         + " is not successfully deleted from XMA.");
            return status + " Endpoint " + ipAddress
                    + " is not successfully deleted from XMA.";
        }
        return status;
    }

    /**
     * Delete the endpoint circularly according to the beginning IP address
     *
     * @see deviceName=$deviceName <br/>
     *      ipAddress=$ciscoAddr
     * @param deviceName
     *            The device name
     * @param ipAddress
     *            The device ip
     * @return The result
     */
    public String deleteEndpointCircularly() {
        String status = "Failed";
        final List<JEndpointForList> endpointList = deviceManagerHandler
                .getEndpoints4Paging(userToken, 2000);
        final List<Integer> toDeleteEndpointIdList = new ArrayList<Integer>();
        if (CommonUtils.validateIpAddress(inputCmd.get("ipAddress"))) {
            IPAddress ip = new IPAddress(inputCmd.get("ipAddress"));
            for (int i = 0; i < Integer
                    .valueOf(inputCmd.get("totalNum")); i++) {
                for (final JEndpointForList endpoint : endpointList) {
                    if ((inputCmd.get("deviceName") + i)
                            .equals(endpoint.getDeviceName())
                            && ip.toString().equals(endpoint.getIpAddress())) {
                        toDeleteEndpointIdList
                        .add(new Integer(endpoint.getDeviceId()));
                    }
                }
                ip = ip.next();
            }
            if (deviceManagerHandler
                    .deleteDevices(userToken, toDeleteEndpointIdList)
                    .getStatus()
                    .compareTo(com.polycom.webservices.DeviceManager.JStatus.SUCCESS) == 0) {
                status = "SUCCESS";
                logger.info("Endpoints are successfully deleted from XMA.");
            } else {
                status = "Failed";
                logger.error("Endpoints are not successfully deleted from XMA.");
                return status
                        + " Endpoints are not successfully deleted from XMA.";
            }
        } else {
            logger.error("Please input a validate IP address.");
            return "Failed, please input a validate IP address.";
        }
        return status;
    }

    /**
     * Delete the device with dial string
     *
     * @see ipAddress=$hdx1Addr deviceType=HDX
     * @param deviceName
     *            The device name(Optional)
     * @param deviceType
     *            The device type
     * @param ipAddress
     *            The device ip
     * @return The result
     */
    public String deleteEndpointWithDialString() {
        final List<JEndpointForList> endpointList = deviceManagerHandler
                .getEndpoints4Paging(userToken, 100);
        final List<Integer> toDeleteEndpointIdList = new ArrayList<Integer>();
        for (final JEndpointForList endpoint : endpointList) {
            if (inputCmd.get("deviceType")
                    .equals(endpoint.getDeviceType().getDisplayName())
                    && inputCmd.get("ipAddress")
                    .equals(endpoint.getIpAddress())) {
                toDeleteEndpointIdList.add(new Integer(endpoint.getDeviceId()));
            }
        }
        if (toDeleteEndpointIdList.size() >= 1) {
            if (deviceManagerHandler
                    .deleteDevicesWithAlias(userToken, toDeleteEndpointIdList)
                    .getStatus()
                    .compareTo(com.polycom.webservices.DeviceManager.JStatus.SUCCESS) == 0) {
                logger.info("Endpoint " + inputCmd.get("deviceName")
                            + " with dial string is successfully deleted from XMA.");
                return "SUCCESS";
            } else {
                logger.error("Endpoint " + inputCmd.get("deviceName")
                             + " with dial string is not successfully deleted from XMA.");
                return "Failed, Endpoint " + inputCmd.get("deviceName")
                        + " with dial string is not successfully deleted from XMA.";
            }
        } else {
            logger.info("SUCCESS, no endpoint with type "
                    + inputCmd.get("deviceType") + " address "
                    + inputCmd.get("ipAddress") + " exist");
            return "SUCCESS";
        }
    }

    /**
     * Disable the common password
     *
     * @see No param
     *
     * @return The result
     */
    public String disableCommonPassword() {
        String status = "Failed";
        final JWebResult result = deviceManagerHandler
                .disableCommonPassword(userToken);
        if (result.getStatus()
                .equals(com.polycom.webservices.DeviceManager.JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("XMA disable common password successfully.");
        } else {
            status = "Failed";
            logger.error("XMA disable common password failed");
            return status + " set XMA common password.";
        }
        return status;
    }

    /**
     * Get the attribute of peripheral device details
     *
     * @see deviceId=$deviceId <br/>
     *      keyword=DeviceName
     * @param deviceId
     *            The device uid
     * @param keyword
     *            The device details attribute name
     * @return The peripheral device details attribute value
     */
    public String getDeviceDataForPeripheralsSpecific() {
        final int deviceId = Integer.parseInt(inputCmd.get("deviceId"));
        final String keyword = inputCmd.get("keyword");
        final List<JEndpointForDetails> peripherals = deviceManagerHandler
                .getDeviceDataForPeripherals(userToken, deviceId);
        if (peripherals == null || peripherals.isEmpty()) {
            return "DISCONNECTED";
        }
        final JEndpointForDetails endpoint = peripherals.get(0);
        return CommonUtils.invokeGetMethod(endpoint, keyword);
    }

    /**
     * Get the attribute of device details
     *
     * @see deviceRole=$deviceRole <br/>
     *      deviceId=$deviceId <br/>
     *      keyword=DeviceName
     * @param deviceRole
     *            The device role(Optional, default is 0)
     * @param deviceId
     *            The device uid
     * @param keyword
     *            The device details attribute name
     * @return The device details attribute value
     */
    public String getDeviceForDetails() {
        final int deviceRole = Integer.parseInt(inputCmd.get("deviceRole"));
        final int deviceId = Integer.parseInt(inputCmd.get("deviceId"));
        final String keyword = inputCmd.get("keyword");
        final JEndpointForDetails endpoint = deviceManagerHandler
                .getDeviceForDetails(userToken, deviceRole, deviceId);
        return CommonUtils.invokeGetMethod(endpoint, keyword);
    }

    /**
     * Get the device attribute value from SU serial numlist
     *
     * @see ipAddress=$ciscoAddr <br/>
     *      deviceModel=$deviceModel <br/>
     *      isVc2=false <br/>
     *      keyword=SerialNumber
     * @param isVc2
     *            Boolean value, is vc2 device
     * @param ipAddress
     *            The device ip
     * @param deviceModel
     *            The device model
     * @param keyword
     *            The device attribute name
     * @return The device attribute value from SU serial numlist
     */
    public String getDevicesForSUSerialNumList() {
        final String result = "NotFound";
        final boolean isVc2 = Boolean.parseBoolean(inputCmd.get("isVc2"));
        final String ipAddress = inputCmd.get("ipAddress");
        final String keyword = inputCmd.get("keyword");
        final String deviceModel = inputCmd.get("deviceModel");
        final List<JDeviceModelVO> models = new ArrayList<JDeviceModelVO>();
        int[] databaseValues = new int[] {};
        if (deviceModel.equalsIgnoreCase("cisco")) {
            databaseValues = CISCO_DATABASE_VALUES;
        } else if (deviceModel.equalsIgnoreCase("GroupSeries")) {
            databaseValues = GS_DATABASE_VALUES;
        } else if (deviceModel.equalsIgnoreCase("HDX")) {
            databaseValues = HDX_DATABASE_VALUES;
        } else if (deviceModel.equalsIgnoreCase("ITP")) {
            databaseValues = ITP_DATABASE_VALUES;
        } else if (deviceModel.equalsIgnoreCase("RP-Debut")){
            databaseValues = DEBUT_DATABASE_VALUES;
        }
        for (final int databaseValue : databaseValues) {
            final JDeviceModelVO vo = new JDeviceModelVO();
            vo.setDatabaseValue(databaseValue);
            models.add(vo);
        }
        final List<JEndpointForList> endpoints = deviceManagerHandler
                .getDevicesForSUSerialNumList(userToken, isVc2, models);
        if (ipAddress.isEmpty()) {
            return CommonUtils.invokeGetMethod(endpoints, keyword);
        }
        for (final JEndpointForList endpoint : endpoints) {
            if (endpoint.getIpAddress().equals(ipAddress)) {
                return CommonUtils.invokeGetMethod(endpoint, keyword);
            }
        }
        return result;
    }

    /**
     * Get endpoint count with specified attribute value
     *
     * @see value=SYNC___FROM___DMA <br/>
     *      keyword=source
     * @param keyword
     *            The attribute name
     * @param value
     *            The attribute value
     * @return The endpoint count with specified attribute value
     */
    public String getEndpointCount() {
        final String value = inputCmd.get("value");
        final String keyword = inputCmd.get("keyword");
        final List<JEndpointForList> endpointList = deviceManagerHandler
                .getEndpoints4Paging(userToken, 100);
        if (keyword.isEmpty()) {
            return endpointList.size() + "";
        }
        int count = 0;
        for (final JEndpointForList endpoint : endpointList) {
            if (value.equalsIgnoreCase(CommonUtils.invokeGetMethod(endpoint,
                                                                   keyword))) {
                count++;
            }
        }
        return count + "";
    }

    /**
     * Get the device h323 info
     *
     * @see ipAddress=$hdxAddr <br/>
     *      keyword=E164
     * @param ipAddress
     *            The device ip
     * @param keyword
     *            H323 or E164
     * @return H323 id or E164 number
     */
    public String getEndpointH323Info() {
        final String result = "NotFound";
        final String ipAddress = inputCmd.get("ipAddress");
        final String keyword = inputCmd.get("keyword");
        final List<JEndpointForList> endpointList = deviceManagerHandler
                .getEndpoints4Paging(userToken, 100);
        try {
            for (final JEndpointForList endpoint : endpointList) {
                if (ipAddress.equals(endpoint.getIpAddress())
                        && !endpoint.getSerialNumber().isEmpty()) {
                    for (final JDeviceAlias da : endpoint.getAliasList()) {
                        if (da.getType().equals(JDeviceAliasType.E_164)
                                && keyword.equalsIgnoreCase("E164")) {
                            return da.getValue();
                        } else if (da.getType().equals(JDeviceAliasType.H_323)
                                && keyword.equalsIgnoreCase("H323")) {
                            return da.getValue();
                        }
                    }
                }
            }
        } catch (final Exception e) {
            logger.equals("No element for keyword " + keyword);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get the device name list by type
     *
     * @see deviceType=Polycom~Phone
     *
     * @param deviceType
     *            The specified device type
     * @return The device name list by type
     */
    public String getEndpointListByType() {
        final String result = "NotFound";
        final String deviceType = inputCmd.get("deviceType").replaceAll("~",
                " ");
        final List<JEndpointForList> endpointList = deviceManagerHandler
                .getEndpoints4Paging(userToken, 100);
        String epList = "";
        for (final JEndpointForList ep : endpointList) {
            if (ep.getDeviceType().getDisplayName().equals(deviceType)) {
                epList += "," + ep.getDeviceName();
            }
        }
        if (epList.isEmpty()) {
            return result;
        } else {
            return epList.substring(1);
        }
    }

    /**
     * Get the device specified attribute value in monitor view
     *
     * @see ipAddress=$ciscoAddr <br/>
     *      keyword=DeviceId
     * @param ipAddress
     *            The device ip
     * @param keyword
     *            The attribute name
     * @return The device specified attribute value in monitor view
     */
    public String getEndpointSpecificDetail() {
        final String result = "NotFound";
        final String ipAddress = inputCmd.get("ipAddress");
        final String deviceName = inputCmd.get("deviceName");
        final String deviceType = inputCmd.get("deviceType");
        final String keyword = inputCmd.get("keyword");
        final List<JEndpointForList> endpointList = deviceManagerHandler
                .getEndpoints4Paging(userToken, 2000);
        if (!deviceType.isEmpty()) {
            for (int i = endpointList.size() - 1; i >= 0; i--) {
                if (!endpointList.get(i).getDeviceType().getDisplayName()
                        .equalsIgnoreCase(deviceType)) {
                    endpointList.remove(i);
                }
            }
        }
        if (ipAddress.isEmpty() && deviceName.isEmpty()) {
            return CommonUtils.invokeGetMethod(endpointList, keyword);
        }
        for (final JEndpointForList endpoint : endpointList) {
            if (ipAddress.equals(endpoint.getIpAddress())
                    || deviceName.equalsIgnoreCase(endpoint.getDeviceName())) {
                return CommonUtils.invokeGetMethod(endpoint, keyword);
            }
        }
        return result;
    }

    public int getFailNum() {
        return failNum;
    }

    public int getPassNum() {
        return passNum;
    }

    /**
     * Retrieve the specific information from the peripheral view page
     *
     * @see ipAddress=$ipAddress <br/>
     *      keyword=$keyword
     * @param ipAddress
     *            The IP address of the peripheral
     * @param keyword
     *            The device details attribute name
     * @return The device specified attribute value in peripheral view
     */
    public String getPeripheralsViewSpecific() {
        final String result = "NotFound";
        final String ipAddress = inputCmd.get("ipAddress");
        final String keyword = inputCmd.get("keyword");
        final JQueryCondition queryCondition = new JQueryCondition();
        queryCondition.setAttributeName("1");
        queryCondition.setAttributeValue("1");
        queryCondition.setHasNoCondition(false);
        queryCondition.setIsAsc(true);
        queryCondition.setIsRelation(false);
        queryCondition.setOperator(Operator.EQUAL);
        queryCondition.setOrderBy("Endpoint_Type");
        final List<JEndpointForList> peripherals = deviceManagerHandler
                .getPeripheralEndpoints4Paging(userToken, queryCondition, 50);
        for (final JEndpointForList peripheral : peripherals) {
            if (peripheral.getIpAddress().equals(ipAddress)) {
                return CommonUtils.invokeGetMethod(peripheral, keyword);
            }
        }
        return result;
    }

    @Override
    protected void injectCmdArgs() {
        put("ipAddress", "");
        put("deviceType", "");
        put("deviceName", "");
        put("deviceLoginName", "");
        put("devicePassword", "");
        put("keyword", "");
        put("deviceRole", "0");
        put("deviceId", "0");
        put("username", "");
        put("roomName", "");
        put("localOrAd", "local");
        put("startIp", "1.1.1.1");
        put("endIp", "1.1.1.2");
        put("expectEpAddress", "");
        put("isVc2", "false");
        put("callId", "0");
        put("h323ID", "");
        put("sysName", "");
        put("sipUri", "");
        put("e164", "");
        put("mode", "");
        put("status", "");
        put("digestPassword", "");
        put("totalNum", "");
        put("expectNum", "");
    }

    /**
     * Reboot the device with device id
     *
     * @see deviceId=$deviceId
     *
     * @param deviceId
     *            The device uid
     * @return The result
     */
    public String rebootDevice() {
        String status = "Failed";
        final int deviceId = Integer.parseInt(inputCmd.get("deviceId"));
        final JWebResult result = deviceManagerHandler.rebootDevice(userToken,
                                                                    deviceId);
        if (result.getStatus()
                .equals(com.polycom.webservices.DeviceManager.JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("device reboot successfully.");
        } else {
            status = "Failed";
            logger.error("device reboot failed");
            return status + " device reboot.";
        }
        return status;
    }

    /**
     * Reset the digest password of phone device
     *
     * @see deviceName=$oneLinePhoneName <br/>
     *      digestPassword=Polycom1234
     *
     * @param deviceName
     *            The device name
     * @param digestPassword
     *            The new digest password
     *
     * @return The result
     */
    public String resetPhoneDigestPassword() {
        String status = "Failed";
        final List<JEndpointForList> endpointList = deviceManagerHandler
                .getEndpoints4Paging(userToken, 100);
        final List<Integer> toResetPasswordEndpointIdList = new ArrayList<Integer>();
        boolean deviceFound = false;
        //
        for (final JEndpointForList endpoint : endpointList) {
            deviceFound = false;
            if (inputCmd.get("deviceName").equals(endpoint.getDeviceName())) {
                deviceFound = true;
                toResetPasswordEndpointIdList
                .add(new Integer(endpoint.getDeviceId()));
                break;
            }
        }
        if (deviceFound) {
            if (deviceManagerHandler
                    .resetPhoneDigestUserPassword(userToken,
                                                  toResetPasswordEndpointIdList,
                                                  inputCmd.get("digestPassword"))
                                                  .getStatus()
                                                  .compareTo(com.polycom.webservices.DeviceManager.JStatus.SUCCESS) == 0) {
                status = "SUCCESS";
                logger.info("Endpoint digest password of "
                        + inputCmd.get("deviceName")
                        + " is successfully updated.");
            } else {
                status = "Failed";
                logger.error("Endpoint digest password of "
                        + inputCmd.get("deviceName")
                        + " is not successfully updated.");
                return status + " Endpoint digest password of "
                + inputCmd.get("deviceName")
                + " is not successfully updated.";
            }
        } else {
            status = "Failed, endpoint " + inputCmd.get("deviceName")
                    + " was not found in xma";
            logger.info("Endpoint " + inputCmd.get("deviceName")
                        + " was not found in XMA.");
        }
        return status;
    }

    /**
     * Set the common password
     *
     * @see deviceLoginName=admin <br/>
     *      devicePassword=$correctDeviceLoginPassword
     *
     * @param deviceLoginName
     *            The login username of device
     * @param devicePassword
     *            The common password
     * @return The result
     */
    public String setCommonPassword() {
        String status = "Failed";
        final String username = inputCmd.get("deviceLoginName");
        final String password = inputCmd.get("devicePassword");
        final JWebResult result = deviceManagerHandler
                .setCommonPassword(userToken, username, password);
        if (result.getStatus()
                .equals(com.polycom.webservices.DeviceManager.JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("XMA set common password successfully.");
        } else {
            status = "Failed";
            logger.error("XMA set common password failed.");
            return status + " set XMA common password.";
        }
        return status;
    }

    /**
     * Set phone default digest account password
     *
     * @see deviceName=$oneLinePhoneName <br/>
     *      digestPassword=Polycom1212
     * @param deviceName
     *            The device name
     * @param digestPassword
     *            The new digest password
     * @return The result
     */
    public String setDigestAccountDefaultPassword() {
        String status = "Failed";
        logger.info("password: " + inputCmd.get("digestPassword"));
        if (deviceManagerHandler
                .setDigestAccountDefaultPassword(userToken,
                                                 inputCmd.get("digestPassword"))
                                                 .getStatus()
                                                 .compareTo(com.polycom.webservices.DeviceManager.JStatus.SUCCESS) == 0) {
            status = "SUCCESS";
            logger.info("Digest account default password is successfully updated.");
        } else {
            status = "Failed";
            logger.error("Digest account default password is not successfully updated.");
            return status
                    + " Digest account default password is not successfully updated.";
        }
        return status;
    }

    /**
     * Update the device attribute
     *
     * @see deviceRole=$deviceRole <br/>
     *      deviceId=$deviceId <br/>
     *      keyword=Description($description)
     * @param deviceRole
     *            The device role(Optional)
     * @param deviceId
     *            The device uid
     * @param keyword
     *            The attribute name and string value
     * @return The result
     */
    public String updateEndpoint() {
        String status = "Failed";
        final int deviceRole = Integer.parseInt(inputCmd.get("deviceRole"));
        final int deviceId = Integer.parseInt(inputCmd.get("deviceId"));
        final String keyword = inputCmd.get("keyword");
        final JEndpointForDetails endpoint = deviceManagerHandler
                .getDeviceForDetails(userToken, deviceRole, deviceId);
        final Pattern p = Pattern.compile("([a-zA-Z0-9]+)\\((.*)\\)");
        final Matcher m = p.matcher(keyword);
        String attributeName = "";
        String strValue = "";
        if (m.find()) {
            attributeName = m.group(1);
            strValue = m.group(2);
        }
        try {
            CommonUtils.invokeSetMethod(endpoint, attributeName, strValue);
        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | InstantiationException e) {
            e.printStackTrace();
            return "Failed, " + e.getMessage();
        }
        final JWebResult result = deviceManagerHandler
                .saveDevice(userToken, endpoint, false);
        if (result.getStatus()
                .equals(com.polycom.webservices.DeviceManager.JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Update endpoint (" + endpoint.getIpAddress() + ") "
                    + attributeName + " to " + strValue + " successfully.");
        } else {
            status = "Failed";
            logger.error("Update endpoint (" + endpoint.getIpAddress() + ") "
                    + attributeName + " to " + strValue + " failed.");
            return status + "Update endpoint (" + endpoint.getIpAddress() + ") "
            + attributeName + " to " + strValue + " failed.";
        }
        return status;
    }
}