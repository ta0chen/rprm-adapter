package com.polycom.sqa.xma.webservices;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonTypeInfo.None;
import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.RsyslogManagerHandler;
import com.polycom.webservices.RsyslogManager.JStatus;
import com.polycom.webservices.RsyslogManager.JWebResult;

/**
 * Rsyslog handler. This class will handle the webservice request of Rsyslog
 * module
 *
 * @author wbchao
 *
 */
public class RsyslogHandler extends XMAWebServiceHandler {
    /**
     * The default folder to store log files
     */
    public final static String LOG_FOLDER = "logs/rprm";

    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "rollLogs ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = " ";
        // final String method = "downloadSyslog ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params = "fileName=jserver.zip ";
        // final String method = "deleteLogs ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params = " ";
        final String command = "https://10.220.202.252:8443/PlcmRmWeb/rest/JRsyslogManager RsyslogManager "
                + method + auth + params;
        final RsyslogHandler handler = new RsyslogHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    private final RsyslogManagerHandler rsyslogManagerHandler;

    public RsyslogHandler(final String cmd) throws IOException {
        super(cmd);
        rsyslogManagerHandler = new RsyslogManagerHandler(webServiceUrl);
    }

    /**
     * Delete the log on TCL server
     *
     * @return The result
     */
    public String deleteLogs() {
        final boolean result = CommonUtils.deleteDir(new File(LOG_FOLDER));
        if (result) {
            logger.info("Delete log folder successfully");
            return "SUCCESS";
        } else {
            logger.info("Delete log folder unsuccessfully");
            return "Failed";
        }
    }

    public String downloadSyslog() {
        final Pattern p = Pattern.compile("https?://(.*):.*");
        final Matcher m = p.matcher(cmdArgs[0]);
        String xmaIp = "localhost";
        if (m.find()) {
            xmaIp = m.group(1);
        }
        final StringBuffer url = new StringBuffer("https://" + xmaIp
                + ":8443/PlcmRmWeb/FileDownload?DownloadType=LOGGER");
        url.append("&").append("Modifier=-1688432715");
        url.append("&").append("FileName=");
        final String fileName = inputCmd.get("fileName");
        final String path = LOG_FOLDER + File.separator + fileName;
        try {
            final String result = CommonUtils
                    .downloadFile(path,
                                  url.toString(),
                                  "Credentials=" + userToken);
            if (result.equals("200")) {
                return "SUCCESS";
            } else {
                return "Failed, the http status code is " + result;
            }
        } catch (final IOException e) {
            logger.error(e.getMessage());
            return "Failed\n" + CommonUtils.getExceptionStackTrace(e);
        }
    }

    @Override
    protected void injectCmdArgs() {
    }

    /**
     * Roll logs
     *
     * @see None
     *
     *
     * @return The result
     */
    public String rollLogs() {
        final JWebResult result = rsyslogManagerHandler.rotate(userToken,
                                                               "SYSTEMLOGS");
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Roll logs successfully.");
            return "SUCCESS";
        } else {
            logger.error("Roll logs failed.");
            return "Failed, " + result.getMessages();
        }
    }
}
