package com.polycom.sqa.xma.webservices.cdr.bean;

import java.util.ArrayList;
import java.util.List;

public class ConferenceUsageCdr {
    private final List<Participant> participants = new ArrayList<Participant>();
    private String                  conferenceName;
    private String                  conferenceScheduler;
    private String                  conferenceSchedulerID;
    private String                  conferenceOwner;
    private String                  conferenceOwnerID;
    private String                  conferenceAlias;
    private String                  conferenceMode;
    private String                  date;
    private String                  scheduledStart;
    private String                  scheduledStop;
    private String                  scheduledDuration;
    private String                  actualStart;
    private String                  actualStop;
    private String                  actualDuration;
    private String                  totalScheduledParticipants;
    private String                  totalActualParticipants;

    public String getActualDuration() {
        return actualDuration;
    }

    public String getActualStart() {
        return actualStart;
    }

    public String getActualStop() {
        return actualStop;
    }

    public String getConferenceAlias() {
        return conferenceAlias;
    }

    public String getConferenceMode() {
        return conferenceMode;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public String getConferenceOwner() {
        return conferenceOwner;
    }

    public String getConferenceOwnerID() {
        return conferenceOwnerID;
    }

    public String getConferenceScheduler() {
        return conferenceScheduler;
    }

    public String getConferenceSchedulerID() {
        return conferenceSchedulerID;
    }

    public String getDate() {
        return date;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public String getScheduledDuration() {
        return scheduledDuration;
    }

    public String getScheduledStart() {
        return scheduledStart;
    }

    public String getScheduledStop() {
        return scheduledStop;
    }

    public String getTotalActualParticipants() {
        return totalActualParticipants;
    }

    public String getTotalScheduledParticipants() {
        return totalScheduledParticipants;
    }

    public void setActualDuration(final String actualDuration) {
        this.actualDuration = actualDuration;
    }

    public void setActualStart(final String actualStart) {
        this.actualStart = actualStart;
    }

    public void setActualStop(final String actualStop) {
        this.actualStop = actualStop;
    }

    public void setConferenceAlias(final String conferenceAlias) {
        this.conferenceAlias = conferenceAlias;
    }

    public void setConferenceMode(final String conferenceMode) {
        this.conferenceMode = conferenceMode;
    }

    public void setConferenceName(final String conferenceName) {
        this.conferenceName = conferenceName;
    }

    public void setConferenceOwner(final String conferenceOwner) {
        this.conferenceOwner = conferenceOwner;
    }

    public void setConferenceOwnerID(final String conferenceOwnerID) {
        this.conferenceOwnerID = conferenceOwnerID;
    }

    public void setConferenceScheduler(final String conferenceScheduler) {
        this.conferenceScheduler = conferenceScheduler;
    }

    public void setConferenceSchedulerID(final String conferenceSchedulerID) {
        this.conferenceSchedulerID = conferenceSchedulerID;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public void setScheduledDuration(final String scheduledDuration) {
        this.scheduledDuration = scheduledDuration;
    }

    public void setScheduledStart(final String scheduledStart) {
        this.scheduledStart = scheduledStart;
    }

    public void setScheduledStop(final String scheduledStop) {
        this.scheduledStop = scheduledStop;
    }

    public void
            setTotalActualParticipants(final String totalActualParticipants) {
        this.totalActualParticipants = totalActualParticipants;
    }

    public
            void
            setTotalScheduledParticipants(final String totalScheduledParticipants) {
        this.totalScheduledParticipants = totalScheduledParticipants;
    }

    @Override
    public String toString() {
        return "ConferenceUsageCdr [conferenceName=" + conferenceName
                + ", conferenceSchedulerID=" + conferenceSchedulerID
                + ", conferenceOwner=" + conferenceOwner + ", conferenceMode="
                + conferenceMode + ", date=" + date + "]";
    }

}
