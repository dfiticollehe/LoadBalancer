# 🔁 Java Load Balancer

A custom-built **Load Balancer** in Java with core production-grade features like health checking, weighted round-robin, retry with fallback, rate limiting, circuit breaking, connection draining, and more.

---

## 🚀 Features

- **Weighted Round Robin** load distribution
- **Health Check Monitoring** (active)
- **Retry with Fallback** when server is unreachable
- **Connection Draining** support (graceful shutdown of backend)
- **Admin APIs** for backend registration/deregistration
- **Circuit Breaker** to isolate failing servers
- **Backend-Level Rate Limiting** using Strategy Pattern
- **Spring Boot Admin Interface** (REST controller for admin APIs)
- Clean **Strategy Pattern** for Load Balancing policies
- **General Response Wrapper** for API consistency

---

## 📦 Tech Stack

- Java 11
- Spring Boot (2.7.x)
- SLF4J + Logback (Logging)
- Lombok
- Optionally Redis (for distributed rate limiter)

---

## 📁 Project Structure

src/main/java/com/loadbalancer/
│
├── Main.java # Spring Boot main entry (CommandLineRunner)
│
├── model/
│ └── BackendServer.java # Server metadata + CircuitBreaker
│
├── core/
│ └── LoadBalancer.java # Load balancing logic
│
├── strategy/
│ ├── LoadBalancingStrategy.java
│ ├── WeightedRoundRobinStrategy.java
│ └── HealthAwareStrategy.java
│
├── server/
│ ├── LBRequestServer.java # Accepts client socket connections
│ └── Worker.java # Handles request forwarding + retry logic
│
├── service/
│ ├── HealthMonitor.java
│ ├── HealthChecker.java
│ ├── AdminController.java # Spring Boot REST controller (register/deregister)
│ └── RateLimiter.java # Interface for backend rate limiting
│
├── circuitbreaker/
│ ├── CircuitBreaker.java
│ └── CircuitState.java
│
└── dto/
└── GeneralResponse.java # Wrapper for API responses


---

## 🛠 How It Works

### 1. Load Balancing
Implements `LoadBalancingStrategy`:
- `WeightedRoundRobinStrategy` distributes traffic based on server weights.
- `HealthAwareStrategy` skips unhealthy backends.

### 2. Retry With Fallback
- Tries other backends on connection failure (up to 3 retries).
- Logs failures, prevents duplicate retries.

### 3. Health Checks
- Background thread pings all backends periodically.
- Updates server health status.

### 4. Circuit Breaker
- Opens after consecutive failures.
- Temporarily disables backend.
- Half-open allows limited test requests after timeout.

### 5. Rate Limiting
- Supports per-backend rate limiter (e.g., fixed window).
- Plugged using strategy pattern.

### 6. Admin API
```http
POST /admin/register
Body: localhost:20001

POST /admin/deregister
Body: localhost:20001

▶️ Running
mvn clean install
java -jar target/loadbalancer-1.0-SNAPSHOT.jar

Make sure your backend services are running on ports like 20001, 20002, 20003
