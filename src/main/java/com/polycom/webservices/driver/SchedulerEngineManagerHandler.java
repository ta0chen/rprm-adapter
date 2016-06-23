package com.polycom.webservices.driver;

import java.net.URL;
import java.util.Calendar;
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
import com.polycom.webservices.SchedulerEngine.JCMConferenceMediaType;
import com.polycom.webservices.SchedulerEngine.JCMConferenceStatus;
import com.polycom.webservices.SchedulerEngine.JConferencePolicy;
import com.polycom.webservices.SchedulerEngine.JCredentials;
import com.polycom.webservices.SchedulerEngine.JSchedBridgeSelectionType;
import com.polycom.webservices.SchedulerEngine.JSchedConf;
import com.polycom.webservices.SchedulerEngine.JSchedDevice;
import com.polycom.webservices.SchedulerEngine.JSchedPart;
import com.polycom.webservices.SchedulerEngine.JSchedRecurrence;
import com.polycom.webservices.SchedulerEngine.JSchedRecurrenceResult;
import com.polycom.webservices.SchedulerEngine.JSchedResult;
import com.polycom.webservices.SchedulerEngine.JSchedRoom;
import com.polycom.webservices.SchedulerEngine.JSchedUIType;
import com.polycom.webservices.SchedulerEngine.JSchedulerEngine;
import com.polycom.webservices.SchedulerEngine.JSchedulerEngine_Service;
import com.polycom.webservices.SchedulerEngine.JSchedulerSettings;
import com.polycom.webservices.SchedulerEngine.JUIUtcDateTime;
import com.polycom.webservices.SchedulerEngine.JWebResult;

/**
 * Scheduler engine handler
 *
 * @author Tao Chen
 *
 */
public class SchedulerEngineManagerHandler {
    protected static Logger    logger           = Logger
            .getLogger("SchedulerEngineManagerHandler");
    private static final QName SERVICE_NAME     = new QName(
                                                            "http://polycom.com/WebServices", "JSchedulerEngine");
    JSchedulerEngine           port;
    private long               recurringHandler = 0;

