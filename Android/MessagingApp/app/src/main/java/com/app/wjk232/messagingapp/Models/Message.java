package com.app.wjk232.messagingapp.Models;

import java.io.Serializable;

public class Message implements Serializable {
    private int id;
    private String message;
    private String username;
    private String subject;
    private boolean exOnce;
    private long bornTime_ms;
    private long timeToLive_ms;
    private long orgTTL;
    public Message(){};

    public Message(int id, String username, String subject, String message, long bornTime,long timeToLive_ms) {
        this.message = message;
        this.username = username;
        this.id = id;
        this.exOnce = false;
        this.subject = subject;
        this.bornTime_ms = bornTime;
        this.timeToLive_ms = timeToLive_ms;
        return;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String name) {
        this.username = name;
    }

    public long getBornTime_ms() {
        return bornTime_ms;
    }

    public void setBornTime_ms(long bornTime_ms) {
        this.bornTime_ms = bornTime_ms;
    }

    public long getTimeToLive_ms() {
        return timeToLive_ms;
    }

    public void setTimeToLive_ms(long timeToLive_ms) {
        this.timeToLive_ms = timeToLive_ms;
    }

    public String toString() {
        return (String) username;
    }

    public void setOrgTTL(long i){
        this.orgTTL = i;
    }
    public long getOrgTTL(){
        return orgTTL;
    }
    public float percentLeftToLive(){
        float percent =  1-((System.currentTimeMillis() - bornTime_ms)/((float)timeToLive_ms));
        if (percent < 0 ) return 0;
        return percent*timeToLive_ms;
    }
}
