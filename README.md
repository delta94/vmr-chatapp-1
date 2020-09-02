# VMR Chat

## 1. Tổng quan

__VMR Chat__ là ứng dụng chat realtime với tính năng cơ bản. Là project thuộc module-10 trong chương trình training fresher của Zalopay.

Ứng dụng có các tính năng sau:

- Đăng ký
- Đăng nhập - đăng xuất sử dụng JWT
- Xem danh sách người dùng trong hệ thống
- Chat 1 - 1
- Hiện trạng thái online của người dùng

Các công nghệ sử dụng:

- Java, Vertx framework để xây dựng logic ở backend
- Logging: Log4j2
- Cơ sở dữ liệu: MySQL
- Cache: Redis
- React, Redux để xây dựng giao diện (có sử dụng template)
- Sử dựng Websocket để hiện thực chức năng chat
- Sử dụng docker để deploy sản phẩm

## 2. Hướng dẫn chạy

Chạy lệnh docker compose (yêu cầu quyền root) tại thư mục `vmr-chatapp`. Cấu hình cần thiết để chạy ứng dụng nằm trong thư mục `vmr-docker`.

```bash
docker-compise up -d
```

## 3. Demo

### 3.1 Register

![Register](../vmr-chatapp/report/img/demo/register.png)

### 3.2 Login

![Login](../vmr-chatapp/report/img/demo/login.png)

### 3.3 Hiện danh sách user, chat 1-1

![Chat](../vmr-chatapp/report/img/demo/chat.png)

## 4. Thiết kế

- [API design](https://app.swaggerhub.com/apis-docs/anhvan1999/vmr-chat/1.0.0)
- [Sequence diagrams](report/doc/sequence-diagrams.md)
- [Database schema](report/doc/database-diagrams.md)
- [Cache design](report/doc/cache-design.md)
