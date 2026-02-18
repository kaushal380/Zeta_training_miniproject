package models;

import models.enums.ProjectStatus;
import models.enums.ProjectType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Project {

    private String id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private ProjectType type;
    private Double estimatedCost;
    private Address location;
    private String managerId;

    private List<String> builderIds = new ArrayList<>();

    public Project() {
    }

    public Project(String id,
                   String name,
                   String description,
                   LocalDate startDate,
                   LocalDate endDate,
                   ProjectStatus status,
                   ProjectType type,
                   Address location,
                   Double estimatedCost) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.type = type;
        this.estimatedCost = estimatedCost;
        this.location = location;
        this.builderIds = new ArrayList<>();
    }

    public Address getLocation() {return location;}

    public void setLocation(Address location) {this.location = location;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public ProjectType getType() {
        return type;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public List<String> getBuilderIds() {
        return builderIds;
    }

    public void setBuilderIds(List<String> builderIds) {
        this.builderIds = builderIds;
    }


    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", estimatedCost=" + estimatedCost +
                ", location=" + location +
                '}';
    }

}
