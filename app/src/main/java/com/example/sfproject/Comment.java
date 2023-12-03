package com.example.sfproject;

import com.google.firebase.Timestamp;
import java.util.UUID;

public class Comment {

    private String commentId;
    private String content, uid, uimg, uname, postKey;
    private Timestamp timestamp;

    // 기본 생성자
    public Comment() {
    }

    // 수정된 생성자
    public Comment(String content, String uid, String uimg, String uname, String postKey) {
        this.commentId = UUID.randomUUID().toString(); // 고유 ID 생성
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.postKey = postKey;
        this.timestamp = Timestamp.now();
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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
