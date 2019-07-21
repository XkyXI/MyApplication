package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION_CODE = 999;

    private String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "audio_recording.aac";
    private MediaRecorder recorder = new MediaRecorder();
    private CountDownTimer fiveSecCountdown;

    private Button recordButton;
    private TextView textArea;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // removes title bar and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setContentView(R.layout.activity_main);

        // ask for permission to record audio at run time
        this.requestPermission();

        this.textArea = findViewById(R.id.textArea);
        this.textArea.setMovementMethod(new ScrollingMovementMethod());
        this.recordButton = findViewById(R.id.recordButton);



        // record five seconds of audio then begin the client task
        this.fiveSecCountdown = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {}
            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "Finished recording...", Toast.LENGTH_SHORT).show();
                recorder.stop();
                new ClientTask().execute(savePath);
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
            Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... paths) {
            File file = new File(paths[0]);
            if (file.exists())
                return ClientAudd.request(file).toString();
            return null;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                textArea.setText(result);
            }
            recordButton.setEnabled(true);
        }
    }
}
