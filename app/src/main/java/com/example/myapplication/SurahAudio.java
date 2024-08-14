package com.example.myapplication;

public class SurahAudio {
    private String audioUri;
    private int noSurah;
    private String reciter;
    private String surah;
    private String tarannum;
    private String surah_tarannum; // New field for storing concatenated surah and tarannum

    // Default constructor (required by Firebase)
    public SurahAudio() {
    }

    public SurahAudio(String surah, int noSurah, String tarannum, String reciter, String audioUri) {
        this.audioUri = audioUri;
        this.noSurah = noSurah;
        this.reciter = reciter;
        this.surah = surah;
        this.tarannum = tarannum;
        this.surah_tarannum = surah + "_" + tarannum; // Concatenate surah and tarannum and store it in the surah_tarannum field
    }

    // Getters and setters

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    public int getNoSurah() {
        return noSurah;
    }

    public void setNoSurah(int noSurah) {
        this.noSurah = noSurah;
    }

    public String getReciter() {
        return reciter;
    }

    public void setReciter(String reciter) {
        this.reciter = reciter;
    }

    public String getSurah() {
        return surah;
    }

    public void setSurah(String surah) {
        this.surah = surah;
    }

    public String getTarannum() {
        return tarannum;
    }

    public void setTarannum(String tarannum) {
        this.tarannum = tarannum;
    }

    public String getSurah_tarannum() {
        return surah_tarannum;
    }

    public void setSurah_tarannum(String surah_tarannum) {
        this.surah_tarannum = surah_tarannum;
    }
}
