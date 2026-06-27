package com.smartroad.model;

import com.google.gson.annotations.SerializedName;

public class ReportResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("id")
    private String id;

    public boolean isSuccess() { return "success".equalsIgnoreCase(status); }

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getId() { return id; }
}
