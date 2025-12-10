package com.example.db_course.service;

import com.example.db_course.IntegrationTestBase;
import com.example.db_course.dto.request.TagCreateDto;
import com.example.db_course.entity.TagEntity;
import com.example.db_course.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TagServiceIT extends IntegrationTestBase {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

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
}
