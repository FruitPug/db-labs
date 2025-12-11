package com.example.db_course.service;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.TaskCommentCreateDto;
import com.example.db_course.entity.*;
import com.example.db_course.entity.enums.*;
import com.example.db_course.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class TaskCommentServiceIT extends IntegrationTestBase {

    @Autowired
    private TaskCommentService taskCommentService;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void createTaskComment() {
        UserEntity user = EntityCreator.getUserEntity();

        userRepository.save(user);

        ProjectEntity project = EntityCreator.getProjectEntity();

        projectRepository.save(project);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);

        taskRepository.save(task);

        TaskCommentCreateDto dto = new TaskCommentCreateDto();
        dto.setTaskId(task.getId());
        dto.setAuthorUserId(user.getId());
        dto.setBody("Test comment");

        taskCommentService.createComment(dto);

        List<TaskCommentEntity> taskComments = taskCommentRepository.findAll();
        assertThat(taskComments).hasSize(1);
        TaskCommentEntity taskComment = taskComments.get(0);
        assertThat(taskComment.getTask().getId()).isEqualTo(task.getId());
        assertThat(taskComment.getAuthor().getId()).isEqualTo(user.getId());
        assertThat(taskComment.getBody()).isEqualTo("Test comment");
    }

    @Test
    @Transactional
    void createTaskComment_whenTaskMissing() {
        UserEntity user = EntityCreator.getUserEntity();

        TaskCommentCreateDto dto = new TaskCommentCreateDto();
        dto.setTaskId(999999L);
        dto.setAuthorUserId(user.getId());
        dto.setBody("Test comment");

        assertThatThrownBy(() -> taskCommentService.createComment(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    @Transactional
    void softDeleteTaskComment_marksDeletedAndFiltersFromFindById() {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task);

        TaskCommentEntity taskComment = EntityCreator.getTaskCommentEntity(user, task);
        taskCommentRepository.save(taskComment);

        Long id = taskComment.getId();

        assertThat(taskCommentRepository.findById(id)).isPresent();

        ResponseEntity<Void> response = taskCommentService.softDeleteComment(id);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        entityManager.flush();
        entityManager.clear();

        assertThat(taskCommentRepository.findById(id)).isEmpty();

        Optional<TaskCommentEntity> raw = taskCommentRepository.findRawById(id);
        assertThat(raw).isPresent();
        assertThat(raw.get().isDeleted()).isTrue();
        assertThat(raw.get().getDeletedAt()).isNotNull();
    }
}
