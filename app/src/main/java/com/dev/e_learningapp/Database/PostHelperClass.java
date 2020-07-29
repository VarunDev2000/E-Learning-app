package com.dev.e_learningapp.Database;

public class PostHelperClass {
    String name,content,email,phoneNo;

    public PostHelperClass(){}

    public PostHelperClass(String name, String content, String email, String phoneNo) {
        this.name = name;
        this.content = content;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
