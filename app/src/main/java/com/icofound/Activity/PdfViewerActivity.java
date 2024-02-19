package com.icofound.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.icofound.Constant;
import com.icofound.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PdfViewerActivity extends AppCompatActivity {
    BufferedInputStream inputStream;
    PDFView pdfView;
    TextView tvCurrentPage;
    String urlString = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        findViewById(R.id.tvDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pdfView = findViewById(R.id.pdfView);
        tvCurrentPage = findViewById(R.id.tvCurrentPage);
        urlString = getIntent().getStringExtra("url");

        Constant.displayProgress(this);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) { // to handle errors.
                e.printStackTrace();
            }
            handler.post(() -> {
                pdfView.fromStream(inputStream)
                        .swipeHorizontal(true)
                        .pageSnap(true)
                        .autoSpacing(true)
                        .pageFling(true)
                        .onError(new OnErrorListener() {
                            @Override
                            public void onError(Throwable t) {

                                Constant.dismissProgress(PdfViewerActivity.this);
                                Constant.displayCommonDialog(PdfViewerActivity.this, getResources().getString(R.string.app_name), t.toString(), "Okay", "Go Back","", new Constant.DialogClicks() {
                                    @Override
                                    public void onPositiveClick(String editTextValue) {
                                        finish();
                                    }

                                    @Override
                                    public void onNegativeClick() {

                                    }
                                });

                            }
                        })
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                setCounter();
                                Constant.dismissProgress(PdfViewerActivity.this);
                            }
                        })
                        .load();




            });
        });



        findViewById(R.id.imgNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pdfView.getCurrentPage() < pdfView.getPageCount())
                pdfView.jumpTo(pdfView.getCurrentPage() +1);


                setCounter();


            }
        });
        findViewById(R.id.imgPrev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pdfView.getCurrentPage() > 0)
                    pdfView.jumpTo(pdfView.getCurrentPage() -1);

                setCounter();


            }
        });


    }



   public static void startPdfViewLib(String url , Activity activity){
        activity.startActivity(new Intent(activity , PdfViewerActivity.class).putExtra("url" , url));
    }


    private void setCounter(){
        tvCurrentPage.setText((pdfView.getCurrentPage() + 1) + "/" + pdfView.getPageCount());
    }
}