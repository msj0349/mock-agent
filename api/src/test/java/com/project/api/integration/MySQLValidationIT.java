package com.project.api.integration;

import com.project.grammar.model.SQLStatement;
import com.project.grammar.parser.OracleToMySQLConverter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MySQL Validation Integration Tests using Testcontainers.
 * 
 * These tests require Docker to be installed and running.
 * To enable these tests, set environment variable: ENABLE_TESTCONTAINERS=true
 * 
 * Example: ENABLE_TESTCONTAINERS=true mvn verify
 */
@Testcontainers
@DisplayName("MySQL Validation Integration Tests with Testcontainers")
@EnabledIfEnvironmentVariable(named = "ENABLE_TESTCONTAINERS", matches = "true")
class MySQLValidationIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private OracleToMySQLConverter converter;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        converter = new OracleToMySQLConverter();
        
        String jdbcUrl = mysqlContainer.getJdbcUrl();
        String username = mysqlContainer.getUsername();
        String password = mysqlContainer.getPassword();
        
        connection = DriverManager.getConnection(jdbcUrl, username, password);
        
        createTestTable();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    @DisplayName("MySQL container should be running")
    void testContainerIsRunning() {
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

    @Test
    @DisplayName("Should execute converted SQL in MySQL")
    void testExecuteConvertedSQL() throws SQLException {
        String oracleSQL = "SELECT SYSDATE FROM DUAL";
        SQLStatement statement = converter.convert(oracleSQL);
        
        String mysqlSQL = "SELECT NOW()";
        
        try (Statement stmt = connection.createStatement()) {
            boolean hasResults = stmt.execute(mysqlSQL);
            assertThat(hasResults).isTrue();
        }
    }

    @Test
    @DisplayName("Should validate converted data types work in MySQL")
    void testConvertedDataTypesInMySQL() throws SQLException {
        String createTableSQL = 
            "CREATE TABLE test_datatypes (" +
            "id DECIMAL(10) PRIMARY KEY, " +
            "name VARCHAR(100), " +
            "created_at DATETIME" +
            ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            
            String insertSQL = 
                "INSERT INTO test_datatypes (id, name, created_at) " +
                "VALUES (1, 'Test', NOW())";
            int rowsAffected = stmt.executeUpdate(insertSQL);
            
            assertThat(rowsAffected).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("Should execute simple converted procedure in MySQL")
    void testExecuteSimpleProcedure() throws SQLException {
        String dropIfExists = "DROP PROCEDURE IF EXISTS update_employee_salary";
        
        String procedureSQL = 
            "CREATE PROCEDURE update_employee_salary(" +
            "IN p_employee_id DECIMAL, " +
            "IN p_new_salary DECIMAL) " +
            "BEGIN " +
            "UPDATE employees SET salary = p_new_salary " +
            "WHERE employee_id = p_employee_id; " +
            "END";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(dropIfExists);
            stmt.execute(procedureSQL);
            
            boolean exists = checkProcedureExists("update_employee_salary");
            assertThat(exists).isTrue();
        }
    }

    @Test
    @DisplayName("Should validate NOW() function works in MySQL")
    void testNowFunctionInMySQL() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            var rs = stmt.executeQuery("SELECT NOW() as current_time");
            
            assertThat(rs.next()).isTrue();
            assertThat(rs.getTimestamp("current_time")).isNotNull();
        }
    }

    @Test
    @DisplayName("Should validate DECIMAL type works in MySQL")
    void testDecimalTypeInMySQL() throws SQLException {
        String createTableSQL = 
            "CREATE TABLE test_decimal (" +
            "id INT PRIMARY KEY, " +
            "amount DECIMAL(10, 2)" +
            ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            stmt.executeUpdate("INSERT INTO test_decimal (id, amount) VALUES (1, 123.45)");
            
            var rs = stmt.executeQuery("SELECT amount FROM test_decimal WHERE id = 1");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getBigDecimal("amount").doubleValue()).isEqualTo(123.45);
        }
    }

    @Test
    @DisplayName("Should validate VARCHAR type works in MySQL")
    void testVarcharTypeInMySQL() throws SQLException {
        String createTableSQL = 
            "CREATE TABLE test_varchar (" +
            "id INT PRIMARY KEY, " +
            "name VARCHAR(100)" +
            ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            stmt.executeUpdate("INSERT INTO test_varchar (id, name) VALUES (1, 'Test Name')");
            
            var rs = stmt.executeQuery("SELECT name FROM test_varchar WHERE id = 1");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Test Name");
        }
    }

    @Test
    @DisplayName("Should validate converted complex query in MySQL")
    void testComplexQueryInMySQL() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO employees (employee_id, name, salary) VALUES (1, 'John Doe', 50000)");
            stmt.executeUpdate("INSERT INTO employees (employee_id, name, salary) VALUES (2, 'Jane Smith', 60000)");
            
            var rs = stmt.executeQuery(
                "SELECT employee_id, name, salary " +
                "FROM employees " +
                "WHERE salary > 45000 " +
                "ORDER BY salary DESC"
            );
            
            int count = 0;
            while (rs.next()) {
                count++;
            }
            
            assertThat(count).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("Should validate UPDATE statement with NOW() in MySQL")
    void testUpdateWithNowInMySQL() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO employees (employee_id, name, salary) VALUES (1, 'Test User', 50000)");
            
            int rowsAffected = stmt.executeUpdate(
                "UPDATE employees SET salary = 55000, last_modified = NOW() WHERE employee_id = 1"
            );
            
            assertThat(rowsAffected).isEqualTo(1);
            
            var rs = stmt.executeQuery("SELECT last_modified FROM employees WHERE employee_id = 1");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getTimestamp("last_modified")).isNotNull();
        }
    }

    private void createTestTable() throws SQLException {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS employees (" +
            "employee_id INT PRIMARY KEY, " +
            "name VARCHAR(100), " +
            "salary DECIMAL(10, 2), " +
            "last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    private boolean checkProcedureExists(String procedureName) throws SQLException {
        String query = 
            "SELECT COUNT(*) as count FROM information_schema.routines " +
            "WHERE routine_schema = 'testdb' AND routine_name = '" + procedureName + "'";
        
        try (Statement stmt = connection.createStatement()) {
            var rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }
}
