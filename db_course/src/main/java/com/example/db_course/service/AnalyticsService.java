package com.example.db_course.service;

import com.example.db_course.dto.response.ProjectTaskStatusStatsDto;
import com.example.db_course.dto.response.UserDoneTasksStatsDto;
import com.example.db_course.repository.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public ResponseEntity<Page<ProjectTaskStatusStatsDto>> getTasksByProjectAndStatus(Pageable pageable) {
        return ResponseEntity.ok(analyticsRepository.tasksByProjectAndStatus(pageable));
    }

    public ResponseEntity<Page<UserDoneTasksStatsDto>> getTopAssigneesByDoneTasks(Pageable pageable) {
        return ResponseEntity.ok(analyticsRepository.topAssigneesByDoneTasks(pageable));
    }
}
