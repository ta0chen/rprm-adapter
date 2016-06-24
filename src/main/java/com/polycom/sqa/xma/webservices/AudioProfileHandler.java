package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.polycom.sqa.xma.webservices.driver.AudioProfileManagerHandler;
import com.polycom.sqa.xma.webservices.driver.ProfileManagerHandler;
import com.polycom.webservices.AudioProfileManager.JAudioProfile;
import com.polycom.webservices.AudioProfileManager.JDeviceModelVO;
import com.polycom.webservices.AudioProfileManager.JProfileGenerateType;
import com.polycom.webservices.AudioProfileManager.JProfileTag;
import com.polycom.webservices.AudioProfileManager.JStatus;
import com.polycom.webservices.AudioProfileManager.JWebResult;

/**
 * Audio Profile handler. This class will handle the webservice request of Audio
 * Profile module
 *
 * @author wbchao
 *
 */
public class AudioProfileHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "addAudioProfile ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "profileName=Default~Phone~Basic~Profile tagName=internal.sipReadLdapUri tagValue=FALSE ";
        // final String params2 =
        // "profileName=ctSiteProfile profileType=EndpointSiteProvision tagNameList=H323.enableH323,SIP.enableSIP,SIP.autoServerDiscovery,internal.ldapserver.default,internal.xmppserver.default,CONFIG.security.SNMPEnabled,H323.gatekeeperAddress,SIP.sipProxyServer,SIP.sipRegistrarServer,internal.ldapserver.ip,internal.xmppserver.ip tagValueList=TRUE,TRUE,FALSE,FALSE,FALSE,FALSE,172.21.125.226,172.21.125.226,172.21.125.226,172.21.125.226,172.21.125.226 ";
        final String params3 = "profileName=audioProfile1 profileType=PhoneAudioProvision deviceModelList=Sound~Station~DUO,Sound~Station~IP~5000 ";
        // final String params4 =
        // "profileName=advancedProfile1 profileType=PhoneAdvancedProvision deviceModelList=Sound~Station~DUO,Sound~Station~IP~5000 ";
        // final String params5 =
        // "profileName=aaa profileNameAfterClone=aaa deviceModelList=Sound~Station~IP~6000 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JAudioProfileManager AudioProfileManager "
                + method + auth + params3;
        final AudioProfileHandler handler = new AudioProfileHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    private final AudioProfileManagerHandler audioProfileManagerHandler;

    public AudioProfileHandler(final String cmd) throws IOException {
        super(cmd);
        audioProfileManagerHandler = new AudioProfileManagerHandler(
                                                                    webServiceUrl);
    }

    /**
     * Add audio profile
     *
     * @see profileName=basicProfile1 <br/>
     *      profileType=PhoneAdvancedProvision <br/>
     *      deviceModelList=Sound~Station~IP~6000,Sound~Station~IP~7000 <br/>
     *      tagNameList=feature.callList.enabled,feature.callListMissed.enabled <br/>
     *      tagValueList=1,1
     *
     * @param profileName
     *            The audio profile name
     * @param profileType
     *            The profile type
     * @param tagNameList
     *            The tag name list, separated by ","(Optional)
     * @param tagValueList
     *            The tag value list, separated by ","(Optional)
     * @param deviceModelList
     *            The device model list
     * @return The result
     */
    @SuppressWarnings("unused")
    public String addAudioProfile() {
        String status = "Failed";
        final ProfileManagerHandler profileManagerHandler = new ProfileManagerHandler(
                webServiceUrl);
        try {
            final String profileName = inputCmd.get("profileName")
                    .replaceAll("~", " ");
            final String[] tagNameList = inputCmd.get("tagNameList").split(",");
            final String[] tagValueList = inputCmd.get("tagValueList")
                    .split(",");
            final String[] deviceModelList = inputCmd.get("deviceModelList")
                    .split(",");
            JAudioProfile profile4Create = new JAudioProfile();
            final List<JAudioProfile> defaultProfileList = audioProfileManagerHandler
                    .getAllDefaultAudioProfiles(userToken);
            profile4Create = getDefaultPhoneProfile(inputCmd.get("profileType")
                                                    .trim());
            if (!profile4Create.equals(null)) {
                for (int i = 0; i < tagNameList.length; i++) {
                    for (int k = 0; k < profile4Create.getProfileTags().size(); k++) {
                        if (profile4Create.getProfileTags().get(k).getTagName()
                                .equalsIgnoreCase(tagNameList[i])) {
                            profile4Create.getProfileTags().get(k)
                            .setTagValue(tagValueList[i]);
                        }
                    }
                }
                profile4Create.setBelongsToAreaUgpId(-1);
                profile4Create.setProfileId(-1);
                profile4Create.setGroupName(inputCmd.get("profileType"));
                profile4Create.setGenereateType(2);
                profile4Create
                .setGenereateTypeEnum(JProfileGenerateType.NEW___GENERATE);
                profile4Create.setDescription("Phone Profile");
                profile4Create.setIsDefault(false);
                profile4Create.setProfileName(profileName);
                final List<JDeviceModelVO> deviceModelVO = getDeviceModelVO(inputCmd.get("profileType")
                                                                            .trim(),
                                                                            deviceModelList);
                for (final JDeviceModelVO dmvo : deviceModelVO) {
                    profile4Create.getDeviceModels().add(dmvo);
                }
                if (audioProfileManagerHandler.add(userToken, profile4Create)
                        .getStatus().equals(JStatus.SUCCESS)) {
                    status = "SUCCESS";
                    logger.info("Successfully add the profile " + profileName);
                } else {
                    status = "Failed";
                    logger.error("Failed to add the profile " + profileName);
                    return status + " Failed to add the profile " + profileName;
                }
            } else {
                status = "Failed";
                logger.error("The input provision profile type is not correct.");
                return status
                        + " The input provision profile type is not correct.";
            }
        } catch (final Exception e) {
            status = "Failed";
            logger.error("Exception found when trying to create provision profile "
                    + "with error message " + e.getMessage());
            e.printStackTrace();
            return status
                    + " Exception found when trying to create provision profile "
                    + "with error message " + e.getMessage();
        }
        return status;
    }

    /**
     * Check the audio profile items
     *
     * @see profileName=basicProfile1 <br/>
     *      tagNameList=feature.callList.enabled,feature.callListMissed.enabled <br/>
     *      tagValueList=1,1 <br/>
     *      deviceModelList=Sound~Station~IP~6000,Sound~Station~IP~7000
     *
     * @param profileName
     *            The audio profile name
     * @param tagNameList
     *            The tag name list, separated by ","(Optional)
     * @param tagValueList
     *            The tag value list, separated by ","(Optional)
     * @param deviceModelList
     *            The device model list
     * @return The result
     */
    public String checkAudioProfileItems() {
        String status = "Failed";
        String comments = "";
        int passedItemCount = 0;
        final String profileName = inputCmd.get("profileName").replaceAll("~",
                " ");
        final String[] tagNameList = inputCmd.get("tagNameList").split(",");
        final String[] tagValueList = inputCmd.get("tagValueList").split(",");
        final JAudioProfile profile = getProfile(profileName);
        if (profile == null) {
            status = "Failed";
            logger.error("There is no profile named " + profileName);
            return status + " there is no profile named " + profileName;
        }
        for (final JProfileTag tag : profile.getProfileTags()) {
            for (int i = 0; i < tagNameList.length; i++) {
                if (tagNameList[i].equals(tag.getTagName())) {
                    if (tagValueList[i].equals(tag.getTagValue())) {
                        passedItemCount++;
                    } else {
                        comments += " item " + tagNameList[i]
                                + " check failed, expect value is "
                                + tagValueList[i] + ", but actual value is "
                                + tag.getTagValue();
                    }
                }
            }
        }
        if (passedItemCount == tagNameList.length) {
            status = "SUCCESS";
            logger.info("Check audio profile items of " + profileName
                        + " successfully.");
        } else {
            status = "Failed";
            logger.error("Check audio profile items of " + profileName
                         + " failed.");
            return status + comments;
        }
        return status;
    }

    /**
     * Clone the audio phone profile The profile tag could be edit on clone
     *
     * @see profileName=basicProfile1 <br/>
     *      profileNameAfterClone=basicProfile1 <br/>
     *      deviceModelList=Sound~Station~IP~7000
     *
     * @param profileName
     *            The profile name
     * @param tagNameList
     *            The tag name list, separated by ","(Optional)
     * @param tagValueList
     *            The tag value list, separated by ","(Optional)
     * @param deviceModelList
     *            The device model list
     * @return
     */
    public String cloneAudioPhoneProfile() {
        String status = "Failed";
        try {
            final String profileName = inputCmd.get("profileName")
                    .replaceAll("~", " ");
            final String profileNameAfterClone = inputCmd
                    .get("profileNameAfterClone").replaceAll("~", " ");
            final String[] tagNameList = inputCmd.get("tagNameList").split(",");
            final String[] tagValueList = inputCmd.get("tagValueList")
                    .split(",");
            final String[] deviceModelList = inputCmd.get("deviceModelList")
                    .split(",");
            final JAudioProfile profile = getProfile(profileName);
            if (profile == null) {
                status = "Failed";
                logger.error("There is no audio profile named " + profileName);
                return status + " there is no audio profile named "
                + profileName;
            } else {
                for (int i = 0; i < tagNameList.length; i++) {
                    for (int k = 0; k < profile.getProfileTags().size(); k++) {
                        profile.getProfileTags().get(k).setParentId(-1);
                        profile.getProfileTags().get(k).setTagId(-1);
                        if (profile.getProfileTags().get(k).getTagName()
                                .equalsIgnoreCase(tagNameList[i])) {
                            profile.getProfileTags().get(k).setParentId(-1);
                            profile.getProfileTags().get(k).setTagId(-1);
                            profile.getProfileTags().get(k)
                            .setTagValue(tagValueList[i]);
                        }
                    }
                }
                profile.setBelongsToAreaUgpId(0);
                profile.setProfileId(-1);
                profile.setGenereateType(1);
                profile.setGenereateTypeEnum(JProfileGenerateType.ALREADY___GENERATE);
                profile.setDescription("Phone Profile");
                profile.setIsDefault(false);
                profile.setProfileName(profileNameAfterClone);
                profile.getDeviceModels().clear();
                final List<JDeviceModelVO> deviceModelVO = getDeviceModelVO(profile.getGroupName(),
                                                                            deviceModelList);
                for (final JDeviceModelVO dmvo : deviceModelVO) {
                    profile.getDeviceModels().add(dmvo);
                }
                if (audioProfileManagerHandler.add(userToken, profile)
                        .getStatus().equals(JStatus.SUCCESS)) {
                    status = "SUCCESS";
                    logger.info("Successfully add the profile "
                            + profileNameAfterClone);
                } else {
                    status = "Failed";
                    logger.error("Failed to add the profile "
                            + profileNameAfterClone);
                    return status + " Failed to add the profile "
                    + profileNameAfterClone;
                }
            }
        } catch (final Exception e) {
            status = "Failed";
            logger.error("Exception found when trying to clone provision profile "
                    + "with error message " + e.getMessage());
            e.printStackTrace();
            return status
                    + " Exception found when trying to clone provision profile "
                    + "with error message " + e.getMessage();
        }
        return status;
    }

    /**
     * Delete the audio profile
     *
     * @see profileName=advancedProfile1
     *
     * @param profileName
     *            The profile name
     * @return The result
     */
    public String deleteAudioProfile() {
        String status = "Failed";
        final String profileName = inputCmd.get("profileName").replaceAll("~",
                " ");
        final JAudioProfile profile = getProfile(profileName);
        if (profile == null) {
            status = "Failed";
            logger.error("There is no audio profile named " + profileName);
            return status + " there is no audio profile named " + profileName;
        }
        final List<Integer> profileIds = new ArrayList<Integer>();
        profileIds.add(profile.getProfileId());
        if (audioProfileManagerHandler.deleteByIds(userToken, profileIds)
                .getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfully delete the audio provision profile "
                    + profileName);
        } else {
            status = "Failed";
            logger.error("Failed to delete the audio provision profile "
                    + profileName);
            return status + " Failed to delete the audio provision profile "
            + profileName;
        }
        return status;
    }

    /**
     * Internal method, get the default audio profile
     *
     * @param profileType
     *            The profile type
     * @return JAudioProfile
     */
    private JAudioProfile getDefaultPhoneProfile(final String profileType) {
        JAudioProfile defaultProfile = new JAudioProfile();
        final List<JAudioProfile> defaultProfileList = audioProfileManagerHandler
                .getAllDefaultAudioProfiles(userToken);
        for (final JAudioProfile profile : defaultProfileList) {
            if (profile.getGroupName().equals(profileType)) {
                defaultProfile = profile;
                return defaultProfile;
            }
        }
        return null;
    }

    /**
     * Internal method, get the device model VO
     *
     * @param profileType
     *            The profile type
     * @param deviceModelList
     *            The device model list
     * @return The result
     */
    private List<JDeviceModelVO> getDeviceModelVO(final String profileType,
                                                  final String[] deviceModelList) {
        final List<JDeviceModelVO> deviceModelVO = new ArrayList<JDeviceModelVO>();
        final List<JDeviceModelVO> deviceModeVOList = audioProfileManagerHandler
                .getAllAvailableAudioModel(userToken, profileType);
        for (int i = 0; i < deviceModelList.length; i++) {
            for (final JDeviceModelVO dvo : deviceModeVOList) {
                final String actualModelName = deviceModelList[i]
                        .replaceAll("~", " ").trim();
                if (dvo.getModelName().equalsIgnoreCase(actualModelName)) {
                    deviceModelVO.add(dvo);
                }
            }
        }
        return deviceModelVO;
    }

    /**
     * Internal method, get the audio profile
     *
     * @param profileName
     *            The profile name
     * @return JAudioProfile
     */
    private JAudioProfile getProfile(final String profileName) {
        final List<JAudioProfile> profiles = audioProfileManagerHandler
                .getAllEndpointProfile(userToken);
        for (JAudioProfile profile : profiles) {
            if (profile.getProfileName().equals(profileName)) {
                profile = audioProfileManagerHandler
                        .getByProfileId(userToken, profile.getProfileId());
                return profile;
            }
        }
        return null;
    }

    @Override
    protected void injectCmdArgs() {
        // You can also provide a name list for some operations like reset
        put("profileName", "");
        put("profileNameAfterClone", "");
        put("profileType", "");
        put("tagName", "");
        put("tagValue", "");
        put("tagNameList", "");
        put("tagValueList", "");
        put("deviceModelList", "");
    }

    /**
     * Reset audio profile
     *
     * @see profileName=Default~Phone~Advanced~Profile
     *
     * @param profileName
     *            The profile name
     * @return The result name
     */
    public String resetAudioPhoneProfile() {
        String status = "Failed";
        final List<Integer> audioProfileIds = new ArrayList<Integer>();
        try {
            final String profileName = inputCmd.get("profileName")
                    .replaceAll("~", " ");
            final String[] profileNameList = profileName.split(",");
            for (int i = 0; i < profileNameList.length; i++) {
                final JAudioProfile profile = getProfile(profileNameList[i]);
                if (profile == null) {
                    logger.error("There is no profile named " + profileName);
                } else {
                    audioProfileIds.add(profile.getProfileId());
                }
            }
            if (audioProfileManagerHandler
                    .resetAudioProfileByIds(userToken, audioProfileIds)
                    .getStatus().equals(JStatus.SUCCESS)) {
                status = "SUCCESS";
                logger.info("Successfully reset the input audio phone provision profile: "
                        + profileName);
            } else {
                status = "Failed";
                logger.error("Failed reset the input audio phone provision profile: "
                        + profileName);
                return status
                        + " Failed reset the input audio phone provision profile: "
                        + profileName;
            }
        } catch (final Exception e) {
            status = "Failed";
            logger.error("Exception found when trying to reset provision profile "
                    + "with error message " + e.getMessage());
            e.printStackTrace();
            return status
                    + " Exception found when trying to reset provision profile "
                    + "with error message " + e.getMessage();
        }
        return status;
    }

    /**
     * Update the audio profile
     *
     * @see profileName=audioProfile1 <br/>
     *      profileType=PhoneAudioProvision <br/>
     *      deviceModelList=Sound~Station~IP~6000
     *
     * @param profileName
     *            The audio profile name
     * @param profileType
     *            The profile type
     * @param tagNameList
     *            The tag name list, separated by ","(Optional)
     * @param tagValueList
     *            The tag value list, separated by ","(Optional)
     * @param deviceModelList
     *            The device model list
     * @return The result
     */
    public String updateAudioProfile() {
        String status = "Failed";
        final String profileName = inputCmd.get("profileName").replaceAll("~",
                " ");
        final String[] tagNameList = inputCmd.get("tagNameList").split(",");
        final String[] tagValueList = inputCmd.get("tagValueList").split(",");
        final String[] deviceModelList = inputCmd.get("deviceModelList")
                .split(",");
        final JAudioProfile profile = getProfile(profileName);
        if (profile == null) {
            status = "Failed";
            logger.error("There is no profile named " + profileName);
            return status + " there is no profile named " + profileName;
        }
        // update device list
        final List<JDeviceModelVO> deviceModelVO = getDeviceModelVO(profile.getGroupName(),
                                                                    deviceModelList);
        profile.getDeviceModels().clear();
        for (final JDeviceModelVO dmvo : deviceModelVO) {
            profile.getDeviceModels().add(dmvo);
        }
        // update tag values
        for (final JProfileTag tag : profile.getProfileTags()) {
            for (int i = 0; i < tagNameList.length; i++) {
                if (tagNameList[i].equals(tag.getTagName())) {
                    logger.info("tag: " + tagNameList[i] + ", value: "
                            + tagValueList[i]);
                    tag.setTagValue(tagValueList[i]);
                    break;
                }
            }
        }
        final JWebResult result = audioProfileManagerHandler.update(userToken,
                                                                    profile);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Update audio profile " + profileName
                        + " successfully.");
        } else {
            status = "Failed";
            logger.error("Update audio profile " + profileName + " failed.");
            return status + " update audio profile " + profileName;
        }
        return status;
    }
}