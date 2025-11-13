package com.project.api.repository;

import com.project.api.entity.ConversionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConversionHistoryRepository extends JpaRepository<ConversionHistory, Long> {
    
    List<ConversionHistory> findByStatementType(String statementType);
    
    List<ConversionHistory> findByCreatedAtAfter(LocalDateTime date);
    
    long countByStatementType(String statementType);
}
