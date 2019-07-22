package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_INFO = "com.example.myapplication.MUSIC_INFO";

    private final int REQUEST_PERMISSION_CODE = 999;

    private String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "audio_recording.aac";
    private MediaRecorder recorder = new MediaRecorder();
    private CountDownTimer fiveSecCountdown;
    private Button recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // removes title bar and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setContentView(R.layout.activity_main);

        // ask for permission to record audio at run time
        this.requestPermission();

        this.recordButton = findViewById(R.id.recordButton);


        // record five seconds of audio then begin the client task
        this.fiveSecCountdown = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {}
            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "Searching", Toast.LENGTH_SHORT).show();
                recorder.stop();
                requestInfo();
            }
        };

        this.recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordButton.setEnabled(false);

                // start recording
                setupMediaRecorder();
                recordAudio();
                fiveSecCountdown.start();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            String msg = "Permission Granted";
            for (int res : grantResults) {
                if (res != PackageManager.PERMISSION_GRANTED) {
                    this.recordButton.setEnabled(false);
                    msg = "Permission Denied";
                }
            }

            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission() {
        int audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (audioPermission != PackageManager.PERMISSION_GRANTED ||
            writePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, this.REQUEST_PERMISSION_CODE);
        }
    }

    private void setupMediaRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(savePath);
    }


    private void recordAudio() {
        try {
            recorder.prepare();
            recorder.start();
            Toast.makeText(this, "Listening", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestInfo () {
        File file = new File(savePath);
        if (file.exists())
            new ClientTask(this, file).execute();
        else
            System.out.println("ERROR: File not found");
    }

    private static class ClientTask extends AsyncTask<Void, Void, ClientAudd.Status> {

        private WeakReference<MainActivity> activityWeakReference;
        private File audioFile;
        private MusicInfo musicInfo;

        private ClientTask(MainActivity context, File file) {
            activityWeakReference = new WeakReference<>(context);
            audioFile = file;
            musicInfo = new MusicInfo();
        }

        @Override
        protected ClientAudd.Status doInBackground(Void... voids) {
            return ClientAudd.request(audioFile, musicInfo);
        }

        @Override
        protected void onPostExecute(ClientAudd.Status status) {
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.recordButton.setEnabled(true);
            switch (status) {
                case ERROR:
                    Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show();
                    break;

                case NOT_FOUND:
                    Toast.makeText(activity, "Not found", Toast.LENGTH_LONG).show();
                    break;

                case SUCCESS:
                    Toast.makeText(activity, "Success", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(activity, MusicDisplayActivity.class);
                    String musicInfoJson = (new Gson()).toJson(musicInfo);
                    intent.putExtra(MainActivity.EXTRA_INFO, musicInfoJson);

                    activity.startActivity(intent);
                    break;
            }
            Log.i("Info", "MusicInfo: " + musicInfo);
        }
    }
}
