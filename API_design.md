# TÀI LIỆU THIẾT KẾ API
## Notification System REST API

**Base URL:** `http://localhost:8080/v1`  

---

## 1. QUI ƯỚC CHUNG

### Response Format - Thành công

```json
{
  "meta": {
    "code": 200,
    "message": "Success message"
  },
  "data": { }
}
```

### Response Format - Phân trang

```json
{
  "meta": {
    "code": 200,
    "page": 1,
    "size": 10,
    "total": 100
  },
  "data": [ ]
}
```

### Response Format - Lỗi

```json
{
  "code": 400,
  "message": "Validation failed",
  "systemMessage": [
    "Error detail 1",
    "Error detail 2"
  ]
}
```

### HTTP Status Codes

| Code | Ý nghĩa |
|------|---------|
| 200 | OK - Thành công |
| 400 | Bad Request - Dữ liệu không hợp lệ |
| 404 | Not Found - Không tìm thấy |
| 500 | Internal Server Error - Lỗi hệ thống |

---

## 2. CAMPAIGN API

### 2.1 Tạo Campaign

**POST** `/campaigns`

**Request Body:**

```json
{
  "name": "Xác nhận đơn hàng",
  "templateId": "550e8400-e29b-41d4-a716-446655440000",
  "targetType": "ALL",
  "targetUserId": null,
  "targetGroupId": null,
  "conditionExpression": null,
  "scheduleType": "IMMEDIATE",
  "scheduledAt": null,
  "cronExpression": null
}
```

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Create campaign success"
  },
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Xác nhận đơn hàng",
    "templateId": "550e8400-e29b-41d4-a716-446655440000",
    "templateCode": "ORDER_CONFIRM",
    "targetType": "ALL",
    "targetUserId": null,
    "targetGroupId": null,
    "targetGroupName": null,
    "conditionExpression": null,
    "scheduleType": "IMMEDIATE",
    "scheduledAt": null,
    "cronExpression": null,
    "status": "RUNNING",
    "createdAt": "2026-07-15T10:30:00"
  }
}
```

**Lưu ý:**
- Nếu `scheduleType = IMMEDIATE` → Campaign chạy ngay, không cần `scheduledAt`
- Nếu `scheduleType = ONCE` → Cần set `scheduledAt` (thời điểm chạy)
- Nếu `scheduleType = RECURRING` → Cần set `cronExpression` (định kỳ)

---

### 2.2 Lấy danh sách Campaign

**GET** `/campaigns?keyword=search`

**Query Parameters:**

| Tham số | Kiểu | Bắt buộc | Mô tả |
|--------|------|---------|-------|
| keyword | string | Không | Tìm kiếm theo tên campaign |

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Get all campaign success"
  },
  "data": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "Xác nhận đơn hàng",
      "templateCode": "ORDER_CONFIRM",
      "targetType": "ALL",
      "scheduleType": "IMMEDIATE",
      "status": "RUNNING",
      "scheduledAt": null
    },
    {
      "id": "223e4567-e89b-12d3-a456-426614174001",
      "name": "Nhắc nhở khuyến mãi",
      "templateCode": "PROMO_REMINDER",
      "targetType": "GROUP",
      "scheduleType": "RECURRING",
      "status": "SCHEDULED",
      "scheduledAt": "2026-07-20T09:00:00"
    }
  ]
}
```

---

### 2.3 Lấy chi tiết Campaign

**GET** `/campaigns/{id}`

**Path Parameters:**

