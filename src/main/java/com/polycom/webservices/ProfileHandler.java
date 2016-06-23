package com.polycom.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.ProfileManagerHandler;
import com.polycom.webservices.ProfileManager.JPolicyType;
import com.polycom.webservices.ProfileManager.JProfile;
import com.polycom.webservices.ProfileManager.JProfileTag;
import com.polycom.webservices.ProfileManager.JStatus;
import com.polycom.webservices.ProfileManager.JWebResult;

/**
 * Profile handler. This class will handle the webservice request of Profile
 * module
 *
 * @author wbchao
 *
 */
public class ProfileHandler extends XMAWebServiceHandler {
    private static final String DEFAULT_NETWORK_PROFILE_NAME      = "Default Network Provisioning Profile";
    private static final String DEFAULT_ADMIN_CONFIG_PROFILE_NAME = "Default Admin Config Provisioning Profile";
    private static final String DEFAULT_RPAD_PROFILE_NAME         = "Default RPAD Provisioning Profile";

    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "resetProfile ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = "profileName=clone ";
        // final String method = "getProfileSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "profileName=Default~RPAD~Provisioning~Profile keyword=isDefault ";
        final String method = "addProfile ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = " profileName=Cloned~Site~Profile profileType=EndpointSiteProvision baseProfileName=New~Site~Profile ";
        // final String method = "getProfileSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "profileName=New~RPAD~Profile
        // keyword=profileTags[tagName=CONFIG.callSettings.answerSingleCall]:tagValue
        // ";
        final String command = "http://localhost:8888/PlcmRmWeb/rest/JProfileManager ProfileManager "
                + method + auth + params;
        final ProfileHandler handler = new ProfileHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    private final ProfileManagerHandler profileManagerHandler;

    public ProfileHandler(final String cmd) throws IOException {
        super(cmd);
        profileManagerHandler = new ProfileManagerHandler(webServiceUrl);
    }

    /**
     * Add the profile
     *
     * @see profileName=autoGrpProfile <br/>
     *      profileType=EndpointGroupProvision <br/>
     *      tagNameList=CONFIG.callSettings.answerSingleCall,CONFIG.callSettings
     *      .answerMultipointCall,CONFIG.callSettings.muteAutoAnswer,XMPP.
     *      autoAcceptInvitation,H323.inboundCallRate,H323.outboundCallRate
     *      <br/>
     *      tagValueList=YES,YES,FALSE,TRUE,1920,1920
     * @param profileName
     *            The profile name
     * @param profileType
     *            The profile type
     * @param tagNameList
     *            The tag name list
     * @param tagValueList
     *            The tag value list
     * @return The result
     */
    public String addProfile() {
        String status = "Failed";
        String errorMsg = "";
        try {
            final String profileName = inputCmd.get("profileName")
                    .replaceAll("~", " ");
            final String[] tagNameList = inputCmd.get("tagNameList").split(",");
            final String[] tagValueList = inputCmd.get("tagValueList")
                    .split(",");
            JProfile profile4Create = null;
            final String profileType = inputCmd.get("profileType");
            JPolicyType policyType = null;
            if (profileType.equalsIgnoreCase("AdminConfigProvision")) {
                profile4Create = getProfileByName(DEFAULT_ADMIN_CONFIG_PROFILE_NAME);
                policyType = JPolicyType.ENDPOINTPROFILE;
            } else if (profileType.equalsIgnoreCase("NetworkProvision")) {
                profile4Create = getProfileByName(DEFAULT_NETWORK_PROFILE_NAME);
                policyType = JPolicyType.ENDPOINTPROFILE;
            } else if (profileType.equalsIgnoreCase("RpadServerProvision")) {
                profile4Create = getProfileByName(DEFAULT_RPAD_PROFILE_NAME);
                policyType = JPolicyType.SERVERPROFILE;
            } else {
                errorMsg = "The input provision profile type is not correct.";
                status = "Failed";
                logger.error(errorMsg);
                return status + ", " + errorMsg;
            }
            final String baseProfileName = inputCmd.get("baseProfileName")
                    .replaceAll("~", " ");
            if (!baseProfileName.isEmpty()) {
                profile4Create = getProfileByName(baseProfileName);
            }
            for (int i = 0; i < tagNameList.length; i++) {
                for (final JProfileTag profiletag : profile4Create
                        .getProfileTags()) {
                    if (profiletag.getTagName()
                            .equalsIgnoreCase(tagNameList[i])) {
                        profiletag.setTagValue(tagValueList[i]);
                    }
                }
            }
            final boolean isLegalProfileName = profileManagerHandler
                    .isLegalProfileName(userToken, policyType, profileName);
            if (isLegalProfileName) {
                profile4Create.setIsDefault(false);
                profile4Create.setProfileName(profileName);
            } else {
                status = "Failed";
                errorMsg = "The input profile name is illegal. "
                        + "Please use another name and try again.";
                logger.error(errorMsg);
                return status + ", " + errorMsg;
            }
            final JWebResult result = profileManagerHandler.add(userToken,
                                                                profile4Create);
            if (result.getStatus().equals(JStatus.SUCCESS)) {
                status = "SUCCESS";
                logger.info("Successfully add the profile " + profileName);
            } else {
                status = "Failed";
                errorMsg = "Failed to add the profile " + profileName + ", "
                        + result.getStatus().value();
                logger.error(errorMsg);
                return status + ", " + errorMsg;
            }
        } catch (final Exception e) {
            e.printStackTrace();
            status = "Failed";
            errorMsg = "Exception found when trying to create provision profile "
                    + "with error message " + e.getMessage();
            logger.error(errorMsg);
            return status + ", " + errorMsg;
        }
        return status;
    }

