package com.polycom.webservices.cdr.bean;


public class ConferenceTypeCdr {
    private String date;
    private String area;
    private String scheduled;
    private String adhoc;
    private String multipoint;
    private String point2Point;
    private String gateway;
    private String embeddedMultiPoint;
    private String twoPersononMCUs;
    private String shortConference;
    private String scheduledMinutes;
    private String executedMinutes;
    private String totalParticipants;
    private String avgParticipantsinMultipoint;

    public String getAdhoc() {
        return adhoc;
    }

    public String getArea() {
        return area;
    }
    
    public String getAvgParticipantsinMultipoint() {
        return avgParticipantsinMultipoint;
    }

    public String getDate() {
        return date;
    }

    public String getEmbeddedMultiPoint() {
        return embeddedMultiPoint;
    }

    public String getExecutedMinutes() {
        return executedMinutes;
    }

    public String getGateway() {
        return gateway;
    }

    public String getMultipoint() {
        return multipoint;
    }

    public String getPoint2Point() {
        return point2Point;
    }

    public String getScheduled() {
        return scheduled;
    }

    public String getScheduledMinutes() {
        return scheduledMinutes;
    }

    public String getShortConference() {
        return shortConference;
    }

    public String getTotalParticipants() {
        return totalParticipants;
    }

    public String getTwoPersononMCUs() {
        return twoPersononMCUs;
    }

    public void setAdhoc(final String adhoc) {
        this.adhoc = adhoc;
    }

    public void setArea(final String area) {
    	this.area = area;
    }
    
    public
            void
            setAvgParticipantsinMultipoint(final String avgParticipantsinMultipoint) {
        this.avgParticipantsinMultipoint = avgParticipantsinMultipoint;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public void setEmbeddedMultiPoint(final String embeddedMultiPoint) {
        this.embeddedMultiPoint = embeddedMultiPoint;
    }

    public void setExecutedMinutes(final String executedMinutes) {
        this.executedMinutes = executedMinutes;
    }

    public void setGateway(final String gateway) {
        this.gateway = gateway;
    }

    public void setMultipoint(final String multipoint) {
        this.multipoint = multipoint;
    }

    public void setPoint2Point(final String point2Point) {
        this.point2Point = point2Point;
    }

    public void setScheduled(final String scheduled) {
        this.scheduled = scheduled;
    }

    public void setScheduledMinutes(final String scheduledMinutes) {
        this.scheduledMinutes = scheduledMinutes;
    }

    public void setShortConference(final String shortConference) {
        this.shortConference = shortConference;
    }

    public void setTotalParticipants(final String totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public void setTwoPersononMCUs(final String twoPersononMCUs) {
        this.twoPersononMCUs = twoPersononMCUs;
    }

    @Override
    public String toString() {
        return "ConferenceTypeCdr [date=" + date + ", scheduled=" + scheduled
                + ", adhoc=" + adhoc + ", scheduledMinutes=" + scheduledMinutes
                + ", executedMinutes=" + executedMinutes
                + ", totalParticipants=" + totalParticipants
                + ", avgParticipantsinMultipoint="
                + avgParticipantsinMultipoint + "]";
    }
}
