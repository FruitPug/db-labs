package com.example.db_course.controller;

import com.example.db_course.dto.request.TaskCreateDto;
import com.example.db_course.dto.request.TaskStatusUpdateDto;
import com.example.db_course.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteTask(@PathVariable Long id) {
        return taskService.softDeleteTask(id);
    }
}
