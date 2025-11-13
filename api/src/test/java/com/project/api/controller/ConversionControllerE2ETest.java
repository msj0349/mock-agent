package com.project.api.controller;

import com.project.api.dto.ConversionRequest;
import com.project.api.dto.ConversionResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Conversion Controller E2E Tests")
class ConversionControllerE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @DisplayName("Should convert Oracle SQL via REST API")
    void testConvertViaAPI() {
        ConversionRequest request = new ConversionRequest(
            "CREATE PROCEDURE test (p_id NUMBER) AS BEGIN NULL; END;",
            false
        );

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/convert")
        .then()
            .statusCode(200)
            .body("success", is(true))
            .body("convertedSQL", notNullValue())
            .body("convertedSQL", containsString("DECIMAL"))
            .body("statementType", equalTo("PROCEDURE"));
    }

    @Test
    @DisplayName("Should handle invalid SQL with error response")
    void testConvertInvalidSQL() {
        ConversionRequest request = new ConversionRequest("", false);

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/convert")
        .then()
            .statusCode(400)
            .body("success", is(false))
            .body("errorMessage", notNullValue());
    }

    @Test
    @DisplayName("Should return conversion warnings")
    void testConversionWithWarnings() {
        ConversionRequest request = new ConversionRequest(
            "CREATE OR REPLACE PROCEDURE complex_proc AS BEGIN NULL; END;",
            false
        );

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/convert")
        .then()
            .statusCode(200)
            .body("success", is(true))
            .body("warnings", notNullValue())
            .body("warnings", not(empty()));
    }

    @Test
    @DisplayName("Should validate converted SQL when requested")
    void testConversionWithValidation() {
        ConversionRequest request = new ConversionRequest(
            "SELECT SYSDATE FROM DUAL",
            true
        );

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/convert")
        .then()
            .statusCode(200)
            .body("success", is(true))
            .body("convertedSQL", containsString("NOW()"));
    }

    @Test
    @DisplayName("Should retrieve conversion history")
    void testGetHistory() {
        ConversionRequest request = new ConversionRequest(
            "SELECT * FROM employees",
            false
        );

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/convert")
        .then()
            .statusCode(200);

        given()
        .when()
            .get("/api/v1/convert/history")
        .then()
            .statusCode(200)
            .body("$", not(empty()));
    }

    @Test
    @DisplayName("Should retrieve specific history entry by ID")
    void testGetHistoryById() {
        ConversionRequest request = new ConversionRequest(
            "SELECT SYSDATE FROM DUAL",
            false
        );

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/convert")
        .then()
            .statusCode(200);

        given()
        .when()
            .get("/api/v1/convert/history/1")
        .then()
            .statusCode(200)
            .body("originalSQL", notNullValue())
            .body("convertedSQL", notNullValue());
    }

    @Test
    @DisplayName("Should return 404 for non-existent history entry")
    void testGetNonExistentHistory() {
        given()
        .when()
            .get("/api/v1/convert/history/99999")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should convert multiple data types")
    void testConvertMultipleDataTypes() {
        ConversionRequest request = new ConversionRequest(
            "CREATE PROCEDURE test (p_id NUMBER, p_name VARCHAR2(100), p_date DATE) AS BEGIN NULL; END;",
            false
        );

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/convert")
        .then()
            .statusCode(200)
            .body("success", is(true))
            .body("convertedSQL", containsString("DECIMAL"))
            .body("convertedSQL", containsString("VARCHAR"));
    }

    @Test
    @DisplayName("Should preserve original SQL in response")
    void testPreserveOriginalSQL() {
        String originalSQL = "SELECT * FROM employees WHERE id = 1";
        ConversionRequest request = new ConversionRequest(originalSQL, false);

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/convert")
        .then()
            .statusCode(200)
            .body("originalSQL", equalTo(originalSQL));
    }
}
