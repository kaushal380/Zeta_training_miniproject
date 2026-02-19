package dto;

import models.enums.Priority;
import models.enums.TaskStatus;

import java.time.LocalDate;

public class TaskUpdateRequest {

    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Priority priority;
    private TaskStatus status;

    public TaskUpdateRequest() {
    }

    public TaskUpdateRequest(String description, LocalDate startDate, LocalDate endDate, Priority priority, TaskStatus status) {

        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.status = status;
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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
