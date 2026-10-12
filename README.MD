# RabbitMQ Simple Example (Spring Cloud Stream)

A minimal Spring Boot project demonstrating asynchronous messaging between two services using **Spring Cloud Stream** and **RabbitMQ** as the message broker.

## Overview

This repository contains two independent Spring Boot applications:

- **Producer** – Sends messages to a RabbitMQ exchange via Spring Cloud Stream.
- **Consumer** – Listens for messages from the same exchange and processes them.

Spring Cloud Stream abstracts the messaging middleware, so the application code focuses on the business logic rather than the underlying RabbitMQ APIs. The `spring-cloud-stream-binder-rabbit` dependency connects the framework to the broker.

## Prerequisites

- Java 25 or later
- Maven 3.9+
- Docker and Docker Compose (for running RabbitMQ)

## Getting Started

### 1. Start RabbitMQ

A `docker-compose.yaml` file is provided to spin up a RabbitMQ instance with the management plugin enabled.

```bash
docker compose up -d
```

The management UI will be available at http://localhost:15672 (default credentials: `guest` / `guest`).

### 2. Run the Producer

Navigate to the `producer` directory and start the application:

```bash
cd producer
./mvnw spring-boot:run
```

The producer will expose a simple endpoint or automatically send messages on startup (depending on the implementation). By default, it publishes to a Spring Cloud Stream output binding.

### 3. Run the Consumer

In a separate terminal, navigate to the `consumer` directory and start the application:

```bash
cd consumer
./mvnw spring-boot:run
```

The consumer listens on the corresponding input binding. Received messages are logged to the console (or handled by a custom `Consumer` bean).

## Configuration

Both applications use Spring Cloud Stream’s default binding names (`output` for the producer, `input` for the consumer) unless overridden in `application.yml` or `application.properties`. The RabbitMQ connection details default to `localhost:5672` with the `guest` user.

You can customize the broker address, credentials, exchange, and queue names by adding the appropriate properties:

```yaml
spring:
rabbitmq:
host: localhost
port: 5672
username: guest
password: guest
```

## Project Structure

```
rabbitMQ-simple-example/
├── docker-compose.yaml          # RabbitMQ service
├── consumer/                    # Spring Boot consumer app
│   ├── src/
│   └── pom.xml
├── producer/                    # Spring Boot producer app
│   ├── src/
│   └── pom.xml
└── README.MD
```

## License

This project is provided for educational purposes. Feel free to use it as a starting point for your own Spring Cloud Stream experiments.

---

# مثال ساده RabbitMQ (Spring Cloud Stream)

یک پروژه مینیمال Spring Boot که ارسال پیام ناهمزمان بین دو سرویس را با استفاده از **Spring Cloud Stream** و **RabbitMQ** به عنوان کارگزار پیام نشان میدهد.

## نمای کلی

این مخزن شامل دو اپلیکیشن مستقل Spring Boot است:

- **Producer (تولیدکننده)** – پیامها را از طریق Spring Cloud Stream به یک صرافی RabbitMQ ارسال میکند.
- **Consumer (مصرفکننده)** – به پیامهای همان صرافی گوش میدهد و آنها را پردازش میکند.

Spring Cloud Stream لایهی واسط پیامرسانی را انتزاع میکند، بنابراین کد اپلیکیشن روی منطق کسبوکار تمرکز دارد نه روی APIهای زیرین RabbitMQ. وابستگی `spring-cloud-stream-binder-rabbit` فریمورک را به کارگزار متصل میکند.

## پیشنیازها

- جاوا 25 یا بالاتر
- Maven 3.9+
- Docker و Docker Compose (برای اجرای RabbitMQ)

## شروع به کار

### ۱. راهاندازی RabbitMQ

فایل `docker-compose.yaml` برای راهاندازی یک نمونه RabbitMQ با پلاگین مدیریت فراهم شده است.

```bash
docker compose up -d
```

رابط مدیریتی در آدرس http://localhost:15672 در دسترس خواهد بود (نام کاربری و رمز عبور پیشفرض: `guest` / `guest`).

### ۲. اجرای Producer

به پوشه `producer` بروید و اپلیکیشن را اجرا کنید:

```bash
cd producer
./mvnw spring-boot:run
```

Producer بسته به پیادهسازی، یک endpoint ساده ارائه میدهد یا هنگام راهاندازی پیامهایی ارسال میکند. به طور پیشفرض، پیامها به یک اتصال خروجی (output binding) Spring Cloud Stream منتشر میشوند.

### ۳. اجرای Consumer

در یک ترمینال جداگانه، به پوشه `consumer` بروید و اپلیکیشن را اجرا کنید:

```bash
cd consumer
./mvnw spring-boot:run
```

Consumer به اتصال ورودی (input binding) متناظر گوش میدهد. پیامهای دریافتی در کنسول لاگ میشوند (یا توسط یک bean سفارشی `Consumer` پردازش میشوند).

## تنظیمات

هر دو اپلیکیشن از نامهای پیشفرض اتصال Spring Cloud Stream استفاده میکنند (`output` برای Producer و `input` برای Consumer) مگر اینکه در `application.yml` یا `application.properties` بازنویسی شوند. جزئیات اتصال RabbitMQ به طور پیشفرض روی `localhost:5672` با کاربر `guest` تنظیم شده است.

میتوانید آدرس کارگزار، اعتبارنامهها، نام صرافی و صف را با افزودن خواص مناسب سفارشی کنید:

```yaml
spring:
rabbitmq:
host: localhost
port: 5672
username: guest
password: guest
```

## ساختار پروژه

```
rabbitMQ-simple-example/
├── docker-compose.yaml          # سرویس RabbitMQ
├── consumer/                    # اپلیکیشن مصرفکننده Spring Boot
│   ├── src/
│   └── pom.xml
├── producer/                    # اپلیکیشن تولیدکننده Spring Boot
│   ├── src/
│   └── pom.xml
└── README.MD
```

## مجوز

این پروژه برای اهداف آموزشی ارائه شده است. استفاده از آن به عنوان نقطه شروع برای آزمایشهای Spring Cloud Stream خودتان آزاد است.