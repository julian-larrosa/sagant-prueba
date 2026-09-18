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

        return notificationMapper.toResponse(saved);
    }

}
