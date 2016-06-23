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
import com.polycom.webservices.ReportManager.JCDRArchiveSettings;
import com.polycom.webservices.ReportManager.JConferenceCDRDetail;
import com.polycom.webservices.ReportManager.JConferenceCDRSummary;
import com.polycom.webservices.ReportManager.JConferenceTypeSummary;
import com.polycom.webservices.ReportManager.JCredentials;
import com.polycom.webservices.ReportManager.JEndpointCDR;
import com.polycom.webservices.ReportManager.JReportManager;
import com.polycom.webservices.ReportManager.JReportManager_Service;
import com.polycom.webservices.ReportManager.JReportMetadata;
import com.polycom.webservices.ReportManager.JReportRequest;
import com.polycom.webservices.ReportManager.JUIUtcDateTime;
import com.polycom.webservices.ReportManager.JWebResult;

/**
 * Device Manager Handler
 *
 * @author Tao Chen
 *
 */
public class ReportManagerHandler {
    protected static Logger      logger       = Logger
            .getLogger("ReportManagerHandler");
    private static final QName   SERVICE_NAME = new QName(
                                                          "http://polycom.com/WebServices", "JReportManager");
    private final JReportManager port;

    /**
     * Construction of ReportManagerHandler class
     */
    public ReportManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JReportManager) jsonInvocationHandler
                    .getProxy(JReportManager.class);
        } else {
            final URL wsdlURL = ReportManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JReportManager.wsdl");
            final JReportManager_Service ss = new JReportManager_Service(
                                                                         wsdlURL, SERVICE_NAME);
            port = ss.getJReportManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext().put(
                                             BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                             webServiceUrl + "/JReportManager");
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
     * Get Conference Type report file path
     *
     * @param userToken
     * @param belongsToAreaUgpId
     * @param startDate
     * @param endDate
     * @param isConf
     * @return
     */
    public String exportConferenceDetailRecords(final String userToken,
            final int belongsToAreaUgpId,
            final JUIUtcDateTime startDate,
            final JUIUtcDateTime endDate,
            final boolean isConf) {
        System.out.println("Invoking exportConferenceDetailRecords...");
        logger.info("Invoking exportConferenceDetailRecords...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<String> filePath = new Holder<String>();
        port.exportConferenceDetailRecords(credentials,
                                           belongsToAreaUgpId,
                                           startDate,
                                           endDate,
                                           isConf,
                                           filePath);
        return filePath.value;
    }

    /**
     * Force CDR Archive in the report administration page
     *
     * @param userToken
     * @param archiveSettings
     * @return
     */
    public JWebResult forceCDRArchive(final String userToken,
            final JCDRArchiveSettings archiveSettings) {
        logger.info("Invoking forceCDRArchive...");
        final JUIUtcDateTime startDate = new JUIUtcDateTime();
        final JUIUtcDateTime endDate = new JUIUtcDateTime();
        startDate.setUnixTime(0);
        endDate.setUnixTime(0);

        final JCredentials _forceCDRArchive_credentials = new JCredentials();
        _forceCDRArchive_credentials.setUserToken(userToken);
        final JCDRArchiveSettings _forceCDRArchive_archiveSettings = archiveSettings;
        final JWebResult _forceCDRArchive__return = port
                .forceCDRArchive(_forceCDRArchive_credentials,
                                 _forceCDRArchive_archiveSettings,
                                 startDate,
                                 endDate);
        return _forceCDRArchive__return;
    }

    /**
     * Get the report metadata
     *
     * @param userToken
     * @param request
     * @return
     */
    public JReportMetadata generateReport(final String userToken,
            final JReportRequest request) {
        System.out.println("Invoking generateReport...");
        logger.info("Invoking generateReport...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JReportMetadata> metadata = new Holder<JReportMetadata>();
        port.generateReport(credentials, request, metadata);
        return metadata.value;
    }

    /**
     * Get the archrive settings in the report administration page
     *
     * @param userToken
     * @return
     */
    public JCDRArchiveSettings getArchiveSettings(final String userToken) {
        logger.info("Invoking getArchiveSettings...");
        final JCredentials _getArchiveSettings_credentials = new JCredentials();
        _getArchiveSettings_credentials.setUserToken(userToken);
        final Holder<JCDRArchiveSettings> archiveSettings = new Holder<JCDRArchiveSettings>();
        port.getArchiveSettings(_getArchiveSettings_credentials,
                                archiveSettings);
        return archiveSettings.value;
    }

    /**
     * Get conference details
     *
     * @param userToken
     * @param startDate
     * @param endDate
     * @return
     */
    public List<JConferenceCDRDetail> getConferenceDetails(
            final String userToken,
            final JUIUtcDateTime startDate,
            final JUIUtcDateTime endDate) {
        System.out.println("Invoking getConferenceDetails...");
        logger.info("Invoking getConferenceDetails...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JConferenceCDRDetail>> conferenceDetails = new Holder<List<JConferenceCDRDetail>>();
        port.getConferenceDetails(credentials,
                                  startDate,
                                  endDate,
                                  conferenceDetails);
        return conferenceDetails.value;
    }

    /**
     * Get conference summary
     *
     * @param userToken
     * @param startDate
     * @param endDate
     * @return
     */
    public List<JConferenceCDRSummary> getConferenceSummaries(
            final String userToken,
            final JUIUtcDateTime startDate,
            final JUIUtcDateTime endDate) {
        System.out.println("Invoking getConferenceSummaries...");
        logger.info("Invoking getConferenceSummaries...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JConferenceCDRSummary>> conferenceSummaries = new Holder<List<JConferenceCDRSummary>>();
        port.getConferenceSummaries(credentials,
                                    startDate,
                                    endDate,
                                    conferenceSummaries);
        return conferenceSummaries.value;
    }

    /**
     * Get conference type summary
     *
     * @param userToken
     * @param startDate
     * @param endDate
     * @return
     */
    public List<JConferenceTypeSummary> getConferenceTypeSummaries(
            final String userToken,
            final JUIUtcDateTime startDate,
            final JUIUtcDateTime endDate) {
        System.out.println("Invoking getConferenceTypeSummaries...");
        logger.info("Invoking getConferenceTypeSummaries...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JConferenceTypeSummary>> conferenceTypeSummaries = new Holder<List<JConferenceTypeSummary>>();
        port.getConferenceTypeSummaries(credentials,
                                        0,
                                        startDate,
                                        endDate,
                                        conferenceTypeSummaries);
        return conferenceTypeSummaries.value;
    }

    /**
     * Get endpoint usage cdr
     *
     * @param userToken
     * @param serialNumbers
     * @param startDate
     * @param endDate
     * @return
     */
    public List<JEndpointCDR> getEndpointCDRs(final String userToken,
            final List<String> serialNumbers,
            final JUIUtcDateTime startDate,
            final JUIUtcDateTime endDate) {
        System.out.println("Invoking getEndpointCDRs...");
        logger.info("Invoking getEndpointCDRs...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JEndpointCDR>> reportCDRs = new Holder<List<JEndpointCDR>>();
        port.getEndpointCDRs(credentials,
                             serialNumbers,
                             startDate,
                             endDate,
                             reportCDRs);
        return reportCDRs.value;
    }

    /**
     * Save the CDR setting in the report administration page
     *
     * @param userToken
     * @param archiveSettings
     * @return
     */
    public JWebResult saveArchiveSettings(final String userToken,
            final JCDRArchiveSettings archiveSettings) {
        logger.info("Invoking saveArchiveSettings...");
        final JCredentials _saveArchiveSettings_credentials = new JCredentials();
        _saveArchiveSettings_credentials.setUserToken(userToken);
        final JCDRArchiveSettings _saveArchiveSettings_archiveSettings = archiveSettings;
        final JWebResult _saveArchiveSettings__return = port
                .saveArchiveSettings(_saveArchiveSettings_credentials,
                                     _saveArchiveSettings_archiveSettings);
        return _saveArchiveSettings__return;
    }
}
