package com.example.twitterclone.IntroLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twitterclone.MainActivity;
import com.example.twitterclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity {

    private Button btLogin;
    private TextView btRegister;
    private EditText etMail, etPass;

    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btLogin = findViewById(R.id.buttonLogin);
        btRegister = findViewById(R.id.btTextRegister);
        etMail = findViewById(R.id.etCorreoLogin);
        etPass = findViewById(R.id.etPasswordLogin);

        //Con cuenta firebase
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etMail.getText().toString();
                String pass = etPass.getText().toString();

                if(email.isEmpty()){
                    Toast.makeText(LoginActivity.this, "No puede estar vacio el correo", Toast.LENGTH_SHORT).show();
                    etMail.requestFocus();
                    return;
                }
                if(pass.isEmpty()){
                    Toast.makeText(LoginActivity.this,"No puede estar vacio la contraseña", Toast.LENGTH_SHORT).show();
                    etPass.requestFocus();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            String fullName = user.getDisplayName();

                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.putExtra("UIDusuario", uid);
                            startActivity(i);

                            Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(LoginActivity.this, "Valide su correo", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Algo a fallado en login", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }
}