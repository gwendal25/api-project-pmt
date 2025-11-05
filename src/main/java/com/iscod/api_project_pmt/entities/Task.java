package com.iscod.api_project_pmt.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="start_date")
    private Date endDate;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name="project_id", nullable = false)
    private Project project;
}
