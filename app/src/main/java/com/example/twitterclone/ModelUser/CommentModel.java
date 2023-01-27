package com.example.twitterclone.ModelUser;

import java.util.ArrayList;

public class CommentModel {

    private String main_post_id;
    private String comment_id;
    private String user_comment;
    private String user_uid;
    private String user_photo_url;
    private String comment_user_name;

    private String comment_user_arroba;
    private ArrayList<String> liked_users_comment;

    public CommentModel(String main_post_id, String comment_id, String user_uid, String comment_user_name, String comment_user_arroba, String user_comment, String user_photo_url, ArrayList<String> liked_users_comment) {
        this.comment_id = comment_id;
        this.user_comment = user_comment;
        this.comment_user_name = comment_user_name;
        this.comment_user_arroba = comment_user_arroba;
        this.user_photo_url = user_photo_url;
        this.liked_users_comment = liked_users_comment;
        this.user_uid = user_uid;
        this.main_post_id = main_post_id;
    }

    public String getMain_post_id() {
        return main_post_id;
    }

    public void setMain_post_id(String main_post_id) {
        this.main_post_id = main_post_id;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public String getComment_user_name() {
        return comment_user_name;
    }

    public void setComment_user_name(String comment_user_name) {
        this.comment_user_name = comment_user_name;
    }

    public String getComment_user_arroba() {
        return comment_user_arroba;
    }

    public void setComment_user_arroba(String comment_user_arroba) {
        this.comment_user_arroba = comment_user_arroba;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser_comment() {
        return user_comment;
    }

    public void setUser_comment(String user_comment) {
        this.user_comment = user_comment;
    }

    public String getUser_photo_url() {
        return user_photo_url;
    }

    public void setUser_photo_url(String user_photo_url) {
        this.user_photo_url = user_photo_url;
    }

    public ArrayList<String> getLiked_users_comment() {
        return liked_users_comment;
    }

    public void setLiked_users_comment(ArrayList<String> liked_users_comment) {
        this.liked_users_comment = liked_users_comment;
    }
}
