# 🌾 AgriFleet: Task 2 — Resource Allocation Service

## 📖 1. Project Overview & System Context

**AgriFleet** is an Intelligent Decision Support System (IDSS) engineered for agricultural harvester and tractor fleet logistics — an **“Uber for Agricultural Machinery.”**

Within the 5-module AgriFleet ecosystem, **Task 2: Resource Allocation Service** provides the optimal asset-matching layer. When farmers submit harvester and machinery booking requests, Task 2 matches available agricultural machinery to pending farm plots while minimizing total network travel costs and deadhead distances.

```text
+-----------------------------------------------------------------------------------+
|                     AGRIFLEET CENTRAL GUI / API GATEWAY                           |
+---------+------------------+------------------+------------------+----------------+
          |                  |                  |                  |
          v                  v                  v                  v
+------------------+ +------------------+ +------------------+ +------------------+
|     TASK 1       | |     TASK 2       | |     TASK 3       | |     TASK 4       |
| Route            | | Resource         | | Network          | | Intelligent      |
| Optimization     | | Allocation       | | Analysis         | | Decision (MCDM) |
| (A* / Dijkstra)  | | (Hungarian)      | | (Tarjan / MST)   | | (TOPSIS)        |
+------------------+ +------------------+ +------------------+ +------------------+
                           |
                           v
                  +-----------------------+
                  | ⚖️ Hungarian Algorithm|
                  | ⚡ Greedy Heuristic   |
                  | 📦 Allocation Batches |
                  +-----------------------+
```

### Core Responsibilities

- **Scheduled Batch Allocation (Hungarian Algorithm):** Optimizes multiple machinery-to-farm assignments globally across a batch of pending requests to minimize total cumulative distance/cost.
- **Real-Time Greedy Allocation:** Immediately provisions and links the closest available machinery asset when dynamic real-time requests are submitted.
- **Assignment Persistence & Tracking:** Records cost matrices, matrix dimensions, batch metadata, deadhead distances, and estimated times of arrival (ETAs) into the SQLite allocation database.

---

## 🎯 2. Learning Outcome Mapping (NIBM PDSA)

| Learning Outcome | Syllabus Requirement | Task 2 Implementation Evidence |
|---|---|---|
| **LO1** | Algorithmic reasoning, selection, and asymptotic complexity analysis | Asymptotic performance analysis of the Hungarian Matrix Matching algorithm (`O(V³)`) versus Greedy heuristic assignment (`O(E log V)`). |
| **LO2** | Design and implementation of custom data structures for novel problems | Cost-matrix dimensional arrays, dynamic batch payload structures, and relational entity binding between vehicles and bookings. |

---

## 🧠 3. Algorithmic Architecture & Mathematical Foundations

```text
                         TASK 2: RESOURCE ALLOCATION
                                    |
                  +-----------------+-----------------+
                  |                                   |
                  v                                   v
       SUB-PROBLEM 1: BATCH              SUB-PROBLEM 2: REAL-TIME
          Hungarian Algorithm                  Greedy Heuristic
                  |                                   |
         Global Cost Minimization            Immediate Nearest-Asset
             Time: O(V³)                       Time: O(E log V)
            Space: O(V²)                        Space: O(V)
```

### A. Scheduled Batch Allocation — Hungarian Algorithm

Given a cost matrix `C`, where `c(i,j)` represents the travel cost (deadhead distance in km) between vehicle `i` and booking farm plot `j`, the Hungarian algorithm finds an optimal matching `M` that minimizes total cost:

```text
min Σ c(i,j),  for all (i,j) ∈ M
```

The algorithm operates iteratively through row and column reductions and line-covering steps to identify independent zeros.

**Guarantee:** Optimal global cost minimization for scheduled harvesting batches.

### B. Real-Time Greedy Allocation

For urgent real-time booking requests where batch waiting times are unacceptable, the greedy allocator evaluates available units and selects the candidate with the minimum distance metric:

```text
Selected Vehicle =
    arg min Distance(Vehicle_v, Booking_target)
    for all v ∈ Available
```

