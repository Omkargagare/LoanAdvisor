# 💰 Loan Advisor — Backend

A production-grade Spring Boot backend for a smart loan recommendation platform. Implements a secure JWT authentication system with refresh token rotation, server-side CSRF protection, and access token blacklisting.

> **Status**: Backend under active development. Frontend not yet started.

---

## 🚀 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security + JWT (jjwt 0.13) |
| Database | MySQL |
| ORM | Spring Data JPA (Hibernate) |
| Build | Maven |
| Utilities | Lombok, Spring Validation |

---

## 🔐 Authentication System

The auth system is fully implemented with industry-standard security practices.

### Flows

- **Register** — `POST /api/v1/auth/register` — Creates a new user account with BCrypt-hashed password (cost 12)
- **Login** — `POST /api/v1/auth/login` — Returns a short-lived JWT access token in the response body + HttpOnly refresh token and CSRF token as cookies
- **Refresh** — `POST /api/v1/auth/refresh` — Issues a new access token via refresh token rotation; validates CSRF token against server-stored BCrypt hash
- **Logout** — `POST /api/v1/auth/logout` — Blacklists the access token JTI and revokes the refresh token; clears all auth cookies

### Security Features

| Feature | Implementation |
|---------|---------------|
| Access token TTL | 15 minutes (JWT) |
| Refresh token TTL | 7 days (stored in `refresh_tokens` table) |
| Token revocation | JTI-based blacklist (`token_blacklist` table) |
| Blacklist enforcement | Checked in `JWTFilter` on every authenticated request |
| Refresh token rotation | Old token revoked on each refresh; new token issued |
| Reuse detection | If a revoked refresh token is used, **all** user tokens are revoked (family revocation) |
| CSRF protection | CSRF token BCrypt hash stored in `refresh_tokens` row; validated server-side on `/refresh` |
| Cookie security | `HttpOnly`, `Secure`, `SameSite=None`, path-scoped to `/api/v1/auth` |
| CORS | Configured globally for `http://localhost:5173` with credentials and `X-CSRF-TOKEN` header |
| Session policy | Stateless (`SessionCreationPolicy.STATELESS`) |
| Password hashing | BCrypt cost factor 12 |

### Database Schema

```
users
  id          INT (PK, auto-increment)
  username    VARCHAR (unique, not null)
  password    VARCHAR (not null, BCrypt)
  role        ENUM('USER', 'ADMIN')

refresh_tokens
  id          INT (PK, auto-increment)
  token       VARCHAR (unique, not null)
  revoked     BOOLEAN
  csrf_token  VARCHAR (BCrypt hash of CSRF token)
  created_at  TIMESTAMP
  expiry_date TIMESTAMP
  user_id     INT (FK → users.id, not null)

token_blacklist
  jti         VARCHAR (PK — JWT ID claim)
  expiry_time TIMESTAMP (indexed for cleanup)
```

---

## 📁 Project Structure

```
Backend/
└── src/main/java/org/omkar/loanbackend/
    ├── config/
    │   └── SecurityConfig.java          # Spring Security chain, CORS bean, BCrypt bean
    ├── controller/
    │   ├── AuthController.java          # Register, Login, Logout, Refresh endpoints
    │   └── OAuth2Controller.java        # OAuth2 stub (planned)
    ├── dto/                             # LoginRequest, RegisterRequest, AuthTokens, etc.
    ├── exception/
    │   ├── custom/                      # CsrfValidationException, InvalidRefreshTokenException, etc.
    │   └── handler/                     # AuthExceptionHandler, ValidationExceptionHandler, GlobalExceptionHandler
    ├── filter/
    │   └── JWTFilter.java               # Per-request JWT validation + blacklist check
    ├── model/
    │   ├── Users.java                   # User entity
    │   ├── RefreshToken.java            # Refresh token entity (with CSRF hash)
    │   ├── BlacklistToken.java          # Blacklisted JTI entity
    │   └── Role.java                    # USER / ADMIN enum
    ├── repo/                            # Spring Data JPA repositories
    ├── response/
    │   └── ApiResponse.java             # Unified API response wrapper
    └── service/
        ├── UserService.java             # Auth business logic
        ├── TokenService.java            # Refresh token + CSRF token generation
        ├── JWTService.java              # JWT generation, parsing, validation
        ├── CookieService.java           # Cookie creation and removal helpers
        └── MyUserDetailsService.java    # Spring Security UserDetailsService
```

---

## ⚙️ Setup

### Prerequisites

- Java 21+
- Maven 3.8+
- MySQL 8+

### 1. Clone the Repository

```bash
git clone https://github.com/Omkargagare/LoanAdvisor.git
cd LoanAdvisor
```

### 2. Create the Database

```sql
CREATE DATABASE LoanDiscovery;
```

### 3. Configure Environment Variables

The application reads credentials from environment variables — never hardcode secrets.

| Variable | Description |
|----------|-------------|
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | Base64-encoded HS256 secret (min 256 bits) |

```bash
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export JWT_SECRET=your_base64_encoded_secret_here
```

### 4. Run the Backend

```bash
cd Backend
./mvnw spring-boot:run
```

The server starts on **`http://localhost:8080`** by default.

---

## 📡 API Reference

All responses use the unified wrapper:
```json
{ "message": "...", "data": { ... }, "success": true }
```

### Auth Endpoints

| Method | Endpoint | Auth Required | Description |
|--------|----------|:---:|-------------|
| `POST` | `/api/v1/auth/register` | ❌ | Register a new user |
| `POST` | `/api/v1/auth/login` | ❌ | Login; returns access token + sets cookies |
| `POST` | `/api/v1/auth/refresh` | ❌ (cookie) | Rotate refresh token; returns new access token |
| `POST` | `/api/v1/auth/logout` | ✅ Bearer | Revoke tokens and clear cookies |

### Register
```bash
POST /api/v1/auth/register
Content-Type: application/json

{ "username": "omkar", "password": "Secret@123" }
```

### Login
```bash
POST /api/v1/auth/login
Content-Type: application/json

{ "username": "omkar", "password": "Secret@123" }
```
**Response**: `{ "data": { "accessToken": "<jwt>" } }`  
**Cookies set**: `refreshToken` (HttpOnly), `csrfToken` (readable by JS)

### Refresh
```bash
POST /api/v1/auth/refresh
X-CSRF-TOKEN: <value from csrfToken cookie>
# refreshToken cookie attached automatically
```

### Logout
```bash
POST /api/v1/auth/logout
Authorization: Bearer <accessToken>
# refreshToken cookie attached automatically
```

---

## 🗺️ Planned Features

- [ ] Loan recommendation engine (rule-based eligibility by income, region, occupation, purpose)
- [ ] EMI calculator endpoint
- [ ] OAuth2 / Google login
- [ ] React frontend
- [ ] Scheduled cleanup job for expired blacklist entries

---

## 👨‍💻 Author

**Omkar Gagare**
