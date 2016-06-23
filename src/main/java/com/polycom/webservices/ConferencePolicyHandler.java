package com.polycom.webservices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.ConferencePolicyManagerHandler;
import com.polycom.sqa.xma.webservices.driver.ConferenceProfileManagerHandler;
import com.polycom.webservices.ConferencePolicyManager.JConferencePolicy;
import com.polycom.webservices.ConferencePolicyManager.JConferencePolicyAttribute;
import com.polycom.webservices.ConferencePolicyManager.JGroup;
import com.polycom.webservices.ConferencePolicyManager.JGroupType;
import com.polycom.webservices.ConferencePolicyManager.JStatus;
import com.polycom.webservices.ConferencePolicyManager.JWebResult;
import com.polycom.webservices.ConferenceProfileManager.JConferenceProfileAttribute;

/**
 * Conference Policy handler. This class will handle the webservice request of
 * Conference Policy module
 *
 * @author wbchao
 *
 */
public class ConferencePolicyHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "addConferencePolicy ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "policyId=a09b93d7-13c5-46d7-9b8b-6e7007ee3ce7 policyName=auto ";
        final String method = "addConferencePolicyWithRoutingName ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "profileName=test mcuId=e367fc8a-c98e-45c1-a888-8d04fe9bda11 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JConferencePolicyManager ConferencePolicyManager "
                + method + auth + params;
        final ConferencePolicyHandler handler = new ConferencePolicyHandler(
                command);
        final String result = handler.build();
        System.out.println("result==" + result);
    }

    ConferencePolicyManagerHandler cpmh;
    ConferenceProfileManagerHandler cpmh2;

    public ConferencePolicyHandler(final String cmd) throws IOException {
        super(cmd);
        cpmh = new ConferencePolicyManagerHandler(webServiceUrl);
        cpmh2 = new ConferenceProfileManagerHandler(webServiceUrl);
    }

    /**
     * Add conference policy
     *
     * @see policyId=$policyId</br>
     *      policyName=auto
     *
     * @param policyId
     *            The conference profile Uid
     * @param policyName
     *            The new policy name to add
     * @return The result
     */
    public String addConferencePolicy() {
        String status = "Failed";
        final String policyId = inputCmd.get("policyId");
        final JConferencePolicy conferencePolicy = cpmh
                .getByConferencePolicyId(userToken, policyId);
        if (conferencePolicy == null) {
            return "Failed, did not find the conference policy with id "
                    + policyId;
        }
        final String policyName = inputCmd.get("policyName");
        conferencePolicy.setPolicyName(policyName);
        final JWebResult result = cpmh.addConferencePolicy(userToken,
                                                           conferencePolicy);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Add conference policy successfully.");
        } else {
            status = "Failed";
            logger.error("Add conference policy failed.");
            return status + "Add conference policy failed.";
        }
        return status;
    }

    /**
     * Add conference policy using routing name
     *
     * @see profileName=$profileName</br>
     *      mcuId=$mcuId
     *
     * @param profileName
     *            The profile name on MCU
     * @param mcuId
     *            The MCU id
     * @param dialOption
     *            Dial option setting on XMA
     * @param supportedMCUs
     *            support mcu setting on XMA
     * @param alwaysUseMCU
     *            Is always use MCU setting on XMA
     * @param isAudioOnlyTemplate
     *            Is audio only template setting on XMA
     *                     
     * @return Profile download result
     */
    public String addConferencePolicyWithRoutingName() {
        String status = "Failed";
        final String profileName = inputCmd.get("profileName");
        final String mcuId = inputCmd.get("mcuId");
        JConferenceProfileAttribute conferenceProfile = null;
        final List<JConferenceProfileAttribute> conferenceProfiles = cpmh2
                .getProfileList(userToken, mcuId);
        for (final JConferenceProfileAttribute profile : conferenceProfiles) {
            if (profileName.equals(profile.getDisplayName())) {
            	conferenceProfile = profile;
            	break;
            }
        }

        final JConferencePolicy policy = new JConferencePolicy();
        policy.setPolicyName(profileName);
//        policy.setAudioOnly(false);
        policy.setAutoStartConference(false);
        policy.setBelongsToAreaUgpId(0);
        policy.setEnableProfile(false);
        policy.setExistingProfileRoutingName(conferenceProfile.getRoutingName());
        policy.setForExternalAPI(false);
        policy.setGatheringRecordIndicating(false);
        policy.setIsDefault(false);
        policy.setLineRate(conferenceProfile.getLineRate());
        policy.setOverrideConferenceIVR(false);
        policy.setPolicytype(0);
        
        final JConferencePolicyAttribute cpa1 = new JConferencePolicyAttribute();
        cpa1.setAttributeName("conferenceProfileXMACustomGroup.dialOptions");
        cpa1.setAttributeValue(inputCmd.get("dialOption"));
        policy.getProfileAttributes().add(cpa1);
        final JConferencePolicyAttribute cpa2 = new JConferencePolicyAttribute();
        cpa2.setAttributeName("conferenceProfileXMACustomGroup.supportedMCUs");
        cpa2.setAttributeValue(inputCmd.get("supportedMCUs"));
        policy.getProfileAttributes().add(cpa2);
        final JConferencePolicyAttribute cpa3 = new JConferencePolicyAttribute();
        cpa3.setAttributeName("conferenceProfileXMACustomGroup.alwaysUseMCU");
        cpa3.setAttributeValue(inputCmd.get("alwaysUseMCU"));
        policy.getProfileAttributes().add(cpa3);
        final JConferencePolicyAttribute cpa4 = new JConferencePolicyAttribute();
        cpa4.setAttributeName("conferenceProfileXMACustomGroup.isAudioOnlyTemplate");
        cpa4.setAttributeValue(inputCmd.get("isAudioOnlyTemplate"));
        policy.getProfileAttributes().add(cpa4);
        
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
        policy.getRoles().add(group);
        
        policy.setUseEntryQueue(false);
        policy.setVideoConference(false);
        
        final JWebResult result = cpmh.addConferencePolicy(userToken,policy);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
        	status = "SUCCESS";
            logger.info("Add conference policy successfully.");
        } else {
        	status = "Failed";
            logger.error("Add conference policy failed.");
            return status + "Add conference policy failed.";
        }
        return status;
    }

    public String deleteConferencePolicy() {
        String status = "Failed";
        final String policyName = inputCmd.get("policyName");
        final JConferencePolicy policy = getConferencePolicyByName(policyName);
        if (policy == null) {
            return "SUCCESS, could not find the conference policy "
                    + policyName + ", so no need to delete";
        }
        final JWebResult result = cpmh
                .deleteConferencePolicyById(userToken, policy.getPolicyId());
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Delete conference policy " + policyName
                    + " successfully.");
        } else {
            status = "Failed";
            logger.error("Delete conference policy " + policyName + " failed.");
            return status + "Delete conference policy " + policyName
                    + " failed.";
        }
        return status;
    }

    public String editConferencePolicy() {
        String status = "Failed";
        final String policyName = inputCmd.get("policyName");
        final JConferencePolicy policy = getConferencePolicyByName(policyName);
        if (policy == null) {
            return "SUCCESS, could not find the conference policy "
                    + policyName + ", so no need to delete";
        }

        // update attribute
        for (int i = 1; i <= 10; i++) {
            final String keyword = inputCmd.get("field" + i);
            final String strValue = inputCmd.get("value" + i).replaceAll("~",
                    " ");
            if (keyword.isEmpty()) {
                continue;
            }
            try {
                CommonUtils.invokeSetMethod(policy, keyword, strValue);
            } catch (IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException
                    | InstantiationException e) {
                e.printStackTrace();
                return "Failed, " + e.getMessage();
            }
        }

        final JWebResult result = cpmh
                .eidtConferencePolicy(userToken, policy);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Edit conference policy " + policyName
                    + " successfully.");
        } else {
            status = "Failed";
            logger.error("Edit conference policy " + policyName + " failed.");
            return status + "Edit conference policy " + policyName
                    + " failed.";
        }
        return status;
    }

    /**
     * Internal method, get the JConferencePolicy by name
     *
     * @param name
     *            The specified name
     * @return The JConferencePolicy
     */
    private JConferencePolicy getConferencePolicyByName(final String name) {
        final List<JConferencePolicy> profiles = cpmh
                .getAllConferencePolicy(userToken);
        for (final JConferencePolicy p : profiles) {
            if (name.equals(p.getPolicyName())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Get the conference policy specified attribute value
     *
     * @see policyName=auto
     *      </br>keyword=profileAttributes\[attributeName=
     *      conferenceProfileGeneralGroup.transferRate\]:attributeValue
     *
     * @param policyName
     *            The conference policy name
     * @param keyword
     *            The attribute name
     * @return The conference policy specified attribute value
     */
    public String getConferencePolicySpecific() {
        final String policyName = inputCmd.get("policyName").replaceAll("~",
                " ");
        final String keyword = inputCmd.get("keyword");
        final JConferencePolicy policy = getConferencePolicyByName(policyName);
        if (policy == null) {
            return "NotFound, could not find the conference policy "
                    + policyName;
        }
        return CommonUtils.invokeGetMethod(policy, keyword);
    }
    
    @Override
    protected void injectCmdArgs() {
        put("policyId", "");
        put("policyName", "");
        put("profileName", "");
        put("dialOption", "Dial_In_Dial_Out");
        put("supportedMCUs", "RMX");
        put("alwaysUseMCU", "FALSE");
        put("isAudioOnlyTemplate", "FALSE");
    }
}
