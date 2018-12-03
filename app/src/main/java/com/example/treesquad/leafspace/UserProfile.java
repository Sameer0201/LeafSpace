package com.example.treesquad.leafspace;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.treesquad.leafspace.db.TreeRecord;
import com.example.treesquad.leafspace.db.api.Api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserProfile extends Activity {

    private Api api;
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        api = Api.getInstance();
        ListView listView = findViewById(R.id.user_post_list);

        api.getAllTreesForActiveUser((trees, success) -> {
            ArrayAdapter<TreeRecord> adapter = new TreeRecordListAdapter((ArrayList<TreeRecord>)trees, getApplicationContext());
            adapter.sort((TreeRecord t1, TreeRecord t2) ->
                    (int) (t2.created.getTime() - t1.created.getTime())
            );
            listView.setAdapter(adapter);

            for(TreeRecord tree : trees) {
                executorService.execute(() ->
                        api.getRecordImage(tree, (record, success1) -> {
                            adapter.notifyDataSetChanged();
                        })
                );
            }
        });

        listView.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
            TreeRecord record = (TreeRecord) adapterView.getAdapter().getItem(i);

            Intent intent = new Intent(UserProfile.this, dataView.class);
            intent.putExtra("tree_record", record);
            startActivity(intent);

        });
    }


}
