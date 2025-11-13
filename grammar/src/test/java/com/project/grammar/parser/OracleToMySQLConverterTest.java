package com.project.grammar.parser;

import com.project.grammar.exception.GrammarException;
import com.project.grammar.model.SQLStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OracleToMySQL Converter Unit Tests")
class OracleToMySQLConverterTest {

    private OracleToMySQLConverter converter;

    @BeforeEach
    void setUp() {
        converter = new OracleToMySQLConverter();
    }

    @Test
    @DisplayName("Should convert NUMBER to DECIMAL")
    void testConvertNumberType() {
        String oracleSQL = "CREATE PROCEDURE test (p_id NUMBER) AS BEGIN NULL; END;";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getConvertedSQL()).contains("DECIMAL");
        assertThat(result.getConvertedSQL()).doesNotContain("NUMBER");
    }

    @Test
    @DisplayName("Should convert VARCHAR2 to VARCHAR")
    void testConvertVarchar2Type() {
        String oracleSQL = "CREATE PROCEDURE test (p_name VARCHAR2(100)) AS BEGIN NULL; END;";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getConvertedSQL()).contains("VARCHAR");
        assertThat(result.getConvertedSQL()).doesNotContain("VARCHAR2");
    }

    @Test
    @DisplayName("Should convert SYSDATE to NOW()")
    void testConvertSysdate() {
        String oracleSQL = "SELECT SYSDATE FROM DUAL;";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getConvertedSQL()).contains("NOW()");
        assertThat(result.getConvertedSQL()).doesNotContain("SYSDATE");
    }

    @Test
    @DisplayName("Should detect procedure statement type")
    void testDetectProcedureType() {
        String oracleSQL = "CREATE OR REPLACE PROCEDURE test_proc AS BEGIN NULL; END;";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getType()).isEqualTo(SQLStatement.StatementType.PROCEDURE);
    }

    @Test
    @DisplayName("Should detect function statement type")
    void testDetectFunctionType() {
        String oracleSQL = "CREATE OR REPLACE FUNCTION test_func RETURN NUMBER AS BEGIN RETURN 1; END;";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getType()).isEqualTo(SQLStatement.StatementType.FUNCTION);
    }

    @Test
    @DisplayName("Should convert procedure syntax with IS")
    void testConvertProcedureSyntaxWithIS() {
        String oracleSQL = "CREATE PROCEDURE test_proc IS BEGIN NULL; END;";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getConvertedSQL()).contains("BEGIN");
        assertThat(result.getConvertedSQL()).doesNotContain(" IS ");
    }

    @Test
    @DisplayName("Should convert procedure syntax with AS")
    void testConvertProcedureSyntaxWithAS() {
        String oracleSQL = "CREATE PROCEDURE test_proc AS BEGIN NULL; END;";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getConvertedSQL()).contains("BEGIN");
        assertThat(result.getWarnings()).isNotEmpty();
    }

    @Test
    @DisplayName("Should add delimiter for MySQL procedure")
    void testAddDelimiter() {
        String oracleSQL = "CREATE PROCEDURE test_proc AS BEGIN NULL; END;";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getConvertedSQL()).contains("DELIMITER");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should throw exception for null or empty input")
    void testConvertNullOrEmpty(String input) {
        assertThatThrownBy(() -> converter.convert(input))
                .isInstanceOf(GrammarException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("Should preserve original SQL in statement")
    void testPreserveOriginalSQL() {
        String oracleSQL = "SELECT * FROM users WHERE id = 1";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getOriginalSQL()).isEqualTo(oracleSQL);
    }

    @Test
    @DisplayName("Should add warnings for complex conversions")
    void testWarningsForComplexConversions() {
        String oracleSQL = "CREATE OR REPLACE PROCEDURE complex_proc AS BEGIN NULL; END;";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getWarnings()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Should validate non-empty SQL")
    void testValidateNonEmpty() {
        String sql = "SELECT 1";
        
        boolean result = converter.validate(sql);
        
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Should not validate null or blank SQL")
    void testValidateNullOrBlank(String sql) {
        boolean result = converter.validate(sql);
        
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should handle mixed case keywords")
    void testMixedCaseKeywords() {
        String oracleSQL = "create procedure TEST as begin select SysDaTE from dual; end;";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getConvertedSQL()).containsIgnoringCase("NOW()");
        assertThat(result.getConvertedSQL()).doesNotContainIgnoringCase("SYSDATE");
    }

    @Test
    @DisplayName("Should convert multiple data types in single statement")
    void testMultipleDataTypeConversions() {
        String oracleSQL = "CREATE PROCEDURE test (p_id NUMBER, p_name VARCHAR2(100)) AS BEGIN NULL; END;";
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getConvertedSQL()).contains("DECIMAL");
        assertThat(result.getConvertedSQL()).contains("VARCHAR(100)");
        assertThat(result.getConvertedSQL()).doesNotContain("NUMBER");
        assertThat(result.getConvertedSQL()).doesNotContain("VARCHAR2");
    }
}
