package com.example.db_course.controller;

import com.example.db_course.dto.request.TaskCreateDto;
import com.example.db_course.dto.request.TaskReassignDto;
import com.example.db_course.dto.request.TaskStatusUpdateDto;
import com.example.db_course.dto.response.TaskResponseDto;
import com.example.db_course.entity.enums.TaskPriority;
import com.example.db_course.entity.enums.TaskStatus;
import com.example.db_course.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<Page<TaskResponseDto>> getTasksFiltered(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long assigneeId,
            Pageable pageable
    ) {
        return taskService.getTasksFiltered(status, priority, projectId, assigneeId, pageable);
    }

    @PostMapping
    public ResponseEntity<Void> createTask(
            @Valid @RequestBody TaskCreateDto dto
    ) {
        return taskService.createTask(dto);
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> updateTaskStatus(
            @Valid @RequestBody TaskStatusUpdateDto dto
    ) {
        return taskService.updateTaskStatus(dto);
    }

    @PatchMapping("/assignee")
    public ResponseEntity<Void> reassignTask(
            @Valid @RequestBody TaskReassignDto dto
    ) {
        return taskService.reassignTask(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteTask(@PathVariable Long id) {
        return taskService.softDeleteTask(id);
    }
}
