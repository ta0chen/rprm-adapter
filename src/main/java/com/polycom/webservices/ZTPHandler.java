package com.polycom.webservices;

import java.io.IOException;

import com.polycom.sqa.xma.webservices.driver.ZTPManagerHandler;
import com.polycom.webservices.ZTPManager.JStatus;
import com.polycom.webservices.ZTPManager.JWebResult;

/**
 * ZTP handler. This class will handle the webservice request of ZTP module
 *
 * @author wbchao
 *
 */
public class ZTPHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "setZTPConfig ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "autoGenerate=false defaultLanguage=German ";
        final String command = "http://localhost:8888/PlcmRmWeb/JZtpManager ZtpManager "
                + method + auth + params;
        final ZTPHandler handler = new ZTPHandler(command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    private final ZTPManagerHandler ztpManagerHandler;

    public ZTPHandler(final String cmd) throws IOException {
        super(cmd);
        ztpManagerHandler = new ZTPManagerHandler(webServiceUrl);
    }

    @Override
    protected void injectCmdArgs() {
        put("autoGenerate", "0");
        put("defaultLanguage", "");
    }

    /**
     * Clear the alert
     *
     * @see autoGenerate=true<br/>
     *      defaultLanguage = English
     *
     * @param autoGenerate
     *            Whether auto generate
     * @param defaultLanguage
     *            The default Language
     *
     * @return The result
     */
    public String setZTPConfig() {
        final boolean autoGenerate = Boolean
                .parseBoolean(inputCmd.get("autoGenerate"));
        final String defaultLanguage = inputCmd.get("defaultLanguage");
        final JWebResult result = ztpManagerHandler
                .setZTPConfig(userToken, autoGenerate, defaultLanguage);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Set ZTP config successfully.");
            return "SUCCESS";
        } else {
            logger.error("Set ZTP config failed.");
            return "Failed, " + result.getMessages();
        }
    }
}
