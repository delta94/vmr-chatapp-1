# Kết quả benmark GRPC service

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

## 3. Transfer

