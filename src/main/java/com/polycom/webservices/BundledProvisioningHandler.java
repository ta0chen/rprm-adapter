package com.polycom.webservices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.BundledProvisioningManagerHandler;
import com.polycom.webservices.BundledProvisioningManager.JEndpointForList;
import com.polycom.webservices.BundledProvisioningManager.JProvisioningBundleAttr;
import com.polycom.webservices.BundledProvisioningManager.JStatus;
import com.polycom.webservices.BundledProvisioningManager.JWebResult;

/**
 * Bundled Provisioning handler. This class will handle the webservice request
 * of Bundled Provisioning module
 *
 * @author wbchao
 *
 */
public class BundledProvisioningHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "downloadBundle ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "deviceId=1073 bundleName=bundle1 description=auto ";
        // final String method = "getBundleSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "bundleName=testBundle keyword=associatedDeviceIds[size] ";
        // final String method = "getAvailableDevicesByBundleSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "bundleName=testBundle keyword=associatedDeviceIds[size] ";
        final String method = "updateBundle ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "bundleName=testBundle field1=associatedDeviceIds value1=update{}";
        // final String method = "deleteBundle ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = "bundleName=bundle1 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JBundledProvisioningManager BundledProvisioningManager "
                + method + auth + params;
        final BundledProvisioningHandler handler = new BundledProvisioningHandler(
                command);
        final String result = handler.build();
        handler.logger.info("result==" + result);
    }

    private final BundledProvisioningManagerHandler bundledProvisioningManagerHandler;

    public BundledProvisioningHandler(final String cmd) throws IOException {
        super(cmd);
        bundledProvisioningManagerHandler = new BundledProvisioningManagerHandler(
                webServiceUrl);
    }

    /**
     * Delete the Bundle
     *
     * @see bundleName = bundle1,bundle2 <br/>
     *      field1=description <br/>
     *      value1=auto
     *
     * @param bundleName
     *            The bundle name
     * @return The result
     */
    public String deleteBundle() {
        String status = "Failed";
        final String bundleName = inputCmd.get("bundleName").replaceAll("~",
                " ");
        final List<JProvisioningBundleAttr> bundlesToBeDeleted = new ArrayList<JProvisioningBundleAttr>();
        final List<JProvisioningBundleAttr> allBundles = bundledProvisioningManagerHandler
                .listBundles(userToken);
        for (final String name : bundleName.split(",")) {
            for (final JProvisioningBundleAttr bundle : allBundles) {
                if (bundle.getName().equals(name)) {
                    bundlesToBeDeleted.add(bundle);
                }
            }
        }
        final JWebResult result = bundledProvisioningManagerHandler
                .swapAndDeleteBundles(userToken, bundlesToBeDeleted, -1);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Delete bundle " + bundleName + " successfully.");
        } else {
            status = "Failed";
            logger.error("Delete bundle " + bundleName
                         + " is not successfully.");
            return status + " Delete bundle " + bundleName
                    + " is not successfully. errorMsg is "
                    + result.getMessages();
        }
        return status;
    }

    /**
     * Download the Bundle
     *
     * @see deviceId=$deviceId<br/>
     *      bundleName = bundle1<br/>
     *      description = auto
     *
     * @param deviceId
     *            The device uid
     * @param bundleName
     *            The bundle name
     * @param description
     *            The description
     * @return The result
     */
    public String downloadBundle() {
        String status = "Failed";
        final Integer deviceId = Integer.parseInt(inputCmd.get("deviceId"));
        final String bundleName = inputCmd.get("bundleName");
        final String description = inputCmd.get("description");
        final JWebResult result = bundledProvisioningManagerHandler
                .downloadBundle(userToken, deviceId, bundleName, description);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Download bundle " + bundleName + " successfully.");
        } else {
            status = "Failed";
            logger.error("Download bundle " + bundleName
                    + " is not successfully.");
            return status + " Download bundle " + bundleName
                    + " is not successfully. errorMsg is "
                    + result.getMessages();
        }
        return status;
    }

    /**
     * Get the Bundle specified attribute value
     *
     * @see bundleName=$bundleName <br/>
     *      keyword=modelName
     *
     * @param bundleName
     *            The bundle name
     * @param keyword
     *            The attribute name
     * @return The bundle specified attribute value
     */
    public String getAvailableDevicesByBundleSpecific() {
        final String bundleName = inputCmd.get("bundleName").replaceAll("~",
                                                                        " ");
        final String keyword = inputCmd.get("keyword");
        final JProvisioningBundleAttr bundle = getBundleByName(bundleName);
        if (bundle == null) {
            return "Failed, could not find the bundle with name " + bundleName;
        }
        final List<JEndpointForList> availableDevices = bundledProvisioningManagerHandler
                .getAvailableDevicesByBundle(userToken, bundle.getBundleId());
        return CommonUtils.invokeGetMethod(availableDevices, keyword);
    }

    /**
     * Internal Method, get the bundle instance
     *
     * @param bundleName
     * @return JProvisioningBundleAttr
     */
    private JProvisioningBundleAttr getBundleByName(final String bundleName) {
        final List<JProvisioningBundleAttr> bundles = bundledProvisioningManagerHandler
                .listBundles(userToken);
        for (final JProvisioningBundleAttr bundle : bundles) {
            if (bundle.getName().equals(bundleName)) {
                return bundle;
            }
        }
        return null;
    }

    /**
     * Get the Bundle specified attribute value
     *
     * @see bundleName=$bundleName <br/>
     *      keyword=modelName
     *
     * @param bundleName
     *            The bundle name
     * @param keyword
     *            The attribute name
     * @return The bundle specified attribute value
     */
    public String getBundleSpecific() {
        final String bundleName = inputCmd.get("bundleName").replaceAll("~",
                                                                        " ");
        final String keyword = inputCmd.get("keyword");
        final JProvisioningBundleAttr bundle = getBundleByName(bundleName);
        return CommonUtils.invokeGetMethod(bundle, keyword);
    }

    /**
     * Inject the args from tcl
     */
    @Override
    protected void injectCmdArgs() {
        put("deviceId", "");
        put("description", "");
        put("bundleName", "");
    }

    /**
     * Update the Bundle
     *
     * @see bundleName = bundle1
     *
     * @param bundleName
     *            The bundle name
     * @param field
     *            [1-10] The attribute name
     * @param value
     *            [1-10] The attribute value to update
     * @return The result
     */
    public String updateBundle() {
        String status = "Failed";
        final String bundleName = inputCmd.get("bundleName").replaceAll("~",
                " ");
        final JProvisioningBundleAttr bundle = getBundleByName(bundleName);
        if (bundle == null) {
            return "Failed, could not find the bundle named " + bundleName;
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
                CommonUtils.invokeSetMethod(bundle, keyword, strValue);
            } catch (IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException
                    | InstantiationException e) {
                e.printStackTrace();
                return "Failed, " + e.getMessage();
            }
        }
        final JWebResult result = bundledProvisioningManagerHandler
                .updateBundle(userToken, bundle);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Update bundle " + bundleName + " successfully.");
        } else {
            status = "Failed";
            logger.error("Update bundle " + bundleName
                         + " is not successfully.");
            return status + " Update bundle " + bundleName
                    + " is not successfully. errorMsg is "
                    + result.getMessages();
        }
        return status;
    }
}