| Tham số | Kiểu | Mô tả |
|--------|------|-------|
| id | string | ID của campaign |

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Get detail campaign success"
  },
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Xác nhận đơn hàng",
    "templateId": "550e8400-e29b-41d4-a716-446655440000",
    "templateCode": "ORDER_CONFIRM",
    "targetType": "ALL",
    "targetUserId": null,
    "targetGroupId": null,
    "targetGroupName": null,
    "conditionExpression": null,
    "scheduleType": "IMMEDIATE",
    "scheduledAt": null,
    "cronExpression": null,
    "status": "RUNNING",
    "createdAt": "2026-07-15T10:30:00"
  }
}
```

---

## 3. NOTIFICATION API

### 3.1 Lấy thông báo của người dùng

**GET** `/notifications/{userId}?pageNumber=0&pageSize=10`

**Path Parameters:**

| Tham số | Kiểu | Mô tả |
|--------|------|-------|
| userId | string | ID của người dùng |

**Query Parameters:**

| Tham số | Kiểu | Mặc định | Mô tả |
|--------|------|---------|-------|
| pageNumber | integer | 0 | Số trang (bắt đầu từ 0) |
| pageSize | integer | 10 | Số bản ghi trên một trang |

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "page": 1,
    "size": 10,
    "total": 50,
    "message": "Get notifications success"
  },
  "data": [
    {
      "fullName": "Nguyễn Văn A",
      "notificationType": "ORDER",
      "channel": "EMAIL",
      "recipientAddress": "nguyenvana@example.com",
      "title": "Xác nhận đơn hàng ORD-123",
      "content": "Đơn hàng ORD-123 của bạn được xác nhận",
      "sentAt": "2026-07-15T10:30:00"
    },
    {
      "fullName": "Nguyễn Văn A",
      "notificationType": "PROMOTION",
      "channel": "SMS",
      "recipientAddress": "0912345678",
      "title": "Khuyến mãi 50%",
      "content": "Giảm giá 50% cho sản phẩm mới",
      "sentAt": "2026-07-14T15:20:00"
    }
  ]
}
```

---

### 3.2 Lấy dashboard phân tích

**GET** `/notifications/summary`

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Load summary notification success"
  },
  "data": {
    "totalNotifications": 10000,
    "totalSent": 9850,
    "totalFailed": 150,
    "totalRead": 7500,
    "successRate": 98.5,
    "errorRate": 1.5,
    "readRate": 75.0
  }
}
```

**Các trường:**

| Trường | Mô tả |
|--------|-------|
| totalNotifications | Tổng số thông báo |
| totalSent | Số thông báo gửi thành công |
| totalFailed | Số thông báo gửi thất bại |
| totalRead | Số thông báo đã đọc |
| successRate | Tỷ lệ thành công (%) |
| errorRate | Tỷ lệ lỗi (%) |
| readRate | Tỷ lệ đọc (%) |

---

## 4. TEMPLATE API

### 4.1 Tạo Template

**POST** `/templates`

**Request Body:**

```json
{
  "code": "ORDER_CONFIRM",
  "channel": "EMAIL",
  "notificationType": "ORDER",
  "language": "vi",
  "subject": "Đơn hàng {{order_id}} được xác nhận",
  "content": "Xin chào {{user_name}},\n\nĐơn hàng {{order_id}} của bạn được xác nhận.\nTổng tiền: {{amount}}\nDự kiến giao: {{delivery_date}}\n\nCảm ơn bạn!",
  "isActive": true
}
```

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Create template success"
  },
  "data": {
    "id": "660e8400-e29b-41d4-a716-446655440000",
    "code": "ORDER_CONFIRM",
    "channel": "EMAIL",
    "notificationType": "ORDER",
    "language": "vi",
    "subject": "Đơn hàng {{order_id}} được xác nhận",
    "content": "Xin chào {{user_name}},...",
    "isActive": true
  }
}
```

---

### 4.2 Lấy danh sách Template

**GET** `/templates?keyword=ORDER&channel=EMAIL&language=vi`

**Query Parameters:**

