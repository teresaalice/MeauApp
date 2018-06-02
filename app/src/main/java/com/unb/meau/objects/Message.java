package com.unb.meau.objects;

import java.util.Date;

public class Message {

    private String sender;
    private String text;
    private Date date;

    public Message() {
    } // Needed for Firebase

    public Message(String sender, String text, Date date) {
        this.sender = sender;
        this.text = text;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