This provides a fast allocation decision with low computational overhead.

---

## ⏱️ 4. Theoretical Complexity Summary

| Algorithm / Operation | Paradigm | Best Case | Average / Worst Case | Space |
|---|---|---:|---:|---:|
| Hungarian Algorithm (Batch) | Combinatorial Optimization | `Ω(V²)` | `O(V³)` | `O(V²)` |
| Greedy Heuristic (Real-Time) | Greedy Selection | `Ω(V)` | `O(E log V)` | `O(V)` |
| Assignment Persistence | Database Write | `O(1)` | `O(1)`* | `O(1)`* |

> **Note:** Actual database persistence cost depends on the number of records written, database indexing, transaction behavior, and I/O performance. The `O(1)` notation represents a single-record persistence operation at the application level.

---

## 📂 5. Project Directory Structure

```text
agrifleet-backend-task2/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
│
├── src/
│   ├── main/
│   │   ├── java/com/agrifleet/allocation_service/
│   │   │   ├── AllocationServiceApplication.java
│   │   │   │
│   │   │   ├── algorithm/
│   │   │   │   ├── HungarianAlgorithm.java
│   │   │   │   └── GreedyAllocator.java
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   └── AllocationController.java
│   │   │   │
│   │   │   ├── entity/
│   │   │   │   ├── AllocationBatchEntity.java
│   │   │   │   └── AllocatedAssignmentEntity.java
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── AllocationBatchRepository.java
│   │   │   │   └── AllocatedAssignmentRepository.java
│   │   │   │
│   │   │   └── service/
│   │   │       └── AllocationService.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── schema.sql
│   │       └── data.sql
│   │
│   └── test/java/com/agrifleet/allocation_service/
│       ├── AllocationServiceApplicationTests.java
│       └── AllocationIntegrationTest.java
│
├── database/
│   └── agrifleet.db              # SQLite database file (runtime-generated)
│
└── README.md
```

### Component Responsibilities

| Component | Responsibility |
|---|---|
| `HungarianAlgorithm.java` | Solves the global minimum-cost assignment problem. |
| `GreedyAllocator.java` | Performs immediate nearest-asset allocation for real-time requests. |
| `AllocationController.java` | Exposes REST API endpoints. |
| `AllocationService.java` | Coordinates algorithms, persistence, and business logic. |
| `AllocationBatchEntity.java` | Maps allocation batch records to SQLite. |
| `AllocatedAssignmentEntity.java` | Maps individual vehicle-booking assignments to SQLite. |
| `AllocationBatchRepository.java` | Provides database access for allocation batches. |
| `AllocatedAssignmentRepository.java` | Provides database access for assignments. |
| `schema.sql` | Defines the SQLite database schema. |
| `data.sql` | Provides regional benchmark/seed data. |

---

# 🔌 6. REST API Specification

## 6.1 Execute Scheduled Batch Allocation

**Endpoint**

```http
POST /api/allocation/batch
```

### Sample Request

```json
{
  "batchType": "SCHEDULED_BATCH",
  "vehicleIds": [1, 2, 3, 8],
  "bookingIds": [1, 2, 3, 4]
}
```

### Sample Response

```json
{
  "batchId": 1,
  "batchType": "SCHEDULED_BATCH",
  "matrixDimensions": "4x4",
  "totalNetworkCost": 18.25,
  "executionTimeMs": 4.12,
  "assignments": [
    {
      "vehicleId": 1,
      "bookingId": 1,
      "deadheadDistanceKm": 5.85,
      "eta": "2026-08-25 07:15:00"
    },
    {
      "vehicleId": 2,
      "bookingId": 2,
      "deadheadDistanceKm": 4.20,
      "eta": "2026-08-25 07:45:00"
    },
    {
      "vehicleId": 3,
      "bookingId": 3,
      "deadheadDistanceKm": 5.80,
      "eta": "2026-08-25 06:40:00"
    },
    {
      "vehicleId": 8,
      "bookingId": 4,
      "deadheadDistanceKm": 7.80,
      "eta": "2026-08-25 08:35:00"
    }
  ]
}
```

