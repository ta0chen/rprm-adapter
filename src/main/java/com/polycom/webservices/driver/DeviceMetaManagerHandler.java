package com.polycom.webservices.driver;

import java.net.URL;
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
import com.polycom.webservices.DeviceMetaManager.JCommonUIObject;
import com.polycom.webservices.DeviceMetaManager.JCredentials;
import com.polycom.webservices.DeviceMetaManager.JDeviceMetaManager;
import com.polycom.webservices.DeviceMetaManager.JDeviceMetaManager_Service;
import com.polycom.webservices.DeviceMetaManager.JDeviceTypeVO;
import com.polycom.webservices.DeviceMetaManager.JProvisionFieldGroupMeta;
import com.polycom.webservices.DeviceMetaManager.JWebResult;

/**
 * Device Meta Manager Handler
 *
 * @author Tao Chen
 *
 */
public class DeviceMetaManagerHandler {
    protected static Logger          logger       = Logger
            .getLogger("DeviceMetaManagerHandler");
    private static final QName       SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JDeviceMetaManager");
    private final JDeviceMetaManager port;

    /**
     * Construction of DeviceMetaManagerHandler class
     */
    public DeviceMetaManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JDeviceMetaManager) jsonInvocationHandler
                    .getProxy(JDeviceMetaManager.class);
        } else {
            final URL wsdlURL = DeviceMetaManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JDeviceMetaManager.wsdl");
            final JDeviceMetaManager_Service ss = new JDeviceMetaManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJDeviceMetaManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JDeviceMetaManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public List<JCommonUIObject>
            getAllDeviceTypeForRule(final String userToken) {
        System.out.println("Invoking getAllDeviceTypeForRule...");
        logger.info("Invoking getAllDeviceTypeForRule...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JCommonUIObject>> deviceTypes = new Holder<List<JCommonUIObject>>();
        port.getAllDeviceTypeForRule(credentials, deviceTypes);
        return deviceTypes.value;
    }

    /**
     * Get all phone provision file groups for specified audio phones
     *
     * @param userToken
     * @return
     */
    public List<JProvisionFieldGroupMeta>
            getAllPhoneProvisionFieldGroups(final String userToken) {
        logger.info("Invoking getAllPhoneProvisionFieldGroups...");
        final JCredentials _getAllPhoneProvisionFieldGroups_credentials = new JCredentials();
        _getAllPhoneProvisionFieldGroups_credentials.setUserToken(userToken);
        final String _getAllPhoneProvisionFieldGroups_locale = "en_US";
        final Holder<List<JProvisionFieldGroupMeta>> _getAllPhoneProvisionFieldGroups_groups = new Holder<List<JProvisionFieldGroupMeta>>();
        final JWebResult _getAllPhoneProvisionFieldGroups__return = port
                .getAllPhoneProvisionFieldGroups(_getAllPhoneProvisionFieldGroups_credentials,
                                                 _getAllPhoneProvisionFieldGroups_locale,
                                                 _getAllPhoneProvisionFieldGroups_groups);
        System.out.println("getAllPhoneProvisionFieldGroups.result="
                + _getAllPhoneProvisionFieldGroups__return);
        System.out
                .println("getAllPhoneProvisionFieldGroups._getAllPhoneProvisionFieldGroups_groups="
                        + _getAllPhoneProvisionFieldGroups_groups.value);
        return _getAllPhoneProvisionFieldGroups_groups.value;
    }

    /**
     * Get the device type by model
     *
     * @param userToken
     * @param deviceModel
     * @return
     */
    public JDeviceTypeVO getDeviceTypeByDeviceModel(final String userToken,
                                                    final String deviceModel) {
        System.out.println("Invoking getDeviceTypeByDeviceModel...");
        logger.info("Invoking getDeviceTypeByDeviceModel...");
        final JCredentials _getDeviceTypeByDeviceModel_credentials = new JCredentials();
        _getDeviceTypeByDeviceModel_credentials.setUserToken(userToken);
        final String _getDeviceTypeByDeviceModel_deviceModel = deviceModel;
        final Holder<JDeviceTypeVO> _getDeviceTypeByDeviceModel_deviceType = new Holder<JDeviceTypeVO>();
        final JWebResult _getDeviceTypeByDeviceModel__return = port
                .getDeviceTypeByDeviceModel(_getDeviceTypeByDeviceModel_credentials,
                                            _getDeviceTypeByDeviceModel_deviceModel,
                                            _getDeviceTypeByDeviceModel_deviceType);
        System.out.println("getDeviceTypeByDeviceModel.result="
                + _getDeviceTypeByDeviceModel__return);
        System.out
                .println("getDeviceTypeByDeviceModel._getDeviceTypeByDeviceModel_deviceType="
                        + _getDeviceTypeByDeviceModel_deviceType.value);
        return _getDeviceTypeByDeviceModel_deviceType.value;
    }
}
