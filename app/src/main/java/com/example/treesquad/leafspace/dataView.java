package com.example.treesquad.leafspace;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treesquad.leafspace.db.Comment;
import com.example.treesquad.leafspace.db.TreeRecord;
import com.example.treesquad.leafspace.db.api.Api;

import java.util.ArrayList;
import java.util.Date;

public class dataView extends AppCompatActivity {

    private ImageView imageView;
    private TextView dataTextView;
    private Api api;
    public TreeRecord t;
    public EditText e ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        imageView = findViewById(R.id.imageView);
        dataTextView = findViewById(R.id.dataText);
        Intent i = getIntent();
        int id = i.getExtras().getInt("id");
        TreeRecord treeRecord = i.getParcelableExtra("tree_record");
        t=treeRecord;
        imageView.setImageBitmap(treeRecord.image);
        e = new EditText(getApplicationContext());
        e.setId(R.id.edit_text);

        //database stuff
        //String s = treeRecord.getTitle();
        String s = "Placeholder Title";
        setTitle(s);
        getComments();
       // TextView textView = (TextView) findViewById(R.id.dataText);
       // textView.setMovementMethod(new ScrollingMovementMethod());


    }

    public void onDownvote(View view) {
    }

    public void onUpvote(View view) {
    }

    public void onNot(View view) {
        Toast.makeText(getApplicationContext(),"Your opinion has been noted.",Toast.LENGTH_LONG).show();
    }

    public void gotoComments(View view) {
        Intent i = new Intent(dataView.this,commentslist.class);
        i.putExtra("treeID","dummy, change this when doing database stuff");
        startActivity(i);
    }

    public void onOK(View view) {
        Toast.makeText(getApplicationContext(),"Your opinion has been noted.",Toast.LENGTH_LONG).show();

    }



    public void getComments(){
        Intent i = getIntent();
        int id = i.getExtras().getInt("id");
        TreeRecord treeRecord = i.getParcelableExtra("tree_record");

        api = Api.getInstance();
        api.getTreeComments(treeRecord, (comments, success) -> {
            String commentText = "";
            for (Comment comment : comments) {
                commentText += comment.text + "\n\n";
            }
          TextView v =   (findViewById(R.id.commentList));
                    v.setText(commentText);
        });
    }

    public void addComment(View view) {
        DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                //String content = (findViewById(R.id.edit_text).toString());
                String content = String.valueOf(e.getText());
                Comment comment = new Comment.Builder().text(content).build();
                api.putTreeComment(t, comment, (info, success1) -> {});

                dialogInterface.cancel();
                getComments();
            }
        };
        AlertDialog.Builder b = new AlertDialog.Builder(dataView.this);
        b.setMessage("Add a new comment.");
        b.setCancelable(true);

        b.setView(e);
        b.setPositiveButton("Add",l );

        b.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog a = b.create();
        a.show();
    }
}