    /**
     * Construction of SchedulerEngineHandler class
     */
    public SchedulerEngineManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JSchedulerEngine) jsonInvocationHandler
                    .getProxy(JSchedulerEngine.class);
        } else {
            final URL wsdlURL = SchedulerEngineManagerHandler.class
                    .getClassLoader()
                    .getResource("wsdl/current/JSchedulerEngine.wsdl");
            final JSchedulerEngine_Service ss = new JSchedulerEngine_Service(
                                                                             wsdlURL, SERVICE_NAME);
            port = ss.getJSchedulerEnginePort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
            .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                 webServiceUrl + "/JSchedulerEngine");
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
     * Delete conference
     *
     * @param userToken
     * @param confId
     * @return
     */
    public JWebResult deleteConference(final String userToken,
            final String confId) {
        System.out.println("Invoking deleteConference...");
        final JCredentials _deleteConference_credentials = new JCredentials();
        _deleteConference_credentials.setUserToken(userToken);
        final String _deleteConference_confId = confId;
        final JWebResult _deleteConference__return = port
                .deleteConference(_deleteConference_credentials,
                                  _deleteConference_confId);
        System.out.println("deleteConference.result="
                + _deleteConference__return);
        return _deleteConference__return;
    }

    /**
     * Delete recurring conference
     *
     * @param userToken
     * @param recurId
     * @return
     */
    public JWebResult deleteRecurringConference(final String userToken,
            final int recurId) {
        System.out.println("Invoking deleteRecurringConference...");
        final JCredentials _deleteRecurringConference_credentials = new JCredentials();
        _deleteRecurringConference_credentials.setUserToken(userToken);
        final int _deleteRecurringConference_recurId = recurId;
        final JWebResult _deleteRecurringConference__return = port
                .deleteRecurringSeries(_deleteRecurringConference_credentials,
                                       _deleteRecurringConference_recurId);
        System.out.println("deleteRecurringConference.result="
                + _deleteRecurringConference__return);
        return _deleteRecurringConference__return;
    }

    /**
     * Find the conference policy for user when trying to schedule a conference
     *
     * @param userToken
     * @return
     */
    public List<JConferencePolicy>
    findConferencePolicysForUser(final String userToken) {
        System.out.println("Invoking findConferencePolicysForUser...");
        logger.info("Invoking findConferencePolicysForUser...");
        final JCredentials _findConferencePolicysForUser_credentials = new JCredentials();
        _findConferencePolicysForUser_credentials.setUserToken(userToken);
        final Holder<List<JConferencePolicy>> _findConferencePolicysForUser_conferencePolicys = new Holder<List<JConferencePolicy>>();
        final JWebResult _findConferencePolicysForUser__return = port
                .findConferencePolicysForUser(_findConferencePolicysForUser_credentials,
                                              _findConferencePolicysForUser_conferencePolicys);
        System.out.println("findConferencePolicysForUser.result="
                + _findConferencePolicysForUser__return);
        System.out
        .println("findConferencePolicysForUser._findConferencePolicysForUser_conferencePolicys="
                + _findConferencePolicysForUser_conferencePolicys.value);
        return _findConferencePolicysForUser_conferencePolicys.value;
    }

    /**
     * Get all the rooms in XMA for scheduling conference
     *
     * @param userToken
     * @return
     */
    public List<JSchedRoom> getAllRooms(final String userToken) {
        System.out.println("Invoking getAllRooms...");
        logger.info("Invoking getAllRooms...");
        final JCredentials _getAllRooms_credentials = new JCredentials();
        _getAllRooms_credentials.setUserToken(userToken);
        final Holder<List<JSchedRoom>> _getAllRooms_rooms = new Holder<List<JSchedRoom>>();
        final JWebResult _getAllRooms__return = port
                .getAllRooms(_getAllRooms_credentials, _getAllRooms_rooms);
        System.out.println("getAllRooms.result=" + _getAllRooms__return);
        System.out.println("getAllRooms._getAllRooms_rooms="
                + _getAllRooms_rooms.value);
        return _getAllRooms_rooms.value;
    }

    /**
     * Get the recurrence dates
     *
     * @param userToken
     * @param clientOffset
     * @param recurrence
     * @return
     */
    public List<JUIUtcDateTime>
    getRecurrenceDates(final String userToken,
                       final JSchedRecurrence recurrence) {
        System.out.println("Invoking getRecurrenceDates...");
        logger.info("Invoking getRecurrenceDates...");
        final JCredentials _getRecurrenceDates_credentials = new JCredentials();
        _getRecurrenceDates_credentials.setUserToken(userToken);
        final JSchedRecurrence _getRecurrenceDates_recurrence = recurrence;
        final Calendar cl = Calendar.getInstance();
        final int _getRecurrenceDates_clientOffset = (cl
                .get(Calendar.ZONE_OFFSET) + cl.get(Calendar.DST_OFFSET))
                / (60 * 1000);
        final Holder<List<JUIUtcDateTime>> _getRecurrenceDates_recurrenceDates = new Holder<List<JUIUtcDateTime>>();
        final JWebResult _getRecurrenceDates__return = port
                .getRecurrenceDates(_getRecurrenceDates_credentials,
                                    _getRecurrenceDates_recurrence,
                                    _getRecurrenceDates_clientOffset,
                                    _getRecurrenceDates_recurrenceDates);
        System.out.println("getRecurrenceDates.result="
                + _getRecurrenceDates__return);
        System.out
        .println("getRecurrenceDates._getRecurrenceDates_recurrenceDates="
                + _getRecurrenceDates_recurrenceDates.value);
        return _getRecurrenceDates_recurrenceDates.value;
    }

    public long getRecurringHandler() {
        return recurringHandler;
    }

    public JSchedRecurrenceResult getRecurringProgress(final String userToken,
            final long handler) {
        System.out.println("Invoking getRecurringProgress...");
        logger.info("Invoking getRecurringProgress...");
        final JCredentials _getRecurringProgress_credentials = new JCredentials();
        _getRecurringProgress_credentials.setUserToken(userToken);
        final long _getRecurringProgress_handler = handler;
        final Holder<JSchedRecurrenceResult> _getRecurringProgress_schedRecurrenceResult = new Holder<JSchedRecurrenceResult>();
        final JWebResult _getRecurringProgress__return = port
                .getRecurringProgress(_getRecurringProgress_credentials,
                                      _getRecurringProgress_handler,
                                      _getRecurringProgress_schedRecurrenceResult);
        System.out.println("getRecurringProgress.result="
                + _getRecurringProgress__return);
        System.out
        .println("getRecurringProgress._getRecurringProgress_schedRecurrenceResult="
                + _getRecurringProgress_schedRecurrenceResult.value);
        return _getRecurringProgress_schedRecurrenceResult.value;
    }

    /**
     * Get the scheduled conference by the conference ID
     *
     * @param userToken
     * @param confId
     * @return
     */
    public JSchedConf getSchedConf(final String userToken,
            final String confId) {
        System.out.println("Invoking getSchedConf...");
        final JCredentials _getSchedConf_credentials = new JCredentials();
        _getSchedConf_credentials.setUserToken(userToken);
        final String _getSchedConf_confId = confId;
        final Holder<JSchedConf> _getSchedConf_schedConfResult = new Holder<JSchedConf>();
        final JWebResult _getSchedConf__return = port
                .getSchedConf(_getSchedConf_credentials,
                              _getSchedConf_confId,
                              _getSchedConf_schedConfResult);
        System.out.println("getSchedConf.result=" + _getSchedConf__return);
        System.out.println("getSchedConf._getSchedConf_schedConfResult="
                + _getSchedConf_schedConfResult.value);
        return _getSchedConf_schedConfResult.value;
    }

    /**
     * Get schedule participant information by UGPID
     *
     * @param userToken
     * @param userUgpId
     * @return
     */
    public JSchedPart getSchedPartByUgpId(final String userToken,
            final int userUgpId) {
        System.out.println("Invoking getSchedPartByUgpId...");
        final JCredentials _getSchedPartByUgpId_credentials = new JCredentials();
        _getSchedPartByUgpId_credentials.setUserToken(userToken);
        final int _getSchedPartByUgpId_userUgpId = userUgpId;
        final Holder<JSchedPart> _getSchedPartByUgpId_schedPart = new Holder<JSchedPart>();
        final JWebResult _getSchedPartByUgpId__return = port
                .getSchedPartByUgpId(_getSchedPartByUgpId_credentials,
                                     _getSchedPartByUgpId_userUgpId,
                                     _getSchedPartByUgpId_schedPart);
        System.out.println("getSchedPartByUgpId.result="
                + _getSchedPartByUgpId__return);
        System.out.println("getSchedPartByUgpId._getSchedPartByUgpId_schedPart="
                + _getSchedPartByUgpId_schedPart.value);
        return _getSchedPartByUgpId_schedPart.value;
    }

    /**
     * Get the settings of the scheduler
     *
     * @param userToken
     * @return
     */
    public JSchedulerSettings getSchedulerSettings(final String userToken) {
        System.out.println("Invoking getSchedulerSettings...");
        logger.info("Invoking getSchedulerSettings...");
        final JCredentials _getSchedulerSettings_credentials = new JCredentials();
        _getSchedulerSettings_credentials.setUserToken(userToken);
        final Holder<JSchedulerSettings> _getSchedulerSettings_settings = new Holder<JSchedulerSettings>();
        final JWebResult _getSchedulerSettings__return = port
                .getSchedulerSettings(_getSchedulerSettings_credentials,
                                      _getSchedulerSettings_settings);
        System.out.println("getSchedulerSettings.result="
                + _getSchedulerSettings__return);
        System.out
        .println("getSchedulerSettings._getSchedulerSettings_settings="
                + _getSchedulerSettings_settings.value);
        return _getSchedulerSettings_settings.value;
    }

    /**
     * Schedule an anytime conference
     *
     * @param userToken
     * @param confname
     * @param schedPart
     * @param schedPart1
     * @param uuid
     */
    public void scheduleAnytimeConference(final String userToken,
            final String confname,
            final Holder<com.polycom.webservices.SchedulerEngine.JSchedPart> schedPart,
            final Holder<com.polycom.webservices.SchedulerEngine.JSchedPart> schedPart1,
            final String uuid) {
        final JUIUtcDateTime utcDate = new JUIUtcDateTime();
        utcDate.setUnixTime(utcDate.getUnixTime());
        final JSchedDevice jsd = new JSchedDevice();
        jsd.getAliasList();
        final JConferencePolicy confPolicy = new JConferencePolicy();
        // Construct the conference policy
        // confPolicy.setAudioOnly(false);
        confPolicy.setAutoStartConference(false);
        confPolicy.setBelongsToAreaUgpId(0);
        confPolicy.setDmaTemplateName("Factory Template");
        confPolicy.setEnableProfile(false);
        confPolicy.setForExternalAPI(false);
        confPolicy.setGatheringRecordIndicating(false);
        confPolicy.setOverrideConferenceIVR(false);
        confPolicy.setPassword(null);
        confPolicy.setPolicyId(confPolicy.getPolicyId());
        confPolicy.setPolicyName("Factory Template");
        confPolicy.setPolicytype(0);
        confPolicy.setUseEntryQueue(false);
        confPolicy.setVideoConference(false);
        System.out.println("Invoking scheduleConference...");
        final com.polycom.webservices.SchedulerEngine.JCredentials _scheduleConference_credentials = new com.polycom.webservices.SchedulerEngine.JCredentials();
        _scheduleConference_credentials.setUserToken(userToken);
        // Construct the request body
        final com.polycom.webservices.SchedulerEngine.JSchedConf _scheduleConference_conf = new com.polycom.webservices.SchedulerEngine.JSchedConf();
        _scheduleConference_conf.setBelongsToAreaUgpId(-1);
        _scheduleConference_conf.setBridgeConference(false);
        _scheduleConference_conf.setCascadedConference(false);
        _scheduleConference_conf.setComments("Tao Any time conference");
        _scheduleConference_conf.setConfName(confname);
        _scheduleConference_conf.setConfChairPin("9555");
        _scheduleConference_conf
        .setConfMediaType(JCMConferenceMediaType.AUDIO_VIDEO);
        _scheduleConference_conf.setConfStatus(JCMConferenceStatus.FUTURE);
        _scheduleConference_conf.setCreatorUgpId(1);
        _scheduleConference_conf.setDeleteOnCompletion(false);
        _scheduleConference_conf.setDmaConfRoomId("1934");
        _scheduleConference_conf.setEmbeddedMPConference(false);
        _scheduleConference_conf.setEndDateTime(utcDate);
        _scheduleConference_conf.setEndpointConference(false);
        _scheduleConference_conf.setOriginalCreatorUgpId(0);
        _scheduleConference_conf.setOwnerName("debuguser4 ddd");
        _scheduleConference_conf.setOwnerUUID(uuid);
        _scheduleConference_conf.setP2PConference(false);
        _scheduleConference_conf.setRecurId(0);
        _scheduleConference_conf.setScheduledConference(false);
        _scheduleConference_conf.setStartDateTime(utcDate);
        _scheduleConference_conf
        .setBridgeSelectionType(JSchedBridgeSelectionType.MULTIDMAPOOL);
        _scheduleConference_conf.setConferencePolicy(confPolicy);
        _scheduleConference_conf.setDeclineConfIfFail(false);
        _scheduleConference_conf.setEmailLocale("en_US");
        _scheduleConference_conf.setIcalSequence(0);
        _scheduleConference_conf.setRecurringInstance(false);
        _scheduleConference_conf
        .setSchedulingUI(JSchedUIType.UI___DMA___ANYTIME___CONF);
        _scheduleConference_conf.setSendEmailAfterConfIsCreated(false);
        _scheduleConference_conf.getSchedPartCollection().add(schedPart.value);
        _scheduleConference_conf.getSchedPartCollection().add(schedPart1.value);
        // _scheduleConference_conf.getSchedPartCollection().add(schedPart2.value);
        final Holder<com.polycom.webservices.SchedulerEngine.JSchedResult> _scheduleConference_schedResult = new Holder<com.polycom.webservices.SchedulerEngine.JSchedResult>();
        final com.polycom.webservices.SchedulerEngine.JWebResult _scheduleConference__return = port
                .scheduleConference(_scheduleConference_credentials,
                                    _scheduleConference_conf,
                                    _scheduleConference_schedResult);
        System.out.println("scheduleConference.result="
                + _scheduleConference__return);
        System.out.println("scheduleConference._scheduleConference_schedResult="
                + _scheduleConference_schedResult.value);
    }

    /**
     * Schedule a conference
     *
     * @param userToken
     * @param conf
     * @return
     */
    public JSchedResult scheduleConference(final String userToken,
            final JSchedConf conf) {
        System.out.println("Invoking scheduleConference...");
        logger.info("Invoking scheduleConference...");
        final JCredentials _scheduleConference_credentials = new JCredentials();
        _scheduleConference_credentials.setUserToken(userToken);
        final JSchedConf _scheduleConference_conf = conf;
        final Holder<JSchedResult> _scheduleConference_schedResult = new Holder<JSchedResult>();
        final JWebResult _scheduleConference__return = port
                .scheduleConference(_scheduleConference_credentials,
                                    _scheduleConference_conf,
                                    _scheduleConference_schedResult);
        System.out.println("scheduleConference.result="
                + _scheduleConference__return);
        System.out.println("scheduleConference._scheduleConference_schedResult="
                + _scheduleConference_schedResult.value);
        return _scheduleConference_schedResult.value;
    }

    /**
     * Schedule an pool conference
     *
     * @param credentials
     * @param conf
     *
     */
    public JWebResult schedulePoolConference(final JCredentials credentials,
            final JSchedConf conf) {
        final JCredentials _scheduleConference_credentials = credentials;
        final JSchedConf _scheduleConference_conf = conf;
        final Holder<JSchedResult> _scheduleConference_schedResult = new Holder<JSchedResult>();
        final JWebResult _scheduleConference__return = port
                .scheduleConference(_scheduleConference_credentials,
                                    _scheduleConference_conf,
                                    _scheduleConference_schedResult);
        System.out.println("scheduleConference.result="
                + _scheduleConference__return);
        System.out.println("scheduleConference._scheduleConference_schedResult="
                + _scheduleConference_schedResult.value);
        return _scheduleConference__return;
    }

    /**
     * Schedule recurring conference
     *
     * @param userToken
     * @param conf
     * @return
     */
    public JWebResult
    scheduleRecurringConferenceNonBlock(final String userToken,
            final JSchedConf conf) {
        System.out.println("Invoking scheduleRecurringConferenceNonBlock...");
        logger.info("Invoking scheduleRecurringConferenceNonBlock...");
        final JCredentials _scheduleRecurringConferenceNonBlock_credentials = new JCredentials();
        _scheduleRecurringConferenceNonBlock_credentials
        .setUserToken(userToken);
        final JSchedConf _scheduleRecurringConferenceNonBlock_conf = conf;
        final Holder<Long> _scheduleRecurringConferenceNonBlock_handler = new Holder<Long>();
        final JWebResult _scheduleRecurringConferenceNonBlock__return = port
                .scheduleRecurringConferenceNonBlock(_scheduleRecurringConferenceNonBlock_credentials,
                                                     _scheduleRecurringConferenceNonBlock_conf,
                                                     _scheduleRecurringConferenceNonBlock_handler);
        System.out.println("scheduleRecurringConferenceNonBlock.result="
                + _scheduleRecurringConferenceNonBlock__return);
        System.out
        .println("scheduleRecurringConferenceNonBlock._scheduleRecurringConferenceNonBlock_handler="
                + _scheduleRecurringConferenceNonBlock_handler.value);
        setRecurringHandler(_scheduleRecurringConferenceNonBlock_handler.value);
        return _scheduleRecurringConferenceNonBlock__return;
    }

    public void setRecurringHandler(final long recurringHandler) {
        this.recurringHandler = recurringHandler;
    }

    /**
     * Conference settings
     *
     * @param userToken
     * @param settings
     * @return
     */
    public JWebResult setSchedulerSettings(final String userToken,
            final JSchedulerSettings settings) {
        System.out.println("Invoking setSchedulerSettings...");
        final JCredentials _setSchedulerSettings_credentials = new JCredentials();
        _setSchedulerSettings_credentials.setUserToken(userToken);
        final JSchedulerSettings _setSchedulerSettings_settings = settings;
        final JWebResult _setSchedulerSettings__return = port
                .setSchedulerSettings(_setSchedulerSettings_credentials,
                                      _setSchedulerSettings_settings);
        System.out.println("setSchedulerSettings.result="
                + _setSchedulerSettings__return);
        return _setSchedulerSettings__return;
    }
}
