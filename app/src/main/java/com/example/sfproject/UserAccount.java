package com.example.sfproject;

public class UserAccount {
    private String idToken; // Firebase UID
    private String emailId;
    private String password;
    private String name;
    private String profileImageUrl; // 프로필 이미지 URL 추가

    private int flower;
    private int flowing;

    private int report;
    public UserAccount() {
        // 기본 생성자가 필요합니다.
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }


    public int getFlower(){return flower;}

    public void setFlower(int flower){} {
        this.flower = flower;
    }
    public int getFlowing(){return flowing;}

    public void setFlowing(int flowing){} {
        this.flowing = flowing;
    }
}



