package com.example.db_course.repository;

import com.example.db_course.entity.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    @Query(value = "select * from tags where id = :id", nativeQuery = true)
    Optional<TagEntity> findRawById(Long id);

    @Query("""
        select t from TagEntity t
        where (t.color = :color)
    """)
    Page<TagEntity> searchTagsFiltered(String color, Pageable pageable);
}
