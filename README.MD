# RabbitMQ Simple Example (Frequency with Spring Cloud Stream)

A minimal Spring Boot project demonstrating asynchronous messaging between two services using **Spring Cloud Stream** and **RabbitMQ** as the message broker, with a focus on controlling the **frequency** of message production and consumption.

## Overview

This repository contains two independent Spring Boot applications:

- **Producer** – Sends messages to a RabbitMQ exchange via Spring Cloud Stream at a configurable frequency.
- **Consumer** – Listens for messages from the same exchange and processes them at its own pace.

Spring Cloud Stream abstracts the messaging middleware, so the application code focuses on the business logic rather than the underlying RabbitMQ APIs. The `spring-cloud-stream-binder-rabbit` dependency connects the framework to the broker. This branch explores how the producer's message emission rate can be tuned using Spring Cloud Stream's poller configuration.

## Prerequisites

- Java 17 or later
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

The producer uses a functional `Supplier` bean to publish messages to a Spring Cloud Stream output binding. The frequency at which messages are generated is controlled by Spring Cloud Stream's poller mechanism. By default, the poller invokes the `Supplier` every 5 seconds. You can override this using the `spring.cloud.stream.poller.fixed-delay` property.

### 3. Run the Consumer

In a separate terminal, navigate to the `consumer` directory and start the application:

```bash
cd consumer
./mvnw spring-boot:run
```

The consumer listens on the corresponding input binding. Received messages are logged to the console or handled by a custom `Consumer` bean. The consumer processes messages as they arrive; if the producer emits faster than the consumer can process, messages will accumulate in the RabbitMQ queue.

## Configuration

Both applications use Spring Cloud Stream's default binding names (`output` for the producer, `input` for the consumer) unless overridden in `application.yml` or `application.properties`. The RabbitMQ connection details default to `localhost:5672` with the `guest` user.

### RabbitMQ Connection

```yaml
spring:
rabbitmq:
host: localhost
port: 5672
username: guest
password: guest
```

### Message Frequency Control

The producer's message emission rate is controlled by the Spring Cloud Stream poller. The default poller interval is 5000 milliseconds (5 seconds). To change it:

```yaml
spring:
cloud:
stream:
poller:
fixed-delay: 2000   # Emit a message every 2 seconds
```

You can also target a specific binding:

```yaml
spring:
cloud:
stream:
bindings:
supply-out-0:
producer:
poller:
fixed-delay: 1000
```

The consumer side can also be configured with concurrency settings to handle higher message volumes:

