package com.polycom.sqa.xma.webservices.driver;

import java.net.URL;
import java.util.LinkedList;
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
import com.polycom.webservices.SiteTopoManager.JCommonUIObject;
import com.polycom.webservices.SiteTopoManager.JCredentials;
import com.polycom.webservices.SiteTopoManager.JDeviceAlarm;
import com.polycom.webservices.SiteTopoManager.JMediaPathExclusion;
import com.polycom.webservices.SiteTopoManager.JNamePlusID;
import com.polycom.webservices.SiteTopoManager.JNamePlusUid;
import com.polycom.webservices.SiteTopoManager.JNetwork;
import com.polycom.webservices.SiteTopoManager.JPolicyAttribute;
import com.polycom.webservices.SiteTopoManager.JQosStatistics;
import com.polycom.webservices.SiteTopoManager.JSite;
import com.polycom.webservices.SiteTopoManager.JSiteDevicesInfo;
import com.polycom.webservices.SiteTopoManager.JSiteLink;
import com.polycom.webservices.SiteTopoManager.JSiteTopoManager;
import com.polycom.webservices.SiteTopoManager.JSiteTopoManager_Service;
import com.polycom.webservices.SiteTopoManager.JSiteType;
import com.polycom.webservices.SiteTopoManager.JSiteWithoutArea;
import com.polycom.webservices.SiteTopoManager.JTerritory;
import com.polycom.webservices.SiteTopoManager.JTopoDataRequest;
import com.polycom.webservices.SiteTopoManager.JTopoDataResponse;
import com.polycom.webservices.SiteTopoManager.JWebResult;

/**
 * Site Topology Manager Handler
 *
 * @author Tao Chen
 *
 */
