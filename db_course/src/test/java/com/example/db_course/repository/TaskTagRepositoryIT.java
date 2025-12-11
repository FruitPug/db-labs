package com.example.db_course.repository;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.entity.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTagRepositoryIT extends IntegrationTestBase {

    @Autowired
    private TaskTagRepository taskTagRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @Transactional
    void findByAuthorAndTask_returnsSavedComment() {
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

        Optional<TaskTagEntity> found = taskTagRepository.findByTaskAndTag(task, tag);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(taskTag.getId());
    }
}
