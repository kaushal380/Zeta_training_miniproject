package models;

import models.enums.TaskStatus;
import models.enums.Priority;

import java.time.LocalDate;

public class Task {

    private String id;
    private String description;
    private TaskStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Priority priority;
    private String assignedBuilderId;
    private String projectId;

    public Task() {
    }

    public Task(String id,
                String description,
                TaskStatus status,
                LocalDate startDate,
                LocalDate endDate,
                Priority priority,
                String assignedBuilderId,
                String projectId) {

        this.id = id;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.assignedBuilderId = assignedBuilderId;
        this.projectId = projectId;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getAssignedBuilderId() {
        return assignedBuilderId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setAssignedBuilderId(String assignedBuilderId) {
        this.assignedBuilderId = assignedBuilderId;
    }

}
