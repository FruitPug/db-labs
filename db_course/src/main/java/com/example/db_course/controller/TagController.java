package com.example.db_course.controller;

import com.example.db_course.dto.request.TagCreateDto;
import com.example.db_course.dto.response.TagResponseDto;
import com.example.db_course.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<Page<TagResponseDto>> getTagsFiltered(
            @RequestParam(required = false) String color,
            Pageable pageable
    ) {
        return tagService.getTagsFiltered(color, pageable);
    }

    @PostMapping
    public ResponseEntity<Void> createTag(
            @Valid @RequestBody TagCreateDto dto
    ) {
        return tagService.createTag(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteTag(@PathVariable Long id) {
        return tagService.softDeleteTag(id);
    }
}
