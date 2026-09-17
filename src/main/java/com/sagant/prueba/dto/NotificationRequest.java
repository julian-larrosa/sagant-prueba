package com.sagant.prueba.dto;

import com.sagant.prueba.model.NotificationChannel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class NotificationRequest {
    
    @NotBlank(message = "Recipient no puede ser vacío")
    private String recipient;

    @NotNull(message = "Channel es obligatorio, elija LOG o EMAIL")
    private NotificationChannel channel;

    @NotBlank(message = "Subject no puede ser vacío")
    private String subject;
    
    @NotBlank(message = "Body no puede ser vacío")
    private String body;
}