| Tham số | Kiểu | Bắt buộc | Mô tả |
|--------|------|---------|-------|
| keyword | string | Không | Tìm kiếm theo mã template |
| channel | string | Không | EMAIL, SMS, PUSH |
| language | string | Không | Mã ngôn ngữ (vi, en, ...) |

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Find template success"
  },
  "data": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440000",
      "code": "ORDER_CONFIRM",
      "channel": "EMAIL",
      "notificationType": "ORDER",
      "language": "vi",
      "subject": "Đơn hàng {{order_id}} được xác nhận",
      "content": "Xin chào {{user_name}},...",
      "isActive": true
    },
    {
      "id": "770e8400-e29b-41d4-a716-446655440001",
      "code": "ORDER_CONFIRM_SMS",
      "channel": "SMS",
      "notificationType": "ORDER",
      "language": "vi",
      "subject": "Xác nhận",
      "content": "Đơn hàng {{order_id}} được xác nhận",
      "isActive": true
    }
  ]
}
```

---

### 4.3 Cập nhật Template

**PUT** `/templates/{id}`

**Path Parameters:**

| Tham số | Kiểu | Mô tả |
|--------|------|-------|
| id | string | ID của template |

**Request Body:**

```json
{
  "code": "ORDER_CONFIRM",
  "channel": "EMAIL",
  "notificationType": "ORDER",
  "language": "vi",
  "subject": "Đơn hàng {{order_id}} được xác nhận",
  "content": "Nội dung cập nhật...",
  "isActive": true
}
```

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Update template success"
  }
}
```

---

### 4.4 Xóa Template

**DELETE** `/templates/{id}`

**Path Parameters:**

| Tham số | Kiểu | Mô tả |
|--------|------|-------|
| id | string | ID của template |

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Delete template success"
  }
}
```

**Lưu ý:** Xóa là soft delete (không xóa dữ liệu thực sự, chỉ đánh dấu deleted = true)

---

## 5. GROUP API

### 5.1 Tạo Group

**POST** `/groups`

**Request Body:**

```json
{
  "name": "Người dùng Premium",
  "description": "Những người dùng có gói Premium"
}
```

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Create group success"
  },
  "data": {
    "id": "880e8400-e29b-41d4-a716-446655440002",
    "name": "Người dùng Premium",
    "description": "Những người dùng có gói Premium"
  }
}
```

---

### 5.2 Lấy danh sách Group

**GET** `/groups`

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Get all groups success"
  },
  "data": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440002",
      "name": "Người dùng Premium",
      "description": "Những người dùng có gói Premium"
    },
    {
      "id": "990e8400-e29b-41d4-a716-446655440003",
      "name": "Người dùng VIP",
      "description": "Gói VIP"
    }
  ]
}
```

---

### 5.3 Thêm thành viên vào Group

**POST** `/groups/{groupId}/members`

**Path Parameters:**

| Tham số | Kiểu | Mô tả |
|--------|------|-------|
| groupId | string | ID của group |

**Request Body:**

```json
{
  "userIds": [
    "aaa1-b2c3-d4e5-f6g7",
    "bbb2-c3d4-e5f6-g7h8",
    "ccc3-d4e5-f6g7-h8i9"
  ]
}
```

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Insert member in group success"
  }
}
```

**Lưu ý:** 
- Không tạo thành viên trùng lặp
- Kiểm tra user có tồn tại không

---

### 5.4 Lấy thành viên của Group

**GET** `/groups/{groupId}/members`

**Path Parameters:**

| Tham số | Kiểu | Mô tả |
|--------|------|-------|
| groupId | string | ID của group |

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Get users in group success"
  },
  "data": [
    {
      "id": "aaa1-b2c3-d4e5-f6g7",
      "fullName": "Nguyễn Văn A",
      "email": "nguyenvana@example.com",
      "phone": "0912345678",
      "deviceToken": "token_xyz",
      "isActive": true
    },
    {
      "id": "bbb2-c3d4-e5f6-g7h8",
      "fullName": "Trần Thị B",
      "email": "tranthib@example.com",
      "phone": "0987654321",
      "deviceToken": "token_abc",
      "isActive": true
    }
  ]
}
```

---

## 6. USER PREFERENCE API

### 6.1 Lấy tùy chọn của người dùng

**GET** `/preferences/{userId}`

**Path Parameters:**

| Tham số | Kiểu | Mô tả |
|--------|------|-------|
| userId | string | ID của người dùng |

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Get preferences by user success"
  },
  "data": [
    {
      "notificationType": "ORDER",
      "channel": "EMAIL",
      "isEnabled": true
    },
    {
      "notificationType": "ORDER",
      "channel": "SMS",
      "isEnabled": false
    },
    {
      "notificationType": "PROMOTION",
      "channel": "EMAIL",
      "isEnabled": true
    },
    {
      "notificationType": "PROMOTION",
      "channel": "PUSH",
      "isEnabled": false
    }
  ]
}
```

