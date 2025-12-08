package com.example.db_course.service;

import com.example.db_course.dto.request.TagCreateDto;
import com.example.db_course.entity.TagEntity;
import com.example.db_course.mapper.TagMapper;
import com.example.db_course.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
}
