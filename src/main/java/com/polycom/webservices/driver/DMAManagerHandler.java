package com.polycom.webservices.driver;

import java.net.URL;
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
import com.polycom.webservices.DMAManager.JCredentials;
import com.polycom.webservices.DMAManager.JDMAManager;
import com.polycom.webservices.DMAManager.JDMAManager_Service;
import com.polycom.webservices.DMAManager.JDmaLocalCluster;
import com.polycom.webservices.DMAManager.JWebResult;

/**
 * DMA handler
 *
 * @author Lingxi Zhao
 *
 */
public class DMAManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("DMAManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JDMAManager");
    private JDMAManager        port;

    /**
     * Construction of the DMAManagerHandler class
     */
    public DMAManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JDMAManager) jsonInvocationHandler
                    .getProxy(JDMAManager.class);
        } else {
            final URL wsdlURL = DMAManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JDMAManager.wsdl");
            final JDMAManager_Service ss = new JDMAManager_Service(wsdlURL,
                    SERVICE_NAME);
            port = ss.getJDMAManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext().put(
                                             BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                             webServiceUrl + "/JDMAManager");
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
     * add DMA
     *
     * @param userToken
     * @param deviceName
     * @param description
     * @param ipAddress
     * @param httpPort
     * @param username
     * @param password
     * @param percentPortAllocation
     * @param mcuPoolOrderSource
     * @param callServer
     * @param supportDmaFailOver
     * @return
     */
    public JWebResult addDMA(final String userToken,
                             final String deviceName,
                             final String description,
                             final String ipAddress,
                             final Integer httpPort,
                             final String username,
                             final String password,
                             final int percentPortAllocation,
                             final boolean mcuPoolOrderSource,
                             final boolean callServer,
                             final boolean supportDmaFailOver) {
        System.out.println("Invoking addDMA...");
        final com.polycom.webservices.DMAManager.JCredentials _addDMA_credentials = new JCredentials();
        _addDMA_credentials.setUserToken(userToken);
        final JDmaLocalCluster _addDMA_dma = new JDmaLocalCluster();
        _addDMA_dma.setDeviceName(deviceName);
        _addDMA_dma.setDescription(description);
        _addDMA_dma.setIpAddress(ipAddress);
        _addDMA_dma.setHttpPort(httpPort);
        _addDMA_dma.setUsername(username);
        _addDMA_dma.setPassword(password);
        _addDMA_dma.setPercentPortAllocation(percentPortAllocation);
        _addDMA_dma.setMcuPoolOrderSource(mcuPoolOrderSource);
        _addDMA_dma.setCallServer(callServer);
        _addDMA_dma.setSupportDmaFailOver(supportDmaFailOver);
        final JWebResult _addDMA__return = port.addDMA(_addDMA_credentials,
                                                       _addDMA_dma);
        System.out.println("addDMA.result=" + _addDMA__return);
        return _addDMA__return;
    }

    /**
     * delete DMA
     *
     * @param userToken
     * @param dmaId
     * @return
     */
    public JWebResult deleteDMA(final String userToken, final String dmaId) {
        System.out.println("Invoking deleteDMA...");
        final com.polycom.webservices.DMAManager.JCredentials _deleteDMA_credentials = new JCredentials();
        _deleteDMA_credentials.setUserToken(userToken);
        final String _deleteDMA_dmaId = dmaId;
        final JWebResult _deleteDMA__return = port
                .deleteDMA(_deleteDMA_credentials, _deleteDMA_dmaId);
        System.out.println("addDMA.result=" + _deleteDMA__return);
        return _deleteDMA__return;
    }

    /**
     * Get DMA by device UUID
     *
     * @param userToken
     * @param deviceUUID
     * @return
     */
    public JDmaLocalCluster getDMA(final String userToken,
                                   final String deviceUUID) {
        logger.info("Invoking getDMA...");
        final JCredentials _getDMA_credentials = new JCredentials();
        _getDMA_credentials.setUserToken(userToken);
        final String _getDMA_deviceUUID = deviceUUID;
        final Holder<JDmaLocalCluster> _getDMA_detail = new Holder<JDmaLocalCluster>();
        final JWebResult _getDMA__return = port.getDMA(_getDMA_credentials,
                                                       _getDMA_deviceUUID,
                                                       _getDMA_detail);
        System.out.println("getDMA.result=" + _getDMA__return);
        System.out.println("getDMA._getDMA_detail=" + _getDMA_detail.value);
        return _getDMA_detail.value;
    }

    /**
     * Get DMA
     *
     * @param userToken
     * @return
     */
    public List<JDmaLocalCluster> getDMAs(final String userToken) {
        System.out.println("Invoking getDMA...");
        logger.info("Invoking getDMA...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JDmaLocalCluster>> dmasRetrieved = new Holder<List<JDmaLocalCluster>>();
        port.getDMAs(credentials, dmasRetrieved);
        return dmasRetrieved.value;
    }

    /**
     * Update specified DMA
     *
     * @param userToken
     * @param dma
     * @return
     */
    public JWebResult updateDMA(final String userToken,
                                final JDmaLocalCluster dma) {
        logger.info("Invoking updateDMA...");
        final JCredentials _updateDMA_credentials = new JCredentials();
        _updateDMA_credentials.setUserToken(userToken);
        final JDmaLocalCluster _updateDMA_dma = dma;
        final JWebResult _updateDMA__return = port
                .updateDMA(_updateDMA_credentials, _updateDMA_dma);
        System.out.println("updateDMA.result=" + _updateDMA__return);
        return _updateDMA__return;
    }
}