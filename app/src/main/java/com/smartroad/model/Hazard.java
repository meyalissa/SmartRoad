package com.smartroad.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** Hazard record (Serializable so it can be passed via Intent extras). */
public class Hazard implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("type")
    private String type;
    @SerializedName("description")
    private String description;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("status")
    private String status;
    @SerializedName("datetime")
    private String datetime;
    @SerializedName("photo")
    private String photoUrl;
    @SerializedName("reporter")
    private String reporter;

    public Hazard() { }

    public String getId() { return id; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getDatetime() { return datetime; }
    public String getPhotoUrl() { return photoUrl; }
    public String getReporter() { return reporter; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }

    public double getLatitudeAsDouble() {
        try { return Double.parseDouble(latitude); } catch (Exception e) { return 0d; }
    }

    public double getLongitudeAsDouble() {
        try { return Double.parseDouble(longitude); } catch (Exception e) { return 0d; }
    }

    public void setId(String id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
    public void setStatus(String status) { this.status = status; }
    public void setDatetime(String datetime) { this.datetime = datetime; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setReporter(String reporter) { this.reporter = reporter; }
}