---

## 6.2 Fetch Allocated Assignments for a Batch

**Endpoint**

```http
GET /api/allocation/batches/{batchId}/assignments
```

### Example Request

```http
GET http://localhost:8082/api/allocation/batches/1/assignments
```

### Sample Response

```json
[
  {
    "assignmentId": 1,
    "batchId": 1,
    "vehicleId": 1,
    "bookingId": 1,
    "deadheadDistanceKm": 5.85,
    "estimatedEta": "2026-08-25 07:15:00",
    "assignmentStatus": "CONFIRMED"
  }
]
```

---

# 🗄️ 7. SQLite Database Configuration

Task 2 uses **SQLite** for lightweight, persistent allocation data storage.

The SQLite database is maintained externally from the Java application as a local `.db` file. The Spring Boot service connects to this database using the **Xerial SQLite JDBC driver**.

### Database Location

```text
database/agrifleet.db
```

The database file is created automatically when the application starts if it does not already exist.

### SQLite JDBC Dependency

Add the following dependency to `pom.xml`:

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.50.3.0</version>
</dependency>
```

> If the project already uses another compatible Xerial SQLite JDBC version, retain the project's existing version.

### Spring Boot Configuration

Example `application.properties`:

```properties
server.port=8082

spring.datasource.url=jdbc:sqlite:database/agrifleet.db
spring.datasource.driver-class-name=org.sqlite.JDBC

spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=none

spring.sql.init.mode=always
```

### Hibernate SQLite Dialect

For Spring Boot / Hibernate versions that do not provide the SQLite dialect by default, add:

```xml
<dependency>
    <groupId>org.hibernate.orm>
    <artifactId>hibernate-community-dialects</artifactId>
</dependency>
```

This enables:

```properties
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
```

### Database Initialization

The project uses:

```text
src/main/resources/schema.sql
src/main/resources/data.sql
```

`schema.sql` is responsible for creating the required tables, while `data.sql` inserts benchmark or regional seed data.

---

# 🚀 8. How to Build & Run

## Prerequisites

- **Java Development Kit (JDK):** 17 or higher
- **Maven:** Maven Wrapper is included
- **SQLite:** SQLite database is embedded through the Xerial JDBC driver
- **Node.js & npm:** Required only for running the frontend dashboard

## Running the Backend Service

### 1. Clone the Repository

```bash
git clone https://github.com/AgriFleet/agrifleet-backend-task2.git
cd agrifleet-backend-task2
```

### 2. Create / Verify the SQLite Directory

Linux/macOS:

```bash
mkdir -p database
```

Windows PowerShell:

```powershell
New-Item -ItemType Directory -Force database
```

The application will create:

```text
database/agrifleet.db
```

when the service initializes the SQLite connection.

### 3. Start the Spring Boot Microservice

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The service will run on:

```text
http://localhost:8082
```

---

# 🧪 9. Testing

Run the complete Maven test suite:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

The project includes:

- `AllocationServiceApplicationTests.java` — verifies Spring Boot application context loading.
- `AllocationIntegrationTest.java` — validates end-to-end allocation functionality and API/database integration.

---

# 📊 10. Allocation Workflow

```text
Farmer Booking Request
        |
        v
+-------------------------+
| Pending Booking Queue   |
+-------------------------+
        |
        +----------------------------+
        |                            |
        v                            v
 Scheduled Batch              Real-Time Request
        |                            |
        v                            v
 Cost Matrix C                 Available Vehicles
        |                            |
        v                            v
 Hungarian Algorithm          Greedy Allocator
        |                            |
        +-------------+--------------+
                      |
                      v
             Optimal Allocation
                      |
                      v
              SQLite Persistence
                      |
                      v
             Allocation Response
                      |
                      v
              Frontend / Gateway
```

---

# 🧮 11. Example Allocation Logic

For a scheduled batch containing 4 vehicles and 4 booking requests, Task 2 generates a `4 × 4` cost matrix:

```text
             Booking 1   Booking 2   Booking 3   Booking 4
