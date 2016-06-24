package com.polycom.sqa.xma.webservices.driver;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
//import org.apache.cxf.common.util.Base64Utility;
import org.apache.log4j.Logger;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.interceptor.JsonInvocationHandler;
import com.polycom.sqa.xma.webservices.driver.interceptor.SoapHeaderOutInterceptor;
import com.polycom.webservices.UserManager.H323Alias;
import com.polycom.webservices.UserManager.JCommonUIObject;
import com.polycom.webservices.UserManager.JConfGuest;
import com.polycom.webservices.UserManager.JCredentials;
import com.polycom.webservices.UserManager.JEndpointForDetails;
import com.polycom.webservices.UserManager.JEnterpriseType;
import com.polycom.webservices.UserManager.JGroup;
import com.polycom.webservices.UserManager.JPermission;
import com.polycom.webservices.UserManager.JRoom;
import com.polycom.webservices.UserManager.JTaskInfo;
import com.polycom.webservices.UserManager.JUser;
import com.polycom.webservices.UserManager.JUserLockout;
import com.polycom.webservices.UserManager.JUserLoginStatus;
import com.polycom.webservices.UserManager.JUserManager;
import com.polycom.webservices.UserManager.JUserManager_Service;
import com.polycom.webservices.UserManager.JUserSearchType;
import com.polycom.webservices.UserManager.JUserType;
import com.polycom.webservices.UserManager.JWebResult;
import com.polycom.webservices.UserManager.SipAlias;

/**
 * User manager handler
 *
 * @author Tao Chen
 *
 */
public class UserManagerHandler {
    protected static Logger    logger               = Logger
            .getLogger("UserManagerHandler");
    private static final QName SERVICE_NAME         = new QName(
            "http://polycom.com/WebServices", "JUserManager");
    final TLSClientParameters  tlsClientParameters  = new TLSClientParameters();
    private String             userToken            = "";
    private int                loginUserUgpId       = 0;
    JUserManager               port;
    private List<String>       guiduuidlist         = new ArrayList<String>();
    private Boolean            issystemadconnection = false;
    private JWebResult         result               = null;

