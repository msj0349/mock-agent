package com.project.api.controller;

import com.project.api.dto.ConversionRequest;
import com.project.api.dto.ConversionResponse;
import com.project.api.entity.ConversionHistory;
import com.project.api.repository.ConversionHistoryRepository;
import com.project.grammar.exception.GrammarException;
import com.project.grammar.model.SQLStatement;
import com.project.grammar.parser.OracleToMySQLConverter;
import com.project.mapping.service.ConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/convert")
public class ConversionController {
    private static final Logger logger = LoggerFactory.getLogger(ConversionController.class);

    private final ConversionService conversionService;
    private final ConversionHistoryRepository historyRepository;

    public ConversionController(ConversionService conversionService,
                                ConversionHistoryRepository historyRepository) {
        this.conversionService = conversionService;
        this.historyRepository = historyRepository;
    }

    @PostMapping
    public ResponseEntity<ConversionResponse> convert(@RequestBody ConversionRequest request) {
        logger.info("Received conversion request");
        
        ConversionResponse response = new ConversionResponse();
        
        try {
            SQLStatement statement = conversionService.convertSingleStatement(request.getOracleSQL());
            
            response.setOriginalSQL(statement.getOriginalSQL());
            response.setConvertedSQL(statement.getConvertedSQL());
            response.setStatementType(statement.getType().name());
            response.setWarnings(statement.getWarnings());
            response.setSuccess(true);
            
            if (request.isValidate()) {
                boolean isValid = conversionService.validateConversion(statement);
                if (!isValid) {
                    response.getWarnings().add("Validation failed for converted SQL");
                }
            }
            
            saveToHistory(statement);
            
            return ResponseEntity.ok(response);
            
        } catch (GrammarException e) {
            logger.error("Grammar exception during conversion", e);
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            logger.error("Unexpected error during conversion", e);
            response.setSuccess(false);
            response.setErrorMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<ConversionResponse>> getHistory() {
        List<ConversionHistory> history = historyRepository.findAll();
        
        List<ConversionResponse> responses = history.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<ConversionResponse> getHistoryById(@PathVariable Long id) {
        return historyRepository.findById(id)
            .map(this::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private void saveToHistory(SQLStatement statement) {
        try {
            ConversionHistory history = new ConversionHistory();
            history.setOriginalSQL(statement.getOriginalSQL());
            history.setConvertedSQL(statement.getConvertedSQL());
            history.setStatementType(statement.getType().name());
            history.setWarnings(String.join("; ", statement.getWarnings()));
            
            historyRepository.save(history);
        } catch (Exception e) {
            logger.warn("Failed to save conversion history", e);
        }
    }

    private ConversionResponse toResponse(ConversionHistory history) {
        ConversionResponse response = new ConversionResponse();
        response.setOriginalSQL(history.getOriginalSQL());
        response.setConvertedSQL(history.getConvertedSQL());
        response.setStatementType(history.getStatementType());
        response.setSuccess(true);
        
        if (history.getWarnings() != null && !history.getWarnings().isEmpty()) {
            response.setWarnings(List.of(history.getWarnings().split("; ")));
        }
        
        return response;
    }
}
