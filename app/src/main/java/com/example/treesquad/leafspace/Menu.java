package com.example.treesquad.leafspace;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.treesquad.leafspace.db.Comment;
import com.example.treesquad.leafspace.db.api.Api;
import com.example.treesquad.leafspace.db.TreeRecord;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

public class Menu extends AppCompatActivity {

    private static final String TAG = "LEAFSPACE";
    private Api api;
    private ImageView menuPic;
    private List<TreeRecord> treeRecords;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecycleViewTreeAdapter rAdapter;

    private TextView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        // set an exit transition
        getWindow().setExitTransition(new Slide());

        setContentView(R.layout.activity_menu);
        loadingView = findViewById(R.id.MenuLoadingText);
        recyclerView = findViewById(R.id.MenuRecycleView);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        rAdapter = new RecycleViewTreeAdapter(treeRecords);
        recyclerView.setAdapter(rAdapter);

        //menuPic = findViewById(R.id.menuPic);
        api = Api.getInstance();

        api.getAllTrees((treeRecords, success) -> {
            if (!success) {
                Log.d(TAG, "failed to fetch tree records");
                return;
            }
            this.treeRecords = treeRecords;
            // just display the first tree in the list for proof of concept

            TreeRecord record = treeRecords.get(0);


            // images need to be downloaded separately
            api.getRecordImage(record, (treeRecord, success1) -> {
                //menuPic.setImageBitmap(treeRecord.image);
                this.runOnUiThread(() -> {
                    Log.d(TAG, "Posting update runnable");
                    loadingView.setVisibility(View.INVISIBLE);
                    rAdapter.SetRecords(treeRecords);

                });
            });

            // test adding a comment
            Comment comment = new Comment.Builder().text("test " + new Date()).build();
            api.putTreeComment(record, comment, (info, success1) -> {});

        });

    }
    private int[] pids= {0,0,0,0,0,0,0,0,0,0};
    private int currentPID=0;

    public void onImgClick(View view) {
        Intent i = new Intent(Menu.this, dataView.class);
        int pid=pids[currentPID];
        i.putExtra("id",pid);
        treeRecords.get(0).image = null; //if you dont do this the parcel is too big and android dies
        i.putExtra("tree_record", treeRecords.get(0));
        //database stuff
        startActivity(i);
    }


    //depending on how the database is implemented will determine how these methods are implemented in reality.
    //the onnextimg will also be put on an alarm for like 15 seconds to scroll through all of the trees
    public void onprevimg(View view) {
        if(currentPID==0){
            currentPID=9;
        }
        else{
            currentPID--;
        }
        //set the main img to current pid, then change the prev and next. This requires database stuff
    }

    public void onnextimg(View view) {
        if(currentPID==9){
            currentPID=0;
        }
        else{
            currentPID++;
        }
        //set the main img to current pid, then change the prev and next. This requires database stuff

    }

    public void OnAddNewTreeClicked(View view)
    {
        Intent i = new Intent(this, TreeInputActivity.class);
        startActivity(i,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void OnBrowseTreesClicked(View view)
    {
        Intent i = new Intent(this, MapActivity.class);
        startActivity(i,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public class RecycleViewTreeAdapter extends RecyclerView.Adapter<RecycleViewTreeAdapter.TreeViewHolder>{
        @NonNull
        @Override
        public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tree_card_layout,parent,false);
            TreeViewHolder tvh = new TreeViewHolder(v);
            return tvh;
        }

        @Override
        public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {

            if(holder.treeType!=null) holder.treeType.setText(treeRecords.get(position).species);

            if(holder.treeImage!=null)holder.treeImage.setImageBitmap(treeRecords.get(position).image);

            api.getRecordImage(treeRecords.get(position), (trecord, success1) -> {
                if(holder.treeImage!=null)holder.treeImage.setImageBitmap(trecord.image);
                holder.itemView.invalidate();
            });

            holder.treeId = treeRecords.get(position).id;
            holder.tr = treeRecords.get(position);

        }

        @Override
        public int getItemCount() {
            if(trees==null)return 0;
            return trees.size();
        }

        public class TreeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            CardView cardView;
            ImageView treeImage;
            TextView treeType;
            String treeId;
            TreeRecord tr;

            TreeViewHolder(View itemView)
            {
                super(itemView);
                this.cardView = (CardView) itemView.findViewById(R.id.MenuCardView);
                this.treeImage = (ImageView) itemView.findViewById(R.id.CardViewImageView);
                this.treeType = (TextView) itemView.findViewById(R.id.CardViewTreeText);

                itemView.setOnClickListener(this);
            }

            public void onClick(View v)
            {
                Intent i = new Intent(Menu.this, dataView.class);

                i.putExtra("id",treeId);
                tr.image = null; //if you dont do this the parcel is too big and android dies
                i.putExtra("tree_record", this.tr);
                //database stuff
                startActivity(i);
            }
        }
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        List<TreeRecord> trees;
        public RecycleViewTreeAdapter(List<TreeRecord> tempRecords)
        {
            trees = tempRecords;
        }
        public void SetRecords(List<TreeRecord> tempRecords)
        {
            trees=tempRecords;
            notifyDataSetChanged();
        }
    }
}
