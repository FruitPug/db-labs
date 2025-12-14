package com.example.db_course.integration.scenarios;

import com.example.db_course.EntityCreator;
import com.example.db_course.dto.request.TaskCommentCreateDto;
import com.example.db_course.entity.ProjectEntity;
import com.example.db_course.entity.TaskCommentEntity;
import com.example.db_course.entity.TaskEntity;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.integration.IntegrationTestBase;
import com.example.db_course.repository.ProjectRepository;
import com.example.db_course.repository.TaskCommentRepository;
import com.example.db_course.repository.TaskRepository;
import com.example.db_course.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskCommentIT extends IntegrationTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private TaskCommentRepository taskCommentRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;

    @Autowired private EntityManager entityManager;

    @Test
    @Transactional
    void createTaskComment() throws Exception {
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

        mockMvc.perform(post("/task-comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        List<TaskCommentEntity> taskComments = taskCommentRepository.findAll();
        assertThat(taskComments).hasSize(1);
        TaskCommentEntity taskComment = taskComments.get(0);
        assertThat(taskComment.getTask().getId()).isEqualTo(task.getId());
        assertThat(taskComment.getAuthor().getId()).isEqualTo(user.getId());
        assertThat(taskComment.getBody()).isEqualTo("Test comment");
    }

    @Test
    @Transactional
    void createTaskComment_whenTaskMissing() throws Exception {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        TaskCommentCreateDto dto = new TaskCommentCreateDto();
        dto.setTaskId(999999L);
        dto.setAuthorUserId(user.getId());
        dto.setBody("Test comment");

        mockMvc.perform(post("/task-comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    void softDeleteTaskComment_marksDeletedAndFiltersFromFindById() throws Exception {
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

        mockMvc.perform(delete("/task-comments/{id}", id))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        assertThat(taskCommentRepository.findById(id)).isEmpty();

        TaskCommentEntity raw = taskCommentRepository.findRawById(id).orElseThrow();
        assertThat(raw.isDeleted()).isTrue();
        assertThat(raw.getDeletedAt()).isNotNull();
    }

    @Test
    @Transactional
    void getTaskCommentsFiltered_filtersByTaskAndExcludesSoftDeleted() throws Exception {
        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        TaskEntity task1 = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task1);

        TaskEntity task2 = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task2);

        TaskCommentEntity taskComment1 = EntityCreator.getTaskCommentEntity(user, task1);
        taskCommentRepository.save(taskComment1);

        TaskCommentEntity taskComment2 = EntityCreator.getTaskCommentEntity(user, task1);
        taskComment2.setDeleted(true);
        taskComment2.setDeletedAt(LocalDateTime.now());
        taskCommentRepository.save(taskComment2);

        TaskCommentEntity taskComment3 = EntityCreator.getTaskCommentEntity(user, task2);
        taskCommentRepository.save(taskComment3);

        mockMvc.perform(get("/task-comments")
                        .param("taskId", task1.getId().toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].taskId").value(task1.getId()))
                .andExpect(jsonPath("$.content[0].authorId").value(user.getId()))
                .andExpect(jsonPath("$.content[0].body").value(taskComment1.getBody()));
    }
}
