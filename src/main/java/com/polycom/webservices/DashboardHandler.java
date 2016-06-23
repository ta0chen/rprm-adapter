package com.polycom.webservices;

import java.io.IOException;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.DashboardManagerHandler;
import com.polycom.webservices.DashboardManager.JDashboardCMAConfig;
import com.polycom.webservices.DashboardManager.JDashboardDataResponse;
import com.polycom.webservices.DashboardManager.JDmaPodRequest;
import com.polycom.webservices.DashboardManager.JDmaPodResponse;
import com.polycom.webservices.DashboardManager.JLicensesPodRequest;
import com.polycom.webservices.DashboardManager.JLicensesPodResponse;
import com.polycom.webservices.DashboardManager.JPbxPodResponse;
import com.polycom.webservices.DashboardManager.JRedundancyNode;
import com.polycom.webservices.DashboardManager.JRedundancyNodeRole;
import com.polycom.webservices.DashboardManager.JRedundancyNodeStatus;
import com.polycom.webservices.DashboardManager.JRedundancyPodRequest;
import com.polycom.webservices.DashboardManager.JRedundancyPodResponse;
import com.polycom.webservices.DashboardManager.JUMCCPUMemoryDiskPodRequest;
import com.polycom.webservices.DashboardManager.JUMCDashboardDataRequest;
import com.polycom.webservices.DashboardManager.JUMCDashboardDataResponse;
import com.polycom.webservices.DashboardManager.JUMCEndpointPiePodRequest;
import com.polycom.webservices.DashboardManager.JUMCEndpointPodRequest;
import com.polycom.webservices.DashboardManager.JUMCSystemInfoPodRequest;
import com.polycom.webservices.DashboardManager.JUMCTodaysConferenceStatPodRequest;
import com.polycom.webservices.DashboardManager.JUMCTodaysScheduledConferencePodRequest;
import com.polycom.webservices.DashboardManager.JUMCUserPodRequest;
import com.polycom.webservices.DashboardManager.JUsersPodRequest;

/**
 * Dashboard handler. This class will handle the webservice request of Dashboard
 * module
 *
 * @author wbchao
 *
 */
public class DashboardHandler extends XMAWebServiceHandler {
    public static final String PACKAGE_NAME = "com.polycom.webservices.DashboardManager";

    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "getDashboardDMAPodAndValidation ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "dmaStatus=Up dmaName=DMA-245 dmaLicenseStatus=Active
        // sipSignalingEnabled=Yes h323SignalingEnabled=Yes
        // dmaSignalingAddress=172.21.125.245 dmaH323RasPort=1719
        // dmaH225SignalingPort=1720 dmaSipTcpPort=5060 dmaSipUdpPort=5060
        // dmaSipTlsPort=5061 dmaSiteTopoIntegrated=Yes
        // dmaSubscribedForDevices=Yes dmaSubscribedForActiveCalls=Yes
        // dmaPollingMcuPoolOrders=Yes ";
        // final String method = "getDashboardResourceManagerConfigSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = "keyword=hardwareVersion ";
        // final String method = "getDashboardRedundancySpecific ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params = "keyword=nodes[status=ONLINE]:hostIp ";
        final String method = "getUmcDashboardDataSpecific ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "keyword=redundancy:status ";
        final String command = "https://10.220.208.251:8443/PlcmRmWeb/rest/JDashboardManager DashboardManager "
                + method + auth + params;
        final DashboardHandler handler = new DashboardHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    DashboardManagerHandler dmh;

    public DashboardHandler(final String cmd) throws IOException {
        super(cmd);
        dmh = new DashboardManagerHandler(webServiceUrl);
    }

