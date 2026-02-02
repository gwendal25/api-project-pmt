package com.iscod.api_project_pmt.repositories;

import com.iscod.api_project_pmt.entities.TaskHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskHistoryEntryRepository extends JpaRepository<TaskHistoryEntry, Long> {
}