    /**
     * Delete the profile
     *
     * @see profileName=autoSiteProfile
     * @param profileName
     *            The profile name
     * @return The result
     */
    public String deleteProfile() {
        String status = "Failed";
        final String profileName = inputCmd.get("profileName").replaceAll("~",
                                                                          " ");
        final JProfile profile = getProfileByName(profileName);
        if (profile == null) {
            status = "Failed";
            logger.error("There is no profile named " + profileName);
            return status + " there is no profile named " + profileName;
        }
        final List<Integer> profileIds = new ArrayList<Integer>();
        profileIds.add(profile.getProfileId());
        if (profileManagerHandler.deleteByIds(userToken, profileIds).getStatus()
                .equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfully delete the provision profile "
                    + profileName);
        } else {
            status = "Failed";
            logger.error("Failed to delete the provision profile "
                    + profileName);
            return status + " Failed to delete the provision profile "
                    + profileName;
        }
        return status;
    }

    /**
     * Internal method, get the method by name
     *
     * @param profileName
     *            The profile name
     * @return JProfile
     */
    private JProfile getProfileByName(final String profileName) {
        final List<JProfile> epProfiles = profileManagerHandler
                .getAllEndpointProfile(userToken);
        for (final JProfile profile : epProfiles) {
            if (profile.getProfileName().equals(profileName)) {
                return profileManagerHandler
                        .getByProfileId(userToken, profile.getProfileId());
            }
        }
        final List<JProfile> serverProfiles = profileManagerHandler
                .getAllServerProfile(userToken);
        for (final JProfile profile : serverProfiles) {
            if (profile.getProfileName().equals(profileName)) {
                return profileManagerHandler
                        .getByProfileId(userToken, profile.getProfileId());
            }
        }
        return null;
    }

    /**
     * Get the profile specified attribute value
     *
     * @see profileName=autoSiteProfile<br/>
     *      keyword=description
     * @param profileName
     *            The profile name
     * @param keyword
     *            The attribute name
     * @return The profile specified attribute value
     */
    public String getProfileSpecific() {
        final String profileName = inputCmd.get("profileName").replaceAll("~",
                                                                          " ");
        final JProfile profile = getProfileByName(profileName);
        if (profile == null) {
            return "NotFound, could not find the profile with name: "
                    + profileName;
        }
        final String keyword = inputCmd.get("keyword");
        return CommonUtils.invokeGetMethod(profile, keyword);
    }

    @Override
    protected void injectCmdArgs() {
        put("profileName", "");
        put("profileType", "");
        put("tagName", "");
        put("tagValue", "");
        put("tagNameList", "");
        put("tagValueList", "");
        put("baseProfileName", "");
    }

    /**
     * Reset the profile
     *
     * @see profileName=autoSiteProfile
     * @param profileName
     *            The profile name
     * @return The result
     */
    public String resetProfile() {
        String status = "Failed";
        final String profileName = inputCmd.get("profileName").replaceAll("~",
                                                                          " ");
        final JProfile profile = getProfileByName(profileName);
        if (profile == null) {
            status = "Failed";
            logger.error("There is no profile named " + profileName);
            return status + " there is no profile named " + profileName;
        }
        final JWebResult result = profileManagerHandler
                .resetById(userToken, profile.getProfileId());
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Reset profile " + profileName + " successfully.");
        } else {
            status = "Failed";
            logger.error("Reset profile " + profileName + " failed.");
            return status + " reset profile " + profileName;
        }
        return status;
    }

    /**
     * Update the profile
     *
     * @see profileName=autoSiteProfile <br/>
     *      tagName=internal.sipReadLdapUri <br/>
     *      tagValue=FALSE
     * @param profileName
     *            The profile name
     * @param tagName
     *            The attribute name
     * @param tagValue
     *            The attribute value to update
     * @return The result
     */
    public String updateProfile() {
        String status = "Failed";
        final String profileName = inputCmd.get("profileName").replaceAll("~",
                                                                          " ");
        final String tagName = inputCmd.get("tagName");
        final String tagValue = inputCmd.get("tagValue").replaceAll("~", " ");
        final JProfile profile = getProfileByName(profileName);
        if (profile == null) {
            status = "Failed";
            logger.error("There is no profile named " + profileName);
            return status + " there is no profile named " + profileName;
        }
        for (final JProfileTag tag : profile.getProfileTags()) {
            if (tag.getTagName().equals(tagName)) {
                tag.setTagValue(tagValue);
            }
        }
        final JWebResult result = profileManagerHandler.update(userToken,
                                                               profile);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Update profile " + profileName + " successfully.");
        } else {
            status = "Failed";
            logger.error("Update profile " + profileName + " failed.");
            return status + " update profile " + profileName;
        }
        return status;
    }
}