package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.SiteTopoManagerHandler;
import com.polycom.webservices.SiteTopoManager.JCommonUIObject;
import com.polycom.webservices.SiteTopoManager.JInternetCallRouting;
import com.polycom.webservices.SiteTopoManager.JInternetCallRoutingMethod;
import com.polycom.webservices.SiteTopoManager.JIsdnNumberAssignment;
import com.polycom.webservices.SiteTopoManager.JIsdnNumberAssignmentMethod;
import com.polycom.webservices.SiteTopoManager.JMediaPathExclusion;
import com.polycom.webservices.SiteTopoManager.JNetwork;
import com.polycom.webservices.SiteTopoManager.JPolicyAttribute;
import com.polycom.webservices.SiteTopoManager.JQosStatistics;
import com.polycom.webservices.SiteTopoManager.JSite;
import com.polycom.webservices.SiteTopoManager.JSiteDevicesInfo;
import com.polycom.webservices.SiteTopoManager.JSiteLink;
import com.polycom.webservices.SiteTopoManager.JSiteStatistics;
import com.polycom.webservices.SiteTopoManager.JSiteType;
import com.polycom.webservices.SiteTopoManager.JStatus;
import com.polycom.webservices.SiteTopoManager.JTerritory;
import com.polycom.webservices.SiteTopoManager.JTopoDataRequest;
import com.polycom.webservices.SiteTopoManager.JTopoDataResponse;
import com.polycom.webservices.SiteTopoManager.JTopoExclusionsRequest;
import com.polycom.webservices.SiteTopoManager.JTopoExclusionsResponse;
import com.polycom.webservices.SiteTopoManager.JTopoLinksRequest;
import com.polycom.webservices.SiteTopoManager.JTopoLinksResponse;
import com.polycom.webservices.SiteTopoManager.JTopoSiteStatisticsRequest;
import com.polycom.webservices.SiteTopoManager.JTopoSiteStatisticsResponse;
import com.polycom.webservices.SiteTopoManager.JTopoTerritoriesRequest;
import com.polycom.webservices.SiteTopoManager.JTopoTerritoriesResponse;
import com.polycom.webservices.SiteTopoManager.JWebResult;

/**
 * Site Topo handler. This class will handle the webservice request of Site Topo
 * module
 *
 * @author wbchao
 *
 */
