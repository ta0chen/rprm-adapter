package com.polycom.sqa.xma.webservices.driver;

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
import com.polycom.webservices.ProvisioningManager.JCredentials;
import com.polycom.webservices.ProvisioningManager.JPolicy;
import com.polycom.webservices.ProvisioningManager.JPolicyAttribute;
import com.polycom.webservices.ProvisioningManager.JProvisioningManager;
import com.polycom.webservices.ProvisioningManager.JProvisioningManager_Service;
import com.polycom.webservices.ProvisioningManager.JScheduledProfile;
import com.polycom.webservices.ProvisioningManager.JUIUtcDateTime;
import com.polycom.webservices.ProvisioningManager.JWebResult;

/**
 * Provisioning Manager Handler
 *
 * @author Tao Chen
 *
 */
public class ProvisioningManagerHandler {
    protected static Logger            logger       = Logger
            .getLogger("ProvisioningManagerHandler");
    private static final QName         SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JProvisioningManager");
    private final JProvisioningManager port;

    /**
     * Construction of DeviceManagerHandler class
     */
    public ProvisioningManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JProvisioningManager) jsonInvocationHandler
                    .getProxy(JProvisioningManager.class);
        } else {
            final URL wsdlURL = ProvisioningManagerHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JProvisioningManager.wsdl");
            final JProvisioningManager_Service ss = new JProvisioningManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJProvisioningManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JProvisioningManager");
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
     * Add provisioning profile
     *
     * @param userToken
     * @param profile
     * @param profileAttrs
     * @return
     */
    public JWebResult addProfile(final String userToken,
                                 final JPolicy profile,
                                 final List<JPolicyAttribute> profileAttrs) {
        System.out.println("Invoking addProfile...");
        logger.info("Invoking addProfile...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port.addProfile(credentials,
                                                  profile,
                                                  profileAttrs);
        return result;
    }

    /**
     * Clear endpoints provisioning status
     *
     * @param userToken
     * @param deviceIdList
     * @return
     */
    public JWebResult
            clearProvisioningStatus(final String userToken,
                                    final List<Integer> deviceIdList) {
        logger.info("Invoking clearProvisioningStatus...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final List<Integer> deviceIds = deviceIdList;
        final JWebResult result = port.clearProvisioningStatus(credentials,
                                                               deviceIds);
        System.out.println("clearProvisioningStatus.result=" + result);
        return result;
    }

    /**
     * Clone the profile
     *
     * @param userToken
     * @param profileIdToClone
     * @param clonedProfileName
     * @return
     */
    public JWebResult cloneProfile(final String userToken,
                                   final int profileIdToClone,
                                   final String clonedProfileName) {
        System.out.println("Invoking cloneProfile...");
        logger.info("Invoking cloneProfile...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.cloneProfile(credentials,
                                 profileIdToClone,
                                 clonedProfileName);
    }

    /**
     * Delete the profile
     *
     * @param userToken
     * @param profileId
     * @return
     */
    public JWebResult deleteProfile(final String userToken,
                                    final int profileId) {
        System.out.println("Invoking deleteProfiles...");
        logger.info("Invoking deleteProfiles...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final List<Integer> profileIds = new ArrayList<Integer>();
        profileIds.add(new Integer(profileId));
        return port.deleteProfiles(credentials, profileIds);
    }

    /**
     * Edit the profile
     *
     * @param userToken
     * @param profileAttrs
     * @return
     */
    public JWebResult editProfile(final String userToken,
                                  final List<JPolicyAttribute> profileAttrs) {
        System.out.println("Invoking updateProfileAttributes...");
        logger.info("Invoking updateProfileAttributes...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port.updateProfileAttributes(credentials,
                                                               profileAttrs);
        return result;
    }

    /**
     * Get the profile detial attributes
     *
     * @param userToken
     * @param profileId
     * @return
     */
    public List<JPolicyAttribute> getProfileAttributes(final String userToken,
                                                       final int profileId) {
        System.out.println("Invoking getProfileAttributes...");
        logger.info("Invoking getProfileAttributes...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JPolicyAttribute>> profileAttrs = new Holder<List<JPolicyAttribute>>();
        port.getProfileAttributes(credentials, profileId, profileAttrs);
        return profileAttrs.value;
    }

    /**
     * Get the scheduled provisioning profile
     *
     * @param userToken
     * @return
     */
    public List<JScheduledProfile>
            getScheduledProfiles(final String userToken) {
        System.out.println("Invoking getScheduledProfiles...");
        logger.info("Invoking getScheduledProfiles...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JScheduledProfile>> holder = new Holder<List<JScheduledProfile>>();
        port.getScheduledProfiles(credentials, holder);
        return holder.value;
    }

    /**
     * Trigger schedule provision
     *
     * @param userToken
     * @param deviceIds
     * @param profileId
     * @param isDeviceTime
     * @param isNow
     * @param utcDate
     * @return
     */
    public JWebResult scheduleProvision(final String userToken,
                                        final List<Integer> deviceIds,
                                        final int profileId,
                                        final boolean isDeviceTime,
                                        final boolean isNow,
                                        final JUIUtcDateTime utcDate) {
        System.out.println("Invoking addProfile...");
        logger.info("Invoking addProfile...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port
                .scheduleProvision(credentials,
                                   deviceIds,
                                   profileId,
                                   isDeviceTime,
                                   isNow,
                                   utcDate);
        return result;
    }
}
