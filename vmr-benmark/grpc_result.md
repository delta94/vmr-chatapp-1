# Kết quả benmark các GRPC service

- [Kết quả benmark các GRPC service](#kết-quả-benmark-các-grpc-service)
  - [1. Transfer](#1-transfer)
    - [1.1 Điều kiện test](#11-điều-kiện-test)
    - [1.2 Kết quả test](#12-kết-quả-test)
    - [1.3 Nhận xét](#13-nhận-xét)
  - [2. GetBalance](#2-getbalance)
    - [2.1 Điều kiện test](#21-điều-kiện-test)
    - [2.2 Kết quả test](#22-kết-quả-test)
  - [3. GetHistory](#3-gethistory)
    - [3.1 Điều kiện test](#31-điều-kiện-test)
    - [3.2 Kết quả test](#32-kết-quả-test)
  - [4. GetChatFriendList](#4-getchatfriendlist)
    - [4.1 Điều kiện test](#41-điều-kiện-test)
    - [4.2 Kết quả test](#42-kết-quả-test)
  - [5. GetFriendList](#5-getfriendlist)
    - [5.1 Điều kiện test](#51-điều-kiện-test)
    - [5.2 Kết quả test](#52-kết-quả-test)

## 1. Transfer

### 1.1 Điều kiện test

- Chuyển tiền ngẫu nhiên (random sender & receiver) giữa các user trong hệ thống
- Số dư trong tài khoản luôn đủ cho mỗi lần chuyển (để check balance không bị fail)
- Chuyển tiền thành công
- Các thông số test:
  |Thông số|Giá trị|
  |--|--|
  |Số lượng user|1000|
  |MySQL pool size|10|
  |Số lượng request|500|
  |Số lượng request song song|20|

### 1.2 Kết quả test

```bash
Summary:
  Count:        500
  Total:        1.89 s
  Slowest:      112.51 ms
  Fastest:      0.45 ms
  Average:      73.16 ms
  Requests/sec: 264.70

Response time histogram:
  0.454 [1]     |
  11.660 [0]    |
  22.866 [3]    |∎
  34.072 [5]    |∎
  45.278 [6]    |∎∎
  56.484 [40]   |∎∎∎∎∎∎∎∎∎∎
  67.690 [103]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  78.896 [147]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  90.102 [160]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  101.308 [24]  |∎∎∎∎∎∎
  112.514 [11]  |∎∎∎

Latency distribution:
  10 % in 55.27 ms
  25 % in 64.83 ms
  50 % in 75.33 ms
  75 % in 82.47 ms
  90 % in 88.11 ms
  95 % in 90.95 ms
  99 % in 105.14 ms
Status code distribution:
  [OK]   500 responses
```

### 1.3 Nhận xét

- So với các lời gọi gRPC khác, _transfer_ có thời gian P99 là lâu nhất.
- Lý do:
  - Thời gian thực thi của hàm BCrypt check password
  - Thao tác transfer đòi hỏi update/insert data ở nhiều bảng, sử dụng lock ở bảng users khi tiến hành cập nhật số dư.

## 2. GetBalance

### 2.1 Điều kiện test

- Truy vấn balance của user ngẫu nhiên

|Thông số|Giá trị|
|--|--|
|Số lượng user|1000|
|MySQL pool size|10|
|Số lượng request|500|
|Số lượng request song song|20|

### 2.2 Kết quả test

```bash
Summary:
  Count:        500
  Total:        94.02 ms
  Slowest:      19.82 ms
  Fastest:      1.00 ms
  Average:      8.67 ms
  Requests/sec: 5318.25

Response time histogram:
  0.996 [1]     |
  2.879 [13]    |∎∎∎∎
  4.762 [38]    |∎∎∎∎∎∎∎∎∎∎∎
  6.644 [112]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  8.527 [137]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  10.410 [66]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  12.292 [42]   |∎∎∎∎∎∎∎∎∎∎∎∎
  14.175 [46]   |∎∎∎∎∎∎∎∎∎∎∎∎∎
  16.058 [14]   |∎∎∎∎
  17.941 [12]   |∎∎∎∎
  19.823 [19]   |∎∎∎∎∎∎

Latency distribution:
  10 % in 4.67 ms
  25 % in 6.14 ms
  50 % in 7.76 ms
  75 % in 10.54 ms
  90 % in 13.86 ms
  95 % in 17.61 ms
  99 % in 19.42 ms

Status code distribution:
  [OK]   500 responses
```

## 3. GetHistory

### 3.1 Điều kiện test

- 500 request với offset tăng dần, get history của cùng 1 user

|Thông số|Giá trị|
|--|--|
|Số lượng user|1000|
|Số lượng history trong db|4000|
|MySQL pool size|10|
|Số lượng request|500|
|Số lượng request song song|20|
|Số lượng history item|20|

### 3.2 Kết quả test

```bash
Summary:
  Count:        500
  Total:        243.68 ms
  Slowest:      37.54 ms
  Fastest:      1.87 ms
  Average:      22.65 ms
  Requests/sec: 2051.86

Response time histogram:
  1.873 [1]     |
  5.440 [0]     |
  9.007 [1]     |
  12.574 [12]   |∎∎∎∎
  16.141 [50]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  19.708 [100]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  23.275 [101]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  26.842 [110]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  30.410 [92]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  33.977 [26]   |∎∎∎∎∎∎∎∎∎
  37.544 [7]    |∎∎∎

Latency distribution:
  10 % in 15.62 ms
  25 % in 18.24 ms
  50 % in 22.69 ms
  75 % in 26.83 ms
  90 % in 29.35 ms
  95 % in 31.84 ms
  99 % in 34.96 ms

Status code distribution:
  [OK]   500 responses
```

## 4. GetChatFriendList

### 4.1 Điều kiện test

- Số lượng friend: 7

|Thông số|Giá trị|
|--|--|
|Số lượng user|1000|
|Số lượng history trong db|4000|
|MySQL pool size|10|
|Số lượng request|500|
|Số lượng request song song|20|
|Số lượng history item|20|

### 4.2 Kết quả test

```bash
Summary:
  Count:        500
  Total:        115.69 ms
  Slowest:      25.96 ms
  Fastest:      1.59 ms
  Average:      10.70 ms
  Requests/sec: 4322.00

Response time histogram:
  1.591 [1]     |
  4.027 [40]    |∎∎∎∎∎∎∎∎∎∎∎∎
  6.464 [38]    |∎∎∎∎∎∎∎∎∎∎∎∎
  8.901 [104]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  11.337 [130]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  13.774 [66]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  16.211 [62]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  18.648 [23]   |∎∎∎∎∎∎∎
  21.084 [25]   |∎∎∎∎∎∎∎∎
  23.521 [10]   |∎∎∎
  25.958 [1]    |

Latency distribution:
  10 % in 4.86 ms
  25 % in 7.88 ms
  50 % in 9.99 ms
  75 % in 13.70 ms
  90 % in 18.09 ms
  95 % in 19.31 ms
  99 % in 21.93 ms

Status code distribution:
  [OK]   500 responses
```

## 5. GetFriendList

### 5.1 Điều kiện test

- Số lượng friend: 7

|Thông số|Giá trị|
|--|--|
|Số lượng user|1000|
|Số lượng history trong db|4000|
|MySQL pool size|10|
|Số lượng request|500|
|Số lượng request song song|20|
|Số lượng history item|20|

### 5.2 Kết quả test

```bash
Summary:
  Count:        500
  Total:        122.94 ms
  Slowest:      31.43 ms
  Fastest:      1.02 ms
  Average:      11.47 ms
  Requests/sec: 4066.90

Response time histogram:
  1.020 [1]     |
  4.062 [27]    |∎∎∎∎∎∎∎∎
  7.103 [101]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  10.144 [134]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  13.186 [91]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  16.227 [49]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  19.268 [36]   |∎∎∎∎∎∎∎∎∎∎∎
  22.310 [17]   |∎∎∎∎∎
  25.351 [7]    |∎∎
  28.393 [36]   |∎∎∎∎∎∎∎∎∎∎∎
  31.434 [1]    |

Latency distribution:
  10 % in 4.53 ms
  25 % in 6.95 ms
  50 % in 9.75 ms
  75 % in 14.20 ms
  90 % in 21.25 ms
  95 % in 26.16 ms
  99 % in 27.72 ms

Status code distribution:
  [OK]   500 responses
```
