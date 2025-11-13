package com.project.grammar.exception;

import com.project.core.exception.BaseException;

public class GrammarException extends BaseException {

    public GrammarException(String message) {
        super(message, "GRAMMAR_ERROR");
    }

    public GrammarException(String message, Throwable cause) {
        super(message, "GRAMMAR_ERROR", cause);
    }

    public GrammarException(String message, String errorCode) {
        super(message, errorCode);
    }
}
