package com.polycom.webservices;

import java.io.File;
import java.io.IOException;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.PatchUpgradeManagerHandler;
import com.polycom.webservices.PatchUpgrade.JStatus;
import com.polycom.webservices.PatchUpgrade.JWebResult;

/**
 * Patch Upgrade handler. This class will handle the webservice request of
 * Profile module
 *
 * @author wbchao
 *
 */
public class PatchUpgradeHandler extends XMAWebServiceHandler {
    private final static String RPRM_UPLOAD_PATH = "/var/polycom/cma/upgrade/";
    private final static String PACKAGE_FOLDER   = "RprmVersion";
    private final static String SSH_USERNAME     = "root";
    private final static String SSH_PASSWORD     = "Crestone_4357M_";

    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "canUpgrade ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params = " fileName=rprm-9.0.0-201551.bin ";
        // final String method = "startupgradeunpacking ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params = " fileName=rprm-9.0.0-201551.bin ";
        // final String method = "getunpackagingstatus ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params = " ";
        // final String method = "runUpgrade ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params = " ";
        final String method = "deleteUpgradePackage ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "  ";
        final String command = "http://10.220.202.228/PlcmRmWeb/JPatchUpgrade PatchUpgradeManager "
                + method + auth + params;
        final PatchUpgradeHandler handler = new PatchUpgradeHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    private final PatchUpgradeManagerHandler patchUpgradeManagerHandler;

    public PatchUpgradeHandler(final String cmd) throws IOException {
        super(cmd);
        patchUpgradeManagerHandler = new PatchUpgradeManagerHandler(
                                                                    webServiceUrl);
    }

    /**
     * Test the package could be upgrade
     *
     * @see rprmBuild=9.0.0-201551
     * @param rprmBuild
     *            The rprm build number
     * @return The result
     */
    public String canUpgrade() {
        final String fileName = inputCmd.get("fileName");
        final String srcFile = "RprmVersion/" + fileName;
        final String destPath = RPRM_UPLOAD_PATH;
        // Copy the source file to XMA
        final long interval = 180 * 1000;
        String command = "";
        try {
            // Clean the folder first
            command = "rm -rf " + destPath + "*.*";
            CommonUtils.sshAndOperation(rprmIp,
                                        SSH_USERNAME,
                                        SSH_PASSWORD,
                                        command);
            // Copy the src file to RPRM server
            CommonUtils.scpPutFile(rprmIp,
                                   SSH_USERNAME,
                                   SSH_PASSWORD,
                                   srcFile,
                                   destPath);
            Thread.sleep(interval);
            // Refresh
            command = "ls -l" + destPath + fileName;
            final String response = CommonUtils
                    .sshAndOperation(rprmIp,
                                     SSH_USERNAME,
                                     SSH_PASSWORD,
                                     command);
            logger.info("file==" + response);
            // Change the file owner
            command = "chown plcm:plcm " + destPath + fileName;
            CommonUtils.sshAndOperation(rprmIp,
                                        SSH_USERNAME,
                                        SSH_PASSWORD,
                                        command);
        } catch (final Exception e) {
            e.printStackTrace();
            return "Failed, got exception " + e.getCause().getMessage();
        }
        // Token timeout, retrieve again
        userToken = umh.userLogin(inputCmd.get("username"),
                                  inputCmd.get("domain"),
                                  inputCmd.get("password"));
        final JWebResult result = patchUpgradeManagerHandler
                .canUpgrade(userToken);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            return "SUCCESS";
        } else {
            final String errorMsg = "Upload rprm package error: "
                    + result.getMessages();
            logger.error(errorMsg);
            return "Failed. " + errorMsg;
        }
    }

    /**
     * Delete the Upgrade Package file on TCL server
     *
     * @see
     * @param filename
     *            The cdr file name
     * @return The result
     */
    public String deleteUpgradePackage() {
        final boolean result = CommonUtils.deleteDir(new File(PACKAGE_FOLDER));
        if (result) {
            logger.info("Delete package folder successfully");
            return "SUCCESS";
        } else {
            logger.info("Delete package folder unsuccessfully");
            return "Failed";
        }
    }

    /**
     * Get the unpackaging status
     *
     * @see
     * @return The result
     */
    public String getunpackagingstatus() {
        final JStatus status = patchUpgradeManagerHandler
                .getUnpackingStatus(userToken);
        return status.value() + "";
    }

    @Override
    protected void injectCmdArgs() {
        put("fileName", "");
    }

    /**
     * Start rprm upgrade
     *
     * @see
     * @return The result
     */
    public String runUpgrade() {
        final JWebResult result = patchUpgradeManagerHandler
                .runUpgrade(userToken);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            return "SUCCESS";
        } else {
            final String errorMsg = "Run rprm upgrade error: "
                    + result.getMessages();
            logger.error(errorMsg);
            return "Failed. " + errorMsg;
        }
    }

    /**
     * Start unpacking uploaded package file
     *
     * @see rprmBuild=9.0.0-201551
     * @param rprmBuild
     *            The rprm build number
     * @return The result
     */
    public String startupgradeunpacking() {
        final String fileName = inputCmd.get("fileName");
        final JWebResult result = patchUpgradeManagerHandler
                .startUpgradeUnpacking(userToken, fileName);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            return "SUCCESS";
        } else {
            final String errorMsg = "Unpacking the build file error: "
                    + result.getMessages();
            logger.error(errorMsg);
            return "Failed. " + errorMsg;
        }
    }
}