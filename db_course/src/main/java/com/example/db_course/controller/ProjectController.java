package com.example.db_course.controller;

import com.example.db_course.dto.request.ProjectCreateDto;
import com.example.db_course.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Void> createProject(
            @Valid @RequestBody ProjectCreateDto projectCreateDto
    ) {
        return projectService.createProjectWithOwner(projectCreateDto);
    }
}