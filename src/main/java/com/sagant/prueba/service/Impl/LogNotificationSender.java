package com.sagant.prueba.service.Impl;

import org.springframework.stereotype.Component;

import com.sagant.prueba.model.Notification;
import com.sagant.prueba.service.NotificationSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LogNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info("LOG");
        log.info("Notificacion enviada a {}", notification.getRecipientEmail());
        log.info("Tema : {}", notification.getSubject());
        log.info("Cuerpo: {}", notification.getBody());
    }

}
