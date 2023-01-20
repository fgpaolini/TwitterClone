package com.example.twitterclone.HomePage;

import static com.example.twitterclone.MainActivity.MDATABASE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.twitterclone.AdpTweet.AdpTweet;
import com.example.twitterclone.MainActivity;
import com.example.twitterclone.ModelUser.TweetModel;
import com.example.twitterclone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment{

    private ArrayList<TweetModel> list_posts;
    private FloatingActionButton fabPostFunction;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        list_posts = new ArrayList<>();

        fabPostFunction = v.findViewById(R.id.fabPost);
        fabPostFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeFragment.this.getContext(), PostActivity.class);
                startActivity(i);
            }
        });

        //Recogida de los posts
        MDATABASE.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot post: snapshot.getChildren()){

                    if(!post.getKey().equals("postCount")){

                        String id_post = "";
                        String user_url_profile = "";
                        String user_poster = "";
                        String user_uid = "";
                        String content_post = "";
                        String image_url = "";
                        ArrayList<String> users_liked = new ArrayList<>();

                        for(DataSnapshot data: post.getChildren()){

                            if (data.getKey().equals("User")) {
                                user_poster = data.getValue().toString();
                            }
                            else if (data.getKey().equals("UID")) {
                                user_uid = data.getValue().toString();
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


                        }

                        id_post = post.getKey();
                        list_posts.add(new TweetModel(id_post, user_poster, user_uid, content_post, image_url, user_url_profile, users_liked));

                    }

                }

                createRecycleProductsA(v, list_posts);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    public void createRecycleProductsA(@NonNull View v, ArrayList<TweetModel> list_to_show){
        AdpTweet adpShop_adaptor_2 = new AdpTweet(v.getContext(), list_to_show);
        RecyclerView recyclerViewPopular = v.findViewById(R.id.recyclePosts);
        recyclerViewPopular.setLayoutManager(new GridLayoutManager(v.getContext(),1));
        recyclerViewPopular.setAdapter(adpShop_adaptor_2);
    }


}