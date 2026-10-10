# RabbitMQ Simple Example

A simple multi-module Spring Boot project demonstrating how to send and receive messages using **RabbitMQ**. The project uses **Spring Boot 4.1.1**, **Spring AMQP 4.1.1**, **Java 25**, and **Jackson 3** for JSON message conversion.

---

## Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [How to Run](#how-to-run)
- [REST API](#rest-api)
- [Configuration](#configuration)
- [How It Works](#how-it-works)
- [Troubleshooting](#troubleshooting)
- [License](#license)

---

## Overview

This project is a minimal working example of a **producer–consumer** architecture using RabbitMQ as the message broker. It demonstrates:

- Defining **multiple queues** bound to a **single Topic Exchange**
- Sending different types of messages to different queues using **routing keys**
- Using **Jackson 3** (`JacksonJsonMessageConverter`) for JSON serialization, compatible with Spring Boot 4.x
- Running RabbitMQ locally via **Docker Compose**
- A clean separation between the **producer** and **consumer** Spring Boot applications

---

## Project Structure

```text
rabbitMQ-simple-example/
├── docker-compose.yml
├── pom.xml                      (parent POM)
├── producer/
│   ├── pom.xml
│   └── src/main/java/com/example/producer/
│       ├── ProducerApplication.java
│       ├── config/RabbitMQConfig.java
│       ├── controller/CustomDataController.java
│       ├── model/CustomData.java
│       └── services/CustomDataService.java
└── consumer/
    ├── pom.xml
    └── src/main/java/com/example/consumer/
        ├── ConsumerApplication.java
        ├── config/RabbitMQConfig.java
        └── services/MessageConsumer.java
```

---

## Technologies Used

| Technology | Version |
|------------|---------|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Spring AMQP | 4.1.1 |
| RabbitMQ | (via Docker) |
| Jackson | 3.1.5 |
| Lombok | 1.18.46 |
| Maven | 3.9+ |

---

## Architecture

```text
┌────────────────┐        ┌──────────────┐        ┌────────────────┐
│   Producer     │  ───▶  │   RabbitMQ   │  ───▶  │    Consumer    │
│ (port 8081)    │        │   Broker     │        │  (port 8082)   │
└────────────────┘        └──────────────┘        └────────────────┘
        │                        │                        │
        │  Publish to Exchange   │   Bind Queues          │
        │  with Routing Key      │   to Exchange          │
        ▼                        ▼                        ▼
   ┌─────────────┐        ┌─────────────┐        ┌─────────────────┐
   │ TopicEx.    │───────▶│ Queue #1    │───────▶│ @RabbitListener │
   │ app.exchange│───────▶│ Queue #2    │───────▶│ @RabbitListener │
   └─────────────┘        └─────────────┘        └─────────────────┘
```

- **One Topic Exchange** (`app.exchange`)
- **Two Queues**: one for plain messages, one for `CustomData` objects
- **Two Routing Keys**: `message.routing.key` and `custom.data.routing.key`

This pattern (one exchange + multiple queues) is simple, flexible, and easy to extend. You can add more queues without creating new exchanges.

---

## Prerequisites

Before running the project, make sure you have:

- **Java 25** installed
- **Maven 3.9+**
- **Docker** and **Docker Compose** installed
- An IDE (IntelliJ IDEA recommended)

---

## How to Run

### 1. Start RabbitMQ

From the project root, run:

```bash
docker-compose up -d
```

This starts RabbitMQ with the management plugin enabled.

- **RabbitMQ Management UI:** http://localhost:15672
- **Default credentials:** `guest` / `guest`

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the Consumer

```bash
cd consumer
mvn spring-boot:run
```

The consumer starts on **port 8082** and begins listening to the queues.

### 4. Run the Producer

In a new terminal:

```bash
cd producer
mvn spring-boot:run
```

The producer starts on **port 8081**.

---

## REST API

The producer exposes two endpoints:

### 1. Send a plain message

```http
GET /api/custom-data/message?message=Hello
```

Example:

```bash
curl "http://localhost:8081/api/custom-data/message?message=Hello%20RabbitMQ"
```

**Response:**

```text
Message sent successfully: Hello RabbitMQ
```

### 2. Send a CustomData object

```http
POST /api/custom-data
Content-Type: application/json
```

**Request body:**

```json
{
  "id": 1,
  "name": "Sample Data",
  "description": "This is a test message"
}
```

Example:

```bash
curl -X POST http://localhost:8081/api/custom-data \
  -H "Content-Type: application/json" \
  -d "{\"id\":1,\"name\":\"Sample Data\",\"description\":\"Test\"}"
```

**Response:**

```text
Custom data sent successfully
```

---

## Configuration

### Producer — `application.properties`

```properties
server.port=8081
spring.application.name=producer

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

rabbitmq.exchange.name=app.exchange
rabbitmq.queue.name-message=rabbitmqMessageQueue
rabbitmq.queue.name-custom-data=rabbitmqCustomDataQueue
rabbitmq.routing-key.message=message.routing.key
rabbitmq.routing-key.custom-data=custom.data.routing.key
```

### Consumer — `application.properties`

```properties
server.port=8082
spring.application.name=consumer

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

rabbitmq.queue.name-message=rabbitmqMessageQueue
rabbitmq.queue.name-custom-data=rabbitmqCustomDataQueue
```

---

## How It Works

### Producer side

1. A REST request arrives at `CustomDataController`.
2. The controller delegates to `CustomDataService`.
3. The service uses `RabbitTemplate` to publish the message to `app.exchange` with the appropriate **routing key**.
4. `RabbitMQConfig` declares the exchange, queues, and bindings. `RabbitAdmin` ensures they are created on startup.

### Consumer side

1. `MessageConsumer` uses `@RabbitListener` on each queue.
2. When a message arrives, Spring AMQP deserializes it using `JacksonJsonMessageConverter`.
3. The listener logs the message.

### Why `JacksonJsonMessageConverter` (not `Jackson2JsonMessageConverter`)?

Spring Boot 4.x uses **Jackson 3**, where the base package changed from `com.fasterxml.jackson.databind` to `tools.jackson.databind`. The old `Jackson2JsonMessageConverter` still references the old package and throws `NoClassDefFoundError` at startup. The new `JacksonJsonMessageConverter` is the correct choice for Jackson 3.

---

## Troubleshooting

### Error: `NoClassDefFoundError: com/fasterxml/jackson/databind/json/JsonMapper`

You are using `Jackson2JsonMessageConverter` with Jackson 3 on the classpath. Replace it with:

```java
@Bean
public MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter();
}
```

### Error: `Ambiguous mapping. Cannot map ... method`

Two controller methods are mapped to the same URL and HTTP method. Make sure each `@GetMapping` / `@PostMapping` has a unique path or a different HTTP verb.

### Consumer does not receive messages

- Check that RabbitMQ is running: `docker ps`
- Open the management UI at http://localhost:15672 and verify the queues and exchange exist.
- Make sure the consumer is running **before** the producer sends messages, or ensure the queues are declared on both sides.

### Connection refused

Make sure RabbitMQ is running on `localhost:5672` and that you have run `docker-compose up -d`.

---

## License

This project is open source and available under the [MIT License](LICENSE).

---

---

# نمونه ساده RabbitMQ

یک پروژهی چندماژولی ساده با Spring Boot که نحوهی ارسال و دریافت پیام با **RabbitMQ** را نشان میدهد. این پروژه از **Spring Boot 4.1.1**، **Spring AMQP 4.1.1**، **Java 25** و **Jackson 3** برای تبدیل پیامهای JSON استفاده میکند.

---

## فهرست مطالب

- [معرفی](#معرفی)
- [ساختار پروژه](#ساختار-پروژه)
- [تکنولوژیهای استفادهشده](#تکنولوژیهای-استفادهشده)
- [معماری](#معماری)
- [پیشنیازها](#پیشنیازها)
- [نحوهی اجرا](#نحوهی-اجرا)
- [APIهای REST](#apiهای-rest)
- [تنظیمات](#تنظیمات)
- [نحوهی کار](#نحوهی-کار)
- [رفع اشکال](#رفع-اشکال)
- [مجوز](#مجوز)

---

## معرفی

این پروژه یک نمونهی حداقلی و کاربردی از معماری **Producer–Consumer** با استفاده از RabbitMQ به عنوان Message Broker است. موارد زیر را نشان میدهد:

- تعریف **چند صف (Queue)** متصل به **یک Topic Exchange**
- ارسال انواع مختلف پیامها به صفهای مختلف با استفاده از **Routing Key**
- استفاده از **Jackson 3** (به وسیلهی `JacksonJsonMessageConverter`) برای سریالیزه/دیسریالیزه کردن JSON، سازگار با Spring Boot 4.x
- اجرای RabbitMQ به صورت محلی با **Docker Compose**
- جداسازی تمیز بین اپلیکیشنهای **producer** و **consumer**

---

## ساختار پروژه

```text
rabbitMQ-simple-example/
├── docker-compose.yml
├── pom.xml                      (POM والد)
├── producer/
│   ├── pom.xml
│   └── src/main/java/com/example/producer/
│       ├── ProducerApplication.java
│       ├── config/RabbitMQConfig.java
│       ├── controller/CustomDataController.java
│       ├── model/CustomData.java
│       └── services/CustomDataService.java
└── consumer/
    ├── pom.xml
    └── src/main/java/com/example/consumer/
        ├── ConsumerApplication.java
        ├── config/RabbitMQConfig.java
        └── services/MessageConsumer.java
```

---

## تکنولوژیهای استفادهشده

| تکنولوژی | نسخه |
|----------|------|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Spring AMQP | 4.1.1 |
| RabbitMQ | (با Docker) |
| Jackson | 3.1.5 |
| Lombok | 1.18.46 |
| Maven | 3.9+ |

---

## معماری

```text
┌────────────────┐        ┌──────────────┐        ┌────────────────┐
│   Producer     │  ───▶  │   RabbitMQ   │  ───▶  │    Consumer    │
│  (پورت 8081)   │        │   Broker     │        │  (پورت 8082)   │
└────────────────┘        └──────────────┘        └────────────────┘
        │                        │                        │
        │  انتشار به Exchange    │   اتصال صفها به        │
        │  با Routing Key        │   Exchange             │
        ▼                        ▼                        ▼
   ┌─────────────┐        ┌─────────────┐        ┌─────────────────┐
   │ TopicEx.    │───────▶│ صف #1       │───────▶│ @RabbitListener │
   │ app.exchange│───────▶│ صف #2       │───────▶│ @RabbitListener │
   └─────────────┘        └─────────────┘        └─────────────────┘
```

- **یک Topic Exchange** با نام `app.exchange`
- **دو صف**: یکی برای پیامهای ساده و دیگری برای آبجکتهای `CustomData`
- **دو Routing Key**: `message.routing.key` و `custom.data.routing.key`

این الگو (یک Exchange به همراه چند صف) ساده، منعطف و قابل توسعه است. برای اضافه کردن صف جدید، فقط یک Queue و یک Binding جدید اضافه میکنید و نیازی به ساخت Exchange جدید نیست.

---

## پیشنیازها

قبل از اجرا مطمئن شوید موارد زیر نصب هستند:

- **Java 25**
- **Maven 3.9+**
- **Docker** و **Docker Compose**
- یک IDE (پیشنهاد: IntelliJ IDEA)

---

## نحوهی اجرا

### ۱. راهاندازی RabbitMQ

از ریشهی پروژه اجرا کنید:

```bash
docker-compose up -d
```

این دستور RabbitMQ را با پلاگین مدیریت راهاندازی میکند.

- **پنل مدیریت RabbitMQ:** http://localhost:15672
- **نام کاربری/رمز پیشفرض:** `guest` / `guest`

### ۲. ساخت پروژه

```bash
mvn clean install
```

### ۳. اجرای Consumer

```bash
cd consumer
mvn spring-boot:run
```

Consumer روی **پورت 8082** بالا میآید و شروع به گوش دادن به صفها میکند.

### ۴. اجرای Producer

در ترمینال جدید:

```bash
cd producer
mvn spring-boot:run
```

Producer روی **پورت 8081** بالا میآید.

---

## APIهای REST

Producer دو Endpoint دارد:

### ۱. ارسال پیام ساده

```http
GET /api/custom-data/message?message=Hello
```

مثال:

```bash
curl "http://localhost:8081/api/custom-data/message?message=Hello%20RabbitMQ"
```

**پاسخ:**

```text
Message sent successfully: Hello RabbitMQ
```

### ۲. ارسال آبجکت CustomData

```http
POST /api/custom-data
Content-Type: application/json
```

**بدنهی درخواست:**

```json
{
  "id": 1,
  "name": "Sample Data",
  "description": "This is a test message"
}
```

مثال:

```bash
curl -X POST http://localhost:8081/api/custom-data \
  -H "Content-Type: application/json" \
  -d "{\"id\":1,\"name\":\"Sample Data\",\"description\":\"Test\"}"
```

**پاسخ:**

```text
Custom data sent successfully
```

---

## تنظیمات

### Producer — `application.properties`

```properties
server.port=8081
spring.application.name=producer

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

rabbitmq.exchange.name=app.exchange
rabbitmq.queue.name-message=rabbitmqMessageQueue
rabbitmq.queue.name-custom-data=rabbitmqCustomDataQueue
rabbitmq.routing-key.message=message.routing.key
rabbitmq.routing-key.custom-data=custom.data.routing.key
```

### Consumer — `application.properties`

```properties
server.port=8082
spring.application.name=consumer

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

rabbitmq.queue.name-message=rabbitmqMessageQueue
rabbitmq.queue.name-custom-data=rabbitmqCustomDataQueue
```

---

## نحوهی کار

### سمت Producer

۱. یک درخواست REST به `CustomDataController` میرسد.
۲. کنترلر کار را به `CustomDataService` میسپارد.
۳. سرویس با استفاده از `RabbitTemplate` پیام را به `app.exchange` با **Routing Key** مناسب منتشر میکند.
۴. `RabbitMQConfig` صفها، Exchange و Bindingها را تعریف میکند. `RabbitAdmin` هنگام راهاندازی اطمینان میدهد که همه ایجاد شوند.

### سمت Consumer

۱. `MessageConsumer` با `@RabbitListener` به هر صف گوش میدهد.
۲. وقتی پیامی میرسد، Spring AMQP آن را با `JacksonJsonMessageConverter` دیسریالیزه میکند.
۳. Listener پیام را در لاگ ثبت میکند.

### چرا `JacksonJsonMessageConverter` و نه `Jackson2JsonMessageConverter`؟

Spring Boot 4.x از **Jackson 3** استفاده میکند که پکیج پایهی آن از `com.fasterxml.jackson.databind` به `tools.jackson.databind` تغییر کرده است. کانورتر قدیمی `Jackson2JsonMessageConverter` هنوز به پکیج قدیمی ارجاع میدهد و هنگام راهاندازی خطای `NoClassDefFoundError` میدهد. کانورتر جدید `JacksonJsonMessageConverter` انتخاب درست برای Jackson 3 است.

---

## رفع اشکال

### خطا: `NoClassDefFoundError: com/fasterxml/jackson/databind/json/JsonMapper`

شما از `Jackson2JsonMessageConverter` همراه با Jackson 3 روی classpath استفاده میکنید. آن را به این شکل تغییر دهید:

```java
@Bean
public MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter();
}
```

### خطا: `Ambiguous mapping. Cannot map ... method`

دو متد کنترلر به یک URL و متد HTTP یکسان نگاشت شدهاند. مطمئن شوید هر `@GetMapping` / `@PostMapping` مسیر یکتا یا متد HTTP متفاوتی دارد.

### Consumer پیامی دریافت نمیکند

- بررسی کنید که RabbitMQ در حال اجرا باشد: `docker ps`
- پنل مدیریت را در http://localhost:15672 باز کنید و وجود صفها و Exchange را بررسی کنید.
- مطمئن شوید Consumer **قبل از** ارسال پیام توسط Producer اجرا شده، یا اینکه صفها در هر دو طرف تعریف شده باشند.

### اتصال برقرار نمیشود (Connection refused)

مطمئن شوید RabbitMQ روی `localhost:5672` در حال اجراست و دستور `docker-compose up -d` را اجرا کردهاید.

---

## مجوز

این پروژه به صورت open source و تحت [مجوز MIT](LICENSE) منتشر شده است.