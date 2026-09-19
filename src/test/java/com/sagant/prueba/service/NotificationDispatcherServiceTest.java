package com.sagant.prueba.service;

import com.sagant.prueba.model.Notification;
import com.sagant.prueba.model.NotificationChannel;
import com.sagant.prueba.model.NotificationStatus;
import com.sagant.prueba.repository.NotificationRepository;
import com.sagant.prueba.service.Impl.EmailNotificationSender;
import com.sagant.prueba.service.Impl.LogNotificationSender;
import com.sagant.prueba.service.Impl.NotificationDispatcherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationDispatcherServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private LogNotificationSender logSender;

    @Mock
    private EmailNotificationSender emailSender;

    private NotificationDispatcherServiceImpl dispatcherService;

    @BeforeEach
    void setUp() {
        dispatcherService = new NotificationDispatcherServiceImpl(notificationRepository, logSender, emailSender);
    }

    @Test
    @DisplayName("Debe despachar por canal LOG y actualizar estado")
    void dispatch_LogChannel_Success() {
        Notification notification = Notification.builder()
                .id(1L)
                .recipientEmail("admin@empresa.com")
                .channel(NotificationChannel.LOG)
                .subject("Test Log")
                .body("Mensaje Log")
                .status(NotificationStatus.PENDING)
                .build();

        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));

        dispatcherService.dispatch(notification);

        verify(logSender, times(1)).send(notification);
        verify(emailSender, never()).send(any());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    @DisplayName("Debe reintentar el canal EMAIL cuando falla y actualizar a FAILED")
    void dispatch_EmailChannel_Failure_ExhaustsRetries() {
        ReflectionTestUtils.setField(dispatcherService, "maxRetries", 1);
        ReflectionTestUtils.setField(dispatcherService, "retryDelayMs", 50L);

        Notification notification = Notification.builder()
                .id(2L)
                .recipientEmail("cliente@empresa.com")
                .channel(NotificationChannel.EMAIL)
                .subject("Test Fail")
                .body("Mensaje Fail")
                .status(NotificationStatus.PENDING)
                .build();

        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));
        doThrow(new RuntimeException("SMTP Connection Error")).when(emailSender).send(notification);

        dispatcherService.dispatch(notification);

        verify(emailSender, times(2)).send(notification);
        verify(notificationRepository, atLeastOnce()).save(notification);
    }
}