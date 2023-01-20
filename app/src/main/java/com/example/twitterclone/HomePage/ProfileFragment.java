package com.example.twitterclone.HomePage;

import static android.app.Activity.RESULT_OK;
import static com.example.twitterclone.MainActivity.LOGGED_USER;
import static com.example.twitterclone.MainActivity.MDATABASE;
import static com.example.twitterclone.MainActivity.MTSTORAGE;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.twitterclone.AdpTweet.AdpTweet;
import com.example.twitterclone.LoginActivity;
import com.example.twitterclone.MainActivity;
import com.example.twitterclone.ModelUser.TweetModel;
import com.example.twitterclone.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private ImageView ivPhotoUser;
    private Button btChangePhoto, btLogout;
    private TextView etName, etDesctiption, etUser;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ArrayList<TweetModel> my_tweets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ivPhotoUser = v.findViewById(R.id.ivProfileUserPic);
        btChangePhoto = v.findViewById(R.id.changeProfilePicBtn);
        btLogout = v.findViewById(R.id.btLogOut);

        etName = v.findViewById(R.id.profileUserName);
        etUser = v.findViewById(R.id.profileUser);
        etDesctiption = v.findViewById(R.id.profileDescription);

        my_tweets = new ArrayList<>();

        Uri profile_photo = Uri.parse(LOGGED_USER.getURL_image());
        Glide.with(ProfileFragment.this).load(String.valueOf(profile_photo)).into(ivPhotoUser);
        etName.setText(LOGGED_USER.getName());
        etUser.setText(LOGGED_USER.getUser());
        etDesctiption.setText(LOGGED_USER.getUser_description());

        //Necesario para busqueda de imagenes
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null){
                //Tras haber elegido una imagen
                Intent data_imagen = result.getData();
                Uri imageUri = data_imagen.getData();
                ContentResolver contentResolver =getActivity().getContentResolver();
                try{
                    if(Build.VERSION.SDK_INT < 28){
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
                        saveImage(imageUri, LOGGED_USER.getUID());
                        ivPhotoUser.setImageBitmap(bitmap);
                    }
                    else{
                        ImageDecoder.Source source = ImageDecoder.createSource(contentResolver,imageUri);
                        saveImage(imageUri, LOGGED_USER.getUID());
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        ivPhotoUser.setImageBitmap(bitmap);
                    }
                }
                catch (IOException e){
                    System.out.println(e.getMessage());
                }

            }
            else{
                Toast.makeText(ProfileFragment.this.getContext(),"Cancelado...",Toast.LENGTH_SHORT).show();
            }
        });

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileFragment.this.getContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        btChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1ro se busca en el movil el archivo que desea cambiar
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                activityResultLauncher.launch(i);
            }
        });


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
                        if(user_uid.equals(LOGGED_USER.getUID())){
                            id_post = post.getKey();
                            my_tweets.add(new TweetModel(id_post, user_poster, user_uid, content_post, image_url, user_url_profile, users_liked));
                        }
                    }

                }

                createRecycleProductsA(v, my_tweets);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return v;
    }

    public void createRecycleProductsA(@NonNull View v, ArrayList<TweetModel> list_to_show){
        AdpTweet adpShop_adaptor_2 = new AdpTweet(v.getContext(), list_to_show);
        RecyclerView recyclerViewPopular = v.findViewById(R.id.rvProfileTweets);
        recyclerViewPopular.setLayoutManager(new GridLayoutManager(v.getContext(),1));
        recyclerViewPopular.setAdapter(adpShop_adaptor_2);
    }

    //Creas en el Firebase Storage un nuevo imagen
    public void saveImage(Uri imageUri, String uid){

        if(imageUri != null){

            StorageReference ubicacionImagen = MTSTORAGE.child("User").child(uid).child("profile.png");
            ubicacionImagen.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ubicacionImagen.getDownloadUrl().addOnCompleteListener(task -> {
                        Uri imageURL = task.getResult();
                        MDATABASE.child("User").child(LOGGED_USER.getUID()).child("pic").setValue(imageURL.toString());
                    });
                    Toast.makeText(ProfileFragment.this.getContext(), "Imagen subido exitosamente", Toast.LENGTH_SHORT).show();
                }
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(ProfileFragment.this.getContext(), "Algo a salido mal...", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


}