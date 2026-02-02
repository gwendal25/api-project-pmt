package com.iscod.api_project_pmt.repositories;

import com.iscod.api_project_pmt.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
