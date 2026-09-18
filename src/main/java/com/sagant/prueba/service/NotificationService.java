package com.sagant.prueba.service;

import com.sagant.prueba.dto.*;

public interface NotificationService {

    NotificationResponse createAndDispatch(NotificationRequest request);

    void updateStatusToSent(Long notificationId);

    void updateStatusToFailed(Long notificationId, String errorMessage);
}
