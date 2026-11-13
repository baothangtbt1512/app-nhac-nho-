# app-nhac-nho-

**Bảo Anh Nhắc Bạn** (`com.baoanh.nhacban`, phát triển theo đặc tả **NAGU – Never Give Up Reminder**) là ứng dụng Android nhắc nhở liên tục (Persistent Reminder), quản lý lịch và theo dõi thói quen hoạt động **100% Offline** (không khai báo quyền `INTERNET`).

---

## Tính năng chính

1. **Nhắc nhở liên tục chống bỏ lỡ (Persistent Reminder):**
   - Hỗ trợ lặp chu kỳ nhắc lại mỗi `1 / 3 / 5 / 10 / 15 / 30 / 60` phút đến khi xác nhận hoàn thành (`maxRepeatCount = 0`), cùng các chế độ lặp lịch `ONCE`, `DAILY`, `WEEKLY`, `MONTHLY`.
   - **4 cấp độ cảnh báo (`escalationLevel`):**
     1. Thông báo thường
     2. Rung
     3. Chuông + thông báo nổi
     4. Màn hình báo động toàn màn hình (`AlarmAlertActivity`, bật sáng màn hình kể cả khi khóa máy)
   - Hỗ trợ hoãn (`Snooze`), tạm dừng, hoàn thành nhanh từ thông báo (`NotificationActionReceiver`) và tự động đặt lại toàn bộ lịch nhắc sau khi khởi động lại máy (`BootReceiver`).

2. **5 màn hình chức năng (Jetpack Compose + Material 3):**
   - **Nhắc nhở (`HomeScreen`):** tạo/sửa/lọc nhắc nhở theo danh mục & mức ưu tiên.
   - **Lịch (`CalendarScreen`) & Đồng bộ Google Calendar (`GoogleCalendarHelper`, `CalendarSyncScheduler`):** đồng bộ sự kiện qua `CalendarProvider` hệ thống, tùy chọn tự động đồng bộ định kỳ và lọc bỏ sự kiện cả ngày / ngày lễ.
   - **Thói quen (`HabitScreen`):** quản lý & check-in thói quen hằng ngày.
   - **Thống kê (`StatisticsScreen`):** theo dõi tỷ lệ hoàn thành công việc & thói quen.
   - **Cài đặt (`SettingsScreen`):** tùy chỉnh thời gian snooze, chu kỳ nhắc, chuông/rung, giao diện Sáng/Tối và đồng bộ lịch.

3. **Lưu trữ hoàn toàn trên máy:**
   - CSDL cục bộ SQLite (`DatabaseHelper.kt`), không gửi dữ liệu ra ngoài.

---

## Cấu trúc thư mục

- `BaoAnhNhacBan.apk` — File APK bản Release cài đặt trực tiếp trên Android (API 26+, ~10.3 MB).
- `docs/SPEC_NAGU.md` — Tài liệu đặc tả yêu cầu phần mềm (SRS) **NAGU – Never Give Up Reminder**.
- `app/` — Toàn bộ mã nguồn Kotlin, Jetpack Compose UI, BroadcastReceivers, SQLite Helper và Android resources.
- `gradle/`, `build.gradle.kts`, `settings.gradle.kts`, `gradlew*` — Cấu hình Gradle (AGP `8.7.2`, Kotlin `2.0.21`, Java `21`, `compileSdk 34`, `minSdk 26`).

---

## Cách build lại từ mã nguồn

```bash
./gradlew assembleRelease
```
File APK đầu ra nằm tại `app/build/outputs/apk/release/app-release.apk`.
