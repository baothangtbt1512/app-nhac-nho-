Dưới đây là bản đặc tả yêu cầu phần mềm (SRS) cho dự án NAGU – Never Give Up Reminder. Tài liệu được viết theo cấu trúc có thể sử dụng làm đầu vào cho quá trình phân tích, thiết kế UI/UX, phát triển bằng Android Studio hoặc giao cho AI coding agent như Antigravity.

PRODUCT SPECIFICATION

Version 1.0

# NAGU

Never Give Up Reminder

Ứng dụng nhắc nhở liên tục, quản lý công việc và theo dõi thói quen trên Android.

[Task Reminder App – Clean & Intentional UX by Aminul Islam | Head of Design @ Uxovian for Uxovian on Dribbble](https://images.openai.com/static-rsc-4/jaiTPy3ocGo0xq-OU7WK2qz2wILnekx7H3jPug4sYsfB6P8QIENZdel0WwDniSPODqhmu2ejKHizGUlAKfjVsXWz2UMOMitcFZmqoP0eVyIwv5WgjtTcz6_dlLPRbujqP7QjhS7oqlzExtL2yS3lcWc2chY60wyCe3l9IU4SZzQ?purpose=inline)

[dribbble.com](https://dribbble.com/shots/26926861-Task-Reminder-App-Clean-Intentional-UX?utm_source=chatgpt.com)

Nền tảng

Android APK

Kiến trúc

Offline-first / Local-only

Ngôn ngữ

Kotlin

Database

Room / SQLite

# TÀI LIỆU ĐẶC TẢ YÊU CẦU PHẦN MỀM (SRS)

Dự án: NAGU – Never Give Up Reminder

Phiên bản: 1.0

Loại sản phẩm: Ứng dụng Android Native

Mô hình triển khai: Hoạt động hoàn toàn Offline, xử lý trực tiếp trên thiết bị người dùng.

# 1. TỔNG QUAN DỰ ÁN

## 1.1. Giới thiệu

NAGU là ứng dụng Android được phát triển nhằm hỗ trợ người dùng quản lý lời nhắc, công việc cá nhân và thói quen thông qua hệ thống thông báo chủ động.

Khác với các ứng dụng nhắc việc thông thường chỉ gửi một thông báo tại thời điểm được thiết lập, NAGU tập trung vào cơ chế nhắc nhở liên tục (Persistent Reminder). Khi một lời nhắc đến thời điểm kích hoạt, ứng dụng sẽ tiếp tục gửi thông báo theo chu kỳ được cấu hình cho đến khi người dùng thực hiện một hành động xử lý hợp lệ.

Ứng dụng hướng đến việc giảm tình trạng quên công việc, trì hoãn nhiệm vụ và bỏ lỡ các hoạt động quan trọng trong sinh hoạt, học tập và công việc.

## 1.2. Mục tiêu sản phẩm

Các mục tiêu chính:

1. Xây dựng ứng dụng nhắc nhở có khả năng hoạt động độc lập, không phụ thuộc máy chủ bên ngoài.
2. Đảm bảo lời nhắc được kích hoạt theo thời gian người dùng thiết lập.
3. Hỗ trợ nhắc nhở lặp lại liên tục cho đến khi được xác nhận hoàn thành.
4. Cung cấp nhiều cấp độ cảnh báo bằng âm thanh, rung và thông báo toàn màn hình.
5. Hỗ trợ quản lý công việc, thói quen và lịch nhắc cá nhân.
6. Lưu trữ toàn bộ dữ liệu trực tiếp trên thiết bị.
7. Đảm bảo dữ liệu và lịch nhắc có thể khôi phục sau khi điện thoại khởi động lại.
8. Cung cấp giao diện trực quan, dễ sử dụng và có khả năng tùy chỉnh.

## 1.3. Đối tượng sử dụng

- Học sinh, sinh viên cần quản lý lịch học và thời gian ôn tập.
- Nhân viên văn phòng cần quản lý công việc và deadline.
- Người thường xuyên quên các nhiệm vụ cá nhân.
- Người muốn xây dựng và duy trì thói quen.
- Người cần hệ thống báo thức nhắc nhở nhiều lần.

## 1.4. Phạm vi sản phẩm

NAGU phiên bản 1.0 tập trung vào ứng dụng Android Native.

Phạm vi bao gồm:

- Quản lý lời nhắc.
- Quản lý công việc.
- Lịch nhắc định kỳ.
- Hệ thống báo thức và thông báo.
- Nhắc nhở liên tục.
- Quản lý thói quen.
- Thống kê.
- Cài đặt cá nhân.
- Sao lưu và khôi phục dữ liệu cục bộ.

Không nằm trong phạm vi phiên bản 1.0:

- Đồng bộ dữ liệu qua Cloud.
- Đăng nhập tài khoản.
- Quản lý nhiều người dùng.
- Tích hợp máy chủ.
- Tích hợp AI trực tuyến.
- Gửi thông báo qua Telegram hoặc dịch vụ bên thứ ba.

# 2. NGUYÊN TẮC VẬN HÀNH

## 2.1. Nguyên tắc Offline hoàn toàn

NAGU phải hoạt động độc lập trên thiết bị Android.

Các yêu cầu bắt buộc:

- Không yêu cầu kết nối Internet.
- Không có backend hoặc server.
- Không yêu cầu tài khoản.
- Không sử dụng API trực tuyến.
- Không gửi dữ liệu người dùng ra bên ngoài.
- Không chứa quảng cáo hoặc SDK phân tích hành vi.
- Toàn bộ dữ liệu được lưu bằng Room Database.
- Các chức năng cốt lõi phải sử dụng được khi thiết bị bật chế độ máy bay.

Ứng dụng không khai báo quyền `INTERNET` trong AndroidManifest.

## 2.2. Nguyên tắc nhắc nhở liên tục

Đây là nguyên tắc cốt lõi của NAGU.

Một lời nhắc sau khi được kích hoạt sẽ chuyển sang trạng thái đang chờ xử lý.

Hệ thống tiếp tục nhắc theo chu kỳ cho đến khi xảy ra một trong các trường hợp:

- Người dùng xác nhận hoàn thành.
- Người dùng hủy lời nhắc.
- Người dùng tạm dừng lời nhắc.
- Người dùng thay đổi lịch nhắc.
- Lời nhắc được xử lý theo quy tắc riêng đã thiết lập.

Việc vuốt bỏ thông báo không được xem là hành động hoàn thành.

## 2.3. Nguyên tắc kiểm soát

Người dùng có quyền:

- Tùy chỉnh tần suất nhắc.
- Tùy chỉnh âm thanh.
- Tùy chỉnh rung.
- Chọn mức độ cảnh báo.
- Thiết lập thời gian hoãn.
- Tạm dừng hoặc tiếp tục.
- Thiết lập giới hạn thời gian nhắc nếu muốn.

Ứng dụng không tự ý xóa hoặc đánh dấu hoàn thành công việc.

# 3. ĐẶC TẢ CHỨC NĂNG

## 3.1. Module quản lý lời nhắc (Reminder Management)

P0 – Core

Đây là module trung tâm của ứng dụng.

| Mã     | Chức năng    | Mô tả                          |
| ------ | ------------ | ------------------------------ |
| FR-001 | Tạo lời nhắc | Tạo lời nhắc mới               |
| FR-002 | Chỉnh sửa    | Thay đổi nội dung và thời gian |
| FR-003 | Xóa          | Xóa lời nhắc                   |
| FR-004 | Nhân bản     | Sao chép lời nhắc              |
| FR-005 | Tạm dừng     | Tạm ngừng hoạt động            |
| FR-006 | Tiếp tục     | Kích hoạt lại lời nhắc         |
| FR-007 | Hoàn thành   | Xác nhận hoàn thành            |
| FR-008 | Hủy          | Hủy lời nhắc                   |
| FR-009 | Tìm kiếm     | Tìm theo tên hoặc nội dung     |
| FR-010 | Phân loại    | Gắn danh mục cho lời nhắc      |

Thông tin của một lời nhắc bao gồm:

- Tiêu đề.
- Nội dung mô tả.
- Ngày thực hiện.
- Giờ thực hiện.
- Chế độ lặp.
- Khoảng thời gian nhắc lại.
- Mức độ ưu tiên.
- Danh mục.
- Âm thanh.
- Kiểu rung.
- Chế độ cảnh báo.
- Trạng thái hiện tại.

## 3.2. Module thiết lập lịch nhắc (Scheduling)

P0 – Core

Hệ thống phải hỗ trợ các chế độ:

| Chế độ                | Mô tả                       |
| --------------------- | --------------------------- |
| Một lần               | Kích hoạt tại một thời điểm |
| Hằng ngày             | Lặp mỗi ngày                |
| Hằng tuần             | Chọn các thứ trong tuần     |
| Hằng tháng            | Chọn ngày trong tháng       |
| Hằng năm              | Lặp theo ngày và tháng      |
| Theo khoảng thời gian | Lặp theo số phút hoặc giờ   |
| Tùy chỉnh             | Thiết lập lịch riêng        |

Đối với lời nhắc lặp theo khoảng thời gian, hệ thống hỗ trợ:

- 1 phút.
- 3 phút.
- 5 phút.
- 10 phút.
- 15 phút.
- 30 phút.
- 60 phút.
- Tùy chỉnh số phút.

Các chu kỳ trên phải áp dụng được cho cơ chế nhắc lại sau khi đến hạn.

## 3.3. Module Persistent Reminder

P0 – Tính năng cốt lõi nhất

### 3.3.1. Mô tả

Khi lời nhắc được kích hoạt, hệ thống bắt đầu chu trình thông báo liên tục.

Ví dụ:

| Thời gian | Hành động     |
| --------- | ------------- |
| 08:00     | Nhắc lần đầu  |
| 08:05     | Nhắc lần hai  |
| 08:10     | Nhắc lần ba   |
| 08:15     | Nhắc lần bốn  |
| 08:20     | Tiếp tục nhắc |
| 08:25     | Tiếp tục nhắc |

Chu trình tiếp tục cho đến khi người dùng xác nhận hoàn thành hoặc thực hiện hành động xử lý khác.

### 3.3.2. Các hành động trên lời nhắc

Mỗi thông báo cần có các nút thao tác nhanh:

- Hoàn thành: Dừng chu trình nhắc.
- Hoãn: Tạm dừng trong một khoảng thời gian.
- Mở chi tiết: Mở trang thông tin lời nhắc.
- Tạm dừng: Ngừng nhắc đến khi được kích hoạt lại.

### 3.3.3. Cơ chế Snooze

Người dùng có thể lựa chọn:

- 5 phút.
- 10 phút.
- 15 phút.
- 30 phút.
- 1 giờ.
- Tùy chỉnh.

Sau thời gian hoãn, hệ thống tiếp tục chu trình nhắc.

### 3.3.4. Giới hạn

Việc vuốt bỏ thông báo không được thay đổi trạng thái lời nhắc.

Ứng dụng phải sử dụng cơ chế lập lịch tiếp theo độc lập với trạng thái hiển thị thông báo.

## 3.4. Module thông báo nâng cao (Advanced Notification)

P0 – Core

Các chức năng:

| Mã     | Chức năng               | Mô tả                                 |
| ------ | ----------------------- | ------------------------------------- |
| FR-011 | Persistent Notification | Duy trì thông báo trong khi chờ xử lý |
| FR-012 | Heads-up                | Hiển thị thông báo nổi                |
| FR-013 | Lock Screen             | Hiển thị trên màn hình khóa           |
| FR-014 | Custom Sound            | Chọn âm thanh                         |
| FR-015 | Vibration               | Tùy chỉnh rung                        |
| FR-016 | Alarm Mode              | Chế độ báo thức                       |
| FR-017 | Repeat Sound            | Phát lại âm thanh                     |
| FR-018 | Notification Actions    | Thao tác nhanh                        |
| FR-019 | Escalation              | Tăng mức cảnh báo                     |
| FR-020 | Full Screen Alert       | Giao diện cảnh báo toàn màn hình      |

### 3.4.1. Hệ thống Escalation

Hệ thống hỗ trợ tối thiểu 4 cấp độ:

| Cấp độ  | Điều kiện              | Hành vi                |
| ------- | ---------------------- | ---------------------- |
| Level 1 | Lần nhắc đầu           | Thông báo bình thường  |
| Level 2 | Chưa xử lý             | Thông báo + rung       |
| Level 3 | Tiếp tục chưa xử lý    | Heads-up + âm thanh    |
| Level 4 | Quá thời gian cấu hình | Báo thức theo cấu hình |

Người dùng được phép thiết lập thời gian chuyển cấp và vô hiệu hóa từng cấp.

Lưu ý: Chế độ toàn màn hình và khả năng phát âm thanh khi màn hình khóa phải tuân theo quyền và chính sách Android. Ứng dụng không được tự ý vượt qua chế độ Không làm phiền hoặc quyền hệ thống.

## 3.5. Module quản lý công việc (Task Management)

P1 – Enhancement

Chức năng:

- Tạo công việc.
- Chỉnh sửa công việc.
- Xóa công việc.
- Gắn danh mục.
- Thiết lập deadline.
- Đánh dấu hoàn thành.
- Thiết lập mức độ ưu tiên.
- Tạo checklist.
- Theo dõi công việc quá hạn.
- Lọc công việc theo trạng thái.

Các trạng thái công việc:

1. Chưa thực hiện.
2. Đang thực hiện.
3. Đang chờ xử lý.
4. Hoàn thành.
5. Quá hạn.
6. Đã hủy.

## 3.6. Module Habit Tracking

P1 – Enhancement

Hỗ trợ xây dựng và theo dõi thói quen.

Các chức năng:

- Tạo thói quen mới.
- Thiết lập tần suất.
- Đánh dấu hoàn thành.
- Theo dõi chuỗi ngày liên tiếp.
- Thống kê tỷ lệ duy trì.
- Xem lịch sử.
- Nhắc nhở khi chưa thực hiện.

Ví dụ:

| Thói quen     | Tần suất    |
| ------------- | ----------- |
| Học tiếng Anh | Hằng ngày   |
| Đọc sách      | Hằng ngày   |
| Tập thể dục   | 3 buổi/tuần |
| Uống nước     | Mỗi 2 giờ   |

## 3.7. Module Calendar

P1 – Enhancement

Lịch hiển thị lời nhắc và công việc theo ngày, tuần và tháng.

Các chức năng:

- Xem lịch theo tháng.
- Xem lịch theo tuần.
- Xem lịch theo ngày.
- Hiển thị số lượng công việc.
- Phân biệt công việc hoàn thành và quá hạn.
- Truy cập nhanh chi tiết lời nhắc.

## 3.8. Module Statistics

P1 – Enhancement

Thống kê:

- Tổng số lời nhắc.
- Tổng số công việc.
- Số lượng đã hoàn thành.
- Số lượng quá hạn.
- Tỷ lệ hoàn thành.
- Số lần hoãn.
- Thói quen duy trì tốt nhất.
- Biểu đồ tiến độ theo ngày và tuần.

Tất cả số liệu được tính từ dữ liệu cục bộ.

## 3.9. Module Quick Add và Voice Input

P2 – Advanced

Hỗ trợ tạo lời nhắc nhanh bằng văn bản hoặc giọng nói.

Ví dụ:

"Nhắc tôi học tiếng Anh lúc 8 giờ tối mỗi ngày, lặp lại sau 10 phút nếu chưa hoàn thành."

Ứng dụng phân tích thành:

| Thuộc tính     | Giá trị       |
| -------------- | ------------- |
| Tiêu đề        | Học tiếng Anh |
| Giờ            | 20:00         |
| Tần suất       | Hằng ngày     |
| Chu kỳ nhắc    | 10 phút       |
| Điều kiện dừng | Hoàn thành    |

Voice Input có thể sử dụng bộ nhận dạng giọng nói cục bộ nếu thiết bị hỗ trợ. Không được phụ thuộc vào dịch vụ nhận dạng trực tuyến trong chế độ Offline.

## 3.10. Module Backup & Restore

P1 – Enhancement

Các chức năng:

- Xuất toàn bộ dữ liệu JSON.
- Nhập dữ liệu từ bản sao lưu.
- Kiểm tra tính hợp lệ của dữ liệu.
- Phát hiện bản ghi trùng lặp.
- Cho phép chọn dữ liệu cần khôi phục.
- Sao lưu cấu hình ứng dụng.

Sao lưu được thực hiện qua bộ chọn tệp của Android, không cần máy chủ.

# 4. ĐẶC TẢ GIAO DIỆN UI/UX

## 4.1. Nguyên tắc thiết kế

NAGU sử dụng phong cách Minimal Productivity kết hợp Material 3.

Yêu cầu:

- Giao diện hiện đại.
- Ít thao tác.
- Dễ nhìn.
- Tối ưu màn hình điện thoại.
- Hỗ trợ Dark Mode và Light Mode.
- Có thiết kế nhất quán.
- Các hành động quan trọng dễ tiếp cận.

## 4.2. Cấu trúc điều hướng

Ứng dụng sử dụng Bottom Navigation gồm 5 mục:

| Mục        | Nội dung  |
| ---------- | --------- |
| Home       | Trang chủ |
| Calendar   | Lịch      |
| Habits     | Thói quen |
| Statistics | Thống kê  |
| Settings   | Cài đặt   |

## 4.3. Màn hình chính

Trang chủ cần hiển thị:

- Lời chào.
- Ngày hiện tại.
- Tổng số công việc hôm nay.
- Số công việc hoàn thành.
- Số công việc quá hạn.
- Danh sách lời nhắc sắp đến.
- Danh sách lời nhắc đang chờ xử lý.
- Nút tạo lời nhắc nhanh.

Các bộ lọc:

- Tất cả.
- Hôm nay.
- Sắp đến.
- Đang nhắc.
- Quá hạn.
- Hoàn thành.

## 4.4. Màn hình tạo lời nhắc

Các trường dữ liệu:

1. Tiêu đề.
2. Mô tả.
3. Ngày.
4. Giờ.
5. Lặp lại.
6. Khoảng nhắc lại.
7. Mức ưu tiên.
8. Danh mục.
9. Âm thanh.
10. Rung.
11. Chế độ cảnh báo.
12. Giới hạn nhắc.

## 4.5. Màn hình chi tiết lời nhắc

Hiển thị:

- Thông tin lời nhắc.
- Thời gian kích hoạt.
- Chu kỳ lặp.
- Số lần đã nhắc.
- Trạng thái.
- Lịch sử xử lý.
- Nút hoàn thành.
- Nút hoãn.
- Nút chỉnh sửa.
- Nút tạm dừng.

## 4.6. Màn hình cài đặt

Các nhóm:

- Cài đặt giao diện.
- Cài đặt âm thanh.
- Cài đặt rung.
- Cài đặt thông báo.
- Cài đặt báo thức.
- Cài đặt mặc định cho lời nhắc.
- Quản lý quyền Android.
- Sao lưu và khôi phục.
- Thông tin ứng dụng.

# 5. ĐẶC TẢ KIẾN TRÚC KỸ THUẬT

## 5.1. Công nghệ sử dụng

| Thành phần            | Công nghệ                  |
| --------------------- | -------------------------- |
| Ngôn ngữ              | Kotlin                     |
| UI Framework          | Jetpack Compose            |
| Design System         | Material 3                 |
| Architecture          | MVVM + Clean Architecture  |
| Database              | Room / SQLite              |
| Scheduler             | AlarmManager               |
| Background Processing | WorkManager                |
| Notification          | Android Notification API   |
| Audio                 | MediaPlayer / AudioManager |
| Dependency Injection  | Hilt                       |
| Preferences           | DataStore                  |
| Serialization         | Kotlin Serialization       |
| Build System          | Gradle Kotlin DSL          |

## 5.2. Kiến trúc tổng thể

```
Presentation Layer
       |
       v
ViewModel
       |
       v
Domain Layer
       |
       v
Repository Interface
       |
       v
Data Layer
       |
       +------ Room Database
       |
       +------ DataStore
       |
       +------ AlarmManager
       |
       +------ Notification Manager
       |
       +------ Audio Manager
```

Ứng dụng phải tách biệt giao diện, nghiệp vụ và dữ liệu để dễ bảo trì, kiểm thử và mở rộng.

## 5.3. Cấu trúc thư mục

```
NAGU/
├── app/
│   └── src/main/
│       ├── java/com/nagu/reminder/
│       │   ├── core/
│       │   │   ├── common/
│       │   │   ├── constants/
│       │   │   └── utils/
│       │   ├── data/
│       │   │   ├── database/
│       │   │   ├── dao/
│       │   │   ├── entity/
│       │   │   ├── repository/
│       │   │   └── backup/
│       │   ├── domain/
│       │   │   ├── model/
│       │   │   ├── repository/
│       │   │   └── usecase/
│       │   ├── alarm/
│       │   ├── notification/
│       │   ├── audio/
│       │   ├── receiver/
│       │   ├── service/
│       │   ├── navigation/
│       │   └── ui/
│       │       ├── home/
│       │       ├── reminder/
│       │       ├── calendar/
│       │       ├── habits/
│       │       ├── statistics/
│       │       ├── settings/
│       │       └── components/
│       └── AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
```

# 6. THIẾT KẾ CƠ SỞ DỮ LIỆU

## 6.1. Bảng Reminders

Lưu thông tin lời nhắc.

| Trường            | Kiểu dữ liệu | Mô tả               |
| ----------------- | ------------ | ------------------- |
| id                | Long         | Khóa chính          |
| title             | String       | Tiêu đề             |
| description       | String?      | Mô tả               |
| scheduledAt       | Long         | Thời điểm kích hoạt |
| repeatType        | String       | Kiểu lặp            |
| repeatInterval    | Int          | Khoảng lặp          |
| priority          | Int          | Mức ưu tiên         |
| categoryId        | Long?        | Danh mục            |
| soundId           | String?      | Âm thanh            |
| vibrationEnabled  | Boolean      | Bật rung            |
| escalationEnabled | Boolean      | Bật tăng cấp        |
| maxRepeatCount    | Int?         | Số lần nhắc tối đa  |
| status            | String       | Trạng thái          |
| createdAt         | Long         | Ngày tạo            |
| updatedAt         | Long         | Ngày cập nhật       |

## 6.2. Bảng ReminderEvents

Lưu lịch sử kích hoạt và xử lý.

| Trường     | Kiểu dữ liệu | Mô tả        |
| ---------- | ------------ | ------------ |
| id         | Long         | Khóa chính   |
| reminderId | Long         | ID lời nhắc  |
| eventType  | String       | Loại sự kiện |
| eventTime  | Long         | Thời điểm    |
| note       | String?      | Ghi chú      |

Các loại sự kiện:

- TRIGGERED.
- NOTIFIED.
- SNOOZED.
- COMPLETED.
- PAUSED.
- RESUMED.
- CANCELLED.
- MISSED.

## 6.3. Bảng Tasks

Lưu công việc.

Các trường chính:

- id.
- title.
- description.
- deadline.
- priority.
- status.
- categoryId.
- createdAt.
- completedAt.

## 6.4. Bảng Habits

Lưu thông tin thói quen.

Các trường chính:

- id.
- name.
- description.
- frequency.
- targetPerWeek.
- startDate.
- isActive.

## 6.5. Bảng HabitLogs

Lưu lịch sử thực hiện thói quen.

Các trường chính:

- id.
- habitId.
- completedAt.
- completionDate.
- note.

## 6.6. Bảng Categories

Lưu danh mục:

- id.
- name.
- color.
- icon.
- createdAt.

## 6.7. Bảng Settings

Lưu cấu hình cá nhân:

- theme.
- defaultSound.
- defaultVibration.
- defaultSnooze.
- defaultRepeatInterval.
- escalationSettings.
- firstDayOfWeek.

# 7. ĐẶC TẢ QUY TRÌNH NGHIỆP VỤ

## 7.1. Quy trình tạo lời nhắc

\#chatgpt-mermaid-\_r_2ae\_{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;fill:rgb(237, 237, 237);}@keyframes edge-animation-frame{from{stroke-dashoffset:0;}}@keyframes dash{to{stroke-dashoffset:0;}}#chatgpt-mermaid-\_r_2ae\_ .edge-animation-slow{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 50s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_2ae\_ .edge-animation-fast{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 20s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_2ae\_ .error-icon{fill:rgb(48, 48, 48);}#chatgpt-mermaid-\_r_2ae\_ .error-text{fill:rgb(237, 237, 237);stroke:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2ae\_ .edge-thickness-normal{stroke-width:1px;}#chatgpt-mermaid-\_r_2ae\_ .edge-thickness-thick{stroke-width:3.5px;}#chatgpt-mermaid-\_r_2ae\_ .edge-pattern-solid{stroke-dasharray:0;}#chatgpt-mermaid-\_r_2ae\_ .edge-thickness-invisible{stroke-width:0;fill:none;}#chatgpt-mermaid-\_r_2ae\_ .edge-pattern-dashed{stroke-dasharray:3;}#chatgpt-mermaid-\_r_2ae\_ .edge-pattern-dotted{stroke-dasharray:2;}#chatgpt-mermaid-\_r_2ae\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2ae\_ .marker.cross{stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2ae\_ svg{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;}#chatgpt-mermaid-\_r_2ae\_ p{margin:0;}#chatgpt-mermaid-\_r_2ae\_ .label{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2ae\_ .cluster-label text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2ae\_ .cluster-label span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2ae\_ .cluster-label span p{background-color:transparent;}#chatgpt-mermaid-\_r_2ae\_ .label text,#chatgpt-mermaid-\_r_2ae\_ span{fill:rgb(237, 237, 237);color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2ae\_ .node rect,#chatgpt-mermaid-\_r_2ae\_ .node circle,#chatgpt-mermaid-\_r_2ae\_ .node ellipse,#chatgpt-mermaid-\_r_2ae\_ .node polygon,#chatgpt-mermaid-\_r_2ae\_ .node path{fill:rgb(9, 23, 44);stroke:rgb(31, 78, 148);stroke-width:1px;}#chatgpt-mermaid-\_r_2ae\_ .rough-node .label text,#chatgpt-mermaid-\_r_2ae\_ .node .label text,#chatgpt-mermaid-\_r_2ae\_ .image-shape .label,#chatgpt-mermaid-\_r_2ae\_ .icon-shape .label{text-anchor:middle;}#chatgpt-mermaid-\_r_2ae\_ .node .katex path{fill:#000;stroke:#000;stroke-width:1px;}#chatgpt-mermaid-\_r_2ae\_ .rough-node .label,#chatgpt-mermaid-\_r_2ae\_ .node .label,#chatgpt-mermaid-\_r_2ae\_ .image-shape .label,#chatgpt-mermaid-\_r_2ae\_ .icon-shape .label{text-align:center;}#chatgpt-mermaid-\_r_2ae\_ .node.clickable{cursor:pointer;}#chatgpt-mermaid-\_r_2ae\_ .root .anchor path{fill:rgb(175, 175, 175)!important;stroke-width:0;stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2ae\_ .arrowheadPath{fill:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2ae\_ .edgePath .path{stroke:rgb(175, 175, 175);stroke-width:1px;}#chatgpt-mermaid-\_r_2ae\_ .flowchart-link{stroke:rgb(175, 175, 175);fill:none;}#chatgpt-mermaid-\_r_2ae\_ .edgeLabel{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_2ae\_ .edgeLabel p{background-color:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_2ae\_ .edgeLabel rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_2ae\_ .labelBkg{background-color:rgba(0, 0, 0, 0.5);}#chatgpt-mermaid-\_r_2ae\_ .cluster rect{fill:rgb(48, 48, 48);stroke:rgba(255, 255, 255, 0.15);stroke-width:1px;}#chatgpt-mermaid-\_r_2ae\_ .cluster text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2ae\_ .cluster span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2ae\_ div.mermaidTooltip{position:absolute;text-align:center;max-width:200px;padding:2px;font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:12px;background:rgb(48, 48, 48);border:1px solid rgba(255, 255, 255, 0.15);border-radius:2px;pointer-events:none;z-index:100;}#chatgpt-mermaid-\_r_2ae\_ .flowchartTitleText{text-anchor:middle;font-size:18px;fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2ae\_ rect.text{fill:none;stroke-width:0;}#chatgpt-mermaid-\_r_2ae\_ .icon-shape,#chatgpt-mermaid-\_r_2ae\_ .image-shape{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_2ae\_ .icon-shape p,#chatgpt-mermaid-\_r_2ae\_ .image-shape p{background-color:rgb(0, 0, 0);padding:2px;}#chatgpt-mermaid-\_r_2ae\_ .icon-shape .label rect,#chatgpt-mermaid-\_r_2ae\_ .image-shape .label rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_2ae\_ .label-icon{display:inline-block;height:1em;overflow:visible;vertical-align:-0.125em;}#chatgpt-mermaid-\_r_2ae\_ .node .label-icon path{fill:currentColor;stroke:revert;stroke-width:revert;}#chatgpt-mermaid-\_r_2ae\_ .node .neo-node{stroke:rgb(31, 78, 148);}#chatgpt-mermaid-\_r_2ae\_ [data-look="neo"].node rect,#chatgpt-mermaid-\_r_2ae\_ [data-look="neo"].cluster rect,#chatgpt-mermaid-\_r_2ae\_ [data-look="neo"].node polygon{stroke:url(#chatgpt-mermaid-\_r_2ae\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2ae\_ [data-look="neo"].swimlane.cluster rect{filter:none;}#chatgpt-mermaid-\_r_2ae\_ [data-look="neo"].node path{stroke:url(#chatgpt-mermaid-\_r_2ae\_-gradient);stroke-width:1px;}#chatgpt-mermaid-\_r_2ae\_ [data-look="neo"].node .outer-path{filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2ae\_ [data-look="neo"].node .neo-line path{stroke:rgb(31, 78, 148);filter:none;}#chatgpt-mermaid-\_r_2ae\_ [data-look="neo"].node circle{stroke:url(#chatgpt-mermaid-\_r_2ae\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2ae\_ [data-look="neo"].node circle .state-start{fill:#000000;}#chatgpt-mermaid-\_r_2ae\_ [data-look="neo"].icon-shape .icon{fill:url(#chatgpt-mermaid-\_r_2ae\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2ae\_ [data-look="neo"].icon-shape .icon-neo path{stroke:url(#chatgpt-mermaid-\_r_2ae\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2ae\_ .node text{font-size:14px;font-weight:600;letter-spacing:normal;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_2ae\_ .edgeLabels text{font-size:13px;font-weight:600;letter-spacing:-0.08px;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_2ae\_ .node tspan[font-weight="normal"],#chatgpt-mermaid-\_r_2ae\_ .edgeLabels tspan[font-weight="normal"]{font-weight:600;}#chatgpt-mermaid-\_r_2ae\_ .edgeLabel .label rect{opacity:1;rx:13px;ry:13px;fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-width:1px;}#chatgpt-mermaid-\_r_2ae\_ .node rect,#chatgpt-mermaid-\_r_2ae\_ .node circle,#chatgpt-mermaid-\_r_2ae\_ .node ellipse,#chatgpt-mermaid-\_r_2ae\_ .node polygon,#chatgpt-mermaid-\_r_2ae\_ .node path{fill:rgb(0, 40, 77);stroke:rgba(255, 255, 255, 0.1);stroke-width:1px;}#chatgpt-mermaid-\_r_2ae\_ .node rect{rx:16px;ry:16px;}#chatgpt-mermaid-\_r_2ae\_ .mermaid-decision .label-container{fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-dasharray:2,2;}#chatgpt-mermaid-\_r_2ae\_ .edgePaths .flowchart-link{stroke:rgb(175, 175, 175);stroke-width:1px;stroke-linecap:round;stroke-linejoin:round;}#chatgpt-mermaid-\_r_2ae\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2ae\_ :root{--mermaid-font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";}Mở ứng dụngChọn tạo lời nhắcNhập nội dungThiết lập thời gianThiết lập chu kỳLưu vào Room DatabaseĐăng ký AlarmManagerHoàn tất

## 7.2. Quy trình nhắc nhở liên tục

\#chatgpt-mermaid-\_r_2al\_{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;fill:rgb(237, 237, 237);}@keyframes edge-animation-frame{from{stroke-dashoffset:0;}}@keyframes dash{to{stroke-dashoffset:0;}}#chatgpt-mermaid-\_r_2al\_ .edge-animation-slow{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 50s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_2al\_ .edge-animation-fast{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 20s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_2al\_ .error-icon{fill:rgb(48, 48, 48);}#chatgpt-mermaid-\_r_2al\_ .error-text{fill:rgb(237, 237, 237);stroke:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2al\_ .edge-thickness-normal{stroke-width:1px;}#chatgpt-mermaid-\_r_2al\_ .edge-thickness-thick{stroke-width:3.5px;}#chatgpt-mermaid-\_r_2al\_ .edge-pattern-solid{stroke-dasharray:0;}#chatgpt-mermaid-\_r_2al\_ .edge-thickness-invisible{stroke-width:0;fill:none;}#chatgpt-mermaid-\_r_2al\_ .edge-pattern-dashed{stroke-dasharray:3;}#chatgpt-mermaid-\_r_2al\_ .edge-pattern-dotted{stroke-dasharray:2;}#chatgpt-mermaid-\_r_2al\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2al\_ .marker.cross{stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2al\_ svg{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;}#chatgpt-mermaid-\_r_2al\_ p{margin:0;}#chatgpt-mermaid-\_r_2al\_ .label{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2al\_ .cluster-label text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2al\_ .cluster-label span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2al\_ .cluster-label span p{background-color:transparent;}#chatgpt-mermaid-\_r_2al\_ .label text,#chatgpt-mermaid-\_r_2al\_ span{fill:rgb(237, 237, 237);color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2al\_ .node rect,#chatgpt-mermaid-\_r_2al\_ .node circle,#chatgpt-mermaid-\_r_2al\_ .node ellipse,#chatgpt-mermaid-\_r_2al\_ .node polygon,#chatgpt-mermaid-\_r_2al\_ .node path{fill:rgb(9, 23, 44);stroke:rgb(31, 78, 148);stroke-width:1px;}#chatgpt-mermaid-\_r_2al\_ .rough-node .label text,#chatgpt-mermaid-\_r_2al\_ .node .label text,#chatgpt-mermaid-\_r_2al\_ .image-shape .label,#chatgpt-mermaid-\_r_2al\_ .icon-shape .label{text-anchor:middle;}#chatgpt-mermaid-\_r_2al\_ .node .katex path{fill:#000;stroke:#000;stroke-width:1px;}#chatgpt-mermaid-\_r_2al\_ .rough-node .label,#chatgpt-mermaid-\_r_2al\_ .node .label,#chatgpt-mermaid-\_r_2al\_ .image-shape .label,#chatgpt-mermaid-\_r_2al\_ .icon-shape .label{text-align:center;}#chatgpt-mermaid-\_r_2al\_ .node.clickable{cursor:pointer;}#chatgpt-mermaid-\_r_2al\_ .root .anchor path{fill:rgb(175, 175, 175)!important;stroke-width:0;stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2al\_ .arrowheadPath{fill:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2al\_ .edgePath .path{stroke:rgb(175, 175, 175);stroke-width:1px;}#chatgpt-mermaid-\_r_2al\_ .flowchart-link{stroke:rgb(175, 175, 175);fill:none;}#chatgpt-mermaid-\_r_2al\_ .edgeLabel{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_2al\_ .edgeLabel p{background-color:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_2al\_ .edgeLabel rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_2al\_ .labelBkg{background-color:rgba(0, 0, 0, 0.5);}#chatgpt-mermaid-\_r_2al\_ .cluster rect{fill:rgb(48, 48, 48);stroke:rgba(255, 255, 255, 0.15);stroke-width:1px;}#chatgpt-mermaid-\_r_2al\_ .cluster text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2al\_ .cluster span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2al\_ div.mermaidTooltip{position:absolute;text-align:center;max-width:200px;padding:2px;font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:12px;background:rgb(48, 48, 48);border:1px solid rgba(255, 255, 255, 0.15);border-radius:2px;pointer-events:none;z-index:100;}#chatgpt-mermaid-\_r_2al\_ .flowchartTitleText{text-anchor:middle;font-size:18px;fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2al\_ rect.text{fill:none;stroke-width:0;}#chatgpt-mermaid-\_r_2al\_ .icon-shape,#chatgpt-mermaid-\_r_2al\_ .image-shape{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_2al\_ .icon-shape p,#chatgpt-mermaid-\_r_2al\_ .image-shape p{background-color:rgb(0, 0, 0);padding:2px;}#chatgpt-mermaid-\_r_2al\_ .icon-shape .label rect,#chatgpt-mermaid-\_r_2al\_ .image-shape .label rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_2al\_ .label-icon{display:inline-block;height:1em;overflow:visible;vertical-align:-0.125em;}#chatgpt-mermaid-\_r_2al\_ .node .label-icon path{fill:currentColor;stroke:revert;stroke-width:revert;}#chatgpt-mermaid-\_r_2al\_ .node .neo-node{stroke:rgb(31, 78, 148);}#chatgpt-mermaid-\_r_2al\_ [data-look="neo"].node rect,#chatgpt-mermaid-\_r_2al\_ [data-look="neo"].cluster rect,#chatgpt-mermaid-\_r_2al\_ [data-look="neo"].node polygon{stroke:url(#chatgpt-mermaid-\_r_2al\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2al\_ [data-look="neo"].swimlane.cluster rect{filter:none;}#chatgpt-mermaid-\_r_2al\_ [data-look="neo"].node path{stroke:url(#chatgpt-mermaid-\_r_2al\_-gradient);stroke-width:1px;}#chatgpt-mermaid-\_r_2al\_ [data-look="neo"].node .outer-path{filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2al\_ [data-look="neo"].node .neo-line path{stroke:rgb(31, 78, 148);filter:none;}#chatgpt-mermaid-\_r_2al\_ [data-look="neo"].node circle{stroke:url(#chatgpt-mermaid-\_r_2al\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2al\_ [data-look="neo"].node circle .state-start{fill:#000000;}#chatgpt-mermaid-\_r_2al\_ [data-look="neo"].icon-shape .icon{fill:url(#chatgpt-mermaid-\_r_2al\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2al\_ [data-look="neo"].icon-shape .icon-neo path{stroke:url(#chatgpt-mermaid-\_r_2al\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2al\_ .node text{font-size:14px;font-weight:600;letter-spacing:normal;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_2al\_ .edgeLabels text{font-size:13px;font-weight:600;letter-spacing:-0.08px;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_2al\_ .node tspan[font-weight="normal"],#chatgpt-mermaid-\_r_2al\_ .edgeLabels tspan[font-weight="normal"]{font-weight:600;}#chatgpt-mermaid-\_r_2al\_ .edgeLabel .label rect{opacity:1;rx:13px;ry:13px;fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-width:1px;}#chatgpt-mermaid-\_r_2al\_ .node rect,#chatgpt-mermaid-\_r_2al\_ .node circle,#chatgpt-mermaid-\_r_2al\_ .node ellipse,#chatgpt-mermaid-\_r_2al\_ .node polygon,#chatgpt-mermaid-\_r_2al\_ .node path{fill:rgb(0, 40, 77);stroke:rgba(255, 255, 255, 0.1);stroke-width:1px;}#chatgpt-mermaid-\_r_2al\_ .node rect{rx:16px;ry:16px;}#chatgpt-mermaid-\_r_2al\_ .mermaid-decision .label-container{fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-dasharray:2,2;}#chatgpt-mermaid-\_r_2al\_ .edgePaths .flowchart-link{stroke:rgb(175, 175, 175);stroke-width:1px;stroke-linecap:round;stroke-linejoin:round;}#chatgpt-mermaid-\_r_2al\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2al\_ :root{--mermaid-font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";}Đến thời điểm kích hoạtKích hoạt báo thứcHiển thị thông báo và phátcảnh báoNgười dùng xử lý?Cập nhật trạng tháiLên lịch lạiChuyển trạng thái PAUSEDGiữ trạng thái chờ xử lýLên lịch nhắc tiếp theoHoàn thànhHoãnTạm dừngVuốt bỏ / Không phản hồi

## 7.3. Quy trình khôi phục sau khi khởi động lại

\#chatgpt-mermaid-\_r_2as\_{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;fill:rgb(237, 237, 237);}@keyframes edge-animation-frame{from{stroke-dashoffset:0;}}@keyframes dash{to{stroke-dashoffset:0;}}#chatgpt-mermaid-\_r_2as\_ .edge-animation-slow{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 50s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_2as\_ .edge-animation-fast{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 20s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_2as\_ .error-icon{fill:rgb(48, 48, 48);}#chatgpt-mermaid-\_r_2as\_ .error-text{fill:rgb(237, 237, 237);stroke:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2as\_ .edge-thickness-normal{stroke-width:1px;}#chatgpt-mermaid-\_r_2as\_ .edge-thickness-thick{stroke-width:3.5px;}#chatgpt-mermaid-\_r_2as\_ .edge-pattern-solid{stroke-dasharray:0;}#chatgpt-mermaid-\_r_2as\_ .edge-thickness-invisible{stroke-width:0;fill:none;}#chatgpt-mermaid-\_r_2as\_ .edge-pattern-dashed{stroke-dasharray:3;}#chatgpt-mermaid-\_r_2as\_ .edge-pattern-dotted{stroke-dasharray:2;}#chatgpt-mermaid-\_r_2as\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2as\_ .marker.cross{stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2as\_ svg{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;}#chatgpt-mermaid-\_r_2as\_ p{margin:0;}#chatgpt-mermaid-\_r_2as\_ .label{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2as\_ .cluster-label text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2as\_ .cluster-label span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2as\_ .cluster-label span p{background-color:transparent;}#chatgpt-mermaid-\_r_2as\_ .label text,#chatgpt-mermaid-\_r_2as\_ span{fill:rgb(237, 237, 237);color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2as\_ .node rect,#chatgpt-mermaid-\_r_2as\_ .node circle,#chatgpt-mermaid-\_r_2as\_ .node ellipse,#chatgpt-mermaid-\_r_2as\_ .node polygon,#chatgpt-mermaid-\_r_2as\_ .node path{fill:rgb(9, 23, 44);stroke:rgb(31, 78, 148);stroke-width:1px;}#chatgpt-mermaid-\_r_2as\_ .rough-node .label text,#chatgpt-mermaid-\_r_2as\_ .node .label text,#chatgpt-mermaid-\_r_2as\_ .image-shape .label,#chatgpt-mermaid-\_r_2as\_ .icon-shape .label{text-anchor:middle;}#chatgpt-mermaid-\_r_2as\_ .node .katex path{fill:#000;stroke:#000;stroke-width:1px;}#chatgpt-mermaid-\_r_2as\_ .rough-node .label,#chatgpt-mermaid-\_r_2as\_ .node .label,#chatgpt-mermaid-\_r_2as\_ .image-shape .label,#chatgpt-mermaid-\_r_2as\_ .icon-shape .label{text-align:center;}#chatgpt-mermaid-\_r_2as\_ .node.clickable{cursor:pointer;}#chatgpt-mermaid-\_r_2as\_ .root .anchor path{fill:rgb(175, 175, 175)!important;stroke-width:0;stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2as\_ .arrowheadPath{fill:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2as\_ .edgePath .path{stroke:rgb(175, 175, 175);stroke-width:1px;}#chatgpt-mermaid-\_r_2as\_ .flowchart-link{stroke:rgb(175, 175, 175);fill:none;}#chatgpt-mermaid-\_r_2as\_ .edgeLabel{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_2as\_ .edgeLabel p{background-color:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_2as\_ .edgeLabel rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_2as\_ .labelBkg{background-color:rgba(0, 0, 0, 0.5);}#chatgpt-mermaid-\_r_2as\_ .cluster rect{fill:rgb(48, 48, 48);stroke:rgba(255, 255, 255, 0.15);stroke-width:1px;}#chatgpt-mermaid-\_r_2as\_ .cluster text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2as\_ .cluster span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2as\_ div.mermaidTooltip{position:absolute;text-align:center;max-width:200px;padding:2px;font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:12px;background:rgb(48, 48, 48);border:1px solid rgba(255, 255, 255, 0.15);border-radius:2px;pointer-events:none;z-index:100;}#chatgpt-mermaid-\_r_2as\_ .flowchartTitleText{text-anchor:middle;font-size:18px;fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_2as\_ rect.text{fill:none;stroke-width:0;}#chatgpt-mermaid-\_r_2as\_ .icon-shape,#chatgpt-mermaid-\_r_2as\_ .image-shape{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_2as\_ .icon-shape p,#chatgpt-mermaid-\_r_2as\_ .image-shape p{background-color:rgb(0, 0, 0);padding:2px;}#chatgpt-mermaid-\_r_2as\_ .icon-shape .label rect,#chatgpt-mermaid-\_r_2as\_ .image-shape .label rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_2as\_ .label-icon{display:inline-block;height:1em;overflow:visible;vertical-align:-0.125em;}#chatgpt-mermaid-\_r_2as\_ .node .label-icon path{fill:currentColor;stroke:revert;stroke-width:revert;}#chatgpt-mermaid-\_r_2as\_ .node .neo-node{stroke:rgb(31, 78, 148);}#chatgpt-mermaid-\_r_2as\_ [data-look="neo"].node rect,#chatgpt-mermaid-\_r_2as\_ [data-look="neo"].cluster rect,#chatgpt-mermaid-\_r_2as\_ [data-look="neo"].node polygon{stroke:url(#chatgpt-mermaid-\_r_2as\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2as\_ [data-look="neo"].swimlane.cluster rect{filter:none;}#chatgpt-mermaid-\_r_2as\_ [data-look="neo"].node path{stroke:url(#chatgpt-mermaid-\_r_2as\_-gradient);stroke-width:1px;}#chatgpt-mermaid-\_r_2as\_ [data-look="neo"].node .outer-path{filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2as\_ [data-look="neo"].node .neo-line path{stroke:rgb(31, 78, 148);filter:none;}#chatgpt-mermaid-\_r_2as\_ [data-look="neo"].node circle{stroke:url(#chatgpt-mermaid-\_r_2as\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2as\_ [data-look="neo"].node circle .state-start{fill:#000000;}#chatgpt-mermaid-\_r_2as\_ [data-look="neo"].icon-shape .icon{fill:url(#chatgpt-mermaid-\_r_2as\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2as\_ [data-look="neo"].icon-shape .icon-neo path{stroke:url(#chatgpt-mermaid-\_r_2as\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_2as\_ .node text{font-size:14px;font-weight:600;letter-spacing:normal;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_2as\_ .edgeLabels text{font-size:13px;font-weight:600;letter-spacing:-0.08px;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_2as\_ .node tspan[font-weight="normal"],#chatgpt-mermaid-\_r_2as\_ .edgeLabels tspan[font-weight="normal"]{font-weight:600;}#chatgpt-mermaid-\_r_2as\_ .edgeLabel .label rect{opacity:1;rx:13px;ry:13px;fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-width:1px;}#chatgpt-mermaid-\_r_2as\_ .node rect,#chatgpt-mermaid-\_r_2as\_ .node circle,#chatgpt-mermaid-\_r_2as\_ .node ellipse,#chatgpt-mermaid-\_r_2as\_ .node polygon,#chatgpt-mermaid-\_r_2as\_ .node path{fill:rgb(0, 40, 77);stroke:rgba(255, 255, 255, 0.1);stroke-width:1px;}#chatgpt-mermaid-\_r_2as\_ .node rect{rx:16px;ry:16px;}#chatgpt-mermaid-\_r_2as\_ .mermaid-decision .label-container{fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-dasharray:2,2;}#chatgpt-mermaid-\_r_2as\_ .edgePaths .flowchart-link{stroke:rgb(175, 175, 175);stroke-width:1px;stroke-linecap:round;stroke-linejoin:round;}#chatgpt-mermaid-\_r_2as\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_2as\_ :root{--mermaid-font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";}Thiết bị khởi độngBOOT_COMPLETED ReceiverĐọc Room DatabaseLọc lời nhắc còn hiệu lựcTính thời điểm tiếp theoĐăng ký lại AlarmManagerHoạt động bình thường

## 7.4. Quy trình xử lý lời nhắc quá hạn

Khi thiết bị không hoạt động hoặc không thể kích hoạt báo thức đúng thời điểm, ứng dụng cần đối chiếu lịch đã lưu với thời gian hiện tại khi có cơ hội thực thi.

Các trường hợp:

- Lời nhắc một lần đã quá hạn: chuyển sang trạng thái cần xử lý hoặc bỏ lỡ theo cấu hình.
- Lời nhắc định kỳ: tính lần kích hoạt tiếp theo theo lịch.
- Lời nhắc đang trong chu trình liên tục: tiếp tục chu trình theo quy tắc phục hồi.
- Lời nhắc đã hoàn thành: không được kích hoạt lại.

# 8. YÊU CẦU PHI CHỨC NĂNG

## 8.1. Hiệu năng

- Thời gian mở ứng dụng mục tiêu dưới 2 giây trên thiết bị Android tầm trung.
- Các thao tác CRUD phải phản hồi nhanh.
- Không thực hiện truy vấn database nặng trên Main Thread.
- Không để giao diện bị treo khi có nhiều lời nhắc.
- Hỗ trợ quản lý tối thiểu 1.000 lời nhắc.

## 8.2. Độ tin cậy

- Lưu dữ liệu trước khi đăng ký báo thức.
- Có khả năng khôi phục lịch nhắc sau reboot.
- Không tạo nhiều báo thức trùng lặp.
- Đảm bảo thao tác hoàn thành có hiệu lực ngay.
- Có cơ chế phát hiện và xử lý báo thức đã lỗi thời.
- Có cơ chế đồng bộ trạng thái giữa Room và AlarmManager.

## 8.3. Bảo mật và quyền riêng tư

- Không gửi dữ liệu ra ngoài.
- Không yêu cầu tài khoản.
- Không thu thập dữ liệu phân tích.
- Chỉ yêu cầu các quyền cần thiết.
- Dữ liệu sao lưu phải do người dùng chủ động xuất.

## 8.4. Khả năng tương thích

Mục tiêu tương thích: Android 8.0 trở lên, với kiểm thử trên nhiều phiên bản Android thực tế.

Ứng dụng phải có cơ chế xử lý khác biệt về:

- Exact Alarm permission.
- Notification permission.
- Full-screen intent permission.
- Background execution limits.
- Battery optimization.
- Boot receiver.
- Notification channel.

Không được cam kết báo thức chính xác tuyệt đối trên mọi thiết bị do giới hạn của hệ điều hành và chính sách quản lý pin từ nhà sản xuất.

# 9. QUẢN LÝ QUYỀN ANDROID

Các quyền dự kiến:

| Permission             | Mục đích                                               |
| ---------------------- | ------------------------------------------------------ |
| POST_NOTIFICATIONS     | Hiển thị thông báo                                     |
| SCHEDULE_EXACT_ALARM   | Đăng ký báo thức chính xác khi phù hợp                 |
| USE_EXACT_ALARM        | Chỉ sử dụng nếu đáp ứng điều kiện chính sách phân phối |
| RECEIVE_BOOT_COMPLETED | Khôi phục lịch sau reboot                              |
| VIBRATE                | Rung                                                   |
| USE_FULL_SCREEN_INTENT | Cảnh báo toàn màn hình khi đủ điều kiện                |

Lưu ý: Không phải tất cả quyền đều được khai báo và sử dụng đồng thời. Quyền phải được lựa chọn theo Android SDK mục tiêu và chính sách Google Play. Nếu phân phối APK trực tiếp, vẫn phải tuân thủ các giới hạn kỹ thuật của Android.

# 10. TIÊU CHÍ KIỂM THỬ VÀ NGHIỆM THU

## 10.1. Kiểm thử chức năng

| ID     | Test case                  | Kết quả mong đợi         |
| ------ | -------------------------- | ------------------------ |
| TC-001 | Tạo lời nhắc               | Lưu thành công           |
| TC-002 | Sửa lời nhắc               | Dữ liệu cập nhật         |
| TC-003 | Xóa lời nhắc               | Không còn được kích hoạt |
| TC-004 | Kích hoạt đúng giờ         | Có thông báo             |
| TC-005 | Nhắc lặp                   | Đúng chu kỳ              |
| TC-006 | Vuốt bỏ thông báo          | Vẫn tiếp tục nhắc        |
| TC-007 | Nhấn hoàn thành            | Dừng nhắc                |
| TC-008 | Snooze                     | Nhắc lại đúng thời điểm  |
| TC-009 | Tạm dừng                   | Không phát cảnh báo      |
| TC-010 | Khởi động lại              | Khôi phục lịch           |
| TC-011 | Mất Internet               | Vẫn hoạt động            |
| TC-012 | Đổi Dark/Light             | Giao diện cập nhật       |
| TC-013 | Xuất dữ liệu               | Tạo file JSON            |
| TC-014 | Nhập dữ liệu               | Khôi phục chính xác      |
| TC-015 | Có nhiều lời nhắc cùng lúc | Xử lý độc lập            |

## 10.2. Kiểm thử tình huống đặc biệt

- Tắt màn hình trong nhiều giờ.
- Đóng ứng dụng khỏi danh sách gần đây.
- Thiết bị khởi động lại.
- Thay đổi múi giờ.
- Thay đổi ngày và giờ hệ thống.
- Bật chế độ tiết kiệm pin.
- Thu hồi quyền thông báo.
- Thu hồi quyền báo thức chính xác.
- Force Stop ứng dụng.
- Xóa dữ liệu ứng dụng.
- Cài đặt lại APK.

Các trường hợp không thể hoạt động do giới hạn hệ điều hành phải được ghi nhận và thông báo rõ cho người dùng.

## 10.3. Điều kiện nghiệm thu MVP

MVP được coi là đạt yêu cầu khi:

1. Hoàn thành CRUD lời nhắc.
2. Hoạt động được cơ chế nhắc liên tục.
3. Hỗ trợ ít nhất 5 kiểu chu kỳ nhắc.
4. Có âm thanh và rung.
5. Có Snooze và Complete.
6. Vuốt thông báo không làm mất trạng thái lời nhắc.
7. Khôi phục lịch sau khi reboot trong điều kiện quyền được cấp.
8. Sử dụng được khi Offline.
9. Không yêu cầu tài khoản hoặc server.
10. Build thành công APK và cài đặt được trên thiết bị Android mục tiêu.

# 11. KẾ HOẠCH PHÁT TRIỂN

Dự án chia thành 8 giai đoạn.

| Giai đoạn | Hạng mục                        | Sản phẩm bàn giao          |
| --------- | ------------------------------- | -------------------------- |
| Phase 0   | Chốt SRS và kiến trúc           | Tài liệu kỹ thuật          |
| Phase 1   | Khởi tạo Android Project        | Project chạy được          |
| Phase 2   | UI/UX và Navigation             | Bộ giao diện hoàn chỉnh    |
| Phase 3   | Room Database                   | CRUD và repository         |
| Phase 4   | AlarmManager                    | Hệ thống lập lịch          |
| Phase 5   | Notification và Audio           | Nhắc liên tục              |
| Phase 6   | Recovery và Permissions         | Khôi phục và quản lý quyền |
| Phase 7   | Task, Habit, Statistics, Backup | Các module mở rộng         |
| Phase 8   | Kiểm thử và phát hành           | APK Release                |

# 12. ĐỊNH HƯỚNG PHÁT TRIỂN TƯƠNG LAI

Các chức năng có thể bổ sung trong phiên bản tiếp theo:

- Nhận diện câu lệnh tạo lời nhắc bằng giọng nói Offline.
- Widget hiển thị lời nhắc.
- Quick Settings Tile.
- Tích hợp lịch hệ thống Android.
- Giao diện đồng hồ báo thức nâng cao.
- Chế độ tập trung.
- Quản lý mục tiêu dài hạn.
- Theo dõi thói quen nâng cao.
- Tùy chỉnh nhiều profile nhắc nhở.
- Xuất báo cáo PDF.
- Mã hóa bản sao lưu bằng mật khẩu.

# 13. QUY ĐỊNH ĐỐI VỚI QUÁ TRÌNH TRIỂN KHAI

Các nguyên tắc bắt buộc khi phát triển dự án:

13.1. Nguyên tắc kiến trúc

- Sử dụng Kotlin và Jetpack Compose.
- Tuân thủ MVVM và phân tách layer.
- Không đưa logic nghiệp vụ trực tiếp vào Composable.
- Không truy vấn database trên Main Thread.
- Sử dụng Repository để quản lý dữ liệu.
- Tách riêng Alarm Engine và Notification Engine.

13.2. Nguyên tắc code

- Code phải có thể biên dịch.
- Không sử dụng dữ liệu giả để thay thế chức năng thật.
- Không tạo nút giao diện không có hành động.
- Không để TODO ở các chức năng thuộc phạm vi đã nghiệm thu.
- Có xử lý lỗi và trạng thái loading phù hợp.
- Có unit test cho logic nghiệp vụ quan trọng.

13.3. Nguyên tắc bàn giao

Mỗi giai đoạn phải cung cấp:

1. Danh sách file được tạo hoặc chỉnh sửa.
2. Nội dung code đầy đủ.
3. Hướng dẫn cấu hình.
4. Hướng dẫn chạy ứng dụng.
5. Hướng dẫn build APK.
6. Kết quả kiểm thử.
7. Các lỗi còn tồn tại nếu có.

Không chuyển sang giai đoạn tiếp theo nếu giai đoạn hiện tại chưa đạt tiêu chí nghiệm thu.

# 14. TỔNG KẾT ĐẶC TẢ SẢN PHẨM

# NAGU – Never Give Up Reminder

Định hướng sản phẩm đã xác định

Sứ mệnh: Giúp người dùng không bỏ quên những việc quan trọng thông qua hệ thống nhắc nhở liên tục, chủ động và có thể tùy chỉnh.

Giá trị cốt lõi:

- Nhắc đến khi được xử lý.
- Hoạt động không cần Internet.
- Dữ liệu thuộc quyền kiểm soát của người dùng.
- Không phụ thuộc server bên ngoài.
- Có khả năng phục hồi lịch nhắc.
- Giao diện đơn giản, trải nghiệm tốt.

Phạm vi phiên bản 1.0

Nền tảng

Android Native

Ngôn ngữ

Kotlin

Giao diện

Jetpack Compose

Lưu trữ

Room / SQLite

Kết nối mạng

Không sử dụng

Máy chủ

Không có

Đầu ra

APK cài đặt trực tiếp

Đề xuất: Sử dụng tài liệu này làm SRS nền tảng cho dự án. Trước khi triển khai, cần chốt thêm UI Design System, các trạng thái vòng đời của lời nhắc và quy tắc xử lý các lời nhắc định kỳ khi người dùng chưa hoàn thành chu kỳ trước. Đây là ba phần có ảnh hưởng lớn nhất đến chất lượng thực tế của NAGU.