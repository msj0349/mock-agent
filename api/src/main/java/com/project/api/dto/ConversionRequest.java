package com.project.api.dto;

public class ConversionRequest {
    private String oracleSQL;
    private boolean validate;

    public ConversionRequest() {
    }

    public ConversionRequest(String oracleSQL, boolean validate) {
        this.oracleSQL = oracleSQL;
        this.validate = validate;
    }

    public String getOracleSQL() {
        return oracleSQL;
    }

    public void setOracleSQL(String oracleSQL) {
        this.oracleSQL = oracleSQL;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }
}
