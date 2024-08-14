package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class UserPlayer extends AppCompatActivity {

    ImageView surahImageView;
    TextView surahNameTextView;
    Spinner tarannumChooseSpinner;
    Button playAudioButton, btnback;

    String[] tarannumOptions = {"Bayyati", "Hijjaz", "Nahwan", "Raz"};

    // Firebase database reference
    DatabaseReference audioRef;

    // MediaPlayer for audio playback
    private MediaPlayer mediaPlayer;

    // Variables to hold the selected surah and tarannum
    private String selectedSurah;
    private String selectedTarannum;

    // Variables to hold the selected surah and tarannum



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_player);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actiobvar_colo));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Tarannum Tune " + "</font>"));


        surahImageView = findViewById(R.id.surahI);
        tarannumChooseSpinner = findViewById(R.id.tarannumChoose);
        ArrayAdapter<String> tarannumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tarannumOptions);
        tarannumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tarannumChooseSpinner.setAdapter(tarannumAdapter);
        btnback = findViewById(R.id.btnBack);
        surahNameTextView = findViewById(R.id.surahNameTextView);

        // Initialize the MediaPlayer
        mediaPlayer = new MediaPlayer();

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Get the surah details from the intent
        selectedSurah = getIntent().getStringExtra("surahName");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Set the surah name in the TextView
        surahNameTextView.setText(selectedSurah);

        // Load the image using Picasso
        Picasso.get().load(imageUrl).into(surahImageView);

        // Get a reference to the audio node
        audioRef = FirebaseDatabase.getInstance().getReference("Audio");

        // Set the OnItemSelectedListener for the Spinner
        tarannumChooseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected tarannum
                selectedTarannum = tarannumOptions[position];

                Log.d("TAG", "Selected Tarannum: " + selectedTarannum);

                Log.d("TAG", "Selected Surah: " + selectedSurah);

                // Get the corresponding audio data from Firebase
                retrieveDataFromFirebase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if no tarannum is selected
            }
        });

        // Set the OnClickListener for the "Play Audio" button
        playAudioButton = findViewById(R.id.playAudioButton);
        playAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    // If the audio is currently playing, pause it and update the button text
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playAudioButton.setText("Play Audio");
                    } else { // If the audio is not playing, start it and update the button text
                        mediaPlayer.start();
                        playAudioButton.setText("Pause Audio");
                    }
                }
            }
        });
    }

    // Method to retrieve data from both "surah" and "tarannum" selections
    // Method to retrieve data from both "surah" and "tarannum" selections
    private void retrieveDataFromFirebase() {
        // Get a reference to the "Audio" node in the Firebase Realtime Database
        DatabaseReference audioRef = FirebaseDatabase.getInstance().getReference("Audio");

        // Perform the query to retrieve data based on the selected "surah_tarannum"
        Query query = audioRef.orderByChild("surah_tarannum").equalTo(selectedSurah + "_" + selectedTarannum);

        // Execute the query using addListenerForSingleValueEvent
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the query has data and process the result
                if (dataSnapshot.exists()) {
                    // Create a list to store all the audio files for the selected surah and tarannum
                    ArrayList<SurahAudio> audioList = new ArrayList<>();

                    // Loop through the dataSnapshot to get all audio files
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SurahAudio audio = snapshot.getValue(SurahAudio.class);
                        if (audio != null) {
                            audioList.add(audio);
                        }
                    }

                    // Check if there is at least one audio file found
                    if (!audioList.isEmpty()) {
                        // For now, let's use the first audio file found (you can change this as needed)
                        SurahAudio selectedAudio = audioList.get(0);

                        // Get the audio URL from the selected audio file
                        String audioUrl = selectedAudio.getAudioUri();

                        // Set the audio URL in the MediaPlayer
                        if (audioUrl != null) {
                            try {
                                mediaPlayer.reset(); // Reset the MediaPlayer before loading new audio
                                mediaPlayer.setDataSource(audioUrl);
                                mediaPlayer.prepare();
                                playAudioButton.setText("Play Audio"); // Reset the button text
                            } catch (IOException e) {
                                e.printStackTrace();
                                showFailureMessage("Failed to retrieve audio");
                            }
                        } else {
                            showFailureMessage("Audio URL not found");
                            Log.d("TAG", "DataSnapshot does not exist");
                        }
                    } else {
                        showFailureMessage("No audio data found for the selected surah and tarannum");
                    }
                } else {
                    showFailureMessage("Audio data not found for the selected surah and tarannum");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if needed
                showFailureMessage("Database error: " + databaseError.getMessage());
            }
        });
    }



    // Method to show a failure message as a Toast
    private void showFailureMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer resources when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}