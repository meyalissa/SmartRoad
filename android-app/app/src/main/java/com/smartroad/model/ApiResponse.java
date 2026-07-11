package com.smartroad.model;

import com.google.gson.annotations.SerializedName;

/**
 * Generic response envelope for simple write endpoints (profile update,
 * password change). The fullname/email/photo fields are only populated by
 * update_profile.php — other endpoints simply leave them null.
 */
public class ApiResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("fullname")
    private String fullname;
    @SerializedName("email")
    private String email;
    @SerializedName("photo")
    private String photoUrl;

    public boolean isSuccess() { return "success".equalsIgnoreCase(status); }

    public String getMessage() { return message; }
    public String getFullname() { return fullname; }
    public String getEmail() { return email; }
    public String getPhotoUrl() { return photoUrl; }

    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
}
