package com.example.db_course.controller;

import com.example.db_course.dto.response.ProjectTaskStatusStatsDto;
import com.example.db_course.dto.response.UserDoneTasksStatsDto;
import com.example.db_course.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/projects/tasks-by-status")
    public ResponseEntity<Page<ProjectTaskStatusStatsDto>> tasksByProjectAndStatus(Pageable pageable) {
        return analyticsService.getTasksByProjectAndStatus(pageable);
    }

    @GetMapping("/users/top-done")
    public ResponseEntity<Page<UserDoneTasksStatsDto>> topUsersByDoneTasks(
            Pageable pageable
    ) {
        return analyticsService.getTopAssigneesByDoneTasks(pageable);
    }
}
