package com.example.sfproject;

import com.google.firebase.Timestamp;

public class Comment {

    private String content, uid, uimg, uname, postKey;
    private Timestamp timestamp;

    // 기본 생성자
    public Comment() {
    }

    // 생성자
    public Comment(String content, String uid, String uimg, String uname, String postKey) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.postKey = postKey;
        this.timestamp = Timestamp.now(); // Firestore에서 제공하는 Timestamp 사용
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
