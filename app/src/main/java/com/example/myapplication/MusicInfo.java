package com.example.myapplication;

public class MusicInfo {
    private String artist;
    private String artistArt;
    private String title;
    private String lyrics;

    private int timeCode;
    private int duration;

    MusicInfo() {
        this.artist = null;
        this.artistArt = null;
        this.title = null;
        this.lyrics = null;
        this.timeCode = -1;
        this.duration = -1;
    }


    String getArtist() { return artist; }
    void setArtist(String artist) { this.artist = artist; }

    String getArtistArt() { return artistArt; }
    void setArtistArt(String artistArt) { this.artistArt = artistArt; }

    String getTitle() { return title; }
    void setTitle(String title) { this.title = title; }

    String getLyrics() { return lyrics; }
    void setLyrics(String lyrics) { this.lyrics = lyrics; }

    int getTimeCode() { return timeCode; }
    void setTimeCode(int timeCode) { this.timeCode = timeCode; }

    int getDuration() { return duration; }
    void setDuration(int duration) { this.duration = duration; }


    public boolean allEntriesFilled() {
        return this.artist != null && this.artistArt != null && this.title != null &&
                this.lyrics != null && this.timeCode != -1 && this.duration != -1;
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "artist='" + artist + '\'' +
                ", artistArt='" + artistArt + '\'' +
                ", title='" + title + '\'' +
                ", timeCode=" + timeCode +
                ", duration=" + duration +
                ", lyrics='" + lyrics + '\'' +
                '}';
    }
}
