package com.polycom.webservices;

import java.io.IOException;
import java.util.List;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.AccessControlListManagerHandler;
import com.polycom.sqa.xma.webservices.driver.GroupManagerHandler;
import com.polycom.webservices.AccessControlListManager.JAccessControlList;
import com.polycom.webservices.AccessControlListManager.JDeviceType;
import com.polycom.webservices.AccessControlListManager.JGroup;
import com.polycom.webservices.AccessControlListManager.JModel;
import com.polycom.webservices.AccessControlListManager.JStatus;
import com.polycom.webservices.AccessControlListManager.JWebResult;

public class AccessControlListHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "addAcl ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "name=autoACL     description=acldescription  deviceTypeList=RPD  groupNameList=localgroup";
        final String command = "https://10.220.202.6:8443/PlcmRmWeb/rest/JAccessControlListManager AccessControlListManager "
                + method + auth + params;
        final AccessControlListHandler handler = new AccessControlListHandler(
                command);
        handler.logger.info("hoho");
        handler.build();
    }

    private final AccessControlListManagerHandler accessControlListManagerHandler;

    public AccessControlListHandler(final String cmd) throws IOException {
        super(cmd);
        accessControlListManagerHandler = new AccessControlListManagerHandler(
                webServiceUrl);
    }

    /**
     * Add ACL
     *
     * @see name=autoACL <br/>
     *      description=ACL description <br/>
     *      deviceTypeList=HDX,VVX,RPM <br/>
     *      aclModelList=ipad,iphone <br/>
     *      groupNameList=group1
     * @param name
     *            ACL name
     * @param description
     *            ACL description
     * @param deviceTypeList
     *            Device type list
     * @param aclModelList
     *            Detail model list for RPM
     * @param groupNameList
     *            The group name list
     * @return The result
     */
    public String addAcl() {
        String status = "Failed";
        final GroupManagerHandler groupManagerHandler = new GroupManagerHandler(
                webServiceUrl);
        final JAccessControlList accessControlList = new JAccessControlList();
        accessControlList.setAclName(inputCmd.get("name"));
        accessControlList.setAclDescription(inputCmd.get("description")
                .replaceAll("~", " "));
        accessControlList.setBelongsToAreaUgpId(0);
        final String[] deviceList = inputCmd.get("deviceTypeList")
                .replaceAll("~", " ").split(",");
        for (int i = 0; i < deviceList.length; i++) {
            JDeviceType deviceType = null;
            if (deviceList[i].equalsIgnoreCase("HDX")) {
                deviceType = JDeviceType.HD;
            } else if (deviceList[i].equalsIgnoreCase("CMAD")) {
                deviceType = JDeviceType.VL;
            } else if (deviceList[i].equalsIgnoreCase("RPM")) {
                deviceType = JDeviceType.RP___MOBILE;
            } else if (deviceList[i].equalsIgnoreCase("RPD")) {
                deviceType = JDeviceType.RP___DESKTOP;
            } else if (deviceList[i].equalsIgnoreCase("GroupSeries")) {
                deviceType = JDeviceType.GROUPSERIES;
            } else if (deviceList[i].equalsIgnoreCase("ITP")) {
                deviceType = JDeviceType.ITP;
            } else if (deviceList[i].equalsIgnoreCase("VVX")) {
                deviceType = JDeviceType.VX;
            } else if (deviceList[i].equalsIgnoreCase("RP-Debut")) {
                deviceType = JDeviceType.DB;
            }
            accessControlList.getAclDevices().add(deviceType);
        }
        if (!inputCmd.get("aclModelList").isEmpty()
                && inputCmd.get("deviceTypeList").contains("RPM")) {
            final String[] aclModelList = inputCmd.get("aclModelList")
                    .replaceAll("~", " ").split(",");
            for (int i = 0; i < aclModelList.length; i++) {
                JModel model = null;
                if (aclModelList[i].equalsIgnoreCase("ipad")) {
                    model = JModel.RP___MOBILE___IPAD;
                } else if (aclModelList[i].equalsIgnoreCase("iphone")) {
                    model = JModel.RP___MOBILE___IPHONE;
                } else if (aclModelList[i].equalsIgnoreCase("android tablet")) {
                    model = JModel.RP___MOBILE___ANDROID___TABLET;
                } else if (aclModelList[i].equalsIgnoreCase("android phone")) {
                    model = JModel.RP___MOBILE___ANDROID___PHONE;
                }
                accessControlList.getAclModels().add(model);
            }
        }
        final List<com.polycom.webservices.GroupManager.JGroup> groupList = groupManagerHandler
                .getVisibleGroups(userToken);
        final String[] groupNameList = inputCmd.get("groupNameList")
                .replaceAll("~", " ").split(",");
        for (int i = 0; i < groupNameList.length; i++) {
            for (final com.polycom.webservices.GroupManager.JGroup group : groupList) {
                if (groupNameList[i].equals(group.getGroupName())) {
                    final JGroup aclGroup = new JGroup();
                    try {
                        CommonUtils.copyProperties(group, aclGroup);
                        if (!group.getParentGroupDNs().isEmpty()) {
                            aclGroup.getParentGroupDNs()
                                    .set(0, group.getParentGroupDNs().get(0));
                        }
                        if (!group.getParentGroupNames().isEmpty()) {
                            aclGroup.getParentGroupNames()
                                    .set(0, group.getParentGroupNames().get(0));
                        }
                    } catch (final Exception e) {
                        logger.error("Exception found when trying to copy properties from the group "
                                + "manager handler with the error message "
                                + e.getMessage());
                        e.printStackTrace();
                        return "Failed, Exception found when trying to copy properties from the group "
                                + "manager handler with the error message "
                                + e.getMessage();
                    }
                    accessControlList.getAclGroups().add(aclGroup);
                }
            }
        }
        final JWebResult result = accessControlListManagerHandler
                .addAcl(userToken, accessControlList);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfully create ACL " + inputCmd.get("name")
                    + " on RPRM.");
        } else {
            status = "Failed";
            logger.error("Failed to create ACL " + inputCmd.get("name")
                    + " on RPRM.");
            return status + " Failed to create ACL " + inputCmd.get("name")
                    + " on RPRM.";
        }
        return status;
    }

    /**
     * Delete ACL
     *
     * @see name=autoACL <br/>
     * @param name
     *            ACL name
     * @return The result
     */
    public String deleteAcl() {
        String status = "Failed";
        String aclId = "";
        final List<JAccessControlList> aclList = accessControlListManagerHandler
                .getAll(userToken);
        for (final JAccessControlList acl : aclList) {
            if (acl.getAclName().equals(inputCmd.get("name"))) {
                aclId = acl.getDbKey();
            }
        }
        final JWebResult result = accessControlListManagerHandler
                .deleteAcl(userToken, aclId);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfully deleted the ACL " + inputCmd.get("name")
                    + " on the RPRM.");
        } else {
            logger.error("Failed to delete the ACL " + inputCmd.get("name")
                    + " on the RPRM.");
            return status + ", Failed to delete the ACL " + inputCmd.get("name")
                    + " on the RPRM.";
        }
        return status;
    }

    @Override
    protected void injectCmdArgs() {
        inputCmd.put("name", "");
        inputCmd.put("newName", "");
        inputCmd.put("description", "");
        inputCmd.put("deviceTypeList", "");
        inputCmd.put("groupNameList", "");
        inputCmd.put("aclModelList", "");
    }

    /**
     * Update specified ACL. For device type, group and model list, these input
     * parameters are optional. If they are input, the original corresponding
     * fields data will be replaced with the new ones.
     *
     * @see name=autoACL <br/>
     *      newName=newautoACL <br/>
     *      description=ACL description <br/>
     *      deviceTypeList=HDX,VVX,RPM <br/>
     *      aclModelList=ipad,iphone <br/>
     *      groupNameList=group1
     * @param name
     *            ACL name
     * @param newName
     *            ACL new name
     * @param description
     *            ACL new description
     * @param deviceTypeList
     *            Device type list
     * @param aclModelList
     *            Detail model list for RPM
     * @param groupNameList
     *            The group name list
     * @return The result
     */
    public String updateAcl() {
        String status = "Failed";
        JAccessControlList accessControlList = null;
        final GroupManagerHandler groupManagerHandler = new GroupManagerHandler(
                webServiceUrl);
        groupManagerHandler.getVisibleGroups(userToken);
        final List<JAccessControlList> aclList = accessControlListManagerHandler
                .getAll(userToken);
        for (final JAccessControlList acl : aclList) {
            if (acl.getAclName().equals(inputCmd.get("name"))) {
                accessControlList = acl;
            }
        }
        if (accessControlList == null) {
            logger.error("Cannot find the specified ACL " + inputCmd.get("name")
                    + " on RPRM.");
            return status + " Cannot find the specified ACL "
                    + inputCmd.get("name") + " on RPRM.";
        }
        // update the name
        if (!inputCmd.get("newName").isEmpty()) {
            accessControlList.setAclName(inputCmd.get("newName"));
        }
        // update the description
        if (!inputCmd.get("description").isEmpty()) {
            accessControlList.setAclDescription(inputCmd.get("description"));
        }
        // update the device type
        if (!inputCmd.get("deviceTypeList").isEmpty()) {
            accessControlList.getAclDevices().clear();
            final String[] deviceList = inputCmd.get("deviceTypeList")
                    .replaceAll("~", " ").split(",");
            for (int i = 0; i < deviceList.length; i++) {
                JDeviceType deviceType = null;
                if (deviceList[i].equalsIgnoreCase("HDX")) {
                    deviceType = JDeviceType.HD;
                } else if (deviceList[i].equalsIgnoreCase("CMAD")) {
                    deviceType = JDeviceType.VL;
                } else if (deviceList[i].equalsIgnoreCase("RPM")) {
                    deviceType = JDeviceType.RP___MOBILE;
                } else if (deviceList[i].equalsIgnoreCase("RPD")) {
                    deviceType = JDeviceType.RP___DESKTOP;
                } else if (deviceList[i].equalsIgnoreCase("GroupSeries")) {
                    deviceType = JDeviceType.GROUPSERIES;
                } else if (deviceList[i].equalsIgnoreCase("ITP")) {
                    deviceType = JDeviceType.ITP;
                } else if (deviceList[i].equalsIgnoreCase("VVX")) {
                    deviceType = JDeviceType.VX;
                } else if (deviceList[i].equalsIgnoreCase("RP-Debut")) {
                    deviceType = JDeviceType.DB;
                }
                accessControlList.getAclDevices().add(deviceType);
            }
            // update the RPM models
            if (!inputCmd.get("aclModelList").isEmpty()
                    && inputCmd.get("deviceTypeList").contains("RPM")) {
                accessControlList.getAclModels().clear();
                final String[] aclModelList = inputCmd.get("aclModelList")
                        .replaceAll("~", " ").split(",");
                for (int i = 0; i < aclModelList.length; i++) {
                    JModel model = null;
                    if (aclModelList[i].equalsIgnoreCase("ipad")) {
                        model = JModel.RP___MOBILE___IPAD;
                    } else if (aclModelList[i].equalsIgnoreCase("iphone")) {
                        model = JModel.RP___MOBILE___IPHONE;
                    } else if (aclModelList[i]
                            .equalsIgnoreCase("android tablet")) {
                        model = JModel.RP___MOBILE___ANDROID___TABLET;
                    } else
                        if (aclModelList[i].equalsIgnoreCase("android phone")) {
                        model = JModel.RP___MOBILE___ANDROID___PHONE;
                    }
                    accessControlList.getAclModels().add(model);
                }
            }
        }
        // update the associated group
        if (!inputCmd.get("groupNameList").isEmpty()) {
            accessControlList.getAclGroups().clear();
            final List<com.polycom.webservices.GroupManager.JGroup> groupList = groupManagerHandler
                    .getVisibleGroups(userToken);
            final String[] groupNameList = inputCmd.get("groupNameList")
                    .replaceAll("~", " ").split(",");
            for (int i = 0; i < groupNameList.length; i++) {
                for (final com.polycom.webservices.GroupManager.JGroup group : groupList) {
                    if (groupNameList[i].equals(group.getGroupName())) {
                        final JGroup aclGroup = new JGroup();
                        try {
                            CommonUtils.copyProperties(group, aclGroup);
                            if (!group.getParentGroupDNs().isEmpty()) {
                                aclGroup.getParentGroupDNs()
                                        .set(0,
                                             group.getParentGroupDNs().get(0));
                            }
                            if (!group.getParentGroupNames().isEmpty()) {
                                aclGroup.getParentGroupNames()
                                        .set(0,
                                             group.getParentGroupNames()
                                                     .get(0));
                            }
                        } catch (final Exception e) {
                            logger.error("Exception found when trying to copy properties from the group "
                                    + "manager handler with the error message "
                                    + e.getMessage());
                            e.printStackTrace();
                            return "Failed, Exception found when trying to copy properties from the group "
                                    + "manager handler with the error message "
                                    + e.getMessage();
                        }
                        accessControlList.getAclGroups().add(aclGroup);
                    }
                }
            }
        }
        final JWebResult result = accessControlListManagerHandler
                .updateAcl(userToken, accessControlList);
        if (result.getStatus().equals(JStatus.SUCCESS)) {
            status = "SUCCESS";
            logger.info("Successfully updated ACL " + inputCmd.get("name")
                    + " on RPRM.");
        } else {
            status = "Failed";
            logger.error("Failed to update ACL " + inputCmd.get("name")
                    + " on RPRM.");
            return status + " Failed to update ACL " + inputCmd.get("name")
                    + " on RPRM.";
        }
        return status;
    }
}
