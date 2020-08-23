# Cache design

| Key | Type | Chức năng |
|:-------|:--------|:-------------|
|`chatapp:username:<username>:id`|STRING|Lưu userId tương ứng với username|
|`chatapp:user:<userId>:info`|HASH|Lưu `username`, `password`, `fullname` của user có id là `userId`|
|`chatapp:users:online`|SET|Tập hợp các userId của người dùng online|
|`chatapp:user:<userId>:friends`|SET|Lưu danh sách bạn bè của một user|
|`chatapp:conservation:<conservationId>:users`|SET|Lưu danh sách `userId` của một cuộc trò chuyện|
|`chatapp:conservation:<conservationId>:channel`|CHANNEL|Phục vụ cho việc gửi nhận tin nhắn|
|`chatapp:conservation:<conservationId>:list`|LIST|List chứa 50 tin nhắn cuối của cuộc hội thoại|
|`chatapp:user:<userId>:conservations`| SET| Tập hợp chứa các cuộc hội thoại của user|
|`chatapp:jwt:<jwtValue>:expire`| STRING|nhận giá trị 1 để đánh dấu `jwtValue` hết hạn|
