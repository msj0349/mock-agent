package com.project.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversion_history")
public class ConversionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_sql", columnDefinition = "TEXT")
    private String originalSQL;

    @Column(name = "converted_sql", columnDefinition = "TEXT")
    private String convertedSQL;

    @Column(name = "statement_type")
    private String statementType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "warnings", columnDefinition = "TEXT")
    private String warnings;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalSQL() {
        return originalSQL;
    }

    public void setOriginalSQL(String originalSQL) {
        this.originalSQL = originalSQL;
    }

    public String getConvertedSQL() {
        return convertedSQL;
    }

    public void setConvertedSQL(String convertedSQL) {
        this.convertedSQL = convertedSQL;
    }

    public String getStatementType() {
        return statementType;
    }

    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }
}
