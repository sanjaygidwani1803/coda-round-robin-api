### Coda Round Robin API

### In Scope (Covered Features)

- **App API** – echoes back any valid JSON POST request
- **Router API** – receives JSON POST requests and forwards them in round-robin fashion to App instances
- Configurable target instances via `targets.json`
- Thread-safe, lock-free round-robin load balancer (skips degraded instances)
- Degrades slow instances based on response latency; auto-recovers after cooldown
- Robust error handling and detailed logging for traceability
- Unit tests for load balancer, router service, and health monitor 
- Clean, layered architecture for extensibility and production maintainability

### Out of Scope (Future Enhancements)

- **Rate limiting / throttling** – Add a global or per-client QPS limit to protect downstream services under high load
- **Weighted round-robin** – Route requests based on instance capacity or health metrics instead of equal distribution

### How to Run

- Requirements: JDK 21+, Apache Maven
- In separate terminals, start the App API on three different ports
- In another terminal, start the Router API
- Send a sample POST request using curl or Postman
```bash
cd app-api
mvn clean package
java -DPORT=5001 -jar target/app-api-1.0.0.jar
java -DPORT=5002 -jar target/app-api-1.0.0.jar
java -DPORT=5003 -jar target/app-api-1.0.0.jar

cd router-api
mvn clean package
java -DPORT=8080 -jar target/router-api-1.0.0.jar

curl -X POST http://localhost:8080/forward \
  -H "Content-Type: application/json" \
  -d '{"game":"Mobile Legends","gamerID":"GYUTDTE","points":20}'
