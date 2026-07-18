package com.smartroad.model;

import com.google.gson.annotations.SerializedName;

/** Response payload returned by report_hazard.php after submitting a new hazard report. */
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
