package com.example.db_course.controller;

import com.example.db_course.dto.request.ProjectMemberCreateDto;
import com.example.db_course.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project-members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @PostMapping
    public ResponseEntity<Void> createProjectMember(
            @Valid @RequestBody ProjectMemberCreateDto dto
    ) {
        return projectMemberService.createProjectMember(dto);
    }
}
