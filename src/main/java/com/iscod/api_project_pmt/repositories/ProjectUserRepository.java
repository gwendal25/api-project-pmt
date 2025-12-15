package com.iscod.api_project_pmt.repositories;

import com.iscod.api_project_pmt.entities.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
}
