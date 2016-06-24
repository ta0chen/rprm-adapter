package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.SystemAlertManagerHandler;
import com.polycom.webservices.SystemAlertManager.JAlertLevel;
import com.polycom.webservices.SystemAlertManager.JStatus;
import com.polycom.webservices.SystemAlertManager.JSystemAlert;
import com.polycom.webservices.SystemAlertManager.JSystemAlertThresholdConfig;
import com.polycom.webservices.SystemAlertManager.JWebResult;

/**
 * System Alert handler. This class will handle the webservice request of System
 * Alert module
 *
 * @author wbchao
 *
 */
public class SystemAlertHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "getSystemAlertSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params = "startTime=1422354581000 keyword=parms ";
        final String method = "clearAllSystemAlerts ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = " ";
        final String command = "http://localhost:8888/PlcmRmWeb/JSystemAlertManager SystemAlertManager "
                + method + auth + params;
        final SystemAlertHandler handler = new SystemAlertHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    private final SystemAlertManagerHandler systemAlertManagerHandler;

    public SystemAlertHandler(final String cmd) throws IOException {
        super(cmd);
        systemAlertManagerHandler = new SystemAlertManagerHandler(
                webServiceUrl);
    }

    /**
     * Clear all alerts
     *
     * @see No param
     *
     * @return The result
     */
    public String clearAllSystemAlerts() {
        String status = "SUCCESS";
        final Map<JSystemAlert, JAlertLevel> alerts = systemAlertManagerHandler
                .getList(userToken);
        for (final JSystemAlert a : alerts.keySet()) {
            final JWebResult result = systemAlertManagerHandler
                    .clear(userToken, a.getId());
            if (!result.getStatus().equals(JStatus.SUCCESS)) {
                status = "Failed";
                logger.error("Delete system alert failed. Id is " + a.getId());
            }
        }
        if (status.equals("SUCCESS")) {
            logger.info("Clear all system alerts successfully.");
        }
        return status;
    }

    /**
     * Clear the alert
     *
     * @see id=$systemAlertId
     *
     * @param id
     *            The system alert id
     *
     * @return The result
     */
    public String clearSystemAlert() {
        String status = "Failed";
        final int id = Integer.parseInt(inputCmd.get("id"));
        final JWebResult result = systemAlertManagerHandler.clear(userToken,
                                                                  id);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Delete system alert successfully.");
        } else {
            status = "Failed";
            logger.error("Delete system alert failed.");
            return status + "Delete system alert failed.";
        }
        return status;
    }

    /**
     * Get the system alert specified attribute value
     *
     * @see startTime=$alertStartTimeInMillSec <br/>
     *      keyword=parms
     * @param startTime
     *            The start search point
     * @param keyword
     *            The attribute name
     * @param endTime
     *            The end search point(Optional)
     * @return The system alert specified attribute value
     */
    public String getSystemAlertSpecific() {
        final String result = "NotFound";
        final String keyword = inputCmd.get("keyword");
        final long startTime = Long.parseLong(inputCmd.get("startTime"));
        final long endTime = Long.parseLong(inputCmd.get("endTime"));
        final Map<JSystemAlert, JAlertLevel> alerts = systemAlertManagerHandler
                .getList(userToken);
        for (final JSystemAlert alert : alerts.keySet()) {
            final long updateTime = alert.getCreateDate().getUnixTime();
            if (updateTime > startTime && updateTime < endTime) {
                if (keyword.endsWith("level")) {
                    return alerts.get(alert).toString();
                }
                return CommonUtils.invokeGetMethod(alert, keyword);
            }
        }
        return result;
    }

    @Override
    protected void injectCmdArgs() {
        put("startTime", "0");
        put("endTime", Long.MAX_VALUE + "");
        put("id", "");
    }

    /**
     * Save System Alert Threshold Config
     *
     * @see field1=diskspaceAlertThreshold<br/>
     *      value1=90
     *
     * @param field
     *            [1-10] The attribute name
     * @param value
     *            [1-10] The attribute value to update
     * @return The result
     */
    public String setAlertThresholdConfiguration() {
        final JSystemAlertThresholdConfig thresholdConfig = systemAlertManagerHandler
                .getAlertThresholdConfiguration(userToken);
        for (int i = 1; i <= 10; i++) {
            final String keyword = inputCmd.get("field" + i);
            final String strValue = inputCmd.get("value" + i).replaceAll("~",
                                                                         " ");
            if (keyword.isEmpty()) {
                continue;
            }
            try {
                CommonUtils.invokeSetMethod(thresholdConfig, keyword, strValue);
            } catch (IllegalAccessException
                     | IllegalArgumentException
                     | InvocationTargetException
                     | InstantiationException e) {
                e.printStackTrace();
                return "Failed, " + e.getMessage();
            }
        }
        final JWebResult result = systemAlertManagerHandler
                .setAlertThresholdConfiguration(userToken, thresholdConfig);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Delete system alert successfully.");
            return "SUCCESS";
        } else {
            logger.error("Delete system alert failed.");
            return "Failed. Delete system alert failed.";
        }
    }
}