---

### 6.2 Cập nhật tùy chọn của người dùng

**PUT** `/preferences/{userId}`

**Path Parameters:**

| Tham số | Kiểu | Mô tả |
|--------|------|-------|
| userId | string | ID của người dùng |

**Request Body:**

```json
{
  "preferences": [
    {
      "notiType": "ORDER",
      "channel": "EMAIL",
      "enabled": true
    },
    {
      "notiType": "ORDER",
      "channel": "SMS",
      "enabled": false
    },
    {
      "notiType": "PROMOTION",
      "channel": "PUSH",
      "enabled": true
    }
  ]
}
```

**Response:** `200 OK`

```json
{
  "meta": {
    "code": 200,
    "message": "Update preferences success"
  }
}
```

**Lưu ý:**
- Nếu preference chưa tồn tại → Tạo mới
- Nếu đã tồn tại → Cập nhật giá trị

---

## 7. CHANNEL TYPES

| Channel | Mô tả | Địa chỉ nhận |
|---------|-------|-------------|
| EMAIL | Gửi qua email | Lấy từ user.email |
| SMS | Gửi SMS | Lấy từ user.phone |
| PUSH | Thông báo đẩy | Lấy từ user.deviceToken |

---

## 8. NOTIFICATION TYPES

| Type | Mô tả |
|------|-------|
| SYSTEM | Thông báo hệ thống |
| ORDER | Liên quan đơn hàng |
| PROMOTION | Khuyến mãi, khuyến nghị |
| SECURITY | Bảo mật, xác thực |

---

## 9. SCHEDULE TYPES

### IMMEDIATE - Gửi ngay lập tức

```json
{
  "scheduleType": "IMMEDIATE",
  "scheduledAt": null,
  "cronExpression": null
}
```

Campaign sẽ chạy ngay sau khi tạo.

### ONCE - Gửi một lần

```json
{
  "scheduleType": "ONCE",
  "scheduledAt": "2026-07-20T14:30:00",
  "cronExpression": null
}
```

Campaign sẽ chạy vào thời điểm chỉ định.

### RECURRING - Gửi định kỳ

```json
{
  "scheduleType": "RECURRING",
  "scheduledAt": null,
  "cronExpression": "0 9 * * MON-FRI"
}
```

Campaign sẽ chạy định kỳ theo Cron expression.

**Cron Expression Format:**

```
0 9 * * MON-FRI
│ │ │ │ └─ Ngày trong tuần (0-6 hoặc MON-SUN)
│ │ │ └─── Tháng (1-12)
│ │ └───── Ngày (1-31)
│ └─────── Giờ (0-23)
└───────── Phút (0-59)
```

**Ví dụ Cron:**

| Expression | Ý nghĩa |
|-----------|---------|
| `0 9 * * MON-FRI` | 9:00 sáng từ Thứ 2 đến Thứ 6 |
| `0 0 * * *` | Hàng ngày lúc 0:00 (nửa đêm) |
| `0 */6 * * *` | Mỗi 6 giờ (0, 6, 12, 18 giờ) |
| `0 9,17 * * *` | 9:00 và 17:00 hàng ngày |
| `0 0 1 * *` | Ngày đầu tháng lúc 0:00 |

---

## 10. TARGET TYPES

| Type | Mô tả |
|------|-------|
| ALL | Gửi tất cả người dùng |
| USER | Gửi một người dùng cụ thể |
| GROUP | Gửi một nhóm người dùng |
| CONDITION | Dự trữ cho tương lai (chưa implement) |

---

## 11. CAMPAIGN STATUSES

