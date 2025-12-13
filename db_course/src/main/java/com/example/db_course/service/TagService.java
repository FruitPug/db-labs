package com.example.db_course.service;

import com.example.db_course.dto.request.TagCreateDto;
import com.example.db_course.dto.response.TagResponseDto;
import com.example.db_course.entity.TagEntity;
import com.example.db_course.mapper.TagMapper;
import com.example.db_course.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public ResponseEntity<Void> createTag(TagCreateDto dto) {
        TagEntity tag = TagMapper.fromCreateDto(dto);
        tagRepository.save(tag);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> softDeleteTag(Long id) {
        TagEntity tag = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        if (tag.isDeleted()) {
            return ResponseEntity.ok().build();
        }

        tag.setDeleted(true);
        tag.setDeletedAt(LocalDateTime.now());

        tagRepository.save(tag);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Page<TagResponseDto>> getTagsFiltered(String color, Pageable pageable) {
        Page<TagEntity> page = tagRepository.searchTagsFiltered(color, pageable);

        Page<TagResponseDto> dtoPage = page.map(TagMapper::toResponseDto);

        return ResponseEntity.ok(dtoPage);
    }
}
