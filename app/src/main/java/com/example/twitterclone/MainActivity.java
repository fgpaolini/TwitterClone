package com.example.twitterclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import com.example.twitterclone.databinding.ActivityMainBinding;
import com.example.twitterclone.HomePage.HomeFragment;
import com.example.twitterclone.HomePage.PostFragment;
import com.example.twitterclone.HomePage.ProfileFragment;
import com.example.twitterclone.ModelUser.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    public static FirebaseAuth MAUTH;
    public static StorageReference MTSTORAGE;
    public static DatabaseReference MDATABASE;
    public static String USER_UID;
    public static ModelUser LOGGED_USER;

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

        //Creacion de campos para cada fragmento
        //Haber incluido en Graddle(app) viewBiding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_fragment:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.post_fragment:
                    replaceFragment(new PostFragment());
                    break;
                case R.id.profile_fragment:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView2, fragment);
        fragmentTransaction.commit();
    }
}