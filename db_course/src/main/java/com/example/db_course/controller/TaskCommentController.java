package com.example.db_course.controller;

import com.example.db_course.dto.request.TaskCommentCreateDto;
import com.example.db_course.service.TaskCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task-comments")
@RequiredArgsConstructor
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    @PostMapping
    public ResponseEntity<Void> createComment(
            @Valid @RequestBody TaskCommentCreateDto dto
    ) {
        return taskCommentService.createComment(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteComment(@PathVariable Long id) {
        return taskCommentService.softDeleteComment(id);
    }
}
