package com.sagant.prueba.dto;

import com.sagant.prueba.model.NotificationStatus;

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
public class NotificationResponse {
    
    private Long id;
    private NotificationStatus status;
    private String message;
}