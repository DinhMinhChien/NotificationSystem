# TÀI LIỆU THIẾT KẾ HỆ THỐNG
## Notification System (Hệ thống Quản lý Thông báo)

---

## 1. TỔNG QUAN HỆ THỐNG

### Mục đích
Hệ thống cung cấp giải pháp gửi thông báo đến người dùng qua nhiều kênh (Email, SMS, Push) một cách linh hoạt, đáng tin cậy và có thể mở rộng.

### Tính năng chính
-  Hỗ trợ 3 kênh gửi: Email, SMS, Push (Hoàn thành workflow của EMAIL tương đối hoàn chỉnh , luồng SMS và FireBase đang tìm hướng phát triển và đang dừng lại ở mock dữ liệu) 
-  Tạo và quản lý campaign thông báo
-  3 loại lên lịch: Lập tức, Một lần, Định kỳ (Cron)
-  Quản lý tùy chọn người dùng (opt-in/opt-out)
-  Hỗ trợ nhóm người dùng
-  Không gửi trùng lặp (Idempotency)
-  Tự động thử lại khi gửi thất bại
-  Bảng điều khiển phân tích (thành công, thất bại, tỷ lệ đọc)

### Stack công nghệ
```
Backend:      Spring Boot 3.5.9 + Java 21
Database:     JPA/Hibernate + MySQL
Message:      Apache Kafka
Mapping:      MapStruct
Mail:         JavaMailSender
Logging:      SLF4J + Logback
```

---

## 2. KIẾN TRÚC HỆ THỐNG

### Sơ đồ kiến trúc theo lớp

```
┌─────────────────────────────────────┐
│   REST Controllers                   │
│   (5 API endpoints)                 │
└────────────────┬────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│   Service Layer                      │
│   (6 Services - Business Logic)     │
└────────────────┬────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│   Mapper Layer (MapStruct)           │
│   (Entity ↔ DTO)                    │
└────────────────┬────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│   Repository Layer (JPA)             │
│   (Database Access)                  │
└────────────────┬────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│   Entity Layer (Domain Models)       │
└─────────────────────────────────────┘
```

### Các thành phần chính

| Thành phần | Số lượng | Vai trò |
|-----------|---------|--------|
| REST Controllers | 5 | Campaign, Notification, Template, Group, Preference |
| Services | 6 | Xử lý business logic |
| Repositories | 8 | Truy cập database |
| Entities | 8 | Mô hình dữ liệu |
| Schedulers | 2 | Lên lịch campaign & thử lại |
| Kafka Consumer | 1 | Xử lý sự kiện async |
| Senders | 4 | Email, SMS, Push + Factory |

---

## 3. QUY TRÌNH LÀM VIỆC CHÍNH

### Workflow 1: Tạo Campaign (Ngay lập tức)

```
1. Gửi API: POST /v1/campaigns
   ├─ Kiểm tra template tồn tại
   ├─ Xác định danh sách người nhận
   ├─ Lưu vào database
   └─ Status = RUNNING
   
2. Publish event vào Kafka topic
   
3. Kafka Consumer nhận event
   ├─ Lọc người dùng hợp lệ
   ├─ Tạo Notification records
   ├─ Gửi thông báo async
   └─ Cập nhật status
```

### Workflow 2: Campaign Lên lịch (ONCE / RECURRING)

```
1. Gửi API: POST /v1/campaigns
   └─ Status = SCHEDULED
   
2. CampaignScheduler (chạy mỗi 10 giây)
   ├─ Tìm campaigns sẵn sàng
   ├─ Cập nhật status → RUNNING
   └─ Publish event vào Kafka
   
3. Kafka Consumer xử lý như Workflow 1
```

### Workflow 3: Thử lại gửi thất bại

```
1. NotificationRetryScheduler (chạy mỗi 30 giây)
   ├─ Tìm Notifications với status = FAILED
   ├─ retryCount < 3
   └─ Gửi lại
   
2. Max 3 lần thử lại → Dừng
```

---

## 4. MÔ HÌNH DỮ LIỆU

### Các bảng chính

```
User (Người dùng)
├─ id, fullName, email, phone, deviceToken, isActive
├─ 1:N GroupMember (Thành viên nhóm)
├─ 1:N Notification (Thông báo)
└─ 1:N UserPreference (Tùy chọn)

Campaign (Chiến dịch)
├─ id, name, templateId, targetType, scheduleType
├─ status, scheduledAt, cronExpression
├─ 1:N Notification
├─ N:1 Template
├─ N:1 TargetUser (nếu targetType=USER)
└─ N:1 TargetGroup (nếu targetType=GROUP)

Template (Mẫu)
├─ id, code, channel, notificationType
├─ language, subject, content, isActive
└─ 1:N Campaign

Group (Nhóm)
├─ id, name, description
└─ 1:N GroupMember

GroupMember (Thành viên nhóm)
├─ id, userId, groupId
├─ N:1 User
└─ N:1 Group

Notification (Thông báo)
├─ id, userId, campaignId, channel, status
├─ idempotencyKey (không lặp lại)
├─ recipientAddress, title, content
├─ sentAt, isRead, retryCount, errorMessage
├─ N:1 User
└─ N:1 Campaign

UserPreference (Tùy chọn người dùng)
├─ id, userId, notificationType, channel
├─ isEnabled (tắt/bật)
└─ N:1 User
```

