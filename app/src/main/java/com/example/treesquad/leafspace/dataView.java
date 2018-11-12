package com.example.treesquad.leafspace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.treesquad.leafspace.db.TreeRecord;

public class dataView extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        imageView = findViewById(R.id.imageView);
        Intent i = getIntent();
        int id = i.getExtras().getInt("id");
        TreeRecord treeRecord = i.getParcelableExtra("tree_record");
        imageView.setImageBitmap(treeRecord.image);

        //database stuff
        String s = "Placeholder Post title";
        setTitle(s);

       // TextView textView = (TextView) findViewById(R.id.dataText);
       // textView.setMovementMethod(new ScrollingMovementMethod());

    }

    public void onDownvote(View view) {
    }

    public void onUpvote(View view) {
    }
}
