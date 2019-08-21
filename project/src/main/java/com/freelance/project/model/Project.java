package com.freelance.project.model;

import java.io.Serializable;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class Project implements Serializable {

    private static final long serialVersionUID = -6994655395272795259L;

    private String projectId;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerEmail;
    private String projectDescription;
    private String projectStatus;

    public Project() {

    }

    public Project(JsonObject json) {
        this.projectId = json.getString("projectId");
        this.ownerFirstName = json.getString("ownerFirstName");
        this.ownerLastName = json.getString("ownerLastName");
        this.ownerEmail = json.getString("ownerEmail");
        this.projectDescription = json.getString("projectDescription");
        this.projectStatus = json.getString("projectStatus");
    }

    public JsonObject toJson() {

        final JsonObject json = new JsonObject();
        json.put("projectId", this.projectId);
        json.put("ownerFirstName", this.ownerFirstName);
        json.put("ownerLastName", this.ownerLastName);
        json.put("ownerEmail", this.ownerEmail);
        json.put("projectDescription", this.projectDescription);
        json.put("projectStatus", this.projectStatus);
        return json;
    }


    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getOwnerFirstName() {
        return this.ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerLastName() {
        return this.ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerEmail() {
        return this.ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getProjectDescription() {
        return this.projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProjectStatus() {
        return this.projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }
}
