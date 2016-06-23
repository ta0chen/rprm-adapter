package com.polycom.webservices.driver;

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
import com.polycom.webservices.NamingSchemeService.JCredentials;
import com.polycom.webservices.NamingSchemeService.JH323AvailableField;
import com.polycom.webservices.NamingSchemeService.JH323IDScheme;
import com.polycom.webservices.NamingSchemeService.JNamingSchemeService;
import com.polycom.webservices.NamingSchemeService.JNamingSchemeService_Service;
import com.polycom.webservices.NamingSchemeService.JSIPURIScheme;
import com.polycom.webservices.NamingSchemeService.JSchemeType;
import com.polycom.webservices.NamingSchemeService.JSystemAvailableField;
import com.polycom.webservices.NamingSchemeService.JSystemNameScheme;
import com.polycom.webservices.NamingSchemeService.JWebResult;

/**
 * Scheme handler
 *
 * @author Lingxi Zhao
 *
 */
public class NamingSchemeServiceHandler {
    protected static Logger      logger       = Logger
            .getLogger("NamingSchemeServiceHandler");
    private static final QName   SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JNamingSchemeService");
    private JNamingSchemeService port;

    /**
     * Construction of the NamingSchemeServiceHandler class
     */
    public NamingSchemeServiceHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JNamingSchemeService) jsonInvocationHandler
                    .getProxy(JNamingSchemeService.class);
        } else {
            final URL wsdlURL = NamingSchemeServiceHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JNamingSchemeService.wsdl");
            final JNamingSchemeService_Service ss = new JNamingSchemeService_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJNamingSchemeServicePort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JNamingSchemeService");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public JH323IDScheme getH323IDScheme(final String userToken,
                                         final int belongsToAreaUgpId) {
        System.out.println("Invoking getH323IDScheme...");
        final com.polycom.webservices.NamingSchemeService.JCredentials _getH323IDScheme_credentials = new JCredentials();
        _getH323IDScheme_credentials.setUserToken(userToken);
        final Integer _getH323IDScheme_belongsToAreaUgpId = new Integer(
                belongsToAreaUgpId);
        final Holder<JH323IDScheme> scheme = new Holder<JH323IDScheme>();
        port.getH323IDScheme(_getH323IDScheme_credentials,
                             _getH323IDScheme_belongsToAreaUgpId,
                             scheme);
        return scheme.value;
    }

    public JSIPURIScheme getSIPURIScheme(final String userToken,
                                         final int belongsToAreaUgpId) {
        System.out.println("Invoking getSIPURIScheme...");
        final com.polycom.webservices.NamingSchemeService.JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Integer _getSystemNameScheme_belongsToAreaUgpId = new Integer(
                belongsToAreaUgpId);
        final Holder<JSIPURIScheme> scheme = new Holder<JSIPURIScheme>();
        port.getSIPURIScheme(credentials,
                             _getSystemNameScheme_belongsToAreaUgpId,
                             scheme);
        return scheme.value;
    }

    public JSystemNameScheme getSystemNameScheme(final String userToken,
                                                 final int belongsToAreaUgpId) {
        System.out.println("Invoking getSystemNameScheme...");
        final com.polycom.webservices.NamingSchemeService.JCredentials _getSystemNameScheme_credentials = new JCredentials();
        _getSystemNameScheme_credentials.setUserToken(userToken);
        final Integer _getSystemNameScheme_belongsToAreaUgpId = new Integer(
                belongsToAreaUgpId);
        final Holder<JSystemNameScheme> scheme = new Holder<JSystemNameScheme>();
        port.getSystemNameScheme(_getSystemNameScheme_credentials,
                                 _getSystemNameScheme_belongsToAreaUgpId,
                                 scheme);
        return scheme.value;
    }

    /**
     * Update H323ID Scheme
     *
     * @param userToken
     * @param namingField1
     * @param separator1
     * @param namingField2
     * @param separator2
     * @param namingField3
     * @param separator3
     * @param namingField4
     * @return
     */
    public JWebResult UpdateH323IDScheme(final String userToken,
                                         final JH323AvailableField namingField1,
                                         final JH323AvailableField separator1,
                                         final JH323AvailableField namingField2,
                                         final JH323AvailableField separator2,
                                         final JH323AvailableField namingField3,
                                         final JH323AvailableField separator3,
                                         final JH323AvailableField namingField4,
                                         final String SystemNameSchemeGuid) {
        System.out.println("Invoking UpdateH323IDScheme...");
        final com.polycom.webservices.NamingSchemeService.JCredentials _UpdateH323IDScheme_credentials = new JCredentials();
        _UpdateH323IDScheme_credentials.setUserToken(userToken);
        final JH323IDScheme _UpdateH323IDScheme_scheme = new JH323IDScheme();
        _UpdateH323IDScheme_scheme.getFields().add(namingField1);
        _UpdateH323IDScheme_scheme.getFields().add(separator1);
        _UpdateH323IDScheme_scheme.getFields().add(namingField2);
        _UpdateH323IDScheme_scheme.getFields().add(separator2);
        _UpdateH323IDScheme_scheme.getFields().add(namingField3);
        _UpdateH323IDScheme_scheme.getFields().add(separator3);
        _UpdateH323IDScheme_scheme.getFields().add(namingField4);
        _UpdateH323IDScheme_scheme.setUseSystemNameAsHostName(false);
        _UpdateH323IDScheme_scheme.setGenerateH323Aliasflag(false);
        _UpdateH323IDScheme_scheme.setEnabledflag(false);
        _UpdateH323IDScheme_scheme.setProvisionflag(false);
        _UpdateH323IDScheme_scheme.setSchemetype(JSchemeType.H_323_ID);
        _UpdateH323IDScheme_scheme.setBelongsToAreaUgpId(-1);
        _UpdateH323IDScheme_scheme.setDomainName("");
        _UpdateH323IDScheme_scheme
                .setSystemNameSchemeGuid(SystemNameSchemeGuid);
        final JWebResult _UpdateH323IDScheme__return = port
                .updateH323IDScheme(_UpdateH323IDScheme_credentials,
                                    _UpdateH323IDScheme_scheme);
        System.out.println("UpdateH323IDScheme.result="
                + _UpdateH323IDScheme__return);
        return _UpdateH323IDScheme__return;
    }

    public JWebResult updateSIPURIScheme(final String userToken,
                                         final JSIPURIScheme scheme) {
        logger.info("Invoking updateSIPURIScheme...");
        System.out.println("Invoking updateSIPURIScheme...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.updateSIPURIScheme(credentials, scheme);
    }

    /**
     * Update System Name Scheme
     *
     * @param userToken
     * @param namingField1
     * @param separator1
     * @param namingField2
     * @param separator2
     * @param namingField3
     * @param separator3
     * @param namingField4
     * @param isUseSysNameForHostname
     * @param isUseSysNameForH323ID
     * @return
     */
    public JWebResult
            UpdateSystemNameScheme(final String userToken,
                                   final JSystemAvailableField namingField1,
                                   final JSystemAvailableField separator1,
                                   final JSystemAvailableField namingField2,
                                   final JSystemAvailableField separator2,
                                   final JSystemAvailableField namingField3,
                                   final JSystemAvailableField separator3,
                                   final JSystemAvailableField namingField4,
                                   final String SystemNameSchemeGuid,
                                   final boolean isUseSysNameForHostname,
                                   final boolean isUseSysNameForH323ID) {
        System.out.println("Invoking UpdateSystemNameScheme...");
        final com.polycom.webservices.NamingSchemeService.JCredentials _UpdateSystemNameScheme_credentials = new JCredentials();
        _UpdateSystemNameScheme_credentials.setUserToken(userToken);
        final JSystemNameScheme _UpdateSystemNameScheme_scheme = new JSystemNameScheme();
        _UpdateSystemNameScheme_scheme.getFields().add(namingField1);
        _UpdateSystemNameScheme_scheme.getFields().add(separator1);
        _UpdateSystemNameScheme_scheme.getFields().add(namingField2);
        _UpdateSystemNameScheme_scheme.getFields().add(separator2);
        _UpdateSystemNameScheme_scheme.getFields().add(namingField3);
        _UpdateSystemNameScheme_scheme.getFields().add(separator3);
        _UpdateSystemNameScheme_scheme.getFields().add(namingField4);
        _UpdateSystemNameScheme_scheme
                .setUseSystemNameAsHostName(isUseSysNameForHostname);
        _UpdateSystemNameScheme_scheme
                .setGenerateH323Aliasflag(isUseSysNameForH323ID);
        _UpdateSystemNameScheme_scheme.setEnabledflag(false);
        _UpdateSystemNameScheme_scheme.setProvisionflag(false);
        _UpdateSystemNameScheme_scheme.setSchemetype(JSchemeType.SYSTEM_NAME);
        _UpdateSystemNameScheme_scheme.setBelongsToAreaUgpId(-1);
        _UpdateSystemNameScheme_scheme.setDomainName("");
        _UpdateSystemNameScheme_scheme
                .setSystemNameSchemeGuid(SystemNameSchemeGuid);
        final JWebResult _UpdateSystemNameScheme__return = port
                .updateSystemNameScheme(_UpdateSystemNameScheme_credentials,
                                        _UpdateSystemNameScheme_scheme);
        System.out.println("UpdateSystemNameScheme.result="
                + _UpdateSystemNameScheme__return);
        return _UpdateSystemNameScheme__return;
    }
}