package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.task.TaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskHistoryEntryDto;
import com.iscod.api_project_pmt.dtos.task.TaskRequest;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.TaskHistoryEntry;
import com.iscod.api_project_pmt.enums.TaskPriority;
import com.iscod.api_project_pmt.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class TaskMapperTest {

    @Mock
    private TaskHistoryEntryMapper taskHistoryEntryMapper;

    private TaskMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TaskMapperImpl();
        mapper.taskHistoryEntryMapper = taskHistoryEntryMapper;
    }

    @Test
    void testToDto() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTaskPriority(TaskPriority.HIGH);
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        task.setEndDate(new Date());

        // Act
        TaskDto result = mapper.toDto(task);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Task");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getTaskPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(result.getTaskStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(result.getEndDate()).isEqualTo(task.getEndDate());
        // taskHistoryEntries should be mapped
    }

    @Test
    void testToPartialDto() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTaskPriority(TaskPriority.HIGH);
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        task.setEndDate(new Date());

        // Act
        TaskDto result = mapper.toPartialDto(task);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Task");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getTaskPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(result.getTaskStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(result.getEndDate()).isEqualTo(task.getEndDate());
        // taskHistoryEntries should be null or empty as per mapping ignore
    }

    @Test
    void testToTask() {
        // Arrange
        TaskRequest request = new TaskRequest();
        request.setName("New Task");
        request.setDescription("New Description");
        request.setTaskPriority(TaskPriority.MEDIUM);
        request.setTaskStatus(TaskStatus.NOT_STARTED);
        request.setEndDate(new Date());

        // Act
        Task result = mapper.toTask(request);

        // Assert
        assertThat(result.getName()).isEqualTo("New Task");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getTaskPriority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(result.getTaskStatus()).isEqualTo(TaskStatus.NOT_STARTED);
        assertThat(result.getEndDate()).isEqualTo(request.getEndDate());
    }

    @Test
    void testUpdate() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setName("Old Name");
        task.setDescription("Old Description");
        task.setTaskPriority(TaskPriority.LOW);
        task.setTaskStatus(TaskStatus.FINISHED);

        TaskRequest request = new TaskRequest();
        request.setName("Updated Name");
        request.setDescription("Updated Description");
        request.setTaskPriority(TaskPriority.HIGH);
        request.setTaskStatus(TaskStatus.IN_PROGRESS);
        request.setEndDate(new Date());

        // Act
        mapper.update(request, task);

        // Assert
        assertThat(task.getId()).isEqualTo(1L); // id should not change
        assertThat(task.getName()).isEqualTo("Updated Name");
        assertThat(task.getDescription()).isEqualTo("Updated Description");
        assertThat(task.getTaskPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(task.getTaskStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(task.getEndDate()).isEqualTo(request.getEndDate());
    }

    @Test
    void testToDtoWithHistory() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTaskPriority(TaskPriority.HIGH);
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        task.setEndDate(new Date());

        TaskHistoryEntry entry1 = new TaskHistoryEntry();
        entry1.setId(2L);

        TaskHistoryEntry entry2 = new TaskHistoryEntry();
        entry2.setId(3L);

        Set<TaskHistoryEntry> historySet = new LinkedHashSet<>();
        historySet.add(entry1);
        historySet.add(entry2);
        task.setTaskHistoryEntries(historySet);

        TaskHistoryEntryDto dto1 = new TaskHistoryEntryDto();
        dto1.setId(2L);
        dto1.setEditDate(new Date(System.currentTimeMillis() - 1000)); // older

        TaskHistoryEntryDto dto2 = new TaskHistoryEntryDto();
        dto2.setId(3L);
        dto2.setEditDate(new Date()); // newer

        when(taskHistoryEntryMapper.toDto(any(TaskHistoryEntry.class))).thenReturn(dto1, dto2);

        // Act
        TaskDto result = mapper.toDtoWithHistory(task);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Task");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getTaskPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(result.getTaskStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(result.getEndDate()).isEqualTo(task.getEndDate());
        assertThat(result.getTaskHistoryEntries()).hasSize(2);
        // Sorted by editDate descending (newer first)
        assertThat(result.getTaskHistoryEntries().get(0).getId()).isEqualTo(3L);
        assertThat(result.getTaskHistoryEntries().get(1).getId()).isEqualTo(2L);
    }
}