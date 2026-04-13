package org.example.springlab.lab9javaspring.repository;

import org.example.springlab.lab9javaspring.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
