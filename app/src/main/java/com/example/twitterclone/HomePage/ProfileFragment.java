package com.example.twitterclone.HomePage;

import static android.app.Activity.RESULT_OK;
import static com.example.twitterclone.HomePage.HomeFragment.ALL_POSTS;
import static com.example.twitterclone.MainActivity.LOGGED_USER;
import static com.example.twitterclone.MainActivity.MDATABASE;
import static com.example.twitterclone.MainActivity.MTSTORAGE;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.twitterclone.AdpTweet.AdpTweet;
import com.example.twitterclone.AdpTweet.TimeAdapter;
import com.example.twitterclone.IntroLogin.LoginActivity;
import com.example.twitterclone.ModelUser.TweetModel;
import com.example.twitterclone.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private ShapeableImageView ivPhotoUser;
    private Button btLogout, btChangeName, btChangeProfile;

    private ToggleButton btEditProfile;
    private ImageButton ibChangePic, nameEditPencil, userEditPencil, descriptionEditPencil;
    private TextView etName, etDesctiption, etUser;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ArrayList<TweetModel> my_tweets, lists_retweets;

    private long currentTime;

    private TimeAdapter tweetTimeAdp;

    private TabLayout postsTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ivPhotoUser = v.findViewById(R.id.ivProfileUserPic);
        btEditProfile = v.findViewById(R.id.editProfileBtn);
        ibChangePic = v.findViewById(R.id.changePicImBtn);
        btLogout = v.findViewById(R.id.btnBack);
        btChangeName = v.findViewById(R.id.btChangeUserName);
        btChangeProfile = v.findViewById(R.id.btChangeUserDescription);
        postsTab = v.findViewById(R.id.postsTab);

        nameEditPencil = v.findViewById(R.id.namePencil);
        descriptionEditPencil = v.findViewById(R.id.descriptionPencil);

        etName = v.findViewById(R.id.profileUserName);
        etUser = v.findViewById(R.id.profileUser);
        etDesctiption = v.findViewById(R.id.profileDescription);

        //currentTime = 1674321765l;
        currentTime = System.currentTimeMillis();
        my_tweets = new ArrayList<>();
        lists_retweets = new ArrayList<>();

        Uri profile_photo = Uri.parse(LOGGED_USER.getURL_image());
        Glide.with(ProfileFragment.this).load(String.valueOf(profile_photo)).into(ivPhotoUser);
        etName.setText("Nombre: " + LOGGED_USER.getName());
        etUser.setText("Usuario: " + LOGGED_USER.getUser());
        etDesctiption.setText("Descripcion: " + LOGGED_USER.getUser_description());

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

        btEditProfile.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                // The toggle is enabled

                ibChangePic.setVisibility(View.VISIBLE);
                nameEditPencil.setVisibility(View.VISIBLE);
                descriptionEditPencil.setVisibility(View.VISIBLE);
                ivPhotoUser.setStrokeWidth(10);
                ivPhotoUser.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this.getContext(), R.color.black)));


            } else {
                // The toggle is disabled
                ibChangePic.setVisibility(View.GONE);
                nameEditPencil.setVisibility(View.GONE);
                descriptionEditPencil.setVisibility(View.GONE);
                ivPhotoUser.setStrokeWidth(0);

            }
        });


        ibChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1ro se busca en el movil el archivo que desea cambiar
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                activityResultLauncher.launch(i);
            }
        });


        nameEditPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Preparar la pantalla de carga
                AlertDialog change_name_dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileFragment.this.getContext());
                builder.setCancelable(false);

                //Preparar para agregar el layout
                LayoutInflater inflater = getLayoutInflater();
                v = inflater.inflate(R.layout.alert_change_name, null);

                //Configurando el layout en el view
                builder.setView(v);
                change_name_dialog = builder.create();

                change_name_dialog.show();

                TextView tvChangeTitle;
                EditText etChange;
                Button btChange;

                tvChangeTitle = v.findViewById(R.id.titleChange);
                tvChangeTitle.setText("Cambiar Nombre");
                etChange = v.findViewById(R.id.etNameToChange);
                etChange.setHint("Nombre");
                btChange = v.findViewById(R.id.btChangeNameDatabase);

                btChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String new_name = etChange.getText().toString();
                        if(new_name.isEmpty()){
                            Toast.makeText(ProfileFragment.this.getContext(), "No puede estar el nombre vacio", Toast.LENGTH_SHORT).show();
                            etChange.requestFocus();
                            return;
                        }
                        MDATABASE.child("User").child(LOGGED_USER.getUID()).child("name").setValue(new_name);

                        //Cambio de nombre de todos los post del mismo usuario
                        MDATABASE.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot post: snapshot.getChildren()){
                                    for(DataSnapshot data_post: post.getChildren()){
                                        if(data_post.getKey().equals("UID") && data_post.getValue().toString().equals(LOGGED_USER.getUID())){
                                            MDATABASE.child("Posts").child(post.getKey()).child("name").setValue(new_name);
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        Toast.makeText(ProfileFragment.this.getContext(), "Nombre cambiado", Toast.LENGTH_SHORT).show();
                        change_name_dialog.dismiss();
                    }
                });
            }
        });

        descriptionEditPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Preparar la pantalla de carga
                AlertDialog change_name_dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileFragment.this.getContext());
                builder.setCancelable(false);

                //Preparar para agregar el layout
                LayoutInflater inflater = getLayoutInflater();
                v = inflater.inflate(R.layout.alert_change_name, null);

                //Configurando el layout en el view
                builder.setView(v);
                change_name_dialog = builder.create();

                change_name_dialog.show();

                TextView tvChangeTitle;
                EditText etChange;
                Button btChange;

                tvChangeTitle = v.findViewById(R.id.titleChange);
                tvChangeTitle.setText("Cambiar descripcion");
                etChange = v.findViewById(R.id.etNameToChange);
                etChange.setHint("Descripcion");
                btChange = v.findViewById(R.id.btChangeNameDatabase);

                btChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String new_description = etChange.getText().toString();
                        if(new_description.isEmpty()){
                            Toast.makeText(ProfileFragment.this.getContext(), "No puede estar la descricion vacio", Toast.LENGTH_SHORT).show();
                            etChange.requestFocus();
                            return;
                        } else if (new_description.length() >= 51) {
                            Toast.makeText(ProfileFragment.this.getContext(), "No puede haber mas de 50 caracteres", Toast.LENGTH_SHORT).show();
                            etChange.requestFocus();
                            return;
                        }
                        MDATABASE.child("User").child(LOGGED_USER.getUID()).child("description").setValue(new_description);

                        Toast.makeText(ProfileFragment.this.getContext(), "Descripcion cambiado", Toast.LENGTH_SHORT).show();
                        change_name_dialog.dismiss();
                    }
                });
            }
        });

        fillUsersPost(v);
        fillRetweetPost();

        postsTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    createRecycleViewMyTweets(v, my_tweets);
                } else if (tab.getPosition() == 1) {
                    createRecycleViewMyTweets(v, lists_retweets);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return v;
    }

    public void createRecycleViewMyTweets(@NonNull View v, ArrayList<TweetModel> list_to_show){
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

    public void fillUsersPost(View v){
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
                        if(user_uid.equals(LOGGED_USER.getUID())){

                            id_post = post.getKey();
                            my_tweets.add(new TweetModel(id_post, user_poster, user_name, user_uid, post_time, content_post, image_url, user_url_profile, users_liked, number_comments, users_retweet));
                        }
                    }

                }

                createRecycleViewMyTweets(v, my_tweets);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void fillRetweetPost(){
        for(TweetModel tweet: ALL_POSTS){
            for(String user: tweet.getUsers_retweet()){
                if(user.equals(LOGGED_USER.getUID())){
                    lists_retweets.add(tweet);
                }
            }
        }
    }


}