package com.example.twitterclone.HomePage;

import static com.example.twitterclone.MainActivity.LOGGED_USER;
import static com.example.twitterclone.MainActivity.MDATABASE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.twitterclone.AdpTweet.AdpComment;
import com.example.twitterclone.ModelUser.CommentModel;
import com.example.twitterclone.ModelUser.TweetModel;
import com.example.twitterclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {

    private TextView tvName, tvUser, tvTweet, tvLiked, tvRetweeted;
    private ImageView ivProfile,ivImage, ivProfileLoggedUser;
    private CheckBox likeButton, retweetButton;
    private String post_id;
    private ArrayList<CommentModel> list_comments;
    private Button btCommentar, btVolver;
    private EditText etTextoCommentar;

    private TweetModel main_tweet;
    private int number_comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Bundle extras = getIntent().getExtras();
        post_id = extras.getString("post_id");

        tvName = findViewById(R.id.tweet_post_name);
        tvUser = findViewById(R.id.tweet_post_user);
        tvLiked = findViewById(R.id.tvAmmountLiked);
        tvRetweeted = findViewById(R.id.tvAmmountRetweet);
        tvTweet = findViewById(R.id.profileDescription);
        ivProfile = findViewById(R.id.post_profile_user);
        ivProfileLoggedUser = findViewById(R.id.post_profile_userCommenting);
        ivImage = findViewById(R.id.tweet_post_image);
        likeButton = findViewById(R.id.likeBtn);
        btCommentar = findViewById(R.id.btCommentarEnviar);
        etTextoCommentar = findViewById(R.id.etCommentar);
        btVolver = findViewById(R.id.btCancel);
        retweetButton = findViewById(R.id.rtBtn);
        likeButton = findViewById(R.id.likeBtn);

        list_comments = new ArrayList<>();

        MDATABASE.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot post: snapshot.getChildren()){
                    if(!post.getKey().equals("postCount") && post.getKey().equals(post_id)){

                        String id_post = "";
                        String user_url_profile = "";
                        String user_poster = "";
                        String user_name = "";
                        String user_uid = "";
                        String content_post = "";
                        String post_time = "";
                        String image_url = "";
                        ArrayList<String> users_liked = new ArrayList<>();
                        ArrayList<String> users_retweet = new ArrayList<>();

                        for(DataSnapshot data: post.getChildren()){

                            if (data.getKey().equals("User")) {
                                user_poster = data.getValue().toString();
                            }

                            else if (data.getKey().equals("name")) {
                                user_name = data.getValue().toString();
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
                            else if (data.getKey().equals("comments")){
                                fill_list_comments();
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

                        main_tweet = new TweetModel(id_post, user_poster, user_name, user_uid, post_time, content_post, image_url, user_url_profile, users_liked, number_comments, users_retweet);

                        //Post pricipal a comentar rellenar
                        tvName.setText(main_tweet.getUser_poster());
                        tvUser.setText(main_tweet.getUser_poster());
                        tvTweet.setText(main_tweet.getContent_post());
                        Uri profile_photo = Uri.parse(main_tweet.getUser_url_profile());
                        Glide.with(CommentActivity.this).load(String.valueOf(profile_photo)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivProfile);

                        Uri profile_photo_logged_user = Uri.parse(LOGGED_USER.getURL_image());

                        Glide.with(CommentActivity.this).load(String.valueOf(profile_photo_logged_user)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivProfileLoggedUser);

                        if(!main_tweet.getImage_url().equals("")){
                            Uri post_photo = Uri.parse(image_url);
                            Glide.with(CommentActivity.this).load(String.valueOf(post_photo)).into(ivImage);
                        }
                        else{
                            ivImage.setVisibility(View.GONE);
                        }
                        //Para checkbox like
                        if(main_tweet.getUsers_liked().size() == 0){
                            tvLiked.setText("");
                        } else{
                            //Precarga del boton del like
                            for(String user: main_tweet.getUsers_liked()){
                                if(user.equals(LOGGED_USER.getUID())){
                                    likeButton.setChecked(true);
                                    break;
                                }
                                else{
                                    likeButton.setChecked(false);
                                }
                            }
                            tvLiked.setText(Integer.toString(main_tweet.getUsers_liked().size()));
                        }
                        //Para checkbox retweet
                        if(main_tweet.getUsers_retweet().size() == 0){
                            tvRetweeted.setText("");
                        } else{
                            //Precarga del boton del like
                            for(String user: main_tweet.getUsers_retweet()){
                                if(user.equals(LOGGED_USER.getUID())){
                                    retweetButton.setChecked(true);
                                    break;
                                }
                                else{
                                    retweetButton.setChecked(false);
                                }
                            }
                            tvRetweeted.setText(Integer.toString(main_tweet.getUsers_retweet().size()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!likeButton.isChecked()){
                    //Android
                    ArrayList<String> liked_list_users = main_tweet.getUsers_liked();
                    if(liked_list_users.contains(LOGGED_USER.getUID())){
                        liked_list_users.remove(LOGGED_USER.getUID());
                    }
                    main_tweet.setUsers_liked(liked_list_users);

                    //Firebasae
                    MDATABASE.child("Posts").child(post_id).child("liked_users").child(LOGGED_USER.getUID()).setValue(false);
                }
                else if (likeButton.isChecked()) {
                    //Android
                    ArrayList<String> liked_list_users = main_tweet.getUsers_liked();
                    liked_list_users.add(LOGGED_USER.getUID());
                    main_tweet.setUsers_liked(liked_list_users);

                    //Firebasae
                    MDATABASE.child("Posts").child(post_id).child("liked_users").child(LOGGED_USER.getUID()).setValue(true);

                }
                if(main_tweet.getUsers_liked().size() == 0){
                    tvLiked.setText("");
                } else{
                    tvLiked.setText(Integer.toString(main_tweet.getUsers_liked().size()));
                }
            }
        });

        //Acciones en caso de click retweet
        retweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!retweetButton.isChecked()){
                    //Android
                    ArrayList<String> retweet_list_users = main_tweet.getUsers_retweet();
                    if(retweet_list_users.contains(LOGGED_USER.getUID())){
                        retweet_list_users.remove(LOGGED_USER.getUID());
                    }
                    main_tweet.setUsers_liked(retweet_list_users);

                    //Firebasae
                    MDATABASE.child("Posts").child(main_tweet.getId_post()).child("retweet_users").child(LOGGED_USER.getUID()).setValue(false);
                }
                else if (retweetButton.isChecked()) {
                    //Android
                    ArrayList<String> retweet_list_users = main_tweet.getUsers_retweet();
                    retweet_list_users.add(LOGGED_USER.getUID());
                    main_tweet.setUsers_liked(retweet_list_users);

                    //Firebasae
                    MDATABASE.child("Posts").child(main_tweet.getId_post()).child("retweet_users").child(LOGGED_USER.getUID()).setValue(true);

                }
                if(main_tweet.getUsers_retweet().size() == 0){
                    tvRetweeted.setText("");
                } else{
                    tvRetweeted.setText(Integer.toString(main_tweet.getUsers_retweet().size()));
                }
            }
        });


        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btCommentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comentario = etTextoCommentar.getText().toString();
                if(comentario.isEmpty()){
                    Toast.makeText(CommentActivity.this, "Comentario vacio...", Toast.LENGTH_SHORT).show();
                    etTextoCommentar.requestFocus();
                    return;
                }
                else{
                    number_comments++;
                    MDATABASE.child("Posts").child(post_id).child("comments").child("comment"+number_comments).child("comment_text").setValue(comentario);
                    MDATABASE.child("Posts").child(post_id).child("comments").child("comment"+number_comments).child("pic").setValue(LOGGED_USER.getURL_image());
                    MDATABASE.child("Posts").child(post_id).child("comments").child("comment"+number_comments).child("user_comment").setValue(LOGGED_USER.getUID());
                    MDATABASE.child("Posts").child(post_id).child("comments").child("count_comments").setValue(number_comments);
                    ArrayList<String> nueva_lista_meGusta = new ArrayList<>();
                    list_comments.add(new CommentModel(post_id, "comment"+number_comments, LOGGED_USER.getUID(), comentario, LOGGED_USER.getURL_image(), nueva_lista_meGusta));
                    etTextoCommentar.setText("");
                    Toast.makeText(CommentActivity.this, "Comentado!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void fill_list_comments(){

        MDATABASE.child("Posts").child(post_id).child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot comment: snapshot.getChildren()){
                    if(!comment.getKey().equals("count_comments")){
                        //Coger datos del comentario
                        String main_post_id = "";
                        String comment_id = "";
                        String user_uid = "";
                        String user_comment = "";
                        String user_photo_url = "";
                        ArrayList<String> liked_users_comment = new ArrayList<>();

                        comment_id = comment.getKey();
                        main_post_id = post_id;
                        for(DataSnapshot comment_data: comment.getChildren()){
                            if (comment_data.getKey().equals("comment_text")) {
                                user_comment = comment_data.getValue().toString();
                            }
                            else if (comment_data.getKey().equals("pic")) {
                                user_photo_url = comment_data.getValue().toString();
                            }
                            else if (comment_data.getKey().equals("user_comment")) {
                                user_uid = comment_data.getValue().toString();
                            }
                            else if (comment_data.getKey().equals("liked_users")){
                                for(DataSnapshot user_liked: comment_data.getChildren()){
                                    String checked_is_liked = user_liked.getValue().toString();
                                    if(checked_is_liked.equals("true")){
                                        liked_users_comment.add(user_liked.getKey());
                                    }
                                }
                            }
                        }
                        list_comments.add(new CommentModel(main_post_id, comment_id, user_uid, user_comment, user_photo_url, liked_users_comment));
                    }
                    else {
                        number_comments = Integer.parseInt(comment.getValue().toString());
                    }
                }

                createRecycleProductsA(list_comments);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void createRecycleProductsA(ArrayList<CommentModel> list_to_show){
        AdpComment adpShop_adaptor_2 = new AdpComment(CommentActivity.this, list_to_show);
        RecyclerView recyclerViewPopular = findViewById(R.id.recycleComments);
        recyclerViewPopular.setLayoutManager(new GridLayoutManager(CommentActivity.this,1));
        recyclerViewPopular.setAdapter(adpShop_adaptor_2);
    }

}