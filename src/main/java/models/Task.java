package models;

import models.enums.Priority;
import models.enums.TaskStatus;

import java.time.LocalDate;

public class Task {

    private String id;
    private String taskName;
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
                String taskName,
                String description,
                TaskStatus status,
                LocalDate startDate,
                LocalDate endDate,
                Priority priority,
                String assignedBuilderId,
                String projectId) {

        this.id = id;
        this.taskName = taskName;
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

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getAssignedBuilderId() {
        return assignedBuilderId;
    }

    public void setAssignedBuilderId(String assignedBuilderId) {
        this.assignedBuilderId = assignedBuilderId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
