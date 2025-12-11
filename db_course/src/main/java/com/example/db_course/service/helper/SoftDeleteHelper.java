package com.example.db_course.service.helper;

import com.example.db_course.entity.interfaces.SoftDeletable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class SoftDeleteHelper {

    public <T extends SoftDeletable> ResponseEntity<Void> softDelete(
            Long id,
            Function<Long, Optional<T>> findById,
            Consumer<T> saveEntity,
            Supplier<? extends RuntimeException> notFoundSupplier
    ) {
        T entity = findById.apply(id).orElseThrow(notFoundSupplier);

        if (entity.isDeleted()) {
            return ResponseEntity.ok().build();
        }

        entity.setDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        saveEntity.accept(entity);

        return ResponseEntity.ok().build();
    }
}
