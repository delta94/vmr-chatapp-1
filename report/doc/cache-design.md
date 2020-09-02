# Cache design

| Key | Type | Chức năng |
|:-------|:--------|:-------------|
|`vmr:username:<username>:id`|STRING|Lưu userId tương ứng với username|
|`vmr:user:<userId>:info`|HASH|Lưu `username`, `name` của user có id là `userId`|
|`vmr:users:list`|LIST|Danh sách user trong hệ thống|
|`vmr:users:online`|SET|Tập hợp các userId của người dùng online|
|`vmr:jwt:<jwtValue>:expire`| STRING|nhận giá trị `true` để đánh dấu `jwtValue` hết hạn|
|`vmr:chat:userId1:userId2`| LIST|Lưu 20 message gần nhất được chat giữa 2 user|
