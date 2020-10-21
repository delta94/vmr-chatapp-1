# Kết quả benmark các GRPC service

- [Kết quả benmark các GRPC service](#kết-quả-benmark-các-grpc-service)
  - [1. GetBalance](#1-getbalance)
  - [2. GetHistory](#2-gethistory)
  - [3. Transfer (Sender và Receiver ngẫu nhiên)](#3-transfer-sender-và-receiver-ngẫu-nhiên)
  - [4. GetChatFriendList](#4-getchatfriendlist)
  - [5. GetFriendList](#5-getfriendlist)

## 1. GetBalance

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

## 2. GetHistory

```bash
Summary:
  Count:        500
  Total:        135.43 ms
  Slowest:      29.68 ms
  Fastest:      0.87 ms
  Average:      12.16 ms
  Requests/sec: 3692.00

Response time histogram:
  0.870 [1]     |
  3.752 [10]    |∎∎∎
  6.633 [17]    |∎∎∎∎∎
  9.514 [126]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  12.395 [137]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  15.276 [109]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  18.157 [43]   |∎∎∎∎∎∎∎∎∎∎∎∎∎
  21.038 [36]   |∎∎∎∎∎∎∎∎∎∎∎
  23.919 [18]   |∎∎∎∎∎
  26.800 [1]    |
  29.681 [2]    |∎

Latency distribution:
  10 % in 7.57 ms 
  25 % in 9.17 ms 
  50 % in 11.25 ms 
  75 % in 14.45 ms 
  90 % in 18.74 ms 
  95 % in 20.59 ms 
  99 % in 23.55 ms 

Status code distribution:
  [OK]   500 responses 
```

## 3. Transfer (Sender và Receiver ngẫu nhiên)

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

## 4. GetChatFriendList

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
