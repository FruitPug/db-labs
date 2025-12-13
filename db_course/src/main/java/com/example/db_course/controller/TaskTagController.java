package com.example.db_course.controller;

import com.example.db_course.dto.request.TaskTagCreateDto;
import com.example.db_course.dto.response.TaskTagResponseDto;
import com.example.db_course.service.TaskTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task-tags")
@RequiredArgsConstructor
public class TaskTagController {

    private final TaskTagService taskTagService;

    @GetMapping
    public ResponseEntity<Page<TaskTagResponseDto>> getTaskTagsFiltered(
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long tagId,
            Pageable pageable
    ) {
        return taskTagService.getTaskTagsFiltered(taskId, tagId, pageable);
    }

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
