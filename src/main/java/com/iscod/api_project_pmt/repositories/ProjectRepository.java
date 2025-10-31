package com.iscod.api_project_pmt.repositories;

import com.iscod.api_project_pmt.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
