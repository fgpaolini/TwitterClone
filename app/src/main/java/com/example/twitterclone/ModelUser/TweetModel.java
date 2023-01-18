package com.example.twitterclone.ModelUser;

public class TweetModel {

    private String user_url_profile;
    private String user_poster;
    private String user_uid;
    private String content_post;
    private String image_url;

    public TweetModel(String user_poster, String user_uid, String content_post, String image_url, String user_url_profile) {
        this.user_poster = user_poster;
        this.user_uid = user_uid;
        this.content_post = content_post;
        this.image_url = image_url;
        this.user_url_profile = user_url_profile;
    }

    public String getUser_poster() {
        return user_poster;
    }

    public void setUser_poster(String user_poster) {
        this.user_poster = user_poster;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getContent_post() {
        return content_post;
    }

    public void setContent_post(String content_post) {
        this.content_post = content_post;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUser_url_profile() {
        return user_url_profile;
    }

    public void setUser_url_profile(String user_url_profile) {
        this.user_url_profile = user_url_profile;
    }

    @Override
    public String toString() {
        return "TweetModel{" +
                "user_url_profile='" + user_url_profile + '\'' +
                ", user_poster='" + user_poster + '\'' +
                ", user_uid='" + user_uid + '\'' +
                ", content_post='" + content_post + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}
