package com.polycom.webservices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.DashboardManagerHandler;
import com.polycom.sqa.xma.webservices.driver.PbxServerManagerHandler;
import com.polycom.webservices.DashboardManager.JDashboardDataResponse;
import com.polycom.webservices.PbxServerManager.JPbxServer;
import com.polycom.webservices.PbxServerManager.JStatus;
import com.polycom.webservices.PbxServerManager.JWebResult;

/**
 * Pbx Server handler. This class will handle the webservice request of Pbx
 * Server module
 *
 * @author wbchao
 *
 */
public class PbxServerHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "addCUCM ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "deviceName=aaa cucmUsername=admin cucmPassword=Polycom123
        // description=auto ipAddress=10.220.206.65 ";
        // final String method = "deleteCUCM ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "deviceName=abc ipAddress=10.220.206.65";
        //
        // final String command =
        // "http://localhost:8888/PlcmRmWeb/JPbxServerManager PbxServerManager "
        // + method + auth + params1;
        // final String method = "checkCucmInMoniterView ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "deviceName=auto ipAddress=10.220.206.65 cucmStatus=online ";
        // final String method = "syncCucmServer ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "ipAddress=10.220.206.65 ";
        final String method = "updateCucmServer ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params1 = "ipAddress=10.220.206.66 field1=username value1=test field2=password value2=Polycom321 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JPbxServerManager PbxServerManager "
                + method + auth + params1;
        final PbxServerHandler handler = new PbxServerHandler(command);
        final String result = handler.build();
        System.out.println("result==" + result);
    }

    private final PbxServerManagerHandler pbxServerManagerHandler;

    public PbxServerHandler(final String cmd) throws IOException {
        super(cmd);
        pbxServerManagerHandler = new PbxServerManagerHandler(webServiceUrl);
    }

    /**
     * Add CUCM to XMA
     *
     * @see deviceName=autoCucm65 <br/>
     *      description=automation <br/>
     *      ipAddress=$cucmAddr <br/>
     *      cucmUsername=$cucmUsername <br/>
     *      cucmPassword=$cucmPassword
     *
     *
     * @param deviceName
     *            The CUCM name
     * @param description
     *            The description
     * @param ipAddress
     *            The CUCM ip
     * @param cucmUsername
     *            The CUCM login username
     * @param cucmPassword
     *            The CUCM login password
     * @return The result
     */
    public String addCucm() {
        String status = "Failed";
        final JPbxServer pbxServer = new JPbxServer();
        pbxServer.setConnectionType("HTTPS");
        pbxServer.setWebServicePort(8443);
        pbxServer.setFullSyncInterval(86400);
        pbxServer.setLdapAttributeForUserId("");
        pbxServer.setHttpPort(0);
        final String ipAddress = inputCmd.get("ipAddress");
        pbxServer.setDeviceName(inputCmd.get("deviceName"));
        pbxServer.setUsername(inputCmd.get("cucmUsername"));
        pbxServer.setPassword(inputCmd.get("cucmPassword"));
        pbxServer.setDescription(inputCmd.get("description"));
        pbxServer.setIpAddress(ipAddress);
        pbxServer.setLdapAttributeForUserId(inputCmd
                .get("ldapAttributeForUserId"));
        if (!inputCmd.get("ldapAttributeForUserId").isEmpty()) {
            pbxServer.setSameXmaAdServer(true);
        }
        for (int i = 1; i <= 10; i++) {
            final String keyword = inputCmd.get("field" + i);
            final String strValue = inputCmd.get("value" + i);
            if (keyword.isEmpty()) {
                break;
            } else {
                try {
                    CommonUtils.invokeSetMethod(pbxServer, keyword, strValue);
                } catch (IllegalAccessException
                         | IllegalArgumentException
                         | InvocationTargetException
                         | InstantiationException e) {
                    e.printStackTrace();
                    return "Failed to set " + strValue + " to " + keyword
                            + " field in JPbxServer Object!";
                }
            }
        }
        if (getCucmServerByIp(ipAddress) != null) {
            return "SUCCESS, the CUCM " + ipAddress
                    + " is already exist, no need to add.";
        }
        final JWebResult result = pbxServerManagerHandler.addCUCM(userToken,
                                                                  pbxServer);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("CUCM successfully added into XMA.");
            status = "SUCCESS";
        } else {
            logger.error("CUCM failed to add into XMA, please make sure your CUCM information is correct.");
            status = "Failed";
        }
        return status;
    }

    /**
     * Check the CUCM attribute in dashboard
     *
     * @see deviceName=CucmForAuto <br/>
     *      ipAddress=$cucmAddr <br/>
     *      cucmStatus=online
     * @param ipAddress
     *            The CUCM ip
     * @param deviceName
     *            The CUCM name
     * @param cucmStatus
     *            The CUCM status
     * @return The result
     */
    public String checkCucmInDashboard() {
        String status = "Failed";
        final DashboardManagerHandler dmh = new DashboardManagerHandler(
                webServiceUrl);
        // JDashboardDataResponse dashboardInfo =
        // dmh.getSpecifiedDashboardData(userToken, "cmaconfig", "cmainfo",
        // "dma", "pbx", "license");
        final JDashboardDataResponse dashboardInfo = dmh
                .getDashboardData(userToken);
        if (dashboardInfo == null || dashboardInfo.getPbxStatus() == null
                || dashboardInfo.getPbxStatus().getStatus() == null) {
            status = "Failed";
            logger.error("Check CUCM status " + status
                    + ", dashboard info or pbx status info is null!");
            return "Check CUCM status " + status
                    + ", dashboard info or pbx status info is null!";
        }
        if (inputCmd.get("ipAddress")
                .equals(dashboardInfo.getPbxStatus().getAddress())
                && inputCmd.get("deviceName")
                        .equals(dashboardInfo.getPbxStatus().getName())
                && inputCmd.get("cucmStatus").equalsIgnoreCase(dashboardInfo
                        .getPbxStatus().getStatus().toString())) {
            status = "SUCCESS";
            logger.info("Check CUCM status " + status + ", it's "
                    + dashboardInfo.getPbxStatus().getStatus().toString());
        } else {
            status = "Failed";
            logger.error("Check CUCM status " + status + ", it's "
                    + dashboardInfo.getPbxStatus().getStatus().toString());
            return "Check CUCM status " + status + ", it's "
                    + dashboardInfo.getPbxStatus().getStatus().toString();
        }
        return status;
    }

    /**
     * Check the CUCM attribute in monitor view
     *
     * @see deviceName=CucmForAuto <br/>
     *      ipAddress=$cucmAddr <br/>
     *      cucmStatus=online
     * @param ipAddress
     *            The CUCM ip
     * @param deviceName
     *            The CUCM name
     * @param cucmStatus
     *            The CUCM status
     * @return The result
     */
    public String checkCucmInMoniterView() {
        String status = "Failed";
        boolean find = false;
        final List<JPbxServer> pbxServerList = pbxServerManagerHandler
                .getCUCMs(userToken);
        for (final JPbxServer pbxServer : pbxServerList) {
            if (pbxServer.getDeviceName().equals(inputCmd.get("deviceName"))
                    && pbxServer.getIpAddress()
                            .equals(inputCmd.get("ipAddress"))) {
                find = true;
                logger.info("real: " + pbxServer.getDeviceStatus().toString());
                logger.info("expect: " + inputCmd.get("cucmStatus"));
                if (pbxServer.getDeviceStatus().toString()
                        .equalsIgnoreCase(inputCmd.get("cucmStatus"))) {
                    status = "SUCCESS";
                    logger.info("Check CUCM status " + status + ", it's "
                            + pbxServer.getDeviceStatus().toString());
                } else {
                    status = "Failed";
                    logger.error("Check CUCM status " + status + ", it's "
                            + pbxServer.getDeviceStatus().toString());
                    return "Check CUCM status " + status + ", it's "
                            + pbxServer.getDeviceStatus().toString();
                }
            }
        }
        if (find == false) {
            status = "Failed";
            logger.error("Check CUCM status " + status
                    + ", didn't find cucm to check");
            return "Check CUCM status " + status
                    + ", didn't find cucm to check";
        }
        return status;
    }

    /**
     * Delete the CUCM
     *
     * @see deviceName=autoCucm65 <br/>
     *      ipAddress=$cucmAddr
     * @param deviceName
     *            The CUCM name
     * @param ipAddress
     *            The CUCM ip
     * @return The result
     */
    public String deleteCucm() {
        String status = "Failed";
        final String ipAddress = inputCmd.get("ipAddress");
        final JPbxServer pbxServer = getCucmServerByIp(ipAddress);
        if (pbxServer == null) {
            return "Failed, could not find the CUCM with ip " + ipAddress;
        }
        final JWebResult result = pbxServerManagerHandler
                .deleteCUCM(userToken, pbxServer.getDeviceUUID());
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("CUCM " + ipAddress
                    + " is successfully deleted from XMA.");
        } else {
            status = "Failed";
            logger.error("CUCM " + ipAddress
                    + " is not successfully deleted from XMA.");
            return status + " CUCM " + ipAddress
                    + " is not successfully deleted from XMA.";
        }
        return status;
    }

    /**
     * Internal method, get the CUCM by ip
     *
     * @param ipAddress
     *            The CUCM ip
     * @return JPbxServer
     */
    private JPbxServer getCucmServerByIp(final String ipAddress) {
        final List<JPbxServer> pbxServerList = pbxServerManagerHandler
                .getCUCMs(userToken);
        for (final JPbxServer pbxServer : pbxServerList) {
            if (pbxServer == null) {
                continue;
            }
            if ("CUCM"
                    .equalsIgnoreCase(pbxServer.getDeviceType()
                            .getDisplayName())
                    && pbxServer.getIpAddress()
                            .equals(inputCmd.get("ipAddress"))) {
                return pbxServer;
            }
        }
        return null;
    }

    /**
     * Get the CUCM specific attribute value in monitor view
     *
     * @see ipAddress=$cucmAddr <br/>
     *      keyword=deviceName
     * @param ipAddress
     *            The CUCM ip
     * @param keyword
     *            The attribute name
     * @return The CUCM specific attribute value in monitor view
     */
    public String getCucmSpecificDetailInMoniterView() {
        final String keyword = inputCmd.get("keyword");
        final String ipAddress = inputCmd.get("ipAddress");
        final JPbxServer pbxServer = getCucmServerByIp(ipAddress);
        if (pbxServer == null) {
            return "Failed, could not find the CUCM with ip " + ipAddress;
        } else {
            return CommonUtils.invokeGetMethod(pbxServer, keyword);
        }
    }

    /**
     * Inject the args from tcl
     */
    @Override
    protected void injectCmdArgs() {
        put("deviceName", "");
        put("description", "");
        put("ipAddress", "");
        put("cucmUsername", "");
        put("cucmPassword", "");
        put("ldapAttributeForUserId", "");
        put("cucmStatus", "offline");
    }

    /**
     * Sync the CUCM server
     *
     * @see ipAddress=$cucmAddr
     * @param ipAddress
     *            The CUCM ip
     * @return The result
     */
    public String syncCucmServer() {
        String status = "Failed";
        final String ipAddress = inputCmd.get("ipAddress");
        final JPbxServer pbxServer = getCucmServerByIp(ipAddress);
        if (pbxServer == null) {
            return "Failed, could not find the CUCM with ip " + ipAddress;
        }
        final Boolean result = pbxServerManagerHandler
                .synchronizeCucmServer(userToken, pbxServer);
        if (result == true) {
            status = "SUCCESS";
            logger.info("CUCM " + ipAddress
                    + " is successfully sync from XMA.");
        } else {
            status = "Failed";
            logger.error("CUCM " + ipAddress
                    + " is not successfully sync from XMA.");
            return status + " CUCM " + ipAddress
                    + " is not successfully sync from XMA.";
        }
        return status;
    }

    /**
     * Update the CUCM server on XMA
     *
     * @see ipAddress=$cucmAddr <br/>
     *      field1=username <br/>
     *      value1=$cucmUpdateUsername <br/>
     *      field2=password <br/>
     *      value2=$cucmUpdatePassword
     *
     * @param ipAddress
     *            The CUCM ip
     * @param field
     *            [1-10] The field name
     * @param value
     *            [1-10] The value to update
     * @return The result
     */
    public String updateCucmServer() {
        String status = "Failed";
        final String ipAddress = inputCmd.get("ipAddress");
        final JPbxServer pbxServer = getCucmServerByIp(ipAddress);
        if (pbxServer == null) {
            return "Failed, could not find the CUCM with ip " + ipAddress;
        }
        for (int i = 1; i <= 10; i++) {
            final String keyword = inputCmd.get("field" + i);
            final String strValue = inputCmd.get("value" + i);
            if (keyword.isEmpty()) {
                break;
            } else {
                try {
                    CommonUtils.invokeSetMethod(pbxServer, keyword, strValue);
                } catch (IllegalAccessException
                         | IllegalArgumentException
                         | InvocationTargetException
                         | InstantiationException e) {
                    e.printStackTrace();
                    return "Failed to set " + strValue + " to " + keyword
                            + " field in JPbxServer Object!";
                }
            }
        }
        final JWebResult result = pbxServerManagerHandler
                .updateCucmServer(userToken, pbxServer);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("CUCM " + ipAddress
                    + " is successfully updated from XMA.");
        } else {
            status = "Failed";
            logger.error("CUCM " + ipAddress
                    + " is not successfully updated from XMA.");
            return status + " CUCM " + ipAddress
                    + " is not successfully updated from XMA.";
        }
        return status;
    }
}