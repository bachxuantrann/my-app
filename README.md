# My App - Microservices Architecture

*My Pet Project - Dự án được phát triển với mục đích học tập, nghiên cứu và ứng dụng các công nghệ kiến trúc phần mềm hiện đại.*

## 📖 Tổng quan dự án

Dự án được thiết kế theo kiến trúc Microservices với trọng tâm là xây dựng một hệ sinh thái các dịch vụ xoay quanh một trung tâm xác thực và định danh duy nhất. Thay vì mỗi dịch vụ phải tự quản lý người dùng và xác thực, hệ thống sử dụng một mô hình tập trung, giúp dễ dàng tích hợp đa nền tảng (Web, Mobile, 3rd-party) một cách an toàn.

## 🏗 Kiến trúc hệ thống

Hệ thống đang trong quá trình phát triển, với lộ trình các thành phần (services) như sau:

### 1. SSO Service (Identity Provider) - *Trạng thái: Đã hoàn thành*
Đóng vai trò là "Trái tim bảo mật" của hệ thống, một Máy chủ Định danh Tập trung (Identity Provider) tuân thủ hoàn toàn tiêu chuẩn **OAuth 2.0** và **OpenID Connect (OIDC) 1.0**.
- **Chức năng cốt lõi:** Quản lý người dùng, cấp phát JWT (Access Token, ID Token), quản lý ứng dụng vệ tinh (Client Management) và cung cấp màn hình Consent (ủy quyền).
- **Bảo mật & Quản lý phiên:** Chống Brute-force (Rate Limiting), hỗ trợ Global Logout, vô hiệu hóa Token tức thời bằng Redis Blacklist (Revoke Session).
- **Giám sát (Audit):** Mọi sự kiện đăng nhập/đăng xuất đều được bắn dưới dạng Event message qua topic Kafka (`sso-audit-events`).
- Xem tài liệu thiết kế chi tiết: [SSO OIDC System Design](./sso-service/SSO_OIDC_System_Design.md).

### 2. Gateway Service - *(Sắp ra mắt)*
API Gateway sẽ là cổng giao tiếp duy nhất (Single Entry Point) cho toàn bộ request từ người dùng.
- Chịu trách nhiệm định tuyến (Routing) request đến các service tương ứng.
- Xác thực tập trung: Kiểm tra tính hợp lệ của Token thông qua `sso-service` trước khi cho phép request đi sâu vào các service nội bộ.

### 3. Các Service / Ứng dụng Vệ tinh - *(Sắp ra mắt)*
Các microservice nghiệp vụ hoặc các ứng dụng Frontend/Mobile khác. Chúng đóng vai trò là các **Client / Resource Server**, không tự quản lý mật khẩu mà sẽ xác thực thông qua `sso-service`.

## 🛠 Công nghệ sử dụng

- **Backend Core**: Java, Spring Boot 3.x
- **Identity & Security**: Spring Security, Spring Authorization Server 1.1+ (OAuth2 / OIDC)
- **Database**: PostgreSQL 16
- **Caching & Blacklist**: Redis 7
- **Message Broker & Event Stream**: Apache Kafka (KRaft mode) + Kafka UI
- **Deployment & Infrastructure**: Docker & Docker Compose

## 🚀 Hướng dẫn cài đặt & Khởi chạy (Local)

### 1. Yêu cầu môi trường
- Docker & Docker Compose
- Java 17+
- Maven

### 2. Khởi chạy các dịch vụ hạ tầng nền tảng (Infrastructure)
Dự án cung cấp sẵn file `docker-compose.dev.yml` để khởi chạy các dịch vụ cần thiết (PostgreSQL, Redis, Kafka).

Đầu tiên, sao chép file biến môi trường:
```bash
cp .env.example .env
```
*(Bạn có thể mở file `.env` để cấu hình lại các thông số mật khẩu, port nếu cần)*

Khởi chạy bằng Docker Compose:
```bash
docker-compose -f docker-compose.dev.yml up -d
```
Sau khi chạy thành công, bạn có thể truy cập **Kafka UI** tại: `http://localhost:8080` (Cổng mặc định nếu chưa đổi trong `.env`).

### 3. Khởi chạy SSO Service
Mở một terminal mới, di chuyển vào thư mục `sso-service` và khởi chạy ứng dụng Spring Boot:

```bash
cd sso-service
./mvnw spring-boot:run
```

## 📚 Tài liệu đính kèm
- [Thiết kế hệ thống SSO & OIDC Flows](./sso-service/SSO_OIDC_System_Design.md)
