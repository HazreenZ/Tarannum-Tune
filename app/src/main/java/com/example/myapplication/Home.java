package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Home extends AppCompatActivity {

    Button btnUser,btnAdmin;
    TextView welcome, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        btnUser= findViewById(R.id.btnUser);
        btnAdmin= findViewById(R.id.btnAdmin);
        welcome= findViewById((R.id.welcome));
        title=findViewById(R.id.title);

        Typeface typeface=Typeface.createFromAsset(getAssets(), "fonts/officecodepro-bold.otf");
        welcome.setTypeface(typeface);

        Typeface typeface1=Typeface.createFromAsset(getAssets(),"fonts/freebooterscript.ttf");
        title.setTypeface(typeface1);



        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, UserLogin.class);
                startActivity(intent);
            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                Intent intent = new Intent(Home.this, AdminLogin.class);
                startActivity(intent);
            }
        });

    }
}