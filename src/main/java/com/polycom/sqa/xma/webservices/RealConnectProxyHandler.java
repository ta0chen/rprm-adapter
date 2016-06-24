package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.RealConnectProxyManagerHandler;
import com.polycom.webservices.RealConnectProxyManager.JRealConnectProxyEntry;
import com.polycom.webservices.RealConnectProxyManager.JRealConnectProxyFilter;
import com.polycom.webservices.RealConnectProxyManager.JRealConnectProxyRule;
import com.polycom.webservices.RealConnectProxyManager.JStatus;
import com.polycom.webservices.RealConnectProxyManager.JWebResult;

/**
 * Real Connect Proxy handler. This class will handle the webservice request of
 * Redundancy module
 *
 * @author wbchao
 *
 */
public class RealConnectProxyHandler extends XMAWebServiceHandler {
    public static void main(final String[] args) throws IOException {
        // final String method = "addProxyEntry ";
        // final String auth =
        // "username=admin domain=local password=UG9seWNvbTEyMw== ";
        // final String params =
        // "host=172.21.113.11 type=Exchange matchString1=css\\.com
        // matchType1=Lync protocol1=tel ";
        // final String method = "getProxyEntrySpecific ";
        // final String auth =
        // "username=admin domain=local password=UG9seWNvbTEyMw== ";
        // final String params = "host=172.21.113.11 keyword=fqdn ";
        final String method = "deleteProxyEntry ";
        final String auth = "username=admin domain=local password=UG9seWNvbTEyMw== ";
        final String params = "host=172.21.113.11 ";
        final String command = "http://10.220.202.245:80/PlcmRmWeb/JRealConnectProxyManager RealConnectProxyManager "
                + method + auth + params;
        final XMAWebServiceHandler handler = new RealConnectProxyHandler(
                command);
        handler.logger.info("cmd==" + command);
        final String result = handler.build();
        handler.logger.info("result for " + method + " is " + result);
    }

    private final RealConnectProxyManagerHandler rcpmh;

    public RealConnectProxyHandler(final String cmd) throws IOException {
        super(cmd);
        rcpmh = new RealConnectProxyManagerHandler(webServiceUrl);
    }

    /**
     * Add a calendar connector
     *
     * @see host=172.21.113.11<br/>
     *      type=Exchange <br/>
     *      matchString1=css\\.com<br/>
     *      matchType1=Lync<br/>
     *      protocol1=tel
     *
     * @param host
     *            The calendar server ip
     * @param type
     *            The calendar server type
     * @param matchString
     *            The rule dialString match regexpress
     * @param matchType
     *            The rule match type
     * @param protocal
     *            The rule match protocal
     * @param prefix
     *            The rule prefix
     * @param suffix
     *            The rule postfix
     * @return The result
     */
    public String addProxyEntry() {
        String status = "Failed";
        final String host = inputCmd.get("host");
        // final String type = inputCmd.get("type");
        final JRealConnectProxyEntry entry = new JRealConnectProxyEntry();
        entry.setHost(host);
        entry.setFqdn("127.0.0.1");
        for (int i = 1; i <= 10; i++) {
            final String matchString = inputCmd.get("matchString" + i);
            if (matchString == null || matchString.isEmpty()) {
                break;
            }
            final String matchType = inputCmd.get("matchType" + i);
            final String protocal = inputCmd.get("protocol" + i);
            final String prefix = inputCmd.get("prefix" + i);
            final String postfix = inputCmd.get("suffix" + i);
            final JRealConnectProxyRule rule = new JRealConnectProxyRule();
            rule.setMatch(matchString);
            rule.setMatchType(matchType);
            rule.setProtocol(protocal);
            rule.setPostfix(postfix);
            rule.setPrefix(prefix);
            entry.getRules().add(rule);
        }
        final List<JRealConnectProxyFilter> filters = entry.getFilters();
        final String filterString = inputCmd.get("filterString");
        if (!filterString.isEmpty()) {
            final JRealConnectProxyFilter filter = new JRealConnectProxyFilter();
            filter.setFilterType("FreeBusy");
            filter.setMatch(filterString);
            filters.add(filter);
        }
        final JWebResult result = rcpmh.addProxyEntry(userToken, entry);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Add calendar connector " + host + " successfully");
            return "SUCCESS";
        } else {
            status = "Failed";
            final String errorMsg = "Add calendar connector " + host
                    + " failed";
            logger.error(errorMsg);
            return status + errorMsg;
        }
    }

    /**
     * Delete the calendar connector
     *
     * @see host=172.21.113.11
     *
     * @param host
     *            The host ip to delete
     * @return The result
     */
    public String deleteProxyEntry() {
        String status = "Failed";
        final String host = inputCmd.get("host");
        final JRealConnectProxyEntry entry = getRealConnectProxyEntryByIp(host);
        if (entry == null) {
            return "Failed, could not find the RealConnectProxyEntry with host ip "
                    + host;
        }
        final JWebResult result = rcpmh.deleteProxyEntryByFQDN(userToken,
                                                               entry.getFqdn());
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Delete calendar connector " + host + " successfully");
            return "SUCCESS";
        } else {
            status = "Failed";
            final String errorMsg = "Delete calendar connector " + host
                    + " failed";
            logger.error(errorMsg);
            return status + errorMsg;
        }
    }

    /**
     * Get the specified attribute value with the specified host ip
     *
     * @see host=172.21.113.11 </br>
     *      keyword = fqdn
     *
     * @param host
     *            The proxy entry ip to search
     * @param keyword
     *            The attribute name
     * @return The specified attribute value with the specified host ip
     */
    public String getProxyEntrySpecific() {
        final String keyword = inputCmd.get("keyword");
        final String host = inputCmd.get("host");
        final JRealConnectProxyEntry entry = getRealConnectProxyEntryByIp(host);
        return CommonUtils.invokeGetMethod(entry, keyword);
    }

    /**
     * Internal Method, get Real Connect Proxy Entry By Ip
     *
     * @param ip
     *            The Calendar server ip
     * @return The JRealConnectProxyEntry
     */
    private JRealConnectProxyEntry
            getRealConnectProxyEntryByIp(final String ip) {
        final List<JRealConnectProxyEntry> proxyEntries = rcpmh
                .getProxyEntries(userToken);
        for (final JRealConnectProxyEntry entry : proxyEntries) {
            if (entry.getHost().equals(ip)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    protected void injectCmdArgs() {
        put("host", "");
        put("type", "");
        put("filterString", "");
    }
}
