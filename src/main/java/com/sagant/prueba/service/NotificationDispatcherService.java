package com.sagant.prueba.service;

import com.sagant.prueba.model.Notification;

public interface NotificationDispatcherService {

    public void dispatch(Notification notification);
}