public class SiteTopoHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "addSite ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params1 = "siteName=site1 siteDescription=auto ipaddr1=172.21.98.194 maskSize1=32 ";
        // final String method = "getSiteUid ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "siteName=c1 ";
        // final String method = "addExclusion ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "siteLinkSiteUidA=efdc3eaa-4f08-42f1-864f-4045646d85c8
        // siteLinkSiteUidB=5610d685-cdf1-4b81-8754-0f051ecbc81b
        // exclusionName=e1 exclusionDescription=auto ";
        // final String method = "getNetworkSpcific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "ip=172.21.98.116 siteUid=be98a74d-3181-49b2-8263-4965ebd8eee1
        // keyword=Bandwidth ";
        // final String method = "getSiteSpcific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "siteUid=be98a74d-3181-49b2-8263-4965ebd8eee1 keyword=Bandwidth ";
        // final String method = "getInetCallRoutingConfig ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "siteUid=be98a74d-3181-49b2-8263-4965ebd8eee1 keyword=IcrMethod ";
        // final String method = "addTerritory ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "enableConferenceRoomHost=true territoryName=territory1
        // territoryDescription=auto siteName=site1,site2 ";
        // final String method = "deleteTerritory ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "territoryUid=235b42d0-bf52-461a-9e69-469d7b55b602 ";
        // final String method = "getSiteLinkStatisticsSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "siteLinkName=sitelink1 keyword=averageQosData:packetLossRateCount ";
        // final String method = "getTerritorySpecfic ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "keyword=Uid territoryName=T1 ";
        //
        // final String method = "updateSite ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "updateAttrName1=TerritoryUid
        // updateAttrValue1=17c0441b-c462-4fbf-84cf-ca60fe070a60
        // siteUid=e9b7fb9e-b9fb-4ccd-9283-277f5d0fb198 ";
        // final String method = "updateSite ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // String params1 =
        // "updateAttrName1=CityCode updateAttrValue1=021
        // updateAttrName2=LocationName updateAttrValue2=Shanghai,~Shanghai,~CN
        // siteUid=e9b7fb9e-b9fb-4ccd-9283-277f5d0fb198 ";
        // params1 +=
        // "updateAttrName3=Longitude updateAttrValue3=121.368
        // updateAttrName4=Latitude updateAttrValue4=31.109 ";
        // final String method = "updateSiteLink ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "siteLinkName=link1 updateAttrName1=description
        // updateAttrValue1=autolink updateAttrName2=bandwidth
        // updateAttrValue2=100000 ";
        // final String method = "addSiteLink ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "siteLinkName=link1 siteLinkDescription=auto
        // siteLinkSiteUidA=be98a74d-3181-49b2-8263-4965ebd8eee1
        // siteLinkSiteUidB=41393567-9ad1-49d5-9d98-555cfa7ccf93 ";
        // final String method = "getSiteLinkUid ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "siteLinkName=link1 ";
        // final String method = "getSiteStatisticsSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "siteName=site2 keyword=UsedBWPercent ";
        // final String method = "getNetworkTopoDataSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "keyword=UsedBWPercent ip=172.21.98.101 siteName=site1 ";
        // final String method = "deleteSite ";
        // final String auth = "username=admin password=UG9seWNvbTEyMw==
        // domain=LOCAL ";
        // final String params1 = "siteName=cloud1 ";
        // final String method = "deleteAllSites ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = " ";
        // final String method = "deleteExclusions ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "exclusionUid=c0b0424b-4002-4940-bf4c-f367e61093f7 ";
        // final String method = "addCloud ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 =
        // "cloudName=cloud1 cloudDescription=auto1 siteName=s1 ";
        // final String method = "updateCloud ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params1 = "siteName=cloud1 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JSiteTopoManager SiteTopoManager "
                + method + auth + params1;
        final SiteTopoHandler handler = new SiteTopoHandler(command);
        final String result = handler.build();
        System.out.println("result for " + method + " is " + result);
    }

    private final SiteTopoManagerHandler stmh;
    private final String                 DEFAULT_BANDWIDTH = "-2000000000000";
    private final String                 DEFAULT_BITRATE   = "-2000000000";

    public SiteTopoHandler(final String cmd) throws IOException {
        super(cmd);
        stmh = new SiteTopoManagerHandler(webServiceUrl);
    }

    /**
     * Add cloud
     *
     * @see cloudName=cloud1 <br/>
     *      cloudDescription=auto1 <br/>
     *      siteName=site3,site4
     *
     * @param cloudName
     *            The cloud name
     * @param cloudDescription
     *            The cloud description
     * @param siteName
     *            The site name list, split with ","
     * @return The result
     */
    public String addCloud() {
        String status = "Failed";
        final JSite cloud = new JSite();
        CommonUtils.generateField(cloud);
        final String cloudName = inputCmd.get("cloudName");
        final String cloudDescription = inputCmd.get("cloudDescription");
        cloud.setBandwidth(Long.parseLong(DEFAULT_BANDWIDTH));
        cloud.setBitrate(Long.parseLong(DEFAULT_BITRATE));
        cloud.setName(cloudName);
        cloud.setDescription(cloudDescription);
        cloud.setType(JSiteType.MPLS);
        final List<JCommonUIObject> sites = stmh
                .getListOfSiteForRule(userToken);
        final List<JSiteLink> siteLinks = new ArrayList<JSiteLink>();
        final String[] siteNames = inputCmd.get("siteName").split(",");
        for (final String siteName : siteNames) {
            for (final JCommonUIObject site : sites) {
                if (siteName.equals(site.getDisplayString())) {
                    final JSiteLink siteLink = new JSiteLink();
                    siteLink.setBandwidth(Long.parseLong(DEFAULT_BANDWIDTH));
                    siteLink.setBitrate(Long.parseLong(DEFAULT_BITRATE));
                    siteLink.setName(cloudName + "/" + siteName);
                    siteLink.setSiteUidA(site.getKeyValue());
                    siteLinks.add(siteLink);
                    break;
                }
            }
        }
        final JWebResult result = stmh.addCloud(userToken, cloud, siteLinks);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Add cloud " + cloud.getName() + " successfully.");
        } else {
            status = "Failed";
            logger.error("Add cloud " + cloud.getName() + " failed.");
            return status + "Add cloud " + cloud.getName() + " failed.";
        }
        return status;
    }

    /**
     * Add exclusion
     *
     * @see exclusionName=exclusion1 <br/>
     *      exclusionDescription=auto <br/>
     *      siteLinkSiteUidA=$site3Uid <br/>
     *      siteLinkSiteUidB=$site4Uid
     *
     * @param exclusionName
     *            The exclusion name
     * @param exclusionDescription
     *            The exclusion description
     * @param siteLinkSiteUidA
     *            The site A uid
     * @param siteLinkSiteUidB
     *            The site B uid
     * @return The result
     */
    public String addExclusion() {
        String status = "Failed";
        final List<JMediaPathExclusion> exclusions = new ArrayList<JMediaPathExclusion>();
        final JMediaPathExclusion exclusion = new JMediaPathExclusion();
        exclusion.setName(inputCmd.get("exclusionName"));
        exclusion.setDescription(inputCmd.get("exclusionDescription"));
        exclusion.setSiteAUid(inputCmd.get("siteLinkSiteUidA"));
        exclusion.setSiteBUid(inputCmd.get("siteLinkSiteUidB"));
        exclusions.add(exclusion);
        final JWebResult result = stmh.addExclusions(userToken, exclusions);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Add exclusion " + exclusion.getName()
                    + " successfully.");
        } else {
            status = "Failed";
            logger.error("Add exclusion " + exclusion.getName() + " failed.");
            return status + "Add exclusion " + exclusion.getName() + " failed.";
        }
        return status;
    }

    /**
     * Create site. Currently only support maximum 3 IP addresses.
     *
     * @see siteName=$siteNameXMA <br/>
     *      siteDescription=auto <br/>
     *      ipaddr1=1.1.1.1 <br/>
     *      maskSize1=32 <br/>
     *      siteTotalBandwidth=1000000
     *
     * @param siteName
     *            The site name
     * @param siteDescription
     *            The site description
     * @param ipaddr
     *            [1-3] The include ips
     * @param maskSize
     *            [1-3] The mask size
     * @param bandwidth
     *            [1-3] The bandwidth
     * @param bitRate
     *            [1-3] The bit rate
     * @param siteTotalBandwidth
     *            The site total bandwidth
     * @return The result
     */
    public String addSite() {
        String status = "Failed";
        // get the territory
        final String territoryUid = stmh.getListOfTerritory(userToken);
        logger.info("The territory UID is: " + territoryUid);
        if (!getInputCmd().get("siteName").isEmpty()
                && !getInputCmd().get("siteDescription").isEmpty()
                && !getInputCmd().get("ipaddr1").isEmpty()
                && !getInputCmd().get("maskSize1").isEmpty()) {
            final List<JNetwork> networkList = new LinkedList<JNetwork>();
            // compose the network list for the site
            final JNetwork network1 = new JNetwork();
            logger.info("Input ip is: " + getInputCmd().get("ipaddr1"));
            network1.setAddress(getInputCmd().get("ipaddr1"));
            network1.setMaskSize(Integer
                    .valueOf(getInputCmd().get("maskSize1")));
            network1.setName(getInputCmd().get("ipaddr1") + "/"
                    + getInputCmd().get("maskSize1"));
            network1.setBandwidth(Long.parseLong(inputCmd.get("bandwidth1")));
            network1.setBitrate(Long.parseLong(inputCmd.get("bitRate1")));
            networkList.add(network1);
            if (!getInputCmd().get("ipaddr2").isEmpty()
                    && !getInputCmd().get("maskSize2").isEmpty()) {
                final JNetwork network2 = new JNetwork();
                network2.setAddress(getInputCmd().get("ipaddr2"));
                network2.setMaskSize(Integer
                        .valueOf(getInputCmd().get("maskSize2")));
                network2.setName(getInputCmd().get("ipaddr2") + "/"
                        + getInputCmd().get("maskSize2"));
                network2.setBandwidth(Long
                        .parseLong(inputCmd.get("bandwidth2")));
                network2.setBitrate(Long.parseLong(inputCmd.get("bitRate2")));
                networkList.add(network2);
            }
            if (!getInputCmd().get("ipaddr3").isEmpty()
                    && !getInputCmd().get("maskSize3").isEmpty()) {
                final JNetwork network3 = new JNetwork();
                network3.setAddress(getInputCmd().get("ipaddr3"));
                network3.setMaskSize(Integer
                        .valueOf(getInputCmd().get("maskSize3")));
                network3.setName(getInputCmd().get("ipaddr3") + "/"
                        + getInputCmd().get("maskSize3"));
                network3.setBandwidth(Long
                        .parseLong(inputCmd.get("bandwidth3")));
                network3.setBitrate(Long.parseLong(inputCmd.get("bitRate3")));
                networkList.add(network3);
            }
            final String siteDescription = inputCmd.get("siteDescription");
            final String siteName = inputCmd.get("siteName");
            final JSite addSite = new JSite();
            final String bandwitdth = inputCmd.get("siteTotalBandwidth");
            if (!bandwitdth.isEmpty()) {
                addSite.setBandwidth(Long.parseLong(bandwitdth));
            }
            final String maxBitrate = inputCmd.get("siteCallMaxBitrate");
            if (!maxBitrate.isEmpty()) {
                addSite.setBitrate(Long.parseLong(maxBitrate));
            }
            addSite.setCityCode("010");
            addSite.setContinentId(4);
            addSite.setCountryCode("86");
            addSite.setDescription(siteDescription);
            addSite.setEnableMutualtls(false);
            final JInternetCallRouting icr = new JInternetCallRouting();
            final String h323RoutingMethod = inputCmd.get("h323RoutingMethod");
            if (h323RoutingMethod.isEmpty()) {
                icr.setIcrMethod(JInternetCallRoutingMethod.H_323_FIREWALL);
                icr.setIpPort(1720);
            } else {
                icr.setIcrMethod(JInternetCallRoutingMethod
                        .fromValue(h323RoutingMethod));
                icr.setIpAddress(inputCmd.get("h323RoutingIp"));
                icr.setIpPort(Integer
                        .parseInt(inputCmd.get("h323RoutingPort")));
            }
            final String sipRoutingMethod = inputCmd.get("sipRoutingMethod");
            if (sipRoutingMethod.isEmpty()) {
                icr.setIcrSIPMethod(JInternetCallRoutingMethod.SIP_FIREWALL);
                icr.setSipPort(5070);
            } else {
                icr.setIcrSIPMethod(JInternetCallRoutingMethod
                        .fromValue(sipRoutingMethod));
                icr.setSipAddress(inputCmd.get("sipRoutingIp"));
                icr.setSipPort(Integer
                        .parseInt(inputCmd.get("sipRoutingPort")));
            }
            addSite.setInetCallRoutingConfig(icr);
            final JIsdnNumberAssignment ina = new JIsdnNumberAssignment();
            ina.setAssignmentMethod(JIsdnNumberAssignmentMethod.NONE);
            ina.setIsdnPhoneNumberLength(0);
            ina.setExtensionLength(0);
            addSite.setIsdnNumberAssignmentConfig(ina);
            addSite.setLatitude((float) 39.929);
            addSite.setLcrTableUid(0);
            addSite.setLocationName("Beijing, Beijing, CN");
            addSite.setLongitude((float) 116.388);
            addSite.setName(siteName);
            addSite.setOverrideItuDialingRules(false);
            addSite.setProvisioningPolicyUid(0);
            addSite.setSiteForAccessServer(false);
            addSite.setSitePhoneNumberLength(0);
            addSite.setTerritoryUid(territoryUid);
            addSite.setType(JSiteType.PRIVATE);
            addSite.setBelongsToAreaUgpId(0);
            final JWebResult result = stmh.addSite(userToken,
                                                   addSite,
                                                   networkList);
            if (result.getStatus().compareTo(JStatus.SUCCESS) == 0) {
                status = "SUCCESS";
                logger.info("Site " + getInputCmd().get("siteName")
                        + " is successfully created.");
            } else {
                status = "Failed";
                logger.error("Site " + getInputCmd().get("siteName")
                        + " is not successfully created.");
                return status + " Site " + getInputCmd().get("siteName")
                        + " is not successfully created.";
            }
        } else {
            status = "Failed";
            logger.error("Some parameters are missing in the input command. Please check your command.");
            return status
                    + " Some parameters are missing in the input command. Please check your command.";
        }
        return status;
    }

    /**
     * Add site link
     *
     * @see siteLinkName=link1 <br/>
     *      siteLinkDescription=auto <br/>
     *      siteLinkSiteUidA=$sUid1 <br/>
     *      siteLinkSiteUidB=$sUid2
     *
     * @param siteLinkName
     *            The site link name
     * @param siteLinkDescription
     *            The site link description
     * @param siteLinkSiteUidA
     *            The site A
     * @param siteLinkSiteUidB
     *            The site B
     * @return The result
     */
    public String addSiteLink() {
        String status = "Failed";
        final JSiteLink siteLink = new JSiteLink();
        siteLink.setName(inputCmd.get("siteLinkName"));
        siteLink.setDescription(inputCmd.get("siteLinkDescription"));
        siteLink.setBandwidth(Long
                .parseLong(inputCmd.get("siteLinkBandwidth")));
        siteLink.setBitrate(Long.parseLong(inputCmd.get("siteLinkBitrate")));
        siteLink.setSiteUidA(inputCmd.get("siteLinkSiteUidA"));
        siteLink.setSiteUidB(inputCmd.get("siteLinkSiteUidB"));
        final JWebResult result = stmh.addSiteLink(userToken, siteLink);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Add site " + siteLink.getName() + " successfully.");
        } else {
            status = "Failed";
            logger.error("Add site " + siteLink.getName() + " failed.");
            return status + "Add site " + siteLink.getName() + " failed.";
        }
        return status;
    }

    /**
     * Add territory
     *
     * @see dmaUid=$primaryNodeUid <br/>
     *      backupDmaUid=$nodeId2 <br/>
     *      territoryName=territory1 <br/>
     *      territoryDescription=auto <br/>
     *      enableConferenceRoomHost=true <br/>
     *      siteName=site1,site2
     *
     * @param dmaUid
     *            The primary cluster DMA uid
     * @param backupDmaUid
     *            The backup cluster DMA uid
     * @param territoryName
     *            The territory name
     * @param territoryDescription
     *            The territory description
     * @param enableConferenceRoomHost
     *            Whether enable the conference room host
     * @param siteName
     *            The included site list
     * @return The result
     */
    public String addTerritory() {
        String status = "Failed";
        final JTerritory dmaTerritory = new JTerritory();
        final String dmaUid = inputCmd.get("dmaUid");
        final String backupDmaUid = inputCmd.get("backupDmaUid");
        final String territoryName = inputCmd.get("territoryName");
        final String territoryDescription = inputCmd
                .get("territoryDescription");
        dmaTerritory.setPrimaryNodeUid(dmaUid);
        dmaTerritory.setBackupNodeUid(backupDmaUid);
        dmaTerritory.setName(territoryName);
        dmaTerritory.setDescription(territoryDescription);
        if (inputCmd.get("enableConferenceRoomHost").equalsIgnoreCase("true")) {
            dmaTerritory.setConferenceRoomHost(true);
        } else {
            dmaTerritory.setConferenceRoomHost(false);
        }
        final String siteName = inputCmd.get("siteName");
        final List<JCommonUIObject> sites = stmh
                .getListOfSiteForRule(userToken);
        final List<String> siteIdsInTerritory = new ArrayList<String>();
        for (final String name : siteName.split(",")) {
            for (final JCommonUIObject site : sites) {
                if (name.equals(site.getDisplayString())) {
                    siteIdsInTerritory.add(site.getKeyValue());
                }
            }
        }
        final JWebResult result = stmh
                .addTerritory(userToken, dmaTerritory, siteIdsInTerritory);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Add Territory " + territoryName + " successfully.");
        } else {
            status = "Failed";
            logger.error("Add Territory " + territoryName + " failed.");
            return status + "Add Territory " + territoryName + " failed.";
        }
        return status;
    }

    /**
     * Delete all site links.
     *
     * @see No param
     *
     * @return The result
     */
    public String deleteAllSiteLinks() {
        String status = "SUCCESS";
        String failedSiteLink = "";
        final JTopoDataRequest dataReq = new JTopoDataRequest();
        final JTopoLinksRequest linksRequest = new JTopoLinksRequest();
        linksRequest.setToken(0);
        dataReq.setLinksRequest(linksRequest);
        final JTopoDataResponse dataResp;
        try {
        	dataResp = stmh.getTopoData(userToken, dataReq);
        } catch (final Exception e) {
			e.printStackTrace();
			return "Failed, got exception when get topo data. Error msg is "
					+ e.getMessage();
		}
        final JTopoLinksResponse linksResponse = dataResp.getLinksResponse();
        final List<JSiteLink> links = linksResponse.getLinks();
        for (final JSiteLink link : links) {
            final JWebResult result = stmh.deleteSiteLink(userToken,
                                                          link.getUid());
            if (!result.getStatus().equals(JStatus.SUCCESS)) {
                status = "Failed";
                failedSiteLink += link.getName() + " ";
            }
        }
        if (status.equals("Failed")) {
            logger.error("delete the sitelink[" + failedSiteLink.trim()
                    + "] failed");
            return status + " ,delete the sitelink[" + failedSiteLink.trim()
                    + "] failed";
        }
        return status;
    }

    /**
     * Delete all sites.
     *
     * @see No param
     *
     * @return The result
     */
    public String deleteAllSites() {
        String status = "Failed";
        final List<JCommonUIObject> sites = stmh
                .getListOfSiteForRule(userToken);
        final List<String> siteUids = new ArrayList<String>();
        for (final JCommonUIObject site : sites) {
            if ("Internet/VPN".equals(site.getDisplayString())) {
                continue;
            } else {
                siteUids.add(site.getKeyValue());
            }
        }
        final JWebResult result = stmh.deleteSites(userToken, siteUids);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("All sites is deleted successfully.");
        } else {
            status = "Failed";
            logger.error("Failed to delete all sites");
            return status + "Failed to delete all sites";
        }
        return status;
    }

    /**
     * Delete the exclusion
     *
     * @see exclusionUid=$exclusionUid1
     *
     * @param exclusionUid
     *            The exclusion uid
     * @return The result
     */
    public String deleteExclusion() {
        String status = "Failed";
        final String exclusionUid = inputCmd.get("exclusionUid");
        final List<String> exclusions = new ArrayList<String>();
        exclusions.add(exclusionUid);
        final JWebResult result = stmh.deleteExclusions(userToken, exclusions);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Delete exclusion successfully.");
        } else {
            status = "Failed";
            logger.error("Delete exclusion failed.");
            return status + "Delete exclusion failed.";
        }
        return status;
    }

    /**
     * Delete specified site.
     *
     * @see siteName=$siteName
     *
     * @param siteName
     *            The site name
     * @return The result
     */
    public String deleteSite() {
        String status = "Failed";
        final String siteName = getInputCmd().get("siteName");
        final List<String> siteUids = new ArrayList<String>();
        final JSite site = getSiteByName(siteName);
        if (site != null) {
            siteUids.add(site.getUid());
        }
        final JWebResult result = stmh.deleteSites(userToken, siteUids);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Site " + siteName + " is deleted successfully.");
        } else {
            status = "Failed";
            logger.error("Site " + siteName + " is deleted failed.");
            return status + "Site " + siteName + " is deleted failed.";
        }
        return status;
    }

    /**
     * Delete specified site link.
     *
     * @see siteLinkUid=$sitelinkUid2
     *
     * @param siteLinkUid
     *            The site link uid
     * @return The result
     */
    public String deleteSiteLink() {
        String status = "Failed";
        final String siteLinkUid = inputCmd.get("siteLinkUid");
        final JWebResult result = stmh.deleteSiteLink(userToken, siteLinkUid);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Delete siteLink successfully.");
        } else {
            status = "Failed";
            logger.error("Delete siteLink failed.");
            return status + "Delete siteLink failed.";
        }
        return status;
    }

    /**
     * Delete territory
     *
     * @see territoryUid=$territoryUid
     *
     * @param territoryUid
     *            The territory uid
     * @return The result
     */
    public String deleteTerritory() {
        String status = "Failed";
        final String territoryUid = inputCmd.get("territoryUid");
        final JWebResult result = stmh.deleteTerritory(userToken, territoryUid);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Delete territory successfully.");
        } else {
            status = "Failed";
            logger.error("Delete territory failed.");
            return status + "Delete territory failed.";
        }
        return status;
    }

    /**
     * Get the site specified attribute value
     *
     * @see siteUid=$siteUid1 <br/>
     *      keyword=$id
     * @param siteUid
     *            The site uid
     * @param keyword
     *            The attribute name
     * @return The site specified attribute value
     */
    public String getDevicesInfoForSiteSpecific() {
        String result = "";
        final String siteUid = inputCmd.get("siteUid");
        final String keyword = inputCmd.get("keyword");
        final JSiteDevicesInfo siteDevicesInfo = stmh
                .getDevicesInfoForSite(userToken, siteUid);
        result = CommonUtils.invokeGetMethod(siteDevicesInfo, keyword);
        return result;
    }

    /**
     * Get the exclusion topo data specified attribute value
     *
     * @see exclusionName=exclusion1 <br/>
     *      keyword=uid
     *
     * @param exclusionName
     *            The exclusion name
     * @param keyword
     *            The attribute name
     * @return The exclusion topo data specified attribute value
     */
    public String getExclusionTopoDataSpecific() {
        final String result = "NotFound";
        final String exclusionName = inputCmd.get("exclusionName");
        final String keyword = inputCmd.get("keyword");
        final JTopoDataRequest dataReq = new JTopoDataRequest();
        final JTopoExclusionsRequest exclusionsRequest = new JTopoExclusionsRequest();
        exclusionsRequest.setToken(0);
        dataReq.setExclusionsRequest(exclusionsRequest);
        final JTopoDataResponse dataResp = stmh.getTopoData(userToken, dataReq);
        final JTopoExclusionsResponse exclusionsResponse = dataResp
                .getExclusionsResponse();
        final List<JMediaPathExclusion> exclusions = exclusionsResponse
                .getExclusions();
        for (final JMediaPathExclusion exclusion : exclusions) {
            if (exclusionName.equals(exclusion.getName())) {
                return CommonUtils.invokeGetMethod(exclusion, keyword);
            }
        }
        return result;
    }

    /**
     * Internal method, get the network by ip
     *
     * @param siteUid
     *            The site uid included the network
     * @param ip
     *            The network to find
     * @return JNetwork
     */
    private JNetwork getNetworkByIp(final String siteUid, final String ip) {
        final List<JNetwork> networks = stmh.getNetworkDetails(userToken,
                                                               siteUid);
        for (final JNetwork network : networks) {
            if (ip.equals(network.getAddress())) {
                return network;
            }
        }
        return null;
    }

    /**
     * Get the network detail specified attribute value
     *
     * @see ip=$e1_win_addr <br/>
     *      siteUid=$siteUid1 <br/>
     *      keyword=Address
     *
     * @param ip
     *            The network device ip
     * @param siteUid
     *            The site uid
     * @param keyword
     *            The attribute name
     * @return The network detail specified attribute value
     */
    public String getNetworkDetailSpcific() {
        final String ip = inputCmd.get("ip");
        final String siteUid = inputCmd.get("siteUid");
        final String keyword = inputCmd.get("keyword");
        final JNetwork network = getNetworkByIp(siteUid, ip);
        if (network == null) {
            return "Failed, could not find the network with " + ip;
        }
        return CommonUtils.invokeGetMethod(network, keyword);
    }

    /**
     * Get the network topo data specified attribute value
     *
     * @see keyword=UsedBWPercent <br/>
     *      ip=$e5_gs_addr <br/>
     *      siteName=site2
     *
     * @param siteName
     *            The site name
     * @param ip
     *            The network ip
     * @param keyword
     *            The attribute name
     * @return The network topo data specified attribute value
     */
    public String getNetworkTopoDataSpecific() {
        final String result = "NotFound";
        final String siteName = inputCmd.get("siteName");
        final String ip = inputCmd.get("ip");
        final String keyword = inputCmd.get("keyword");
        final JTopoDataRequest dataReq = new JTopoDataRequest();
        final JTopoSiteStatisticsRequest siteStatisticsRequest = new JTopoSiteStatisticsRequest();
        siteStatisticsRequest.setToken(0);
        siteStatisticsRequest.setIncludeSubnets(true);
        dataReq.setSiteStatisticsRequest(siteStatisticsRequest);
        final JTopoDataResponse dataResp = stmh.getTopoData(userToken, dataReq);
        final JTopoSiteStatisticsResponse siteStatisticsResponse = dataResp
                .getSiteStatisticsResponse();
        final List<JSiteStatistics> siteStatistics = siteStatisticsResponse
                .getSiteStatistics();
        for (final JSiteStatistics s : siteStatistics) {
            if (siteName.equals(s.getSiteStatistics().getName())) {
                final List<JQosStatistics> subnetStatistics = s
                        .getSubnetStatistics();
                for (final JQosStatistics ep : subnetStatistics) {
                    if (ep.getName().contains(ip)) {
                        return CommonUtils.invokeGetMethod(ep, keyword);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Internal method, get the site by name
     *
     * @param siteName
     *            The site name
     * @return JSite
     */
    private JSite getSiteByName(final String siteName) {
        final List<JSite> sites = stmh.getListOfSite(userToken);
        for (final JSite site : sites) {
            if (siteName.equals(site.getName())) {
                return site;
            }
        }
        return null;
    }

    /**
     * Get the site detail specified attribute value
     *
     * @see keyword=UsedBWPercent <br/>
     *      siteUid=siteUid2
     *
     * @param siteUid
     *            The site uid
     * @param keyword
     *            The attribute name
     * @return The site detail specified attribute value
     */
    public String getSiteDetailSpcific() {
        final String siteUid = inputCmd.get("siteUid");
        final String keyword = inputCmd.get("keyword");
        final JSite site = stmh.getSiteDetails(userToken, siteUid);
        if (site == null) {
            return "Failed, could not find the site";
        }
        return CommonUtils.invokeGetMethod(site, keyword);
    }

    /**
     * Internal method, get site link by name
     *
     * @param siteLinkName
     *            The site link name
     * @return JSiteLink
     */
    private JSiteLink getSiteLinkByName(final String siteLinkName) {
        final JTopoDataRequest dataReq = new JTopoDataRequest();
        final JTopoLinksRequest linksRequest = new JTopoLinksRequest();
        linksRequest.setToken(0);
        dataReq.setLinksRequest(linksRequest);
        final JTopoDataResponse dataResp = stmh.getTopoData(userToken, dataReq);
        final JTopoLinksResponse linksResponse = dataResp.getLinksResponse();
        final List<JSiteLink> links = linksResponse.getLinks();
        for (final JSiteLink link : links) {
            if (siteLinkName.equals(link.getName())) {
                return link;
            }
        }
        return null;
    }

    /**
     * Internal method, get the site link statistic
     *
     * @param name
     *            The site link name
     * @return JQosStatistics
     */
    private JQosStatistics getSiteLinkStatistics(final String name) {
        final List<JQosStatistics> sites = stmh
                .getListOfSiteLinkMonitor(userToken, "");
        for (final JQosStatistics site : sites) {
            if (name.equals(site.getName())) {
                return site;
            }
        }
        return null;
    }

    /**
     * Get the site link statistics specified attribute value
     *
     * @see siteLinkName=link1 <br/>
     *      keyword=usedBWPercent
     *
     * @param siteLinkName
     *            The site link name
     * @param keyword
     *            The attribute name
     * @return The site link statistics specified attribute value
     */
    public String getSiteLinkStatisticsSpecific() {
        String result = "NotFound";
        final String keyword = inputCmd.get("keyword");
        final JQosStatistics site = getSiteLinkStatistics(inputCmd
                .get("siteLinkName"));
        if (site != null) {
            result = CommonUtils.invokeGetMethod(site, keyword);
        }
        return result;
    }

    /**
     * Get the site link uid by name
     *
     * @see siteLinkName=link1
     *
     * @param siteLinkName
     *            The site link name
     * @return The site link uid
     */
    public String getSiteLinkUid() {
        final String siteLinkName = inputCmd.get("siteLinkName");
        final JSiteLink siteLink = getSiteLinkByName(siteLinkName);
        if (siteLink == null) {
            return "NotFound, could not find the sitelink with name "
                    + siteLinkName;
        } else {
            return siteLink.getUid();
        }
    }

    /**
     * Get the site profile attribute value
     *
     * @see siteUid=$siteUid <br/>
     *      attrName=$attrName
     * @param siteUid
     *            The site uid
     * @param attrName
     *            The attribute name
     *
     * @return The site profile attribute value
     */
    public String getSiteProfileAttr() {
        final String result = "NotFound";
        final String siteUid = inputCmd.get("siteUid");
        final String attrName = inputCmd.get("attrName");
        final List<JPolicyAttribute> attrs = stmh.getProfileAttrs(userToken,
                                                                  siteUid);
        for (final JPolicyAttribute attr : attrs) {
            if (attrName.equals(attr.getAttrName())) {
                return attr.getAttrValue();
            }
        }
        return result;
    }

    /**
     * Get the site name list in specified territory
     *
     * @see territoryUid=$defaultTerritoryUid
     * @param territoryUid
     *            The territory uid
     * @return The site name list in specified territory
     */
    public String getSitesInTerritory() {
        final String result = "NotFound";
        final String territoryUid = inputCmd.get("territoryUid");
        final List<JSite> sites = stmh.getSitesInTerritory(userToken,
                                                           territoryUid);
        final StringBuffer siteNames = new StringBuffer();
        for (final JSite site : sites) {
            siteNames.append(",").append(site.getName());
        }
        if (siteNames.length() > 0) {
            return siteNames.substring(1).toString();
        } else {
            return result;
        }
    }

    /**
     * Get the site specified attribute value
     *
     * @see keyword=UsedBWPercent <br/>
     *      siteUid=siteUid2
     *
     * @param siteUid
     *            The site uid
     * @param keyword
     *            The attribute name
     * @return The site specified attribute value
     */
    public String getSiteSpcific() {
        final String siteUid = inputCmd.get("siteUid");
        final String keyword = inputCmd.get("keyword");
        final JSite site = stmh.getSite(userToken, siteUid);
        if (site == null) {
            return "Failed, could not find the site";
        }
        return CommonUtils.invokeGetMethod(site, keyword);
    }

    /**
     * Internal method, get the site statistic
     *
     * @param name
     *            The site name
     * @return JQosStatistics
     */
    private JQosStatistics getSiteStatistics(final String name) {
        final List<JQosStatistics> sites = stmh.getListOfSiteMonitor(userToken,
                                                                     "");
        for (final JQosStatistics site : sites) {
            if (name.equals(site.getName())) {
                return site;
            }
        }
        return null;
    }

    /**
     * Get the site statistics specified attribute value
     *
     * @see siteLinkName=link1 <br/>
     *      keyword=usedBWPercent
     *
     * @param siteName
     *            The site name
     * @param keyword
     *            The attribute name
     * @return The site statistics specified attribute value
     */
    public String getSiteStatisticsSpecific() {
        String result = "NotFound";
        final String keyword = inputCmd.get("keyword");
        final JQosStatistics site = getSiteStatistics(inputCmd.get("siteName"));
        if (site != null) {
            result = CommonUtils.invokeGetMethod(site, keyword);
        }
        return result;
    }

    /**
     * Get the site uid
     *
     * @see siteName=site3
     *
     * @param siteName
     *            The site name
     * @return The site uid
     */
    public String getSiteUid() {
        final JSite site = getSiteByName(inputCmd.get("siteName"));
        if (site == null) {
            return "NotFound";
        } else {
            return site.getUid();
        }
    }

    /**
     * Get the territory specified attribute value
     *
     * @see keyword=Uid <br/>
     *      territoryName=territory1
     *
     * @param territoryName
     *            The territory name
     * @param keyword
     *            The attribute name
     * @return The territory specified attribute value
     */
    public String getTerritorySpecfic() {
        String result = "NotFound";
        final String territoryName = inputCmd.get("territoryName")
                .replaceAll("~", " ");
        final String keyword = inputCmd.get("keyword");
        final JTerritory territory = getTerritoryTopoData(territoryName);
        if (territory != null) {
            result = CommonUtils.invokeGetMethod(territory, keyword);
        }
        return result;
    }

    /**
     * Internal method, get the territory by name
     *
     * @param name
     *            The territory name
     * @return JTerritory
     */
    private JTerritory getTerritoryTopoData(final String name) {
        final JTopoDataRequest dataReq = new JTopoDataRequest();
        final JTopoTerritoriesRequest territoriesRequest = new JTopoTerritoriesRequest();
        territoriesRequest.setToken(0);
        dataReq.setTerritoriesRequest(territoriesRequest);
        final JTopoDataResponse dataResp = stmh.getTopoData(userToken, dataReq);
        final JTopoTerritoriesResponse territoriesResponse = dataResp
                .getTerritoriesResponse();
        final List<JTerritory> territories = territoriesResponse
                .getTerritories();
        for (final JTerritory territory : territories) {
            if (name.equals(territory.getName())) {
                return territory;
            }
        }
        return null;
    }

    /**
     * Inject the args from tcl
     */
    @Override
    protected void injectCmdArgs() {
        // Site name
        inputCmd.put("siteName", "");
        // Site call max bit rate
        inputCmd.put("siteCallMaxBitrate", "");
        // Site total bandwidth
        inputCmd.put("siteTotalBandwidth", "");
        // Site description
        inputCmd.put("siteDescription", "");
        // IP Address 1#
        inputCmd.put("ipaddr1", "");
        // Mask size for IP 1#
        inputCmd.put("maskSize1", "");
        // Bandwidth for IP 1#
        inputCmd.put("bandwidth1", DEFAULT_BANDWIDTH);
        // BitRate for IP 1#
        inputCmd.put("bitRate1", DEFAULT_BITRATE);
        // IP Address 2#
        inputCmd.put("ipaddr2", "");
        // Mask size for IP 2#
        inputCmd.put("maskSize2", "");
        // Bandwidth for IP 2#
        inputCmd.put("bandwidth2", DEFAULT_BANDWIDTH);
        // BitRate for IP 2#
        inputCmd.put("bitRate2", DEFAULT_BITRATE);
        // IP Address 3#
        inputCmd.put("ipaddr3", "");
        // Bandwidth for IP 3#
        inputCmd.put("bandwidth3", DEFAULT_BANDWIDTH);
        // BitRate for IP 3#
        inputCmd.put("bitRate3", DEFAULT_BITRATE);
        // Mask size for IP 3#
        inputCmd.put("maskSize3", "");
        // H323 routing method
        inputCmd.put("h323RoutingMethod", "");
        // H323 routing ip address
        inputCmd.put("h323RoutingIp", "");
        // H323 routing port
        inputCmd.put("h323RoutingPort", "0");
        // SIP routing method
        inputCmd.put("sipRoutingMethod", "");
        // SIP routing ip address
        inputCmd.put("sipRoutingIp", "");
        // SIP routing port
        inputCmd.put("sipRoutingPort", "0");
        inputCmd.put("ip", "");
        inputCmd.put("siteUid", "");
        inputCmd.put("attrName", "");
        inputCmd.put("territoryName", "");
        inputCmd.put("territoryDescription", "");
        inputCmd.put("territoryUid", "");
        inputCmd.put("enableConferenceRoomHost", "");
        inputCmd.put("updateAttrName1", "");
        inputCmd.put("updateAttrValue1", "");
        inputCmd.put("updateAttrName2", "");
        inputCmd.put("updateAttrValue2", "");
        inputCmd.put("updateAttrName3", "");
        inputCmd.put("updateAttrValue3", "");
        inputCmd.put("updateAttrName4", "");
        inputCmd.put("updateAttrValue4", "");
        inputCmd.put("siteLinkUid", "");
        inputCmd.put("siteLinkName", "");
        inputCmd.put("siteLinkDescription", "");
        inputCmd.put("siteLinkBandwidth", "2000000000000");
        inputCmd.put("siteLinkBitrate", "2000000000");
        inputCmd.put("siteLinkSiteUidA", "");
        inputCmd.put("siteLinkSiteUidB", "");
        inputCmd.put("exclusionName", "");
        inputCmd.put("exclusionDescription", "");
        inputCmd.put("exclusionUid", "");
        inputCmd.put("cloudName", "");
        inputCmd.put("cloudDescription", "");
        inputCmd.put("cloudUid", "");
        inputCmd.put("dmaUid", "");
        inputCmd.put("backupDmaUid", "");
    }

    /**
     * Update the cloud
     *
     * @see cloudUid=$cloudUid1 <br/>
     *      siteName=site1,site2
     * @param cloudUid
     *            The cloud uid
     * @param siteName
     *            The new site name list
     * @return The result
     */
    public String updateCloud() {
        String status = "Failed";
        final String cloudUid = inputCmd.get("cloudUid");
        final JSite cloud = stmh.getSite(userToken, cloudUid);
        final List<JCommonUIObject> sites = stmh
                .getListOfSiteForRule(userToken);
        final List<JSiteLink> siteLinks = new ArrayList<JSiteLink>();
        final String[] siteNames = inputCmd.get("siteName").split(",");
        for (final String siteName : siteNames) {
            for (final JCommonUIObject site : sites) {
                if (siteName.equals(site.getDisplayString())) {
                    final JSiteLink siteLink = new JSiteLink();
                    siteLink.setBandwidth(Long.parseLong(DEFAULT_BANDWIDTH));
                    siteLink.setBitrate(Long.parseLong(DEFAULT_BITRATE));
                    siteLink.setName(cloud.getName() + "/" + siteName);
                    siteLink.setSiteUidA(site.getKeyValue());
                    siteLink.setSiteUidB(cloud.getUid());
                    siteLinks.add(siteLink);
                    break;
                }
            }
        }
        final JWebResult result = stmh.updateCloud(userToken, cloud, siteLinks);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Update cloud " + cloud.getName() + " successfully.");
        } else {
            status = "Failed";
            logger.error("Update cloud " + cloud.getName() + " failed.");
            return status + "Update cloud " + cloud.getName() + " failed.";
        }
        return status;
    }

    /**
     * Update the site
     *
     * @see updateAttrName1=CityCode <br/>
     *      updateAttrValue1=021 <br/>
     *      updateAttrName2=LocationName <br/>
     *      updateAttrValue2=Shanghai,~Shanghai,~CN <br/>
     *      updateAttrName3=Longitude <br/>
     *      updateAttrValue3=121.368 <br/>
     *      updateAttrName4=Latitude <br/>
     *      updateAttrValue4=31.109 <br/>
     *      siteUid=$siteUid1
     *
     * @param siteUid
     *            The site uid
     * @param updateAttrName
     *            [1-4] The attribute name
     * @param updateAttrValue
     *            [1-4] The attribute value to update
     * @return The result
     */
    public String updateSite() {
        String status = "Failed";
        final String siteUid = inputCmd.get("siteUid");
        final JSite site = stmh.getSite(userToken, siteUid);
        for (int i = 1; i <= 4; i++) {
            final String attrName = inputCmd.get("updateAttrName" + i);
            final String attrValue = inputCmd.get("updateAttrValue" + i)
                    .replaceAll("~", " ");
            if (attrName.isEmpty()) {
                continue;
            }
            try {
                CommonUtils.invokeSetMethod(site, attrName, attrValue);
            } catch (IllegalAccessException
                     | IllegalArgumentException
                     | InvocationTargetException
                     | InstantiationException e) {
                e.printStackTrace();
                return "Failed, " + e.getMessage();
            }
        }
        final List<JNetwork> networkList = stmh.getNetwork(userToken, siteUid);
        for (int i = 1; i <= 3; i++) {
            final String ipAddress = inputCmd.get("ipaddr" + i);
            if (ipAddress.isEmpty()) {
                continue;
            } else {
                final String maskSize = inputCmd.get("maskSize" + i);
                final String bandwidth = inputCmd.get("bandwidth" + i);
                final String bitRate = inputCmd.get("bitRate" + 1);
                for (final JNetwork ep : networkList) {
                    if (ipAddress.equals(ep.getAddress())) {
                        if (!maskSize.isEmpty()) {
                            ep.setMaskSize(Integer.parseInt(maskSize));
                        } else if (!bandwidth.isEmpty()) {
                            ep.setBandwidth(Long.parseLong(bandwidth));
                        } else if (!bitRate.isEmpty()) {
                            ep.setBitrate(Long.parseLong(bitRate));
                        }
                    }
                }
            }
        }
        final JWebResult result = stmh.updateSite(userToken, site, networkList);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Update site " + site.getName() + " successfully.");
        } else {
            status = "Failed";
            logger.error("Update site " + site.getName()
                    + " failed. Error msg is " + result.getMessages());
            return status + "Update site " + site.getName()
                    + " failed. Error msg is " + result.getMessages();
        }
        return status;
    }

    /**
     * Update the site
     *
     * @see updateAttrName1=description <br/>
     *      updateAttrValue1=auto <br/>
     *      siteLinkName=link1
     *
     * @param siteLinkName
     *            The site link name
     * @param updateAttrName
     *            [1-4] The attribute name
     * @param updateAttrValue
     *            [1-4] The attribute value to update
     * @return The result
     */
    public String updateSiteLink() {
        String status = "Failed";
        final String siteLinkName = inputCmd.get("siteLinkName");
        final JSiteLink siteLink = getSiteLinkByName(siteLinkName);
        if (siteLink == null) {
            return "NotFound, could not find the sitelink with name "
                    + siteLinkName;
        }
        for (int i = 1; i <= 4; i++) {
            final String attrName = inputCmd.get("updateAttrName" + i);
            final String attrValue = inputCmd.get("updateAttrValue" + i)
                    .replaceAll("~", " ");
            if (attrName.isEmpty()) {
                continue;
            }
            try {
                CommonUtils.invokeSetMethod(siteLink, attrName, attrValue);
            } catch (IllegalAccessException
                     | IllegalArgumentException
                     | InvocationTargetException
                     | InstantiationException e) {
                e.printStackTrace();
                return "Failed, " + e.getMessage();
            }
        }
        final JWebResult result = stmh.updateSiteLink(userToken, siteLink);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Update siteLink " + siteLink.getName()
                    + " successfully.");
        } else {
            status = "Failed";
            logger.error("Update siteLink " + siteLink.getName()
                    + " failed. Error msg is " + result.getMessages());
            return status + "Update siteLink " + siteLink.getName()
                    + " failed. Error msg is " + result.getMessages();
        }
        return status;
    }

    /**
     * Update territory
     *
     * @see territoryName=territory1 <br/>
     *      siteName=site2
     * @param territoryName
     *            The territory name
     * @param siteName
     *            The new site name list
     * @return The result
     */
    public String updateTerritory() {
        String status = "Failed";
        final String territoryName = inputCmd.get("territoryName");
        final JTerritory dmaTerritory = getTerritoryTopoData(territoryName);
        final List<JCommonUIObject> sites = stmh
                .getListOfSiteForRule(userToken);
        final List<String> siteIdsInTerritory = new ArrayList<String>();
        for (final JCommonUIObject site : sites) {
            if (inputCmd.get("siteName").contains(site.getDisplayString())) {
                siteIdsInTerritory.add(site.getKeyValue());
            }
        }
        final JWebResult result = stmh
                .updateTerritory(userToken, dmaTerritory, siteIdsInTerritory);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Update Territory " + dmaTerritory.getName()
                    + " successfully.");
        } else {
            status = "Failed";
            logger.error("Update Territory " + dmaTerritory.getName()
                    + " failed.");
            return status + "Update Territory " + dmaTerritory.getName()
                    + " failed.";
        }
        return status;
    }
}
