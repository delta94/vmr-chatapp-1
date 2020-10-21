math.randomseed(os.time())
i = 0
request = function()
  i = i + 1
  wrk.method = "POST"
  wrk.body   = '{"username": "username'..i..'", "password": "12345678", "name": "foo"}'
  wrk.headers["Content-Type"] = "application/json"
  return wrk.format(wrk.method, wrk.path, wrk.headers, wrk.body)
end