package com.project.grammar.parser;

import com.project.core.util.StringUtils;
import com.project.grammar.exception.GrammarException;
import com.project.grammar.model.SQLStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OracleToMySQLConverter {
    private static final Logger logger = LoggerFactory.getLogger(OracleToMySQLConverter.class);

    private static final Pattern CREATE_PROCEDURE_PATTERN = 
        Pattern.compile("CREATE\\s+(OR\\s+REPLACE\\s+)?PROCEDURE", Pattern.CASE_INSENSITIVE);
    private static final Pattern CREATE_FUNCTION_PATTERN = 
        Pattern.compile("CREATE\\s+(OR\\s+REPLACE\\s+)?FUNCTION", Pattern.CASE_INSENSITIVE);
    private static final Pattern NUMBER_TYPE_PATTERN = 
        Pattern.compile("\\bNUMBER\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern VARCHAR2_PATTERN = 
        Pattern.compile("\\bVARCHAR2\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern SYSDATE_PATTERN = 
        Pattern.compile("\\bSYSDATE\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DECODE_PATTERN = 
        Pattern.compile("\\bDECODE\\s*\\(", Pattern.CASE_INSENSITIVE);
    private static final Pattern IS_KEYWORD_PATTERN = 
        Pattern.compile("\\bIS\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern AS_KEYWORD_PATTERN = 
        Pattern.compile("\\bAS\\b", Pattern.CASE_INSENSITIVE);

    public SQLStatement convert(String oracleSQL) {
        if (StringUtils.isBlank(oracleSQL)) {
            throw new GrammarException("Oracle SQL cannot be null or empty", "EMPTY_INPUT");
        }

        logger.debug("Converting Oracle SQL to MySQL: {}", oracleSQL);

        SQLStatement statement = new SQLStatement(oracleSQL);
        statement.setType(detectStatementType(oracleSQL));

        String converted = oracleSQL;

        converted = convertDataTypes(converted);
        converted = convertFunctions(converted);
        converted = convertProcedureSyntax(converted, statement);

        statement.setConvertedSQL(converted);

        logger.debug("Conversion completed. Warnings: {}", statement.getWarnings().size());

        return statement;
    }

    private SQLStatement.StatementType detectStatementType(String sql) {
        if (CREATE_PROCEDURE_PATTERN.matcher(sql).find()) {
            return SQLStatement.StatementType.PROCEDURE;
        } else if (CREATE_FUNCTION_PATTERN.matcher(sql).find()) {
            return SQLStatement.StatementType.FUNCTION;
        }
        return SQLStatement.StatementType.UNKNOWN;
    }

    private String convertDataTypes(String sql) {
        sql = NUMBER_TYPE_PATTERN.matcher(sql).replaceAll("DECIMAL");
        sql = VARCHAR2_PATTERN.matcher(sql).replaceAll("VARCHAR");
        return sql;
    }

    private String convertFunctions(String sql) {
        sql = SYSDATE_PATTERN.matcher(sql).replaceAll("NOW()");
        
        Matcher decodeMatcher = DECODE_PATTERN.matcher(sql);
        if (decodeMatcher.find()) {
            sql = convertDecode(sql);
        }
        
        return sql;
    }

    private String convertDecode(String sql) {
        Pattern decodePattern = Pattern.compile("DECODE\\s*\\(([^,]+),\\s*([^,]+),\\s*([^,]+),\\s*([^)]+)\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = decodePattern.matcher(sql);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String condition = matcher.group(1);
            String value1 = matcher.group(2);
            String thenValue = matcher.group(3);
            String elseValue = matcher.group(4);
            String replacement = "CASE WHEN " + condition + " = " + value1 + " THEN " + thenValue + " ELSE " + elseValue + " END";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }

    private String convertProcedureSyntax(String sql, SQLStatement statement) {
        if (statement.getType() == SQLStatement.StatementType.PROCEDURE) {
            Matcher m = CREATE_PROCEDURE_PATTERN.matcher(sql);
            if (m.find()) {
                StringBuffer sb = new StringBuffer();
                m.appendReplacement(sb, Matcher.quoteReplacement("DELIMITER $$\nCREATE PROCEDURE"));
                m.appendTail(sb);
                sql = sb.toString();
            }
            
            sql = IS_KEYWORD_PATTERN.matcher(sql).replaceAll("BEGIN");
            sql = AS_KEYWORD_PATTERN.matcher(sql).replaceAll("BEGIN");
            
            if (!sql.trim().endsWith("$$")) {
                sql += "\n$$\nDELIMITER ;";
            }
            
            statement.addWarning("Procedure syntax converted - manual review recommended");
        }
        
        return sql;
    }

    public boolean validate(String sql) {
        if (StringUtils.isBlank(sql)) {
            return false;
        }
        
        return sql.trim().length() > 0;
    }
}