### Các kiểu dữ liệu quan trọng

| Enum | Giá trị |
|------|--------|
| **CampaignStatus** | SCHEDULED, RUNNING, COMPLETED, CANCELLED |
| **NotificationStatus** | QUEUED, SENDING, SENT, FAILED, CANCELLED |
| **ChannelType** | EMAIL, SMS, PUSH |
| **NotificationType** | SYSTEM, ORDER, PROMOTION, SECURITY |
| **ScheduleType** | IMMEDIATE, ONCE, RECURRING |
| **TargetType** | ALL, USER, GROUP, CONDITION |

---

## 5. TÍCH HỢP KAFKA

### Cấu hình

**Topic:** `notification-topic`

**Message format (JSON):**
```json
{
  "campaignId": "uuid-xxx",
  "payload": {
    "order_id": "ORD-123",
    "amount": "99.99",
    "delivery_date": "2026-08-15"
  }
}
```

### Consumer

```
Listener: NotificationConsumer
Retry attempts: 4 lần
Backoff: 2s → 4s → 8s → 16s (exponential)
```

### Luồng xử lý

```
NotificationConsumer nhận message
    ↓
Tải Campaign & Template từ DB
    ↓
Xác định danh sách người nhận
    (ALL/USER/GROUP/CONDITION)
    ↓
Lọc người dùng:
├─ Chỉ người dùng active
├─ Phải có địa chỉ nhận (email/phone/token)
└─ Tùy chọn phải bật
    ↓
Render template (thay thế biến)
    ↓
Tạo Notification + Idempotency key
    ↓
Kiểm tra trùng lặp + Lưu vào DB
    ↓
Gửi async (ngoài transaction)
```

---

## 6. SCHEDULERS (Lên lịch tự động)

### CampaignScheduler
- **Chạy:** Mỗi 10 giây
- **Nhiệm vụ:** 
  - Tìm campaigns: status=SCHEDULED AND scheduledAt <= hiện tại
  - Cập nhật status → RUNNING
  - Publish event vào Kafka

### NotificationRetryScheduler
- **Chạy:** Mỗi 30 giây
- **Nhiệm vụ:**
  - Tìm Notifications: status=FAILED AND retryCount < 3
  - Gửi lại
  - Cập nhật retryCount

---

## 7. KIỂM SOÁT NGƯỜI DÙNG

### Tùy chọn người dùng (User Preference)

Mỗi người dùng có thể bật/tắt nhận thông báo theo:
- **Loại thông báo:** SYSTEM, ORDER, PROMOTION, SECURITY
- **Kênh gửi:** EMAIL, SMS, PUSH

**Mặc định:** Bật (enable) nếu không cấu hình

**Ví dụ:**
```
Người dùng A:
├─ ORDER + EMAIL → Bật (nhận email đơn hàng)
├─ PROMOTION + EMAIL → Tắt (không nhận email khuyến mãi)
└─ PROMOTION + SMS → Bật (nhận tin SMS khuyến mãi)
```

### Lọc người nhận

Trước khi tạo thông báo, hệ thống sẽ lọc bỏ:
1. Người dùng không active (isActive = false)
2. Không có địa chỉ nhận cho kênh đó
3. Đã tắt tùy chọn cho loại + kênh này

---

## 8. BIẾN MẪU (Template Variables)

### Cách dùng
```
Mẫu: "Xin chào {{user_name}}, đơn hàng {{order_id}} được xác nhận"
↓
Thay thế: "Xin chào Nguyễn Văn A, đơn hàng ORD-123 được xác nhận"
```

### Các biến có sẵn
```
{{user_id}}          → ID người dùng
{{user_name}}        → Tên người dùng
{{email}}            → Email
{{phone}}            → Số điện thoại
{{campaign_id}}      → ID campaign
{{campaign_name}}    → Tên campaign
{{template_code}}    → Mã template
{{order_id}}         → Từ payload (tuỳ chỉnh)
{{amount}}           → Từ payload (tuỳ chỉnh)
...                  → Các biến khác từ payload
```

---

## 9. KHÔNG GỬI TRÙNG LẶP (Idempotency)

### Khóa trùng lặp
```
campaignId:userId:channel:scheduledAt
```

### Cách hoạt động
1. Mỗi lần tạo Notification, tạo idempotency key
2. Trước khi lưu, kiểm tra key đã tồn tại chưa
3. Nếu đã tồn tại → Bỏ qua (không tạo lại)
4. Nếu chưa → Lưu vào DB

**Lợi ích:** Kafka retry, scheduler re-run không sẽ tạo thông báo lại

---

## 10. BẢO MẬT

### Audit Log
Tất cả bảng tự động ghi lại:
- `createdAt` - Lúc tạo
- `createdBy` - Ai tạo (từ SecurityContext)
- `updatedAt` - Lúc sửa
- `updatedBy` - Ai sửa
- `deleted` - Xóa mềm (không xóa thực sự)

### Validations
- Kiểm tra template tồn tại
- Kiểm tra user/group tồn tại
- Validate Cron expression
- Input validation (JSR-380)



### Cấu hình Environment
```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://db-host:3306/notification
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka1:9092,kafka2:9092
MAIL_SMTP_HOST: smtp.gmail.com
MAIL_SMTP_USERNAME: notifications@example.com
```


