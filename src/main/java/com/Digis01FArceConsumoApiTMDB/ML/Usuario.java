package com.Digis01FArceConsumoApiTMDB.ML;

public class Usuario {
    private Boolean success;
    private String expires_at;
    private String request_token;
    private String username;
    private String password;
    private String session_id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}
