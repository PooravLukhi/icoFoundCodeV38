package com.icofound;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icofound.Activity.EducationActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Constant {

    private static ProgressDialog progressDialog;

    public static void generatePDF(ImageView pdfView , String pdfUrl , int width){


            class RetrievePDFfromUrl extends AsyncTask<String, Void, Bitmap> {
                @Override
                protected Bitmap doInBackground(String... strings) {


                    URL url = null;
                    try {
                        url = new URL(pdfUrl);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) url.openConnection();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    InputStream inputStream = null;
                    try {
                        inputStream = connection.getInputStream();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    File pdfFile = null;
                    try {
                        pdfFile = File.createTempFile("temp_pdf", ".pdf");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(pdfFile);
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while (true) {
                        try {
                            if (!((bytesRead = inputStream.read(buffer)) > 0)) break;
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        try {
                            outputStream.write(buffer, 0, bytesRead);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    try {
                        outputStream.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    // Generate thumbnail
                    ParcelFileDescriptor fileDescriptor = null;
                    try {
                        fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    PdfRenderer pdfRenderer = null;
                    try {
                        pdfRenderer = new PdfRenderer(fileDescriptor);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    PdfRenderer.Page page = pdfRenderer.openPage(0);

                    int thumbnailWidth = width; // Set your desired width
                    int thumbnailHeight = width; // Set your desired height
                    Bitmap thumbnailBitmap = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight, Bitmap.Config.ARGB_8888);
                    page.render(thumbnailBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    // Set the thumbnail image in ImageView

                    // Close resources
                    page.close();
                    pdfRenderer.close();
                    try {
                        fileDescriptor.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    return thumbnailBitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    // after the execution of our async
                    // task we are loading our pdf in our pdf view.
                    pdfView.setImageBitmap(bitmap);

                }
            }

            new RetrievePDFfromUrl().execute(pdfUrl);
        }



        public static void displayProgress(Context context){
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("iCoFound");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
        }

    public static void dismissProgress(Context context){

        try {
            progressDialog.dismiss();
        }catch (Exception e){

        }
    }

    public static void displayCommonDialog(Context context , String title , String desc ,String negativeBtn , String positiveBtn , String hint ,DialogClicks dialogClicks){

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_common);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        TextView tvDesc = dialog.findViewById(R.id.tvDesc);
        tvDesc.setText(desc);



        EditText etEditText = dialog.findViewById(R.id.etEdittext);
        LinearLayout layEditText = dialog.findViewById(R.id.layEditText);
        if (hint.equalsIgnoreCase(""))
            layEditText.setVisibility(View.GONE);

        etEditText.setHint(hint);


        TextView tvNegative = dialog.findViewById(R.id.tvNegative);
        tvNegative.setText(negativeBtn);
        TextView tvPositive = dialog.findViewById(R.id.tvPositive);
        tvPositive.setText(positiveBtn);


        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialogClicks.onNegativeClick();
            }
        });
        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialogClicks.onPositiveClick(etEditText.getText().toString());

            }
        });

        dialog.show();

    }

    public interface DialogClicks{
        public void onPositiveClick(String edittextValue);
        public void onNegativeClick();
    }
}
