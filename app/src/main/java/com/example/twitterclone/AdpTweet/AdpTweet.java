package com.example.twitterclone.AdpTweet;

import static com.example.twitterclone.MainActivity.LOGGED_USER;
import static com.example.twitterclone.MainActivity.MDATABASE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.twitterclone.HomePage.ActivityUserInfo;
import com.example.twitterclone.HomePage.CommentActivity;
import com.example.twitterclone.HomePage.ProfileFragment;
import com.example.twitterclone.ModelUser.TweetModel;
import com.example.twitterclone.R;
import com.google.android.material.imageview.ShapeableImageView;

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
        TextView tvName, tvUser, tvTweet, tvCountComment, tvTTime, tvLiked, tvRetweeted;
        ImageView ivProfile, commentButton, verifiedCheck;

        ShapeableImageView ivImage;
        CheckBox likeButton, retweetButton;
        ConstraintLayout cardLayout;

        //Recogera componentes del layout
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvName = itemView.findViewById(R.id.tweet_post_name);
            tvUser = itemView.findViewById(R.id.tweet_post_user);
            tvLiked = itemView.findViewById(R.id.tvAmmountLiked);
            tvRetweeted = itemView.findViewById(R.id.tvAmmountRetweet);
            tvTweet = itemView.findViewById(R.id.profileDescription);
            tvTTime = itemView.findViewById(R.id.tvTweetTime);
            ivProfile = itemView.findViewById(R.id.post_profile_user);
            ivImage = itemView.findViewById(R.id.tweet_post_image);
            likeButton = itemView.findViewById(R.id.likeBtn);
            commentButton = itemView.findViewById(R.id.commentBtn);
            tvCountComment = itemView.findViewById(R.id.textCountComments);
            cardLayout = itemView.findViewById(R.id.cardViewTweet);
            retweetButton = itemView.findViewById(R.id.rtBtn);
            verifiedCheck = itemView.findViewById(R.id.verifiedMark);
        }

        //Pondra la informacion al objeto
        public void bindData(@NonNull TweetModel tweet){

            long currentTime = System.currentTimeMillis();
            TimeAdapter tweetTimeAdp = new TimeAdapter(tweet.getPost_time(), currentTime);

            String post_time = tweetTimeAdp.getTime();

            tvName.setText(tweet.getUser_name());
            tvUser.setText("@" + tweet.getUser_poster());
            tvTweet.setText(tweet.getContent_post());
            tvTTime.setText(post_time);


            //Volver a poner el foto
            Uri profile_photo = Uri.parse(tweet.getUser_url_profile());
            Glide.with(itemView).load(String.valueOf(profile_photo)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivProfile);

            //Numero posts
            if(tweet.getNumber_comments() != 0){
                tvCountComment.setVisibility(itemView.VISIBLE);
                tvCountComment.setText(Integer.toString(tweet.getNumber_comments()));
            }

            //Imagen
            if(!tweet.getImage_url().equals("")){
                ivImage.setVisibility(itemView.VISIBLE);
                Uri post_photo = Uri.parse(tweet.getImage_url());
                Glide.with(itemView).load(String.valueOf(post_photo)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivImage);
            }
            else{
                ivImage.setVisibility(itemView.GONE);
            }

            if(tweet.getUsers_liked().size() == 0){
                tvLiked.setText("");
            } else{
                //Precarga del boton del like
                for(String user: tweet.getUsers_liked()){
                    if(user.equals(LOGGED_USER.getUID())){
                        likeButton.setChecked(true);
                        break;
                    }
                    else{
                        likeButton.setChecked(false);
                    }
                }
                tvLiked.setText(Integer.toString(tweet.getUsers_liked().size()));
            }

            if(tweet.getUsers_retweet().size() == 0){
                tvRetweeted.setText("");
            } else{
                //Precarga del boton del like
                for(String user: tweet.getUsers_retweet()){
                    if(user.equals(LOGGED_USER.getUID())){
                        retweetButton.setChecked(true);
                        break;
                    }
                    else{
                        retweetButton.setChecked(false);
                    }
                }
                tvRetweeted.setText(Integer.toString(tweet.getUsers_retweet().size()));
            }

            if(tweet.getUser_uid().equals("aDuwD6lDDYM2Fl0Z6YUy3AliVZw2")){
                verifiedCheck.setVisibility(View.VISIBLE);
            }
            else if(tweet.getUser_uid().equals("jgPTFOeHctTydx2Xhr4Z8ZM04GG3")){
                verifiedCheck.setVisibility(View.VISIBLE);
            }
            else{
                verifiedCheck.setVisibility(View.GONE);
            }

            //Acciones en caso de ser clicado el like
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!likeButton.isChecked()){
                        //Android
                        ArrayList<String> liked_list_users = tweet.getUsers_liked();
                        if(liked_list_users.contains(LOGGED_USER.getUID())){
                            liked_list_users.remove(LOGGED_USER.getUID());
                        }
                        tweet.setUsers_liked(liked_list_users);

                        //Firebasae
                        MDATABASE.child("Posts").child(tweet.getId_post()).child("liked_users").child(LOGGED_USER.getUID()).setValue(false);
                    }
                    else if (likeButton.isChecked()) {
                        //Android
                        ArrayList<String> liked_list_users = tweet.getUsers_liked();
                        liked_list_users.add(LOGGED_USER.getUID());
                        tweet.setUsers_liked(liked_list_users);

                        //Firebasae
                        MDATABASE.child("Posts").child(tweet.getId_post()).child("liked_users").child(LOGGED_USER.getUID()).setValue(true);

                    }
                    if(tweet.getUsers_liked().size() == 0){
                        tvLiked.setText("");
                    } else{
                        tvLiked.setText(Integer.toString(tweet.getUsers_liked().size()));
                    }
                }
            });

            //Acciones en caso de click retweet
            retweetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!retweetButton.isChecked()){
                        //Android
                        ArrayList<String> retweet_list_users = tweet.getUsers_retweet();
                        if(retweet_list_users.contains(LOGGED_USER.getUID())){
                            retweet_list_users.remove(LOGGED_USER.getUID());
                        }
                        tweet.setUsers_liked(retweet_list_users);

                        //Firebasae
                        MDATABASE.child("Posts").child(tweet.getId_post()).child("retweet_users").child(LOGGED_USER.getUID()).setValue(false);
                    }
                    else if (retweetButton.isChecked()) {
                        //Android
                        ArrayList<String> retweet_list_users = tweet.getUsers_retweet();
                        retweet_list_users.add(LOGGED_USER.getUID());
                        tweet.setUsers_liked(retweet_list_users);

                        //Firebasae
                        MDATABASE.child("Posts").child(tweet.getId_post()).child("retweet_users").child(LOGGED_USER.getUID()).setValue(true);

                    }
                    if(tweet.getUsers_retweet().size() == 0){
                        tvRetweeted.setText("");
                    } else{
                        tvRetweeted.setText(Integer.toString(tweet.getUsers_retweet().size()));
                    }
                }
            });

            //Acciones en caso de click en el perfil
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(itemView.getContext(), ActivityUserInfo.class);
                    i.putExtra("user_UID", tweet.getUser_uid());
                    itemView.getContext().startActivity(i);
                }
            });

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(itemView.getContext(), CommentActivity.class);
                    i.putExtra("post_id", tweet.getId_post());
                    itemView.getContext().startActivity(i);
                }
            });

            cardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(itemView.getContext(), CommentActivity.class);
                    i.putExtra("post_id", tweet.getId_post());
                    itemView.getContext().startActivity(i);
                }
            });

            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Preparar la pantalla de carga
                    AlertDialog change_name_dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setCancelable(false);

                    //Preparar para agregar el layout
                    LayoutInflater inflater = LayoutInflater.from(v.getContext());
                    v = inflater.inflate(R.layout.alert_image, null);

                    //Configurando el layout en el view
                    builder.setView(v);
                    change_name_dialog = builder.create();

                    change_name_dialog.show();

                    Button btDismiss = v.findViewById(R.id.buttonDismissAlert);
                    ImageView imageBigger = v.findViewById(R.id.imageBigger);

                    Uri profile_photo = Uri.parse(tweet.getImage_url());
                    Glide.with(itemView).load(String.valueOf(profile_photo)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageBigger);

                    btDismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            change_name_dialog.dismiss();
                        }
                    });


                }
            });
        }
    }
}
