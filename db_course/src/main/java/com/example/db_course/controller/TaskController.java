package com.example.db_course.controller;

import com.example.db_course.dto.request.TaskCreateDto;
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
}
