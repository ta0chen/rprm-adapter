package com.polycom.webservices.driver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.log4j.Logger;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.interceptor.JsonInvocationHandler;
import com.polycom.sqa.xma.webservices.driver.interceptor.SoapHeaderOutInterceptor;
import com.polycom.webservices.RuleManager.JCredentials;
import com.polycom.webservices.RuleManager.JRule;
import com.polycom.webservices.RuleManager.JRuleManager;
import com.polycom.webservices.RuleManager.JRuleManager_Service;
import com.polycom.webservices.RuleManager.JRuleRelation;
import com.polycom.webservices.RuleManager.JWebResult;

/**
 * Rule Manager Handler
 *
 * @author Tao Chen
 *
 */
public class RuleManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("RuleManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JRuleManager");
    JRuleManager               port;

    /**
     * Construction of RuleManagerHandler class
     */
    public RuleManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JRuleManager) jsonInvocationHandler
                    .getProxy(JRuleManager.class);
        } else {
            final URL wsdlURL = RuleManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JRuleManager.wsdl");
            final JRuleManager_Service ss = new JRuleManager_Service(wsdlURL,
                    SERVICE_NAME);
            port = ss.getJRuleManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext().put(
                                             BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                             webServiceUrl + "/JRuleManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    /**
     * Activate the specified rule
     *
     * @param userToken
     * @param ruleUUID
     * @return
     */
    public JWebResult activeRule(final String userToken,
                                 final String ruleUUID) {
        System.out.println("Invoking activeRule...");
        logger.info("Invoking activeRule...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.activeRule(credentials, ruleUUID);
    }

    /**
     * Create provisioning rule
     *
     * @param userToken
     * @param newRelations
     * @param rule
     * @return
     */
    public JWebResult addRuleAndRelations(final String userToken,
                                          final List<JRuleRelation> newRelations,
                                          final JRule rule) {
        System.out.println("Invoking addRuleAndRelations...");
        final JCredentials _addRuleAndRelations_credentials = new JCredentials();
        _addRuleAndRelations_credentials.setUserToken(userToken);
        final JRule _addRuleAndRelations_rule = rule;
        final List<JRuleRelation> _addRuleAndRelations_oldRelations = new ArrayList<JRuleRelation>();
        final List<JRuleRelation> _addRuleAndRelations_newRelations = newRelations;
        final Holder<JRule> _addRuleAndRelations_resultRule = new Holder<JRule>();
        final JWebResult _addRuleAndRelations__return = port
                .addRuleAndRelations(_addRuleAndRelations_credentials,
                                     _addRuleAndRelations_rule,
                                     _addRuleAndRelations_oldRelations,
                                     _addRuleAndRelations_newRelations,
                                     _addRuleAndRelations_resultRule);
        System.out.println("addRuleAndRelations.result="
                + _addRuleAndRelations__return);
        System.out
                .println("addRuleAndRelations._addRuleAndRelations_resultRule="
                        + _addRuleAndRelations_resultRule.value);
        return _addRuleAndRelations__return;
    }

    /**
     * Deactivate the specified rule
     *
     * @param userToken
     * @param ruleUUID
     * @return
     */
    public JWebResult deactiveRule(final String userToken,
                                   final String ruleUUID) {
        System.out.println("Invoking deactiveRule...");
        logger.info("Invoking deactiveRule...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.deactiveRule(credentials, ruleUUID);
    }

    /**
     * Delete the specified rule
     *
     * @param userToken
     * @param ruleUUID
     * @return
     */
    public JWebResult deleteRule(final String userToken,
                                 final String ruleUUID) {
        System.out.println("Invoking deleteRule...");
        logger.info("Invoking deleteRule...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.deleteRule(credentials, ruleUUID);
    }

    /**
     * get All Rule Relations
     *
     * @param userToken
     * @param newRule
     * @return
     */
    public List<JRuleRelation> getAllRuleRelations(final String userToken) {
        System.out.println("Invoking getAllRuleRelations...");
        logger.info("Invoking getAllRuleRelations...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JRuleRelation>> ruleRelations = new Holder<List<JRuleRelation>>();
        port.getAllRuleRelations(credentials, ruleRelations);
        return ruleRelations.value;
    }

    /**
     * get All Rule Relations By ruleUUID
     *
     * @param userToken
     * @param newRule
     * @return
     */
    public List<JRuleRelation>
            getAllRuleRelationsByruleUUID(final String userToken,
                                          final String ruleUUID) {
        System.out.println("Invoking getAllRuleRelationsByruleUUID...");
        logger.info("Invoking getAllRuleRelationsByruleUUID...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JRuleRelation>> ruleRelations = new Holder<List<JRuleRelation>>();
        port.getAllRuleRelationsByruleUUID(credentials,
                                           ruleUUID,
                                           ruleRelations);
        return ruleRelations.value;
    }

    /**
     * Get all provision rules as a list
     *
     * @param userToken
     * @return
     */
    public List<JRule> getAllRules(final String userToken) {
        System.out.println("Invoking getAllRules...");
        logger.info("Invoking getAllRules...");
        final JCredentials _getAllRules_credentials = new JCredentials();
        _getAllRules_credentials.setUserToken(userToken);
        final Holder<List<JRule>> _getAllRules_rules = new Holder<List<JRule>>();
        final JWebResult _getAllRules__return = port
                .getAllRules(_getAllRules_credentials, _getAllRules_rules);
        System.out.println("getAllRules.result=" + _getAllRules__return);
        System.out.println("getAllRules._getAllRules_rules="
                + _getAllRules_rules.value);
        return _getAllRules_rules.value;
    }

    /**
     * Check the candidate rule is legal for add
     *
     * @param userToken
     * @param ruleName
     * @return
     */
    public boolean isLegalRuleName(final String userToken,
                                   final String ruleName) {
        System.out.println("Invoking isLegalRuleName...");
        logger.info("Invoking isLegalRuleName...");
        final JCredentials _isLegalRuleName_credentials = new JCredentials();
        _isLegalRuleName_credentials.setUserToken(userToken);
        final String _isLegalRuleName_ruleName = ruleName;
        final Holder<Boolean> _isLegalRuleName_isLegal = new Holder<Boolean>();
        final JWebResult _isLegalRuleName__return = port
                .isLegalRuleName(_isLegalRuleName_credentials,
                                 _isLegalRuleName_ruleName,
                                 _isLegalRuleName_isLegal);
        System.out
                .println("isLegalRuleName.result=" + _isLegalRuleName__return);
        System.out.println("isLegalRuleName._isLegalRuleName_isLegal="
                + _isLegalRuleName_isLegal.value);
        return _isLegalRuleName_isLegal.value;
    }

    /**
     * Update rule
     *
     * @param userToken
     * @param newRule
     * @return
     */
    public JWebResult updateRule(final String userToken, final JRule newRule) {
        System.out.println("Invoking updateRule...");
        logger.info("Invoking updateRule...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.updateRule(credentials, newRule);
    }

    /**
     * Update rule priority
     *
     * @param userToken
     * @param newRule
     * @return
     */
    public JWebResult updateRulePriority(final String userToken,
                                         final List<JRule> newRules) {
        System.out.println("Invoking updateRulePriority...");
        logger.info("Invoking updateRulePriority...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.updateRulePriority(credentials, newRules);
    }

    /**
     * Update Rule Relations
     *
     * @param userToken
     * @param rule
     *            id
     * @return
     */
    public JWebResult
            updateRuleRelations(final String userToken,
                                final List<JRuleRelation> oldRelations,
                                final List<JRuleRelation> newRelations) {
        System.out.println("Invoking updateRuleRelations...");
        logger.info("Invoking updateRuleRelations...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.updateRuleRelations(credentials,
                                        oldRelations,
                                        newRelations);
    }
}
