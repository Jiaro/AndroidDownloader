package com.example.androiddownloader.HelperClasses;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloaderServiceManager extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting for URL:" + intent.getStringExtra("URL"), Toast.LENGTH_SHORT).show();
        Thread thread = new DownloadThread(intent.getStringExtra("URL"));
        thread.start();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
    private class DownloadThread extends Thread{
        private String URL;
        public DownloadThread(String _URL){
            URL=_URL;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(URL);
                URLConnection connection = url.openConnection();
                connection.connect();
                int filelength = connection.getContentLength();
                String filename = URLUtil.guessFileName(URL,null,null);
                InputStream input = new BufferedInputStream(connection.getInputStream());
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+filename;
                File file = new File(path);
                file.createNewFile();
                OutputStream output = new FileOutputStream(file);
                byte data[] = new byte[1024];
                int count;
                long total =0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                    updateStatusBar((int) (total*100/filelength));
                }

                output.flush();
                output.close();
                input.close();


            } catch (Exception e) {
                Log.e("Error on Download",e.getMessage(),e);
            }
            stopSelf();
        }
        private void updateStatusBar(int x){
            Log.d("Status download",String.valueOf(x));
            Intent broadcast = new Intent();
            broadcast.setAction("UpdateStatusBar");
            broadcast.putExtra("progress",x);
            sendBroadcast(broadcast);
        }
    }
}