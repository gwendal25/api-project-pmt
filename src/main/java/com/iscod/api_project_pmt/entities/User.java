package com.iscod.api_project_pmt.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @Email
    @Column(name = "email")
    private String email;

    @NotBlank
    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<ProjectUser> projects = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST})
    private Set<Task> tasks = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name="tasks_notifications",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="task_id"))
    private Set<Task> notificationTasks;

    public void AssignTask(Task task) {
        tasks.add(task);
    }

    public void UnassignTask(Task task) {
        tasks.remove(task);
    }

    public void addNotificationTask(Task task) {
        notificationTasks.add(task);
    }

    public void removeNotificationTask(Task task) {
        notificationTasks.remove(task);
    }
}
