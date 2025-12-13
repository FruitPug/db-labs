package com.example.db_course.service;

import com.example.db_course.EntityCreator;
import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.TagCreateDto;
import com.example.db_course.dto.response.TagResponseDto;
import com.example.db_course.entity.*;
import com.example.db_course.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class TagServiceIT extends IntegrationTestBase {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void createTag() {
        TagCreateDto dto = new TagCreateDto();
        dto.setName("test_tag");
        dto.setColor("red");

        tagService.createTag(dto);
        List<TagEntity> tags = tagRepository.findAll();
        assertThat(tags).hasSize(1);
        TagEntity tag = tags.get(0);
        assertThat(tag.getName()).isEqualTo(dto.getName());
    }

    @Test
    @Transactional
    void softDeleteTag_marksDeletedAndFiltersFromFindById() {
        TagEntity tag = EntityCreator.getTagEntity();
        tagRepository.save(tag);

        Long id = tag.getId();

        assertThat(tagRepository.findById(id)).isPresent();

        ResponseEntity<Void> response = tagService.softDeleteTag(id);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        entityManager.flush();
        entityManager.clear();

        assertThat(tagRepository.findById(id)).isEmpty();

        Optional<TagEntity> raw = tagRepository.findRawById(id);
        assertThat(raw).isPresent();
        assertThat(raw.get().isDeleted()).isTrue();
        assertThat(raw.get().getDeletedAt()).isNotNull();
    }

    @Test
    @Transactional
    void getTagsFiltered_filtersByColorAndExcludesSoftDeleted() {
        TagEntity tag1 = TagEntity.builder()
                .name("Test tag 1")
                .color("red")
                .createdAt(LocalDateTime.now())
                .build();
        tagRepository.save(tag1);

        TagEntity tag2 = TagEntity.builder()
                .name("Test tag 2")
                .color("red")
                .createdAt(LocalDateTime.now())
                .deleted(true)
                .deletedAt(LocalDateTime.now())
                .build();
        tagRepository.save(tag2);

        TagEntity tag3 = TagEntity.builder()
                .name("Test tag 3")
                .color("green")
                .createdAt(LocalDateTime.now())
                .build();
        tagRepository.save(tag3);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<TagResponseDto> page = tagService.getTagsFiltered("red", pageable).getBody();

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1);
        TagResponseDto dto = page.getContent().get(0);
        assertThat(dto.getName()).isEqualTo(tag1.getName());
        assertThat(dto.getColor()).isEqualTo(tag1.getColor());
    }
}
