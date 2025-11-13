package com.project.api.dto;

import java.util.List;

public class ConversionResponse {
    private String originalSQL;
    private String convertedSQL;
    private String statementType;
    private List<String> warnings;
    private boolean success;
    private String errorMessage;

    public ConversionResponse() {
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

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
