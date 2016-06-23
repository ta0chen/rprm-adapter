package com.polycom.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.ConferenceServiceHandler;
import com.polycom.sqa.xma.webservices.driver.NetworkDeviceManagerHandler;
import com.polycom.sqa.xma.webservices.driver.SchedulerEngineManagerHandler;
import com.polycom.sqa.xma.webservices.driver.UserManagerHandler;
import com.polycom.webservices.ConferenceService.ExternalCmaOngoingConference;
import com.polycom.webservices.ConferenceService.JAnytimeConference;
import com.polycom.webservices.ConferenceService.JCMConferenceStatus;
import com.polycom.webservices.ConferenceService.JLeanConference;
import com.polycom.webservices.SchedulerEngine.JCMConferenceConnectionType;
import com.polycom.webservices.SchedulerEngine.JCMConferenceMediaType;
import com.polycom.webservices.SchedulerEngine.JCallDirection;
import com.polycom.webservices.SchedulerEngine.JConferencePolicy;
import com.polycom.webservices.SchedulerEngine.JConferencePolicyAttribute;
import com.polycom.webservices.SchedulerEngine.JCredentials;
import com.polycom.webservices.SchedulerEngine.JDeviceAlias;
import com.polycom.webservices.SchedulerEngine.JDeviceAliasType;
import com.polycom.webservices.SchedulerEngine.JSchedBridgeSelectionType;
import com.polycom.webservices.SchedulerEngine.JSchedConf;
import com.polycom.webservices.SchedulerEngine.JSchedConnectionType;
import com.polycom.webservices.SchedulerEngine.JSchedDevice;
import com.polycom.webservices.SchedulerEngine.JSchedPart;
import com.polycom.webservices.SchedulerEngine.JSchedPartMode;
import com.polycom.webservices.SchedulerEngine.JSchedRecurrence;
import com.polycom.webservices.SchedulerEngine.JSchedRecurrenceType;
import com.polycom.webservices.SchedulerEngine.JSchedResult;
import com.polycom.webservices.SchedulerEngine.JSchedUIType;
import com.polycom.webservices.SchedulerEngine.JSchedulerSettings;
import com.polycom.webservices.SchedulerEngine.JStatus;
import com.polycom.webservices.SchedulerEngine.JUIUtcDateTime;
import com.polycom.webservices.SchedulerEngine.JWebResult;
import com.polycom.webservices.UserManager.JConfGuest;
import com.polycom.webservices.UserManager.JDeviceAssociation;
import com.polycom.webservices.UserManager.JUser;

/**
 * Schedule Conference handler. This class will handle the webservice request of
 * Schedule Conference module
 *
 * @author wbchao
 *
 */
