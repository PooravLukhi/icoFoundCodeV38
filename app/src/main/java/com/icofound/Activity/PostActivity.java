package com.icofound.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icofound.Class.PdfDownloader;
import com.icofound.Class.Utils;
import com.icofound.Constant;
import com.icofound.Model.Post;
import com.icofound.Model.User;
import com.icofound.R;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    TextView cancel,post;
    ImageView gallery,postLayout , imgLink , imgDelete;

    RelativeLayout layViewPdf , postLayoutRelative;
    String cameraurl;
    User user;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    File cameraphotoFile = null;
    EditText post_txt;
    Post postmodel;
    Utils utils;
    private static final int PICK_PDF_REQUEST = 1;
    private BufferedInputStream inputStream;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        utils = new Utils(PostActivity.this);
        utils.intialise();

        imgDelete = findViewById(R.id.imgDelete);
        imgLink = findViewById(R.id.imgLink);
        cancel = findViewById(R.id.cancel);
        gallery = findViewById(R.id.gallery);
        postLayout = findViewById(R.id.postLayout);
        layViewPdf = findViewById(R.id.layViewPdf);
        postLayoutRelative = findViewById(R.id.postLayoutRelative);
        post = findViewById(R.id.post);
        post_txt = findViewById(R.id.post_txt);

        progressDialog = new ProgressDialog(PostActivity.this);

        user = (User) getIntent().getSerializableExtra("user");

        postmodel = (Post) getIntent().getSerializableExtra("post");

        if (postmodel != null){
            post_txt.setText(postmodel.getPost());

            progressDialog.setTitle(getString(R.string.app_name));
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();


            if (postmodel.getPostImgLink().toLowerCase().contains(".pdf")){

                Constant.generatePDF( postLayout,postmodel.getPostImgLink() ,500  );

                layViewPdf.setVisibility(View.VISIBLE);
                postLayoutRelative.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                },2000);
            }
            else{
                Glide.with(PostActivity.this).load(postmodel.getPostImgLink()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressDialog.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressDialog.dismiss();
                        return false;
                    }
                }).placeholder(R.drawable.ic_person).into(postLayout);
                postLayout.setVisibility(View.VISIBLE);
                
            }


        }

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkgallerypermission()) {
                    final Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 2002);
                } else {

                    String permission =android.Manifest.permission.READ_EXTERNAL_STORAGE;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        permission =  Manifest.permission.READ_MEDIA_IMAGES ;

                    ActivityCompat.requestPermissions(PostActivity.this, new String[]{permission}, 202);
                }

            }
        });
        findViewById(R.id.imgPDF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf"); // Filter for PDF files
                startActivityForResult(intent, PICK_PDF_REQUEST);

            }
        });
          findViewById(R.id.imgLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Constant.displayCommonDialog(PostActivity.this, getResources().getString(R.string.app_name), "Please Enter Link", "Cancel", "Okay", "Enter Hint", new Constant.DialogClicks() {
                    @Override
                    public void onPositiveClick(String edittextValue) {

                        if (edittextValue.equalsIgnoreCase(""))
                            Toast.makeText(PostActivity.this, "Please Enter Link", Toast.LENGTH_SHORT).show();
                        else  if  (edittextValue.contains("www") || edittextValue.contains("http") ) {

                            Constant.displayProgress(PostActivity.this);
                            PdfDownloader downloader = new PdfDownloader(PostActivity.this ,new PdfDownloader.DownloadListener() {
                                @Override
                                public void onDownloadComplete(File file) {


                                    Constant.dismissProgress(PostActivity.this);

                                    Uri uri = FileProvider.getUriForFile(PostActivity.this, getPackageName()+".provider", file);


                                    uploadphoto(uri  , "pdf");

                                }

                                @Override
                                public void onDownloadFailed(Exception e) {
                                    Toast.makeText(PostActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    Constant.dismissProgress(PostActivity.this);

                                }
                            });
                            downloader.execute(edittextValue);
                        }
                        else{
                            Toast.makeText(PostActivity.this, "Please Enter Proper Link", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
            }
        });








        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str_post_txt = post_txt.getText().toString();

                if (postLayout.getVisibility() == View.GONE && TextUtils.isEmpty(str_post_txt)){
                    post_txt.requestFocus();
                    post_txt.setError("Please Write about your post");
                }else {

                    if (postmodel == null){
                        uploadpost(cameraurl,str_post_txt);
                        Dialog dialog = new Dialog(PostActivity.this);
                        dialog.setContentView(R.layout.post_update);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        TextView ok = dialog.findViewById(R.id.ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    } else {

                        if (TextUtils.isEmpty(cameraurl)) {
                            updatepost(postmodel.getPostImgLink(),str_post_txt,postmodel);
                        } else {
                            updatepost(cameraurl,str_post_txt,postmodel);
                        }

//                        updatepost(cameraurl,str_post_txt,postmodel);
                        Dialog dialog = new Dialog(PostActivity.this);
                        dialog.setContentView(R.layout.post_update);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        TextView ok = dialog.findViewById(R.id.ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }

                }
            }
        });


        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postLayout.setVisibility(View.GONE);
                layViewPdf.setVisibility(View.GONE);
                postLayoutRelative.setVisibility(View.GONE);
            }
        });



    }

    private void updatepost(String cameraurl, String str_post_txt, Post postmodel) {

        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Updating Your Post...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        DocumentReference documentReference = firestore.collection("Posts").document(postmodel.getId());
        Map<String, Object> upload = new HashMap<>();

        upload.put("approved",false);
        upload.put("postImgLink",cameraurl);
        upload.put("post",str_post_txt);
        Long tsLong = System.currentTimeMillis()/1000;
        upload.put("timestamp",tsLong);

        documentReference.update(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();

            }
        });

    }

    private void uploadpost(String cameraurl, String post_txt) {

        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Uploading post...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        Post post = new Post();
        post.setPost(post_txt);
        post.setisApproved(false);
        post.setisExpanded(false);
        post.setPostImgLink(cameraurl);
        post.setId(UUID.randomUUID().toString());
        Long tsLong = System.currentTimeMillis()/1000;
        post.setTimestamp(tsLong);
        post.setUserId(user.getId());
        post.setUserName(user.getName());
        post.setContentType(0);
        post.setProfilePicLink(user.getProfilePicLink());

        DocumentReference documentReference = firestore.collection("Posts").document(post.getId());
        documentReference.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
               progressDialog.dismiss();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 202 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            final Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 2002);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2002 && resultCode == RESULT_OK && resultCode != RESULT_CANCELED){
            Uri uri = data.getData();
            postLayout.setVisibility(View.VISIBLE);
            postLayoutRelative.setVisibility(View.VISIBLE);

            Glide.with(PostActivity.this).load(uri).placeholder(R.drawable.ic_person).into(postLayout);
            uploadphoto(uri , "jpg");
            layViewPdf.setVisibility(View.GONE);

        }
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            postLayout.setVisibility(View.VISIBLE);
            layViewPdf.setVisibility(View.VISIBLE);
            postLayoutRelative.setVisibility(View.VISIBLE);

            Uri selectedPdfUri = data.getData();
            uploadphoto(selectedPdfUri  , "pdf");
        }



    }

    private void uploadphoto(Uri uri , String extention) {
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);

