package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.TaskDto;
import com.iscod.api_project_pmt.dtos.TaskRequest;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.mappers.TaskMapper;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import com.iscod.api_project_pmt.repositories.TaskRepository;
import com.iscod.api_project_pmt.services.TaskHistoryEntryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/tasks")
public class TaskController {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskHistoryEntryService taskHistoryEntryService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskMapper.toDtoWithHistory(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            return ResponseEntity.notFound().build();
        }

        taskHistoryEntryService.AddTaskHistoryEntryToTask(taskRequest, task);
        return ResponseEntity.ok(taskMapper.toDtoWithHistory(task));
    }
}