    /**
     * Construction of UserManagerHandler class
     */
    public UserManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JUserManager) jsonInvocationHandler
                    .getProxy(JUserManager.class);
        } else {
            final URL wsdlURL = UserManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JUserManager.wsdl");
            final JUserManager_Service ss = new JUserManager_Service(wsdlURL,
                    SERVICE_NAME);
            port = ss.getJUserManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext().put(
                                             BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                             webServiceUrl + "/JUserManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    /**
     * Add guest
     *
     * @param userToken
     * @param firstName
     * @param lastName
     * @param email
     * @param location
     * @param dialInOrNot
     * @param joinMode
     * @param isH323
     * @param isSIP
     * @param isISDN
     * @param sipUri
     * @param countryCode
     * @param areaCode
     * @param isdnNum
     * @param extenNum
     * @param useModDialNum
     * @param modDialNum
     * @param aliasType
     * @param aliasValue
     * @return
     */
    public JWebResult addGuest(final String userToken,
                               final JConfGuest newGuest) {
        System.out.println("Invoking addConfGuest...");
        logger.info("Invoking addConfGuest...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<Integer> confGuestId = new Holder<Integer>();
        final JWebResult result = port.addConfGuest(credentials,
                                                    newGuest,
                                                    confGuestId);
        return result;
    }

    /**
     * Add machine account
     *
     * @param userToken
     *            User token.
     * @param guid
     *            The GUID string for user or room. Set null, if it associates
     *            with a new room (created automatically).
     * @param maOwnerId
     *            Set 0, if associate with a new room (created automatically).
     * @param description
     *            Machine account description
     * @param passwd
     *            Machine account password. It will be encrypted with base 64
     *            format.
     * @param machineAccountName
     *            Machine account name.
     * @param RoomOrUser
     *            Set true, if the machine account will associate with a room.
     *            Set false, if the machine account associate with a new room
     *            (created automatically) or just associate with a user.
     * @return The status of this operation. Pass or Fail.
     */
    public JWebResult addMachineAccount(final String userToken,
                                        final JUser user) {
        System.out.println("Invoking addMachineAccount...");
        logger.info("Invoking addMachineAccount...");
        final JCredentials _addMachineAccount_credentials = new JCredentials();
        _addMachineAccount_credentials.setUserToken(userToken);
        final JWebResult _addMachineAccount__return = port
                .addMachineAccount(_addMachineAccount_credentials, user);
        System.out.println("addMachineAccount.result="
                + _addMachineAccount__return);
        return _addMachineAccount__return;
    }

    /**
     * Create user without dial string since it can be generated by the REST API
     *
     * @param userToken
     * @param firstname
     * @param lastname
     * @param username
     * @param email
     * @param password
     * @param deviceIDs
     * @param group
     * @return
     */
    public JWebResult addUserExt(final String userToken,
                                 final String firstname,
                                 final String lastname,
                                 final String username,
                                 final String email,
                                 final String password,
                                 final List<Integer> deviceIDs,
                                 final JGroup... group) {
        System.out.println("Invoking addUserExt...");
        logger.info("Invoking addUserExt...");
        final JCredentials _addUserExt_credentials = new JCredentials();
        _addUserExt_credentials.setUserToken(userToken);
        final JUser _addUserExt_user = new JUser();
        _addUserExt_user.setBelongsToAreaUgpId(-1);
        _addUserExt_user.setDbKey(0);
        _addUserExt_user.setEnterpriseType(JEnterpriseType.LOCAL);
        _addUserExt_user.setUgpId(0);
        _addUserExt_user.setAlertProfileId(-1);
        _addUserExt_user.setDaysToPWExpire(0);
        _addUserExt_user.setEmail(email);
        _addUserExt_user.setFailedAttemptsSinceStart(0);
        _addUserExt_user.setFailedLoginAttempts(0);
        _addUserExt_user.setFirstName(firstname);
        _addUserExt_user.setLastName(lastname);
        _addUserExt_user.setLockout(JUserLockout.UNLOCKED);
        _addUserExt_user.setMachineAccountOwner(0);
        _addUserExt_user.setPassword(password);
        _addUserExt_user.setRoom(false);
        _addUserExt_user.setUserName(username);
        final List<Integer> _addUserExt_groupIDs = new LinkedList<Integer>();
        for (final JGroup gp : group) {
            _addUserExt_groupIDs.add(gp.getUgpId());
        }
        final List<Integer> _addUserExt_deviceIDs = deviceIDs;
        final List<H323Alias> _addUserExt_h323Base = new ArrayList<H323Alias>();
        final List<H323Alias> _addUserExt_h323New = new ArrayList<H323Alias>();
        final List<H323Alias> _addUserExt_h323Update = new ArrayList<H323Alias>();
        final List<H323Alias> _addUserExt_h323Delete = new ArrayList<H323Alias>();
        final List<SipAlias> _addUserExt_sipBase = new ArrayList<SipAlias>();
        final List<SipAlias> _addUserExt_sipNew = new ArrayList<SipAlias>();
        final List<SipAlias> _addUserExt_sipUpdate = new ArrayList<SipAlias>();
        final List<SipAlias> _addUserExt_sipDelete = new ArrayList<SipAlias>();
        final JWebResult _addUserExt__return = port
                .addUserExt(_addUserExt_credentials,
                            _addUserExt_user,
                            _addUserExt_groupIDs,
                            _addUserExt_deviceIDs,
                            _addUserExt_h323Base,
                            _addUserExt_h323New,
                            _addUserExt_h323Update,
                            _addUserExt_h323Delete,
                            _addUserExt_sipBase,
                            _addUserExt_sipNew,
                            _addUserExt_sipUpdate,
                            _addUserExt_sipDelete);
        System.out.println("addUserExt.result=" + _addUserExt__return);
        return _addUserExt__return;
    }

    /**
     * Create a room
     *
     * @param userToken
     * @param roomname
     * @param description
     * @param email
     * @param h323New
     * @param sipNew
     * @param siteUid
     * @param enterpriseType
     *            Local or enterprise
     * @param guid
     * @return
     */
    public JWebResult createRoomExt(final String userToken,
                                    final JRoom room,
                                    final List<H323Alias> h323New,
                                    final List<SipAlias> sipNew,
                                    final boolean forceDelete) {
        System.out.println("Invoking createRoomExt...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final List<H323Alias> h323Base = new ArrayList<H323Alias>();
        final List<H323Alias> h323Update = new ArrayList<H323Alias>();
        final List<H323Alias> h323Delete = new ArrayList<H323Alias>();
        final List<SipAlias> sipBase = new ArrayList<SipAlias>();
        final List<SipAlias> sipUpdate = new ArrayList<SipAlias>();
        final List<SipAlias> sipDelete = new ArrayList<SipAlias>();
        final JWebResult result = port
                .createRoomExt(credentials,
                               room,
                               h323Base,
                               h323New,
                               h323Update,
                               h323Delete,
                               sipBase,
                               sipNew,
                               sipUpdate,
                               sipDelete,
                               forceDelete);
        return result;
    }

    /**
     * Delete all the orphan AD user related data on RPRM
     *
     * @param userToken
     * @return
     */
    public JWebResult deleteAllOrphanAdUser(final String userToken) {
        logger.info("Invoking deleteAllOrphanAdUser...");
        final JCredentials _deleteAllOrphanAdUser_credentials = new JCredentials();
        _deleteAllOrphanAdUser_credentials.setUserToken(userToken);
        final List<String> _deleteAllOrphanAdUser_guiduuidlist = getGuidList();
        final boolean _deleteAllOrphanAdUser_issystemadconnection = getIssystemadConnection();
        final JTaskInfo _deleteAllOrphanAdUser_taskInfoVal = new JTaskInfo();
        final Holder<JTaskInfo> _deleteAllOrphanAdUser_taskInfo = new Holder<JTaskInfo>(
                _deleteAllOrphanAdUser_taskInfoVal);
        final JWebResult _deleteAllOrphanAdUser__return = port
                .deleteAllOrphanAdUser(_deleteAllOrphanAdUser_credentials,
                                       _deleteAllOrphanAdUser_guiduuidlist,
                                       _deleteAllOrphanAdUser_issystemadconnection,
                                       _deleteAllOrphanAdUser_taskInfo);
        return _deleteAllOrphanAdUser__return;
    }

    /**
     * Delete guest
     *
     * @param userToken
     * @param guestId
     * @return
     */
    public JWebResult deleteGuest(final String userToken,
                                  final List<Integer> guestId) {
        System.out.println("Invoking deleteConfGuest...");
        logger.info("Invoking deleteConfGuest...");
        final JCredentials _deleteConfGuest_credentials = new JCredentials();
        _deleteConfGuest_credentials.setUserToken(userToken);
        final List<Integer> _deleteConfGuest_confGuestId = guestId;
        final JWebResult _deleteConfGuest__return = port
                .deleteConfGuest(_deleteConfGuest_credentials,
                                 _deleteConfGuest_confGuestId);
        System.out
                .println("deleteConfGuest.result=" + _deleteConfGuest__return);
        return _deleteConfGuest__return;
    }

    /**
     * Delete the specified machine account
     *
     * @param userToken
     *            User token
     * @param maID
     *            Machine account ID
     * @return
     */
    public JWebResult deleteMachineAccount(final String userToken,
                                           final int maID) {
        System.out.println("Invoking deleteMachineAccount...");
        logger.info("Invoking deleteMachineAccount...");
        final JCredentials _deleteMachineAccount_credentials = new JCredentials();
        _deleteMachineAccount_credentials.setUserToken(userToken);
        final int _deleteMachineAccount_userID = maID;
        final JWebResult _deleteMachineAccount__return = port
                .deleteMachineAccount(_deleteMachineAccount_credentials,
                                      _deleteMachineAccount_userID);
        System.out.println("deleteMachineAccount.result="
                + _deleteMachineAccount__return);
        return _deleteMachineAccount__return;
    }

    /**
     * Delete specified room
     *
     * @param userToken
     * @param room
     * @return
     */
    public JWebResult deleteRoom(final String userToken, final JRoom room) {
        System.out.println("Invoking deleteRoom...");
        logger.info("Invoking deleteRoom...");
        final JCredentials _deleteRoom_credentials = new JCredentials();
        _deleteRoom_credentials.setUserToken(userToken);
        final JRoom _deleteRoom_roomVal = room;
        final Holder<JRoom> _deleteRoom_room = new Holder<JRoom>(
                _deleteRoom_roomVal);
        final JWebResult _deleteRoom__return = port
                .deleteRoom(_deleteRoom_credentials, _deleteRoom_room);
        System.out.println("deleteRoom.result=" + _deleteRoom__return);
        System.out.println("deleteRoom._deleteRoom_room="
                + _deleteRoom_room.value);
        return _deleteRoom__return;
    }

    /**
     * Delete the specified users
     *
     * @param userToken
     * @param userIDs
     * @param userNames
     * @return
     */
    public JWebResult deleteUsers(final String userToken,
                                  final List<Integer> userIDs,
                                  final List<String> userNames) {
        System.out.println("Invoking deleteUsers...");
        logger.info("Invoking deleteUsers...");
        final JCredentials _deleteUsers_credentials = new JCredentials();
        _deleteUsers_credentials.setUserToken(userToken);
        final List<Integer> _deleteUsers_userIDs = userIDs;
        final List<String> _deleteUsers_userNames = userNames;
        final JWebResult _deleteUsers__return = port
                .deleteUsers(_deleteUsers_credentials,
                             _deleteUsers_userIDs,
                             _deleteUsers_userNames);
        System.out.println("deleteUsers.result=" + _deleteUsers__return);
        return _deleteUsers__return;
    }

    /**
     * Edit guest
     *
     * @param userToken
     * @param firstName
     * @param lastName
     * @param email
     * @param location
     * @param dialInOrNot
     * @param joinMode
     * @param isH323
     * @param isSIP
     * @param isISDN
     * @param sipUri
     * @param countryCode
     * @param areaCode
     * @param isdnNum
     * @param extenNum
     * @param useModDialNum
     * @param modDialNum
     * @param aliasType
     * @param aliasValue
     * @param ugpid
     * @param guestId
     * @return
     */
    public JWebResult editGuest(final String userToken,
                                final JConfGuest _editConfGuest_confGuest) {
        System.out.println("Invoking editConfGuest...");
        logger.info("Invoking editConfGuest...");
        final JCredentials _editConfGuest_credentials = new JCredentials();
        _editConfGuest_credentials.setUserToken(userToken);
        final JWebResult _editConfGuest__return = port
                .editConfGuest(_editConfGuest_credentials,
                               _editConfGuest_confGuest);
        System.out.println("editConfGuest.result=" + _editConfGuest__return);
        return _editConfGuest__return;
    }

    /**
     * Find specified participants in the on going conference
     *
     * @param userToken
     * @param firstName
     * @param lastName
     * @return
     */
    public List<JUser> findConferenceParticipants(final String userToken,
                                                  final String firstName,
                                                  final String lastName) {
        System.out.println("Invoking findConferenceParticipants...");
        logger.info("Invoking findConferenceParticipants...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JUser>> users = new Holder<List<JUser>>();
        port.findConferenceParticipants(credentials,
                                        firstName,
                                        lastName,
                                        false,
                                        users);
        return users.value;
    }

    public List<JEndpointForDetails> findDeviceForUser(final String userToken,
                                                       final int userID,
                                                       final String guid) {
        System.out.println("Invoking findUser...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JUser> user = new Holder<JUser>();
        final Holder<List<JEndpointForDetails>> details = new Holder<List<JEndpointForDetails>>();
        port.find(credentials, userID, guid, user, details);
        return details.value;
    }

    /**
     * Find a conference participant
     *
     * @param firstName
     * @param lastName
     * @param userToken
     * @return
     */
    public Holder<List<com.polycom.webservices.UserManager.JUser>>
            findParticipant(final String firstName,
                            final String lastName,
                            final String userToken) {
        System.out.println("Invoking findConferenceParticipants...");
        final JCredentials _findConferenceParticipants_credentials = new JCredentials();
        _findConferenceParticipants_credentials.setUserToken(userToken);
        final String _findConferenceParticipants_firstName = firstName;
        final String _findConferenceParticipants_lastName = lastName;
        final boolean _findConferenceParticipants_tag = false;
        final Holder<List<JUser>> _findConferenceParticipants_users = new Holder<List<JUser>>();
        final JWebResult _findConferenceParticipants__return = port
                .findConferenceParticipants(_findConferenceParticipants_credentials,
                                            _findConferenceParticipants_firstName,
                                            _findConferenceParticipants_lastName,
                                            _findConferenceParticipants_tag,
                                            _findConferenceParticipants_users);
        System.out.println("findConferenceParticipants.result="
                + _findConferenceParticipants__return);
        System.out
                .println("findConferenceParticipants._findConferenceParticipants_users="
                        + _findConferenceParticipants_users.value);
        System.out.println("guid is: " + _findConferenceParticipants_users.value
                .iterator().next().getGUID());
        System.out
                .println("ugpid is: " + _findConferenceParticipants_users.value
                        .iterator().next().getUgpId());
        return _findConferenceParticipants_users;
    }

    /**
     * findUser
     *
     * @param userToken
     * @param userID
     * @param guid
     * @return user, details
     */
    public JUser findUser(final String userToken,
                          final int userID,
                          final String guid) {
        System.out.println("Invoking findUser...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JUser> user = new Holder<JUser>();
        final Holder<List<JEndpointForDetails>> details = new Holder<List<JEndpointForDetails>>();
        port.find(credentials, userID, guid, user, details);
        return user.value;
    }

    public List<String> getGuidList() {
        return guiduuidlist;
    }

    public Boolean getIssystemadConnection() {
        return issystemadconnection;
    }

    public int getLoginUserUgpId() {
        return loginUserUgpId;
    }

    /**
     * Get the list of the machine account
     *
     * @param userToken
     * @return
     */
    public List<JUser> getMachineAccounts(final String userToken) {
        System.out.println("Invoking getMachineAccounts...");
        logger.info("Invoking getMachineAccounts...");
        final JCredentials _getMachineAccounts_credentials = new JCredentials();
        _getMachineAccounts_credentials.setUserToken(userToken);
        final Holder<List<JUser>> _getMachineAccounts_machineAccounts = new Holder<List<JUser>>();
        final JWebResult _getMachineAccounts__return = port
                .getMachineAccounts(_getMachineAccounts_credentials,
                                    _getMachineAccounts_machineAccounts);
        System.out.println("getMachineAccounts.result="
                + _getMachineAccounts__return);
        System.out
                .println("getMachineAccounts._getMachineAccounts_machineAccounts="
                        + _getMachineAccounts_machineAccounts.value);
        return _getMachineAccounts_machineAccounts.value;
    }

    /**
     * Get the orphan AD related data on RPRM
     *
     * @param userToken
     * @return
     */
    public JWebResult getOrphanADData(final String userToken) {
        logger.info("Invoking getOrphanADData...");
        final JCredentials _getOrphanADData_credentials = new JCredentials();
        _getOrphanADData_credentials.setUserToken(userToken);
        final Holder<List<String>> _getOrphanADData_guiduuidlist = new Holder<List<String>>();
        final Holder<Boolean> _getOrphanADData_issystemadconnection = new Holder<Boolean>();
        final Holder<JTaskInfo> _getOrphanADData_taskInfo = new Holder<JTaskInfo>();
        final JWebResult _getOrphanADData__return = port
                .getOrphanADData(_getOrphanADData_credentials,
                                 _getOrphanADData_guiduuidlist,
                                 _getOrphanADData_issystemadconnection,
                                 _getOrphanADData_taskInfo);
        setGuidList(_getOrphanADData_guiduuidlist.value);
        setIssystemadConnection(_getOrphanADData_issystemadconnection.value);
        return _getOrphanADData__return;
    }

    /**
     * Get the reserved H323 Alias before updating
     *
     * @param userToken
     * @param userID
     * @return
     */
    public List<H323Alias> getReservedH323Alias(final String userToken,
                                                final int userID) {
        System.out.println("Invoking getReservedH323Alias...");
        logger.info("Invoking getReservedH323Alias...");
        final JCredentials _getReservedH323Alias_credentials = new JCredentials();
        _getReservedH323Alias_credentials.setUserToken(userToken);
        final int _getReservedH323Alias_userID = userID;
        final Holder<List<H323Alias>> _getReservedH323Alias_reservation = new Holder<List<H323Alias>>();
        final JWebResult _getReservedH323Alias__return = port
                .getReservedH323Alias(_getReservedH323Alias_credentials,
                                      _getReservedH323Alias_userID,
                                      _getReservedH323Alias_reservation);
        System.out.println("getReservedH323Alias.result="
                + _getReservedH323Alias__return);
        System.out
                .println("getReservedH323Alias._getReservedH323Alias_reservation="
                        + _getReservedH323Alias_reservation.value);
        return _getReservedH323Alias_reservation.value;
    }

    /**
     * Get the reserved SIP Alias before updating
     *
     * @param userToken
     * @param userID
     * @return
     */
    public List<SipAlias> getReservedSipUri(final String userToken,
                                            final int userID) {
        System.out.println("Invoking getReservedSipUri...");
        logger.info("Invoking getReservedSipUri...");
        final JCredentials _getReservedSipUri_credentials = new JCredentials();
        _getReservedSipUri_credentials.setUserToken(userToken);
        final int _getReservedSipUri_userID = userID;
        final Holder<List<SipAlias>> _getReservedSipUri_reservation = new Holder<List<SipAlias>>();
        final JWebResult _getReservedSipUri__return = port
                .getReservedSipUri(_getReservedSipUri_credentials,
                                   _getReservedSipUri_userID,
                                   _getReservedSipUri_reservation);
        System.out.println("getReservedSipUri.result="
                + _getReservedSipUri__return);
        System.out.println("getReservedSipUri._getReservedSipUri_reservation="
                + _getReservedSipUri_reservation.value);
        return _getReservedSipUri_reservation.value;
    }

    public JWebResult getResult() {
        return result;
    }

    /**
     * Get room list
     *
     * @param userToken
     */
    public List<JRoom> getRooms(final String userToken) {
        return getRooms(userToken, null);
    }

    /**
     * Get room list
     *
     * @param userToken
     */
    public List<JRoom> getRooms(final String userToken,
                                final String searchStr) {
        System.out.println("Invoking getRooms...");
        logger.info("Invoking getRooms...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JRoom>> _getRooms_rooms = new Holder<List<JRoom>>();
        port.getRooms(credentials, _getRooms_rooms);
        if (searchStr != null && !searchStr.isEmpty()) {
            final Pattern pattern = Pattern.compile(searchStr);
            final List<JRoom> rooms = new ArrayList<JRoom>();
            for (final JRoom room : _getRooms_rooms.value) {
                final Matcher matcher = pattern.matcher(room.getName());
                if (matcher.find()) {
                    rooms.add(room);
                }
            }
            return rooms;
        } else {
            return _getRooms_rooms.value;
        }
    }

    /**
     * Get users and rooms when trying to create machine account
     *
     * @param userToken
     * @param searchString
     * @return
     */
    public List<JUser> getUserAndRooms(final String userToken,
                                       final String searchString) {
        System.out.println("Invoking getUserAndRooms...");
        logger.info("Invoking getUserAndRooms...");
        final JCredentials _getUserAndRooms_credentials = new JCredentials();
        _getUserAndRooms_credentials.setUserToken(userToken);
        final String _getUserAndRooms_searchString = searchString;
        final JUserSearchType _getUserAndRooms_localUsersOnly = JUserSearchType.ALL___USERS;
        final List<JUserType> _getUserAndRooms_userTypes = new LinkedList<JUserType>();
        final JUserType userTypes = JUserType.NORMAL;
        _getUserAndRooms_userTypes.add(userTypes);
        final Holder<List<JUser>> _getUserAndRooms_users = new Holder<List<JUser>>();
        final JWebResult _getUserAndRooms__return = port
                .getUserAndRooms(_getUserAndRooms_credentials,
                                 _getUserAndRooms_searchString,
                                 _getUserAndRooms_localUsersOnly,
                                 _getUserAndRooms_userTypes,
                                 _getUserAndRooms_users);
        System.out
                .println("getUserAndRooms.result=" + _getUserAndRooms__return);
        System.out.println("getUserAndRooms._getUserAndRooms_users="
                + _getUserAndRooms_users.value);
        return _getUserAndRooms_users.value;
    }

    public List<JCommonUIObject> getUserGroupsForRule(final String userToken) {
        System.out.println("Invoking getUserGroupsForRule...");
        logger.info("Invoking getUserGroupsForRule...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JCommonUIObject>> userGroups = new Holder<List<JCommonUIObject>>();
        port.getUserGroupsForRule(credentials, userGroups);
        return userGroups.value;
    }

    /**
     * Get specified user's detail information
     *
     * @param userToken
     * @param searchString
     * @param localOrAD
     *            Local, AD or alluser
     * @return
     */
    public List<JUser> getUsers(final String userToken,
                                final String searchString,
                                final String localOrAD) {
        System.out.println("Invoking getUsers...");
        logger.info("Invoking getUsers...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        JUserSearchType localUsersOnly = null;
        if (localOrAD.equalsIgnoreCase("local")) {
            localUsersOnly = JUserSearchType.LOCAL___ONLY;
        } else if (localOrAD.equalsIgnoreCase("AD")) {
            localUsersOnly = JUserSearchType.AD___ONLY;
        } else if (localOrAD.equalsIgnoreCase("AllUser")) {
            localUsersOnly = JUserSearchType.ALL___USERS;
        } else {
            localUsersOnly = JUserSearchType.LOCAL___ONLY;
        }
        final List<JUserType> userTypes = new LinkedList<JUserType>();
        final JUserType userType = JUserType.NORMAL;
        userTypes.add(userType);
        final Holder<List<JUser>> users = new Holder<List<JUser>>();
        port.getUsers(credentials,
                      searchString,
                      localUsersOnly,
                      userTypes,
                      users);
        return users.value;
    }

    /**
     *
     * @param userToken
     * @param guest
     * @return true means duplicated
     */
    public boolean isDuplicated4Guest(final String userToken,
                                      final JConfGuest guest) {
        logger.info("Invoking isDuplicated4Guest...");
        System.out.println("Invoking isDuplicated4Guest...");
        result = null;
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<Boolean> inUse = new Holder<Boolean>();
        result = port.isDuplicated4Guest(credentials, guest, inUse);
        return inUse.value;
    }

    /**
     * Logout XMA
     *
     * @param userToken
     * @return
     */
    public String logout(final String userToken) {
        System.out.println("Invoking logout...");
        final JCredentials _logout_credentials = new JCredentials();
        _logout_credentials.setUserToken(userToken);
        final boolean _logout_isTimeout = false;
        final JWebResult _logout__return = port.logout(_logout_credentials,
                                                       _logout_isTimeout);
        System.out.println("logout.result=" + _logout__return);
        return _logout__return.getStatus().toString();
    }

    /**
     * Search conference guest
     *
     * @param userToken
     */
    public List<JConfGuest> searchConfGuest(final String userToken) {
        System.out.println("Invoking searchConfGuests...");
        logger.info("Invoking searchConfGuests...");
        final JCredentials _searchConfGuests_credentials = new JCredentials();
        _searchConfGuests_credentials.setUserToken(userToken);
        final Holder<List<JConfGuest>> _searchConfGuests_guests = new Holder<List<JConfGuest>>();
        final JWebResult _searchConfGuests__return = port
                .searchConfGuests(_searchConfGuests_credentials,
                                  _searchConfGuests_guests);
        System.out.println("searchConfGuests.result="
                + _searchConfGuests__return);
        System.out.println("searchConfGuests._searchConfGuests_guests="
                + _searchConfGuests_guests.value);
        return _searchConfGuests_guests.value;
    }

    /**
     * Search guests
     *
     * @param userToken
     * @return
     */
    public List<JConfGuest> searchGuest(final String userToken) {
        return searchGuest(userToken, null);
    }

    /**
     * Search guests
     *
     * @param userToken
     * @return
     */
    public List<JConfGuest> searchGuest(final String userToken,
                                        final String searchStr) {
        System.out.println("Invoking searchConfGuests...");
        logger.info("Invoking searchConfGuests...");
        final JCredentials _searchConfGuests_credentials = new JCredentials();
        _searchConfGuests_credentials.setUserToken(userToken);
        final Holder<List<JConfGuest>> _searchConfGuests_guests = new Holder<List<JConfGuest>>();
        final JWebResult _searchConfGuests__return = port
                .searchConfGuests(_searchConfGuests_credentials,
                                  _searchConfGuests_guests);
        System.out.println("searchConfGuests.result="
                + _searchConfGuests__return);
        System.out.println("searchConfGuests._searchConfGuests_guests="
                + _searchConfGuests_guests.value);
        if (searchStr != null && !searchStr.isEmpty()) {
            final Pattern pattern = Pattern.compile(searchStr);
            final List<JConfGuest> guests = new ArrayList<JConfGuest>();
            for (final JConfGuest guest : _searchConfGuests_guests.value) {
                final Matcher matcher = pattern.matcher(guest.getDisplayName());
                if (matcher.find()) {
                    guests.add(guest);
                }
            }
            return guests;
        } else {
            return _searchConfGuests_guests.value;
        }
    }

    public void setGuidList(final List<String> guiduuidlist) {
        this.guiduuidlist = guiduuidlist;
    }

    public void setIssystemadConnection(final Boolean issystemadconnection) {
        this.issystemadconnection = issystemadconnection;
    }

    public void setLoginUserUgpId(final int ugpid) {
        loginUserUgpId = ugpid;
    }

    /**
     * Update the specified machine account
     *
     * @param userToken
     *            User token
     * @param machineAccountName
     *            The machine account name
     * @param guid
     *            The machine account owner GUID string
     * @param maOwnerId
     *            The machine account owner ID
     * @param DisableOrNot
     *            True to disable the machine account. False to enable the
     *            machine account. False is the default value.
     * @param UnlockOrNot
     *            True to unlock the machine account. False is the default value
     *            which means the machine account is unlocked so far.
     * @return The status of the update
     */
    public JWebResult updateMachineAccount(final String userToken,
                                           final String machineAccountName,
                                           final String guid,
                                           final int maOwnerId,
                                           final boolean DisableOrNot,
                                           final boolean UnlockOrNot) {
        System.out.println("Invoking updateMachineAccount...");
        logger.info("Invoking updateMachineAccount...");
        final JCredentials _updateMachineAccount_credentials = new JCredentials();
        _updateMachineAccount_credentials.setUserToken(userToken);
        JUser _updateMachineAccount_user = null;
        for (final JUser machineAccount : getMachineAccounts(userToken)) {
            if (machineAccount.getUserName()
                    .equalsIgnoreCase(machineAccountName)
                    && machineAccount.getUserType()
                            .compareTo(JUserType.MACHINE___ACCOUNT) == 0) {
                logger.info("Found the machine account " + machineAccountName
                        + " and prepare to update it later.");
                _updateMachineAccount_user = machineAccount;
            }
        }
        // Prepare to update the machine account
        if (DisableOrNot && _updateMachineAccount_user.getLoginStatus()
                .compareTo(JUserLoginStatus.NORMAL) == 0) {
            _updateMachineAccount_user
                    .setLoginStatus(JUserLoginStatus.DISABLED);
        } else if (!DisableOrNot && _updateMachineAccount_user.getLoginStatus()
                .compareTo(JUserLoginStatus.DISABLED) == 0) {
            _updateMachineAccount_user
                    .setLoginStatus(JUserLoginStatus.PASSWORD___CHANGE___REQUIRED);
        }
        if (UnlockOrNot) {
            _updateMachineAccount_user.setLockout(JUserLockout.UNLOCKED);
        }
        if (!guid.isEmpty() && maOwnerId != 0) {
            _updateMachineAccount_user.setGUIDAsString(guid);
            _updateMachineAccount_user.setMachineAccountOwner(maOwnerId);
        }
        // Keep it null since machind account does not use it
        final List<Integer> _updateMachineAccount_deviceIDs = new ArrayList<Integer>();
        final JWebResult _updateMachineAccount__return = port
                .updateMachineAccount(_updateMachineAccount_credentials,
                                      _updateMachineAccount_user,
                                      _updateMachineAccount_deviceIDs);
        System.out.println("updateMachineAccount.result="
                + _updateMachineAccount__return);
        return _updateMachineAccount__return;
    }

    /**
     * Update the specified room
     *
     * @param userToken
     * @param room
     * @param h323Base
     * @param h323Update
     * @param h323New
     * @param sipBase
     * @param sipUpdate
     * @param sipNew
     * @return
     */
    public JWebResult updateRoomExt(final String userToken,
                                    final JRoom room,
                                    final List<H323Alias> h323Base,
                                    final List<H323Alias> h323Update,
                                    final List<H323Alias> h323New,
                                    final List<SipAlias> sipBase,
                                    final List<SipAlias> sipUpdate,
                                    final List<SipAlias> sipNew) {
        System.out.println("Invoking updateRoomExt...");
        logger.info("Invoking updateRoomExt...");
        final JCredentials _updateRoomExt_credentials = new JCredentials();
        _updateRoomExt_credentials.setUserToken(userToken);
        final JRoom _updateRoomExt_room = room;
        final List<H323Alias> _updateRoomExt_h323Base = h323Base;
        final List<H323Alias> _updateRoomExt_h323New = h323New;
        final List<H323Alias> _updateRoomExt_h323Update = h323Update;
        final List<H323Alias> _updateRoomExt_h323Delete = new ArrayList<H323Alias>();
        final List<SipAlias> _updateRoomExt_sipBase = sipBase;
        final List<SipAlias> _updateRoomExt_sipNew = sipNew;
        final List<SipAlias> _updateRoomExt_sipUpdate = sipUpdate;
        final List<SipAlias> _updateRoomExt_sipDelete = new ArrayList<SipAlias>();
        final JWebResult _updateRoomExt__return = port
                .updateRoomExt(_updateRoomExt_credentials,
                               _updateRoomExt_room,
                               _updateRoomExt_h323Base,
                               _updateRoomExt_h323New,
                               _updateRoomExt_h323Update,
                               _updateRoomExt_h323Delete,
                               _updateRoomExt_sipBase,
                               _updateRoomExt_sipNew,
                               _updateRoomExt_sipUpdate,
                               _updateRoomExt_sipDelete);
        System.out.println("updateRoomExt.result=" + _updateRoomExt__return);
        return _updateRoomExt__return;
    }

    public JWebResult updateUserExt(final String userToken,
                                    final JUser user,
                                    final List<Integer> groupIDs,
                                    final List<Integer> deviceIDs,
                                    final List<H323Alias> h323Base,
                                    final List<H323Alias> h323Update,
                                    final List<H323Alias> h323New,
                                    final List<H323Alias> h323Delete,
                                    final List<SipAlias> sipBase,
                                    final List<SipAlias> sipUpdate,
                                    final List<SipAlias> sipNew,
                                    final List<SipAlias> sipDelete) {
        System.out.println("Invoking updateUserExt...");
        logger.info("Invoking updateUserExt...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port
                .updateUserExt(credentials,
                               user,
                               groupIDs,
                               deviceIDs,
                               h323Base,
                               h323New,
                               h323Update,
                               h323Delete,
                               sipBase,
                               sipNew,
                               sipUpdate,
                               sipDelete);
        return result;
    }

    public JWebResult updateUserForDevice(final String userToken,
                                          final JUser user,
                                          final List<Integer> deviceIds) {
        System.out.println("Invoking updateUserForDevice...");
        logger.info("Invoking updateUserForDevice...");
        final JCredentials _updateUserForDevice_credentials = new JCredentials();
        final JUser _updateUserForDevice_user = user;
        _updateUserForDevice_credentials.setUserToken(userToken);
        final List<Integer> _updateUserForDevice_groupIds = deviceIds;
        final JWebResult _updateUserForDevice__return = port
                .updateUserForDevice(_updateUserForDevice_credentials,
                                     _updateUserForDevice_user,
                                     _updateUserForDevice_groupIds);
        System.out.println("_updateUserExt.result="
                + _updateUserForDevice__return);
        return _updateUserForDevice__return;
    }

    /**
     * Login XMA
     *
     * @param userName
     * @param domain
     * @param password
     * @return
     */
    @SuppressWarnings("unused")
    public String userLogin(final String userName,
                            final String domain,
                            final String password) {
        System.out.println("Invoking login...");
        final String _login_userName = userName;
        final String _login_password = password;
        final String _login_domain = domain;
        final Holder<JCredentials> _login_credentials = new Holder<JCredentials>();
        final Holder<JPermission> _login_permission = new Holder<JPermission>();
        final Holder<Integer> _login_forceLogoutKey = new Holder<Integer>();
        final Holder<JUser> _login_user = new Holder<JUser>();
        final JWebResult _login__return = port
                .login(_login_userName,
                       _login_password,
                       _login_domain,
                       _login_credentials,
                       _login_permission,
                       _login_forceLogoutKey,
                       _login_user);
        System.out.println("login._login_credentials="
                + _login_credentials.value);
        userToken = _login_credentials.value.getUserToken();
        setLoginUserUgpId(_login_user.value.getUgpId());
        return userToken;
    }
}
