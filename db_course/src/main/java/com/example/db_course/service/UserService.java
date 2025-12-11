package com.example.db_course.service;

import com.example.db_course.dto.request.UserCreateDto;
import com.example.db_course.dto.responce.UserResponseDto;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.mapper.UserMapper;
import com.example.db_course.repository.UserRepository;
import com.example.db_course.service.helper.SoftDeleteHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SoftDeleteHelper softDeleteHelper;

    @Transactional
    public ResponseEntity<Void> createUser(UserCreateDto dto) {
        UserEntity user = UserMapper.fromCreateDto(dto);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> softDeleteUser(Long id) {
        return softDeleteHelper.softDelete(
                id,
                userRepository::findById,
                userRepository::save,
                () -> new IllegalArgumentException("User not found")
        );
    }

    @Transactional
    public ResponseEntity<UserResponseDto> getUserById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(UserMapper.toResponseDto(user));
    }
}
