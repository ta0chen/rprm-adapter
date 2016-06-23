package com.polycom.webservices.cdr.bean;

public class EndpointUsageCdr {
    private String name;
    private String model;
    private String ipAddress;
    private String ISDNNumber1;
    private String ISDNNumber2;
    private String serialNumber;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String callDuration;
    private String accountNumber;
    private String remoteSystemName;
    private String callNumber1;
    private String callNumber2;
    private String transportType;
    private String callRate;
    private String systemManufacturer;
    private String callDirection;
    private String conferenceId;
    private String callId;
    private String h320Channels;
    private String endpointAlias;
    private String endpointAdditionalAlias;
    private String endpointType;
    private String endpointTransportAddress;
    private String audioProtocolTx;
    private String audioProtocolRx;
    private String videoProtocolTx;
    private String videoProtocolRx;
    private String videoFormatTx;
    private String videoFormatRx;
    private String precedenceLevel;
    private String peopleMins;
    private String peoplecountcallbegin;
    private String peoplecountpeakvalue;
    private String peoplecountcallend;
    private String disconnectInfo;
    private String q850Cause;
    private String totalH320Errors;
    private String avgPercentPacketLossTx;
    private String avgPercentPacketLossRx;
    private String avgPacketsLostTx;
    private String avgPacketsLostRx;
    private String avgLatencyTx;
    private String avgLatencyRx;
    private String maxLatencyTx;
    private String maxLatencyRx;
    private String avgJitterTx;
    private String avgJitterRx;
    private String maxJitterTx;
    private String maxJitterRx;
    private String area;
    private String type;

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAudioProtocolRx() {
        return audioProtocolRx;
    }

    public String getAudioProtocolTx() {
        return audioProtocolTx;
    }

    public String getAvgJitterRx() {
        return avgJitterRx;
    }

    public String getAvgJitterTx() {
        return avgJitterTx;
    }

    public String getAvgLatencyRx() {
        return avgLatencyRx;
    }

    public String getAvgLatencyTx() {
        return avgLatencyTx;
    }

    public String getAvgPacketsLostRx() {
        return avgPacketsLostRx;
    }

    public String getAvgPacketsLostTx() {
        return avgPacketsLostTx;
    }

    public String getAvgPercentPacketLossRx() {
        return avgPercentPacketLossRx;
    }

    public String getAvgPercentPacketLossTx() {
        return avgPercentPacketLossTx;
    }

