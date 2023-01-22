package com.example.twitterclone.IntroLogin;

import static android.widget.Toast.LENGTH_LONG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twitterclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private Button btRegister;
    private EditText etName, etUser, etMail, etPass;
    private TextView etLoggin;

    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ArrayList<String> list_names;
    private ArrayList<String> list_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        list_names = new ArrayList<>();
        list_mail = new ArrayList<>();

        mDatabase.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    for(DataSnapshot data: snapshot.getChildren()){
                        String databaseName = data.child("name").getValue().toString();
                        String databaseMail = data.child("email").getValue().toString();
                        list_names.add(databaseName);
                        list_mail.add(databaseMail);
                    }
                }
                catch (NullPointerException e){
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btRegister = findViewById(R.id.buttonLogin);

        etName = findViewById(R.id.etNombreReg);
        etUser = findViewById(R.id.etUserReg);
        etMail = findViewById(R.id.etEmailReg);
        etPass = findViewById(R.id.etPassReg);

        etLoggin = findViewById(R.id.btTextRegister);

        etLoggin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    public void registerUser(){
        String name = etName.getText().toString();
        String user = etUser.getText().toString();
        String mail = etMail.getText().toString();
        String password = etPass.getText().toString();


        if(password.length() < 6){
            Toast.makeText(RegisterActivity.this,"Pon al menos 6 caracteres en la contraseña", Toast.LENGTH_SHORT).show();
            etPass.requestFocus();
            return;
        }
        if(name.isEmpty()){
            Toast.makeText(RegisterActivity.this,"No puede estar vacio el nombre!", Toast.LENGTH_SHORT).show();
            etName.requestFocus();
            return;
        }
        if(user.isEmpty()){
            Toast.makeText(RegisterActivity.this,"No puede estar vacio el usuario!", Toast.LENGTH_SHORT).show();
            etName.requestFocus();
            return;
        }
        if(mail.isEmpty()){
            Toast.makeText(RegisterActivity.this,"No puede estar vacia el mail!", Toast.LENGTH_SHORT).show();
            etMail.requestFocus();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            Toast.makeText(RegisterActivity.this, "Pon un correo valido!", Toast.LENGTH_SHORT).show();
            etMail.requestFocus();
            return;
        }

        //Comprueba que no haya users y correos que existan
        for(String check_name: list_names){
            if(name.equals(check_name)){
                Toast.makeText(RegisterActivity.this, "Nombre de usuario usado.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        for(String check_mail: list_mail){
            if(mail.equals(check_mail)){
                Toast.makeText(RegisterActivity.this, "Correo usado.", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        //Codigo despues de que los datos introducidos esten bien
        mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registrado. Valide su correo.", LENGTH_LONG).show();

                        String uid = mAuth.getCurrentUser().getUid();
                        mDatabase.child("User").child(uid).child("name").setValue(name);
                        mDatabase.child("User").child(uid).child("email").setValue(mail);
                        mDatabase.child("User").child(uid).child("user").setValue(user);
                        mDatabase.child("User").child(uid).child("description").setValue("Soy una descripcion predeterminada");

                        StorageReference ubicacionImagen = mStorage.child("User").child(uid).child("profile.png");
                        Uri imageUri = Uri.parse("android.resource://" + RegisterActivity.this.getPackageName() + "/" + R.drawable.ic_debug_user);
                        ubicacionImagen.putFile(imageUri).addOnSuccessListener(taskSnapshot -> ubicacionImagen.getDownloadUrl().addOnCompleteListener(task2 -> {
                            Uri imageURL = task2.getResult();
                            mDatabase.child("User").child(uid).child("pic").setValue(imageURL.toString());
                        }));
                        finish();

                    } else {
                        Toast.makeText(RegisterActivity.this, "Algo ha fallado!", LENGTH_LONG).show();
                    }
                });

            } else {
                Toast.makeText(RegisterActivity.this, "Algo ha fallado!", LENGTH_LONG).show();
            }
        });



    }

}