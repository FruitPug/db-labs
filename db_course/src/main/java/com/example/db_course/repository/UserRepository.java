package com.example.db_course.repository;

import com.example.db_course.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(value = "select * from users where id = :id", nativeQuery = true)
    Optional<UserEntity> findRawById(Long id);
}
