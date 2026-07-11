package com.smartroad.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** Road-maintenance record for a hazard report — present only once the admin has logged one. */
public class Maintenance implements Serializable {

    @SerializedName("team")
    private String team;
    @SerializedName("notes")
    private String notes;
    @SerializedName("repair_date")
    private String repairDate;
    @SerializedName("completed_date")
    private String completedDate;

    public String getTeam() { return team; }
    public String getNotes() { return notes; }
    public String getRepairDate() { return repairDate; }
    public String getCompletedDate() { return completedDate; }
}
