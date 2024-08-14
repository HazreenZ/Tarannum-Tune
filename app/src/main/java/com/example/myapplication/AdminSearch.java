package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.myapplication.SurahImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdminSearch extends AppCompatActivity {

    FirebaseAuth auth;
    ListView listView;
    ArrayList<SurahImage> myArrayList = new ArrayList<>();
    ArrayList<SurahImage> filteredArrayList = new ArrayList<>();
    FirebaseUser user;
    DatabaseReference mRef;
    ArrayAdapter<SurahImage> myArrayAdapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_search);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actiobvar_colo));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Main User " + "</font>"));

        myArrayAdapter = new ArrayAdapter<>(AdminSearch.this,
                android.R.layout.simple_list_item_1, filteredArrayList);

        listView = findViewById(R.id.listView);
        listView.setAdapter(myArrayAdapter);

        mRef = FirebaseDatabase.getInstance().getReference("Surah");



        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), UserLogin.class);
            startActivity(intent);
            finish();
        }

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Assuming "surah" is a property of the child node, get the name of the surah
                SurahImage surahName = snapshot.getValue(SurahImage.class);
                if (surahName != null) {
                    myArrayList.add(surahName);
                    filteredArrayList.add(surahName);
                    myArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle child changes if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle child removal if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle child movement if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors if needed
            }
        });

        // Set the OnItemClickListener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected surah object from the ArrayList
                SurahImage selectedSurah = filteredArrayList.get(position);

                // Pass the surah details to the UserPlayer activity
                Intent intent = new Intent(AdminSearch.this, UserPlayer.class);
                intent.putExtra("surahName", selectedSurah.getSurah());
                intent.putExtra("imageUrl", selectedSurah.getmImageUri());
                // Add more surah details to the intent if needed
                startActivity(intent);
            }
        });


        searchView = findViewById(R.id.Searchadmin);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Call a method to handle search with newText
                handleSearch(s);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainuser, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks
        if (item.getItemId() == R.id.menu_logout) {
            // Perform logout action
            logoutUser();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        // Perform logout operation
        Intent intent= new Intent(AdminSearch.this, Home.class);
        startActivity(intent);
        finish();
    }

    private void handleSearch(String query) {
        filteredArrayList.clear();
        for (SurahImage surah : myArrayList) {
            if (surah.getSurah().toLowerCase().contains(query.toLowerCase())) {
                filteredArrayList.add(surah);
            }
        }

        // Custom sorting: Place matching items at the top
        Collections.sort(filteredArrayList, new Comparator<SurahImage>() {
            @Override
            public int compare(SurahImage surah1, SurahImage surah2) {
                boolean isMatch1 = surah1.getSurah().toLowerCase().contains(query.toLowerCase());
                boolean isMatch2 = surah2.getSurah().toLowerCase().contains(query.toLowerCase());

                if (isMatch1 && !isMatch2) {
                    return -1; // surah1 comes before surah2
                } else if (!isMatch1 && isMatch2) {
                    return 1; // surah1 comes after surah2
                } else {
                    return 0; // Preserve the original order
                }
            }
        });
        myArrayAdapter.notifyDataSetChanged();
    }


}

