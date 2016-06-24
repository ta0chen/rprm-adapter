package com.polycom.sqa.xma.webservices;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.GroupManagerHandler;
import com.polycom.sqa.xma.webservices.driver.SoftupdateManagerHandler;
import com.polycom.webservices.GroupManager.JGroup;
import com.polycom.webservices.SoftupdateManager.JDeviceType;
import com.polycom.webservices.SoftupdateManager.JModel;
import com.polycom.webservices.SoftupdateManager.JSoftupdateProfile;
import com.polycom.webservices.SoftupdateManager.JSoftupdateVersion;
import com.polycom.webservices.SoftupdateManager.JStatus;
import com.polycom.webservices.SoftupdateManager.JUIUtcDateTime;
import com.polycom.webservices.SoftupdateManager.JWebResult;

/**
 * Softupdate handler. This class will handle the webservice request of
 * Softupdate module
 *
 * @author wbchao
 *
 */
public class SoftupdateHandler extends XMAWebServiceHandler {
    public static String XMA_UPLOAD_DIR        = "/opt/polycom/cma/current/jserver/web/ROOT.war/download/temp/dump/";
    public static String XMA_LEGACY_UPLOAD_DIR = "/opt/polycom/cma/current/jserver/web/ROOT.war/download/Updates/";

    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "updateLegacyProfile ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "description=auto7.1.1 xmaIP=10.220.202.228 deviceType=Tandberg softupdateFilename=s52010tc7_1_1.pkg deviceModel=SXSeries ";
        // final String method = "clearLegacySUProfile ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = "deviceModel=SXSeries ";
        // final String method = "addSoftupdate ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "xmaIP=10.220.202.228 deviceType=GroupSeries
        // softupdateFilename=polycom-gseries-release-4.3.0-230161.tar ";
        // final String method1 = "getLegacySUProfile ";
        // final String auth1 =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "deviceModel=SXSeries keyword=UploadDT ";
        final String command = "http://localhost:8888/PlcmRmWeb/JSoftupdateManager SoftupdateManager "
                + method + auth + params;
        final SoftupdateHandler handler = new SoftupdateHandler(command);
        final String result = handler.build();
        handler.logger.info("result==" + result);
        // final String method2 = "updatePolicy ";
        // final String auth2 =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params2 =
        // "deviceType=RPD deviceModel=MAC versionToUse=3.2.0.48423
        // allowNewerVersion=true ";
        //
        // final String command2 =
        // "http://localhost:8888/PlcmRmWeb/JSoftupdateManager SoftupdateManager
        // "
        // + method2 + auth2 + params2;
        //
        // final SoftupdateHandler handler2 = new SoftupdateHandler(command2);
        // handler2.build(handler2.cmd);
        //
        // final String method3 = "deleteSoftupdate ";
        // final String auth3 =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params3 = "deviceType=GroupSeries";
        //
        // final String command3 =
        // "http://localhost:8888/PlcmRmWeb/JSoftupdateManager SoftupdateManager
        // "
        // + method3 + auth3 + params3;
        //
        // final SoftupdateHandler handler3 = new SoftupdateHandler(command3);
        // handler3.build(handler3.cmd);
    }

    private final SoftupdateManagerHandler softupdateManagerHandler;

    public SoftupdateHandler(final String cmd) throws IOException {
        super(cmd);
        softupdateManagerHandler = new SoftupdateManagerHandler(webServiceUrl);
    }

    /**
     * Add softupdate file to XMA
     *
     * @see xmaIP=$xmaAddr <br/>
     *      deviceType=GroupSeries <br/>
     *      softupdateFilename=$oldGsVersion
     *
     * @param xmaIP
     *            The XMA ip
     * @param deviceType
     *            The device type of softupdate file
     * @param softupdateFilename
     *            The softupdate file name
     * @param transferFileUsername
     *            The SSH login username(Optional, default is plcm)
     * @param transferFilePassword
     *            The SSH login password(Optional, default is Polycom123)
     * @return The result
     */
    public String addSoftupdate() {
        JDeviceType deviceType = null;
        JModel deviceModel = null;
        final String description = inputCmd.get("description");
        final String keyFilename = inputCmd.get("keyFilename");
        String softupdateFilename = "";
        int testGroupId = 0;
        // determine the group name if it is a trial upgrade
        if (!inputCmd.get("groupName").isEmpty()) {
            final GroupManagerHandler groupManagerHandler = new GroupManagerHandler(
                    webServiceUrl);
            groupManagerHandler.getVisibleGroups(userToken);
            final List<JGroup> groups = groupManagerHandler
                    .getVisibleGroups(userToken);
            if (!groups.isEmpty()) {
                for (final JGroup group : groups) {
                    if (group.getGroupName()
                            .equals(inputCmd.get("groupName"))) {
                        testGroupId = group.getDbKey();
                    }
                }
            }
        }
        String srcFile = "";
        final String dstPath = XMA_UPLOAD_DIR;
        String status = "Failed";
        if (inputCmd.get("deviceType").equalsIgnoreCase("GroupSeries")) {
            deviceType = JDeviceType.GROUPSERIES;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("RPD")) {
            deviceType = JDeviceType.RP___DESKTOP;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("ITP")) {
            deviceType = JDeviceType.ITP;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("HDX")) {
            deviceType = JDeviceType.HD;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("Cmad")) {
            deviceType = JDeviceType.VL;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("VVX")) {
            deviceType = JDeviceType.VX;
        } else {
            logger.error("soft update failed, please make sure your device type information is correct.");
            return status;
        }
        if (inputCmd.get("deviceModel").equalsIgnoreCase("WIN")) {
            deviceModel = JModel.RP___DESKTOP;
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("MAC")) {
            deviceModel = JModel.RP___DESKTOP___MAC;
        }
        if (deviceType == JDeviceType.GROUPSERIES
                || deviceType == JDeviceType.ITP) {
            srcFile = "GsVersion" + File.separator
                    + inputCmd.get("softupdateFilename");
        } else if (deviceType == JDeviceType.RP___DESKTOP) {
            srcFile = "RPDVersion" + File.separator
                    + inputCmd.get("softupdateFilename");
        } else if (deviceType == JDeviceType.HD) {
            srcFile = "HDXVersion" + File.separator
                    + inputCmd.get("softupdateFilename");
        }
        softupdateFilename = inputCmd.get("softupdateFilename");
        // Check the availability of the destination directory on XMA
        // The target directory is not available for a FTSU XMA
        if (!CommonUtils.sshAndOperation(inputCmd.get("xmaIP"),
                                         inputCmd.get("transferFileUsername"),
                                         inputCmd.get("transferFilePassword"),
                                         "ls " + dstPath)
                .contains("0")) {
            if (CommonUtils.sshAndOperation(inputCmd.get("xmaIP"),
                                            inputCmd.get("transferFileUsername"),
                                            inputCmd.get("transferFilePassword"),
                                            "mkdir -p " + dstPath)
                    .contains("1")) {
                logger.info("The destination directory" + dstPath
                        + " for the upgrade package is created successfully.");
            } else {
                logger.error("The destination directory " + dstPath
                        + " for the upgrade package is failed to create.");
            }
        }
        // Copy the source file to XMA
        logger.info("srcFile:" + srcFile);
        logger.info("dstPath:" + dstPath);
        CommonUtils.scpPutFile(inputCmd.get("xmaIP"),
                               inputCmd.get("transferFileUsername"),
                               inputCmd.get("transferFilePassword"),
                               srcFile,
                               dstPath);
        final JWebResult result = softupdateManagerHandler
                .addSoftupdate(userToken,
                               deviceType,
                               deviceModel,
                               description,
                               keyFilename,
                               softupdateFilename,
                               testGroupId);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info(inputCmd.get("deviceModel") + " soft update succeed.");
            status = "SUCCESS";
        } else {
            logger.error(inputCmd.get("deviceModel") + " soft update failed.");
            status = "Failed";
        }
        return status;
    }

    /**
     * Clear the software update pkg(Used for Cisco device)
     *
     * @see deviceModel=$deviceModel
     *
     * @param deviceModel
     *            The Cisco device model
     * @return The result
     */
    public String clearLegacySUProfile() {
        String status = "Failed";
        JModel deviceModel = null;
        if (inputCmd.get("deviceModel").equalsIgnoreCase("SXSeries")) {
            deviceModel = JModel.TB___SX_SERIES;
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("EXSeries")) {
            deviceModel = JModel.TB___EX_SERIES;
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("C-Series")) {
            deviceModel = JModel.TB___CSERIES;
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("T150")) {
            deviceModel = JModel.TB___T_150;
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("MXP")) {
            deviceModel = JModel.TB___MXP;
        }
        final List<JModel> models = new ArrayList<JModel>();
        models.add(deviceModel);
        final JWebResult result = softupdateManagerHandler
                .clearLegacySUProfile(userToken, models);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info(inputCmd.get("deviceModel")
                    + " clear profile succeed.");
            status = "SUCCESS";
        } else {
            logger.error(inputCmd.get("deviceModel")
                    + " clear profile failed.");
            status = "Failed";
        }
        return status;
    }

    /**
     * Delete the software update pkg(Used for Polycom device)
     *
     * @see deviceModel=$deviceModel
     *
     * @param deviceModel
     *            The Polycom device model
     * @return The result
     */
    public String deleteSoftupdate() {
        JDeviceType deviceType = null;
        JModel deviceModel = null;
        final Integer areaUgpId = new Integer(-1);
        String status = "Failed";
        if (inputCmd.get("deviceType").equalsIgnoreCase("GroupSeries")) {
            deviceType = JDeviceType.GROUPSERIES;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("RPD")) {
            deviceType = JDeviceType.RP___DESKTOP;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("ITP")) {
            deviceType = JDeviceType.ITP;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("HDX")) {
            deviceType = JDeviceType.HD;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("Cmad")) {
            deviceType = JDeviceType.VL;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("VVX")) {
            deviceType = JDeviceType.VX;
        } else {
            logger.error("soft update failed, please make sure your device type information is correct.");
            return status;
        }
        if (inputCmd.get("deviceModel").equalsIgnoreCase("WIN")) {
            deviceModel = JModel.RP___DESKTOP;
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("MAC")) {
            deviceModel = JModel.RP___DESKTOP___MAC;
        }
        final List<JSoftupdateVersion> softupdateVersions = softupdateManagerHandler
                .getSoftupdates(userToken, deviceType, deviceModel, areaUgpId);
        final List<Integer> softupdateVersionIds = new ArrayList<Integer>();
        for (final JSoftupdateVersion softupdateversion : softupdateVersions) {
            softupdateVersionIds.add(new Integer(
                    softupdateversion.getSoftUpdateVersionId()));
        }
        if (softupdateManagerHandler
                .deleteSoftupdate(userToken, softupdateVersionIds).getStatus()
                .compareTo(JStatus.SUCCESS) == 0) {
            logger.info(inputCmd.get("deviceModel")
                    + " delete softupdate succeed.");
            status = "SUCCESS";
        } else {
            logger.error(inputCmd.get("deviceModel")
                    + " delete softupdate failed.");
            status = "Failed";
        }
        return status;
    }

    /**
     * Get the legacy softupdate profile specified attribute value(used for
     * Cisco)
     *
     * @see deviceModel=$deviceModel <br/>
     *      keyword=UploadDT
     * @param deviceModel
     *            The Cisco device model
     * @return The legacy softupdate profile specified attribute value
     */
    public String getLegacySUProfileSpecific() {
        final String result = "NotFound";
        final String keyword = inputCmd.get("keyword");
        JModel deviceModel = null;
        if (inputCmd.get("deviceModel").equalsIgnoreCase("SXSeries")) {
            deviceModel = JModel.TB___SX_SERIES;
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("EXSeries")) {
            deviceModel = JModel.TB___EX_SERIES;
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("C-Series")) {
            deviceModel = JModel.TB___CSERIES;
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("T150")) {
            deviceModel = JModel.TB___T_150;
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("MXP")) {
            deviceModel = JModel.TB___MXP;
        }
        final List<JSoftupdateProfile> profiles = softupdateManagerHandler
                .getLegacySUProfiles(userToken);
        try {
            for (final JSoftupdateProfile profile : profiles) {
                if (profile.getModel().equals(deviceModel)) {
                    final String methodName = "get" + keyword;
                    final Method method = JSoftupdateProfile.class
                            .getDeclaredMethod(methodName, new Class[] {});
                    final Object obj = method.invoke(profile, new Object[] {});
                    if (obj != null) {
                        return obj.toString();
                    }
                }
            }
        } catch (NoSuchMethodException
                 | SecurityException
                 | IllegalAccessException
                 | IllegalArgumentException
                 | InvocationTargetException e) {
            logger.equals("No element for keyword " + keyword);
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void injectCmdArgs() {
        put("deviceType", "");
        put("deviceModel", "");
        put("description", "");
        put("keyFilename", "");
        put("softupdateFilename", "");
        put("testGroupId", "");
        put("xmaIP", "");
        put("transferFileUsername", "root");
        put("transferFilePassword", "Crestone_4357M_");
        put("versionToUse", "");
        put("allowNewerVersion", "");
        put("isNow", "true");
        put("isDeviceTime", "false");
        put("utcDate", "");
        put("groupName", "");
    }

    /**
     * Schedule the softupdate
     *
     * @see deviceId=$deviceId <br/>
     *      isNow=false <br/>
     *      utcDate=$utcDate
     * @param deviceId
     *            The device id
     * @param isNow
     *            Whether do softupdate now
     * @param utcDate
     *            The schedule time in millions
     * @return The result
     */
    public String scheduleSoftupdate() {
        String status = "Failed";
        final int deviceId = Integer.parseInt(inputCmd.get("deviceId"));
        final List<Integer> deviceIds = new ArrayList<Integer>();
        deviceIds.add(deviceId);
        final boolean isNow = Boolean.parseBoolean(inputCmd.get("isNow"));
        final boolean isDeviceTime = Boolean
                .parseBoolean(inputCmd.get("isDeviceTime"));
        long utcDate = System.currentTimeMillis();
        if (!inputCmd.get("utcDate").isEmpty()) {
            utcDate = Long.parseLong(inputCmd.get("utcDate"));
        }
        final JUIUtcDateTime updateTime = new JUIUtcDateTime();
        updateTime.setUnixTime(utcDate);
        final JWebResult result = softupdateManagerHandler
                .schedule(userToken,
                          deviceIds,
                          true,
                          false,
                          false,
                          false,
                          isDeviceTime,
                          isNow,
                          updateTime);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Schedule software update succeed.");
            status = "SUCCESS";
        } else {
            logger.error("Schedule software update failed");
            status = "Failed";
        }
        return status;
    }

    /**
     * Update hte legacy softupdate profile
     *
     * @see description=$description <br/>
     *      deviceType=Tandberg <br/>
     *      deviceModel=SXSeries <br/>
     *      xmaIP=$xmaAddr <br/>
     *      softupdateFilename=$pkgName6
     *
     * @param description
     *            The description
     * @param deviceType
     *            The device type(Cisco device is Tandberg)
     * @param deviceModel
     *            The device model
     * @param xmaIP
     *            The XMA ip
     * @param softupdateFilename
     *            The softupdate file name
     * @return The result
     */
    public String updateLegacyProfile() {
        JDeviceType deviceType = null;
        JModel deviceModel = null;
        String srcFile = "";
        String dstPath = XMA_LEGACY_UPLOAD_DIR;
        String softupdateFilename = "";
        final String description = inputCmd.get("description");
        String status = "Failed";
        if (inputCmd.get("deviceType").equalsIgnoreCase("Tandberg")) {
            deviceType = JDeviceType.TB;
            srcFile = "CiscoVersion" + File.separator
                    + inputCmd.get("softupdateFilename");
        }
        if (inputCmd.get("deviceModel").equalsIgnoreCase("SXSeries")) {
            deviceModel = JModel.TB___SX_SERIES;
            dstPath = dstPath + "Cisco/CiscoTelePresenceSXSeries";
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("EXSeries")) {
            deviceModel = JModel.TB___EX_SERIES;
            dstPath = dstPath + "Cisco/CiscoTelePresenceEXSeries";
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("C-Series")) {
            deviceModel = JModel.TB___CSERIES;
            dstPath = dstPath + "Cisco/CiscoC-Series";
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("T150")) {
            deviceModel = JModel.TB___T_150;
            dstPath = "/var/polycom/cma/SoftUpdate/TANDBERG MXP";
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("MXP")) {
            deviceModel = JModel.TB___MXP;
            dstPath = "/var/polycom/cma/SoftUpdate/TANDBERG T150";
        }
        softupdateFilename = dstPath + "/" + inputCmd.get("softupdateFilename");
        // Check the availability of the destination directory on XMA
        // The target directory is not available for a FTSU XMA
        if (!CommonUtils.sshAndOperation(inputCmd.get("xmaIP"),
                                         inputCmd.get("transferFileUsername"),
                                         inputCmd.get("transferFilePassword"),
                                         "ls " + dstPath)
                .contains("0")) {
            if (CommonUtils.sshAndOperation(inputCmd.get("xmaIP"),
                                            inputCmd.get("transferFileUsername"),
                                            inputCmd.get("transferFilePassword"),
                                            "mkdir -p " + dstPath)
                    .contains("1")) {
                logger.info("The destination directory" + dstPath
                        + " for the upgrade package is created successfully.");
            } else {
                logger.error("The destination directory " + dstPath
                        + " for the upgrade package is failed to create.");
            }
        }
        // Copy the source file to XMA
        logger.info("srcFile:" + srcFile);
        logger.info("dstPath:" + dstPath);
        CommonUtils.scpPutFile(inputCmd.get("xmaIP"),
                               inputCmd.get("transferFileUsername"),
                               inputCmd.get("transferFilePassword"),
                               srcFile,
                               dstPath);
        final JSoftupdateProfile profile = new JSoftupdateProfile();
        profile.setDescription(description);
        profile.setDeviceType(deviceType);
        profile.setModel(deviceModel);
        profile.setKeyFileNeeded(false);
        profile.setLocation(softupdateFilename);
        final JWebResult result = softupdateManagerHandler
                .updateLegacyProfile(userToken, profile);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info(inputCmd.get("deviceModel")
                    + " update profile succeed.");
            status = "SUCCESS";
        } else {
            logger.error(inputCmd.get("deviceModel")
                    + " update profile failed.");
            status = "Failed";
        }
        return status;
    }

    /**
     * Update the policy to do auto suftupdate of Polycom device
     *
     * @see deviceType=GROUPSERIES <br/>
     *      allowNewerVersion=false <br/>
     *      versionToUse=$version
     * @param deviceType
     *            The polycom device type
     * @param allowNewerVersion
     *            Whether all new version
     * @param versionToUse
     *            Use this version to do softupdate
     * @param deviceModel
     *            Win or Mac for RPD
     * @return The result
     */
    public String updatePolicy() {
        JDeviceType deviceType = null;
        JModel deviceModel = null;
        String versionToUse = "";
        boolean allowNewerVersion = false;
        String status = "Failed";
        if (inputCmd.get("deviceType").equalsIgnoreCase("GroupSeries")) {
            deviceType = JDeviceType.GROUPSERIES;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("RPD")) {
            deviceType = JDeviceType.RP___DESKTOP;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("ITP")) {
            deviceType = JDeviceType.ITP;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("HDX")) {
            deviceType = JDeviceType.HD;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("Cmad")) {
            deviceType = JDeviceType.VL;
        } else if (inputCmd.get("deviceType").equalsIgnoreCase("VVX")) {
            deviceType = JDeviceType.VX;
        } else {
            logger.error("soft update failed, please make sure your device type information is correct.");
            return status;
        }
        if (inputCmd.get("deviceModel").equalsIgnoreCase("WIN")) {
            deviceModel = JModel.RP___DESKTOP;
        } else if (inputCmd.get("deviceModel").equalsIgnoreCase("MAC")) {
            deviceModel = JModel.RP___DESKTOP___MAC;
        }
        if (!inputCmd.get("versionToUse").isEmpty()) {
            versionToUse = inputCmd.get("versionToUse");
        }
        if (inputCmd.get("allowNewerVersion").equalsIgnoreCase("true")) {
            allowNewerVersion = true;
        } else {
            allowNewerVersion = false;
        }
        if (softupdateManagerHandler
                .updatePolicy(userToken,
                              deviceType,
                              deviceModel,
                              -1,
                              versionToUse,
                              allowNewerVersion)
                .getStatus().compareTo(JStatus.SUCCESS) == 0) {
            logger.info(inputCmd.get("deviceModel")
                    + " update policy succeed.");
            status = "SUCCESS";
        } else {
            logger.error(inputCmd.get("deviceModel")
                    + " update policy failed.");
            status = "Failed";
        }
        return status;
    }
}