package com.sagant.prueba.service.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sagant.prueba.dto.NotificationRequest;
import com.sagant.prueba.dto.NotificationResponse;
import com.sagant.prueba.repository.*;
import com.sagant.prueba.mapper.*;
import com.sagant.prueba.service.*;
import com.sagant.prueba.model.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationDispatcherService dispatcherService;

    @Override
    public NotificationResponse createAndDispatch(NotificationRequest request) {

        Notification notification = notificationMapper.toEntity(request);
        Notification saved = notificationRepository.save(notification);
        dispatcherService.dispatch(saved);

        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional
    public void updateStatusToSent(Long notificationId) {

        if (!notificationRepository.findById(notificationId).isPresent()) {
            log.error("No se encontró la notificación con ID: {}", notificationId);
            return;
        }
        Notification notification = notificationRepository.findById(notificationId).get();
        notification.setStatus(NotificationStatus.SENT);
        notificationRepository.save(notification);
        log.info("Notificación: {} actualizada a SENT.");
    }

    @Override
    @Transactional
    public void updateStatusToFailed(Long notificationId, String errorMessage) {
        if (!notificationRepository.findById(notificationId).isPresent()) {
            log.error("No se encontró la notificación con ID: {}", notificationId);
            return;
        }

        Notification notification = notificationRepository.findById(notificationId).get();
        notification.setStatus(NotificationStatus.FAILED);
        notification.setErrorMessage(errorMessage);
        notification.setRetryCount(notification.getRetryCount() + 1);
        notificationRepository.save(notification);
        log.error("Notificación: {} actualizada a FAILED.", notificationId);
    }

}
