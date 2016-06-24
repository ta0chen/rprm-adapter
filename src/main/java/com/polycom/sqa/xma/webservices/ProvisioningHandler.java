package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.polycom.sqa.xma.webservices.driver.ProvisioningManagerHandler;
import com.polycom.webservices.ProvisioningManager.JDeviceType;
import com.polycom.webservices.ProvisioningManager.JPolicy;
import com.polycom.webservices.ProvisioningManager.JPolicyAttribute;
import com.polycom.webservices.ProvisioningManager.JPolicyType;
import com.polycom.webservices.ProvisioningManager.JScheduledProfile;
import com.polycom.webservices.ProvisioningManager.JStatus;
import com.polycom.webservices.ProvisioningManager.JUIUtcDateTime;
import com.polycom.webservices.ProvisioningManager.JWebResult;

/**
 * Scheduled Provisioning handler. This class will handle the webservice request
 * of
 * Scheduled Provisioning module
 *
 * @author wbchao
 *
 */
public class ProvisioningHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "checkProfileAttributes ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params1 = "profileName=cisco_SXSeries_auto attr:Audio_Volume=15 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JProvisioningManager ProvisioningManager "
                + method + auth + params1;
        final ProvisioningHandler handler = new ProvisioningHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    private final ProvisioningManagerHandler provisioningManagerHandler;

    public ProvisioningHandler(final String cmd) throws IOException {
        super(cmd);
        provisioningManagerHandler = new ProvisioningManagerHandler(
                                                                    webServiceUrl);
    }

    /**
     * Add profile to XMA
     *
     * @see deviceType=TB <br/>
     *      profileName=$profileNames($ciscoEP($id)) <br/>
     *      attr:EndpointSeries=$ciscoEP($id) <br/>
     *      attr:SIP_Profile_1_Proxy_1_Address=$sipAddress <br/>
     *      attr:SIP_Profile_1_URI_1=$ciscoEP($id)_sip_$id <br/>
     *      attr:H323_Profile_Gatekeeper_Address=$gkAddress <br/>
     *      attr:H323_Profile_H323Alias_E164=1000$id <br/>
     *      attr:H323_Profile_H323Alias_ID=$ciscoEP($id)_h323_$id <br/>
     *      attr:Phonebook_Server_1_URL=$phoneServerUrl
     *
     * @param deviceType
     *            The device type
     * @param profileName
     *            The profile name
     * @param attrName
     *            Should start with "attr:" e.g.
     *            attr:SIP_Profile_1_Proxy_1_Address (This param could be more
     *            than one)
     * @return The result
     */
    public String addProfile() {
        String status = "Failed";
        final String deviceType = inputCmd.get("deviceType");
        final String profileName = inputCmd.get("profileName");
        final JPolicy profile = new JPolicy();
        profile.setIsDefault(false);
        profile.setPolicyDeviceType(JDeviceType.fromValue(deviceType));
        profile.setPolicyId(0);
        profile.setPolicyName(profileName);
        profile.setPolicyOrder(0);
        profile.setPolicyType(JPolicyType.SCHEDULED___PROVISIONING);
        final List<JPolicyAttribute> profileAttrs = new ArrayList<JPolicyAttribute>();
        for (final String key : inputCmd.keySet()) {
            if (key.startsWith("attr:")) {
                final String attrName = key.split(":")[1];
                final String attrValue = inputCmd.get(key);
                final JPolicyAttribute attr = new JPolicyAttribute();
                attr.setAttrName(attrName);
                attr.setAttrValue(attrValue);
                profileAttrs.add(attr);
            }
        }
        final JWebResult result = provisioningManagerHandler
                .addProfile(userToken, profile, profileAttrs);
        if (result
                .getStatus()
                .equals(com.polycom.webservices.ProvisioningManager.JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Add profile " + profileName + " successfully");
        } else {
            status = "Failed";
            final String errorMsg = "Add profile " + profileName + " failed";
            logger.error(errorMsg);
            return status + errorMsg;
        }
        return status;
    }

    /**
     * Add profile to XMA
     *
     * @see profileName=$profileName <br/>
     *      attr:Audio_Volume=$audioVolume <br/>
     *      attr:EndpointSeries=$model <br/>
     *      attr:SIP_Profile_1_Proxy_1_Address=$sipAddress <br/>
     *      attr:SIP_Profile_1_URI_1=$model\_sip_3 <br/>
     *      attr:H323_Profile_Gatekeeper_Address=$gkAddress <br/>
     *      attr:H323_Profile_H323Alias_E164=10003 <br/>
     *      attr:H323_Profile_H323Alias_ID=$model\_h323_3 <br/>
     *      attr:Phonebook_Server_1_URL=$phoneServerUrl
     *
     * @param deviceType
     *            The device type
     * @param profileName
     *            The profile name
     * @param attrName
     *            Should start with "attr:" e.g.
     *            attr:SIP_Profile_1_Proxy_1_Address (This param could be more
     *            than one)
     * @return The result
     */
    public String checkProfileAttributes() {
        String status = "Failed";
        String detailCheckErrMsg = "";
        final String profileName = inputCmd.get("profileName");
        final List<JScheduledProfile> profiles = provisioningManagerHandler
                .getScheduledProfiles(userToken);
        int policyId = 0;
        for (final JScheduledProfile profile : profiles) {
            if (profile.getPolicyName().equals(profileName)) {
                policyId = profile.getPolicyId();
                break;
            }
        }
        final List<JPolicyAttribute> profileAttrs = provisioningManagerHandler
                .getProfileAttributes(userToken, policyId);
        int checkAttributesCount = 0;
        int passedAttributesCount = 0;
        for (final String key : inputCmd.keySet()) {
            if (key.startsWith("attr:")) {
                checkAttributesCount++;
                final String attrName = key.split(":")[1];
                final String attrValue = inputCmd.get(key);
                for (final JPolicyAttribute profileAttr : profileAttrs) {
                    final String name = profileAttr.getAttrName();
                    if (name.equals(attrName)) {
                        if (profileAttr.getAttrValue().equals(attrValue)) {
                            passedAttributesCount++;
                        } else {
                            logger.error(profileName + " " + attrName
                                         + " checking: " + "expect value: "
                                         + attrValue + " actual value: "
                                         + profileAttr.getAttrValue());
                            detailCheckErrMsg = detailCheckErrMsg
                                    .concat(profileName + " " + attrName
                                            + " checking: " + "expect value: "
                                            + attrValue + " actual value: "
                                            + profileAttr.getAttrValue());
                        }
                    }
                }
            }
        }
        if (passedAttributesCount == checkAttributesCount) {
            status = "SUCCESS";
            logger.info(profileName + " attributes check successfully");
        } else {
            status = "Failed";
            final String errorMsg = profileName + " attributes check failed";
            logger.error(errorMsg);
            return status + ". Fail point is " + detailCheckErrMsg;
        }
        return status;
    }

    /**
     * Clear the provisioning status on endpoints list
     *
     * @param deviceIds
     *            Endpoint device IDs list
     *
     * @return The result
     */
    public String clearProvisioningStatus(){
        String status = "Failed";
        final String[] deviceStrIds = inputCmd.get("deviceIds").split("\\|");
        final List<Integer> deviceIds = new ArrayList<Integer>();
        for (final String id : deviceStrIds) {
            deviceIds.add(Integer.parseInt(id));
        }

        final JWebResult result = provisioningManagerHandler.clearProvisioningStatus(userToken, deviceIds);

        if(result.getStatus().equals(JStatus.SUCCESS)){
            status = "SUCCESS";
            logger.info("Successfully clean the provisioning status on endpoints.");
        } else {
            status = "Failed";
            logger.error("Failed to clean the provisioning status on endpoints.");
            return status+" Failed to clean the provisioning status on endpoints.";
        }
        return status;
    }

    /**
     * Clone Profile
     *
     * @see profileName=$profileName <br/>
     *      newProfileName=$profileNameClone
     * @param profileName
     *            Src profile name
     * @param newProfileName
     *            Taget profile name
     * @return The result
     */
    public String cloneProfile() {
        String status = "Failed";
        final String profileName = inputCmd.get("profileName");
        final String newProfileName = inputCmd.get("newProfileName");
        final List<JScheduledProfile> profiles = provisioningManagerHandler
                .getScheduledProfiles(userToken);
        int profileId = 0;
        for (final JScheduledProfile profile : profiles) {
            if (profile.getPolicyName().equals(profileName)) {
                profileId = profile.getPolicyId();
                break;
            }
        }
        final JWebResult result = provisioningManagerHandler
                .cloneProfile(userToken, profileId, newProfileName);
        if (result
                .getStatus()
                .equals(com.polycom.webservices.ProvisioningManager.JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Clone profile " + profileName + " successfully");
        } else {
            status = "Failed";
            final String errorMsg = "Clone profile " + profileName + " failed";
            logger.error(errorMsg);
            return status + errorMsg;
        }
        return status;
    }

    /**
     * Delete profile
     *
     * @see profileName=$profileNames($id)
     *
     * @param profileName
     *            Profile name on XMA
     * @return The result
     */
    public String deleteProfile() {
        String status = "Failed";
        final String profileName = inputCmd.get("profileName");
        final List<JScheduledProfile> profiles = provisioningManagerHandler
                .getScheduledProfiles(userToken);
        int profileId = 0;
        for (final JScheduledProfile profile : profiles) {
            if (profile.getPolicyName().equals(profileName)) {
                profileId = profile.getPolicyId();
                break;
            }
        }
        final JWebResult result = provisioningManagerHandler
                .deleteProfile(userToken, profileId);
        if (result
                .getStatus()
                .equals(com.polycom.webservices.ProvisioningManager.JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Delete profile " + profileName + " successfully");
        } else {
            status = "Failed";
            final String errorMsg = "Delete profile " + profileName + " failed";
            logger.error(errorMsg);
            return status + errorMsg;
        }
        return status;
    }

    /**
     * Edit the profile
     *
     * @see profileName=$profileName <br/>
     *      attr:Audio_Volume=$audioVolume
     * @param profileName
     *            The profile name to edit
     * @param attrName
     *            Should start with "attr:" e.g.
     *            attr:SIP_Profile_1_Proxy_1_Address (This param could be more
     *            than one)
     * @return The result
     */
    public String editProfile() {
        String status = "Failed";
        final String profileName = inputCmd.get("profileName");
        final List<JScheduledProfile> profiles = provisioningManagerHandler
                .getScheduledProfiles(userToken);
        int policyId = 0;
        for (final JScheduledProfile profile : profiles) {
            if (profile.getPolicyName().equals(profileName)) {
                policyId = profile.getPolicyId();
                break;
            }
        }
        final List<JPolicyAttribute> profileAttrs = provisioningManagerHandler
                .getProfileAttributes(userToken, policyId);
        for (final String key : inputCmd.keySet()) {
            boolean findAttr = false;
            if (key.startsWith("attr:")) {
                final String attrName = key.split(":")[1];
                final String attrValue = inputCmd.get(key);
                for (final JPolicyAttribute profileAttr : profileAttrs) {
                    if (profileAttr.getAttrName().equals(attrName)) {
                        profileAttr.setAttrValue(attrValue);
                        findAttr = true;
                        break;
                    }
                }
                if (!findAttr) {
                    final JPolicyAttribute newAttr = new JPolicyAttribute();
                    newAttr.setAttrName(attrName);
                    newAttr.setAttrValue(attrValue);
                    newAttr.setPolicyId(policyId);
                    profileAttrs.add(newAttr);
                }
            }
        }
        final JWebResult result = provisioningManagerHandler
                .editProfile(userToken, profileAttrs);
        if (result
                .getStatus()
                .equals(com.polycom.webservices.ProvisioningManager.JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Edit profile " + profileName + " successfully");
        } else {
            status = "Failed";
            final String errorMsg = "Edit profile " + profileName + " failed";
            logger.error(errorMsg);
            return status + errorMsg;
        }
        return status;
    }

    /**
     * Get the profile id with specified profile name
     *
     * @see profileName=$profileName
     *
     * @param profileName
     *            The profile name
     * @return The profile id
     */
    public String getProfileId() {
        String result = "NotFound";
        final String profileName = inputCmd.get("profileName");
        final List<JScheduledProfile> profiles = provisioningManagerHandler
                .getScheduledProfiles(userToken);
        for (final JScheduledProfile profile : profiles) {
            if (profile.getPolicyName().equals(profileName)) {
                result = profile.getPolicyId() + "";
            }
        }
        return result;
    }

    @Override
    protected void injectCmdArgs() {
        put("deviceType", "");
        put("profileName", "");
        put("keyword", "");
        put("deviceIds", "0");
        put("profileId", "0");
        put("isNow", "true");
        put("utcDate", "");
        put("isDeviceTime", "false");
        put("newProfileName", "");
    }

    /**
     * Schedule a provision
     *
     * @see deviceIds=$deviceId <br/>
     *      profileId=$profileId <br/>
     *      isNow=true
     *
     * @param deviceIds
     *            The devices need to schedule provision
     * @param profileId
     *            The specified provision profile
     * @param isDeviceTime
     *            Boolean value, if to use device time(Optional, default is
     *            false)
     * @param isNow
     *            Boolean value, if do provision now(Optional, default is true)
     * @param utcDate
     *            Long value, the schedule provision UTC time(If isNow=true,
     *            this param is no used)
     * @return The result
     */
    public String scheduleProvision() {
        String status = "Failed";
        final String[] deviceStrIds = inputCmd.get("deviceIds").split("\\|");
        final List<Integer> deviceIds = new ArrayList<Integer>();
        for (final String id : deviceStrIds) {
            deviceIds.add(Integer.parseInt(id));
        }
        final int profileId = Integer.parseInt(inputCmd.get("profileId"));
        final boolean isDeviceTime = Boolean.parseBoolean(inputCmd
                                                          .get("isDeviceTime"));
        final boolean isNow = Boolean.parseBoolean(inputCmd.get("isNow"));
        final JUIUtcDateTime utcDate = new JUIUtcDateTime();
        utcDate.setUnixTime(System.currentTimeMillis());
        final String utcStr = inputCmd.get("utcDate");
        if (!utcStr.isEmpty()) {
            utcDate.setUnixTime(Long.parseLong(utcStr));
        }
        final JWebResult result = provisioningManagerHandler
                .scheduleProvision(userToken,
                                   deviceIds,
                                   profileId,
                                   isDeviceTime,
                                   isNow,
                                   utcDate);
        final String scheduleTime = isNow ? "now" : "later";
        if (result
                .getStatus()
                .equals(com.polycom.webservices.ProvisioningManager.JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Schedule provision " + scheduleTime + " successfully");
        } else {
            status = "Failed";
            final String errorMsg = "Schedule provision " + scheduleTime
                    + " failed";
            logger.error(errorMsg);
            return status + errorMsg;
        }
        return status;
    }
}