public class SiteTopoManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("SiteTopoManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JSiteTopoManager");
    JSiteTopoManager           port;
    private List<JTerritory>   territories;

    /**
     * Construction of SiteTopoManagerHandler class
     */
    public SiteTopoManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JSiteTopoManager) jsonInvocationHandler
                    .getProxy(JSiteTopoManager.class);
        } else {
            final URL wsdlURL = SiteTopoManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JSiteTopoManager.wsdl");
            final JSiteTopoManager_Service ss = new JSiteTopoManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJSiteTopoManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JSiteTopoManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    public JWebResult addCloud(final String userToken,
                               final JSiteWithoutArea cloud,
                               final List<JSiteLink> siteLinks) {
        System.out.println("Invoking addCloud...");
        logger.info("Invoking addCloud...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<String> newUID = new Holder<String>();
        return port.addCloud(credentials, cloud, siteLinks, newUID);
    }

    public JWebResult
            addExclusions(final String userToken,
                          final List<JMediaPathExclusion> exclusions) {
        System.out.println("Invoking addExclusions...");
        logger.info("Invoking addExclusions...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.addExclusions(credentials, exclusions);
    }

    /**
     * Add a new site
     *
     * @param userToken
     * @param desc
     * @param siteName
     * @param territoryUid
     * @param ipaddr
     * @param maskSize
     * @return
     */
    public JWebResult addSite(final String userToken,
                              final JSite addSite,
                              final List<JNetwork> networkList) {
        System.out.println("Invoking addSite...");
        logger.info("Invoking addSite...");
        final JCredentials _addSite_credentials = new JCredentials();
        _addSite_credentials.setUserToken(userToken);
        final List<JNetwork> _addSite_networkList = networkList;
        final Holder<String> _addSite_newUID = new Holder<String>();
        final JWebResult _addSite__return = port
                .addSite(_addSite_credentials,
                         addSite,
                         _addSite_networkList,
                         _addSite_newUID);
        System.out.println("addSite.result=" + _addSite__return);
        System.out.println("addSite._addSite_newUID=" + _addSite_newUID.value);
        return _addSite__return;
    }

    public JWebResult addSiteLink(final String userToken,
                                  final JSiteLink siteLink) {
        System.out.println("Invoking addSiteLink...");
        logger.info("Invoking addSiteLink...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<String> newUID = new Holder<String>();
        return port.addSiteLink(credentials, siteLink, newUID);
    }

    public JWebResult addTerritory(final String userToken,
                                   final JTerritory dmaTerritory,
                                   final List<String> siteIdsInTerritory) {
        System.out.println("Invoking addTerritory...");
        logger.info("Invoking addTerritory...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<String> newUID = new Holder<String>();
        return port.addTerritory(credentials,
                                 dmaTerritory,
                                 siteIdsInTerritory,
                                 newUID);
    }

    public JWebResult deleteExclusions(final String userToken,
                                       final List<String> exclusions) {
        System.out.println("Invoking deleteExclusions...");
        logger.info("Invoking deleteExclusions...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.deleteExclusions(credentials, exclusions);
    }

    public JWebResult deleteSiteLink(final String userToken,
                                     final String siteLinkUid) {
        System.out.println("Invoking deleteSiteLink...");
        logger.info("Invoking deleteSiteLink...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.deleteSiteLink(credentials, siteLinkUid);
    }

    /**
     * Delete specified site
     *
     * @param userToken
     * @param siteUid
     * @return
     */
    public JWebResult deleteSites(final String userToken,
                                  final List<String> siteUid) {
        System.out.println("Invoking deleteSites...");
        logger.info("Invoking deleteSites...");
        final JCredentials _deleteSites_credentials = new JCredentials();
        _deleteSites_credentials.setUserToken(userToken);
        final List<String> _deleteSites_siteUidsToDelete = siteUid;
        final Holder<List<String>> _deleteSites_siteUidsNotDeleted = new Holder<List<String>>();
        final JWebResult _deleteSites__return = port
                .deleteSites(_deleteSites_credentials,
                             _deleteSites_siteUidsToDelete,
                             _deleteSites_siteUidsNotDeleted);
        System.out.println("deleteSites.result=" + _deleteSites__return);
        System.out.println("deleteSites._deleteSites_siteUidsNotDeleted="
                + _deleteSites_siteUidsNotDeleted.value);
        return _deleteSites__return;
    }

    public JWebResult deleteTerritory(final String userToken,
                                      final String territoryUid) {
        System.out.println("Invoking deleteTerritory...");
        logger.info("Invoking deleteTerritory...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.deleteTerritory(credentials, territoryUid);
    }

    public List<JDeviceAlarm> getDeviceAlarmsForSite(final String userToken,
                                                     final String siteUid) {
        System.out.println("Invoking getDeviceAlarmsForSite...");
        logger.info("Invoking getDeviceAlarmsForSite...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JDeviceAlarm>> deviceAlarms = new Holder<List<JDeviceAlarm>>();
        port.getDeviceAlarmsForSite(credentials, siteUid, deviceAlarms);
        return deviceAlarms.value;
    }

    public JSiteDevicesInfo getDevicesInfoForSite(final String userToken,
                                                  final String siteUid) {
        System.out.println("Invoking getDevicesInfoForSite...");
        logger.info("Invoking getDevicesInfoForSite...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JSiteDevicesInfo> siteDevicesInfo = new Holder<JSiteDevicesInfo>();
        port.getDevicesInfoForSite(credentials, siteUid, siteDevicesInfo);
        return siteDevicesInfo.value;
    }

    public List<JSite> getListOfSite(final String userToken) {
        System.out.println("Invoking getListOfSite...");
        logger.info("Invoking getListOfSite...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JSite>> siteList = new Holder<List<JSite>>();
        final Holder<List<JNamePlusUid>> namePlusUidOfTerritories = new Holder<List<JNamePlusUid>>();
        port.getListOfSite(credentials,
                           null,
                           null,
                           siteList,
                           namePlusUidOfTerritories);
        return siteList.value;
    }

    public List<JCommonUIObject> getListOfSiteForRule(final String userToken) {
        System.out.println("Invoking getListOfSiteForRule...");
        logger.info("Invoking getListOfSiteForRule...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JCommonUIObject>> siteList = new Holder<List<JCommonUIObject>>();
        port.getListOfSiteForRule(credentials, siteList);
        return siteList.value;
    }

    public List<JQosStatistics>
            getListOfSiteLinkMonitor(final String userToken,
                                     final String territoryUid) {
        System.out.println("Invoking getListOfSiteLinkMonitor...");
        logger.info("Invoking getListOfSiteLinkMonitor...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JQosStatistics>> qosStatisticsList = new Holder<List<JQosStatistics>>();
        port.getListOfSiteLinkMonitor(credentials,
                                      territoryUid,
                                      qosStatisticsList);
        return qosStatisticsList.value;
    }

    public List<JQosStatistics>
            getListOfSiteMonitor(final String userToken,
                                 final String territoryUid) {
        System.out.println("Invoking getListOfSiteMonitor...");
        logger.info("Invoking getListOfSiteMonitor...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JQosStatistics>> qosStatisticsList = new Holder<List<JQosStatistics>>();
        port.getListOfSiteMonitor(credentials, territoryUid, qosStatisticsList);
        return qosStatisticsList.value;
    }

    public List<JNamePlusUid> getListOfSiteNameUid(final String userToken,
                                                   final String territoryUid) {
        System.out.println("Invoking getListOfSiteNameUid...");
        logger.info("Invoking getListOfSiteNameUid...");
        final JCredentials _getListOfSiteNameUid_credentials = new JCredentials();
        _getListOfSiteNameUid_credentials.setUserToken(userToken);
        final List<JSiteType> _getListOfSiteNameUid_siteTypes = new LinkedList<JSiteType>();
        _getListOfSiteNameUid_siteTypes.add(JSiteType.PRIVATE);
        final String _getListOfSiteNameUid_territoryUid = territoryUid;
        final Holder<List<JNamePlusUid>> _getListOfSiteNameUid_namePlusUidOfSites = new Holder<List<JNamePlusUid>>();
        final JWebResult _getListOfSiteNameUid__return = port
                .getListOfSiteNameUid(_getListOfSiteNameUid_credentials,
                                      _getListOfSiteNameUid_siteTypes,
                                      _getListOfSiteNameUid_territoryUid,
                                      _getListOfSiteNameUid_namePlusUidOfSites);
        System.out.println("getListOfSiteNameUid.result="
                + _getListOfSiteNameUid__return);
        System.out
                .println("getListOfSiteNameUid._getListOfSiteNameUid_namePlusUidOfSites="
                        + _getListOfSiteNameUid_namePlusUidOfSites.value);
        return _getListOfSiteNameUid_namePlusUidOfSites.value;
    }

    /**
     * Get the XMA default territory UID
     *
     * @param userToken
     * @return
     */
    public String getListOfTerritory(final String userToken) {
        System.out.println("Invoking getListOfTerritory...");
        logger.info("Invoking getListOfTerritory...");
        final JCredentials _getListOfTerritory_credentials = new JCredentials();
        _getListOfTerritory_credentials.setUserToken(userToken);
        final Holder<List<JTerritory>> _getListOfTerritory_dmaTerritories = new Holder<List<JTerritory>>();
        final Holder<List<JNamePlusUid>> _getListOfTerritory_namePlusUidOfDmaNodes = new Holder<List<JNamePlusUid>>();
        final Holder<String> _getListOfTerritory_cmaTerritoryUid = new Holder<String>();
        final JWebResult _getListOfTerritory__return = port
                .getListOfTerritory(_getListOfTerritory_credentials,
                                    _getListOfTerritory_dmaTerritories,
                                    _getListOfTerritory_namePlusUidOfDmaNodes,
                                    _getListOfTerritory_cmaTerritoryUid);
        System.out.println("getListOfTerritory.result="
                + _getListOfTerritory__return);
        System.out
                .println("getListOfTerritory._getListOfTerritory_dmaTerritories="
                        + _getListOfTerritory_dmaTerritories.value);
        System.out
                .println("getListOfTerritory._getListOfTerritory_namePlusUidOfDmaNodes="
                        + _getListOfTerritory_namePlusUidOfDmaNodes.value);
        System.out
                .println("getListOfTerritory._getListOfTerritory_cmaTerritoryUid="
                        + _getListOfTerritory_cmaTerritoryUid.value);
        logger.info("getListOfTerritory._getListOfTerritory_cmaTerritoryUid="
                + _getListOfTerritory_cmaTerritoryUid.value);
        territories = _getListOfTerritory_dmaTerritories.value;
        return _getListOfTerritory_cmaTerritoryUid.value;
    }

    public List<JNetwork> getNetwork(final String userToken,
                                     final String siteUid) {
        final Holder<JSite> site = new Holder<JSite>();
        final Holder<List<JNetwork>> networkList = new Holder<List<JNetwork>>();
        final Holder<List<JNamePlusID>> namePlusIDOfLCRs = new Holder<List<JNamePlusID>>();
        final Holder<List<JPolicyAttribute>> profileAttrs = new Holder<List<JPolicyAttribute>>();
        getSiteDetailsWithProvisioningProfile(userToken,
                                              siteUid,
                                              site,
                                              networkList,
                                              namePlusIDOfLCRs,
                                              profileAttrs);
        return networkList.value;
    }

    public List<JNetwork> getNetworkDetails(final String userToken,
                                            final String siteUid) {
        final Holder<JSite> site = new Holder<JSite>();
        final Holder<List<JNetwork>> networkList = new Holder<List<JNetwork>>();
        getSiteDetails(userToken, siteUid, site, networkList);
        return networkList.value;
    }

    public List<JPolicyAttribute> getProfileAttrs(final String userToken,
                                                  final String siteUid) {
        final Holder<JSite> site = new Holder<JSite>();
        final Holder<List<JNetwork>> networkList = new Holder<List<JNetwork>>();
        final Holder<List<JNamePlusID>> namePlusIDOfLCRs = new Holder<List<JNamePlusID>>();
        final Holder<List<JPolicyAttribute>> profileAttrs = new Holder<List<JPolicyAttribute>>();
        getSiteDetailsWithProvisioningProfile(userToken,
                                              siteUid,
                                              site,
                                              networkList,
                                              namePlusIDOfLCRs,
                                              profileAttrs);
        return profileAttrs.value;
    }

    public JSite getSite(final String userToken, final String siteUid) {
        final Holder<JSite> site = new Holder<JSite>();
        final Holder<List<JNetwork>> networkList = new Holder<List<JNetwork>>();
        final Holder<List<JNamePlusID>> namePlusIDOfLCRs = new Holder<List<JNamePlusID>>();
        final Holder<List<JPolicyAttribute>> profileAttrs = new Holder<List<JPolicyAttribute>>();
        getSiteDetailsWithProvisioningProfile(userToken,
                                              siteUid,
                                              site,
                                              networkList,
                                              namePlusIDOfLCRs,
                                              profileAttrs);
        return site.value;
    }

    public JSite getSiteDetails(final String userToken, final String siteUid) {
        final Holder<JSite> site = new Holder<JSite>();
        final Holder<List<JNetwork>> networkList = new Holder<List<JNetwork>>();
        getSiteDetails(userToken, siteUid, site, networkList);
        return site.value;
    }

    public JWebResult getSiteDetails(final String userToken,
                                     final String siteUid,
                                     final Holder<JSite> site,
                                     final Holder<List<JNetwork>> networkList) {
        System.out.println("Invoking getSiteDetails...");
        logger.info("Invoking getSiteDetails...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JNamePlusID>> namePlusIDOfLCRs = new Holder<List<JNamePlusID>>();
        return port.getSiteDetails(credentials,
                                   siteUid,
                                   site,
                                   networkList,
                                   namePlusIDOfLCRs);
    }

    public JWebResult
            getSiteDetailsWithProvisioningProfile(final String userToken,
                                                  final String siteUid,
                                                  final Holder<JSite> site,
                                                  final Holder<List<JNetwork>> networkList,
                                                  final Holder<List<JNamePlusID>> namePlusIDOfLCRs,
                                                  final Holder<List<JPolicyAttribute>> profileAttrs) {
        System.out.println("Invoking getSiteDetailsWithProvisioningProfile...");
        logger.info("Invoking getSiteDetailsWithProvisioningProfile...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.getSiteDetailsWithProvisioningProfile(credentials,
                                                          siteUid,
                                                          site,
                                                          networkList,
                                                          namePlusIDOfLCRs,
                                                          profileAttrs);
    }

    public List<JSite> getSitesInTerritory(final String userToken,
                                           final String territoryUid) {
        System.out.println("Invoking getSitesInTerritory...");
        logger.info("Invoking getSitesInTerritory...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JSite>> sites = new Holder<List<JSite>>();
        port.getSitesInTerritory(credentials, territoryUid, sites);
        return sites.value;
    }

    public List<JTerritory> getTerritories() {
        return territories;
    }

    public JTopoDataResponse getTopoData(final String userToken,
                                         final JTopoDataRequest dataReq) {
        System.out.println("Invoking getTopoData...");
        logger.info("Invoking getTopoData...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JTopoDataResponse> dataResp = new Holder<JTopoDataResponse>();
        port.getTopoData(credentials, dataReq, dataResp);
        return dataResp.value;
    }

    public JWebResult updateCloud(final String userToken,
                                  final JSiteWithoutArea cloud,
                                  final List<JSiteLink> siteLinks) {
        System.out.println("Invoking updateCloud...");
        logger.info("Invoking updateCloud...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.updateCloud(credentials, cloud, siteLinks);
    }

    public JWebResult updateSite(final String userToken,
                                 final JSite site,
                                 final List<JNetwork> networkList) {
        System.out.println("Invoking updateSite...");
        logger.info("Invoking updateSite...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.updateSite(credentials, site, networkList);
    }

    public JWebResult updateSiteLink(final String userToken,
                                     final JSiteLink siteLink) {
        System.out.println("Invoking updateSiteLink...");
        logger.info("Invoking updateSiteLink...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.updateSiteLink(credentials, siteLink);
    }

    public JWebResult updateTerritory(final String userToken,
                                      final JTerritory dmaTerritory,
                                      final List<String> siteIdsInTerritory) {
        System.out.println("Invoking updateTerritory...");
        logger.info("Invoking updateTerritory...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        return port.updateTerritory(credentials,
                                    dmaTerritory,
                                    siteIdsInTerritory);
    }
}
