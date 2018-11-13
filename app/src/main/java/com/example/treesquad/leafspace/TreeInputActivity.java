package com.example.treesquad.leafspace;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TreeInputActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_input);

        getSupportActionBar().setTitle("Add New Tree");

    }
}
