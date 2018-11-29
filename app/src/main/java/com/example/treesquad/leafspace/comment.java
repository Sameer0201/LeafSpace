package com.example.treesquad.leafspace;



public class comment {
    String Content;
    String author;

    public comment(String content, String author) {
        this.Content = content;
        this.author = author;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return this.author + " says..."+
                "\n"
                + this.Content;
    }
}
