package com.polycom.webservices;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.ConfigManagerHandler;
import com.polycom.sqa.xma.webservices.driver.DashboardManagerHandler;
import com.polycom.sqa.xma.webservices.driver.RedundancyManagerHandler;
import com.polycom.webservices.DashboardManager.JRedundancyPodRequest;
import com.polycom.webservices.DashboardManager.JUMCDashboardDataRequest;
import com.polycom.webservices.DashboardManager.JUMCDashboardDataResponse;
import com.polycom.webservices.RedundancyManager.JRedundancyConfig;
import com.polycom.webservices.RedundancyManager.JRedundancyNodeRole;
import com.polycom.webservices.RedundancyManager.JRedundancyServer;
import com.polycom.webservices.RedundancyManager.JStatus;
import com.polycom.webservices.RedundancyManager.JWebResult;

import bsh.This;

/**
 * Redundancy handler. This class will handle the webservice request of
 * Redundancy module
 *
 * @author wbchao
 *
 */
public class RedundancyHandler extends XMAWebServiceHandler {
    public static void main(final String[] args) throws IOException {
        // final String method = "setRedundancyServer ";
        // final String auth =
        // "username=admin domain=local password=UG9seWNvbTEyMw== ";
        // final String params =
        // "peerServerRedundantIP=101.101.0.252
        // localServerRedundantIP=100.100.0.251
        // vhostName=82HA-NIC2.autoB.xma.eng flag=false
        // localPassword=!Pr1mary_4i8_ ";
        final String method = "getRedundancyServerSpecific ";
        final String auth = "username=admin domain=local password=UG9seWNvbTEyMw== ";
        final String params = "keyword=[status=ONLINE]:hostname ";
        final String command = "http://10.220.202.245:80/PlcmRmWeb/JRedundancyManager RedundancyManager "
                + method + auth + params;
        final XMAWebServiceHandler handler = new RedundancyHandler(command);
        handler.logger.info("cmd==" + command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
        // params = "localServerRedundantIP=10.220.206.251 ";
        // command =
        // "http://10.220.208.251:80/PlcmRmWeb/JRedundancyManager
        // RedundancyManager "
        // + method + auth + params;
        // handler = new RedundancyHandler(command);
        // handler.logger.info("cmd==" + command);
        // result = handler.build();
        // handler.logger.info("result for " + method + " is " + result);
    }

    private final RedundancyManagerHandler rmh;

    public RedundancyHandler(final String cmd) throws IOException {
        super(cmd);
        rmh = new RedundancyManagerHandler(webServiceUrl);
    }

    /**
     * Check the Redundancy Server
     *
     * @see peerServerRedundantIP=$primaryRedundantIP <br/>
     *      localServerRedundantIP=$secondaryRedundantIP <br/>
     *      virtualIP=$virtualIP <br/>
     *      vhostName=$vhostName <br/>
     *      activeHostName=$secondHostname <br/>
     *      inactiveHostName=$primaryHostname <br/>
     *      activeMachineStatus=ONLINE <br/>
     *      inactiveMachineStatus=ONLINE <br/>
     *      lastFailoverReason=FAILOVER_BY_MANUALLY
     *
     *
     * @param peerServerRedundantIP
     *            The peer redundant server ip
     * @param localServerRedundantIP
     *            The local redundant server ip
     * @param virtualIP
     *            The virtual server ip
     * @param vhostName
     *            The virtual host name
     * @param activeHostName
     *            The active host name
     * @param inactiveHostName
     *            The inactive host name
     * @param activeMachineStatus
     *            The expected active machine status
     * @param inactiveMachineStatus
     *            The expected inactive machine status
     * @param lastFailoverReason
     *            The last failover reason
     * @return The result
     */
    public String checkRedundancyServer() {
        String status = "SUCCESS";
        String errorMsg = "";
        final JRedundancyConfig networkInfo = rmh
                .getAllRedundancyServerConfig(userToken);
        final List<JRedundancyServer> serversInfo = rmh
                .getAllRedundancyServer4ServerInfo(userToken);
        final Map<String, String> failoverInfo = rmh
                .getAllRedundancyServer4FailoverInfo(userToken);
        // Since virtual IP is optional, need to verify separately
        final String virtualIP = getInputCmd().get("virtualIP");
        final String peerServerRedundantIP = getInputCmd()
                .get("peerServerRedundantIP");
        final String localServerRedundantIP = getInputCmd()
                .get("localServerRedundantIP");
        final String vhostName = getInputCmd().get("vhostName");
        final String redundancyServerInfo = "Get redundancy server configuration: \n"
                + "Virtual IP is: " + virtualIP + "\n" + "Peer server IP is: "
                + peerServerRedundantIP + "\n" + "Local server IP is: "
                + localServerRedundantIP + "\n" + "VHostname is: " + vhostName
                + "\n";
        if (!virtualIP.isEmpty()) {
            if (!networkInfo.getPublicip().equals(virtualIP)) {
                status = "Failed";
                return status + ", check virtualIP failed.\n"
                        + redundancyServerInfo;
            }
        } else {
            // Check the redundancy server network configuration when the
            // virtual IP is empty
            if (!networkInfo.getPublicip().isEmpty()) {
                status = "Failed";
                return status + ", check public ip empty failed.\n"
                        + redundancyServerInfo;
            }
        }
        if (!networkInfo.getDestinationip().equals(peerServerRedundantIP)) {
            status = "Failed";
            return status + ", check peerServerRedundantIP failed. Actual: "
                    + networkInfo.getDestinationip() + " Expected:"
                    + peerServerRedundantIP + "\n" + redundancyServerInfo;
        }
        if (!networkInfo.getServerip().equals(localServerRedundantIP)) {
            status = "Failed";
            return status + ", check localServerRedundantIP failed. Actual: "
                    + networkInfo.getServerip() + " Expected:"
                    + localServerRedundantIP + "\n" + redundancyServerInfo;
        }
        if (!networkInfo.getVhostname().equals(vhostName)) {
            status = "Failed";
            return status + ", check vhostName failed. Actual: "
                    + networkInfo.getVhostname() + " Expected:" + vhostName
                    + "\n" + redundancyServerInfo;
        }
        logger.info("The redundancy configuration is as expected.");
        // Check each unit for the redundancy information on the redundancy
        // configuration page
        final String activeHostName = getInputCmd().get("activeHostName");
        final String activeMachineStatus = getInputCmd()
                .get("activeMachineStatus");
        final String inactiveHostName = getInputCmd().get("inactiveHostName");
        final String inactiveMachineStatus = getInputCmd()
                .get("inactiveMachineStatus");
        final String lastFailoverReason = getInputCmd()
                .get("lastFailoverReason");
        for (final JRedundancyServer server : serversInfo) {
            // Check the active unit
            if (server.getRole().equals(JRedundancyNodeRole.ACTIVE)) {
                if (!server.getHostname().equals(activeHostName)) {
                    status = "Failed";
                    errorMsg = "Active host name expect: " + activeHostName
                            + ", but got: " + server.getHostname();
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
                if (!server.getServerip().equals(localServerRedundantIP)) {
                    status = "Failed";
                    errorMsg = "Active unit local server redundant IP address expect: "
                            + localServerRedundantIP + ", but got: "
                            + server.getServerip();
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
                // Check the machine status, online or offline
                if (activeMachineStatus.isEmpty()) {
                    status = "Failed";
                    logger.error("The activeMachineStatus lost. You should specify it in your command.");
                    return status
                            + " The activeMachineStatus lost. You should specify it in your command.";
                }
                if (activeMachineStatus
                        .equalsIgnoreCase(server.getStatus().toString())) {
                    logger.info("The active unit is online now which is as expected.");
                } else {
                    status = "Failed";
                    errorMsg = "Active unit status expect: "
                            + activeMachineStatus + ", but got: "
                            + server.getStatus();
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
            }
            // Check the inactive unit
            if (server.getRole().equals(JRedundancyNodeRole.INACTIVE)) {
                if (!server.getHostname().equals(inactiveHostName)) {
                    status = "Failed";
                    errorMsg = "Inactive host name expect: " + inactiveHostName
                            + ", but got: " + server.getHostname();
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
                if (!server.getServerip().equals(peerServerRedundantIP)) {
                    status = "Failed";
                    errorMsg = "Inactive unit peer server redundant IP address expect: "
                            + peerServerRedundantIP + ", but got: "
                            + server.getServerip();
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
                // Check the machine status, online or offline
                if (inactiveMachineStatus.isEmpty()) {
                    status = "Failed";
                    errorMsg = "The inactiveMachineStatus lost. You should specify it in your command.";
                    logger.error(errorMsg);
                    return "Failed. " + errorMsg;
                }
                if (!server.getStatus().toString()
                        .equals(inactiveMachineStatus)) {
                    status = "Failed";
                    errorMsg = "Inactive unit status expect: "
                            + inactiveMachineStatus + ", but got: "
                            + server.getStatus();
                    logger.error(errorMsg);
                    return "Failed. " + errorMsg;
                }
            }
        }
        // Check the fail over information
        if (!lastFailoverReason.isEmpty()) {
            if (!lastFailoverReason
                    .equals(failoverInfo.get("lastFailoverReason"))) {
                status = "Failed";
                errorMsg = "The last fail over reason is not as the expected: "
                        + lastFailoverReason + " Current reason is: "
                        + failoverInfo.get("lastFailoverReason");
                logger.error(errorMsg);
                return "Failed. " + errorMsg;
            }
            // Check the last fail over reason between the dash board and
            // redundancy configuration page
            final DashboardManagerHandler dmh = new DashboardManagerHandler(
                    webServiceUrl);
            final JUMCDashboardDataRequest dataRequest = new JUMCDashboardDataRequest();
            final JRedundancyPodRequest redundancy = new JRedundancyPodRequest();
            redundancy.setPodId("id-redundancy");
            dataRequest.setRedundancy(redundancy);
            final JUMCDashboardDataResponse dataResponse = dmh
                    .getUMCDashboardData(userToken, dataRequest);
            // Check the last fail over time between the dash board and
            // redundancy configuration page
            final String dashboardLastFailoverTime = dataResponse
                    .getRedundancy().getLastFailoverTime();
            if (!dashboardLastFailoverTime
                    .equals(failoverInfo.get("lastFailoverTime"))) {
                status = "Failed";
                errorMsg = "The last fail over time on the redundancy configuration page is not the same with that on the dashboard."
                        + "\nRedundancy: "
                        + failoverInfo.get("lastFailoverTime") + "\nDashboard: "
                        + dashboardLastFailoverTime;
                logger.error(errorMsg);
                return "Failed. " + errorMsg;
            }
        }
        return status;
    }

    /**
     * The the Redundancy server attribute value
     *
     * @see keyword=[status=ONLINE]:hostname
     * @param keyword
     *            The Redundancy server attribute name
     * @return The Redundancy server attribute value
     */
    public String getRedundancyServerSpecific() {
        final List<JRedundancyServer> servers = rmh
                .getAllRedundancyServers(userToken);
        final String keyword = inputCmd.get("keyword");
        return CommonUtils.invokeGetMethod(servers, keyword);
    }

    @Override
    protected void injectCmdArgs() {
        // Virtual IP address
        put("virtualIP", "");
        // Peer server redundant IP
        put("peerServerRedundantIP", "");
        // Local server redundant IP
        put("localServerRedundantIP", "");
        // Virtual host name. It is mandatory.
        put("vhostName", "");
        // Flag. Should be always false when setting up the redundancy server.
        put("flag", "");
        // Local server password
        put("localPassword", "");
        // Remote server password
        put("remotePassword", "");
        // Last fail over reason
        put("lastFailoverReason", "");
        // Active host name on the redundancy pod
        put("activeHostName", "");
        // Inactive host name on the redundancy pod
        put("inactiveHostName", "");
        // Active machine status on the redundancy configuration page
        put("activeMachineStatus", "");
        // Inactive machine status on the redundancy configuration page
        put("inactiveMachineStatus", "");
    }

    /**
     * Reset the redundancy server
     *
     * @see No param
     *
     * @return The result
     */
    public String resetRedundancyServer() {
        String status = "Failed";
        try {
            final JWebResult result = rmh.resetRedundancyServer(userToken);
            if (result.getStatus().equals(JStatus.SUCCESS)) {
                status = "SUCCESS";
                logger.info("Redundancy server reset successfully. It is now rebooted automatically.");
            } else {
                status = "Failed";
                logger.error("Redundancy server reset failed. Cannot get the reset response from XMA. Please check.");
                return status
                        + " Redundancy server reset failed. Cannot get the reset response from XMA. Please check.";
            }
        } catch (final Exception e) {
            e.printStackTrace();
            logger.error("Because the XMA may just rebooted, execption "
                    + e.getMessage()
                    + " is thrown. Due to this problem, reset failed to send response.");
            status = "SUCCESS";
            return status
                    + " Because the XMA may just rebooted, execption is thrown. Due to this problem, reset failed to send response.\n"
                    + e.getCause().getMessage();
        }
        return status;
    }

    /**
     * Set up Redundancy Server
     *
     * @see This method does not used in TCL yet
     *
     * @param localPassword
     *            The local password
     * @param remotePassword
     *            The remote password
     * @param virtualIP
     *            The virtual ip
     * @param vhostName
     *            The virtual host name
     * @param activeHostName
     *            The active host name
     * @param inactiveHostName
     *            The inactive host name
     * @param activeMachineStatus
     *            The expected active machine status
     * @param inactiveMachineStatus
     *            The expected inactive machine status
     * @param lastFailoverReason
     *            The last failover reason
     * @return The result
     */
    public String setRedundancyServer() {
        String status = "Failed";
        final boolean flag = Boolean.parseBoolean(inputCmd.get("flag"));
        // Check if the password on each side is needed during setup
        final String localPassword = getInputCmd().get("localPassword");
        final String remotePassword = getInputCmd().get("remotePassword");
        final String virtualIP = getInputCmd().get("virtualIP");
        final String localServerRedundantIP = getInputCmd()
                .get("localServerRedundantIP");
        final String peerServerRedundantIP = getInputCmd()
                .get("peerServerRedundantIP");
        final String vhostName = getInputCmd().get("vhostName");
        final JRedundancyConfig redundancyConfig = new JRedundancyConfig();
        redundancyConfig.setDestinationip(peerServerRedundantIP);
        redundancyConfig.setEnable(flag);
        redundancyConfig.setPolling(0);
        redundancyConfig.setPort(0);
        redundancyConfig.setPublicip(virtualIP);
        redundancyConfig.setServerip(localServerRedundantIP);
        redundancyConfig.setVhostname(vhostName);
        if (!localPassword.isEmpty()) {
            final int testPasswordResult = rmh
                    .testPassword(userToken,
                                  0,
                                  localPassword,
                                  redundancyConfig);
            if (testPasswordResult != 0) {
                logger.error("Test local password failed");
                status = "Failed";
                return status
                        + " Test local password failed got test reponse code: "
                        + testPasswordResult;
            }
        }
        if (!remotePassword.isEmpty()) {
            final int testPasswordResult = rmh
                    .testPassword(userToken,
                                  1,
                                  remotePassword,
                                  redundancyConfig);
            if (testPasswordResult != 0) {
                logger.error("Test remote password failed");
                status = "Failed";
                return status
                        + " Test remote password failed got test reponse code: "
                        + testPasswordResult;
            }
        }
        // Start to set up the redundant server
        try {
            final JWebResult resultAddRedundancyServer = rmh
                    .addRedundancyServer(userToken, redundancyConfig);
            if (resultAddRedundancyServer.getStatus().equals(JStatus.SUCCESS)) {
                status = "SUCCESS";
                logger.info("Redundancy server set up successfully. It is now rebooted automatically.");
            } else {
                status = "Failed";
                logger.error("Redundancy server set up failed. Cannot get the setup response from XMA. Please check.");
                return status
                        + " Redundancy server set up failed. Cannot get the setup response from XMA. Please check.";
            }
        } catch (final WebServiceException e) {
            e.printStackTrace();
            logger.error("Because the XMA may just rebooted, execption "
                    + e.getMessage()
                    + " is thrown. Due to this problem setup operation failed to send response.");
            status = "SUCCESS";
            return status + " Because the XMA may just rebooted, execption "
                    + e.getMessage()
                    + " is thrown. Due to this problem setup operation failed to send response.";
        }
        return status;
    }

    /**
     * Switch the redundancy server
     *
     * @see No param
     *
     * @param cmdArgs
     * @return The result
     */
    public String switchRedundancyServer() {
        String status = "Failed";
        final JWebResult switch_result = rmh.switchRedundancyServer(userToken);
        if (switch_result.getStatus().equals(JStatus.SUCCESS)) {
            // setup succeed restart the server
            final ConfigManagerHandler cmh = new ConfigManagerHandler(
                    webServiceUrl);
            final com.polycom.webservices.ConfigManager.JWebResult result = cmh
                    .restartServer(userToken);
            if (result.getStatus()
                    .equals(com.polycom.webservices.ConfigManager.JStatus.SUCCESS)) {
                System.out.println("Restart server succeed...");
                logger.info("The redundancy server switched successfully. The XMA is now rebooted.");
                status = "SUCCESS";
            } else {
                status = "Failed";
                logger.error("Restart server failed...\n");
                return status + "Restart server failed...";
            }
        } else {
            status = "Failed";
            logger.error("Switch the redundancy server failed...\n");
            logger.error("The reason for the failure is: "
                    + switch_result.getMessages());
            return status + "Switch the redundancy server failed..."
                    + " The reason for the failure is: "
                    + switch_result.getMessages();
        }
        return status;
    }

    /**
     * Verify the redundancy server ip
     *
     * @see localServerRedundantIP=$secondaryNIC2IP
     *
     * @param localServerRedundantIP
     *            The local redundancy server ip
     * @return The result
     */
    public String verifyAfterResetRedundancyServer() {
        String status = "";
        String configInfo = "";
        final JRedundancyConfig result = rmh
                .getAllRedundancyServerConfig(userToken);
        if (result == null) {
            return "Failed, could not get redundancy config.";
        }
        if (result.getPublicip().isEmpty()
                && result.getDestinationip().isEmpty()
                && result.getVhostname().isEmpty()
                && result.getServerip().equalsIgnoreCase(inputCmd
                        .get("localServerRedundantIP"))) {
            status = "SUCCESS";
        } else {
            status = "Failed";
            configInfo = "Verify redundancy server configuration after rest failed: \n"
                    + "Current Public IP is: " + result.getPublicip() + "\n"
                    + "Current Desination IP is: " + result.getDestinationip()
                    + "\n" + "Current Server IP is: " + result.getServerip()
                    + "\n" + "Current VHostname is: " + result.getVhostname()
                    + "\n";
            logger.error(configInfo);
        }
        return status + ". " + configInfo;
    }
}
