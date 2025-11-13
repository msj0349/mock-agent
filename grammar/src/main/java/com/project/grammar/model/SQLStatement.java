package com.project.grammar.model;

import java.util.ArrayList;
import java.util.List;

public class SQLStatement {
    private String originalSQL;
    private String convertedSQL;
    private StatementType type;
    private List<String> warnings;

    public SQLStatement() {
        this.warnings = new ArrayList<>();
    }

    public SQLStatement(String originalSQL) {
        this.originalSQL = originalSQL;
        this.warnings = new ArrayList<>();
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

    public StatementType getType() {
        return type;
    }

    public void setType(StatementType type) {
        this.type = type;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public enum StatementType {
        PROCEDURE,
        FUNCTION,
        PACKAGE,
        TRIGGER,
        VIEW,
        UNKNOWN
    }
}
