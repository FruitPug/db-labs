package com.example.db_course.dto.responce;

import com.example.db_course.entity.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private String fullName;
    private UserRole role;
}
