package com.polycom.webservices;

import java.io.IOException;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.DMAManagerHandler;
import com.polycom.sqa.xma.webservices.driver.NetworkDeviceManagerHandler;
import com.polycom.webservices.DMAManager.JDmaLocalCluster;
import com.polycom.webservices.DMAManager.JStatus;
import com.polycom.webservices.DMAManager.JWebResult;
import com.polycom.webservices.NetworkDeviceManager.NetworkDeviceForList;

/**
 * DMA handler. This class will handle the webservice request of DMA module
 *
 * @author wbchao
 *
 */
public class DMAHandler extends XMAWebServiceHandler {
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
        // "deviceName=dma201 description=automation ipAddress=172.21.120.201
        // httpPort=8443 dmaUsername=admin dmaPassword=admin
        // percentPortAllocation=100 mcuPoolOrderSource=true callServer=true
        // supportDmaFailOver=true ";
        // final String method = "deleteDMA ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "deviceName=aaa ipAddress=10.220.202.156 ";
        final String method = "updateDMA ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "ipAddress=10.220.202.209 keyword=deviceName ";
        final String params2 = "ipAddress=172.21.125.226 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JDMAManager DMAManager "
                + method + auth + params2;
        final DMAHandler handler = new DMAHandler(command);
        final String result = handler.build();
        System.out.println("result==" + result);
    }

    private final DMAManagerHandler dmaManagerHandler;

    public DMAHandler(final String cmd) throws IOException {
        super(cmd);
        dmaManagerHandler = new DMAManagerHandler(webServiceUrl);
    }

    /**
     * Add DMA to XMA
     *
     * @see deviceName=DMA226 <br/>
     *      description=DMA604 <br/>
     *      ipAddress=$dmaAddr <br/>
     *      httpPort=8443 <br/>
     *      dmaUsername=$dmausername <br/>
     *      dmaPassword=$dmauserpassword <br/>
     *      percentPortAllocation=100 <br/>
     *      mcuPoolOrderSource=true <br/>
     *      callServer=true <br/>
     *      supportDmaFailOver=false
     *
     * @param ipAddress
     *            The DMA ip
     * @param mcuPoolOrderSource
     *            The MCU pool order source(Optional, default is false)
     * @param callServer
     *            Whether use call server(Optional, default is false)
     * @param supportDmaFailOver
     *            Whether enable DMA failover(Optional, default is false)
     * @param httpPort
     *            The DMA http port
     * @param percentPortAllocation
     *            The percent port allocation
     * @param deviceName
     *            The DMA name
     * @param description
     *            The DMA description
     * @param dmaUsername
     *            The DMA login username
     * @param dmaPassword
     *            The DMA login password
     *
     * @return The result
     */
    public String addDMA() {
        boolean mcuPoolOrderSource = false;
        boolean callServer = false;
        boolean supportDmaFailOver = false;
        int httpPort = 0;
        int percentPortAllocation = 0;
        final String status = "Failed";
        final String dmaIp = inputCmd.get("ipAddress");
        final JDmaLocalCluster dma = getDMAByIp(dmaIp);
        if (dma != null) {
            return "SUCCESS, the DMA with " + dmaIp
                    + " is already added on XMA.";
        }
        if (inputCmd.get("mcuPoolOrderSource").equalsIgnoreCase("true")) {
            mcuPoolOrderSource = true;
        } else {
            mcuPoolOrderSource = false;
        }
        if (inputCmd.get("callServer").equalsIgnoreCase("true")) {
            callServer = true;
        } else {
            callServer = false;
        }
        if (inputCmd.get("supportDmaFailOver").equalsIgnoreCase("true")) {
            supportDmaFailOver = true;
        } else {
            supportDmaFailOver = false;
        }
        httpPort = Integer.parseInt(inputCmd.get("httpPort"));
        percentPortAllocation = Integer
                .parseInt(inputCmd.get("percentPortAllocation"));
        final JWebResult result = dmaManagerHandler
                .addDMA(userToken,
                        inputCmd.get("deviceName"),
                        inputCmd.get("description"),
                        dmaIp,
                        new Integer(httpPort),
                        inputCmd.get("dmaUsername"),
                        inputCmd.get("dmaPassword"),
                        percentPortAllocation,
                        mcuPoolOrderSource,
                        callServer,
                        supportDmaFailOver);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("DMA successfully added into XMA.");
            return "SUCCESS";
        } else {
            logger.error("Failed, " + result.getMessages());
            return "Failed, " + result.getMessages();
        }
    }

    /**
     * Delete the DMA from XMA
     *
     * @see deviceName=DMA226 <br/>
     *      ipAddress=$dmaAddr
     *
     * @param deviceName
     *            The DMA name
     * @param ipAddress
     *            The DMA ip
     * @return The result
     */
    public String deleteDMA() {
        String status = "Failed";
        final NetworkDeviceManagerHandler ndManagerHandler = new NetworkDeviceManagerHandler(
                webServiceUrl);
        final List<NetworkDeviceForList> deviceList = ndManagerHandler
                .getNetworkDevicesForList(userToken);
        String dmaId = "";
        final String deviceName = inputCmd.get("deviceName");
        final String ipAddress = inputCmd.get("ipAddress");
        String msg = "";
        for (final NetworkDeviceForList networkDevice : deviceList) {
            if (networkDevice.getDeviceName().equals(deviceName)
                    || networkDevice.getIpAddress().equals(ipAddress)) {
                dmaId = networkDevice.getDeviceUUID();
                break;
            }
        }
        if (dmaId.isEmpty()) {
            msg = "the DMA " + ipAddress
                    + " is not exist on XMA, no need to delete";
            status = "SUCCESS, " + msg;
        }
        final JWebResult result = dmaManagerHandler.deleteDMA(userToken, dmaId);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            msg = "Delete DMA " + ipAddress + " on XMA successfully";
            status = "SUCCESS";
            logger.info(msg);
        } else {
            msg = "failed to delete DMA " + ipAddress + " on XMA";
            status = "Failed, " + msg;
            logger.info(msg);
        }
        return status;
    }

    /**
     * Internal method, get the DMA by ip
     *
     * @param dmaIp
     *            The DMA ip
     * @return The DMA object JDmaLocalCluster
     */
    private JDmaLocalCluster getDMAByIp(final String dmaIp) {
        final List<JDmaLocalCluster> dmaList = dmaManagerHandler
                .getDMAs(userToken);
        for (final JDmaLocalCluster dma : dmaList) {
            if (dmaIp.equals(dma.getIpAddress())) {
                return dma;
            }
        }
        return null;
    }

    /**
     * Get the DMA specified attribute value
     *
     * @see ipAddress=$dmaAddr1 <br/>
     *      keyword=deviceStatus
     *
     * @param ipAddress
     *            The DMA ip
     * @param keyword
     *            The DMA attribute name
     * @return The DMA specified attribute value
     */
    public String getDmaSpecific() {
        final String ipAddress = inputCmd.get("ipAddress");
        final String keyword = inputCmd.get("keyword");
        final JDmaLocalCluster dma = getDMAByIp(ipAddress);
        return CommonUtils.invokeGetMethod(dma, keyword);
    }

    @Override
    protected void injectCmdArgs() {
        put("deviceName", "");
        put("description", "");
        put("ipAddress", "");
        put("newIpAddress", "");
        put("httpPort", "");
        put("dmaUsername", "");
        put("dmaPassword", "");
        put("percentPortAllocation", "");
        put("mcuPoolOrderSource", "");
        put("callServer", "");
        put("supportDmaFailOver", "");
    }

    /**
     * Update the DMA
     *
     * @see ipAddress=$dmaAddr3
     *
     * @param ipAddress
     *            The DMA ip
     * @param newIpAddress
     *            The new DMA ip
     * @param mcuPoolOrderSource
     *            The MCU pool order source(Optional, default is false)
     * @param callServer
     *            Whether use call server(Optional, default is false)
     * @param supportDmaFailOver
     *            Whether enable DMA failover(Optional, default is false)
     * @param httpPort
     *            The DMA http port(Optional)
     * @param percentPortAllocation
     *            The percent port allocation(Optional)
     * @param deviceName
     *            The DMA name(Optional)
     * @param description
     *            The DMA description(Optional)
     * @param dmaUsername
     *            The DMA login username(Optional)
     * @param dmaPassword
     *            The DMA login password(Optional)
     *
     * @return The result
     * @return
     */
    public String updateDma() {
        String status = "Failed";
        boolean mcuPoolOrderSource = false;
        boolean callServer = false;
        boolean supportDmaFailOver = false;
        int percentPortAllocation = 0;
        final JDmaLocalCluster dma = getDMAByIp(inputCmd.get("ipAddress"));
        if (dma == null) {
            status = "Failed";
            logger.error("Cannot find the specified DMA: "
                    + inputCmd.get("ipAddress"));
            return status + " Cannot find the specified DMA: "
                    + inputCmd.get("ipAddress");
        }
        // Update the DMA
        if (!inputCmd.get("newIpAddress").isEmpty()) {
            dma.setIpAddress(inputCmd.get("newIpAddress"));
        }
        if (!inputCmd.get("deviceName").isEmpty()) {
            dma.setDeviceName(inputCmd.get("deviceName"));
        }
        if (!inputCmd.get("description").isEmpty()) {
            dma.setDescription(inputCmd.get("description"));
        }
        if (!inputCmd.get("httpPort").isEmpty()) {
            dma.setHttpPort(Integer.valueOf(inputCmd.get("httpPort")));
        }
        if (!inputCmd.get("dmaUsername").isEmpty()) {
            dma.setUsername(inputCmd.get("dmaUsername"));
        }
        if (!inputCmd.get("dmaPassword").isEmpty()) {
            dma.setPassword(inputCmd.get("dmaPassword"));
        }
        if (!inputCmd.get("mcuPoolOrderSource").isEmpty()) {
            if (inputCmd.get("mcuPoolOrderSource").equalsIgnoreCase("true")) {
                mcuPoolOrderSource = true;
            } else {
                mcuPoolOrderSource = false;
            }
            dma.setMcuPoolOrderSource(mcuPoolOrderSource);
        }
        if (!inputCmd.get("callServer").isEmpty()) {
            if (inputCmd.get("callServer").equalsIgnoreCase("true")) {
                callServer = true;
            } else {
                callServer = false;
            }
            dma.setCallServer(callServer);
        }
        if (!inputCmd.get("supportDmaFailOver").isEmpty()) {
            if (inputCmd.get("supportDmaFailOver").equalsIgnoreCase("true")) {
                supportDmaFailOver = true;
            } else {
                supportDmaFailOver = false;
            }
            dma.setSupportDmaFailOver(supportDmaFailOver);
        }
        if (!inputCmd.get("percentPortAllocation").isEmpty()) {
            percentPortAllocation = Integer
                    .parseInt(inputCmd.get("percentPortAllocation"));
            dma.setPercentPortAllocation(percentPortAllocation);
        }
        if (dmaManagerHandler.updateDMA(userToken, dma).getStatus()
                .equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("DMA " + inputCmd.get("ipAddress")
                    + " updated successfully.");
        } else {
            status = "Failed";
            logger.error("DMA " + inputCmd.get("ipAddress")
                    + " was failed to update.");
            return status + " DMA " + inputCmd.get("ipAddress")
                    + " was failed to update.";
        }
        return status;
    }
}