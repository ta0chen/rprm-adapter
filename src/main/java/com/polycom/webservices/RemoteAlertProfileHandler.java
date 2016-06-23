package com.polycom.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.polycom.sqa.xma.webservices.driver.RemoteAlertProfileManagerHandler;
import com.polycom.webservices.RemoteAlertProfileManager.JDeviceAlertSetting;
import com.polycom.webservices.RemoteAlertProfileManager.JDeviceForDetails;
import com.polycom.webservices.RemoteAlertProfileManager.JDeviceType;
import com.polycom.webservices.RemoteAlertProfileManager.JDeviceTypeAlertSetting;
import com.polycom.webservices.RemoteAlertProfileManager.JRemoteAlertProfile;
import com.polycom.webservices.RemoteAlertProfileManager.JStatus;
import com.polycom.webservices.RemoteAlertProfileManager.JViewType;
import com.polycom.webservices.RemoteAlertProfileManager.JWebResult;

/**
 * Remote alert profile handler
 *
 * @author Tao Chen
 *
 */
public class RemoteAlertProfileHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "addRemoteAlertProfile ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "name=haha description=autoRemoteAlertProfile enable=true systemAlertsInfo=true systemAlertsMajor=true systemAlertsMinor=true deviceTypeList=HDX,GroupSeries,VVX deviceAlertList=true|true|false,true|false|true,false|true|false epIpList=1.1.1.1,172.21.126.80 epAlertList=true|true|true,true|false|true networkDeviceIpList=172.21.120.60 networkDeviceAlertList=false|true|true ";
        final String command = "http://localhost:8888/PlcmRmWeb/JRemoteAlertProfileManager RemoteAlertProfileManager "
                + method + auth + params;
        final RemoteAlertProfileHandler handler = new RemoteAlertProfileHandler(
                                                                                command);
        handler.logger.info("hoho");
        final String result = handler.build();
        handler.logger.info("The result is: " + result);
    }

    private final RemoteAlertProfileManagerHandler remoteAlertProfileManagerHandler;

    public RemoteAlertProfileHandler(final String cmd) throws IOException {
        super(cmd);
        remoteAlertProfileManagerHandler = new RemoteAlertProfileManagerHandler(
                                                                                webServiceUrl);
    }

    /**
     * Create the remote alert profile on the RPRM
     *
     * @see name=Remote alert profile name<br/>
     *      description=Remote alert profile description<br/>
     *      enable=True or False<br/>
     *      systemAlertsInfo=True or False<br/>
     *      systemAlertsMinor=True or False<br/>
     *      systemAlertsMajor=True or False<br/>
     *      deviceTypeList=HDX,VVX,GroupSeries<br/>
     *      deviceAlertList=true|true|false,true|false|true,false|true|false<br/>
     *      epIpList=1.1.1.1,2.2.2.2<br/>
     *      epAlertList=true|true|true,true|false|true<br/>
     *      networkDeviceIpList=1.1.1.1,2.2.2.2<br/>
     *      networkDeviceAlertList=true|true|true,true|false|true<br/>
     *
     * @param name
     *            Remote alert profile name
     * @param description
     *            Remote alert profile description
     * @param enable
     *            Enable the remote alert profile or not
     * @param systemAlertsInfo
     *            Enable the system info alert or not
     * @param systemAlertsMinor
     *            Enable the system minor alert or not
     * @param systemAlertsMajor
     *            Enable the system major alert or not
     * @param deviceTypeList
     *            Device type list
     * @param deviceAlertList
     *            Device alert list, this is in order of info|minor|major for
     *            each element of the list
     * @param epIpList
     *            IP addresses list of the endpoints
     * @param epAlertList
     *            Endpoint alert list, this is in order of info|minor|major for
     *            each element of the list
     * @param networkDeviceIpList
     *            Network device IP addresses list
     * @param networkDeviceAlertList
     *            Network device alert list, this is in order of
     *            info|minor|major for each element of the list
     *
     * @return The result, SUCCESS or Failed
     */
    public String addRemoteAlertProfile() {
        String status = "Failed";
        boolean profileEnable = false;
        boolean alertWarning = false;
        boolean alertMajor = false;
        boolean alertMinor = false;
        boolean alertCritical = false;

        final JRemoteAlertProfile alertProfile = new JRemoteAlertProfile();

        // set the basic settings for the alert profile
        if (inputCmd.get("enable").equalsIgnoreCase("true")) {
            profileEnable = true;
        }
        if (inputCmd.get("systemAlertsWarning").equalsIgnoreCase("true")) {
            alertWarning = true;
        }
        if (inputCmd.get("systemAlertsMajor").equalsIgnoreCase("true")) {
            alertMajor = true;
        }
        if (inputCmd.get("systemAlertsMinor").equalsIgnoreCase("true")) {
            alertMinor = true;
        }
        if (inputCmd.get("systemAlertsCritical").equalsIgnoreCase("true")) {
            alertCritical = true;
        }
        alertProfile.setName(inputCmd.get("name"));
        alertProfile.setDescription(inputCmd.get("description"));
        alertProfile.setProfileEnabled(profileEnable);
        alertProfile.setSystemAlertsWarning(alertWarning);
        alertProfile.setSystemAlertsMajor(alertMajor);
        alertProfile.setSystemAlertsMinor(alertMinor);
        alertProfile.setSystemAlertsCritical(alertCritical);

        // set the device types and their severities
        final List<JDeviceForDetails> deviceForDetailsList = remoteAlertProfileManagerHandler
                .getEndpoints4PagingInAlertProfile(userToken,
                                                   JViewType.EP_ALERTABLE___VIEW);

        final List<JDeviceForDetails> networkForDetailsList = remoteAlertProfileManagerHandler
                .getAlertableNetworkDevices(userToken);

        final String[] deviceTypeList = inputCmd.get("deviceTypeList")
                .replaceAll("~", " ").split(",");
        // deviceAlertList should be in order as info|minor|major for each
        // device type
        final String[] deviceAlertList = inputCmd.get("deviceAlertList")
                .split(",");

        // device alert settings
        // set the default value firstly
        final List<JDeviceType> dtList = new ArrayList<JDeviceType>();
        dtList.add(JDeviceType.NX);
        dtList.add(JDeviceType.TB);
        dtList.add(JDeviceType.HD);
        dtList.add(JDeviceType.QD);
        dtList.add(JDeviceType.VX);
        dtList.add(JDeviceType.LS);
        dtList.add(JDeviceType.GROUPSERIES);
        dtList.add(JDeviceType.ITP);
        dtList.add(JDeviceType.CT);
        dtList.add(JDeviceType.MG);
        dtList.add(JDeviceType.CR);

        for (final JDeviceType dt : dtList) {
            final JDeviceTypeAlertSetting deviceTypeAlertSetting = new JDeviceTypeAlertSetting();
            deviceTypeAlertSetting.setDeviceType(dt);
            deviceTypeAlertSetting.setWarning(false);
            deviceTypeAlertSetting.setMajor(false);
            deviceTypeAlertSetting.setMinor(false);
            deviceTypeAlertSetting.setCritical(false);
            alertProfile.getDeviceTypeAlertSettings()
            .add(deviceTypeAlertSetting);
        }

        // update the device type alert setting according to the input
        for (int i = 0; i < deviceTypeList.length; i++) {
            boolean warning = false;
            boolean major = false;
            boolean minor = false;
            boolean critical = false;
            JDeviceType deviceType = null;
            final JDeviceTypeAlertSetting deviceTypeAlertSetting = new JDeviceTypeAlertSetting();
            if (deviceTypeList[i].equalsIgnoreCase("VSX")) {
                deviceType = JDeviceType.NX;
            } else if (deviceTypeList[i].equalsIgnoreCase("Cisco")) {
                deviceType = JDeviceType.TB;
            } else if (deviceTypeList[i].equalsIgnoreCase("HDX")) {
                deviceType = JDeviceType.HD;
            } else if (deviceTypeList[i].equalsIgnoreCase("QDX")) {
                deviceType = JDeviceType.QD;
            } else if (deviceTypeList[i].equalsIgnoreCase("VVX")) {
                deviceType = JDeviceType.VX;
            } else if (deviceTypeList[i].equalsIgnoreCase("LifeSize")) {
                deviceType = JDeviceType.LS;
            } else if (deviceTypeList[i].equalsIgnoreCase("GroupSeries")) {
                deviceType = JDeviceType.GROUPSERIES;
            } else if (deviceTypeList[i].equalsIgnoreCase("ITP")) {
                deviceType = JDeviceType.ITP;
            } else if (deviceTypeList[i].equalsIgnoreCase("CENTRO")) {
                deviceType = JDeviceType.CT;
            } else if (deviceTypeList[i].equalsIgnoreCase("MGC")) {
                deviceType = JDeviceType.MG;
            } else if (deviceTypeList[i].equalsIgnoreCase("RMX")) {
                deviceType = JDeviceType.CR;
            }

            final String[] deviceAlert = deviceAlertList[i].split("\\|");
            // warning
            if (deviceAlert[0].equalsIgnoreCase("true")) {
                warning = true;
            }
            // minor
            if (deviceAlert[1].equalsIgnoreCase("true")) {
                major = true;
            }
            // major
            if (deviceAlert[2].equalsIgnoreCase("true")) {
                minor = true;
            }
            // critical
            if (deviceAlert[3].equalsIgnoreCase("true")) {
                critical = true;
            }
            deviceTypeAlertSetting.setDeviceType(deviceType);
            deviceTypeAlertSetting.setWarning(warning);
            deviceTypeAlertSetting.setMajor(major);
            deviceTypeAlertSetting.setMinor(minor);
            deviceTypeAlertSetting.setCritical(critical);

            int location = 0;
            final List<JDeviceTypeAlertSetting> dts = alertProfile
                    .getDeviceTypeAlertSettings();
            for (final JDeviceTypeAlertSetting dt : dts) {
                if (dt.getDeviceType().equals(deviceType)) {
                    location = dts.indexOf(dt);
                    break;
                }
            }
            dts.remove(location);
            dts.add(location, deviceTypeAlertSetting);
        }

        // endpoint alert settings
        if (!inputCmd.get("epIpList").isEmpty()) {
            final String[] epIpList = inputCmd.get("epIpList").split(",");
            // epAlertList should be in order as info|minor|major for each
            // endpoint
            final String[] epAlertList = inputCmd.get("epAlertList").split(",");
            for (int i = 0; i < epIpList.length; i++) {
                boolean warning = false;
                boolean major = false;
                boolean minor = false;
                boolean critical = false;
                JDeviceForDetails deviceDetail = new JDeviceForDetails();
                final JDeviceAlertSetting deviceAlertSetting = new JDeviceAlertSetting();
                final String[] epAlert = epAlertList[i].split("\\|");
                // warning
                if (epAlert[0].equalsIgnoreCase("true")) {
                    warning = true;
                }
                // minor
                if (epAlert[1].equalsIgnoreCase("true")) {
                    major = true;
                }
                // major
                if (epAlert[2].equalsIgnoreCase("true")) {
                    minor = true;
                }
                // critical
                if (epAlert[3].equalsIgnoreCase("true")) {
                    critical = true;
                }
                deviceAlertSetting.setWarning(warning);
                deviceAlertSetting.setMajor(major);
                deviceAlertSetting.setMinor(minor);
                deviceAlertSetting.setCritical(critical);

                for (final JDeviceForDetails deviceForDetails : deviceForDetailsList) {
                    if (deviceForDetails.getIpAddress().equals(epIpList[i])) {
                        deviceDetail = deviceForDetails;
                        break;
                    }
                }
                deviceAlertSetting.setDevice(deviceDetail);
                alertProfile.getEndpointAlertSettings().add(deviceAlertSetting);
            }
        }

        // network device alert settings
        if (!inputCmd.get("networkDeviceIpList").isEmpty()) {
            final String[] networkDeviceIpList = inputCmd
                    .get("networkDeviceIpList").split(",");
            // networkDeviceAlertList should be in order as info|minor|major for
            // each network
            final String[] networkDeviceAlertList = inputCmd
                    .get("networkDeviceAlertList").split(",");
            for (int i = 0; i < networkDeviceIpList.length; i++) {
                boolean warning = false;
                boolean major = false;
                boolean minor = false;
                boolean critical = false;
                JDeviceForDetails deviceDetail = new JDeviceForDetails();
                final JDeviceAlertSetting deviceAlertSetting = new JDeviceAlertSetting();
                final String[] networkAlert = networkDeviceAlertList[i]
                        .split("\\|");
                // warning
                if (networkAlert[0].equalsIgnoreCase("true")) {
                    warning = true;
                }
                // minor
                if (networkAlert[1].equalsIgnoreCase("true")) {
                    major = true;
                }
                // major
                if (networkAlert[2].equalsIgnoreCase("true")) {
                    minor = true;
                }
                // critical
                if (networkAlert[3].equalsIgnoreCase("true")) {
                    critical = true;
                }
                deviceAlertSetting.setWarning(warning);
                deviceAlertSetting.setMajor(major);
                deviceAlertSetting.setMinor(minor);
                deviceAlertSetting.setCritical(critical);
                for (final JDeviceForDetails networkDeviceDetails : networkForDetailsList) {
                    if (networkDeviceDetails.getIpAddress()
                            .equals(networkDeviceIpList[i])) {
                        deviceDetail = networkDeviceDetails;
                        break;
                    }
                }
                deviceAlertSetting.setDevice(deviceDetail);
                alertProfile.getNetworkDeviceAlertSettings()
                .add(deviceAlertSetting);
            }
        }

        final JWebResult result = remoteAlertProfileManagerHandler
                .addRemoteAlertProfile(userToken, alertProfile);

        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Successfully add the remote alert profile "
                    + inputCmd.get("name"));
            status = "SUCCESS";
        } else {
            logger.error("Failed to add the remote alert profile "
                    + inputCmd.get("name"));
            status = "Failed";
        }

        return status;
    }

    /**
     * Delet the specified remote alert profile from the RPRM
     *
     * @param name
     *            Remote alert profile name
     * @return The result, SUCCESS or Failed
     */
    public String deleteRemoteAlertProfile() {
        String status = "Failed";
        final JRemoteAlertProfile alertProfile = findRemoteAlertProfile(inputCmd
                                                                        .get("name"));
        if (alertProfile == null) {
            logger.error("Cannot find the specified remote alert profile "
                    + inputCmd.get("name"));
            status = "Failed, "
                    + "Cannot find the specified remote alert profile "
                    + inputCmd.get("name");
        }
        final JWebResult result = remoteAlertProfileManagerHandler
                .deleteRemoteAlertProfile(userToken,
                                          alertProfile.getProfileId());
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Successfully delete the remote alert profile "
                    + inputCmd.get("name") + " from the RPRM.");
            status = "SUCCESS";
        } else {
            logger.error("Failed to delete the remote alert profile "
                    + inputCmd.get("name") + " from the RPRM.");
        }
        return status;
    }

    /**
     * Find the specified remote alert profile
     *
     * @param name
     *            Remote alert profile name
     * @return
     */
    public JRemoteAlertProfile findRemoteAlertProfile(final String name) {
        JRemoteAlertProfile result = null;
        final List<JRemoteAlertProfile> remoteAlertProfileList = remoteAlertProfileManagerHandler
                .getRemoteAlertProfiles(userToken);
        for (final JRemoteAlertProfile remoteAlertProfile : remoteAlertProfileList) {
            if (remoteAlertProfile.getName().equals(name)) {
                result = remoteAlertProfile;
                break;
            }
        }
        return result;
    }

    @Override
    protected void injectCmdArgs() {
        inputCmd.put("name", "");
        inputCmd.put("description", "");
        inputCmd.put("enable", "false");
        inputCmd.put("systemAlertsWarning", "false");
        inputCmd.put("systemAlertsCritical", "false");
        inputCmd.put("systemAlertsMajor", "false");
        inputCmd.put("systemAlertsMinor", "false");
        inputCmd.put("deviceTypeList", "");
        inputCmd.put("deviceAlertList", "");
        inputCmd.put("epAlertList", "");
        inputCmd.put("epIpList", "");
        inputCmd.put("networkDeviceAlertList", "");
        inputCmd.put("networkDeviceIpList", "");
    }
}
