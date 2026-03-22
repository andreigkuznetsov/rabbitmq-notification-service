package com.example.notificationservice.support;

import com.example.notificationservice.dto.NotificationMessage;
import com.example.notificationservice.entity.NotificationEntity;
import com.example.notificationservice.model.NotificationChannel;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.dto.CreateNotificationRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TestDataFactory {

    private static final String DEFAULT_USER_ID = "USR-77";
    private static final String DEFAULT_EMAIL = "user@example.com";
    private static final String DEFAULT_ORDER_ID = "ORD-555";
    private static final int DEFAULT_AMOUNT = 1200;

    private TestDataFactory() {
    }

    /**
     * Генерация уникального notificationId
     */
    public static String randomNotificationId() {
        return "NTF-" + UUID.randomUUID();
    }

    /**
     * Payload по умолчанию
     */
    public static Map<String, Object> defaultPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", DEFAULT_ORDER_ID);
        payload.put("amount", DEFAULT_AMOUNT);
        return payload;
    }

    /**
     * Payload с кастомными значениями
     */
    public static Map<String, Object> payload(String orderId, int amount) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("amount", amount);
        return payload;
    }

    /**
     * Тело запроса для контроллера (POST /notifications)
     */
    public static Map<String, Object> emailRequestBody(String notificationId, String templateCode) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("notificationId", notificationId);
        requestBody.put("userId", DEFAULT_USER_ID);
        requestBody.put("channel", "EMAIL");
        requestBody.put("recipient", DEFAULT_EMAIL);
        requestBody.put("templateCode", templateCode);
        requestBody.put("payload", defaultPayload());
        return requestBody;
    }

    /**
     * Тело запроса с кастомным payload
     */
    public static Map<String, Object> emailRequestBody(
            String notificationId,
            String templateCode,
            Map<String, Object> payload
    ) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("notificationId", notificationId);
        requestBody.put("userId", DEFAULT_USER_ID);
        requestBody.put("channel", "EMAIL");
        requestBody.put("recipient", DEFAULT_EMAIL);
        requestBody.put("templateCode", templateCode);
        requestBody.put("payload", payload);
        return requestBody;
    }

    /**
     * Entity со статусом QUEUED (для worker/integration тестов)
     */
    public static NotificationEntity queuedEmailEntity(String notificationId, String templateCode) {
        NotificationEntity entity = new NotificationEntity();
        entity.setNotificationId(notificationId);
        entity.setUserId(DEFAULT_USER_ID);
        entity.setChannel(NotificationChannel.EMAIL);
        entity.setRecipient(DEFAULT_EMAIL);
        entity.setTemplateCode(templateCode);
        entity.setPayloadJson("{\"orderId\":\"" + DEFAULT_ORDER_ID + "\",\"amount\":" + DEFAULT_AMOUNT + "}");
        entity.setStatus(NotificationStatus.QUEUED);
        entity.setRetryCount(0);
        entity.setErrorCode(null);
        entity.setErrorMessage(null);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    /**
     * Entity с кастомным payloadJson
     */
    public static NotificationEntity queuedEmailEntity(
            String notificationId,
            String templateCode,
            String payloadJson
    ) {
        NotificationEntity entity = new NotificationEntity();
        entity.setNotificationId(notificationId);
        entity.setUserId(DEFAULT_USER_ID);
        entity.setChannel(NotificationChannel.EMAIL);
        entity.setRecipient(DEFAULT_EMAIL);
        entity.setTemplateCode(templateCode);
        entity.setPayloadJson(payloadJson);
        entity.setStatus(NotificationStatus.QUEUED);
        entity.setRetryCount(0);
        entity.setErrorCode(null);
        entity.setErrorMessage(null);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    /**
     * Сообщение для RabbitMQ
     */
    public static NotificationMessage emailMessage(String notificationId, String templateCode) {
        NotificationMessage message = new NotificationMessage();
        message.setNotificationId(notificationId);
        message.setUserId(DEFAULT_USER_ID);
        message.setChannel(NotificationChannel.EMAIL);
        message.setRecipient(DEFAULT_EMAIL);
        message.setTemplateCode(templateCode);
        message.setPayload(defaultPayload());
        return message;
    }

    /**
     * Сообщение с кастомным payload
     */
    public static NotificationMessage emailMessage(
            String notificationId,
            String templateCode,
            Map<String, Object> payload
    ) {
        NotificationMessage message = new NotificationMessage();
        message.setNotificationId(notificationId);
        message.setUserId(DEFAULT_USER_ID);
        message.setChannel(NotificationChannel.EMAIL);
        message.setRecipient(DEFAULT_EMAIL);
        message.setTemplateCode(templateCode);
        message.setPayload(payload);
        return message;
    }

    public static CreateNotificationRequest emailRequest(String notificationId, String templateCode) {
        return emailRequest(notificationId, templateCode, defaultPayload());
    }

    public static CreateNotificationRequest emailRequest(
            String notificationId,
            String templateCode,
            Map<String, Object> payload
    ) {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setNotificationId(notificationId);
        request.setUserId(DEFAULT_USER_ID);
        request.setChannel(NotificationChannel.EMAIL);
        request.setRecipient(DEFAULT_EMAIL);
        request.setTemplateCode(templateCode);
        request.setPayload(payload);
        return request;
    }
}
