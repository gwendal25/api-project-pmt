package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.TaskRequest;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.TaskHistoryEntry;
import com.iscod.api_project_pmt.mappers.TaskMapper;
import com.iscod.api_project_pmt.repositories.TaskHistoryEntryRepository;
import com.iscod.api_project_pmt.repositories.TaskRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Data
@Service
public class TaskHistoryEntryServiceImpl implements TaskHistoryEntryService {
    @Autowired
    public TaskHistoryEntryRepository taskHistoryEntryRepository;
    @Autowired
    public TaskRepository taskRepository;
    @Autowired
    public TaskMapper taskMapper;

    @Override
    public void AddTaskHistoryEntryToTask(TaskRequest taskRequest, Task task) {
        Task newTask = new Task();
        taskMapper.update(taskRequest, newTask);
        if(!newTask.equals(task)) {
            TaskHistoryEntry taskHistoryEntry = new TaskHistoryEntry(
                    task.getName(),
                    task.getDescription(),
                    task.getEndDate(),
                    task.getTaskPriority(),
                    task.getTaskStatus()
            );
            taskHistoryEntry.setEditDate(new Date());
            taskHistoryEntry.setTask(task);
            taskMapper.update(taskRequest, task);
            task.addTaskHistoryEntry(taskHistoryEntry);
            taskHistoryEntryRepository.save(taskHistoryEntry);
            taskRepository.save(task);
        }
    }
}
