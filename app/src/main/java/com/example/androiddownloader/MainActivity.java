package com.example.androiddownloader;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.androiddownloader.HelperClasses.DownloaderServiceManager;

public class MainActivity extends AppCompatActivity {
    private StatusBarRequestReceiver receiver ;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        setContentView(R.layout.activity_main);
        setupDownloadButton();
        setupReciever();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar = findViewById(R.id.progressBar);

    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void setupDownloadButton() {
        final Button saveButton = findViewById(R.id.downloadButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.inputURL);
                String potentialURL = input.getText().toString();
                startDownloadService(potentialURL);

            }
        });
    }

    private void startDownloadService(String url) {
        Intent intent = new Intent(this, DownloaderServiceManager.class);
        intent.putExtra("URL", url);
        startService(intent);

    }

    private void setupReciever() {
        IntentFilter filter = new IntentFilter("UpdateStatusBar");
        receiver = new StatusBarRequestReceiver();
        registerReceiver(receiver,filter);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission not granted", "Permission");
            askForPermissions();
        }

    }

    private void askForPermissions() {
        String[] permissionArray = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this,permissionArray,1337);
    }

    public class StatusBarRequestReceiver extends BroadcastReceiver {
        private int laststatus =0;
        @Override
        public void onReceive(Context context, Intent intent) {
            laststatus=0;
            laststatus=intent.getIntExtra("progress",laststatus);
            progressBar.setProgress(laststatus);


        }

    }
    }

