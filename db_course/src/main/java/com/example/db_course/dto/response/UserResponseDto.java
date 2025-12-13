package com.example.db_course.dto.response;

import com.example.db_course.entity.enums.UserRole;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponseDto {
    String fullName;
    UserRole role;
}
