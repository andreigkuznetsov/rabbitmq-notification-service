package com.example.notificationservice.repository;

import com.example.notificationservice.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Optional<NotificationEntity> findByNotificationId(String notificationId);

    boolean existsByNotificationId(String notificationId);
}