Vehicle 1       C11         C12         C13         C14
Vehicle 2       C21         C22         C23         C24
Vehicle 3       C31         C32         C33         C34
Vehicle 8       C41         C42         C43         C44
```

The Hungarian algorithm evaluates the matrix to identify the assignment combination that produces the lowest total network cost.

For a real-time request, the system instead evaluates currently available machinery and immediately selects the nearest suitable asset.

---

# 🔐 12. Data Persistence

Task 2 persists allocation information to SQLite for tracking and analysis.

### Allocation Batch

Stores information such as:

- Batch ID
- Batch type
- Matrix dimensions
- Total network cost
- Execution time
- Batch creation timestamp

### Allocated Assignment

Stores information such as:

- Assignment ID
- Batch ID
- Vehicle ID
- Booking ID
- Deadhead distance
- Estimated arrival time
- Assignment status

This persistence layer allows the system to retain allocation decisions after the allocation algorithm has completed.

---

# 🔗 13. Integration with the AgriFleet Ecosystem

Task 2 is designed as an independent Spring Boot microservice within the larger AgriFleet platform.

```text
                 AGRIFLEET CENTRAL API GATEWAY
                            |
        +-------------------+-------------------+
        |                   |                   |
        v                   v                   v
     TASK 1              TASK 2              TASK 3
 Route Optimization   Resource Allocation   Network Analysis
        |                   |                   |
        |          +--------+--------+          |
        |          |                 |          |
        |      Hungarian         Greedy        |
        |      Algorithm         Allocator     |
        |          |                 |          |
        +----------+--------+--------+----------+
                           |
                           v
                    SQLite Persistence
```

Task 2 consumes relevant vehicle and booking information and produces allocation decisions that can be consumed by the central gateway and other AgriFleet modules.

---

# 🛠️ 14. Technology Stack

| Technology | Purpose |
|---|---|
| **Java 17+** | Core programming language |
| **Spring Boot** | Microservice framework |
| **Spring Web** | REST API implementation |
| **Spring Data JPA** | Persistence abstraction |
| **Hibernate** | ORM |
| **SQLite** | Lightweight relational database |
| **Xerial SQLite JDBC** | Java-to-SQLite connectivity |
| **Maven** | Dependency and build management |
| **JUnit / Spring Boot Test** | Automated testing |
| **Node.js / npm** | Frontend dashboard environment |

---

# 📈 15. Key Performance Metrics

Task 2 exposes and records important allocation metrics:

- **Matrix Dimensions** — size of the allocation problem.
- **Total Network Cost** — cumulative deadhead distance/cost of the selected assignments.
- **Execution Time** — algorithm execution duration.
- **Deadhead Distance** — distance traveled by machinery before reaching its assigned farm plot.
- **ETA** — estimated time of arrival at the assigned booking location.

These metrics can be used to compare allocation efficiency and support future decision-making modules.

---

# 🎓 16. Academic Relevance

Task 2 demonstrates practical application of:

1. **Combinatorial optimization**
2. **Greedy algorithm design**
3. **Asymptotic complexity analysis**
4. **Matrix-based data structures**
5. **RESTful microservice architecture**
6. **Object-relational mapping**
7. **Relational database persistence**
8. **Algorithm-to-database integration**
9. **Automated integration testing**

The module therefore provides implementation evidence for algorithmic reasoning, data-structure design, complexity analysis, and practical software engineering requirements under the NIBM PDSA curriculum.

---

# 👨‍💻 17. Development Notes

When modifying the allocation algorithms, keep the following separation:

```text
Controller
    ↓
Service
    ↓
Algorithm Engine
    ↓
Repository
    ↓
SQLite Database
```

The algorithm classes should remain as independent as possible from the REST and persistence layers. This separation makes the algorithms easier to test, benchmark, reuse, and compare.

---

# 📄 18. License

This project is developed as part of the **AgriFleet academic project** and is intended for educational and demonstration purposes.
