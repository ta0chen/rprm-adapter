package com.polycom.sqa.xma.webservices;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.cdr.CsvParser;
import com.polycom.sqa.xma.webservices.cdr.XlsParser;
import com.polycom.sqa.xma.webservices.cdr.bean.ConferenceTypeCdr;
import com.polycom.sqa.xma.webservices.cdr.bean.ConferenceUsageCdr;
import com.polycom.sqa.xma.webservices.cdr.bean.EndpointUsageCdr;
import com.polycom.sqa.xma.webservices.cdr.bean.Participant;
import com.polycom.sqa.xma.webservices.driver.DeviceManagerHandler;
import com.polycom.sqa.xma.webservices.driver.ReportManagerHandler;
import com.polycom.webservices.DeviceManager.JEndpointForList;
import com.polycom.webservices.ReportManager.JCDRArchiveSettings;
import com.polycom.webservices.ReportManager.JConferenceCDRDetail;
import com.polycom.webservices.ReportManager.JConferenceCDRSummary;
import com.polycom.webservices.ReportManager.JConferenceTypeSummary;
import com.polycom.webservices.ReportManager.JEndpointCDR;
import com.polycom.webservices.ReportManager.JReportMetadata;
import com.polycom.webservices.ReportManager.JReportParamType;
import com.polycom.webservices.ReportManager.JReportParameter;
import com.polycom.webservices.ReportManager.JReportRequest;
import com.polycom.webservices.ReportManager.JReportType;
import com.polycom.webservices.ReportManager.JStatus;
import com.polycom.webservices.ReportManager.JUIUtcDateTime;
import com.polycom.webservices.ReportManager.JWebResult;

//import bsh.This;
import jxl.read.biff.BiffException;

/**
 * Report handler. This class will handle the webservice request of Report
 * module
 *
 * @author wbchao
 *
 */
