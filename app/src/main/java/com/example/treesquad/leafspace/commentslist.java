package com.example.treesquad.leafspace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class commentslist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentslist);
        LinearLayout v = (LinearLayout) findViewById(R.id.commentList);
        ArrayList<comment> h = getComments("null");
        for (comment c:h) {
            TextView u = new TextView(this);
            u.setText(
                    c.toString()+
            "\n"
            );
            v.addView(u);
        }
    }

    public ArrayList<comment> getComments(String id){
        ArrayList<comment> c = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
        c.add(new comment("test comment"+i,"loading"));
        }
        return c;
        }

}
