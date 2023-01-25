package com.example.twitterclone.IntroLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.twitterclone.R;

public class IntroAct extends AppCompatActivity {

    private Button btn_create_account;

    private TextView tv_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        btn_create_account = findViewById(R.id.btn_create_account);
        tv_login = findViewById(R.id.tv_login);

        btn_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IntroAct.this, RegisterActivity.class);

                startActivity(i);
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IntroAct.this, LoginActivity.class);

                startActivity(i);
            }
        });
    }
}