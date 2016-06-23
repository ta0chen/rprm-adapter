package com.polycom.webservices;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.LicenseManagerHandler;
import com.polycom.webservices.LicenseManager.JDuration;
import com.polycom.webservices.LicenseManager.JFeatureEntity;
import com.polycom.webservices.LicenseManager.JLicenseViewInfo;
import com.polycom.webservices.LicenseManager.JStatus;
import com.polycom.webservices.LicenseManager.JWebResult;

/**
 * License handler. This class will handle the webservice request of License
 * module
 *
 * @author wbchao
 *
 */
public class LicenseHandler extends XMAWebServiceHandler {
    /**
     * The default folder to store license files
     */
    public final static String  LICENSE_FOLDER      = "XMALicense/download";
    private final static String LICENSE_UPLOAD_PATH = "/var/polycom/cma/temp/license/";

    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "downloadLicenseFile ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = " ";
        // final String method = "deleteLicenseFile ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = " ";
        // final String method = "getCurrentLicenseInfoSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = "keyword=versions ";
        // final String method = "updateLicense ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "IPAddress=10.220.202.228 primaryLicenseFileName=10.220.202.228_license_basic.xml ";
        // final String method = "getLicenseFeatures ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = "checkDate=12/02/2016 ";
        final String method = "updateLicense ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "IPAddress=10.220.202.228 primaryLicenseFileName=10.220.202.228_license_basic.xml ";
        final String command = "http://10.220.202.228:8080/PlcmRmWeb/JLicenseManager LicenseManager "
                + method + auth + params;
        final LicenseHandler handler = new LicenseHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    LicenseManagerHandler lmh;

    public LicenseHandler(final String cmd) throws IOException {
        super(cmd);
        lmh = new LicenseManagerHandler(webServiceUrl);
    }

    /**
     * Delete the cdr file on TCL server
     *
     * @see filename=$id
     * @param filename
     *            The cdr file name
     * @return The result
     */
    public String deleteLicenseFile() {
        final boolean result = CommonUtils.deleteDir(new File(LICENSE_FOLDER));
        if (result) {
            logger.info("Delete license folder successfully");
            return "SUCCESS";
        } else {
            logger.info("Delete license folder unsuccessfully");
            return "Failed";
        }
    }

    /**
     * Download license xml File
     *
     * @see No param
     *
     * @return The result
     */
    public String downloadLicenseFile() {
        final Pattern p = Pattern.compile("https?://(.*):.*");
        final Matcher m = p.matcher(cmdArgs[0]);
        String xmaIp = "localhost";
        if (m.find()) {
            xmaIp = m.group(1);
        }
        final StringBuffer url = new StringBuffer("https://" + xmaIp
                                                  + ":8443/PlcmRmWeb/FileDownload?DownloadType=LICENSE");
        url.append("&").append("Credentials=" + userToken);
        url.append("&").append("Modifier=");
        url.append("&").append("FileName=");
        final String path = LICENSE_FOLDER + "/" + "downloadLicense.xml";
        try {
            CommonUtils.downloadFile(path, url.toString(), "Credentials="
                    + userToken);
        } catch (final IOException e) {
            logger.error(e.getMessage());
            return CommonUtils.getExceptionStackTrace(e);
        }
        return "SUCCESS";
    }

    /**
     * Get the license attribute value of current license
     *
     * @see keyword=total
     * @param keword
     *            The attribute name of current license
     * @return The license attribute value of current license
     */
    public String getCurrentLicenseInfoSpecific() {
        final String result = "NotFound";
        final JLicenseViewInfo license = lmh.getCurrentLicenseInfo(userToken);
        final String keyword = inputCmd.get("keyword");
        if (keyword.isEmpty()) {
            return result + ", keyword is empty!";
        }
        return CommonUtils.invokeGetMethod(license, keyword);
    }

