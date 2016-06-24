package com.polycom.sqa.xma.webservices.driver;

import java.net.URL;

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
import com.polycom.webservices.NumberingSchemeService.JCredentials;
import com.polycom.webservices.NumberingSchemeService.JE164RulesList;
import com.polycom.webservices.NumberingSchemeService.JNumberingSchemeService;
import com.polycom.webservices.NumberingSchemeService.JNumberingSchemeService_Service;
import com.polycom.webservices.NumberingSchemeService.JWebResult;

/**
 * Numbering Scheme Service Handler
 *
 * @author Tao Chen
 *
 */
public class NumberingSchemeServiceHandler {
    protected static Logger    logger         = Logger
            .getLogger("NumberingSchemeServiceHandler");
    private static final QName SERVICE_NAME   = new QName(
            "http://polycom.com/WebServices", "JNumberingSchemeService");
    JNumberingSchemeService    port;
    private JE164RulesList     jE164RulesList = new JE164RulesList();

    /**
     * Construction of NumberingSchemeServiceHandler class
     */
    public NumberingSchemeServiceHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JNumberingSchemeService) jsonInvocationHandler
                    .getProxy(JNumberingSchemeService.class);
        } else {
            final URL wsdlURL = NumberingSchemeServiceHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JNumberingSchemeService.wsdl");
            final JNumberingSchemeService_Service ss = new JNumberingSchemeService_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJNumberingSchemeServicePort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JNumberingSchemeService");
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
     * Find the current E164 scheme rule on XMA
     *
     * @param userToken
     * @return
     */
    public JWebResult findE164NumberingScheme(final String userToken) {
        System.out.println("Invoking findE164NumberingScheme...");
        logger.info("Invoking findE164NumberingScheme...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JE164RulesList> numberingScheme = new Holder<JE164RulesList>();
        final JWebResult result = port
                .findE164NumberingScheme(credentials, -1, numberingScheme);
        System.out.println("findE164NumberingScheme.result=" + result);
        System.out
                .println("findE164NumberingScheme._findE164NumberingScheme_numberingScheme="
                        + numberingScheme.value);
        setE164RulesList(numberingScheme.value);
        return result;
    }

    /**
     * Get the default E164 scheme rule on XMA
     *
     * @param userToken
     * @return
     */
    public JE164RulesList
            getDefaultDeviceTypeRuleinE164Scheme(final String userToken) {
        System.out.println("Invoking getDefaultDeviceTypeRuleinE164Scheme...");
        logger.info("Invoking getDefaultDeviceTypeRuleinE164Scheme...");
        final JCredentials _getDefaultDeviceTypeRuleinE164Scheme_credentials = new JCredentials();
        _getDefaultDeviceTypeRuleinE164Scheme_credentials
                .setUserToken(userToken);
        final Holder<JE164RulesList> _getDefaultDeviceTypeRuleinE164Scheme_numberingScheme = new Holder<JE164RulesList>();
        final JWebResult _getDefaultDeviceTypeRuleinE164Scheme__return = port
                .getDefaultDeviceTypeRuleinE164Scheme(_getDefaultDeviceTypeRuleinE164Scheme_credentials,
                                                      _getDefaultDeviceTypeRuleinE164Scheme_numberingScheme);
        System.out.println("getDefaultDeviceTypeRuleinE164Scheme.result="
                + _getDefaultDeviceTypeRuleinE164Scheme__return);
        System.out
                .println("getDefaultDeviceTypeRuleinE164Scheme._getDefaultDeviceTypeRuleinE164Scheme_numberingScheme="
                        + _getDefaultDeviceTypeRuleinE164Scheme_numberingScheme.value);
        setE164RulesList(_getDefaultDeviceTypeRuleinE164Scheme_numberingScheme.value);
        return _getDefaultDeviceTypeRuleinE164Scheme_numberingScheme.value;
    }

    /**
     * Return the JE164RulesList
     *
     * @return
     */
    public JE164RulesList getE164RulesList() {
        return jE164RulesList;
    }

    /**
     * Modify the E164 numbering scheme for VC2 endpoints
     *
     * @param userToken
     * @param numberingScheme
     * @return
     */
    public JWebResult
            modifyE164NumberingScheme(final String userToken,
                                      final JE164RulesList numberingScheme) {
        System.out.println("Invoking modifyE164NumberingScheme...");
        logger.info("Invoking modifyE164NumberingScheme...");
        final JCredentials credential = new JCredentials();
        credential.setUserToken(userToken);
        final JWebResult result = port
                .modifyE164NumberingScheme(credential, -1, numberingScheme);
        System.out.println("modifyE164NumberingScheme.result=" + result);
        return result;
    }

    /**
     * Set the JE164RulesList
     *
     * @param jE164RulesList
     */
    public void setE164RulesList(final JE164RulesList jE164RulesList) {
        this.jE164RulesList = jE164RulesList;
    }
}
