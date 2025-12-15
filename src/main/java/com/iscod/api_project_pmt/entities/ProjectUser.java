package com.iscod.api_project_pmt.entities;

import com.iscod.api_project_pmt.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="project_user")
public class ProjectUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @ManyToOne
    @JoinColumn(name="project_id")
    Project project;

    @ManyToOne
    @JoinColumn(name="user_id")
    User user;

    @Column(name="role")
    UserRole role;

    public ProjectUser(Project project, User user, UserRole role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }
}
