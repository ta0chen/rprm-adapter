package com.polycom.sqa.rest.xma;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

public class XmaRestHandlerTest {
    protected static Logger logger = Logger.getLogger("XmaRestHandlerTest");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void applyBaseline() {
        final String name ="UCAPL COMMON SETTINGS";  //COMMERCIAL UCAPL
        final String method = "get ";
        final String url = "https://10.220.202.252:8443/api/rest/rprm/security-option-management/baselines?name-space=security" + " ";
        final String rootElement = "plcm-baseline-list ";
        final String xmlOrJson = "json ";
        final String keyWord = "plcmBaselineList[name=" + name +"]:uuid ";
        final String params = " ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);

        final String method1 = "post ";
        final String url1 = "https://10.220.202.252:8443/api/rest/rprm/security-option-management/baselines/" + result + "/apply ";
        final String rootElement1 = "plcm-baseline ";
        final String xmlOrJson1 = "json ";
        final String params1 = " ";
        final String command1 = "XMA " + url1 + method1 + rootElement1 + xmlOrJson1
                + params1;
        final XmaRestHandler handler1 = XmaRestHandler.getInstance(command1);
        final String result1 = handler1.build(command1);
        logger.info("result==" + result1);
    }
    @Test
    public void changeUserLoginPassword(){
        final String url = "https://10.220.202.252:8443/api/rest/users/password ";
        final String method = "post ";
        final String rootElement = "plcm-change-password-request ";
        final String xmlOrJson = "json ";
        final String params = "old-password=Polycom123 new-password=Polycom124 ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void createAdGroup() {
        final String adGroup = "adgroup1";
        String method = "get ";
        String params = " ";
        final String xmlOrJson = "json ";
        String keyWord = "plcmGroupList[name=" + adGroup + "]:groupUuid ";
        String rootElement = "plcm-group-list ";
        String url = "10.220.202.252:8443/api/rest/groups/enterprise-groups?search-string="
                + adGroup + " ";
        params = " ";
        String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String uuid = handler.build(command);
        logger.info("group uuid==" + uuid);
        keyWord = "plcmGroupList[name=" + adGroup + "]:domain ";
        command = "XMA " + url + method + rootElement + xmlOrJson + keyWord
                + params;
        handler = XmaRestHandler.getInstance(command);
        final String domain = handler.build(command);
        logger.info("group domain==" + domain);
        method = "post ";
        url = "10.220.202.252:8443/api/rest/groups/enterprise-groups ";
        rootElement = "plcm-string-list ";
        params = "plcmStringList={value=" + uuid + "}";
        command = "XMA " + url + method + rootElement + xmlOrJson + params;
        handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void createAdRoom() {
        // read the AD user first to get the uuid
        final String domain = "SQA";
        final String adUser = "adroom6";
        String method = "get ";
        String params = " ";
        final String xmlOrJson = "json ";
        final String keyWord = "uuid ";
        String rootElement = "plcm-user ";
        String url = "10.220.202.252:8443/api/rest/users/" + domain + "/"
                + adUser + " ";
        String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String uuid = handler.build(command);
        logger.info("The uuid is: " + uuid);
        method = "post ";
        url = "10.220.202.252:8443/api/rest/rooms ";
        rootElement = "plcm-room ";
        params = "roomname=adroom6 emailAddress=adroom7@sqa.com description=auto"
                + " siteUuid=0fc03000-fc30-0300-fc30-fc030f00f000"
                + " roomUuid=" + uuid;
        command = "XMA " + url + method + rootElement + xmlOrJson + params;
        handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void createBaseline() {
        final String method = "post ";
        final String url = "10.220.202.6:8443/api/rest/rprm/security-option-management/baselines ";
        final String rootElement = "plcm-baseline ";
        final String bodyType = "json ";
        final String params = "name=baselineTest1 description=baselineDescription isDefault=false";
        final String command = "XMA " + url + method + rootElement + bodyType
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void createLocalGroup() {
        final String method = "post ";
        final String url = "10.220.202.252:8443/api/rest/groups ";
        final String rootElement = "plcm-group ";
        final String bodyType = "json ";
        final String params = "name=auto2 domain=local description=auto2 enterpriseDirectoryViewable=false";
        final String command = "XMA " + url + method + rootElement + bodyType
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void createMachineAccount() {
        final String roomName = "lroom";
        String method = "get ";
        String params = " ";
        final String xmlOrJson = "json ";
        final String keyWord = "plcmRoomList[roomname=" + roomName
                + "]:roomUuid ";
        String rootElement = "plcm-room-list ";
        String url = "172.21.120.6:8443/api/rest/rooms ";
        params = " ";
        String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String roomUuid = handler.build(command);
        logger.info("Room lroom uuid==" + roomUuid);
        method = "post ";
        url = "172.21.120.6:8443/api/rest/machine-accounts ";
        rootElement = "plcm-machine-account ";
        params = "machineAccountName=lmma description=haha password=Polycom123 disabled=false locked=false roomUuid="
                + roomUuid;
        command = "XMA " + url + method + rootElement + xmlOrJson + params;
        handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void createMachineAccountAutoRoomCreation() {
        String method = "get ";
        String params = " ";
        final String xmlOrJson = "json ";
        method = "post ";
        final String url = "172.21.120.6:8443/api/rest/machine-accounts ";
        final String rootElement = "plcm-machine-account ";
        params = "machineAccountName=autoMa description=hoho password=Polycom123 disabled=false locked=false";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void createRoom() {
        final String method = "post ";
        final String xmlOrJson = "json ";
        final String url = "172.21.120.150:8443/api/rest/rooms ";
        final String rootElement = "plcm-room ";
        final String params = "roomname=localroom1 emailAddress=localroom1@163.com description=LocalRoomDescription siteUuid=0fc03000-fc30-0300-fc30-fc030f00f000";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void createUser() {
        final String method = "post ";
        final String url = "10.220.202.228:8443/api/rest/users ";
        final String rootElement = "plcm-user ";
        final String xmlOrJson = "json ";
        final String params = "username=autotest firstName=auto lastName=test emailAddress=autotest@p.com title=boss department=sqa city=bj phoneNumber=223344 password=Polycom123 plcmUserRoleList={name=Administrator}";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void deleteMachineAccount() {
        final String machineAccount = "lmma";
        String method = "get ";
        String params = " ";
        final String xmlOrJson = "json ";
        final String keyWord = "plcmMachineAccountList[machineAccountName="
                + machineAccount + "]:machineAccountUuid ";
        String rootElement = "plcm-machine-account-list ";
        String url = "172.21.120.6:8443/api/rest/machine-accounts ";
        params = " ";
        String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String maUuid = handler.build(command);
        logger.info("machind account lmma uuid==" + maUuid);
        method = "delete ";
        url = "172.21.120.6:8443/api/rest/machine-accounts/" + maUuid + " ";
        rootElement = "plcm-machine-account ";
        params = "";
        command = "XMA " + url + method + rootElement + xmlOrJson + params;
        handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void deleteRoom() {
        // Get the room you want to delete
        final String roomName = "lroom";
        String method = "get ";
        String params = " ";
        final String xmlOrJson = "json ";
        final String keyWord = "plcmRoomList[roomname=" + roomName
                + "]:roomUuid ";
        String rootElement = "plcm-room-list ";
        String url = "172.21.120.6:8443/api/rest/rooms ";
        String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String uuid = handler.build(command);
        logger.info("The uuid is: " + uuid);
        method = "delete ";
        url = "172.21.120.6:8443/api/rest/rooms/" + uuid + " ";
        rootElement = "plcm-room ";
        params = " ";
        command = "XMA " + url + method + rootElement + xmlOrJson + params;
        handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void deleteSpecifiedDialString() {
        final String method = "delete ";
        final String dialStringID = "340";
        final String dialStringType = "H323";
        final String url = "10.220.202.198:8443/api/rest/dial-string-reservations/"
                + dialStringID + "/" + dialStringType + " ";
        final String rootElement = "plcm-dial-string-reservation ";
        final String xmlOrJson = "xml ";
        final String params = " ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void deleteUser() {
        final String method = "delete ";
        final String domain = "local";
        final String userName = "autotest";
        final String url = "10.220.202.198:8443/api/rest/users/" + domain + "/"
                + userName + " ";
        final String rootElement = "plcm-user ";
        final String xmlOrJson = "json ";
        final String params = " ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void getRoom() {
        final String method = "get ";
        final String url = "10.220.202.228:8443/api/rest/rooms ";
        final String rootElement = "plcm-room ";
        final String params = " ";
        final String command = "XMA " + url + method + rootElement + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void getSecurityBaseline() {
        final String method = "get ";
        final String url = "https://10.220.202.252:8443/api/rest/rprm/security-option-management/baselines?name-space=security ";
        final String rootElement = "plcm-baseline-list ";
        final String xmlOrJson = "json ";
        final String params = "plcmBaselineList[name=UCAPL~COMMON~SETTINGS]:uuid ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void getSecurityBaselineProfile() {
        final String method = "get ";
        final String url = "https://10.220.202.252:8443/api/rest/rprm/common-setting/profiles/7ab4ccee-ea95-48a4-9d25-48a9daacd8d0?name-space=security ";
        final String rootElement = "plcm-common-setting-profile ";
        final String xmlOrJson = "json ";
        final String params = "attributes[key=password.conference.pin.minLength]:value ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void getSecuritySetting() {
        final String method = "get ";
        final String url = "https://10.220.202.252:8443/api/rest/rprm/common-setting/current-setting?name-space=security&group-name=securecommunication.group ";
        final String rootElement = "plcm-map-item-list ";
        final String xmlOrJson = "json ";
        final String params = "plcmMapItemList[key=ldap.server.enforceSecure]:value ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void getSpecifiedDialString() {
        final String method = "get ";
        final String dialStringID = "340";
        final String dialStringType = "H323";
        final String url = "10.220.202.198:8443/api/rest/dial-string-reservations/"
                + dialStringID + "/" + dialStringType + " ";
        final String rootElement = "plcm-dial-string-reservation ";
        final String xmlOrJson = "xml ";
        final String params = " ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void getSpecifiedInfoForDevice() {
        final String method = "get ";
        final String ip = "1.1.1.1";
        final String url = "10.220.202.252:8443/api/rest/devices?ip=1.1.1.1 ";
        final String rootElement = "plcm-device-list-v3 ";
        final String xmlOrJson = "json ";
        final String keyWord = "plcmDeviceV3List[ipAddress=" + ip
                + "]:deviceIdentifier ";
        final String params = " ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    public void getSystemHardening() {
        final String method = "get ";
        final String url = "https://172.21.120.181:8443/api/rest/rprm/common-setting/current-setting?name-space=security&group-name=system.group ";
        final String rootElement = "plcm-map-item-list ";
        final String xmlOrJson = "json ";
        final String params = "plcmMapItemList[key=system.allowPing]:value ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void getUser() {
        final String method = "get ";
        final String domain = "local";
        final String userName = "debuguser99";
        final String url = "172.21.120.181:8443/api/rest/users/" + domain + "/"
                + userName + " ";
        final String rootElement = "plcm-user ";
        final String xmlOrJson = "json ";
        final String keyWord = "uuid ";
        final String params = " ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void getXMASystemTime() {
        final String method = "get ";
        final String url = "https://10.220.202.228:8443/api/rest/config/time-config ";
        final String rootElement = "plcm-time-config ";
        final String xmlOrJson = "xml ";
        final String keyWord = "currentTime ";
        final String params = " ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void h323DialAliasCreate() {
        final String roomName = "autotestRoom";
        String method = "get ";
        String params = " ";
        final String xmlOrJson = "json ";
        final String keyWord = "plcmRoomList[roomname=" + roomName
                + "]:roomUuid ";
        String rootElement = "plcm-room-list ";
        String url = "10.220.202.252:8443/api/rest/rooms ";
        String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String ownerUuid = handler.build(command);
        logger.info("The room uuid is: " + ownerUuid);
        method = "post ";
        url = "10.220.202.252:8443/api/rest/dial-alias/h323-alias ";
        rootElement = "plcm-h323-alias ";
        params = "deviceTypeId=30 e164Number=1212 h323Alias=room323Alias_3_1 aliasRole=Master aliasGroupUuid=505f2b82-8b2a-8009-7cf0-52597a9d9cbe ownerUuid="
                + ownerUuid;
        command = "XMA " + url + method + rootElement + xmlOrJson + params;
        handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void h323DialAliasDelete() {
        final String roomName = "localRoom";
        String method = "get ";
        String params = " ";
        final String xmlOrJson = "json ";
        String keyWord = "plcmRoomList[roomname=" + roomName + "]:roomUuid ";
        String rootElement = "plcm-room-list ";
        String url = "10.220.202.252:8443/api/rest/rooms ";
        String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String ownerUuid = handler.build(command);
        logger.info("The room uuid is: " + ownerUuid);
        url = "10.220.202.252:8443/api/rest/dial-alias/h323-alias?owner-uuid="
                + ownerUuid + " ";
        rootElement = "plcm-h323-alias-list ";
        keyWord = "plcmH323AliasList[h323Alias=afjakfkaf]:aliasUuid ";
        command = "XMA " + url + method + rootElement + xmlOrJson + keyWord
                + params;
        handler = XmaRestHandler.getInstance(command);
        final String aliasUuid = handler.build(command);
        logger.info("The alias uuid is: " + aliasUuid);
        method = "delete ";
        url = "10.220.202.252:8443/api/rest/dial-alias/h323-alias/" + aliasUuid
                + " ";
        rootElement = "plcm-h323-alias ";
        params = "";
        command = "XMA " + url + method + rootElement + xmlOrJson + params;
        handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void reserveDialString() {
        final String method2 = "post ";
        final String method1 = "get ";
        final String domain = "local";
        final String userName = "autotest";
        final String xmlOrJson = "xml ";
        final String params1 = " ";
        final String url1 = "10.220.202.198:8443/api/rest/users/" + domain + "/"
                + userName + " ";
        final String rootElement1 = "plcm-user ";
        final String keyWord = "atomLinkList:href ";
        final String url2 = "10.220.202.198:8443/api/rest/dial-string-reservations ";
        String href = "";
        final String command = "XMA " + url1 + method1 + rootElement1
                + xmlOrJson + keyWord + params1;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        href = handler.build(command);
        logger.info("The href is: " + href);
        final String rootElement2 = "plcm-dial-string-reservation ";
        final String params2 = "dialStringTypeEnum=H323 deviceType=HDX h323Id=bbbHDX e164Number=334455 atomLinkList={href="
                + href + ","
                + "rel=urn:com:polycom:api:rest:link-relations:belongs-to-user"
                + "," + "type=application/vnd.plcm.plcm-user+"
                + xmlOrJson.trim() + "}";
        final String command2 = "XMA " + url2 + method2 + rootElement2
                + xmlOrJson + params2;
        final String result = handler.build(command2);
        logger.info("result==" + result);
    }

    @Test
    public void reserveDirectMCUConference() {
        final String method = "post ";
        final String xmlOrJson = "xml ";
        final String rootElement = "plcm-reservation ";
        final String url = "10.220.202.219:8443/api/rest/reservations ";
        final String params = "plcmReservedParticipantList={username=debuguser,domain=LOCAL,participantName=debuguser~aaa,connectionTypeEnum=H323,dialDirectionEnum=DIAL_IN,dialNumber=172.21.126.193,chairperson=true,confOwner=true,emailAddress=debuguser@123.com,userTypeEnum=USER,modeEnum=VIDEO,bitRateEnum=RATE384,deviceId=327}"
                + "{username=debuguser2,domain=LOCAL,participantName=debuguser2~bbb,connectionTypeEnum=H323,dialDirectionEnum=DIAL_OUT,dialNumber=172.21.126.253,chairperson=false,confOwner=false,emailAddress=debuguserw@123.com,userTypeEnum=USER,modeEnum=VIDEO,bitRateEnum=RATE384,deviceId=63} "
                + "name=hahaConf  confPasscode=4321 chairPasscode=1234 templateName=Default~Template bridgeSelectionTypeEnum=SINGLEBRIDGE scheduledMcuIp=172.21.104.229 conferenceId=2323 "
                + "supportedLanguageEnum=ENGLISH scheduleConferenceTypeEnum=API_CMA_CONF sendEmail=false "
                + "startTime=2015-06-02T11:25:53.000+08:00 "
                + "endTime=2015-06-02T12:35:53.000+08:00 ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void reserveDirectMCUDailyRecurringConference() {
        final String method = "post ";
        final String xmlOrJson = "xml ";
        final String rootElement = "plcm-reservation ";
        final String url = "10.220.202.219:8443/api/rest/reservations ";
        final String params = "plcmReservedParticipantList={username=debuguser,domain=LOCAL,participantName=debuguser~aaa,connectionTypeEnum=H323,dialDirectionEnum=DIAL_IN,dialNumber=172.21.126.193,chairperson=true,confOwner=true,emailAddress=debuguser@123.com,userTypeEnum=USER,modeEnum=VIDEO,bitRateEnum=RATE384,deviceId=327}"
                + "{username=debuguser2,domain=LOCAL,participantName=debuguser2~bbb,connectionTypeEnum=H323,dialDirectionEnum=DIAL_OUT,dialNumber=172.21.126.253,chairperson=false,confOwner=false,emailAddress=debuguserw@123.com,userTypeEnum=USER,modeEnum=VIDEO,bitRateEnum=RATE384,deviceId=63} "
                + "name=hahaConf  confPasscode=4321 chairPasscode=1234 templateName=Default~Template bridgeSelectionTypeEnum=SINGLEBRIDGE scheduledMcuIp=172.21.104.229 conferenceId=2323 "
                + "supportedLanguageEnum=ENGLISH scheduleConferenceTypeEnum=API_CMA_CONF sendEmail=false conferenceReserveTypeEnum=RECURRING "
                + "startTime=2015-06-02T14:00:00.000+08:00 "
                + "endTime=2015-06-02T15:00:00.000+08:00 "
                + "plcmSchedRecurrence:schedRecurrenceTypeEnum=DAILY plcmSchedRecurrence:duration=60 plcmSchedRecurrence:endTimeTypeEnum=END_AFTER_OCCURENCES plcmSchedRecurrence:occurrences=3 plcmSchedRecurrence:recurStartTime=2015-06-02T14:00:00.000+08:00 plcmSchedRecurrence:recurEndTime=2015-06-07T11:30:00.000+08:00 "
                + "plcmSchedRecurrence:plcmRecurDaily:dailyTypeEnum=EVERY_N_DAY plcmSchedRecurrence:plcmRecurDaily:interval=1 ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void reserveDirectMCUMonthlyRecurringConference() {
        final String method = "post ";
        final String xmlOrJson = "xml ";
        final String rootElement = "plcm-reservation ";
        final String url = "10.220.202.219:8443/api/rest/reservations ";
        final String params = "plcmReservedParticipantList={username=debuguser,domain=LOCAL,participantName=debuguser~aaa,connectionTypeEnum=H323,dialDirectionEnum=DIAL_IN,dialNumber=172.21.126.193,chairperson=true,confOwner=true,emailAddress=debuguser@123.com,userTypeEnum=USER,modeEnum=VIDEO,bitRateEnum=RATE384,deviceId=327}"
                + "{username=debuguser2,domain=LOCAL,participantName=debuguser2~bbb,connectionTypeEnum=H323,dialDirectionEnum=DIAL_OUT,dialNumber=172.21.126.253,chairperson=false,confOwner=false,emailAddress=debuguserw@123.com,userTypeEnum=USER,modeEnum=VIDEO,bitRateEnum=RATE384,deviceId=63} "
                + "name=hahaConf  confPasscode=4321 chairPasscode=1234 templateName=Default~Template bridgeSelectionTypeEnum=SINGLEBRIDGE scheduledMcuIp=172.21.104.229 conferenceId=2323 "
                + "supportedLanguageEnum=ENGLISH scheduleConferenceTypeEnum=API_CMA_CONF sendEmail=false conferenceReserveTypeEnum=RECURRING "
                + "startTime=2015-06-02T14:20:00.000+08:00 "
                + "endTime=2015-06-02T15:15:00.000+08:00 "
                + "plcmSchedRecurrence:schedRecurrenceTypeEnum=MONTHLY plcmSchedRecurrence:duration=60 plcmSchedRecurrence:endTimeTypeEnum=END_AFTER_OCCURENCES plcmSchedRecurrence:occurrences=3 plcmSchedRecurrence:recurStartTime=2015-06-02T14:20:00.000+08:00 plcmSchedRecurrence:recurEndTime=2015-10-07T11:30:00.000+08:00 "
                + "plcmSchedRecurrence:plcmRecurMonthly:day=2 plcmSchedRecurrence:plcmRecurMonthly:interval=1 ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void reserveDirectMCUWeeklyRecurringConference() {
        final String method = "post ";
        final String xmlOrJson = "xml ";
        final String rootElement = "plcm-reservation ";
        final String url = "10.220.202.219:8443/api/rest/reservations ";
        final String params = "plcmReservedParticipantList={username=debuguser,domain=LOCAL,participantName=debuguser~aaa,connectionTypeEnum=H323,dialDirectionEnum=DIAL_IN,dialNumber=172.21.126.193,chairperson=true,confOwner=true,emailAddress=debuguser@123.com,userTypeEnum=USER,modeEnum=VIDEO,bitRateEnum=RATE384,deviceId=327}"
                + "{username=debuguser2,domain=LOCAL,participantName=debuguser2~bbb,connectionTypeEnum=H323,dialDirectionEnum=DIAL_OUT,dialNumber=172.21.126.253,chairperson=false,confOwner=false,emailAddress=debuguserw@123.com,userTypeEnum=USER,modeEnum=VIDEO,bitRateEnum=RATE384,deviceId=63} "
                + "name=hahaConf  confPasscode=4321 chairPasscode=1234 templateName=Default~Template bridgeSelectionTypeEnum=SINGLEBRIDGE scheduledMcuIp=172.21.104.229 conferenceId=2323 "
                + "supportedLanguageEnum=ENGLISH scheduleConferenceTypeEnum=API_CMA_CONF sendEmail=false conferenceReserveTypeEnum=RECURRING "
                + "startTime=2015-06-02T14:15:00.000+08:00 "
                + "endTime=2015-06-02T15:15:00.000+08:00 "
                + "plcmSchedRecurrence:schedRecurrenceTypeEnum=WEEKLY plcmSchedRecurrence:duration=60 plcmSchedRecurrence:endTimeTypeEnum=END_AFTER_OCCURENCES plcmSchedRecurrence:occurrences=3 plcmSchedRecurrence:recurStartTime=2015-06-02T14:15:00.000+08:00 plcmSchedRecurrence:recurEndTime=2015-09-07T11:30:00.000+08:00 "
                + "plcmSchedRecurrence:plcmRecurWeekly:dayOfWeekMask=0010000 plcmSchedRecurrence:plcmRecurWeekly:interval=1 ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void reserveDMAPooledConference() {
        final String method = "post ";
        final String xmlOrJson = "xml ";
        final String rootElement = "plcm-reservation ";
        final String url = "10.220.202.219:8443/api/rest/reservations ";
        final String params = "plcmReservedParticipantList={username=debuguser,domain=LOCAL,participantName=debuguser~aaa,connectionTypeEnum=H323,dialDirectionEnum=DIAL_IN,dialNumber=172.21.126.193,chairperson=false,confOwner=true,emailAddress=debuguser@123.com,userTypeEnum=USER,modeEnum=VIDEO,bitRateEnum=RATE384,deviceId=327}"
                + "{username=debuguser2,domain=LOCAL,participantName=debuguser2~bbb,connectionTypeEnum=H323,dialDirectionEnum=DIAL_OUT,dialNumber=172.21.126.253,chairperson=false,confOwner=false,emailAddress=debuguserw@123.com,userTypeEnum=USER,modeEnum=VIDEO,bitRateEnum=RATE384,deviceId=63} "
                + "name=hahaConf templateName=Factory~Template vmrRoomId=1212 "
                + "supportedLanguageEnum=ENGLISH sendEmail=false "
                + "startTime=2015-06-02T14:31:00.000+08:00 "
                + "endTime=2015-06-02T15:31:00.000+08:00 ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void sipDialAliasCreate() {
        final String roomName = "localRoom";
        String method = "get ";
        String params = " ";
        final String xmlOrJson = "json ";
        final String keyWord = "plcmRoomList[roomname=" + roomName
                + "]:roomUuid ";
        String rootElement = "plcm-room-list ";
        String url = "10.220.202.252:8443/api/rest/rooms ";
        String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String ownerUuid = handler.build(command);
        logger.info("The room uuid is: " + ownerUuid);
        method = "post ";
        url = "10.220.202.252:8443/api/rest/dial-alias/sip-alias ";
        rootElement = "plcm-sip-alias ";
        params = "deviceTypeId=24 sipUri=hhhh@p.com ownerUuid=" + ownerUuid;
        command = "XMA " + url + method + rootElement + xmlOrJson + params;
        handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void sipDialAliasDelete() {
        final String roomName = "localRoom";
        String method = "get ";
        String params = " ";
        final String xmlOrJson = "json ";
        String keyWord = "plcmRoomList[roomname=" + roomName + "]:roomUuid ";
        String rootElement = "plcm-room-list ";
        String url = "10.220.202.252:8443/api/rest/rooms ";
        String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String ownerUuid = handler.build(command);
        logger.info("The room uuid is: " + ownerUuid);
        url = "10.220.202.252:8443/api/rest/dial-alias/sip-alias?owner-uuid="
                + ownerUuid + " ";
        rootElement = "plcm-sip-alias-list ";
        keyWord = "plcmSipAliasList[sipUri=hhhh@p.com]:aliasUuid ";
        command = "XMA " + url + method + rootElement + xmlOrJson + keyWord
                + params;
        handler = XmaRestHandler.getInstance(command);
        final String aliasUuid = handler.build(command);
        logger.info("The alias uuid is: " + aliasUuid);
        method = "delete ";
        url = "10.220.202.252:8443/api/rest/dial-alias/sip-alias/" + aliasUuid
                + " ";
        rootElement = "plcm-sip-alias ";
        params = "";
        command = "XMA " + url + method + rootElement + xmlOrJson + params;
        handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void updateGroupForMember() {
        final String adGroup = "group1";
        final String searchString = "debuguser";
        String method = "get ";
        String params = " ";
        final String xmlOrJson = "json ";
        String keyWord = "plcmGroupList[name=" + adGroup + "]:groupUuid ";
        String rootElement = "plcm-group-list ";
        String url = "10.220.202.6:8443/api/rest/groups ";
        params = " ";
        String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String uuid = handler.build(command);
        logger.info("group uuid==" + uuid);
        // search the available group member
        url = "10.220.202.6:8443/api/rest/groups/"
                + uuid
                + "/group-members/available-members?type=group-uuid&search-string="
                + searchString + " ";
        rootElement = "plcm-group-member-detail ";
        keyWord = "plcmUserList[username=" + searchString + "]:uuid ";
        command = "XMA " + url + method + rootElement + xmlOrJson + keyWord
                + params;
        handler = XmaRestHandler.getInstance(command);
        final String usrUid = handler.build(command);
        logger.info("user uuid==" + usrUid);
        method = "put ";
        url = "10.220.202.6:8443/api/rest/groups/" + uuid
                + "/group-members?type=group-uuid ";
        rootElement = "plcm-group-member ";
        params = "plcmUserUuidList:plcmStringList=replaceWith{value="
                + usrUid
                + "}{value=a714a622-a3e0-4ab7-be03-bc5e81e41b4f} plcmGroupUuidList:plcmStringList=replaceWith{value=12cce874-f9e0-4fa3-9002-fd52d3050f18}";
        // params =
        // "plcmGroupUuidList:plcmStringList=replaceWith{value=12cce874-f9e0-4fa3-9002-fd52d3050f18}";
        command = "XMA " + url + method + rootElement + xmlOrJson + params;
        handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void updatePasswordRequirement() {
        final String method = "put ";
        final String url = "https://172.21.120.181:8443/api/rest/rprm/common-setting/current-setting?name-space=security&group-name=password.group ";
        final String rootElement = "plcm-map-item-list ";
        final String xmlOrJson = "json ";
        final String params = "plcmMapItemList={key=password.rule.minLength,value=8} ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void updateSecuritySetting() {
        final String method = "put ";
        final String url = "https://172.21.120.181:8443/api/rest/rprm/common-setting/current-setting?name-space=security&group-name=system.group ";
        final String rootElement = "plcm-map-item-list ";
        final String xmlOrJson = "json ";
        final String params = "plcmMapItemList={key=system.allowPing,value=false} ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void updateSessionManagement() {
        final String method = "put ";
        final String url = "https://172.21.120.181:8443/api/rest/rprm/common-setting/current-setting?name-space=security&group-name=session.group ";
        final String rootElement = "plcm-map-item-list ";
        final String xmlOrJson = "json ";
        final String params = "plcmMapItemList={key=session.login.enableBanner,value=false} ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }

    @Test
    public void updateSystemHardening() {
        final String method = "put ";
        final String url = "https://172.21.120.181:8443/api/rest/rprm/common-setting/current-setting?name-space=security&group-name=system.group ";
        final String rootElement = "plcm-map-item-list ";
        final String xmlOrJson = "json ";
        final String params = "plcmMapItemList={key=system.allowPing,value=false} ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
    }
    @Test
    public void updateUser() {
        final String method = "get ";
        final String domain = "local";
        final String userName = "debuguser99";
        final String url = "172.21.120.181:8443/api/rest/users/" + domain + "/"
                + userName + " ";
        final String rootElement = "plcm-user ";
        final String xmlOrJson = "json ";
        final String keyWord = "uuid ";
        final String params = " ";
        final String command = "XMA " + url + method + rootElement + xmlOrJson
                + keyWord + params;
        final XmaRestHandler handler = XmaRestHandler.getInstance(command);
        final String result = handler.build(command);
        logger.info("result==" + result);
        final String methodP = "put ";
        final String urlP = "172.21.120.181:8443/api/rest/users/" + result
                + " ";
        final String rootElementP = "plcm-user ";
        final String xmlOrJsonP = "json ";
        // final String keyWordP = " ";
        final String paramsP = "locked=false ";
        final String commandP = "XMA " + urlP + methodP + rootElementP
                + xmlOrJsonP + paramsP;
        final XmaRestHandler handlerP = XmaRestHandler.getInstance(commandP);
        final String resultP = handlerP.build(commandP);
        logger.info("result==" + resultP);
    }
}
