package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.TaskHistoryEntry;

public interface TaskHistoryEntryService {
    TaskHistoryEntry save(Task task);
}
