package com.simsekberna.etech_app_v1.Models;

public class User {
    public String username;
    public String email;
    public String profile;
    public Object cartInfo;
    public User(){

    }
    public User(String username, String email, String profile) {
        this.username = username;
        this.email = email;
        this.profile = profile;
    }
}
