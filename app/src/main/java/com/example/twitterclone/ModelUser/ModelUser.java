package com.example.twitterclone.ModelUser;

public class ModelUser {

    private String name;
    private String user;
    private String mail;
    private String UID;

    public ModelUser(String name, String user, String mail, String UID) {
        this.name = name;
        this.user = user;
        this.mail = mail;
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    @Override
    public String toString() {
        return "ModelUser{" +
                "name='" + name + '\'' +
                ", user='" + user + '\'' +
                ", mail='" + mail + '\'' +
                ", UID='" + UID + '\'' +
                '}';
    }
}
