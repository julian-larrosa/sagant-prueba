package com.sagant.prueba.service.Impl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import com.sagant.prueba.service.*;
import com.sagant.prueba.model.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcherServiceImpl implements NotificationDispatcherService {

    private final NotificationService notificationService;
    private final LogNotificationSender logSender;
    private final EmailNotificationSender emailSender;

    @Value("${app.dispatch.max-retries:2}")
    private int maxRetries;

    @Value("${app.dispatch.retry-delay-ms:1000}")
    private long retryDelayMs;

    public void dispatch(Notification notification) {

        log.info("Iniciando despacho asíncrono para la notificación: {}", notification.getId());
        logSender.send(notification);
        if (notification.getChannel() == NotificationChannel.EMAIL) {
            dispatchWithRetry(notification, emailSender);
        } else {
            notificationService.updateStatusToSent(notification.getId());
        }
    }

    private void dispatchWithRetry(Notification notification, NotificationSender sender) {
        int attempt = 0;
        while (attempt <= maxRetries) {
            try {
                attempt++;
                log.info("[Intento {} de {} para ID: {}", attempt, maxRetries, notification.getId());
                sender.send(notification);
                notificationService.updateStatusToSent(notification.getId());
                return;
            } catch (Exception e) {
                log.warn("Intento {} falló para ID: {}. Error: {}", attempt, notification.getId(),
                        e.getMessage());
                if (attempt > maxRetries) {
                    log.error("Reintentos agotados para ID: {}. Queda como FAILED.",
                            notification.getId());
                    notificationService.updateStatusToFailed(notification.getId(), e.getMessage());
                } else {
                    waitBeforeRetry();
                }
            }
        }
    }

    private void waitBeforeRetry() {
        try {
            Thread.sleep(retryDelayMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
