package com.smartroad.model;

import com.google.gson.annotations.SerializedName;

/** Response payload returned by login.php. */
public class LoginResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("id")
    private String id;
    @SerializedName("fullname")
    private String fullname;
    @SerializedName("username")
    private String username;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() { return "success".equalsIgnoreCase(status); }

    public String getStatus() { return status; }
    public String getId() { return id; }
    public String getFullname() { return fullname; }
    public String getUsername() { return username; }
    public String getMessage() { return message; }

    public void setStatus(String status) { this.status = status; }
    public void setId(String id) { this.id = id; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public void setUsername(String username) { this.username = username; }
    public void setMessage(String message) { this.message = message; }
}
