package com.backend.restservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.restservice.dto.RestaurantDto;
import com.backend.restservice.dto.SessionDto;
import com.backend.restservice.service.SessionService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.backend.restservice.helper.ApiResponse;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    @Operation(summary = "Create a new session for participant to join")
    public ResponseEntity<ApiResponse<?>> createSession(@Valid @RequestBody SessionDto newSessionDto,
            HttpServletRequest request) {
        String currUser = request.getUserPrincipal().getName();

        return ResponseEntity.ok(sessionService.createSession(newSessionDto, currUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a session by id to display list of participants and restaurants submitted or to view the result")
    public ResponseEntity<ApiResponse<?>> getSessionById(@PathVariable Long id, HttpServletRequest request) {
        String currUser = request.getUserPrincipal().getName();

        return ResponseEntity.ok(sessionService.getSessionById(id, currUser));
    }

    @PutMapping("/{id}/participant")
    @Operation(summary = "Add a new participant to an active session")
    public ResponseEntity<ApiResponse<?>> joinSession(@PathVariable Long id, HttpServletRequest request) {
        String currUser = request.getUserPrincipal().getName();

        return ResponseEntity.ok(sessionService.joinSession(id, currUser));
    }

    @PutMapping("/{id}/restaurant")
    @Operation(summary = "Add a new restaurant to an active session")
    public ResponseEntity<ApiResponse<?>> addRestaurantToSession(@Valid @RequestBody RestaurantDto newRestaurantDto,
            @PathVariable Long id, HttpServletRequest request) {
        String currUser = request.getUserPrincipal().getName();

        return ResponseEntity.ok(sessionService.addRestaurantToSession(newRestaurantDto, id, currUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "End an active event to get a selected restaurant")
    public ResponseEntity<ApiResponse<?>> endSession(@PathVariable Long id, HttpServletRequest request) {
        String currUser = request.getUserPrincipal().getName();

        return ResponseEntity.ok(sessionService.endSession(id, currUser));
    }
}
