package com.example.treesquad.leafspace.db.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.treesquad.leafspace.db.Comment;
import com.example.treesquad.leafspace.db.TreeRecord;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Api {
    private final static String TAG = "TREESPACE-API";
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private static Api apiInstance;

    private Api() {
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    public static Api getInstance() {
        if (Api.apiInstance == null) {
            Api.apiInstance = new Api();
        }
        return Api.apiInstance;
    }

    private void getImageByName(String name, Callback<Bitmap> callback) {
        Log.d(TAG, "Grabbing image with name: " + name);
        this.storage.getReference()
                .child(name)
                .getBytes(1025 * 1024*25)
                .addOnSuccessListener(bytes -> {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    callback.handle(image, true);
                });
    }

    private void putImage(Bitmap bitmap, Callback<String> callback) {
        String imageFileName = "img" + System.currentTimeMillis() + ".jpg";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        storage.getReference()
                .child(imageFileName)
                .putBytes(stream.toByteArray())
                .addOnCompleteListener(task ->
                    callback.handle(imageFileName, task.isSuccessful())
                );
    }

    public void getTreeById(String id, Callback<TreeRecord> callback) {
        Log.d(TAG, "Grabbing tree with id: " + id);
        this.db.collection("trees")
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Date created = (Date) task.getResult().get("created");
                        String imageName = (String) task.getResult().get("image");
                        GeoPoint location = (GeoPoint) task.getResult().get("location");



                        getImageByName(imageName, (bitmap, success) ->
                                callback.handle(new TreeRecord(location, bitmap, created, imageName, id), success)
                        );
                    } else {
                        callback.handle(null, false);
                    }
                });
    }

    public void getTreeComments(TreeRecord treeRecord, Callback<List<Comment>> callback) {
        this.db.collection("trees")
                .document(treeRecord.id)
                .collection("comments")
                .get()
                .addOnSuccessListener(task -> {
                   List<Comment> comments = new ArrayList<>();
                   for (DocumentSnapshot doc : task.getDocuments()) {
                       comments.add(new Comment(doc));
                   }
                   callback.handle(comments, true);
                })
                .addOnFailureListener(task -> callback.handle(null, false));
    }

    public void putTreeComment(TreeRecord treeRecord, Comment comment, Callback<Comment> callback) {
        comment.created = new Date();
        this.db.collection("trees")
                .document(treeRecord.id)
                .collection("comments")
                .add(comment.toMap())
                .addOnCompleteListener(task -> callback.handle(comment, task.isSuccessful()));
    }

    public void getRecordImage(TreeRecord record, Callback<TreeRecord> callback) {
        getImageByName(record.imageName, (bitmap, success) -> {
            record.image = bitmap;
            callback.handle(record, success);
        });
    }

    public void getAllTrees(Callback<List<TreeRecord>> callback) {
        this.db.collection("trees")
                .get()
                .addOnSuccessListener(task -> {
                    List<TreeRecord> records = new ArrayList<>(task.getDocuments().size());
                    for (DocumentSnapshot doc : task.getDocuments()) {
                        records.add(new TreeRecord(doc));
                    }
                    callback.handle(records, true);
                })
                .addOnFailureListener(task -> callback.handle(null, false));
    }

    public void deleteTree(TreeRecord record, Callback<TreeRecord> callback) {
        db.collection("trees")
                .document(record.id)
                .delete()
                .addOnCompleteListener(task -> callback.handle(record, task.isSuccessful()));
    }

    public void putTree(TreeRecord record, Callback<TreeRecord> callback) {
        putImage(record.image, (imageFileName, success) -> {
            if (success) {
                record.imageName = imageFileName;
                db.collection("trees")
                        .add(record.toMap())
                        .addOnCompleteListener(dbtask -> callback.handle(record, dbtask.isSuccessful()));
            } else {
                callback.handle(null, false);
            }
        });
    }

    private void updateTree(TreeRecord record, Map<String, Object> recordMap, Callback<TreeRecord> callback) {
        db.collection("trees")
                .document(record.id)
                .set(recordMap)
                .addOnCompleteListener(task ->
                    callback.handle(record, task.isSuccessful())
                );
    }

    public void updateTree(TreeRecord record, boolean updateImg, Callback<TreeRecord> callback) {
        Map<String, Object> recordMap = new HashMap<>();
        recordMap.put("created", record.created);
        recordMap.put("location", record.location);
        if (updateImg) {
            putImage(record.image, (imageFileName, success) -> {
                if (success) {
                    recordMap.put("image", imageFileName);
                    updateTree(record, recordMap, callback);
                }
            });
        } else {
            updateTree(record, recordMap, callback);
        }
    }

    public interface Callback<T> {
        void handle(T t, boolean success);
    }
}
