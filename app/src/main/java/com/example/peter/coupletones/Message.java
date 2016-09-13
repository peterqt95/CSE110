package com.example.peter.coupletones;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Samuel on 5/6/2016.
 */
public class Message {
    String senderID;
    String receiverID;
    String message;
    double lat;
    double lng;
    String locName;

    public Message() {}

    public Message(String toID, String fromID, String messageContent, double lng, double lat,
                   String locName) {
        this.senderID = fromID;
        this.receiverID = toID;
        this.message = messageContent;
        this.lat = lat;
        this.lng = lng;
        this.locName = locName;
    }

    @JsonProperty("senderID")
    public String getSenderID() {
        return senderID;
    }

    @JsonProperty("receiverID")
    public String getReceiverID() {
        return receiverID;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("lng")
    public double getLng() {
        return lng;
    }
    @JsonProperty("lat")
    public double getLat() {
        return lat;
    }
    @JsonProperty("locName")
    public String getLocName() {
        return locName;
    }
}

