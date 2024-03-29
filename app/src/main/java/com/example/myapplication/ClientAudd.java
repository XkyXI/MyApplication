package com.example.myapplication;


import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class ClientAudd {

    public enum Status {
        SUCCESS, NOT_FOUND, ERROR
    }

    static Status request(File file, MusicInfo musicInfo) {
        String url = "https://api.audd.io/?return=lyrics,timecode,deezer,itunes";
        String response = getResponse(file, url);
        if (response != null) {
            return parseResponse(response, musicInfo);
        }
        return Status.ERROR;
    }

    // ================================================================
    // Private helper functions
    // ================================================================
    private static String getResponse(File file, String url) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("audio/aac"), file))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response resp = client.newCall(request).execute();
            return resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Status parseResponse(String input, MusicInfo musicInfo) {
        JsonObject json = new JsonParser().parse(input).getAsJsonObject();

        Log.i("Response", "Response: " + json);

        String status = json.get("status").getAsString();
        if (status.equals("error") || !status.equals("success")) return Status.ERROR;

        String result = json.get("result").toString();
        if (result.equals("null")) return Status.NOT_FOUND;


        JsonObject songInfo = json.get("result").getAsJsonObject();

        if (songInfo.has("artist"))
            musicInfo.setArtist(songInfo.get("artist").getAsString());
        if (songInfo.has("title"))
            musicInfo.setTitle(songInfo.get("title").getAsString());
        if (songInfo.has("lyrics"))
            musicInfo.setLyrics(songInfo.getAsJsonObject("lyrics").get("lyrics").getAsString());
        if (songInfo.has("timecode")) {
            String time = songInfo.get("timecode").getAsString();
            String[] times = time.split(":");
            int minute = Integer.parseInt(times[0]);
            int second = Integer.parseInt(times[1]);
            musicInfo.setTimeCode(minute * 60 + second);
        }

        if (songInfo.has("deezer")) {
            JsonObject deezerInfo = songInfo.getAsJsonObject("deezer");

            musicInfo.setArtist(deezerInfo.getAsJsonObject("artist").get("name").getAsString());
            musicInfo.setTitle(deezerInfo.get("title").getAsString());
            musicInfo.setDuration(deezerInfo.get("duration").getAsInt());
            musicInfo.setArtistArt(deezerInfo.getAsJsonObject("artist").get("picture_medium").getAsString());
        } else if (songInfo.has("itunes")) {
            JsonObject itunesInfo = songInfo.getAsJsonObject("itunes");

            musicInfo.setArtist(itunesInfo.get("artistName").getAsString());
            musicInfo.setTitle(itunesInfo.get("trackName").getAsString());
            musicInfo.setDuration(itunesInfo.get("trackTimeMillis").getAsInt() / 1000);
            musicInfo.setArtistArt(itunesInfo.get("artworkUrl100").getAsString());
        }
        return Status.SUCCESS;
    }
}
