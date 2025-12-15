package com.iscod.api_project_pmt.repositories;

import com.iscod.api_project_pmt.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);
}
