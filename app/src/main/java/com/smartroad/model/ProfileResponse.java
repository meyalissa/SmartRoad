package com.smartroad.model;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("id")
    private String id;
    @SerializedName("fullname")
    private String fullname;
    @SerializedName("username")
    private String username;
    @SerializedName("photo")
    private String photoUrl;
    @SerializedName("total_reports")
    private int totalReports;
    @SerializedName("resolved_reports")
    private int resolvedReports;
    @SerializedName("pending_reports")
    private int pendingReports;

    public String getId() { return id; }
    public String getFullname() { return fullname; }
    public String getUsername() { return username; }
    public String getPhotoUrl() { return photoUrl; }
    public int getTotalReports() { return totalReports; }
    public int getResolvedReports() { return resolvedReports; }
    public int getPendingReports() { return pendingReports; }

    public void setFullname(String fullname) { this.fullname = fullname; }
    public void setUsername(String username) { this.username = username; }
    public void setTotalReports(int t) { this.totalReports = t; }
    public void setResolvedReports(int r) { this.resolvedReports = r; }
    public void setPendingReports(int p) { this.pendingReports = p; }
}
