package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class UserLogin extends AppCompatActivity {

    TextView userlogin;
    Button btnSignin,btnLogin;
    ImageButton btnHome;
    FirebaseAuth mAuth;

    FirebaseFirestore fStore;

    ProgressBar progressBar;

    EditText editTextemail,editTextPassword;

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent= new Intent(getApplicationContext(), UserLogin.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        androidx.appcompat.app.ActionBar actionBar= getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actiobvar_colo));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+ "Log In "+"</font>"));


        userlogin=findViewById(R.id.userlogin);
        Typeface typeface=Typeface.createFromAsset(getAssets(), "fonts/officecodepro-bold.otf");
        userlogin.setTypeface(typeface);

        btnSignin=findViewById(R.id.btnSignin);
        btnHome=findViewById(R.id.btnHome);
        btnLogin=findViewById(R.id.btnLogin);
        editTextemail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        progressBar=findViewById(R.id.Progressbar);

        mAuth = FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();



        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(UserLogin.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(UserLogin.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email=editTextemail.getText().toString();
                password=editTextPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(UserLogin.this,"enter email",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(UserLogin.this,"enter password",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                                    Intent intent= new Intent(getApplicationContext(), UserSearch.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                   Toast.makeText(UserLogin.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

    }
}