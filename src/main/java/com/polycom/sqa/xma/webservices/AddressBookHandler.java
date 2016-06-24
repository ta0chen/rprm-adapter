package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.AddressBookManagerHandler;
import com.polycom.sqa.xma.webservices.driver.DeviceManagerHandler;
import com.polycom.sqa.xma.webservices.driver.GroupManagerHandler;
import com.polycom.webservices.AddressBookManager.JAddressBook;
import com.polycom.webservices.AddressBookManager.JAddressBookAssignment;
import com.polycom.webservices.AddressBookManager.JAddressBookSpecifier;
import com.polycom.webservices.AddressBookManager.JAddressBookTier;
import com.polycom.webservices.AddressBookManager.JEndpointAddressBookMember;
import com.polycom.webservices.AddressBookManager.JGabEntry;
import com.polycom.webservices.AddressBookManager.JStatus;
import com.polycom.webservices.AddressBookManager.JUserGroupAddressBookMember;
import com.polycom.webservices.AddressBookManager.JWebResult;
import com.polycom.webservices.DeviceManager.JEndpointForList;
import com.polycom.webservices.GroupManager.JGroup;
import com.polycom.webservices.UserManager.JConfGuest;
import com.polycom.webservices.UserManager.JRoom;
import com.polycom.webservices.UserManager.JUser;

/**
 * Address Book handler. This class will handle the webservice request of
 * Address Book module
 *
 * @author wbchao
 */
