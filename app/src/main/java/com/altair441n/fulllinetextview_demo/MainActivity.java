package com.altair441n.fulllinetextview_demo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.altair441n.fulllinetextview.FullLineTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FullLineTextView tvContent = findViewById(R.id.tv_content);
        TextView tvExpand = findViewById(R.id.tv_expand);
        tvContent.setExpandableListener(expandable -> tvExpand.setVisibility(expandable ? View.VISIBLE : View.GONE));
        tvContent.setOnExpandListener(expand -> {
            if (expand) {
                tvExpand.setText("折叠");
            } else {
                tvExpand.setText("展开");
            }
        });
        tvExpand.setOnClickListener(view -> tvContent.setExpand(!tvContent.isExpand()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}