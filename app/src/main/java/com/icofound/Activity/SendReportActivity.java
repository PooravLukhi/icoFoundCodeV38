package com.icofound.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.icofound.Class.Utils;
import com.icofound.R;

public class SendReportActivity extends AppCompatActivity {

    TextView cancel, send;
    String id, userid,commentid;
    EditText et_subject, et_message;
    boolean text = false;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);

        utils = new Utils(SendReportActivity.this);
        utils.intialise();

        cancel = findViewById(R.id.cancel);
        send = findViewById(R.id.send);
        et_subject = findViewById(R.id.et_subject);
        et_message = findViewById(R.id.et_message);

        id = getIntent().getStringExtra("postlist");
        commentid = getIntent().getStringExtra("commentId");
        userid = getIntent().getStringExtra("userid");
        text = getIntent().getBooleanExtra("text", false);

        if (text) {
            et_subject.setText("Report user" + " - " + userid);
        } else {
            if (id != null){
                et_subject.setText("Report a Post" + " - " + id);
            }else if (commentid != null){
                et_subject.setText("Report a Comment" + " - " + commentid);
            }

        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = et_subject.getText().toString();
                String message = et_message.getText().toString();

                if (TextUtils.isEmpty(subject) || subject.equalsIgnoreCase("")) {
                    Dialog dialog = new Dialog(SendReportActivity.this);
                    dialog.setContentView(R.layout.contactus_dialog);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    TextView ok = dialog.findViewById(R.id.cancel);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                } else if (TextUtils.isEmpty(message) || message.equalsIgnoreCase("")) {
                    Dialog dialog = new Dialog(SendReportActivity.this);
                    dialog.setContentView(R.layout.contactus_dialog);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    TextView error_msg = dialog.findViewById(R.id.error_msg);
                    error_msg.setText("Please add description");

                    TextView ok = dialog.findViewById(R.id.cancel);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                } else {

                    /*Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"contactus@arthasvc.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, subject);
                    i.putExtra(Intent.EXTRA_TEXT, message);

                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(SendReportActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }*/


                    Intent intent=new Intent(Intent.ACTION_SEND);
                    String[] recipients={"contactus@arthasvc.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                    intent.putExtra(Intent.EXTRA_TEXT,message);
                    intent.setType("text/html");
                    intent.setPackage("com.google.android.gm");
                    startActivity(Intent.createChooser(intent, "Send mail"));

                }
            }
        });

    }
}