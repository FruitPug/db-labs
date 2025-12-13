package com.example.db_course.service;

import com.example.db_course.dto.request.UserCreateDto;
import com.example.db_course.dto.response.UserResponseDto;
import com.example.db_course.entity.UserEntity;
import com.example.db_course.entity.enums.UserRole;
import com.example.db_course.mapper.UserMapper;
import com.example.db_course.repository.UserRepository;
import com.example.db_course.service.helper.SoftDeleteHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<UserResponseDto>> getUsersFiltered(UserRole role, Pageable pageable) {
        Page<UserEntity> page = userRepository.searchUsersFiltered(role, pageable);

        Page<UserResponseDto> dtoPage = page.map(UserMapper::toResponseDto);

        return ResponseEntity.ok(dtoPage);
    }
}
