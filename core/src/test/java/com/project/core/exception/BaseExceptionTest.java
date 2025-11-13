package com.project.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BaseException Tests")
class BaseExceptionTest {

    @Test
    @DisplayName("Should create exception with message only")
    void testConstructorWithMessage() {
        BaseException exception = new BaseException("Test error message");
        
        assertThat(exception.getMessage()).isEqualTo("Test error message");
        assertThat(exception.getErrorCode()).isEqualTo("GENERIC_ERROR");
    }

    @Test
    @DisplayName("Should create exception with message and custom error code")
    void testConstructorWithMessageAndCode() {
        BaseException exception = new BaseException("Test error", "CUSTOM_CODE");
        
        assertThat(exception.getMessage()).isEqualTo("Test error");
        assertThat(exception.getErrorCode()).isEqualTo("CUSTOM_CODE");
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void testConstructorWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        BaseException exception = new BaseException("Wrapped error", cause);
        
        assertThat(exception.getMessage()).isEqualTo("Wrapped error");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo("GENERIC_ERROR");
    }

    @Test
    @DisplayName("Should create exception with message, error code, and cause")
    void testConstructorWithAllParameters() {
        Throwable cause = new IllegalArgumentException("Invalid argument");
        BaseException exception = new BaseException("Processing failed", "PROC_ERROR", cause);
        
        assertThat(exception.getMessage()).isEqualTo("Processing failed");
        assertThat(exception.getErrorCode()).isEqualTo("PROC_ERROR");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Invalid argument");
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void testIsRuntimeException() {
        BaseException exception = new BaseException("Test");
        
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
