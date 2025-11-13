package com.project.api.repository;

import com.project.api.entity.ConversionHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ConversionHistory Repository Integration Tests")
class ConversionHistoryRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ConversionHistoryRepository repository;

    @Test
    @DisplayName("Should save and retrieve conversion history")
    void testSaveAndRetrieve() {
        ConversionHistory history = createTestHistory();
        
        ConversionHistory saved = repository.save(history);
        entityManager.flush();
        
        Optional<ConversionHistory> found = repository.findById(saved.getId());
        
        assertThat(found).isPresent();
        assertThat(found.get().getOriginalSQL()).isEqualTo("SELECT SYSDATE FROM DUAL");
        assertThat(found.get().getConvertedSQL()).isEqualTo("SELECT NOW() FROM DUAL");
    }

    @Test
    @DisplayName("Should find by statement type")
    void testFindByStatementType() {
        ConversionHistory history1 = createTestHistory();
        history1.setStatementType("PROCEDURE");
        
        ConversionHistory history2 = createTestHistory();
        history2.setStatementType("FUNCTION");
        
        repository.save(history1);
        repository.save(history2);
        entityManager.flush();
        
        List<ConversionHistory> procedures = repository.findByStatementType("PROCEDURE");
        
        assertThat(procedures).hasSize(1);
        assertThat(procedures.get(0).getStatementType()).isEqualTo("PROCEDURE");
    }

    @Test
    @DisplayName("Should find by created date")
    void testFindByCreatedAtAfter() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        
        ConversionHistory history = createTestHistory();
        repository.save(history);
        entityManager.flush();
        
        List<ConversionHistory> recent = repository.findByCreatedAtAfter(yesterday);
        List<ConversionHistory> future = repository.findByCreatedAtAfter(tomorrow);
        
        assertThat(recent).isNotEmpty();
        assertThat(future).isEmpty();
    }

    @Test
    @DisplayName("Should count by statement type")
    void testCountByStatementType() {
        ConversionHistory history1 = createTestHistory();
        history1.setStatementType("PROCEDURE");
        
        ConversionHistory history2 = createTestHistory();
        history2.setStatementType("PROCEDURE");
        
        ConversionHistory history3 = createTestHistory();
        history3.setStatementType("FUNCTION");
        
        repository.save(history1);
        repository.save(history2);
        repository.save(history3);
        entityManager.flush();
        
        long procedureCount = repository.countByStatementType("PROCEDURE");
        long functionCount = repository.countByStatementType("FUNCTION");
        
        assertThat(procedureCount).isEqualTo(2);
        assertThat(functionCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should persist warnings")
    void testPersistWarnings() {
        ConversionHistory history = createTestHistory();
        history.setWarnings("Warning 1; Warning 2; Warning 3");
        
        ConversionHistory saved = repository.save(history);
        entityManager.flush();
        entityManager.clear();
        
        Optional<ConversionHistory> found = repository.findById(saved.getId());
        
        assertThat(found).isPresent();
        assertThat(found.get().getWarnings()).contains("Warning 1");
        assertThat(found.get().getWarnings()).contains("Warning 2");
    }

    @Test
    @DisplayName("Should auto-set created timestamp")
    void testAutoSetCreatedAt() {
        ConversionHistory history = createTestHistory();
        
        ConversionHistory saved = repository.save(history);
        entityManager.flush();
        
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should handle large SQL text")
    void testLargeSQLText() {
        StringBuilder largeSql = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeSql.append("SELECT * FROM table").append(i).append("; ");
        }
        
        ConversionHistory history = createTestHistory();
        history.setOriginalSQL(largeSql.toString());
        history.setConvertedSQL(largeSql.toString());
        
        ConversionHistory saved = repository.save(history);
        entityManager.flush();
        
        Optional<ConversionHistory> found = repository.findById(saved.getId());
        
        assertThat(found).isPresent();
        assertThat(found.get().getOriginalSQL()).hasSize(largeSql.length());
    }

    @Test
    @DisplayName("Should retrieve all history entries")
    void testFindAll() {
        repository.save(createTestHistory());
        repository.save(createTestHistory());
        repository.save(createTestHistory());
        entityManager.flush();
        
        List<ConversionHistory> all = repository.findAll();
        
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    private ConversionHistory createTestHistory() {
        ConversionHistory history = new ConversionHistory();
        history.setOriginalSQL("SELECT SYSDATE FROM DUAL");
        history.setConvertedSQL("SELECT NOW() FROM DUAL");
        history.setStatementType("QUERY");
        history.setWarnings("Test warning");
        return history;
    }
}