public class SchedulerEngineHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        // final String method = "deleteRecurringConf ";
        final String method = "scheduleDmaConference ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final Date date = new Date();
        final long startTime = date.getTime();
        final String startTimeStr = Long.toString(startTime);
        final long endTime = startTime + 3600000;
        final String endTimeStr = Long.toString(endTime);
        // final String params1 =
        // "conferenceName=test2 conferenceTemplate=Factory~Template startTime="
        // + startTimeStr
        // + " endTime="
        // + endTimeStr
        // + " conferenceType=poolConference conferenceRoomId=7777 "
        // +
        // "ownerName=h323ipguest1~auto
        // partNameList=h323ipguest1~auto,h323ipguest2~auto,isdnuser1~abc,isdnuser2~def
        // "
        // + "partTypeList=guest,guest,user,user "
        // + "partDialDirectionList=dialout,dialout,dialout,dialout "
        // + "partMediaTypeList=video,video,video,video "
        // + "partDialTypeList=h323,h323,isdn,isdn "
        // + "deviceIpList=10.220.202.240,172.21.126.45,5275006,5275005:2222 ";
        final String params1 = "conferenceName=SanofiPoolConf conferenceType=poolConference conferenceRoomId=1998"
                + " conferenceTemplate=Factory~Template" + " startTime="
                + startTimeStr + " endTime=" + endTimeStr
                + " ownerName=debuguser8~hhh     partNameList=debuguser8~hhh,isdnuser2~def,lroom,sipsecond~guest2,sip~guest     partTypeList=user,user,room,guest,guest     partDialDirectionList=dialin,dialout,dialin,dialout,dialin     partMediaTypeList=video,video,video,video,audio     deviceIpList=10.220.202.240,5275023,172.21.113.245,debuguser11@123.com,sipgues1t@123.com     partDialTypeList=H323,ISDN,H323,SIP,SIP     partLineRateList=384,384,1472,1024,64";
        // final String params2 = "pinLength=4";
        // final String params3 = "conferenceName=poolConf";
        // " ownerName=lingxi1~temp1 conferenceType=poolConference
        // recurrenceType=daily dayOfMask=0111110 recurrenceNum=0
        // endDate=2015,2,15 partNameList=lingxi1~temp1,lingxi2~temp2
        // partMediaTypeList=video,video partTypeList=user,user
        // partDialDirectionList=dialout,dialout partDialTypeList=h323,h323
        // deviceIpList=172.21.98.119,10.230.111.57 ";
        // final String params1 =
        // "conferenceName=autoconfapitest conferenceTemplate=Factory~Template
        // startTime="
        // + startTimeStr
        // + " endTime="
        // + endTimeStr
        // +
        // " ownerName=lingxi1~temp1 conferenceType=poolConference
        // chairPerson=lingxi1~temp1 chairpersonPassword=1234
        // conferencePassword=3214 recurrenceType=daily dayOfMask=0111110
        // recurrenceNum=0 endDate=2015,2,15
        // partNameList=lingxi1~temp1,lingxi2~temp2
        // partMediaTypeList=video,video partTypeList=user,user
        // partDialDirectionList=dialin,dialin partDialTypeList=h323,h323
        // deviceIpList=172.21.98.119,10.230.111.57 ";
        // final String params1 =
        // "conferenceName=autoconftest conferenceTemplate=Default~Template
        // conferencePassword=3214 chairpersonPassword=1234 startTime="
        // + startTimeStr
        // + " endTime="
        // + endTimeStr
        // +
        // " ownerName=debuguser~aaa chairPerson=debuguser~aaa
        // conferenceType=recurringConf recurrenceType=weekly dayOfMask=0010000
        // recurrenceNum=3 endDate=2014,11,10
        // partNameList=debuguser~aaa,debuguser2~bbb partTypeList=user,user
        // partDialDirectionList=dialout,dialout partDialTypeList=sip,h323
        // deviceIpList=172.21.126.83,172.21.126.253";
        // final String params2 =
        // "conferenceType=anytime conferenceTemplate=Factory~Template
        // startTime="
        // + startTimeStr
        // + " endTime="
        // + endTimeStr
        // +
        // " ownerName=debuguser10~jjj
        // partNameList=debuguser10~jjj,adroom1,zhao~lingxi
        // partTypeList=user,room,guest
        // partDialDirectionList=dialin,dialout,dialout
        // partDialTypeList=sip,h323_id,h323
        // deviceIpList=10.230.110.213,172.21.126.82,172.21.126.83 ";
        // final String params3 =
        // "conferenceName=autoconftest
        // conferenceTemplate=Default~Audio~Template conferencePassword=3214
        // chairpersonPassword=1234 startTime="
        // + startTimeStr
        // + " endTime="
        // + endTimeStr
        // +
        // " ownerName=debuguser~aaa chairPerson=debuguser~aaa mediaType=audio
        // partNameList=debuguser~aaa,debuguser2~bbb partTypeList=user,user
        // partDialDirectionList=dialin,dialin partDialTypeList=sip,h323
        // deviceIpList=172.21.126.83,172.21.126.253";
        // final String params4 =
        // "conferenceName=autoconftest conferenceTemplate=Default~Template
        // conferencePassword=3214 chairpersonPassword=1234 startTime="
        // + startTimeStr
        // + " endTime="
        // + endTimeStr
        // +
        // " ownerName=debuguser~aaa chairPerson=debuguser~aaa mediaType=video
        // partNameList=debuguser~aaa,h323e164~guest partTypeList=user,guest
        // partDialDirectionList=dialout,dialout partDialTypeList=sip,h323
        // deviceIpList=172.21.126.84,172.21.98.16";
        // final String params5 =
        // "conferenceName=IsdnGuest conferenceTemplate=Default~Template
        // conferenceID=1236 startTime="
        // + startTimeStr
        // + " endTime="
        // + endTimeStr
        // +
        // " maxSpeed=384 conferenceDuration=60 ownerName=debuguser~aaa
        // confMediaType=video
        // partNameList=debuguser~aaa,debuguser2~bbb,guest1~auto,guest2~auto
        // partTypeList=user,user,guest,guest
        // partDialDirectionList=dialout,dialout,dialout,dialout
        // partMediaTypeList=video,video,audio,video
        // partDialTypeList=h323,h323,isdn,isdn
        // deviceIpList=172.21.126.80,172.21.126.81,5275006,5275018 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JSchedulerEngine SchedulerEngineManager "
                + method + auth + params1;
        final SchedulerEngineHandler handler = new SchedulerEngineHandler(
                command);
        final String result = handler.build();
        System.out.println("result==" + result);
    }

    private final SchedulerEngineManagerHandler seh;
    private final ConferenceServiceHandler      csh;

    public SchedulerEngineHandler(final String cmd) throws IOException {
        super(cmd);
        seh = new SchedulerEngineManagerHandler(webServiceUrl);
        csh = new ConferenceServiceHandler(webServiceUrl);
    }

    /**
     * Internal method, convert the guest to JSchedPart
     *
     * @param umh
     *            The user manager handler
     * @param guest
     *            The guest object
     * @param index
     *            The index
     * @return JSchedPart
     */
    private JSchedPart convertGuestToJSchedPart(final UserManagerHandler umh,
                                                final JConfGuest guest,
                                                final int index) {
        final JSchedPart schedPart = new JSchedPart();
        final JSchedDevice schedDevice = new JSchedDevice();
        schedPart.setAllDevicesOutOfArea(false);
        schedPart.setBelongsToAreaUgpId(0);
        final String[] directionList = inputCmd.get("partDialDirectionList")
                .split(",");
        final String[] encryptionList = inputCmd.get("partEncryptList")
                .split(",");
        final String[] lineRateList = inputCmd.get("partLineRateList")
                .split(",");
        if (directionList[index].equals("dialin")) {
            schedPart.setCallDirection(JCallDirection.INCOMING);
            schedDevice.setDialIn(true);
        } else {
            schedPart.setCallDirection(JCallDirection.OUTGOING);
            schedDevice.setDialIn(false);
        }
        schedPart.setCascadeLink(false);
        schedPart.setChairperson(false);
        schedPart.setConfGuestId(guest.getId());
        schedPart.setDeviceEntityId_Dont_Use(0);
        schedPart.setEmail(guest.getEmail());
        if (encryptionList.length > index) {
            setPartEncryptionInfo(schedPart, encryptionList[index]);
        } else {
            setPartEncryptionInfo(schedPart, "auto");
        }
        schedPart.setFirstName(guest.getFirstName());
        schedPart.setGroup(false);
        schedPart.setGuest(true);
        schedPart.setITPMaster(false);
        schedPart.setLastName(guest.getLastName());
        schedPart.setLecturer(false);
        schedPart
                .setMode(JSchedPartMode.fromValue(guest.getJoinMode().value()));
        schedPart.setOperator(false);
        schedPart.setOwner(false);
        schedPart.setOwnerUGPId(guest.getUgpId());
        schedPart.setParentDeviceId(0);
        schedPart.setRoom(false);
        schedPart.setServiceName("##ANYMCUSERVICE##");
        schedPart.setVip(false);
        schedPart.setVirtualParticipant(false);
        schedPart.setGuestBookLocation(guest.getLocation());
        schedDevice.setBehindFirewall(false);
        schedDevice.setBelongsToAreaUgpId(0);
        schedDevice.setDeviceId(-1);
        schedDevice.setH323Capable(true);
        schedDevice.setIpAddress(guest.getIpAddr());
        schedDevice.setIpExtension(guest.getExtensionIP());
        schedDevice.setIsdnAreaCode(guest.getAreaCode());
        schedDevice.setIsdnCapable(true);
        schedDevice.setIsdnCountryCode(guest.getCountryCode());
        schedDevice.setIsdnExtension(guest.getExtensionISDN());
        if (guest.isModifiedDialNumber()) {
            schedDevice.setIsdnFullNumber(guest.getModDialNumber());
        } else {
            schedDevice.setIsdnFullNumber(guest.getCountryCode() + "("
                    + guest.getAreaCode() + ")" + guest.getIsdnNumber());
        }
        schedDevice.setIsdnPhoneNumber(guest.getIsdnNumber());
        schedDevice.setName(guest.getFirstName() + " " + guest.getLastName());
        schedDevice.setOwnerUgpId(0);
        if (guest.isSupportsH323()) {
            if (guest.getAlias() == null) {
                schedDevice
                        .setPreferredConnectionType(JSchedConnectionType.H_323);
                schedPart.setExtention(guest.getExtensionIP());
            } else {
                final JDeviceAlias deviceAlias = new JDeviceAlias();
                if (guest.getAlias().getType().value()
                        .equals(JDeviceAliasType.TRANSPORT_ADDRESS.value())) {
                    schedDevice
                            .setPreferredConnectionType(JSchedConnectionType.H_323);
                } else if (guest.getAlias().getType().value()
                        .equals(JDeviceAliasType.E_164.value())) {
                    schedDevice
                            .setPreferredConnectionType(JSchedConnectionType.H_323___E_164);
                } else if (guest.getAlias().getType().value()
                        .equals(JDeviceAliasType.H_323.value())) {
                    schedDevice
                            .setPreferredConnectionType(JSchedConnectionType.H_323___ID);
                } else if (guest.getAlias().getType().value()
                        .equals(JDeviceAliasType.URL.value())) {
                    schedDevice
                            .setPreferredConnectionType(JSchedConnectionType.H_323___ANNEX___O);
                }
                try {
                    CommonUtils.copyProperties(guest.getAlias(), deviceAlias);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                deviceAlias.setDbKey(new Integer(0));
                deviceAlias.setDeviceEntityId(0);
                deviceAlias.setSourceId(null);
                schedDevice.getAliasList().add(deviceAlias);
                schedPart.setExtention(guest.getAlias().getExtension());
            }
        } else if (guest.isSupportsIsdn()) {
            schedDevice.setPreferredConnectionType(JSchedConnectionType.ISDN);
            schedPart.setExtention(guest.getExtensionISDN());
        } else {
            schedDevice.setPreferredConnectionType(JSchedConnectionType.SIP);
            schedPart.setExtention(guest.getExtensionSIP());
        }
        schedDevice.setScheduledDevice(true);
        schedDevice.setSipCapable(true);
        schedDevice.setSipUri(guest.getSipUri());
        schedDevice.setVip(false);
        schedDevice.setAllowedOnRMX(false);
        schedDevice.setSiteLatitude(Double.NaN);
        schedDevice.setSiteLongitude(Double.NaN);
        schedDevice.setRequiredMcuService("##ANYMCUSERVICE##");
        final String maxSpeed = inputCmd.get("maxSpeed");
        int maxSpeedInt = 10240;
        int speedInt = 0;
        if (inputCmd.get("conferenceType").equalsIgnoreCase("anytime")
                || inputCmd.get("conferenceType")
                        .equalsIgnoreCase("poolConference")
                || inputCmd.get("conferenceType")
                        .equalsIgnoreCase("recurringPoolConf")) {
        	speedInt = -1;
        } else {
        	speedInt = 384;
        }
        if (!maxSpeed.isEmpty()) {
            maxSpeedInt = Integer.parseInt(maxSpeed);
        }
        if (!inputCmd.get("partLineRateList").isEmpty()
                && lineRateList.length > index) {
        	speedInt = Integer.parseInt(lineRateList[index]);
        }
        schedDevice.setMaxSpeedIP(maxSpeedInt);
        schedDevice.setMaxSpeedISDN(maxSpeedInt);
        schedDevice.setVideoSpeed(speedInt);
        schedPart.getSchedDeviceCollection().add(schedDevice);
        return schedPart;
    }

    /**
     * Internal method, convert the user/room to JSchedPart
     *
     * @param umh
     *            The user manager handler
     * @param guest
     *            The user object
     * @param index
     *            The index
     * @return JSchedPart
     */
    private JSchedPart convertUserOrRoomToJSchedPart(final JUser user,
                                                     final int index) {
    	int defaultVideoSpeed = 0;
        final NetworkDeviceManagerHandler ndmh = new NetworkDeviceManagerHandler(
                webServiceUrl);
        final String[] participantList = inputCmd.get("partNameList")
                .split(",");
        final String[] directionList = inputCmd.get("partDialDirectionList")
                .split(",");
        final String[] deviceIpList = inputCmd.get("deviceIpList").split(",");
        final String[] dialTypeList = inputCmd.get("partDialTypeList")
                .split(",");
        final String[] encryptionList = inputCmd.get("partEncryptList")
                .split(",");
        final String[] lineRateList = inputCmd.get("partLineRateList")
                .split(",");
        final String[] mediaList = inputCmd.get("partMediaTypeList").split(",");
        final Integer ugpid = user.getUgpId();
        final JSchedPart schedPart = seh.getSchedPartByUgpId(userToken,
                                                             ugpid.intValue());
        if (directionList[index].equals("dialout")) {
            schedPart.setCallDirection(JCallDirection.OUTGOING);
        } else if (directionList[index].equals("dialin")) {
            schedPart.setCallDirection(JCallDirection.INCOMING);
        }
        if (participantList[index].equals(inputCmd.get("ownerName"))) {
            schedPart.setOwner(true);
        }
        if (participantList[index].equals(inputCmd.get("chairPerson"))) {
            schedPart.setChairperson(true);
        }
        if (inputCmd.get("conferenceType").equalsIgnoreCase("anytime")
                || inputCmd.get("conferenceType")
                        .equalsIgnoreCase("poolConference")
                || inputCmd.get("conferenceType")
                        .equalsIgnoreCase("recurringPoolConf")) {
        	defaultVideoSpeed = -1;
        } else {
        	defaultVideoSpeed = 384;
        }
        schedPart.setServiceName("##ANYMCUSERVICE##");
        if (!inputCmd.get("conferenceType").equalsIgnoreCase("anytime")
                && !inputCmd.get("conferenceType")
                        .equalsIgnoreCase("poolConference")
                && !inputCmd.get("conferenceType")
                        .equalsIgnoreCase("recurringPoolConf")) {
            ndmh.getAvailableBridges(userToken);
            schedPart.setMcuId(ndmh.getAvailableMCU().iterator().next()
                    .getDeviceUUID());
            schedPart.setDeviceEntityId_Dont_Use(ndmh.getAvailableMCU()
                    .iterator().next().getDeviceId());
        }
        if (mediaList.length > index) {
            if (mediaList[index].equalsIgnoreCase("video")) {
                schedPart.setMode(JSchedPartMode.VIDEO);
            } else if (mediaList[index].equalsIgnoreCase("audio")) {
                schedPart.setMode(JSchedPartMode.AUDIO);
            } else if (mediaList[index].equalsIgnoreCase("person")) {
                schedPart.setMode(JSchedPartMode.IN___PERSON);
            } else {
                schedPart.setMode(JSchedPartMode.VIDEO);
            }
        } else {
            schedPart.setMode(JSchedPartMode.VIDEO);
        }
        if (inputCmd.get("confMediaType").equalsIgnoreCase("audio")) {
            schedPart.setMediaType(JCMConferenceMediaType.AUDIO);
        } else {
            schedPart.setMediaType(JCMConferenceMediaType.AUDIO_VIDEO);
        }
        for (int j = 0; j < schedPart.getSchedDeviceCollection().size(); j++) {
            if (schedPart.getSchedDeviceCollection().get(j).getIpAddress()
                    .equals(deviceIpList[index])) {
                if (directionList[index].equals("dialout")) {
                    schedPart.getSchedDeviceCollection().get(j)
                            .setDialIn(false);
                } else {
                    schedPart.getSchedDeviceCollection().get(j).setDialIn(true);
                }
                schedPart.getSchedDeviceCollection().get(j)
                        .setScheduledDevice(true);
                schedPart.getSchedDeviceCollection().get(j)
                        .setPreferredConnectionType(JSchedConnectionType
                                .fromValue(dialTypeList[index].toUpperCase()));
                schedPart.getSchedDeviceCollection().get(j)
                        .setIsdnFullNumber(deviceIpList[j]);
            	if (mediaList.length > index && mediaList[index].equalsIgnoreCase("audio")) {
            		schedPart.getSchedDeviceCollection().get(j).setVideoSpeed(0);
            	} else {
                    if (!inputCmd.get("partLineRateList").isEmpty()
                        && lineRateList.length > index) {
                	    schedPart.getSchedDeviceCollection().get(j)
                            .setVideoSpeed(Integer.valueOf(lineRateList[index])
                                    .intValue());
                	} else {
                		schedPart.getSchedDeviceCollection().get(j).setVideoSpeed(defaultVideoSpeed);
                	}
                }
                schedPart.getSchedDeviceCollection().get(j).setAllowedOnRMX(false);
                break;
            }
        }
        if (encryptionList.length > index) {
            setPartEncryptionInfo(schedPart, encryptionList[index]);
        } else {
            setPartEncryptionInfo(schedPart, "auto");
        }
        // Add this part for isdn user test scene
        if (dialTypeList[index].equalsIgnoreCase("isdn")) {
            final String[] numberList = inputCmd.get("deviceIpList").split(",");
            final String[] numbers = numberList[index].split(":");
            final String number = numbers[0];
            String extention = "";
            if (numbers.length == 2) {
                extention = numbers[1];
            }
            schedPart.setExtention(extention);
            final List<JSchedDevice> deviceList = schedPart
                    .getSchedDeviceCollection();
            deviceList.get(0)
                    .setPreferredConnectionType(JSchedConnectionType.ISDN);
            schedPart.setExtention(extention);
            deviceList.get(0).setIsdnCapable(true);
            deviceList.get(0).setIsdnExtension(extention);
            deviceList.get(0).setIsdnFullNumber(number);
            deviceList.get(0).setScheduledDevice(true);
            deviceList.get(0).setAllowedOnRMX(false);
            deviceList.get(0).setSiteName("");
            deviceList.get(0).setSiteUid("");
        }
        return schedPart;
    }

    /**
     * Internal method, create temp guest participant
     *
     * @param index
     *            The index
     * @return JSchedPart
     */
    private JSchedPart createTempGuestPart(final int index) {
        final JSchedPart schedPart = new JSchedPart();
        final JSchedDevice schedDevice = new JSchedDevice();
        schedPart.setAllDevicesOutOfArea(false);
        schedPart.setBelongsToAreaUgpId(0);
        final String[] nameList = inputCmd.get("partNameList").split(",");
        final String[] directionList = inputCmd.get("partDialDirectionList")
                .split(",");
        final String[] dialTypeList = inputCmd.get("partDialTypeList")
                .split(",");
        final String[] mediaTypeList = inputCmd.get("partMediaTypeList")
                .split(",");
        final String[] numberList = inputCmd.get("deviceIpList").split(",");
        final String[] encryptionList = inputCmd.get("partEncryptList")
                .split(",");
        final String[] lineRateList = inputCmd.get("partLineRateList")
                .split(",");
        if (directionList[index].equals("dialin")) {
            schedPart.setCallDirection(JCallDirection.INCOMING);
            schedDevice.setDialIn(true);
        } else {
            schedPart.setCallDirection(JCallDirection.OUTGOING);
            schedDevice.setDialIn(false);
        }
        schedPart.setCascadeLink(false);
        schedPart.setChairperson(false);
        schedPart.setConfGuestId(0);
        schedPart.setDeviceEntityId_Dont_Use(0);
        schedPart.setEmail(nameList[index].split("~")[0]
                + nameList[index].split("~")[1] + "@123.com");
        if (encryptionList.length > index) {
            setPartEncryptionInfo(schedPart, encryptionList[index]);
        } else {
            setPartEncryptionInfo(schedPart, "auto");
        }
        schedPart.setFirstName(nameList[index].split("~")[0]);
        schedPart.setGroup(false);
        schedPart.setGuest(true);
        schedPart.setITPMaster(false);
        schedPart.setLastName(nameList[index].split("~")[1]);
        schedPart.setLecturer(false);
        schedPart.setMode(JSchedPartMode
                .valueOf(mediaTypeList[index].toUpperCase()));
        schedPart.setOperator(false);
        schedPart.setOwner(false);
        schedPart.setOwnerUGPId(0);
        schedPart.setParentDeviceId(0);
        schedPart.setRoom(false);
        schedPart.setServiceName("##ANYMCUSERVICE##");
        schedPart.setVip(false);
        schedPart.setVirtualParticipant(false);
        schedPart.setGuestBookLocation("bj");
        schedDevice.setBehindFirewall(false);
        schedDevice.setBelongsToAreaUgpId(0);
        schedDevice.setDeviceId(-1);
        schedDevice.setH323Capable(true);
        schedPart.setExtention("");
        schedDevice.setIsdnAreaCode("");
        schedDevice.setIsdnCapable(true);
        schedDevice.setIsdnCountryCode("");
        schedDevice.setIsdnExtension("");
        schedDevice.setIsdnFullNumber("");
        schedDevice.setIsdnPhoneNumber("");
        schedDevice.setName(nameList[index].split("~")[0]
                + nameList[index].split("~")[1]);
        schedDevice.setOwnerUgpId(0);
        schedDevice.setScheduledDevice(true);
        schedDevice.setSipCapable(true);
        schedDevice.setVip(false);
        schedDevice.setAllowedOnRMX(false);
        schedDevice.setRequiredMcuService("##ANYMCUSERVICE##");
        final String maxSpeed = inputCmd.get("maxSpeed");
        int maxSpeedInt = 10240;
        int speedInt = 0;
        if (inputCmd.get("conferenceType").equalsIgnoreCase("anytime")
                || inputCmd.get("conferenceType")
                        .equalsIgnoreCase("poolConference")
                || inputCmd.get("conferenceType")
                        .equalsIgnoreCase("recurringPoolConf")) {
        	speedInt = -1;
        } else {
        	speedInt = 384;
        }
        if (!maxSpeed.isEmpty()) {
            maxSpeedInt = Integer.parseInt(maxSpeed);
        }
        if (!inputCmd.get("partLineRateList").isEmpty()
                && lineRateList.length > index) {
        	speedInt = Integer.parseInt(lineRateList[index]);
        }
        schedDevice.setMaxSpeedIP(maxSpeedInt);
        schedDevice.setMaxSpeedISDN(maxSpeedInt);
        schedDevice.setVideoSpeed(speedInt);
        final String[] numbers = numberList[index].split(":");
        final String number = numbers[0];
        String extention = "";
        if (numbers.length == 2) {
            extention = numbers[1];
        }
        schedPart.setExtention(extention);
        if (dialTypeList[index].equalsIgnoreCase("h323")) {
            schedDevice.setIpAddress(number);
            schedDevice.setIpExtension(extention);
            schedDevice.setPreferredConnectionType(JSchedConnectionType.H_323);
        } else if (dialTypeList[index].equalsIgnoreCase("h323_e164")
                || dialTypeList[index].equalsIgnoreCase("h323_id")
                || dialTypeList[index].equalsIgnoreCase("h323_annexO")) {
            final JDeviceAlias deviceAlias = new JDeviceAlias();
            deviceAlias.setDbKey(new Integer(0));
            deviceAlias.setDeviceEntityId(0);
            deviceAlias.setSourceId(null);
            deviceAlias.setValue(number);
            deviceAlias.setExtension(extention);
            if (dialTypeList[index].equalsIgnoreCase("h323_e164")) {
                deviceAlias.setType(JDeviceAliasType.E_164);
                schedDevice
                        .setPreferredConnectionType(JSchedConnectionType.H_323___E_164);
            } else if (dialTypeList[index].equalsIgnoreCase("h323_id")) {
                deviceAlias.setType(JDeviceAliasType.H_323);
                schedDevice
                        .setPreferredConnectionType(JSchedConnectionType.H_323___ID);
            } else {
                deviceAlias.setType(JDeviceAliasType.URL);
                schedDevice
                        .setPreferredConnectionType(JSchedConnectionType.H_323___ANNEX___O);
            }
            schedDevice.getAliasList().add(deviceAlias);
        } else if (dialTypeList[index].equalsIgnoreCase("sip")) {
            schedDevice.setPreferredConnectionType(JSchedConnectionType.SIP);
            schedPart.setExtention(extention);
            schedDevice.setSipUri(number);
        } else if (dialTypeList[index].equalsIgnoreCase("isdn")) {
            schedDevice.setPreferredConnectionType(JSchedConnectionType.ISDN);
            schedPart.setExtention(extention);
            schedDevice.setIsdnCapable(true);
            schedDevice.setIsdnExtension(extention);
            schedDevice.setIsdnFullNumber(number);
        }
        schedPart.getSchedDeviceCollection().add(schedDevice);
        return schedPart;
    }

    /**
     * Delete the anytime conference
     *
     * @see conferenceName=DMA~supercluster~auto$id <br/>
     *      conferenceRoomId=$toDelVmr($id)
     * @param conferenceName
     *            The conference name
     * @param conferenceRoomId
     *            The conference id
     *
     * @return The result
     */
    public String deleteAnytimeConference() {
        String status = "Failed";
        final String conferenceName = inputCmd.get("conferenceName");
        final String conferenceRoomId = inputCmd.get("conferenceRoomId");
        if (conferenceName.isEmpty() || conferenceRoomId.isEmpty()) {
            status = "Failed";
            logger.error("Arguements missing. The conference name and VMR number should both be specified.");
            return status
                    + " Arguements missing. The conference name and VMR number should both be specified.";
        }
        try {
            final JAnytimeConference toDeleteConf = getAnytimeConference(conferenceName,
                                                                         conferenceRoomId);
            if (toDeleteConf == null) {
                status = "Failed";
                logger.error("Did not find conference named " + conferenceName);
                return status + "Did not find conference named "
                        + conferenceName;
            }
            final JWebResult result = seh
                    .deleteConference(userToken, toDeleteConf.getCmConfId());
            if (result.getStatus().equals(JStatus.SUCCESS)) {
                logger.info("Delete conference " + conferenceName);
                return "SUCCESS";
            } else {
                status = "Failed";
                logger.error("Failed to delete conference " + conferenceName);
                return status + " Failed to delete conference "
                        + conferenceName;
            }
        } catch (final Exception e) {
            e.printStackTrace();
            status = "Failed";
            logger.error("Exception was found when trying to delete the anytime conference. "
                    + "The error message is: " + e.getMessage());
            return status
                    + " Exception was found when trying to delete the anytime conference. "
                    + "The error message is: " + e.getMessage();
        }
    }

    /**
     * Delete conference
     *
     * @param conferenceName
     *            The conference name
     * @param conferenceID
     *            The conference id
     *
     * @return The result
     */
    public String deleteConference() {
        String status = "Failed";
        if (inputCmd.get("conferenceName").isEmpty()) {
            status = "Failed";
            logger.error("Arguements missing. The conference name should be specified.");
            return status
                    + " Arguements missing. The conference name should be specified.";
        }
        final List<JLeanConference> confList = csh
                .getFilteredLeanConferenceList(userToken,
                		"confName:"+getInputCmd().get("conferenceName").replace("~", " "));
        final List<JLeanConference> toDeleteConfList = new ArrayList<JLeanConference>();
        for (int i = 0; i < confList.size(); i++) {
            if (inputCmd.get("conferenceName")
                    .equals(confList.get(i).getConfName())
                    && confList.get(i).getConfStatus()
                            .equals(JCMConferenceStatus.FUTURE)) {
                toDeleteConfList.add(confList.get(i));
            }
        }
        if (toDeleteConfList.size() < 1) {
            status = "Failed";
            logger.error("Did not find conference name "
                    + inputCmd.get("conferenceName"));
            return status + " Did not find conference name "
                    + inputCmd.get("conferenceName");
        } else if (toDeleteConfList.size() > 1) {
            status = "Failed";
            logger.error("Find more than one conference through name "
                    + inputCmd.get("conferenceName"));
            return status + " Find more than one conference through name "
                    + inputCmd.get("conferenceName");
        }
        try {
            if (seh.deleteConference(userToken,
                                     toDeleteConfList.get(0).getCmConfId())
                    .getStatus()
                    .compareTo(com.polycom.webservices.SchedulerEngine.JStatus.SUCCESS) == 0) {
                logger.info("Delete conference "
                        + inputCmd.get("conferenceName"));
                return "SUCCESS";
            } else {
                status = "Failed";
                logger.error("Failed to delete conference "
                        + inputCmd.get("conferenceName"));
                return status + " Failed to delete conference "
                        + inputCmd.get("conferenceName");
            }
        } catch (final Exception e) {
            e.printStackTrace();
            status = "Failed";
            logger.error("Exception was found when trying to delete the conference "
                    + inputCmd.get("conferenceName") + ". "
                    + "The error message is: " + e.getMessage());
            return status
                    + " Exception was found when trying to delete the conference "
                    + inputCmd.get("conferenceName") + ". "
                    + "The error message is: " + e.getMessage();
        }
    }

    /**
     * Delete the recurring conference
     *
     * @see conferenceName=DMA~supercluster~auto$id
     *
     * @param conferenceName
     *            The conference name
     *
     * @return The result
     */
    public String deleteRecurringConference() {
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final String confName = inputCmd.get("confName");
        final ConferenceServiceHandler csh = new ConferenceServiceHandler(
                webServiceUrl);
        // Get the future conference ID
        String confId = "";
        final List<JLeanConference> confList = csh
                .getFilteredLeanConferenceList(userToken, "confName:"+confName, "confStatus:future");
        for (final JLeanConference lc : confList) {
            if ((lc.getConfStatus()
                    .compareTo(JCMConferenceStatus.FUTURE___ALERTS) == 0)
                    || (lc.getConfStatus()
                            .compareTo(JCMConferenceStatus.FUTURE) == 0)) {
                confId = lc.getCmConfId();
                break;
            }
        }
        logger.info("The conference ID is: " + confId);
        // Get ongoing conference recurring id
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        if (ongoingConfInfo != null) {
            final int recurId = ongoingConfInfo.getRecurId();
            if (seh.deleteRecurringConference(userToken, recurId).getStatus()
                    .compareTo(com.polycom.webservices.SchedulerEngine.JStatus.SUCCESS) == 0) {
                logger.info("Delete conference "
                        + inputCmd.get("conferenceName"));
                return "SUCCESS";
            } else {
                logger.error("Failed to delete conference "
                        + inputCmd.get("conferenceName"));
                return " Failed to delete conference "
                        + inputCmd.get("conferenceName");
            }
        } else {
            logger.error("Failed to delete conference "
                    + inputCmd.get("conferenceName")
                    + ", didn't find conference");
            return " Failed to delete conference "
                    + inputCmd.get("conferenceName") + "";
        }
    }

    /**
     * Internal method, get anytime conference by name
     *
     * @param confName
     *            The conference name
     * @param vmr
     *            The vmr id
     * @return JAnytimeConference
     */
    private JAnytimeConference getAnytimeConference(final String confName,
                                                    final String vmr) {
        final ConferenceServiceHandler csh = new ConferenceServiceHandler(
                webServiceUrl);
        final List<JAnytimeConference> anytimeConfList = csh
                .getAnytimeConferenceList(userToken);
        for (final JAnytimeConference anytimeConf : anytimeConfList) {
            if (anytimeConf.getConfName().equals(confName)
                    && anytimeConf.getDmaConfRoomId().equals(vmr)) {
                logger.info("Found the specificed anytime conference "
                        + confName);
                return anytimeConf;
            }
        }
        return null;
    }

    @Override
    protected void injectCmdArgs() {
        // Last name of the guest
        put("conferenceType", "");
        put("conferenceName", "automation");
        put("conferenceTemplate", "");
        put("chairpersonPassword", "");
        put("conferencePassword", "");
        put("conferenceID", "");
        put("conferenceRoomId", "");
        put("conferenceDesc", "");
        put("conferenceDuration", "60");
        put("confMediaType", "video");
        put("recurrenceNum", "3");
        put("recurrenceType", "weekly");
        put("recurrenceInterval", "1");
        put("recurrenceInstance", "1");
        put("startTime", "");
        put("endTime", "");
        put("endDate", "2016,1,1");
        put("dayOfMask", "1111111");
        put("dayOfMonth", "0");
        put("ownerName", "");
        put("chairPerson", "");
        put("partNameList", "");
        put("partTypeList", "");
        put("epNameList", "");
        put("partDialDirectionList", "");
        put("partEncryptList", "");
        put("partLineRateList", "");
        put("partMediaTypeList", "");
        put("partDialTypeList", "");
        put("deviceIpList", "");
        put("maxSpeed", "");
        put("partLineRate", "");
        put("allowOverbooking", "false");
        put("conferenceTimeWarning", "true");
        put("includConferenceCreator", "false");
        put("pinLength", "4");
        put("rpwsServerAddress", "");
        put("supportCloudAxis", "false");
    }

    @SuppressWarnings("unused")
    private int retrieveParticipantsUgpId(final String username) {
        int ugpid = 0;
        final List<JUser> userList = umh
                .findConferenceParticipants(userToken,
                                            username.split("~")[0],
                                            username.split("~")[1]);
        for (final JUser user : userList) {
            if (user.getFirstName().equals(username.split("~")[0])
                    && user.getLastName().equals(username.split("~")[1])) {
                ugpid = user.getUgpId();
                break;
            }
        }
        return ugpid;
    }

    /**
     * Retrieve the UGPID from the user page. It is only available for user with
     * user operation roles.
     *
     * @param username
     * @return
     */
    @SuppressWarnings("unused")
    private int retrieveUserUgpId(final String username) {
        int ugpid = 0;
        final List<JUser> userList = umh.getUsers(userToken,
                                                  username,
                                                  "AllUser");
        for (final JUser user : userList) {
            if (user.getUserName().equals(username)) {
                ugpid = user.getUgpId();
                break;
            }
        }
        return ugpid;
    }

    /**
     * Schedule DMA Conference
     *
     * @see conferenceName=$confName1 <br/>
     *      conferenceType=anytime <br/>
     *      conferenceRoomId=1230 <br/>
     *      conferenceTemplate=Factory~Template <br/>
     *      ownerName=debuguser8~hhh <br/>
     *      partNameList=debuguser8~hhh,debuguser3~ccc <br/>
     *      partTypeList=user,user <br/>
     *      partDialDirectionList=dialin,dialin <br/>
     *      deviceIpList=$e1_win_addr,$e5_gs_addr <br/>
     *      partDialTypeList=h323,h323
     *
     * @param conferenceName
     *            The conference name
     * @param conferenceType
     *            The conference type
     * @param conferenceRoomId
     *            The conference id
     * @param conferenceTemplate
     *            The conference template
     * @param ownerName
     *            The conference owner name
     * @param partNameList
     *            The participant name list
     * @param partDialDirectionList
     *            The participant direction list
     * @param deviceIpList
     *            The device list
     * @param partDialTypeList
     *            The dial type list
     * @return The result
     */
    public String scheduleDmaConference() {
        String status = "Failed";
        try {
            final JSchedConf conf = new JSchedConf();
            final JConferencePolicy confPolicy = new JConferencePolicy();
            setConfPolicy(conf, confPolicy);
            conf.setBelongsToAreaUgpId(-1);
            conf.setBillingCode("None");
            conf.setBridgeConference(false);
            conf.setCascadedConference(false);
            conf.setComments("");
            conf.setConfChairPin(inputCmd.get("chairpersonPassword"));
            conf.setConfMediaType(JCMConferenceMediaType.AUDIO_VIDEO);
            conf.setConfName(inputCmd.get("conferenceName").replaceAll("~",
                                                                       " "));
            conf.setConfPassword(inputCmd.get("conferencePassword"));
            conf.setConnectionType(JCMConferenceConnectionType.MULTIPOINT);
            conf.setCreatorName(inputCmd.get("username"));
            // need to retrieve the creator ugpid, 1 is admin
            conf.setCreatorUgpId(loginUserUgpId);
            conf.setDeleteOnCompletion(false);
            conf.setDmaConfRoomId(inputCmd.get("conferenceRoomId"));
            conf.setEmbeddedMPConference(false);
            final JUIUtcDateTime utcDate = new JUIUtcDateTime();
            utcDate.setUnixTime(Long
                    .parseLong(inputCmd.get("endTime") + "000"));
            conf.setEndDateTime(utcDate);
            conf.setEndpointConference(false);
            conf.setOriginalCreatorUgpId(0);
            conf.setOwnerName(inputCmd.get("ownerName").replace("~", " "));
            String ownerUUID = "";
            List<JUser> userList = umh
                    .findConferenceParticipants(userToken,
                                                inputCmd.get("ownerName")
                                                        .split("~")[0],
                                                inputCmd.get("ownerName")
                                                        .split("~")[1]);
            for (final JUser user : userList) {
                if (user.getFirstName()
                        .equals(inputCmd.get("ownerName").split("~")[0])
                        && user.getLastName().equals(inputCmd.get("ownerName")
                                .split("~")[1])) {
                    ownerUUID = user.getGUID();
                    break;
                }
            }
            conf.setOwnerUUID(ownerUUID);
            conf.setP2PConference(false);
            conf.setRecurId(0);
            conf.setScheduledConference(false);
            final JUIUtcDateTime utcDate2 = new JUIUtcDateTime();
            utcDate2.setUnixTime(Long
                    .parseLong(inputCmd.get("startTime") + "000"));
            conf.setStartDateTime(utcDate2);
            conf.setToken(new Long(0));
            conf.setBridgeSelectionType(JSchedBridgeSelectionType.MULTIDMAPOOL);
            conf.setDeclineConfIfFail(false);
            conf.setEmailLocale("en_US");
            conf.setIcalSequence(0);
            conf.setRecurringInstance(false);
            if (inputCmd.get("conferenceType")
                    .equalsIgnoreCase("recurringPoolConf")) {
                final JSchedRecurrence sr = new JSchedRecurrence();
                sr.setDayOfMonth(Integer.valueOf(inputCmd.get("dayOfMonth"))
                        .intValue());
                sr.setDayOfWeekMask(inputCmd.get("dayOfMask"));
                sr.setDuration(Integer
                        .valueOf(inputCmd.get("conferenceDuration")));
                sr.setInstance(Integer
                        .valueOf(inputCmd.get("recurrenceInstance"))
                        .intValue());
                sr.setInterval(Integer
                        .valueOf(inputCmd.get("recurrenceInterval"))
                        .intValue());
                sr.setOccurrences(Integer
                        .valueOf(inputCmd.get("recurrenceNum")));
                if (inputCmd.get("recurrenceType").equalsIgnoreCase("weekly")) {
                    sr.setRecurrenceType(JSchedRecurrenceType.WEEKLY);
                } else if (inputCmd.get("recurrenceType")
                        .equalsIgnoreCase("daily")) {
                    sr.setRecurrenceType(JSchedRecurrenceType.DAILY);
                } else if (inputCmd.get("recurrenceType")
                        .equalsIgnoreCase("monthly")) {
                    sr.setRecurrenceType(JSchedRecurrenceType.MONTHLY);
                } else if (inputCmd.get("recurrenceType")
                        .equalsIgnoreCase("monthlynthday")) {
                    sr.setRecurrenceType(JSchedRecurrenceType.MONTHLY___NTH___DAY);
                }
                final Calendar startDate = Calendar.getInstance();
                final Calendar endDate = Calendar.getInstance();
                final String[] ymd = inputCmd.get("endDate").split(",");
                // add temply for resolve problem that culculated enddate more
                // than one month then input
                int y = Integer.parseInt(ymd[0]);
                int m = Integer.parseInt(ymd[1]);
                final int d = Integer.parseInt(ymd[2]);
                if (m == 1) {
                    m = 12;
                    y -= 1;
                } else {
                    m -= 1;
                }
                endDate.set(y, m, d);
                final JUIUtcDateTime stD = new JUIUtcDateTime();
                final JUIUtcDateTime enD = new JUIUtcDateTime();
                stD.setUnixTime(startDate.getTime().getTime());
                enD.setUnixTime(endDate.getTime().getTime());
                sr.setPatternStartDate(stD);
                sr.setPatternEndDate(enD);
                final List<JUIUtcDateTime> timeLists = seh
                        .getRecurrenceDates(userToken, sr);
                final Calendar cl = Calendar.getInstance();
                final int hour = cl.get(Calendar.HOUR_OF_DAY);
                final int minute = cl.get(Calendar.MINUTE);
                for (int i = 0; i < timeLists.size(); i++) {
                    final Calendar calendar = Calendar.getInstance();
                    final Date date = new Date(timeLists.get(i).getUnixTime());
                    calendar.setTime(date);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute + 1);
                    timeLists.get(i).setUnixTime(calendar.getTime().getTime());
                }
                for (final JUIUtcDateTime time : timeLists) {
                    conf.getRecurrenceDates().add(time);
                }
                conf.setRecurringInstance(true);
                conf.setRecurrence(sr);
            } else {
                conf.setRecurringInstance(false);
            }
            if (inputCmd.get("conferenceType").equalsIgnoreCase("anytime")) {
                conf.setSchedulingUI(JSchedUIType.UI___DMA___ANYTIME___CONF);
                conf.setConfStatus(com.polycom.webservices.SchedulerEngine.JCMConferenceStatus.FUTURE);
            } else if (inputCmd.get("conferenceType")
                    .equalsIgnoreCase("poolConference")
                    || inputCmd.get("conferenceType")
                            .equalsIgnoreCase("recurringPoolConf")) {
                conf.setSchedulingUI(JSchedUIType.UI___DMA___CONF);
            }
            conf.setSendEmailAfterConfIsCreated(false);
            final String[] participantList = inputCmd.get("partNameList")
                    .split(",");
            final String[] partTypeList = inputCmd.get("partTypeList")
                    .split(",");
            for (int i = 0; i < participantList.length; i++) {
                final String participant = participantList[i];
                JSchedPart schedPart = new JSchedPart();
                if (partTypeList[i].equals("user")) {
                    userList = umh
                            .findConferenceParticipants(userToken,
                                                        participant
                                                                .split("~")[0],
                                                        participant
                                                                .split("~")[1]);
                    final JUser user = userList.get(0);
                    schedPart = convertUserOrRoomToJSchedPart(user, i);
                } else if (partTypeList[i].equals("room")) {
                    userList = umh.findConferenceParticipants(userToken,
                                                              participant
                                                                      .split("~")[0],
                                                              "");
                    if (userList.isEmpty()) {
                        userList = umh
                                .findConferenceParticipants(userToken,
                                                            "",
                                                            participant
                                                                    .split("~")[0]);
                    }
                    final JUser user = userList.get(0);
                    schedPart = convertUserOrRoomToJSchedPart(user, i);
                } else if (partTypeList[i].equals("guest")) {
                    for (final JConfGuest guest : umh
                            .searchConfGuest(userToken)) {
                        if (guest.getFirstName()
                                .equalsIgnoreCase(participant.split("~")[0])
                                && guest.getLastName()
                                        .equalsIgnoreCase(participant
                                                .split("~")[1])) {
                            schedPart = convertGuestToJSchedPart(umh, guest, i);
                            break;
                        }
                    }
                } else if (partTypeList[i].equalsIgnoreCase("tempguest")) {
                    schedPart = createTempGuestPart(i);
                }
                conf.getSchedPartCollection().add(schedPart);
            }
            if (inputCmd.get("conferenceType")
                    .equalsIgnoreCase("recurringPoolConf")) {
                if (seh.scheduleRecurringConferenceNonBlock(userToken, conf)
                        .getStatus()
                        .equals(com.polycom.webservices.SchedulerEngine.JStatus.SUCCESS)) {
                    logger.info("Successfully schedule recurring conference"
                            + inputCmd.get("conferenceName"));
                    try {
                        Thread.sleep(30000);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!seh.getRecurringProgress(userToken,
                                                  seh.getRecurringHandler())
                            .getEmailBody().isEmpty()) {
                        status = "SUCCESS";
                        logger.info("Launch recurring conference "
                                + inputCmd.get("conferenceName")
                                + " successfully");
                    } else {
                        status = "Failed";
                        logger.error("Failed to launch the recurring conference "
                                + inputCmd.get("conferenceName"));
                        return status
                                + "Failed to launch the recurring conference "
                                + inputCmd.get("conferenceName");
                    }
                } else {
                    status = "Failed";
                    logger.error("Failed to schedule recurring conference "
                            + inputCmd.get("conferenceName"));
                    return status + " Failed to schedule recurring conference "
                            + inputCmd.get("conferenceName");
                }
            } else {
                final JSchedResult scheduleConference = seh
                        .scheduleConference(userToken, conf);
                final String emailBody = scheduleConference.getEmailBody();
                if (scheduleConference == null || emailBody == null
                        || emailBody.isEmpty()) {
                    status = "Failed";
                    logger.error("Failed to schedule DMA conference "
                            + inputCmd.get("conferenceName"));
                    return status + " Failed to schedule DMA conference "
                            + inputCmd.get("conferenceName");
                } else {
                    logger.info("Successfully schedule DMA conference "
                            + inputCmd.get("conferenceName"));
                    status = "SUCCESS";
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
            status = "Failed, got exception when schedule DMA conference. Error msg is "
                    + e.getMessage();
        }
        return status;
    }

    /**
     * Schedule MCU Conference
     *
     * @see conferenceName=$confName <br/>
     *      conferenceTemplate=Default~Audio~Template <br/>
     *      conferencePassword=3214 <br/>
     *      chairpersonPassword=1234 <br/>
     *      conferenceID=$confId <br/>
     *      startTime=$startTimeInMillSec <br/>
     *      conferenceDuration=60 <br/>
     *      ownerName=debuguser3~ccc <br/>
     *      chairPerson=debuguser3~ccc <br/>
     *      confMediaType=audio <br/>
     *      partNameList=debuguser3~ccc,debuguser5~eee,debuguser~aaa,ad~user2,
     *      debuguser10~jjj,chen~tao,debuguser8~hhh,debuguser7~ggg,lroom <br/>
     *      partTypeList=user,user,user,user,user,user,user,user,room <br/>
     *      partDialDirectionList=dialin,dialin,dialin,dialin,dialout,dialout,
     *      dialout,dialout,dialin <br/>
     *      partMediaTypeList=audio,audio,audio,audio,audio,audio,audio,audio,
     *      audio <br/>
     *      partDialTypeList=h323,h323_id,h323_e164,sip,h323,h323_id,h323_e164,
     *      sip,h323 <br/>
     *      deviceIpList=$e5_gs_addr,$e3_hdx_addr,$e1_hdx_addr,$e4_mac_addr,
     *      $e3_mac_addr,$e2_win_addr,$e1_win_addr,$e7_gs_addr,$e9_gs_addr
     *
     *
     * @param conferenceName
     *            The conference name
     * @param conferenceType
     *            The conference type
     * @param conferenceRoomId
     *            The conference id
     * @param conferenceTemplate
     *            The conference template
     * @param conferencePassword
     *            The conference password
     * @param chairpersonPassword
     *            The conference chairperson password
     * @param ownerName
     *            The conference owner name
     * @param startTime
     *            The conference start time in millions
     * @param conferenceDuration
     *            The conference duration in minute
     * @param chairPerson
     *            The conference chairperson
     * @param confMediaType
     *            The conference media type(audio/video)
     * @param partNameList
     *            The participant name list
     * @param partDialDirectionList
     *            The participant direction list
     * @param partMediaTypeList
     *            The participant media type(audio/video) list
     * @param deviceIpList
     *            The device list
     * @param partDialTypeList
     *            The dial type list
     * @return The result
     */
    public String scheduleMcuConference() {
        String status = "Failed";
        final JSchedConf conf = new JSchedConf();
        JConferencePolicy confPolicy = new JConferencePolicy();
        // Construct the conference policy
        for (final JConferencePolicy policy : seh
                .findConferencePolicysForUser(userToken)) {
            if (policy.getPolicyName().equals(inputCmd.get("conferenceTemplate")
                    .replace("~", " "))) {
                logger.info("find: " + policy.getPolicyName());
                confPolicy = policy;
            }
        }
        conf.setConferencePolicy(confPolicy);
        if (inputCmd.get("conferenceType").equalsIgnoreCase("recurringConf")) {
            final JSchedRecurrence sr = new JSchedRecurrence();
            sr.setDayOfMonth(0);
            sr.setDayOfWeekMask(inputCmd.get("dayOfMask"));
            sr.setDuration(Integer.valueOf(inputCmd.get("conferenceDuration")));
            sr.setInstance(1);
            sr.setInterval(1);
            sr.setOccurrences(Integer.valueOf(inputCmd.get("recurrenceNum")));
            if (inputCmd.get("recurrenceType").equalsIgnoreCase("weekly")) {
                sr.setRecurrenceType(JSchedRecurrenceType.WEEKLY);
            } else
                if (inputCmd.get("recurrenceType").equalsIgnoreCase("daily")) {
                sr.setRecurrenceType(JSchedRecurrenceType.DAILY);
            } else if (inputCmd.get("recurrenceType")
                    .equalsIgnoreCase("monthly")) {
                sr.setRecurrenceType(JSchedRecurrenceType.MONTHLY);
            }
            final Calendar startDate = Calendar.getInstance();
            final Calendar endDate = Calendar.getInstance();
            final String[] ymd = inputCmd.get("endDate").split(",");
            endDate.set(Integer.valueOf(ymd[0]),
                        Integer.valueOf(ymd[1]),
                        Integer.valueOf(ymd[0]));
            final JUIUtcDateTime stD = new JUIUtcDateTime();
            final JUIUtcDateTime enD = new JUIUtcDateTime();
            stD.setUnixTime(startDate.getTime().getTime());
            enD.setUnixTime(endDate.getTime().getTime());
            sr.setPatternStartDate(stD);
            sr.setPatternEndDate(enD);
            final List<JUIUtcDateTime> timeLists = seh
                    .getRecurrenceDates(userToken, sr);
            final Calendar cl = Calendar.getInstance();
            final int hour = cl.get(Calendar.HOUR_OF_DAY);
            final int minute = cl.get(Calendar.MINUTE);
            for (int i = 0; i < timeLists.size(); i++) {
                final Calendar calendar = Calendar.getInstance();
                final Date date = new Date(timeLists.get(i).getUnixTime());
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute + 1);
                timeLists.get(i).setUnixTime(calendar.getTime().getTime());
            }
            for (final JUIUtcDateTime time : timeLists) {
                conf.getRecurrenceDates().add(time);
            }
            conf.setRecurringInstance(true);
            conf.setRecurrence(sr);
        } else {
            conf.setRecurringInstance(false);
        }
        conf.setConferenceAlias(inputCmd.get("conferenceID"));
        conf.setBelongsToAreaUgpId(-1);
        conf.setBillingCode("None");
        conf.setBridgeConference(false);
        conf.setCascadedConference(false);
        conf.setComments(inputCmd.get("conferenceDesc"));
        conf.setConfChairPin(inputCmd.get("chairpersonPassword"));
        if (inputCmd.get("confMediaType").equalsIgnoreCase("video")) {
            conf.setConfMediaType(JCMConferenceMediaType.AUDIO_VIDEO);
        } else if (inputCmd.get("confMediaType").equalsIgnoreCase("audio")) {
            conf.setConfMediaType(JCMConferenceMediaType.AUDIO);
        } else {
            conf.setConfMediaType(JCMConferenceMediaType.AUDIO_VIDEO);
        }
        conf.setConfName(inputCmd.get("conferenceName").replaceAll("~", " "));
        conf.setConfPassword(inputCmd.get("conferencePassword"));
        conf.setConnectionType(JCMConferenceConnectionType.MULTIPOINT);
        conf.setCreatorName(inputCmd.get("username"));
        // need to retrieve the creator ugpid, 1 is admin
        conf.setCreatorUgpId(loginUserUgpId);
        conf.setDeleteOnCompletion(false);
        conf.setEmbeddedMPConference(false);
        final JUIUtcDateTime utcDate = new JUIUtcDateTime();
        if (!inputCmd.get("endTime").isEmpty()) {
            utcDate.setUnixTime(Long.parseLong(inputCmd.get("endTime")));
        } else {
            utcDate.setUnixTime(Long.parseLong(inputCmd.get("startTime"))
                    + (Long.parseLong(inputCmd.get("conferenceDuration"))
                            * 60000));
        }
        conf.setEndDateTime(utcDate);
        final JUIUtcDateTime utcDate2 = new JUIUtcDateTime();
        utcDate2.setUnixTime(Long.parseLong(inputCmd.get("startTime")));
        conf.setStartDateTime(utcDate2);
        conf.setEndpointConference(false);
        conf.setOriginalCreatorUgpId(0);
        conf.setOwnerName(inputCmd.get("ownerName").replace("~", " "));
        conf.setComments("auto test");
        String ownerUUID = "";
        List<JUser> userList = umh
                .findConferenceParticipants(userToken,
                                            inputCmd.get("ownerName")
                                                    .split("~")[0],
                                            inputCmd.get("ownerName")
                                                    .split("~")[1]);
        for (final JUser user : userList) {
            if (user.getFirstName()
                    .equals(inputCmd.get("ownerName").split("~")[0])
                    && user.getLastName()
                            .equals(inputCmd.get("ownerName").split("~")[1])) {
                ownerUUID = user.getGUID();
            }
        }
        conf.setOwnerUUID(ownerUUID);
        conf.setP2PConference(false);
        conf.setRecurId(0);
        conf.setScheduledConference(false);
        conf.setToken(new Long(0));
        conf.setBridgeSelectionType(JSchedBridgeSelectionType.SINGLEBRIDGE);
        conf.setDeclineConfIfFail(false);
        conf.setEmailLocale("en_US");
        conf.setIcalSequence(0);
        // conf.setRecurringInstance(false);
        conf.setSendEmailAfterConfIsCreated(false);
        final String[] participantList = inputCmd.get("partNameList")
                .split(",");
        final String[] partTypeList = inputCmd.get("partTypeList").split(",");
        final String[] deviceIpList = inputCmd.get("deviceIpList").split(",");
        final String[] mediaTypeList = inputCmd.get("partMediaTypeList")
                .split(",");
        for (int i = 0; i < participantList.length; i++) {
            final String participant = participantList[i];
            JSchedPart schedPart = new JSchedPart();
            if (partTypeList[i].equals("user")) {
                userList = umh
                        .findConferenceParticipants(userToken,
                                                    participant.split("~")[0],
                                                    participant.split("~")[1]);
                for (final JUser user : userList) {
                    for (final JDeviceAssociation deviceasso : user
                            .getDeviceAssociations()) {
                        if (deviceasso.getIpAddr().equals(deviceIpList[i])) {
                            if (mediaTypeList[i].equalsIgnoreCase("audio")) {
                                schedPart.setMode(JSchedPartMode.AUDIO);
                                schedPart
                                        .setMediaType(JCMConferenceMediaType.AUDIO);
                            } else {
                                schedPart.setMode(JSchedPartMode.VIDEO);
                                schedPart
                                        .setMediaType(JCMConferenceMediaType.AUDIO_VIDEO);
                            }
                            schedPart = convertUserOrRoomToJSchedPart(user, i);
                        }
                    }
                    // Add code for in person participant
                    if (user.getDeviceAssociations().size() == 0) {
                        schedPart.setMode(JSchedPartMode.IN___PERSON);
                        schedPart = convertUserOrRoomToJSchedPart(user, i);
                    }
                }
            } else if (partTypeList[i].equals("room")) {
                userList = umh.findConferenceParticipants(userToken,
                                                          participant
                                                                  .split("~")[0],
                                                          "");
                if (userList.isEmpty()) {
                    userList = umh
                            .findConferenceParticipants(userToken,
                                                        "",
                                                        participant
                                                                .split("~")[0]);
                }
                final JUser user = userList.get(0);
                schedPart = convertUserOrRoomToJSchedPart(user, i);
            } else if (partTypeList[i].equals("guest")) {
                for (final JConfGuest guest : umh.searchConfGuest(userToken)) {
                    if (guest.getFirstName()
                            .equalsIgnoreCase(participant.split("~")[0])
                            && guest.getLastName().equalsIgnoreCase(participant
                                    .split("~")[1])) {
                        schedPart = convertGuestToJSchedPart(umh, guest, i);
                        break;
                    }
                }
            } else if (partTypeList[i].equalsIgnoreCase("tempguest")) {
                schedPart = createTempGuestPart(i);
            }
            conf.getSchedPartCollection().add(schedPart);
        }
        if (inputCmd.get("conferenceType").equalsIgnoreCase("recurringConf")) {
            if (seh.scheduleRecurringConferenceNonBlock(userToken, conf)
                    .getStatus()
                    .equals(com.polycom.webservices.SchedulerEngine.JStatus.SUCCESS)) {
                logger.info("Successfully schedule recurring conference"
                        + inputCmd.get("conferenceName"));
                try {
                    Thread.sleep(20000);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                if (!seh.getRecurringProgress(userToken,
                                              seh.getRecurringHandler())
                        .getEmailBody().isEmpty()) {
                    status = "SUCCESS";
                    logger.info("Launch recurring conference "
                            + inputCmd.get("conferenceName") + " successfully");
                } else {
                    status = "Failed";
                    logger.error("Failed to launch the recurring conference "
                            + inputCmd.get("conferenceName"));
                    return status + "Failed to launch the recurring conference "
                            + inputCmd.get("conferenceName");
                }
            } else {
                status = "Failed";
                logger.error("Failed to schedule recurring MCU conference "
                        + inputCmd.get("conferenceName"));
                return status + " Failed to schedule recurring MCU conference "
                        + inputCmd.get("conferenceName");
            }
        } else {
            final JSchedResult scheduleConference = seh
                    .scheduleConference(userToken, conf);
            final String emailBody = scheduleConference.getEmailBody();
            if (emailBody == null || emailBody.isEmpty()) {
                status = "Failed";
                logger.error("Failed to schedule MCU conference "
                        + inputCmd.get("conferenceName"));
                return status + " Failed to schedule MCU conference "
                        + inputCmd.get("conferenceName");
            } else {
                logger.info("Successfully schedule MCU conference "
                        + inputCmd.get("conferenceName"));
                status = "SUCCESS";
            }
        }
        return status;
    }

    private void setConfPolicy(final JSchedConf conf,
                               final JConferencePolicy confPolicy) {
        confPolicy.setPolicyId("F6200CE1-EF3D-BD9F-D65D-5EE8885C716F");
        final JConferencePolicyAttribute cpa1 = new JConferencePolicyAttribute();
        cpa1.setAttributeId("734424A6-35A2-A8C0-C760-5EE8885C42EC");
        cpa1.setAttributeName("supportesMCUs");
        cpa1.setAttributeValue("DMA_ONLY");
        cpa1.setPolicyId("F6200CE1-EF3D-BD9F-D65D-5EE8885C716F");
        confPolicy.getProfileAttributes().add(cpa1);
        final JConferencePolicyAttribute cpa2 = new JConferencePolicyAttribute();
        cpa2.setAttributeId("FCBFD307-76F4-15F9-2E25-5EE8885C4A0F");
        cpa2.setAttributeName("transferRate");
        cpa2.setAttributeValue("10240");
        cpa2.setPolicyId("F6200CE1-EF3D-BD9F-D65D-5EE8885C716F");
        confPolicy.getProfileAttributes().add(cpa2);
        final JConferencePolicyAttribute cpa3 = new JConferencePolicyAttribute();
        cpa3.setAttributeId("9EDD46AE-A7A9-092D-0C80-5EE8885CFD29");
        cpa3.setAttributeName("isStartConferenceLeader");
        cpa3.setAttributeValue("false");
        cpa3.setPolicyId("F6200CE1-EF3D-BD9F-D65D-5EE8885C716F");
        confPolicy.getProfileAttributes().add(cpa3);
        final JConferencePolicyAttribute cpa4 = new JConferencePolicyAttribute();
        cpa4.setAttributeId("C8CAEFA8-C1B0-05C0-154D-5EE8885CD0D2");
        cpa4.setAttributeName("conferenceProfileIVRGroup.isStartConferenceLeader");
        if (!inputCmd.get("chairpersonPassword").equalsIgnoreCase("")) {
            cpa4.setAttributeValue("true");
        } else {
            cpa4.setAttributeValue("false");
        }
        cpa4.setPolicyId("F6200CE1-EF3D-BD9F-D65D-5EE8885C716F");
        confPolicy.getProfileAttributes().add(cpa4);
        final JConferencePolicyAttribute cpa5 = new JConferencePolicyAttribute();
        cpa5.setAttributeId("7C5E4762-220F-81DD-16E1-5EE8885CB558");
        cpa5.setAttributeName("conferenceProfileGeneralGroup.transferRate");
        cpa5.setAttributeValue("Rate10240");
        cpa5.setPolicyId("F6200CE1-EF3D-BD9F-D65D-5EE8885C716F");
        confPolicy.getProfileAttributes().add(cpa5);
        final JConferencePolicyAttribute cpa6 = new JConferencePolicyAttribute();
        cpa6.setAttributeId("E47DEFA6-7E86-1BDC-20C7-5EE8885C9EE8");
        cpa6.setAttributeName("conferenceProfileVideoSettingGroup.layout");
        cpa6.setAttributeValue("Unknown");
        cpa6.setPolicyId("F6200CE1-EF3D-BD9F-D65D-5EE8885C716F");
        confPolicy.getProfileAttributes().add(cpa6);
        confPolicy.setPolicyName(inputCmd.get("conferenceTemplate")
                .replace("~", " "));
        confPolicy.setDmaTemplateName(inputCmd.get("conferenceTemplate")
                .replace("~", " "));
        conf.setConferencePolicy(confPolicy);
    }

    /**
     * Internal method, JSchedPart encryption status setting
     *
     * @param part
     *            participant object
     * @param setting
     *            encryption setting, value include yes, auto or no
     */
    private void setPartEncryptionInfo(final JSchedPart part,
                                       final String setting) {
        if (setting.equalsIgnoreCase("yes")) {
            part.setEncryption(true);
            part.setEncryptionAuto(false);
        } else if (setting.equalsIgnoreCase("auto")) {
            part.setEncryption(false);
            part.setEncryptionAuto(true);
        } else if (setting.equalsIgnoreCase("no")) {
            part.setEncryption(false);
            part.setEncryptionAuto(false);
        } else {
            part.setEncryption(false);
            part.setEncryptionAuto(true);
        }
    }

    /**
     * Update the conference settings
     *
     * @param allowOverbooking
     *            Allow overbooking of dial-in participants
     * @param conferenceTimeWarning
     *            Conference time warning
     * @param includConferenceCreator
     *            Automatically include conference creator (scheduler) in new
     *            conferences
     * @param pinLength
     *            Conference and chairperson passcode length
     * @return The result
     */
    public String setSchedulerSettings() {
        String status = "Failed";
        boolean allowOverbooking = false;
        boolean conferenceTimeWarning = true;
        boolean includConferenceCreator = false;
        boolean supportCloudAxis = false;
        if (inputCmd.get("allowOverbooking").equals("true")) {
            allowOverbooking = true;
        }
        if (inputCmd.get("conferenceTimeWarning").equals("false")) {
            conferenceTimeWarning = false;
        }
        if (inputCmd.get("includConferenceCreator").equals("true")) {
            includConferenceCreator = true;
        }
        if (inputCmd.get("supportCloudAxis").equals("true")) {
            supportCloudAxis = true;
        }
        final JSchedulerSettings schSettings = new JSchedulerSettings();
        schSettings.setAllowOverbooking(allowOverbooking);
        schSettings.setConferenceTimeWarning(conferenceTimeWarning);
        schSettings.setIncludeConferenceCreator(includConferenceCreator);
        schSettings.setPinLength(Integer.valueOf(inputCmd.get("pinLength")));
        schSettings.setRpwsAddress(inputCmd.get("rpwsServerAddress"));
        schSettings.setSupportCloudAxisEmailLink(supportCloudAxis);
        schSettings.setAudioOnlyConferenceEnabled(true);
        final JWebResult result = seh.setSchedulerSettings(userToken,
                                                           schSettings);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Successfully update the conference settings.");
            status = "SUCCESS";
        } else {
            logger.error("Failed to update the conference settings.");
            status = "Failed";
            return status + " Failed to update the conference settings.";
        }
        return status;
    }
}
