package com.iscod.api_project_pmt.entities;

import com.iscod.api_project_pmt.enums.TaskPriority;
import com.iscod.api_project_pmt.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tasks_history")
public class TaskHistoryEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="description", columnDefinition = "TEXT")
    private String description;

    @Column(name="start_date")
    private Date endDate;

    @Column(name="edit_date")
    private Date editDate;

    @Column(name="task_priority")
    private TaskPriority taskPriority;

    @Column(name="task_status")
    private TaskStatus taskStatus;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    public TaskHistoryEntry(String name, String description, Date endDate, TaskPriority taskPriority, TaskStatus taskStatus) {
        this.name = name;
        this.description = description;
        this.endDate = endDate;
        this.taskPriority = taskPriority;
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return "TaskHistoryEntry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", endDate=" + endDate +
                ", taskPriority=" + taskPriority +
                ", taskStatus=" + taskStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TaskHistoryEntry that = (TaskHistoryEntry) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(endDate, that.endDate) && taskPriority == that.taskPriority && taskStatus == that.taskStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, endDate, taskPriority, taskStatus);
    }
}
