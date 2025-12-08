package com.example.db_course.service;

import com.example.db_course.dto.request.UserCreateDto;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.mapper.UserMapper;
import com.example.db_course.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<Void> createUser(UserCreateDto dto) {
        UserEntity user = UserMapper.fromCreateDto(dto);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
