# VMR Chat

- [VMR Chat](#vmr-chat)
  - [1. Tổng quan](#1-tổng-quan)
  - [2. Hướng dẫn chạy](#2-hướng-dẫn-chạy)
  - [3. Demo](#3-demo)
    - [3.1 Register](#31-register)
    - [3.2 Login](#32-login)
    - [3.3 Hiện danh sách user, chat 1-1](#33-hiện-danh-sách-user-chat-1-1)
    - [3.4 Thêm bạn bè](#34-thêm-bạn-bè)
  - [4. Thiết kế](#4-thiết-kế)

## 1. Tổng quan

__VMR Chat__ là ứng dụng chat realtime với tính năng cơ bản. Là project thuộc module-10 trong chương trình training fresher của Zalopay.

Ứng dụng có các tính năng sau:

- Đăng ký
- Đăng nhập - đăng xuất sử dụng JWT
- Tìm kiếm bạn bè
- Kết bạn
- Chat 1 - 1
- Hiện trạng thái online của người dùng
- Chuyển tiền
- Xem số dư
- Hiện lịch sử giao dịch

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
docker-compose up -d
```

## 3. Demo

### 3.1 Register

![Register](report/img/demo/register.png)

### 3.2 Login

![Login](report/img/demo/login.png)

### 3.3 Hiện danh sách user, chat 1-1

![Chat](report/img/demo/chat-1-1.png)

### 3.4 Thêm bạn bè

![Add friend](report/img/demo/add-friend.png)

## 4. Thiết kế

- [Mockup](https://balsamiq.cloud/seo701z/pbade9k)
- [GRPC API Design](report/doc/grpc-design.md)
- [Database design](https://dbdiagram.io/d/5f709c5a7da1ea736e2f825f)
- [API design](https://app.swaggerhub.com/apis-docs/anhvan1999/vmr-chat/1.0.0)
- [Sequence diagrams](report/doc/sequence-diagrams.md)
- [Cache design](report/doc/cache-design.md)
