package com.project.mapping.service;

import com.project.grammar.model.SQLStatement;
import com.project.grammar.parser.OracleToMySQLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ConversionService {
    private static final Logger logger = LoggerFactory.getLogger(ConversionService.class);
    
    private final OracleToMySQLConverter converter;

    public ConversionService(OracleToMySQLConverter converter) {
        this.converter = converter;
    }

    public SQLStatement convertSingleStatement(String oracleSQL) {
        logger.info("Converting single Oracle statement");
        return converter.convert(oracleSQL);
    }

    public List<SQLStatement> convertBatch(List<String> oracleSQLList) {
        logger.info("Converting batch of {} statements", oracleSQLList.size());
        
        List<SQLStatement> results = new ArrayList<>();
        
        for (String sql : oracleSQLList) {
            try {
                SQLStatement statement = converter.convert(sql);
                results.add(statement);
            } catch (Exception e) {
                logger.error("Error converting statement: {}", sql, e);
                SQLStatement errorStatement = new SQLStatement(sql);
                errorStatement.addWarning("Conversion failed: " + e.getMessage());
                results.add(errorStatement);
            }
        }
        
        return results;
    }

    public boolean validateConversion(SQLStatement statement) {
        if (statement == null || statement.getConvertedSQL() == null) {
            return false;
        }
        
        return converter.validate(statement.getConvertedSQL());
    }
}
