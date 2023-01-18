package com.example.twitterclone.AdpTweet;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.twitterclone.ModelUser.TweetModel;
import com.example.twitterclone.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdpTweet extends RecyclerView.Adapter<AdpTweet.ViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<TweetModel> list_post;

    public AdpTweet(Context context, ArrayList<TweetModel> list_post) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);;
        this.list_post = list_post;
    }

    @NonNull
    @Override
    public AdpTweet.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.tweet_resource, null);
        return new AdpTweet.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdpTweet.ViewHolder holder, int position) {
        holder.bindData(list_post.get(position));
    }

    @Override
    public int getItemCount() {
        return list_post.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvUser, tvTweet;
        ImageView ivProfile,ivImage;
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();

        //Recogera componentes del layout
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvName = itemView.findViewById(R.id.tweet_post_name);
            tvUser = itemView.findViewById(R.id.tweet_post_user);
            tvTweet = itemView.findViewById(R.id.tweet_post);
            ivProfile = itemView.findViewById(R.id.post_profile_user);
            ivImage = itemView.findViewById(R.id.tweet_post_image);
        }

        //Pondra la informacion al objeto
        public void bindData(@NonNull TweetModel tweet){
            tvName.setText(tweet.getUser_poster());
            tvUser.setText(tweet.getUser_poster());
            tvTweet.setText(tweet.getContent_post());
            Uri profile_photo = Uri.parse(tweet.getUser_url_profile());
            Glide.with(itemView).load(String.valueOf(profile_photo)).into(ivProfile);
            if(!tweet.getImage_url().equals("")){
                Uri post_photo = Uri.parse(tweet.getUser_url_profile());
                Glide.with(itemView).load(String.valueOf(post_photo)).into(ivImage);
            }
            else{
                ivImage.setVisibility(itemView.GONE);
            }
        }
    }
}
