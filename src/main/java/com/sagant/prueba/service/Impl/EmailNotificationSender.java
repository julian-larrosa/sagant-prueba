package com.sagant.prueba.service.Impl;

import com.sagant.prueba.model.Notification;
import com.sagant.prueba.service.NotificationSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;

    @Override
    public void send(Notification notification) {
        log.info("Enviando mensaje a: {}", notification.getRecipientEmail());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@notification-service.com");
        message.setTo(notification.getRecipientEmail());
        message.setSubject(notification.getSubject());
        message.setText(notification.getBody());
        mailSender.send(message);

        log.info("Email ha sido mandado a: {}", notification.getRecipientEmail());
    }

}