    /**
     * The the CUCM attribute value on dashboard
     *
     * @see keyword=status
     * @param keyword
     *            The CUCM attribute name
     * @return The CUCM attribute value
     */
    public String getDashboardCucmSpecific() {
        String result = "NotFound";
        final String keyword = inputCmd.get("keyword");
        final JDashboardDataResponse dashboardInfo = dmh
                .getDashboardData(userToken);
        if (dashboardInfo == null || dashboardInfo.getPbxStatus() == null) {
            logger.error("dashboard info or pbx status info is null!");
            return result + ", no dashboard found!";
        }
        final JPbxPodResponse pbxStatus = dashboardInfo.getPbxStatus();
        if (pbxStatus == null) {
            return result + ", no cucm panal in dashboad.";
        }
        try {
            result = CommonUtils.invokeGetMethod(pbxStatus, keyword);
        } catch (SecurityException | IllegalArgumentException e) {
            logger.equals("No element for keyword " + keyword);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Validate the value of dashboard
     *
     * @see redundancyStatus=Configured <br/>
     *      activeHostIP=$secondaryRedundantIP <br/>
     *      inactiveHostIP=$primaryRedundantIP <br/>
     *      activeHostName=$secondHostname <br/>
     *      inactiveHostName=$primaryHostname <br/>
     *      activeStatus=ONLINE <br/>
     *      inactiveStatus=ONLINE <br/>
     *      redundancyStatus=Configured <br/>
     *      vhostName=$vhostName <br/>
     *      lastFailoverReason=FAILOVER_BY_MANUALLY
     *
     * @param activeHostIP
     *            The expected active host ip
     * @param activeHostName
     *            The expected active host name
     * @param activeStatus
     *            The expected active host status
     * @param inactiveHostIP
     *            The expected inactive host ip
     * @param inactiveHostName
     *            The expected inactive host name
     * @param inactiveStatus
     *            The expected inactive host status
     *
     * @return The result
     */
    public String getDashboardDataAndValidation() {
        final JDashboardDataResponse dashboardRtn = dmh
                .getDashboardData(userToken);
        if (dashboardRtn == null) {
            return "Failed, could not get redundancy info in dashboard";
        }
        final StringBuffer errorMsg = new StringBuffer();
        final String redundancyStatus = inputCmd.get("redundancyStatus");
        final String virtualIP = inputCmd.get("virtualIP");
        final String vhostName = inputCmd.get("vhostName");
        // check the active and inactive node respectively
        for (final JRedundancyNode node : dashboardRtn.getRedundancy()
                .getNodes()) {
            if (node.getRole().equals(JRedundancyNodeRole.ACTIVE)) {
                final String activeHostIP = inputCmd.get("activeHostIP");
                final String activeHostName = inputCmd.get("activeHostName");
                if (!node.getStatus().equals(JRedundancyNodeStatus.ONLINE)) {
                    errorMsg.append("The status of active server is not ONLINE\n");
                }
                if (!activeHostIP.isEmpty()
                        && !activeHostIP.equals(node.getHostIp())) {
                    errorMsg.append("The active host ip is not as expected "
                            + activeHostIP + ", but got " + node.getHostIp()
                            + "\n");
                }
                if (!activeHostName.isEmpty()
                        && !activeHostName.equals(node.getHostName())) {
                    errorMsg.append("The active host name is not as expected "
                            + activeHostName + ", but got " + node.getHostName()
                            + "\n");
                }
            } else if (node.getRole().equals(JRedundancyNodeRole.INACTIVE)) {
                final String inactiveHostIP = inputCmd.get("inactiveHostIP");
                final String inactiveHostName = inputCmd
                        .get("inactiveHostName");
                final String inactiveStatus = inputCmd.get("inactiveStatus");
                if (!inactiveStatus.isEmpty() && !inactiveStatus
                        .equalsIgnoreCase(node.getStatus().toString())) {
                    errorMsg.append("The status of inactive server is not ONLINE\n");
                }
                if (!inactiveHostIP.isEmpty()
                        && !inactiveHostIP.equals(node.getHostIp())) {
                    errorMsg.append("The inactive host ip is not as expected "
                            + inactiveHostIP + ", but got " + node.getHostIp()
                            + "\n");
                }
                if (!inactiveHostName.isEmpty()
                        && !inactiveHostName.equals(node.getHostName())) {
                    errorMsg.append("The inactive host name is not as expected "
                            + inactiveHostName + ", but got "
                            + node.getHostName() + "\n");
                }
            }
        }
        // Check the redundancy status on the dash board redundancy pod
        final String actualRedundancyStatus = dashboardRtn.getRedundancy()
                .getStatus().toString();
        if (!redundancyStatus.isEmpty()
                && !redundancyStatus.equalsIgnoreCase(actualRedundancyStatus)) {
            errorMsg.append("The redundancy status on dashboard is not as expected "
                    + redundancyStatus + ", but got " + actualRedundancyStatus
                    + "\n");
        }
        // Check the redundancy virtual IP if available
        final String actualVirtualIp = dashboardRtn.getRedundancy()
                .getVirtualIP();
        if (!virtualIP.isEmpty()
                && !virtualIP.equalsIgnoreCase(actualVirtualIp)) {
            errorMsg.append("The redundancy virtual ip on dashboard is not as expected "
                    + virtualIP + ", but got " + actualVirtualIp + "\n");
        }
        // Check the redundancy virtual host name
        final String actualVirtualHostname = dashboardRtn.getCmaConfig()
                .getCmaConfig().getRedundancyHostname();
        if (!vhostName.isEmpty()
                && !vhostName.equalsIgnoreCase(actualVirtualHostname)) {
            errorMsg.append("The redundancy virtual hostname on dashboard is not as expected "
                    + vhostName + ", but got " + actualVirtualHostname + "\n");
        }
        // Check the last fail over reason and last fail over time
        final String lastFailoverReason = inputCmd.get("lastFailoverReason");
        final String actualLastFailoverReason = dashboardRtn.getRedundancy()
                .getLastFailoverReason();
        if (!lastFailoverReason.isEmpty() && !lastFailoverReason
                .equalsIgnoreCase(actualLastFailoverReason)) {
            errorMsg.append("The redundancy last Failover Reason on dashboard is not as expected "
                    + lastFailoverReason + ", but got "
                    + actualLastFailoverReason + "\n");
        }
        if (errorMsg.length() > 0) {
            return "Failed, " + errorMsg.toString();
        } else {
            return "SUCCESS";
        }
    }

    /**
     * Validate DMA dashboard value
     *
     * @see dmaStatus=Up <br/>
     *      dmaName=DMA-245 <br/>
     *      dmaLicenseStatus=Active <br/>
     *      sipSignalingEnabled=Yes <br/>
     *      h323SignalingEnabled=Yes <br/>
     *      dmaSignalingAddress=172.21.125.245 <br/>
     *      dmaH323RasPort=1719 <br/>
     *      dmaH225SignalingPort=1720 <br/>
     *      dmaSipTcpPort=5060 <br/>
     *      dmaSipUdpPort=5060 <br/>
     *      dmaSipTlsPort=5061 <br/>
     *      dmaSiteTopoIntegrated=Yes <br/>
     *      dmaSubscribedForDevices=Yes <br/>
     *      dmaSubscribedForActiveCalls=Yes <br/>
     *      dmaPollingMcuPoolOrders=Yes
     *
     * @param dmaStatus
     *            The expected DMA status
     * @param dmaName
     *            The expected DMA name
     * @param dmaLicenseStatus
     *            The expected DMA license status
     * @param sipSignalingEnabled
     *            The expected DMA sip signal status
     * @param h323SignalingEnabled
     *            The expected DMA H323 signal status
     * @param dmaH323RasPort
     *            The expected DMA H323 ras port
     * @param dmaH225SignalingPort
     *            The expected DMA H225 signal port
     * @param dmaSipTcpPort
     *            The expected DMA sip tcp port
     * @param dmaSipUdpPort
     *            The expected DMA sip udp port
     * @param dmaSipTlsPort
     *            The expected DMA sip tls port
     * @param dmaSiteTopoIntegrated
     *            The expected DMA site topo integrated status
     * @param dmaSubscribedForDevices
     *            The expected DMA subscribed for device status
     * @param dmaSubscribedForActiveCalls
     *            The expected DMA subscribed for active call status
     * @param dmaPollingMcuPoolOrders
     *            The expected DMA polling mcu pool orders
     *
     * @return The result
     */
    public String getDashboardDMAPodAndValidation() {
        String status = "Failed";
        final JDashboardDataResponse dashboardRtn = dmh
                .getDashboardData(userToken);
        if (!dashboardRtn.toString().isEmpty()) {
            if (!inputCmd.get("dmaStatus").isEmpty()
                    && !inputCmd.get("dmaStatus").isEmpty()
                    && !inputCmd.get("dmaName").isEmpty()
                    && !inputCmd.get("dmaLicenseStatus").isEmpty()
                    && !inputCmd.get("sipSignalingEnabled").isEmpty()
                    && !inputCmd.get("h323SignalingEnabled").isEmpty()
                    && !inputCmd.get("dmaSignalingAddress").isEmpty()
                    && !inputCmd.get("dmaH323RasPort").isEmpty()
                    && !inputCmd.get("dmaH225SignalingPort").isEmpty()
                    && !inputCmd.get("dmaSipTcpPort").isEmpty()
                    && !inputCmd.get("dmaSipUdpPort").isEmpty()
                    && !inputCmd.get("dmaSipTlsPort").isEmpty()
                    && !inputCmd.get("dmaSiteTopoIntegrated").isEmpty()
                    && !inputCmd.get("dmaSubscribedForDevices").isEmpty()
                    && !inputCmd.get("dmaSubscribedForActiveCalls").isEmpty()) {
                // Check the DMA status
                if (inputCmd.get("dmaStatus").equalsIgnoreCase("Up")) {
                    if (dashboardRtn.getDmaStatus().isDmaStatus()) {
                        status = "SUCCESS";
                        logger.info("The DMA status is as expected: "
                                + inputCmd.get("dmaStatus"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA status is not as expected: "
                                + inputCmd.get("dmaStatus"));
                        return status + " The DMA status is not as expected: "
                                + inputCmd.get("dmaStatus");
                    }
                } else if (inputCmd.get("dmaStatus").equalsIgnoreCase("Down")) {
                    if (!dashboardRtn.getDmaStatus().isDmaStatus()) {
                        status = "SUCCESS";
                        logger.info("The DMA status is as expected: "
                                + inputCmd.get("dmaStatus"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA status is not as expected: "
                                + inputCmd.get("dmaStatus"));
                        return status + " The DMA status is not as expected: "
                                + inputCmd.get("dmaStatus");
                    }
                } else {
                    status = "Failed";
                    logger.error("The format of the input DMA status is not correct. "
                            + "Please input Up or Down.");
                    return status
                            + " The format of the input DMA status is not correct. "
                            + "Please input Up or Down.";
                }
                // Check the DMA name
                if (dashboardRtn.getDmaStatus().getDmaName()
                        .equals(inputCmd.get("dmaName"))) {
                    status = "SUCCESS";
                    logger.info("The DMA name is as expected: "
                            + inputCmd.get("dmaName"));
                } else {
                    status = "Failed";
                    logger.error("The DMA name is not as expected: "
                            + inputCmd.get("dmaName"));
                    return status + " The DMA name is not as expected: "
                            + inputCmd.get("dmaName");
                }
                // Check the DMA signaling address
                if (dashboardRtn.getDmaStatus().getSignalingAddress()
                        .equals(inputCmd.get("dmaSignalingAddress"))) {
                    status = "SUCCESS";
                    logger.info("The DMA signaling address is as expected: "
                            + inputCmd.get("dmaSignalingAddress"));
                } else {
                    status = "Failed";
                    logger.error("The DMA signaling address is not as expected: "
                            + inputCmd.get("dmaSignalingAddress"));
                    return status
                            + " The DMA signaling address is not as expected: "
                            + inputCmd.get("dmaSignalingAddress");
                }
                // Check the DMA license status
                if (inputCmd.get("dmaLicenseStatus")
                        .equalsIgnoreCase("Active")) {
                    if (dashboardRtn.getDmaStatus().isIsDmaLicenseActive()) {
                        status = "SUCCESS";
                        logger.info("The DMA license status is as expected: "
                                + inputCmd.get("dmaLicenseStatus"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA license status is not as expected: "
                                + inputCmd.get("dmaLicenseStatus"));
                        return status
                                + " The DMA license status is not as expected: "
                                + inputCmd.get("dmaLicenseStatus");
                    }
                } else if (inputCmd.get("dmaLicenseStatus")
                        .equalsIgnoreCase("Inactive")) {
                    if (!dashboardRtn.getDmaStatus().isIsDmaLicenseActive()) {
                        status = "SUCCESS";
                        logger.info("The DMA license status is as expected: "
                                + inputCmd.get("dmaLicenseStatus"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA license status is not as expected: "
                                + inputCmd.get("dmaLicenseStatus"));
                        return status
                                + " The DMA license status is not as expected: "
                                + inputCmd.get("dmaLicenseStatus");
                    }
                } else {
                    status = "Failed";
                    logger.error("The format of the input DMA license status is not correct. "
                            + "Please input Yes or No.");
                    return status
                            + " The format of the input DMA license status is not correct. "
                            + "Please input Yes or No.";
                }
                // Check the DMA SIP signaling enabled configuration
                if (inputCmd.get("sipSignalingEnabled")
                        .equalsIgnoreCase("Yes")) {
                    if (dashboardRtn.getDmaStatus().isSipSignalingEnabled()) {
                        status = "SUCCESS";
                        logger.info("The DMA SIP signaling enabled configuration is as expected: "
                                + inputCmd.get("sipSignalingEnabled"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA SIP signaling enabled configuration is not as expected: "
                                + inputCmd.get("sipSignalingEnabled"));
                        return status
                                + " The DMA SIP signaling enabled configuration is not as expected: "
                                + inputCmd.get("sipSignalingEnabled");
                    }
                } else if (inputCmd.get("sipSignalingEnabled")
                        .equalsIgnoreCase("No")) {
                    if (!dashboardRtn.getDmaStatus().isSipSignalingEnabled()) {
                        status = "SUCCESS";
                        logger.info("The DMA SIP signaling enabled configuration is as expected: "
                                + inputCmd.get("sipSignalingEnabled"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA SIP signaling enabled configuration is not as expected: "
                                + inputCmd.get("sipSignalingEnabled"));
                        return status
                                + " The DMA SIP signaling enabled configuration is not as expected: "
                                + inputCmd.get("sipSignalingEnabled");
                    }
                } else {
                    status = "Failed";
                    logger.error("The format of the input DMA SIP signaling enabled configuration is not correct. "
                            + "Please input Yes or No.");
                    return status
                            + " The format of the input DMA SIP signaling enabled configuration is not correct. "
                            + "Please input Yes or No.";
                }
                // Check the DMA H323 signaling enabled configuration
                if (inputCmd.get("h323SignalingEnabled")
                        .equalsIgnoreCase("Yes")) {
                    if (dashboardRtn.getDmaStatus().isH323SignalingEnabled()) {
                        status = "SUCCESS";
                        logger.info("The DMA H.323 signaling enabled configuration is as expected: "
                                + inputCmd.get("h323SignalingEnabled"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA H.323 signaling enabled configuration is not as expected: "
                                + inputCmd.get("h323SignalingEnabled"));
                        return status
                                + " The DMA H.323 signaling enabled configuration is not as expected: "
                                + inputCmd.get("h323SignalingEnabled");
                    }
                } else if (inputCmd.get("h323SignalingEnabled")
                        .equalsIgnoreCase("No")) {
                    if (!dashboardRtn.getDmaStatus().isH323SignalingEnabled()) {
                        status = "SUCCESS";
                        logger.info("The DMA H.323 signaling enabled configuration is as expected: "
                                + inputCmd.get("h323SignalingEnabled"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA H.323 signaling enabled configuration is not as expected: "
                                + inputCmd.get("h323SignalingEnabled"));
                        return status
                                + " The DMA H.323 signaling enabled configuration is not as expected: "
                                + inputCmd.get("h323SignalingEnabled");
                    }
                } else {
                    status = "Failed";
                    logger.error("The format of the input DMA H.323 signaling enabled configuration is not correct. "
                            + "Please input Yes or No.");
                    return status
                            + " The format of the input DMA H.323 signaling enabled configuration is not correct. "
                            + "Please input Yes or No.";
                }
                // Check the DMA H.323 RAS port
                if (inputCmd.get("dmaH323RasPort")
                        .equalsIgnoreCase(String.valueOf(dashboardRtn
                                .getDmaStatus().getH323RasPort()))) {
                    status = "SUCCESS";
                    logger.info("The DMA H.323 RAS port is as expected: "
                            + inputCmd.get("dmaH323RasPort"));
                } else {
                    status = "Failed";
                    logger.error("The DMA H.323 RAS port is not as expected: "
                            + inputCmd.get("dmaH323RasPort"));
                    return status
                            + " The DMA H.323 RAS port is not as expected: "
                            + inputCmd.get("dmaH323RasPort");
                }
                // Check the DMA H.225 signaling port
                if (inputCmd.get("dmaH225SignalingPort")
                        .equalsIgnoreCase(String.valueOf(dashboardRtn
                                .getDmaStatus().getH225SignalingPort()))) {
                    status = "SUCCESS";
                    logger.info("The DMA H.225 signaling port is as expected: "
                            + inputCmd.get("dmaH225SignalingPort"));
                } else {
                    status = "Failed";
                    logger.error("The DMA H.225 signaling port is not as expected: "
                            + inputCmd.get("dmaH225SignalingPort"));
                    return status
                            + " The DMA H.225 signaling port is not as expected: "
                            + inputCmd.get("dmaH225SignalingPort");
                }
                // Check the DMA SIP TCP port
                if (inputCmd.get("dmaSipTcpPort")
                        .equalsIgnoreCase(String.valueOf(dashboardRtn
                                .getDmaStatus().getSipTcpPort()))) {
                    status = "SUCCESS";
                    logger.info("The DMA SIP TCP port is as expected: "
                            + inputCmd.get("dmaSipTcpPort"));
                } else {
                    status = "Failed";
                    logger.error("The DMA SIP TCP port is not as expected: "
                            + inputCmd.get("dmaSipTcpPort"));
                    return status + " The DMA SIP TCP port is not as expected: "
                            + inputCmd.get("dmaSipTcpPort");
                }
                // Check the DMA SIP UDP port
                if (inputCmd.get("dmaSipUdpPort")
                        .equalsIgnoreCase(String.valueOf(dashboardRtn
                                .getDmaStatus().getSipUdpPort()))) {
                    status = "SUCCESS";
                    logger.info("The DMA SIP UDP port is as expected: "
                            + inputCmd.get("dmaSipUdpPort"));
                } else {
                    status = "Failed";
                    logger.error("The DMA SIP UDP port is not as expected: "
                            + inputCmd.get("dmaSipUdpPort"));
                    return status + " The DMA SIP UDP port is not as expected: "
                            + inputCmd.get("dmaSipUdpPort");
                }
                // Check the DMA SIP TLS port
                if (inputCmd.get("dmaSipTlsPort")
                        .equalsIgnoreCase(String.valueOf(dashboardRtn
                                .getDmaStatus().getSipTlsPort()))) {
                    status = "SUCCESS";
                    logger.info("The DMA SIP TLS port is as expected: "
                            + inputCmd.get("dmaSipTlsPort"));
                } else {
                    status = "Failed";
                    logger.error("The DMA SIP TLS port is not as expected: "
                            + inputCmd.get("dmaSipTlsPort"));
                    return status + " The DMA SIP TLS port is not as expected: "
                            + inputCmd.get("dmaSipTlsPort");
                }
                // Check the DMA site topology integrated status
                if (inputCmd.get("dmaSiteTopoIntegrated")
                        .equalsIgnoreCase("Yes")) {
                    if (dashboardRtn.getDmaStatus().isSiteTopoIntegrated()) {
                        status = "SUCCESS";
                        logger.info("The DMA site topology integrated status is as expected: "
                                + inputCmd.get("dmaSiteTopoIntegrated"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA site topology integrated status is not as expected: "
                                + inputCmd.get("dmaSiteTopoIntegrated"));
                        return status
                                + " The DMA site topology integrated status is not as expected: "
                                + inputCmd.get("dmaSiteTopoIntegrated");
                    }
                } else if (inputCmd.get("dmaSiteTopoIntegrated")
                        .equalsIgnoreCase("No")) {
                    if (!dashboardRtn.getDmaStatus().isSiteTopoIntegrated()) {
                        status = "SUCCESS";
                        logger.info("The DMA site topology integrated status is as expected: "
                                + inputCmd.get("dmaSiteTopoIntegrated"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA site topology integrated status is not as expected: "
                                + inputCmd.get("dmaSiteTopoIntegrated"));
                        return status
                                + " The DMA site topology integrated status is not as expected: "
                                + inputCmd.get("dmaSiteTopoIntegrated");
                    }
                } else {
                    status = "Failed";
                    logger.error("The format of the input DMA site topology integrated status is not correct. "
                            + "Please input Yes or No.");
                    return status
                            + " The format of the input DMA site topology integrated status is not correct. "
                            + "Please input Yes or No.";
                }
                // Check the DMA subscribed for devices notifications
                if (inputCmd.get("dmaSubscribedForDevices")
                        .equalsIgnoreCase("Yes")) {
                    if (dashboardRtn.getDmaStatus().isSubscribedForDevices()) {
                        status = "SUCCESS";
                        logger.info("The DMA subscribed for devices notifications status is as expected: "
                                + inputCmd.get("dmaSubscribedForDevices"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA subscribed for devices notifications status is not as expected: "
                                + inputCmd.get("dmaSubscribedForDevices"));
                        return status
                                + " The DMA subscribed for devices notifications status is not as expected: "
                                + inputCmd.get("dmaSubscribedForDevices");
                    }
                } else if (inputCmd.get("dmaSubscribedForDevices")
                        .equalsIgnoreCase("No")) {
                    if (!dashboardRtn.getDmaStatus().isSubscribedForDevices()) {
                        status = "SUCCESS";
                        logger.info("The DMA subscribed for devices notifications status is as expected: "
                                + inputCmd.get("dmaSubscribedForDevices"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA subscribed for devices notifications status is not as expected: "
                                + inputCmd.get("dmaSubscribedForDevices"));
                        return status
                                + " The DMA subscribed for devices notifications status is not as expected: "
                                + inputCmd.get("dmaSubscribedForDevices");
                    }
                } else {
                    status = "Failed";
                    logger.error("The format of the input DMA subscribed for devices notifications status is not correct. "
                            + "Please input Yes or No.");
                    return status
                            + " The format of the input DMA subscribed for devices notifications status is not correct. "
                            + "Please input Yes or No.";
                }
                // Check the DMA subscribed for active call notifications
                if (inputCmd.get("dmaSubscribedForActiveCalls")
                        .equalsIgnoreCase("Yes")) {
                    if (dashboardRtn.getDmaStatus()
                            .isSubscribedForActiveCalls()) {
                        status = "SUCCESS";
                        logger.info("The DMA subscribed for active call notifications status is as expected: "
                                + inputCmd.get("dmaSubscribedForActiveCalls"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA subscribed for active call notifications status is not as expected: "
                                + inputCmd.get("dmaSubscribedForActiveCalls"));
                        return status
                                + " The DMA subscribed for active call notifications status is not as expected: "
                                + inputCmd.get("dmaSubscribedForActiveCalls");
                    }
                } else if (inputCmd.get("dmaSubscribedForActiveCalls")
                        .equalsIgnoreCase("No")) {
                    if (!dashboardRtn.getDmaStatus()
                            .isSubscribedForActiveCalls()) {
                        status = "SUCCESS";
                        logger.info("The DMA subscribed for active call notifications status is as expected: "
                                + inputCmd.get("dmaSubscribedForActiveCalls"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA subscribed for active call notifications status is not as expected: "
                                + inputCmd.get("dmaSubscribedForActiveCalls"));
                        return status
                                + " The DMA subscribed for active call notifications status is not as expected: "
                                + inputCmd.get("dmaSubscribedForActiveCalls");
                    }
                } else {
                    status = "Failed";
                    logger.error("The format of the input DMA subscribed for active call notifications status is not correct. "
                            + "Please input Yes or No.");
                    return status
                            + " The format of the input DMA subscribed for active call notifications status is not correct. "
                            + "Please input Yes or No.";
                }
                // Check the DMA polling MCU pool orders
                if (inputCmd.get("dmaPollingMcuPoolOrders")
                        .equalsIgnoreCase("Yes")) {
                    if (dashboardRtn.getDmaStatus().isPollingMcuPoolOrders()) {
                        status = "SUCCESS";
                        logger.info("The DMA polling MCU pool orders is as expected: "
                                + inputCmd.get("dmaPollingMcuPoolOrders"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA polling MCU pool orders is not as expected: "
                                + inputCmd.get("dmaPollingMcuPoolOrders"));
                        return status
                                + " The DMA polling MCU pool orders is not as expected: "
                                + inputCmd.get("dmaPollingMcuPoolOrders");
                    }
                } else if (inputCmd.get("dmaPollingMcuPoolOrders")
                        .equalsIgnoreCase("No")) {
                    if (!dashboardRtn.getDmaStatus().isPollingMcuPoolOrders()) {
                        status = "SUCCESS";
                        logger.info("The DMA polling MCU pool orders is as expected: "
                                + inputCmd.get("dmaPollingMcuPoolOrders"));
                    } else {
                        status = "Failed";
                        logger.error("The DMA polling MCU pool orders is not as expected: "
                                + inputCmd.get("dmaPollingMcuPoolOrders"));
                        return status
                                + " The DMA polling MCU pool orders is not as expected: "
                                + inputCmd.get("dmaPollingMcuPoolOrders");
                    }
                } else {
                    status = "Failed";
                    logger.error("The format of the input DMA polling MCU pool orders is not correct. "
                            + "Please input Yes or No.");
                    return status
                            + " The format of the input DMA polling MCU pool orders is not correct. "
                            + "Please input Yes or No.";
                }
            } else {
                status = "Failed";
                logger.error("Some mandatory arguments are missing, please check your input commands.");
                return status
                        + " Some mandatory arguments are missing, please check your input commands.";
            }
        } else {
            status = "Failed";
            logger.error("Failed to get the dashboard data from XMA");
            return status + " Failed to get the dashboard data from XMA";
        }
        return status;
    }

    /**
     * Get the DMA attribute value on dashboard
     *
     * @see keyword=signalingAddress
     * @param keword
     *            The attribute name of DMA dashboard
     * @return The DMA attribute value on dashboard
     */
    public String getDashboardDmaSpecific() {
        final String result = "NotFound";
        final JDashboardDataResponse dashboardRtn = dmh
                .getDashboardData(userToken);
        final JDmaPodResponse dmaStatus = dashboardRtn.getDmaStatus();
        final String keyword = inputCmd.get("keyword");
        if (keyword.isEmpty()) {
            return result + ", keyword is empty!";
        }
        return CommonUtils.invokeGetMethod(dmaStatus, keyword);
    }

    /**
     * Get the license attribute value on dashboard
     *
     * @see keyword=total
     * @param keword
     *            The attribute name of license dashboard
     * @return The license attribute value on dashboard
     */
    public String getDashboardLicenseSpecific() {
        final String result = "NotFound";
        final JDashboardDataResponse dashboardRtn = dmh
                .getDashboardData(userToken);
        final JLicensesPodResponse license = dashboardRtn.getLicenses();
        final String keyword = inputCmd.get("keyword");
        if (keyword.isEmpty()) {
            return result + ", keyword is empty!";
        }
        return CommonUtils.invokeGetMethod(license, keyword);
    }

    /**
     * The the Redundancy status attribute value on dashboard
     *
     * @see keyword=status
     * @param keyword
     *            The Redundancy status attribute name
     * @return The Redundancy status attribute value
     */
    public String getDashboardRedundancySpecific() {
        final String result = "NotFound";
        final String keyword = inputCmd.get("keyword");
        final JDashboardDataResponse dashboardInfo = dmh
                .getDashboardData(userToken);
        if (dashboardInfo == null || dashboardInfo.getRedundancy() == null) {
            logger.error("dashboard info or Redundancy status info is null!");
            return result + ", no Redundancy status on dashboard found!";
        }
        final JRedundancyPodResponse redundancyStatus = dashboardInfo
                .getRedundancy();
        if (redundancyStatus == null) {
            return result + ", no Redundancy status panal in dashboad.";
        }
        try {
            return CommonUtils.invokeGetMethod(redundancyStatus, keyword);
        } catch (SecurityException | IllegalArgumentException e) {
            logger.equals("No element for keyword " + keyword);
            e.printStackTrace();
            return "NotFound, exception is\n"
                    + CommonUtils.getExceptionStackTrace(e);
        }
    }

    /**
     * Get the Resource Manager Config attribute value on dashboard
     *
     * @see keyword=hardwareVersion
     * @param keword
     *            The attribute name of Resource Manager Config dashboard
     * @return The Resource Manager Config attribute value on dashboard
     */
    public String getDashboardResourceManagerConfigSpecific() {
        final String result = "NotFound";
        final JDashboardDataResponse dashboardRtn = dmh
                .getDashboardData(userToken);
        final JDashboardCMAConfig cmaConfig = dashboardRtn.getCmaConfig()
                .getCmaConfig();
        final String keyword = inputCmd.get("keyword");
        if (keyword.isEmpty()) {
            return result + ", keyword is empty!";
        }
        return CommonUtils.invokeGetMethod(cmaConfig, keyword);
    }

    /**
     * Get the license number from dashboard
     *
     * @see no param
     * @return The license number from dashboard
     */
    public String getLicenseNumberFromDashboard() {
        String number = "";
        final JDashboardDataResponse dashboardRtn = dmh
                .getDashboardData(userToken);
        number = String.valueOf(dashboardRtn.getLicenses().getTotal());
        return number;
    }

    /**
     * The the UMC dashboard attribute value
     *
     * @see keyword=status
     * @param keyword
     *            The UMC dashboard attribute name
     * @return The UMC dashboard value
     */
    public String getUmcDashboardDataSpecific() {
        final String keyword = inputCmd.get("keyword");
        final JUMCDashboardDataRequest dataRequest = new JUMCDashboardDataRequest();
        final JUMCTodaysScheduledConferencePodRequest todayscheduledconfs = new JUMCTodaysScheduledConferencePodRequest();
        todayscheduledconfs.setPodId("id-todayscheduledconfs");
        dataRequest.setUmc_Todays_Scheduled_Conference(todayscheduledconfs);
        final JUMCEndpointPiePodRequest endpointpie = new JUMCEndpointPiePodRequest();
        endpointpie.setPodId("id-endpointpie");
        dataRequest.setUmc_Endpoint_Pie(endpointpie);
        final JUMCUserPodRequest umcUser = new JUMCUserPodRequest();
        umcUser.setPodId("id-users");
        dataRequest.setUmc_User(umcUser);
        final JUMCEndpointPodRequest endpointnumber = new JUMCEndpointPodRequest();
        endpointnumber.setPodId("id-endpointnumber");
        dataRequest.setUmc_Endpoint(endpointnumber);
        final JLicensesPodRequest licenses = new JLicensesPodRequest();
        licenses.setPodId("id-licenses");
        dataRequest.setLicenses(licenses);
        final JUMCCPUMemoryDiskPodRequest cpu = new JUMCCPUMemoryDiskPodRequest();
        cpu.setPodId("id-cpu");
        dataRequest.setUmc_Cpumemdisk(cpu);
        final JUMCTodaysConferenceStatPodRequest todayconferencesstatistics = new JUMCTodaysConferenceStatPodRequest();
        todayconferencesstatistics.setPodId("id-todayconferencesstatistics");
        dataRequest.setUmc_Todays_Conference_Stat(todayconferencesstatistics);
        final JUMCSystemInfoPodRequest systemInfo = new JUMCSystemInfoPodRequest();
        cpu.setPodId("umc_system_info");
        dataRequest.setUmc_System_Info(systemInfo);
        final JUsersPodRequest usersloggedin = new JUsersPodRequest();
        cpu.setPodId("id-usersloggedin");
        dataRequest.setUsers(usersloggedin);
        final JRedundancyPodRequest redundancy = new JRedundancyPodRequest();
        redundancy.setPodId("id-redundancy");
        dataRequest.setRedundancy(redundancy);
        final JDmaPodRequest dmaStatus = new JDmaPodRequest();
        dmaStatus.setPodId("id-dmaStatus");
        dataRequest.setDmaStatus(dmaStatus);
        final JUMCDashboardDataResponse dataResponse = dmh
                .getUMCDashboardData(userToken, dataRequest);
        return CommonUtils.invokeGetMethod(dataResponse, keyword);
    }

    @Override
    protected void injectCmdArgs() {
        // Active host IP address on the redundancy pod
        put("activeHostIP", "");
        // Inactive host IP address on the redundancy pod
        put("inactiveHostIP", "");
        // Active host name on the redundancy pod
        put("activeHostName", "");
        // Inactive host name on the redundancy pod
        put("inactiveHostName", "");
        // Role status on the active host on the redundancy pod
        put("activeRole", "");
        // Role status on the inactive host on the redundancy pod
        put("inactiveRole", "");
        // Connection status on the active host on the redundancy pod
        put("activeStatus", "");
        // Connection status on the inactive host on the redundancy pod
        put("inactiveStatus", "");
        // Redundancy status on the redundancy pod: Configured or NotConfigured
        put("redundancyStatus", "");
        // The virtual IP address on the redundancy pod. It is optional since
        // XMA supports non-virtual IP scenario.
        put("virtualIP", "");
        // The virtual host name on the redundancy pod. It is mandatory.
        put("vhostName", "");
        // Last fail over time on the redundancy pod
        put("lastFailoverTime", "");
        // Last fail over reason on the redundancy pod
        put("lastFailoverReason", "");
        // The redundancy server virtual IP address on the resource manager
        // configuration pod. If
        // virtual IP is not configured, will show the virtual host name
        put("redundancyIP", "");
        // Total number of licenses on the license pod
        put("totalLicenses", "");
        // Licenses in use number on the licenses pod
        put("usedLicenses", "");
        // DMA status, Up or Down
        put("dmaStatus", "");
        // DMA name
        put("dmaName", "");
        // DMA license status, Active or Inactive
        put("dmaLicenseStatus", "");
        // DMA SIP signaling enabled, Yes or No
        put("sipSignalingEnabled", "");
        // DMA H.323 signaling enabled, Yes or No
        put("h323SignalingEnabled", "");
        // DMA signaling address. DMA IP address.
        put("dmaSignalingAddress", "");
        // DMA H.323 RAS port, 1719
        put("dmaH323RasPort", "");
        // DMA H.225 signaling port, 1720
        put("dmaH225SignalingPort", "");
        // DMA SIP TCP port, 5060
        put("dmaSipTcpPort", "");
        // DMA SIP UDP port, 5060
        put("dmaSipUdpPort", "");
        // DMA SIP TLS port, 5061
        put("dmaSipTlsPort", "");
        // DMA site topologyintegrated, Yes or NO
        put("dmaSiteTopoIntegrated", "");
        // DMA subscribed for devices notifications, Yes or No
        put("dmaSubscribedForDevices", "");
        // DMA subscribed for active call notifications
        put("dmaSubscribedForActiveCalls", "");
        // DMA polling MCU pool orders
        put("dmaPollingMcuPoolOrders", "");
        // keyword for attribute
        put("keyword", "");
    }
}
