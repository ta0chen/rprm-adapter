package com.polycom.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.ConferenceServiceHandler;
import com.polycom.sqa.xma.webservices.driver.DeviceMetaManagerHandler;
import com.polycom.sqa.xma.webservices.driver.GroupManagerHandler;
import com.polycom.sqa.xma.webservices.driver.NetworkDeviceManagerHandler;
import com.polycom.sqa.xma.webservices.driver.SchedulerEngineManagerHandler;
import com.polycom.webservices.ConferenceService.ExternalCmaOngoingConference;
import com.polycom.webservices.ConferenceService.ExternalCmaOngoingParticipant;
import com.polycom.webservices.ConferenceService.JCMConferenceConnectionType;
import com.polycom.webservices.ConferenceService.JCMConferenceHostType;
import com.polycom.webservices.ConferenceService.JCMConferenceMediaType;
import com.polycom.webservices.ConferenceService.JCMConferenceStatus;
import com.polycom.webservices.ConferenceService.JCallDirection;
import com.polycom.webservices.ConferenceService.JDeviceAlias;
import com.polycom.webservices.ConferenceService.JDeviceAliasType;
import com.polycom.webservices.ConferenceService.JDeviceStatus;
import com.polycom.webservices.ConferenceService.JDeviceTypeVO;
import com.polycom.webservices.ConferenceService.JLeanConference;
import com.polycom.webservices.ConferenceService.JOngoingDevice;
import com.polycom.webservices.ConferenceService.JSchedConnectionType;
import com.polycom.webservices.ConferenceService.JSchedDevice;
import com.polycom.webservices.ConferenceService.JSchedPart;
import com.polycom.webservices.ConferenceService.JSchedPartMode;
import com.polycom.webservices.ConferenceService.JStatus;
import com.polycom.webservices.ConferenceService.JWebResult;
import com.polycom.webservices.ConferenceService.ParticipantStatus;
import com.polycom.webservices.GroupManager.JUsersAndGroups;
import com.polycom.webservices.UserManager.JConfGuest;
import com.polycom.webservices.UserManager.JEndpointForDetails;
import com.polycom.webservices.UserManager.JUser;

/**
 * Conference handler. This class will handle the webservice request of
 * Conference module
 *
 * @author wbchao
 *
 */
public class ConferenceHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "addParticipantToOngoingConference ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "conferenceName=anytimeconftest firstName=h323e164 lastName=guest partEndpointType=HDX partDialString=1909111495 partDialType=E164 partType=guest conferenceType=anytimeConference";
        // final String method = "getOngoingConferenceSpecific ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        // final String params =
        // "conferenceName=auto
        // keyword=participants[firstName=debuguser8]:lastName";
        // final String method = "terminateConference ";
        // final String auth =
        // "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