    public String getCallDirection() {
        return callDirection;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public String getCallId() {
        return callId;
    }

    public String getCallNumber1() {
        return callNumber1;
    }

    public String getCallNumber2() {
        return callNumber2;
    }

    public String getCallRate() {
        return callRate;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public String getDisconnectInfo() {
        return disconnectInfo;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndpointAdditionalAlias() {
        return endpointAdditionalAlias;
    }

    public String getEndpointAlias() {
        return endpointAlias;
    }

    public String getEndpointTransportAddress() {
        return endpointTransportAddress;
    }

    public String getEndpointType() {
        return endpointType;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getH320Channels() {
        return h320Channels;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getISDNNumber1() {
        return ISDNNumber1;
    }

    public String getISDNNumber2() {
        return ISDNNumber2;
    }

    public String getMaxJitterRx() {
        return maxJitterRx;
    }

    public String getMaxJitterTx() {
        return maxJitterTx;
    }

    public String getMaxLatencyRx() {
        return maxLatencyRx;
    }

    public String getMaxLatencyTx() {
        return maxLatencyTx;
    }

    public String getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public String getPeopleCountCallBegin() {
        return peoplecountcallbegin;
    }

    public String getPeopleCountCallEnd() {
        return peoplecountcallend;
    }

    public String getPeopleCountPeakValue() {
        return peoplecountpeakvalue;
    }

    public String getPeopleMins() {
        return peopleMins;
    }

    public String getPrecedenceLevel() {
        return precedenceLevel;
    }

    public String getQ850Cause() {
        return q850Cause;
    }

    public String getRemoteSystemName() {
        return remoteSystemName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getSystemManufacturer() {
        return systemManufacturer;
    }

    public String getTotalH320Errors() {
        return totalH320Errors;
    }

    public String getTransportType() {
        return transportType;
    }

    public String getVideoFormatRx() {
        return videoFormatRx;
    }

    public String getVideoFormatTx() {
        return videoFormatTx;
    }

    public String getVideoProtocolRx() {
        return videoProtocolRx;
    }

    public String getVideoProtocolTx() {
        return videoProtocolTx;
    }

    public String getArea() {
    	return area;
    }
    
    public String getType() {
    	return type;
    }
    
    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAudioProtocolRx(final String audioProtocolRx) {
        this.audioProtocolRx = audioProtocolRx;
    }

    public void setAudioProtocolTx(final String audioProtocolTx) {
        this.audioProtocolTx = audioProtocolTx;
    }

    public void setAvgJitterRx(final String avgJitterRx) {
        this.avgJitterRx = avgJitterRx;
    }

    public void setAvgJitterTx(final String avgJitterTx) {
        this.avgJitterTx = avgJitterTx;
    }

    public void setAvgLatencyRx(final String avgLatencyRx) {
        this.avgLatencyRx = avgLatencyRx;
    }

    public void setAvgLatencyTx(final String avgLatencyTx) {
        this.avgLatencyTx = avgLatencyTx;
    }

    public void setAvgPacketsLostRx(final String avgPacketsLostRx) {
        this.avgPacketsLostRx = avgPacketsLostRx;
    }

    public void setAvgPacketsLostTx(final String avgPacketsLostTx) {
        this.avgPacketsLostTx = avgPacketsLostTx;
    }

    public void setAvgPercentPacketLossRx(final String avgPercentPacketLossRx) {
        this.avgPercentPacketLossRx = avgPercentPacketLossRx;
    }

    public void setAvgPercentPacketLossTx(final String avgPercentPacketLossTx) {
        this.avgPercentPacketLossTx = avgPercentPacketLossTx;
    }

    public void setCallDirection(final String callDirection) {
        this.callDirection = callDirection;
    }

    public void setCallDuration(final String callDuration) {
        this.callDuration = callDuration;
    }

    public void setCallId(final String callId) {
        this.callId = callId;
    }

    public void setCallNumber1(final String callNumber1) {
        this.callNumber1 = callNumber1;
    }

    public void setCallNumber2(final String callNumber2) {
        this.callNumber2 = callNumber2;
    }

    public void setCallRate(final String callRate) {
        this.callRate = callRate;
    }

    public void setConferenceId(final String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public void setDisconnectInfo(final String disconnectInfo) {
        this.disconnectInfo = disconnectInfo;
    }

    public void setEndDate(final String endDate) {
        this.endDate = endDate;
    }

    public void
            setEndpointAdditionalAlias(final String endpointAdditionalAlias) {
        this.endpointAdditionalAlias = endpointAdditionalAlias;
    }

    public void setEndpointAlias(final String endpointAlias) {
        this.endpointAlias = endpointAlias;
    }

    public void
            setEndpointTransportAddress(final String endpointTransportAddress) {
        this.endpointTransportAddress = endpointTransportAddress;
    }

    public void setEndpointType(final String endpointType) {
        this.endpointType = endpointType;
    }

    public void setEndTime(final String endTime) {
        this.endTime = endTime;
    }

    public void setH320Channels(final String h320Channels) {
        this.h320Channels = h320Channels;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setISDNNumber1(final String iSDNNumber1) {
        ISDNNumber1 = iSDNNumber1;
    }

    public void setISDNNumber2(final String iSDNNumber2) {
        ISDNNumber2 = iSDNNumber2;
    }

    public void setMaxJitterRx(final String maxJitterRx) {
        this.maxJitterRx = maxJitterRx;
    }

    public void setMaxJitterTx(final String maxJitterTx) {
        this.maxJitterTx = maxJitterTx;
    }

    public void setMaxLatencyRx(final String maxLatencyRx) {
        this.maxLatencyRx = maxLatencyRx;
    }

    public void setMaxLatencyTx(final String maxLatencyTx) {
        this.maxLatencyTx = maxLatencyTx;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setPeopleCountCallBegin(final String peoplecountcallbegin) {
        this.peoplecountcallbegin = peoplecountcallbegin;
    }

    public void setPeopleCountCallEnd(final String peoplecountcallend) {
        this.peoplecountcallend = peoplecountcallend;
    }

    public void setPeopleCountPeakValue(final String peoplecountpeakvalue) {
        this.peoplecountpeakvalue = peoplecountpeakvalue;
    }

    public void setPeopleMins(final String peopleMins) {
        this.peopleMins = peopleMins;
    }

    public void setPrecedenceLevel(final String precedenceLevel) {
        this.precedenceLevel = precedenceLevel;
    }

    public void setQ850Cause(final String q850Cause) {
        this.q850Cause = q850Cause;
    }

    public void setRemoteSystemName(final String remoteSystemName) {
        this.remoteSystemName = remoteSystemName;
    }

    public void setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setStartDate(final String startDate) {
        this.startDate = startDate;
    }

    public void setStartTime(final String startTime) {
        this.startTime = startTime;
    }

    public void setSystemManufacturer(final String systemManufacturer) {
        this.systemManufacturer = systemManufacturer;
    }

    public void setTotalH320Errors(final String totalH320Errors) {
        this.totalH320Errors = totalH320Errors;
    }

    public void setTransportType(final String transportType) {
        this.transportType = transportType;
    }

    public void setVideoFormatRx(final String videoFormatRx) {
        this.videoFormatRx = videoFormatRx;
    }

    public void setVideoFormatTx(final String videoFormatTx) {
        this.videoFormatTx = videoFormatTx;
    }

    public void setVideoProtocolRx(final String videoProtocolRx) {
        this.videoProtocolRx = videoProtocolRx;
    }

    public void setVideoProtocolTx(final String videoProtocolTx) {
        this.videoProtocolTx = videoProtocolTx;
    }

    public void setArea(final String area) {
    	this.area = area;
    }

    public void setType(final String type) {
    	this.type = type;
    }
    
    @Override
    public String toString() {
        return "EndpointUsageCdr [name=" + name + ", model=" + model
                + ", ipAddress=" + ipAddress + ", startDate=" + startDate
                + ", endDate=" + endDate + "]";
    }

}
