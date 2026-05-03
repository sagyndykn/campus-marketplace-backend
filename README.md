# Campus Marketplace — Backend

A RESTful API for a student marketplace platform built for SDU University, enabling students to buy and sell items within the campus community.

---

## Problem Statement

Students at SDU University have no dedicated platform to trade items with each other. Existing marketplaces (Kaspi, OLX) are not campus-specific and do not verify student identity. This backend provides a secure, verified marketplace restricted to `@sdu.edu.kz` email addresses.

---

## Features

- **Email-verified registration** — OTP sent to `@sdu.edu.kz` email on sign-up
- **JWT authentication** — stateless auth with refresh tokens and token blacklisting on logout
- **Forgot password** — OTP-based password reset flow
- **Listings CRUD** — create, read, update, delete marketplace listings
- **Photo upload** — up to 5 photos per listing stored in MinIO object storage
- **Feed with filters** — search by keyword, category, price range, seller, and exclude specific listing
- **Favorites** — add/remove/list favorited listings per user
- **Real-time chat** — WebSocket-based messaging between users
- **User profiles** — name, phone, avatar upload
- **Role-based access** — USER and ADMIN roles

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Database | MongoDB |
| Cache / Session | Redis |
| Object Storage | MinIO |
| Auth | JWT (jjwt) + BCrypt |
| Email | Spring Mail (SMTP) |
| Real-time | WebSocket (STOMP) |
| Build Tool | Maven |
| Containerization | Docker Compose |

---

## Installation

### Prerequisites
- Java 17+
- Maven
- Docker + Docker Compose

### Steps

**1. Clone the repository**
```bash
git clone <repository-url>
cd campus-marketplace-backend
```

**2. Configure environment**

Copy `src/main/resources/application.properties` and fill in your values:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/marketplace
spring.data.redis.host=localhost
spring.data.redis.port=6379
minio.endpoint=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket=marketplace
jwt.secret=your-256-bit-secret-key
jwt.expiration=86400000
spring.mail.host=smtp.gmail.com
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

**3. Start infrastructure (MongoDB + Redis + MinIO)**
```bash
docker-compose up -d
```

**4. Run the application**
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## API Endpoints

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register with `@sdu.edu.kz` email — sends OTP |
| POST | `/api/auth/verify-otp` | Verify OTP → returns JWT + refresh token |
| POST | `/api/auth/login` | Login with email + password |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/resend-otp` | Resend OTP to email |
| POST | `/api/auth/logout` | Invalidate JWT |
| POST | `/api/auth/forgot-password` | Send password reset OTP |
| POST | `/api/auth/verify-reset-otp` | Verify reset OTP → returns JWT |
| POST | `/api/auth/change-password` | Set new password |

### Listings
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/listings` | Paginated feed |
| POST | `/api/listings` | Create a listing |
| GET | `/api/listings/my` | Get current user's listings |
| GET | `/api/listings/{id}` | Get listing by ID |
| PUT | `/api/listings/{id}` | Update listing |
| DELETE | `/api/listings/{id}` | Delete listing |
| POST | `/api/listings/{id}/photos` | Upload photos (multipart, max 5) |
| DELETE | `/api/listings/{id}/photos` | Delete a photo by URL |
| POST | `/api/listings/{id}/favorite` | Add to favorites |
| DELETE | `/api/listings/{id}/favorite` | Remove from favorites |
| GET | `/api/listings/favorites` | Get all favorited listings |

### Users
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users/me` | Get current user profile |
| PUT | `/api/users/me` | Update name and phone |
| POST | `/api/users/me/avatar` | Upload avatar image |

### Chat
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/chat/conversations` | Start a conversation |
| GET | `/api/chat/conversations` | List all conversations |
| GET | `/api/chat/conversations/{id}/messages` | Get messages in a conversation |
| WS | `/ws` | WebSocket endpoint (STOMP) |

### Query Parameters for GET /api/listings
| Param | Type | Description |
|---|---|---|
| `category` | string | Filter by category enum value |
| `sellerId` | string | Filter by seller ID |
| `excludeId` | string | Exclude a specific listing ID |
| `search` | string | Search in listing title |
| `minPrice` | number | Minimum price filter |
| `maxPrice` | number | Maximum price filter |
| `page` | int | Page number (default: 0) |
| `size` | int | Page size (default: 20) |

### Category Values
`ELECTRONICS`, `CLOTHING`, `HOME_AND_GARDEN`, `AUTO`, `SPORTS`, `BOOKS`, `GAMES`, `OTHER`, `SDU`

---

## Project Structure

```
src/
├── main/java/com/campus/marketplace/
│   ├── config/          # SecurityConfig, WebConfig, MinioConfig, RedisConfig, WebSocketConfig
│   ├── controller/      # AuthController, ListingController, UserController, ChatController
│   ├── dto/
│   │   ├── request/     # RegisterRequest, LoginRequest, CreateListingRequest, ...
│   │   └── response/    # AuthResponse, ListingResponse, UserResponse, ConversationResponse
│   ├── enums/           # Category, ListingStatus, Role
│   ├── model/           # User, Listing (MongoDB documents)
│   ├── repository/      # UserRepository, ListingRepository
│   ├── security/        # JwtService, JwtAuthFilter
│   └── service/
│       └── impl/        # AuthServiceImpl, ListingServiceImpl, OtpServiceImpl, UserServiceImpl
└── test/
```

---

## Team

| Student ID | Name |
|---|---|
| 230110056 | Aknur Buktash |
| 230103136 | Akmaral Adilbek |
| 230103243 | Nurmuhammed Sagyndyk |
| 230103225 | Danial Makssatuly |
