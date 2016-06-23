package com.polycom.webservices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.DeviceMetaManagerHandler;
import com.polycom.sqa.xma.webservices.driver.ProfileManagerHandler;
import com.polycom.sqa.xma.webservices.driver.RuleManagerHandler;
import com.polycom.sqa.xma.webservices.driver.SiteTopoManagerHandler;
import com.polycom.webservices.ProfileManager.JProfile;
import com.polycom.webservices.RuleManager.JNode;
import com.polycom.webservices.RuleManager.JNodeType;
import com.polycom.webservices.RuleManager.JRule;
import com.polycom.webservices.RuleManager.JRuleRelation;
import com.polycom.webservices.RuleManager.JRuleRelationType;
import com.polycom.webservices.RuleManager.JRuleStatus;
import com.polycom.webservices.RuleManager.JStatus;
import com.polycom.webservices.RuleManager.JWebResult;

/**
 * Rule handler. This class will handle the webservice request of Rule module
 *
 * @author wbchao
 *
 */
public class RuleHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "addRule ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params1 = "profileNameList=AdminConfig3,NetworkProfile3   description=auto        provisionRuleName=UserRule      nodeType=user   nodeAttribute=userName  nodeOperater=in         nodeValue=debuguser5,aduser1 ";
        // final String method = "updateRulePriority ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "provisionRuleName=SiteRule priority=0 ";
        // final String method = "cloneRule ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "provisionRuleName=SiteRule newProvisionRuleName=SiteRuleCloned ";
        // final String method = "updateRuleRelations ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "provisionRuleName=SiteRule profileNameList=NetworkProfile1 ";
        // final String method = "updateRule ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "siteNames=S3,S4 provisionRuleName=aaa ruleType=rule4site field1=description value1=auto2 ";
        // final String method = "updateRuleRelations ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "provisionRuleName=aaa profileNameList=autoGrpProfile,autoSiteProfile ";
        // final String method = "deleteRule ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "provisionRuleName=aaa ";
        // final String method = "getRuleSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "provisionRuleName=Group~Rule keyword=ruleStatus ";
        final String command = "http://localhost:8888/PlcmRmWeb/JRuleManager RuleManager "
                + method + auth + params1;
        final RuleHandler handler = new RuleHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    /**
     * The Endpoint model Map key--Model value--DBKey
     */
    @SuppressWarnings("serial")
    public Map<String, String> dataMap = new HashMap<String, String>() {
                                           {
                                               put("in", "memberOf");
                                               put("equals", "==");
                                               put("contains", "contains");
                                               put("AND", "&&");
                                               put("OR", "||");
                                           }
                                       };
    RuleManagerHandler         rmh;

    public RuleHandler(final String cmd) throws IOException {
        super(cmd);
        rmh = new RuleManagerHandler(webServiceUrl);
    }

    /**
     * Active the rule
     *
     * @see provisionRuleName=$ruleName
     *
     * @param provisionRuleName
     *            The rule name
     * @return The result
     */
    public String activeRule() {
        String status = "Failed";
        String msg = "";
        final String provisionRuleName = inputCmd.get("provisionRuleName");
        final JRule rule = getRuleByName(provisionRuleName);
        if (rule == null) {
            msg = "Could not find the rule with name " + provisionRuleName;
            logger.info(msg);
            return status + ", " + msg;
        }
        final JWebResult result = rmh.activeRule(userToken, rule.getRuleUUID());
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("The rule " + provisionRuleName
                        + " is successfully activated on XMA.");
        } else {
            msg = "The rule " + provisionRuleName + " is failed to activate.";
            logger.error(msg);
            status = "Failed" + ", " + msg;
        }
        return status;
    }

    private String addNode(final JNode node, final String cursor) {
        final String nodeType = inputCmd.get("nodeType" + cursor);
        final String whetherOrNot = inputCmd.get("whetherOrNot" + cursor);
        if (nodeType == null) {
            return "";
        } else if (nodeType.toUpperCase().matches("AND|OR")) {
            final String nextLeftCursor = cursor + "1";
            final String nextLeftNodeType = inputCmd.get("nodeType"
                    + nextLeftCursor);
            String ruleStringLeft = "";
            String ruleStringRight = "";
            if (nextLeftNodeType != null) {
                final JNode leftNode = new JNode();
                node.setLeftNode(leftNode);
                ruleStringLeft = addNode(leftNode, nextLeftCursor);
            }
            final String nextRightCursor = cursor + "2";
            final String nextRightNodeType = inputCmd.get("nodeType"
                    + nextRightCursor);
            if (nextRightNodeType != null) {
                final JNode rightNode = new JNode();
                node.setRightNode(rightNode);
                ruleStringRight = addNode(rightNode, nextRightCursor);
            }
            node.setNodeType(JNodeType.fromValue(nodeType.toUpperCase()));
            String result = "(" + ruleStringLeft + ") "
                    + dataMap.get(nodeType.toUpperCase()) + " ("
                    + ruleStringRight + ")";
            if ("true".equalsIgnoreCase(whetherOrNot)) {
                node.setWhetherNot(true);
                result = "(!(" + result + "))";
            }
            return result;
        } else {// it's leaf node
            final String nodeAttribute = inputCmd.get("nodeAttribute" + cursor);
            final String nodeOperater = dataMap.get(inputCmd.get("nodeOperater"
                    + cursor));
            final String nodeValue = inputCmd.get("nodeValue" + cursor)
                    .replaceAll("~", " ");
            node.setNodeType(JNodeType.ELEMENT);
            node.setLeftAttribute(nodeAttribute);
            node.setOperator(nodeOperater);
            node.setLeftDimension(nodeType);
            String rightDimension = "";
            String rightDimensionRuleStr = "";
            if ("memberOf".equals(nodeOperater)) {
                for (final String subValue : nodeValue.split(",")) {
                    final String ruleInData = dataMap.get(subValue) == null
                            ? subValue : dataMap.get(subValue);
                    node.getRuleInData().add(ruleInData);
                }
                rightDimension = getRandomKey();
                rightDimensionRuleStr = "(inDataMap[\"" + rightDimension
                        + "\"])";
            } else if ("userGroup".equals(nodeType)) {
                rightDimension = dataMap.get(nodeValue) == null ? nodeValue
                        : dataMap.get(nodeValue);
                rightDimensionRuleStr = rightDimension;
            } else {
                rightDimension = dataMap.get(nodeValue) == null ? nodeValue
                        : dataMap.get(nodeValue);
                rightDimensionRuleStr = "\"" + rightDimension + "\"";
            }
            node.setRightDimension(rightDimension);
            String result = "(this[\"dimension." + nodeType + "_"
                    + nodeAttribute + "\"] " + nodeOperater + " "
                    + rightDimensionRuleStr + ")";
            if ("true".equalsIgnoreCase(whetherOrNot)) {
                node.setWhetherNot(true);
                result = "(!(" + result + "))";
            }
            return result;
        }
    }

    /**
     * Add rule
     *
     * @see deviceTypes=CMA-Desktop,HDX,GroupSeries,ITP,RP-Desktop,RP-Mobile,VVX <br/>
     *      profileNameList=autoGrpProfile,autoSiteProfile <br/>
     *      description=auto <br/>
     *      provisionRuleName=deviceRule <br/>
     *      ruleType=rule4device
     *
     * @param deviceTypes
     *            The device types
     * @param siteNames
     *            The site name list
     * @param ruleType
     *            rule4device or rule4site
     * @param profileNameList
     *            The profile name list
     * @param description
     *            The description
     * @param provisionRuleName
     *            The rule name
     * @return The result
     */
    public String addRule() {
        String status = "Failed";
        final String profileNameList = inputCmd.get("profileNameList");
        final List<JRuleRelation> relations = generateRuleRelations(profileNameList,
                                                                    null);
        if (relations.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the specified profile in the XMA. "
                    + "Please provide valide profile name. "
                    + "Otherwise the provisioning rule will not be able to create.");
            return status
                    + " Cannot find the specified profile in the XMA. "
                    + "Please provide valide profile name. "
                    + "Otherwise the provisioning rule will not be able to create.";
        }
        final JRule newRule = new JRule();
        final String description = inputCmd.get("description");
        final String provisionRuleName = inputCmd.get("provisionRuleName")
                .replaceAll("~", " ");
        newRule.setDescription(description);
        if (inputCmd.get("isActive").equalsIgnoreCase("false")) {
            newRule.setActive(false);
        } else {
            newRule.setActive(true);
        }
        newRule.setAreaUUID(-1);
        newRule.setEndRule(false);
        newRule.setName(provisionRuleName);
        newRule.setRuleStatus(JRuleStatus.READY);
        cacheData();
        final JNode n = new JNode();
        final String ruleString = addNode(n, "");
        newRule.setRuleString(ruleString);
        newRule.setConditionTreeRoot(n);
        if (!rmh.isLegalRuleName(userToken, newRule.getName())) {
            status = "Failed";
            logger.error("The provision rule name specified is invalid. "
                    + "Please provide a valid name.");
            return status + " The provision rule name specified is invalid. "
            + "Please provide a valid name.";
        }
        final JWebResult result = rmh.addRuleAndRelations(userToken,
                                                          relations,
                                                          newRule);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Rule " + provisionRuleName + " added successfully.");
        } else {
            status = "Failed";
            logger.error("Rule " + provisionRuleName
                         + " was failed to add into XMA.");
            return status + " Rule " + provisionRuleName
                    + " was failed to add into XMA.";
        }
        return status;
    }

    private void cacheData() {
        final DeviceMetaManagerHandler dmmh = new DeviceMetaManagerHandler(
                                                                           webServiceUrl);
        final List<com.polycom.webservices.DeviceMetaManager.JCommonUIObject> deviceTypes = dmmh
                .getAllDeviceTypeForRule(userToken);
        for (final com.polycom.webservices.DeviceMetaManager.JCommonUIObject uiObj : deviceTypes) {
            dataMap.put(uiObj.getDisplayString(), uiObj.getKeyValue());
        }
        final List<com.polycom.webservices.UserManager.JCommonUIObject> userGroups = umh
                .getUserGroupsForRule(userToken);
        for (final com.polycom.webservices.UserManager.JCommonUIObject uiObj : userGroups) {
            dataMap.put(uiObj.getDisplayString(), uiObj.getKeyValue());
        }
        final SiteTopoManagerHandler stmh = new SiteTopoManagerHandler(
                webServiceUrl);
        final List<com.polycom.webservices.SiteTopoManager.JCommonUIObject> siteList = stmh
                .getListOfSiteForRule(userToken);
        for (final com.polycom.webservices.SiteTopoManager.JCommonUIObject uiObj : siteList) {
            dataMap.put(uiObj.getDisplayString(), uiObj.getKeyValue());
        }
    }

    /**
     * Clone the rule
     *
     * @see provisionRuleName=$ruleName <br/>
     *      newProvisionRuleName=clonedRule
     *
     * @param provisionRuleName
     *            The rule name
     * @param newProvisionRuleName
     *            The new rule name
     * @return The result
     */
    public String cloneRule() {
        String status = "Failed";
        String msg = "";
        final String provisionRuleName = inputCmd.get("provisionRuleName");
        final String newProvisionRuleName = inputCmd
                .get("newProvisionRuleName");
        final JRule rule = getRuleByName(provisionRuleName);
        if (rule == null) {
            msg = "Could not find the rule with name " + provisionRuleName;
            logger.info(msg);
            return status + ", " + msg;
        }
        rule.setName(newProvisionRuleName);
        rule.setPriority(rmh.getAllRules(userToken).size() + "");
        final List<JRuleRelation> relations = rmh
                .getAllRuleRelationsByruleUUID(userToken, rule.getRuleUUID());
        rule.setRuleUUID(null);
        final JWebResult result = rmh.addRuleAndRelations(userToken,
                                                          relations,
                                                          rule);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info(provisionRuleName + " is successfully cloned on XMA.");
        } else {
            msg = provisionRuleName + " is failed to clone."
                    + result.getStatus();
            logger.error(msg);
            status = "Failed" + ", " + msg;
        }
        return status;
    }

    /**
     * Deactive the rule
     *
     * @see provisionRuleName=$ruleName
     *
     * @param provisionRuleName
     *            The rule name
     * @return The result
     */
    public String deactiveRule() {
        String status = "Failed";
        String msg = "";
        final String provisionRuleName = inputCmd.get("provisionRuleName");
        final JRule rule = getRuleByName(provisionRuleName);
        if (rule == null) {
            msg = "Could not find the rule with name " + provisionRuleName;
            logger.info(msg);
            return status + ", " + msg;
        }
        final JWebResult result = rmh.deactiveRule(userToken,
                                                   rule.getRuleUUID());
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("The rule " + provisionRuleName
                        + " is successfully deactivated on XMA.");
        } else {
            msg = "The rule " + provisionRuleName + " is failed to deactivate.";
            logger.error(msg);
            status = "Failed" + ", " + msg;
        }
        return status;
    }

    /**
     * Delete the rule
     *
     * @see provisionRuleName=AutoGenRule
     *
     * @param provisionRuleName
     *            The rule name
     * @return The result
     */
    public String deleteRule() {
        String status = "Failed";
        String msg = "";
        final String provisionRuleName = inputCmd.get("provisionRuleName");
        final JRule rule = getRuleByName(provisionRuleName);
        if (rule == null) {
            msg = "Could not find the rule with name " + provisionRuleName;
            logger.info(msg);
            return status + ", " + msg;
        }
        final JWebResult result = rmh.deleteRule(userToken, rule.getRuleUUID());
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("The rule " + provisionRuleName
                        + " is successfully deleted on XMA.");
        } else {
            msg = "The rule " + provisionRuleName + " is failed to delete.";
            logger.error(msg);
            status = "Failed" + ", " + msg;
        }
        return status;
    }

    /**
     * Internal method, generate the rule relations
     *
     * @param profileList
     *            The profile list
     * @param ruleId
     *            The rule id
     * @return The list of JRuleRelation
     */
    private List<JRuleRelation> generateRuleRelations(final String profileList,
                                                      final String ruleId) {
        final List<JRuleRelation> relations = new ArrayList<JRuleRelation>();
        final ProfileManagerHandler pmh = new ProfileManagerHandler(
                                                                    webServiceUrl);
        final String[] profileNameList = profileList.replaceAll("~", " ")
                .split(",");
        final List<JProfile> profiles = pmh.getAllEndpointProfile(userToken);
        for (final String profileName : profileNameList) {
            for (final JProfile profile : profiles) {
                if (profileName.equals(profile.getProfileName())) {
                    final JRuleRelation ruleRelation = new JRuleRelation();
                    ruleRelation
                    .setRelationType(JRuleRelationType.RULE_AND_PROFILE);
                    ruleRelation.setRelationUUID(String.valueOf(profile
                                                                .getProfileId()));
                    ruleRelation.setRuleUUID(ruleId);
                    relations.add(ruleRelation);
                }
            }
        }
        return relations;
    }

    /**
     * Internal method, get the random key
     *
     * @return The random key
     */
    private String getRandomKey() {
        final Random r = new Random();
        return "BC245328-0814-9235-6A05-336243D9B" + r.nextInt(999);
    }

    /**
     * Internal method, get the rule by name
     *
     * @param provisionRuleName
     *            The rule name
     * @return JRule
     */
    private JRule getRuleByName(final String provisionRuleName) {
        final List<JRule> ruleList = rmh.getAllRules(userToken);
        for (final JRule rule : ruleList) {
            if (rule.getName().equals(provisionRuleName)) {
                return rule;
            }
        }
        return null;
    }

    /**
     * Get the rule specified attribute value
     *
     * @see provisionRuleName=$ruleName <br/>
     *      keyword=ruleUUID
     *
     * @param provisionRuleName
     *            The rule name
     * @param keyword
     *            The attribute name
     * @return The rule specified attribute value
     */
    public String getRuleSpecific() {
        final String result = "NotFound";
        final List<JRule> ruleList = rmh.getAllRules(userToken);
        final String provisionRuleName = inputCmd.get("provisionRuleName")
                .replaceAll("~", " ");
        final String keyword = inputCmd.get("keyword");
        for (final JRule rule : ruleList) {
            if (provisionRuleName.equals(rule.getName())) {
                return CommonUtils.invokeGetMethod(rule, keyword);
            }
        }
        return result;
    }

    @Override
    protected void injectCmdArgs() {
        // Provisioning rule name
        put("provisionRuleName", "");
        put("profileNameList", "");
        put("description", "");
        put("ruleType", "");
        put("siteNames", "");
        put("deviceTypes", "");
        put("isActive", "");
        put("usernames", "");
        put("groupName", "");
        put("priority", "0");
        put("newProvisionRuleName", "0");
    }

    /**
     * Update the rule
     *
     * @see provisionRuleName=$ruleName <br/>
     *      field1=description <br/>
     *      value1=auto
     *
     * @param provisionRuleName
     *            The rule name
     * @param field
     *            [1-10] The attribute name
     * @param value
     *            [1-10] The attribute value to update
     * @return The result
     */
    public String updateRule() {
        String status = "Failed";
        String msg = "";
        final String provisionRuleName = inputCmd.get("provisionRuleName");
        final JRule rule = getRuleByName(provisionRuleName);
        if (rule == null) {
            msg = "Could not find the rule with name " + provisionRuleName;
            logger.info(msg);
            return status + ", " + msg;
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
                CommonUtils.invokeSetMethod(rule, keyword, strValue);
            } catch (IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException
                    | InstantiationException e) {
                e.printStackTrace();
                return "Failed, " + e.getMessage();
            }
        }
        final JWebResult result = rmh.updateRule(userToken, rule);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("The rule " + provisionRuleName
                        + " is successfully updated on XMA.");
        } else {
            msg = "The rule " + provisionRuleName + " is failed to update.";
            logger.error(msg);
            status = "Failed" + ", " + msg;
        }
        return status;
    }

    /**
     * Update the rule priority
     *
     * @see provisionRuleName=$ruleName <br/>
     *      priority=0
     *
     * @param provisionRuleName
     *            The rule name
     * @param priority
     *            The priority
     * @return The result
     */
    public String updateRulePriority() {
        final List<JRule> rules = rmh.getAllRules(userToken);
        final String provisionRuleName = inputCmd.get("provisionRuleName")
                .replaceAll("~", " ");
        final int priority = Integer.parseInt(inputCmd.get("priority"));
        rules.sort(new Comparator<JRule>() {
            @Override
            public int compare(final JRule r1, final JRule r2) {
                final int key1 = Integer.parseInt(r1.getPriority());
                final int key2 = Integer.parseInt(r2.getPriority());
                return key1 > key2 ? 1 : key1 < key2 ? -1 : 0;
            }
        });
        for (int i = 0; i < rules.size(); i++) {
            final JRule rule = rules.get(i);
            if (rule.getName().equalsIgnoreCase(provisionRuleName)) {
                rules.remove(i);
                rules.add(priority, rule);
                break;
            }
        }
        for (int i = 0; i < rules.size(); i++) {
            rules.get(i).setPriority(i + "");
        }
        String msg = "";
        final JWebResult result = rmh.updateRulePriority(userToken, rules);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("The rule " + provisionRuleName
                        + " is successfully updated priority to " + priority);
            return "SUCCESS";
        } else {
            msg = "The rule " + provisionRuleName
                    + " is failed to update priority to " + priority;
            logger.error(msg);
            return "Failed" + ", " + msg;
        }
    }

    /**
     * Update the rule relations
     *
     * @see provisionRuleName=$ruleName <br/>
     *      profileNameList=autoGrpProfile,autoSiteProfile
     *
     * @param provisionRuleName
     *            The rule name
     * @return The result
     */
    public String updateRuleRelations() {
        String status = "Failed";
        String msg = "";
        final String provisionRuleName = inputCmd.get("provisionRuleName");
        final JRule rule = getRuleByName(provisionRuleName);
        if (rule == null) {
            msg = "Could not find the rule with name " + provisionRuleName;
            logger.info(msg);
            return status + ", " + msg;
        }
        final List<JRuleRelation> oldRelations = rmh
                .getAllRuleRelationsByruleUUID(userToken, rule.getRuleUUID());
        final String profileNameList = inputCmd.get("profileNameList");
        final List<JRuleRelation> newRelations = generateRuleRelations(profileNameList,
                                                                       rule.getRuleUUID());
        final JWebResult result = rmh.updateRuleRelations(userToken,
                                                          oldRelations,
                                                          newRelations);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("The rule relations of " + provisionRuleName
                        + " is successfully updated on XMA.");
        } else {
            msg = "The rule relations of " + provisionRuleName
                    + " is failed to update.";
            logger.error(msg);
            status = "Failed" + ", " + msg;
        }
        return status;
    }
}
