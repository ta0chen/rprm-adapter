package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.ws.Holder;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.AddressBookManagerHandler;
import com.polycom.sqa.xma.webservices.driver.GroupManagerHandler;
import com.polycom.webservices.AddressBookManager.JAddressBook;
import com.polycom.webservices.GroupManager.JAddressBookAssignment;
import com.polycom.webservices.GroupManager.JAddressBookSpecifier;
import com.polycom.webservices.GroupManager.JEnterpriseType;
import com.polycom.webservices.GroupManager.JGroup;
import com.polycom.webservices.GroupManager.JGroupType;
import com.polycom.webservices.GroupManager.JRole;
import com.polycom.webservices.GroupManager.JStatus;
import com.polycom.webservices.GroupManager.JUser;
import com.polycom.webservices.GroupManager.JUsersAndGroups;
import com.polycom.webservices.GroupManager.JWebResult;

/**
 * Group handler. This class will handle the webservice request of Group module
 *
 * @author wbchao
 *
 */
public class GroupHandler extends XMAWebServiceHandler {
    public static List<JRole> ALL_ROLES = new ArrayList<>();
    GroupManagerHandler       gmh;

    public GroupHandler(final String cmd) throws IOException {
        super(cmd);
        gmh = new GroupManagerHandler(webServiceUrl);
        if (ALL_ROLES.isEmpty()) {
            ALL_ROLES = gmh.getAllRoles(userToken);
        }
    }

