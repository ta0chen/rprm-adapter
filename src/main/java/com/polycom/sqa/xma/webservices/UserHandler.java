package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.DeviceManagerHandler;
import com.polycom.sqa.xma.webservices.driver.DeviceMetaManagerHandler;
import com.polycom.sqa.xma.webservices.driver.RoleManagerHandler;
import com.polycom.sqa.xma.webservices.driver.SiteTopoManagerHandler;
import com.polycom.webservices.DeviceManager.JEndpointForList;
import com.polycom.webservices.RoleManager.JPolicyAttribute;
import com.polycom.webservices.RoleManager.JRole;
import com.polycom.webservices.SiteTopoManager.JNamePlusUid;
import com.polycom.webservices.UserManager.H323Alias;
import com.polycom.webservices.UserManager.JConfGuest;
import com.polycom.webservices.UserManager.JDeviceAlias;
import com.polycom.webservices.UserManager.JDeviceAliasType;
import com.polycom.webservices.UserManager.JDeviceAssociation;
import com.polycom.webservices.UserManager.JDeviceTypeVO;
import com.polycom.webservices.UserManager.JEnterpriseType;
import com.polycom.webservices.UserManager.JGroup;
import com.polycom.webservices.UserManager.JGroupType;
import com.polycom.webservices.UserManager.JRoom;
import com.polycom.webservices.UserManager.JSchedPartMode;
import com.polycom.webservices.UserManager.JStatus;
import com.polycom.webservices.UserManager.JUIUtcDateTime;
import com.polycom.webservices.UserManager.JUser;
import com.polycom.webservices.UserManager.JUserLoginStatus;
import com.polycom.webservices.UserManager.JUserType;
import com.polycom.webservices.UserManager.JWebResult;
import com.polycom.webservices.UserManager.SipAlias;

import sun.misc.BASE64Encoder;

/**
 * User handler. This class will handle the webservice request of User module
 *
 * @author wbchao
 *
 */
