package com.icofound.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.icofound.Adapters.SkillAdp;
import com.icofound.Model.SkillData;
import com.icofound.R;

import java.util.ArrayList;
import java.util.List;

public class SkillListActivity extends AppCompatActivity {
    public static int selectedPos = 0;
    public static SkillData skillData;
    List<SkillData> skillDataList;
    TextView tvDone, tvCancel;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_list);




        tvCancel = findViewById(R.id.tvCancel);
        tvDone = findViewById(R.id.tvDone);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(new SkillAdp(skillDataList, new SkillAdp.OnItemClick() {

            @Override
            public void onItemClick(View view, int pos) {
                skillData = skillDataList.get(pos);
                selectCards(view, pos);

            }
        }));


    }

    public void selectCards(View view, int i) {
        selectedPos = i;
//        startActivity(new Intent(SkillListActivity.this, ProductDetailActivity.class));


    }

}