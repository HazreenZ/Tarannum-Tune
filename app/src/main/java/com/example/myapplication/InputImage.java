package com.example.myapplication;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class InputImage extends AppCompatActivity {

    static final int PICK_IMAGE_REQUEST = 1;

    Button btnfilePicker, btnUpload, btnback;
    ImageView Image;
    EditText Surah, Verse,surahNum;
    ProgressBar progressBar;
    private Uri migmageUri;
    private StorageReference mStoreRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask<UploadTask.TaskSnapshot> mUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_image);

        btnfilePicker = findViewById(R.id.btnfilePicker);
        btnUpload = findViewById(R.id.btnUpload);
        btnback = findViewById(R.id.btnBack);
        Image = findViewById(R.id.ImageS);
        Surah = findViewById(R.id.iSurah);
        Verse = findViewById(R.id.iVerse);
        surahNum =findViewById(R.id.surahNum);
        progressBar = findViewById(R.id.ImProgressbar);

        mStoreRef = FirebaseStorage.getInstance().getReference("Surah");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Surah");

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actiobvar_colo));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "ADMIN " + "</font>"));


        btnfilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add functionality for the back button here if needed
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            migmageUri = data.getData();

            Picasso.get().load(migmageUri).into(Image);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (migmageUri != null) {
            StorageReference fileRef = mStoreRef.child(System.currentTimeMillis() + "."
                    + getFileExtension(migmageUri));

            mUpload = fileRef.putFile(migmageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(InputImage.this, "Upload Successful", Toast.LENGTH_SHORT).show();

                            // Get the download URL
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    int verseValue = Integer.parseInt(Verse.getText().toString().trim());
                                    int SurahNumValue= Integer.parseInt(surahNum.getText().toString().trim());
                                    SurahImage surahImage = new SurahImage(Surah.getText().toString().trim(), verseValue, downloadUri.toString(),SurahNumValue);
                                    // Now you can do whatever you need to do with the download URL (e.g., store it in the database)

                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(surahImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(InputImage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(InputImage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });

        } else {
            Toast.makeText(this, "No File selected", Toast.LENGTH_SHORT).show();
        }
    }
}
