package com.JohnBravos.bookhub_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Tag(name = "Health", description = "Health Check API")
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Returns server health status")
    public ResponseEntity<Map<String, String>> health() {
        log.debug("Health check called");
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "timestamp", LocalDateTime.now().toString(),
                "service", "BookHub Manager API"
        ));
    }
}
