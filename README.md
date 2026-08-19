# Inventory Service

A Java backend service for managing and querying a simplified technical inventory.
Built with Spring Boot 4.0.7 and Java 21.

## Running the service

### Prerequisites
- Java 21
- Gradle (wrapper included)

### Start
```bash
./gradlew bootRun
```
The service starts on `http://localhost:8080`.

### Run tests
```bash
./gradlew test
```

## API Endpoints

### Equipment by location
```
GET /api/v1/locations/{locationId}/equipment
```

### Equipment connections
```
GET /api/v1/equipment/{equipmentId}/connections
```

### Inventory summary
```
GET /api/v1/inventory/summary
```

### Connected equipment (BFS traversal)
```
GET /api/v1/equipment/{equipmentId}/connected?depth={maxDepth}
```
Returns equipment reachable through active connections up to the given depth. Default depth is 1.

### API Documentation
Swagger UI is available at `http://localhost:8080/swagger-ui.html` when the service is running.

## Key technical decisions

**In-memory storage** — Used `Map`-based repositories instead of JPA/H2. The dataset is small and the assignment focus is on domain modelling and business logic, not persistence. In production I would use a relational database with proper indexing.

**Java records for domain model** — `Location`, `Equipment`, and `Connection` are immutable records. Since there is no JPA/Hibernate requirement (no mutable proxies needed), records provide equals/hashCode/toString natively without Lombok or boilerplate.

**BFS for connected equipment** — Breadth-first search is the natural fit because the query asks for equipment by depth level. A visited set (`depthById` map) prevents infinite loops in cyclic graphs. Only active connections are traversed — this matches the assignment example where A→E (inactive) is not counted at depth 1.

**Unified exception handling** — A single `GlobalException` carries an `HttpStatus`, caught by `@RestControllerAdvice`. This avoids a separate exception class per error type while keeping error responses consistent across all endpoints.

**Data quality: warn and load** — Invalid references (equipment pointing to a nonexistent location, connections pointing to nonexistent equipment) are logged as warnings but still loaded. This preserves data rather than silently dropping records. Structurally broken data (missing required fields like `id`) fails fast at startup.

## Data quality handling

The sample dataset contains two intentional inconsistencies:

| Issue | Record | Handling |
|---|---|---|
| Equipment references unknown location | Equipment F → LOC-999 | Loaded with warning. Equipment is real, only the reference is broken. |
| Connection references unknown equipment | CON-BROKEN → DOES-NOT-EXIST | Loaded with warning. BFS naturally skips it since the target is not in the equipment store. |

Additional cases handled:
- **Duplicate IDs** — first entry wins, duplicates are skipped with a warning
- **Cyclic connections** — BFS visited set prevents infinite loops

## Assumptions

- Connections are bidirectional for traversal purposes (if A→B exists, B can reach A)
- Connected equipment traversal only follows active connections
- Equipment and connection status values are limited to ACTIVE and INACTIVE
- The `depth` query parameter defaults to 1 if not provided

## Intentionally left out (timebox)

- Pagination for list endpoints
- Authentication and authorization
- Request logging and metrics
- Filtering and sorting query parameters
- HATEOAS links in responses
- Containerization (Dockerfile, docker-compose)

## Production improvements

- Replace in-memory store with PostgreSQL + proper indexing on foreign keys
- Add pagination to all list endpoints
- Add request validation with more granular error messages
- Implement caching for frequently queried graph traversals
- Add observability (structured logging, metrics, health checks)
- Add integration tests with Testcontainers for database layer
- Consider a graph database if connection traversal becomes the primary use case at scale
