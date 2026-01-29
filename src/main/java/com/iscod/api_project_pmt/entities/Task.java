package com.iscod.api_project_pmt.entities;

import com.iscod.api_project_pmt.enums.TaskPriority;
import com.iscod.api_project_pmt.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.joda.time.DateTimeComparator;

import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @NotBlank
    @Column(name="name")
    private String name;

    @NotBlank
    @Column(name="description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name="end_date")
    private Date endDate;

    @NotNull
    @Column(name="task_priority")
    private TaskPriority taskPriority;

    @NotNull
    @Column(name="task_status")
    private TaskStatus taskStatus;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name="project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToMany(mappedBy="notificationTasks")
    private Set<User> notificationUsers;

    @OneToMany(mappedBy="task", cascade = {CascadeType.PERSIST})
    private Set<TaskHistoryEntry> taskHistoryEntries = new HashSet<>();

    public void addTaskHistoryEntry(TaskHistoryEntry taskHistoryEntry) {
        taskHistoryEntries.add(taskHistoryEntry);
    }

    public void addNotificationUser(User user) {
        notificationUsers.add(user);
    }

    public void removeNotificationUser(User user) {
        notificationUsers.remove(user);
    }

    public boolean hasNotificationUser(User user) {
        return notificationUsers.contains(user);
    }

    public Task(String name, String description, Date endDate, TaskPriority taskPriority, TaskStatus taskStatus) {
        this.name = name;
        this.description = description;
        this.endDate = endDate;
        this.taskPriority = taskPriority;
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", endDate=" + endDate +
                ", taskPriority=" + taskPriority +
                ", taskStatus=" + taskStatus +
                ", project=" + project +
                ", taskHistoryEntries=" + taskHistoryEntries +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && DateTimeComparator.getDateOnlyInstance().compare(endDate, task.endDate) == 0 && taskPriority == task.taskPriority && taskStatus == task.taskStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, endDate, taskPriority, taskStatus);
    }

    public List<String> getUsersTaskAssignedNotifiedMails() {
        return notificationUsers.stream().map(User::getEmail).toList();
    }
}
