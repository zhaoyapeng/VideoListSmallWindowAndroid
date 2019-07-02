package com.zhaoyp.video;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zhaoyp.video.activity.DetailsActivity;
import com.zhaoyp.video.activity.ListActivity;
import com.zhaoyp.video.activity.VerticalDetailsActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button textList;
    protected Button textDetail;
    protected Guideline guideline4;
    protected Button textDetailsV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.text_list) {
            startActivity(new Intent(this, ListActivity.class));
        } else if (view.getId() == R.id.text_detail) {
            startActivity(new Intent(this, DetailsActivity.class));
        } else if (view.getId() == R.id.text_details_v) {
            startActivity(new Intent(this, VerticalDetailsActivity.class));
        }
    }

    private void initView() {
        textList = (Button) findViewById(R.id.text_list);
        textList.setOnClickListener(MainActivity.this);
        textDetail = (Button) findViewById(R.id.text_detail);
        textDetail.setOnClickListener(MainActivity.this);
        guideline4 = (Guideline) findViewById(R.id.guideline4);
        textDetailsV = (Button) findViewById(R.id.text_details_v);
        textDetailsV.setOnClickListener(MainActivity.this);
    }
}
