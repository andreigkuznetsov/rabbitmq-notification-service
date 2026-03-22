# 📩 RabbitMQ Notification Service

Микросервис для отправки уведомлений с использованием RabbitMQ.  
Проект демонстрирует построение event-driven сервиса с валидацией входных данных, обработкой сообщений и покрытием тестами.

---

## 🚀 Основные возможности

- 📬 Отправка уведомлений через RabbitMQ
- ✅ Валидация входящих запросов
- 📧 Поддержка каналов уведомлений (MVP: EMAIL)
- ❌ Ограничение неподдерживаемых каналов (например, SMS)
- 🧪 Покрытие unit-тестами
- 🐳 Запуск через Docker Compose

---

## 🏗 Архитектура

Проект реализован как классический Spring Boot сервис:

```text
Controller → Service → Validation → Message Broker (RabbitMQ)
```

---

### Основные компоненты:

- **NotificationController** — принимает запросы
- **NotificationService** — бизнес-логика отправки
- **NotificationValidationService** — валидация входных данных
- **RabbitMQ Producer** — публикация сообщений
- **DTO / Model** — структура данных

---

## 📦 Технологии

- Java 21
- Spring Boot
- RabbitMQ
- Gradle
- JUnit 5
- AssertJ
- Docker / Docker Compose
- Spring Data JPA
- PostgreSQL 
- Lombok

---

## ⚙️ Запуск проекта

### 1. Клонирование

```bash
git clone https://github.com/andreigkuznetsov/rabbitmq-notification-service.git
cd rabbitmq-notification-service
```

---

### 2. Запуск RabbitMQ

```bash
docker-compose up -d
```

---

RabbitMQ будет доступен:

- UI: http://localhost:15672
- login: guest / pass: guest

---

### 3. Запуск приложения

```bash
./gradlew bootRun
```

---

## 📡 API

### Создание уведомления

```text
POST /notifications
```

### Пример запроса:

```bash
{
  "notificationId": "NTF-1001",
  "userId": "USR-77",
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "templateCode": "ORDER_CREATED",
  "payload": {
    "orderId": "ORD-1"
  }
}
```

---

## 🧠 Валидация

Сервис проверяет:

- наличие обязательных полей
- корректность канала уведомлений
- формат получателя (для EMAIL)

Пример ограничения MVP:

- ❌ SMS канал запрещён

---

## 🧪 Тестирование

Проект содержит unit-тесты для проверки бизнес-логики:

### Примеры сценариев:
- ✅ Разрешён EMAIL канал
- ❌ Запрещён SMS канал
- ❌ Ошибки при некорректных данных

Технологии:

- JUnit 5
- AssertJ
- Mockito (моки зависимостей)

Подход:

- unit-тесты изолируют бизнес-логику
- внешние зависимости заменяются mock-объектами
- проверяются как позитивные, так и негативные сценарии

Запуск тестов:

```bash
./gradlew test
```

---

## 🔄 Поток обработки
1. Клиент отправляет HTTP запрос
2. Происходит валидация
3. Создаётся сообщение
4. Сообщение отправляется в RabbitMQ
5. Далее может быть обработано consumer-ом

---

## 📈 Что демонстрирует проект

Этот проект показывает:

- работу с message broker (RabbitMQ)
- построение event-driven архитектуры
- реализацию валидации бизнес-данных
- написание unit-тестов
- базовую интеграцию с инфраструктурой (Docker)

---


## 🧑‍💻 Автор

Андрей Кузнецов — QA / AQA инженер
Опыт: manual + automation testing (Java)

---

## 📌 Планы по развитию
 - Добавить consumer сервис
 - Поддержка SMS / PUSH каналов
 - Интеграционные тесты
 - Retry / DLQ механизмы
 - Observability (Prometheus + Grafana)