| Status | Mô tả |
|--------|-------|
| SCHEDULED | Đã lên lịch, chờ thực hiện |
| RUNNING | Đang chạy/xử lý |
| COMPLETED | Đã hoàn thành |
| CANCELLED | Đã hủy |

---

## 12. TEMPLATE VARIABLES

### Built-in Variables

Những biến này luôn có sẵn trong mọi template:

```
{{user_id}}          Mã người dùng
{{user_name}}        Tên người dùng
{{email}}            Email người dùng
{{phone}}            Số điện thoại
{{campaign_id}}      Mã campaign
{{campaign_name}}    Tên campaign
{{template_code}}    Mã template
```

### Custom Variables

Custom variables đến từ `payload` khi tạo campaign:

```json
{
  "campaignId": "xxx",
  "payload": {
    "order_id": "ORD-123",
    "amount": "$99.99",
    "delivery_date": "2026-08-15"
  }
}
```

Trong template, bạn có thể dùng:
- `{{order_id}}`
- `{{amount}}`
- `{{delivery_date}}`

### Template Example

**Template trong Database:**

```
Subject: Đơn hàng {{order_id}} được xác nhận
Content:
Xin chào {{user_name}},

Đơn hàng {{order_id}} của bạn được xác nhận.
Tổng tiền: {{amount}}
Dự kiến giao: {{delivery_date}}

Cảm ơn bạn!
```

**Sau khi render (thay thế biến):**

```
Subject: Đơn hàng ORD-123 được xác nhận
Content:
Xin chào Nguyễn Văn A,

Đơn hàng ORD-123 của bạn được xác nhận.
Tổng tiền: $99.99
Dự kiến giao: 2026-08-15

Cảm ơn bạn!
```

---

## 13. ERROR CODES & MESSAGES

### Template not exist

```json
{
  "code": 400,
  "message": "Validation failed",
  "systemMessage": [
    "Template not exist"
  ]
}
```

### User not exist

```json
{
  "code": 400,
  "message": "Validation failed",
  "systemMessage": [
    "User not exist"
  ]
}
```

### Group not exist

```json
{
  "code": 400,
  "message": "Validation failed",
  "systemMessage": [
    "Group not exist"
  ]
}
```

### Invalid Cron expression

```json
{
  "code": 400,
  "message": "Validation failed",
  "systemMessage": [
    "Invalid cron expression format: abc xyz"
  ]
}
```

### Scheduled time is required

```json
{
  "code": 400,
  "message": "Validation failed",
  "systemMessage": [
    "Scheduled time is required"
  ]
}
```

### Cron expression is required

```json
{
  "code": 400,
  "message": "Validation failed",
  "systemMessage": [
    "Cron expression is required"
  ]
}
```

### Template already exists

```json
{
  "code": 400,
  "message": "Validation failed",
  "systemMessage": [
    "Template already exists"
  ]
}
```

### Another template with same code already exists

```json
{
  "code": 400,
  "message": "Validation failed",
  "systemMessage": [
    "Another template with same code, channel, and language already exists"
  ]
}
```

---

## 14. QUICK REFERENCE

### Endpoints Summary

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/campaigns` | Tạo campaign |
| GET | `/campaigns` | Lấy danh sách campaign |
| GET | `/campaigns/{id}` | Lấy chi tiết campaign |
| GET | `/notifications/{userId}` | Lấy thông báo người dùng |
| GET | `/notifications/summary` | Lấy thống kê |
| POST | `/templates` | Tạo template |
| GET | `/templates` | Lấy danh sách template |
| PUT | `/templates/{id}` | Cập nhật template |
| DELETE | `/templates/{id}` | Xóa template |
| POST | `/groups` | Tạo group |
| GET | `/groups` | Lấy danh sách group |
| POST | `/groups/{id}/members` | Thêm thành viên |
| GET | `/groups/{id}/members` | Lấy thành viên group |
| GET | `/preferences/{userId}` | Lấy tùy chọn người dùng |
| PUT | `/preferences/{userId}` | Cập nhật tùy chọn |

---

