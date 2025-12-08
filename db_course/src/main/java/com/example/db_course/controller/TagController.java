package com.example.db_course.controller;

import com.example.db_course.dto.request.TagCreateDto;
import com.example.db_course.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<Void> createTag(
            @Valid @RequestBody TagCreateDto dto
    ) {
        return tagService.createTag(dto);
    }
}
