package com.iscod.api_project_pmt.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private Date startDate;

    @OneToMany(mappedBy = "project", cascade = {CascadeType.PERSIST})
    private Set<Task> tasks = new HashSet<Task>();

    @OneToMany(mappedBy = "project")
    private Set<ProjectUser> projectUsers = new HashSet<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public Project(String name, String description, Date startDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
    }
}
