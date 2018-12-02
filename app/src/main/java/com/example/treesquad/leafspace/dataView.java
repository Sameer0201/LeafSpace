package com.example.treesquad.leafspace;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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

public class dataView extends AppCompatActivity implements Api.Callback {

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
        String id = i.getExtras().getString("id");
        TreeRecord treeRecord = i.getParcelableExtra("tree_record");

        Api.getInstance().getRecordImage(treeRecord,this);
        t=treeRecord;
        imageView.setImageBitmap(treeRecord.image);


        e = new EditText(getApplicationContext());
        e.setId(R.id.edit_text);

        //database stuff
        //String s = treeRecord.getTitle();

        String a = treeRecord.species;
        ((TextView) findViewById(R.id.species)).setText("Species:"+treeRecord.species);
      try {
          ((TextView) findViewById(R.id.age)).setText("Age in Years:"+Integer.toString(treeRecord.age));
      } catch (NullPointerException e){
          Log.i("DATAVIEW","Failed to get the age");
      }
        ((TextView) findViewById(R.id.dataText)).setText("Recommended Action:"+treeRecord.recommendations+"\n\n");
        ((TextView) findViewById(R.id.diam)).setText("Diameter:"+Double.toString(treeRecord.diameter));
        ((TextView) findViewById(R.id.height)).setText("Height:"+Double.toString(treeRecord.height));
        ((TextView) findViewById(R.id.life)).setText("Life expectancy:"+Double.toString(treeRecord.lifeExpectancy));

       // ((TextView) findViewById(R.id.health)).setText(treeRecord.health);



        String s = "Placeholder Title";
        setTitle(s);
        getComments();
        //TextView textView = (TextView) findViewById(R.id.dataText);
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

    @Override
    public void handle(Object o, boolean success) {
        if(o instanceof TreeRecord)
        {
            imageView.setImageBitmap(((TreeRecord) o).image);
        }
    }

    /*
    public void PopulateViews()
    {
        TreeRecord treeRecord = t;
        imageView.setImageBitmap(treeRecord.image);

        String a = treeRecord.species;
        ((TextView) findViewById(R.id.species)).setText("Species:"+treeRecord.species);
        try {
            ((TextView) findViewById(R.id.age)).setText("Age in Years:"+Integer.toString(treeRecord.age));
        } catch (NullPointerException e){
            Log.i("DATAVIEW","Failed to get the age");
        }
        ((TextView) findViewById(R.id.dataText)).setText("Recommended Action:"+treeRecord.recommendations+"\n\n");
        ((TextView) findViewById(R.id.diam)).setText("Diameter:"+Double.toString(treeRecord.diameter));
        ((TextView) findViewById(R.id.height)).setText("Height:"+Double.toString(treeRecord.height));
        ((TextView) findViewById(R.id.life)).setText("Life expectancy:"+Double.toString(treeRecord.lifeExpectancy));

        // ((TextView) findViewById(R.id.health)).setText(treeRecord.health);
    }

    @Override
    public void handle(Object o, boolean success) {
        Log.d("LEAFSPACE","Received object: " + o.toString());
        if(o instanceof  TreeRecord)
        {
            t = (TreeRecord) o;
            PopulateViews();
        }
    }
    */
}
