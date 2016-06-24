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
import com.polycom.webservices.ConferenceService.ConferenceFilter;
import com.polycom.webservices.ConferenceService.Direction;
import com.polycom.webservices.ConferenceService.ExternalCmaOngoingConference;
import com.polycom.webservices.ConferenceService.ExternalCmaOngoingParticipant;
import com.polycom.webservices.ConferenceService.JAnytimeConference;
import com.polycom.webservices.ConferenceService.JCMConferenceStatus;
import com.polycom.webservices.ConferenceService.JConferenceService;
import com.polycom.webservices.ConferenceService.JConferenceService_Service;
import com.polycom.webservices.ConferenceService.JCredentials;
import com.polycom.webservices.ConferenceService.JLeanConference;
import com.polycom.webservices.ConferenceService.JSchedMessage;
import com.polycom.webservices.ConferenceService.JSchedPart;
import com.polycom.webservices.ConferenceService.JSchedVideoLayout;
import com.polycom.webservices.ConferenceService.JUIUtcDateTime;
import com.polycom.webservices.ConferenceService.JWebResult;
import com.polycom.webservices.ConferenceService.Pager;
import com.polycom.webservices.ConferenceService.Sort;

/**
 * Conference service handler
 *
 * @author Tao Chen
 *
 */
public class ConferenceServiceHandler {
    protected static Logger    logger       = Logger
            .getLogger("ConferenceServiceHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JConferenceService");
    JConferenceService         port;

