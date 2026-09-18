package com.sagant.prueba.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sagant.prueba.service.NotificationService;
import com.sagant.prueba.config.*;
import com.sagant.prueba.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import com.sagant.prueba.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = SwaggerConstants.CREATED, description = "notificación creada exitosamente")
    @Operation(summary = "Crear nueva notificación", description = "Crea una notificación con estado PENDING")
    public ResponseEntity<com.sagant.prueba.dto.ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.createAndDispatch(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.accepted(response));
    }

}
