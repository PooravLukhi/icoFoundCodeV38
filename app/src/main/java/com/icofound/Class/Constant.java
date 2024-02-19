package com.icofound.Class;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.icofound.Activity.ContactUsActivity;
import com.icofound.R;

public class Constant {

    public static String tableUsers = "Users";
    public static String id = "id";
    public static String token = "token";
    public static String name = "name";

    public static String ratingCount = "ratingCount";
    public static String location = "location";
    public static String zipcode = "zipcode";
    public static String showAge = "showAge";
    public static String gender = "gender";
    public static String profilePicLink = "profilePicLink";
    public static String email = "email";
    public static String blockedUserIds = "blockedUserIds";
    public static String bizValues = "bizValues";
    public static String bio = "bio";

    public static String tableExperience = "experience";
    public static String orgName = "orgName";
    public static String titleName = "titleName";
    public static String workDesc = "workDesc";
    public static String startDate = "startDate";
    public static String endDate = "endDate";
    public static String isCurrentWorkPlace = "isCurrentWorkPlace";

    public static String tableEducation = "education";
    public static String courseName = "courseName";
    public static String courseYear = "courseYear";
    public static String courseDescription = "courseDescription";
    public static String degree = "degree";
    public static String schoolName = "schoolName";

    public static String tableSocialMediaLinks = "socialMediaLinks";
    public static String socialMediaLink = "socialMediaLink";

    public static String tableProfessionPreference = "professionPreference";
    public static String professionPref = "ProfessionPref";
    public static String skillPref = "skillPref";

    public static String tableCoreValues = "coreValues";
    public static String inspiration = "inspiration";
    public static String workCulture = "workCulture";
    public static String workValues = "workValues";

    public static String mySkills = "mySkills";
    public static String inspire = "inspire";
    public static String values = "values";
    public static String Swipes = "Swipes";
    public static String conversations = "Conversations";
    public static String tableConnections = "Connections";
    public static String connectionInfoData = "connectionInfoData";

    public static String policyTermsAckDate = "policyTermsAckDate";
    public static String myProfession = "myProfession";
    public static String skillPreference = "skillPreference";


//    public static String tableMySkills = "mySkills";

    
    
    public static void openUrlInBrowser(Context context , String url){
        try {

            if (!url.startsWith("http"))
                url = "http://" + url;


            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        }catch (Exception e){


            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.contactus_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            TextView errorMsg = dialog.findViewById(R.id.error_msg);
            errorMsg.setText("Webpage Not Available");
            TextView ok = dialog.findViewById(R.id.cancel);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

//            Toast.makeText(context, "Webpage Not Available", Toast.LENGTH_SHORT).show();
        }
    }

    public static void readMoreTextView(TextView textView , String desc){
        try {
            String descc = desc;
            if (desc.length()>180) {
                desc=desc.substring(0,180)+"...";
                textView.setText(Html.fromHtml(desc+"<font color='black'> <u>Read More</u></font>"));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText(descc);
                    }
                });
            }
            else
                textView.setText(desc);
        }catch (Exception e){
        }
    }



    public static void hideKeyBoardMethod(final Context con, final View view) {
        try {
            view.post(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
