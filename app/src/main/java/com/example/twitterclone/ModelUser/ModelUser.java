package com.example.twitterclone.ModelUser;

public class ModelUser {

    private String name;
    private String user;
    private String mail;
    private String UID;
    private String URL_image;

    public ModelUser(String name, String user, String mail, String UID, String URL_image) {
        this.name = name;
        this.user = user;
        this.mail = mail;
        this.UID = UID;
        this.URL_image = URL_image;
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
