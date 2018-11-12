package com.example.treesquad.leafspace.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
        this.storage.getReference()
                .child(name)
                .getBytes(1025 * 1024)
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
                        Date created = (Date) doc.get("created");
                        String imageName = (String) doc.get("image");
                        GeoPoint location = (GeoPoint) doc.get("location");
                        records.add(new TreeRecord(location, null, created, imageName, doc.getId()));
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
                Map<String, Object> recordMap = new HashMap<>();
                recordMap.put("created", record.created);
                recordMap.put("image", imageFileName);
                recordMap.put("location", record.location);

                db.collection("trees")
                        .add(recordMap)
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
