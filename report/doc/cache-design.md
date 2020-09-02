# Cache design

| Key | Type | Chức năng |
|:-------|:--------|:-------------|
|`vmr:user:<userId>:info`|HASH|Lưu `username`, `name` của user có id là `userId`|
|`vmr:users:list`|LIST|Danh sách user trong hệ thống|
|`vmr:jwt:<jwtValue>:expire`| STRING|nhận giá trị `true` để đánh dấu `jwtValue` hết hạn|
|`vmr:chat:userId1:userId2`| LIST|Lưu 20 message gần nhất được chat giữa 2 user|
