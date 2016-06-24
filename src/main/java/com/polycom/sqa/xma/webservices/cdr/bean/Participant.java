package com.polycom.sqa.xma.webservices.cdr.bean;

public class Participant {
    private String conferenceName;
    private String scheduledStart;
    private String scheduledStop;
    private String scheduledDuration;
    private String actualStart;
    private String actualStop;
    private String actualDuration;

    public String getActualDuration() {
        return actualDuration;
    }

    public String getActualStart() {
        return actualStart;
    }

    public String getActualStop() {
        return actualStop;
    }

    public String getConferenceName() {
        return conferenceName;
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

    public void setActualDuration(final String actualDuration) {
        this.actualDuration = actualDuration;
    }

    public void setActualStart(final String actualStart) {
        this.actualStart = actualStart;
    }

    public void setActualStop(final String actualStop) {
        this.actualStop = actualStop;
    }

    public void setConferenceName(final String conferenceName) {
        this.conferenceName = conferenceName;
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

    @Override
    public String toString() {
        return "Participant [conferenceName=" + conferenceName
                + ", scheduledStart=" + scheduledStart + ", scheduledStop="
                + scheduledStop + ", scheduledDuration=" + scheduledDuration
                + ", actualStart=" + actualStart + ", actualStop=" + actualStop
                + ", actualDuration=" + actualDuration + "]";
    }
}
