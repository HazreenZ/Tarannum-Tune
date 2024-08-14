package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;

public class TestUi extends AppCompatActivity {

    Spinner chooseState;

    TextView welcome,Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_ui);

        welcome=findViewById(R.id.Welcome);
        Title=findViewById(R.id.title);

        Typeface typeface=Typeface.createFromAsset(getAssets(), "fonts/officecodepro-bold.otf"); //Any Fonts
        welcome.setTypeface(typeface);

        Typeface typeface1=Typeface.createFromAsset(getAssets(), "fonts/officecodepro-bold.otf"); //Any Font
         Title.setTypeface(typeface1);


        chooseState=findViewById(R.id.stateChoose);
        chooseState.setPrompt("Choose State");
    }
}