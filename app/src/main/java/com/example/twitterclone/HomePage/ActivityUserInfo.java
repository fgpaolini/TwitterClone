package com.example.twitterclone.HomePage;

import static com.example.twitterclone.HomePage.HomeFragment.ALL_POSTS;
import static com.example.twitterclone.MainActivity.LOGGED_USER;
import static com.example.twitterclone.MainActivity.MDATABASE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.twitterclone.AdpTweet.AdpTweet;
import com.example.twitterclone.MainActivity;
import com.example.twitterclone.ModelUser.ModelUser;
import com.example.twitterclone.ModelUser.TweetModel;
import com.example.twitterclone.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityUserInfo extends AppCompatActivity {


    private ImageView ivPhotoUser;
    private Button btLogout;
    private TextView etName, etDesctiption, etUser;
    private ArrayList<TweetModel> users_tweets, lists_retweets;
    private String UID;
    private ModelUser user;
    private TabLayout postsTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Bundle extras = getIntent().getExtras();
        UID = extras.getString("user_UID");

        ivPhotoUser = findViewById(R.id.ivProfileUserPic);
        btLogout = findViewById(R.id.btLogOut);

        etName = findViewById(R.id.profileUserName);
        etUser = findViewById(R.id.profileUser);
        etDesctiption = findViewById(R.id.profileDescription);
        postsTab = findViewById(R.id.postsTab);

        users_tweets = new ArrayList<>();
        lists_retweets = new ArrayList<>();
        fill_logged_user();
        fill_user_posts();

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        postsTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    createRecycleViewMyTweets(users_tweets);
                } else if (tab.getPosition() == 1) {
                    createRecycleViewMyTweets(lists_retweets);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void fill_logged_user() {
        user = new ModelUser("NO_NAME", "NO_USER", "NO_MAIL", "NO_UID", "NO_URL","NO_DESCRIPTION");
        MDATABASE.child("User").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                user.setUID(snapshot.getKey());

                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().equals("name")) {
                        user.setName(data.getValue().toString());
                    } else if (data.getKey().equals("email")) {
                        user.setMail(data.getValue().toString());
                    } else if (data.getKey().equals("pic")) {
                        user.setURL_image(data.getValue().toString());
                    } else if (data.getKey().equals("user")) {
                        user.setUser(data.getValue().toString());
                    } else if (data.getKey().equals("description")) {
                        user.setUser_description(data.getValue().toString());
                    }
                }
                Uri profile_photo = Uri.parse(user.getURL_image());
                Glide.with(ActivityUserInfo.this).load(String.valueOf(profile_photo)).into(ivPhotoUser);
                etName.setText("Nombre: " + user.getName());
                etUser.setText("Usuario: " + user.getUser());
                etDesctiption.setText("Descripcion: " + user.getUser_description());

                fillRetweetPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fill_user_posts(){

        MDATABASE.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot post: snapshot.getChildren()){

                    if(!post.getKey().equals("postCount")){

                        String id_post = "";
                        String user_url_profile = "";
                        String user_poster = "";
                        String user_name = "";
                        String user_uid = "";
                        String content_post = "";
                        String image_url = "";
                        String post_time = "";
                        int number_comments = 0;
                        ArrayList<String> users_liked = new ArrayList<>();
                        ArrayList<String> users_retweet = new ArrayList<>();

                        for(DataSnapshot data: post.getChildren()){

                            if (data.getKey().equals("User")) {
                                user_poster = data.getValue().toString();
                            }

                            else if (data.getKey().equals("name")) {
                                user_name = data.getValue().toString();
                            }

                            else if (data.getKey().equals("comments")) {
                                for(DataSnapshot counting: data.getChildren()){
                                    if(!counting.getKey().equals("count_comments")){
                                        number_comments++;
                                    }
                                }
                            }
                            else if (data.getKey().equals("UID")) {
                                user_uid = data.getValue().toString();
                            }

                            else if (data.getKey().equals("postTime")) {

                                post_time = data.getValue().toString();
                            }
                            else if (data.getKey().equals("textPost")) {
                                content_post = data.getValue().toString();
                            }
                            else if (data.getKey().equals("Image1")) {
                                image_url = data.getValue().toString();
                            }
                            else if (data.getKey().equals("pic")){
                                user_url_profile = data.getValue().toString();
                            }
                            else if (data.getKey().equals("liked_users")){
                                for(DataSnapshot user_liked: data.getChildren()){
                                    String checked_is_liked = user_liked.getValue().toString();
                                    if(checked_is_liked.equals("true")){
                                        users_liked.add(user_liked.getKey());
                                    }
                                }
                            }
                            else if (data.getKey().equals("retweet_users")){
                                for(DataSnapshot user_retweet: data.getChildren()){
                                    String checked_is_retweeted = user_retweet.getValue().toString();
                                    if(checked_is_retweeted.equals("true")){
                                        users_retweet.add(user_retweet.getKey());
                                    }
                                }
                            }


                        }
                        if(user_uid.equals(user.getUID())){
                            id_post = post.getKey();
                            users_tweets.add(new TweetModel(id_post, user_poster, user_name, user_uid,post_time, content_post, image_url, user_url_profile, users_liked, number_comments, users_retweet));
                        }
                    }

                }

                createRecycleViewMyTweets(users_tweets);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void fillRetweetPost(){
        for(TweetModel tweet: ALL_POSTS){
            for(String userCheck: tweet.getUsers_retweet()){
                if(userCheck.equals(user.getUID())){
                    lists_retweets.add(tweet);
                }
            }
        }
    }

    public void createRecycleViewMyTweets(ArrayList<TweetModel> list_to_show){
        AdpTweet adpShop_adaptor_2 = new AdpTweet(ActivityUserInfo.this, list_to_show);
        RecyclerView recyclerViewPopular = findViewById(R.id.rvProfileTweets);
        recyclerViewPopular.setLayoutManager(new GridLayoutManager(ActivityUserInfo.this,1));
        recyclerViewPopular.setAdapter(adpShop_adaptor_2);
    }

}