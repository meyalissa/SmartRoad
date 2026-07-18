package com.smartroad.model;

import com.google.gson.annotations.SerializedName;

/** Response payload returned by profile.php, combining account details with report statistics. */
public class ProfileResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("id")
    private String id;
    @SerializedName("fullname")
    private String fullname;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("photo")
    private String photoUrl;
    @SerializedName("created_at")
    private String joinDate;
    @SerializedName("total_reports")
    private int totalReports;
    @SerializedName("resolved_reports")
    private int resolvedReports;
    @SerializedName("pending_reports")
    private int pendingReports;
    @SerializedName("investigating_reports")
    private int investigatingReports;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() { return "success".equalsIgnoreCase(status); }

    public String getId() { return id; }
    public String getFullname() { return fullname; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhotoUrl() { return photoUrl; }
    public String getJoinDate() { return joinDate; }
    public int getTotalReports() { return totalReports; }
    public int getResolvedReports() { return resolvedReports; }
    public int getPendingReports() { return pendingReports; }
    public int getInvestigatingReports() { return investigatingReports; }
    public String getMessage() { return message; }

    public void setStatus(String status) { this.status = status; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setTotalReports(int t) { this.totalReports = t; }
    public void setResolvedReports(int r) { this.resolvedReports = r; }
    public void setPendingReports(int p) { this.pendingReports = p; }
    public void setInvestigatingReports(int i) { this.investigatingReports = i; }
}
