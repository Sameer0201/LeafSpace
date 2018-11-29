package com.example.treesquad.leafspace;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.treesquad.leafspace.db.api.Api;
import com.example.treesquad.leafspace.db.TreeRecord;
import com.google.firebase.firestore.GeoPoint;

public class TreeInputActivity extends AppCompatActivity {
    private Button submitButton;
    private EditText locationEditText;
    private EditText speciesEditText;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_input);

        getSupportActionBar().setTitle("Add New Tree");
        submitButton = findViewById(R.id.AddTreeSubmitButton);
        locationEditText = findViewById(R.id.TreeLocationEditText);
        speciesEditText = findViewById(R.id.TreeTypeEditText);
        submitButton.setOnClickListener(this::submitTree);

        api = Api.getInstance();
    }

    private void submitTree(View view) {
        String[] values = locationEditText.getText().toString().split(",");
        double lat = 0, lon = 0;
        if (values.length >= 2) {
            lat = Double.parseDouble(values[0]);
            lon = Double.parseDouble(values[1]);
        }

        // TODO: replace this with the actual image
        Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.raw.defaulttree1);

        TreeRecord record = new TreeRecord.Builder(new GeoPoint(lat, lon))
                .species(speciesEditText.getText().toString())
                .image(image)
                .build();

        api.putTree(record, (treeRecord, success) -> {
            // TODO: see if it worked here
        });
    }
}