    /**
     * Create the local group
     *
     * @see groupName=lgroup1 <br/>
     *      localGroupDescription=Auto~Group1~Description <br/>
     *      enterpriseDirViewable=True <br/>
     *      groupMember1=luser1 <br/>
     *      groupMember1Type=user <br/>
     *      groupMember2=luser2 <br/>
     *      groupMember2Type=user <br/>
     *      groupMember3=lroom1 <br/>
     *      groupMember3Type=room <br/>
     *      groupMember4=adroom2 <br/>
     *      groupMember4Type=room <br/>
     *      addressBookName=Default
     *
     *
     * @param groupName
     *            The group name
     * @param enterpriseDirViewable
     *            Wheter enable the enterprise dir view
     * @param groupMember
     *            [1-4] The username to add to this group
     * @param groupMember
     *            [1-4]Type The type of user
     * @param addressBookName
     *            The address book name to add
     * @return The result
     */
    public String createLocalGroup() {
        String status = "Failed";
        final String groupName = getInputCmd().get("groupName");
        if (groupName.isEmpty()) {
            status = "Failed";
            logger.error("Please provide a group name in the input command");
            return status + " Please provide a group name in the input command";
        }
        final JGroup group = new JGroup();
        group.setGroupName(groupName);
        group.setEnterpriseType(JEnterpriseType.LOCAL);
        group.setGroupType(JGroupType.LDAP);
        final String localGroupDescription = getInputCmd()
                .get("localGroupDescription");
        group.setGroupDesc(localGroupDescription);
        // set the enterprise viewable flag. Default value is false if does
        // not set it
        final String enterpriseDirViewable = getInputCmd()
                .get("enterpriseDirViewable");
        if (!enterpriseDirViewable.isEmpty()) {
            final boolean isVisible = Boolean
                    .parseBoolean(enterpriseDirViewable);
            group.setVisible(isVisible);
        }
        // get the group members
        final JUsersAndGroups members = new JUsersAndGroups();
        for (int i = 1; i <= 10; i++) {
            final String groupMember = getInputCmd().get("groupMember" + i);
            final String groupMemberType = getInputCmd()
                    .get("groupMember" + i + "Type");
            if (groupMember == null || groupMember.isEmpty()) {
                continue;
            }
            final JUsersAndGroups userAndGroups = gmh
                    .searchForAvailableGroupMembers(userToken, groupMember);
            if (groupMemberType.equalsIgnoreCase("group")) {
                members.getGroups().addAll(userAndGroups.getGroups());
            } else if (groupMemberType.equalsIgnoreCase("user")) {
                members.getUsers().addAll(userAndGroups.getUsers());
            } else if (groupMemberType.equalsIgnoreCase("room")) {
                members.getUsers().addAll(userAndGroups.getUsers());
            }
        }
        // choose the address book
        final JAddressBookSpecifier abs = new JAddressBookSpecifier();
        final String addressBookName = getInputCmd().get("addressBookName");
        if (!addressBookName.isEmpty()) {
            final AddressBookManagerHandler abmh = new AddressBookManagerHandler(
                    webServiceUrl);
            for (final JAddressBook addressbook : abmh
                    .getLeanAddressBooks(userToken)) {
                if (addressbook.getDisplayname()
                        .equalsIgnoreCase(addressBookName)) {
                    abs.setAddressBookAssignment(JAddressBookAssignment.SPECIFIC);
                    abs.setAddressBookDn(addressbook.getDN());
                    abs.setAddressBookId(addressbook.getAddressBookSpecifier()
                            .getAddressBookId());
                    break;
                }
            }
        }
        group.setAddressBookSpecifier(abs);
        // Set roles
        final String roleNames = getInputCmd().get("roleNames");
        final List<JRole> roles = getRoles(roleNames);
        group.getAssociatedRoles().addAll(roles);
        // add local group
        final JWebResult result = gmh.addLocalGroup(userToken, group, members);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Create local group " + groupName + " successfully.");
        } else {
            status = "Failed";
            logger.error("Failed to create local group " + groupName
                    + "errorMsg is: " + result.getMessages());
            return status + " Failed to create local group " + groupName
                    + "errorMsg is: " + result.getMessages();
        }
        return status;
    }

    /**
     * Delete the group
     *
     * @see groupName=$ciscoGroup
     *
     * @param groupName
     *            The group name
     * @return The result
     */
    public String deleteGroup() {
        String status = "Failed";
        if (!getInputCmd().get("groupName").isEmpty()) {
            final List<JGroup> groups = gmh.getVisibleGroups(userToken);
            for (final JGroup group : groups) {
                if (group.getGroupName()
                        .equals(getInputCmd().get("groupName"))) {
                    final List<Integer> grpList = new LinkedList<Integer>();
                    grpList.add(group.getDbKey());
                    if (gmh.deleteGroups(userToken, grpList).getStatus()
                            .equals(JStatus.SUCCESS)) {
                        status = "SUCCESS";
                        logger.info("Group " + getInputCmd().get("groupName")
                                + " removed successfully.");
                    } else {
                        status = "Failed";
                        logger.error("Failed to remove the group "
                                + getInputCmd().get("groupName"));
                        return status + " Failed to remove the group "
                                + getInputCmd().get("groupName");
                    }
                }
            }
        } else {
            status = "Failed";
            logger.error("Please provide a group name for removal in the input command");
            return status
                    + " Please provide a group name for removal in the input command";
        }
        return status;
    }

    /**
     * Internal method, get the group by name
     *
     * @param groupName
     * @return JGroup
     */
    private JGroup getGroupByName(final String groupName) {
        final List<JGroup> groups = gmh.getVisibleGroups(userToken);
        for (final JGroup group : groups) {
            if (group.getGroupName().trim().equals(groupName)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Get the group member specified attribute value
     *
     * @see groupName=$memberName <br/>
     *      keyword=modelName
     *
     * @param groupName
     *            The group name
     * @param keyword
     *            The attribute name
     * @return The group member specified attribute value
     */
    public String getGroupMemberSpecific() {
        final String groupName = inputCmd.get("groupName").replaceAll("~", " ");
        final String keyword = inputCmd.get("keyword");
        final JGroup group = getGroupByName(groupName);
        if (group == null) {
            return "NotFound, could not find group named " + groupName;
        }
        final String groupDN = "ugpId=" + group.getUgpId()
                + ",ou=local,ou=users,ou=readimanager";
        final JUsersAndGroups members = gmh.getGroupMembers(userToken, groupDN);
        return CommonUtils.invokeGetMethod(members, keyword);
    }

    /**
     * Get the group specified attribute value
     *
     * @see groupName=$groupName <br/>
     *      keyword=modelName
     *
     * @param groupName
     *            The group name
     * @param keyword
     *            The attribute name
     * @return The group specified attribute value
     */
    public String getGroupSpecific() {
        final String groupName = inputCmd.get("groupName").replaceAll("~", " ");
        final String keyword = inputCmd.get("keyword");
        final JGroup group = getGroupByName(groupName);
        return CommonUtils.invokeGetMethod(group, keyword);
    }

    /**
     * Internal method, get the roles by name
     *
     * @param roleNames
     * @return Role list
     */
    private List<JRole> getRoles(final String roleNames) {
        final List<JRole> roles = new ArrayList<JRole>();
        if (roleNames.equalsIgnoreCase("all")) {
            return ALL_ROLES;
        }
        final String[] roleStrs = roleNames.replaceAll("~", " ").split(",");
        for (final JRole role : ALL_ROLES) {
            for (final String roleName : roleStrs) {
                if (role.getGroupName().equalsIgnoreCase(roleName)) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }

    /**
     * Import the LDAP group to XMA
     *
     * @see groupName=adgroup2
     *
     * @param groupName
     *            The LDAP group name
     * @return The result
     */
    public String importLDAPGroup() {
        String status = "Failed";
        final String groupName = getInputCmd().get("groupName");
        if (groupName.isEmpty()) {
            status = "Failed";
            logger.error("Please provide a LDAP group name in the input command for importing");
            return status
                    + " Please provide a LDAP group name in the input command for importing";
        }
        final List<JGroup> groups = gmh
                .searchForAvailableEnterpriseGroupsForImport(userToken,
                                                             groupName);
        JWebResult result = gmh.importEnterpriseGroups(userToken, groups);
        if (result.getStatus()
                .equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("LDAP group " + groupName
                    + " is successfully imported into XMA.");
        } else {
            status = "Failed";
            logger.error("Failed to import the LDAP group " + groupName
                    + " into XMA.");
            return status + " Failed to import the LDAP group " + groupName
                    + " into XMA.";
        }
        return status;
    }

    @Override
    protected void injectCmdArgs() {
        // Local group name. LDAP or local.
        put("groupName", "");
        // Local group description
        put("localGroupDescription", "");
        // The address book the group belongs to
        put("addressBookName", "");
        // The flag for the enterprise directory viewable check, True or
        // False. Default value is False.
        put("enterpriseDirViewable", "");
        put("roleNames", "");
    }

    /**
     * Update the LDAP group
     *
     * @see groupName=adgroup1 <br/>
     *      enterpriseDirViewable=true <br/>
     *      addressBookName=MAB1
     *
     * @param groupName
     *            The group name
     * @param enterpriseDirViewable
     *            Wheter enable the enterprise dir view
     * @param groupMember
     *            [1-4] The username to add to this group
     * @param groupMember
     *            [1-4]Type The type of user
     * @param addressBookName
     *            The address book name to add
     * @return The result
     */
    public String updateLDAPGroup() {
        String status = "Failed";
        final String groupName = getInputCmd().get("groupName");
        if (groupName.isEmpty()) {
            status = "Failed";
            logger.error("Please provide a group name in the input command");
            return status + " Please provide a group name in the input command";
        }
        final JGroup groupToUpdate = getGroupByName(groupName);
        if (groupToUpdate == null) {
            return "Failed, could not find the group named " + groupName;
        }
        // update the address book
        final String addressBookName = getInputCmd().get("addressBookName");
        if (!addressBookName.isEmpty()) {
            final JAddressBookSpecifier abs = new JAddressBookSpecifier();
            final AddressBookManagerHandler abmh = new AddressBookManagerHandler(
                    webServiceUrl);
            for (final JAddressBook addressbook : abmh
                    .getLeanAddressBooks(userToken)) {
                if (addressbook.getDisplayname()
                        .equalsIgnoreCase(addressBookName)) {
                    abs.setAddressBookAssignment(JAddressBookAssignment.SPECIFIC);
                    abs.setAddressBookDn(addressbook.getDN());
                    abs.setAddressBookId(addressbook.getAddressBookSpecifier()
                            .getAddressBookId());
                } else if (addressBookName.equalsIgnoreCase("None")) {
                    abs.setAddressBookAssignment(JAddressBookAssignment.NONE);
                    abs.setAddressBookId(-1);
                } else if (addressBookName.equalsIgnoreCase("All")) {
                    abs.setAddressBookAssignment(JAddressBookAssignment.ALL);
                    abs.setAddressBookId(0);
                } else if (addressBookName.equalsIgnoreCase("Default")) {
                    abs.setAddressBookAssignment(JAddressBookAssignment.DEFAULT);
                    abs.setAddressBookId(-2);
                }
            }
            groupToUpdate.setAddressBookSpecifier(abs);
        }
        // update the enterprise viewable flag
        final String enterpriseDirViewable = getInputCmd()
                .get("enterpriseDirViewable");
        if (!enterpriseDirViewable.isEmpty()) {
            final boolean isVisible = Boolean
                    .parseBoolean(enterpriseDirViewable);
            groupToUpdate.setVisible(isVisible);
        }
        // update the role according to the input command
        // get the available roles for later usage
        // get the role1 according to roleName1
        final String roleNames = getInputCmd().get("roleNames");
        if (!roleNames.isEmpty()) {
            final List<JRole> roles = getRoles(roleNames);
            groupToUpdate.getAssociatedRoles().addAll(roles);
        }
        // update the LDAP group
        final JWebResult result = gmh.updateEnterpriseGroup(userToken,
                                                            groupToUpdate);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("LDAP group " + getInputCmd().get("groupName")
                    + " is updated successfully.");
        } else {
            status = "Failed";
            logger.error("Failed to update the LDAP group "
                    + getInputCmd().get("groupName"));
            return status + " Failed to update the LDAP group "
                    + getInputCmd().get("groupName");
        }
        return status;
    }

    /**
     * Update the local group
     *
     * @see groupName=adgroup1 <br/>
     *      enterpriseDirViewable=true <br/>
     *      addressBookName=MAB1
     *
     * @param groupName
     *            The group name
     * @param enterpriseDirViewable
     *            Wheter enable the enterprise dir view
     * @param groupMember
     *            [1-4] The username to add to this group
     * @param groupMember
     *            [1-4]Type The type of user
     * @param addressBookName
     *            The address book name to add
     * @return The result
     */
    public String updateLocalGroup() {
        String status = "Failed";
        final String groupName = getInputCmd().get("groupName");
        if (groupName.isEmpty()) {
            status = "Failed";
            logger.error("Please provide a group name in the input command");
            return status + " Please provide a group name in the input command";
        }
        final JGroup groupToUpdate = getGroupByName(groupName);
        if (groupToUpdate == null) {
            return "Failed, could not find the group named " + groupName;
        }
        // update the address book
        final String addressBookName = getInputCmd().get("addressBookName");
        if (!addressBookName.isEmpty()) {
            final JAddressBookSpecifier abs = new JAddressBookSpecifier();
            final AddressBookManagerHandler abmh = new AddressBookManagerHandler(
                    webServiceUrl);
            for (final JAddressBook addressbook : abmh
                    .getLeanAddressBooks(userToken)) {
                if (addressbook.getDisplayname().equals(addressBookName)) {
                    abs.setAddressBookAssignment(JAddressBookAssignment.SPECIFIC);
                    abs.setAddressBookDn(addressbook.getDN());
                    abs.setAddressBookId(addressbook.getAddressBookSpecifier()
                            .getAddressBookId());
                } else if (addressBookName.equalsIgnoreCase("None")) {
                    abs.setAddressBookAssignment(JAddressBookAssignment.NONE);
                    abs.setAddressBookId(-1);
                } else if (addressBookName.equalsIgnoreCase("All")) {
                    abs.setAddressBookAssignment(JAddressBookAssignment.ALL);
                    abs.setAddressBookId(0);
                } else if (addressBookName.equalsIgnoreCase("Default")) {
                    abs.setAddressBookAssignment(JAddressBookAssignment.DEFAULT);
                    abs.setAddressBookId(-2);
                }
            }
            groupToUpdate.setAddressBookSpecifier(abs);
        }
        // update the enterprise viewable flag
        final String enterpriseDirViewable = getInputCmd()
                .get("enterpriseDirViewable");
        if (!enterpriseDirViewable.isEmpty()) {
            final boolean isVisible = Boolean
                    .parseBoolean(enterpriseDirViewable);
            groupToUpdate.setVisible(isVisible);
        }
        // update the role according to the input command
        // get the available roles for later usage
        // get the role1 according to roleName1
        // Set roles
        final String roleNames = getInputCmd().get("roleNames");
        if (!roleNames.isEmpty()) {
            final List<JRole> roles = getRoles(roleNames);
            groupToUpdate.getAssociatedRoles().clear();
            groupToUpdate.getAssociatedRoles().addAll(roles);
        }
        // update the group
        JWebResult result = gmh.updateLocalGroup(userToken, groupToUpdate);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Local group " + groupName + " updated successfully.");
        } else {
            status = "Failed";
            logger.error("Failed to update the local group " + groupName);
            return status + " Failed to update the local group " + groupName;
        }
        // set the group member according to the input command
        // get the group members
        // get the group members
        if (getInputCmd().get("groupMember1") == null) {
            return status;
        }
        final JUsersAndGroups members = new JUsersAndGroups();
        for (int i = 1; i <= 10; i++) {
            final String groupMember = getInputCmd().get("groupMember" + i);
            final String groupMemberType = getInputCmd()
                    .get("groupMember" + i + "Type");
            if (groupMember == null || groupMember.isEmpty()) {
                continue;
            }
            final JUsersAndGroups userAndGroups = gmh
                    .searchForAvailableGroupMembers(userToken, groupMember);
            if (groupMemberType.equalsIgnoreCase("group")) {
                members.getGroups().addAll(userAndGroups.getGroups());
            } else if (groupMemberType.equalsIgnoreCase("user")) {
                members.getUsers().addAll(userAndGroups.getUsers());
            } else if (groupMemberType.equalsIgnoreCase("room")) {
                members.getUsers().addAll(userAndGroups.getUsers());
            }
        }
        result = gmh.setGroupMembers(userToken,
                                     groupToUpdate.getUgpId(),
                                     members);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Local group " + groupName
                    + " has successfully set the group members.");
        } else {
            status = "Failed";
            logger.error("Failed to set the group members to local group "
                    + groupName);
            return status + " Failed to set the group members to local group "
                    + groupName;
        }
        return status;
    }
    /**
     * Add favorite list
     * 
     * @param favoriteMembers
     *        favoriteListName
     *            
     * @return favorite list ugpid
     */
	public String addFavoriteList() {
		String status = "Failed";

		final String[] favoriteMembers = inputCmd.get("favoriteMembers").split(
				",");
		final String favoriteListName = inputCmd.get("favoriteListName");
		final JUsersAndGroups members = new JUsersAndGroups();
		if (favoriteListName.isEmpty() || favoriteMembers.length == 0) {
			status = "Failed";
			logger.error("Please provide a group name for removal in the input command");
			return status
					+ " Please provide a group name for removal in the input command";
		}
		
		for (final String memberName : favoriteMembers) {
			for (final JUser user : gmh.searchForAvailableGroupMembers(
					userToken, memberName).getUsers()) {
				members.getUsers().add(user);
			}
		}
		
		if (members.getUsers().isEmpty() && members.getGroups().isEmpty()) {
			status = "Failed";
			logger.error("Did not search any member to add to favorite list");
			return status
					+ " Did not search any member to add to favorite list";
		}
		
		
		JGroup group = new JGroup();
		group.setDbKey(new Integer(0));
		group.setBelongsToAreaUgpId(0);
		group.setEnterpriseType(JEnterpriseType.LOCAL);
		group.setUgpId(new Integer(0));
		group.setGroupName(favoriteListName);
		group.setGroupType(JGroupType.LDAP);
		group.setIsDefaultGroup(false);
		group.setIsInherited(false);
		group.setVisible(false);
		group.setPolicyId(0);

		Holder<JGroup> groupHolder = new Holder<JGroup>();
		groupHolder.value = group;
		if (gmh.addFavoriteList(userToken, groupHolder, members).getStatus().equals(JStatus.SUCCESS)) {
			List<Integer> favoriteListUgpIds = new ArrayList<Integer>();
			favoriteListUgpIds.add(groupHolder.value.getUgpId());
			if (gmh.setUsersFavoriteList(userToken, favoriteListUgpIds).getStatus()
					.equals(JStatus.SUCCESS)) {
			status = "SUCCESS:" + groupHolder.value.getUgpId().intValue();
			logger.info("favoritelist " + getInputCmd().get("favoriteListName")
					+ " added successfully.");
			} else {
				status = "Failed";
				logger.error("Failed to add favoritelist " + favoriteListName);
				return status + " Failed to add favoritelist " + favoriteListName;
			}
		} else {
			status = "Failed";
			logger.error("Failed to add favoritelist " + favoriteListName);
			return status + " Failed to add favoritelist " + favoriteListName;
		}
		return status;
	}
	
    /**
     * Delete favorite list
     * 
     * @param favoriteListName
     *            
     * @return The result
     */
	public String deleteFavoriteList() {
		String status = "Failed";

		final String favoriteListId = getInputCmd().get("favoriteListId");
		if (favoriteListId.isEmpty()) {
			status = "Failed";
			logger.error("Please provide favoritelist id in the input command");
			return status
					+ " Please provide favoritelist id in the input command";
		}
		

                final List<Integer> grpList = new LinkedList<Integer>();
                grpList.add(Integer.valueOf(favoriteListId));
                if (gmh.deleteFavoriteList(userToken, grpList).getStatus()
                        .equals(JStatus.SUCCESS)) {
        			List<Integer> favoriteListUgpIds = new ArrayList<Integer>();
        			if (gmh.setUsersFavoriteList(userToken, favoriteListUgpIds).getStatus()
        					.equals(JStatus.SUCCESS)) {
        			status = "SUCCESS";
        			logger.info("favoritelist " + getInputCmd().get("favoriteListName")
        					+ " deleted successfully.");
        			} else {
        				status = "Failed";
        				logger.error("Failed to remove the favoritelist " + favoriteListId);
        				return status + " Failed to remove the favoritelist " + favoriteListId;
        			}
                } else {
                    status = "Failed";
                    logger.error("Failed to remove the FavoriteList "
                            + favoriteListId);
                    return status + " Failed to remove the FavoriteList "
                            + favoriteListId;
                }

		return status;
	}
	
    /**
     * Get favorite list members
     * 
     * @param favoriteListId
     *            
     * @return The result
     */
	public String getFavoriteListMembers() {
		String members = "";

		final String favoriteListId = getInputCmd().get("favoriteListId");
		if (favoriteListId.isEmpty()) {
			logger.error("Please provide favoritelist id in the input command");
			return "Please provide favoritelist id in the input command";
		}
		
		JUsersAndGroups favoriteMembers = gmh.getGroupMembers(userToken,"ugpId=" + getInputCmd().get("favoriteListId") + ",ou=local,ou=users,ou=readimanager");
		for (JUser user : favoriteMembers.getUsers()) {
			members = members.concat(user.getFirstName() + user.getLastName() + ",");
		}
		for (JGroup group : favoriteMembers.getGroups()) {
			members = members.concat(group.getGroupName() + ",");
		}
		members = members.substring(0, members.length() - 1);
		
		return members;
	}
}
