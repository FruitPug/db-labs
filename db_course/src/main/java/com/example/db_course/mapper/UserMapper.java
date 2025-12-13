package com.example.db_course.mapper;

import com.example.db_course.dto.request.UserCreateDto;
import com.example.db_course.dto.response.UserResponseDto;
import com.example.db_course.entity.UserEntity;

import java.time.LocalDateTime;

public class UserMapper {

    public static UserEntity fromCreateDto(UserCreateDto dto) {
        return UserEntity.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .role(dto.getRole())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    public static UserResponseDto toResponseDto(UserEntity user) {
        return UserResponseDto.builder()
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
