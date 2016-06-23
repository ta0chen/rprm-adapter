package com.polycom.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.polycom.sqa.xma.webservices.driver.WhitelistManagerHandler;
import com.polycom.webservices.WhitelistManager.JStatus;
import com.polycom.webservices.WhitelistManager.JWebResult;

public class WhitelistHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "updateWhiteList ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        String params = "keyword=whiteList ";
        params = "status=false ipList=1.1.1.1,172.21.98.16 ";
        final String command = "https://10.220.202.6:8443/PlcmRmWeb/rest/JWhiteListManager WhitelistManagerHandler "
                + method + auth + params;
        final WhitelistHandler handler = new WhitelistHandler(command);
        final String result = handler.build();
        handler.logger.info("The result is: " + result);
    }
    private final WhitelistManagerHandler whiteListManagerHandler;

    public WhitelistHandler(final String cmd) throws IOException {
        super(cmd);
        whiteListManagerHandler = new WhitelistManagerHandler(webServiceUrl);
    }

    /**
     * Retrieve specific information from the white list
     *
     * @see keyword=clientIpAddress<br/>
     * @param keyword
     *            The specific information need to retrieve.
     * @return The return value.
     */
    public String getWhiteListSpecificInfo(){
        String rtn = "NotFound";
        final JWebResult result = whiteListManagerHandler.getWhitelist(userToken);

        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Successfully retrieve the white list information on the RPRM.");
        } else {
            logger.error("Failed to retrieve the white list information on the RPRM.");
        }

        final String keyword =inputCmd.get("keyword");
        if(keyword.equalsIgnoreCase("status")){
            rtn = whiteListManagerHandler.getWhiteListEnableStatus().toString();
        } else if(keyword.equalsIgnoreCase("clientIpAddress")){
            rtn = whiteListManagerHandler.getClientIpAddress();
        } else if (keyword.equalsIgnoreCase("whiteList")){
            rtn = whiteListManagerHandler.getWhiteList().toString();
        }
        return rtn;
    }

    @Override
    protected void injectCmdArgs() {
        inputCmd.put("status", "false");
        inputCmd.put("ipList", "");
        inputCmd.put("keyword","");
    }

    /**
     * Update the white list
     *
     * @see status=true<br/>
     *      ipList=1.1.1.1,2.2.2.2<br/>
     * @param status
     *            The status of the white list. True or false.
     * @param ipList
     *            The IP address list for the white list.
     *
     * @return The status of the update.
     */
    public String updateWhiteList() {
        String status = "Failed";
        Boolean whiteListStatus = false;
        final List<String> whitelist = new ArrayList<String>();
        if (inputCmd.get("status").equalsIgnoreCase("true")) {
            whiteListStatus = true;
        }

        if (!inputCmd.get("ipList").isEmpty()) {
            final String[] ipList = inputCmd.get("ipList").split(",");
            for (int i = 0; i < ipList.length; i++) {
                whitelist.add(ipList[i]);
            }
        }

        final JWebResult result = whiteListManagerHandler
                .saveWhitelist(userToken, whiteListStatus, whitelist);

        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfuly update the white list on the RPRM with status as "
                    + inputCmd.get("status"));
        } else {
            logger.error("Failed to update the white list on the RPRM with status as "
                    + inputCmd.get("status"));
            return status
                    + " Failed to update the white list on the RPRM with status as "
                    + inputCmd.get("status");
        }

        return status;
    }

}
