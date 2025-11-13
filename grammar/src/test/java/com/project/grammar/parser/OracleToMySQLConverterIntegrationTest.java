package com.project.grammar.parser;

import com.project.grammar.model.SQLStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OracleToMySQL Converter Integration Tests")
class OracleToMySQLConverterIntegrationTest {

    private OracleToMySQLConverter converter;

    @BeforeEach
    void setUp() {
        converter = new OracleToMySQLConverter();
    }

    @Test
    @DisplayName("Should convert simple procedure from file")
    void testConvertSimpleProcedureFromFile() throws IOException {
        String oracleSQL = loadResourceFile("oracle-samples/simple_procedure.sql");
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(SQLStatement.StatementType.PROCEDURE);
        assertThat(result.getConvertedSQL()).contains("DECIMAL");
        assertThat(result.getConvertedSQL()).contains("NOW()");
        assertThat(result.getConvertedSQL()).doesNotContain("NUMBER");
        assertThat(result.getConvertedSQL()).doesNotContain("SYSDATE");
    }

    @Test
    @DisplayName("Should convert function with DECODE from file")
    void testConvertFunctionWithDecodeFromFile() throws IOException {
        String oracleSQL = loadResourceFile("oracle-samples/function_with_decode.sql");
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(SQLStatement.StatementType.FUNCTION);
        assertThat(result.getConvertedSQL()).contains("VARCHAR");
        assertThat(result.getConvertedSQL()).doesNotContain("VARCHAR2");
    }

    @Test
    @DisplayName("Should convert complex procedure with all features")
    void testConvertComplexProcedureFromFile() throws IOException {
        String oracleSQL = loadResourceFile("oracle-samples/complex_procedure.sql");
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(SQLStatement.StatementType.PROCEDURE);
        
        assertThat(result.getConvertedSQL()).contains("DECIMAL");
        assertThat(result.getConvertedSQL()).contains("NOW()");
        assertThat(result.getConvertedSQL()).doesNotContain("NUMBER");
        assertThat(result.getConvertedSQL()).doesNotContain("SYSDATE");
        
        assertThat(result.getWarnings()).isNotEmpty();
        assertThat(result.getWarnings().get(0)).contains("manual review");
    }

    @Test
    @DisplayName("Should match expected output for simple procedure")
    void testSimpleProcedureMatchesExpected() throws IOException {
        String oracleSQL = loadResourceFile("oracle-samples/simple_procedure.sql");
        
        SQLStatement result = converter.convert(oracleSQL);
        String converted = result.getConvertedSQL().trim();
        
        assertThat(converted).contains("DELIMITER");
        assertThat(converted).contains("CREATE PROCEDURE");
        assertThat(converted).contains("BEGIN");
        assertThat(converted).contains("$");
        
        assertThat(converted).contains("DECIMAL");
        assertThat(converted).contains("NOW()");
    }

    @Test
    @DisplayName("Should preserve procedure logic during conversion")
    void testPreserveProcedureLogic() throws IOException {
        String oracleSQL = loadResourceFile("oracle-samples/simple_procedure.sql");
        
        SQLStatement result = converter.convert(oracleSQL);
        
        assertThat(result.getConvertedSQL()).contains("UPDATE employees");
        assertThat(result.getConvertedSQL()).contains("SET salary");
        assertThat(result.getConvertedSQL()).contains("WHERE employee_id");
        assertThat(result.getConvertedSQL()).contains("COMMIT");
    }

    @Test
    @DisplayName("Should convert all data types in complex procedure")
    void testComplexProcedureDataTypeConversion() throws IOException {
        String oracleSQL = loadResourceFile("oracle-samples/complex_procedure.sql");
        
        SQLStatement result = converter.convert(oracleSQL);
        String converted = result.getConvertedSQL();
        
        int numberCount = countOccurrences(oracleSQL, "NUMBER");
        int decimalCount = countOccurrences(converted, "DECIMAL");
        
        assertThat(decimalCount).isGreaterThanOrEqualTo(numberCount);
        
        assertThat(converted).doesNotContain("NUMBER");
    }

    @Test
    @DisplayName("Should handle multiple SYSDATE occurrences")
    void testMultipleSysdateConversion() throws IOException {
        String oracleSQL = loadResourceFile("oracle-samples/complex_procedure.sql");
        
        SQLStatement result = converter.convert(oracleSQL);
        
        int sysdateCountOriginal = countOccurrences(oracleSQL, "SYSDATE");
        int nowCountConverted = countOccurrences(result.getConvertedSQL(), "NOW()");
        
        assertThat(nowCountConverted).isGreaterThanOrEqualTo(sysdateCountOriginal);
        assertThat(result.getConvertedSQL()).doesNotContainIgnoringCase("SYSDATE");
    }

    private String loadResourceFile(String filename) throws IOException {
        Path resourcePath = Paths.get("src/test/resources", filename);
        
        if (!Files.exists(resourcePath)) {
            resourcePath = Paths.get("grammar/src/test/resources", filename);
        }
        
        if (!Files.exists(resourcePath)) {
            ClassLoader classLoader = getClass().getClassLoader();
            if (classLoader.getResource(filename) != null) {
                return new String(classLoader.getResourceAsStream(filename).readAllBytes());
            }
        }
        
        return Files.readString(resourcePath);
    }

    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        String lowerText = text.toLowerCase();
        String lowerPattern = pattern.toLowerCase();
        
        while ((index = lowerText.indexOf(lowerPattern, index)) != -1) {
            count++;
            index += lowerPattern.length();
        }
        
        return count;
    }
}
