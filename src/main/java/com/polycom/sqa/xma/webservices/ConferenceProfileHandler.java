package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.ConferenceProfileManagerHandler;
import com.polycom.sqa.xma.webservices.driver.ConferencePolicyManagerHandler;
import com.polycom.webservices.ConferencePolicyManager.JStatus;
import com.polycom.webservices.ConferencePolicyManager.JWebResult;
import com.polycom.webservices.ConferenceProfileManager.JConferencePolicy;
import com.polycom.webservices.ConferenceProfileManager.JConferenceProfileAttribute;
import com.polycom.webservices.ConferencePolicyManager.JGroup;
import com.polycom.webservices.ConferencePolicyManager.JGroupType;

/**
 * Conference Profile handler. This class will handle the webservice request of
 * Conference Profile module
 *
 * @author wbchao
 *
 */
public class ConferenceProfileHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "downloadProfileById ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "mcuId=20004436-96d1-4c70-8eba-a56a9c3e9e65 profileName=autoprofile policyName=autoprofile";
        final String command = "http://localhost:8888/PlcmRmWeb/JConferenceProfileManager ConferenceProfileManager "
                + method + auth + params;
        final ConferenceProfileHandler handler = new ConferenceProfileHandler(
                                                                              command);
        final String result = handler.build();
        System.out.println("result==" + result);
    }

    ConferenceProfileManagerHandler cpmh;
    ConferencePolicyManagerHandler cpmh2;
    
    public ConferenceProfileHandler(final String cmd) throws IOException {
        super(cmd);
        cpmh = new ConferenceProfileManagerHandler(webServiceUrl);
        cpmh2 = new ConferencePolicyManagerHandler(webServiceUrl);
    }

    /**
     * Download the conference profile from MCU
     *
     * @see profileName=$profileName</br>
     *      mcuId=$mcuId
     *
     * @param profileName
     *            The profile name on MCU
     * @param mcuId
     *            The MCU id
     *
     * @return Profile download result
     */
    public String downloadProfileById() {
        String status = "Failed";
        final String profileName = inputCmd.get("profileName");
        final String mcuId = inputCmd.get("mcuId");
        final JConferenceProfileAttribute conferenceProfile = getConferenceProfileByName(mcuId,
                                                                                         profileName);
        final JConferencePolicy policy = cpmh
                .downloadProfileById(userToken,
                                     mcuId,
                                     conferenceProfile.getProfileId());
        if (policy == null) {
            logger.error("Fail to download conference profile " + profileName
                    + " from MCU");
            return status + ", fail to download conference profile "
                    + profileName + " from MCU";
        }

        policy.setPolicyName(profileName);
        final com.polycom.webservices.ConferencePolicyManager.JConferencePolicy copyedPolicy = new com.polycom.webservices.ConferencePolicyManager.JConferencePolicy();
        try {
            CommonUtils.copyProperties(policy, copyedPolicy);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        
        final JGroup group = new JGroup();
        group.setUgpId(1005);
        group.setGroupName("Scheduler");
        group.setGroupType(JGroupType.ROLE);
        group.setIsDefaultGroup(false);
        group.setVisible(false);
        group.setIsInherited(false);
        group.setDbKey(0);
        group.setPolicyId(0);
        group.setBelongsToAreaUgpId(0);
        copyedPolicy.getRoles().add(group);
        
        final JWebResult result = cpmh2.addConferencePolicy(userToken,copyedPolicy);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
        	status = "SUCCESS";
            logger.info("Download conference policy successfully.");
        } else {
        	status = "Failed";
            logger.error("Download conference policy failed.");
            return status + "Download conference policy failed.";
        }
        return status;
    }
    
    /**
     * Internal method, get the JConferenceProfileAttribute by name
     *
     * @param mcuId
     *            The specified mcu id
     * @param conferenceProfileName
     *            The specified profile name
     * @return The JConferenceProfileAttribute
     */
    private JConferenceProfileAttribute
            getConferenceProfileByName(final String mcuId,
                    final String conferenceProfileName) {
        final List<JConferenceProfileAttribute> conferenceProfiles = cpmh
                .getProfileList(userToken, mcuId);
        for (final JConferenceProfileAttribute profile : conferenceProfiles) {
            if (conferenceProfileName.equals(profile.getDisplayName())) {
                return profile;
            }
        }
        return null;
    }

    @Override
    protected void injectCmdArgs() {
        put("profileName", "");
        put("mcuId", "");
    }
}
