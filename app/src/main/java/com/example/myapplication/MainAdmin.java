package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TintableCheckedTextView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainAdmin extends AppCompatActivity {

    Button btnSearch,btnAdd, btnLogout,btnAddSurah,btnUpdate;
    TextView textView;

    FirebaseAuth mAuth;

//    protected void onStart() {
//        super.onStart();
//        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//            startActivity(new Intent(getApplicationContext(), UserLogin.class));
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        btnSearch= findViewById(R.id.btnSearch);
        btnAdd= findViewById(R.id.btnAdd);
        textView= findViewById(R.id.textView);
        btnLogout=findViewById(R.id.btnLogout);
        btnAddSurah=findViewById(R.id.btnAddSurah);
        btnUpdate=findViewById(R.id.btnUpdate);


        androidx.appcompat.app.ActionBar actionBar= getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actiobvar_colo));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+ "ADMIN "+"</font>"));

        Typeface typeface=Typeface.createFromAsset(getAssets(), "fonts/freebooterscript.ttf");
        textView.setTypeface(typeface);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainAdmin.this, InputData.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(MainAdmin.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        btnAddSurah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainAdmin.this, InputImage.class);
                startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainAdmin.this, AdminSearch.class);
                startActivity(intent);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainAdmin.this,AdminDataList.class);
                startActivity(intent);
            }
        });
    }
}