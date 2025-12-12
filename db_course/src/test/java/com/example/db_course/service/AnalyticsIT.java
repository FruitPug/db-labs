package com.example.db_course.service;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.response.ProjectTaskStatusStatsDto;
import com.example.db_course.dto.response.UserDoneTasksStatsDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.*;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.TaskRepository;
import com.example.db_course.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyticsIT extends IntegrationTestBase {

    @Autowired private AnalyticsService analyticsService;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private EntityManager entityManager;

    @Test
    @Transactional
    void tasksByProjectAndStatus_returnsCounts_andExcludesDeleted() {
        ProjectEntity project1 = ProjectEntity.builder()
                .name("P1")
                .description("d")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        projectRepository.save(project1);

        ProjectEntity project2 = ProjectEntity.builder()
                .name("P2")
                .description("d")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(true)
                .deletedAt(LocalDateTime.now())
                .build();
        projectRepository.save(project2);

        UserEntity creator = UserEntity.builder()
                .email("c@test.com")
                .fullName("Creator")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(creator);

        taskRepository.save(TaskEntity.builder()
                .project(project1)
                .creator(creator)
                .title("t1")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        taskRepository.save(TaskEntity.builder()
                .project(project1)
                .creator(creator)
                .title("t2")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(true) // excluded
                .deletedAt(LocalDateTime.now())
                .build());

        taskRepository.save(TaskEntity.builder()
                .project(project1)
                .creator(creator)
                .title("t3")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        taskRepository.save(TaskEntity.builder()
                .project(project2)
                .creator(creator)
                .title("t4")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        entityManager.flush();
        entityManager.clear();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<ProjectTaskStatusStatsDto> stats = analyticsService.getTasksByProjectAndStatus(pageable).getBody();
        assertThat(stats).isNotNull();

        assertThat(stats).anySatisfy(r -> {
            if (r.getProjectId().equals(project1.getId()) && r.getStatus() == TaskStatus.TODO) {
                assertThat(r.getTaskCount()).isEqualTo(1L);
            }
        });

        assertThat(stats).anySatisfy(r -> {
            if (r.getProjectId().equals(project1.getId()) && r.getStatus() == TaskStatus.DONE) {
                assertThat(r.getTaskCount()).isEqualTo(1L);
            }
        });

        assertThat(stats).noneMatch(r -> r.getProjectId().equals(project2.getId()));
    }

    @Test
    @Transactional
    void topUsersByDoneTasks_returnsRanking() {
        ProjectEntity project = ProjectEntity.builder()
                .name("P")
                .description("d")
                .status(ProjectStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        projectRepository.save(project);

        UserEntity creator = UserEntity.builder()
                .email("creator@test.com")
                .fullName("Creator")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(creator);

        UserEntity u1 = UserEntity.builder()
                .email("u1@test.com")
                .fullName("U1")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(u1);

        UserEntity u2 = UserEntity.builder()
                .email("u2@test.com")
                .fullName("U2")
                .role(UserRole.DEVELOPER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        userRepository.save(u2);

        taskRepository.save(TaskEntity.builder()
                .project(project)
                .creator(creator)
                .assignee(u1)
                .title("d1")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        taskRepository.save(TaskEntity.builder()
                .project(project)
                .creator(creator)
                .assignee(u1)
                .title("d2")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        taskRepository.save(TaskEntity.builder()
                .project(project)
                .creator(creator)
                .assignee(u2)
                .title("d3")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build());

        entityManager.flush();
        entityManager.clear();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<UserDoneTasksStatsDto> page = analyticsService.getTopAssigneesByDoneTasks(pageable).getBody();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(2);

        UserDoneTasksStatsDto dto1 = page.getContent().get(0);
        assertThat(dto1.getUserId()).isEqualTo(u1.getId());
        assertThat(dto1.getDoneCount()).isEqualTo(2L);
        assertThat(dto1.getRank()).isEqualTo(1);

        UserDoneTasksStatsDto dto2 = page.getContent().get(1);
        assertThat(dto2.getUserId()).isEqualTo(u2.getId());
        assertThat(dto2.getDoneCount()).isEqualTo(1L);
        assertThat(dto2.getRank()).isEqualTo(2);
    }
}
