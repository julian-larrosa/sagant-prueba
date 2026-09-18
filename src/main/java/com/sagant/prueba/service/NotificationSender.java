package com.sagant.prueba.service;

import com.sagant.prueba.model.Notification;

public interface NotificationSender {

    void send(Notification notification);
}
