package com.sagant.prueba.mapper;

import com.sagant.prueba.dto.NotificationRequest;
import com.sagant.prueba.dto.NotificationResponse;
import com.sagant.prueba.model.Notification;
import com.sagant.prueba.model.NotificationStatus;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationRequest request) {
        if (request == null) {
            return null;
        }

        return Notification.builder()
                .recipientEmail(request.getRecipient())
                .channel(request.getChannel())
                .subject(request.getSubject())
                .body(request.getBody())
                .status(NotificationStatus.PENDING)
                .build();
    }

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationResponse.builder()
                .id(notification.getId())
                .status(notification.getStatus())
                .message("Notificación aceptada para despacho asíncrono")
                .build();
    }
}