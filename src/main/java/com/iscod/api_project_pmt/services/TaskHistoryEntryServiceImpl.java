package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.TaskHistoryEntry;
import com.iscod.api_project_pmt.mappers.TaskHistoryEntryMapper;
import com.iscod.api_project_pmt.repositories.TaskHistoryEntryRepository;
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
    public TaskHistoryEntryMapper taskHistoryEntryMapper;

    @Override
    public TaskHistoryEntry save(Task task) {
        TaskHistoryEntry taskHistoryEntry = taskHistoryEntryMapper.toTaskHistoryEntry(task);
        taskHistoryEntry.setEditDate(new Date());
        taskHistoryEntry.setTask(task);
        return taskHistoryEntryRepository.save(taskHistoryEntry);
    }
}
