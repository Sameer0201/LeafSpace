package com.example.treesquad.leafspace;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.treesquad.leafspace.R;
import com.example.treesquad.leafspace.db.TreeRecord;
import com.example.treesquad.leafspace.db.api.Api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TreeRecordListAdapter extends ArrayAdapter<TreeRecord> {

    private Api api;
    private DateFormat dateFormat;


    public TreeRecordListAdapter(ArrayList<TreeRecord> data, Context context) {
        super(context, R.layout.tree_record_row_item, data);
        api = Api.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, new Locale("en", "US"));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TreeRecord treeRecord = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.tree_record_row_item, parent, false);
            viewHolder.treeSpecies = convertView.findViewById(R.id.tree_species);
            viewHolder.treeDate = convertView.findViewById(R.id.tree_date);
            viewHolder.treePreview = convertView.findViewById(R.id.tree_preview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.treeSpecies.setText(treeRecord.species);
        viewHolder.treeDate.setText(dateFormat.format(treeRecord.created));

        if(treeRecord.image == null) {
            viewHolder.treePreview.setImageResource(R.drawable.tree);
        } else {
            viewHolder.treePreview.setImageBitmap(treeRecord.image);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView treeSpecies;
        TextView treeDate;
        ImageView treePreview;

    }
}
