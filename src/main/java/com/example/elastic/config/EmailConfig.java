package com.example.elastic.config;

import org.springframework.context.annotation.Configuration;

@Configuration // Email configuration
public class EmailConfig {
    private String user;
    private String password;
    private String accessToken;

    // getters and setters
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
