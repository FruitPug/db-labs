package com.example.db_course.entity;

import java.time.LocalDateTime;

/**
 * Common contract for entities that support soft deletion.
 */
public interface SoftDeletable {
    boolean isDeleted();
    void setDeleted(boolean deleted);
    void setDeletedAt(LocalDateTime deletedAt);
    void setUpdatedAt(LocalDateTime updatedAt);
}
