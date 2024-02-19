package com.icofound.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.icofound.Adapters.ListAdapter;
import com.icofound.Class.Utils;
import com.icofound.R;
import com.icofound.TinyDB;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class FilterFragment extends Fragment {

    TextView clear,save,myskills;
    MaterialSpinner i_am_professional;
    String[] shapes = {"Investor", "Executive", "Founder", "Director", "Team Member/Individual Contributor","C - Suite","Freelancer","Student","Other"};
    String str_i_proffessional;
    TinyDB tinydb;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String skill_filter, profession_filter;

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        clear = view.findViewById(R.id.clear);
        save = view.findViewById(R.id.save);
        myskills = view.findViewById(R.id.myskills);
        i_am_professional = view.findViewById(R.id.i_am_professional);


        tinydb = new TinyDB(getContext());

        preferences = getActivity().getSharedPreferences("filter",MODE_PRIVATE);
        editor = preferences.edit();

        skill_filter = preferences.getString("skill_filter", "");
        profession_filter = preferences.getString("profession_filter", "");

        i_am_professional.setItems(shapes);
        i_am_professional.setSelected(true);

        if (!profession_filter.equalsIgnoreCase("")){

            for (int i = 0; i < shapes.length; i++) {

                if (profession_filter.equalsIgnoreCase(shapes[i])){
                    i_am_professional.setTextColor(Color.BLACK);
                    i_am_professional.setSelectedIndex(i);
                    str_i_proffessional = shapes[i];
                    break;
                }
            }
        }

        if (!skill_filter.equalsIgnoreCase("")){
            myskills.setText(skill_filter);
        }

        i_am_professional.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                i_am_professional.setTextColor(Color.BLACK);
                str_i_proffessional = shapes[position];
            }
        });

        myskills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear().commit();
                tinydb.putListString("filterskills",new ArrayList<>());
                getParentFragmentManager().popBackStack();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("skill_filter",myskills.getText().toString());
                editor.putString("profession_filter",str_i_proffessional);
                editor.commit();
                if (getParentFragmentManager() != null){
                    getParentFragmentManager().popBackStack();
                }

            }
        });

        return view;
    }

    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.fragment_my_skills);

        RecyclerView listrecycler = bottomSheetDialog.findViewById(R.id.listrecycler);
        TextView done = bottomSheetDialog.findViewById(R.id.done);

        String[] skills =  {"iOS Developer",
                "Java Developer",
                "FullStack Developer",
                "Cloud computing",
                "Artificial intelligence",
                "Sales leadership",
                "Analysis",
                "Translation",
                "Mobile app development",
                "People management",
                "Video production",
                "Audio production",
                "UX design",
                "SEO/SEM marketing",
                "Blockchain",
                "Industrial design",
                "Creativity",
                "Collaboration",
                "Adaptability",
                "Time management",
                "Persuasion",
                "Digital journalism",
                "Animation",
                "Marketing"};

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        ListAdapter adapter = new ListAdapter(getActivity(), Utils.skillDataList, tinydb,true, false);
        listrecycler.setLayoutManager(manager);
        listrecycler.setAdapter(adapter);

        done.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                String listString = tinydb.getListString("filterskills").stream().map(Object::toString)
                        .collect(Collectors.joining(","));

                myskills.setText(listString);

            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog.dismiss();
                String listString = tinydb.getListString("filterskills").stream().map(Object::toString)
                        .collect(Collectors.joining(","));

                myskills.setText(listString);

            }
        });

        bottomSheetDialog.show();
    }
}