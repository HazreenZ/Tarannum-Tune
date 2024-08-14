package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity {

    Button btnLogin,btnSignUp;
    TextView textView;

    ProgressBar progressBar;
    FirebaseAuth mAuth;

    FirebaseFirestore fStore;
    EditText editTextEmail,editTextPassword,rePassword;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent= new Intent(getApplicationContext(), UserLogin.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        androidx.appcompat.app.ActionBar actionBar= getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actiobvar_colo));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">"+ "Sign Up "+"</font>"));


        mAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

       btnSignUp=findViewById(R.id.btnSignUp);
       editTextEmail=findViewById(R.id.editTextEmail);
       editTextPassword=findViewById(R.id.editTextPassword);
       rePassword=findViewById(R.id.rePassword);

       progressBar=findViewById(R.id.Progressbar);

        btnLogin=findViewById(R.id.btnLogin);
        textView=findViewById(R.id.textView);



        Typeface typeface=Typeface.createFromAsset(getAssets(), "fonts/officecodepro-bold.otf");
        textView.setTypeface(typeface);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(SignUp.this, UserLogin.class);
                startActivity(intent);

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email,password,repassword;

                email=editTextEmail.getText().toString();
                password=editTextPassword.getText().toString();
                repassword=rePassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(SignUp.this,"enter email",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(SignUp.this,"enter password",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(repassword)){
                    Toast.makeText(SignUp.this,"enter password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(repassword)){
                    Toast.makeText(SignUp.this,"Password do not match",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    FirebaseUser user =mAuth.getCurrentUser();
                                    Toast.makeText(SignUp.this, "Account created",
                                            Toast.LENGTH_SHORT).show();
                                    DocumentReference df=fStore.collection("user").document(user.getUid());
                                    Map<String, Object> userInfo=new HashMap<>();
                                    userInfo.put("email",editTextEmail.getText().toString());
                                    userInfo.put("password",editTextPassword.getText().toString());
                                    userInfo.put("isUser","1");

                                    df.set(userInfo);

                                    Intent intent= new Intent(getApplicationContext(), UserLogin.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                     Toast.makeText(SignUp.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }

}