    /**
     * Get the license available features and start/stop date with the checkDate
     *
     * @see checkDate=05-24-2015
     * @param checkDate
     *            The date to check the license features
     * @return The license available features and start/stop date with the
     *         checkDate
     */
    public String getLicenseFeatures() {
        final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy",
                                                         Locale.ENGLISH);
        final String checkDate = inputCmd.get("checkDate").replaceAll("~", " ");
        Date find = null;
        try {
            if (checkDate.matches("[0-9]+")) {
                find = new Date(Long.parseLong(checkDate));
            } else {
                find = df.parse(checkDate);
            }
        } catch (NumberFormatException | ParseException e) {
            e.printStackTrace();
            return "Failed, the checkDate format is wrong";
        }
        final JLicenseViewInfo license = lmh.getCurrentLicenseInfo(userToken);
        final List<JDuration> durationList = license.getDurationList();
        for (final JDuration duration : durationList) {
            final Date start = new Date(duration.getStartDate().getUnixTime());
            final Date stop = new Date(duration.getEndDate().getUnixTime());
            if (find.before(stop) && find.after(start)) {
                final StringBuffer sb = new StringBuffer();
                sb.append("StartDate=").append(df.format(start)).append("-");
                sb.append("EndDate=").append(df.format(stop)).append(": [");
                for (final JFeatureEntity feature : duration.getFeatures()) {
                    final String featureName = feature.getFeature().value();
                    final int featureValue = feature.getResource();
                    sb.append(featureName).append("=");
                    sb.append(featureValue).append(", ");
                }
                System.out.println(sb.substring(0, sb.length() - 2) + "]");
                return sb.substring(0, sb.length() - 2) + "]";
            }
        }
        return "NotFound, did not find any feature available at the time "
                + checkDate;
    }

    @Override
    protected void injectCmdArgs() {
        // Primary License File Name
        put("primaryLicenseFileName", "");
        // Secondary License File Name
        put("secondaryLicenseFileName", "");
        // The IP address of the current active XMA unit
        put("IPAddress", "");
        // The user name for updating the license
        put("username4license", "root");
        // The password for updating the license
        put("password4license", "Crestone_4357M_");
        put("checkDate", "checkDate");
    }

    /**
     * Update the license
     *
     * @see IPAddress=$xmaAddr <br/>
     *      secondaryLicenseFileName=5C70DZ1_XMA8-252_83_Secondary_45000.xml
     *
     * @param IPAddress
     *            The XMA ip
     * @param primaryLicenseFileName
     *            The primary license file name(Optional)
     * @param secondaryLicenseFileName
     *            The secondary license file name(Optional)
     * @param username4license
     *            The XMA SSH username
     * @param password4license
     *            The XMA SSH password
     * @return The result
     */
    public String updateLicense() {
        String status = "Failed";
        String command = "";
        String srcFile = "";
        String fileName = "";
        if (!getInputCmd().get("primaryLicenseFileName").isEmpty()
                && getInputCmd().get("secondaryLicenseFileName").isEmpty()) {
            srcFile = "XMALicense/"
                    + getInputCmd().get("primaryLicenseFileName");
            fileName = getInputCmd().get("primaryLicenseFileName");
            logger.info("The primary license source file is: " + srcFile);
        } else if (getInputCmd().get("primaryLicenseFileName").isEmpty()
                && !getInputCmd().get("secondaryLicenseFileName").isEmpty()) {
            srcFile = "XMALicense/"
                    + getInputCmd().get("secondaryLicenseFileName");
            fileName = getInputCmd().get("secondaryLicenseFileName");
            logger.info("The secondary license source file is: " + srcFile);
        } else {
            status = "Failed";
            logger.error("Please specify one and only one license file in the input command.");
            return status
                    + " Please specify one and only one license file in the input command.";
        }
        final String[] temp = fileName.split("/");
        fileName = temp[temp.length - 1];
        final String destFilename = lmh.getConfigurationForUpload(userToken);
        final String destPath = LICENSE_UPLOAD_PATH;
        logger.info("Got the the file name for license configuration: "
                + destFilename);
        logger.info("Got the file path for license configuration: " + destPath);
        final String IPAddress = getInputCmd().get("IPAddress");
        if (IPAddress.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the XMA IP address in the input command.");
            return status
                    + " Cannot find the XMA IP address in the input command.";
        }
        // Copy the source license file to XMA
        final String username4license = getInputCmd().get("username4license");
        final String password4license = getInputCmd().get("password4license");
        final long interval = 5000;
        try {
            // Change the uploaded license file owner to plcm
            command = "rm -rf " + destPath + "*.*";
            CommonUtils.sshAndOperation(IPAddress,
                                        username4license,
                                        password4license,
                                        command);
            CommonUtils.scpPutFile(IPAddress,
                                   username4license,
                                   password4license,
                                   srcFile,
                                   destPath);
            Thread.sleep(interval);
            // create the license file
            command = "ls -l" + destPath + fileName;
            final String response = CommonUtils
                    .sshAndOperation(IPAddress,
                                     username4license,
                                     password4license,
                                     command);
            logger.info("file==" + response);
            // Change the uploaded license file owner to plcm
            command = "chown plcm:plcm " + destPath + fileName;
            CommonUtils.sshAndOperation(IPAddress,
                                        username4license,
                                        password4license,
                                        command);
            // Rename the file name with the destination file name
            command = "mv " + destPath + fileName + " " + destPath
                    + destFilename;
            CommonUtils.sshAndOperation(IPAddress,
                                        username4license,
                                        password4license,
                                        command);
        } catch (final Exception e) {
            e.printStackTrace();
            return "Failed, got exception " + e.getCause().getMessage();
        }
        final JWebResult result = lmh
                .getLicensePreview(userToken, destFilename);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            if (lmh.uploadLicense(userToken, destFilename).getStatus()
                    .compareTo(JStatus.SUCCESS) == 0) {
                status = "SUCCESS";
                logger.info("License file upload successfully");
            } else {
                status = "Failed";
                logger.error("Upload the license after preview failed. "
                        + "Please check the XMA Jserver log for detail reason.");
                return status + " Upload the license after preview failed. "
                        + result.getStatus();
            }
        } else {
            status = "Failed";
            logger.error("Get the license preview failed. Please validate the license file."
                    + result.getStatus());
            return status
                    + " Get the license preview failed. Please validate the license file. "
                    + result.getStatus();
        }
        return status;
    }
}
