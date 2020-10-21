# Kết quả benmark cho các HTTP service

## 1. Get chat message (load từ cache)

```bash
Running 10s test @ http://localhost:8080/api/protected/chat/2/0
  50 threads and 50 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    31.19ms    5.22ms  49.87ms   67.18%
    Req/Sec    32.03      5.35    80.00     74.75%
  Latency Distribution
     50%   31.71ms
     75%   34.50ms
     90%   37.41ms
     99%   43.56ms
  16064 requests in 10.10s, 37.52MB read
Requests/sec:   1590.40
Transfer/sec:      3.71MB
```

## 2. Get chat message (load từ database)

```bash
Running 10s test @ http://localhost:8080/api/protected/chat/2/1
  50 threads and 50 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    30.75ms   19.79ms 120.77ms   70.30%
    Req/Sec    34.04     12.91   110.00     58.97%
  Latency Distribution
     50%   28.07ms
     75%   41.66ms
     90%   55.86ms
     99%   96.73ms
  17067 requests in 10.10s, 40.50MB read
Requests/sec:   1689.67
Transfer/sec:      4.01MB
```
