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

## 3. Login

```bash
Running 10s test @ http://localhost:8080/api/public/login
  50 threads and 50 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   136.31ms   85.82ms 509.22ms   68.12%
    Req/Sec     9.16      4.91    30.00     77.73%
  Latency Distribution
     50%  117.21ms
     75%  189.47ms
     90%  252.46ms
     99%  395.30ms
  3814 requests in 10.10s, 0.88MB read
Requests/sec:    377.70
Transfer/sec:     89.26KB
```

## 4. Register

```bash
Running 10s test @ http://localhost:8080/api/public/register
  1 threads and 50 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   146.79ms   46.45ms 246.88ms   65.95%
    Req/Sec   339.89     42.47   414.00     72.00%
  Latency Distribution
     50%  152.72ms
     75%  183.45ms
     90%  202.71ms
     99%  229.35ms
  3383 requests in 10.01s, 837.39KB read
Requests/sec:    338.12
Transfer/sec:     83.69KB
```
