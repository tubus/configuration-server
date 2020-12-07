package com.github.tubus.ui.util;

import com.github.tubus.ui.data.exception.InternalConfigurationServerException;
import com.vaadin.flow.component.notification.Notification;
import lombok.extern.slf4j.Slf4j;


/**
 * Utils for vaadin notification
 */
@Slf4j
public final class NotificationUtils {

    private NotificationUtils() {
    }

    /**
     * Execute method throwing InternalConfigurationServerException
     * and show exception details in notification
     * @param method - Runnable method
     */
    public static void tryWithNotification(Runnable method) {
        try {
            method.run();
        } catch (InternalConfigurationServerException exception) {
            exception.getDetails().forEach(detail -> {
                Notification.show(detail, 1500, Notification.Position.MIDDLE);
            });
        } catch (Exception exception) {
            log.error("Unrecognized error", exception);
        }
    }
}