public class AddressBookHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "getGlobalAddressBookSpecificDetail ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params1 = "ipAddress=172.21.104.195 keyword=gabDisplayName ";
        // final String params1 = "name=copyMAB1 newname=newname4copy tier=3 ";
        // final String params2 =
        // "user1=aduser10|0|AD user2=aduser2|2|AD user3=aduser3|3|AD ";
        // final String params3 = "group1=lingxi|1 ";
        // final String params4 = "room1=DD|2 ";
        // final String params5 = "endpoint1=661022|2 ";
        // final String params6 = "guest1=ct ";
        final String command = "http://172.21.120.167/PlcmRmWeb/JAddressBookManager AddressBookManager "
                + method + auth + params1;
        // + params2
        // + params3
        // + params4
        // + params5 + params6;
        final AddressBookHandler handler = new AddressBookHandler(command);
        handler.logger.info("hoho");
        final String result = handler.build();
        handler.logger.info("The result is: " + result);
    }

    // Manager Handler
    private final AddressBookManagerHandler addressBookManagerHandler;

    public AddressBookHandler(final String cmd) throws IOException {
        super(cmd);
        addressBookManagerHandler = new AddressBookManagerHandler(
                webServiceUrl);
    }

    /**
     * Add address book
     *
     * @see name=MAB1 <br/>
     *      description=MAB1Description <br/>
     *      tier=5 <br/>
     *      user1=luser6|0 <br/>
     *      user2=luser7|1 <br/>
     *      user3=aduser6|2|AD <br/>
     *      user4=luser8|5 <br/>
     *      user5=aduser7|1|AD <br/>
     *      room1=lroom5 <br/>
     *      room2=adroom3 <br/>
     *      room3=adroom5|1 <br/>
     *      room4=lroom2|2 <br/>
     *      room5=lroom3|3 <br/>
     *      room6=adroom4|4 <br/>
     *      room7=lroom4|5 <br/>
     *      group1=lgroup1|1 <br/>
     *      group2=adgroup1|5 <br/>
     *      guest1=h323AnnexO <br/>
     *      guest2=h323e164|5 <br/>
     *      guest3=h323ip|4 <br/>
     *      guest4=isdn|1 <br/>
     *      guest5=sip|3 <br/>
     *      endpoint1=LegacyHDX3|1
     * @param name
     *            Address book name
     * @param description
     *            Address book description
     * @param tier
     *            [0-5] The tier number of address book
     * @param user
     *            [1-5] The user name. e.g. luser1|2 means add luser1 to tier2,
     *            no tier means add to root tier
     * @param guest
     *            [1-5] The guest name. e.g. guest1|3 means add guest1 to tier3,
     *            no tier means add to root tier
     * @param group
     *            [1-3] The group name. e.g. group1|2 means add group1 to tier2,
     *            no tier means add to root tier
     * @param room
     *            [1-7] The room name. e.g. room1|2 means add room to tier2, no
     *            tier means add to root tier
     * @param endpoint
     *            [1-3] The endpoint name. e.g. ep1|2 means add ep1 to tier2, no
     *            tier means add to root tier
     * @return The result
     */
    public String addAddressBook() {
        String status = "Failed";
        final GroupManagerHandler gmh = new GroupManagerHandler(webServiceUrl);
        final DeviceManagerHandler dmh = new DeviceManagerHandler(
                webServiceUrl);
        final JAddressBook jAddressBook = new JAddressBook();
        jAddressBook.setName(inputCmd.get("name"));
        jAddressBook.setDescription(inputCmd.get("description"));
        // Create tiers
        int tierNumber = 0;
        final Map<String, JAddressBookTier> tierMap = new HashMap<String, JAddressBookTier>();
        final String value = inputCmd.get("tier");
        if (!value.isEmpty() && value.matches("[012345]")) {
            tierNumber = Integer.parseInt(value);
            for (int i = 1; i <= tierNumber; i++) {
                final JAddressBookTier tier = new JAddressBookTier();
                tier.setName("Tier" + i);
                tierMap.put(i + "", tier);
            }
        }
        // Create users and add users to specified tier
        final List<String> userSearchStr = new ArrayList<String>();
        userSearchStr.add(inputCmd.get("user1"));
        userSearchStr.add(inputCmd.get("user2"));
        userSearchStr.add(inputCmd.get("user3"));
        userSearchStr.add(inputCmd.get("user4"));
        userSearchStr.add(inputCmd.get("user5"));
        for (final String searchStr : userSearchStr) {
            if (searchStr != null && !searchStr.isEmpty()) {
                final String[] args = searchStr.split("\\|");
                List<JUser> users = null;
                if (args.length < 3) {
                    users = umh.getUsers(userToken, args[0], "local");
                } else {
                    users = umh.getUsers(userToken, args[0], args[2]);
                }
                if (args.length < 2) {
                    logger.error("The tier for the user" + args[0]
                            + " must be specified, 0 means root");
                    return status;
                }
                if (Integer.parseInt(args[1]) > tierMap.size()) {
                    logger.error("The spcefied user tier should be less than tier number, tier number="
                            + tierMap.size() + ", user tier=" + args[1]);
                    return status;
                }
                if (!args[1].trim().equals("0")) {
                    // add users to specified tier
                    addUser(users, tierMap.get(args[1]).getUsers());
                } else {
                    // add users to root
                    addUser(users, jAddressBook.getUsers());
                }
            }
        }
        // Create guests and add guests to specified tier
        final List<String> guestSearchStr = new ArrayList<String>();
        guestSearchStr.add(inputCmd.get("guest1"));
        guestSearchStr.add(inputCmd.get("guest2"));
        guestSearchStr.add(inputCmd.get("guest3"));
        guestSearchStr.add(inputCmd.get("guest4"));
        guestSearchStr.add(inputCmd.get("guest5"));
        for (final String searchStr : guestSearchStr) {
            if (searchStr != null && !searchStr.isEmpty()) {
                final String[] args = searchStr.split("\\|");
                final List<JConfGuest> guests = umh.searchGuest(userToken,
                                                                args[0]);
                if (args.length == 2) {
                    if (Integer.parseInt(args[1]) > tierMap.size()) {
                        logger.error("The spcefied guest tier should be less than tier number, tier number="
                                + tierMap.size() + ", guest tier=" + args[1]);
                        return status;
                    }
                    // add users to specified tier
                    addGuest(guests, tierMap.get(args[1]).getGuests());
                } else {
                    // add users to root
                    addGuest(guests, jAddressBook.getGuests());
                }
            }
        }
        // Add group to tier
        final List<String> groupSearchStr = new ArrayList<String>();
        groupSearchStr.add(inputCmd.get("group1"));
        groupSearchStr.add(inputCmd.get("group2"));
        groupSearchStr.add(inputCmd.get("group3"));
        for (final String searchStr : groupSearchStr) {
            if (searchStr != null && !searchStr.isEmpty()) {
                final String[] args = searchStr.split("\\|");
                final List<JGroup> allGroups = gmh.getVisibleGroups(userToken);
                final List<JGroup> groups = new ArrayList<JGroup>();
                for (final JGroup g : allGroups) {
                    if (g.getDisplayname().equalsIgnoreCase(args[0])) {
                        groups.add(g);
                    }
                }
                if (args.length == 2) {
                    if (Integer.parseInt(args[1]) > tierMap.size()) {
                        logger.error("The spcefied group tier should be less than tier number, tier number="
                                + tierMap.size() + ", group tier=" + args[1]);
                        return status;
                    }
                    // add groups to specified tier
                    addGroup(groups, tierMap.get(args[1]).getGroups());
                } else {
                    // add groups to root
                    addGroup(groups, jAddressBook.getGroups());
                }
            }
        }
        // Add rooms to specified tier
        final List<String> roomSearchStr = new ArrayList<String>();
        roomSearchStr.add(inputCmd.get("room1"));
        roomSearchStr.add(inputCmd.get("room2"));
        roomSearchStr.add(inputCmd.get("room3"));
        roomSearchStr.add(inputCmd.get("room4"));
        roomSearchStr.add(inputCmd.get("room5"));
        roomSearchStr.add(inputCmd.get("room6"));
        roomSearchStr.add(inputCmd.get("room7"));
        for (final String searchStr : roomSearchStr) {
            if (searchStr != null && !searchStr.trim().isEmpty()) {
                final String[] args = searchStr.split("\\|");
                final List<JRoom> rooms = umh.getRooms(userToken, args[0]);
                if (args.length == 2) {
                    if (Integer.parseInt(args[1]) > tierMap.size()) {
                        logger.error("The spcefied room tier should be less than tier number, tier number="
                                + tierMap.size() + ", room tier=" + args[1]);
                        return status;
                    }
                    // add rooms to specified tier
                    addRoom(rooms, tierMap.get(args[1]).getRooms());
                } else {
                    // add rooms to root
                    addRoom(rooms, jAddressBook.getRooms());
                }
            }
        }
        // Add endpoints to specified tier
        final List<String> endpointSearchStr = new ArrayList<String>();
        endpointSearchStr.add(inputCmd.get("endpoint1"));
        endpointSearchStr.add(inputCmd.get("endpoint2"));
        endpointSearchStr.add(inputCmd.get("endpoint3"));
        for (final String searchStr : endpointSearchStr) {
            if (searchStr != null && !searchStr.trim().isEmpty()) {
                final String[] args = searchStr.split("\\|");
                final List<JEndpointForList> endpoints = dmh
                        .getUnassignedDynamicDevices(userToken, args[0]);
                if (args.length == 2) {
                    if (Integer.parseInt(args[1]) > tierMap.size()) {
                        logger.error("The spcefied endpoint tier should be less than tier number, tier number="
                                + tierMap.size() + ", endpoint tier="
                                + args[1]);
                        return status;
                    }
                    // add endpoints to specified tier
                    addEndpoint(endpoints, tierMap.get(args[1]).getEndpoints());
                } else {
                    // add endpoints to root
                    addEndpoint(endpoints, jAddressBook.getEndpoints());
                }
            }
        }
        // Add tier to address book
        for (final String key : tierMap.keySet()) {
            if (key.equals("1")) {
                jAddressBook.getTiers().add(tierMap.get(key));
            } else {
                final int tierNum = Integer.parseInt(key) - 1;
                final List<JAddressBookTier> tierList = tierMap
                        .get(tierNum + "").getTiers();
                tierList.add(tierMap.get(key));
            }
        }
        final JWebResult result = addressBookManagerHandler
                .addAddressBook(userToken, jAddressBook);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Create addressbook " + jAddressBook.getName()
                    + " successfully.");
        } else {
            logger.error("Failed to add address book "
                    + jAddressBook.getName());
            status = "Failed";
        }
        return status;
    }

    /**
     * Internal method to add endpoints to endpoint address book member list
     *
     * @param endpoints
     *            The endpoint list to add
     * @param target
     *            The endpoint address book member list to add to
     * @return Endpoint address book member list
     */
    private List<JEndpointAddressBookMember>
            addEndpoint(final List<JEndpointForList> endpoints,
                        final List<JEndpointAddressBookMember> target) {
        for (final JEndpointForList endpoint : endpoints) {
            final JEndpointAddressBookMember member = new JEndpointAddressBookMember();
            member.setUgpId(endpoint.getDeviceId());
            member.setOwnerUGP_ID(endpoint.getOwnerUgpId());
            target.add(member);
        }
        return target;
    }

    /**
     * Internal method to add groups to group address book member list
     *
     * @param groups
     *            The group list to add
     * @param target
     *            The group address book member list to add to
     * @return group address book member list
     */
    private List<JUserGroupAddressBookMember>
            addGroup(final List<JGroup> groups,
                     final List<JUserGroupAddressBookMember> target) {
        for (final JGroup group : groups) {
            final JUserGroupAddressBookMember member = new JUserGroupAddressBookMember();
            member.setUgpId(group.getUgpId());
            member.setGUID(group.getGUID());
            target.add(member);
        }
        return target;
    }

    /**
     * Internal method to add guests to guest address book member list
     *
     * @param guests
     *            The guest list to add
     * @param target
     *            The guest address book member list to add to
     * @return guest address book member list
     */
    private List<JUserGroupAddressBookMember>
            addGuest(final List<JConfGuest> guests,
                     final List<JUserGroupAddressBookMember> target) {
        for (final JUser guest : guests) {
            final JUserGroupAddressBookMember member = new JUserGroupAddressBookMember();
            member.setUgpId(guest.getUgpId());
            target.add(member);
        }
        return target;
    }

    /**
     * Internal method to add rooms to room address book member list
     *
     * @param rooms
     *            The room list to add
     * @param target
     *            The room address book member list to add to
     * @return room address book member list
     */
    private List<JUserGroupAddressBookMember>
            addRoom(final List<JRoom> rooms,
                    final List<JUserGroupAddressBookMember> target) {
        for (final JRoom room : rooms) {
            final JUserGroupAddressBookMember member = new JUserGroupAddressBookMember();
            member.setUgpId(room.getUgpId());
            target.add(member);
        }
        return target;
    }

    /**
     * Internal method to add users to user address book member list
     *
     * @param users
     *            The user list to add
     * @param target
     *            The user address book member list to add to
     * @return user address book member list
     */
    private List<JUserGroupAddressBookMember>
            addUser(final List<JUser> users,
                    final List<JUserGroupAddressBookMember> target) {
        for (final JUser user : users) {
            final JUserGroupAddressBookMember member = new JUserGroupAddressBookMember();
            member.setUgpId(user.getUgpId());
            member.setGUID(user.getGUID());
            target.add(member);
        }
        return target;
    }

    /**
     * Copy the address book
     *
     * @see name=MAB1 <br/>
     *      name4copy=MAB3 <br/>
     *      descriptioin4copy=MAB3Description
     * @param name
     *            Src address book name
     * @param name4copy
     *            New address book name
     * @param descriptioin4copy
     *            New address book description
     * @return The result
     */
    public String copyAddressBook() {
        String status = "Failed";
        final String searchStr = inputCmd.get("name");
        final JAddressBook addressBook = addressBookManagerHandler
                .getAddressBookByName(userToken, searchStr);
        if (addressBook != null) {
            if (!inputCmd.get("name4copy").isEmpty()) {
                addressBook.setName(inputCmd.get("name4copy"));
                addressBook.setDescription(inputCmd.get("descriptioin4copy"));
                if (addressBookManagerHandler
                        .addAddressBook(userToken, addressBook).getStatus()
                        .equals(JStatus.SUCCESS)) {
                    status = "SUCCESS";
                    logger.info("Copy address book " + inputCmd.get("name")
                            + " to " + inputCmd.get("name4copy")
                            + " successfully.");
                } else {
                    status = "Failed";
                    logger.error("Failed to copy the address book "
                            + inputCmd.get("name") + " to "
                            + inputCmd.get("name4copy"));
                    return status + " Failed to copy the address book "
                            + inputCmd.get("name") + " to "
                            + inputCmd.get("name4copy");
                }
            } else {
                status = "Failed";
                logger.error("Cannot find the new MAB name in the command. "
                        + "Please provide a name4copy in the input command.");
                return status + " Cannot find the new MAB name in the command. "
                        + "Please provide a name4copy in the input command.";
            }
        } else {
            logger.error("Fail to search addressbook by name " + searchStr);
            status = "Failed";
            return status + " Fail to search addressbook by name " + searchStr;
        }
        return status;
    }

    /**
     * Delete the address book
     *
     * @see name=MAB3
     * @param name
     *            Address book name
     * @return The result
     */
    public String deleteAddressBooks() {
        String status = "Failed";
        final String searchStr = inputCmd.get("name");
        final List<JAddressBook> books = addressBookManagerHandler
                .getAddressBooks(userToken, searchStr);
        final JWebResult result = addressBookManagerHandler
                .deleteAddressBooks(userToken, books);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Delete addressbook " + searchStr + " successfully.");
        } else {
            logger.error("Failed to delete address book " + searchStr);
            status = "Failed";
        }
        return status;
    }

    /**
     * Find the address book
     *
     * @see name=MAB1
     * @param name
     *            Address book name to find
     * @return SUCCESS if there is address book with specified name
     */
	public String findAddressBook() {
		String status = "Failed";
		final String searchStr = inputCmd.get("name");
		try {
			final JAddressBook addressBook = addressBookManagerHandler
					.getAddressBookByName(userToken, searchStr);
			if (addressBook != null) {
				status = "SUCCESS";
				logger.info("Search addressbook by name " + searchStr
						+ " successfully.");
			} else {
				logger.error("Fail to search addressbook by name " + searchStr);
				status = "Failed";
			}
		} catch (final Exception e) {
			e.printStackTrace();
			status = "Failed, got exception when get address book. Error msg is "
					+ e.getMessage();
		}
		return status;
	}

    /**
     * Get specific detail information from the GAB entries
     *
     * @return
     */
    public String getGlobalAddressBookSpecificDetail() {
        final String result = "NotFound";
        final String keyword = inputCmd.get("keyword");
        final String ipAddress = inputCmd.get("ipAddress");
        final String gabDisplayName = inputCmd.get("gabDisplayName");
        final List<JGabEntry> gabList = addressBookManagerHandler
                .getGlobalAddressBook(userToken);
        for (final JGabEntry gab : gabList) {
            if ((gab.getIpAddress() != null
                    && gab.getIpAddress().equals(ipAddress))
                    || gab.getGabDisplayName().equals(gabDisplayName)) {
                return CommonUtils.invokeGetMethod(gab, keyword);
            }
        }
        return result;
    }

    @Override
    protected void injectCmdArgs() {
        inputCmd.put("name", "");
        inputCmd.put("newname", "");
        inputCmd.put("name4copy", "");
        inputCmd.put("descriptioin", "automation");
        inputCmd.put("newdescription", "automation");
        inputCmd.put("descriptioin4copy", "automation");
        inputCmd.put("userSearchStr", "");
        inputCmd.put("tier", "");
        inputCmd.put("user1", "");
        inputCmd.put("user2", "");
        inputCmd.put("user3", "");
        inputCmd.put("user4", "");
        inputCmd.put("user5", "");
        inputCmd.put("group1", "");
        inputCmd.put("group2", "");
        inputCmd.put("group3", "");
        inputCmd.put("room1", "");
        inputCmd.put("room2", "");
        inputCmd.put("room3", "");
        inputCmd.put("room4", "");
        inputCmd.put("room5", "");
        inputCmd.put("room6", "");
        inputCmd.put("room7", "");
        inputCmd.put("endpoint1", "");
        inputCmd.put("endpoint2", "");
        inputCmd.put("endpoint3", "");
        inputCmd.put("guest1", "");
        inputCmd.put("guest2", "");
        inputCmd.put("guest3", "");
        inputCmd.put("guest4", "");
        inputCmd.put("guest5", "");
        inputCmd.put("priority", "");
        inputCmd.put("keyword", "");
    }

    /**
     * Set the specified address book as default
     *
     * @see name=MAB3
     * @param name
     *            Address book name
     * @return The result
     */
    public String setDefaultAddressBook() {
        String status = "Failed";
        if (!inputCmd.get("name").isEmpty()) {
            final JAddressBookSpecifier abs = new JAddressBookSpecifier();
            if (inputCmd.get("name").equalsIgnoreCase("all")) {
                abs.setAddressBookAssignment(JAddressBookAssignment.ALL);
                abs.setAddressBookId(0);
            } else if (inputCmd.get("name").equalsIgnoreCase("none")) {
                abs.setAddressBookAssignment(JAddressBookAssignment.NONE);
                abs.setAddressBookId(-1);
            } else {
                final JAddressBook addressBook = addressBookManagerHandler
                        .getAddressBookByName(userToken, inputCmd.get("name"));
                if (addressBook != null) {
                    abs.setAddressBookAssignment(JAddressBookAssignment.SPECIFIC);
                    abs.setAddressBookId(addressBook.getUgpId());
                } else {
                    logger.error("Fail to search addressbook by name "
                            + inputCmd.get("name"));
                    status = "Failed";
                    return status + " Fail to search addressbook by name "
                            + inputCmd.get("name");
                }
            }
            if (addressBookManagerHandler.setDefaultAddressBook(userToken, abs)
                    .getStatus().equals(JStatus.SUCCESS)) {
                status = "SUCCESS";
                logger.info("Set default address book " + inputCmd.get("name")
                        + " successfully.");
            } else {
                status = "Failed";
                logger.error("Failed to set the address book"
                        + inputCmd.get("name") + " as default address book.");
                return status + " Failed to set the address book"
                        + inputCmd.get("name") + " as default address book.";
            }
        } else {
            status = "Failed";
            logger.error("Cannot find the address book name in the command. "
                    + "Please provide a valid address book name in the input command.");
            return status
                    + " Cannot find the address book name in the command. "
                    + "Please provide a valid address book name in the input command.";
        }
        return status;
    }

    /**
     * Update the specified address book. Have to provide all the members need
     * to associate to the address book when trying to update it.
     *
     * @see name=MAB3 <br/>
     *      tier=5 <br/>
     *      user1=luser7|2 <br/>
     *      user2=aduser6|1|AD <br/>
     *      room1=adroom3|1 <br/>
     *      room2=lroom5|2 <br/>
     *      room3=adroom4|4 <br/>
     *      room4=lroom4|5 <br/>
     *      guest1=h323AnnexO|1 <br/>
     *      guest2=h323e164|5 <br/>
     *      guest3=h323ip|4
     * @param name
     *            The address book name
     * @param newname
     *            The new name(Optional)
     * @param newdescription
     *            The new description(Optional)
     * @param tier
     *            [0-5] The tier number of address book
     * @param user
     *            [1-5] The user name. e.g. luser1|2 means add luser1 to tier2,
     *            no tier means add to root tier
     * @param guest
     *            [1-5] The guest name. e.g. guest1|3 means add guest1 to tier3,
     *            no tier means add to root tier
     * @param group
     *            [1-3] The group name. e.g. group1|2 means add group1 to tier2,
     *            no tier means add to root tier
     * @param room
     *            [1-7] The room name. e.g. room1|2 means add room to tier2, no
     *            tier means add to root tier
     * @param endpoint
     *            [1-3] The endpoint name. e.g. ep1|2 means add ep1 to tier2, no
     *            tier means add to root tier
     * @return The result
     */
    public String updateAddressBook() {
        String status = "Failed";
        final GroupManagerHandler gmh = new GroupManagerHandler(webServiceUrl);
        final DeviceManagerHandler dmh = new DeviceManagerHandler(
                webServiceUrl);
        if (!inputCmd.get("name").isEmpty()) {
            final JAddressBook addressBook = addressBookManagerHandler
                    .getAddressBookByName(userToken, inputCmd.get("name"));
            // update the MAB name
            if (!inputCmd.get("newname").isEmpty()) {
                addressBook.setName(inputCmd.get("newname"));
            }
            // update the MAB description
            if (!inputCmd.get("newdescription").isEmpty()) {
                addressBook.setDescription(inputCmd.get("newdescription"));
            }
            final Map<String, JAddressBookTier> tierMap = new HashMap<String, JAddressBookTier>();
            addressBook.getTiers().clear();
            addressBook.getEndpoints().clear();
            addressBook.getGroups().clear();
            addressBook.getRooms().clear();
            addressBook.getUsers().clear();
            addressBook.getGuests().clear();
            int tierNumber = 0;
            final String value = inputCmd.get("tier");
            if (!value.isEmpty() && value.matches("[012345]")) {
                tierNumber = Integer.parseInt(value);
                for (int i = 1; i <= tierNumber; i++) {
                    final JAddressBookTier tier = new JAddressBookTier();
                    tier.setName("Tier" + i);
                    tierMap.put(i + "", tier);
                }
            }
            // Update the tiers
            if (tierMap != null) {
                // Create users and add users to specified tier
                final List<String> userSearchStr = new ArrayList<String>();
                userSearchStr.add(inputCmd.get("user1"));
                userSearchStr.add(inputCmd.get("user2"));
                userSearchStr.add(inputCmd.get("user3"));
                userSearchStr.add(inputCmd.get("user4"));
                userSearchStr.add(inputCmd.get("user5"));
                for (final String searchStr : userSearchStr) {
                    if (searchStr != null && !searchStr.isEmpty()) {
                        final String[] args = searchStr.split("\\|");
                        List<JUser> users = null;
                        if (args.length < 3) {
                            users = umh.getUsers(userToken, args[0], "local");
                        } else {
                            users = umh.getUsers(userToken, args[0], args[2]);
                        }
                        if (args.length < 2) {
                            logger.error("The tier for the user" + args[0]
                                    + " must be specified, 0 means root");
                            return status;
                        }
                        if (Integer.parseInt(args[1]) > tierMap.size()) {
                            logger.error("The spcefied user tier should be less than tier number, tier number="
                                    + tierMap.size() + ", user tier="
                                    + args[1]);
                            return status;
                        }
                        if (!args[1].trim().equals("0")) {
                            // add users to specified tier
                            addUser(users, tierMap.get(args[1]).getUsers());
                        } else {
                            // add users to root
                            addUser(users, addressBook.getUsers());
                        }
                    }
                }
                // Create guests and add guests to specified tier
                final List<String> guestSearchStr = new ArrayList<String>();
                guestSearchStr.add(inputCmd.get("guest1"));
                guestSearchStr.add(inputCmd.get("guest2"));
                guestSearchStr.add(inputCmd.get("guest3"));
                guestSearchStr.add(inputCmd.get("guest4"));
                guestSearchStr.add(inputCmd.get("guest5"));
                for (final String searchStr : guestSearchStr) {
                    if (searchStr != null && !searchStr.isEmpty()) {
                        final String[] args = searchStr.split("\\|");
                        final List<JConfGuest> guests = umh
                                .searchGuest(userToken, args[0]);
                        if (args.length == 2) {
                            if (Integer.parseInt(args[1]) > tierMap.size()) {
                                logger.error("The spcefied guest tier should be less than tier number, tier number="
                                        + tierMap.size() + ", guest tier="
                                        + args[1]);
                                return status;
                            }
                            // add users to specified tier
                            addGuest(guests, tierMap.get(args[1]).getGuests());
                        } else {
                            // add users to root
                            addGuest(guests, addressBook.getGuests());
                        }
                    }
                }
                // Add group to tier
                gmh.getVisibleGroups(userToken);
                final List<String> groupSearchStr = new ArrayList<String>();
                groupSearchStr.add(inputCmd.get("group1"));
                groupSearchStr.add(inputCmd.get("group2"));
                groupSearchStr.add(inputCmd.get("group3"));
                for (final String searchStr : groupSearchStr) {
                    if (searchStr != null && !searchStr.isEmpty()) {
                        final String[] args = searchStr.split("\\|");
                        final List<JGroup> allGroups = gmh
                                .getVisibleGroups(userToken);
                        final List<JGroup> groups = new ArrayList<JGroup>();
                        for (final JGroup g : allGroups) {
                            if (g.getDisplayname().equalsIgnoreCase(args[0])) {
                                groups.add(g);
                            }
                        }
                        if (args.length == 2) {
                            if (Integer.parseInt(args[1]) > tierMap.size()) {
                                logger.error("The spcefied group tier should be less than tier number, tier number="
                                        + tierMap.size() + ", group tier="
                                        + args[1]);
                                return status;
                            }
                            // add groups to specified tier
                            addGroup(groups, tierMap.get(args[1]).getGroups());
                        } else {
                            // add groups to root
                            addGroup(groups, addressBook.getGroups());
                        }
                    }
                }
                // Add rooms to specified tier
                final List<String> roomSearchStr = new ArrayList<String>();
                roomSearchStr.add(inputCmd.get("room1"));
                roomSearchStr.add(inputCmd.get("room2"));
                roomSearchStr.add(inputCmd.get("room3"));
                roomSearchStr.add(inputCmd.get("room4"));
                roomSearchStr.add(inputCmd.get("room5"));
                roomSearchStr.add(inputCmd.get("room6"));
                roomSearchStr.add(inputCmd.get("room7"));
                for (final String searchStr : roomSearchStr) {
                    if (searchStr != null && !searchStr.trim().isEmpty()) {
                        final String[] args = searchStr.split("\\|");
                        final List<JRoom> rooms = umh.getRooms(userToken,
                                                               args[0]);
                        if (args.length == 2) {
                            if (Integer.parseInt(args[1]) > tierMap.size()) {
                                logger.error("The spcefied room tier should be less than tier number, tier number="
                                        + tierMap.size() + ", room tier="
                                        + args[1]);
                                return status;
                            }
                            // add rooms to specified tier
                            addRoom(rooms, tierMap.get(args[1]).getRooms());
                        } else {
                            // add rooms to root
                            addRoom(rooms, addressBook.getRooms());
                        }
                    }
                }
                // Add endpoints to specified tier
                final List<String> endpointSearchStr = new ArrayList<String>();
                endpointSearchStr.add(inputCmd.get("endpoint1"));
                endpointSearchStr.add(inputCmd.get("endpoint2"));
                endpointSearchStr.add(inputCmd.get("endpoint3"));
                for (final String searchStr : endpointSearchStr) {
                    if (searchStr != null && !searchStr.trim().isEmpty()) {
                        final String[] args = searchStr.split("\\|");
                        final List<JEndpointForList> endpoints = dmh
                                .getUnassignedDynamicDevices(userToken,
                                                             args[0]);
                        if (args.length == 2) {
                            if (Integer.parseInt(args[1]) > tierMap.size()) {
                                logger.error("The spcefied endpoint tier should be less than tier number, tier number="
                                        + tierMap.size() + ", endpoint tier="
                                        + args[1]);
                                return status;
                            }
                            // add endpoints to specified tier
                            addEndpoint(endpoints,
                                        tierMap.get(args[1]).getEndpoints());
                        } else {
                            // add endpoints to root
                            addEndpoint(endpoints, addressBook.getEndpoints());
                        }
                    }
                }
                // Add tier to address book
                for (final String key : tierMap.keySet()) {
                    if (key.equals("1")) {
                        addressBook.getTiers().add(tierMap.get(key));
                    } else {
                        final int tierNum = Integer.parseInt(key) - 1;
                        final List<JAddressBookTier> tierList = tierMap
                                .get(tierNum + "").getTiers();
                        tierList.add(tierMap.get(key));
                    }
                }
                if (addressBookManagerHandler
                        .updateAddressBook(userToken, addressBook).getStatus()
                        .equals(JStatus.SUCCESS)) {
                    status = "SUCCESS";
                    logger.info("Update the address book "
                            + inputCmd.get("name") + " successfully.");
                } else {
                    status = "Failed";
                    logger.error("Failed to update the address book "
                            + inputCmd.get("name"));
                    return status + " Failed to update the address book "
                            + inputCmd.get("name");
                }
            }
        } else {
            status = "Failed";
            logger.error("Cannot find the address book name in the command. "
                    + "Please provide a valid address book name in the input command.");
            return status
                    + " Cannot find the address book name in the command. "
                    + "Please provide a valid address book name in the input command.";
        }
        return status;
    }

    /**
     * Update the address book priority
     *
     * @see name=MAB3<br/>
     *      priority=1
     * @param name
     *            The address book name
     * @param priority
     *            Int value, the priority
     * @return The result
     */
    public String updateAddressBookPriorities() {
        String status = "Failed";
        int priority = 0;
        if (!inputCmd.get("name").isEmpty()
                && !inputCmd.get("priority").isEmpty()) {
            final List<Integer> orderedAddressBookIds = new ArrayList<Integer>();
            // get the list of the address books it contains the priority
            // already as the ascending style
            final List<JAddressBook> ablist = addressBookManagerHandler
                    .getAddressBooks(userToken);
            for (final JAddressBook ab : ablist) {
                orderedAddressBookIds.add(ab.getUgpId());
                if (ab.getDisplayname().equals(inputCmd.get("name"))) {
                    priority = ab.getPriority();
                }
            }
            if (!(Integer
                    .valueOf(inputCmd.get("priority")) > orderedAddressBookIds
                            .size())) {
                // the current priority is the current place where the address
                // book is
                // just switch the positions
                final int currentUgpid = orderedAddressBookIds
                        .get(priority - 1);
                final int ugpidtobereplace = orderedAddressBookIds
                        .get(Integer.valueOf(inputCmd.get("priority")) - 1);
                orderedAddressBookIds.set(priority - 1, ugpidtobereplace);
                orderedAddressBookIds.set(
                                          Integer.valueOf(inputCmd
                                                  .get("priority")) - 1,
                                          currentUgpid);
                if (addressBookManagerHandler
                        .updateAddressBookPriorities(userToken,
                                                     orderedAddressBookIds)
                        .getStatus().equals(JStatus.SUCCESS)) {
                    status = "SUCCESS";
                    logger.info("Update the address book "
                            + inputCmd.get("name") + " priority to "
                            + inputCmd.get("priority") + " successful.");
                } else {
                    status = "Failed";
                    logger.error("Failed to update the address book "
                            + inputCmd.get("name") + " priority to "
                            + inputCmd.get("priority"));
                    return status + " Failed to update the address book "
                            + inputCmd.get("name") + " priority to "
                            + inputCmd.get("priority");
                }
            } else {
                status = "Failed";
                logger.error("Cannot set the priority as biger as the total size of the address books.");
                return status
                        + " Cannot set the priority as biger as the total size of the address books.";
            }
        } else {
            status = "Failed";
            logger.error("Cannot find the address book name or expected priority in the input command.");
            return status
                    + " Cannot find the address book name or expected priority in the input command.";
        }
        return status;
    }
}
