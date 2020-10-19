# Kết quả benmark các GRPC service

- [Kết quả benmark các GRPC service](#kết-quả-benmark-các-grpc-service)
  - [1. GetBalance](#1-getbalance)
  - [2. GetHistory](#2-gethistory)
  - [3. Transfer (Sender và Receiver cố định)](#3-transfer-sender-và-receiver-cố-định)
  - [4. Transfer (Sender và Receiver ngẫu nhiên)](#4-transfer-sender-và-receiver-ngẫu-nhiên)
  - [3. GetChatFriendList](#3-getchatfriendlist)
  - [4. GetFriendList](#4-getfriendlist)

## 1. GetBalance

```bash
Summary:
  Count:        2000
  Total:        693.37 ms
  Slowest:      36.92 ms
  Fastest:      4.83 ms
  Average:      17.03 ms
  Requests/sec: 2884.47

Response time histogram:
  4.826 [1]     |
  8.036 [6]     |
  11.246 [8]    |
  14.456 [41]   |∎
  17.665 [1477] |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  20.875 [322]  |∎∎∎∎∎∎∎∎∎
  24.085 [45]   |∎
  27.295 [1]    |
  30.505 [53]   |∎
  33.715 [28]   |∎
  36.924 [18]   |

Latency distribution:
  10 % in 15.22 ms
  25 % in 15.41 ms
  50 % in 15.77 ms
  75 % in 17.43 ms
  90 % in 19.44 ms
  95 % in 23.92 ms
  99 % in 33.17 ms

Status code distribution:
  [OK]   2000 responses  
```

## 2. GetHistory

```bash
Summary:
  Count:        2000
  Total:        988.66 ms
  Slowest:      37.32 ms
  Fastest:      5.19 ms
  Average:      24.31 ms
  Requests/sec: 2022.94

Response time histogram:
  5.187 [1]     |
  8.401 [6]     |
  11.614 [5]    |
  14.828 [8]    |
  18.042 [12]   |
  21.255 [32]   |∎
  24.469 [1150] |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  27.683 [671]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  30.896 [100]  |∎∎∎
  34.110 [7]    |
  37.323 [8]    |

Latency distribution:
  10 % in 22.63 ms
  25 % in 23.44 ms
  50 % in 24.20 ms
  75 % in 25.02 ms
  90 % in 26.54 ms
  95 % in 27.96 ms
  99 % in 30.18 ms

Status code distribution:
  [OK]   2000 responses
```

## 3. Transfer (Sender và Receiver cố định)

```bash
Summary:
  Count:        2000
  Total:        40.84 s
  Slowest:      1.39 s
  Fastest:      181.81 ms
  Average:      1.01 s
  Requests/sec: 48.98

Response time histogram:
  181.807 [1]   |
  303.090 [4]   |
  424.373 [4]   |
  545.656 [6]   |
  666.939 [6]   |
  788.222 [5]   |
  909.505 [3]   |
  1030.788 [1485]       |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  1152.070 [455]        |∎∎∎∎∎∎∎∎∎∎∎∎
  1273.353 [24] |∎
  1394.636 [7]  |

Latency distribution:
  10 % in 969.91 ms
  25 % in 987.95 ms
  50 % in 1.01 s
  75 % in 1.03 s
  90 % in 1.05 s
  95 % in 1.08 s
  99 % in 1.17 s

Status code distribution:
  [OK]   2000 responses
```

## 4. Transfer (Sender và Receiver ngẫu nhiên)

```bash
Summary:
  Count:        2000
  Total:        11.67 s
  Slowest:      970.55 ms
  Fastest:      47.80 ms
  Average:      288.21 ms
  Requests/sec: 171.34

Response time histogram:
  47.799 [1]    |
  140.074 [6]   |
  232.349 [405] |∎∎∎∎∎∎∎∎∎∎∎∎∎
  324.624 [1204]        |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  416.898 [264] |∎∎∎∎∎∎∎∎∎
  509.173 [71]  |∎∎
  601.448 [8]   |
  693.723 [9]   |
  785.998 [11]  |
  878.273 [14]  |
  970.547 [7]   |

Latency distribution:
  10 % in 218.58 ms 
  25 % in 238.06 ms 
  50 % in 264.22 ms 
  75 % in 306.11 ms 
  90 % in 378.59 ms 
  95 % in 434.28 ms 
  99 % in 790.26 ms 

Status code distribution:
  [OK]   2000 responses 
```

## 3. GetChatFriendList

```bash
Summary:
  Count:        2000
  Total:        561.56 ms
  Slowest:      32.31 ms
  Fastest:      1.73 ms
  Average:      13.43 ms
  Requests/sec: 3561.54

Response time histogram:
  1.733 [1]     |
  4.790 [42]    |∎∎∎∎
  7.847 [220]   |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  10.904 [475]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  13.962 [415]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  17.019 [410]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  20.076 [205]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  23.134 [109]  |∎∎∎∎∎∎∎∎∎
  26.191 [72]   |∎∎∎∎∎∎
  29.248 [39]   |∎∎∎
  32.306 [12]   |∎

Latency distribution:
  10 % in 7.37 ms
  25 % in 9.50 ms
  50 % in 12.68 ms
  75 % in 16.38 ms
  90 % in 20.88 ms
  95 % in 23.74 ms
  99 % in 28.67 ms

Status code distribution:
  [OK]   2000 responses
```

## 4. GetFriendList

```bash
Summary:
  Count:        2000
  Total:        750.69 ms
  Slowest:      51.75 ms
  Fastest:      0.78 ms
  Average:      17.76 ms
  Requests/sec: 2664.20

Response time histogram:
  0.781 [1]     |
  5.878 [57]    |∎∎∎
  10.975 [235]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  16.072 [555]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  21.169 [672]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  26.266 [253]  |∎∎∎∎∎∎∎∎∎∎∎∎∎∎∎
  31.362 [135]  |∎∎∎∎∎∎∎∎
  36.459 [51]   |∎∎∎
  41.556 [25]   |∎
  46.653 [6]    |
  51.750 [10]   |∎

Latency distribution:
  10 % in 9.47 ms
  25 % in 13.02 ms
  50 % in 17.20 ms
  75 % in 20.99 ms
  90 % in 27.13 ms
  95 % in 30.99 ms
  99 % in 40.22 ms

Status code distribution:
  [OK]   2000 responses
```
