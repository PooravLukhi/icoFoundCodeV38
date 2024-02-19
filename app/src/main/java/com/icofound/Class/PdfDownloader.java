package com.icofound.Class;


import android.app.Activity;
import android.os.AsyncTask;

import com.icofound.Activity.PostActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfDownloader extends AsyncTask<String, Void, File> {

    private DownloadListener listener;
    private Activity activity;

    public interface DownloadListener {
        void onDownloadComplete(File file);
        void onDownloadFailed(Exception e);
    }

    public PdfDownloader(Activity postActivity, DownloadListener listener) {
        this.listener = listener;
        activity = postActivity;
    }

    @Override
    protected File doInBackground(String... urls) {
        String pdfUrl = urls[0];
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        File pdfFile = null;

        try {
            URL url = new URL(pdfUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                pdfFile = new File(activity.getCacheDir()+"/" +System.currentTimeMillis()+".pdf"); // Change the file path as needed
                outputStream = new FileOutputStream(pdfFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (pdfFile != null) {
                pdfFile.delete(); // Delete the file if download fails
            }
            return null;
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pdfFile;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if (file != null) {
            listener.onDownloadComplete(file);
        } else {
            listener.onDownloadFailed(new Exception("Download failed."));
        }
    }
}