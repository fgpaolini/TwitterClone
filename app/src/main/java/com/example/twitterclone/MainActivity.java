package com.example.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.twitterclone.HomePage.SearchFragment;
import com.example.twitterclone.databinding.ActivityMainBinding;
import com.example.twitterclone.HomePage.HomeFragment;
import com.example.twitterclone.HomePage.ProfileFragment;
import com.example.twitterclone.ModelUser.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity{

    public static FirebaseAuth MAUTH;
    public static StorageReference MTSTORAGE;
    public static DatabaseReference MDATABASE;
    public LinearLayout linearLayoutBar;
    public static String USER_UID;
    public static ModelUser LOGGED_USER;
    private ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se usara este fragmento al empezar
        replaceFragment(new HomeFragment());

        MAUTH = FirebaseAuth.getInstance();
        MTSTORAGE = FirebaseStorage.getInstance().getReference();
        MDATABASE = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        USER_UID = extras.getString("UIDusuario");

        //Rellena el usuario logeado
        fill_logged_user();

        //Creacion de campos para cada fragmento
        //Haber incluido en Graddle(app) viewBiding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_fragment:
                    binding.linearLayoutBar.setVisibility(View.VISIBLE);
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.search_fragment:

                    replaceFragment(new SearchFragment());
                    binding.linearLayoutBar.setVisibility(View.GONE);
                    break;
                case R.id.profile_fragment:
                    replaceFragment(new ProfileFragment());
                    binding.linearLayoutBar.setVisibility(View.GONE);
                    break;
            }
            return true;
        });
    }

    public void fill_logged_user() {
        LOGGED_USER = new ModelUser("NO_NAME", "NO_USER", "NO_MAIL", "NO_UID", "NO_URL","NO_DESCRIPTION");
        MDATABASE.child("User").child(USER_UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().equals("name")) {
                        LOGGED_USER.setName(data.getValue().toString());
                    } else if (data.getKey().equals("email")) {
                        LOGGED_USER.setMail(data.getValue().toString());
                    } else if (data.getKey().equals("pic")) {
                        LOGGED_USER.setURL_image(data.getValue().toString());
                    } else if (data.getKey().equals("user")) {
                        LOGGED_USER.setUser(data.getValue().toString());
                    } else if (data.getKey().equals("description")) {
                        LOGGED_USER.setUser_description(data.getValue().toString());
                    }
                }
                LOGGED_USER.setUID(USER_UID);
                profile = findViewById(R.id.searchProfilePic);
                Uri profile_photo = Uri.parse(LOGGED_USER.getURL_image());
                Glide.with(MainActivity.this).load(String.valueOf(profile_photo)).into(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void  replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView2, fragment);
        fragmentTransaction.commit();
    }
}