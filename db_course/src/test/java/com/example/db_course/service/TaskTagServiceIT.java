package com.example.db_course.service;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.TaskTagCreateDto;
import com.example.db_course.dto.response.TaskTagResponseDto;
import com.example.db_course.entity.*;
import com.example.db_course.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TaskTagServiceIT extends IntegrationTestBase {

    @Autowired
    private TaskTagService taskTagService;

    @Autowired
    private TaskTagRepository taskTagRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TagRepository tagRepository;

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

        TagEntity tag = EntityCreator.getTagEntity();
        tagRepository.save(tag);

        TaskTagCreateDto dto = new TaskTagCreateDto();
        dto.setTaskId(task.getId());
        dto.setTagId(tag.getId());

        taskTagService.createTaskTag(dto);

        List<TaskTagEntity> taskTags = taskTagRepository.findAll();
        assertThat(taskTags).hasSize(1);
        TaskTagEntity taskTag = taskTags.get(0);
        assertThat(taskTag.getTask().getId()).isEqualTo(task.getId());
        assertThat(taskTag.getTag().getId()).isEqualTo(tag.getId());
    }

    @Test
    @Transactional
    void createTaskTag_whenTaskIsMissing() {
        TagEntity tag = EntityCreator.getTagEntity();
        tagRepository.save(tag);

        TaskTagCreateDto dto = new TaskTagCreateDto();
        dto.setTaskId(999999L);
        dto.setTagId(tag.getId());

        assertThatThrownBy(() -> taskTagService.createTaskTag(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task not found");
    }

    @Test
    @Transactional
    void hardDeleteTaskTag_physicallyRemovesRow() {
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

        ResponseEntity<Void> response = taskTagService.hardDeleteProject(id);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        entityManager.flush();
        entityManager.clear();

        assertThat(taskTagRepository.findRawById(id)).isEmpty();
    }

    @Test
    @Transactional
    void hardDeleteProject_whenNotFound_throwsException() {
        Long nonExistingId = 999999L;

        assertThatThrownBy(() -> taskTagService.hardDeleteProject(nonExistingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task tag not found");
    }

    @Test
    @Transactional
    void getTaskTagsFiltered_filtersByTag() {
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

        PageRequest pageable = PageRequest.of(0, 10);
        Page<TaskTagResponseDto> page = taskTagService.getTaskTagsFiltered(
                task.getId(),
                tag1.getId(),
                pageable)
                .getBody();

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1);
        TaskTagResponseDto dto = page.getContent().get(0);
        assertThat(dto.getTaskId()).isEqualTo(task.getId());
        assertThat(dto.getTagId()).isEqualTo(tag1.getId());
    }
}
