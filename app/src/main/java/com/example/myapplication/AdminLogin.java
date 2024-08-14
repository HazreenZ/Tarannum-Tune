package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminLogin extends AppCompatActivity {

    TextView adminlogin;

    EditText editTextEmail, editTextPassword;
    Button btnLogin;
    ImageButton btnHome;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), UserSearch.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actiobvar_colo));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Log In " + "</font>"));

        adminlogin = findViewById(R.id.adminlogin);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/officecodepro-bold.otf");
        adminlogin.setTypeface(typeface);

        btnLogin = findViewById(R.id.btnLogin);
        btnHome = findViewById(R.id.btnHome);

        editTextPassword = findViewById(R.id.aeditTextPassword);
        editTextEmail = findViewById(R.id.aeditTextEmail);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminLogin.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                mAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();

                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(AdminLogin.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(AdminLogin.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        checkIfAdmin(user.getUid(), new AdminCheckCallback() {
                                            @Override
                                            public void onAdminChecked(boolean isAdmin) {
                                                if (isAdmin) {
                                                    Log.d("TAG", "User is an admin");
                                                    Toast.makeText(getApplicationContext(), "You are an Admin", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(AdminLogin.this, MainAdmin.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish(); // Finish the current activity to prevent going back to the login screen
                                                } else {
                                                    Log.d("TAG", "User is not an admin");
                                                    Toast.makeText(getApplicationContext(), "You are not an Admin", Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                    Intent intent = new Intent(AdminLogin.this, UserLogin.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish(); // Finish the current activity to prevent going back to the login screen
                                                }
                                            }
                                        });
                                    }
                                    // Sign in success, update UI with the signed-in user's information
//                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AdminLogin.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private void checkIfAdmin(String uid, AdminCheckCallback callback) {
        DocumentReference df = fStore.collection("user").document(uid);
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String isAdmin = document.getString("isAdmin");
                        if (isAdmin != null && isAdmin.equals("1")) {
                            Log.d("TAG", "User is an admin");
                            callback.onAdminChecked(true);
                        } else {
                            Log.d("TAG", "User is not an admin");
                            callback.onAdminChecked(false);
                        }
                    } else {
                        Log.d("TAG", "User does not exist");
                        callback.onAdminChecked(false);
                    }
                } else {
                    Log.d("TAG", "Error getting document: ", task.getException());
                    callback.onAdminChecked(false);
                }
            }
        });
    }

    // Define the callback interface
    interface AdminCheckCallback {
        void onAdminChecked(boolean isAdmin);
    }

}
