package com.icofound.Class;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.dd.CircularProgressButton;
import com.icofound.Activity.MainActivity;
import com.icofound.Model.SkillData;
import com.icofound.R;

import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback;
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.DialogPropertiesPendulum;
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Utils implements Serializable, InternetConnectivityListener {

    private InternetAvailabilityChecker mInternetAvailabilityChecker;
    Dialog dialog;
    Context context;
    public static boolean isShowDialog = false;
    TextView ok;
    public static List<SkillData> skillDataList = new ArrayList<>();


    public Utils(Context context) {
        this.context = context;
    }

    public void intialise() {

        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_network_error);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInternetAvailabilityChecker.onNetworkChange(true);
            }
        });

        dialog.setCancelable(false);

    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (isConnected) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } else {
            if (!((Activity) context).isFinishing()) {
                if (dialog != null)
                dialog.show();
            }

        }
    }

}
