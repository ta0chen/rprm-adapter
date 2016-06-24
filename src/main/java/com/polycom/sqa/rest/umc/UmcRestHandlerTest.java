package com.polycom.sqa.rest.umc;

import org.junit.BeforeClass;
import org.junit.Test;

public class UmcRestHandlerTest {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void testCreateDma() {
        final String method = "POST ";
        final String url = "https://172.21.120.6:8443/api/rest/rprm/ndm/devices ";
        final String rootElement = "VOServer ";
        final String returnKeyword = " ";
        final StringBuffer params = new StringBuffer();
        params.append("deviceType=").append(47).append(" ");
        params.append("serverBasic:addOperationMode=").append("ADD_BY_IP")
                .append(" ");
        params.append("serverBasic:networkConfig:ipAddress=")
                .append("10.220.202.107").append(" ");
        params.append("serverBasic:networkConfig:port=").append("8443")
                .append(" ");
        params.append("serverBasic:deviceName=").append("DMA").append(" ");
        params.append("serverBasic:mgmtUserName=").append("admin").append(" ");
        params.append("serverBasic:mgmtPassword=").append("admin").append(" ");
        params.append("serverBasic:version=").append("6.3.0").append(" ");
        params.append("flextraLicense:flextraLicenseEnabled=").append("false")
                .append(" ");
        params.append("serviceIntegration(VODMAServiceIntegration):integrated=")
                .append("true").append(" ");
        params.append("serviceIntegration(VODMAServiceIntegration):isCallServer=")
                .append("true").append(" ");
        params.append("serviceIntegration(VODMAServiceIntegration):schedulingCapacity=")
                .append("100").append(" ");
        final long startTime = System.currentTimeMillis();
        final String command = "UMC " + url + method + rootElement
                + returnKeyword + params.toString();
        System.out.println(command);
        final UmcRestHandler handler = UmcRestHandler.getInstance(command);
        final String result = handler.build(command);
        final long endTime = System.currentTimeMillis();
        final long duration = (endTime - startTime) / 1000;
        System.out.println("result for " + method + " is " + result);
        System.out.println("duration " + duration + "s");
    }

    @Test
    public void testCreateImage() {
        final String method = "POST ";
        final String url = "https://10.220.209.189:9443/api/rest/rprm/ndm/instanceImages ";
        final String rootElement = "VOInstanceImage ";
        final String returnKeyword = " ";
        final String params = "name=plcm-rpp-dma-6.3.1-207675-vmdk.ova versionNum=6.3.1-207675 providerType=VMWARE instanceType=47 imageFormatType=OVF imageUrl=http:// ";
        final long startTime = System.currentTimeMillis();
        final String command = "UMC " + url + method + rootElement
                + returnKeyword + params;
        final UmcRestHandler handler = UmcRestHandler.getInstance(command);
        final String result = handler.build(command);
        final long endTime = System.currentTimeMillis();
        final long duration = (endTime - startTime) / 1000;
        System.out.println("result for " + method + " is " + result);
        System.out.println("duration " + duration + "s");
    }

    @Test
    public void testCreateProvider() {
        final String method = "POST ";
        final String url = "https://10.220.209.189:9443/api/rest/rprm/ndm/devices ";
        final String rootElement = "VOProvider ";
        final String returnKeyword = " ";
        final String params = "providerType=VMWARE providerName=wenbo providerUrl=https://172.21.111.178/sdk userName=UMC password=UMC123 resourceGroups={} ";
        final long startTime = System.currentTimeMillis();
        final String command = "UMC " + url + method + rootElement
                + returnKeyword + params;
        final UmcRestHandler handler = UmcRestHandler.getInstance(command);
        final String result = handler.build(command);
        final long endTime = System.currentTimeMillis();
        final long duration = (endTime - startTime) / 1000;
        System.out.println("result for " + method + " is " + result);
        System.out.println("duration " + duration + "s");
    }

    @Test
    public void testDeleteNetworkDevice() {
        final String method = "Delete ";
        final String deviceId = "df832b61-d7f9-45f8-958f-862380d89030";
        final String url = "https://10.220.202.252:8443/api/rest/rprm/ndm/devices/"
                + deviceId + "?deleteFromProvider=false ";
        final String rootElement = "VOServer ";
        final String returnKeyword = " ";
        final String params = " ";
        final long startTime = System.currentTimeMillis();
        final String command = "UMC " + url + method + rootElement
                + returnKeyword + params;
        System.out.println(command);
        final UmcRestHandler handler = UmcRestHandler.getInstance(command);
        final String result = handler.build(command);
        final long endTime = System.currentTimeMillis();
        final long duration = (endTime - startTime) / 1000;
        System.out.println("result for " + method + " is " + result);
        System.out.println("duration " + duration + "s");
    }

