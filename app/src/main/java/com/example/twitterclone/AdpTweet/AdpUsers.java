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
import com.example.twitterclone.ModelUser.ModelUser;
import com.example.twitterclone.R;

import java.util.ArrayList;

public class AdpUsers extends RecyclerView.Adapter<AdpUsers.ViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<ModelUser> list_user;

    public AdpUsers(Context context, ArrayList<ModelUser> list_user) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);;
        this.list_user = list_user;
    }

    @NonNull
    @Override
    public AdpUsers.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.resource_user_search, null);
        return new AdpUsers.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdpUsers.ViewHolder holder, int position) {
        holder.bindData(list_user.get(position));
        ModelUser user_to_enter = list_user.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), ActivityUserInfo.class);
                i.putExtra("user_UID", user_to_enter.getUID());
                v.getContext().startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list_user.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvUserName, userDescription;
        ImageView ivProfile;



        //Recogera componentes del layout
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvName = itemView.findViewById(R.id.user_name);
            tvUserName = itemView.findViewById(R.id.user_name_2);
            ivProfile = itemView.findViewById(R.id.post_search_user_image);
            userDescription = itemView.findViewById(R.id.profileDescription);
        }

        //Pondra la informacion al objeto
        public void bindData(@NonNull ModelUser user){
            tvName.setText(user.getName());
            tvUserName.setText(user.getUser());
            userDescription.setText(user.getUser_description());
            Uri profile_photo = Uri.parse(user.getURL_image());
            Glide.with(itemView).load(String.valueOf(profile_photo)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivProfile);
        }
    }
}
