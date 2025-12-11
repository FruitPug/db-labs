package com.example.db_course.controller;

import com.example.db_course.dto.request.TaskTagCreateDto;
import com.example.db_course.service.TaskTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task-tags")
@RequiredArgsConstructor
public class TaskTagController {

    private final TaskTagService taskTagService;

    @PostMapping
    public ResponseEntity<Void> createTaskTag(
            @Valid @RequestBody TaskTagCreateDto dto
    ) {
        return taskTagService.createTaskTag(dto);
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteProject(@PathVariable Long id) {
        return taskTagService.hardDeleteProject(id);
    }
}