    /**
     * Construction of ConferenceServiceHandler class
     */
    public ConferenceServiceHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JConferenceService) jsonInvocationHandler
                    .getProxy(JConferenceService.class);
        } else {
            final URL wsdlURL = ConferenceServiceHandler.class.getClassLoader()
                    .getResource("wsdl/current/JConferenceService.wsdl");
            final JConferenceService_Service ss = new JConferenceService_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJConferenceServicePort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JConferenceService");
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
     * Add participants into specified conference
     *
     * @param userToken
     * @param confId
     * @param ongoingParts
     * @return
     */
    public JWebResult
            addOngoingParticipantsToConference(final String userToken,
                                               final String confId,
                                               final List<ExternalCmaOngoingParticipant> ongoingParts) {
        System.out.println("Invoking addOngoingParticipantsToConference...");
        logger.info("Invoking addOngoingParticipantsToConference...");
        final JCredentials _addOngoingParticipantsToConference_credentials = new JCredentials();
        _addOngoingParticipantsToConference_credentials.setUserToken(userToken);
        final List<ExternalCmaOngoingParticipant> _addOngoingParticipantsToConference_ongoingParts = ongoingParts;
        final String _addOngoingParticipantsToConference_confId = confId;
        final JWebResult _addOngoingParticipantsToConference__return = port
                .addOngoingParticipantsToConference(_addOngoingParticipantsToConference_credentials,
                                                    _addOngoingParticipantsToConference_ongoingParts,
                                                    _addOngoingParticipantsToConference_confId);
        System.out.println("addOngoingParticipantsToConference.result="
                + _addOngoingParticipantsToConference__return);
        return _addOngoingParticipantsToConference__return;
    }

    /**
     * Add scheduled participants to the conference
     *
     * @param userToken
     * @param confId
     * @param parts
     * @return
     */
    public JWebResult
            addSchedParticipantsToConference(final String userToken,
                                             final String confId,
                                             final List<JSchedPart> parts) {
        System.out.println("Invoking addSchedParticipantsToConference...");
        logger.info("Invoking addSchedParticipantsToConference...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JSchedMessage>> messages = new Holder<List<JSchedMessage>>();
        final JWebResult result = port
                .addSchedParticipantsToConference(credentials,
                                                  parts,
                                                  confId,
                                                  messages);
        return result;
    }

    /**
     * Extend the duration of the specified conference
     *
     * @param userToken
     * @param confId
     * @param endTime
     * @return
     */
    public JWebResult extendConference(final String userToken,
                                       final String confId,
                                       final long endTime) {
        System.out.println("Invoking extendConference...");
        logger.info("Invoking extendConference...");
        final JCredentials _extendConference_credentials = new JCredentials();
        _extendConference_credentials.setUserToken(userToken);
        final String _extendConference_confId = confId;
        final JUIUtcDateTime _extendConference_endTime = new JUIUtcDateTime();
        _extendConference_endTime.setUnixTime(endTime);
        final JWebResult _extendConference__return = port
                .extendConference(_extendConference_credentials,
                                  _extendConference_confId,
                                  _extendConference_endTime);
        System.out.println("extendConference.result="
                + _extendConference__return);
        return _extendConference__return;
    }

    /**
     * getAnytimeConference
     *
     * @param userToken
     * @param confName
     * @return
     */
    public JAnytimeConference getAnytimeConference(final String userToken,
                                                   final String confName) {
        JAnytimeConference toDeleteAnytimeConference = null;
        System.out.println("Invoking getAnytimeConference...");
        logger.info("Invoking getAnytimeConference...");
        final JCredentials _getAnytimeConference_credentials = new JCredentials();
        _getAnytimeConference_credentials.setUserToken(userToken);
        final Holder<List<JAnytimeConference>> _getAnytimeConference_anytimeConferences = new Holder<List<JAnytimeConference>>();
        final JWebResult _getAnytimeConference__return = port
                .getAnytimeConferenceList(_getAnytimeConference_credentials,
                                          _getAnytimeConference_anytimeConferences);
        System.out.println("getAnytimeConference.result="
                + _getAnytimeConference__return);
        for (final JAnytimeConference anytimeConference : _getAnytimeConference_anytimeConferences.value) {
            if (confName.equalsIgnoreCase(anytimeConference.getConfName())) {
                toDeleteAnytimeConference = anytimeConference;
                System.out
                        .println("getAnytimeConference._getAnytimeConference_anytimeConferences="
                                + anytimeConference.getConfName());
                break;
            }
        }
        return toDeleteAnytimeConference;
    }

    /**
     * Get all scheduled DMA anytime conferences from anytime conference page
     *
     * @param userToken
     * @return
     */
    public List<JAnytimeConference>
            getAnytimeConferenceList(final String userToken) {
        logger.info("Invoking getAnytimeConferenceList...");
        final JCredentials _getAnytimeConferenceList_credentials = new JCredentials();
        _getAnytimeConferenceList_credentials.setUserToken(userToken);
        final Holder<List<JAnytimeConference>> _getAnytimeConferenceList_anytimeConferences = new Holder<List<JAnytimeConference>>();
        final JWebResult _getAnytimeConferenceList__return = port
                .getAnytimeConferenceList(_getAnytimeConferenceList_credentials,
                                          _getAnytimeConferenceList_anytimeConferences);
        System.out.println("getAnytimeConferenceList.result="
                + _getAnytimeConferenceList__return);
        System.out
                .println("getAnytimeConferenceList._getAnytimeConferenceList_anytimeConferences="
                        + _getAnytimeConferenceList_anytimeConferences.value);
        return _getAnytimeConferenceList_anytimeConferences.value;
    }

    /**
     * Get filtered lean conference list
     *
     * @param userToken
     * @param confName
     * @return
     */
    public List<JLeanConference>
            getFilteredLeanConferenceList(final String userToken,
                                          final String... filters) {
        System.out.println("Invoking getFilteredLeanConferenceList...");
        logger.info("Invoking getFilteredLeanConferenceList...");
        final JCredentials _getFilteredLeanConferenceList_credentials = new JCredentials();
        _getFilteredLeanConferenceList_credentials.setUserToken(userToken);
        final ConferenceFilter _getFilteredLeanConferenceList_filter = new ConferenceFilter();
        final JUIUtcDateTime dt = new JUIUtcDateTime();
        dt.setUnixTime(dt.getUnixTime());
        for(String filter : filters){
        	String[] keyWordMap = filter.split(":");
        	if (keyWordMap.length < 2) {
        		continue;
        	}
        	if ("confName".equalsIgnoreCase(keyWordMap[0])) {
        		_getFilteredLeanConferenceList_filter.setConfName(keyWordMap[1]);
        	}
        	if ("confStatus".equalsIgnoreCase(keyWordMap[0])) {
        		if ("future".equalsIgnoreCase(keyWordMap[1])) {
        			_getFilteredLeanConferenceList_filter.getAcceptableStates().add(JCMConferenceStatus.FUTURE);
        			_getFilteredLeanConferenceList_filter.getAcceptableStates().add(JCMConferenceStatus.FUTURE___ALERTS);
        		} else if ("ongoing".equalsIgnoreCase(keyWordMap[1])) {
        			_getFilteredLeanConferenceList_filter.getAcceptableStates().add(JCMConferenceStatus.ACTIVE);
        			_getFilteredLeanConferenceList_filter.getAcceptableStates().add(JCMConferenceStatus.ACTIVE___ALERTS);
        		} else {
        			_getFilteredLeanConferenceList_filter.getAcceptableStates().add(JCMConferenceStatus.DECLINED);
        			_getFilteredLeanConferenceList_filter.getAcceptableStates().add(JCMConferenceStatus.DELETED);
        			_getFilteredLeanConferenceList_filter.getAcceptableStates().add(JCMConferenceStatus.FINISHED);
        		}
        	}
        	if ("isAnytimeConf".equalsIgnoreCase(keyWordMap[0])) {
        		_getFilteredLeanConferenceList_filter.setAnytimeConf(Boolean.valueOf(keyWordMap[1]).booleanValue());
        		_getFilteredLeanConferenceList_filter.setMinimumToken(Long.valueOf(0));
        	}
        }
        
        final Boolean _getFilteredLeanConferenceList_includeMcuInfo = true;
        final Holder<List<JLeanConference>> _getFilteredLeanConferenceList_leanConferences = new Holder<List<JLeanConference>>();
        final Holder<Long> _getFilteredLeanConferenceList_token = new Holder<Long>();
        final JWebResult _getFilteredLeanConferenceList__return = port
                .getFilteredLeanConferenceList(_getFilteredLeanConferenceList_credentials,
                                               _getFilteredLeanConferenceList_filter,
                                               _getFilteredLeanConferenceList_includeMcuInfo,
                                               _getFilteredLeanConferenceList_leanConferences,
                                               _getFilteredLeanConferenceList_token);
        System.out.println("getFilteredLeanConferenceList.result="
                + _getFilteredLeanConferenceList__return);
        System.out
                .println("getFilteredLeanConferenceList._getFilteredLeanConferenceList_leanConferences="
                        + _getFilteredLeanConferenceList_leanConferences.value);
        System.out
                .println("getFilteredLeanConferenceList._getFilteredLeanConferenceList_token="
                        + _getFilteredLeanConferenceList_token.value);
        logger.info("getFilteredLeanConferenceList._getFilteredLeanConferenceList_leanConferences="
                + _getFilteredLeanConferenceList_leanConferences.value);
        return _getFilteredLeanConferenceList_leanConferences.value;
    }

    /**
     * Get an on going conference
     *
     * @param userToken
     * @param confId
     * @return
     */
    public ExternalCmaOngoingConference
            getOngoingConference(final String userToken, final String confId) {
        System.out.println("Invoking getOngoingConference...");
        logger.info("Invoking getOngoingConference...");
        final JCredentials _getOngoingConference_credentials = new JCredentials();
        _getOngoingConference_credentials.setUserToken(userToken);
        final String _getOngoingConference_confId = confId;
        final Holder<ExternalCmaOngoingConference> _getOngoingConference_ongoingConference = new Holder<ExternalCmaOngoingConference>();
        final JWebResult _getOngoingConference__return = port
                .getOngoingConference(_getOngoingConference_credentials,
                                      _getOngoingConference_confId,
                                      _getOngoingConference_ongoingConference);
        System.out.println("getOngoingConference.result="
                + _getOngoingConference__return);
        System.out
                .println("getOngoingConference._getOngoingConference_ongoingConference="
                        + _getOngoingConference_ongoingConference.value);
        return _getOngoingConference_ongoingConference.value;
    }

    /**
     * Get paged participants
     *
     * @param userToken
     * @param confId
     * @return
     */
    public List<ExternalCmaOngoingParticipant>
            getPagedParticipants(final String userToken, final String confId) {
        System.out.println("Invoking getPagedParticipants...");
        logger.info("Invoking getPagedParticipants...");
        final JCredentials _getPagedParticipants_credentials = new JCredentials();
        _getPagedParticipants_credentials.setUserToken(userToken);
        final String _getPagedParticipants_confId = confId;
        final Holder<Pager> _getPagedParticipants_pager = new Holder<Pager>();
        final Pager page = new Pager();
        page.setPageNumber(1);
        page.setPageSize(50);
        final Sort sort = new Sort();
        sort.setDirection(Direction.ASC);
        sort.setProperty("name");
        page.setSort(sort);
        page.setTotalPageNumber(1);
        _getPagedParticipants_pager.value = page;
        final Holder<List<ExternalCmaOngoingParticipant>> _getPagedParticipants_participants = new Holder<List<ExternalCmaOngoingParticipant>>();
        final Holder<JCMConferenceStatus> _getPagedParticipants_confStatus = new Holder<JCMConferenceStatus>();
        final JWebResult _getPagedParticipants__return = port
                .getPagedParticipants(_getPagedParticipants_credentials,
                                      _getPagedParticipants_confId,
                                      _getPagedParticipants_pager,
                                      _getPagedParticipants_participants,
                                      _getPagedParticipants_confStatus);
        System.out.println("getPagedParticipants.result="
                + _getPagedParticipants__return);
        return _getPagedParticipants_participants.value;
    }

    /**
     * Remove participant from conference
     *
     * @param userToken
     * @param confId
     * @param partId
     * @return
     */
    public JWebResult removeParticipantFromConference(final String userToken,
                                                      final String confId,
                                                      final String partId) {
        System.out.println("Invoking removeParticipantFromConference...");
        logger.info("Invoking removeParticipantFromConference...");
        final JCredentials _removeParticipantFromConference_credentials = new JCredentials();
        _removeParticipantFromConference_credentials.setUserToken(userToken);
        final String _removeParticipantFromConference_confId = confId;
        final String _removeParticipantFromConference_partId = partId;
        final JWebResult _removeParticipantFromConference__return = port
                .removeParticipant(_removeParticipantFromConference_credentials,
                                   _removeParticipantFromConference_confId,
                                   _removeParticipantFromConference_partId);
        System.out.println("removeParticipantFromConference.result="
                + _removeParticipantFromConference__return);
        return _removeParticipantFromConference__return;
    }

    /**
     * Set conference auto layout
     *
     * @param userToken
     * @param confId
     * @param isAutoLayout
     * @return
     */
    public JWebResult setConferenceAutoLayout(final String userToken,
                                              final String confId,
                                              final Boolean isAutoLayout) {
        System.out.println("Invoking setConferenceAutoLayout...");
        logger.info("Invoking setConferenceAutoLayout...");
        final JCredentials _setConferenceAutoLayout_credentials = new JCredentials();
        _setConferenceAutoLayout_credentials.setUserToken(userToken);
        final String _setConferenceAutoLayout_confId = confId;
        final Boolean _setConferenceAutoLayout_isAutoLayout = isAutoLayout;
        final JWebResult _setConferenceAutoLayout__return = port
                .setAutoLayout(_setConferenceAutoLayout_credentials,
                               _setConferenceAutoLayout_confId,
                               _setConferenceAutoLayout_isAutoLayout);
        System.out.println("setConferenceLayout.result="
                + _setConferenceAutoLayout__return);
        return _setConferenceAutoLayout__return;
    }

    /**
     * Set conference layout
     *
     * @param userToken
     * @param confId
     * @param layout
     * @return
     */
    public JWebResult setConferenceLayout(final String userToken,
                                          final String confId,
                                          final String layout) {
        System.out.println("Invoking setConferenceLayout...");
        logger.info("Invoking setConferenceLayout...");
        final JCredentials _setConferenceLayout_credentials = new JCredentials();
        _setConferenceLayout_credentials.setUserToken(userToken);
        final String _setConferenceLayout_confId = confId;
        final String _setConferenceLayout_layout = layout;
        final JWebResult _setConferenceLayout__return = port
                .setConferenceLayout(_setConferenceLayout_credentials,
                                     _setConferenceLayout_confId,
                                     JSchedVideoLayout
                                             .fromValue(_setConferenceLayout_layout));
        System.out.println("setConferenceLayout.result="
                + _setConferenceLayout__return);
        return _setConferenceLayout__return;
    }

    /**
     * Mute or un-mute the participant's audio connection
     *
     * @param userToken
     * @param confId
     * @param partId
     * @param connectionState
     * @return
     */
    public JWebResult setParticipantAudioMute(final String userToken,
                                              final String confId,
                                              final String partId,
                                              final boolean connectionState) {
        System.out.println("Invoking setParticipantAudioMute...");
        logger.info("Invoking setParticipantAudioMute...");
        final JCredentials _setParticipantAudioMute_credentials = new JCredentials();
        _setParticipantAudioMute_credentials.setUserToken(userToken);
        final String _setParticipantAudioMute_confId = confId;
        final String _setParticipantAudioMute_partId = partId;
        final boolean _setParticipantAudioMute_connectionState = connectionState;
        final JWebResult _setParticipantAudioMute__return = port
                .setParticipantAudioMute(_setParticipantAudioMute_credentials,
                                         _setParticipantAudioMute_confId,
                                         _setParticipantAudioMute_partId,
                                         _setParticipantAudioMute_connectionState);
        System.out.println("setParticipantAudioMute.result="
                + _setParticipantAudioMute__return);
        return _setParticipantAudioMute__return;
    }

    /**
     * Set the participant's connect status
     *
     * @param userToken
     * @param confId
     * @param partId
     * @param connectionState
     * @return
     */
    public JWebResult
            setParticipantConnectStatus(final String userToken,
                                        final String confId,
                                        final String partId,
                                        final boolean connectionState) {
        System.out.println("Invoking setParticipantConnectStatus...");
        logger.info("Invoking setParticipantConnectStatus...");
        final JCredentials _setParticipantConnectStatus_credentials = new JCredentials();
        _setParticipantConnectStatus_credentials.setUserToken(userToken);
        final String _setParticipantConnectStatus_confId = confId;
        final String _setParticipantConnectStatus_partId = partId;
        final boolean _setParticipantConnectStatus_connectionState = connectionState;
        final JWebResult _setParticipantConnectStatus__return = port
                .setParticipantConnection(_setParticipantConnectStatus_credentials,
                                          _setParticipantConnectStatus_confId,
                                          _setParticipantConnectStatus_partId,
                                          _setParticipantConnectStatus_connectionState);
        System.out.println("setParticipantConnectStatus.result="
                + _setParticipantConnectStatus__return);
        return _setParticipantConnectStatus__return;
    }

    /**
     * Mute or un-mute the participant's video connection
     *
     * @param userToken
     * @param confId
     * @param partId
     * @param connectionState
     * @return
     */
    public JWebResult setParticipantVideoMute(final String userToken,
                                              final String confId,
                                              final String partId,
                                              final boolean connectionState) {
        System.out.println("Invoking setParticipantVideoMute...");
        logger.info("Invoking setParticipantVideoMute...");
        final JCredentials _setParticipantVideoMute_credentials = new JCredentials();
        _setParticipantVideoMute_credentials.setUserToken(userToken);
        final String _setParticipantVideoMute_confId = confId;
        final String _setParticipantVideoMute_partId = partId;
        final boolean _setParticipantVideoMute_connectionState = connectionState;
        final JWebResult _setParticipantVideoMute__return = port
                .setParticipantVideoMute(_setParticipantVideoMute_credentials,
                                         _setParticipantVideoMute_confId,
                                         _setParticipantVideoMute_partId,
                                         _setParticipantVideoMute_connectionState);
        System.out.println("setParticipantVideoMute.result="
                + _setParticipantVideoMute__return);
        return _setParticipantVideoMute__return;
    }

    /**
     * Manage the conference
     *
     * @param userToken
     * @param confId
     * @return
     */
    public JWebResult startWatchingConference(final String userToken,
                                              final String confId) {
        System.out.println("Invoking startWatchingConference...");
        logger.info("Invoking startWatchingConference...");
        final JCredentials _startWatchingConference_credentials = new JCredentials();
        _startWatchingConference_credentials.setUserToken(userToken);
        final String _startWatchingConference_cmaConferenceId = confId;
        final JWebResult _startWatchingConference__return = port
                .startWatchingConference(_startWatchingConference_credentials,
                                         _startWatchingConference_cmaConferenceId);
        System.out.println("startWatchingConference.result="
                + _startWatchingConference__return);
        return _startWatchingConference__return;
    }

    /**
     * Stop managing the conference
     *
     * @param userToken
     * @param confId
     * @return
     */
    public JWebResult stopWatchingConference(final String userToken,
                                             final String confId) {
        System.out.println("Invoking stopWatchingConference...");
        logger.info("Invoking stopWatchingConference...");
        final JCredentials _stopWatchingConference_credentials = new JCredentials();
        _stopWatchingConference_credentials.setUserToken(userToken);
        final String _stopWatchingConference_cmaConferenceId = confId;
        final JWebResult _stopWatchingConference__return = port
                .stopWatchingConference(_stopWatchingConference_credentials,
                                        _stopWatchingConference_cmaConferenceId);
        System.out.println("stopWatchingConference.result="
                + _stopWatchingConference__return);
        return _stopWatchingConference__return;
    }

    /**
     * Terminate the conference
     *
     * @param userToken
     * @param confId
     * @return
     */
    public JWebResult terminateConference(final String userToken,
                                          final String confId) {
        System.out.println("Invoking terminateConference...");
        logger.info("Invoking terminateConference...");
        final JCredentials _terminateConference_credentials = new JCredentials();
        _terminateConference_credentials.setUserToken(userToken);
        final String _terminateConference_confId = confId;
        final JWebResult _terminateConference__return = port
                .terminateConference(_terminateConference_credentials,
                                     _terminateConference_confId);
        System.out.println("terminateConference.result="
                + _terminateConference__return);
        return _terminateConference__return;
    }
}
