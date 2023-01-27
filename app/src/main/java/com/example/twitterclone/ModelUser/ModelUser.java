package com.example.twitterclone.ModelUser;

public class ModelUser {

    private String name;
    private String user;
    private String mail;
    private String UID;
    private String URL_image;
    private String user_description;
    private boolean isVerified;

    public ModelUser(String name, String user, String mail, boolean isVerified,  String UID, String URL_image, String user_description) {
        this.name = name;
        this.user = user;
        this.mail = mail;
        this.isVerified = isVerified;
        this.UID = UID;
        this.URL_image = URL_image;
        this.user_description = user_description;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
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

    public String getURL_image() {
        return URL_image;
    }

    public void setURL_image(String URL_image) {
        this.URL_image = URL_image;
    }

    public String getUser_description() {
        return user_description;
    }

    public void setUser_description(String user_description) {
        this.user_description = user_description;
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
