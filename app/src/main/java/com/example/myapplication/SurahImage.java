package com.example.myapplication;

import java.io.Serializable;

public class SurahImage implements Serializable {

    private String surah;
    private int verse;
    private String mImageUri;

    private int SurahNum;

    // Empty constructor
    public SurahImage() {
    }

    // Corrected constructor with different parameter names
    public SurahImage(String surah, int verse, String mImageUri,int SurahNum) {
        this.surah = surah;
        this.verse = verse;
        this.mImageUri = mImageUri;
        this.SurahNum= SurahNum;
    }

    public int getSurahNum() {return SurahNum;}

    public void setSurahNum(int surahNum) {this.SurahNum = surahNum;}

    public String getSurah() {
        return surah;
    }

    public void setSurah(String surah) {
        this.surah = surah;
    }

    public int getVerse() {
        return verse;
    }

    public void setVerse(int verse) {
        this.verse = verse;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    public String toString() {
        // Return the surah name to be displayed in the ListView
        return getSurah();
    }
}