//         final String params = "conferenceName=anytimeconftest ";
        final String command = "http://localhost:8888/PlcmRmWeb/JConferenceService ConferenceService "
                + method + auth + params;
        final ConferenceHandler handler = new ConferenceHandler(command);
        final String result = handler.build();
        System.out.println("result==" + result);
    }

    private final ConferenceServiceHandler csh;

    public ConferenceHandler(final String cmd) throws IOException {
        super(cmd);
        csh = new ConferenceServiceHandler(webServiceUrl);
    }

    /**
     * Add Favorite To Ongoing Conference
     *
     *
     * @param conferenceName
     *            The conference name
     * @param favoriteListId
     *            The favorite list ugpid
     * @param favoriteMembers
     *            Favorite members which need to add to conference
     * @param partDeviceIp
     *            Part device ip
     * @param conferenceType
     *            Conference type
     * @return The result
     */
    public String addFavoriteToOngoingConference() {
        String status = "Failed";
        final List<Integer> ugpids = new LinkedList<Integer>();
        final GroupManagerHandler gh = new GroupManagerHandler(webServiceUrl);
        final SchedulerEngineManagerHandler seh = new SchedulerEngineManagerHandler(
                webServiceUrl);
        final List<JSchedPart> listofschedPart = new LinkedList<JSchedPart>();
        if (getInputCmd().get("conferenceName").isEmpty()
                || getInputCmd().get("favoriteListId").isEmpty()
                || getInputCmd().get("favoriteMembers").isEmpty()) {
            status = "Failed";
            logger.error("There are some mandatory parameters missing in the input command. "
                    + "Please check your input command.");
            return status
                    + " There are some mandatory parameters missing in the input command. "
                    + "Please check your input command.";
        }
        final String[] favoriteMembersSearchString = getInputCmd()
                .get("favoriteMembers").split(",");
        // Get the active conference ID
        final String confID = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The conference ID is: " + confID);
        if (confID.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot add participant to conference.");
            return status
                    + " Cannot find the conference ID using the given conference name.";
        }
        final JUsersAndGroups favoriteMembers = gh
                .getGroupMembers(userToken,
                                 "ugpId=" + getInputCmd().get("favoriteListId")
                                         + ",ou=local,ou=users,ou=readimanager");
        for (final com.polycom.webservices.GroupManager.JUser user : favoriteMembers
                .getUsers()) {
            for (final String memberSearchString : favoriteMembersSearchString) {
                if (memberSearchString.equalsIgnoreCase(user.getFirstName())
                        || memberSearchString
                                .equalsIgnoreCase(user.getLastName())
                        || memberSearchString
                                .equalsIgnoreCase(user.getDisplayName())) {
                    ugpids.add(user.getUgpId());
                }
            }
        }
        if (ugpids.size() == 0) {
            status = "Failed";
            logger.error("Cannot get favorite member by input "
                    + getInputCmd().get("favoriteMembers"));
            return status + " Cannot get favorite member by input "
                    + getInputCmd().get("favoriteMembers");
        }
        final String[] direction = inputCmd.get("partDialDirection").split(",");
        final String[] mode = inputCmd.get("mode").split(",");
        final String[] deviceIp = inputCmd.get("partDeviceIp").split(",");
        final String[] dialType = inputCmd.get("partDialType").split(",");
        final String[] encryption = inputCmd.get("partEncrypt").split(",");
        final String[] lineRate = inputCmd.get("partLineRate").split(",");
        final String confType = inputCmd.get("conferenceType");
        for (int i = 0; i < ugpids.size(); i++) {
            final com.polycom.webservices.ConferenceService.JSchedPart schedPart = new com.polycom.webservices.ConferenceService.JSchedPart();
            try {
                CommonUtils.copyProperties(
                                           seh.getSchedPartByUgpId(userToken,
                                                                   ugpids.get(i)),
                                           schedPart);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            setJSchedPartInfo(schedPart,
                              direction.length > i && !direction[i].isEmpty()
                                      ? direction[i] : "dialout",
                              mode.length > i && !mode[i].isEmpty() ? mode[i]
                                      : "video",
                              deviceIp[i],
                              dialType.length > i && !dialType[i].isEmpty()
                                      ? dialType[i] : "H323_E164",
                              encryption.length > i && !encryption[i].isEmpty()
                                      ? encryption[i] : "auto",
                              lineRate.length > i && !lineRate[i].isEmpty()
                                      ? lineRate[i] : "4096",
                              confType);
            listofschedPart.add(schedPart);
        }
        if (csh.addSchedParticipantsToConference(userToken,
                                                 confID,
                                                 listofschedPart)
                .getStatus()
                .compareTo(com.polycom.webservices.ConferenceService.JStatus.SUCCESS) == 0) {
            status = "SUCCESS";
            logger.info("Successfuly added favorites "
                    + getInputCmd().get("favoriteMembers"));
        } else {
            status = "Failed";
            logger.error("Failed to add the favorites "
                    + getInputCmd().get("favoriteMembers"));
            return status + " Failed to add the participant "
                    + getInputCmd().get("favoriteMembers");
        }
        return status;
    }

    /**
     * Add guest to ongoing conference
     *
     * @see conferenceName=$confName <br/>
     *      guestFirstName=ct <br/>
     *      guestLastName=guest
     *
     * @param conferenceName
     *            The conference name
     * @param guestFirstName
     *            The first name of guest
     * @param guestLastName
     *            The last name of guest
     * @return The result
     */
    public String addGuestToOngoingConference() {
        final List<JSchedPart> listofschedPart = new LinkedList<JSchedPart>();
        final String conferenceName = inputCmd.get("conferenceName")
                .replace("~", " ");
        // Get the active conference ID
        final String confId = getActiveConfId(conferenceName);
        logger.info("The conference ID is: " + confId);
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        final String firstName = inputCmd.get("firstName");
        final String lastName = inputCmd.get("lastName");
        // Search available guests
        for (final JConfGuest guest : umh.searchGuest(userToken)) {
            JSchedPart jsp = null;
            if (guest.getFirstName().equals(firstName)
                    && guest.getLastName().equals(lastName)) {
                // Guest in guestbook
                // Convert the guest to JSchedPart and add it to on going
                // conference
                jsp = convertGuestToJSchedPart(guest);
            } else {
                continue;
            }
            if (!ongoingConfInfo.getHostingDevices().isEmpty()
                    && ongoingConfInfo.getHostingDevices().iterator().next()
                            .getMcuId() != null) {
                jsp.setMcuId(ongoingConfInfo.getHostingDevices().get(0)
                        .getMcuId());
            }
            listofschedPart.add(jsp);
        }
        final JWebResult result = csh
                .addSchedParticipantsToConference(userToken,
                                                  confId,
                                                  listofschedPart);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            logger.info("Guest " + firstName + " " + lastName
                    + "is successfully added into the on going conference "
                    + conferenceName);
            return "SUCCESS";
        } else {
            logger.error("Guest " + firstName + " " + lastName
                    + "is not successfully added into the on going conference "
                    + conferenceName);
            return "Failed, " + result.getMessages();
        }
    }

    /**
     * Add Participant To Ongoing Conference
     *
     * @see conferenceName=$confName <br/>
     *      firstName=debuguser <br/>
     *      lastName=aaa <br/>
     *      partEndpointType=HDX <br/>
     *      partDialString=$hdxE164 <br/>
     *      partDialType=E164
     *
     * @param conferenceName
     *            The conference name
     * @param partEndpointType
     *            The participant endpoint type
     * @param partDialString
     *            The participant dial string
     * @param partDialType
     *            The participant dial type
     * @param firstName
     *            The first name of participant
     * @param lastName
     *            The last name of participant
     * @param partType
     *            The participant type, e.g. user or guest
     * @return The result
     */
    public String addParticipantToOngoingConference() {
        String status = "Failed";
        String dmaID = "";
        String dmaConfID = "";
        final SchedulerEngineManagerHandler seh = new SchedulerEngineManagerHandler(
                webServiceUrl);
        final String conferenceType = getInputCmd().get("conferenceType");
        final String conferenceName = getInputCmd().get("conferenceName")
                .replaceAll("~", " ");
        final String firstName = getInputCmd().get("firstName");
        final String lastName = getInputCmd().get("lastName");
        final String partType = getInputCmd().get("partType");
        final String direction = getInputCmd().get("partDialDirection");
        final String userEncryption = getInputCmd().get("partEncrypt");
        final String guestEncryption = getInputCmd().get("guestEncrypt");
        String dialRate = getInputCmd().get("partLineRate");

        if (conferenceType.isEmpty()) {
            return "Failed, the conferenceType is empty";
        }
        if (conferenceName.isEmpty()) {
            return "Failed, the conferenceName is empty";
        }
        if (firstName.isEmpty() && lastName.isEmpty()) {
            return "Failed, the firstName and lastName are both empty for "
                    + conferenceType;
        }
        // Get the active conference id
        final String confId = getActiveConfId(conferenceName);
        if (confId.isEmpty()) {
            return "Failed, Cannot find the conference id using the given conference name."
                    + "Due to this problem, we cannot add participant to conference.";
        }
        if (conferenceType.equalsIgnoreCase("adhocConference")
                || conferenceType.equalsIgnoreCase("anytimeConference")) {
            final String partEndpointType = getInputCmd()
                    .get("partEndpointType");
            final String partDialString = getInputCmd().get("partDialString");
            final String partDialType = getInputCmd().get("partDialType");
            final String mode = getInputCmd().get("mode");
            if (partEndpointType.isEmpty()) {
                return "Failed, the partEndpointType is empty for "
                        + conferenceType;
            }
            if (partDialString.isEmpty()) {
                return "Failed, the partDialString is empty for "
                        + conferenceType;
            }
            if (partDialType.isEmpty()) {
                return "Failed, the partDialType is empty for "
                        + conferenceType;
            }
            if (dialRate.isEmpty()) {
            	dialRate = "-1";
            }
            // Get the on going conference information
            final ExternalCmaOngoingConference ongoingConfInfo = csh
                    .getOngoingConference(userToken, confId);
            final List<ExternalCmaOngoingParticipant> partList = new LinkedList<ExternalCmaOngoingParticipant>();
            if (partType.equalsIgnoreCase("user")) {
                final JUser user = findUserForConference(firstName, lastName);
                if (user == null) {
                    return "Failed, could not find conference participant with name "
                            + firstName + " " + lastName;
                }
                final com.polycom.webservices.SchedulerEngine.JSchedPart schedPart = seh
                        .getSchedPartByUgpId(userToken, user.getUgpId());
                dmaConfID = ongoingConfInfo.getDmaConfId();
                dmaID = ongoingConfInfo.getDmaId();
                final JDeviceTypeVO jtvo = getConfDeviceTypeVO(partEndpointType);
                partList.add(JSchedPart2ExternalCmaOngoingParticipant(ongoingConfInfo,
                                                                      schedPart,
                                                                      dmaID,
                                                                      dmaConfID,
                                                                      partDialString,
                                                                      partDialType,
                                                                      jtvo,
                                                                      mode,
                                                                      direction,
                                                                      dialRate,
                                                                      userEncryption
                                                                      ));
            } else if (partType.equalsIgnoreCase("guest")) {
                JConfGuest confguest = null;
                for (final JConfGuest guest : umh.searchConfGuest(userToken)) {
                    if (guest.getFirstName().equals(firstName)
                            && guest.getLastName().equals(lastName)) {
                        confguest = guest;
                        break;
                    }
                }
                if (confguest == null) {
                    confguest = createTempGuest(firstName, lastName);
                }
                final JDeviceTypeVO jtvo = getConfDeviceTypeVO(partEndpointType);
                partList.add(JConfGuest2ExternalCmaOngoingParticipant(ongoingConfInfo,
                                                                      confguest,
                                                                      partDialString,
                                                                      partDialType,
                                                                      jtvo,
                                                                      mode,
                                                                      direction,
                                                                      dialRate,
                                                                      guestEncryption));
            }
            final JWebResult result = csh
                    .addOngoingParticipantsToConference(userToken,
                                                        confId,
                                                        partList);
            if (result.getStatus().equals(JStatus.SUCCESS)) {
                status = "SUCCESS";
                logger.info("Successfuly added the participant " + firstName
                        + " " + lastName + " into the on going conference "
                        + conferenceName.replace("~", " "));
            } else {
                status = "Failed";
                logger.error("Failed to add the participant " + firstName + " "
                        + lastName + " into the on going conference "
                        + conferenceName);
                return status + " Failed to add the participant " + firstName
                        + " " + lastName + " into the on going conference "
                        + conferenceName + "\n" + result.getMessages();
            }
        } else if (conferenceType.equalsIgnoreCase("poolConference")
                || conferenceType.equalsIgnoreCase("directConference")) {
            final List<JSchedPart> listofschedPart = new LinkedList<JSchedPart>();
            if (partType.equalsIgnoreCase("user")) {
                final JUser user = findUserForConference(firstName, lastName);
                if (user == null) {
                    return "Failed, could not find conference participant with name "
                            + firstName + " " + lastName;
                }
                final JSchedPart toPart = new JSchedPart();
                try {
                    final com.polycom.webservices.SchedulerEngine.JSchedPart fromPart = seh
                            .getSchedPartByUgpId(userToken, user.getUgpId());
                    CommonUtils.copyProperties(fromPart, toPart);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                setJSchedPartInfo(toPart,
                                  inputCmd.get("partDialDirection"),
                                  inputCmd.get("mode"),
                                  inputCmd.get("partDeviceIp"),
                                  inputCmd.get("partDialType"),
                                  inputCmd.get("partEncrypt"),
                                  inputCmd.get("partLineRate"),
                                  inputCmd.get("conferenceType"));
                listofschedPart.add(toPart);
            } else if (partType.equalsIgnoreCase("tempguest")) {
                final JSchedPart schedPart = createTempGuestSchedPart();
                listofschedPart.add(schedPart);
            }
            final JWebResult result = csh
                    .addSchedParticipantsToConference(userToken,
                                                      confId,
                                                      listofschedPart);
            if (result.getStatus().equals(JStatus.SUCCESS)) {
                status = "SUCCESS";
                logger.info("Successfuly added the participant " + firstName
                        + " " + lastName + " into the on going conference "
                        + conferenceName.replace("~", " "));
            } else {
                status = "Failed";
                logger.error("Failed to add the participant " + firstName + " "
                        + lastName + " into the on going conference "
                        + conferenceName);
                return status + " Failed to add the participant " + firstName
                        + " " + lastName + " into the on going conference "
                        + conferenceName;
            }
        }
        return status;
    }

    /**
     * Change the conference layout
     *
     * @see conferenceName=$confName
     *
     * @param conferenceName
     *            The conference name
     * @param confLayout
     *            The conference layout
     * @return The result
     */
    public String changeConferenceLayout() {
        String status = "Failed";
        // Get the active conference ID
        final String confId = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        final String layout = getInputCmd().get("confLayout");
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot change conference layout.");
            return status;
        }
        if (csh.setConferenceLayout(userToken, confId, layout).getStatus()
                .equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfully change the layout of conference "
                    + getInputCmd().get("conferenceName").replace("~", " ")
                    + " to " + layout);
        } else {
            status = "Failed";
            logger.error("Failed to change the layout of conference "
                    + getInputCmd().get("conferenceName").replace("~", " ")
                    + " to " + layout);
            return status + " Failed to change the layout of conference "
                    + getInputCmd().get("conferenceName").replace("~", " ")
                    + " to " + layout;
        }
        return status;
    }

    /**
     * Internal method, check the participant detail in on going conference
     *
     * @param participant
     *            The actual participant
     * @param partName
     *            The expected participant name
     * @param partStatus
     *            The expected participant status
     * @param partDialMode
     *            The expected participant dial mode
     * @param partMediaType
     *            The expected participant media type
     * @param partEndpointName
     *            The expected participant endpoint name
     * @param partAccessMode
     *            The expected participant access mode
     * @param partDialNum
     *            The expected participant dial number
     * @param partBitRate
     *            The expected participant bit rate
     * @return The result
     */
    private String
            checkParticipantDetailInOngoingConf(final ExternalCmaOngoingParticipant participant,
                                                final String partName,
                                                final String partStatus,
                                                final String partDialMode,
                                                final String partMediaType,
                                                final String partEndpointName,
                                                final String partAccessMode,
                                                final String partDialNum,
                                                final String partBitRate) {
        String status = "Failed";
        // Check the participant connection status
        final ParticipantStatus actualStatus = participant.getStatus();
        if (getInputCmd().get(partStatus).equalsIgnoreCase("connected")
                && (actualStatus.equals(ParticipantStatus.CONNECTED)
                        || actualStatus
                                .equals(ParticipantStatus.CONNECTED_WARNING)
                        || actualStatus
                                .equals(ParticipantStatus.CONNECTED_ERRORS))) {
            status = "SUCCESS";
            logger.info("Participant " + getInputCmd().get(partName)
                    + " connection status is as expected "
                    + getInputCmd().get(partStatus));
        } else
            if (getInputCmd().get(partStatus).equalsIgnoreCase("disconnected")
                    && participant.getStatus()
                            .compareTo(ParticipantStatus.DISCONNECTED) == 0) {
            status = "SUCCESS";
            logger.info("Participant " + getInputCmd().get(partName)
                    + " connection status is as expected "
                    + getInputCmd().get(partStatus));
        } else {
            status = "Failed";
            logger.error("Participant " + getInputCmd().get(partName)
                    + " connection status is not as expected "
                    + getInputCmd().get(partStatus));
            return status + " Participant " + getInputCmd().get(partName)
                    + " connection status is not as expected "
                    + getInputCmd().get(partStatus);
        }
        // Check the participant dial mode part1DialMode
        if (getInputCmd().get(partDialMode).equalsIgnoreCase("Dial-In")
                && participant.getCallDirection()
                        .compareTo(JCallDirection.INCOMING) == 0) {
            status = "SUCCESS";
            logger.info("The participant " + getInputCmd().get(partName)
                    + " dial mode is as expected "
                    + getInputCmd().get(partDialMode));
        } else if (getInputCmd().get(partDialMode).equalsIgnoreCase("Dial-Out")
                && participant.getCallDirection()
                        .compareTo(JCallDirection.OUTGOING) == 0) {
            status = "SUCCESS";
            logger.info("The participant " + getInputCmd().get(partName)
                    + " dial mode is as expected "
                    + getInputCmd().get(partDialMode));
        } else {
            status = "Failed";
            logger.error("The participant " + getInputCmd().get(partName)
                    + " dial mode is not as expected "
                    + getInputCmd().get(partDialMode));
            return status + " The participant " + getInputCmd().get(partName)
                    + " dial mode is not as expected "
                    + getInputCmd().get(partDialMode);
        }
        // Check the participant participant type
        if (getInputCmd().get(partMediaType).equalsIgnoreCase("AudioVideo")
                && participant.getMediaType()
                        .compareTo(JCMConferenceMediaType.AUDIO_VIDEO) == 0) {
            status = "SUCCESS";
            logger.info("The participant " + getInputCmd().get(partName)
                    + " media type is as expected "
                    + getInputCmd().get(partMediaType));
        } else if (getInputCmd().get(partMediaType).equalsIgnoreCase("Audio")
                && participant.getMediaType()
                        .compareTo(JCMConferenceMediaType.AUDIO) == 0) {
            status = "SUCCESS";
            logger.info("The participant " + getInputCmd().get(partName)
                    + " media type is as expected "
                    + getInputCmd().get(partMediaType));
        } else {
            status = "Failed";
            logger.error("The participant " + getInputCmd().get(partName)
                    + " media type is not as expected "
                    + getInputCmd().get(partMediaType));
            return status + " The participant " + getInputCmd().get(partName)
                    + " media type is not as expected "
                    + getInputCmd().get(partMediaType);
        }
        // Check the participant endpoint name
        if (getInputCmd().get(partEndpointName)
                .equalsIgnoreCase(participant.getInUseDevice().getName())) {
            status = "SUCCESS";
            logger.info("The participant " + getInputCmd().get(partName)
                    + " endpoint name is as expected "
                    + getInputCmd().get(partEndpointName));
        } else {
            status = "Failed";
            logger.error("The participant " + getInputCmd().get(partName)
                    + " endpoint name is not as expected "
                    + getInputCmd().get(partEndpointName));
            return status + " The participant " + getInputCmd().get(partName)
                    + " endpoint name is not as expected "
                    + getInputCmd().get(partEndpointName);
        }
        // Check the participant access mode
        final String expectAccessMode = getInputCmd().get(partAccessMode);
        String actualAccessMode = null;
        if (participant.getAudioConnectionType() != null) {
            actualAccessMode = participant.getAudioConnectionType().value();
        } else {
            if (participant.getInUseDevice()
                    .getPreferredConnectionType() != null) {
                actualAccessMode = participant.getInUseDevice()
                        .getPreferredConnectionType().value();
            } else {
                if (participant.getInUseDevice().getDialString()
                        .contains("sip")) {
                    actualAccessMode = "SIP";
                } else {
                    actualAccessMode = "H323";
                }
            }
        }
        if (actualAccessMode.toUpperCase()
                .contains(expectAccessMode.toUpperCase())) {
            status = "SUCCESS";
            logger.info("The participant " + getInputCmd().get(partName)
                    + " access mode is as expected " + expectAccessMode);
        } else {
            status = "Failed";
            final String errorMsg = "The participant "
                    + getInputCmd().get(partName) + " access mode "
                    + expectAccessMode + " does not support."
                    + " The actual access mode is " + actualAccessMode;
            logger.error(errorMsg);
            return status + errorMsg;
        }
        // // Check dial number
        // if (participant.getDialNumber() == null
        // || participant.getDialNumber().equals(getInputCmd()
        // .get(partDialNum))) {
        // status = "SUCCESS";
        // logger.info("The participant " + getInputCmd().get(partName)
        // + " dial number is as expected "
        // + getInputCmd().get(partDialNum));
        // } else {
        // status = "Failed";
        // logger.error("The participant " + getInputCmd().get(partName)
        // + " dial number is not as expected "
        // + getInputCmd().get(partDialNum)
        // + " The actual dial number is "
        // + participant.getDialNumber());
        // return status + " The participant " + getInputCmd().get(partName)
        // + " dial number is not as expected "
        // + getInputCmd().get(partDialNum)
        // + " The actual dial number is "
        // + participant.getDialNumber();
        // }
        // Check the participant 1# call bit rate part1BitRate
        if (!getInputCmd().get(partBitRate).isEmpty()) {
            if (Integer.valueOf(getInputCmd().get(partBitRate)) >= (participant
                    .getInUseDevice().getAudioRateUsedKbps()
                    + participant.getInUseDevice().getVideoRateUsedKbps())) {
                status = "SUCCESS";
                logger.info("The participant " + getInputCmd().get(partName)
                        + " bit rate is as expected "
                        + "which is below the negotiated call rate "
                        + getInputCmd().get(partBitRate) + " kbps.");
            } else {
                status = "Failed";
                logger.error("The participant " + getInputCmd().get(partName)
                        + " bit rate is greater than the negotiated call rate "
                        + getInputCmd().get(partBitRate)
                        + ". The actual call rate is "
                        + (participant.getInUseDevice().getAudioRateUsedKbps()
                                + participant.getInUseDevice()
                                        .getVideoRateUsedKbps()));
                return status + " The participant "
                        + getInputCmd().get(partName)
                        + " bit rate is greater than the negotiated call rate "
                        + getInputCmd().get(partBitRate)
                        + ". The actual call rate is "
                        + (participant.getInUseDevice().getAudioRateUsedKbps()
                                + participant.getInUseDevice()
                                        .getVideoRateUsedKbps());
            }
        } else {
            logger.info("No bit rate found in the input commands. "
                    + "Hence will not validate the bit rate for participant"
                    + getInputCmd().get(partName));
        }
        return status;
    }

    /**
     * Internal method, convert guest to JSchedPart
     *
     * @param umh
     *            The UserManagerHandler instance
     * @param guest
     *            The guest to convert
     * @return JSchedPart
     */
    private JSchedPart convertGuestToJSchedPart(final JConfGuest guest) {
        final JSchedPart schedPart = new JSchedPart();
        final JSchedDevice schedDevice = new JSchedDevice();
        if (guest.isDialIn()) {
            schedPart.setCallDirection(JCallDirection.INCOMING);
        } else {
            schedPart.setCallDirection(JCallDirection.OUTGOING);
        }
        schedPart.setConfGuestId(guest.getId());
        schedPart.setEmail(guest.getEmail());
        schedPart.setFirstName(guest.getFirstName());
        schedPart.setGuest(true);
        schedPart.setLastName(guest.getLastName());
        schedPart.setMode(JSchedPartMode.fromValue(getInputCmd().get("mode").toUpperCase()));
        if (getInputCmd().get("partEncrypt").equalsIgnoreCase("yes")) {
        	schedPart.setEncryption(true);
        } else {
        	schedPart.setEncryption(false);
        }
        schedPart.setEncryptionAuto(false);
        schedPart.setServiceName("##ANYMCUSERVICE##");
        schedPart.setGuestBookLocation(guest.getLocation());
        schedDevice.setDeviceId(-1);
        schedDevice.setDialIn(guest.isDialIn());
        schedDevice.setH323Capable(guest.isSupportsH323());
        schedDevice.setIpAddress(guest.getIpAddr());
        schedDevice.setIpExtension(guest.getExtensionIP());
        schedDevice.setIsdnAreaCode(guest.getAreaCode());
        schedDevice.setIsdnCapable(guest.isSupportsIsdn());
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
            schedDevice.setPreferredConnectionType(JSchedConnectionType.H_323);
        } else if (guest.isSupportsIsdn()) {
        	schedDevice.setPreferredConnectionType(JSchedConnectionType.ISDN);
        } else if (guest.isSupportsSip()) {
        	schedDevice.setPreferredConnectionType(JSchedConnectionType.SIP);
        }
        schedDevice.setScheduledDevice(true);
        schedDevice.setSipCapable(guest.isSupportsSip());
        schedDevice.setSipUri(guest.getSipUri());
        if (getInputCmd().get("partLineRate").isEmpty()) {
        	schedDevice.setVideoSpeed(10240);
        } else {
        	schedDevice.setVideoSpeed(Integer.valueOf(getInputCmd().get("partLineRate")).intValue());	
        }
        schedDevice.setMaxSpeedIP(10240);
        schedDevice.setMaxSpeedISDN(10240);
        schedDevice.setRequiredMcuService("##ANYMCUSERVICE##");
        schedPart.getSchedDeviceCollection().add(schedDevice);
        return schedPart;
    }

    /**
     * Internal method, create temp guest participant
     *
     * @param index
     *            The index
     * @return JSchedPart
     */
    private JConfGuest createTempGuest(final String firstName,
                                       final String lastName) {
        final JConfGuest guest = new JConfGuest();
        CommonUtils.generateField(guest);
        guest.setFirstName(firstName);
        guest.setLastName(lastName);
        return guest;
    }

    /**
     * Internal method, create temp guest participant
     *
     * @param index
     *            The index
     * @return JSchedPart
     */
    private JSchedPart createTempGuestSchedPart() {
        final JSchedPart schedPart = new JSchedPart();
        final JSchedDevice schedDevice = new JSchedDevice();
        schedPart.setAllDevicesOutOfArea(false);
        schedPart.setBelongsToAreaUgpId(0);
        final String firstName = inputCmd.get("firstName");
        final String lastName = inputCmd.get("lastName");
        final String direction = inputCmd.get("partDialDirection");
        final String dialString = inputCmd.get("partDialString");
        final String dialType = inputCmd.get("partDialType");
        final String encryption = inputCmd.get("partEncrypt");
        final String mediaType = inputCmd.get("mode");
        if (direction.equals("dialin")) {
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
        schedPart.setEmail(firstName + lastName + "@123.com");
        if (encryption.equalsIgnoreCase("yes")) {
            schedPart.setEncryption(true);
            schedPart.setEncryptionAuto(false);
        } else if (encryption.equalsIgnoreCase("auto")) {
            schedPart.setEncryption(true);
            schedPart.setEncryptionAuto(true);
        } else if (encryption.equalsIgnoreCase("no")) {
            schedPart.setEncryption(false);
            schedPart.setEncryptionAuto(false);
        } else {
            schedPart.setEncryption(false);
            schedPart.setEncryptionAuto(true);
        }
        schedPart.setFirstName(firstName);
        schedPart.setGroup(false);
        schedPart.setGuest(true);
        schedPart.setITPMaster(false);
        schedPart.setLastName(lastName);
        schedPart.setLecturer(false);
        schedPart.setMode(JSchedPartMode.valueOf(mediaType));
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
        schedDevice.setName(firstName + lastName);
        schedDevice.setOwnerUgpId(0);
        schedDevice.setScheduledDevice(true);
        schedDevice.setSipCapable(true);
        schedDevice.setVip(false);
        schedDevice.setAllowedOnRMX(false);
        schedDevice.setRequiredMcuService("##ANYMCUSERVICE##");
        int maxSpeedInt = 10240;
        int speedInt = 0;
        if (inputCmd.get("conferenceType").equalsIgnoreCase("anytime")
                || inputCmd.get("conferenceType").equalsIgnoreCase("poolConference")
                || inputCmd.get("conferenceType").equalsIgnoreCase("recurringPoolConf")) {
        	speedInt = -1;
        } else {
        	speedInt = 384;
        }
        if (!inputCmd.get("partLineRate").isEmpty()) {
        	speedInt = Integer.parseInt(inputCmd.get("partLineRate"));
        }
        schedDevice.setMaxSpeedIP(maxSpeedInt);
        schedDevice.setMaxSpeedISDN(maxSpeedInt);
        schedDevice.setVideoSpeed(speedInt);
        final String[] numbers = dialString.split(":");
        final String number = numbers[0];
        String extention = "";
        if (numbers.length == 2) {
            extention = numbers[1];
        }
        schedPart.setExtention(extention);
        if (dialType.equalsIgnoreCase("h323")) {
            schedDevice.setIpAddress(number);
            schedDevice.setIpExtension(extention);
            schedDevice.setPreferredConnectionType(JSchedConnectionType.H_323);
        } else if (dialType.equalsIgnoreCase("h323_e164")
                || dialType.equalsIgnoreCase("h323_id")
                || dialType.equalsIgnoreCase("h323_annexO")) {
            final JDeviceAlias deviceAlias = new JDeviceAlias();
            deviceAlias.setDbKey(new Integer(0));
            deviceAlias.setDeviceEntityId(0);
            deviceAlias.setSourceId(null);
            deviceAlias.setValue(number);
            deviceAlias.setExtension(extention);
            if (dialType.equalsIgnoreCase("h323_e164")) {
                deviceAlias.setType(JDeviceAliasType.E_164);
                schedDevice
                        .setPreferredConnectionType(JSchedConnectionType.H_323___E_164);
            } else if (dialType.equalsIgnoreCase("h323_id")) {
                deviceAlias.setType(JDeviceAliasType.H_323);
                schedDevice
                        .setPreferredConnectionType(JSchedConnectionType.H_323___ID);
            } else {
                deviceAlias.setType(JDeviceAliasType.URL);
                schedDevice
                        .setPreferredConnectionType(JSchedConnectionType.H_323___ANNEX___O);
            }
            schedDevice.getAliasList().add(deviceAlias);
        } else if (dialType.equalsIgnoreCase("sip")) {
            schedDevice.setPreferredConnectionType(JSchedConnectionType.SIP);
            schedPart.setExtention(extention);
            schedDevice.setSipUri(number);
        } else if (dialType.equalsIgnoreCase("isdn")) {
            schedDevice.setPreferredConnectionType(JSchedConnectionType.ISDN);
            schedPart.setExtention(extention);
            schedDevice.setIsdnCapable(true);
            schedDevice.setIsdnExtension(extention);
            schedDevice.setIsdnFullNumber(number);
        }
        schedPart.getSchedDeviceCollection().add(schedDevice);
        return schedPart;
    }

    public String extendConference() {
        // Get the active conference ID
        final String conferenceName = getInputCmd().get("conferenceName")
                .replace("~", " ");
        final String confId = getActiveConfId(conferenceName);
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            return "Failed, could not find the conference id with conference name "
                    + conferenceName;
        }
        final long confEndTime = Long.parseLong(inputCmd.get("confEndTime"));
        final JWebResult result = csh.extendConference(userToken,
                                                       confId,
                                                       confEndTime);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            return "SUCCESS";
        } else {
            return "Failed to extend conferenc " + conferenceName
                    + " errorMsg is " + result.getMessages();
        }
    }

    /**
     * Internal method, get the user for conference participant
     *
     * @param firstName
     * @param lastName
     * @return JUser
     */
    private JUser findUserForConference(final String firstName,
                                        final String lastName) {
        final List<JUser> userList = umh
                .findConferenceParticipants(userToken, firstName, lastName);
        for (final JUser user : userList) {
            if (user.getFirstName().equals(firstName)
                    && user.getLastName().equals(lastName)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Internal method, get active conference id
     *
     * @param confName
     *            The conference name
     * @return The active conference id
     */
	private String getActiveConfId(final String confName) {
		String confId = "";
		if (confName.contains("*")) {
			List<JLeanConference> confList = csh.getFilteredLeanConferenceList(
					userToken, "confStatus:ongoing");
			for (final JLeanConference lc : confList) {
				if (lc.getConfName().contains(confName.replace("*", ""))) {
					confId = lc.getCmConfId();
				}
			}
		} else {
			List<JLeanConference> confList = csh.getFilteredLeanConferenceList(
					userToken, "confName:" + confName, "confStatus:ongoing");
			if (confList.size() != 1) {
				confList = csh.getFilteredLeanConferenceList(userToken,
						"confName:");
				for (final JLeanConference lc : confList) {
					if (confName.equals(lc.getConfName())
							&& ((lc.getConfStatus().compareTo(
									JCMConferenceStatus.ACTIVE___ALERTS) == 0) || (lc
									.getConfStatus().compareTo(
											JCMConferenceStatus.ACTIVE) == 0))) {
						confId = lc.getCmConfId();
						logger.info("The active conference ID is: " + confId);
					}
				}
			} else {
				confId = confList.get(0).getCmConfId();
			}
		}
		return confId;
	}

    /**
     * Internal method, get the Device VO
     *
     * @param deviceType
     *            The device type
     * @return JDeviceTypeVO
     */
    private JDeviceTypeVO getConfDeviceTypeVO(final String deviceType) {
        final DeviceMetaManagerHandler dmmh = new DeviceMetaManagerHandler(
                webServiceUrl);
        final com.polycom.webservices.DeviceMetaManager.JDeviceTypeVO metaDeviceType = dmmh
                .getDeviceTypeByDeviceModel(userToken, deviceType);
        final com.polycom.webservices.ConferenceService.JDeviceTypeVO _addDeviceAutoPopulate_deviceType = new com.polycom.webservices.ConferenceService.JDeviceTypeVO();
        final com.polycom.webservices.ConferenceService.JDeviceCapability dc = new com.polycom.webservices.ConferenceService.JDeviceCapability();
        dc.setAlertable(metaDeviceType.getCapability().isAlertable());
        dc.setAssignable(metaDeviceType.getCapability().isAssignable());
        dc.setCallStatusStartTimeInGMTMode(metaDeviceType.getCapability()
                .isCallStatusStartTimeInGMTMode());
        dc.setEndpoint(true);
        dc.setGroupedDevice(metaDeviceType.getCapability().isGroupedDevice());
        dc.setH323Supported(metaDeviceType.getCapability().isH323Supported());
        dc.setMinusEqualsEmpty(metaDeviceType.getCapability()
                .isMinusEqualsEmpty());
        dc.setNeedAdditionalCommand(metaDeviceType.getCapability()
                .isNeedAdditionalCommand());
        dc.setNotConcernedWithConferenceGuestDevice(metaDeviceType
                .getCapability().isNotConcernedWithConferenceGuestDevice());
        dc.setSoftEndpoint(metaDeviceType.getCapability().isSoftEndpoint());
        dc.setSupportAclByModel(metaDeviceType.getCapability()
                .isSupportAclByModel());
        dc.setSupportAutoReplaceH323TraversalIP(metaDeviceType.getCapability()
                .isSupportAutoReplaceH323TraversalIP());
        dc.setSupportAutoSoftupdate(metaDeviceType.getCapability()
                .isSupportAutoSoftupdate());
        dc.setSupportCDR(metaDeviceType.getCapability().isSupportCDR());
        dc.setSupportDialout(metaDeviceType.getCapability().isSupportDialout());
        dc.setSupportGMS(metaDeviceType.getCapability().isSupportGMS());
        dc.setSupportH320(metaDeviceType.getCapability().isSupportH320());
        dc.setSupportLegacyProvision(metaDeviceType.getCapability()
                .isSupportLegacyProvision());
        dc.setSupportLegacySoftupdate(metaDeviceType.getCapability()
                .isSupportLegacySoftupdate());
        dc.setSupportMSM(metaDeviceType.getCapability().isSupportMSM());
        dc.setSupportMaintenanceWindow(metaDeviceType.getCapability()
                .isSupportMaintenanceWindow());
        dc.setSupportManualAdd(metaDeviceType.getCapability()
                .isSupportManualAdd());
        dc.setSupportSIPTraversal(metaDeviceType.getCapability()
                .isSupportSIPTraversal());
        dc.setSupportSNMPV3(metaDeviceType.getCapability().isSupportSNMPV3());
        dc.setSupportSoftupdateByModel(metaDeviceType.getCapability()
                .isSupportSoftupdateByModel());
        dc.setSupportVC2AndLegacyMode(metaDeviceType.getCapability()
                .isSupportVC2AndLegacyMode());
        dc.setSupportsBundledProvisioning(metaDeviceType.getCapability()
                .isSupportsBundledProvisioning());
        dc.setSupportsCDRSync(metaDeviceType.getCapability()
                .isSupportsCDRSync());
        dc.setSupportsCredentialsChanged(metaDeviceType.getCapability()
                .isSupportsCredentialsChanged());
        dc.setSupportsGetCallStatus(metaDeviceType.getCapability()
                .isSupportsGetCallStatus());
        dc.setSupportsHangUp(metaDeviceType.getCapability().isSupportsHangUp());
        dc.setSupportsLegacyNotification(metaDeviceType.getCapability()
                .isSupportsLegacyNotification());
        dc.setSupportsPeripheral(metaDeviceType.getCapability()
                .isSupportsPeripheral());
        dc.setSupportsPresence(metaDeviceType.getCapability()
                .isSupportsPresence());
        dc.setSupportsRabbitEyeConfig(metaDeviceType.getCapability()
                .isSupportsRabbitEyeConfig());
        dc.setSupportsSystemConfig(metaDeviceType.getCapability()
                .isSupportsSystemConfig());
        dc.setSupportsVenusConfig(metaDeviceType.getCapability()
                .isSupportsVenusConfig());
        dc.setVc2Device(metaDeviceType.getCapability().isVc2Device());
        dc.setVideoHardwareEndpoint(metaDeviceType.getCapability()
                .isVideoHardwareEndpoint());
        dc.setVideoSoftwareEndpoint(metaDeviceType.getCapability()
                .isVideoSoftwareEndpoint());
        dc.setAAutopopulatedDeviceType(metaDeviceType.getCapability()
                .isAAutopopulatedDeviceType());
        dc.setABorderDevice(metaDeviceType.getCapability().isABorderDevice());
        dc.setACommunicableDeviceType(metaDeviceType.getCapability()
                .isACommunicableDeviceType());
        dc.setAGabDevice(metaDeviceType.getCapability().isAGabDevice());
        dc.setAManageableDeviceType(metaDeviceType.getCapability()
                .isAManageableDeviceType());
        dc.setAPollingForCallDataDeviceType(metaDeviceType.getCapability()
                .isAPollingForCallDataDeviceType());
        dc.setAPollingForStatusDeviceType(metaDeviceType.getCapability()
                .isAPollingForStatusDeviceType());
        dc.setGKRegisteredNetworkDevice(metaDeviceType.getCapability()
                .isGKRegisteredNetworkDevice());
        _addDeviceAutoPopulate_deviceType.setCapability(dc);
        if (deviceType.equalsIgnoreCase("HDX")
                || deviceType.equalsIgnoreCase("GroupSeries")
                || deviceType.equalsIgnoreCase("ITP")) {
            _addDeviceAutoPopulate_deviceType
                    .setDbValue(metaDeviceType.getDbValue());
            _addDeviceAutoPopulate_deviceType
                    .setDisplayName(metaDeviceType.getDisplayName());
            _addDeviceAutoPopulate_deviceType
                    .setFamily(com.polycom.webservices.ConferenceService.JDeviceFamilyType.VIDEO___ENDPOINT);
            _addDeviceAutoPopulate_deviceType
                    .setSchemaName(metaDeviceType.getSchemaName());
            _addDeviceAutoPopulate_deviceType
                    .setShortName(metaDeviceType.getShortName());
        }
        final List<com.polycom.webservices.DeviceMetaManager.JDeviceTypeParam> metadtpList = metaDeviceType
                .getParams();
        for (final com.polycom.webservices.DeviceMetaManager.JDeviceTypeParam metadtp : metadtpList) {
            final com.polycom.webservices.ConferenceService.JDeviceTypeParam dtp = new com.polycom.webservices.ConferenceService.JDeviceTypeParam();
            dtp.setName(metadtp.getName());
            dtp.setValue(metadtp.getValue());
            _addDeviceAutoPopulate_deviceType.getParams().add(dtp);
        }
        return _addDeviceAutoPopulate_deviceType;
    }

    /**
     * Get the specified attribute value of conference
     *
     * @see conferenceName=$confName <br/>
     *      keyword=startDateTime:unixTime
     *
     * @param conferenceName
     *            The conference name
     *
     * @param keyword
     *            The attribute name of ExternalCmaOngoingConference class
     * @return The keyword attribute value
     */
    public String getConferenceSpecific() {
        final String keyword = inputCmd.get("keyword");
        // Get the active or future conference ID
        final String confId;
        try {
            confId = getConfId(getInputCmd().get("conferenceName")
                    .replace("~", " "));
        } catch (final Exception e) {
            e.printStackTrace();
            return "Failed, got exception when get conference. Error msg is "
                    + e.getMessage();
        }
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the conference detail information.");
            return "Failed, cannot find the conference ID using the given conference name "
                    + getInputCmd().get("conferenceName").replace("~", " ");
        }
        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        return CommonUtils.invokeGetMethod(ongoingConfInfo, keyword);
    }

    /**
     * Internal method, get active or future conference id
     *
     * @param confName
     *            The conference name
     * @return The conference id
     */
    private String getConfId(final String confName) {
        String confId = "";
        confId = getActiveConfId(confName);
        if (confId.isEmpty()) {
        	confId = getFutureConfId(confName);
        }
        return confId;
    }

    /**
     * Internal method, return the full name with firstName + lastName
     *
     * @param firstName
     *            The first name, could be null
     * @param lastName
     *            The last name, could be null
     * @return The full name with firstName + lastName
     */
    private String getFullName(String firstName, String lastName) {
        if (firstName == null) {
            firstName = "";
        }
        if (lastName == null) {
            lastName = "";
        }
        return firstName + lastName;
    }

    /**
     * Get the specified attribute value of the future conference
     *
     * @see conferenceName=$confName <br/>
     *      keyword=startDateTime:unixTime
     *
     * @param conferenceName
     *            The conference name
     *
     * @param keyword
     *            The attribute name of ExternalCmaOngoingConference class
     * @return The keyword attribute value
     */
    public String getFutureConferenceSpecific() {
        final String keyword = inputCmd.get("keyword");
        // Get the active conference ID
        final String confId = getFutureConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The future conference ID is: " + confId);
        if (confId.isEmpty()) {
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the conference detail information.");
            return "Failed, cannot find the conference ID using the given conference name "
                    + getInputCmd().get("conferenceName").replace("~", " ");
        }
        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        return CommonUtils.invokeGetMethod(ongoingConfInfo, keyword);
    }

    /**
     * Internal method, get active conference id
     *
     * @param confName
     *            The conference name
     * @return The active conference id
     */
	private String getFutureConfId(final String confName) {
		String confId = "";
		if (confName.contains("*")) {
			List<JLeanConference> confList = csh.getFilteredLeanConferenceList(
					userToken, "confStatus:future");
			for (final JLeanConference lc : confList) {
				if (lc.getConfName().contains(confName.replace("*", ""))) {
					confId = lc.getCmConfId();
				}
			}
		} else if (confName.contains("anytime")) {
			List<JLeanConference> confList = csh.getFilteredLeanConferenceList(
					userToken, "confStatus:future", "isAnytimeConf:true");
			for (final JLeanConference lc : confList) {
				if (lc.getConfName().equalsIgnoreCase(confName)) {
					confId = lc.getCmConfId();
				}
			}
		} else {
			List<JLeanConference> confList = csh.getFilteredLeanConferenceList(
					userToken, "confName:" + confName, "confStatus:future");
			if (confList.size() != 1) {
				confList = csh.getFilteredLeanConferenceList(userToken,
						"confName:");
				for (final JLeanConference lc : confList) {
					if (confName.equals(lc.getConfName())
							&& ((lc.getConfStatus().compareTo(
									JCMConferenceStatus.FUTURE) == 0) || (lc
									.getConfStatus()
									.compareTo(
											JCMConferenceStatus.FUTURE___ALERTS) == 0))) {
						confId = lc.getCmConfId();
						logger.info("The future conference ID is: " + confId);
					}
				}
			} else {
				confList.get(0).getCmConfId();
			}
		}
		return confId;
	}

    /**
     * Validate the conference info
     *
     * @see conferenceName=$confName <br/>
     *      confStatus=active <br/>
     *      confType=AdhocMCUConf <br/>
     *      confMediaType=AudioVideo <br/>
     *      confCreatorName=N/A <br/>
     *      confOwnerName=N/A <br/>
     *      confRate=$rate <br/>
     *      confConnectionType=MULTIPOINT <br/>
     *      confStartTime=$XMACurrTime <br/>
     *      confDuration=$ConfDuration <br/>
     *      partNum=12
     *
     * @param conferenceName
     *            The conference name
     * @param confStatus
     *            The expected conference status
     * @param confType
     *            The expected conference type
     * @param confMediaType
     *            The expected conference media type
     * @param confCreatorName
     *            The expected conference creator name
     * @param confConnectionType
     *            The expected conference connection type
     * @param confStartTime
     *            The expected conference start time
     * @param confDuration
     *            The expected conference duration
     * @param confOwnerName
     *            The expected conference owner name
     * @param confPassword
     *            The expected conference password(Optional)
     * @param partNum
     *            The expected conference participant number
     * @param chairPersonPassword
     *            The expected conference chair person password(Optional)
     * @param vmrNum
     *            The expected conference vmr number(Optional)
     * @return The result
     */
    public String getOngoingConferenceAndValidateConferenceInfo() {
        String status = "Failed";
        // Get the active conference ID
        final String confId = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The conference ID is: " + confId);
        String errorMsg = "";
        if (confId.isEmpty()) {
            status = "Failed";
            errorMsg = "Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the conference detail information.";
            logger.error(errorMsg);
            return status + ", " + errorMsg;
        }
        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        if (!getInputCmd().get("conferenceName").isEmpty()
                && !getInputCmd().get("confStatus").isEmpty()
                && !getInputCmd().get("confMediaType").isEmpty()
                && !getInputCmd().get("confCreatorName").isEmpty()
                && !getInputCmd().get("confConnectionType").isEmpty()
                && !getInputCmd().get("confOwnerName").isEmpty()) {
            // check the conference name
            if (ongoingConfInfo.getConfName().equals(getInputCmd()
                    .get("conferenceName").replace("~", " "))) {
                status = "SUCCESS";
                logger.info("The conferenc name is as expected: "
                        + getInputCmd().get("conferenceName").replace("~",
                                                                      " "));
            } else {
                status = "Failed";
                errorMsg = "The conference name is not correct. The expected name is: "
                        + getInputCmd().get("conferenceName").replace("~", " ")
                        + ". However the current name is: "
                        + ongoingConfInfo.getConfName();
                logger.error(errorMsg);
                return status + ", " + errorMsg;
            }
            // check the conference participants count if any
            if (!getInputCmd().get("partNum").isEmpty()) {
                if (String.valueOf(ongoingConfInfo.getParticipants().size())
                        .equals(getInputCmd().get("partNum"))) {
                    status = "SUCCESS";
                    logger.info("The conference participants number is as expected");
                } else {
                    status = "Failed";
                    errorMsg = "The conference participants number is not as expected."
                            + "The expected number is: "
                            + getInputCmd().get("partNum")
                            + ". However the current number is: "
                            + ongoingConfInfo.getParticipants().size();
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
            } else {
                logger.info("Command without partNum param, so no need to check conference participants count.");
            }
            // check the conference creator name
            if (!getInputCmd().get("confType").equalsIgnoreCase("AnytimeConf")
                    && !getInputCmd().get("confType")
                            .equalsIgnoreCase("AdhocDMAConf")) {
                final String expectConfCreatorName = getInputCmd()
                        .get("confCreatorName").replaceAll("~", " ").trim();
                final String actualConfCreatorName = ongoingConfInfo
                        .getCreatorName();
                if (actualConfCreatorName.equals(expectConfCreatorName)) {
                    status = "SUCCESS";
                    logger.info("The conference creator name is as expected: "
                            + getInputCmd().get("confCreatorName"));
                } else if (actualConfCreatorName.isEmpty()
                        && expectConfCreatorName.equals("N/A")) {
                    status = "SUCCESS";
                    logger.info("The conference creator name is as expected: "
                            + getInputCmd().get("confCreatorName"));
                } else {
                    status = "Failed";
                    errorMsg = "The conference creator name is not correct. The expected name is: "
                            + getInputCmd().get("confCreatorName")
                            + ". however the current name is: "
                            + ongoingConfInfo.getCreatorName();
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
            }
            // check the conference owner name
            final String expectConfOwnerName = getInputCmd()
                    .get("confOwnerName").replaceAll("~", " ").trim();
            final String actualConfOwnerName = ongoingConfInfo.getOwnerName();
            if (actualConfOwnerName.equals(expectConfOwnerName)) {
                status = "SUCCESS";
                logger.info("The conference owner name is as expected: "
                        + getInputCmd().get("confOwnerName"));
            } else if (actualConfOwnerName.isEmpty()
                    && expectConfOwnerName.equals("N/A")) {
                status = "SUCCESS";
                logger.info("The conference owner name is as expected: "
                        + getInputCmd().get("confOwnerName"));
            } else {
                status = "Failed";
                errorMsg = "The conference owner name is not correct. The expected name is: "
                        + getInputCmd().get("confOwnerName")
                                .replaceAll("~", " ").trim()
                        + ". however the current name is: "
                        + ongoingConfInfo.getOwnerName();
                logger.error(errorMsg);
                return status + ", " + errorMsg;
            }
            // check the conference status
            if (getInputCmd().get("confStatus").equalsIgnoreCase("ACTIVE")
                    && ((ongoingConfInfo.getConfStatus()
                            .compareTo(JCMConferenceStatus.ACTIVE) == 0)
                            || (ongoingConfInfo.getConfStatus()
                                    .compareTo(JCMConferenceStatus.ACTIVE___ALERTS) == 0))) {
                status = "SUCCESS";
                logger.info("The conference status is as expected: "
                        + getInputCmd().get("confStatus"));
            } else if (getInputCmd().get("confStatus")
                    .equalsIgnoreCase("FUTURE")
                    && ((ongoingConfInfo.getConfStatus()
                            .compareTo(JCMConferenceStatus.FUTURE) == 0)
                            || (ongoingConfInfo.getConfStatus()
                                    .compareTo(JCMConferenceStatus.FUTURE___ALERTS) == 0))) {
                status = "SUCCESS";
                logger.info("The conference status is as expected: "
                        + getInputCmd().get("confStatus"));
            } else if (getInputCmd().get("confStatus")
                    .equalsIgnoreCase("FINISHED")
                    && (ongoingConfInfo.getConfStatus()
                            .compareTo(JCMConferenceStatus.FINISHED) == 0)) {
                status = "SUCCESS";
                logger.info("The conference status is as expected: "
                        + getInputCmd().get("confStatus"));
            } else
                if (getInputCmd().get("confStatus").equalsIgnoreCase("DELETED")
                        && (ongoingConfInfo.getConfStatus()
                                .compareTo(JCMConferenceStatus.DELETED) == 0)) {
                status = "SUCCESS";
                logger.info("The conference status is as expected: "
                        + getInputCmd().get("confStatus"));
            } else {
                status = "Failed";
                errorMsg = "The conference status is not correct. The expected status is: "
                        + getInputCmd().get("confStatus")
                        + ". However the current status is: "
                        + ongoingConfInfo.getConfStatus().toString();
                logger.error(errorMsg);
                return status + ", " + errorMsg;
            }
            // check the conference type
            if (!getInputCmd().get("confType").isEmpty()) {
                if (getInputCmd().get("confType").equalsIgnoreCase("PooledConf")
                        && !ongoingConfInfo.isBridgeConference()
                        && ongoingConfInfo.getConfHostType()
                                .compareTo(JCMConferenceHostType.CMA) == 0) {
                    status = "SUCCESS";
                    logger.info("The conference type is as expected: "
                            + getInputCmd().get("confType"));
                } else if (getInputCmd().get("confType")
                        .equalsIgnoreCase("DirectMCUConf")
                        && ongoingConfInfo.isBridgeConference()
                        && ongoingConfInfo.getConfHostType()
                                .compareTo(JCMConferenceHostType.CMA) == 0) {
                    status = "SUCCESS";
                    logger.info("The conference type is as expected: "
                            + getInputCmd().get("confType"));
                } else if (getInputCmd().get("confType")
                        .equalsIgnoreCase("AnytimeConf")
                        && !ongoingConfInfo.isBridgeConference()
                        && ongoingConfInfo.isScheduledConference()
                        && ongoingConfInfo.getScheduledStartDateTime() == null) {
                    status = "SUCCESS";
                    logger.info("The conference type is as expected: "
                            + getInputCmd().get("confType"));
                } else if (getInputCmd().get("confType")
                        .equalsIgnoreCase("AdhocMCUConf")
                        && ongoingConfInfo.isBridgeConference()
                        && !ongoingConfInfo.isScheduledConference()) {
                    status = "SUCCESS";
                    logger.info("The conference type is as expected: "
                            + getInputCmd().get("confType"));
                } else if (getInputCmd().get("confType")
                        .equalsIgnoreCase("AdhocDMAConf")
                        && !ongoingConfInfo.isBridgeConference()
                        && !ongoingConfInfo.isScheduledConference()) {
                    status = "SUCCESS";
                    logger.info("The conference type is as expected: "
                            + getInputCmd().get("confType"));
                } else {
                    status = "Failed";
                    errorMsg = "The conference type is not as expected: "
                            + getInputCmd().get("confType");
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
            }
            // check the conference media type
            if (getInputCmd().get("confMediaType")
                    .equalsIgnoreCase("AudioVideo")
                    && ongoingConfInfo.getConfMediaType()
                            .compareTo(JCMConferenceMediaType.AUDIO_VIDEO) == 0) {
                status = "SUCCESS";
                logger.info("The conference media type is as expected: "
                        + getInputCmd().get("confMediaType"));
            } else
                if (getInputCmd().get("confMediaType").equalsIgnoreCase("Audio")
                        && ongoingConfInfo.getConfMediaType()
                                .compareTo(JCMConferenceMediaType.AUDIO) == 0) {
                status = "SUCCESS";
                logger.info("The conference media type is as expected: "
                        + getInputCmd().get("confMediaType"));
            } else {
                status = "Failed";
                errorMsg = "The conference media type is not as expected: "
                        + getInputCmd().get("confMediaType");
                logger.error(errorMsg);
                return status + ", " + errorMsg;
            }
            // check the conference connection type
            if (getInputCmd().get("confConnectionType")
                    .equalsIgnoreCase("MULTIPOINT")
                    && ongoingConfInfo.getConnectionType()
                            .compareTo(JCMConferenceConnectionType.MULTIPOINT) == 0) {
                status = "SUCCESS";
                logger.info("The conference connection type is as expected: "
                        + getInputCmd().get("confConnectionType"));
            } else if (getInputCmd().get("confConnectionType")
                    .equalsIgnoreCase("EMCU")
                    && ongoingConfInfo.getConnectionType()
                            .compareTo(JCMConferenceConnectionType.EMBEDDED_MULTIPOINT) == 0) {
                status = "SUCCESS";
                logger.info("The conference connection type is as expected: "
                        + getInputCmd().get("confConnectionType"));
            } else if (getInputCmd().get("confConnectionType")
                    .equalsIgnoreCase("P2P")
                    && ongoingConfInfo.getConnectionType()
                            .compareTo(JCMConferenceConnectionType.POINT_TO_POINT) == 0) {
                status = "SUCCESS";
                logger.info("The conference connection type is as expected: "
                        + getInputCmd().get("confConnectionType"));
            } else if (getInputCmd().get("confConnectionType")
                    .equalsIgnoreCase("GATEWAY")
                    && ongoingConfInfo.getConnectionType()
                            .compareTo(JCMConferenceConnectionType.GATEWAY) == 0) {
                status = "SUCCESS";
                logger.info("The conference connection type is as expected: "
                        + getInputCmd().get("confConnectionType"));
            } else {
                status = "Failed";
                errorMsg = "The conference connection type is not as expected: "
                        + getInputCmd().get("confConnectionType");
                logger.error(errorMsg);
                return status + ", " + errorMsg;
            }
            // check the conference password if any
            if (!getInputCmd().get("confPassword").isEmpty()) {
                if (ongoingConfInfo.getConfPassword()
                        .equals(getInputCmd().get("confPassword"))) {
                    status = "SUCCESS";
                    logger.info("The conference password is as expected: "
                            + getInputCmd().get("confPassword"));
                } else {
                    status = "Failed";
                    errorMsg = "The conference password is not correct. The expected password is: "
                            + getInputCmd().get("confPassword")
                            + ". However the current password is: "
                            + ongoingConfInfo.getConfPassword();
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
            } else {
                logger.info("The on going conference does not have the password.");
            }
            // check the conference chairperson password if any
            if (!getInputCmd().get("chairPersonPassword").isEmpty()) {
                if (ongoingConfInfo.getConfChairPin()
                        .equals(getInputCmd().get("chairPersonPassword"))) {
                    status = "SUCCESS";
                    logger.info("The conference chairperson password is as expected: "
                            + getInputCmd().get("chairPersonPassword"));
                } else {
                    status = "Failed";
                    errorMsg = "The conference chairperson password is not correct. The expected password is: "
                            + getInputCmd().get("chairPersonPassword")
                            + ". However the current chairperson password is: "
                            + ongoingConfInfo.getConfChairPin();
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
            } else {
                logger.info("The on going conference chairperson does not have the password.");
            }
            // check VMR if any
            if (!getInputCmd().get("vmrNum").isEmpty()) {
                if (ongoingConfInfo.getDmaConfRoomId()
                        .equals(getInputCmd().get("vmrNum"))) {
                    status = "SUCCESS";
                    logger.info("The VMR number is as expected: "
                            + getInputCmd().get("vmrNum"));
                } else {
                    status = "Failed";
                    errorMsg = "The VMR number is not as expected: "
                            + getInputCmd().get("vmrNum");
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
            } else {
                logger.info("Not VMR input. Hence assume it is not DMA related conference.");
            }
            // check conference start time
            final String confType = getInputCmd().get("confType");
            if (confType.equalsIgnoreCase("DirectMCUConf")
                    || confType.equalsIgnoreCase("PooledConf")) {
                logger.info("The conference schedule end time is: "
                        + ongoingConfInfo.getScheduledEndDateTime()
                                .getUnixTime());
                logger.info("The conference schedule start time is: "
                        + ongoingConfInfo.getScheduledEndDateTime()
                                .getUnixTime());
                if ((Long.valueOf(getInputCmd().get("confStartTime"))
                        - ongoingConfInfo.getScheduledStartDateTime()
                                .getUnixTime()) <= 60000) {
                    status = "SUCCESS";
                    logger.info("The conference start time is as expected: "
                            + getInputCmd().get("confStartTime"));
                } else {
                    status = "Failed";
                    errorMsg = "The conference start time is not as expected: "
                            + getInputCmd().get("confStartTime");
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
            }
            // check the conference duration time, will not check any time
            // conference
            if (confType.equalsIgnoreCase("DirectMCUConf")
                    || confType.equalsIgnoreCase("PooledConf")) {
                if ((ongoingConfInfo.getScheduledEndDateTime().getUnixTime()
                        - ongoingConfInfo.getScheduledStartDateTime()
                                .getUnixTime())
                        / 1000 == Integer
                                .valueOf(getInputCmd().get("confDuration"))) {
                    status = "SUCCESS";
                    logger.info("Conference duration is as expected: "
                            + getInputCmd().get("confDuration") + " seconds");
                } else {
                    status = "Failed";
                    errorMsg = "Conference duration is not as expected: "
                            + getInputCmd().get("confDuration") + " seconds";
                    logger.error(errorMsg);
                    return status + ", " + errorMsg;
                }
            }
        } else {
            status = "Failed";
            errorMsg = "Some parameters are missing when trying to validate the on going conference."
                    + " Please check your input command.";
            logger.error(errorMsg);
            return status + ", " + errorMsg;
        }
        return status;
    }

    /**
     * Validate the participants info
     *
     * @see partNum=3 <br/>
     *      participant1=debuguseraaa <br/>
     *      part1Status=connected <br/>
     *      part1MediaType=AudioVideo <br/>
     *      part1EndpointName=aaadebuguserHDX <br/>
     *      part1AccessMode=H323_E164 <br/>
     *      part1DialNum=$vmr <br/>
     *      part1DialMode=Dial-In <br/>
     *      participant2=chentao <br/>
     *      part2Status=connected <br/>
     *      part2MediaType=AudioVideo <br/>
     *      part2EndpointName=taochenRP-Desktop <br/>
     *      part2AccessMode=$calltype <br/>
     *      part2DialNum=$vmr <br/>
     *      part2BitRate=$callrate <br/>
     *      part2DialMode=Dial-In <br/>
     *      participant3=debuguser3ccc <br/>
     *      part3Status=connected <br/>
     *      part3MediaType=AudioVideo <br/>
     *      part3EndpointName=cccdebuguser3GroupSeries <br/>
     *      part3AccessMode=H323_E164 <br/>
     *      part3DialNum=$vmr <br/>
     *      part3DialMode=Dial-In
     *
     * @param conferenceName
     *            The conference name
     * @param partNum
     *            The expected participant number
     * @param partName
     *            [1-partNum] The expected participant name
     * @param partStatus
     *            [1-partNum] The expected participant status
     * @param partDialMode
     *            [1-partNum] The expected participant dial mode
     * @param partMediaType
     *            [1-partNum] The expected participant media type
     * @param partEndpointName
     *            [1-partNum] The expected participant endpoint name
     * @param partAccessMode
     *            [1-partNum] The expected participant access mode
     * @param partDialNum
     *            [1-partNum] The expected participant dial number
     * @param partBitRate
     *            [1-partNum] The expected participant bit rate
     * @return The result
     */
    public String getOngoingConferenceAndValidateParticipantsInfo() {
        String status = "Failed";
        // Get the active conference ID
        final String confId = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the participants detail information.");
            return status;
        }
        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        // Check the conference participants number
        if (String.valueOf(ongoingConfInfo.getParticipants().size())
                .equals(getInputCmd().get("partNum"))) {
            status = "SUCCESS";
            logger.info("The conference participants number is as expected");
        } else {
            status = "Failed";
            logger.error("The conference participants number is not as expected."
                    + "The expected number is: " + getInputCmd().get("partNum")
                    + ". However the current number is: "
                    + ongoingConfInfo.getParticipants().size());
            return status
                    + " The conference participants number is not as expected."
                    + "The expected number is: " + getInputCmd().get("partNum")
                    + ". However the current number is: "
                    + ongoingConfInfo.getParticipants().size();
        }
        // Check the participant detail information
        for (final ExternalCmaOngoingParticipant participant : ongoingConfInfo
                .getParticipants()) {
            String participantName = "";
            if (participant.getFirstName() != null
                    && participant.getLastName() != null) {
                participantName = participant.getFirstName()
                        + participant.getLastName();
            } else if (participant.getFirstName() == null
                    && participant.getLastName() != null) {
                participantName = participant.getLastName();
            } else if (participant.getLastName() == null
                    && participant.getFirstName() != null) {
                participantName = participant.getFirstName();
            }
            logger.info("Checking participant " + participantName);
            for (int i = 1; i <= ongoingConfInfo.getParticipants()
                    .size(); i++) {
                if (getInputCmd().get("participant" + i)
                        .equals(participant.getFirstName()
                                + participant.getLastName())) {
                    status = checkParticipantDetailInOngoingConf(participant,
                                                                 "participant"
                                                                         + i,
                                                                 "part" + i
                                                                         + "Status",
                                                                 "part" + i
                                                                         + "DialMode",
                                                                 "part" + i
                                                                         + "MediaType",
                                                                 "part" + i
                                                                         + "EndpointName",
                                                                 "part" + i
                                                                         + "AccessMode",
                                                                 "part" + i
                                                                         + "DialNum",
                                                                 "part" + i
                                                                         + "BitRate");
                    if (!status.equalsIgnoreCase("SUCCESS")) {
                        logger.error("Participant "
                                + getInputCmd().get("participant" + i)
                                + " detail information checking during the conference failed.");
                        return status + " Participant "
                                + getInputCmd().get("participant" + i)
                                + " detail information checking during the conference failed.";
                    }
                }
            }
        }
        return status;
    }

    /**
     * Get the ongoing conference id
     *
     * @see conferenceName=$confName
     *
     * @param conferenceName
     *            The conference name
     * @return The conference id
     */
    public String getOngoingConferenceId() {
        String confID = "";
        // Get the active conference ID
        confID = getActiveConfId(getInputCmd().get("conferenceName")
                .replace("~", " "));
        logger.info("The conference ID is: " + confID);
        return confID;
    }

    /**
     * Get the specified attribute value of conference
     *
     * @see conferenceName=$confName <br/>
     *      keyword=startDateTime:unixTime
     *
     * @param conferenceName
     *            The conference name
     *
     * @param keyword
     *            The attribute name of ExternalCmaOngoingConference class
     * @return The keyword attribute value
     */
    public String getOngoingConferenceSpecific() {
        final String keyword = inputCmd.get("keyword");
        // Get the active conference ID
        final String confId = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The on going conference ID is: " + confId);
        if (confId.isEmpty()) {
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the conference detail information.");
            return "Failed, cannot find the conference ID using the given conference name "
                    + getInputCmd().get("conferenceName").replace("~", " ");
        }
        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        return CommonUtils.invokeGetMethod(ongoingConfInfo, keyword);
    }

    /**
     * Internal method, get the paticipant from firstName and lastName
     *
     * @param conf
     *            The conference to search
     * @param firstName
     *            The expected firstName
     * @param lastName
     *            The expected lastName
     * @return The participant
     */
    private ExternalCmaOngoingParticipant
            getParticipant(final ExternalCmaOngoingConference conf,
                           final String firstName,
                           final String lastName,
                           final String dialNumber) {
        final String expectedFullName = getFullName(firstName, lastName);
        for (final ExternalCmaOngoingParticipant participant : conf
                .getParticipants()) {
            final String actualFirstName = participant.getFirstName();
            final String actualLastName = participant.getLastName();
            final String actualFullName = getFullName(actualFirstName,
                                                      actualLastName);
            final String actualDialNumber = participant.getDialNumber();
            if (!actualFullName.isEmpty() && (participant.isRoom()
                    ? actualFullName.contains(expectedFullName)
                    : actualFullName.equals(expectedFullName))) {
                return participant;
            } else if (actualDialNumber != null
                    && actualDialNumber.equals(dialNumber)) {
                return participant;
            }
        }
        return null;
    }

    /**
     * Get the participant audio mute state
     *
     * @see conferenceName=$confName <br/>
     *      firstName=ad <br/>
     *      lastName=user2
     *
     * @param conferenceName
     *            The conference name
     * @param firstName
     *            The first name of participant
     * @param lastName
     *            The last name of participant
     * @return The participant audio mute state
     */
    public String getParticipantAudioMute() {
        String status = "Failed";
        // Get the active conference ID
        final String confId = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the conference detail information.");
            return status;
        }
        final String lastName = getInputCmd().get("lastName");
        final String firstName = getInputCmd().get("firstName");
        final String dialString = getInputCmd().get("partDialString");
        
        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        final ExternalCmaOngoingParticipant targetParticipant = getParticipant(ongoingConfInfo,
                                                                               firstName,
                                                                               lastName,
                                                                               dialString);
        if (targetParticipant == null) {
            return "Failed, could not find participant " + firstName + " "
                    + lastName + " in conference";
        } else {
            final String result = targetParticipant.isAudioMute() + "";
            return result.toUpperCase();
        }
    }

    /**
     * Get the participant video mute state
     *
     * @see conferenceName=$confName <br/>
     *      firstName=ad <br/>
     *      lastName=user2
     *
     * @param conferenceName
     *            The conference name
     * @param firstName
     *            The first name of participant
     * @param lastName
     *            The last name of participant
     * @return The participant video mute state
     */
    public String getParticipantVideoMute() {
        String status = "Failed";
        // Get the active conference ID
        final String confId = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the conference detail information.");
            return status;
        }
        final String lastName = getInputCmd().get("lastName");
        final String firstName = getInputCmd().get("firstName");
        final String dialString = getInputCmd().get("partDialString");

        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        final ExternalCmaOngoingParticipant targetParticipant = getParticipant(ongoingConfInfo,
                                                                               firstName,
                                                                               lastName,
                                                                               dialString);
        if (targetParticipant == null) {
            return "Failed, could not find participant " + firstName + " "
                    + lastName + " in conference";
        } else {
            final String result = targetParticipant.isVideoMute() + "";
            return result.toUpperCase();
        }
    }

    @Override
    protected void injectCmdArgs() {
        // Conference name
        put("conferenceName", "");
        // Conference status. Active, inactive or something else
        put("confStatus", "");
        // Conference password
        put("confPassword", "");
        // Conference chairperson password
        put("chairPersonPassword", "");
        // Conference type. Pooled, MCU conference etc.
        put("confType", "");
        // Conference media type. AudioVideo, Audio etc.
        put("confMediaType", "");
        // Conference start time
        put("confStartTime", "");
        // Conference stop time
        put("confEndTime", "");
        // Conference duration. In seconds.
        put("confDuration", "");
        // Conference creator name. Firstname lastname.
        put("confCreatorName", "");
        // Conference owner name
        put("confOwnerName", "");
        // Conference chair person name
        put("confChairPersonName", "");
        // Conference connection type. Multipoint, EMCU etc.
        put("confConnectionType", "");
        // Conference layout
        put("confLayout", "");
        // Conference auto layout
        put("confAutoLayout", "false");
        // VMR number for pooled and any time conference
        put("vmrNum", "");
        // Conference expected participants number
        put("partNum", "");
        // First name of the participant to be added in the on going conference
        put("firstName", "");
        // Last name of the participant to be added in the on going conference
        put("lastName", "");
        // Participant dial string for the one added in the on going conference
        put("partDialString", "");
        // Participant dial type for the one added in the on going conference,
        // SIP, H323, E164 or ISDN
        put("partDialType", "");
        // The participant endpoint type which is added in the on going
        // conference, HDX, GS
        put("partEndpointType", "");
        // The participant type which is added in the on going conference, User,
        // Guest
        put("partType", "user");
        // The participant call direction
        put("partDialDirection", "dialout");
        // The participant mode, video or audio
        put("mode", "video");
        // The participant device ip
        put("partDeviceIp", "");
        // The participant encryption setting
        put("partEncrypt", "auto");
        // The participant line rate setting
        put("partLineRate", "");
        // The participant audio/video connection type. True for unmute, false
        // for mute.
        put("connectionState", "");
        // Conference participant 1# name. Should be FirstnameLastname format.
        put("participant1", "");
        // Participant 1# connection status
        put("part1Status", "");
        // Participant 1# type. AudioVideo etc.
        put("part1MediaType", "");
        // Participant 1# endpoint
        put("part1EndpointName", "");
        // Participant 1# mode type. Audio or video
        put("part1ModeType", "");
        // Participant 1# access mode. H323, H323ID, E164, IP, SIP etc.
        put("part1AccessMode", "");
        // Participant 1# dial number
        put("part1DialNum", "");
        // Participant 1# bit rate
        put("part1BitRate", "");
        // Participant 1# dial mode. Dial in or out.
        put("part1DialMode", "");
        // Participant 1# bridge name.
        put("part1BridgeName", "");
        // Participant 1# encryption status
        put("part1Encryption", "");
        // Conference participant 2# name. Should be FirstnameLastname format.
        put("participant2", "");
        // Participant 2# connection status
        put("part2Status", "");
        // Participant 2# type. AudioVideo etc.
        put("part2MediaType", "");
        // Participant 2# endpoint
        put("part2EndpointName", "");
        // Participant 2# access mode. H323, H323ID, E164, IP, SIP etc.
        put("part2AccessMode", "");
        // Participant 2# dial number
        put("part2DialNum", "");
        // Participant 2# bit rate
        put("part2BitRate", "");
        // Participant 2# dial mode. Dial in or out.
        put("part2DialMode", "");
        // Conference participant 3# name. Should be FirstnameLastname format.
        put("participant3", "");
        // Participant 3# connection status
        put("part3Status", "");
        // Participant 3# type. AudioVideo etc.
        put("part3MediaType", "");
        // Participant 3# endpoint
        put("part3EndpointName", "");
        // Participant 3# access mode. H323, H323ID, E164, IP, SIP etc.
        put("part3AccessMode", "");
        // Participant 3# dial number
        put("part3DialNum", "");
        // Participant 3# bit rate
        put("part3BitRate", "");
        // Participant 3# dial mode. Dial in or out.
        put("part3DialMode", "");
        // Conference participant 4# name. Should be FirstnameLastname format.
        put("participant4", "");
        // Participant 4# connection status
        put("part4Status", "");
        // Participant 4# type. AudioVideo etc.
        put("part4MediaType", "");
        // Participant 4# endpoint
        put("part4EndpointName", "");
        // Participant 4# access mode. H323, H323ID, E164, IP, SIP etc.
        put("part4AccessMode", "");
        // Participant 4# dial number
        put("part4DialNum", "");
        // Participant 4# bit rate
        put("part4BitRate", "");
        // Participant 4# dial mode. Dial in or out.
        put("part4DialMode", "");
        // Conference participant 5# name. Should be FirstnameLastname format.
        put("participant5", "");
        // Participant 5# connection status
        put("part5Status", "");
        // Participant 5# type. AudioVideo etc.
        put("part5MediaType", "");
        // Participant 5# endpoint
        put("part5EndpointName", "");
        // Participant 5# access mode. H323, H323ID, E164, IP, SIP etc.
        put("part5AccessMode", "");
        // Participant 5# dial number
        put("part5DialNum", "");
        // Participant 5# bit rate
        put("part5BitRate", "");
        // Participant 5# dial mode. Dial in or out.
        put("part5DialMode", "");
        // Conference participant 6# name. Should be FirstnameLastname format.
        put("participant6", "");
        // Participant 6# connection status
        put("part6Status", "");
        // Participant 6# type. AudioVideo etc.
        put("part6MediaType", "");
        // Participant 6# endpoint
        put("part6EndpointName", "");
        // Participant 6# access mode. H323, H323ID, E164, IP, SIP etc.
        put("part6AccessMode", "");
        // Participant 6# dial number
        put("part6DialNum", "");
        // Participant 6# bit rate
        put("part6BitRate", "");
        // Participant 6# dial mode. Dial in or out.
        put("part6DialMode", "");
        // Conference participant 7# name. Should be FirstnameLastname format.
        put("participant7", "");
        // Participant 7# connection status
        put("part7Status", "");
        // Participant 7# type. AudioVideo etc.
        put("part7MediaType", "");
        // Participant 7# endpoint
        put("part7EndpointName", "");
        // Participant 7# access mode. H323, H323ID, E164, IP, SIP etc.
        put("part7AccessMode", "");
        // Participant 7# dial number
        put("part7DialNum", "");
        // Participant 7# bit rate
        put("part7BitRate", "");
        // Participant 7# dial mode. Dial in or out.
        put("part7DialMode", "");
        // Conference participant 8# name. Should be FirstnameLastname format.
        put("participant8", "");
        // Participant 8# connection status
        put("part8Status", "");
        // Participant 8# type. AudioVideo etc.
        put("part8MediaType", "");
        // Participant 8# endpoint
        put("part8EndpointName", "");
        // Participant 8# access mode. H323, H323ID, E164, IP, SIP etc.
        put("part8AccessMode", "");
        // Participant 8# dial number
        put("part8DialNum", "");
        // Participant 8# bit rate
        put("part8BitRate", "");
        // Participant 8# dial mode. Dial in or out.
        put("part8DialMode", "");
        // Guest name
        put("guestName", "");
        // First name of the guest
        put("guestFirstName", "");
        // Last name of the guest
        put("guestLastName", "");
        // encryption setting of guest
        put("guestEncrypt", "auto");
        // Favorite list id
        put("favoriteListId", "");
        // Favorite list member search string
        put("favoriteMembers", "");
    }

    /**
     * Internal method, convert JConfGuest to ExternalCmaOngoingParticipant
     *
     * @param ongoingConf
     *            The conference
     * @param guest
     *            The guest to convert
     * @param dialstring
     *            The dial string
     * @param dialType
     *            The dial type
     * @param jtvo
     *            JDeviceTypeVO
     * @param mode
     *            audio or video
     * @return ExternalCmaOngoingParticipant
     */
    private ExternalCmaOngoingParticipant
            JConfGuest2ExternalCmaOngoingParticipant(final ExternalCmaOngoingConference ongoingConf,
                                                     final JConfGuest guest,
                                                     final String dialstring,
                                                     final String dialType,
                                                     final JDeviceTypeVO jtvo,
                                                     final String mode,
                                                     final String direction,
                                                     final String dialRate,
                                                     final String encryption) {
        final String[] numbers = dialstring.split(":");
        final String number = numbers[0];
        String extention = "";
        if (numbers.length == 2) {
            extention = numbers[1];
        }
        final ExternalCmaOngoingParticipant ecop = new ExternalCmaOngoingParticipant();
        ecop.setAllDevicesOutOfArea(false);
        ecop.setBelongsToAreaUgpId(guest.getBelongsToAreaUgpId());
        if (direction.equals("dialout")) {
        	ecop.setCallDirection(JCallDirection.OUTGOING);
        } else if (direction.equals("dialin")) {
        	ecop.setCallDirection(JCallDirection.INCOMING);
        }  
        ecop.setCascadeLink(false);
        ecop.setChairperson(false);
        ecop.setConfGuestId(0);
        ecop.setDeviceEntityId_Dont_Use(294);
        if (encryption.equalsIgnoreCase("yes")) {
        	ecop.setEncryption(true);
        	ecop.setEncryptionAuto(false);
        } else if (encryption.equalsIgnoreCase("auto")) {
        	ecop.setEncryption(true);
        	ecop.setEncryptionAuto(true);
        } else if (encryption.equalsIgnoreCase("no")) {
        	ecop.setEncryption(false);
        	ecop.setEncryptionAuto(false);
        } else {
        	ecop.setEncryption(false);
        	ecop.setEncryptionAuto(true);
        }

        ecop.setGroup(false);
        ecop.setGuest(true);
        ecop.setITPMaster(false);
        ecop.setLastName(guest.getLastName());
        ecop.setFirstName(guest.getFirstName());
        ecop.setLecturer(false);
        if (!ongoingConf.getHostingDevices().isEmpty() && ongoingConf.getHostingDevices().iterator().next()
                .getMcuId() != null) {
            ecop.setMcuId(ongoingConf.getHostingDevices().iterator().next()
                    .getMcuId());
        }
        if (mode.equalsIgnoreCase("video")) {
            ecop.setMode(JSchedPartMode.VIDEO);
        } else if (mode.equalsIgnoreCase("audio")) {
            ecop.setMode(JSchedPartMode.AUDIO);
        } else {
            ecop.setMode(JSchedPartMode.VIDEO);
        }
        ecop.setOperator(false);
        ecop.setOwner(false);
        ecop.setOwnerUGPId(guest.getUgpId());
        ecop.setParentDeviceId(0);
        ecop.setRoom(false);
        if (guest.getRequiredMcuServiceIP() != null) {
            ecop.setServiceName(guest.getRequiredMcuServiceIP());
        } else {
            ecop.setServiceName("##ANYMCUSERVICE##");
        }
        ecop.setVip(false);
        ecop.setVirtualParticipant(false);
        ecop.setAudioMcuMute(false);
        ecop.setAudioMute(false);
        ecop.setAudioSelfMute(false);
        ecop.setBridgePartId(0);
        ecop.setCdrCreated(false);
        if (ongoingConf.getDmaConfId() != null) {
            ecop.setDmaConfId(ongoingConf.getDmaConfId());
        }
        if (ongoingConf.getDmaId() != null) {
            ecop.setDmaId(ongoingConf.getDmaId());
        }
        final JOngoingDevice jod = new JOngoingDevice();
		if (guest.getAlias() != null) {
			final com.polycom.webservices.ConferenceService.JDeviceAlias confda = new com.polycom.webservices.ConferenceService.JDeviceAlias();
			confda.setDbKey(guest.getAlias().getDbKey());
			confda.setDeviceEntityId(guest.getAlias().getDeviceEntityId());
			confda.setValue(guest.getAlias().getValue());
			if (com.polycom.webservices.UserManager.JDeviceAliasType.E_164
					.equals(guest.getAlias().getType())) {
				confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.E_164);
			} else if (com.polycom.webservices.UserManager.JDeviceAliasType.H_323
					.equals(guest.getAlias().getType())) {
				confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.H_323);
			} else if (com.polycom.webservices.UserManager.JDeviceAliasType.URL
					.equals(guest.getAlias().getType())) {
				confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.URL);
			} else if (com.polycom.webservices.UserManager.JDeviceAliasType.PARTY_NUMBER
					.equals(guest.getAlias().getType())) {
				confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.PARTY_NUMBER);
			} else if (com.polycom.webservices.UserManager.JDeviceAliasType.TRANSPORT_ADDRESS
					.equals(guest.getAlias().getType())) {
				confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.TRANSPORT_ADDRESS);
			} else if (com.polycom.webservices.UserManager.JDeviceAliasType.E_MAIL
					.equals(guest.getAlias().getType())) {
				confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.E_MAIL);
			} else if (com.polycom.webservices.UserManager.JDeviceAliasType.UNKNOWN
					.equals(guest.getAlias().getType())) {
				confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.UNKNOWN);
			}
			jod.getAliasList().add(confda);
		}
        jod.setBehindFirewall(false);
        jod.setBelongsToAreaUgpId(guest.getBelongsToAreaUgpId());
        jod.setDeviceId(-1);
        jod.setDialIn(guest.isDialIn());
        jod.setName(guest.getDisplayName());
        jod.setOwnerUgpId(0);
        if (dialType.equalsIgnoreCase("SIP")) {
            jod.setPreferredConnectionType(JSchedConnectionType.SIP);
            jod.setSipCapable(true);
            ecop.setExtention(guest.getExtensionSIP());
        } else if (dialType.equalsIgnoreCase("H323")) {
            jod.setPreferredConnectionType(JSchedConnectionType.H_323);
            jod.setH323Capable(true);
            ecop.setExtention(guest.getExtensionIP());
        } else if (dialType.equalsIgnoreCase("E164")) {
            jod.setPreferredConnectionType(JSchedConnectionType.H_323___E_164);
            jod.setH323Capable(true);
            ecop.setExtention(guest.getExtensionIP());
        } else if (dialType.equalsIgnoreCase("H323Annexo")) {
            jod.setPreferredConnectionType(JSchedConnectionType.H_323___ANNEX___O);
            jod.setH323Capable(true);
            ecop.setExtention(guest.getExtensionIP());
        } else if (dialType.equalsIgnoreCase("H323ID")) {
            jod.setPreferredConnectionType(JSchedConnectionType.H_323___ID);
            jod.setH323Capable(true);
            ecop.setExtention(guest.getExtensionIP());
        } else if (dialType.equalsIgnoreCase("ISDN")) {
            jod.setPreferredConnectionType(JSchedConnectionType.ISDN);
            jod.setIsdnCapable(true);
            jod.setIsdnFullNumber(number);
            ecop.setExtention(guest.getExtensionISDN());
        } else {
            jod.setPreferredConnectionType(JSchedConnectionType.H_323___E_164);
            ecop.setExtention(guest.getExtensionIP());
        }
        jod.setScheduledDevice(false);
        if (dialRate.isEmpty()) {
        	jod.setVideoSpeed(0);
        } else {
        	jod.setVideoSpeed(Integer.valueOf(dialRate).intValue());	
        }
        jod.setVip(false);
        if (!extention.isEmpty()) {
        	ecop.setExtention(extention);
        }
        jod.setDialString(number);
        jod.setDeviceType(jtvo);
        ecop.setInUseDevice(jod);
        if (!ongoingConf.getHostingDevices().isEmpty()) {
            ecop.setHostingDevice(ongoingConf.getHostingDevices().iterator()
                .next());
        }
        ecop.setNeedsHelp(false);
        ecop.setVideoMute(false);
        return ecop;
    }

    public String joinConference() {
        String status = "Failed";
        if (getInputCmd().get("conferenceName").isEmpty()
                || (getInputCmd().get("firstName").isEmpty()
                        && getInputCmd().get("lastName").isEmpty())) {
            status = "Failed";
            logger.error("There are some mandatory parameters missing in the input command. "
                    + "Please check your input command.");
            return status
                    + " There are some mandatory parameters missing in the input command. "
                    + "Please check your input command.";
        }
        // Get the active conference ID
        final String confID = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The conference ID is: " + confID);
        if (confID.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot save participant to favorite.");
            return status
                    + " Cannot find the conference ID using the given conference name.";
        }
        // Get id and guid of user
        final String searchString = inputCmd.get("firstName").isEmpty()
                ? inputCmd.get("firstName") : inputCmd.get("lastName");
        final List<JUser> users = umh.getUsers(userToken,
                                               searchString,
                                               "local");
        JUser targetUser = null;
        for (final JUser user : users) {
            if (searchString.equals(user.getFirstName())
                    || searchString.equals(user.getLastName())) {
                targetUser = user;
            }
        }
        if (targetUser == null) {
            logger.error("Cannot find the user " + searchString);
            return "Failed, Cannot find the user " + searchString;
        }
        final Integer userId = targetUser.getUgpId();
        final String guid = targetUser.getGUID();
        final JUser user = umh.findUser(userToken, userId.intValue(), guid);
        if (user == null) {
            logger.error("Cannot find the user " + searchString);
            return "Failed, Cannot find the user " + searchString;
        }
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confID);
        final List<ExternalCmaOngoingParticipant> partList = new ArrayList<ExternalCmaOngoingParticipant>();
        final JEndpointForDetails details = umh
                .findDeviceForUser(userToken, userId.intValue(), guid).get(0);
        partList.add(JUser2ExternalCmaOngoingParticipant(ongoingConfInfo,
                                                         user,
                                                         details));
        if (csh.addOngoingParticipantsToConference(userToken, confID, partList)
                .getStatus()
                .compareTo(com.polycom.webservices.ConferenceService.JStatus.SUCCESS) == 0) {
            status = "SUCCESS";
            logger.info(getInputCmd().get("firstName") + " "
                    + getInputCmd().get("lastName")
                    + " join the conference successfully");
        } else {
            status = "Failed";
            logger.error(getInputCmd().get("firstName") + " "
                    + getInputCmd().get("lastName")
                    + " join the conference failed");
            return status + getInputCmd().get("firstName") + " "
                    + getInputCmd().get("lastName")
                    + " join the conference failed";
        }
        return status;
    }

    /**
     * Internal method, convert JSchedPart to ExternalCmaOngoingParticipant
     *
     * @param ongoingConf
     *            The conference
     * @param schedPart
     *            The JSchedPart to convert
     * @param dmaID
     *            The dmaId
     * @param dmaConfID
     *            The dma conference id
     * @param dialstring
     *            The dial string
     * @param dialType
     *            The dial type
     * @param jtvo
     *            JDeviceTypeVO
     * @param mode
     *            audio or video
     * @return ExternalCmaOngoingParticipant
     */
    private ExternalCmaOngoingParticipant
            JSchedPart2ExternalCmaOngoingParticipant(final ExternalCmaOngoingConference ongoingConf,
                                                     final com.polycom.webservices.SchedulerEngine.JSchedPart schedPart,
                                                     final String dmaID,
                                                     final String dmaConfID,
                                                     final String dialstring,
                                                     final String dialType,
                                                     final com.polycom.webservices.ConferenceService.JDeviceTypeVO jtvo,
                                                     final String mode,
                                                     final String direction,
                                                     final String dialRate,
                                                     final String encryption) {
        final ExternalCmaOngoingParticipant ecop = new ExternalCmaOngoingParticipant();
        ecop.setAllDevicesOutOfArea(schedPart.isAllDevicesOutOfArea());
        ecop.setBelongsToAreaUgpId(schedPart.getBelongsToAreaUgpId());
        if (direction.equals("dialout")) {
        	ecop.setCallDirection(JCallDirection.OUTGOING);
        } else if (direction.equals("dialin")) {
        	ecop.setCallDirection(JCallDirection.INCOMING);
        }  
        ecop.setCascadeLink(schedPart.isCascadeLink());
        ecop.setChairperson(schedPart.isChairperson());
        ecop.setConfGuestId(schedPart.getConfGuestId());
        ecop.setDeviceEntityId_Dont_Use(schedPart.getDeviceEntityId_Dont_Use());
        ecop.setEmail(schedPart.getEmail());
        if (encryption.equalsIgnoreCase("yes")) {
        	ecop.setEncryption(true);
        	ecop.setEncryptionAuto(false);
        } else if (encryption.equalsIgnoreCase("auto")) {
        	ecop.setEncryption(true);
        	ecop.setEncryptionAuto(true);
        } else if (encryption.equalsIgnoreCase("no")) {
        	ecop.setEncryption(false);
        	ecop.setEncryptionAuto(false);
        } else {
        	ecop.setEncryption(false);
        	ecop.setEncryptionAuto(true);
        }

        ecop.setGroup(schedPart.isGroup());
        ecop.setGuest(schedPart.isGuest());
        ecop.setITPMaster(schedPart.isITPMaster());
        ecop.setLastName(schedPart.getLastName());
        ecop.setFirstName(schedPart.getFirstName());
        ecop.setLecturer(schedPart.isLecturer());
        if (!ongoingConf.getHostingDevices().isEmpty() && ongoingConf.getHostingDevices().iterator().next()
                .getMcuId() != null) {
            ecop.setMcuId(ongoingConf.getHostingDevices().iterator().next()
                    .getMcuId());
        }
        if (mode.equalsIgnoreCase("video")) {
            ecop.setMode(JSchedPartMode.VIDEO);
        } else if (mode.equalsIgnoreCase("audio")) {
            ecop.setMode(JSchedPartMode.AUDIO);
        } else {
            ecop.setMode(JSchedPartMode.VIDEO);
        }
        ecop.setOperator(schedPart.isOperator());
        ecop.setOwner(schedPart.isOwner());
        ecop.setOwnerUGPId(schedPart.getOwnerUGPId());
        ecop.setParentDeviceId(schedPart.getParentDeviceId());
        ecop.setRoom(schedPart.isRoom());
        ecop.setServiceName("##ANYMCUSERVICE##");
        ecop.setVip(schedPart.isVip());
        ecop.setVirtualParticipant(schedPart.isVirtualParticipant());
        ecop.setAudioMcuMute(false);
        ecop.setAudioMute(false);
        ecop.setAudioSelfMute(false);
        ecop.setBridgePartId(0);
        ecop.setCdrCreated(false);
        ecop.setDmaConfId(dmaConfID);
        ecop.setDmaId(dmaID);
        final JOngoingDevice jod = new JOngoingDevice();
        final List<com.polycom.webservices.SchedulerEngine.JSchedDevice> schedDeviceCollection = schedPart
                .getSchedDeviceCollection();
        if (schedDeviceCollection.size() != 0) {
            for (final com.polycom.webservices.SchedulerEngine.JDeviceAlias schda : schedDeviceCollection
                    .iterator().next().getAliasList()) {
                final com.polycom.webservices.ConferenceService.JDeviceAlias confda = new com.polycom.webservices.ConferenceService.JDeviceAlias();
                confda.setDbKey(schda.getDbKey());
                confda.setDeviceEntityId(schda.getDeviceEntityId());
                confda.setValue(schda.getValue());
                if (schda.getType()
                        .compareTo(com.polycom.webservices.SchedulerEngine.JDeviceAliasType.E_164) == 0) {
                    confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.E_164);
                } else if (schda.getType()
                        .compareTo(com.polycom.webservices.SchedulerEngine.JDeviceAliasType.H_323) == 0) {
                    confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.H_323);
                } else if (schda.getType()
                        .compareTo(com.polycom.webservices.SchedulerEngine.JDeviceAliasType.URL) == 0) {
                    confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.URL);
                } else if (schda.getType()
                        .compareTo(com.polycom.webservices.SchedulerEngine.JDeviceAliasType.URL) == 0) {
                    confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.URL);
                } else if (schda.getType()
                        .compareTo(com.polycom.webservices.SchedulerEngine.JDeviceAliasType.PARTY_NUMBER) == 0) {
                    confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.PARTY_NUMBER);
                } else if (schda.getType()
                        .compareTo(com.polycom.webservices.SchedulerEngine.JDeviceAliasType.TRANSPORT_ADDRESS) == 0) {
                    confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.TRANSPORT_ADDRESS);
                } else if (schda.getType()
                        .compareTo(com.polycom.webservices.SchedulerEngine.JDeviceAliasType.E_MAIL) == 0) {
                    confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.E_MAIL);
                } else if (schda.getType()
                        .compareTo(com.polycom.webservices.SchedulerEngine.JDeviceAliasType.UNKNOWN) == 0) {
                    confda.setType(com.polycom.webservices.ConferenceService.JDeviceAliasType.UNKNOWN);
                }
                jod.getAliasList().add(confda);
            }
        }
        if (schedDeviceCollection.size() != 0) {
            jod.setBehindFirewall(schedDeviceCollection.iterator().next()
                    .isBehindFirewall());
        } else {
            jod.setBehindFirewall(false);
        }
        jod.setBelongsToAreaUgpId(schedDeviceCollection.iterator().next()
                .getBelongsToAreaUgpId());
        jod.setDeviceId(schedDeviceCollection.iterator().next().getDeviceId());
        jod.setDeviceStatus(JDeviceStatus.ONLINE);
        jod.setDialIn(schedDeviceCollection.iterator().next().isDialIn());
        jod.setExternalId(schedDeviceCollection.iterator().next()
                .getExternalId());
        jod.setH323Capable(schedDeviceCollection.iterator().next()
                .isH323Capable());
        jod.setIpAddress(schedDeviceCollection.iterator().next()
                .getIpAddress());
        jod.setIpExtension(schedDeviceCollection.iterator().next()
                .getIpExtension());
        jod.setIsdnAreaCode(schedDeviceCollection.iterator().next()
                .getIsdnAreaCode());
        jod.setIsdnCapable(schedDeviceCollection.iterator().next()
                .isSipCapable());
        jod.setIsdnCountryCode(schedDeviceCollection.iterator().next()
                .getIsdnCountryCode());
        jod.setIsdnExtension(schedDeviceCollection.iterator().next()
                .getIsdnExtension());
        jod.setIsdnFullNumber(schedDeviceCollection.iterator().next()
                .getIsdnFullNumber());
        jod.setIsdnPhoneNumber(schedDeviceCollection.iterator().next()
                .getIsdnPhoneNumber());
        jod.setName(schedDeviceCollection.iterator().next().getName());
        jod.setOwnerUgpId(schedDeviceCollection.iterator().next()
                .getOwnerUgpId());
        if (dialType.equalsIgnoreCase("SIP")) {
            jod.setPreferredConnectionType(JSchedConnectionType.SIP);
        } else if (dialType.equalsIgnoreCase("H323")) {
            jod.setPreferredConnectionType(JSchedConnectionType.H_323);
        } else if (dialType.equalsIgnoreCase("E164")) {
            jod.setPreferredConnectionType(JSchedConnectionType.H_323___E_164);
        } else if (dialType.equalsIgnoreCase("H323Annexo")) {
            jod.setPreferredConnectionType(JSchedConnectionType.H_323___ANNEX___O);
        } else if (dialType.equalsIgnoreCase("H323ID")) {
            jod.setPreferredConnectionType(JSchedConnectionType.H_323___ID);
        } else if (dialType.equalsIgnoreCase("ISDN")) {
            jod.setPreferredConnectionType(JSchedConnectionType.ISDN);
        } else {
            jod.setPreferredConnectionType(JSchedConnectionType.H_323___E_164);
        }
        jod.setScheduledDevice(schedDeviceCollection.iterator().next()
                .isScheduledDevice());
        jod.setSipCapable(schedDeviceCollection.iterator().next()
                .isSipCapable());
        jod.setSipUri(schedDeviceCollection.iterator().next().getSipUri());
        jod.setSiteLatitude(schedDeviceCollection.iterator().next()
                .getSiteLatitude());
        jod.setSiteLongitude(schedDeviceCollection.iterator().next()
                .getSiteLongitude());
        jod.setSiteName(schedDeviceCollection.iterator().next().getSiteName());
        jod.setSiteUid(schedDeviceCollection.iterator().next().getSiteUid());
        if (dialRate.isEmpty()) {
        	jod.setVideoSpeed(schedDeviceCollection.iterator().next()
                    .getVideoSpeed());
        } else {
        	jod.setVideoSpeed(Integer.valueOf(dialRate).intValue());	
        }
        
        jod.setVip(schedDeviceCollection.iterator().next().isVip());
        jod.setDialString(dialstring);
        jod.setDeviceType(jtvo);
        ecop.setInUseDevice(jod);
        if (ongoingConf.getHostingDevices() != null && ongoingConf.getHostingDevices().size() > 0) {
            ecop.setHostingDevice(ongoingConf.getHostingDevices().get(0));
        }
        ecop.setNeedsHelp(false);
        ecop.setVideoMute(false);
        return ecop;
    }

    /**
     * Internal method, convert JUser and details to
     * ExternalCmaOngoingParticipant
     *
     * @param ongoingConf
     *            The conference
     * @param user
     *            The JUser to convert
     * @param details
     *            The JEndpointForDetails
     * @return ExternalCmaOngoingParticipant
     */
    private ExternalCmaOngoingParticipant
            JUser2ExternalCmaOngoingParticipant(final ExternalCmaOngoingConference ongoingConf,
                                                final JUser user,
                                                final JEndpointForDetails details) {
        final ExternalCmaOngoingParticipant ecop = new ExternalCmaOngoingParticipant();
        ecop.setAllDevicesOutOfArea(false);
        ecop.setBelongsToAreaUgpId(user.getBelongsToAreaUgpId());
        ecop.setCallDirection(JCallDirection.OUTGOING);
        ecop.setCascadeLink(false);
        ecop.setChairperson(false);
        ecop.setConfGuestId(0);
        ecop.setDeviceEntityId_Dont_Use(0);
        ecop.setEmail("");
        ecop.setEncryption(false);
        ecop.setEncryptionAuto(false);
        ecop.setGroup(false);
        ecop.setGuest(false);
        ecop.setITPMaster(false);
        ecop.setLastName(user.getLastName());
        ecop.setFirstName(user.getFirstName());
        ecop.setLecturer(false);
        if (!ongoingConf.getHostingDevices().isEmpty() && ongoingConf
                .getHostingDevices().iterator().next().getMcuId() != null) {
            ecop.setMcuId(ongoingConf.getHostingDevices().iterator().next()
                    .getMcuId());
        }
        ecop.setMode(JSchedPartMode.VIDEO);
        ecop.setOperator(true);
        ecop.setOwner(false);
        ecop.setOwnerUGPId(user.getUgpId());
        ecop.setParentDeviceId(0);
        ecop.setRoom(user.isRoom());
        ecop.setServiceName("##ANYMCUSERVICE##");
        ecop.setVip(details.isVip());
        ecop.setVirtualParticipant(false);
        ecop.setAudioMcuMute(false);
        ecop.setAudioMute(false);
        ecop.setAudioSelfMute(false);
        ecop.setBridgePartId(0);
        ecop.setCdrCreated(false);
        ecop.setDmaConfId(ongoingConf.getDmaConfId());
        ecop.setDmaId(ongoingConf.getDmaId());
        final JOngoingDevice jod = new JOngoingDevice();
        jod.setBehindFirewall(false);
        jod.setBelongsToAreaUgpId(user.getBelongsToAreaUgpId());
        jod.setDeviceId(details.getDeviceId());
        jod.setDeviceStatus(JDeviceStatus
                .fromValue(details.getDeviceStatus().toString()));
        jod.setDialIn(false);
        jod.setExternalId(details.getExternalGuid());
        jod.setH323Capable(details.isH323Supported());
        jod.setIpAddress(details.getIpAddress());
        jod.setIpExtension("");
        jod.setIsdnAreaCode(details.getIsdnAreaCode());
        jod.setIsdnCapable(details.isIsdnSupported());
        jod.setIsdnCountryCode(details.getIsdnCountryCode());
        jod.setIsdnExtension(details.getIsdnExtension());
        jod.setIsdnFullNumber(details.getIsdnPhoneNumber());
        jod.setIsdnPhoneNumber(details.getIsdnPhoneNumber());
        jod.setName(details.getDeviceName());
        jod.setOwnerUgpId(user.getUgpId());
        jod.setPreferredConnectionType(JSchedConnectionType.H_323);
        jod.setScheduledDevice(false);
        jod.setSipCapable(false);
        jod.setSipUri("");
        jod.setSiteLatitude(details.getSiteLatitude());
        jod.setSiteLongitude(details.getSiteLongitude());
        jod.setSiteName(details.getSite());
        jod.setSiteUid(details.getSiteUid());
        jod.setVideoSpeed(0);
        jod.setVip(details.isVip());
        jod.setDialString(details.getIpAddress());
        final com.polycom.webservices.ConferenceService.JDeviceTypeVO deviceType = new com.polycom.webservices.ConferenceService.JDeviceTypeVO();
        try {
            CommonUtils.copyProperties(details.getDeviceType(), deviceType);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        jod.setDeviceType(deviceType);
        ecop.setInUseDevice(jod);
        ecop.setNeedsHelp(false);
        ecop.setVideoMute(true);
        return ecop;
    }

    /**
     * Manage the conference
     *
     * @see conferenceName=$confName
     *
     * @param conferenceName
     *            The conference name
     * @return The result
     */
    public String manageConference() {
        String status = "Failed";
        // Get the active conference ID
        final String conferenceName = getInputCmd().get("conferenceName")
                .replace("~", " ");
        final String confId = getActiveConfId(conferenceName);
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the conference detail information.");
            return status;
        }
        try {
            final JWebResult result = csh.startWatchingConference(userToken,
                                                                  confId);
            if (result.getStatus().equals(JStatus.SUCCESS)) {
                status = "SUCCESS";
                logger.info("Successfully manage the conference "
                        + conferenceName);
            } else {
                status = "Failed";
                logger.error("Failed to manage the conference"
                        + conferenceName);
                return status + " Failed to manage the conference"
                        + conferenceName;
            }
        } catch (final Exception e) {
            e.printStackTrace();
            status = "Failed, got exception when start watching conference. Error msg is "
                    + e.getMessage();
        }
        return status;
    }

    /**
     * Remove participant from ongoing conference
     *
     * @see conferenceName=$confName <br/>
     *      connectionState=true <br/>
     *      confType=AdhocDMAConf <br/>
     *      firstName=debuguser3 <br/>
     *      lastName=ccc
     *
     * @param conferenceName
     *            The conference
     * @param confType
     *            The conference type
     * @param firstName
     *            The first name of participant
     * @param lastName
     *            The last name of participant
     * @return The result
     */
    public String removeParitipantFromOngoingConference() {
        String status = "Failed";
        String partId = "";
        // Get the active conference ID
        final String confId = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot remove participant from conference.");
            return status;
        }
        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        if (getInputCmd().get("lastName").isEmpty()
                && getInputCmd().get("firstName").isEmpty()) {
            status = "Failed";
            logger.error("Some necessary parameters are missing in the remove participant from the conference command.");
            return status
                    + " Some necessary parameters are missing in the remove participant from the conference command.";
        }
        for (final ExternalCmaOngoingParticipant participant : ongoingConfInfo
                .getParticipants()) {
            if ((getInputCmd().get("firstName").isEmpty())
                    ? (getInputCmd().get("lastName")
                            .equals(participant.getLastName()))
                    : ((getInputCmd().get("lastName").isEmpty())
                            ? getInputCmd().get("firstName")
                                    .equals(participant.getFirstName())
                            : (getInputCmd().get("firstName")
                                    .equals(participant.getFirstName())
                                    && getInputCmd().get("lastName")
                                            .equals(participant
                                                    .getLastName())))) {
                partId = participant.getConfSvcPartId();
            } else {
                if (!getInputCmd().get("partDialString").isEmpty()
                        && getInputCmd().get("partDialString")
                                .equals(participant.getDialNumber())) {
                    partId = participant.getConfSvcPartId();
                }
            }
        }
        if (partId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the specified participant in the on going conference. "
                    + "Due to this problem cannot remove participant from the conference.");
            return status
                    + " Cannot find the specified participant in the on going conference. "
                    + "Due to this problem cannot remove participant from the conference.";
        }
        try {
            if (csh.removeParticipantFromConference(userToken, confId, partId)
                    .getStatus().equals(JStatus.SUCCESS)) {
                status = "SUCCESS";
                logger.info("Successfully remove participant from the conference "
                        + getInputCmd().get("firstName") + " "
                        + getInputCmd().get("lastName"));
            } else {
                status = "Failed";
                logger.error("Failed to remove participant from the conference "
                        + getInputCmd().get("firstName") + " "
                        + getInputCmd().get("lastName"));
                return status
                        + " Failed to remove participant from the conference "
                        + getInputCmd().get("firstName") + " "
                        + getInputCmd().get("lastName");
            }
        } catch (final Exception e) {
            logger.error("Exception found with message " + e.getMessage());
            e.printStackTrace();
            return "Failed Exception found when trying to remove participant from the conference.";
        }
        return status;
    }

    /**
     * Save participant to favorite list
     *
     *
     * @param conferenceName
     *            The conference name
     * @param favoriteListId
     *            The favorite list ugpid
     * @param favoriteMembers
     *            Favorite members which need to add to conference
     * @param partDeviceIp
     *            Part device ip
     * @param conferenceType
     *            Conference type
     * @return The result
     */
    public String saveParticipantToFavoriteList() {
        String status = "Failed";
        final GroupManagerHandler gh = new GroupManagerHandler(webServiceUrl);
        if (getInputCmd().get("conferenceName").isEmpty()
                || getInputCmd().get("favoriteListId").isEmpty()
                || (getInputCmd().get("firstName").isEmpty()
                        && getInputCmd().get("lastName").isEmpty())) {
            status = "Failed";
            logger.error("There are some mandatory parameters missing in the input command. "
                    + "Please check your input command.");
            return status
                    + " There are some mandatory parameters missing in the input command. "
                    + "Please check your input command.";
        }
        // Get the active conference ID
        final String confID = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The conference ID is: " + confID);
        if (confID.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot save participant to favorite.");
            return status
                    + " Cannot find the conference ID using the given conference name.";
        }
        final JUsersAndGroups favoriteMembers = gh
                .getGroupMembers(userToken,
                                 "ugpId=" + getInputCmd().get("favoriteListId")
                                         + ",ou=local,ou=users,ou=readimanager");
        // Get participant user ID
        final List<ExternalCmaOngoingParticipant> participants = csh
                .getPagedParticipants(userToken, confID);
        int participantUgpId = 0;
        for (final ExternalCmaOngoingParticipant participant : participants) {
            if (inputCmd.get("firstName").isEmpty()
                    && !inputCmd.get("lastName").isEmpty()) {
                if (inputCmd.get("lastName")
                        .equalsIgnoreCase(participant.getLastName())) {
                    participantUgpId = participant.getOwnerUGPId();
                    break;
                }
            } else if (!inputCmd.get("firstName").isEmpty()
                    && inputCmd.get("lastName").isEmpty()) {
                if (inputCmd.get("firstName")
                        .equalsIgnoreCase(participant.getFirstName())) {
                    participantUgpId = participant.getOwnerUGPId();
                    break;
                }
            } else {
                if (inputCmd.get("firstName")
                        .equalsIgnoreCase(participant.getFirstName())
                        && inputCmd.get("lastName")
                                .equalsIgnoreCase(participant.getLastName())) {
                    participantUgpId = participant.getOwnerUGPId();
                    break;
                }
            }
        }
        if (participantUgpId == 0) {
            status = "Failed";
            logger.error("Cannot find participant " + inputCmd.get("firstName")
                    + inputCmd.get("lastName"));
            return status + " Cannot find participant "
                    + inputCmd.get("firstName") + inputCmd.get("lastName");
        }
        final com.polycom.webservices.GroupManager.JUser user = new com.polycom.webservices.GroupManager.JUser();
        user.setUgpId(Integer.valueOf(participantUgpId));
        user.setRoom(false);
        favoriteMembers.getUsers().add(user);
        if (gh.setFavoriteListMembers(userToken,
                                      Integer.valueOf(getInputCmd()
                                              .get("favoriteListId"))
                                              .intValue(),
                                      favoriteMembers)
                .getStatus()
                .equals(com.polycom.webservices.GroupManager.JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Save participant " + getInputCmd().get("firstName")
                    + getInputCmd().get("lastName")
                    + " to favorite list success");
        } else {
            status = "Failed";
            logger.error("Save participant " + getInputCmd().get("firstName")
                    + getInputCmd().get("lastName")
                    + " to favorite list failed");
            return status + " Save participant "
                    + getInputCmd().get("firstName")
                    + getInputCmd().get("lastName")
                    + " to favorite list failed";
        }
        return status;
    }

    /**
     * Set conference auto layout
     *
     * @see conferenceName=$confName
     *
     * @param conferenceName
     *            The conference name
     * @param confAutoLayout
     *            The conference is auto layout
     * @return The result
     */
    public String setConferenceAutoLayout() {
        String status = "Failed";
        // Get the active conference ID
        final String confId = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot set conference auto layout.");
            return status;
        }
        final Boolean isAutoLayout = Boolean
                .valueOf(getInputCmd().get("confAutoLayout"));
        if (csh.setConferenceAutoLayout(userToken, confId, isAutoLayout)
                .getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfully change the layout of conference "
                    + getInputCmd().get("conferenceName").replace("~", " ")
                    + " to " + isAutoLayout.toString());
        } else {
            status = "Failed";
            logger.error("Failed to set conference auto layout of conference "
                    + getInputCmd().get("conferenceName").replace("~", " ")
                    + " to " + isAutoLayout.toString());
            return status
                    + " Failed to set conference auto layout of conference "
                    + getInputCmd().get("conferenceName").replace("~", " ")
                    + " to " + isAutoLayout.toString();
        }
        return status;
    }

    /**
     * Internal method, set JSchedPart
     *
     * @param JSchedPart
     *            participant
     * @return void
     */
    private void setJSchedPartInfo(final JSchedPart schedPart,
                                   final String direction,
                                   final String mode,
                                   final String deviceIp,
                                   final String dialType,
                                   final String encryption,
                                   final String lineRate,
                                   final String confType) {
    	int defaultVideoSpeed = 0;
        final NetworkDeviceManagerHandler ndmh = new NetworkDeviceManagerHandler(
                webServiceUrl);
        if (direction.equals("dialout")) {
            schedPart.setCallDirection(JCallDirection.OUTGOING);
        } else if (direction.equals("dialin")) {
            schedPart.setCallDirection(JCallDirection.INCOMING);
        }
        schedPart.setServiceName("##ANYMCUSERVICE##");
        if (!confType.equalsIgnoreCase("anytime")
                && !confType.equalsIgnoreCase("poolConference")
                && !confType.equalsIgnoreCase("recurringPoolConf")) {
            ndmh.getAvailableBridges(userToken);
            schedPart.setMcuId(ndmh.getAvailableMCU().iterator().next()
                    .getDeviceUUID());
            schedPart.setDeviceEntityId_Dont_Use(ndmh.getAvailableMCU()
                    .iterator().next().getDeviceId());
            defaultVideoSpeed = 384;
        } else {
        	defaultVideoSpeed = -1;
        }
        for (int j = 0; j < schedPart.getSchedDeviceCollection().size(); j++) {
            if ((!dialType.equalsIgnoreCase("ISDN") && schedPart.getSchedDeviceCollection().get(j).getIpAddress()
                    .equals(deviceIp)) || (dialType.equalsIgnoreCase("ISDN") && schedPart.getSchedDeviceCollection().get(j).getIsdnFullNumber().contains(deviceIp))) {
                if (direction.equals("dialout")) {
                    schedPart.getSchedDeviceCollection().get(j)
                            .setDialIn(false);
                } else {
                    schedPart.getSchedDeviceCollection().get(j).setDialIn(true);
                }
                schedPart.getSchedDeviceCollection().get(j)
                        .setScheduledDevice(true);
                schedPart.getSchedDeviceCollection().get(j)
                        .setPreferredConnectionType(JSchedConnectionType
                                .fromValue(dialType.toUpperCase()));
                schedPart.getSchedDeviceCollection().get(j)
                        .setIsdnFullNumber(deviceIp);
                if (mode.equalsIgnoreCase("video")) {
                    schedPart.setMode(JSchedPartMode.VIDEO);
                    schedPart.setMediaType(JCMConferenceMediaType.AUDIO_VIDEO);
                } else if (mode.equalsIgnoreCase("audio")) {
                    schedPart.setMode(JSchedPartMode.AUDIO);
                    schedPart.setMediaType(JCMConferenceMediaType.AUDIO);
                } else {
                    schedPart.setMode(JSchedPartMode.VIDEO);
                    schedPart.setMediaType(JCMConferenceMediaType.AUDIO_VIDEO);
                }
                if (!lineRate.isEmpty()) {
                    // schedPart.getSchedDeviceCollection().get(j).setVideoSpeed(Integer.parseInt(inputCmd.get("partLineRate")));
                    schedPart.getSchedDeviceCollection().get(j)
                            .setVideoSpeed(Integer.valueOf(lineRate)
                                    .intValue());
                } else {
                	schedPart.getSchedDeviceCollection().get(j)
                    .setVideoSpeed(defaultVideoSpeed);
                }
                break;
            }
        }
        if (encryption.equalsIgnoreCase("yes")) {
            schedPart.setEncryption(true);
            schedPart.setEncryptionAuto(false);
        } else if (encryption.equalsIgnoreCase("auto")) {
            schedPart.setEncryption(false);
            schedPart.setEncryptionAuto(true);
        } else if (encryption.equalsIgnoreCase("no")) {
            schedPart.setEncryption(false);
            schedPart.setEncryptionAuto(false);
        } else {
            schedPart.setEncryption(false);
            schedPart.setEncryptionAuto(true);
        }
    }

    /**
     * Set participant audio mute state
     *
     * @see conferenceName=$confName <br/>
     *      connectionState=true <br/>
     *      confType=AdhocDMAConf <br/>
     *      firstName=debuguser3 <br/>
     *      lastName=ccc
     *
     * @param conferenceName
     *            The conference
     * @param connectionState
     *            The mute state to set
     * @param firstName
     *            The first name of participant
     * @param lastName
     *            The last name of participant
     * @return The result
     */
    public String setParticipantAudioMute() {
        String status = "Failed";
        String partId = "";
        boolean connectionFlag = false;
        final String conferenceName = getInputCmd().get("conferenceName")
                .replace("~", " ");
        // Get the active conference ID
        final String confId = getActiveConfId(conferenceName);
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the conference detail information.");
            return status;
        }
        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        final String connectionState = getInputCmd().get("connectionState");
        final String lastName = getInputCmd().get("lastName");
        final String firstName = getInputCmd().get("firstName");
        final String dialString = getInputCmd().get("partDialString");
        if (connectionState.isEmpty()) {
            return "Faild, could not find connectionState";
        }
        connectionFlag = Boolean.parseBoolean(connectionState);
        final ExternalCmaOngoingParticipant targetParticipant = getParticipant(ongoingConfInfo,
                                                                               firstName,
                                                                               lastName,
                                                                               dialString);
        if (targetParticipant == null) {
            return "Failed, could not find participant " + firstName + " "
                    + lastName + " in conference";
        }
        partId = targetParticipant.getConfSvcPartId();
        try {
            final JWebResult result = csh
                    .setParticipantAudioMute(userToken,
                                             confId,
                                             partId,
                                             connectionFlag);
            if (result.getStatus().equals(JStatus.SUCCESS)) {
                status = "SUCCESS";
                logger.info("Successfully set the audio status on participant "
                        + firstName + " " + lastName);
                return status;
            } else {
                status = "Failed";
                logger.error("Failed to set the audio status on participant "
                        + firstName + " " + lastName);
                return status
                        + " Failed to set the audio status on participant "
                        + firstName + " " + lastName;
            }
        } catch (final Exception e) {
            logger.error("Exception found with message " + e.getMessage());
            e.printStackTrace();
            return "Failed Exception found when trying to set audio mute for paticipant.";
        }
    }

    /**
     * Set participant connection status
     *
     * @see conferenceName=$confName <br/>
     *      connectionState=true <br/>
     *      confType=AdhocDMAConf <br/>
     *      firstName=debuguser3 <br/>
     *      lastName=ccc
     *
     * @param conferenceName
     *            The conference
     * @param connectionState
     *            The connection status to set
     * @param firstName
     *            The first name of participant
     * @param lastName
     *            The last name of participant
     * @return The result
     */
    public String setParticipantConnectionStatus() {
        String partId = "";
        boolean connectionFlag = false;
        final String conferenceName = getInputCmd().get("conferenceName")
                .replace("~", " ");
        // Get the active conference ID
        final String confId = getActiveConfId(conferenceName);
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot set participant connection status.");
            return "Failed, could not find the conference " + conferenceName;
        }
        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        final String connectionState = getInputCmd().get("connectionState");
        if (connectionState.isEmpty()) {
            return "Failed, the param connectionState is required";
        }
        connectionFlag = Boolean.parseBoolean(connectionState);
        final String confType = getInputCmd().get("confType");
        if (confType.isEmpty()) {
            return "Failed, the param confType is required";
        }
        final String firstName = getInputCmd().get("firstName");
        final String lastName = getInputCmd().get("lastName");
        final String fullName = getFullName(firstName, lastName);
        final String dialString = getInputCmd().get("partDialString");

        if (confType.equalsIgnoreCase("AdhocDMAConf")
                || confType.equalsIgnoreCase("AdhocMCUConf")
                || confType.equalsIgnoreCase("PoolConf")) {
            for (final ExternalCmaOngoingParticipant participant : ongoingConfInfo
                    .getParticipants()) {
                final String currentParticipantFullName = getFullName(participant
                        .getFirstName(), participant.getLastName());
                if (currentParticipantFullName.equals(fullName)) {
                    partId = participant.getConfSvcPartId();
                    break;
                } else if (! dialString.isEmpty() && dialString.equals(participant.getDialNumber())) {
                	partId = participant.getConfSvcPartId();
                    break;
                }
            }
        }
        if (confType.equalsIgnoreCase("DirectMCUConf")) {
            for (final ExternalCmaOngoingParticipant participant : ongoingConfInfo
                    .getParticipants()) {
                final String currentParticipantFullName = getFullName(participant
                        .getFirstName(), participant.getLastName());
                if (currentParticipantFullName.equals(fullName)) {
                    partId = participant.getSchedPartId();
                    break;
                } else if (! dialString.isEmpty() && dialString.equals(participant.getDialNumber())) {
                	partId = participant.getSchedPartId();
                    break;
                }
            }
        }
        if (partId.isEmpty()) {
            logger.error("Cannot find the specified participant in the on going conference. "
                    + "Due to this problem cannot set connection status for it.");
            return "Failed."
                    + " Cannot find the specified participant in the on going conference. "
                    + "Due to this problem cannot set connection status for it.";
        }
        try {
            final JWebResult result = csh
                    .setParticipantConnectStatus(userToken,
                                                 confId,
                                                 partId,
                                                 connectionFlag);
            if (result.getStatus().equals(JStatus.SUCCESS)) {
                logger.info("Successfully set the connection status on participant "
                        + firstName + " " + lastName);
                return "SUCCESS";
            } else {
                logger.error("Failed to set the connection status on participant "
                        + firstName + " " + lastName);
                return "Failed,"
                        + " Failed to set the connection status on participant "
                        + firstName + " " + lastName + "\n"
                        + result.getMessages();
            }
        } catch (final Exception e) {
            logger.error("Exception found with message " + e.getMessage());
            e.printStackTrace();
            return "Failed\n " + CommonUtils.getExceptionStackTrace(e);
        }
    }

    /**
     * Set participant video mute state
     *
     * @see conferenceName=$confName <br/>
     *      connectionState=true <br/>
     *      confType=AdhocDMAConf <br/>
     *      firstName=debuguser3<br/>
     *      lastName=ccc
     *
     * @param conferenceName
     *            The conference
     * @param connectionState
     *            The mute state to set
     * @param firstName
     *            The first name of participant
     * @param lastName
     *            The last name of participant
     * @return The result
     */
    public String setParticipantVideoMute() {
        String status = "Failed";
        String partId = "";
        boolean connectionFlag = false;
        final String conferenceName = getInputCmd().get("conferenceName")
                .replace("~", " ");
        // Get the active conference ID
        final String confId = getActiveConfId(conferenceName);
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the conference detail information.");
            return status;
        }
        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        final String connectionState = getInputCmd().get("connectionState");
        final String lastName = getInputCmd().get("lastName");
        final String firstName = getInputCmd().get("firstName");
        final String dialString = getInputCmd().get("partDialString");
        final String expectedFullName = firstName + lastName;
        if (connectionState.isEmpty()) {
            return "Faild, could not find connectionState";
        }

        connectionFlag = Boolean.parseBoolean(connectionState);
        final ExternalCmaOngoingParticipant targetParticipant = getParticipant(ongoingConfInfo,
                                                                               firstName,
                                                                               lastName,
                                                                               dialString);
        if (targetParticipant == null) {
            return "Failed, could not find participant " + expectedFullName
                    + " in conference";
        }
        partId = targetParticipant.getConfSvcPartId();
        try {
            final JWebResult result = csh
                    .setParticipantVideoMute(userToken,
                                             confId,
                                             partId,
                                             connectionFlag);
            if (result.getStatus().equals(JStatus.SUCCESS)) {
                status = "SUCCESS";
                logger.info("Successfully set the video status on participant "
                        + expectedFullName);
                return status;
            } else {
                status = "Failed";
                logger.error("Failed to set the video status on participant "
                        + firstName + " " + lastName);
                return status
                        + " Failed to set the video status on participant "
                        + expectedFullName;
            }
        } catch (final Exception e) {
            logger.error("Exception found with message " + e.getMessage());
            e.printStackTrace();
            return "Failed Exception found when trying to set video bock for paticipant.";
        }
    }

    /**
     * Terminate the conference
     *
     * @see conferenceName=$confName
     *
     * @param conferenceName
     *            The conference name
     * @return The result
     */
    public String terminateConference() {
        String status = "Failed";
        // Get the active conference ID
        final String conferenceName = getInputCmd().get("conferenceName")
                .replace("~", " ");
        final String confId = getActiveConfId(conferenceName);
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the conference detail information.");
            return status;
        }
        if (csh.terminateConference(userToken, confId).getStatus()
                .equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfully terminate the conference "
                    + conferenceName);
        } else {
            status = "Failed";
            logger.error("Failed to terminate the conference "
                    + conferenceName);
            return status + " Failed to terminate the conference "
                    + conferenceName;
        }
        return status;
    }

    /**
     * Unmanage the conference
     *
     * @see conferenceName=$confName
     *
     * @param conferenceName
     *            The conference name
     * @return The result
     */
    public String unmanageConference() {
        String status = "Failed";
        // Get the active conference ID
        final String confId = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the conference detail information.");
            return status;
        }
        if (csh.stopWatchingConference(userToken, confId).getStatus()
                .equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfully unmanage the conference "
                    + getInputCmd().get("conferenceName").replace("~", " "));
        } else {
            status = "Failed";
            logger.error("Failed to unmanage the conference"
                    + getInputCmd().get("conferenceName").replace("~", " "));
            return status + " Failed to unmanage the conference"
                    + getInputCmd().get("conferenceName").replace("~", " ");
        }
        return status;
    }

    /**
     * validate the participant info in conference
     *
     * @see conferenceName=$confName <br/>
     *      participant1=lroom <br/>
     *      part1Status=connected <br/>
     *      part1MediaType=AudioVideo <br/>
     *      part1EndpointName=lroomGroupSeries <br/>
     *      part1AccessMode=SIP <br/>
     *      part1DialNum=$confName@DMA-api.sqa.org <br/>
     *      part1DialMode=Dial-Out <br/>
     *      part1BridgeName=Factory~Pool~Order
     *
     * @param conferenceName
     *            The conference name
     * @param part1Status
     *            The expected participant status(Optional)
     * @param part1MediaType
     *            The expected participant media type(Optional)
     * @param part1EndpointName
     *            The expected participant endpoint name(Optional)
     * @param part1AccessMode
     *            The expected participant access mode(Optional)
     * @param part1DialNum
     *            The expected participant dial number(Optional)
     * @param part1DialMode
     *            The expected participant dial mode(Optional)
     * @param part1BridgeName
     *            The expected participant bridge name(Optional)
     * @return The result
     */
    public String validateParticipantInfoInConference() {
        String status = "Failed";
        // Get the active conference ID
        final String confId = getActiveConfId(getInputCmd()
                .get("conferenceName").replace("~", " "));
        logger.info("The conference ID is: " + confId);
        if (confId.isEmpty()) {
            status = "Failed";
            logger.error("Cannot find the conference ID using the given conference name."
                    + "Due to this problem, we cannot validate the participants detail information.");
            return status;
        }
        // Get the on going conference information
        final ExternalCmaOngoingConference ongoingConfInfo = csh
                .getOngoingConference(userToken, confId);
        // Check the participant detail information
        ExternalCmaOngoingParticipant checkedParticipant = null;
        final List<ExternalCmaOngoingParticipant> partList = new LinkedList<ExternalCmaOngoingParticipant>();
        for (final ExternalCmaOngoingParticipant participant : ongoingConfInfo
                .getParticipants()) {
        	if (participant.getSchedPartId() == null) {
        		continue;
        	}
            String participantName = "";
            String firstName = participant.getFirstName();
            String lastName = participant.getLastName();
            if (firstName == null) {
                firstName = "";
            }
            if (lastName == null) {
                lastName = "";
            }
            participantName = firstName + lastName;
            logger.info("Checking participant " + participantName);
            // Add below part for temp guest name synced from DMA sometimes
            if (getInputCmd().get("participant1").contains("|")) {
                final String[] Names = getInputCmd().get("participant1")
                        .split("|");
                for (final String name : Names) {
                    if (participantName.startsWith(name.replaceAll("~", " "))) {
                        checkedParticipant = participant;
                        partList.add(checkedParticipant);
                        break;
                    }
                }
            } else {
                final String participant1 = getInputCmd().get("participant1")
                        .replaceAll("~", " ");
                if (participantName.startsWith(participant1)) {
                    checkedParticipant = participant;
                    partList.add(checkedParticipant);
                }
                // Add below part for temp guest name synced from DMA sometimes
                if (partList.size() == 0 && !getInputCmd().get("part1DialNum").isEmpty()) {
                	if (getInputCmd().get("part1DialNum").equals(participant.getDialNumber())) {
                		checkedParticipant = participant;
                		partList.add(checkedParticipant);
                	}
                }
            }
        }
        // If can not find participant, return fail directly.
        if (partList.size() == 0) {
            logger.error("Can not find participant "
                    + getInputCmd().get("participant1"));
            return status + ", can't find participant "
                    + getInputCmd().get("participant1");
        }
        // If participant find, start check participant detail information.
        // Check the participant connection status
        if (partList.size() > 1) {
            for (final ExternalCmaOngoingParticipant part : partList) {
                if (getInputCmd().get("part1EndpointName").replaceAll("~", " ")
                        .equals(part.getInUseDevice().getName())) {
                    checkedParticipant = part;
                }
            }
        }
        final ParticipantStatus actualStatus = checkedParticipant.getStatus();
        if (getInputCmd().get("part1Status").equalsIgnoreCase("connected")
                && (actualStatus.equals(ParticipantStatus.CONNECTED)
                        || actualStatus
                                .equals(ParticipantStatus.CONNECTED_WARNING)
                        || actualStatus
                                .equals(ParticipantStatus.CONNECTED_ERRORS))) {
            status = "SUCCESS";
            logger.info("Participant " + getInputCmd().get("participant1")
                    + " connection status is as expected "
                    + getInputCmd().get("part1Status"));
        } else if (getInputCmd().get("part1Status")
                .equalsIgnoreCase("disconnected")
                && actualStatus.equals(ParticipantStatus.DISCONNECTED)) {
            status = "SUCCESS";
            logger.info("Participant " + getInputCmd().get("participant1")
                    + " connection status is as expected "
                    + getInputCmd().get("part1Status"));
        } else if (getInputCmd().get("part1Status").equalsIgnoreCase("unknown")
                && actualStatus.equals(ParticipantStatus.UNKNOWN)) {
            status = "SUCCESS";
            logger.info("Participant " + getInputCmd().get("participant1")
                    + " connection status is as expected "
                    + getInputCmd().get("part1Status"));
        } else {
            status = "Failed";
            logger.error("Participant " + getInputCmd().get("participant1")
                    + " connection status is not as expected "
                    + getInputCmd().get("part1Status") + ", but was "
                    + actualStatus);
            return status + " Participant " + getInputCmd().get("participant1")
                    + " connection status is not as expected "
                    + getInputCmd().get("part1Status") + ", but was "
                    + actualStatus;
        }
        // Check the participant dial mode part1DialMode
        if (!getInputCmd().get("part1DialMode").isEmpty()) {
            if (getInputCmd().get("part1DialMode").equalsIgnoreCase("Dial-In")
                    && checkedParticipant.getCallDirection()
                            .compareTo(JCallDirection.INCOMING) == 0) {
                status = "SUCCESS";
                logger.info("The participant "
                        + getInputCmd().get("participant1")
                        + " dial mode is as expected "
                        + getInputCmd().get("part1DialMode"));
            } else if (getInputCmd().get("part1DialMode")
                    .equalsIgnoreCase("Dial-Out")
                    && checkedParticipant.getCallDirection()
                            .compareTo(JCallDirection.OUTGOING) == 0) {
                status = "SUCCESS";
                logger.info("The participant "
                        + getInputCmd().get("participant1")
                        + " dial mode is as expected "
                        + getInputCmd().get("part1DialMode"));
            } else {
                status = "Failed";
                logger.error("The participant "
                        + getInputCmd().get("participant1")
                        + " dial mode is not as expected "
                        + getInputCmd().get("part1DialMode"));
                return status + " The participant "
                        + getInputCmd().get("participant1")
                        + " dial mode is not as expected "
                        + getInputCmd().get("part1DialMode");
            }
        }
        // Check the participant participant type
        if (!getInputCmd().get("part1MediaType").isEmpty()) {
            if (getInputCmd().get("part1MediaType")
                    .equalsIgnoreCase("AudioVideo")
                    && checkedParticipant.getMediaType()
                            .compareTo(JCMConferenceMediaType.AUDIO_VIDEO) == 0) {
                status = "SUCCESS";
                logger.info("The participant "
                        + getInputCmd().get("participant1")
                        + " media type is as expected "
                        + getInputCmd().get("part1MediaType"));
            } else if (getInputCmd().get("part1MediaType")
                    .equalsIgnoreCase("Audio")
                    && checkedParticipant.getMediaType()
                            .compareTo(JCMConferenceMediaType.AUDIO) == 0) {
                status = "SUCCESS";
                logger.info("The participant "
                        + getInputCmd().get("participant1")
                        + " media type is as expected "
                        + getInputCmd().get("part1MediaType"));
            } else {
                status = "Failed";
                logger.error("The participant "
                        + getInputCmd().get("participant1")
                        + " media type is not as expected "
                        + getInputCmd().get("part1MediaType"));
                return status + " The participant "
                        + getInputCmd().get("participant1")
                        + " media type is not as expected "
                        + getInputCmd().get("part1MediaType");
            }
        }
        // Check the participant participant type
        if (!getInputCmd().get("part1ModeType").isEmpty()) {
            if (getInputCmd().get("part1ModeType").equalsIgnoreCase("Audio")
                    && checkedParticipant.getMode()
                            .compareTo(JSchedPartMode.AUDIO) == 0) {
                status = "SUCCESS";
                logger.info("The participant "
                        + getInputCmd().get("participant1")
                        + " media mode is as expected "
                        + getInputCmd().get("part1ModeType"));
            } else
                if (getInputCmd().get("part1ModeType").equalsIgnoreCase("Video")
                        && checkedParticipant.getMode()
                                .compareTo(JSchedPartMode.VIDEO) == 0) {
                status = "SUCCESS";
                logger.info("The participant "
                        + getInputCmd().get("participant1")
                        + " media mode is as expected "
                        + getInputCmd().get("part1ModeType"));
            } else if (getInputCmd().get("part1ModeType")
                    .equalsIgnoreCase("InPersion")
                    && checkedParticipant.getMode()
                            .compareTo(JSchedPartMode.IN___PERSON) == 0) {
                status = "SUCCESS";
                logger.info("The participant "
                        + getInputCmd().get("participant1")
                        + " media mode is as expected "
                        + getInputCmd().get("part1ModeType"));
            } else {
                status = "Failed";
                logger.error("The participant "
                        + getInputCmd().get("participant1")
                        + " media mode is not as expected "
                        + getInputCmd().get("part1ModeType"));
                return status + " The participant "
                        + getInputCmd().get("participant1")
                        + " media mode is not as expected "
                        + getInputCmd().get("part1ModeType");
            }
        }
        // Check the participant endpoint name
        if (!getInputCmd().get("part1EndpointName").isEmpty()) {
            if (getInputCmd().get("part1EndpointName").replaceAll("~", " ")
                    .equalsIgnoreCase(checkedParticipant.getInUseDevice()
                            .getName())) {
                status = "SUCCESS";
                logger.info("The participant "
                        + getInputCmd().get("participant1")
                        + " endpoint name is as expected "
                        + getInputCmd().get("part1EndpointName"));
            } else {
                status = "Failed";
                logger.error("The participant "
                        + getInputCmd().get("participant1")
                        + " endpoint name is not as expected "
                        + getInputCmd().get("part1EndpointName"));
                return status + " The participant "
                        + getInputCmd().get("participant1")
                        + " endpoint name is not as expected "
                        + getInputCmd().get("part1EndpointName") + " ,it's "
                        + checkedParticipant.getInUseDevice().getName();
            }
        }
        // Check the participant access mode
        if (!getInputCmd().get("part1AccessMode").isEmpty()) {
            try {
                final String expectAccessMode = getInputCmd()
                        .get("part1AccessMode");
                String actualAccessMode = null;
                // if (checkedParticipant.getAudioConnectionType() != null) {
                // actualAccessMode =
                // checkedParticipant.getAudioConnectionType()
                // .value();
                // } else {
                actualAccessMode = checkedParticipant.getInUseDevice()
                        .getPreferredConnectionType().value();
                // }
                if (actualAccessMode.toUpperCase()
                        .contains(expectAccessMode.toUpperCase())) {
                    status = "SUCCESS";
                    logger.info("The participant "
                            + getInputCmd().get("participant1")
                            + " access mode is as expected "
                            + expectAccessMode);
                } else {
                    status = "Failed";
                    final String errorMsg = "The participant "
                            + getInputCmd().get("participant1")
                            + " access mode " + expectAccessMode
                            + " does not support."
                            + " The actual access mode is " + actualAccessMode;
                    logger.error(errorMsg);
                    return status + errorMsg;
                }
            } catch (final Exception e) {
                logger.error("Exception found with message " + e.getMessage());
                e.printStackTrace();
                return "Failed Exception found when trying to get the access mode for "
                        + "participant " + getInputCmd().get("participant1")
                        + " with message " + e.getMessage();
            }
        }
        // Check the participant 1# call bit rate part1BitRate
        if (!getInputCmd().get("part1BitRate").isEmpty()) {
            final double actualBitRate = checkedParticipant.getInUseDevice()
                    .getAudioRateUsedKbps()
                    + checkedParticipant.getInUseDevice()
                            .getVideoRateUsedKbps();
            if (Integer.valueOf(getInputCmd().get("part1BitRate")) > 0) {
                if (Integer.valueOf(getInputCmd()
                        .get("part1BitRate")) >= actualBitRate) {
                    status = "SUCCESS";
                    logger.info("The participant "
                            + getInputCmd().get("participant1")
                            + " bit rate is as expected "
                            + "which is below the negotiated call rate "
                            + getInputCmd().get("part1BitRate") + " kbps.");
                } else {
                    status = "Failed";
                    logger.error("The participant "
                            + getInputCmd().get("participant1")
                            + " bit rate is greater than the negotiated call rate "
                            + getInputCmd().get("part1BitRate")
                            + ". The actual call rate is "
                            + (checkedParticipant.getInUseDevice()
                                    .getAudioRateUsedKbps()
                                    + checkedParticipant.getInUseDevice()
                                            .getVideoRateUsedKbps()));
                    return status + " The participant "
                            + getInputCmd().get("participant1")
                            + " bit rate is greater than the negotiated call rate "
                            + getInputCmd().get("part1BitRate")
                            + ". The actual call rate is "
                            + (checkedParticipant.getInUseDevice()
                                    .getAudioRateUsedKbps()
                                    + checkedParticipant.getInUseDevice()
                                            .getVideoRateUsedKbps());
                }
            } else
                if (Integer.valueOf(getInputCmd().get("part1BitRate")) == 0) {
                for (int i = 0; i < 3; i++) {
                    if (0 < actualBitRate) {
                        status = "SUCCESS";
                        logger.info("The participant "
                                + getInputCmd().get("participant1")
                                + " bit rate is larger than 0 kbps");
                        break;
                    } else {
                        status = "Failed";
                        if (i < 2) {
                            logger.info("The participant "
                                    + getInputCmd().get("participant1")
                                    + " bit rate is "
                                    + (checkedParticipant.getInUseDevice()
                                            .getAudioRateUsedKbps()
                                            + checkedParticipant
                                                    .getInUseDevice()
                                                    .getVideoRateUsedKbps()));
                            try {
                                Thread.sleep(2000);
                            } catch (final Exception e) {
                                logger.info("In validateParticipantInfoInConference, when check bitrate, exception occured during sleep");
                            }
                            continue;
                        } else {
                            status = "Failed";
                            logger.error("The participant "
                                    + getInputCmd().get("participant1")
                                    + " bit rate is "
                                    + (checkedParticipant.getInUseDevice()
                                            .getAudioRateUsedKbps()
                                            + checkedParticipant
                                                    .getInUseDevice()
                                                    .getVideoRateUsedKbps()));
                            return status + " The participant "
                                    + getInputCmd().get("participant1")
                                    + " bit rate is 0 kbps.";
                        }
                    }
                }
            } else {
                status = "Failed";
                logger.error("expect bit rate error, it should not be less than 0");
                return status
                        + " expect bit rate error, it should not be less than 0";
            }
        } else {
            logger.info("No bit rate found in the input commands. "
                    + "Hence will not validate the bit rate for participant"
                    + getInputCmd().get("participant1"));
        }
        // Check the participant bridge name
        if (!getInputCmd().get("part1BridgeName").isEmpty()) {
            try {
                if (!getInputCmd().get("part1BridgeName").isEmpty()) {
                    String actualBridgeName = "";
                    if (checkedParticipant.getHostingDevice() == null) {
                        actualBridgeName = "N/A";
                    } else {
                        actualBridgeName = checkedParticipant.getHostingDevice()
                                .getName();
                    }
                    if (getInputCmd().get("part1BridgeName").replace("~", " ")
                            .equalsIgnoreCase(actualBridgeName)) {
                        status = "SUCCESS";
                        logger.info("The participant "
                                + getInputCmd().get("participant1")
                                + " bridge name is as expected "
                                + getInputCmd().get("part1BridgeName"));
                    } else {
                        status = "Failed";
                        logger.error("The participant "
                                + getInputCmd().get("participant1")
                                + " bridge name is not as expected "
                                + getInputCmd().get("part1BridgeName")
                                + ", actual value is: " + actualBridgeName);
                        return status + " The participant "
                                + getInputCmd().get("participant1")
                                + " bridge name is not as expected: "
                                + "expect name is "
                                + getInputCmd().get("part1BridgeName")
                                + ", but actual name is " + actualBridgeName;
                    }
                }
            } catch (final Exception e) {
                logger.error("Exception found with message " + e.getMessage());
                e.printStackTrace();
                return "Failed Exception found when trying to get the bridge for "
                        + "participant " + getInputCmd().get("participant1")
                        + " with message " + e.getMessage();
            }
        }
        // Check the participant encrypt status
        if (!getInputCmd().get("part1Encryption").isEmpty()) {
            try {
            	if ((getInputCmd().get("part1Encryption").equalsIgnoreCase("no") && (checkedParticipant.isEncryption() == null || checkedParticipant.isEncryption() == false)) 
            			|| (getInputCmd().get("part1Encryption").equalsIgnoreCase("yes") && (checkedParticipant.isEncryption() != null && checkedParticipant.isEncryption() == true))) {
                    status = "SUCCESS";
                    logger.info("The participant "
                            + getInputCmd().get("participant1")
                            + " encryption status is as expected "
                            + getInputCmd().get("part1Encryption"));
            	} else {
                    status = "Failed";
                    logger.error("The participant "
                            + getInputCmd().get("participant1")
                            + " encryption status is not as expected "
                            + getInputCmd().get("part1Encryption"));
                    return status + " The participant "
                            + getInputCmd().get("participant1")
                            + " encryption status is not as expected: "
                            + "expect name is "
                            + getInputCmd().get("part1Encryption");
            	}
            } catch (final Exception e) {
                logger.error("Exception found with message " + e.getMessage());
                e.printStackTrace();
                return "Failed Exception found when trying to get the encryption status for "
                        + "participant " + getInputCmd().get("participant1")
                        + " with message " + e.getMessage();
            }
        }
        return status;
    }
}
