package com.polycom.sqa.xma.webservices.driver;

import java.net.URL;
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
import com.polycom.webservices.GroupManager.JCredentials;
import com.polycom.webservices.GroupManager.JGroup;
import com.polycom.webservices.GroupManager.JGroupManager;
import com.polycom.webservices.GroupManager.JGroupManager_Service;
import com.polycom.webservices.GroupManager.JGroupType;
import com.polycom.webservices.GroupManager.JPolicy;
import com.polycom.webservices.GroupManager.JRole;
import com.polycom.webservices.GroupManager.JUsersAndGroups;
import com.polycom.webservices.GroupManager.JWebResult;

/**
 * Group handler
 *
 * @author Tao Chen
 *
 */
public class GroupManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("GroupManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JGroupManager");
    JGroupManager              port;

    /**
     * Construction of the GroupHandler class
     */
    public GroupManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JGroupManager) jsonInvocationHandler
                    .getProxy(JGroupManager.class);
        } else {
            final URL wsdlURL = GroupManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JGroupManager.wsdl");
            final JGroupManager_Service ss = new JGroupManager_Service(wsdlURL,
                    SERVICE_NAME);
            port = ss.getJGroupManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext().put(
                                             BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                             webServiceUrl + "/JGroupManager");
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
     * Add favorite list
     *
     * @param userToken
     * @param group
     * @param groupMembers
     * @return
     */
    public JWebResult addFavoriteList(final String userToken, final Holder<JGroup> group, final JUsersAndGroups groupMembers) {
        System.out.println("Invoking addFavoriteList...");
        final JCredentials _addFavoriteList_credentials = new JCredentials();
        _addFavoriteList_credentials.setUserToken(userToken);
        final Holder<JGroup> _addFavoriteList_group = group;
        final JUsersAndGroups _addFavoriteList_groupMembers = groupMembers;
        final JWebResult _addFavoriteList__return = port.addFavoriteList(_addFavoriteList_credentials, _addFavoriteList_group, _addFavoriteList_groupMembers);
        System.out.println("addFavoriteList.result="
                + _addFavoriteList__return.getStatus().toString());
        return _addFavoriteList__return;
    }

    /**
     * Add a local group
     *
     * @param userToken
     * @param Groupname
     * @param description
     * @param isVisible
     * @param groupMembers
     * @param role
     * @return
     */
    public JWebResult addLocalGroup(final String userToken,
                                    final JGroup group,
                                    final JUsersAndGroups groupMembers) {
        System.out.println("Invoking addLocalGroup...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port.addLocalGroup(credentials,
                                                     group,
                                                     groupMembers);
        return result;
    }

    /**
     * Delete favorite list
     *
     * @param userToken
     * @param groupIds
     * @return
     */
    public JWebResult deleteFavoriteList(final String userToken, final List<Integer> groupIds) {
        System.out.println("Invoking deleteFavoriteList...");
        final JCredentials _deleteFavoriteList_credentials = new JCredentials();
        _deleteFavoriteList_credentials.setUserToken(userToken);
        final List<Integer> _deleteFavoriteList_groupIds = groupIds;
        final JWebResult _deleteFavoriteList__return = port.deleteFavoriteLists(_deleteFavoriteList_credentials, _deleteFavoriteList_groupIds);
        System.out.println("deleteFavoriteList.result="
                + _deleteFavoriteList__return);
        return _deleteFavoriteList__return;
    }
	
    /**
     * Delete specified groups
     *
     * @param userToken
     * @param groupIds
     * @return
     */
    public JWebResult deleteGroups(final String userToken,
                                   final List<Integer> groupIds) {
        System.out.println("Invoking deleteGroups...");
        logger.info("Invoking deleteGroups...");
        final JCredentials _deleteGroups_credentials = new JCredentials();
        _deleteGroups_credentials.setUserToken(userToken);
        final List<Integer> _deleteGroups_groupIds = groupIds;
        final JWebResult _deleteGroups__return = port
                .deleteGroups(_deleteGroups_credentials,
                              _deleteGroups_groupIds);
        System.out.println("deleteGroups.result=" + _deleteGroups__return);
        return _deleteGroups__return;
    }

    /**
     * Get available groups
     *
     * @param userToken
     * @param allGroups
     * @param allRoles
     * @return
     */
    public List<JRole> getAllRoles(final String userToken) {
        System.out.println("Invoking getVisibleGroups...");
        logger.info("Invoking getVisibleGroups...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JGroup>> groups = new Holder<List<JGroup>>();
        final Holder<Integer> groupCount = new Holder<Integer>();
        final Holder<List<JPolicy>> allProvisioningProfiles = new Holder<List<JPolicy>>();
        final Holder<List<JRole>> allRoles = new Holder<List<JRole>>();
        port.getVisibleGroups(credentials,
                              JGroupType.LDAP,
                              groups,
                              groupCount,
                              allProvisioningProfiles,
                              allRoles);
        return allRoles.value;
    }

    /**
     * Get the group members when trying to update a group
     *
     * @param userToken
     * @param groupDN
     * @return
     */
    public JUsersAndGroups getGroupMembers(final String userToken,
                                           final String groupDN) {
        System.out.println("Invoking getGroupMembers...");
        logger.info("Invoking getGroupMembers...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<JUsersAndGroups> groupMembers = new Holder<JUsersAndGroups>();
        port.getGroupMembers(credentials, groupDN, groupMembers);
        return groupMembers.value;
    }

    public List<JGroup> getVisibleGroups(final String userToken) {
        System.out.println("Invoking getVisibleGroups...");
        logger.info("Invoking getVisibleGroups...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JGroup>> groups = new Holder<List<JGroup>>();
        final Holder<Integer> groupCount = new Holder<Integer>();
        final Holder<List<JPolicy>> allProvisioningProfiles = new Holder<List<JPolicy>>();
        final Holder<List<JRole>> allRoles = new Holder<List<JRole>>();
        port.getVisibleGroups(credentials,
                              JGroupType.LDAP,
                              groups,
                              groupCount,
                              allProvisioningProfiles,
                              allRoles);
        return groups.value;
    }

    /**
     * Add LDAP groups to XMA
     *
     * @param userToken
     * @param groupsToImport
     * @return
     */
    public JWebResult
            importEnterpriseGroups(final String userToken,
                                   final List<JGroup> groupsToImport) {
        System.out.println("Invoking importEnterpriseGroups...");
        logger.info("Invoking importEnterpriseGroups...");
        final JCredentials _importEnterpriseGroups_credentials = new JCredentials();
        _importEnterpriseGroups_credentials.setUserToken(userToken);
        final List<JGroup> _importEnterpriseGroups_groupsToImport = groupsToImport;
        final JWebResult _importEnterpriseGroups__return = port
                .importEnterpriseGroups(_importEnterpriseGroups_credentials,
                                        _importEnterpriseGroups_groupsToImport);
        System.out.println("importEnterpriseGroups.result="
                + _importEnterpriseGroups__return);
        return _importEnterpriseGroups__return;
    }

    /**
     * Check if the group is in use
     *
     * @param userToken
     * @param groupId
     * @return
     */
    public boolean isSoftupdatesTestGroup(final String userToken,
                                          final int groupId) {
        System.out.println("Invoking isSoftupdatesTestGroup...");
        logger.info("Invoking isSoftupdatesTestGroup...");
        final JCredentials _isSoftupdatesTestGroup_credentials = new JCredentials();
        _isSoftupdatesTestGroup_credentials.setUserToken(userToken);
        final int _isSoftupdatesTestGroup_groupId = groupId;
        final Holder<Boolean> _isSoftupdatesTestGroup_inUse = new Holder<Boolean>();
        final JWebResult _isSoftupdatesTestGroup__return = port
                .isSoftupdatesTestGroup(_isSoftupdatesTestGroup_credentials,
                                        _isSoftupdatesTestGroup_groupId,
                                        _isSoftupdatesTestGroup_inUse);
        System.out.println("isSoftupdatesTestGroup.result="
                + _isSoftupdatesTestGroup__return);
        System.out
                .println("isSoftupdatesTestGroup._isSoftupdatesTestGroup_inUse="
                        + _isSoftupdatesTestGroup_inUse.value);
        return _isSoftupdatesTestGroup_inUse.value;
    }

    /**
     * Search for available LDAP group from XMA
     *
     * @param userToken
     * @param searchString
     * @return
     */
    public List<JGroup>
            searchForAvailableEnterpriseGroupsForImport(final String userToken,
                                                        final String searchString) {
        System.out
                .println("Invoking searchForAvailableEnterpriseGroupsForImport...");
        logger.info("Invoking searchForAvailableEnterpriseGroupsForImport...");
        final JCredentials _searchForAvailableEnterpriseGroupsForImport_credentials = new JCredentials();
        _searchForAvailableEnterpriseGroupsForImport_credentials
                .setUserToken(userToken);
        final String _searchForAvailableEnterpriseGroupsForImport_searchString = searchString;
        final Holder<List<JGroup>> _searchForAvailableEnterpriseGroupsForImport_groups = new Holder<List<JGroup>>();
        final JWebResult _searchForAvailableEnterpriseGroupsForImport__return = port
                .searchForAvailableEnterpriseGroupsForImport(_searchForAvailableEnterpriseGroupsForImport_credentials,
                                                             _searchForAvailableEnterpriseGroupsForImport_searchString,
                                                             _searchForAvailableEnterpriseGroupsForImport_groups);
        System.out.println("searchForAvailableEnterpriseGroupsForImport.result="
                + _searchForAvailableEnterpriseGroupsForImport__return);
        System.out
                .println("searchForAvailableEnterpriseGroupsForImport._searchForAvailableEnterpriseGroupsForImport_groups="
                        + _searchForAvailableEnterpriseGroupsForImport_groups.value);
        return _searchForAvailableEnterpriseGroupsForImport_groups.value;
    }

    /**
     * Search for available group members when trying to creating a group with
     * members. The member may be user, room or group.
     *
     * @param userToken
     * @param searchString
     * @return
     */
    public JUsersAndGroups
            searchForAvailableGroupMembers(final String userToken,
                                           final String searchString) {
        System.out.println("Invoking searchForAvailableGroupMembers...");
        logger.info("Invoking searchForAvailableGroupMembers...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final String groupDN = "";
        final Holder<JUsersAndGroups> groupMembers = new Holder<JUsersAndGroups>();
        port.searchForAvailableGroupMembers(credentials,
                                            groupDN,
                                            searchString,
                                            groupMembers);
        return groupMembers.value;
    }

    /**
     * Set favorite list members
     *
     * @param userToken
     * @param groupId
     * @param groupMembers
     * @return
     */
    public JWebResult
            setFavoriteListMembers(final String userToken,
                                   final int groupId,
                                   final JUsersAndGroups groupMembers) {
        System.out.println("Invoking setFavoriteListMembers...");
        final JCredentials _setFavoriteListMembers_credentials = new JCredentials();
        _setFavoriteListMembers_credentials.setUserToken(userToken);
        final int _setFavoriteListMembers_groupId = groupId;
        final JUsersAndGroups _setFavoriteListMembers_groupMembers = groupMembers;
        final JWebResult _setFavoriteListMembers__return = port
                .setFavoriteListMembers(_setFavoriteListMembers_credentials,
                                        _setFavoriteListMembers_groupId,
                                        _setFavoriteListMembers_groupMembers);
        System.out.println("setFavoriteListMembers.result="
                + _setFavoriteListMembers__return);
        return _setFavoriteListMembers__return;
    }

    /**
     * Set the group members when trying to update the group in this field
     *
     * @param userToken
     * @param groupId
     * @param groupMembers
     * @return
     */
    public JWebResult setGroupMembers(final String userToken,
                                      final int groupId,
                                      final JUsersAndGroups groupMembers) {
        System.out.println("Invoking setGroupMembers...");
        logger.info("Invoking setGroupMembers...");
        final JCredentials _setGroupMembers_credentials = new JCredentials();
        _setGroupMembers_credentials.setUserToken(userToken);
        final int _setGroupMembers_groupId = groupId;
        final JUsersAndGroups _setGroupMembers_groupMembers = groupMembers;
        final JWebResult _setGroupMembers__return = port
                .setGroupMembers(_setGroupMembers_credentials,
                                 _setGroupMembers_groupId,
                                 _setGroupMembers_groupMembers);
        System.out
                .println("setGroupMembers.result=" + _setGroupMembers__return);
        return _setGroupMembers__return;
    }
    /**
     * Add favorite list
     *
     * @param userToken
     * @param userUgpId
     * @param favoriteListUgpIds
     * @return
     */
    public JWebResult setUsersFavoriteList(final String userToken, final List<Integer> favoriteListUgpIds) {
        System.out.println("Invoking setUsersFavoriteList...");
        final JCredentials _setUsersFavoriteList_credentials = new JCredentials();
        _setUsersFavoriteList_credentials.setUserToken(userToken);
        final int _setUsersFavoriteList_userUgpId = 1;
        final List<Integer> _setUsersFavoriteList_favoriteListUgpIds = favoriteListUgpIds;
        final JWebResult _setUsersFavoriteList__return = port.setUsersFavoriteLists(_setUsersFavoriteList_credentials, _setUsersFavoriteList_userUgpId, _setUsersFavoriteList_favoriteListUgpIds);
        System.out.println("setUsersFavoriteList.result="
                + _setUsersFavoriteList__return);
        return _setUsersFavoriteList__return;
    }

    /**
     * Update the LDAP group
     *
     * @param userToken
     * @param group
     * @return
     */
    public JWebResult updateEnterpriseGroup(final String userToken,
                                            final JGroup group) {
        System.out.println("Invoking updateEnterpriseGroup...");
        logger.info("Invoking updateEnterpriseGroup...");
        final JCredentials _updateEnterpriseGroup_credentials = new JCredentials();
        _updateEnterpriseGroup_credentials.setUserToken(userToken);
        final JGroup _updateEnterpriseGroup_group = group;
        final JWebResult _updateEnterpriseGroup__return = port
                .updateEnterpriseGroup(_updateEnterpriseGroup_credentials,
                                       _updateEnterpriseGroup_group);
        System.out.println("updateEnterpriseGroup.result="
                + _updateEnterpriseGroup__return);
        return _updateEnterpriseGroup__return;
    }

    /**
     * Update the local group
     *
     * @param userToken
     * @param group
     * @return
     */
    public JWebResult updateLocalGroup(final String userToken,
                                       final JGroup group) {
        System.out.println("Invoking updateLocalGroup...");
        final JCredentials _updateLocalGroup_credentials = new JCredentials();
        _updateLocalGroup_credentials.setUserToken(userToken);
        final JGroup _updateLocalGroup_group = group;
        final JWebResult _updateLocalGroup__return = port
                .updateLocalGroup(_updateLocalGroup_credentials,
                                  _updateLocalGroup_group);
        System.out.println("updateLocalGroup.result="
                + _updateLocalGroup__return);
        return _updateLocalGroup__return;
    }
}
