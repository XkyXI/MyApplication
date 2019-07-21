package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;

public class ClientAsyncTask extends AsyncTask {

    private Context context;
    private String filePath;

    private ClientAsyncTask(Context context, String path) {
        this.context = context;
        this.filePath = path;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
