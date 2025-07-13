# 🎓 MOOC Recommendation App

A full-featured backend service for recommending MOOCs (Massive Online Courses), built using **Spring Boot**, **MongoDB**, and **Redis**.

This project enables users to register, receive personalized course suggestions, manage saved courses, submit feedback, and offers admins deep analytics and dashboard control.

---

## 📦 Tech Stack

- ☕ **Java 17**
- 🚀 **Spring Boot 3.x**
- 🧾 **Spring Data MongoDB**
- 📬 **Spring Mail (Gmail SMTP)**
- 🛡 **Spring Security with JWT**
- 💾 **Redis Cloud** – OTP, signup session, rate-limiting
- 📖 **SpringDoc OpenAPI (Swagger UI + ReDoc)**

---

## 🔧 Features

### 👤 Authentication
- Custom registration with OTP verification
- Login with JWT token
- Forgot password with OTP flow
- Rate limiting and cooldown (via Redis)

### 🎯 Course Recommendation
- Filter & search courses by tags, platform, difficulty, duration
- Save & unsave courses
- Shareable course URLs
- Save/share count tracking

### 📊 Admin Dashboard
- Real-time course/user/feedback stats
- Platform-wise and interest analytics
- Admin-only course CRUD APIs

### 📥 Feedback Module
- Users can submit feedback on the platform
- Admin can view all feedback in dashboard

---

## 📂 Project Structure

```plaintext
src/
├── main/
│   ├── java/com/dhanesh/mooc/
│   │   ├── controller/         # Student & Admin APIs
│   │   ├── service/            # Business logic
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── entity/             # MongoDB documents
│   │   ├── repository/         # Spring Data Repositories
│   │   ├── config/             # Security, Mail, Redis, etc.
│   │   └── security/           # JWT & auth filters
│   └── resources/
│       └── application.properties  # Uses .env + dotenv-java

docs/
├── openapi.json              # OpenAPI 3.0 spec (for Swagger/Redoc)
└── redoc.html                # Pretty static API documentation

````
---

## 🚀 Getting Started

### 1. Clone the Project

```bash
git clone https://github.com/<your-username>/mooc-recommendation-app.git
cd mooc-recommendation-app
````

### 2. Create a `.env` File

```env
MONGO_URI=...
REDIS_HOST=...
REDIS_PORT=...
REDIS_USERNAME=...
REDIS_PASSWORD=...
JWT_SECRET=...
JWT_EXPIRATION_MS=3600000
MAIL_USER=your-email@gmail.com
MAIL_PASS=your-app-password
OTP_TTL_MINUTES=5
SESSION_TTL_MINUTES=10
OTP_COOLDOWN_SECONDS=30
OTP_RATE_LIMIT=5
OTP_RATE_LIMIT_DURATION=5
```

### 3. Run the App

```bash
mvn spring-boot:run
```
---
### 📖 API Documentation

[![Swagger UI](https://img.shields.io/badge/Swagger-UI-blue?logo=swagger)](https://dhanesh76.github.io/mooc/swagger.html)
[![OpenAPI Spec](https://img.shields.io/badge/OpenAPI-Spec-yellow?logo=openapi)](https://dhanesh76.github.io/mooc/openapi.json)

📁 These files are also available in the [`docs/`](./docs) folder of this repository.


---

## ✨ Modules Covered

| Module         | Description                                |
| -------------- | ------------------------------------------ |
| Auth           | Signup, login, OTP, password reset         |
| Course         | Add/update/delete/search/filter/save/share |
| Dashboard      | Admin stats and analytics                  |
| Feedback       | User feedback submission & admin view      |
| Saved Courses  | Track and view saved courses               |
| Sharing        | Generate shareable course links            |
| Recommendation | Tag & interest-based course suggestions    |

---

Perfect! You can include that Postman collection in your `README.md` like this:

---

## 🔧 Postman Collection

You can try out all the tested API endpoints using the Postman collection below:

[![Run in Postman](https://run.pstmn.io/button.svg)](https://dhaneshv.postman.co/workspace/Dhanesh-V's-Workspace~637ce80a-f746-4ef0-8b8b-c30f24567016/collection/45135482-405d8e61-ecd0-4a48-a2f2-a7cb9e83b8af?action=share&creator=45135482)

---

## 🪪 License

This project is licensed under the MIT License.

---

## 👨‍💻 Author
**Dhanesh V**

Java & Spring Developer | Backend Systems 
