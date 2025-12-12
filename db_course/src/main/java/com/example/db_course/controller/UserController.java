package com.example.db_course.controller;

import com.example.db_course.dto.request.UserCreateDto;
import com.example.db_course.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> createUser(
            @Valid @RequestBody UserCreateDto dto
    ) {
        return userService.createUser(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable Long id) {
        return userService.softDeleteUser(id);
    }
}
