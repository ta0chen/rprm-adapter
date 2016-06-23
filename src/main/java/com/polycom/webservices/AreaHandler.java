package com.polycom.webservices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.AreaManagerHandler;
import com.polycom.webservices.AreaManager.JArea;
import com.polycom.webservices.AreaManager.JStatus;
import com.polycom.webservices.AreaManager.JWebResult;

/**
 * Area handler. This class will handle the webservice request of Area module
 *
 * @author Wenbo Chao
 *
 */
public class AreaHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "setAreasConfiguration ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params = "configure=true ";
        // final String method = "addArea ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params = "areaName=a1 field1=description value1=auto ";
        final String method = "deleteArea ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "areaName=a1 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JAreaManager AreaManager "
                + method + auth + params;
        final AreaHandler handler = new AreaHandler(command);
        final String result = handler.build();
        System.out.println("result==" + result);
    }

    AreaManagerHandler amh;

    public AreaHandler(final String cmd) throws IOException {
        super(cmd);
        amh = new AreaManagerHandler(webServiceUrl);
    }

    /**
     * Add area
     *
     * @see areaName=area1</br>
     *      field1=description </br>
     *      value1=auto
     * @param areaName
     *            The area name
     * @param field
     *            [1-10] The attribute name
     * @param value
     *            [1-10] The attribute value to update
     * @return The result
     */
    public String addArea() {
        final String areaName = inputCmd.get("areaName");
        final JArea area = new JArea();
        area.setGroupName(areaName);
        for (int i = 1; i <= 10; i++) {
            final String keyword = inputCmd.get("field" + i);
            final String strValue = inputCmd.get("value" + i);
            if (keyword.isEmpty()) {
                break;
            } else {
                try {
                    CommonUtils.invokeSetMethod(area, keyword, strValue);
                } catch (IllegalAccessException
                         | IllegalArgumentException
                         | InvocationTargetException
                         | InstantiationException e) {
                    e.printStackTrace();
                    return "Failed to set " + strValue + " to " + keyword
                            + " field in JArea Object!";
                }
            }
        }
        final JWebResult result = amh.addArea(userToken, area);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Add area successfully.");
            return "SUCCESS";
        } else {
            logger.error("Add area configuration failed.");
            return "Failed, add area failed.\n" + result.getMessages();
        }
    }

    /**
     * Set area configuration
     *
     * @see configure=true
     * @param singular
     *            The singular name(Optional)
     * @param plural
     *            The plural name(Optional)
     * @param configure
     *            Whether enable configuration
     * @return The result
     */
    public String deleteArea() {
        final String areaName = inputCmd.get("areaName");
        final JArea area = getAreaByName(areaName);
        if (area == null) {
            return "Failed, could not find the area named " + areaName;
        }
        final JWebResult result = amh.deleteArea(userToken, area);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Delete area successfully.");
            return "SUCCESS";
        } else {
            logger.error("Delete area configuration failed.");
            return "Failed, delete area" + areaName + " failed.\n"
                    + result.getMessages();
        }
    }

    /**
     * Internal method, get the area by name
     *
     * @param areaName
     *            The areaName to search
     * @return JArea
     */
    private JArea getAreaByName(final String areaName) {
        final List<JArea> areas = amh.getAreas(userToken);
        for (final JArea area : areas) {
            if (area.getGroupName().equals(areaName)) {
                return area;
            }
        }
        return null;
    }

    @Override
    protected void injectCmdArgs() {
        put("configure", "");
        put("singular", "Area");
        put("plural", "Areas");
        put("areaName", "");
    }

    /**
     * Set area configuration
     *
     * @see configure=true
     * @param singular
     *            The singular name(Optional)
     * @param plural
     *            The plural name(Optional)
     * @param configure
     *            Whether enable configuration
     * @return The result
     */
    public String setAreasConfiguration() {
        final String singular = inputCmd.get("singular");
        final String plural = inputCmd.get("plural");
        final boolean configure = Boolean
                .parseBoolean(inputCmd.get("configure"));
        final JWebResult result = amh
                .setAreasConfiguration(userToken, singular, plural, configure);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Set area configuration successfully.");
            return "SUCCESS";
        } else {
            logger.error("Set area configuration failed.");
            return "Failed, Set area configuration failed.\n"
                    + result.getMessages();
        }
    }
}