//        Uri uri = Uri.fromFile(cameraphotoFile);

        StorageReference ref = storageReference.child("Posts" + "/" + user.getId() + "/" + UUID.randomUUID().toString() + "."+ extention);
        ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){

                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();
                            cameraurl = uri.toString();


                            postLayout.setVisibility(View.VISIBLE);
                            if (extention.toLowerCase().contains("pdf")) {
                                Constant.generatePDF(postLayout, cameraurl, 500);
                                layViewPdf.setVisibility(View.VISIBLE);
                                postLayoutRelative.setVisibility(View.VISIBLE);


                                layViewPdf.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        PdfViewerActivity.startPdfViewLib(cameraurl , PostActivity.this);
                                    }
                                });


                            }


                        }
                    });


                }else {
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PostActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkgallerypermission() {

        String permission =android.Manifest.permission.READ_EXTERNAL_STORAGE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permission =  Manifest.permission.READ_MEDIA_IMAGES ;

        if (ContextCompat.checkSelfPermission(PostActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static File createImageFile(Context context,User user) throws IOException {
        File path= new File(context.getFilesDir(), "iCoFound" + File.separator + "Images");
        if(!path.exists()){
            path.mkdirs();
        }
        File outFile = new File(path, user.getId() +".jpg");

        return outFile;

    }

    public File compressImage(PostActivity postActivity, Uri uri, User user) throws IOException {

        String filePath = getPathFromURI(postActivity,uri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = 0;
        if (actualHeight > 0){
           imgRatio = actualWidth / actualHeight;
        }

        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        }
        catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        File file = createImageFile(postActivity,user);

        try {
            out = new FileOutputStream(file);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return file;

    }

    public static String getPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}