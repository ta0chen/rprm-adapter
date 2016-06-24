package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.AudioSoftupdateManagerHandler;
import com.polycom.webservices.AudioSoftupdateManager.JSoftupdateVersion;
import com.polycom.webservices.AudioSoftupdateManager.JStatus;
import com.polycom.webservices.AudioSoftupdateManager.JWebResult;

/**
 * Audio softupdate handler. This class will handle the webservice request of
 * Audio Softupdate module
 *
 * @author wbchao
 *
 */
public class AudioSoftupdateHandler extends XMAWebServiceHandler {
    /**
     * The default fold to store audio softupdate pkg on XMA
     */
    public static final String AUDIO_SOFTUPDATE_FILE_LOCATION = "/var/polycom/ftphome/temp";

    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "addAudioSoftupdate ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "xmaIp=10.220.202.228 softupdateFilename=Polycom_UC_Software_4_0_4_release_sig_split.zip ";
        //
        // final String method = "updateAudioPolicy ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = "versionName=4.0.7.2514 ";
        // final String method = "deleteAudioSoftupdate ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = "versionName=4.0.7.2514 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JAudioSoftupdateManager AudioSoftupdateManager "
                + method + auth + params;
        final AudioSoftupdateHandler handler = new AudioSoftupdateHandler(
                                                                          command);
        final String result = handler.build();
        handler.logger.info("result==" + result);
    }

    private final AudioSoftupdateManagerHandler audioSoftupdateManagerHandler;

    public AudioSoftupdateHandler(final String cmd) throws IOException {
        super(cmd);
        audioSoftupdateManagerHandler = new AudioSoftupdateManagerHandler(
                                                                          webServiceUrl);
    }

    /**
     * Add audio softupdate pkg to XMA
     *
     * @see xmaIp=$xmaAddr <br/>
     *      softupdateFilename=$softwareVersion2(versionFileName)
     *
     * @param description
     *            Discription(Optional)
     * @param softupdateFilename
     *            The upload file name
     * @param xmaIp
     *            The XMA ip address
     * @param transferFileUsername
     *            The XMA SSH username(Optional, default is plcm)
     * @param transferFilePassword
     *            The XMA SSH password(Optional, default is Polycom123)
     * @return The result
     */
    public String addAudioSoftupdate() {
        String status = "Failed";
        final String description = inputCmd.get("description");
        final String softupdateFilename = inputCmd.get("softupdateFilename");
        final String srcFile = "PhoneVersion" + "/" + softupdateFilename;
        // Check the availability of the destination directory on XMA
        // The target directory is not available for a FTSU XMA
        final String xmaIp = inputCmd.get("xmaIp");
        final String transferFileUsername = inputCmd
                .get("transferFileUsername");
        final String transferFilePassword = inputCmd
                .get("transferFilePassword");
        // Copy the source file to XMA
        logger.info("srcFile:" + srcFile);
        logger.info("dstPath:" + AUDIO_SOFTUPDATE_FILE_LOCATION);
        CommonUtils.scpPutFile(xmaIp,
                               transferFileUsername,
                               transferFilePassword,
                               srcFile,
                               AUDIO_SOFTUPDATE_FILE_LOCATION);
        final String dstPath = AUDIO_SOFTUPDATE_FILE_LOCATION + "/"
                + softupdateFilename;
        // Change the uploaded license file owner to plcm
        final String command = "chown plcm:plcm " + dstPath;
        CommonUtils.sshAndOperation(xmaIp,
                                    transferFileUsername,
                                    transferFilePassword,
                                    command);
        final JWebResult result = audioSoftupdateManagerHandler
                .addAudioSoftupdate(userToken, description, "/"
                        + softupdateFilename);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Audio softupdate file " + softupdateFilename
                        + " is successfully uploaded to XMA.");
        } else {
            status = "Failed";
            logger.error("Audio softupdate file " + softupdateFilename
                         + " is not successfully uploaded to XMA.");
            return status + " Audio softupdate file " + softupdateFilename
                    + " is not successfully uploaded to XMA.";
        }
        return status;
    }

    /**
     * Delete the audio softupdate file on XMA
     *
     * @see versionName=$version1
     *
     * @param versionName
     *            The version name to delete
     * @return The result
     */
    public String deleteAudioSoftupdate() {
        String status = "Failed";
        final String versionName = inputCmd.get("versionName");
        final JSoftupdateVersion version = getAudioSoftupdateVersionByName(versionName);
        final List<Integer> versionIds = new ArrayList<Integer>();
        versionIds.add(version.getSoftUpdateVersionId());
        final JWebResult result = audioSoftupdateManagerHandler
                .deleteAudioSoftupdate(userToken, versionIds);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Delete audio software " + versionName
                        + " successfully.");
        } else {
            status = "Failed";
            logger.error("Delete audio software " + versionName
                         + " is not successfully.");
            return status + " Delete audio software " + versionName
                    + " is not successfully.";
        }
        return status;
    }

    /**
     * Internal method. Get the JSoftupdateVersion with version name This method
     * is internal method and did not open to TCL
     *
     * @param versionName
     *            The version name as keyword to search
     * @return JSoftupdateVersion
     */
    private JSoftupdateVersion
    getAudioSoftupdateVersionByName(final String versionName) {
        final List<JSoftupdateVersion> versionList = audioSoftupdateManagerHandler
                .getAudioImages(userToken, -1);
        for (final JSoftupdateVersion version : versionList) {
            if (version.getDeviceVersion().equals(versionName)) {
                return version;
            }
        }
        return null;
    }

    /**
     * Inject the args from tcl
     */
    @Override
    protected void injectCmdArgs() {
        put("softupdateFilename", "");
        put("description", "");
        put("versionName", "");
        put("xmaIp", "");
        put("transferFileUsername", "root");
        put("transferFilePassword", "Crestone_4357M_");
    }

    /**
     * Update the audio policy, use the specified version to do audio softupdate
     *
     * @see versionName=$softwareVersion2(version)
     *
     * @param versionName
     *            The audio softupdate version name
     * @return The result
     */
    public String updateAudioPolicy() {
        String status = "Failed";
        final String versionName = inputCmd.get("versionName");
        final JWebResult result = audioSoftupdateManagerHandler
                .updateAudioPolicy(userToken, -1, versionName);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Update audio software " + versionName
                        + " to use successfully.");
        } else {
            status = "Failed";
            logger.error("Update audio software " + versionName
                         + " is not successfully.");
            return status + " Update audio software " + versionName
                    + " is not successfully.";
        }
        return status;
    }
}