public class ReportHandler extends XMAWebServiceHandler {
    /**
     * The default folder to store cdr files
     */
    public final static String CDR_FOLDER = "cdr";

    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args)
            throws ParseException, IOException {
        // final String method = "downloadConferenceUsageReportCSVFile ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = " ";
        // final String method = "getConferenceTypeInfoCSV ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "filename=confSummaryReport.csv date=9/2014 keyword=Adhoc ";
        // final String method = "getEndpointUsageInfoXls ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "filename=ENDPOINT_CDR_DETAIL-8.xls
        // endpointName=cccdebuguser3GroupSeries date=1412737733000
        // keyword=CallDuration ";
        // final String method = "getConferenceUsageInfoCsv ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "filename=CONFLIST_DETAIL_ALL_CSV-54.csv scheduledDate=1411700503000
        // keyword=TotalActualParticipants ";
        // final String method = "getConferenceSummaries ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "scheduledDate=1411525533000 keyword=ConfId ";
        // final String method = "getConferenceDetails ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "confId=854c414e-e819-4e63-b1f0-a984155920cf
        // participant=debuguser2~bbb keyword=size ";
        // final String method = "getEndpointUsageInfoCsv ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "filename=ENDPOINT_CDR_DETAIL_ALL_CSV-27.csv
        // endpointName=cccdebuguser3GroupSeries keyword=CallDuration
        // startDate=1412745258000 date=1412745628000 ";
        // final String method = "getConferenceUsageInfoCsv ";
        // final String method = "downloadConferenceUsageReportCsvFile ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params1 = "filename=CONFLIST_DETAIL_ALL_CSV-45.csv
        // keyword=TotalActualParticipants scheduleDate=1442888895000 ";
        // final String params1 = "startDate=1442888626000 ";
        // final String method = "getEndpointCDRs ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "endpointName=cccdebuguser3GroupSeries keyword=CallDuration
        // startDate=1412745258000 scheduledDate=1412745628000 ";

        // final String method = "saveArchiveSettings ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params1 = "keepInterval=30 enableArchive=true
        // transmissionFrequency=7 ftpHost=10.220.202.103 ftpPort=21
        // ftpUsername=xma ftpPassword=Polycom123 ";

        final String method = "forceCDRArchive ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params1 = " ";

        final String command = "https://172.21.120.181:8443/PlcmRmWeb/rest/JReportManager ReportManager "
                + method + auth + params1;
        final ReportHandler handler = new ReportHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    private final SimpleDateFormat     defalutDF          = new SimpleDateFormat(
                                                                                 "MM-dd-yyyy HH:mm:ss", Locale.ENGLISH);
    private final String               DEFAULT_START_DATE = "09-01-2014 00:00:00";
    private final ReportManagerHandler reportManagerHandler;

    public ReportHandler(final String cmd) throws IOException {
        super(cmd);
        reportManagerHandler = new ReportManagerHandler(webServiceUrl);
    }

    /**
     * Delete the cdr file on TCL server
     *
     * @see filename=$id
     * @param filename
     *            The cdr file name
     * @return The result
     */
    public String deleteCdrFile() {
        final String filename = inputCmd.get("filename");
        final File file = new File(CDR_FOLDER + File.separator + filename);
        file.delete();
        return "SUCCESS";
    }

    /**
     * Download Conference Type Report Csv File
     *
     * @see This method is not used in TCL
     *
     * @param startDate
     *            The start date in millions
     * @param endDate
     *            Date The end date in millions(Optional)
     * @return The result
     */
    public String downloadConferenceTypeReportCsvFile() {
        final String status = "Failed";
        final JUIUtcDateTime startDate = new JUIUtcDateTime();
        final String startDateStr = inputCmd.get("startDate");
        if (startDateStr.isEmpty()) {
            startDate.setUnixTime(getDefaultStartDate());
        } else {
            startDate.setUnixTime(Long.parseLong(startDateStr));
        }
        final JUIUtcDateTime endDate = new JUIUtcDateTime();
        final String endDateStr = inputCmd.get("endDate");
        if (endDateStr.isEmpty()) {
            endDate.setUnixTime(System.currentTimeMillis());
        } else {
            endDate.setUnixTime(Long.parseLong(endDateStr));
        }
        boolean isConf = true;
        if (inputCmd.get("isConf").equalsIgnoreCase("false")) {
            isConf = false;
        }
        final String fileName = reportManagerHandler
                .exportConferenceDetailRecords(userToken,
                                               0,
                                               startDate,
                                               endDate,
                                               isConf);
        final Pattern p = Pattern.compile("https?://(.*):.*");
        final Matcher m = p.matcher(cmdArgs[0]);
        String xmaIp = "localhost";
        if (m.find()) {
            xmaIp = m.group(1);
        }
        final StringBuffer url = new StringBuffer("https://" + xmaIp
                                                  + ":8443/PlcmRmWeb/FileDownload?DownloadType=REPORT");
        url.append("&").append("Credentials=" + userToken);
        url.append("&").append("Modifier=" + fileName);
        final String path = CDR_FOLDER + File.separator + fileName;
        try {
            CommonUtils.downloadFile(path,
                                     url.toString(),
                                     "Credentials=" + userToken);
        } catch (final IOException e) {
            logger.error(e.getMessage());
            return status;
        }
        return fileName;
    }

    /**
     * Download Conference Usage Report Csv File to TCL server
     *
     * @see startDate=$downloadCdrStartDate
     *
     * @param startDate
     *            The start date in millions
     * @param endDate
     *            Date The end date in millions(Optional)
     * @return The result
     */
    public String downloadConferenceUsageReportCsvFile() {
        final String status = "Failed";
        final JReportRequest request = new JReportRequest();
        request.setOwnerUgpId(1);
        request.setReportType(JReportType.CONFLIST___DETAIL___ALL___CSV);
        final JReportParameter p1 = new JReportParameter();
        p1.setType(JReportParamType.START___DATE);
        final String startDateStr = inputCmd.get("startDate");
        if (startDateStr.isEmpty()) {
            p1.setValue(getDefaultStartDate() + "");
        } else {
            p1.setValue(Long.parseLong(startDateStr) + "");
        }
        final JReportParameter p2 = new JReportParameter();
        p2.setType(JReportParamType.END___DATE);
        final String endDate = inputCmd.get("endDate");
        if (endDate.isEmpty()) {
            p2.setValue(System.currentTimeMillis() + "");
        } else {
            p2.setValue(endDate);
        }
        request.getParameters().add(p1);
        request.getParameters().add(p2);
        final JReportMetadata reportMetadata = reportManagerHandler
                .generateReport(userToken, request);
        final String fileName = reportMetadata.getReportFileName();
        final Pattern p = Pattern.compile("https?://(.*):.*");
        final Matcher m = p.matcher(cmdArgs[0]);
        String xmaIp = "localhost";
        if (m.find()) {
            xmaIp = m.group(1);
        }
        final StringBuffer url = new StringBuffer("https://" + xmaIp
                                                  + ":8443/PlcmRmWeb/FileDownload?DownloadType=REPORT");
        url.append("&").append("Modifier=" + fileName);
        url.append("&").append("Credentials=" + userToken);
        final String path = CDR_FOLDER + File.separator + fileName;
        try {
            CommonUtils.downloadFile(path,
                                     url.toString(),
                                     "Credentials=" + userToken);
        } catch (final IOException e) {
            logger.error(e.getMessage());
            return status;
        }
        return fileName;
    }

    /**
     * Download Endpoint Usage Report File to TCL server
     *
     * @see startDate=$downloadCdrStartDate
     *
     * @param startDate
     *            The start date in millions
     * @param endDate
     *            Date The end date in millions(Optional)
     * @return The result
     */
    public String downloadEndpointUsageReportFile() {
        final String status = "Failed";
        final String endpointName = inputCmd.get("endpointName");
        final String serialNumber = inputCmd.get("serialNumber");
        final DeviceManagerHandler dmh = new DeviceManagerHandler(
                                                                  webServiceUrl);
        final List<JEndpointForList> eps = dmh
                .getDevicesFromEndpointCdr(userToken, endpointName);
        if (eps.isEmpty()) {
            final String errorMsg = "Cannot find the device with endpoint name "
                    + endpointName;
            logger.error(errorMsg);
            return status + ", " + errorMsg;
        }
        final JReportRequest request = new JReportRequest();
        request.setOwnerUgpId(1);
        final JReportParameter p1 = new JReportParameter();
        p1.setType(JReportParamType.START___DATE);
        final String startDateStr = inputCmd.get("startDate");
        if (startDateStr.isEmpty()) {
            p1.setValue(getDefaultStartDate() + "");
        } else {
            p1.setValue(Long.parseLong(startDateStr) + "");
        }
        final JReportParameter p2 = new JReportParameter();
        p2.setType(JReportParamType.END___DATE);
        final String endDate = inputCmd.get("endDate");
        if (endDate.isEmpty()) {
            p2.setValue(System.currentTimeMillis() + "");
        } else {
            p2.setValue(endDate);
        }
        final JReportParameter p3 = new JReportParameter();
        if ("xls".equalsIgnoreCase(inputCmd.get("reportType"))) {
            request.setReportType(JReportType.ENDPOINT___CDR___DETAIL);
            p3.setType(JReportParamType.ENDPOINT___ID);
            if (serialNumber.isEmpty()) {
                p3.setValue(eps.get(0).getSerialNumber());
            } else {
                p3.setValue(serialNumber);
            }
        } else {
            request.setReportType(JReportType.ENDPOINT___CDR___DETAIL___ALL___CSV);
            p3.setType(JReportParamType.ENDPOINT___LIST);
            final StringBuffer serialNumbers = new StringBuffer();
            for (final JEndpointForList ep : eps) {
                final List<JEndpointForList> endpointList = dmh
                        .getEndpoints4Paging(userToken, 2000);
                for (final JEndpointForList endpoint : endpointList) {
                    if (ep.getSerialNumber()
                            .equalsIgnoreCase(endpoint.getSerialNumber())) {
                        serialNumbers.append(",").append(ep.getSerialNumber());
                        break;
                    }
                }
            }
            p3.setValue(serialNumbers.substring(1));
        }
        request.getParameters().add(p1);
        request.getParameters().add(p2);
        request.getParameters().add(p3);
        final JReportMetadata reportMetadata = reportManagerHandler
                .generateReport(userToken, request);
        final String fileName = reportMetadata.getReportFileName();
        final Pattern p = Pattern.compile("https?://(.*):.*");
        final Matcher m = p.matcher(cmdArgs[0]);
        String xmaIp = "localhost";
        if (m.find()) {
            xmaIp = m.group(1);
        }
        final StringBuffer url = new StringBuffer("https://" + xmaIp
                                                  + ":8443/PlcmRmWeb/FileDownload?DownloadType=REPORT");
        // url.append("&").append("Credentials=" + userToken);
        url.append("&").append("Modifier=" + fileName);
        url.append("&").append("ClientId=");
        url.append("&").append("FileName=");
        final String path = CDR_FOLDER + File.separator + fileName;
        try {
            CommonUtils.downloadFile(path,
                                     url.toString(),
                                     "Credentials=" + userToken);
        } catch (final IOException e) {
            logger.error(e.getMessage());
            return status;
        }
        return fileName;
    }

    /**
     * Force CDR Archive in the report administration page
     *
     * @see
     *
     * @param
     *
     * @return
     */
    public String forceCDRArchive() {
        String status = "Failed";

        final JCDRArchiveSettings archiveSettings = reportManagerHandler
                .getArchiveSettings(userToken);
        final JWebResult result = reportManagerHandler
                .forceCDRArchive(userToken, archiveSettings);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Force CDR Archive successfully.");
        } else {
            status = "Failed";
            logger.error("Force CDR Archive failed.");
            return status + "Force CDR Archive failed.";
        }
        return status;
    }

    /**
     * Get Cdr Archive Setting Specific Information by keyword in the report
     * administration page
     *
     * @see keyword=$keyword
     *
     * @param keyword
     *            The key word in archive settings
     *
     * @return
     */
    public String getCdrArchiveSettingSpecificInfo() {
        final String keyword = inputCmd.get("keyword");
        final JCDRArchiveSettings archiveSettings = reportManagerHandler
                .getArchiveSettings(userToken);

        return CommonUtils.invokeGetMethod(archiveSettings, keyword);
    }

    /**
     * Get the conference detail specified attribute value
     *
     * @see keyword=size <br/>
     *      confId=$cdrConfId <br/>
     *      startDate=$downloadCdrStartDate
     * @param keyword
     *            The attribute name
     * @param confId
     *            The conference id
     * @param startDate
     *            The start date in millions
     * @param endDate
     *            Date The end date in millions(Optional)
     * @return The conference detail specified attribute value
     */
    public String getConferenceDetails() {
        String result = "NotFound";
        final String keyword = inputCmd.get("keyword");
        final JUIUtcDateTime startDate = new JUIUtcDateTime();
        final String startDateStr = inputCmd.get("startDate");
        if (startDateStr.isEmpty()) {
            startDate.setUnixTime(getDefaultStartDate());
        } else {
            startDate.setUnixTime(Long.parseLong(startDateStr));
        }
        final JUIUtcDateTime endDate = new JUIUtcDateTime();
        final String endDateStr = inputCmd.get("endDate");
        if (endDateStr.isEmpty()) {
            endDate.setUnixTime(System.currentTimeMillis());
        } else {
            endDate.setUnixTime(Long.parseLong(endDateStr));
        }
        final List<JConferenceCDRDetail> cdrDetails = reportManagerHandler
                .getConferenceDetails(userToken, startDate, endDate);
        final String confId = inputCmd.get("confId");
        final String participant = inputCmd.get("participant").replaceAll("~",
                " ");
        int size = 0;
        if (keyword.equalsIgnoreCase("size")) {
            result = 0 + "";
        }
        try {
            for (final JConferenceCDRDetail cdr : cdrDetails) {
                // Not the participant cdr
                if (cdr.getParticipantName() == null) {
                    continue;
                }
                if (confId.equals(cdr.getConfId())) {
                    if (keyword.equalsIgnoreCase("size")) {
                        result = ++size + "";
                        continue;
                    }
                    if (participant.equals(cdr.getParticipantName())) {
                        return CommonUtils.invokeGetMethod(cdr, keyword);
                    }
                }
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            return result + ", " + e.getMessage();
        }
        return result;
    }

    /**
     * Get the conference summary specified attribute value
     *
     * @see scheduledDate=$serchTimePoint <br/>
     *      keyword=ConfId <br/>
     *      startDate=$downloadCdrStartDate
     * @param scheduledDate
     *            The conference scheduled date
     * @param keyword
     *            The attribute name
     * @param startDate
     *            The start date in millions
     * @param endDate
     *            Date The end date in millions(Optional)
     * @return The conference summary specified attribute value
     */
    public String getConferenceSummaries() {
        final String result = "NotFound";
        final String keyword = inputCmd.get("keyword");
        final String scheduledDate = inputCmd.get("scheduledDate")
                .replaceAll("~", " ");
        final JUIUtcDateTime startDate = new JUIUtcDateTime();
        final String startDateStr = inputCmd.get("startDate");
        if (startDateStr.isEmpty()) {
            startDate.setUnixTime(getDefaultStartDate());
        } else {
            startDate.setUnixTime(Long.parseLong(startDateStr));
        }
        final JUIUtcDateTime endDate = new JUIUtcDateTime();
        final String endDateStr = inputCmd.get("endDate");
        if (endDateStr.isEmpty()) {
            endDate.setUnixTime(System.currentTimeMillis());
        } else {
            endDate.setUnixTime(Long.parseLong(endDateStr));
        }
        final List<JConferenceCDRSummary> summaries = reportManagerHandler
                .getConferenceSummaries(userToken, startDate, endDate);
        if (keyword.equalsIgnoreCase("size")) {
            return summaries.size() + "";
        }
        final SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm a",
                                                         Locale.ENGLISH);
        Date find = null;
        try {
            if (scheduledDate.matches("[0-9]+")) {
                find = new Date(Long.parseLong(scheduledDate));
            } else {
                find = df.parse(scheduledDate);
            }
            logger.info("Find info with time point " + df.format(find)
            + " in GUI");
            for (final JConferenceCDRSummary cdr : summaries) {
                final Date start = new Date(cdr.getStartTime().getUnixTime());
                final Date stop = new Date(cdr.getEndTime().getUnixTime());
                if (find.before(stop) && find.after(start)) {
                    return CommonUtils.invokeGetMethod(cdr, keyword);
                }
            }
        } catch (IllegalArgumentException | ParseException e) {
            e.printStackTrace();
            return result + ", " + e.getMessage();
        }
        return result;
    }

    /**
     * Get the Conference Type Info attribute value in csv file
     *
     * @see This method is not used in TCL yet
     *
     * @param filename
     *            The csv file name
     * @param date
     *            The date to find
     * @param keyword
     *            The attribute name
     * @return The Conference Type Info attribute value in csv file
     */
    public String getConferenceTypeInfoCsv() {
        final String result = "NotFound";
        // Parse the input command
        CommonUtils.parseInputCmd(cmdArgs, inputCmd);
        final String filename = inputCmd.get("filename");
        final String keyword = inputCmd.get("keyword");
        final String date = inputCmd.get("date");
        List<ConferenceTypeCdr> report = null;
        try {
            report = CsvParser.getConferenceTypeCdr(new File(
                                                             "cdr" + File.separator + filename));
        } catch (final IOException e1) {
            e1.printStackTrace();
            return result;
        }
        if (keyword.equalsIgnoreCase("size")) {
            return report.size() + "";
        }
        for (final ConferenceTypeCdr cdr : report) {
            if (cdr.getDate().equals(date)) {
                return CommonUtils.invokeGetMethod(cdr, keyword);
            }
        }
        return result;
    }

    /**
     * Get the conference type summary specified attribute value
     *
     * @see scheduledDate=$serchTimePoint <br/>
     *      keyword=ConfId <br/>
     *      startDate=$downloadCdrStartDate
     * @param keyword
     *            The attribute name
     * @param startDate
     *            The start date in millions
     * @param endDate
     *            Date The end date in millions(Optional)
     * @return The conference summary specified attribute value
     */
    public String getConferenceTypeSummaries() {
        final String result = "NotFound";
        final String keyword = inputCmd.get("keyword");
        final JUIUtcDateTime startDate = new JUIUtcDateTime();
        final String startDateStr = inputCmd.get("startDate");
        final String date = inputCmd.get("date");

        if (startDateStr.isEmpty()) {
            startDate.setUnixTime(getDefaultStartDate());
        } else {
            startDate.setUnixTime(Long.parseLong(startDateStr));
        }
        final JUIUtcDateTime endDate = new JUIUtcDateTime();
        final String endDateStr = inputCmd.get("endDate");
        if (endDateStr.isEmpty()) {
            endDate.setUnixTime(System.currentTimeMillis());
        } else {
            endDate.setUnixTime(Long.parseLong(endDateStr));
        }
        logger.info("startDate: " + startDate.getUnixTime());
        logger.info("endDate: " + endDate.getUnixTime());
        final List<JConferenceTypeSummary> summaries = reportManagerHandler
                .getConferenceTypeSummaries(userToken, startDate, endDate);

        try {
            for (final JConferenceTypeSummary cdr : summaries) {
                if ((cdr.getReportMonth() + "/" + cdr.getReportYear())
                        .equals(date)) {
                    final String methodName = "get" + keyword;
                    final Method method = CommonUtils
                            .getDeclaredMethod(cdr, methodName, new Class[] {});
                    return method.invoke(cdr, new Object[] {}).toString();
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
            return result + ", " + e.getMessage();
        }
        return result;
    }

    /**
     * Get the Conference Usage Info attribute value in csv file
     *
     * @see filename=$conferenceUsageCsvFilenameAfter <br/>
     *      keyword=TotalActualParticipants <br/>
     *      scheduledDate=$serchTimePoint
     *
     * @param filename
     *            The csv file name
     * @param scheduledDate
     *            The date to find
     * @param keyword
     *            The attribute name
     * @return The Conference Usage Info attribute value in csv file
     */
    public String getConferenceUsageInfoCsv() {
        final String result = "NotFound";
        // Parse the input command
        CommonUtils.parseInputCmd(cmdArgs, inputCmd);
        final String filename = inputCmd.get("filename");
        final String keyword = inputCmd.get("keyword");
        final String scheduledDate = inputCmd.get("scheduledDate")
                .replaceAll("~", " ");
        final String participant = inputCmd.get("participant").replaceAll("~",
                " ");
        List<ConferenceUsageCdr> report = null;
        try {
            report = CsvParser.getConferenceUsageCdr(new File(
                                                              CDR_FOLDER + File.separator + filename));
        } catch (final IOException e1) {
            e1.printStackTrace();
            return result;
        }
        if (keyword.equalsIgnoreCase("size")) {
            return report.size() + "";
        }
        final SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm a",
                                                         Locale.ENGLISH);
        try {
            Date find = null;
            if (scheduledDate.matches("[0-9]+")) {
                find = new Date(Long.parseLong(scheduledDate));
            } else {
                find = df.parse(scheduledDate);
            }
            logger.info("Find info with time point " + df.format(find) + " in "
                    + filename);
            for (final ConferenceUsageCdr cdr : report) {
                final Date start = df
                        .parse(cdr.getDate() + " " + cdr.getActualStart());
                final Date stop = df
                        .parse(cdr.getDate() + " " + cdr.getActualStop());
                if (find.before(stop) && find.after(start)) {
                    if (participant.isEmpty()) {
                        return CommonUtils.invokeGetMethod(cdr, keyword);
                    } else {
                        for (final Participant p : cdr.getParticipants()) {
                            if (p.getConferenceName().equals(participant)) {
                                return CommonUtils.invokeGetMethod(p, keyword);
                            }
                        }
                    }
                }
            }
        } catch (IllegalArgumentException | ParseException e) {
            e.printStackTrace();
            return result + ", " + e.getMessage();
        }
        return result;
    }

    /**
     * Internal method, get the default time in millions
     *
     * @return The default time in millions
     */
    private long getDefaultStartDate() {
        try {
            return defalutDF.parse(DEFAULT_START_DATE).getTime();
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get the Endpoint Cdr Specified attribute value
     *
     * @see keyword=size <br/>
     *      endpointName=$id <br/>
     *      startDate=$downloadCdrStartDate
     * @param keyword
     *            The attribute name
     * @param endpointName
     *            The endpoint name
     * @param startDate
     *            The start date in millions
     * @param endDate
     *            Date The end date in millions(Optional)
     * @return The Endpoint Cdr Specified attribute value
     */
    public String getEndpointCdrSpecific() {
        String result = "NotFound";
        final String endpointName = inputCmd.get("endpointName");
        final String serialNumber = inputCmd.get("serialNumber");
        final DeviceManagerHandler dmh = new DeviceManagerHandler(
                                                                  webServiceUrl);
        final List<JEndpointForList> eps = dmh
                .getDevicesFromEndpointCdr(userToken, endpointName);
        if (eps.isEmpty()) {
            final String errorMsg = "Cannot find the device with endpoint name "
                    + endpointName;
            logger.error(errorMsg);
            return "Failed, " + errorMsg;
        }
        final String keyword = inputCmd.get("keyword");
        final String scheduledDate = inputCmd.get("scheduledDate")
                .replaceAll("~", " ");
        final JUIUtcDateTime startDate = new JUIUtcDateTime();
        final String startDateStr = inputCmd.get("startDate");
        if (startDateStr.isEmpty()) {
            startDate.setUnixTime(getDefaultStartDate());
        } else {
            startDate.setUnixTime(Long.parseLong(startDateStr));
        }
        final JUIUtcDateTime endDate = new JUIUtcDateTime();
        final String endDateStr = inputCmd.get("endDate");
        if (endDateStr.isEmpty()) {
            endDate.setUnixTime(System.currentTimeMillis());
        } else {
            endDate.setUnixTime(Long.parseLong(endDateStr));
        }
        final List<String> serialNumbers = new ArrayList<String>();
        for (final JEndpointForList ep : eps) {
            serialNumbers.add(ep.getSerialNumber());
        }
        logger.info("serialNumber: " + serialNumbers + ", startDate" + startDate + ", endDate" + endDate);
        final List<JEndpointCDR> endpointCdrs = reportManagerHandler
                .getEndpointCDRs(userToken, serialNumbers, startDate, endDate);
        final SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss",
                                                         Locale.ENGLISH);
        Date find = null;
        try {
            int size = 0;
            if (keyword.equalsIgnoreCase("size")) {
                result = 0 + "";
            }
            for (final JEndpointCDR cdr : endpointCdrs) {
                if (serialNumber.equalsIgnoreCase(cdr.getSerialNumber())) {
                    if (keyword.equalsIgnoreCase("size")) {
                        result = ++size + "";
                        continue;
                    }
                    if (scheduledDate.matches("[0-9]+")) {
                        find = new Date(Long.parseLong(scheduledDate));
                    } else {
                        find = df.parse(scheduledDate);
                    }
                    logger.info("Find info with time point " + df.format(find)
                    + " in GUI");
                    final Date start = new Date(
                                                cdr.getStartDateTime().getUnixTime());
                    final Date stop = new Date(
                                               cdr.getEndDateTime().getUnixTime());
                    System.out.println("Start: " + df.format(start) + ", Stop: "
                            + df.format(stop));
                    if (find.before(stop) && find.after(start)) {
                        return CommonUtils.invokeGetMethod(cdr, keyword);
                    }
                }
            }
        } catch (IllegalArgumentException | ParseException e) {
            e.printStackTrace();
            return result + ", " + e.getMessage();
        }
        return result;
    }

    /**
     * Get the endpoint usage info specified attribute value in csv file
     *
     * @see keyword=size <br/>
     *      endpointName=$id <br/>
     *      startDate=$downloadCdrStartDate
     * @param keyword
     *            The attribute name
     * @param endpointName
     *            The endpoint name
     * @param startDate
     *            The start date in millions
     * @param endDate
     *            Date The end date in millions(Optional)
     * @return The endpoint usage info specified attribute value in csv file
     */
    public String getEndpointUsageInfoCsv() {
        String result = "NotFound";
        // Parse the input command
        CommonUtils.parseInputCmd(cmdArgs, inputCmd);
        final String filename = inputCmd.get("filename");
        final String keyword = inputCmd.get("keyword");
        final String serialNumber = inputCmd.get("serialNumber");
        final String date = inputCmd.get("date").replaceAll("~", " ");
        List<EndpointUsageCdr> report = null;
        try {
            report = CsvParser.getEndpointUsageCdr(new File(
                                                            CDR_FOLDER + File.separator + filename));
        } catch (final IOException e1) {
            e1.printStackTrace();
            return result;
        }
        final SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm a",
                                                         Locale.ENGLISH);
        try {
            Date find = null;
            int size = 0;
            if (keyword.equalsIgnoreCase("size")) {
                result = 0 + "";
            }
            for (final EndpointUsageCdr cdr : report) {
                if (serialNumber.equalsIgnoreCase(cdr.getSerialNumber())) {
                    if (keyword.equalsIgnoreCase("size")) {
                        result = ++size + "";
                        continue;
                    }
                    if (date.matches("[0-9]+")) {
                        find = new Date(Long.parseLong(date));
                    } else {
                        find = df.parse(date);
                    }
                    final Date start = df.parse(cdr.getStartDate() + " "
                            + cdr.getStartTime());
                    final Date stop = df
                            .parse(cdr.getEndDate() + " " + cdr.getEndTime());
                    if (find.before(stop) && find.after(start)) {
                        result = CommonUtils.invokeGetMethod(cdr, keyword);
                        final Pattern p = Pattern
                                .compile("([0-9]{2}):([0-9]{2}):([0-9]{2})");
                        final Matcher m = p.matcher(result);
                        if (keyword.equals("CallDuration") && m.find()) {
                            final int hour = Integer.parseInt(m.group(1));
                            final int minute = Integer.parseInt(m.group(2));
                            final int second = Integer.parseInt(m.group(3));
                            result = second + minute * 60 + hour * 60 * 60 + "";
                        }
                        return result;
                    }
                }
            }
        } catch (IllegalArgumentException | ParseException e) {
            e.printStackTrace();
            return result + ", " + e.getMessage();
        }
        return result;
    }

    /**
     * Get the endpoint usage info specified attribute value in xls file
     *
     * @see keyword=size <br/>
     *      endpointName=$id <br/>
     *      startDate=$downloadCdrStartDate
     * @param keyword
     *            The attribute name
     * @param endpointName
     *            The endpoint name
     * @param startDate
     *            The start date in millions
     * @param endDate
     *            Date The end date in millions(Optional)
     * @return The endpoint usage info specified attribute value in xls file
     */
    public String getEndpointUsageInfoXls() {
        String result = "NotFound";
        // Parse the input command
        CommonUtils.parseInputCmd(cmdArgs, inputCmd);
        final String filename = inputCmd.get("filename");
        final String keyword = inputCmd.get("keyword");
        final String endpointName = inputCmd.get("endpointName");
        final String date = inputCmd.get("date").replaceAll("~", " ");
        List<EndpointUsageCdr> report = null;
        try {
            report = XlsParser.getEndpointUsageCdr(new File(
                                                            CDR_FOLDER + File.separator + filename));
        } catch (final IOException | BiffException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e1) {
            e1.printStackTrace();
            return result;
        }
        if (keyword.equalsIgnoreCase("size")) {
            return report.size() + "";
        }
        try {
            final SimpleDateFormat df = new SimpleDateFormat(
                                                             "MM-dd-yyyy HH:mm:ss", Locale.ENGLISH);
            Date find = null;
            if (date.matches("[0-9]+")) {
                find = new Date(Long.parseLong(date));
            } else {
                find = df.parse(date);
            }
            logger.info(df.format(find));
            for (final EndpointUsageCdr cdr : report) {
                final Date start = df
                        .parse(cdr.getStartDate() + " " + cdr.getStartTime());
                final Date stop = df
                        .parse(cdr.getEndDate() + " " + cdr.getEndTime());
                if (cdr.getName().equals(endpointName) && find.before(stop)
                        && find.after(start)) {
                    result = CommonUtils.invokeGetMethod(cdr, keyword);
                    final Pattern p = Pattern
                            .compile("([0-9]{2}):([0-9]{2}):([0-9]{2})");
                    final Matcher m = p.matcher(result);
                    if (keyword.equals("CallDuration") && m.find()) {
                        final int hour = Integer.parseInt(m.group(1));
                        final int minute = Integer.parseInt(m.group(2));
                        final int second = Integer.parseInt(m.group(3));
                        result = second + minute * 60 + hour * 60 * 60 + "";
                    }
                    return result;
                }
            }
        } catch (IllegalArgumentException | ParseException e) {
            e.printStackTrace();
            return result + ", " + e.getMessage();
        }
        return result;
    }

    @Override
    protected void injectCmdArgs() {
        put("startDate", "");
        put("endDate", "");
        put("isConf", "");
        put("deviceIp", "");
        put("reportType", "");
        put("filename", "");
        put("date", "");
        put("scheduledDate", "");
        put("participant", "");
        put("endpointName", "");
        put("serialNumber", "");
        put("confId", "");

        put("keepInterval", "");
        put("enableArchive", "");
        put("transferStartDate", "");
        put("transferStartTime", "");
        put("transmissionFrequency", "");
        put("ftpHost", "");
        put("ftpPort", "");
        put("ftpUsername", "");
        put("ftpPassword", "");
        put("ftpDirectory", "");

    }

    /**
     * Save the new value in the report administration page
     *
     * @see keepInterval=$keepInterval <br/>
     *      enableArchive=$enableArchive <br/>
     *      transferStartDate=$transferStartDate <br/>
     *      transferStartTime=$transferStartTime <br/>
     *      transmissionFrequency=$transmissionFrequency <br/>
     *      ftpHost=$ftpHost <br/>
     *      ftpPort=$ftpPort <br/>
     *      ftpUsername=$ftpUsername <br/>
     *      ftpPassword=$ftpPassword <br/>
     *      ftpDirectory=$ftpDirectory
     *
     * @param keepInterval
     *            Retention Period for Conference and Endpoint CDRs (In Days)
     * @param enableArchive
     *            Enable FTP of CDRs (CSV Format)
     * @param transferStartDate
     *            Next Transfer Date
     * @param transferStartTime
     *            Transfer Start Time (Server Time in 24h Format)
     * @param transmissionFrequency
     *            CDR Transmission Frequency (In Days)
     * @param ftpHost
     *            Host Name or IP Address of FTP Server
     * @param ftpPort
     *            FTP Port
     * @param ftpUsername
     *            FTP User Name
     * @param ftpPassword
     *            FTP Password
     * @param ftpDirectory
     *            FTP Directory
     *
     * @return Save archive settings result
     */
    public String saveArchiveSettings() {
        String status = "Failed";

        final JCDRArchiveSettings archiveSettings = new JCDRArchiveSettings();
        archiveSettings.setKeepInterval(Integer
                                        .parseInt(inputCmd.get("keepInterval")));
        archiveSettings.setEnableArchive(Boolean
                                         .valueOf(inputCmd.get("enableArchive")));
        archiveSettings.setTransferStartDate(inputCmd.get("transferStartDate"));
        archiveSettings.setTransferStartTime(inputCmd.get("transferStartTime"));
        archiveSettings.setTransmissionFrequency(Integer
                                                 .parseInt(inputCmd.get("transmissionFrequency")));
        archiveSettings.setFtpHost(inputCmd.get("ftpHost"));
        archiveSettings.setFtpPort(Integer.parseInt(inputCmd.get("ftpPort")));
        archiveSettings.setFtpUsername(inputCmd.get("ftpUsername"));
        archiveSettings.setFtpPassword(inputCmd.get("ftpPassword"));
        archiveSettings.setFtpDirectory(inputCmd.get("ftpDirectory"));

        final JWebResult result = reportManagerHandler
                .saveArchiveSettings(userToken, archiveSettings);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Save archive settings successfully.");
        } else {
            status = "Failed";
            logger.error("Save archive settings failed.");
            return status + "Save archive settings failed.";
        }
        return status;
    }
}