package com.unb.meau.objects;

import java.util.Date;
import java.util.HashMap;

public class Chat {

    private HashMap<String, Boolean> users;
    private HashMap<String, String> usersNames;
    private HashMap<String, String> photos;
    private HashMap<String, Boolean> visualized;
    private String lastMessage;
    private Date lastMessageDate;

    public Chat() {
    } // Needed for Firebase

    public Chat(HashMap<String, Boolean> users, HashMap<String, String> usersNames, HashMap<String, String> photos, HashMap<String, Boolean> visualized, String lastMessage, Date lastMessageDate) {
        this.users = users;
        this.usersNames = usersNames;
        this.photos = photos;
        this.visualized = visualized;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
    }

    public HashMap<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, Boolean> users) {
        this.users = users;
    }

    public HashMap<String, String> getUsersNames() {
        return usersNames;
    }

    public void setUsersNames(HashMap<String, String> usersNames) {
        this.usersNames = usersNames;
    }

    public HashMap<String, String> getPhotos() {
        return photos;
    }

    public void setPhotos(HashMap<String, String> photos) {
        this.photos = photos;
    }

    public HashMap<String, Boolean> getVisualized() {
        return visualized;
    }

    public void setVisualized(HashMap<String, Boolean> visualized) {
        this.visualized = visualized;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }
}
