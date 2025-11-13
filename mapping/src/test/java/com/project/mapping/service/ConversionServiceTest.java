package com.project.mapping.service;

import com.project.grammar.exception.GrammarException;
import com.project.grammar.model.SQLStatement;
import com.project.grammar.parser.OracleToMySQLConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConversionService Tests with Mocked Dependencies")
class ConversionServiceTest {

    @Mock
    private OracleToMySQLConverter mockConverter;

    private ConversionService service;

    @BeforeEach
    void setUp() {
        service = new ConversionService(mockConverter);
    }

    @Test
    @DisplayName("Should convert single statement using converter")
    void testConvertSingleStatement() {
        String oracleSQL = "SELECT SYSDATE FROM DUAL";
        SQLStatement expected = new SQLStatement(oracleSQL);
        expected.setConvertedSQL("SELECT NOW() FROM DUAL");
        
        when(mockConverter.convert(oracleSQL)).thenReturn(expected);
        
        SQLStatement result = service.convertSingleStatement(oracleSQL);
        
        assertThat(result).isNotNull();
        assertThat(result.getConvertedSQL()).isEqualTo("SELECT NOW() FROM DUAL");
        verify(mockConverter, times(1)).convert(oracleSQL);
    }

    @Test
    @DisplayName("Should convert batch of statements")
    void testConvertBatch() {
        List<String> sqlList = Arrays.asList(
            "SELECT SYSDATE FROM DUAL",
            "SELECT * FROM employees WHERE id = 1"
        );
        
        SQLStatement stmt1 = new SQLStatement(sqlList.get(0));
        stmt1.setConvertedSQL("SELECT NOW() FROM DUAL");
        
        SQLStatement stmt2 = new SQLStatement(sqlList.get(1));
        stmt2.setConvertedSQL("SELECT * FROM employees WHERE id = 1");
        
        when(mockConverter.convert(sqlList.get(0))).thenReturn(stmt1);
        when(mockConverter.convert(sqlList.get(1))).thenReturn(stmt2);
        
        List<SQLStatement> results = service.convertBatch(sqlList);
        
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getConvertedSQL()).contains("NOW()");
        verify(mockConverter, times(2)).convert(anyString());
    }

    @Test
    @DisplayName("Should handle conversion errors in batch")
    void testConvertBatchWithErrors() {
        List<String> sqlList = Arrays.asList(
            "SELECT SYSDATE FROM DUAL",
            "INVALID SQL"
        );
        
        SQLStatement stmt1 = new SQLStatement(sqlList.get(0));
        stmt1.setConvertedSQL("SELECT NOW() FROM DUAL");
        
        when(mockConverter.convert(sqlList.get(0))).thenReturn(stmt1);
        when(mockConverter.convert(sqlList.get(1)))
            .thenThrow(new GrammarException("Invalid SQL syntax"));
        
        List<SQLStatement> results = service.convertBatch(sqlList);
        
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getConvertedSQL()).isNotNull();
        assertThat(results.get(1).getWarnings()).isNotEmpty();
        assertThat(results.get(1).getWarnings().get(0)).contains("Conversion failed");
    }

    @Test
    @DisplayName("Should validate conversion result")
    void testValidateConversion() {
        SQLStatement statement = new SQLStatement();
        statement.setConvertedSQL("SELECT NOW() FROM DUAL");
        
        when(mockConverter.validate(anyString())).thenReturn(true);
        
        boolean result = service.validateConversion(statement);
        
        assertThat(result).isTrue();
        verify(mockConverter, times(1)).validate(anyString());
    }

    @Test
    @DisplayName("Should return false for null statement in validation")
    void testValidateNullStatement() {
        boolean result = service.validateConversion(null);
        
        assertThat(result).isFalse();
        verify(mockConverter, never()).validate(anyString());
    }

    @Test
    @DisplayName("Should return false for statement with null converted SQL")
    void testValidateStatementWithNullConvertedSQL() {
        SQLStatement statement = new SQLStatement();
        statement.setConvertedSQL(null);
        
        boolean result = service.validateConversion(statement);
        
        assertThat(result).isFalse();
        verify(mockConverter, never()).validate(anyString());
    }

    @Test
    @DisplayName("Should handle empty batch list")
    void testConvertEmptyBatch() {
        List<String> emptyList = Arrays.asList();
        
        List<SQLStatement> results = service.convertBatch(emptyList);
        
        assertThat(results).isEmpty();
        verify(mockConverter, never()).convert(anyString());
    }

    @Test
    @DisplayName("Should propagate converter calls correctly")
    void testConverterInteraction() {
        String sql = "CREATE PROCEDURE test AS BEGIN NULL; END;";
        SQLStatement expected = new SQLStatement(sql);
        
        when(mockConverter.convert(sql)).thenReturn(expected);
        
        service.convertSingleStatement(sql);
        
        verify(mockConverter, times(1)).convert(sql);
        verifyNoMoreInteractions(mockConverter);
    }
}
