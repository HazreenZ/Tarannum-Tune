package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer; // Add this import statement
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class InputData extends AppCompatActivity {

    static final int PICK_AUDIO_REQUEST = 1;

    Button btnFilePicker, btnUpload, btnBack, btnPlayAudio;
    EditText surah, Nosurah, tarannum, reciter;
    ProgressBar progressBar;
    private Uri audioUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private UploadTask mUploadTask;
    private MediaPlayer mediaPlayer;

    private int NoSurahValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actiobvar_colo));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Input Data " + "</font>"));

        btnFilePicker = findViewById(R.id.filePickerButton);
        btnUpload = findViewById(R.id.uploadButton);
        btnBack = findViewById(R.id.btnBack);
        btnPlayAudio = findViewById(R.id.playAudioButton);
        surah = findViewById(R.id.Surah);
        Nosurah = findViewById(R.id.NoSurah);
        tarannum = findViewById(R.id.tarannum);
        reciter = findViewById(R.id.Reciter);
        progressBar = findViewById(R.id.progressBar);

        mStorageRef = FirebaseStorage.getInstance().getReference("Audio");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Audio");

        btnFilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoSurahValue = Integer.parseInt(Nosurah.getText().toString().trim()); // Get NoSurahValue here
                uploadAudioFile();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            audioUri = data.getData();

            // Now retrieve the Surah data using the NoSurahValue from Firebase
            Query surahRef = mDatabaseRef.orderByChild("NoSurah").equalTo(NoSurahValue);

            surahRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SurahAudio surahAudio = snapshot.getValue(SurahAudio.class);
                        if (surahAudio != null) {
                            surah.setText(surahAudio.getSurah());
                            Nosurah.setText(String.valueOf(surahAudio.getNoSurah()));
                            tarannum.setText(surahAudio.getTarannum());
                            reciter.setText(surahAudio.getReciter());
                            break; // Assuming there is only one surah with the given NoSurahValue
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any database error (optional)
                }
            });
        }
    }

// Rest of the code...



    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadAudioFile() {
        if (audioUri != null) {
            StorageReference fileRef = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(audioUri));

            UploadTask uploadTask = fileRef.putFile(audioUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(InputData.this, "Upload Successful", Toast.LENGTH_SHORT).show();

                            // Get the download URL
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    int NoSurahValue = Integer.parseInt(Nosurah.getText().toString().trim());
                                    String selectedTarannum = tarannum.getText().toString().trim();
                                    String selectedSurah = surah.getText().toString().trim();
                                    SurahAudio surahAudio = new SurahAudio(selectedSurah, NoSurahValue, selectedTarannum, reciter.getText().toString().trim(), downloadUri.toString());

                                    // Update the surah_tarannum field in the SurahAudio object
                                    String surah_tarannum = selectedSurah + "_" + selectedTarannum;
                                    surahAudio.setSurah_tarannum(surah_tarannum);

                                    // Now you can do whatever you need to do with the download URL (e.g., store it in the database)
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(surahAudio);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(InputData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(InputData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void playAudio() {
        if (audioUri != null) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, audioUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No audio selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
