# first — Spring Boot Backend

## What was changed (latest)
- FinicialController.java: full rewrite — all endpoints now return ResponseEntity<?> with proper error bodies; removed old stubs (/Stock, /buyStock, /sellStock); added @CrossOrigin(origins = "http://localhost:4200") at class level (replaces CorsConfig.java for this controller); /book endpoint removed from controller (kept in router.java to avoid duplicate mapping)


- application.properties: switched from MySQL to H2 in-memory database
- pom.xml: Java version was already 17 (no change needed)
- pom.xml: removed non-existent spring-boot-starter-data-jpa-test and spring-boot-starter-webmvc-test, replaced with spring-boot-starter-test
- pom.xml: added H2 runtime dependency
- Added CorsConfig.java to allow requests from Angular frontend on localhost:4200
- User.java: initialized `investments` list to `new ArrayList<>()` — was null, causing NPE on POST /user
- StudentService.java: `createdNewUser` now returns `null` for password instead of echoing the plaintext password back in the response
- UserDto.java: replaced `@JsonIgnore` on `password` with `@JsonProperty(access = WRITE_ONLY)` — `@JsonIgnore` blocked incoming password on registration (saving null to DB), causing NPE on login; WRITE_ONLY allows the field in but never out
- StudentService.java: `loginUser` now checks `user.getPassword() == null` before `.equals()` to guard against stale null-password rows
- StudentService.java: added `@Transactional` to `createdNewUser`, `loginUser`, `createExpanse`, and `Inverstment` — fixes 500 on POST /login caused by lazy-loading of `expenses`/`investments` collections outside a Hibernate session
- User.java: changed `expenses` and `investments` to `FetchType.EAGER` and added `@JsonIgnore` — ensures collections are always loaded with the User and never serialized into API responses
- application.properties: added `spring.jpa.open-in-view=false` — disables Open Session in View anti-pattern so Hibernate sessions are not held open for the full HTTP request lifecycle
- ExpanseDto.java: added `@JsonFormat(pattern = "yyyy-MM-dd")` on `expenseDate` — fixes 500 on POST /Expanse caused by Jackson failing to deserialize LocalDate from plain string in Spring Boot 4
- InvestmentDto.java: added `@JsonFormat(pattern = "yyyy-MM-dd")` on `investmentDate` — same fix for POST /investment
- application.properties: added `spring.jackson.serialization.write-dates-as-timestamps=false` and `fail-on-unknown-properties=false` — registers JavaTimeModule globally for ISO date handling
- InvestmentDto.java: changed `int quantity` to `Integer quantity`, switched to `@Data` — allows proper null-safety and AllArgsConstructor with Long investmentId as first field
- StudentService.java: renamed `deleteExpense(Long id)` → `deleteExpense(Long expenseId)` and same for `deleteInvestment` — error messages now include the id
- FinicialController.java: `DELETE /Expanse/{id}`, `GET /Expanse/{userId}`, `GET /investment/{userId}` now return `ResponseEntity` with proper 404/500 error bodies

## How to run

```bash
JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-17.0.19.10-hotspot" ./mvnw spring-boot:run
```

## How to verify backend is running

```bash
curl http://localhost:8080/book
# Expected response: Hare krishna
```

## H2 Console (inspect database tables)
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:finappdb
- Username: sa
- Password: (leave empty)

## Available API endpoints
| Method | Endpoint                  | Description                    |
|--------|--------------------------|--------------------------------|
| GET    | /book                    | Health check                   |
| POST   | /user                    | Register new user              |
| POST   | /login                   | Login user                     |
| POST   | /Expanse                 | Add expense                    |
| GET    | /Expanse/{userId}        | Get all expenses for a user    |
| DELETE | /Expanse/{id}            | Delete expense by id           |
| DELETE | /investment/{id}         | Delete investment by id        |
| POST   | /investment              | Add investment                 |
| GET    | /investment/{userId}     | Get all investments for a user |
| POST   | /Stock                   | Add stock listing (stub)       |
| GET    | /Stock                   | List all stocks                |
| POST   | /buyStock                | Buy stock (empty stub)         |
| PATCH  | /sellStock               | Sell stock (empty stub)        |
| POST   | /gold                    | Add gold holding               |
| GET    | /gold/{userId}           | Get all gold holdings for user |
| DELETE | /gold/{goldId}           | Delete gold holding by id      |
| GET    | /wellness/{userId}       | Get wellness score for user    |
| POST   | /investment/buy-more     | Buy more of an existing stock  |
| POST   | /investment/sell         | Sell units of a stock          |
| POST   | /goal                    | Create a goal                  |
| GET    | /goal/{userId}           | Get all goals for user         |
| PATCH  | /goal/{goalId}/progress  | Add amount to goal progress    |
| DELETE | /goal/{goalId}           | Delete goal by id              |

## CORS
Enabled for http://localhost:4200 (Angular dev server) via CorsConfig.java

## Known TODOs for backend team
1. Add `role` field to User entity and LoginResponseDto
2. Add `username`, `mobileNo`, `address` to UserDto and User entity
3. Add PUT /Expanse/{id} — edit expense
4. Implement buyStock and sellStock logic (currently empty stubs)
5. Implement BCrypt password encryption
6. Add JWT token authentication
