package com.example.db_course.integration.scenarios;

import com.example.db_course.EntityCreator;
import com.example.db_course.dto.request.TaskTagCreateDto;
import com.example.db_course.entity.*;
import com.example.db_course.integration.IntegrationTestBase;
import com.example.db_course.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskTagIT extends IntegrationTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private TaskTagRepository taskTagRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private TagRepository tagRepository;

    @Autowired private EntityManager entityManager;

    @Test
    @Transactional
    void createTaskTag() throws Exception {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task);

        TagEntity tag = EntityCreator.getTagEntity();
        tagRepository.save(tag);

        TaskTagCreateDto dto = new TaskTagCreateDto();
        dto.setTaskId(task.getId());
        dto.setTagId(tag.getId());

        mockMvc.perform(post("/task-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        List<TaskTagEntity> taskTags = taskTagRepository.findAll();
        assertThat(taskTags).hasSize(1);
        TaskTagEntity taskTag = taskTags.get(0);
        assertThat(taskTag.getTask().getId()).isEqualTo(task.getId());
        assertThat(taskTag.getTag().getId()).isEqualTo(tag.getId());
    }

    @Test
    @Transactional
    void createTaskTag_whenTaskIsMissing() throws Exception {
        TagEntity tag = EntityCreator.getTagEntity();
        tagRepository.save(tag);

        TaskTagCreateDto dto = new TaskTagCreateDto();
        dto.setTaskId(999999L);
        dto.setTagId(tag.getId());

        mockMvc.perform(post("/task-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    void hardDeleteTaskTag_physicallyRemovesRow() throws Exception {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task);

        TagEntity tag = EntityCreator.getTagEntity();
        tagRepository.save(tag);

        TaskTagEntity taskTag = EntityCreator.getTaskTagEntity(tag, task);
        taskTagRepository.save(taskTag);

        Long id = taskTag.getId();

        assertThat(taskTagRepository.findRawById(id)).isPresent();

        mockMvc.perform(delete("/task-tags/{id}/hard", id))
                .andExpect(status().is2xxSuccessful());

        entityManager.flush();
        entityManager.clear();

        assertThat(taskTagRepository.findRawById(id)).isEmpty();
    }

    @Test
    @Transactional
    void hardDeleteProject_whenNotFound_throwsException() throws Exception {
        Long nonExistingId = 999999L;

        mockMvc.perform(delete("/task-tags/{id}/hard", nonExistingId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    void getTaskTagsFiltered_filtersByTag() throws Exception {
        UserEntity user = EntityCreator.getUserEntity();
        userRepository.save(user);

        ProjectEntity project = EntityCreator.getProjectEntity();
        projectRepository.save(project);

        TaskEntity task = EntityCreator.getTaskEntity(user, project);
        taskRepository.save(task);

        TagEntity tag1 = EntityCreator.getTagEntity();
        tag1.setName("tag1");
        tag1.setColor("red");
        tagRepository.save(tag1);

        TagEntity tag2 = EntityCreator.getTagEntity();
        tag2.setName("tag2");
        tag2.setColor("blue");
        tagRepository.save(tag2);

        TaskTagEntity taskTag1 = EntityCreator.getTaskTagEntity(tag1, task);
        taskTagRepository.save(taskTag1);

        TaskTagEntity taskTag2 = EntityCreator.getTaskTagEntity(tag2, task);
        taskTagRepository.save(taskTag2);

        mockMvc.perform(get("/task-tags")
                        .param("taskId", task.getId().toString())
                        .param("tagId", tag1.getId().toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].taskId").value(task.getId()))
                .andExpect(jsonPath("$.content[0].tagId").value(tag1.getId()));
    }
}
