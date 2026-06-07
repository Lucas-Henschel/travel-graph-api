package com.travelGraph.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelGraph.controller.exceptions.StandardError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class WriteErrorResponse {
    public static void writeErrorResponse(
        HttpServletResponse response,
        HttpServletRequest request,
        HttpStatus status,
        Exception exception,
        ObjectMapper objectMapper
    ) throws IOException {
        List<String> errors = List.of(exception.getMessage());

        StandardError err = new StandardError(
            Instant.now(),
            status.value(),
            errors,
            exception.getMessage(),
            request.getRequestURI()
        );

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(err));
    }
}