    @Test
    public void testGetImages() {
        final String method = "GET ";
        final String url = "https://10.220.209.189:9443/api/rest/rprm/ndm/instanceImages ";
        final String rootElement = "VOInstanceImageCollection ";
        final String returnKeyword = "instanceImageCollection[name=plcm-rpp-dma-6.3.1-207675-vmdk.ova]:id ";
        final String params = " ";
        final long startTime = System.currentTimeMillis();
        final String command = "UMC " + url + method + rootElement
                + returnKeyword + params;
        final UmcRestHandler handler = UmcRestHandler.getInstance(command);
        final String result = handler.build(command);
        final long endTime = System.currentTimeMillis();
        final long duration = (endTime - startTime) / 1000;
        System.out.println("result for " + method + " is " + result);
        System.out.println("duration " + duration + "s");
    }

    @Test
    public void testGetNetworkDevice() {
        final String method = "GET ";
        final String url = "https://172.21.120.181:8443/api/rest/rprm/ndm/devices ";
        final String rootElement = "VOServerCollection ";
        final String returnKeyword = "servers[serverBasic:deviceName=DMA226]:networkDeviceId ";
        final String params = " ";
        final long startTime = System.currentTimeMillis();
        final String command = "UMC " + url + method + rootElement
                + returnKeyword + params;
        System.out.println(command);
        final UmcRestHandler handler = UmcRestHandler.getInstance(command);
        final String result = handler.build(command);
        final long endTime = System.currentTimeMillis();
        final long duration = (endTime - startTime) / 1000;
        System.out.println("result for " + method + " is " + result);
        System.out.println("duration " + duration + "s");
    }

    @Test
    public void testGetProvider() {
        final String method = "GET ";
        final String url = "https://10.220.209.181:9443/api/rest/rprm/ndm/providers ";
        final String rootElement = "VOProviderCollection ";
        final String returnKeyword = "providers[providerName=b]:id ";
        final String params = " ";
        final long startTime = System.currentTimeMillis();
        final String command = "UMC " + url + method + rootElement
                + returnKeyword + params;
        final UmcRestHandler handler = UmcRestHandler.getInstance(command);
        final String result = handler.build(command);
        final long endTime = System.currentTimeMillis();
        final long duration = (endTime - startTime) / 1000;
        System.out.println("result for " + method + " is " + result);
        System.out.println("duration " + duration + "s");
    }

    @Test
    public void testUpdateNetworkDevice() {
        final String method = "PUT ";
        final String deviceId = "d77a6645-778f-4c07-b787-688c5f718f2f";
        final String url = "https://10.220.202.252:8443/api/rest/rprm/ndm/devices/"
                + deviceId + " ";
        final String rootElement = "VOServer ";
        final String returnKeyword = " ";
        final StringBuffer params = new StringBuffer();
        params.append("serviceIntegration:integrated=true").append(" ");
        params.append("serviceIntegration:rpcsServer=RpcsServer").append(" ");
        final long startTime = System.currentTimeMillis();
        final String command = "UMC " + url + method + rootElement
                + returnKeyword + params.toString();
        System.out.println(command);
        final UmcRestHandler handler = UmcRestHandler.getInstance(command);
        final String result = handler.build(command);
        final long endTime = System.currentTimeMillis();
        final long duration = (endTime - startTime) / 1000;
        System.out.println("result for " + method + " is " + result);
        System.out.println("duration " + duration + "s");
    }

    @Test
    public void testUUMCateProvider() {
        final String method = "PUT ";
        final String url = "https://10.220.209.189:9443/api/rest/rprm/ndm/providers/1 ";
        final String rootElement = "VOProvider ";
        final String returnKeyword = " ";
        final String params = "providerName=c userName=UMC password=UMC123 resourceGroups={}";
        final long startTime = System.currentTimeMillis();
        final String command = "UMC " + url + method + rootElement
                + returnKeyword + params;
        final UmcRestHandler handler = UmcRestHandler.getInstance(command);
        final String result = handler.build(command);
        final long endTime = System.currentTimeMillis();
        final long duration = (endTime - startTime) / 1000;
        System.out.println("result for " + method + " is " + result);
        System.out.println("duration " + duration + "s");
    }
}
