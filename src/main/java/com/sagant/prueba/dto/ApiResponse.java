package com.sagant.prueba.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .status(201)
                .message("Creado exitosamente")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message("OK")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> noContent(T data) {
        return ApiResponse.<T>builder()
                .status(204)
                .message("Sin contenido")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> accepted(T data) {
        return ApiResponse.<T>builder()
                .status(202)
                .message("Aceptado para procesamiento asíncrono")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
