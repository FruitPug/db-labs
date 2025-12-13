package com.example.db_course.controller;

import com.example.db_course.dto.request.TaskCommentCreateDto;
import com.example.db_course.dto.response.TaskCommentResponseDto;
import com.example.db_course.service.TaskCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task-comments")
@RequiredArgsConstructor
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    @GetMapping
    public ResponseEntity<Page<TaskCommentResponseDto>> getCommentsFiltered(
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long userId,
            Pageable pageable
    ) {
        return taskCommentService.getCommentsFiltered(taskId, userId, pageable);
    }

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
