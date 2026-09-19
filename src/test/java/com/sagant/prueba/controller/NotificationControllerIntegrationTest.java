package com.sagant.prueba.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagant.prueba.dto.NotificationRequest;
import com.sagant.prueba.model.NotificationChannel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.security.api-key:key}")
    private String apiKey;

    private static final String ENDPOINT = "/api/notifications";

    @Test
    @DisplayName("Debe responder 202 Accepted cuando el request y la API Key son válidos")
    void createNotification_Success() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .recipient("test@empresa.com")
                .channel(NotificationChannel.LOG)
                .subject("Asunto de Prueba")
                .body("Cuerpo del mensaje de prueba")
                .build();

        mockMvc.perform(post(ENDPOINT)
                .header("X-API-KEY", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value(202));
    }

    @Test
    @DisplayName("Debe responder 401 Unauthorized cuando falta la API Key")
    void createNotification_Unauthorized_MissingApiKey() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .recipient("test@empresa.com")
                .channel(NotificationChannel.LOG)
                .subject("Asunto")
                .body("Cuerpo")
                .build();

        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Debe responder 400 Bad Request cuando el recipient está vacío")
    void createNotification_BadRequest_InvalidPayload() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .recipient("") // recipient vacío
                .channel(NotificationChannel.LOG)
                .subject("Asunto")
                .body("Cuerpo")
                .build();

        mockMvc.perform(post(ENDPOINT)
                .header("X-API-KEY", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}