```yaml
spring:
cloud:
stream:
bindings:
consume-in-0:
consumer:
concurrency: 3
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

## Key Concepts Demonstrated

- **Poller-Based Frequency Control**: Using `spring.cloud.stream.poller.fixed-delay` to control how often the producer's `Supplier` is invoked.
- **Functional Programming Model**: Writing the producer as a `Supplier` and the consumer as a `Consumer` bean.
- **Backpressure Observation**: Watching queue depth grow in the RabbitMQ management UI when the producer outpaces the consumer.
- **Binding-Level Configuration**: Applying poller and consumer settings to specific bindings rather than globally.

## License

This project is provided for educational purposes. Feel free to use it as a starting point for your own Spring Cloud Stream experiments.

---

# مثال ساده RabbitMQ (فرکانس با Spring Cloud Stream)

یک پروژه مینیمال Spring Boot که ارسال پیام ناهمزمان بین دو سرویس را با استفاده از **Spring Cloud Stream** و **RabbitMQ** به عنوان کارگزار پیام نشان میدهد، با تمرکز بر کنترل **فرکانس** تولید و مصرف پیام.

## نمای کلی

این مخزن شامل دو اپلیکیشن مستقل Spring Boot است:

- **Producer (تولیدکننده)** – پیامها را با فرکانس قابل تنظیم از طریق Spring Cloud Stream به یک صرافی RabbitMQ ارسال میکند.
- **Consumer (مصرفکننده)** – به پیامهای همان صرافی گوش میدهد و آنها را با سرعت خودش پردازش میکند.

Spring Cloud Stream لایهی واسط پیامرسانی را انتزاع میکند، بنابراین کد اپلیکیشن روی منطق کسبوکار تمرکز دارد نه روی APIهای زیرین RabbitMQ. وابستگی `spring-cloud-stream-binder-rabbit` فریمورک را به کارگزار متصل میکند. این شاخه نحوه تنظیم نرخ ارسال پیام تولیدکننده را با استفاده از تنظیمات poller در Spring Cloud Stream بررسی میکند.

## پیشنیازها

- جاوا ۱۷ یا بالاتر
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

Producer از یک bean تابعی `Supplier` برای انتشار پیامها به یک اتصال خروجی Spring Cloud Stream استفاده میکند. فرکانسی که پیامها تولید میشوند توسط مکانیزم poller در Spring Cloud Stream کنترل میشود. به طور پیشفرض، poller هر ۵ ثانیه یکبار `Supplier` را فراخوانی میکند. میتوانید این مقدار را با خاصیت `spring.cloud.stream.poller.fixed-delay` بازنویسی کنید.

### ۳. اجرای Consumer

در یک ترمینال جداگانه، به پوشه `consumer` بروید و اپلیکیشن را اجرا کنید:

```bash
cd consumer
./mvnw spring-boot:run
```

Consumer به اتصال ورودی متناظر گوش میدهد. پیامهای دریافتی در کنسول لاگ میشوند یا توسط یک bean سفارشی `Consumer` پردازش میشوند. Consumer پیامها را به محض دریافت پردازش میکند؛ اگر Producer سریعتر از Consumer پیام ارسال کند، پیامها در صف RabbitMQ انباشته میشوند.

## تنظیمات

هر دو اپلیکیشن از نامهای پیشفرض اتصال Spring Cloud Stream استفاده میکنند (`output` برای Producer و `input` برای Consumer) مگر اینکه در `application.yml` یا `application.properties` بازنویسی شوند. جزئیات اتصال RabbitMQ به طور پیشفرض روی `localhost:5672` با کاربر `guest` تنظیم شده است.

### اتصال RabbitMQ

```yaml
spring:
rabbitmq:
host: localhost
port: 5672
username: guest
password: guest
```

### کنترل فرکانس پیام

نرخ انتشار پیام Producer توسط poller در Spring Cloud Stream کنترل میشود. بازه پیشفرض poller ۵۰۰۰ میلیثانیه (۵ ثانیه) است. برای تغییر آن:

```yaml
spring:
cloud:
stream:
poller:
fixed-delay: 2000   # ارسال یک پیام هر ۲ ثانیه
```

همچنین میتوانید یک اتصال خاص را هدف قرار دهید:

```yaml
spring:
cloud:
stream:
bindings:
supply-out-0:
producer:
poller:
fixed-delay: 1000
```

سمت Consumer نیز میتواند با تنظیمات همزمانی برای مدیریت حجم بالاتر پیامها پیکربندی شود:

```yaml
spring:
cloud:
stream:
bindings:
consume-in-0:
consumer:
concurrency: 3
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

## مفاهیم کلیدی نشان داده شده

- **کنترل فرکانس مبتنی بر Poller**: استفاده از `spring.cloud.stream.poller.fixed-delay` برای کنترل اینکه هر چند وقت یکبار `Supplier` تولیدکننده فراخوانی شود.
- **مدل برنامهنویسی تابعی**: نوشتن Producer به صورت `Supplier` و Consumer به صورت bean `Consumer`.
- **مشاهده فشار معکوس**: تماشای رشد عمق صف در رابط مدیریتی RabbitMQ زمانی که Producer از Consumer جلو میزند.
- **پیکربندی در سطح اتصال**: اعمال تنظیمات poller و Consumer به اتصالات خاص به جای تنظیمات سراسری.

## مجوز

این پروژه برای اهداف آموزشی ارائه شده است. استفاده از آن به عنوان نقطه شروع برای آزمایشهای Spring Cloud Stream خودتان آزاد است.