package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.task.TaskRequest;
import com.iscod.api_project_pmt.entities.Task;

public interface TaskHistoryEntryService {
    void AddTaskHistoryEntryToTask(TaskRequest taskRequest, Task task);
}
