package com.example.treesquad.leafspace.db;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Comment {
    public String id;
    public Date created;
    public String text;

    public Comment(DocumentSnapshot doc) {
       id = doc.getId();
       created = doc.getDate("created");
       text = doc.getString("text");
    }

    private Comment(Builder builder) {
        id = builder.id;
        created = builder.created;
        text = builder.text;
    }

    public Map<String,Object> toMap() {
        Map<String, Object> recordMap = new HashMap<>();
        recordMap.put("created", created);
        recordMap.put("text", text);

        return recordMap;
    }

    public static class Builder {
        private String id;
        private Date created;
        private String text;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder created(Date created) {
            this.created = created;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }
    }
}