public class UserHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "updateRoom ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "roomName=localroom roomEnterpriseType=local
        // associateEndpointIP1=172.21.126.76 associateEndpointType1=HDX ";
        // final String method = "addGuest ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // " firstName=guestTest lastName=auto email=guestTest@123.com
        // location=bj dialInOrNot=false joinMode=video isH323=true isSIP=false
        // isISDN=false aliasType=IP aliasValue=5.5.5.5 ";
        // final String method = "updateGuest ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "firstName=guestTest lastName=h323 email=guestTesth323@123.com
        // location=bj newfirstName=guestEdit newlastName=test
        // newemail=guestEdit@123.com newlocation=sh dialInOrNot=true
        // joinMode=audio isSIP=true sipUri=guestTest@sip.com ";
        // final String method = "updateRoom ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "deviceType=HDX roomName=schemelocalroom1 roomEnterpriseType=local
        // newE164Num4H323DialString=4333
        // newH323Alias4H323DialString=testendpoint3H323ID
        // newSipUri4SIPDialString=testendpoint3sipuri ";
        // final String method = "deleteUserDialString ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "searchString=schemeaduser1 userSearchType=AD ";
        final String method = "deleteGuest ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "firstName=h323ip lastName=guest email=h323ip@123.com location=beijing ";
        final String command = "https://172.21.120.181:8443/PlcmRmWeb/rest/JUserManager UserManager "
                + method + auth + params;
        final UserHandler handler = new UserHandler(command);
        final String result = handler.build();
        System.out.println("result==" + result);
    }

    public UserHandler(final String cmd) throws IOException {
        super(cmd);
    }

    /**
     * Add guest
     *
     * @see firstName=CiscoH323 <br/>
     *      lastName=CiscoGuest$id <br/>
     *      email=CiscoGuest$id@123.com <br/>
     *      location=beijing <br/>
     *      dialInOrNot=false <br/>
     *      joinMode=video <br/>
     *      isH323=true <br/>
     *      isSIP=false <br/>
     *      isISDN=false <br/>
     *      aliasType=E164 <br/>
     *      aliasValue=10000$id
     *
     * @param firstName
     *            The first name of guest
     * @param lastName
     *            The last name of guest
     * @param location
     *            The location of guest
     * @param email
     *            The email of guest
     * @param joinMode
     *            Video or Audio
     * @param isH323
     *            Whether support H323
     * @param isSIP
     *            Whether support sip
     * @param isISDN
     *            Whether support ISDN
     * @param aliasType
     *            The alias type
     * @param aliasValue
     *            The alias value
     * @return The result
     */
    public String addGuest() {
        String status = "Failed";
        String errorMsg = "";
        final JConfGuest newGuest = new JConfGuest();
        final String firstName = inputCmd.get("firstName");
        if (!firstName.isEmpty()) {
            newGuest.setFirstName(firstName);
        } else {
            errorMsg = status + ", the first must be exist!";
            logger.error(errorMsg);
            return errorMsg;
        }
        final String lastName = inputCmd.get("lastName");
        if (!lastName.isEmpty()) {
            newGuest.setLastName(lastName);
        }
        final String email = inputCmd.get("email");
        if (!email.isEmpty()) {
            newGuest.setEmail(email);
        } else {
            errorMsg = status + ", the email must be exist!";
            logger.error(errorMsg);
            return errorMsg;
        }
        final String location = inputCmd.get("location");
        if (!location.isEmpty()) {
            newGuest.setLocation(location);
            newGuest.setCity(location);
        } else {
            errorMsg = status + ", the location must be exist!";
            logger.error(errorMsg);
            return errorMsg;
        }
        final String dialInOrNot = inputCmd.get("dialInOrNot");
        if (!dialInOrNot.isEmpty()) {
            if (dialInOrNot.equalsIgnoreCase("true")) {
                newGuest.setDialIn(true);
            } else {
                newGuest.setDialIn(false);
            }
        } else {
            errorMsg = status + ", the dialInOrNot must be exist!";
            logger.error(errorMsg);
            return errorMsg;
        }
        final String joinMode = inputCmd.get("joinMode");
        if (!joinMode.isEmpty()) {
            newGuest.setJoinMode(JSchedPartMode
                    .fromValue(joinMode.toUpperCase()));
        } else {
            errorMsg = status + ", the joinMode must be exist!";
            logger.error(errorMsg);
            return errorMsg;
        }
        final String isH323 = inputCmd.get("isH323");
        if (!isH323.isEmpty()) {
            if (isH323.equalsIgnoreCase("true")) {
                newGuest.setSupportsH323(true);
            } else {
                newGuest.setSupportsH323(false);
            }
        }
        final String isSip = inputCmd.get("isSIP");
        if (!isSip.isEmpty()) {
            if (isSip.equalsIgnoreCase("true")) {
                newGuest.setSupportsSip(true);
            } else {
                newGuest.setSupportsSip(false);
            }
        }
        final String isIsdn = inputCmd.get("isISDN");
        if (!isIsdn.isEmpty()) {
            if (isIsdn.equalsIgnoreCase("true")) {
                newGuest.setSupportsIsdn(true);
            } else {
                newGuest.setSupportsIsdn(false);
            }
        }
        final String sipUri = inputCmd.get("sipUri");
        if (!sipUri.isEmpty()) {
            newGuest.setSipUri(sipUri);
        }
        final String countryCode = inputCmd.get("countryCode");
        if (!countryCode.isEmpty()) {
            newGuest.setCountryCode(countryCode);
        }
        final String areaCode = inputCmd.get("areaCode");
        if (!areaCode.isEmpty()) {
            newGuest.setAreaCode(areaCode);
        }
        final String isdnNum = inputCmd.get("isdnNum");
        if (!isdnNum.isEmpty()) {
            newGuest.setIsdnNumber(isdnNum);
        }
        final String extenIP = inputCmd.get("extenNum");
        if (!extenIP.isEmpty()) {
            newGuest.setExtensionIP(extenIP);
        }
        final String extenSIP = inputCmd.get("extenSIP");
        if (!extenSIP.isEmpty()) {
            newGuest.setExtensionSIP(extenSIP);
        }
        final String extenISDN = inputCmd.get("extenISDN");
        if (!extenISDN.isEmpty()) {
            newGuest.setExtensionISDN(extenISDN);
        }
        final String useModDialNum = inputCmd.get("useModDialNum");
        if (!useModDialNum.isEmpty()) {
            if (useModDialNum.equalsIgnoreCase("true")) {
                newGuest.setModifiedDialNumber(true);
            } else {
                newGuest.setModifiedDialNumber(false);
            }
        }
        final String modDialNum = inputCmd.get("modDialNum");
        if (!modDialNum.isEmpty()) {
            newGuest.setModDialNumber(modDialNum);
        }
        final JDeviceAlias alias = new JDeviceAlias();
        alias.setDbKey(0);
        alias.setDeviceEntityId(0);
        final String aliasType = inputCmd.get("aliasType");
        final String aliasValue = inputCmd.get("aliasValue");
        final String extenH323 = inputCmd.get("extenH323");
        if (!aliasType.isEmpty()) {
            if ("ip".equalsIgnoreCase(aliasType)) {
                alias.setType(JDeviceAliasType.TRANSPORT_ADDRESS);
                newGuest.setIpAddr(aliasValue);
                newGuest.setExtensionIP(extenH323);
            } else if ("h323".equalsIgnoreCase(aliasType)) {
                alias.setType(JDeviceAliasType.H_323);
            } else if ("e164".equalsIgnoreCase(aliasType)) {
                alias.setType(JDeviceAliasType.E_164);
            } else if ("annex-o".equalsIgnoreCase(aliasType)) {
                alias.setType(JDeviceAliasType.URL);
            } else {
                logger.error("Input alias type error, use the default value which is IP.");
                alias.setType(JDeviceAliasType.TRANSPORT_ADDRESS);
            }
        }
        if (!aliasValue.isEmpty()) {
            alias.setValue(aliasValue);
        }
        if (!extenH323.isEmpty()) {
            alias.setExtension(extenH323);
        }
        newGuest.setAlias(alias);
        newGuest.setRequiredMcuServiceIP("##ANYMCUSERVICE##");
        newGuest.setRequiredMcuServiceISDN("##ANYMCUSERVICE##");
        final JUIUtcDateTime utcTime = new JUIUtcDateTime();
        utcTime.setUnixTime(System.currentTimeMillis());
        newGuest.setLastUpdateTimestamp(utcTime);
        final List<Field> fields = CommonUtils.getDeclaredFields(newGuest);
        for (final Field field : fields) {
            Object val = null;
            try {
                field.setAccessible(true);
                val = field.get(newGuest);
                if (val == null && field.getType() == String.class) {
                    field.set(newGuest, "");
                }
            } catch (final Exception e) {
                logger.error("Could find the field " + field.getName());
                return "Failed, could find the field " + field.getName();
            }
        }
        final boolean isDuplicated4Guest = umh.isDuplicated4Guest(userToken,
                                                                  newGuest);
        JWebResult result = umh.getResult();
        if (isDuplicated4Guest) {
            errorMsg = status + ", " + result.getStatus().value()
                    + result.getMessages().get(0);
            logger.error(errorMsg);
            return errorMsg;
        }
        result = umh.addGuest(userToken, newGuest);
        if (result.getStatus().compareTo(JStatus.SUCCESS) == 0) {
            logger.info("Guest successfully added into XMA.");
            status = "SUCCESS";
        } else {
            logger.error("Guest failed to add into XMA, please make sure your guest information is correct.");
            status = "Failed";
        }
        return status;
    }

    /**
     * Add role
     *
     * @see roleName=$roleName <br/>
     *      AttributeList=SYSTEM_ADMIN,CONFERENCE_ADMIN
     * @param roleName
     *            name of role
     * @param AttributeList
     *            list of attribute which value is 1
     * @return The result of add role
     */
    public String addRole() {
        String status = "Failed";
        final String roleName = inputCmd.get("roleName").replace("~", " ");
        final String[] attributeList = inputCmd.get("attributeList").split(",");
        final String[] allAttributes = {
                "SYSTEM_ADMIN",
                "DIRECTORY_ADMIN",
                "CONFERENCE_ADMIN",
                "SITE_TOPOLOGY_ADMIN",
                "DEVICE_MONITOR_ADMIN",
                "REPORT_ADMIN",
                "DEPRECATED_SCHEDULING_LEVEL",
                "DEPRECATED_MAX_CALL_BANDWIDTH",
                "DEPRECATED_MONITORING_LEVEL",
                "EVENT_ADMIN",
                "DASHBOARD_OP",
                "CONFERENCE_OP",
                "REPORT_OP",
                "TROUBLESHOOTING_OP",
                "DEPRECATED_SCHEDULING_CONFERENCE",
                "DEPRECATED_ARP",
                "ASSIGN_AREA_MANAGERS",
                "ASSIGN_ENTITIES_TO_AREA",
                "SYSTEM_MAINTENANCE_TROUBLESHOOTING",
                "PROVISION_PROFILES",
                "SYSTEM_AUDITOR",
                "SCHEDULING_VIEWONLY",
                "SCHEDULING_BASIC",
                "SCHEDULING_ADVANCED",
                "ACCESS_ALL_AREAS",
                "SOFTWARE_UPDATES",
                "NETWORK_DEVICE_ADMIN",
                "NETWORK_DEVICE_VIEWONLY",
                "SCHEDULABLE_RESOURCE_VIEWONLY",
                "TRACE_OP",
                "CONFERENCE_OP_RESTRICTED" };
        final RoleManagerHandler roleManagerHandler = new RoleManagerHandler(
                webServiceUrl);
        final JRole role = new JRole();
        role.setGroupName(roleName);
        role.setBelongsToAreaUgpId(0);
        role.setDbKey(new Integer(0));
        role.setUgpId(new Integer(0));
        role.setGroupType(com.polycom.webservices.RoleManager.JGroupType.ROLE);
        role.setIsDefaultGroup(false);
        role.setIsInherited(false);
        role.setVisible(false);
        role.setPolicyId(0);
        role.setAreaSpecificRole(false);
        role.setDN("");
        role.setDomain("");
        role.setGUID("");
        role.setGUIDAsString("");
        role.setDisplayname("");
        role.setGroupDesc("");
        role.setPredefinedRoleName("");
        int attrValue = 0;
        for (int i = 0; i < allAttributes.length; i++) {
            attrValue = 0;
            for (int j = 0; j < attributeList.length; j++) {
                if (allAttributes[i].equals(attributeList[j])) {
                    attrValue = 1;
                    break;
                }
            }
            final JPolicyAttribute at = new JPolicyAttribute();
            at.setAttrInt(88 + i);
            at.setAttrName(allAttributes[i]);
            at.setAttrValue(new Integer(attrValue).toString());
            at.setFileId(0);
            at.setPolicyAttrId(0);
            at.setPolicyId(0);
            role.getRoleAttrs().add(at);
        }
        if (roleManagerHandler.addRole(userToken, role).getStatus()
                .compareTo(com.polycom.webservices.RoleManager.JStatus.SUCCESS) == 0) {
            status = "SUCCESS";
            logger.info("Add role " + inputCmd.get("roleName")
                    + " successfully.");
        } else {
            status = "Failed";
            logger.error("Add role " + inputCmd.get("roleName") + " failed.");
            return status + " Add role " + inputCmd.get("roleName")
                    + " failed.";
        }
        return status;
    }

    /**
     * Create the machine account
     *
     * @see machineAccountName=schemelocalroomma1 <br/>
     *      machineAccountDescription=
     *      MachineAccount4schemelocalroomma1Description <br/>
     *      machineAcccountPassword=Polycom123 <br/>
     *      associateMachineAccountToUserOrRoom=true <br/>
     *      associateMachineAccountToAutoCreatedRoom=false <br/>
     *      searchString4machineAccount=schemelocalroom1
     *
     *
     * @param machineAccountName
     *            The machine account name
     * @param machineAccountDescription
     *            The machine account description
     * @param machineAcccountPassword
     *            The machine account password
     * @param associateMachineAccountToUserOrRoom
     *            Whether associate to user or room
     * @param associateMachineAccountToAutoCreatedRoom
     *            Whether to auto create user or room
     * @param searchString4machineAccount
     *            Use this string to search user or room
     * @return The result
     */
    public String createMachineAccount() {
        String status = "Failed";
        // true for room. false for user or automatically created room.
        boolean RoomOrUser = false;
        // machine account owner ID. 0 if it associated witha automatically
        // created room.
        int maOwnerId = 0;
        String guid = "";
        final String description = inputCmd.get("machineAccountDescription");
        final String passwd = inputCmd.get("machineAcccountPassword");
        final String machineAccountName = inputCmd.get("machineAccountName");
        if (!machineAccountName.isEmpty() && !description.isEmpty()
                && !passwd.isEmpty()
                && !inputCmd.get("associateMachineAccountToUserOrRoom")
                        .isEmpty()
                && !inputCmd.get("associateMachineAccountToAutoCreatedRoom")
                        .isEmpty()) {
            if (!inputCmd.get("searchString4machineAccount").isEmpty()) {
                final List<JUser> userList = umh
                        .getUserAndRooms(userToken,
                                         inputCmd.get("searchString4machineAccount"));
                for (final JUser user : userList) {
                    if (user.getDisplayName().trim()
                            .equals(inputCmd.get("searchString4machineAccount"))
                            || user.getUserName().equals(inputCmd
                                    .get("searchString4machineAccount"))) {
                        guid = user.getGUIDAsString();
                        maOwnerId = user.getUgpId();
                    }
                }
            }
            if (inputCmd.get("associateMachineAccountToUserOrRoom")
                    .equalsIgnoreCase("true")) {
                RoomOrUser = true;
            } else if (inputCmd.get("associateMachineAccountToUserOrRoom")
                    .equalsIgnoreCase("false")) {
                RoomOrUser = false;
            }
            if (inputCmd.get("associateMachineAccountToAutoCreatedRoom")
                    .equalsIgnoreCase("true")) {
                maOwnerId = 0;
                guid = "";
            }
            final BASE64Encoder base64encoder = new BASE64Encoder();
            final JUser user = new JUser();
            user.setEnterpriseType(JEnterpriseType.LOCAL);
            user.setGUIDAsString(guid);
            user.setLastName(description);
            user.setMachineAccountOwner(maOwnerId);
            user.setPassword(base64encoder.encode(passwd.getBytes()));
            user.setRoom(RoomOrUser);
            user.setUserName(machineAccountName);
            user.setBelongsToAreaUgpId(0);
            user.setDbKey(0);
            user.setDomain("");
            user.setGUID("");
            user.setUgpId(0);
            user.setAlertProfileId(0);
            user.setAreaNames("");
            user.setBareJID("");
            user.setCity("");
            user.setDaysToPWExpire(0);
            user.setDepartment("");
            user.setDisplayName("");
            user.setEmail("");
            user.setExchangeServer("");
            user.setFailedAttemptsSinceStart(0);
            user.setFailedLoginAttempts(0);
            user.setFirstName("");
            user.setLastLoginLocation("");
            user.setLastFailLoginLocation("");
            user.setLastName(description);
            user.setMachineAccountOwner(maOwnerId);
            user.setPhoneNumber("");
            user.setRoom(RoomOrUser);
            user.setSIPURI("");
            user.setTitle("");
            user.setUserURI("");
            if (umh.addMachineAccount(userToken, user).getStatus()
                    .compareTo(JStatus.SUCCESS) == 0) {
                status = "SUCCESS";
                logger.info("Machine account " + machineAccountName
                        + " is successfully added into XMA.");
            } else {
                status = "Failed";
                logger.error("Machine account " + machineAccountName
                        + " is failed to add into XMA.");
                return status + " Machine account " + machineAccountName
                        + " is failed to add into XMA.";
            }
        } else {
            status = "Failed";
            logger.error("Some parameters are missing. Please check the input command.");
            return status
                    + " Some parameters are missing. Please check the input command.";
        }
        return status;
    }

    /**
     * Create the room
     *
     * @see roomName=localroom <br/>
     *      roomEmail=localroom@123.com <br/>
     *      siteName=TestSite1 <br/>
     *      roomEnterpriseType=local <br/>
     *      roomDescription=LocalRoomDescription <br/>
     *      deviceType4H323DialString=HDX <br/>
     *      e164Num4H323DialString=1911 <br/>
     *      h323Alias4H323DialString=localroomHDX <br/>
     *      deviceType4SIPDialString=GroupSeries <br/>
     *      sipUri4SIPDialString=localroomGS@sip.com
     *
     * @param roomName
     *            The room name
     * @param roomEmail
     *            The room email
     * @param roomEnterpriseType
     *            Local or AD
     * @param roomDescription
     *            The room description
     * @param deviceType4H323DialString
     *            The device type for h323
     * @param e164Num4H323DialString
     *            The e164 dial string
     * @param h323Alias4H323DialString
     *            The h323 alias
     * @param deviceType4SIPDialString
     *            The device type for sip
     * @param sipUri4SIPDialString
     *            The sip uri dial string
     * @return The result
     */
    public String createRoom() {
        String status = "Failed";
        boolean forceDelete = false;
        // default sit UID refers to the internet/vpn
        final String siteUidDefault = "0fc03000-fc30-0300-fc30-fc030f00f000";
        final SiteTopoManagerHandler stmh = new SiteTopoManagerHandler(
                webServiceUrl);
        final String roomEmail = inputCmd.get("roomEmail");
        final String roomName = inputCmd.get("roomName").replaceAll("~", " ");
        final String roomEnterpriseType = inputCmd.get("roomEnterpriseType");
        final String roomDescription = inputCmd.get("roomDescription");
        if (roomName.isEmpty()) {
            return "Failed, roomName should not be empty.";
        }
        if (roomEnterpriseType.isEmpty()) {
            return "Failed, roomEnterpriseType should not be empty.";
        }
        // if room already exist, skip add it, return SUCCESS directly
        final List<JRoom> existRooms = umh.getRooms(userToken);
        for (final JRoom room : existRooms) {
            final String addRoomName = roomName;
            final String existRoomName = room.getName();
            if (addRoomName.equals(existRoomName)) {
                logger.info(room.getName() + " already exist, skip add it");
                status = "SUCCESS";
                return status;
            }
        }
        final JRoom newRoom = new JRoom();
        newRoom.setName(roomName);
        newRoom.setDescription(roomDescription);
        newRoom.setEmail(roomEmail);
        newRoom.setBelongsToAreaUgpId(-1);
        newRoom.setSiteUid(siteUidDefault);
        final String siteName = inputCmd.get("siteName");
        if (!siteName.isEmpty()) {
            final List<JNamePlusUid> list = stmh
                    .getListOfSiteNameUid(userToken,
                                          stmh.getListOfTerritory(userToken));
            for (final JNamePlusUid site : list) {
                if (site.getName().equals(siteName)) {
                    newRoom.setSiteUid(site.getUid());
                    break;
                }
            }
        }
        newRoom.setEnterpriseType(JEnterpriseType.LOCAL);
        if (roomEnterpriseType.equalsIgnoreCase("AD")) {
            newRoom.setEnterpriseType(JEnterpriseType.ENTERPRISE);
            List<JUser> users = umh.getUsers(userToken, roomName, "AD");
            for (final JUser user : users) {
                if (user.getUserName().equals(roomName)) {
                    logger.info("Got the AD user from the LDAP server.");
                    newRoom.setGUIDAsString(user.getGUIDAsString());
                    newRoom.setEmail(user.getEmail());
                    newRoom.setUgpId(user.getUgpId());
                    forceDelete = true;
                }
            }
        }
        // compose the H323 and SIP alias data
        final List<H323Alias> h323aliasList = new LinkedList<H323Alias>();
        final List<SipAlias> sipList = new LinkedList<SipAlias>();
        final String deviceType4H323DialString = inputCmd
                .get("deviceType4H323DialString");
        final int e164 = Integer
                .parseInt(inputCmd.get("e164Num4H323DialString"));
        final String h323Id = inputCmd.get("h323Alias4H323DialString");
        final String sipuri = inputCmd.get("sipUri4SIPDialString");
        final String deviceType4SIPDialString = inputCmd
                .get("deviceType4SIPDialString");
        try {
            generateH323AliasList(h323aliasList,
                                  deviceType4H323DialString,
                                  e164,
                                  h323Id,
                                  0);
            generateSipAliasList(sipList, deviceType4SIPDialString, sipuri, 0);
        } catch (IllegalArgumentException
                 | IllegalAccessException
                 | InstantiationException e) {
            e.printStackTrace();
            return "Failed, generate H323/Sip Alias failed\n"
                    + CommonUtils.getExceptionStackTrace(e);
        }
        final JWebResult result = umh
                .createRoomExt(userToken,
                               newRoom,
                               h323aliasList,
                               sipList,
                               forceDelete);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Room " + roomName
                    + " is successfully created on the XMA.");
        } else {
            status = "Failed";
            logger.error("Room " + roomName + " failed to create on the XMA.");
            return status + " Room " + roomName
                    + " failed to create on the XMA.\n" + result.getMessages();
        }
        return status;
    }

    /**
     * Delete all guests
     *
     * @see No param
     *
     * @return The result
     */
    public String deleteAllGuests() {
        String status = "Failed";
        final List<Integer> guestList = new ArrayList<Integer>();
        final List<JConfGuest> guests = umh.searchGuest(userToken);
        for (final JConfGuest guest : guests) {
            guestList.add(guest.getId());
        }
        final JWebResult result = umh.deleteGuest(userToken, guestList);
        if (result.getStatus().compareTo(JStatus.SUCCESS) == 0) {
            logger.info("All guests successfully deleted from XMA.");
            status = "SUCCESS";
        } else {
            logger.error("Fail to delete all guests on XMA");
            status = "Failed";
        }
        return status;
    }

    /**
     * Delete all the orphan AD user related data on RPRM
     *
     * @return
     */
    public String deleteAllOrphanAdUser() {
        String status = "Failed";
        // Retrieve the orphan AD user data on RPRM
        if (umh.getOrphanADData(userToken).getStatus()
                .equals(JStatus.SUCCESS)) {
            logger.info("Successfully retrieve the orphan AD user related data on RPRM.");
        } else {
            logger.error("Failed to retrieve the orphan AD user related data on RPRM.");
            return "Failed to retrieve the orphan AD user related data on RPRM.";
        }
        // Delete the orphan AD user data on RPRM
        final JWebResult result = umh.deleteAllOrphanAdUser(userToken);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Successfully delete all the orphan AD user related data on RPRM.");
            status = "SUCCESS";
        } else {
            logger.error("Failed to delete all the orphan AD user related data on RPRM.");
            return "Failed to delete all the orphan AD user related data on RPRM.";
        }
        return status;
    }

    /**
     * Delete guest
     *
     * @see firstName=ct1 <br/>
     *      lastName=guest1 <br/>
     *      email=ct_update@p.com <br/>
     *      location=beijing2
     *
     * @param firstName
     *            The first name of guest
     * @param lastName
     *            The last name of guest
     * @param location
     *            The location of guest
     * @param email
     *            The email of guest
     * @return The result
     */
    public String deleteGuest() {
        String status = "Failed";
        final String firstName = inputCmd.get("firstName");
        final String email = inputCmd.get("email");
        final String location = inputCmd.get("location");
        final String lastName = inputCmd.get("lastName");
        final List<Integer> guestList = new ArrayList<Integer>();
        for (final JConfGuest guest : umh.searchGuest(userToken)) {
            if (guest.getFirstName().equals(firstName)
                    && guest.getEmail().equals(email)
                    && guest.getLocation().equals(location)
                    && guest.getCity().equals(location)) {
                if (!lastName.isEmpty()) {
                    if (guest.getLastName().equals(lastName)) {
                        logger.info("Found the specificed guest on XMA, the guest ID is: "
                                + guest.getId());
                        guestList.add(guest.getId());
                    } else {
                        logger.error("Cannot find the specificed guest on XMA since the last name: "
                                + guest.getLastName()
                                + "is not as the expected: " + lastName);
                        status = "Failed";
                    }
                } else {
                    logger.info("Found the specificed guest on XMA, the guest ID is: "
                            + guest.getId());
                    guestList.add(guest.getId());
                }
            }
        }
        final JWebResult result = umh.deleteGuest(userToken, guestList);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Successfully delete the specificed guest");
            status = "SUCCESS";
        } else {
            logger.error("Failed to delete the specificed guest");
            status = "Failed";
        }
        return status;
    }

    /**
     * Delete the machine account
     *
     * @see machineAccountName=MachineAccount4ADRoom
     *
     * @param machineAccountName
     *            The machine account name
     * @return The result
     */
    public String deleteMachineAccount() {
        String status = "Failed";
        if (!inputCmd.get("machineAccountName").isEmpty()) {
            final List<JUser> userList = umh.getMachineAccounts(userToken);
            for (final JUser user : userList) {
                if ((user.getUserName()
                        .equalsIgnoreCase(inputCmd.get("machineAccountName"))
                        || user.getDisplayName().trim()
                                .equals(inputCmd.get("machineAccountName")))
                        && user.getUserType()
                                .compareTo(JUserType.MACHINE___ACCOUNT) == 0) {
                    if (umh.deleteMachineAccount(userToken, user.getDbKey())
                            .getStatus().compareTo(JStatus.SUCCESS) == 0) {
                        status = "SUCCESS";
                        logger.info("The machine account "
                                + inputCmd.get("machineAccountName")
                                + " is successfully deleted from XMA.");
                    } else {
                        status = "Failed";
                        logger.error("The machine account "
                                + inputCmd.get("machineAccountName")
                                + " is failed to delete from XMA.");
                        return status + " The machine account "
                                + inputCmd.get("machineAccountName")
                                + " is failed to delete from XMA.";
                    }
                }
            }
        } else {
            status = "Failed";
            logger.error("Please provide a machine account name.");
            return status + " Please provide a machine account name.";
        }
        return status;
    }

    /**
     * delete role
     *
     * @see roleName=$roleName <br/>
     * @param roleName
     *            name of role
     * @return The result of delete role
     */
    public String deleteRole() {
        String status = "Failed";
        final String roleName = inputCmd.get("roleName").replace("~", " ");
        final RoleManagerHandler roleManagerHandler = new RoleManagerHandler(
                webServiceUrl);
        final List<JRole> availRole = roleManagerHandler.getRoles(userToken);
        JRole find = null;
        for (final JRole role : availRole) {
            if (!roleName.isEmpty() && roleName.equals(role.getGroupName())) {
                find = role;
                break;
            }
        }
        if (find == null) {
            return "NotFound, could not find role";
        }
        try {
            if (roleManagerHandler.deleteRole(userToken, find).getStatus()
                    .compareTo(com.polycom.webservices.RoleManager.JStatus.SUCCESS) == 0) {
                status = "SUCCESS";
                logger.info("Delete role " + inputCmd.get("roleName")
                        + " successfully.");
            } else {
                status = "Failed";
                logger.error("Delete role " + inputCmd.get("roleName")
                        + " failed.");
                return status + " Delete role " + inputCmd.get("roleName")
                        + " failed.";
            }
        } catch (final Exception e) {
            e.printStackTrace();
            status = "Failed, got exception when delete role. Error msg is "
                    + e.getMessage();
        }
        return status;
    }

    /**
     * Delete the room
     *
     * @see roomName=adroom4 <br/>
     *      roomEnterpriseType=AD
     * @param roomName
     *            The room name
     * @param roomEnterpriseType
     *            AD or Local
     * @return The result
     */
    public String deleteRoom() {
        String status = "Failed";
        if ((!inputCmd.get("roomName").isEmpty()
                || (!inputCmd.get("roomFirstName").isEmpty()
                        && !inputCmd.get("roomLastName").isEmpty()))
                && !inputCmd.get("roomEnterpriseType").isEmpty()) {
            for (final JRoom room : umh.getRooms(userToken)) {
                if (room.getName().trim().equals(inputCmd.get("roomName"))
                        && (inputCmd.get("roomEnterpriseType")
                                .equalsIgnoreCase("local")
                                || inputCmd.get("roomEnterpriseType")
                                        .equalsIgnoreCase("AD"))) {
                    if (umh.deleteRoom(userToken, room).getStatus()
                            .compareTo(JStatus.SUCCESS) == 0) {
                        status = "SUCCESS";
                        logger.info("Room " + inputCmd.get("roomName")
                                + " deleted successfully.");
                    } else {
                        status = "Failed";
                        logger.error("Room " + inputCmd.get("roomName")
                                + " failed to delete.");
                        return status + " Room " + inputCmd.get("roomName")
                                + " failed to delete.";
                    }
                } else if (room.getName().trim()
                        .equals(inputCmd.get("roomFirstName") + " "
                                + inputCmd.get("roomLastName"))
                        && inputCmd.get("roomEnterpriseType")
                                .equalsIgnoreCase("AD")) {
                    if (umh.deleteRoom(userToken, room).getStatus()
                            .compareTo(JStatus.SUCCESS) == 0) {
                        status = "SUCCESS";
                        logger.info("Room " + inputCmd.get("roomFirstName")
                                + " " + inputCmd.get("roomLastName")
                                + " deleted successfully.");
                    } else {
                        status = "Failed";
                        logger.error("Room " + inputCmd.get("roomFirstName")
                                + " " + inputCmd.get("roomLastName")
                                + " failed to delete.");
                        return status + " Room " + inputCmd.get("roomFirstName")
                                + " " + inputCmd.get("roomLastName")
                                + " failed to delete.";
                    }
                }
            }
        } else {
            status = "Failed";
            logger.error("Cannot find the room name in the input command.");
            return status + " Cannot find the room name in the input command.";
        }
        return status;
    }

    /**
     * Delete the user dial string
     *
     * @see searchString=debuguser11 <br/>
     *      userSearchType=local
     *
     * @param searchString
     *            Use this string to search user
     * @param userSearchType
     *            AD or Local
     * @return The result
     */
    public String deleteUserDialString() {
        String status = "Failed";
        final String searchString = inputCmd.get("searchString");
        final String localOrAD = inputCmd.get("userSearchType");
        final List<JUser> users = umh.getUsers(userToken,
                                               searchString,
                                               localOrAD);
        JUser targetUser = null;
        for (final JUser user : users) {
            if (user.getUserName().equals(searchString)) {
                targetUser = user;
            }
        }
        if (targetUser == null) {
            logger.error("Cannot find the user " + searchString);
            return "Failed, Cannot find the user " + searchString;
        }
        if (targetUser.getDbKey() == null) {
            logger.info("dbKey of " + targetUser.getDisplayName() + " is null");
            return "SUCCESS, dbKey of dbKey of " + targetUser.getDisplayName()
                    + " is null";
        }
        final List<H323Alias> h323Base = umh
                .getReservedH323Alias(userToken,
                                      targetUser.getDbKey().intValue());
        final List<H323Alias> h323Delete = h323Base;
        final List<SipAlias> sipBase = umh
                .getReservedSipUri(userToken, targetUser.getDbKey().intValue());
        final List<SipAlias> sipDelete = sipBase;
        final JWebResult result = umh
                .updateUserExt(userToken,
                               targetUser,
                               null,
                               null,
                               h323Base,
                               null,
                               null,
                               h323Delete,
                               sipBase,
                               null,
                               null,
                               sipDelete);
        if (result.getStatus().compareTo(JStatus.SUCCESS) == 0) {
            logger.info("Reserved dial string successfully deleted from XMA.");
            status = "SUCCESS";
        } else {
            logger.error("Fail to delete reserved dial string on XMA");
            status = "Failed";
            return status;
        }
        return status;
    }

    /**
     * Search the guest
     *
     * @see firstName=isdn <br/>
     *      lastName=guest <br/>
     *      email=isdn@123.com <br/>
     *      location=beijing <br/>
     *      dialInOrNot=true <br/>
     *      joinMode=video <br/>
     *      isH323=false <br/>
     *      isSIP=false <br/>
     *      isISDN=true <br/>
     *      countryCode=52 <br/>
     *      areaCode=01 <br/>
     *      isdnNum=55555
     *
     * @param firstName
     *            The first name of guest
     * @param lastName
     *            The last name of guest
     * @param location
     *            The location of guest(Optional)
     * @param email
     *            The email of guest(Optional)
     * @param joinMode
     *            Video or Audio(Optional)
     * @param isH323
     *            Whether support H323(Optional)
     * @param isSIP
     *            Whether support sip(Optional)
     * @param isISDN
     *            Whether support ISDN(Optional)
     * @param countryCode
     *            The country code(Optional)
     * @param areaCode
     *            The area code(Optional)
     * @param isdnNum
     *            The ISDN number(Optional)
     * @return The result
     */
    public String exactSearchGuest() {
        String status = "Failed";
        for (final JConfGuest guest : umh.searchGuest(userToken)) {
            if (guest.getFirstName().equals(inputCmd.get("firstName"))) {
                if (!inputCmd.get("lastName").isEmpty()) {
                    if (guest.getLastName().equals(inputCmd.get("lastName"))) {
                        logger.info("The last name is as the expected: "
                                + inputCmd.get("lastName"));
                        status = "SUCCESS";
                    } else {
                        logger.error("The last name: " + guest.getLastName()
                                + "is not as the expected: "
                                + inputCmd.get("lastName"));
                        status = "Failed";
                        continue;
                    }
                }
                if (!inputCmd.get("email").isEmpty()) {
                    if (guest.getEmail().equals(inputCmd.get("email"))) {
                        logger.info("The email address is as the expected: "
                                + inputCmd.get("email"));
                        status = "SUCCESS";
                    } else {
                        logger.error("The email address: " + guest.getEmail()
                                + "is not as the expected: "
                                + inputCmd.get("email"));
                        status = "Failed";
                        continue;
                    }
                }
                if (!inputCmd.get("location").isEmpty()) {
                    if (guest.getLocation().equals(inputCmd.get("location"))) {
                        logger.info("The location is as the expected: "
                                + inputCmd.get("location"));
                        status = "SUCCESS";
                    } else {
                        logger.error("The location: " + guest.getLocation()
                                + "is not as the expected: "
                                + inputCmd.get("location"));
                        status = "Failed";
                        continue;
                    }
                }
                if (!inputCmd.get("dialInOrNot").isEmpty()) {
                    if (inputCmd.get("dialInOrNot").equalsIgnoreCase("true")) {
                        if (guest.isDialIn()) {
                            logger.info("The dial mode is as the expected: "
                                    + guest.isDialIn());
                            status = "SUCCESS";
                        } else {
                            logger.error("The dial mode: " + guest.isDialIn()
                                    + "is not as the expected: "
                                    + inputCmd.get("dialInOrNot"));
                            status = "Failed";
                            continue;
                        }
                    }
                    if (inputCmd.get("dialInOrNot").equalsIgnoreCase("false")) {
                        if (!guest.isDialIn()) {
                            logger.info("The dial mode is as the expected: "
                                    + guest.isDialIn());
                            status = "SUCCESS";
                        } else {
                            logger.error("The dial mode: " + guest.isDialIn()
                                    + "is not as the expected: "
                                    + inputCmd.get("dialInOrNot"));
                            status = "Failed";
                            continue;
                        }
                    }
                }
                if (!inputCmd.get("isH323").isEmpty()) {
                    if (inputCmd.get("isH323").equals("true")) {
                        if (guest.isSupportsH323()) {
                            logger.info("The guest supports H323 is as expected");
                            status = "SUCCESS";
                        } else {
                            logger.error("The guest supports H323 is not as expected");
                            status = "Failed";
                            continue;
                        }
                    }
                    if (inputCmd.get("isH323").equals("false")) {
                        if (!guest.isSupportsH323()) {
                            logger.info("The guest supports H323 is as expected");
                            status = "SUCCESS";
                        } else {
                            logger.error("The guest supports H323 is not as expected");
                            status = "Failed";
                            continue;
                        }
                    }
                }
                if (!inputCmd.get("isSIP").isEmpty()) {
                    if (inputCmd.get("isSIP").equals("true")) {
                        if (guest.isSupportsSip()) {
                            logger.info("The guest supports SIP is as expected");
                            status = "SUCCESS";
                        } else {
                            logger.error("The guest supports SIP is not as expected");
                            status = "Failed";
                            continue;
                        }
                    }
                    if (inputCmd.get("isSIP").equals("false")) {
                        if (!guest.isSupportsSip()) {
                            logger.info("The guest supports SIP is as expected");
                            status = "SUCCESS";
                        } else {
                            logger.error("The guest supports SIP is not as expected");
                            status = "Failed";
                            continue;
                        }
                    }
                }
                if (!inputCmd.get("isISDN").isEmpty()) {
                    if (inputCmd.get("isISDN").equals("true")) {
                        if (guest.isSupportsIsdn()) {
                            logger.info("The guest supports ISDN is as expected");
                            status = "SUCCESS";
                        } else {
                            logger.error("The guest supports ISDN is not as expected");
                            status = "Failed";
                            continue;
                        }
                    }
                    if (inputCmd.get("isISDN").equals("false")) {
                        if (!guest.isSupportsIsdn()) {
                            logger.info("The guest supports ISDN is as expected");
                            status = "SUCCESS";
                        } else {
                            logger.error("The guest supports ISDN is not as expected");
                            status = "Failed";
                            continue;
                        }
                    }
                }
                if (!inputCmd.get("sipUri").isEmpty()) {
                    if (guest.getSipUri().equals(inputCmd.get("sipUri"))) {
                        logger.info("The SIP URI is as the expected: "
                                + inputCmd.get("sipUri"));
                        status = "SUCCESS";
                    } else {
                        logger.error("The SIP URI: " + guest.getSipUri()
                                + "is not as the expected: "
                                + inputCmd.get("sipUri"));
                        status = "Failed";
                        continue;
                    }
                }
                if (!inputCmd.get("countryCode").isEmpty()) {
                    if (guest.isSupportsIsdn() && guest.getCountryCode()
                            .equals(inputCmd.get("countryCode"))) {
                        logger.info("The country code is as the expected: "
                                + inputCmd.get("countryCode"));
                        status = "SUCCESS";
                    } else {
                        logger.error("The country code: "
                                + guest.getCountryCode()
                                + "is not as the expected: "
                                + inputCmd.get("countryCode"));
                        status = "Failed";
                        continue;
                    }
                }
                if (!inputCmd.get("areaCode").isEmpty()) {
                    if (guest.isSupportsIsdn() && guest.getAreaCode()
                            .equals(inputCmd.get("areaCode"))) {
                        logger.info("The area code is as the expected: "
                                + inputCmd.get("areaCode"));
                        status = "SUCCESS";
                    } else {
                        logger.error("The area code: " + guest.getAreaCode()
                                + "is not as the expected: "
                                + inputCmd.get("areaCode"));
                        status = "Failed";
                        continue;
                    }
                }
                if (!inputCmd.get("isdnNum").isEmpty()) {
                    if (guest.isSupportsIsdn() && guest.getIsdnNumber()
                            .equals(inputCmd.get("isdnNum"))) {
                        logger.info("The ISDN number is as the expected: "
                                + inputCmd.get("isdnNum"));
                        status = "SUCCESS";
                    } else {
                        logger.error("The ISDN number: " + guest.getIsdnNumber()
                                + "is not as the expected: "
                                + inputCmd.get("isdnNum"));
                        status = "Failed";
                        continue;
                    }
                }
                if (!inputCmd.get("extenNum").isEmpty()) {
                    if (guest.getExtensionIP()
                            .equals(inputCmd.get("extenNum"))) {
                        logger.info("The IP extension number is as the expected: "
                                + inputCmd.get("extenNum"));
                        status = "SUCCESS";
                    } else {
                        logger.error("The IP extension number: "
                                + guest.getExtensionIP()
                                + "is not as the expected: "
                                + inputCmd.get("extenNum"));
                        status = "Failed";
                        continue;
                    }
                }
                if (!inputCmd.get("useModDialNum").isEmpty()) {
                    if (inputCmd.get("useModDialNum")
                            .equalsIgnoreCase("true")) {
                        if (guest.isModifiedDialNumber()) {
                            logger.info("The use modified dial number is as the expected");
                            status = "SUCCESS";
                        } else {
                            logger.error("The use modified dial number is not as the expected");
                            status = "Failed";
                            continue;
                        }
                    }
                    if (inputCmd.get("useModDialNum")
                            .equalsIgnoreCase("false")) {
                        if (!guest.isModifiedDialNumber()) {
                            logger.info("The use modified dial number is as the expected");
                            status = "SUCCESS";
                        } else {
                            logger.error("The use modified dial number is not as the expected");
                            status = "Failed";
                            continue;
                        }
                    }
                }
                if (!inputCmd.get("modDialNum").isEmpty()) {
                    System.out.println("Read the guest info moddial num is: "
                            + guest.getModDialNumber());
                    if (guest.getModDialNumber()
                            .equals(inputCmd.get("modDialNum"))) {
                        logger.info("The modified number is as the expected: "
                                + inputCmd.get("modDialNum"));
                        status = "SUCCESS";
                    } else {
                        logger.error("The modified number: "
                                + guest.getModDialNumber()
                                + "is not as the expected: "
                                + inputCmd.get("modDialNum"));
                        status = "Failed";
                        continue;
                    }
                }
                if (!inputCmd.get("aliasType").isEmpty()) {
                    if (inputCmd.get("aliasType").equalsIgnoreCase("IP")) {
                        if (guest.getIpAddr()
                                .equals(inputCmd.get("aliasValue"))) {
                            logger.info("The IP address is as the expected: "
                                    + inputCmd.get("aliasValue"));
                            status = "SUCCESS";
                        } else {
                            logger.error("The IP address: " + guest.getIpAddr()
                                    + "is not as the expected: "
                                    + inputCmd.get("aliasValue"));
                            status = "Failed";
                            continue;
                        }
                    }
                    if (inputCmd.get("aliasType").equalsIgnoreCase("H323")
                            || inputCmd.get("aliasType")
                                    .equalsIgnoreCase("E164")
                            || inputCmd.get("aliasType")
                                    .equalsIgnoreCase("Annex-O")) {
                        if (guest.getAlias().getValue()
                                .equals(inputCmd.get("aliasValue"))) {
                            logger.info("The alias value is as the expected: "
                                    + inputCmd.get("aliasValue"));
                            status = "SUCCESS";
                        } else {
                            logger.error("The alias value: "
                                    + guest.getAlias().getValue()
                                    + "is not as the expected: "
                                    + inputCmd.get("aliasValue"));
                            status = "Failed";
                            continue;
                        }
                    }
                }
            }
        }
        return status;
    }

    /**
     * Internal method, generated H323Alias object
     *
     * @param deviceType
     *            The device type
     * @param e164
     *            The e164 number
     * @param h323Id
     *            The H323 id
     * @param ugpId
     *            The ugpId
     * @return H323Alias
     */
    private H323Alias generateH323Alias(final String deviceType,
                                        final int e164,
                                        final String h323Id,
                                        final int ugpId) {
        final H323Alias h323Alias = new H323Alias();
        final JDeviceTypeVO deviceTypeVO = getDeviceTypeVO(deviceType);
        h323Alias.setDeviceType(deviceTypeVO);
        h323Alias.setUgp_Id(ugpId);
        h323Alias.setE164Number(e164 + "");
        h323Alias.setH323Alias(h323Id);
        return h323Alias;
    }

    /**
     * Internal method, generated H323Alias object list
     *
     * @param h323AliasList
     *            The list to add
     * @param deviceType
     *            The device type
     * @param e164
     *            The e164 number
     * @param h323Id
     *            The H323 id
     * @param ugpId
     *            The ugpId
     * @return H323Alias list
     */
    private List<H323Alias>
            generateH323AliasList(final List<H323Alias> h323AliasList,
                                  final String deviceType,
                                  final int e164,
                                  final String h323Id,
                                  final int ugpId)
                                          throws IllegalArgumentException,
                                          IllegalAccessException,
                                          InstantiationException {
        if (deviceType == null || deviceType.isEmpty()) {
            return h323AliasList;
        }
        if (deviceType.equals("ITP")) {
            final int[] e164Numbers = new int[] { e164, e164 + 1, e164 + 2 };
            final String[] h323Ids = new String[] {
                    h323Id + "_3_1",
                    h323Id + "_3_2",
                    h323Id + "_3_3" };
            for (int i = 0; i < e164Numbers.length; i++) {
                if (h323AliasList.size() <= i) {
                    final H323Alias h323Alias = generateH323Alias(deviceType,
                                                                  e164Numbers[i],
                                                                  h323Ids[i],
                                                                  ugpId);
                    h323AliasList.add(i, h323Alias);
                } else {
                    h323AliasList.get(i).setE164Number(e164Numbers[i] + "");
                    h323AliasList.get(i).setH323Alias(h323Ids[i]);
                }
            }
        } else {
            if (h323AliasList.isEmpty()) {
                final H323Alias h323Alias = generateH323Alias(deviceType,
                                                              e164,
                                                              h323Id,
                                                              ugpId);
                h323AliasList.add(h323Alias);
            } else {
                int hitIndex = -1;
                for (int i = 0; i < h323AliasList.size(); i++) {
                    if (deviceType.replace("RPDesktop", "RP-Desktop")
                            .equalsIgnoreCase(h323AliasList.get(i)
                                    .getDeviceType().getDisplayName())) {
                        hitIndex = i;
                    }
                }
                if (hitIndex >= 0 && hitIndex < h323AliasList.size()) {
                    h323AliasList.get(hitIndex).setE164Number(e164 + "");
                    h323AliasList.get(hitIndex).setH323Alias(h323Id);
                } else {
                    final H323Alias h323Alias = generateH323Alias(deviceType,
                                                                  e164,
                                                                  h323Id,
                                                                  ugpId);
                    h323AliasList.add(h323Alias);
                }
            }
        }
        return h323AliasList;
    }

    /**
     * Internal method, generate SipAlias object
     *
     * @param deviceType
     *            The device type
     * @param sipuri
     *            The sip uri
     * @param ugpId
     *            The ugpid
     * @return SipAlias
     */
    private SipAlias generateSipAlias(final String deviceType,
                                      final String sipuri,
                                      final int ugpId) {
        final SipAlias sipAlias = new SipAlias();
        final JDeviceTypeVO deviceTypeVO = getDeviceTypeVO(deviceType);
        sipAlias.setDeviceType(deviceTypeVO);
        sipAlias.setUgp_Id(ugpId);
        sipAlias.setSipuri(sipuri);
        return sipAlias;
    }

    /**
     * Internal method, generate SipAlias object list
     *
     * @param sipAliasList
     *            The sip alias list to add
     * @param deviceType
     *            The device type
     * @param sipuri
     *            The sip uri
     * @param ugpId
     *            The ugpid
     * @return SipAlias list
     */
    private List<SipAlias>
            generateSipAliasList(final List<SipAlias> sipAliasList,
                                 final String deviceType,
                                 final String sipuri,
                                 final int ugpId)
                                         throws IllegalArgumentException,
                                         IllegalAccessException,
                                         InstantiationException {
        if (deviceType == null || deviceType.isEmpty()) {
            return sipAliasList;
        }
        if (deviceType.equals("ITP")) {
            final String[] sipuris = new String[] {
                    sipuri + "itp3",
                    "~" + sipuri + "2",
                    "~" + sipuri + "3" };
            for (int i = 0; i < sipuris.length; i++) {
                if (sipAliasList.size() <= i) {
                    final SipAlias sipAlias = generateSipAlias(deviceType,
                                                               sipuris[i],
                                                               ugpId);
                    sipAliasList.add(i, sipAlias);
                } else {
                    sipAliasList.get(i).setSipuri(sipuris[i]);
                }
            }
        } else {
            if (sipAliasList.isEmpty()) {
                final SipAlias sipAlias = generateSipAlias(deviceType,
                                                           sipuri,
                                                           ugpId);
                sipAliasList.add(sipAlias);
            } else {
                int hitIndex = -1;
                for (int i = 0; i < sipAliasList.size(); i++) {
                    if (deviceType.replace("RPDesktop", "RP-Desktop")
                            .equalsIgnoreCase(sipAliasList.get(i)
                                    .getDeviceType().getDisplayName())) {
                        hitIndex = i;
                    }
                }
                if (hitIndex >= 0 && hitIndex < sipAliasList.size()) {
                    sipAliasList.get(hitIndex).setSipuri(sipuri);
                } else {
                    final SipAlias sipAlias = generateSipAlias(deviceType,
                                                               sipuri,
                                                               ugpId);
                    sipAliasList.add(sipAlias);
                }
            }
        }
        return sipAliasList;
    }

    /**
     * Get default roles.
     *
     * @return Default roles list
     */
    public String getDefaultRoles() {
        String roleList = "";
        final RoleManagerHandler roleManagerHandler = new RoleManagerHandler(
                webServiceUrl);
        final List<JRole> availRole = roleManagerHandler.getRoles(userToken);
        for (final JRole role : availRole) {
            roleList = roleList + role.getGroupName() + ",";
        }
        return roleList.substring(0, roleList.length() - 2);
    }

    /**
     * Internal method, get the JDeviceTypeVO by device type
     *
     * @param deviceType
     *            The device type
     * @return JDeviceTypeVO
     */
    private JDeviceTypeVO getDeviceTypeVO(final String deviceType) {
        final DeviceMetaManagerHandler dmmh = new DeviceMetaManagerHandler(
                webServiceUrl);
        final com.polycom.webservices.DeviceMetaManager.JDeviceTypeVO metaDeviceType = dmmh
                .getDeviceTypeByDeviceModel(userToken, deviceType);
        final JDeviceTypeVO deviceTypeVO = new JDeviceTypeVO();
        try {
            CommonUtils.copyProperties(metaDeviceType, deviceTypeVO);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
        return deviceTypeVO;
    }

    /**
     * Get the role specified attribute value
     *
     * @see roleName=$roleName <br/>
     *      keyword=groupName
     * @param roleName
     *            name of role
     * @param keyword
     *            attribute of role
     * @return The role specified attribute value
     */
    public String getRoleSpecific() {
        final String keyword = inputCmd.get("keyword");
        final String roleName = inputCmd.get("roleName").replace("~", " ");
        final RoleManagerHandler roleManagerHandler = new RoleManagerHandler(
                webServiceUrl);
        final List<JRole> availRole = roleManagerHandler.getRoles(userToken);
        JRole find = null;
        for (final JRole role : availRole) {
            if (!roleName.isEmpty() && roleName.equals(role.getGroupName())) {
                find = role;
                break;
            }
        }
        if (find == null) {
            return "NotFound, could not find role";
        }
        return CommonUtils.invokeGetMethod(find, keyword);
    }

    /**
     * Get the room account specified attribute value
     *
     * @see machineAccountName=roomAccount <br/>
     *      keyword=UgpId
     *
     * @param machineAccountName
     *            The account name
     * @param keyword
     *            The attribute name
     *
     * @return The room account specified attribute value
     */
    public String getRoomAccountSpecific() {
        final String accountName = inputCmd.get("machineAccountName");
        if (!accountName.isEmpty()) {
            final List<JUser> userList = umh.getMachineAccounts(userToken);
            for (final JUser user : userList) {
                if ((user.getUserName().equalsIgnoreCase(accountName)
                        || user.getDisplayName().trim().equals(accountName))
                        && user.getUserType()
                                .compareTo(JUserType.MACHINE___ACCOUNT) == 0) {
                    final String keyword = inputCmd.get("keyword");
                    return CommonUtils.invokeGetMethod(user, keyword);
                }
            }
        }
        return "Failed, did not find room account " + accountName;
    }

    /**
     * Get the room specified attribute value
     *
     * @see roomName=schemelocalroom1 <br/>
     *      keyword=UgpId
     *
     * @param roomName
     *            The room name
     * @param keyword
     *            The attribute name
     *
     * @return The room specified attribute value
     */
    public String getRoomSpecific() {
        final String roomName = inputCmd.get("roomName");
        final JRoom room = umh.getRooms(userToken, roomName).get(0);
        final String keyword = inputCmd.get("keyword");
        return CommonUtils.invokeGetMethod(room, keyword);
    }

    /**
     * Get the user specified attribute value in user page
     *
     * @see userName=$userName <br/>
     *      userSearchType=AllUser<br/>
     *      userName=$id<br/>
     *      keyword=displayName
     * @param searchString
     *            The search string for some users
     * @param userSearchType
     *            The user type, local or AD or all user
     * @return The user specified attribute value in user page
     */
    public String getUserSpecific() {
        final String userName = inputCmd.get("userName");
        final String keyword = inputCmd.get("keyword");
        List<JUser> userList;
        try {
            String searchString = inputCmd.get("searchString");
            String userSearchType = inputCmd.get("userSearchType");
            userList = umh.getUsers(userToken,
                                    searchString,
                                    userSearchType);
        } catch (final Exception e) {
            e.printStackTrace();
            return "Failed, got exception when get users. Error msg is "
                    + e.getMessage();
        }
        JUser find = null;
        for (final JUser user : userList) {
            if (user.getDisplayName().trim().equals(userName)
                    || user.getUserName().trim().equals(userName)) {
                find = user;
            }
        }
        if (find == null) {
            return "NotFound, could not find user";
        }
        if (find.getUgpId() != -1) {
            find = umh.findUser(userToken, find.getUgpId(), find.getGUID());
        }
        return CommonUtils.invokeGetMethod(find, keyword);
    }

    @Override
    protected void injectCmdArgs() {
        put("firstName", "");
        put("lastName", "");
        put("email", "");
        put("location", "");
        put("joinMode", "");
        put("sipUri", "");
        put("countryCode", "");
        put("areaCode", "");
        put("isdnNum", "");
        put("extenNum", "");
        put("extenSIP", "");
        put("extenISDN", "");
        put("extenH323", "");
        put("modDialNum", "");
        put("aliasType", "");
        put("aliasValue", "");
        put("isH323", "");
        put("isSIP", "");
        put("isISDN", "");
        put("dialInOrNot", "");
        put("useModDialNum", "");
        put("newfirstName", "");
        put("newlastName", "");
        put("newemail", "");
        put("newlocation", "");
        put("searchString", "");
        put("userSearchType", "local");
        put("newUserName", "");
        put("newPassword", "");
        put("newTitle", "");
        put("newDepartment", "");
        put("newPhoneNumber", "");
        put("userRoleList", "");
        // Room name
        put("roomName", "");
        // New room name
        put("newRoomName", "");
        // Room first name. It is only for AD room.
        put("roomFirstName", "");
        // Room last name. It is only for AD room.
        put("roomLastName", "");
        // Room description
        put("roomDescription", "");
        // New room description
        put("newRoomDescription", "");
        // Room email
        put("roomEmail", "");
        // New room email. Only for local room update purpose.
        put("newRoomEmail", "");
        // Site name the room belongs to
        put("siteName", "");
        // New site name the room belongs to
        put("newSiteName", "");
        // Room enterprise type. Local or AD
        put("roomEnterpriseType", "");
        // Room H323 Dial String Reservation Device Type for room
        // H323 dial string reservation
        put("deviceType4H323DialString", "");
        // E164 number for room H323 dial string reservation
        put("e164Num4H323DialString", "0");
        // H323 Alias for room H323 dial string reservation
        put("h323Alias4H323DialString", "");
        // Room SIP Dial String Reservation Device Type for room
        // SIP dial string reservation
        put("deviceType4SIPDialString", "");
        // SIP URI for room H323 dial string reservation
        put("sipUri4SIPDialString", "");
        // Currently only support maximum 3 endpoints for the association
        // The endpoint IP address that the room need to associate with
        put("associateEndpointIP", "");
        // The endpoint type that the room need to associate with
        put("associateEndpointType", "");
        // New Room H323 Dial String Reservation
        // New Device Type for room H323 dial string reservation
        put("newDeviceType4H323DialString", "");
        // New E164 number for room H323 dial string reservation
        put("newE164Num4H323DialString", "");
        // H323 Alias for room H323 dial string reservation
        put("newH323Alias4H323DialString", "");
        // New Room SIP Dial String Reservation
        // New Device Type for room SIP dial string reservation
        put("newDeviceType4SIPDialString", "");
        // New SIP URI for room H323 dial string reservation
        put("newSipUri4SIPDialString", "");
        // Machine account name
        put("machineAccountName", "");
        // Machine account description
        put("machineAccountDescription", "");
        // Machine account password
        put("machineAcccountPassword", "");
        // Associate the machine account to user or room. False for user or
        // a automatically created new room.
        put("associateMachineAccountToUserOrRoom", "");
        // Associate the machine account to automatically created room or not.
        // True or False.
        put("associateMachineAccountToAutoCreatedRoom", "");
        // Search string for machine account association
        put("searchString4machineAccount", "");
        // Disable the machine account or not
        put("disableMachineAccountOrNot", "");
        // Unlock the machine account or not
        put("unlockMachineAccountOrNot", "");
        put("deviceType", "");
        put("roleName", "");
        put("attributeList", "");
    }

    /**
     * Update the guest
     *
     * @see firstName=ct <br/>
     *      lastName=guest <br/>
     *      email=ct@p.com <br/>
     *      location=beijing <br/>
     *      newfirstName=ct1 <br/>
     *      newlastName=guest1 <br/>
     *      newemail=ct_update@p.com <br/>
     *      newlocation=beijing2 <br/>
     *      dialInOrNot=true <br/>
     *      joinMode=video <br/>
     *      isH323=true <br/>
     *      isSIP=true <br/>
     *      isISDN=false <br/>
     *      sipUri=tao2@p.com <br/>
     *      countryCode=52 <br/>
     *      areaCode=01 <br/>
     *      isdnNum=222 <br/>
     *      extenNum=888 <br/>
     *      useModDialNum=false <br/>
     *      aliasType=IP <br/>
     *      aliasValue=1.1.1.2
     *
     * @param firstName
     *            The first name of guest
     * @param lastName
     *            The last name of guest
     * @param location
     *            The location of guest
     * @param email
     *            The email of guest
     * @param newfirstName
     *            The new first name of guest
     * @param newlastName
     *            The new last name of guest
     * @param newlocation
     *            The new location of guest
     * @param newemail
     *            The new email of guest
     * @param joinMode
     *            Video or Audio
     * @param isH323
     *            Whether support H323
     * @param isSIP
     *            Whether support sip
     * @param isISDN
     *            Whether support ISDN
     * @param aliasType
     *            The alias type
     * @param aliasValue
     *            The alias value
     * @return The result
     */
    public String updateGuest() {
        String status = "Failed";
        String errorMsg = "";
        // Find the specificed guest in the XMA
        JConfGuest toUpdateGuest = null;
        for (final JConfGuest guest : umh.searchGuest(userToken)) {
            if (guest.getFirstName().equals(inputCmd.get("firstName"))
                    && guest.getEmail().equals(inputCmd.get("email"))
                    && guest.getLocation().equals(inputCmd.get("location"))) {
                if (!inputCmd.get("lastName").isEmpty()) {
                    if (guest.getLastName().equals(inputCmd.get("lastName"))) {
                        logger.info("Found the specificed guest on XMA, the guest UGPID is: "
                                + guest.getUgpId() + " and the guest ID is: "
                                + guest.getId());
                        toUpdateGuest = guest;
                        break;
                    }
                } else {
                    logger.info("Found the specificed guest on XMA, the guest UGPID is: "
                            + guest.getUgpId() + " and the guest ID is: "
                            + guest.getId());
                    toUpdateGuest = guest;
                    break;
                }
            }
        }
        if (toUpdateGuest == null) {
            logger.error("Cannot find the specificed guest on XMA");
            return "Failed, Cannot find the specificed guest on XMA";
        }
        // In case user want to change the basic information of the guest
        final String newFirstName = inputCmd.get("newfirstName");
        if (!newFirstName.isEmpty()) {
            toUpdateGuest.setFirstName(newFirstName);
        }
        final String newLastName = inputCmd.get("newlastName");
        if (!newLastName.isEmpty()) {
            toUpdateGuest.setLastName(newLastName);
        }
        final String newEmail = inputCmd.get("newemail");
        if (!newEmail.isEmpty()) {
            toUpdateGuest.setEmail(newEmail);
        }
        final String newLocation = inputCmd.get("newlocation");
        if (!newLocation.isEmpty()) {
            toUpdateGuest.setLocation(newLocation);
        }
        final String dialInOrNot = inputCmd.get("dialInOrNot");
        if (!dialInOrNot.isEmpty()) {
            if (dialInOrNot.equalsIgnoreCase("true")) {
                toUpdateGuest.setDialIn(true);
            } else {
                toUpdateGuest.setDialIn(false);
            }
        }
        final String joinMode = inputCmd.get("joinMode");
        if (!joinMode.isEmpty()) {
            toUpdateGuest.setJoinMode(JSchedPartMode
                    .fromValue(joinMode.toUpperCase()));
        }
        final String isH323 = inputCmd.get("isH323");
        if (!isH323.isEmpty()) {
            if (isH323.equalsIgnoreCase("true")) {
                toUpdateGuest.setSupportsH323(true);
            } else {
                toUpdateGuest.setSupportsH323(false);
            }
        }
        final String isSip = inputCmd.get("isSIP");
        if (!isSip.isEmpty()) {
            if (isSip.equalsIgnoreCase("true")) {
                toUpdateGuest.setSupportsSip(true);
            } else {
                toUpdateGuest.setSupportsSip(false);
            }
        }
        final String isIsdn = inputCmd.get("isISDN");
        if (!isIsdn.isEmpty()) {
            if (isIsdn.equalsIgnoreCase("true")) {
                toUpdateGuest.setSupportsIsdn(true);
            } else {
                toUpdateGuest.setSupportsIsdn(false);
            }
        }
        final String sipUri = inputCmd.get("sipUri");
        if (!sipUri.isEmpty()) {
            toUpdateGuest.setSipUri(sipUri);
        }
        final String countryCode = inputCmd.get("countryCode");
        if (!countryCode.isEmpty()) {
            toUpdateGuest.setCountryCode(countryCode);
        }
        final String areaCode = inputCmd.get("areaCode");
        if (!areaCode.isEmpty()) {
            toUpdateGuest.setAreaCode(areaCode);
        }
        final String isdnNum = inputCmd.get("isdnNum");
        if (!isdnNum.isEmpty()) {
            toUpdateGuest.setIsdnNumber(isdnNum);
        }
        final String extenIP = inputCmd.get("extenNum");
        if (!extenIP.isEmpty()) {
            toUpdateGuest.setExtensionIP(extenIP);
        }
        final String extenSIP = inputCmd.get("extenSIP");
        if (!extenSIP.isEmpty()) {
            toUpdateGuest.setExtensionSIP(extenSIP);
        }
        final String extenISDN = inputCmd.get("extenISDN");
        if (!extenISDN.isEmpty()) {
            toUpdateGuest.setExtensionISDN(extenISDN);
        }
        final String useModDialNum = inputCmd.get("useModDialNum");
        if (!useModDialNum.isEmpty()) {
            if (useModDialNum.equalsIgnoreCase("true")) {
                toUpdateGuest.setModifiedDialNumber(true);
            } else {
                toUpdateGuest.setModifiedDialNumber(false);
            }
        }
        final String modDialNum = inputCmd.get("modDialNum");
        if (!modDialNum.isEmpty()) {
            toUpdateGuest.setModDialNumber(modDialNum);
        }
        JDeviceAlias alias = toUpdateGuest.getAlias();
        final String aliasType = inputCmd.get("aliasType");
        final String aliasValue = inputCmd.get("aliasValue");
        final String extenH323 = inputCmd.get("extenH323");
        if (alias == null && !aliasType.isEmpty()) {
            alias = new JDeviceAlias();
            alias.setDbKey(0);
            alias.setExtension("");
            toUpdateGuest.setAlias(alias);
            toUpdateGuest.setIpAddr("");
        }
        if (!aliasType.isEmpty()) {
            if ("ip".equalsIgnoreCase(aliasType)) {
                alias.setType(JDeviceAliasType.TRANSPORT_ADDRESS);
                toUpdateGuest.setIpAddr(aliasValue);
                toUpdateGuest.setExtensionIP(extenH323);
                toUpdateGuest.setAlias(null);
            } else if ("h323".equalsIgnoreCase(aliasType)) {
                alias.setType(JDeviceAliasType.H_323);
            } else if ("e164".equalsIgnoreCase(aliasType)) {
                alias.setType(JDeviceAliasType.E_164);
            } else if ("annex-o".equalsIgnoreCase(aliasType)) {
                alias.setType(JDeviceAliasType.URL);
            } else {
                logger.error("incorrect alias type, please confirm your input info.");
                return "Failed, incorrect alias type, please confirm your input info.";
            }
        }
        if (!aliasValue.isEmpty()) {
            alias.setValue(aliasValue);
        }
        if (!extenH323.isEmpty()) {
            alias.setExtension(extenH323);
        }
        final JUIUtcDateTime utcTime = new JUIUtcDateTime();
        utcTime.setUnixTime(System.currentTimeMillis());
        toUpdateGuest.setLastUpdateTimestamp(utcTime);
        final List<Field> fields = CommonUtils.getDeclaredFields(toUpdateGuest);
        for (final Field field : fields) {
            Object val = null;
            try {
                field.setAccessible(true);
                val = field.get(toUpdateGuest);
                if (val == null && field.getType() == String.class) {
                    field.set(toUpdateGuest, "");
                }
            } catch (final Exception e) {
                logger.error("Could find the field " + field.getName());
                return "Failed, could find the field " + field.getName();
            }
        }
        final boolean isDuplicated4Guest = umh
                .isDuplicated4Guest(userToken, toUpdateGuest);
        JWebResult result = umh.getResult();
        if (isDuplicated4Guest) {
            if (isDuplicated4Guest) {
                errorMsg = status + ", " + result.getStatus().value()
                        + result.getMessages().get(0);
                logger.error(errorMsg);
                return errorMsg;
            }
        }
        result = umh.editGuest(userToken, toUpdateGuest);
        if (result.getStatus().compareTo(JStatus.SUCCESS) == 0) {
            logger.info("Guest successfully updated into XMA.");
            status = "SUCCESS";
        } else {
            logger.error("Guest failed to update into XMA, please make sure your guest information is correct.");
            status = "Failed";
        }
        return status;
    }

    /**
     * Update the machine account
     *
     * @see machineAccountName=MachineAccount4AutoCreatedRoom <br/>
     *      searchString4machineAccount=localroom1
     *
     * @param machineAccountName
     *            The machine account name
     * @param searchString4machineAccount
     *            The search string
     * @param disableMachineAccountOrNot
     *            Whether disable machine account(Optional)
     * @param unlockMachineAccountOrNot
     *            Whether unlock the machine account(Optional)
     * @return The result
     */
    public String updateMachineAccount() {
        String status = "Failed";
        boolean DisableOrNot = false;
        boolean UnlockOrNot = false;
        String guid = "";
        int maOwnerId = 0;
        if (!inputCmd.get("machineAccountName").isEmpty()) {
            if (!inputCmd.get("searchString4machineAccount").isEmpty()) {
                final List<JUser> userList = umh
                        .getUserAndRooms(userToken,
                                         inputCmd.get("searchString4machineAccount"));
                for (final JUser user : userList) {
                    if (user.getUserName()
                            .equals(inputCmd.get("searchString4machineAccount"))
                            || user.getDisplayName().trim().equals(inputCmd
                                    .get("searchString4machineAccount"))) {
                        guid = user.getGUIDAsString();
                        maOwnerId = user.getUgpId();
                    }
                }
            } else {
                logger.info("Cannot find the search string in the command."
                        + " Will not update the owner of the machind account "
                        + inputCmd.get("machineAccountName"));
            }
            if (!inputCmd.get("disableMachineAccountOrNot").isEmpty()) {
                if (inputCmd.get("disableMachineAccountOrNot")
                        .equalsIgnoreCase("true")) {
                    DisableOrNot = true;
                } else if (inputCmd.get("disableMachineAccountOrNot")
                        .equalsIgnoreCase("false")) {
                    DisableOrNot = false;
                } else {
                    DisableOrNot = false;
                }
            } else {
                logger.info("Cannot find the parameter disableMachineAccountOrNot in the command."
                        + " Will not perform disable or enable the machind account "
                        + inputCmd.get("machineAccountName"));
            }
            if (!inputCmd.get("unlockMachineAccountOrNot").isEmpty()) {
                if (inputCmd.get("unlockMachineAccountOrNot")
                        .equalsIgnoreCase("true")) {
                    UnlockOrNot = true;
                } else if (inputCmd.get("unlockMachineAccountOrNot")
                        .equalsIgnoreCase("false")) {
                    UnlockOrNot = false;
                } else {
                    UnlockOrNot = false;
                }
            } else {
                logger.info("Cannot find the parameter unlockMachineAccountOrNot in the command."
                        + " Will not perform unlock the machind account "
                        + inputCmd.get("machineAccountName"));
            }
            // Update the machine account
            if (umh.updateMachineAccount(userToken,
                                         inputCmd.get("machineAccountName"),
                                         guid,
                                         maOwnerId,
                                         DisableOrNot,
                                         UnlockOrNot)
                    .getStatus().compareTo(JStatus.SUCCESS) == 0) {
                status = "SUCCESS";
                logger.info("Successfully update the machine account "
                        + inputCmd.get("machineAccountName"));
            } else {
                status = "Failed";
                logger.error("Failed to update the machine account "
                        + inputCmd.get("machineAccountName"));
                return status + " Failed to update the machine account "
                        + inputCmd.get("machineAccountName");
            }
        } else {
            status = "Failed";
            logger.error("Please provide a machine account name.");
            return status + " Please provide a machine account name.";
        }
        return status;
    }

    /**
     * Update the specified room. Can update the name, description, site and the
     * dial string. Can also associate maximum 3 endpoints at one time.
     *
     * @see roomName=schemelocalroom1 <br/>
     *      roomEnterpriseType=local <br/>
     *      associateEndpointIP1=$gs5Addr <br/>
     *      associateEndpointType1=GroupSeries <br/>
     *      deviceType=GroupSeries <br/>
     *      newE164Num4H323DialString=3333 <br/>
     *      newH323Alias4H323DialString=testendpoint3H323ID <br/>
     *      newSipUri4SIPDialString=testendpoint3sipuri
     *
     * @param roomName
     *            The room name
     * @param newroomName
     *            The new room name(Optional)
     * @param roomEnterpriseType
     *            Local or AD
     * @param roomDescription
     *            The room description
     * @param associateEndpointIP
     *            The associate the device IP list
     * @param associateEndpointType
     *            The associate the device type list
     * @param deviceType4H323DialString
     *            The device type for h323
     * @param deviceType
     *            The H323 device type
     * @param newE164Num4H323DialString
     *            The new e164 dial string
     * @param newSipUri4SIPDialString
     *            The new sip uri dial string
     * @return The result
     */
    public String updateRoom() {
        String status = "Failed";
        final SiteTopoManagerHandler stmh = new SiteTopoManagerHandler(
                webServiceUrl);
        final DeviceManagerHandler dmh = new DeviceManagerHandler(
                webServiceUrl);
        if (!inputCmd.get("roomName").isEmpty()
                && !inputCmd.get("roomEnterpriseType").isEmpty()) {
            JRoom updateRoom = null;
            for (final JRoom room : umh.getRooms(userToken)) {
                if (room.getName().trim().equals(inputCmd.get("roomName"))) {
                    updateRoom = room;
                    break;
                }
            }
            if (updateRoom == null) {
                status = "Failed";
                logger.error("Please provide the room name and its enterprise type.");
                return status
                        + " Please provide the room name and its enterprise type.";
            }
            updateRoom.setName(updateRoom.getName().trim());
            final List<H323Alias> h323BaseList = umh
                    .getReservedH323Alias(userToken, updateRoom.getDbKey());
            final List<H323Alias> h323Update = h323BaseList;
            List<H323Alias> h323New = null;
            final List<SipAlias> sipBaseList = umh
                    .getReservedSipUri(userToken, updateRoom.getDbKey());
            final List<SipAlias> sipUpdate = sipBaseList;
            List<SipAlias> sipNew = null;
            // update the room name for local room
            if (!inputCmd.get("newRoomName").isEmpty() && inputCmd
                    .get("roomEnterpriseType").equalsIgnoreCase("local")) {
                updateRoom.setName(inputCmd.get("newRoomName"));
            }
            // update the email for local room
            if (!inputCmd.get("newRoomEmail").isEmpty() && inputCmd
                    .get("roomEnterpriseType").equalsIgnoreCase("local")) {
                updateRoom.setEmail(inputCmd.get("newRoomEmail"));
            }
            // update the description of the room
            if (!inputCmd.get("roomDescription").isEmpty()) {
                updateRoom.setDescription(inputCmd.get("roomDescription"));
            }
            // update the site the room belongs to
            if (!inputCmd.get("roomDescription").isEmpty()) {
                final List<JNamePlusUid> list = stmh
                        .getListOfSiteNameUid(userToken,
                                              stmh.getListOfTerritory(userToken));
                for (final JNamePlusUid site : list) {
                    if (site.getName()
                            .equals(inputCmd.get("roomDescription"))) {
                        updateRoom.setSiteUid(site.getUid());
                    }
                }
            }
            final List<JEndpointForList> endpointList = dmh
                    .getUnassignedDynamicDevices(userToken);
            // associate the endpoint1 to the room if available
            if (!inputCmd.get("associateEndpointIP").isEmpty()
                    && !inputCmd.get("associateEndpointType").isEmpty()) {
                final String[] associateEndpointIpList = inputCmd
                        .get("associateEndpointIP").split(",");
                final String[] associateEndpointTypeList = inputCmd
                        .get("associateEndpointType").split(",");
                for (int i = 0; i < associateEndpointIpList.length; i++) {
                    for (final JEndpointForList endpoint : endpointList) {
                        if (endpoint.getModelName()
                                .contains(associateEndpointTypeList[i])
                                && endpoint.getIpAddress()
                                        .equalsIgnoreCase(associateEndpointIpList[i])) {
                            final JDeviceAssociation da = new JDeviceAssociation();
                            da.setDeviceId(endpoint.getDeviceId());
                            da.setDeviceOrder(0);
                            da.setVc2Device(false);
                            updateRoom.getDeviceAssociations().add(da);
                        }
                    }
                }
            }
            // update the H323 stuff
            final String e164Number = inputCmd.get("newE164Num4H323DialString");
            final String deviceType = inputCmd.get("deviceType");
            final String h323Alias = inputCmd
                    .get("newH323Alias4H323DialString");
            if (!e164Number.isEmpty() || !h323Alias.isEmpty()) {
                final int e164 = Integer.parseInt(e164Number);
                try {
                    if (h323Update.isEmpty()) {
                        h323New = new ArrayList<H323Alias>();
                        generateH323AliasList(h323New,
                                              deviceType,
                                              e164,
                                              h323Alias,
                                              updateRoom.getUgpId());
                    } else {
                        generateH323AliasList(h323Update,
                                              deviceType,
                                              e164,
                                              h323Alias,
                                              updateRoom.getUgpId());
                    }
                } catch (IllegalArgumentException
                         | IllegalAccessException
                         | InstantiationException e) {
                    e.printStackTrace();
                }
            }
            // update the SIP stuff
            final String sipuri = inputCmd.get("newSipUri4SIPDialString");
            if (!sipuri.isEmpty()) {
                try {
                    if (sipUpdate.isEmpty()) {
                        sipNew = new ArrayList<SipAlias>();
                        generateSipAliasList(sipNew,
                                             deviceType,
                                             sipuri,
                                             updateRoom.getUgpId());
                    } else {
                        generateSipAliasList(sipUpdate,
                                             deviceType,
                                             sipuri,
                                             updateRoom.getUgpId());
                    }
                } catch (IllegalArgumentException
                         | IllegalAccessException
                         | InstantiationException e) {
                    e.printStackTrace();
                }
            }
            final JWebResult result = umh
                    .updateRoomExt(userToken,
                                   updateRoom,
                                   h323BaseList,
                                   h323Update,
                                   h323New,
                                   sipBaseList,
                                   sipUpdate,
                                   sipNew);
            // Update the room finally
            if (result.getStatus().compareTo(JStatus.SUCCESS) == 0) {
                status = "SUCCESS";
                logger.info("Room " + inputCmd.get("roomName")
                        + " updated successfully.");
            } else {
                status = "Failed";
                logger.error("Failed to update the room "
                        + inputCmd.get("roomName"));
                return status + " Failed to update the room "
                        + inputCmd.get("roomName");
            }
        }
        return status;
    }

    /**
     * Update the user specified
     *
     * @return
     */
    public String updateUser() {
        String status = "Failed";
        final String searchString = inputCmd.get("searchString");
        final String localOrAD = inputCmd.get("userSearchType");
        final List<Integer> deviceIds = new ArrayList<Integer>();
        final List<Integer> groupIds = new ArrayList<Integer>();
        final RoleManagerHandler roleManagerHandler = new RoleManagerHandler(
                webServiceUrl);
        final DeviceManagerHandler dmh = new DeviceManagerHandler(
                webServiceUrl);
        JUser updateUser = null;
        for (final JUser user : umh.getUsers(userToken,
                                             searchString,
                                             localOrAD)) {
            if (user.getUserName().trim()
                    .equals(inputCmd.get("searchString"))) {
                updateUser = user;
                break;
            }
        }
        if (updateUser == null) {
            status = "Failed";
            logger.error("Please provide the user name and its enterprise type.");
            return status
                    + " Please provide the user name and its enterprise type.";
        }
        try {
            final List<H323Alias> h323BaseList = umh
                    .getReservedH323Alias(userToken, updateUser.getUgpId());
            final List<H323Alias> h323Update = new ArrayList<H323Alias>();
            final List<H323Alias> h323New = new ArrayList<H323Alias>();
            final List<H323Alias> h323Delete = new ArrayList<H323Alias>();
            final List<SipAlias> sipBaseList = umh
                    .getReservedSipUri(userToken, updateUser.getUgpId());
            final List<SipAlias> sipUpdate = new ArrayList<SipAlias>();
            final List<SipAlias> sipNew = new ArrayList<SipAlias>();
            final List<SipAlias> sipDelete = new ArrayList<SipAlias>();
            // update the user name for local user
            if (!inputCmd.get("newUserName").isEmpty() && inputCmd
                    .get("userSearchType").equalsIgnoreCase("local")) {
                updateUser.setUserName(inputCmd.get("newUserName"));
            }
            if (!inputCmd.get("newfirstName").isEmpty() && inputCmd
                    .get("userSearchType").equalsIgnoreCase("local")) {
                updateUser.setFirstName(inputCmd.get("newfirstName"));
            }
            if (!inputCmd.get("newlastName").isEmpty() && inputCmd
                    .get("userSearchType").equalsIgnoreCase("local")) {
                updateUser.setLastName(inputCmd.get("newlastName"));
            }
            // update the email for local user
            if (!inputCmd.get("newemail").isEmpty() && inputCmd
                    .get("userSearchType").equalsIgnoreCase("local")) {
                updateUser.setEmail(inputCmd.get("newemail"));
            }
            // update the location for local user
            if (!inputCmd.get("newlocation").isEmpty() && inputCmd
                    .get("userSearchType").equalsIgnoreCase("local")) {
                updateUser.setCity(inputCmd.get("newlocation"));
            }
            // update the password for local user
            if (!inputCmd.get("newPassword").isEmpty() && inputCmd
                    .get("userSearchType").equalsIgnoreCase("local")) {
                updateUser
                        .setLoginStatus(JUserLoginStatus.PASSWORD___CHANGE___REQUIRED);
                updateUser.setPassword(inputCmd.get("newPassword"));
            }
            // update the title for local user
            if (!inputCmd.get("newTitle").isEmpty() && inputCmd
                    .get("userSearchType").equalsIgnoreCase("local")) {
                updateUser.setTitle(inputCmd.get("newTitle"));
            }
            // update the department for local user
            if (!inputCmd.get("newDepartment").isEmpty() && inputCmd
                    .get("userSearchType").equalsIgnoreCase("local")) {
                updateUser.setDepartment(inputCmd.get("newDepartment"));
            }
            // update the phone number for local user
            if (!inputCmd.get("newPhoneNumber").isEmpty() && inputCmd
                    .get("userSearchType").equalsIgnoreCase("local")) {
                updateUser.setPhoneNumber(inputCmd.get("newPhoneNumber"));
            }
            final List<JEndpointForList> endpointList = dmh
                    .getUnassignedDynamicDevices(userToken);
            // associate the endpoint1 to the room if available
            if (!inputCmd.get("associateEndpointIP").isEmpty()
                    && !inputCmd.get("associateEndpointType").isEmpty()) {
                final String[] associateEndpointIpList = inputCmd
                        .get("associateEndpointIP").split(",");
                final String[] associateEndpointTypeList = inputCmd
                        .get("associateEndpointType").split(",");
                for (int i = 0; i < associateEndpointIpList.length; i++) {
                    for (final JEndpointForList endpoint : endpointList) {
                        if (endpoint.getModelName()
                                .contains(associateEndpointTypeList[i])
                                && endpoint.getIpAddress()
                                        .equalsIgnoreCase(associateEndpointIpList[i])) {
                            final JDeviceAssociation da = new JDeviceAssociation();
                            da.setDeviceId(endpoint.getDeviceId());
                            da.setDeviceOrder(0);
                            da.setVc2Device(false);
                            updateUser.getDeviceAssociations().add(da);
                            deviceIds.add(endpoint.getDeviceId());
                        }
                    }
                }
            }
            // update the H323 stuff
            final String[] e164Numbers = inputCmd
                    .get("newE164Num4H323DialString").split(",");
            final String[] deviceTypes = inputCmd.get("deviceType").split(",");
            final String[] h323Aliases = inputCmd
                    .get("newH323Alias4H323DialString").split(",");
            if (!inputCmd.get("newE164Num4H323DialString").isEmpty()
                    || !inputCmd.get("newH323Alias4H323DialString").isEmpty()) {
                for (int i = 0; i < deviceTypes.length; i++) {
                    final String deviceType = deviceTypes[i];
                    final String e164Number = inputCmd
                            .get("newE164Num4H323DialString").isEmpty() ? ""
                                    : e164Numbers[i];
                    final String h323Alias = inputCmd
                            .get("newH323Alias4H323DialString").isEmpty() ? ""
                                    : h323Aliases[i];
                    final int e164 = Integer.parseInt(e164Number);
                    try {
                        int hitIndex = -1;
                        for (int j = 0; j < h323BaseList.size(); j++) {
                            if (deviceType.replace("RPDesktop", "RP-Desktop")
                                    .equalsIgnoreCase(h323BaseList.get(j)
                                            .getDeviceType()
                                            .getDisplayName())) {
                                h323Update.add(h323BaseList.get(j));
                                hitIndex = j;
                            }
                        }
                        if (hitIndex == -1) {
                            generateH323AliasList(h323New,
                                                  deviceType,
                                                  e164,
                                                  h323Alias,
                                                  updateUser.getUgpId());
                        } else {
                            generateH323AliasList(h323Update,
                                                  deviceType,
                                                  e164,
                                                  h323Alias,
                                                  updateUser.getUgpId());
                        }
                    } catch (IllegalArgumentException
                             | IllegalAccessException
                             | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
            // update the SIP stuff
            final String[] sipuris = inputCmd.get("newSipUri4SIPDialString")
                    .split(",");
            if (!inputCmd.get("newSipUri4SIPDialString").isEmpty()) {
                for (int i = 0; i < sipuris.length; i++) {
                    final String sipuri = sipuris[i];
                    final String deviceType = deviceTypes[i];
                    try {
                        int hitIndex = -1;
                        for (int j = 0; j < sipBaseList.size(); j++) {
                            if (deviceType.replace("RPDesktop", "RP-Desktop")
                                    .equalsIgnoreCase(sipBaseList.get(j)
                                            .getDeviceType()
                                            .getDisplayName())) {
                                sipUpdate.add(sipBaseList.get(j));
                                hitIndex = j;
                            }
                        }
                        if (hitIndex == -1) {
                            generateSipAliasList(sipNew,
                                                 deviceType,
                                                 sipuri,
                                                 updateUser.getUgpId());
                        } else {
                            generateSipAliasList(sipUpdate,
                                                 deviceType,
                                                 sipuri,
                                                 updateUser.getUgpId());
                        }
                    } catch (IllegalArgumentException
                             | IllegalAccessException
                             | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
            // update the role stuff
            final List<JRole> availRole = roleManagerHandler
                    .getRoles(userToken);
            final String[] userRoleList = inputCmd.get("userRoleList")
                    .split(",");
            for (final String userRole : userRoleList) {
                for (final JRole role : availRole) {
                    if (role.getGroupName()
                            .equalsIgnoreCase(userRole.replaceAll("~", " "))) {
                        final JGroup ug = new JGroup();
                        ug.setBelongsToAreaUgpId(0);
                        ug.setDN(role.getDN());
                        ug.setDbKey(role.getDbKey());
                        ug.setDomain(role.getDomain());
                        ug.setEnterpriseType(JEnterpriseType.LOCAL);
                        ug.setGUID(role.getGUID());
                        ug.setGUIDAsString(role.getGUIDAsString());
                        ug.setUgpId(role.getUgpId());
                        ug.setDisplayname(role.getGroupName());
                        ug.setGroupName(role.getGroupName());
                        ug.setGroupType(JGroupType.ROLE);
                        ug.setIsDefaultGroup(role.isIsDefaultGroup());
                        ug.setIsInherited(role.isIsInherited());
                        ug.setVisible(role.isVisible());
                        ug.setPolicyId(role.getPolicyId());
                        updateUser.getGroupAssociations().add(ug);
                        groupIds.add(ug.getUgpId());
                    }
                }
            }
            final JWebResult result = umh
                    .updateUserExt(userToken,
                                   updateUser,
                                   groupIds,
                                   deviceIds,
                                   h323BaseList,
                                   h323Update,
                                   h323New,
                                   h323Delete,
                                   sipBaseList,
                                   sipUpdate,
                                   sipNew,
                                   sipDelete);
            // Update the user finally
            if (result.getStatus().compareTo(JStatus.SUCCESS) == 0) {
                status = "SUCCESS";
                logger.info("User " + inputCmd.get("searchString")
                        + " updated successfully.");
            } else {
                status = "Failed";
                logger.error("Failed to update the user "
                        + inputCmd.get("searchString"));
                return status + " Failed to update the user "
                        + inputCmd.get("searchString");
            }
        } catch (final Exception e) {
            logger.error("Found exception when trying to update the specified user "
                    + searchString + " with the error message "
                    + e.getMessage());
            e.printStackTrace();
            status = "Failed";
            return status
                    + " Found exception when trying to update the specified user "
                    + searchString + " with the error message "
                    + e.getMessage();
        }
        return status;
    }

    /**
     * Update the specified user.Can associate endpoints.
     *
     * @see userName=debuguser10 <br/>
     *      userDomain=LOCAL <br/>
     *      deviceName=$deviceName
     *
     * @param userName
     *            The user name
     * @param userDomain
     *            AD or Local
     * @param deviceName
     *            The device name
     *
     * @return The result
     */
    public String updateUserForDevice() {
        String status = "Failed";
        final DeviceManagerHandler dmh = new DeviceManagerHandler(
                webServiceUrl);
        final List<Integer> deviceIds = new ArrayList<Integer>();
        final String userName = inputCmd.get("userName");
        final String deviceName = inputCmd.get("deviceName");
        final String userDomain = inputCmd.get("userDomain");
        if (!userName.isEmpty() && !deviceName.isEmpty()) {
            List<JUser> usersList = umh.getUsers(userToken,
                                                 userName,
                                                 userDomain);
            if (usersList.isEmpty()) {
                usersList = umh.findConferenceParticipants(userToken,
                                                           "",
                                                           userName);
            }
            if (usersList.isEmpty()) {
                usersList = umh.findConferenceParticipants(userToken,
                                                           userName,
                                                           "");
            }
            for (final JUser user : usersList) {
                if (user.getDisplayName().trim().equals(userName)
                        || user.getUserName().trim().equals(userName)) {
                    for (final JEndpointForList endpoint : dmh
                            .getEndpoints4Paging(userToken, 200)) {
                        if (endpoint.getDeviceName().equals(deviceName)) {
                            deviceIds.add(new Integer(endpoint.getDeviceId()));
                        }
                    }
                    // updateUserForDevice finally
                    if (umh.updateUserForDevice(userToken, user, deviceIds)
                            .getStatus().compareTo(JStatus.SUCCESS) == 0) {
                        status = "SUCCESS";
                        logger.info("User " + userName
                                + " associate with device " + deviceName
                                + " successfully.");
                    } else {
                        status = "Failed";
                        logger.error("User " + userName
                                + " associate with device " + deviceName
                                + " failed.");
                        return status + "User " + userName
                                + " associate with device " + deviceName
                                + " failed.";
                    }
                }
            }
        } else {
            status = "Failed";
            logger.error("Please provide the user name and device name.");
            return status + "Please provide the user name and device name.";
        }
        return status;
    }
}
