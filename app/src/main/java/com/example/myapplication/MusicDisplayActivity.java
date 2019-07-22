package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MusicDisplayActivity extends AppCompatActivity {

    private int scrolled = 0;
    Handler handler;

    TextView titleTextView;
    TextView artistTextView;
    TextView lyricsTextView;
    TextView timeTextView;
    ImageView artistImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_display);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        this.titleTextView = findViewById(R.id.titleTextView);
        this.artistTextView = findViewById(R.id.artistTextView);
        this.lyricsTextView = findViewById(R.id.lyricsTextView);
        this.timeTextView = findViewById(R.id.timeTextView);
        this.artistImageView = findViewById(R.id.artistImage);
        this.lyricsTextView.setMovementMethod(new ScrollingMovementMethod());

        this.handler = new Handler();

        Intent intent = getIntent();
        String musicInfoJson = intent.getStringExtra(MainActivity.EXTRA_INFO);
        MusicInfo info = (new Gson()).fromJson(musicInfoJson, MusicInfo.class);

        this.titleTextView.setText(info.getTitle());
        this.artistTextView.setText(info.getArtist());
        this.lyricsTextView.setText(info.getLyrics());
        this.timeTextView.setText(getString(R.string.time_text, info.getTimeCode(), info.getDuration()));
        new downloadImageTask(this.artistImageView).execute(info.getArtistArt());

//        int currentTime = info.getTimeCode();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lyricsTextView.canScrollVertically(1)) {
                    int scroll = (int) (lyricsTextView.getTextSize() / 20);
                    lyricsTextView.scrollBy(0, scroll);
                    scrolled = lyricsTextView.getScrollY(); // TODO fix scroll position after changing orientation
                    System.out.println(titleTextView.getText() + ": Current pos: " + scrolled + ", scroll by: " + scroll);
                    handler.postDelayed(this, 100);
                }
            }
        }, 100);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            handler.removeCallbacksAndMessages(null );
        return super.onKeyDown(keyCode, event);
    }

    private static class downloadImageTask extends AsyncTask<String, Void, Bitmap> {

        WeakReference<ImageView> imageViewWeakReference;

        private downloadImageTask(ImageView img) {
            imageViewWeakReference = new WeakReference<>(img);
            img.setEnabled(false);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().
                    url(urls[0])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return BitmapFactory.decodeStream(response.body().byteStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = imageViewWeakReference.get();
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            imageView.setEnabled(true);
        }
    }
}
