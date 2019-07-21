package com.example.myapplication;

public class MusicInfo {
    String artist;
    String artistArt;
    String title;
    String lyrics;

    int timeCode;
    int duration;

    public MusicInfo() {
        this.artist = null;
        this.artistArt = null;
        this.title = null;
        this.lyrics = null;
        this.timeCode = -1;
        this.duration = -1;
    }


    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getArtistArt() { return artistArt; }
    public void setArtistArt(String artistArt) { this.artistArt = artistArt; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLyrics() { return lyrics; }
    public void setLyrics(String lyrics) { this.lyrics = lyrics; }

    public int getTimeCode() { return timeCode; }
    public void setTimeCode(int timeCode) { this.timeCode = timeCode; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }


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
