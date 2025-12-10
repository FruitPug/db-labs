package com.example.db_course.repository;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.entity.TagEntity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TagRepositoryIT extends IntegrationTestBase {

    @Autowired
    private TagRepository tagRepository;

    @Test
    @Transactional
    public void findById_returnsSavedTag(){
        TagEntity tag = TagEntity.builder()
                .name("test_tag")
                .color("red")
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        tagRepository.save(tag);

        Optional<TagEntity> found = tagRepository.findById(tag.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(tag.getId());
    }
}
