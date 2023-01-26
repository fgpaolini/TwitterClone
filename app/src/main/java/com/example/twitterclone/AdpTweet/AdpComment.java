package com.example.twitterclone.AdpTweet;

import static com.example.twitterclone.MainActivity.LOGGED_USER;
import static com.example.twitterclone.MainActivity.MDATABASE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.twitterclone.HomePage.ActivityUserInfo;
import com.example.twitterclone.ModelUser.CommentModel;
import com.example.twitterclone.R;

import java.util.ArrayList;

public class AdpComment extends RecyclerView.Adapter<AdpComment.ViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<CommentModel> list_comment;

    public AdpComment(Context context, ArrayList<CommentModel> list_comment) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);;
        this.list_comment = list_comment;
    }

    @NonNull
    @Override
    public AdpComment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.comment_resource, null);
        return new AdpComment.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdpComment.ViewHolder holder, int position) {
        holder.bindData(list_comment.get(position));
    }

    @Override
    public int getItemCount() {
        return list_comment.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvComment;
        ImageView ivProfile;
        CheckBox likeButton;

        //Recogera componentes del layout
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvName = itemView.findViewById(R.id.tweet_post_name);
            tvComment = itemView.findViewById(R.id.profileDescription);
            ivProfile = itemView.findViewById(R.id.post_profile_user);
            likeButton = itemView.findViewById(R.id.likeBtn);
        }

        //Pondra la informacion al objeto
        public void bindData(@NonNull CommentModel comments){
            tvName.setText(comments.getUser_comment());
            tvComment.setText(comments.getUser_comment());
            Uri profile_photo = Uri.parse(comments.getUser_photo_url());
            Glide.with(itemView).load(String.valueOf(profile_photo)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivProfile);

            if(comments.getLiked_users_comment().size() == 0){
                likeButton.setText("");
            } else{
                //Precarga del boton del like
                for(String user: comments.getLiked_users_comment()){
                    if(user.equals(LOGGED_USER.getUID())){
                        likeButton.setChecked(true);
                        break;
                    }
                    else{
                        likeButton.setChecked(false);
                    }
                }
                likeButton.setText(Integer.toString(comments.getLiked_users_comment().size()));
            }
            //Acciones en caso de ser clicado el like
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!likeButton.isChecked()){
                        //Android
                        ArrayList<String> liked_list_users = comments.getLiked_users_comment();
                        if(liked_list_users.contains(LOGGED_USER.getUID())){
                            liked_list_users.remove(LOGGED_USER.getUID());
                        }
                        comments.setLiked_users_comment(liked_list_users);

                        //Firebasae
                        MDATABASE.child("Posts").child(comments.getMain_post_id()).child("comments").child(comments.getComment_id()).child("liked_users").child(LOGGED_USER.getUID()).setValue(false);
                    }
                    else if (likeButton.isChecked()) {
                        //Android
                        ArrayList<String> liked_list_users = comments.getLiked_users_comment();
                        liked_list_users.add(LOGGED_USER.getUID());
                        comments.setLiked_users_comment(liked_list_users);

                        //Firebasae
                        MDATABASE.child("Posts").child(comments.getMain_post_id()).child("comments").child(comments.getComment_id()).child("liked_users").child(LOGGED_USER.getUID()).setValue(true);
                    }
                    if(comments.getLiked_users_comment().size() == 0){
                        likeButton.setText("");
                    } else{
                        likeButton.setText(Integer.toString(comments.getLiked_users_comment().size()));
                    }
                }
            });


            //Acciones en caso de click en el perfil
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String search_user = comments.getUser_uid();
                    Intent i = new Intent(itemView.getContext(), ActivityUserInfo.class);
                    i.putExtra("user_UID", search_user);
                    itemView.getContext().startActivity(i);
                }
            });
        }
    }
}
